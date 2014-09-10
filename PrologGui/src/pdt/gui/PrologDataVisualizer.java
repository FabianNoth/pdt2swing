package pdt.gui;

import pdt.gui.data.PrologAdapter;
import pdt.gui.data.PrologGuiBundle;

public interface PrologDataVisualizer {

	public boolean changePrologId(String id);
	public void changedDatabase(String id);
	public PrologAdapter getPrologConnection();
	
	public void setBundle(PrologGuiBundle bundle);
}
