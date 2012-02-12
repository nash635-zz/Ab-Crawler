package yamaloo.HtmlProcessor;
import yamaloo.HtmlProcessor.Config;
public class HtmlProcessorConsole {
	/**
	 * @param args
	 * @throws Throwable
	 */
	
	public static void main(String[] args) throws Throwable {
		Config config = new Config();
		PageProcessor pp = new PageProcessor();
		pp.batchPages(config.getBrandPath()+"sites\\");
	}
}