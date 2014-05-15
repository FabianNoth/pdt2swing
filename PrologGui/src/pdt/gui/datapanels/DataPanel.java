package pdt.gui.datapanels;

public interface DataPanel {
	
	/**
	 * clear the panel (no id selected)
	 */
	public void clearPanel();
	
	/**
	 * check if something on this panel changed, after the last update
	 * @return true if something changed, false otherwise
	 */
	public boolean changed();
	
}
