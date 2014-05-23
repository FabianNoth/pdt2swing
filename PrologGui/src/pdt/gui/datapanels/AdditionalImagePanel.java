package pdt.gui.datapanels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import pdt.gui.datapanels.handler.AdditionalImageHandler;
import pdt.gui.utils.ImageUtils;
import pdt.gui.utils.SimpleLogger;

public class AdditionalImagePanel extends JPanel implements DataPanel {

	private static final long serialVersionUID = 1L;
	
	private final HashMap<String, JLabel> imageLabels = new HashMap<>();
	private final Icon emptyIcon;

	public AdditionalImagePanel(final AdditionalImageHandler handler) {
		handler.setEditPanel(this);

		URL res = ClassLoader.getSystemClassLoader().getResource("res/empty.png");
		emptyIcon = new ImageIcon(res);
		
		List<String> imageNames = handler.getImageNames();
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[imageNames.size() + 1];
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[imageNames.size() + 1];

		for (int i=0; i<=imageNames.size(); i++) {
			gridBagLayout.rowHeights[i] = 0;
			gridBagLayout.rowWeights[i] = 0.0;
		}
		gridBagLayout.rowWeights[imageNames.size()] = Double.MIN_VALUE;
		
		setLayout(gridBagLayout);
		
		
		for (int i=0; i<imageNames.size(); i++) {
			// Label
			String name = imageNames.get(i);
			
			JLabel label = new JLabel(name);
			GridBagConstraints gbc_label = new GridBagConstraints();
			gbc_label.insets = new Insets(0, 0, 5, 5);
			gbc_label.anchor = GridBagConstraints.EAST;
			gbc_label.gridx = 0;
			gbc_label.gridy = i;
			add(label, gbc_label);
			
			JLabel imgLabel = new JLabel(emptyIcon);
			Dimension imgDim = new Dimension(100, 100);
			imgLabel.setMinimumSize(imgDim);
			imgLabel.setPreferredSize(imgDim);
			
			GridBagConstraints gbc_image = new GridBagConstraints();
			gbc_image.insets = new Insets(0, 0, 5, 0);
			gbc_image.fill = GridBagConstraints.HORIZONTAL;
			gbc_image.gridx = 1;
			gbc_image.gridy = i;
			add(imgLabel, gbc_image);

			imageLabels.put(name, imgLabel);
			
			
			JButton btUpload = new JButton("Upload");
			btUpload.setActionCommand(name);
			btUpload.addActionListener(handler);
			
			GridBagConstraints gbc_button = new GridBagConstraints();
			gbc_button.insets = new Insets(0, 0, 5, 0);
			gbc_button.fill = GridBagConstraints.HORIZONTAL;
			gbc_button.gridx = 2;
			gbc_button.gridy = i;
			add(btUpload, gbc_button);
		}
	}
	
	@Override
	public void clearPanel() {
		for (String key : imageLabels.keySet()) {
			JLabel label = imageLabels.get(key);
			label.setIcon(emptyIcon);
		}
	}

	@Override
	public boolean changed() {
		return false;
	}

	public void setData(Map<String, File> imageFiles) {
		for (String key : imageLabels.keySet()) {
			JLabel label = imageLabels.get(key);
			File file = imageFiles.get(key);
			if (file == null) {
				SimpleLogger.warning(key + " is not an image");
				label.setIcon(emptyIcon);
			} else {
				if (file.isFile()) {
					Icon icon = new ImageIcon(ImageUtils.scaleImageFast(file, 100, 100));
					label.setIcon(icon);
				} else {
					label.setIcon(emptyIcon);
				}
			}
		}
	}
	
	

}
