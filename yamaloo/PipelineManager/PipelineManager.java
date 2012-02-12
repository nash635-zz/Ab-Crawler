package yamaloo.PipelineManager;

import java.net.URL;
import java.sql.Timestamp;
import java.util.*;
import java.util.Map.Entry;

import yamaloo.Common.*;

public class PipelineManager {
	public PipelineManager() {

	}

	public static void run() throws Throwable {
		DBManager db = new DBManager();
		List<Site> sites = db.getSiteList();//获取数据库中所有的site
		System.out.println(sites.size());
		Date now = new Date();
		for (Site site : sites) {
			Logger.getInstance().info(String.format("Processing Site [%d, %s]", site.getSiteID(),site.getName()));

			// Check whether to start a new crawl batch
			if (site.isEnabled()&& site.getNextRunTime().getTime() < now.getTime()) {
				boolean crawl = true;
				List<Batch> batches = db.getActiveBatchList(site.getSiteID());
				for (Batch batch : batches) {
					if (batch.getStatus() == Statuses.None
							|| batch.getStatus() == Statuses.Crawling) {
						crawl = false;
						break;
					}
				}

				if (crawl)
					createBatch(db, site);
			}

			// Process existing batches
			for (Batch batch : db.getActiveBatchList(site.getSiteID())) {
				refreshBatchStatus(db, site, batch);
			}
		}

		db.close();
	}

	private static void refreshBatchStatus(DBManager db, Site site, Batch batch)
			throws Throwable {
		Statuses status = batch.getStatus();
		Logger.getInstance().info(
				String.format("Batch [%d] previous status is %s",
						batch.getBatchID(), status.toString()));

		if (status == Statuses.None) {
			// Insert crawler seeds
			List<CrawlerTask> tasks = new ArrayList<CrawlerTask>();
			for (String seed : site.getSeeds()) {
				CrawlerTask task = new CrawlerTask(site.getSiteID(),
						batch.getBatchID(), seed);
				tasks.add(task);
			}

			db.insertCrawlerTaskList(tasks);

			// Update batch
			batch.setStatus(Statuses.Crawling);
			batch.setCrawlBeginTime((new Timestamp(new Date().getTime())));
			db.updateBatch(batch);

			// Update site run time
			site.setLastRunTime(batch.getCrawlBeginTime());
			site.setNextRunTime(new Timestamp(batch.getCrawlBeginTime()
					.getTime() + site.getInterval() * 60 * 1000));
			db.updateSite(site);
		} else if (status == Statuses.Crawling) {
			int count = db.getCrawlingTaskCount(batch.getBatchID());
			if (count == 0) {

				List<CrawlerTask> urls = db.getBatchTasks(batch.getBatchID());
				getStatistics(urls);

				batch.setStatus(Statuses.CrawlFinished);
				batch.setCrawlEndTime((new Timestamp(new Date().getTime())));
				db.updateBatch(batch);
			}
		} else {
		}

		Logger.getInstance().info(
				String.format("Batch [%d] current status is %s",
						batch.getBatchID(), batch.getStatus().toString()));
	}

	private static void createBatch(DBManager db, Site site) throws Throwable {
		// Create batch
		Batch batch = new Batch(site.getSiteID());
		batch.setStatus(Statuses.None);
		batch = db.getBatch(db.createBatch(batch));

		Utility.createDirectory(site.getResourceDir());
		Utility.createDirectory(batch.getBatchDir());

		Logger.getInstance().info("Batch created");
	}

	private static void getStatistics(List<CrawlerTask> list) throws Throwable {
		HashMap<String, Integer> urlStat = new HashMap<String, Integer>();
		for (CrawlerTask task : list) {
			URL url = task.getUrl();
			String key = url.getHost() + url.getPath();

			if (!urlStat.containsKey(key))
				urlStat.put(key, 1);
			else
				urlStat.put(key, urlStat.get(key) + 1);
		}

		List<Integer> counts = new ArrayList<Integer>();
		HashMap<Integer, HashSet<String>> countStat = new HashMap<Integer, HashSet<String>>();
		for (Entry<String, Integer> entry : urlStat.entrySet()) {
			if (entry.getValue() < 5)
				continue;

			counts.add(entry.getValue());

			if (!countStat.containsKey(entry.getValue()))
				countStat.put(entry.getValue(), new HashSet<String>());

			countStat.get(entry.getValue()).add(entry.getKey());
		}

		Collections.sort(counts, Collections.reverseOrder());

		int totalUrlCount = list.size();
		for (int count : counts) {
			for (String url : countStat.get(count))
				Logger.getInstance().info(
						String.format("%d\t(%f)\t%s", count, count * 100.0
								/ totalUrlCount, url));
		}
	}
}
