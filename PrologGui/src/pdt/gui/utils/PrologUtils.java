package pdt.gui.utils;

import java.io.File;

import javax.swing.JFrame;

import org.apache.commons.codec.digest.DigestUtils;
import org.cs3.prolog.common.Util;

public class PrologUtils {

	public static String quoteIfNecessary(String atom) {
		try {
			Integer.parseInt(atom);
			return atom;
		} catch (NumberFormatException e) {}
		return Util.quoteAtomIfNeeded(atom.replace("\\", "/"));
	}
	
	
	private static JFrame activeFrame;
	
	public static void setActiveFrame(JFrame f) {
		activeFrame = f;
	}
	
	public static JFrame getActiveFrame() {
		return activeFrame;
	}
	
	public static String md5Prefix(String input) {
		return Character.toString(md5(input).charAt(0));
	}
	
	private static String md5(String input) {
		return DigestUtils.md5Hex(input);
	}
	
	public static File getDownloadDirectory() {
		return new File(System.getProperty("user.home") + "\\Downloads");
	}
	
}
