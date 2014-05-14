package pdt.gui.datapanels;

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
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import pdt.gui.data.IdListener;
import pdt.gui.utils.ImageUtils;

public class ImagePanel extends JPanel implements IdListener {

	private static final long serialVersionUID = 1L;
	private BufferedImage img;
	private BufferedImage noImg;
	private File imgDir;
	private final Set<ActionListener> actionListener = new HashSet<ActionListener>();
	
	private int posX = 0;
	private int posY = 0;
	
	private String id;
	
	public ImagePanel(File imgDir) {
		this(imgDir, null, false);
	}
	
	public ImagePanel(final File imgDir, ActionListener listener, boolean allowImageUpload) {
		this.imgDir = imgDir;
		if (listener == null) {
			if (allowImageUpload) {
				// add image upload action
				actionListener.add(createImageUploadAction());
			}
		} else {
			actionListener.add(listener);
		}
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
	
	private ActionListener createImageUploadAction() {
		ActionListener uploadListener = new ActionListener() {
			private JFileChooser fileChooser = new JFileChooser();
			@Override public void actionPerformed(ActionEvent e) {
				// show file input dialog (only jpg files)
				int result = fileChooser.showOpenDialog(null);
				fileChooser.setFileFilter(new FileFilter() {
					
					@Override
					public String getDescription() {
						return "JPG Images";
					}
					
					@Override
					public boolean accept(File f) {
						return f.getName().toLowerCase().endsWith(".jpg");
					}
				});
				if (result == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					BufferedImage outputImage = ImageUtils.scaleImage(file, 200, 300);

					// copy to imgDir
					try {
						File destFile = new File(imgDir, id + ".jpg");
						if (destFile.isFile()) {
							int answer = JOptionPane.showConfirmDialog(ImagePanel.this, "Das Bild exisitert bereits. Soll es überschrieben werden?", "Bild überschreiben", JOptionPane.YES_NO_OPTION);
							if (answer == JOptionPane.NO_OPTION) {
								return;
							}
						}
						ImageIO.write(outputImage, "jpg", destFile);
//						FileUtils.copyFile(file, destFile);
						setId(id);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		};
		return uploadListener;
	}

	protected void notifyListeners() {
		if (id != null) {
			ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "image_clicked");
			for (ActionListener l : actionListener) {
				l.actionPerformed(event);
			}
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
		this.id = id;
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
