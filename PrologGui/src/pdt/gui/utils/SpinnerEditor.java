package pdt.gui.utils;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.util.EventObject;

import javax.swing.DefaultCellEditor;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

public class SpinnerEditor extends DefaultCellEditor {

	private static final long serialVersionUID = 1L;
	private JSpinner spinner;
	private JSpinner.DefaultEditor editor;
	private JTextField textField;
	boolean valueSet;

	// Initializes the spinner.
	public SpinnerEditor() {
		super(new JTextField());
		
		SpinnerNumberModel model = new SpinnerNumberModel(0, 0, 10, 1);
		spinner = new JSpinner(model);
		editor = ((JSpinner.DefaultEditor)spinner.getEditor());
		textField = editor.getTextField();
		textField.addFocusListener( new FocusListener() {
			@Override
			public void focusGained( FocusEvent fe ) {
//				int end = textField.getText().length();
//				System.err.println("Got focus (" + end + ")");
				textField.selectAll();
//				textField.setSelectionStart(0);
//				textField.setSelectionEnd(end);
				SwingUtilities.invokeLater( new Runnable() {
					@Override
					public void run() {
						if ( valueSet ) {
							textField.setCaretPosition(1);
						}
						textField.selectAll();
					}
				});
			}
			@Override
			public void focusLost( FocusEvent fe ) {
			}
		});
		textField.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent ae ) {
				stopCellEditing();
			}
		});
	}

	// Prepares the spinner component and returns it.
	@Override
	public Component getTableCellEditorComponent(
			JTable table, Object value, boolean isSelected, int row, int column
			) {
		if ( !valueSet ) {
			spinner.setValue(value);
		}
		SwingUtilities.invokeLater( new Runnable() {
			@Override
			public void run() {
				textField.requestFocus();
			}
		});
		return spinner;
	}

	@Override
	public boolean isCellEditable( EventObject eo ) {
//		System.err.println("isCellEditable");
		if ( eo instanceof KeyEvent ) {
			KeyEvent ke = (KeyEvent)eo;
//			System.err.println("key event: "+ke.getKeyChar());
			textField.setText(String.valueOf(ke.getKeyChar()));
			//textField.select(1,1);
			//textField.setCaretPosition(1);
			//textField.moveCaretPosition(1);
			valueSet = true;
		} else {
			valueSet = false;
		}
		return true;
	}

	// Returns the spinners current value.
	@Override
	public Object getCellEditorValue() {
		return spinner.getValue();
	}

	@Override
	public boolean stopCellEditing() {
//		System.err.println("Stopping edit");
		try {
			editor.commitEdit();
			spinner.commitEdit();
		} catch ( java.text.ParseException e ) {
			JOptionPane.showMessageDialog(null,
					"Invalid value, discarding.");
		}
		return super.stopCellEditing();
	}
}
