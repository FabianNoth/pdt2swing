package pdt.gui.data;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pdt.gui.QueryNode;
import pdt.gui.datapanels.handler.PrologDataHandler;
import pdt.prolog.elements.PrologGoal;

public class PrologGuiBundle {

	private PrologTableData tableData;
	
	private PrologGoal mainGoal;
	private QueryNode rootNode;
	
	private final List<PrologDataHandler<?>> factHandlers = new ArrayList<>();
	private File imgDir;
	// image panel stuff
	private boolean containsImagePanel = false;
	private boolean defaultImageUpload = false;
	private ActionListener imgActionListener;

	private Dimension imageDisplayDim = new Dimension(0, 0);
	private Dimension imageSizeDim = new Dimension(0, 0);
	
	private Dimension bundleSize;
	
	public PrologGuiBundle(PrologTableData tableData, PrologDataHandler<?>... factHandlers) {
		this(tableData, new Dimension(300, 100), factHandlers);
	}
	
	public PrologGuiBundle(PrologTableData tableData, Dimension bundleSize, PrologDataHandler<?>... factHandlers) {
		this.tableData = tableData;
		this.bundleSize = bundleSize;
		this.mainGoal = tableData.getGoal();
		for(PrologDataHandler<?> fh : factHandlers) {
			this.factHandlers.add(fh);
		}
	}

	public void addImagePanel(File imgDir, boolean defaultImageUpload) {
		containsImagePanel = true;
		this.imgDir = imgDir;
		this.defaultImageUpload = defaultImageUpload;
	}
	
	public void addImagePanel(File imgDir, ActionListener imgActionListener) {
		this.imgActionListener = imgActionListener;
		addImagePanel(imgDir, false);
	}
	
	public void setImageDisplayDimension(int width, int height) {
		imageDisplayDim.width = width;
		imageDisplayDim.height = height;
	}

	public void setImageSizeDimension(int width, int height) {
		imageSizeDim.width = width;
		imageSizeDim.height = height;
	}
	
	public PrologTableData getTableData() {
		return tableData;
	}

	public List<PrologDataHandler<?>> getFactHandlers() {
		return factHandlers;
	}
	
	public boolean containsImagePanel() {
		return containsImagePanel;
	}
	public File getImgDir() {
		return imgDir;
	}

	public ActionListener getImgActionListener() {
		return imgActionListener;
	}
	
	public boolean hasDefaultImageUpload() {
		return defaultImageUpload;
	}
	
	public PrologGoal getMainGoal() {
		return mainGoal;
	}
	
	public QueryNode getRootNode() {
		return rootNode;
	}

	public void setRootNode(QueryNode root) {
		this.rootNode = root;
	}

	public Dimension getImageDisplayDimension() {
		return imageDisplayDim;
	}

	public Dimension getImageSizeDimension() {
		return imageSizeDim;
	}

	public Dimension getSize() {
		return bundleSize;
	}
	
}
