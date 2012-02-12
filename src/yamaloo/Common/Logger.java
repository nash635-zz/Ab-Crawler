package yamaloo.Common;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.*;

public class Logger {
	private final static org.apache.log4j.Logger logger = org.apache.log4j.Logger
			.getRootLogger();

	private Logger() {
	}

	static {

		String layout = "[%d] [%t] [%p] %m%n";
		ConsoleAppender console = new ConsoleAppender(new PatternLayout(layout));
		console.setName("Console");
		logger.addAppender(console);

		StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		StackTraceElement main = stack[stack.length - 1];
		File path = new File(Config.getLogRoot(), String.format("%s.log",
				main.getClassName()));

		try {
			RollingFileAppender file = new RollingFileAppender(
					new PatternLayout(layout), path.toString(), true);
			file.setName("File");
			file.setMaxBackupIndex(20);
			logger.addAppender(file);
		} catch (IOException e) {
			e.printStackTrace();
		}

		logger.setLevel(Level.DEBUG);
	}

	public static org.apache.log4j.Logger getInstance() {
		return logger;
	}
}
