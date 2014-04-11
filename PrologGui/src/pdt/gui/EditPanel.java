package pdt.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import pdt.gui.data.PrologSingleFactHandler;
import pdt.prolog.elements.PrologArgument;
import pdt.prolog.elements.PrologNumberRangeArgument;

public class EditPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private final HashMap<String, JComponent> textFields = new HashMap<>();

	/**
	 * Create the panel.
	 */
	public EditPanel(final PrologSingleFactHandler prolog) {

		Map<String, ActionListener> additionalActions = prolog.getAdditionalActions();
		prolog.setEditPanel(this);
		String[] variables = prolog.getArgNames();
		
		int rowCount = 0;
		if (prolog.isMainPredicate()) {
			rowCount = variables.length + 3 + additionalActions.size()/2;
		} else {
			rowCount = variables.length + 2 + additionalActions.size()/2;
		}
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[rowCount];
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[rowCount];
		
		for (int i=0; i<=rowCount-1; i++) {
			gridBagLayout.rowHeights[i] = 0;
			gridBagLayout.rowWeights[i] = 0.0;
		}
		gridBagLayout.rowWeights[rowCount-1] = Double.MIN_VALUE;
		
		setLayout(gridBagLayout);
		
		for (int i=0; i<variables.length; i++) {
			// Label
			String s = variables[i];
			JLabel label = new JLabel(s);
			GridBagConstraints gbc_label = new GridBagConstraints();
			gbc_label.insets = new Insets(0, 0, 5, 5);
			gbc_label.anchor = GridBagConstraints.EAST;
			gbc_label.gridx = 0;
			gbc_label.gridy = i;
			add(label, gbc_label);
			
			PrologArgument arg = prolog.getArgumentWithName(s);
			
			
			JComponent component = null;
			
			if (arg instanceof PrologNumberRangeArgument) {
				PrologNumberRangeArgument numberArg = (PrologNumberRangeArgument) arg;
				component = new JSpinner(new SpinnerNumberModel(numberArg.getLimitMin(), numberArg.getLimitMin(), numberArg.getLimitMax(), 1));
				
			} else {
				component = new JTextField();
				((JTextField) component).setColumns(10);
			}
			
			GridBagConstraints gbc_component = new GridBagConstraints();
			gbc_component.insets = new Insets(0, 0, 5, 0);
			gbc_component.fill = GridBagConstraints.HORIZONTAL;
			gbc_component.gridx = 1;
			gbc_component.gridy = i;
			add(component, gbc_component);
			
			textFields.put(s, component);
			
		}
		
		JButton btUpdate = new JButton("Update");
		btUpdate.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent evt) {
				prolog.updateFromPanel(textFields);
			}
		});
		GridBagConstraints gbc_btUpdate = new GridBagConstraints();
		gbc_btUpdate.insets = new Insets(0, 0, 5, 5);
		gbc_btUpdate.anchor = GridBagConstraints.EAST;
		gbc_btUpdate.gridx = 0;
		gbc_btUpdate.gridy = variables.length;
		add(btUpdate, gbc_btUpdate);
		

		int x = 1;
		int y = variables.length;
		if (prolog.isMainPredicate()) {
			JButton btSaveAsNew = new JButton("Save as New");
			btSaveAsNew.addActionListener(new ActionListener() {
				@Override public void actionPerformed(ActionEvent evt) {
					prolog.saveAsNew(textFields);
				}
			});
			GridBagConstraints gbc_btSaveAsNew = new GridBagConstraints();
			gbc_btSaveAsNew.insets = new Insets(0, 0, 5, 0);
			gbc_btSaveAsNew.fill = GridBagConstraints.HORIZONTAL;
			gbc_btSaveAsNew.gridx = 1;
			gbc_btSaveAsNew.gridy = variables.length;
			add(btSaveAsNew, gbc_btSaveAsNew);
			
			JButton btDelete = new JButton("Delete");
			btDelete.addActionListener(new ActionListener() {
				@Override public void actionPerformed(ActionEvent evt) {
					int answer = JOptionPane.showConfirmDialog(EditPanel.this, "Soll der Eintrag wirklich gelöscht werden?", "Eintrag löschen", JOptionPane.YES_NO_OPTION);
					if (answer == JOptionPane.YES_OPTION) {
						prolog.delete();
					}
				}
			});
			GridBagConstraints gbc_btDelete = new GridBagConstraints();
			gbc_btDelete.insets = new Insets(0, 0, 5, 5);
			gbc_btDelete.anchor = GridBagConstraints.EAST;
			gbc_btDelete.gridx = 0;
			gbc_btDelete.gridy = variables.length+1;
			add(btDelete, gbc_btDelete);
			
			y++;
		}
		
		
		for( Map.Entry<String, ActionListener> entry : additionalActions.entrySet() ) {
			JButton button = new JButton(entry.getKey());
			button.addActionListener(entry.getValue());
			
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.insets = new Insets(0, 0, 5, 5);
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.gridx = x;
			gbc.gridy = y;
			add(button, gbc);
			
			if (x == 1) {
				x = 0;
				y++;
			} else {
				x = 1;
			}
		}
		
	}

	public void setData(Map<String, Object> result) {
		for(String s : textFields.keySet()) {
			setSingleEntry(s, result.get(s).toString());
		}
	}
	
	public void setSingleEntry(String key, String value) {
		JComponent tf = textFields.get(key);
		String setValue = value;
		if (setValue == null) {
			setValue = "";
		}
		
		if (tf instanceof JTextField) {
			((JTextField) tf).setText(value);
		} else if (tf instanceof JSpinner) {
			((JSpinner) tf).setValue(Integer.parseInt(value));
		}
	}
	
	public String getSingleEntry(String key) {
		JComponent tf = textFields.get(key);
		String result = null;
		if (tf != null) {
			if (tf instanceof JTextField) {
				result = ((JTextField) tf).getText();
			} else if (tf instanceof JSpinner) {
				result = ((JSpinner) tf).getValue().toString();
			}
		}
		return result;
	}

}
