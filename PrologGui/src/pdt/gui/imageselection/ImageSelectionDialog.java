package pdt.gui.imageselection;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JDialog;

import pdt.gui.utils.ImageUtils;
import pdt.gui.utils.PrologUtils;

public class ImageSelectionDialog extends JDialog implements KeyListener {

	private static final long serialVersionUID = 1L;
	private ImageSelectionPanel pnl;
	private BufferedImage cuttedImg = null;

	public ImageSelectionDialog() {
		super(PrologUtils.getActiveFrame(), Dialog.ModalityType.APPLICATION_MODAL);
		pnl = new ImageSelectionPanel(1000, 800);
		addKeyListener(this);
		addKeyListener(pnl);
		
		Container contPane = this.getContentPane();
		contPane.setLayout(new BorderLayout());
		contPane.add(pnl, BorderLayout.CENTER);
		this.pack();
	}

	public void setFile(File f) {
		cuttedImg = null;
		BufferedImage img = ImageUtils.loadImage(f);
		pnl.setImage(img);
		pnl.setFullSelectionBox(Color.RED);
	}
	
	public void setFile(File f, double scale) {
		cuttedImg = null;
		pnl.setImage(ImageUtils.loadImage(f));
		pnl.setSelectionBox(new FixedSelectionBox(0, 0, 100, scale, Color.RED));
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			cuttedImg = pnl.cutImage();
			this.setVisible(false);
		} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			cuttedImg = null;
			this.setVisible(false);
		}
	}
	
	public BufferedImage getResultImage() {
		return cuttedImg;
	}

	@Override
	public void keyPressed(KeyEvent e) {}

	@Override
	public void keyTyped(KeyEvent e) {}
	
}
