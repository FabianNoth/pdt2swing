package pdt.gui.imageselection;

import java.awt.Color;
import java.awt.Rectangle;

public class FixedSelectionBox extends SelectionBox {

	private double ratio;

	public FixedSelectionBox(int x, int y, int width, double ratio) {
		this(x, y, width, ratio, Color.BLACK);
	}
	
	public FixedSelectionBox(int x, int y, int width, double ratio, Color color) {
		super(x, y, width, (int) (width * (1/ratio)), color);
		this.ratio = 1/ratio;
	}
	
	@Override
	public void increaseSize(int amountX, int amountY) {
		increaseSize(amountX);
	}

	@Override
	public void increaseSize(int amount) {
		super.increaseSize(amount, (int) (amount*ratio));
	}
	
	@Override
	public void setRectangle(Rectangle rect, double scale) {
		Rectangle newRect = new Rectangle(rect);
		newRect.height = (int) (newRect.width * ratio);
		super.setRectangle(newRect, scale);
	}

}
