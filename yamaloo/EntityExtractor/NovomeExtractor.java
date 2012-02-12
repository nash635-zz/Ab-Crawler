package yamaloo.EntityExtractor;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import yamaloo.Common.CrawlerTask;
import yamaloo.Common.DBManager;
import yamaloo.Common.IEntityExtractor;
import yamaloo.Common.Product;
import yamaloo.Common.Utility;

public class NovomeExtractor extends ProductExtractorBase implements
IEntityExtractor{
	private final Pattern namePattern = Pattern.compile(
			"<div class=\"left showcollection-area\" style=\"margin-top:30px;\">[\\w\\W]*?<div class=\"name\">([\\w\\W]*?)</div>[\\w\\W]*?<div class=\"item\">[\\w\\W]*?nbsp;([\\w\\W]*?)</div>[\\w\\W]*?<div class=\"item\">[\\w\\W]*?nbsp;([\\w\\W]*?)</div>[\\w\\W]*?<div class=\"item\">[\\w\\W]*?</div>", Pattern.MULTILINE);


	public int extractProduct() throws Throwable {
		Pattern pattern = Pattern.compile("http://novome.novoretail.com.cn/showcollection.asp\\?id=[\\w\\W]*");
		int cnt = 0;
		db = new DBManager();
//		System.out.print("<div class=\"left showcollection-area\" style=\"margin-top:30px;\">[\\w\\W]*?<div class=\"name\">([\\w\\W]*?)</div>[\\w\\W]*?<div class=\"item\">售价：&nbsp;([\\w\\W]*?)</div>[\\w\\W]*?<div class=\"item\">货号：&nbsp;([\\w\\W]*?)</div>[\\w\\W]*?<div class=\"item\">[\\w\\W]*?</div>");
		for (CrawlerTask task : this.getTasks()) {
			Matcher matcher = pattern.matcher(task.getUrl().toString());
			if (!matcher.find())
				continue;
			System.out.print(++cnt + " : ");
			if(cnt > 1)
				extractDetailPage(task);
		}
		db.close();
		return cnt;
	}

	private void extractDetailPage(CrawlerTask task) throws Throwable {
		String content = Utility.readAllText(task.getTargetPath());
		content = new String(content.getBytes("gbk"),"utf-8");
//		System.out.println(content);
		String name = "";
		String tempStr = "";
		Matcher matcher = namePattern.matcher(content);
		if (matcher.find())
			name = matcher.group(1);
		else
			return;
		System.out.println(name);
		String description = name;
		System.out.println(description);
		Product product = new Product(name, description);
		product.setBrandID(brandID);
		//获取序列码
		product.setRawSerialNumber(matcher.group(3));
		//获取价格信息
		String price = matcher.group(2);
		product.setPrice(price);
		if(product.getPrice() == "")
			return;
		//获取图片url
		String regx = "<div style=\"margin-top:3px;\">[\\w\\W]*?<img src=\"([\\w\\W]*?)\" [\\w\\W]*?</div>";
		Matcher m2 = Pattern.compile(regx).matcher(content);
		while(m2.find())
		{
			tempStr=m2.group(1);
			tempStr=Trim(tempStr);
			product.setpictureURL(tempStr);
			System.out.println(tempStr);
			product.setMainPicPath(picSavePath + product.getMD5(tempStr) + ".jpg");
			System.out.println("图片网址"+" : " + tempStr);
			downLoadPic(product.getpictureURL(),product.getMD5(tempStr)+".jpg",picSavePath);
		}
		product.setBrandID(brandID);//保险起见再插入一遍
		db.insertProduct(product);
//		products.add(product);
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
	 public void downLoadPic(String picurl,String fileName,String filePath){
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
	 public byte[] gbk2utf8(String chenese){
			char c[] = chenese.toCharArray();
			byte[] fullByte = new byte[3 * c.length];
			for (int i = 0; i < c.length; i++)
			{
				int m = (int) c[i];
				String word = Integer.toBinaryString(m);
				StringBuffer sb = new StringBuffer();
				int len = 16 - word.length();
				for (int j = 0; j < len; j++) {
					sb.append(0);
				}
				sb.append(word);
				sb.insert(0, 1110);
				sb.insert(8, 10);
				sb.insert(16, 10);
				String s1 = sb.substring(0, 8);
				String s2 = sb.substring(8, 16);
				String s3 = sb.substring(16);
				byte b0 = Integer.valueOf(s1, 2).byteValue();
				byte b1 = Integer.valueOf(s2, 2).byteValue();
				byte b2 = Integer.valueOf(s3, 2).byteValue();
				byte[] bf = new byte[3];
				bf[0] = b0;
				fullByte[i * 3] = bf[0];
				bf[1] = b1;
				fullByte[i * 3 + 1] = bf[1];
				bf[2] = b2;
				fullByte[i * 3 + 2] = bf[2];
			}
			return fullByte;
		}
}