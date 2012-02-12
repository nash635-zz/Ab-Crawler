package yamaloo.UrlCollector;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlCollector{
	private List<String> pages = new ArrayList<String>();
	private SinglePageCollector sc = new SinglePageCollector();
	private static final String DEFAULT_ROOT = "http://gap.tmall.com/shop/view_shop.htm?prc=2";
	private static final String BRAND = "Gap";
	private static String CHARSET = "gbk";
	public List<String> getRootUrls(String homepage)
	{
		List<String> rooturl = new ArrayList<String>();
		String regx = "<map name=\"class\">[\\w\\W]*?</map>";
		return rooturl;
	}
	public int processSubRoot(String subroot,String Brand,int totalpage)
	{
		int num = 0;
		String page_1 = "";
		String page_2 = "";
		String re = "(http://[\\s\\S]*?)pageNum=\\d([\\s\\S]*)";
		Matcher m = Pattern.compile(re).matcher(subroot);
		while(m.find())
		{
			page_1 = m.group(1);
			page_2 = m.group(2);
		}
		for(Integer i=1;i<=totalpage;i++)
		{
			String currentpage = page_1 + "pageNum=" + i.toString() + page_2;
			
			sc.getProductUrls(currentpage,Brand,CHARSET);
			System.out.println(currentpage);
		}
		return num;
	}
	public static void main(String[] args)
	{
		UrlCollector uc = new UrlCollector();
		uc.processSubRoot("http://gap.tmall.com/shop/view_shop.htm?prc=1&orderType=_hotkeep&search=y&viewType=grid&pageNum=1",BRAND, 36);
	}
}