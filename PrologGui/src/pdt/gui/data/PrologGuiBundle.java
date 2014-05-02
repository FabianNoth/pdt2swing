package pdt.gui.data;

import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PrologGuiBundle {

	private PrologTableData tableData;
	private final List<PrologDataHandler> factHandlers = new ArrayList<>();
	private File imgDir;
	private ActionListener imgListener;

	public PrologGuiBundle(PrologTableData tableData, PrologDataHandler... factHandlers) {
		this(tableData, null, null, factHandlers);
	}
	
	public PrologGuiBundle(PrologTableData tableData, File imgDir, ActionListener imgListener, PrologDataHandler... factHandlers) {
		this.tableData = tableData;
		this.imgDir = imgDir;
		this.imgListener = imgListener;
		for(PrologDataHandler fh : factHandlers) {
			this.factHandlers.add(fh);
		}
	}

	public PrologTableData getTableData() {
		return tableData;
	}

	public List<PrologDataHandler> getFactHandlers() {
		return factHandlers;
	}
	
	public File getImgDir() {
		return imgDir;
	}

	public ActionListener getImgListener() {
		return imgListener;
	}
	
	public void setFilter(PrologFilter filter) {
		tableData.setFilter(filter);
	}
	
}
