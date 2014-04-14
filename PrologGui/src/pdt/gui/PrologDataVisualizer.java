package pdt.gui;

import pdt.gui.data.PrologConnection;
import pdt.gui.data.PrologTableData;

public interface PrologDataVisualizer {

	public void changePrologId(String id);
	public void changedDatabase(String id);
	public void changePrologData(PrologTableData data); // TODO: add prologFactHandler
	public PrologConnection getPrologConnection();
	
}
