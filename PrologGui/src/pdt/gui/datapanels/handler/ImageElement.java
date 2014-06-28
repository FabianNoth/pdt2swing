package pdt.gui.datapanels.handler;

import pdt.gui.utils.SimpleLogger;

public class ImageElement {

	private String suffix;
	
	private int maxWidth = 0;
	private int maxHeight = 0;
	private boolean createFromDefaultPicture = false;
	
	private int maxSize = 0;

	public ImageElement(String suffix) {
		this.suffix = suffix;
	}
	
	public ImageElement(String suffix, boolean createFromDefaultPicture) {
		this.suffix = suffix;
		this.createFromDefaultPicture = createFromDefaultPicture;
	}
	
	public ImageElement(String suffix, int maxSize, boolean createFromDefaultPicture) {
		this.suffix = suffix;
		this.maxSize = maxSize;
		this.createFromDefaultPicture = createFromDefaultPicture;
		if (maxSize <= 0) {
			SimpleLogger.warning("maxSize must have a positive value");
		}
	}
	
	public ImageElement(String suffix, int maxWidth, int maxHeight, boolean createFromDefaultPicture) {
		this.suffix = suffix;
		this.maxHeight = maxHeight;
		this.maxWidth = maxWidth;
		this.createFromDefaultPicture = createFromDefaultPicture;
		if (maxHeight <= 0 || maxWidth <= 0) {
			this.maxHeight = 0;
			this.maxWidth = 0;
			SimpleLogger.warning("maxSize must have a positive value");
		}
	}

	public String getSuffix() {
		return suffix;
	}

	public int getMaxWidth() {
		return maxWidth;
	}

	public int getMaxHeight() {
		return maxHeight;
	}
	
	public int getMaxSize() {
		return maxSize;
	}
	
	public boolean createFromDefaultPicture() {
		return createFromDefaultPicture;
	}
	
}
