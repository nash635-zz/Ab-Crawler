package yamaloo.HtmlProcessor;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

public class ProductItem{
	public String ProductName = "";
	public String Brand = "";
	public String Price = "";
	public String SerialNumber = "";
	public String ProductDetail = "";
	public String PictureUrl = "";
	public String ProductPageUrl = "";
	public String ProductPageHashCode = "";
	public void clear(){
		ProductName = "";
		Brand = "";
	    Price = "";
		SerialNumber = "";
		ProductDetail = "";
		PictureUrl = "";
		ProductPageUrl = "";
		ProductPageHashCode = "";
	}
	public boolean isSet()
	{
		if(ProductName == "")
			return false;
		else
			return true;
	}
	public void convertCharSet()
	{
		ProductName = convertString(ProductName);
		Brand = convertString(Brand);
		SerialNumber = convertString(SerialNumber);
		ProductDetail = convertString(ProductDetail);
		PictureUrl = convertString(PictureUrl);
		ProductPageUrl = convertString(ProductPageUrl);
		ProductPageHashCode = convertString(ProductPageHashCode);
	}
	public void showDetail()
	{
		System.out.println("ProductName : " + ProductName);
		System.out.println("Brand : " + Brand);
		System.out.println("Price : " + Price);
		System.out.println("SerialNumber : " + SerialNumber);
		System.out.println("ProductDetail : \r\n" + ProductDetail);
		System.out.println("PictureUrl¡¡£º" + PictureUrl);
		System.out.println("ProductPageUrl : " + ProductPageUrl);
		System.out.println("ProductPageHashCode : " + ProductPageHashCode);
	}
	public String convertString(String gbk)
	{
		String utf8 = "";
		try {
			utf8 = new String(gbk2utf8(gbk),"UTF-8");
		}catch(UnsupportedEncodingException e){
			e.printStackTrace();
			}
		return utf8;
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
	public final String getMD5(String s){
		char hexDigits[] = {
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
		'e', 'f'};
		try {
			byte[] strTemp = s.getBytes();
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(strTemp);
			byte[] md = mdTemp.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>>4 & 0xf];
			str[k++] = hexDigits[byte0 & 0xf];
		}
		return new String(str);
		}catch (Exception e){
			return null;
		}
	}
	public String getMD5ofPicUrl()
	{
		return getMD5(PictureUrl);
	}
	public static void main(String[] args){
		String a = "http://item.taobao.com/item.htm?id=12701620086";
//		System.out.println("MD5 value for url is : " + getMD5(a));
	}
}