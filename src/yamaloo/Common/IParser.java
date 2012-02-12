package yamaloo.Common;

import java.io.File;
import java.net.URL;
import java.util.Collection;

import org.ini4j.Profile.Section;

public interface IParser {
	// Init parser
	// Load parser setting
	// different parser may have different config
	boolean initialize(Section setting) throws Throwable;

	Collection<String> parse(File path, URL sourceUrl);
}
