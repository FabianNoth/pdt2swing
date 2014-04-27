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

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import pdt.gui.data.PrologRelationHandler;

public class RelationPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private DefaultListModel<String> listModel;
	private boolean useAutoCompletion;
	private JTextField tfAdd;
	private PrologRelationHandler prolog;

	public RelationPanel(final PrologRelationHandler prolog) {
		this.prolog = prolog;
		prolog.setEditPanel(this);
		useAutoCompletion = prolog.isAutoCompletion();
		
		setLayout(new BorderLayout());
		JPanel addPanel = new JPanel();
		addPanel.setLayout(new BorderLayout());
		add(addPanel, BorderLayout.NORTH);
		tfAdd = new JTextField();
		
		ActionListener actionListener = new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				if (!tfAdd.getText().trim().isEmpty()) {
					prolog.addValue(tfAdd.getText());
				}
			}
		};
		
		tfAdd.addActionListener(actionListener);
		addPanel.add(tfAdd, BorderLayout.CENTER);
		JButton btAdd = new JButton("Add");
		btAdd.addActionListener(actionListener);
		addPanel.add(btAdd, BorderLayout.EAST);
		
		final JList<String> list = new JList<String>();
		list.addKeyListener(new KeyAdapter() {
			@Override public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_DELETE) {
					String s = list.getSelectedValue();
					int answer = JOptionPane.showConfirmDialog(RelationPanel.this, "Soll der Eintrag \"" + s + "\" wirklich gel�scht werden?", "Eintrag l�schen", JOptionPane.YES_NO_OPTION);
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
		if (useAutoCompletion) {
			AutoCompleteDecorator.decorate(tfAdd, prolog.getAutoCompletionList(), false);
		} else {
			tfAdd.setText("");
		}
		listModel.clear();
		for (String s : entries) {
			listModel.addElement(s);
		}
	}

}