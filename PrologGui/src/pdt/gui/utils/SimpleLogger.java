package pdt.gui.utils;

public class SimpleLogger {

	private static boolean loggingEnabled = false;

	public static void setLoggingEnabled(boolean b) {
		loggingEnabled = b;
	}
	
	public static void println(String s) {
		if (loggingEnabled) {
			System.out.println(s);
		}
	}
	
	public static void println(Object o) {
		println(o.toString());
	}
	
}
