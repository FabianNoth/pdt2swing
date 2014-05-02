package pdt.gui.data;

import java.util.List;
import java.util.Map;

public class PrologFilterUtils {

	public static PrologFilter createFullFilter() {
		return new PrologFilter() {
			@Override
			public List<Map<String, Object>> getFilteredList(List<Map<String, Object>> full) {
				return full;
			}
			
			@Override
			public boolean accept(Map<String, Object> entry) {
				return true;
			}
		};
	}

	public static PrologFilter createEqualsFilter(final String key, final String compareValue) {
		return new PrologFilter() {
			@Override public boolean accept(Map<String, Object> entry) {
				String value = entry.get(key).toString();
				return compareValue.equals(value);
			}
		};
	}
	
	public static PrologFilter createPrefixFilter(final String key, final String prefix) {
		return new PrologFilter() {
			@Override public boolean accept(Map<String, Object> entry) {
				String value = entry.get(key).toString();
				if (value == null) {
					return false;
				}
				return value.startsWith(prefix);
			}
		};
	}

	public static PrologFilter createIntegerFilter(final String key, final int from, final int to) {
		return new PrologFilter() {
			@Override public boolean accept(Map<String, Object> entry) {
				String value = entry.get(key).toString();
				if (value == null) {
					return false;
				}
				int intValue = Integer.parseInt(value);
				return intValue >= from && intValue <= to;
			}
		};
	}
}
