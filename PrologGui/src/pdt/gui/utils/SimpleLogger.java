package pdt.gui.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SimpleLogger {

	public static final int LVL_TRACE = 0;
	public static final int LVL_DEBUG = 1;
	public static final int LVL_INFO = 2;
	public static final int LVL_WARNING = 3;
	public static final int LVL_ERROR = 4;
	
	public static int logLevel = LVL_DEBUG;

	public static void trace(Object message) {
		log(message, LVL_TRACE);
	}
	
	public static void debug(Object message) {
		log(message, LVL_DEBUG);
	}
	
	public static void info(Object message) {
		log(message, LVL_INFO);
	}
	
	public static void warning(Object message) {
		log(message, LVL_WARNING);
	}
	
	public static void error(Object message) {
		log(message, LVL_ERROR);
	}
	
	public static void log(Object message, int level) {
		if (level >= logLevel) {
			String outputString = "[" + levelString(level) + " - " + timestamp() + "]: " + message.toString();
			
			if (level == LVL_ERROR) {
				System.err.println(outputString);
			} else {
				System.out.println(outputString);
			}
		}
	}

	private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private static String timestamp() {
		Date date = new Date();
		return dateFormat.format(date);
	}

	private static String levelString(int level) {
		switch (level) {
		case LVL_TRACE:
			return "TRACE";
		case LVL_DEBUG:
			return "DEBUG";
		case LVL_INFO:
			return "INFO";
		case LVL_WARNING:
			return "WARNING";
		case LVL_ERROR:
			return "ERROR";
		default:
			return "UNKNOWN";
		}
	}
	
	
	private static long startTime = 0;
	
	public static void profileStart() {
		startTime = System.currentTimeMillis();
	}
	
	public static void profileEnd(String msg) {
		if (startTime == 0) {
			System.err.println("no start time, call profileStart() before profileEnd()");
			return;
		}
		long endTime = System.currentTimeMillis();
		long duration = endTime - startTime;
		startTime = 0;
		System.out.println("duration " + msg + ": " + duration + "ms");
	}
	
}
