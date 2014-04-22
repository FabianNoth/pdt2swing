package pdt.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import pdt.gui.data.PrologTextFileHandler;

public class TextFilePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextArea textArea;
	
	public TextFilePanel(final PrologTextFileHandler handler) {
		super();
		handler.setEditPanel(this);
		setLayout(new BorderLayout());
		textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		JScrollPane scrollPane = new JScrollPane(textArea);
		add(scrollPane, BorderLayout.CENTER);
		JButton btUpdate = new JButton("Update");
		btUpdate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				handler.updateFromPanel();
			}
		});
		add(btUpdate, BorderLayout.SOUTH);
	}
	
	public void setData(String text) {
		textArea.setText(text);
	}

	public String getData() {
		return textArea.getText();
	}


}
