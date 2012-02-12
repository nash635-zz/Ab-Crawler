package yamaloo.EntityExtractor;

import yamaloo.Common.*;

public class ExtractorHost {
	private ExtractorHost() {

	}

	public static int extract(int batchID) throws Throwable {
		
		Logger.getInstance().info(String.format("Extracting batch %d", batchID));
		
		DBManager db = new DBManager();
		Batch batch = db.getBatch(batchID);
		Site site = db.getSite(batch.getSiteID());
		SiteSetting setting = site.getSetting();
		Logger.getInstance().info(String.format("Site setting loaded, %s", site.getName()));
		db.close();
		
		
		String extractorName = setting.getExtractorName();
		if (extractorName == null || extractorName.isEmpty())
		{
			Logger.getInstance().error("Extractor is not set");
			return -1;
		}
		
		IEntityExtractor extractor = createExtractor(setting.getExtractorName());
		if (extractor == null)
		{
			Logger.getInstance().error("Fail to create extractor instance");
			return -1;
		}
		if (setting.getPicSavePath() == "")
		{
			Logger.getInstance().error("Fail to get picture save path");
			return -1;
		}
		if (setting.getBrandName() == "")
		{
			Logger.getInstance().error("Fail to get BrandName");
			return -1;
		}
//		extractor.initialize(setting.getExtractorSetting());
		extractor.initialize(setting);
		int count = extractor.extract(batchID);
		Logger.getInstance().info(String.format("%d products extracted", count));
		
		return count;
	}
	
	private static IEntityExtractor createExtractor(String name) throws Throwable {

		String fullName = "yamaloo.EntityExtractor." + name;
		Class<?> c = Class.forName(fullName);
		return (IEntityExtractor) c.newInstance();
	}
}
