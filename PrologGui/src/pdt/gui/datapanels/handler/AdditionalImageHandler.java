package pdt.gui.datapanels.handler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;

import pdt.gui.datapanels.AdditionalImagePanel;
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
		
		fileChooser = new JFileChooser();
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
			imageFiles.put(img.getSuffix(), new File(outputDir, imageName(img.getSuffix())));
		}
		
		getEditPanel().setData(imageFiles);
	}

	private String imageName(String suffix) {
		return currentId + "_" + suffix + ".jpg";
	}

	@Override
	public void persistFacts() {}
	
	public List<ImageElement> getImages() {
		return images;
	}

	
	@Override
	public void actionPerformed(ActionEvent evt) {
		
		// show file input dialog (only jpg files)
		int result = fileChooser.showOpenDialog(PrologUtils.getActiveFrame());
		
		if (result == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			BufferedImage outputImage;
			ImageElement image = imageMap.get(evt.getActionCommand());
			
			if (image.getMaxHeight() == -1 && image.getMaxWidth() == -1) {
				outputImage = ImageUtils.loadImage(file);
			} else {
				outputImage = ImageUtils.scaleImageSmooth(file, image.getMaxWidth(), image.getMaxHeight());
			}

			// copy to imgDir
			try {
				File destFile = new File(outputDir, imageName(evt.getActionCommand()));
				if (destFile.isFile()) {
					int answer = JOptionPane.showConfirmDialog(PrologUtils.getActiveFrame(), "Das Bild exisitert bereits. Soll es überschrieben werden?", "Bild überschreiben", JOptionPane.YES_NO_OPTION);
					if (answer == JOptionPane.NO_OPTION) {
						return;
					}
				}
				ImageIO.write(outputImage, "jpg", destFile);
				getEditPanel().setData(imageFiles);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
}
