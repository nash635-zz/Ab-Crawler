package yamaloo.Common;

import java.io.File;
import java.sql.ResultSet;
import java.sql.Timestamp;

public class Site {
	private int siteID = -1;
	private String name;
	private String directoryName;
	private File directory;
	private File seedPath;
	private File siteSettingPath;
	private SiteSetting siteSetting;
	private boolean enabled = true;
	private int interval = 1; // in minutes
	private Timestamp lastRunTime;
	private Timestamp nextRunTime;
	private int priority = Config.getDefaultSitePriority();

	public Site(String name, String directoryName, Timestamp nextRunTime) {
		this.name = name;
		this.directoryName = directoryName;
		this.directory = new File(Config.getSiteRoot(),
				this.directoryName);
//		System.out.println(this.directory);
		this.seedPath = new File(this.directory, "Seed.txt");
		this.siteSettingPath = new File(this.directory, "Config.ini");
		this.nextRunTime = nextRunTime;
	}

	public Site(String name, String directoryName) {
		this(name, directoryName, new Timestamp(new java.util.Date().getTime()));
	}

	public int getSiteID() {
		return siteID;
	}

	public void setSiteID(int siteID) {
		this.siteID = siteID;
	}

	public String getName() {
		return name;
	}

	public String getDirectoryName() {
		return directoryName;
	}

	public File getDirectory() {
		return directory;
	}

	public File getSeedPath() {
		return seedPath;
	}
	
	public File getSettingPath() {
		return siteSettingPath;
	}
	
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public Timestamp getLastRunTime() {
		return lastRunTime;
	}

	public void setLastRunTime(Timestamp lastRunTime) {
		this.lastRunTime = lastRunTime;
	}

	public Timestamp getNextRunTime() {
		return nextRunTime;
	}

	public void setNextRunTime(Timestamp nextRunTime) {
		this.nextRunTime = nextRunTime;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public static Site Parse(ResultSet rs) throws Throwable {
		Site site = new Site(rs.getString("Name"),rs.getString("DirectoryName"));
		site.setSiteID(rs.getInt("SiteID"));
		site.setEnabled(rs.getBoolean("Enabled"));
		site.setInterval(rs.getInt("Interval"));
		site.setLastRunTime(rs.getTimestamp("LastRunTime"));
		site.setNextRunTime(rs.getTimestamp("NextRunTime"));
		site.setPriority(rs.getInt("Priority"));
		return site;
	}

	public String[] getSeeds() throws Throwable {
		return Utility.readAllLines(this.getSeedPath().toString());
	}
	
	public SiteSetting getSetting() throws Throwable
	{
		if (siteSetting == null)
		{
			System.out.print(this.getSettingPath());
			siteSetting = new SiteSetting(this.getSettingPath());
		}
		return siteSetting;
	}
	
	public File getResourceDir()
	{
		return Utility.getResourceDir(this.siteID);
	}
}
