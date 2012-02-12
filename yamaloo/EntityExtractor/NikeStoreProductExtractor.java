package yamaloo.EntityExtractor;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import yamaloo.Common.CrawlerTask;
import yamaloo.Common.IEntityExtractor;
import yamaloo.Common.Product;
import yamaloo.Common.Utility;

public class NikeStoreProductExtractor extends ProductExtractorBase implements
		IEntityExtractor {

	private final Pattern namePattern = Pattern.compile(
			"productCode\">\\W*(.+?)\\W*</h2>", Pattern.MULTILINE);
	private final Pattern descriptionPattern = Pattern.compile(
			"id=\"tabs-1\">([\\s\\S]*?<style[\\s\\S]*?/style>)?([\\s\\S]*?)(关键字|<div id=\"product-comment-list\")", Pattern.MULTILINE);

	private List<Product> products = new ArrayList<Product>();

	public int extractProduct() throws Throwable {
		Pattern pattern = Pattern.compile("product/.+?/detail\\.htm");
		for (CrawlerTask task : this.getTasks()) {
			Matcher matcher = pattern.matcher(task.getUrl().toString());
			if (!matcher.find())
				continue;

			extractDetailPage(task);
		}

		return 0;
	}

	private void extractDetailPage(CrawlerTask task) throws Throwable {
		String content = Utility.readAllText(task.getTargetPath());

		String name = "";
		Matcher matcher = namePattern.matcher(content);
		if (matcher.find())
			name = matcher.group(1);

		String description = "";
		matcher = descriptionPattern.matcher(content);
		if (matcher.find())
		{
			description = matcher.group(2);
			if (description.length() > 512)
				description = description.substring(0, 511);
		}
		
		Product product = new Product(name, description);
		product.setPrice("12345");
		products.add(product);
	}
}
