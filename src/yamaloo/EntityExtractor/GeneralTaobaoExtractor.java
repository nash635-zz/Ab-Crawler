package yamaloo.EntityExtractor;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import yamaloo.Common.CrawlerTask;
import yamaloo.Common.DBManager;
import yamaloo.Common.IEntityExtractor;
import yamaloo.Common.Product;
import yamaloo.Common.Utility;

public class GeneralTaobaoExtractor extends ProductExtractorBase implements
IEntityExtractor{
	private final Pattern namePattern = Pattern.compile(
			"<input type=\"hidden\" name=\"title\" value=\"([\\s\\S]*?)\"", Pattern.MULTILINE);
	private final Pattern descriptionPattern = Pattern.compile(
			"<li title=\"[^<li title=]*?\">([^</li>]+):[&nbsp;]*([\\w\\W]*?)</li>", Pattern.MULTILINE);
	private static String[] desc=new String[]{
		  "产品名称",
		  "货号",
		  "品牌",
	};
//	private DBManager db = null;
//	private List<Product> products = new ArrayList<Product>();
	public int extractProduct() throws Throwable {
		Pattern pattern = Pattern.compile("http://item.taobao.com/item.htm\\?id=[\\w\\W]{1,1024}");
		int cnt = 0;
		db = new DBManager();
		for (CrawlerTask task : this.getTasks()) {
			Matcher matcher = pattern.matcher(task.getUrl().toString());
			if (!matcher.find())
				continue;
//			System.out.println(++cnt + " : " + task.getUrl().toString());
			System.out.print(++cnt + " : ");
			extractDetailPage(task);
		}
		db.close();
		return cnt;
	}

	private void extractDetailPage(CrawlerTask task) throws Throwable {
		String content = Utility.readAllText(task.getTargetPath());

		String name = "";
		Matcher matcher = namePattern.matcher(content);
		if (matcher.find())
			name = matcher.group(1);
		System.out.println(name);
		String description = getDetails(content);
		System.out.println(description);
		Product product = new Product(name, description);
		
		String tempStr="";
		String regx = "";
		for(int i=0;i<desc.length;i++)
	    {
			regx = "<li title=\"[^<li title=]*?\">" + desc[i] + "[\\w\\W][&nbsp;]*([\\w\\W]*?)</li>";
			Matcher m = Pattern.compile(regx).matcher(content);
			tempStr = "";
			while(m.find())
			{
				tempStr=m.group(1);
				tempStr = Trim(tempStr);
//				System.out.println(desc[i]+" : " +tempStr);
				if(i==1)
				{
					product.setRawSerialNumber(tempStr);
				}
				else if(i==2)
				{
					product.setBrandID(brandID);
/*					if(brandName == "")
						product.setBrandID(db.getBrandID(tempStr));
					else
						product.setBrandID(db.getBrandID(brandName));*/
				}
				else
				{
					continue;
				}
			}
	    }
		//获取价格信息
		regx = "<strong id=\"J_StrPrice\" >([\\s\\S]*?)</strong>";
		Matcher m1 = Pattern.compile(regx).matcher(content);
		while(m1.find())
		{
			tempStr=m1.group(1);
			tempStr=Trim(tempStr);
			product.setPrice(tempStr);
		}
		//获取图片url
		regx = "<img id=\"J_ImgBooth\" src=\"(http://[\\s\\S]*?.jpg)\"";
		Matcher m2 = Pattern.compile(regx).matcher(content);
		while(m2.find())
		{
			tempStr=m2.group(1);
			tempStr=Trim(tempStr);
			product.setpictureURL(tempStr);
			product.setMainPicPath(picSavePath + product.getMD5(tempStr) + ".jpg");
//			System.out.println("图片网址"+" : " + tempStr);
			downLoadPic(product.getpictureURL(),product.getMD5(tempStr)+".jpg",picSavePath);
		}
		product.setBrandID(brandID);//保险起见再插入一遍
		db.insertProduct(product);
//		products.add(product);
	}

	private String getDetails(String page)
	{
		String ret = "";
		String tempStr="";
		Matcher m = descriptionPattern.matcher(page);
		tempStr = "";
		while(m.find())
		{
			tempStr=m.group(1);
			tempStr = Trim(tempStr);
			tempStr=m.group(2);
			tempStr = Trim(tempStr);
			ret = ret + tempStr + "\r\n";
		}
		return ret;
	}
	private static String Trim(String s){
		 s = rightTrim(s);
		 return leftTrim(s);
	 }
	 private static String rightTrim(String s) {
		 if (s == null || s.trim().length() == 0)
			 return null;
	     if (s.trim().length() == s.length())
	         return s;
	     if (!s.startsWith(" ")) 
	     {
	         return s.trim();
	     } 
	     else 
	     {
	    	 return s.substring(0, s.indexOf(s.trim().substring(0, 1)) + s.trim().length());
	     }
	 }
	 private static String leftTrim(String s) {
		if (s == null || s.trim().length() == 0)
			return null;
	    if (s.trim().length() == s.length())
	    	return s;
	    if (!s.startsWith(" ")) 
	    {
	        return s;
	    } 
	    else
	    {
	    	return s.substring(s.indexOf(s.trim().substring(0, 1)));
	    }
	}
	 public void downLoadPic(String picurl,String fileName,String filePath)
		{
			URL url;
			BufferedInputStream in;
			FileOutputStream file;
			try {
			   System.out.println("Start download picture!");
			   url = new URL(picurl);
			   in = new BufferedInputStream(url.openStream());
			   file = new FileOutputStream(new File(filePath+fileName));
			   int t;
			   while ((t = in.read()) != -1) {
			    file.write(t);
			   }
			   file.close();
			   in.close();
			   System.out.println("Download picture successed!\r\n" + "path : " + filePath + fileName);
			} catch (MalformedURLException e) {
			   e.printStackTrace();
			} catch (FileNotFoundException e) {
				System.err.println("create filed failed! check the current path valid.");
				e.printStackTrace();
			} catch (IOException e) {
				System.err.println("get page picture failed...");
			   e.printStackTrace();
			}
		}
}