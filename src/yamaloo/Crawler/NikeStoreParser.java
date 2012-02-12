package yamaloo.Crawler;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import yamaloo.Common.*;

public class NikeStoreParser extends GeneralHtmlParser implements IParser {

	private Pattern pagerPattern = Pattern.compile("<a[^>]*? page=\"(\\d+)\">",
			Pattern.CASE_INSENSITIVE);
	private Pattern pricePattern = Pattern.compile(
			"loadurl=\"([^\"]+?listprice.json)\"", Pattern.CASE_INSENSITIVE);

	public NikeStoreParser() {
	}

	public Collection<String> parse(File path, URL sourceUrl) {
		Collection<String> list = super.parse(path, sourceUrl);

		String page;
		try {
			page = sourceUrl.toString().replace(
					"?" + sourceUrl.toURI().getRawQuery(), "");
			if (page.toLowerCase().endsWith("list.htm")) {

				int maxPageNumber = 1;

				Matcher matcher = pagerPattern.matcher(Utility
						.readAllText(path));
				while (matcher.find()) {
					maxPageNumber = Math.max(maxPageNumber,
							Integer.parseInt(matcher.group(1)));
				}

				for (int i = 1; i <= maxPageNumber; i++) {
					list.add(String.format("%s?page=%d", page, i));
				}
			}

			if (page.toLowerCase().endsWith("detail.htm")) {

				// Load price, loadurl="/product/441377-400/listprice.json"
				Matcher matcher = pricePattern.matcher(Utility
						.readAllText(path));

				while (matcher.find()) {
					list.add(Utility.resolveUrl(sourceUrl.toURI(),
							matcher.group(1)).toString());
				}
			}
		} catch (Throwable e) {
			Logger.getInstance().error(e);
		}

		return list;
	}
}
