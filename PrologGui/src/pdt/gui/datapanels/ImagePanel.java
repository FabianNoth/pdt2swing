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
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import pdt.gui.data.IdListener;
import pdt.gui.imageselection.ImageSelectionDialog;
import pdt.gui.utils.ImageUtils;
import pdt.gui.utils.PrologUtils;

public class ImagePanel extends JPanel implements IdListener {

	private static final long serialVersionUID = 1L;
	private BufferedImage img;
	private BufferedImage noImg;
	private File imgDir;
	private final Set<ActionListener> actionListener = new HashSet<ActionListener>();
	
	private int posX = 0;
	private int posY = 0;

	private Dimension displayDimension = new Dimension(0, 0);
	private Dimension imageDimension = new Dimension(0, 0);
	
	private String id;

	public ImagePanel(File imgDir, Dimension dim) {
		this(imgDir, null, false, dim);
	}
	
	public ImagePanel(File imgDir, Dimension displayDim, Dimension imageDim) {
		this(imgDir, null, false, displayDim, imageDim);
	}

	public ImagePanel(final File imgDir, ActionListener listener, boolean allowImageUpload, Dimension displayDim) {
		this(imgDir, listener, allowImageUpload, displayDim, new Dimension(0, 0));
	}
	
	public ImagePanel(final File imgDir, ActionListener listener, boolean allowImageUpload, Dimension displayDim, Dimension imageDim) {
		this.imgDir = imgDir;
		this.displayDimension = displayDim;
		this.imageDimension = imageDim;
		
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

		setPreferredSize(new Dimension(displayDim.width + 20, displayDim.height + 10));
		setMinimumSize(new Dimension(displayDim.width + 20, displayDim.height + 10));
	}
	
	private ActionListener createImageUploadAction() {
		ActionListener uploadListener = new ActionListener() {
			private JFileChooser fileChooser = new JFileChooser(PrologUtils.getDownloadDirectory());
			@Override public void actionPerformed(ActionEvent e) {
				// show file input dialog (only jpg files)
				fileChooser.setFileFilter(new FileFilter() {
					
					@Override
					public String getDescription() {
						return "JPG Images";
					}
					
					@Override
					public boolean accept(File f) {
						return f.isDirectory() || f.getName().toLowerCase().endsWith(".jpg");
					}
				});
				int result = fileChooser.showOpenDialog(PrologUtils.getActiveFrame());
				if (result == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();

					ImageSelectionDialog imgFrame = new ImageSelectionDialog();
					
					if (imageDimension.height > 0) {
						// there is a fixed ratio for the images
						imgFrame.setFile(file, 1.0 * imageDimension.width / imageDimension.height);
					} else {
						// there is no fixed ratio
						imgFrame.setFile(file);
					}
					imgFrame.setVisible(true);
					
					BufferedImage outputImage = imgFrame.getResultImage();
					
					if (outputImage != null) {
						// save to imgDir
						if (imageDimension.height > 0) {
							// resize image to fixed size (unproportional to avoid rounding errors and to ensure exact size)
							outputImage = ImageUtils.scaleUnprop(outputImage, imageDimension.width, imageDimension.height);
						}
						ImageUtils.saveImage(outputImage, getImageFile());
						setId(id);
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
			File imgFile = getImageFile();
			try {
				img = ImageIO.read(imgFile);
				// check if image needs resizing
				if (img.getWidth() > displayDimension.width || img.getHeight() > displayDimension.height) {
					// resize
					img = ImageUtils.scaleImageSmooth(img, displayDimension.width, displayDimension.height);
				}
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

//	@Override
//	public void persistFacts() {}

	@Override
	public boolean changed() {
		return false;
	}

	private File getImageFile() {
		File subdir = new File(imgDir, PrologUtils.md5Prefix(id));
		return new File(subdir, id + ".jpg");
	}

}
