package pdt.gui.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class PrologFilter {
	
	public List<Map<String, Object>> getFilteredList(List<Map<String, Object>> full) {
		ArrayList<Map<String, Object>> filteredList = new ArrayList<>();
		for (Map<String, Object> entry : full) {
			if (accept(entry)) {
				filteredList.add(entry);
			}
		}
		return filteredList;
	}
	
	
	public abstract boolean accept(Map<String, Object> entry);
	
}
