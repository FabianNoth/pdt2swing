package pdt.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import pdt.gui.data.IdListener;

public class ImagePanel extends JPanel implements IdListener {

	private static final long serialVersionUID = 1L;
	private BufferedImage img;
	private BufferedImage noImg;
	private File imgDir;
	private final Set<ActionListener> actionListener = new HashSet<ActionListener>();
	
	private int posX = 0;
	private int posY = 0;
	
	public ImagePanel(File imgDir) {
		this(imgDir, null);
	}
	
	public ImagePanel(File imgDir, ActionListener listener) {
		this.imgDir = imgDir;
		addActionListener(listener);
		try {
			noImg = ImageIO.read(new File(imgDir, "no_image.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() >= 2 && e.getButton() == MouseEvent.BUTTON1) {
					notifyListeners();
				}
			}
		});

		setPreferredSize(new Dimension(220, 310));
		setMinimumSize(new Dimension(220, 310));
	}
	
	protected void notifyListeners() {
		ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "image_clicked");
		for (ActionListener l : actionListener) {
			l.actionPerformed(event);
		}
	}

	private void addActionListener(ActionListener listener) {
		if (listener != null) {
			actionListener.add(listener);
		}
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		calcPos();
		if (img == null) {
			g.drawImage(noImg, posX, posY, null);
		} else {
			g.drawImage(img, posX, posY, null);
		}
	}
	
	@Override
	public void setId(String id) {
		if (id == null) {
			img = null;
		} else {
			File imgFile = new File(imgDir, id + ".jpg");
			try {
				img = ImageIO.read(imgFile);
			} catch (IOException e) {
				img = null;
			}
		}
		calcPos();
		repaint();		
	}

	private void calcPos() {
		BufferedImage useImg = img;
		if (useImg == null) {
			useImg = noImg;
		}
		int diffX = getWidth() - useImg.getWidth();
		int diffY = getHeight() - useImg.getHeight();
		if (diffX >= 0) {
			posX = diffX / 2;
		} else {
			posX = 0;
		}
		
		if (diffY >= 0) {
			posY = diffY / 2;
		} else {
			posY = 0;
		}
	}

	@Override
	public void persistFacts() {}

}
