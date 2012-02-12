package yamaloo.Common;

import org.ini4j.Profile.Section;

public interface IEntityExtractor {
	boolean initialize(SiteSetting setting) throws Throwable;

	int extract(int batchID) throws Throwable;
}
