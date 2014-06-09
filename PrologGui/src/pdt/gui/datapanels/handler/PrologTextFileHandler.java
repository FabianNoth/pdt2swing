package pdt.gui.datapanels.handler;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.jasypt.util.text.BasicTextEncryptor;

import pdt.gui.datapanels.TextFilePanel;
import pdt.gui.utils.SimpleLogger;

public class PrologTextFileHandler extends PrologDataHandler<TextFilePanel> {

	private File file;
	private File outputDir;
	
	private final Map<String, ActionListener> additionalActions = new TreeMap<String, ActionListener>();
	private BasicTextEncryptor textEncryptor;
	
	protected PrologTextFileHandler(String name, File outputDir, BasicTextEncryptor textEncryptor) {
		super(name);
		this.textEncryptor = textEncryptor;
		this.outputDir = outputDir;
	}
	
	@Override
	public void showData() {
		file = new File(outputDir, currentId);
		try {
			String text = null;
			if (textEncryptor == null) {
				text = FileUtils.readFileToString(file);
			} else {
				String encryptedText = FileUtils.readFileToString(file);
				try {
					text = textEncryptor.decrypt(encryptedText);
				} catch(EncryptionOperationNotPossibleException e) {
//					SimpleLogger.error(e.getMessage());
					SimpleLogger.error("Text wasn't encrypted correctly");
					text = encryptedText;
				}				
			}
			getEditPanel().setData(text);
		} catch (IOException e) {
			getEditPanel().setData("");
		}
	}

	public void updateFromPanel() {
		if (currentId == null) {
			return;
		}
		String text = null;
		if (textEncryptor == null) {
			text = getEditPanel().getData();
		} else {
			String plainText = getEditPanel().getData();
			text = textEncryptor.encrypt(plainText);
		}

		file = new File(outputDir, currentId);
		
		if (!outputDir.exists()) {
			int answer = JOptionPane.showConfirmDialog(getEditPanel(), "Soll das Verzeichnis angelegt werden?", "Verzeichnis existiert nicht", JOptionPane.YES_NO_OPTION);
			if (answer == JOptionPane.YES_OPTION) {
				outputDir.mkdirs();
			} else {
				return;
			}
		}
		
		try {
			FileUtils.writeStringToFile(file, text);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
//		updateVisualizer();
	}
	

	public void addAction(String actionName, ActionListener actionListener) {
		additionalActions.put(actionName, actionListener);
	}
	
	public Map<String, ActionListener> getAdditionalActions() {
		return additionalActions;
	}
	
	@Override
	public void persistFacts() {}

}
