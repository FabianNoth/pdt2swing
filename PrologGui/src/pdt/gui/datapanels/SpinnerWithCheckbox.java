package pdt.gui.datapanels;

import java.awt.BorderLayout;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;

import org.cs3.prolog.common.QueryUtils;

public class SpinnerWithCheckbox extends JPanel {

	private static final long serialVersionUID = 1L;

	private JSpinner spinner;
	private JCheckBox checkBox;
	
	public SpinnerWithCheckbox(SpinnerModel model) {
		super();
		spinner = new JSpinner(model);
		checkBox = new JCheckBox("?");
		setLayout(new BorderLayout());
		add(spinner, BorderLayout.CENTER);
		add(checkBox, BorderLayout.EAST);
	}

	public String getValue() {
		String prefix = "s";
		if (checkBox.isSelected()) {
			prefix = "u";
		}
		return QueryUtils.buildTerm(prefix, spinner.getValue().toString());
	}
	
	public void setValue(String value) {
		// select checkbox, if value is unsure
		checkBox.setSelected(value.charAt(0) == 'u');
		
		String dummy = value.substring(2, value.length()-1);
		spinner.setValue(Integer.parseInt(dummy));
	}
}
