package yamaloo.Crawler;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ini4j.Profile.Section;

import yamaloo.Common.IParser;
import yamaloo.Common.Logger;
import yamaloo.Common.Utility;

public class GeneralHtmlParser implements IParser {

	private static final Pattern anchorTag = Pattern.compile(
			"<a\\s*[^>]*?href\\s*=\\s*([\"']?)([^\"' >]+)\\1[^>]*?>",
			Pattern.CASE_INSENSITIVE);
	private List<Pattern> whiteList = new ArrayList<Pattern>();
	private List<Pattern> blackList = new ArrayList<Pattern>();

	private static final Pattern imageTag = Pattern.compile(
			"<img\\s*[^>]*?src\\s*=\\s*([\"']?)([^\"' >]+)\\1[^>]*?>",
			Pattern.CASE_INSENSITIVE);
	private List<Pattern> imageParentWhiteList = new ArrayList<Pattern>();
	private List<Pattern> imageBlackList = new ArrayList<Pattern>();
	private boolean parseImage = false;

	@Override
	public boolean initialize(Section setting) {
		System.out.println("TMD");
		if (setting != null) {
			for (int i = 0;; i++) {
				String key = String.format("WhiteList_%d", i);
				if (!setting.containsKey(key))
				{
					
					break;
				}
				String filter = setting.get(key);
//				System.out.println("TMD!" + filter);
				Pattern pattern = Pattern.compile(filter,
						Pattern.CASE_INSENSITIVE);
				
				this.whiteList.add(pattern);

				Logger.getInstance()
						.info(String.format("%s : %s", key, filter));
			}

			for (int i = 0;; i++) {
				String key = String.format(String.format("BlackList_%d", i));
				if (!setting.containsKey(key))
					break;

				String filter = setting.get(key);
				Pattern pattern = Pattern.compile(filter,
						Pattern.CASE_INSENSITIVE);
				this.blackList.add(pattern);

				Logger.getInstance()
						.info(String.format("%s : %s", key, filter));
			}

			for (int i = 0;; i++) {
				String key = String.format("ImageParentWhiteList_%d", i);
				if (!setting.containsKey(key))
					break;

				String filter = setting.get(key);
				Pattern pattern = Pattern.compile(filter,
						Pattern.CASE_INSENSITIVE);
				this.imageParentWhiteList.add(pattern);

				Logger.getInstance()
						.info(String.format("%s : %s", key, filter));
			}
			
			this.parseImage = !this.imageParentWhiteList.isEmpty();
			
			for (int i = 0;; i++) {
				String key = String.format("ImageBlackList_%d", i);
				if (!setting.containsKey(key))
					break;

				String filter = setting.get(key);
				Pattern pattern = Pattern.compile(filter,
						Pattern.CASE_INSENSITIVE);
				this.imageBlackList.add(pattern);

				Logger.getInstance()
						.info(String.format("%s : %s", key, filter));
			}
		}

		return true;
	}

	// No URL normalize work here, only extract url in content as it is
	// But may need to complete it if needed
	public Collection<String> parse(File path, URL sourceUrl) {
		URI sourceURI = null;
		HashSet<String> urls = new HashSet<String>();

		try {
			sourceURI = (sourceUrl == null) ? null : sourceUrl.toURI();
			String content = Utility.readAllText(path);

			// Parse Anchor tag
			Matcher matcher = anchorTag.matcher(content);
			while (matcher.find()) {
				String url = Utility.resolveUrl(sourceURI, matcher.group(2));
				if (url == null || url.isEmpty())
					continue;

				// Skip self
				if (url == sourceUrl.toString())
					continue;

				// Filter anchor
				if (!this.canMatchGlobalFilter(url))
					continue;

				// Exclude non-text page if anchor is a resource file
				if (!this.parseImage && !Utility.isUrlText(new URL(url)))
					continue;
				
				// Filter if anchor is a resource file
				if (!this.canMatchImageFilter(url))
					continue;

				urls.add(url);
			}

			// Parse image tag if current source url match given pattern
			if (parseImage && this.canMatchImageParentFilter(sourceUrl.toString())) {
				matcher = imageTag.matcher(content);
				while (matcher.find()) {
					String url = Utility
							.resolveUrl(sourceURI, matcher.group(2));
					if (url == null || url.isEmpty())
						continue;

					if (!canMatchImageFilter(url))
						continue;
					
					urls.add(url);
				}
			}

		} catch (Throwable e) {
			urls = null;

			Logger.getInstance().warn(
					String.format("Fail to parse content for %s", path), e);
		}

		return urls;
	}

	private boolean canMatchGlobalFilter(String url) {
		if (url == null || url.isEmpty())
			return false;

		for (Pattern pattern : this.blackList) {
			Matcher matcher = pattern.matcher(url);
			if (matcher.find())
				return false;
		}

		if (whiteList == null || whiteList.isEmpty())
			return true;

		for (Pattern pattern : this.whiteList) {
			Matcher matcher = pattern.matcher(url);
			if (matcher.find())
				return true;
		}

		return false;
	}

	private boolean canMatchImageParentFilter(String url) {
		if (this.imageParentWhiteList == null || this.imageParentWhiteList.isEmpty())
			return false;

		if (url == null || url.isEmpty())
			return false;

		for (Pattern pattern : this.imageParentWhiteList) {
			Matcher matcher = pattern.matcher(url);
			if (matcher.find())
				return true;
		}

		return false;
	}
	
	private boolean canMatchImageFilter(String url) {
		if (url == null || url.isEmpty())
			return false;

		for (Pattern pattern : this.imageBlackList) {
			Matcher matcher = pattern.matcher(url);
			if (matcher.find())
				return false;
		}

		return true;
	}
}
