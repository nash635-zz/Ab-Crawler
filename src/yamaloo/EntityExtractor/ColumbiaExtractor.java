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

public class ColumbiaExtractor extends ProductExtractorBase implements
IEntityExtractor{
	private final Pattern namePattern = Pattern.compile(
			"<div class=\"imgInfo\">[\\w\\W]*?<img src=\"([\\w\\W]*?)\"[\\w\\W]*?alt=\"([\\w\\W]*?)\"[\\w\\W]*?</div>", Pattern.MULTILINE);

	public int extractProduct() throws Throwable {
		Pattern pattern = Pattern.compile("http://www.51columbia.com/goods-[\\w\\W]*?.html");
		int cnt = 0;
		db = new DBManager();
		for (CrawlerTask task : this.getTasks()) {
			Matcher matcher = pattern.matcher(task.getUrl().toString());
			if (!matcher.find())
				continue;
			System.out.print(++cnt + " : ");
			extractDetailPage(task);
		}
		db.close();
		return cnt;
	}

	private void extractDetailPage(CrawlerTask task) throws Throwable {
		String content = Utility.readAllText(task.getTargetPath());
		String name = "";
		String pic = "";
		Matcher matcher = namePattern.matcher(content);
		if (matcher.find())
		{
			pic = matcher.group(1);
			name = matcher.group(2);
		}
		else
			return;
//		name = new String(name.getBytes("gbk"),"utf-8");
		System.out.println(name);
		String description = name;
//		description = new String(description.getBytes("gbk"),"utf-8");
		System.out.println(description);
		Product product = new Product(name, description);
		product.setBrandID(brandID);
		//获取序列码
		String tempStr;
		String regx = "<div class=\"textInfo\">[\\w\\W]*?<li class=\"clearfix\">[\\w\\W]*?<dd>([\\w\\W]*?)</dd>[\\w\\W]*?<dd class=\"ddR\">([\\w\\W]*?)</dd>";
		Matcher m1 = Pattern.compile(regx).matcher(content);
		if(m1.find())
		{
			tempStr=Trim(m1.group(1))+Trim(m1.group(2));
//			tempStr = new String(tempStr.getBytes("gbk"),"utf-8");
			product.setRawSerialNumber(tempStr);
		}
		//获取价格信息
		regx = "<font[\\w\\W]*?id=\"ECS_SHOPPRICE\">[\\w\\W]*?</span>([\\w\\W]*?)</font>";
		m1 = Pattern.compile(regx).matcher(content);
		while(m1.find())
		{
			tempStr=m1.group(1);
			tempStr=Trim(tempStr);
//			tempStr = new String(tempStr.getBytes("gbk"),"utf-8");
			product.setPrice(tempStr);
		}
		//获取图片url
		product.setpictureURL(pic);
		product.setMainPicPath(picSavePath + product.getMD5(pic) + ".jpg");
//		System.out.println("图片网址"+" : " + tempStr);
		downLoadPic(product.getpictureURL(),product.getMD5(pic)+".jpg",picSavePath);
		
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