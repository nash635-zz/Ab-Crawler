package yamaloo.Common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;

public class Utility {
	private Utility() {

	}

	public static URL normalizeUrl(String url) {
		if (url == null)
			return null;

		// CANNOT to lower case, some site cares
		url = url.trim();
		if (url.isEmpty())
			return null;

		// Remove last '/'
		if (url.charAt(url.length() - 1) == '/')
			url = url.substring(0, url.length() - 1);

		// Remove in-page anchor
		int pos = url.indexOf('#');
		if (pos != -1)
			url = url.substring(0, pos);

		if (url.isEmpty())
			return null;

		// Validate url
		URL normalizedUrl = null;
		try {
			normalizedUrl = new URL(url);
		} catch (MalformedURLException e) {
			try {
				// Try to add "http://" to build a valid url
				normalizedUrl = new URL("http://" + url);
			} catch (MalformedURLException e1) {
				return null;
			}
		}

		return normalizedUrl;
	}

	public static String getUrlHash(String url) {
		URL normalizedUrl = normalizeUrl(url);
		return calculateHash(normalizedUrl);
	}

	public static String getUrlHash(URL url) {
		if (url == null)
			return null;

		return getUrlHash(url.toString());
	}

	public static String getUrlHostName(String url) {
		URL normalizedUrl = normalizeUrl(url);
		if (normalizedUrl == null)
			return null;

		return normalizedUrl.getHost();
	}

	public static String getUrlHostName(URL url) {
		if (url == null)
			return null;

		return getUrlHostName(url.toString());
	}

	public static String getUrlHostHash(String url) {
		String host = getUrlHostName(url);
		return calculateHash(host);
	}

	public static String getUrlHostHash(URL url) {
		if (url == null)
			return null;

		return getUrlHash(url.toString());
	}

	private static String calculateHash(String url) {
		if (url == null || url.isEmpty())
			return null;

		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			Logger.getInstance().error("Fail to calculate hash", e);
		}

		if (md == null)
			return null;

		byte[] bytes = md.digest(url.getBytes());

		StringBuffer hash = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			// http://apps.hi.baidu.com/share/detail/38507110
			hash.append(Integer.toHexString(
					(0x000000FF & bytes[i]) | 0xFFFFFF00).substring(6));
		}

		return hash.toString();
	}

	private static String calculateHash(URL url) {
		if (url == null)
			return null;

		return calculateHash(url.toExternalForm());
	}

	public static boolean directoryExists(String path) {
		if (path == null)
			return false;

		File dir = new File(path);
		return dir.isDirectory();
	}

	public static void createDirectory(File dir) throws Throwable {
		if (dir.isDirectory())
			return;

		if (!dir.mkdirs())
			throw new Exception(String.format("Fail to create directory, %s",
					dir));
	}

	public static void createDirectory(String path) throws Throwable {
		createDirectory(new File(path));
	}

	public static String[] readAllLines(String path) throws Throwable {
		LinkedList<String> list = new LinkedList<String>();

		BufferedReader reader = new BufferedReader(new FileReader(path));
		String line = null;
		while ((line = reader.readLine()) != null) {
			list.add(line);
		}
		reader.close();

		return list.toArray(new String[0]);
	}

	public static String readAllText(String path) throws Throwable {
//		System.out.println(path);
		StringBuffer sb = new StringBuffer(1024);

		BufferedReader reader = new BufferedReader(new FileReader(path));
		char[] buf = new char[1024];
		int count = 0;
		while ((count = reader.read(buf)) != -1) {
			sb.append(buf, 0, count);
		}
		reader.close();

		return sb.toString();
	}

	public static String readAllText(File path) throws Throwable {
		return readAllText(path.toString());
	}

	// Create file if file does not exist, overwrite existing file
	public static void writeAllLines(String path, String[] contents)
			throws Throwable {
		File file = new File(path);
		createDirectory(file.getAbsoluteFile().getParent());

		BufferedWriter writer = new BufferedWriter(new FileWriter(path));
		for (String line : contents) {
			writer.write(line);
			writer.newLine();
		}
		writer.close();
	}

	// Create file if file does not exist, overwrite existing file
	public static void writeAllText(String path, String contents)
			throws Throwable {
		File file = new File(path);
		createDirectory(file.getAbsoluteFile().getParent());

		BufferedWriter writer = new BufferedWriter(new FileWriter(path));
		writer.write(contents);
		writer.close();
	}

	public static boolean fileExists(String path) {
		if (path == null)
			return false;

		File file = new File(path);
		return file.isFile();
	}

	public static void deleteFile(String path) throws Throwable {
		if (!fileExists(path))
			return;

		File file = new File(path);
		if (!file.delete())
			throw new Exception(String.format("Fail to delete file, %s", path));
	}

	public static File getBatchDir(int batchID) {
		return new File(Config.getBatchRoot(), String.format("%d", batchID));
	}
	
	public static File getResourceDir(int siteID) {
		return new File(Config.getResourceRoot(), String.format("%d", siteID));
	}

	// TODO: add more check
	public static boolean isContentTypeText(String type) {
		if (type == null || type.isEmpty())
			return false;

		if (type.toLowerCase().contains("text"))
			return true;
		
		if (type.toLowerCase().contains("application"))
			return true;
		
		return false;
	}

	// TODO: add more check, and use a hashset
	public static boolean isUrlText(URL url) {
		if (url == null)
			return false;

		String str = url.getPath();
		if (str == null || str.isEmpty())
			return true;

		str = str.substring(1);
		int pos = str.lastIndexOf('.');
		if (pos <= 0 || pos == str.length())
			return true;

		str = str.substring(pos + 1);
		if (str.equalsIgnoreCase("jpg") || str.equalsIgnoreCase("jpeg")
				|| str.equalsIgnoreCase("png") || str.equalsIgnoreCase("gif")
				|| str.equalsIgnoreCase("bmp") || str.equalsIgnoreCase("tiff")
				|| str.equalsIgnoreCase("swf")
				|| str.equalsIgnoreCase("pdf"))
			return false;

		return true;
	}

	// TODO: Try
	// http://commons.apache.org/lang/api-release/org/apache/commons/lang/StringEscapeUtils.html
	public static String htmlDecode(String input) {
		if (input == null || input.isEmpty())
			return input;

		return input.replace("&amp;", "&").replace("&lt;", "<")
				.replace("&gt;", ">").replace("&quot;", "\"")
				.replace("&nbsp;", " ");
	}

	public static String resolveUrl(URI sourceUrl, String targetUrl) {
		String page = sourceUrl.toString().replace(
				"?" + sourceUrl.getRawQuery(), "");

		String url = Utility.htmlDecode(targetUrl.trim());
		if (url.isEmpty())
			return null;

		// Ignore in-page anchor
		if (url.charAt(0) == '#')
			return sourceUrl.toString();

		// Resolve ?
		if (url.charAt(0) == '?')
			url = page + url;

		// Try to complete url
		try {
			URI uri = new URI(url);
			if (uri.getScheme() == null)
				url = sourceUrl.resolve(uri).toString();

		} catch (URISyntaxException e) {
			url = null;
		}

		return url;
	}

}
