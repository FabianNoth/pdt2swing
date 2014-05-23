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
	private final List<String> imageNames = new ArrayList<String>();
	private final Map<String, File> imageFiles = new HashMap<String, File>();
	private JFileChooser fileChooser;
	
	protected AdditionalImageHandler(String name, File outputDir, String... imageNames) {
		super(name);
		this.outputDir = outputDir;
		for (String s : imageNames) {
			this.imageNames.add(s);
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
		
		for (String name : imageNames) {
			imageFiles.put(name, new File(outputDir, currentId + "_" + name + ".jpg"));
		}
		
		getEditPanel().setData(imageFiles);
	}

	@Override
	public void persistFacts() {}
	
	public List<String> getImageNames() {
		return imageNames;
	}

	
	@Override
	public void actionPerformed(ActionEvent evt) {
		
		// show file input dialog (only jpg files)
		int result = fileChooser.showOpenDialog(PrologUtils.getActiveFrame());
		
		if (result == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			BufferedImage outputImage = ImageUtils.loadImage(file);

			// copy to imgDir
			try {
				File destFile = new File(outputDir, currentId + "_" + evt.getActionCommand() + ".jpg");
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
