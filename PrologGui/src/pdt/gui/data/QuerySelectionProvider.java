package pdt.gui.data;

import pdt.gui.QueryNode;

public interface QuerySelectionProvider {

	/**
	 * Create the complete node hierarchy and return the root node
	 * @return the root element for all possible queries
	 */
	public QueryNode createRoot();
	
}
