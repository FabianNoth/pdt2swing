package pdt.gui.datapanels;

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

import pdt.gui.datapanels.handler.PrologRelationHandler;

public class RelationPanel extends JPanel implements DataPanel {

	private static final long serialVersionUID = 1L;
	private DefaultListModel<String> listModel;
	private boolean useAutoCompletion;
	private JTextField tfAdd;
	private PrologRelationHandler prolog;
	private JButton btAdd;
	private JList<String> list;

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
		btAdd = new JButton("Add");
		btAdd.addActionListener(actionListener);
		addPanel.add(btAdd, BorderLayout.EAST);
		btAdd.setEnabled(false);
		
		list = new JList<String>();
		list.addKeyListener(new KeyAdapter() {
			@Override public void keyReleased(KeyEvent e) {
				System.out.println(e.getKeyCode());
				if (e.getKeyCode() == KeyEvent.VK_DELETE) {
					String s = list.getSelectedValue();
					int answer = JOptionPane.showConfirmDialog(RelationPanel.this, "Soll der Eintrag \"" + s + "\" wirklich gelöscht werden?", "Eintrag löschen", JOptionPane.YES_NO_OPTION);
					if (answer == JOptionPane.YES_OPTION) {
						prolog.removeValue(s);
					}
				}
			}
		});
		listModel = new DefaultListModel<>();
		list.setModel(listModel);
		list.setEnabled(false);
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
		updateButtons(true);
	}

	@Override
	public void clearPanel() {
		tfAdd.setText("");
		listModel.clear();
		updateButtons(false);
	}
	
	private void updateButtons(boolean enabled) {
		btAdd.setEnabled(enabled);
		list.setEnabled(enabled);
	}

	@Override
	public boolean changed() {
		// relations are always added directly, so there are no unsafed states
		// (entry in the textfield is not important)
		return false;
	}

}
