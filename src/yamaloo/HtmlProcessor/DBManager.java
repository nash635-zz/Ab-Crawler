package yamaloo.HtmlProcessor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import yamaloo.Common.*;

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

	public void insertProductList(ProductItem pro) throws Throwable {
		conn.setAutoCommit(false);
		PreparedStatement statement = conn
				.prepareStatement("INSERT INTO `yamaloo`.`products`"
						+ " (`ProductName`, `Brand`, `Price`, `SerialNumber`, `ProductDetail`, `PictureUrl`, `ProductPageUrl`)"
						+ " VALUES (?,?,?,?,?,?,?);");

		statement.setString(1, pro.ProductName);
		statement.setString(2, pro.Brand);
		statement.setString(3, pro.Price);
		statement.setString(4, pro.SerialNumber);
		statement.setString(5, pro.ProductDetail);
		statement.setString(6, pro.PictureUrl);
		statement.setString(7, pro.ProductPageUrl);
		statement.addBatch();
		statement.executeBatch();
		conn.commit();

		statement.close();
		conn.setAutoCommit(true);
	}
}
