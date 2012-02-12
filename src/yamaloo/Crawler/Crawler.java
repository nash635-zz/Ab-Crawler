package yamaloo.Crawler;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import yamaloo.Common.*;

public class Crawler implements Runnable {
	private CrawlerHost host;
	private SiteSetting currentSiteSetting;

	public Crawler(CrawlerHost host) {
		this.host = host;
	}

	@Override
	public void run() {

		CrawlerTask task = null;
		List<CrawlerTask> newTasks = null;

		while (true) {
			try {
				// Get next task
				task = host.dispatchTask();//中间做了好多事

				// No task available for now, take a rest
				if (task == null) {
					Thread.sleep(Config.getCrawlerIdleInterval());
					continue;
				}

				// Load site info
				currentSiteSetting = this.host.getSite(task.getSiteID()).getSetting();

				// Process task
				newTasks = processTask(task);

				// Save result back to host
				host.saveTaskResult(task, newTasks);

				// Clean up
				if (newTasks != null)
					newTasks.clear();

			} catch (Throwable e) {
				Logger.getInstance().error(
						String.format("Error duing processing task, %s",
								task == null ? "" : task.getUrl()), e);
			}
		}
	}

	private List<CrawlerTask> processTask(CrawlerTask task) {
		List<CrawlerTask> list = null;

		// Try to download
		Logger.getInstance().info(String.format("Downloading %s", task));

		task.setCrawlBeginTime(new Timestamp(new Date().getTime()));
		if (!downloadTask(task)) {
			if (!task.isFinished()) {
				task.setRetryCount(task.getRetryCount() + 1);
				if (task.getRetryCount() >= Config.getCrawlerMaxRetryCount()) {
					task.setStatus(Statuses.CrawlFail);
					task.setCrawlEndTime(new Timestamp(new Date().getTime()));
				}
			}

			return null;
		}

		Logger.getInstance().info(String.format("Parsing %s", task));
		list = parseContent(task);
		task.setStatus(Statuses.CrawlFinished);
		task.setCrawlEndTime(new Timestamp(new Date().getTime()));

		return list;
	}

	private boolean downloadTask(CrawlerTask task) {
		boolean ret = false;
		InputStream in = null;
		BufferedOutputStream out = null;
		byte[] buffer = new byte[4096];

		try {
			URLConnection conn = task.getUrl().openConnection();
			conn.setConnectTimeout(Config.getCrawlerConnectTimeout());
			conn.setReadTimeout(Config.getCrawlerReadTimeout());

			// Check file length, skip too large file
			int length = conn.getContentLength();
			if (length > Config.getCrawlerMaxContentLength()) {
				task.setStatus(Statuses.CrawlSkipped);
				task.setCrawlEndTime(new Timestamp(new Date().getTime()));

				Logger.getInstance()
						.info(String
								.format("Content length is too large, skip download, %d",
										length));
				return false;
			}

			// Check content-type, try to use cache for resources
			task.setContentType(conn.getContentType());

			File path = task.getTargetPath();
			if (!task.isContentText()) {
				if (path.exists() && path.length() == length) {
					Logger.getInstance().info(
							String.format("No change for %s, skip downloading",
									task));
					return true;
				}
			}

			in = conn.getInputStream();
			out = new BufferedOutputStream(new FileOutputStream(path));

			int count = 0;
			while ((count = in.read(buffer)) > 0) {
				out.write(buffer, 0, count);
			}

			ret = true;

		} catch (Exception e) {
			Logger.getInstance().warn(
					String.format("Fail to download %s", task), e);
		} finally {
			try {
				if (in != null)
					in.close();

				if (out != null)
					out.close();

			} catch (Exception e) {
			}
		}

		return ret;
	}

	private List<CrawlerTask> parseContent(CrawlerTask task) {
		// Check depth
		if (task.getDepth() >= this.currentSiteSetting.getMaxDepth())
			return null;

		// Check content type, no need to parse a non-text page
		if (!Utility.isContentTypeText(task.getContentType()))
			return null;

		List<CrawlerTask> tasks = new ArrayList<CrawlerTask>();
		Collection<String> urls = this.host.getParser(task.getSiteID()).parse(
				task.getTargetPath(), task.getUrl());
		if (urls == null || urls.isEmpty())
			return tasks;

		for (String url : urls) {
			CrawlerTask t = new CrawlerTask(task.getSiteID(),
					task.getBatchID(), url, task.getDepth() + 1,
					task.getCrawlerTaskID());
			// Remove invalid url
			if (t.getUrl() == null)
				continue;

			tasks.add(t);
		}

		return tasks;
	}
}
