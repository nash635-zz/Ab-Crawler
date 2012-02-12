package yamaloo.Common;

import java.security.MessageDigest;

public class Product {
	private String name;
	private String BrandID;
	private String description;
	private String thumNailPicPath;
	private String mainPicPath;
	private String detailPicPath;
	private String price;
	private String rawSerialNumber;
	private String pictureURL;

	public Product(String name, String description) {
		this.name = name;
		this.description = description;
	}

	public final String getName() {
		return name;
	}
	
	public final String getBrandID() {
		return BrandID;
	}

	public final String getDescription() {
		return description;
	}

	public final String getThumNailPicPath() {
		return thumNailPicPath;
	}

	public final void setThumNailPicPath(String thumNailPicPath) {
		this.thumNailPicPath = thumNailPicPath;
	}

	public final String getMainPicPath() {
		return mainPicPath;
	}

	public final void setMainPicPath(String mainPicPath) {
		this.mainPicPath = mainPicPath;
	}

	public final String getDetailPicPath() {
		return detailPicPath;
	}

	public final void setDetailPicPath(String detailPicPath) {
		this.detailPicPath = detailPicPath;
	}

	public final String getPrice() {
		return price;
	}
	
	public final void setBrandID(String BrandID) {
		this.BrandID = BrandID;
	}

	public final void setPrice(String price) {
		this.price = price;
	}

	public final String getRawSerialNumber() {
		return rawSerialNumber;
	}

	public final void setRawSerialNumber(String rawSerialNumber) {
		this.rawSerialNumber = rawSerialNumber;
	}
	public final String getpictureURL() {
		return pictureURL;
	}
	public final void setpictureURL(String pictureURL) {
		this.pictureURL = pictureURL;
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
}
