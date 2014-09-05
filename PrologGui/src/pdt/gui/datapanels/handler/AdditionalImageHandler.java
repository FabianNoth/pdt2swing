package pdt.gui.datapanels.handler;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;

import pdt.gui.datapanels.AdditionalImagePanel;
import pdt.gui.imageselection.ImageSelectionDialog;
import pdt.gui.utils.ImageUtils;
import pdt.gui.utils.PrologUtils;

public class AdditionalImageHandler extends PrologDataHandler<AdditionalImagePanel> implements ActionListener {

	private File outputDir;
	private final List<ImageElement> images = new ArrayList<>();
	private final Map<String, ImageElement> imageMap = new HashMap<>();
	private final Map<String, File> imageFiles = new HashMap<>();
	private JFileChooser fileChooser;
	
	protected AdditionalImageHandler(String name, File outputDir, ImageElement... imageNames) {
		super(name);
		this.outputDir = outputDir;
		for (ImageElement img : imageNames) {
			this.images.add(img);
			this.imageMap.put(img.getSuffix(), img);
		}
		initFileChooser();
	}

	private void initFileChooser() {
		// set look and feel to system
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		fileChooser = new JFileChooser(PrologUtils.getDownloadDirectory());
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
	}

	@Override
	public void showData() {
		imageFiles.clear();
		
		for (ImageElement img : images) {
			imageFiles.put(img.getSuffix(), getImageFile(img.getSuffix()));
		}
		
		getEditPanel().setData(imageFiles);
	}

	private String imageName(String suffix) {
		return currentId + "_" + suffix + ".jpg";
	}

	private File getImageFile(String suffix) {
		File subdir = new File(outputDir, PrologUtils.md5Prefix(currentId));
		return new File(subdir, imageName(suffix));
	}
	
//	@Override
//	public void persistFacts() {}
	
	public List<ImageElement> getImages() {
		return images;
	}

	
	@Override
	public void actionPerformed(ActionEvent evt) {
		
		// show file input dialog (only jpg files)
		int result = fileChooser.showOpenDialog(PrologUtils.getActiveFrame());
		
		if (result == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			
			ImageElement imgElement = imageMap.get(evt.getActionCommand());
			
			boolean fixedSize = imgElement.getMaxHeight() > 0;	// this automatically means that maxWidth is also > 0

			ImageSelectionDialog imgDialog = new ImageSelectionDialog();
			if (fixedSize) {
				imgDialog.setFile(file, 1.0 * imgElement.getMaxWidth() / imgElement.getMaxHeight());
			} else {
				imgDialog.setFile(file);
			}
			imgDialog.setVisible(true);
			BufferedImage outputImage = imgDialog.getResultImage();

			if (outputImage != null) {
				File destFile = getImageFile(evt.getActionCommand());
				// save to imgDir
				if (fixedSize) {
					outputImage = ImageUtils.scaleUnprop(outputImage, imgElement.getMaxWidth(), imgElement.getMaxHeight());
				}
				ImageUtils.saveImage(outputImage, destFile);
				getEditPanel().setData(imageFiles);
			}
		}
	}

	public void openImage(String name) {

		File destFile = getImageFile(name);
		if (destFile.isFile()) {
			Desktop desktop = Desktop.getDesktop();
			try {
				desktop.open(destFile);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
	}
	
}
