package pdt.gui.datapanels;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import pdt.gui.datapanels.handler.PrologTextFileHandler;

public class TextFilePanel extends JPanel implements DataPanel {

	private static final long serialVersionUID = 1L;
	private JTextArea textArea;
	private JButton btUpdate;
	
	private String dummyText = "";
	
	public TextFilePanel(final PrologTextFileHandler handler) {
		super();
		handler.setEditPanel(this);
		setLayout(new BorderLayout());
		textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		
		textArea.addKeyListener(new KeyAdapter() {
			@Override public void keyReleased(KeyEvent evt) {
				if (evt.getKeyCode() == KeyEvent.VK_S && evt.isControlDown()) {
					saveData(handler);
				}
			}
		});
		
		JScrollPane scrollPane = new JScrollPane(textArea);
		add(scrollPane, BorderLayout.CENTER);
		btUpdate = new JButton("Update");
		btUpdate.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				saveData(handler);
			}
		});
		btUpdate.setEnabled(false);
		add(btUpdate, BorderLayout.SOUTH);
	}

	private void saveData(final PrologTextFileHandler handler) {
		handler.updateFromPanel();
		dummyText = textArea.getText();
	}
	
	public void setData(String text) {
		textArea.setText(text);
		dummyText = text;
		updateButtons(true);
	}

	public String getData() {
		return textArea.getText();
	}
	
	private void updateButtons(boolean enabled) {
		btUpdate.setEnabled(enabled);
	}

	@Override
	public void clearPanel() {
		setData("");
		updateButtons(false);
	}
	
	@Override
	public boolean changed() {
		return(!textArea.getText().equals(dummyText));
	}



}
