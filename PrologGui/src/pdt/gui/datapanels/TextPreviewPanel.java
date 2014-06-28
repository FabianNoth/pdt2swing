package pdt.gui.datapanels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import pdt.gui.datapanels.handler.PrologTextFileHandler;

public class TextPreviewPanel extends JPanel implements DataPanel {

	private static final long serialVersionUID = 1L;
	private JEditorPane textArea;
	
	public TextPreviewPanel(final PrologTextFileHandler handler) {
		super();
		
		setLayout(new BorderLayout());
		textArea = new JEditorPane();
		Dimension dim = new Dimension(1000, 100);
		textArea.setMinimumSize(dim);
		textArea.setPreferredSize(dim);
		textArea.setEditable(false);
		textArea.setContentType("text/html");
		textArea.addKeyListener(new KeyAdapter() {
			@Override public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_F5) {
					handler.updatePreview();
				}
			}
		});

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				handler.updatePreview();
			}
		});
		
		
		JScrollPane scrollPane = new JScrollPane(textArea);
		add(scrollPane, BorderLayout.CENTER);
	}

	public void setData(String text) {
		textArea.setText(text);
	}

	@Override
	public void clearPanel() {
		setData("");
	}

	@Override
	public boolean changed() {
		return false;
	}
	
}
