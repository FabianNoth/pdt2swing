package pdt.gui.datapanels;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import pdt.gui.datapanels.handler.PrologTextFileHandler;

public class TextFilePanel extends JPanel {

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
		JScrollPane scrollPane = new JScrollPane(textArea);
		add(scrollPane, BorderLayout.CENTER);
		btUpdate = new JButton("Update");
		btUpdate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				handler.updateFromPanel();
				dummyText = textArea.getText();
			}
		});
		btUpdate.setEnabled(false);
		add(btUpdate, BorderLayout.SOUTH);
	}
	
	public void setData(String text) {
		textArea.setText(text);
		dummyText = text;
		updateButtons(true);
	}

	public String getData() {
		return textArea.getText();
	}

	// TODO: add interface for clearPanel(), updateButtons(boolean) and changed()
	//       every handler has a Panel reference where these methods are called
	public void clearPanel() {
		setData("");
		updateButtons(false);
	}
	
	private void updateButtons(boolean enabled) {
		btUpdate.setEnabled(enabled);
	}
	
	public boolean changed() {
		return(!textArea.getText().equals(dummyText));
	}


}
