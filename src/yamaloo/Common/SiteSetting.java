package yamaloo.Common;

import java.io.File;

import org.ini4j.Profile.Section;
import org.ini4j.Wini;

public class SiteSetting {
	private int maxDepth = Config.getDefaultCrawlerMaxDepth();
	private float maxQPS = Config.getDefaultCrawlerMaxQPS();

	private String parserName = Config.getDefaultCrawlerParserName();
	private Section parserSetting;

	private String extractorName;
	private Section extractorSetting;
	private String picSavePath;
	private String BrandName;

	public SiteSetting(File path) throws Throwable {
		// Load crawler setting
//		System.out.println(path.toString());
		Wini ini = new Wini(path);
		Section section = ini.get("Crawler");
		if (section != null) {

			// Load site specific setting to overwrite default setting
			if (section.containsKey("MaxDepth")) {
				this.maxDepth = section.get("MaxDepth", int.class);
				Logger.getInstance().info(
						String.format("MaxDepth = %d", this.maxDepth));
			}
			
			if (section.containsKey("MaxQPS")) {
				this.maxQPS = section.get("MaxQPS", int.class);
				Logger.getInstance().info(
						String.format("maxQPS = %f", this.maxQPS));
			}
		}

		section = ini.get("Parser");
		if (section != null) {
			if (section.containsKey("Parser")) {
				this.parserName = section.get("Parser");
				this.parserSetting = section;
				Logger.getInstance().info(
						String.format("Parser = %s", this.parserName));
			}
		}

		section = ini.get("Extractor");
		if (section != null) {
			if (section.containsKey("Extractor")) {
				this.extractorName = section.get("Extractor");
				this.extractorSetting = section;
				Logger.getInstance().info(
						String.format("Extractor = %s", this.extractorName));
			}
		}
		//解析图片路径以及batchid
		section = ini.get("PicturePath");
		if (section != null) {
			// Load site specific setting to overwrite default setting
			if (section.containsKey("PicturePath")) {
				this.picSavePath = section.get("PicturePath");
				Logger.getInstance().info(
						String.format("PicturePath = %s", this.picSavePath));
			}
			if (section.containsKey("BrandName")) {
				this.BrandName = section.get("BrandName");
				Logger.getInstance().info(
						String.format("BrandName = %s", this.BrandName));
			}
		}
	}

	public int getMaxDepth() {
		return maxDepth;
	}

	public float getMaxQPS() {
		return maxQPS;
	}

	public long getMaxQueryInterval() {
		return (long) (1000.0 / this.maxQPS);
	}

	public String getParserName() {
		return this.parserName;
	}

	public Section getParserSetting() {
		return this.parserSetting;
	}

	public String getExtractorName() {
		return this.extractorName;
	}

	public Section getExtractorSetting() {
		return extractorSetting;
	}
	public String getPicSavePath(){
		return picSavePath;
	}
	public String getBrandName(){
		return BrandName;
	}
}
