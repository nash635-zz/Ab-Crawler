package yamaloo.UrlCollector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;

import yamaloo.Common.Site;

public class SinglePageCollector{
    private static final String DEFAULT_ROOT_URL = 	"http://gap.tmall.com/shop/view_shop.htm?prc=2&search=y&orderType=_hotkeep&viewType=grid&pageNum=1";
    private String url2process;
    private int page_num;

    private String getPage(String url, String charset){
    	url2process = url;
    	String ret = "";
		HttpClient client = new HttpClient();
		GetMethod get = new GetMethod(url);
		get.getParams().setContentCharset(charset);
		try {
			client.executeMethod(get);
			ret = get.getResponseBodyAsString();
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			get.releaseConnection();
		}
		return ret;
    }
    // regx : <a href="(http://item.tmall.com/item.htm\?id=[\w\W]*?)"\starget="_blank">
    public List<String> getProductUrls(String pageurl,String Brand,String charset){
    	String page = getPage(pageurl,charset);
    	List<String> urlset = new ArrayList<String>();
		String regx = "<ul class=\"shop-list\">([\\w\\W]*?)</ul>";
		Matcher m = Pattern.compile(regx).matcher(page);
		String tempStr = "";
		while(m.find())
		{
			tempStr += m.group();
		}
//		System.out.println(page);
//		System.out.println(tempStr);
		regx = "<a href=\"(http://item.tmall.com/item.htm\\?id=[\\w\\W]*?)\"\\starget=\"_blank\">";
		m = Pattern.compile(regx).matcher(tempStr);
		while(m.find())
		{
			System.out.println(m.group(1));
			urlset.add(m.group(1));
		}
		save2DB(urlset,Brand);
    	return urlset;
    }
    private void save2DB(List<String> urlset,String Brand)
    {
    	try {
			DBManager dm = new DBManager();
			dm.insertPageUrls(urlset, Brand);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public static void main(String[] args){
    	SinglePageCollector sc = new SinglePageCollector();
    	String page = sc.getPage(DEFAULT_ROOT_URL, "gbk");
    	sc.getProductUrls(page,"Gap","gbk");
    	System.out.println("Completed...");
//    	System.out.println(sc.getPage(DEFAULT_ROOT_URL, "gbk"));
    }
}