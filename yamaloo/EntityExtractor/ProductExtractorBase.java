package yamaloo.EntityExtractor;

import java.util.HashMap;
import java.util.List;

import org.ini4j.Profile.Section;

import yamaloo.Common.*;

public abstract class ProductExtractorBase implements IEntityExtractor {

	private Site site;
	private List<CrawlerTask> tasks;
	private HashMap<String, CrawlerTask> urlMap;
	protected String picSavePath = "";
	protected String brandName = "";
	protected String brandID = "";
	protected DBManager db = null;
	@Override
	public boolean initialize(SiteSetting setting) throws Throwable {	
		picSavePath = setting.getPicSavePath();
		brandName = setting.getBrandName();
		DBManager db = new DBManager();
		brandID = db.getBrandID(brandName);
		db.close();
		return true;
	}

	@Override
	public final int extract(int batchID) throws Throwable {
		getBatchTasks(batchID);
		int cnt = extractProduct();
		return cnt;
//		saveProduct(list);
//		return list.size();
	}

	private void getBatchTasks(int batchID) throws Throwable {
		DBManager db = new DBManager();
		tasks = db.getBatchTasks(batchID);

		urlMap = new HashMap<String, CrawlerTask>();
		for (CrawlerTask task : tasks) {
			urlMap.put(task.getUrlHash(), task);
		}

		db.close();
	}

	protected int extractProduct() throws Throwable {
		return 0;
	}

	private void saveProduct(List<Product> list) throws Throwable {
		DBManager db = new DBManager();
		db.insertProductList(list);
		db.close();
	}

	public final List<CrawlerTask> getTasks() {
		return tasks;
	}

	public final HashMap<String, CrawlerTask> getUrlMap() {
		return urlMap;
	}

	public Site getSite() {
		return site;
	}
}
