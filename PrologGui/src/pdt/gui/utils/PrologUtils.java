package pdt.gui.utils;

import org.cs3.prolog.common.Util;

public class PrologUtils {

	public static String quoteIfNecessary(String atom) {
		try {
			Integer.parseInt(atom);
			return atom;
		} catch (NumberFormatException e) {}
		return Util.quoteAtomIfNeeded(atom);
	}
	
}
