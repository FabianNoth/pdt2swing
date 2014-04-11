package pdt.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import pdt.gui.data.PrologMultipleFactHandler;

public class OneToManyPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private DefaultListModel<String> listModel;
	private boolean useAutoCompletion;
	private JTextField tfAdd;

	public OneToManyPanel(final PrologMultipleFactHandler prolog) {
		prolog.setEditPanel(this);
		useAutoCompletion = prolog.isAutoCompletion();
		
		setLayout(new BorderLayout());
		JPanel addPanel = new JPanel();
		addPanel.setLayout(new BorderLayout());
		add(addPanel, BorderLayout.NORTH);
		
		tfAdd = new JTextField();
		addPanel.add(tfAdd, BorderLayout.CENTER);
		
		JButton btAdd = new JButton("Add");
		btAdd.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent evt) {
				prolog.addValue(tfAdd.getText());
			}
		});
		addPanel.add(btAdd, BorderLayout.EAST);
		
		final JList<String> list = new JList<String>();
		list.addKeyListener(new KeyAdapter() {
			@Override public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_DELETE) {
					String s = list.getSelectedValue();
					int answer = JOptionPane.showConfirmDialog(OneToManyPanel.this, "Soll der Eintrag \"" + s + "\" wirklich gelöscht werden?", "Eintrag löschen", JOptionPane.YES_NO_OPTION);
					if (answer == JOptionPane.YES_OPTION) {
						prolog.removeValue(s);
					}
				}
			}
		});
		listModel = new DefaultListModel<>();
		list.setModel(listModel);
		add(new JScrollPane(list), BorderLayout.CENTER);
	}

	public void setData(List<String> entries) {
		if (!useAutoCompletion) {
			tfAdd.setText("");
		}
		listModel.clear();
		for (String s : entries) {
			listModel.addElement(s);
		}
	}
	
}
