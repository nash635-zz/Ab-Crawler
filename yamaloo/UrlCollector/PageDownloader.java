package yamaloo.UrlCollector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;

import yamaloo.Common.CrawlerTask;
import yamaloo.HtmlProcessor.SinglePageProcessor;

public class PageDownloader{
	private static final String saveroot = "Data\\Site\\";
	private String brand = "";
	private String savepath = "";
	private String pagecharset = "gbk";
	public void setBrand(String _brand, String charset){
		brand = _brand;
		savepath = saveroot + brand + "\\";
		pagecharset = charset;
		File dir = new File(savepath);
		if (dir.exists() && dir.isDirectory()) {
		        //目录存在
			System.out.println("path exists!");
		}else{
		       //不存在
			System.out.println("path doesn't exists! now created!");
			dir.mkdirs();
		}
	}
	
	public List<String> getUrlList(){
		List<String> urls = new ArrayList<String>();
		try {
			DBManager dm = new DBManager();
			System.out.println(brand);
			urls = dm.getUrlsFromDB(brand);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		for (String url : urls){
//			System.out.println(url);
//		}
		return urls;
	}
	public void saveListPages(List<String> urls){
		SinglePageProcessor spp = new SinglePageProcessor();
		String pageStr = "";
		int cnt = 0;
		for (String url : urls){
			pageStr = spp.setPage(url,pagecharset,true);
			spp.saveFile(savepath+"sites\\"+spp.pro.getMD5(url)+".html", pageStr);
			System.out.println("No." + ++cnt + " Save file : " + savepath+"sites\\"+spp.pro.getMD5(url)+".html");
		}
	}
	public void run(){
		setBrand("Gap","gbk");
		List<String> listurl = getUrlList();
		saveListPages(listurl);
		System.out.println("done!");
	}
	public static void main(String[] args){
		PageDownloader pd = new PageDownloader();
		pd.run();
	}
}