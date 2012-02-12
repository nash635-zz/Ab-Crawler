package yamaloo.HtmlProcessor;

public class Config {
	private static final String Default_URL = "http://item.taobao.com/item.htm?id=12804888975";
	private static final boolean IS_ONLINE = false;
	private static final String CHAR_SET = "gbk";
	private static final String SITE_PATH = "Data\\Site\\";
	private static final String CURRENT_BRAND = "Only";
	public String getDefaultUrl(){
		return Default_URL;
	}
	public boolean getIsOnline(){
		return IS_ONLINE;
	}
	public String getCharSet(){
		return CHAR_SET;
	}
	public String getSitePath(){
		return SITE_PATH;
	}
	public String getCurrentBrand(){
		return CURRENT_BRAND;
	}
	public String getBrandPath(){
		return new String(SITE_PATH + CURRENT_BRAND + "\\"); 
	}
}
