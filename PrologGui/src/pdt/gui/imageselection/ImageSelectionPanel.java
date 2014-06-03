package pdt.gui.imageselection;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * JPanel which displays an image
 */
public class ImageSelectionPanel extends JPanel implements MouseListener, MouseWheelListener, KeyListener {

	private static final long serialVersionUID = 1L;
	
	private BufferedImage originalImg;
	private BufferedImage scaledImg;
	
	private Dimension containerSize;
	private double scale;

	private Point pressedPos;
	private SelectionBox visibleSelectionBox = null;
	
	public ImageSelectionPanel(int width, int height) {
		super();
		containerSize = new Dimension(width, height);
		setPreferredSize(containerSize);
		setMinimumSize(containerSize);
		addMouseListener(this);
		addMouseWheelListener(this);
		addKeyListener(this);
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if (scaledImg != null) {
			g.drawImage(scaledImg, 0, 0, null);
			if (visibleSelectionBox != null) {
				visibleSelectionBox.drawScaled(g, scale, scaledImg.getWidth(), scaledImg.getHeight());
			}
		}
	}

	public void scaleToContainer() {
		if (originalImg != null) {
			setPreferredSize(containerSize);
			scaleImage(containerSize);
			revalidate();
		}
	}

	public void containerUpdated(Dimension containerSize) {
		this.containerSize = containerSize;
		scaleToContainer();
	}

	private void scaleImage(Dimension dim) {
	    int newWidth = (int) dim.getWidth();
		int newHeight = (int) dim.getHeight();
		
		int origWidth = originalImg.getWidth();
		int origHeight = originalImg.getHeight();
		
		if (origWidth <= newWidth && origHeight <= newHeight) {
			// image is smaller than area, so no scaling required
			scale = 1.0;
			scaledImg = originalImg;
		} else {
			// image is larger than area, so scaling required
			double scaleWidth = (1.0 * newWidth) / origWidth;
			double scaleHeigth = (1.0 * newHeight) / origHeight;
			
			// use smallest scale factor (so that both sides match)
			scale = Math.min(scaleWidth, scaleHeigth);
			newWidth = (int) (origWidth * scale);
			newHeight = (int) (origHeight * scale);
			
			// scale image
			scaledImg = new BufferedImage(newWidth, newHeight, originalImg.getType());
			Graphics2D g = scaledImg.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.drawImage(originalImg, 0, 0, newWidth, newHeight, 0, 0, originalImg.getWidth(), originalImg.getHeight(), null);
			g.dispose();
		}
		
	}

	public void setImage(BufferedImage img) {
		this.originalImg = img;
		scaleToContainer();
		repaint();
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}
	
	@Override
	public void mousePressed(MouseEvent e) {
		pressedPos = e.getPoint();
	}

	@Override
	public void mouseReleased(MouseEvent evt) {
		if (visibleSelectionBox != null) {
			if (evt.getPoint().equals(pressedPos)) {
				// just a click
//			System.out.println("clicked at " + pressedPos);
				visibleSelectionBox.setCenter(pressedPos.x, pressedPos.y, 1/scale);
			} else {
				// drawn box
//			System.out.println("draw from " + pressedPos + " to " + e.getPoint());
				Rectangle rect = rectangleFromPoints(pressedPos, evt.getPoint());
				visibleSelectionBox.setRectangle(rect, 1/scale);
			}
			repaint();
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent evt) {
		if (visibleSelectionBox != null) {
			int amount = 10 * evt.getWheelRotation() * -1;
			visibleSelectionBox.increaseSize(amount);
			repaint();
		}
	}

	public BufferedImage cutImage() {
		if (visibleSelectionBox != null) {
			BufferedImage subimage = originalImg.getSubimage(visibleSelectionBox.getX(), visibleSelectionBox.getY(), visibleSelectionBox.getWidth(), visibleSelectionBox.getHeight());
			visibleSelectionBox = null;
			setImage(subimage);
			return subimage;
		} else {
			return null;
		}
	}
	
	private Rectangle rectangleFromPoints(Point p1, Point p2) {
		int x = Math.min(p1.x, p2.x);
		int y = Math.min(p1.y, p2.y);
		int width = Math.abs(p1.x - p2.x);
		int height = Math.abs(p1.y - p2.y);
		return new Rectangle(x, y, width, height);
	}

	public void setSelectionBox(SelectionBox fixedSelectionBox) {
		visibleSelectionBox = fixedSelectionBox;
	}

	@Override
	public void keyPressed(KeyEvent evt) {
		if (evt.getKeyCode() == KeyEvent.VK_LEFT) {
			visibleSelectionBox.moveLeft();
			repaint();
		} else if (evt.getKeyCode() == KeyEvent.VK_RIGHT) {
			visibleSelectionBox.moveRight();
			repaint();
		} else if (evt.getKeyCode() == KeyEvent.VK_UP) {
			visibleSelectionBox.moveUp();
			repaint();
		} else if (evt.getKeyCode() == KeyEvent.VK_DOWN) {
			visibleSelectionBox.moveDown();
			repaint();
		}
	}

	@Override public void keyReleased(KeyEvent evt) {}
	@Override public void keyTyped(KeyEvent evt) {}
	
}
