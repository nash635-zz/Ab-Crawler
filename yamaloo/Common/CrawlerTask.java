package yamaloo.Common;

import java.io.File;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;

public class CrawlerTask {
	private int crawlerTaskID;
	private int siteID = -1;
	private int batchID;
	private String urlHash;
	private URL url;
	private int depth;
	private int parentTaskID;
	private Statuses status;
	private Timestamp createTime;
	private Timestamp crawlBeginTime;
	private Timestamp crawlEndTime;
	private int crawlerID = 0;
	private int retryCount = 0;
	private String contentType;

	public CrawlerTask(int siteID, int batchID, String url, int depth,
			int parentTaskID) {
		this.siteID = siteID;
		this.batchID = batchID;
		this.url = Utility.normalizeUrl(url);
		this.urlHash = Utility.getUrlHash(this.url);
		this.depth = depth;
		this.parentTaskID = parentTaskID;
		this.status = Statuses.None;
		this.createTime = new Timestamp(new Date().getTime());
	}

	public CrawlerTask(int siteID, int batchID, String url, int depth) {
		this(siteID, batchID, url, depth, -1);
	}

	public CrawlerTask(int siteID, int batchID, URL url, int depth) {
		this(siteID, batchID, url.toString(), depth);
	}

	public CrawlerTask(int siteID, int batchID, String url) {
		this(siteID, batchID, url, 0);
	}

	public CrawlerTask(int siteID, int batchID, URL url) {
		this(siteID, batchID, url.toString(), 0);
	}

	// For Parsing from ResultSet
	private CrawlerTask() {

	}

	public int getCrawlerTaskID() {
		return crawlerTaskID;
	}

	public void setCrawlerTaskID(int crawlerTaskID) {
		this.crawlerTaskID = crawlerTaskID;
	}

	public int getSiteID() {
		return siteID;
	}

	public int getBatchID() {
		return batchID;
	}

	public String getUrlHash() {
		return urlHash;
	}

	public URL getUrl() {
		return url;
	}

	public int getDepth() {
		return depth;
	}

	public int getParentTaskID() {
		return parentTaskID;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public Statuses getStatus() {
		return status;
	}

	public void setStatus(Statuses status) {
		this.status = status;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public Timestamp getCrawlBeginTime() {
		return crawlBeginTime;
	}

	public void setCrawlBeginTime(Timestamp crawlBeginTime) {
		this.crawlBeginTime = crawlBeginTime;
	}

	public Timestamp getCrawlEndTime() {
		return crawlEndTime;
	}

	public void setCrawlEndTime(Timestamp crawlEndTime) {
		this.crawlEndTime = crawlEndTime;
	}

	public int getCrawlerID() {
		return crawlerID;
	}

	public int getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

	public File getTargetPath() {
		File dir = this.isContentText() ? Utility.getBatchDir(this.batchID)
				: Utility.getResourceDir(this.siteID);
		return new File(dir, String.format("%s.html", this.urlHash));
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public boolean isContentText() {
		return Utility.isContentTypeText(this.contentType);
	}

	public static CrawlerTask Parse(ResultSet rs) throws Throwable {
		CrawlerTask task = new CrawlerTask();
		task.crawlerTaskID = rs.getInt("CrawlerTaskID");
		task.siteID = rs.getInt("SiteID");
		task.batchID = rs.getInt("BatchID");
		task.urlHash = rs.getString("UrlHash");
		task.url = new URL(rs.getString("Url"));
		task.depth = rs.getInt("Depth");
		task.parentTaskID = rs.getInt("ParentTaskID");
		task.status = Statuses.valueOf(rs.getString("Status"));
		task.createTime = rs.getTimestamp("CreateTime");
		task.crawlBeginTime = rs.getTimestamp("CrawlBeginTime");
		task.crawlEndTime = rs.getTimestamp("CrawlEndTime");
		task.crawlerID = rs.getInt("CrawlerID");
		task.retryCount = rs.getInt("RetryCount");
		task.contentType = rs.getString("ContentType");
		return task;
	}

	@Override
	public String toString() {
		return String.format("<%d, %s, %s, %d>", this.crawlerTaskID, this.url,
				this.urlHash, this.parentTaskID);
	}

	public boolean isFinished() {
		if (this.status == Statuses.CrawlFail
				|| this.status == Statuses.CrawlFinished
				|| this.status == Statuses.CrawlSkipped)
			return true;

		return false;
	}
}
