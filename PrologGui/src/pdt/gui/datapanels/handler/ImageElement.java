package pdt.gui.datapanels.handler;

public class ImageElement {

	private String suffix;
	private int maxWidth;
	private int maxHeight;
	
	public ImageElement(String name) {
		this(name, -1, -1);
	}
	
	public ImageElement(String name, int maxWidth, int maxHeight) {
		this.suffix = name;
		this.maxHeight = maxHeight;
		this.maxWidth = maxWidth;
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
	
}
