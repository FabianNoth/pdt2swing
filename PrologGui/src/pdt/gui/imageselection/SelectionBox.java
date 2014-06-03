package pdt.gui.imageselection;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class SelectionBox {
	
	private Rectangle selection;
	private Color color;

	public SelectionBox(int x, int y, int width, int height) {
		this(x, y, width, height, Color.BLACK);
	}
	
	public SelectionBox(int x, int y, int width, int height, Color color) {
		selection = new Rectangle(x, y, width, height);
		this.color = color;
	}
	
	public void setPoint(int x, int y) {
		selection.x = x;
		selection.y = y;
	}
	
	public void setCenter(int centerX, int centerY) {
		selection.x = centerX - selection.width / 2;
		selection.y = centerY - selection.height / 2;
	}
	
	public void increaseSize(int amount) {
		increaseSize(amount, amount);
	}
	
	public void increaseSize(int amountX, int amountY) {
		if ((selection.width + amountX) < Math.abs(amountX)) {
			selection.width = Math.abs(amountX);
		} else {
			selection.x -= amountX/2;
			selection.width += amountX;
		}
		
		if ((selection.height + amountY) < Math.abs(amountY)) {
			selection.height = Math.abs(amountY);
		} else {
			selection.y -= amountY/2;
			selection.height += amountY;
		}
		
	}

	public int getX() {
		return selection.x;
	}
	
	public int getY() {
		return selection.y;
	}
	
	public int getWidth() {
		return selection.width;
	}
	
	public int getHeight() {
		return selection.height;
	}

	public void drawScaled(Graphics g, double scale, int maxX, int maxY) {
		Color dummy = g.getColor();
		
		g.setColor(color);
		int x = (int) (selection.x * scale);
		int y = (int) (selection.y * scale);

		int width = (int) (selection.width * scale);
		
		if (width + x > maxX) {
			width = maxX - x;
		}
		int height = (int) (selection.height * scale);
		
		if (height + y > maxY) {
			height = maxY - y;
		}
		g.drawRect(x, y, width, height);
		
		g.setColor(dummy);
	}

	public void setRectangle(Rectangle rect, double scale) {
		selection.x = (int) (rect.x * scale);
		selection.y = (int) (rect.y * scale);
		selection.width = (int) (rect.width * scale);
		selection.height = (int) (rect.height * scale);
	}

	public void setPoint(int x, int y, double scale) {
		selection.x = (int) (x * scale);
		selection.y = (int) (y * scale);
	}

	public void setCenter(int x, int y, double scale) {
		setCenter((int) (x * scale), (int) (y * scale));
	}

	public void moveLeft() {
		selection.x = Math.max(0, selection.x-10);
	}
	
	public void moveRight() {
		selection.x += 10;
	}
	
	public void moveUp() {
		selection.y = Math.max(0, selection.y-10);
	}
	
	public void moveDown() {
		selection.y += 10;
	}
	
	
	
}
