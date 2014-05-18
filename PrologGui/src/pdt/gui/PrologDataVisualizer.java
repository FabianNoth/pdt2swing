package pdt.gui;

import pdt.gui.data.PrologConnection;
import pdt.gui.data.PrologGuiBundle;

public interface PrologDataVisualizer {

	public boolean changePrologId(String id);
	public void changedDatabase(String id);
	public PrologConnection getPrologConnection();
	
	public void setBundle(PrologGuiBundle bundle);
}
