package yamaloo.UrlCollector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
	public void insertPageUrls(List<String> urls,String Brand) throws Throwable {
		conn.setAutoCommit(false);
		PreparedStatement statement = conn
		.prepareStatement("INSERT INTO `yamaloo`.`pageurls`"
				+ " (`Brand`, `url`)"
				+ "VALUES (?,?);");
		System.out.println(urls.size());
		for (String url : urls)
		{
			statement.setString(1,Brand);
			statement.setString(2, url);
			statement.addBatch();
		}
		statement.addBatch();
		statement.executeBatch();
		conn.commit();

		statement.close();
		conn.setAutoCommit(true);
	}
	//"SELECT * FROM `yamaloo`.`site` WHERE `site`.`SiteID` = ?"
	public List<String> getUrlsFromDB(String brand) throws Throwable {
		PreparedStatement statement = conn
				.prepareStatement("SELECT * FROM `yamaloo`.`pageurls` WHERE `Brand` = ?");

		statement.setString(1, brand);// 1 for the first '?'
		List<String> list = new ArrayList<String>();
		ResultSet rs = statement.executeQuery();
		while (rs.next()) {
			String url = rs.getString("url");
			list.add(url);
		}

		statement.close();
		return list;
	}
}
