package yamaloo.Common;

import java.io.File;
import java.sql.ResultSet;
import java.sql.Timestamp;

public class Batch {

	private int batchID = -1;
	private int siteID = -1;
	private Statuses status = Statuses.None;
	private Timestamp crawlBeginTime;
	private Timestamp crawlEndTime;

	public Batch(int siteID) {
		this.siteID = siteID;
	}

	public int getBatchID() {
		return batchID;
	}

	public int getSiteID() {
		return siteID;
	}

	public Statuses getStatus() {
		return status;
	}

	public void setStatus(Statuses status) {
		this.status = status;
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
	
	public File getBatchDir()
	{
		if (this.batchID <= 0)
			return null;
		
		return Utility.getBatchDir(this.batchID);
	}
	

	public static Batch Parse(ResultSet rs) throws Throwable {
		//Illegal operation on empty result set.这种异常没有处理
//		System.out.println(rs.getInt("SiteID"));
		Batch batch = new Batch(rs.getInt("SiteID"));
		batch.batchID = rs.getInt("BatchID");
		batch.status = Statuses.valueOf(rs.getString("Status"));
		batch.crawlBeginTime = rs.getTimestamp("CrawlBeginTime");
		batch.crawlEndTime = rs.getTimestamp("CrawlEndTime");
		return batch;
	}
}
