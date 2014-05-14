package pdt.gui.datapanels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import org.cs3.prolog.common.QueryUtils;

import pdt.prolog.elements.PrologArgument;
import pdt.prolog.elements.PrologNumberRangeArgument;

public class RatingTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	private List<String> labels;
	private List<Integer> values;
	private List<Boolean> unsure;
	
	public RatingTableModel(PrologArgument[] args) {
		labels = new ArrayList<String>(args.length-1);
		values = new ArrayList<Integer>(args.length-1);
		unsure = new ArrayList<Boolean>(args.length-1);
		
		for (int i=1; i<args.length; i++) {
			if (args[i] instanceof PrologNumberRangeArgument) {
				labels.add(args[i].getName());
				values.add(0);
				unsure.add(true);
			} else {
				System.err.println("Wrong input: " + args[i].getName());
			} 
		}
		
	}
	
	@Override
	public int getColumnCount() {
		// TODO: add possibility for 2 columns (without checkbox)
		return 3;
	}

	@Override
	public int getRowCount() {
		// Id is not visible
		return labels.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int colIndex) {
		switch (colIndex) {
		case 0: // names
			return labels.get(rowIndex);
		case 1: // values
			return values.get(rowIndex);
		case 2: // unsure
			return unsure.get(rowIndex);
		default:
			return null;
		}
	}
	
	public void setData(Map<String, Object> result) {
		if (result == null) {
			for( int i=0; i<labels.size(); i++) {
				setSingleEntry(i, 0, true);
			}
		} else {
			for( int i=0; i<labels.size(); i++) {
//			String term = result.get(labels[i]).toString();
				String term = result.get(labels.get(i)).toString();
				Boolean b = term.charAt(0) == 'u';
				String dummy = term.substring(2, term.length()-1);
				Integer value = Integer.parseInt(dummy);
				
				setSingleEntry(i, value, b);
			}
		}
		fireTableDataChanged();
	}
	
	private void setSingleEntry(int rowIndex, Integer value, Boolean checked) {
		values.set(rowIndex, value);
		unsure.set(rowIndex, checked);
	}

	public String getSingleEntry(String key) {
		int i = labels.indexOf(key);
		
		String prefix = "s";
		if (unsure.get(i)) {
			prefix = "u";
		}
		return QueryUtils.buildTerm(prefix, values.get(i).toString());
	}
	
	@Override
	public String getColumnName(int col) {
		switch (col) {
		case 0:
			return "Eigenschaft";
		case 1:
			return "Bewertung";
		case 2:
			return "Unsicher?";
		default:
			return "XXX";
		}
    }

	@Override
    public Class<?> getColumnClass(int col) {
		switch (col) {
		case 0:
			return String.class;
		case 1:
			return Integer.class;
		case 2:
			return Boolean.class;
		default:
			return String.class;
		}
    }

	@Override
	public boolean isCellEditable(int row, int col) {
		if (col == 1 || col == 2) {
			return true;
		}
		return false;
	}
	
	@Override
	public void setValueAt(Object value, int row, int col) {
		if (col == 1) {
			values.set(row, (Integer) value);
		} else if (col == 2) {
			unsure.set(row, (Boolean) value);
		} else {
			System.err.println("try to write in unwriteable field");
		}
	}
}
