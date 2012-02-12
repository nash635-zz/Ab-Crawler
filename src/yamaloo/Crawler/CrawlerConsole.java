package yamaloo.Crawler;

public class CrawlerConsole {

	/**
	 * @param args
	 * @throws Throwable
	 */
	public static void main(String[] args) throws Throwable {
		CrawlerHost host = new CrawlerHost(1024);
		host.execute();
	}
}