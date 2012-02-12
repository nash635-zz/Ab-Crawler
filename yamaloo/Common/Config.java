package yamaloo.Common;

import java.io.File;

public class Config {
	private final static File dataRoot;
	private final static File logRoot;
	private final static File siteRoot;
	private final static File batchRoot;
	private final static File resourceRoot;

	private final static int defaultSitePriority;
	private final static int defaultCrawlerMaxDepth;
	private final static float defaultCrawlerMaxQPS;
	private final static String defaultCrawlerParserName;

	private final static int crawlerPackagePullLength;
	private final static int crawlerCountPerHost;
	private final static int crawlerHostIdleInterval;
	private final static int crawlerIdleInterval;
	private final static int crawlerMaxRetryCount;
	private final static int crawlerRetryInterval;

	private final static int crawlerConnectTimeout;
	private final static int crawlerReadTimeout;
	private final static int crawlerMaxContentLength;

	private Config() {
	}

	static {
		dataRoot = new File("Data\\").getAbsoluteFile();
		logRoot = new File(dataRoot, "Logs");
		siteRoot = new File(dataRoot, "Site");
		batchRoot = new File(dataRoot, "Batch");
		resourceRoot = new File(dataRoot, "Resource");

		defaultSitePriority = 1000;
		defaultCrawlerMaxDepth = 10;
		defaultCrawlerMaxQPS = 3;
		defaultCrawlerParserName = "GeneralHtmlParser";

		crawlerPackagePullLength = 100;
		crawlerCountPerHost = 2;
		crawlerHostIdleInterval = 1000 * 30;
		crawlerIdleInterval = (int) (500 / defaultCrawlerMaxQPS);
		crawlerMaxRetryCount = 5;
		crawlerRetryInterval = 1000 * 60;

		crawlerConnectTimeout = 1000 * 60;
		crawlerReadTimeout = 1000 * 60;
		crawlerMaxContentLength = 5 * 1024 * 1024;
	}

	public static int getDefaultSitePriority() {
		return defaultSitePriority;
	}

	public static File getDataRoot() {
		return dataRoot;
	}

	public static File getLogRoot() {
		return logRoot;
	}

	public static File getSiteRoot() {
		return siteRoot;
	}

	public static File getBatchRoot() {
		return batchRoot;
	}

	public static File getResourceRoot() {
		return resourceRoot;
	}

	public static int getCrawlerPackagePullLength() {
		return crawlerPackagePullLength;
	}

	public static int getCrawlerCountPerHost() {
		return crawlerCountPerHost;
	}

	public static int getCrawlerHostIdleInterval() {
		return crawlerHostIdleInterval;
	}

	public static int getCrawlerIdleInterval() {
		return crawlerIdleInterval;
	}

	public static int getCrawlerMaxRetryCount() {
		return crawlerMaxRetryCount;
	}

	public static int getCrawlerRetryInterval() {
		return crawlerRetryInterval;
	}

	public static int getDefaultCrawlerMaxDepth() {
		return defaultCrawlerMaxDepth;
	}

	public static float getDefaultCrawlerMaxQPS() {
		return defaultCrawlerMaxQPS;
	}

	public static String getDefaultCrawlerParserName() {
		return defaultCrawlerParserName;
	}

	public static int getCrawlerConnectTimeout() {
		return crawlerConnectTimeout;
	}

	public static int getCrawlerReadTimeout() {
		return crawlerReadTimeout;
	}

	public static int getCrawlerMaxContentLength() {
		return crawlerMaxContentLength;
	}
}
