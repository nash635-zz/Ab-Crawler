package yamaloo.Common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DBManager {
	private Connection conn;

	public DBManager() throws Throwable {
		this("root", "acm", "jdbc:mysql://localhost:3306/yamaloo");
	}

	public DBManager(String userName, String password, String url)
			throws Throwable {
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		conn = DriverManager.getConnection(url, userName, password);
	}

	public Connection getConn() {
		return conn;
	}

	public void close() throws Throwable {
		conn.close();
	}

	public int insertSite(Site site) throws Throwable {
		PreparedStatement statement = conn
				.prepareStatement(
						"INSERT INTO `yamaloo`.`site` (`Name`,`DirectoryName`,`Enabled`,`Interval`,`LastRunTime`,`NextRunTime`,`Priority`) VALUES(?,?,?,?,?,?,?);",
						PreparedStatement.RETURN_GENERATED_KEYS);
		statement.setString(1, site.getName());
		statement.setString(2, site.getDirectoryName());
		statement.setBoolean(3, site.isEnabled());
		statement.setInt(4, site.getInterval());
		statement.setTimestamp(5, site.getLastRunTime());
		statement.setTimestamp(6, site.getNextRunTime());
		statement.setInt(7, site.getPriority());

		statement.executeUpdate();
		ResultSet rs = statement.getGeneratedKeys();
		rs.next();
		int siteID = rs.getInt(1);
		statement.close();

		return siteID;
	}

	public Site getSite(int siteID) throws Throwable {
		PreparedStatement statement = conn
				.prepareStatement("SELECT * FROM `yamaloo`.`site` WHERE `site`.`SiteID` = ?");
		statement.setInt(1, siteID);

		ResultSet rs = statement.executeQuery();
		rs.next();
		Site site = Site.Parse(rs);
		statement.close();

		return site;
	}

	public void updateSite(Site site) throws Throwable {
		PreparedStatement statement = conn
				.prepareStatement("UPDATE `yamaloo`.`site` SET `LastRunTime` = ?, `NextRunTime` = ? WHERE `SiteID` = ?");
		statement.setTimestamp(1, site.getLastRunTime());
		statement.setTimestamp(2, site.getNextRunTime());
		statement.setInt(3, site.getSiteID());

		statement.executeUpdate();
		statement.close();
	}

	public List<Site> getSiteList() throws Throwable {
		Statement statement = conn.createStatement();
		ResultSet rs = statement.executeQuery("SELECT * FROM `yamaloo`.`site`");

		List<Site> list = new ArrayList<Site>();
		while (rs.next()) {
			Site site = Site.Parse(rs);
			list.add(site);
		}

		statement.close();
		return list;
	}

	public int createBatch(Batch batch) throws Throwable {
		PreparedStatement statement = conn
				.prepareStatement(
						"INSERT INTO `yamaloo`.`batch` (`SiteID`, `Status`) VALUES (?, ?);",
						PreparedStatement.RETURN_GENERATED_KEYS);
		statement.setInt(1, batch.getSiteID());
		statement.setString(2, batch.getStatus().toString());

		statement.executeUpdate();
		ResultSet rs = statement.getGeneratedKeys();
		rs.next();
		int batchID = rs.getInt(1);
		statement.close();

		return batchID;
	}

	public Batch getBatch(int batchID) throws Throwable {
		System.out.println(batchID);
		PreparedStatement statement = conn
				.prepareStatement("SELECT * FROM `yamaloo`.`batch` WHERE `batch`.`BatchID` = ?");
		statement.setInt(1, batchID);

		ResultSet rs = statement.executeQuery();
		rs.next();
		Batch batch = Batch.Parse(rs);
		statement.close();

		return batch;
	}

	public void updateBatch(Batch batch) throws Throwable {
		PreparedStatement statement = conn
				.prepareStatement("UPDATE `yamaloo`.`batch` SET `Status` = ?, `CrawlBeginTime` = ?, `CrawlEndTime` = ? WHERE `BatchID` = ?");
		statement.setString(1, batch.getStatus().toString());
		statement.setTimestamp(2, batch.getCrawlBeginTime());
		statement.setTimestamp(3, batch.getCrawlEndTime());
		statement.setInt(4, batch.getBatchID());

		statement.executeUpdate();
		statement.close();
	}

	public List<Batch> getActiveBatchList(int siteID) throws Throwable {
		PreparedStatement statement = conn
				.prepareStatement("SELECT b.* FROM `yamaloo`.`batch` AS b"
						+ " INNER JOIN `yamaloo`.`site` AS s"
						+ " ON b.SiteID = s.SiteID"
						+ " WHERE b.`SiteID` = ? AND b.`Status` IN ('None', 'Crawling')"
						+ " ORDER BY s.Priority");
		statement.setInt(1, siteID);

		List<Batch> list = new ArrayList<Batch>();
		ResultSet rs = statement.executeQuery();

		while (rs.next()) {
			Batch batch = Batch.Parse(rs);
			list.add(batch);
		}

		statement.close();
		return list;
	}

	public int getCrawlingTaskCount(int batchID) throws Throwable {
		PreparedStatement statement = conn
				.prepareStatement("SELECT COUNT(*) FROM `yamaloo`.`crawlertask` WHERE BatchID = ? AND Status IN (?, ?)");
		statement.setInt(1, batchID);
		statement.setString(2, Statuses.None.toString());
		statement.setString(3, Statuses.Crawling.toString());

		ResultSet rs = statement.executeQuery();

		int count = -1;
		rs.next();
		count = rs.getInt(1);

		statement.close();
		return count;
	}

	public void insertCrawlerTaskList(Iterable<CrawlerTask> list)
			throws Throwable {
		conn.setAutoCommit(false);
		PreparedStatement statement = conn
				.prepareStatement("INSERT INTO `yamaloo`.`crawlertask`"
						+ " (`BatchID`, `UrlHash`, `Url`, `Depth`, `ParentTaskID`, `Status`, `CreateTime`)"
						+ " SELECT ?, ?, ?, ?, ?, ?, ? FROM dual"
						+ " WHERE not exists"
						+ " (select * from `yamaloo`.`crawlertask` where BatchID = ? AND UrlHash = ?); ");

		int count = 0;
		for (CrawlerTask task : list) {
			statement.setInt(1, task.getBatchID());
			statement.setString(2, task.getUrlHash());
			statement.setString(3, task.getUrl().toString());
			statement.setInt(4, task.getDepth());
			statement.setInt(5, task.getParentTaskID());
			statement.setString(6, task.getStatus().toString());
			statement.setTimestamp(7, task.getCreateTime());
			statement.setInt(8, task.getBatchID());
			statement.setString(9, task.getUrlHash());

			statement.addBatch();
			count++;

			if (count >= 1000) {
				count = 0;
				statement.executeBatch();
				conn.commit();
			}
		}

		statement.executeBatch();
		conn.commit();

		statement.close();
		conn.setAutoCommit(true);
	}

	public void updateCrawlerTaskList(List<CrawlerTask> list) throws Throwable {
		conn.setAutoCommit(false);
		PreparedStatement statement = conn
				.prepareStatement("UPDATE `yamaloo`.`crawlertask`"
						+ " SET `Status` = ?," + " `CrawlBeginTime` = ?,"
						+ " `CrawlEndTime` = ?," + " `RetryCount` = ?,"
						+ " `ContentType` = ?" + " WHERE CrawlerTaskID = ?");

		int count = 0;
		for (CrawlerTask task : list) {
			statement.setString(1, task.getStatus().toString());
			statement.setTimestamp(2, task.getCrawlBeginTime());
			statement.setTimestamp(3, task.getCrawlEndTime());
			statement.setInt(4, task.getRetryCount());
			statement.setString(5, task.getContentType());
			statement.setInt(6, task.getCrawlerTaskID());

			statement.addBatch();
			count++;

			if (count >= 1000) {
				count = 0;
				statement.executeBatch();
				conn.commit();
			}
		}

		statement.executeBatch();
		conn.commit();

		statement.close();
		conn.setAutoCommit(true);
	}

	// TODO: Priority field is not used yet, fuck mysql
	// Order by RAND() to shuttle Urls from same site
	// Otherwise, you may have a package full of Urls from the same site, which
	// will slow the performance due to QPS limit
	public List<CrawlerTask> getCrawlerTaskPackage(int crawlerID, int count)
			throws Throwable {
		PreparedStatement statement = conn
				.prepareStatement("UPDATE `yamaloo`.`crawlertask`"
						+ " SET `Status` = ?," + " `CrawlerID` = ?"
						+ " WHERE `Status` = ?" + " ORDER BY Depth, RAND()"
						+ " LIMIT ?");
		statement.setString(1, Statuses.Crawling.toString());
		statement.setInt(2, crawlerID);
		statement.setString(3, Statuses.None.toString());
		statement.setInt(4, count);
		statement.execute();
		statement.close();

		// FIFO
		statement = conn
				.prepareStatement("SELECT B.SiteID, T.*"
						+ " FROM (SELECT * FROM `yamaloo`.`crawlertask` WHERE `Status` = ? AND `CrawlerID` = ?) AS T"
						+ " INNER JOIN `yamaloo`.`batch` AS B"
						+ " ON T.BatchID = B.BatchID"
						+ " ORDER BY T.Depth, CrawlerTaskID; ");

		statement.setString(1, Statuses.Crawling.toString());
		statement.setInt(2, crawlerID);

		List<CrawlerTask> list = new ArrayList<CrawlerTask>();
		ResultSet rs = statement.executeQuery();

		while (rs.next()) {
			CrawlerTask task = CrawlerTask.Parse(rs);
			list.add(task);
		}

		statement.close();
		return list;
	}
//crawlertaskdetailview
	public List<CrawlerTask> getBatchTasks(int batchID) throws Throwable {
		PreparedStatement statement = conn
				.prepareStatement("SELECT * FROM `yamaloo`.`crawlertaskdetailview` WHERE `BatchID` = ?");
		statement.setInt(1, batchID);

		List<CrawlerTask> list = new ArrayList<CrawlerTask>();
		ResultSet rs = statement.executeQuery();
		
		while (rs.next()) {
			CrawlerTask task = CrawlerTask.Parse(rs);
			list.add(task);
		}

		statement.close();
		return list;
	}

	public void insertProductList(List<Product> products) throws Throwable {
		conn.setAutoCommit(false);
		PreparedStatement statement = conn
				.prepareStatement("INSERT INTO `yamaloo`.`product`"
						+ " (`ProductName`,`BrandID`, `Description`, `MainPic`, `Price`,`RawSerialNo`)"
						+ " VALUES (?,?,?,?,?,?);");

		for (Product product : products) {
			statement.setString(1, product.getName());
			statement.setString(2, product.getDescription());
			statement.setString(3, product.getBrandID());
			statement.setString(4, product.getpictureURL());
			statement.setString(5, product.getPrice());
			statement.setString(6, product.getRawSerialNumber());

			statement.addBatch();
		}

		statement.executeBatch();
		conn.commit();

		statement.close();
		conn.setAutoCommit(true);
	}
	public void insertProduct(Product product) throws Throwable {
		conn.setAutoCommit(false);
		PreparedStatement statement = conn
				.prepareStatement("INSERT INTO `yamaloo`.`product`"
						+ " (`ProductName`,`BrandID`, `Description`, `MainPic`, `Price`,`RawSerialNo`)"
						+ " VALUES (?,?,?,?,?,?);");

		statement.setString(1, product.getName());
		statement.setString(2, product.getBrandID());
		statement.setString(3, product.getDescription());
		statement.setString(4, product.getpictureURL());
		statement.setString(5, product.getPrice());
		statement.setString(6, product.getRawSerialNumber());
		statement.addBatch();
		statement.executeBatch();
		conn.commit();

		statement.close();
		conn.setAutoCommit(true);
	}
	public String getBrandID(String brandName) throws Throwable {
		PreparedStatement statement = conn
				.prepareStatement("SELECT * FROM `yamaloo`.`brand` WHERE `brand`.`BrandName` = ?");
		statement.setString(1, brandName);

		ResultSet rs = statement.executeQuery();
		rs.next();
		String brandid = rs.getBigDecimal(1).toString();
		statement.close();

		return brandid;
	}
	public static void main(String[] args) throws Throwable{
		DBManager db = new DBManager();
		System.out.println(db.getBrandID("Only"));
		db.close();
	}
}
