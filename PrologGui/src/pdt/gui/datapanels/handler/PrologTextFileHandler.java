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
import org.markdown4j.Markdown4jProcessor;

import pdt.gui.datapanels.TextFilePanel;
import pdt.gui.datapanels.TextPreviewPanel;
import pdt.gui.utils.SimpleLogger;

public class PrologTextFileHandler extends PrologDataHandler<TextFilePanel> {
	
	private File file;
	private File outputDir;
	
	private final Map<String, ActionListener> additionalActions = new TreeMap<String, ActionListener>();
	private BasicTextEncryptor textEncryptor;
	private TextPreviewPanel preview;
	
	protected PrologTextFileHandler(String name, File outputDir, BasicTextEncryptor textEncryptor, boolean showPreview) {
		super(name);
		this.textEncryptor = textEncryptor;
		this.outputDir = outputDir;
		if (showPreview) {
			this.preview = new TextPreviewPanel(this);
		}
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
			setData(text);
		} catch (IOException e) {
			setData("");
		}
	}

	public void updatePreview() {
		if (preview != null) {
			try {
				String html = new Markdown4jProcessor().process(getEditPanel().getData());
				preview.setData(html);
			} catch (IOException e) {
				preview.setData("Can not parse text!");
				SimpleLogger.error(e.getMessage());
			}
		}
	}
	
	private void setData(String data) {
		getEditPanel().setData(data);
		if (preview != null) {
			try {
				String html = new Markdown4jProcessor().process(data);
				preview.setData(html);
			} catch (IOException e) {
				preview.setData("Can not parse text!");
				SimpleLogger.error(e.getMessage());
			}
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
	
//	@Override
//	public void persistFacts() {}


	public TextPreviewPanel getPreview() {
		return preview;
	}

	@Override
	public void clearData() {
		super.clearData();
		if (preview != null) {
			preview.clearPanel();
		}
	}
	
}
 