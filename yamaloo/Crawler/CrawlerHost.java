package yamaloo.Crawler;

import java.util.*;

import yamaloo.Common.*;

public class CrawlerHost {
	private final int crawlerID;
	private LinkedList<CrawlerTask> runningTasks = new LinkedList<CrawlerTask>();
	private ArrayList<CrawlerTask> pendingTasks = new ArrayList<CrawlerTask>();
	private LinkedList<CrawlerTask> finishedTasks = new LinkedList<CrawlerTask>();
	private HashMap<String, CrawlerTask> newTasks = new HashMap<String, CrawlerTask>();

	private HashMap<Integer, Site> sites = new HashMap<Integer, Site>();
	private HashMap<Integer, IParser> parsers = new HashMap<Integer, IParser>();
	private HashMap<Integer, Long> nextRunTimes = new HashMap<Integer, Long>();

	private long lastSaveTime = new Date().getTime();

	public CrawlerHost(int crawlerID) {
		this.crawlerID = crawlerID;
	}

	public int getCrawlerID() {
		return crawlerID;
	}

	public void execute() throws Throwable {

//		this.sites = loadAllSites();
		loadAllSites();
//		System.out.println("TMD!"+sites.size());
		for (int i = 0; i < Config.getCrawlerCountPerHost(); i++) {
			Runnable crawler = new Crawler(this);
			Thread t = new Thread(crawler);
			t.setName(String.format("Crawler_%d", i));
			t.start();
		}
	}

	public synchronized CrawlerTask dispatchTask() throws Throwable {
		CrawlerTask task = getNextAvailableTask();
		if (task != null) {
			Logger.getInstance()
					.info(String
							.format("Task %s dispatched, running task size = %d, pending task size = %d",
									task, this.runningTasks.size(),
									this.pendingTasks.size()));
			return task;
		}

		// If there is no available task at this moment
		// Try to pull new tasks if currently running tasks count is low
		// DO NOT pull tasks if there are still plenty of tasks, but no one is
		// available due to QPS control
		if (this.runningTasks.size() < Config.getCrawlerCountPerHost()) {
			saveData();
			pullData();

			// If now task is available or running after poll, sleep for a long
			// time
			if (this.runningTasks.isEmpty() && this.finishedTasks.isEmpty()
					&& this.pendingTasks.isEmpty() && this.newTasks.isEmpty()) {
				Logger.getInstance().info(
						"No task running or pennding, long sleep");
				Thread.sleep(Config.getCrawlerHostIdleInterval());
				return null;
			}

			// Try to return a available task
			task = getNextAvailableTask();
			if (task != null) {
				Logger.getInstance()
						.info(String
								.format("Task %s dispatched, running task size = %d, pending task size = %d",
										task, this.runningTasks.size(),
										this.pendingTasks.size()));
				return task;
			}
		}

		// There are still a lot tasks, but no task ready due to QPS control
		// Do nothing, just wait
		return null;
	}

	// TODO: Should add retry interval control here
	// Return next available task
	// If runningTasks is empty return null
	// If runningTasks queue is not empty but there is no available task due to
	// QPS control, return null
	private CrawlerTask getNextAvailableTask() throws Throwable {
		CrawlerTask task = this.runningTasks.poll();
		if (task == null) {
			// Logger.getInstance().debug("Empty running task queue");
			return task;
		}

		int taskID = task.getCrawlerTaskID();
		long now = new java.util.Date().getTime();
		while (true) {
			// Return task if QPS control is met
			int siteID = task.getSiteID();
			if (!this.nextRunTimes.containsKey(siteID)) {
				this.nextRunTimes.put(siteID, now
						+ this.sites.get(siteID).getSetting()
								.getMaxQueryInterval());

				this.pendingTasks.add(task);
				return task;
			} else {
				long next = this.nextRunTimes.get(siteID);
				if (next < now) {
					this.nextRunTimes.put(siteID, now
							+ this.sites.get(siteID).getSetting()
									.getMaxQueryInterval());

					this.pendingTasks.add(task);
					return task;
				}
			}

			// Push task back to queue as tail
			// Pop the queue head as next
			this.runningTasks.push(task);
			task = this.runningTasks.poll();

			// Return null if all tasks in queue has been processed
			if (taskID == task.getCrawlerTaskID()) {
				this.runningTasks.push(task);
				// Logger.getInstance().debug("No task avaiable due to QPS control");
				return null;
			}
		}
	}

	private void saveData() throws Throwable {
		if (!this.finishedTasks.isEmpty()) {
			DBManager db = new DBManager();
			db.updateCrawlerTaskList(this.finishedTasks);
			db.close();

			long now = new Date().getTime();
			float qps = this.finishedTasks.size() * 1000.0f
					/ (now - this.lastSaveTime + 1);

			Logger.getInstance().info(
					String.format("%d finished tasks saved,average QPS = %f",
							this.finishedTasks.size(), qps));

			this.lastSaveTime = now;
			this.finishedTasks.clear();
		}

		if (!this.newTasks.isEmpty()) {
			DBManager db = new DBManager();
			db.insertCrawlerTaskList(this.newTasks.values());
			db.close();

			Logger.getInstance().info(
					String.format("%d new tasks saved", this.newTasks.size()));
			this.newTasks.clear();
		}
	}

	private void pullData() throws Throwable {
		List<CrawlerTask> tasks = null;

		int targetPullCount = Config.getCrawlerCountPerHost();
		if (this.runningTasks.size() > 0)
			targetPullCount = Math.max(Config.getCrawlerPackagePullLength()
					- this.runningTasks.size(), targetPullCount);

		DBManager db = new DBManager();
		tasks = db.getCrawlerTaskPackage(this.getCrawlerID(), targetPullCount);

		// Load site info if needed, so we don't need restart crawler
		// every time a new site is added
		for (CrawlerTask task : tasks) {
			if (!this.sites.containsKey(task.getSiteID())) {

				Logger.getInstance().info("Loading site data on demand...");

				Site site = db.getSite(task.getSiteID());
				addSite(site);

				Logger.getInstance()
						.info(String.format("Site data loaded for %s",
								site.getName()));
			}
		}

		db.close();

		// De-dup
		HashSet<Integer> hash = new HashSet<Integer>();
		for (CrawlerTask task : this.runningTasks) {
			hash.add(task.getCrawlerTaskID());
		}

		for (CrawlerTask task : this.pendingTasks) {
			hash.add(task.getCrawlerTaskID());
		}

		int pullCount = hash.size();
		for (CrawlerTask task : tasks) {
			if (hash.add(task.getCrawlerTaskID()))
				this.runningTasks.add(task);
		}
		pullCount = hash.size() - pullCount;

		Logger.getInstance()
				.info(String
						.format("Try to pull %d more tasks, %d pulled, running task size = %d",
								targetPullCount, pullCount,
								this.runningTasks.size()));
	}

	public synchronized void saveTaskResult(CrawlerTask task,
			List<CrawlerTask> newTasks) {
		if (task != null) {
			this.finishedTasks.add(task);

			for (int i = 0; i < this.pendingTasks.size(); i++) {
				CrawlerTask t = this.pendingTasks.get(i);
				if (t.getCrawlerTaskID() == task.getCrawlerTaskID())
					this.pendingTasks.remove(i);
			}
		}

		if (newTasks != null) {
			for (CrawlerTask newTask : newTasks) {
				if (newTask.getUrl() == null)
					continue;

				if (this.newTasks.containsKey(newTask.getUrlHash()))
					continue;

				this.newTasks.put(newTask.getUrlHash(), newTask);
			}
		}
	}

//	public HashMap<Integer, Site> loadAllSites() throws Throwable {
	public void loadAllSites() throws Throwable {
		Logger.getInstance().info("Loading site data...");

//		HashMap<Integer, Site> sites = new HashMap<Integer, Site>();
		DBManager db = new DBManager();
		List<Site> list = db.getSiteList();
		db.close();
//		System.out.println(list.size());
		for (Site site : list) {
			addSite(site);
		}

		Logger.getInstance().info(String.format("%d site setting loaded", list.size()));

//		return sites;
	}

	public synchronized Site getSite(int siteID) {
		return sites.get(siteID);
	}
	
	public synchronized IParser getParser(int siteID) {
		return parsers.get(siteID);
	}

	private static IParser createParser(String name) throws Throwable {

		String fullName = "yamaloo.Crawler." + name;
		Class<?> c = Class.forName(fullName);//类反射啊
		return (IParser) c.newInstance();
	}

	private void addSite(Site site) throws Throwable {
		sites.put(site.getSiteID(), site);
		
		SiteSetting setting = site.getSetting();//设置配置文件路径
		IParser parser = createParser(setting.getParserName());
		parser.initialize(setting.getParserSetting());
		parsers.put(site.getSiteID(), parser);
	}
}
