package pdt.gui.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import org.cs3.prolog.connector.process.PrologProcess;
import org.cs3.prolog.connector.process.PrologProcessException;

import pdt.gui.utils.SimpleLogger;
import pdt.prolog.elements.PrologArgument;
import pdt.prolog.elements.PrologGoal;

public class PrologTableData extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	
	private PrologGoal goal;
	
	private String[] variables;

	private List<Map<String, Object>> data;
	private PrologProcess process;

	public PrologTableData(PrologAdapter con, PrologGoal goal) {
		this.goal = goal;
		
		process = con.getProcess();
		
		updateResultData();
		
		ArrayList<String> variableList = new ArrayList<>();
		for (PrologArgument arg : goal.getArgs()) {
			char firstLetter = arg.getName().charAt(0);
			if (Character.isUpperCase(firstLetter) && !variableList.contains(arg)) {
				variableList.add(arg.getName());
			}
		}
		
		variables = new String[variableList.size()];
		
		for (int i=0; i<variableList.size(); i++) {
			variables[i] = variableList.get(i);
		}
	}
	
	public PrologProcess getProcess() {
		return process;
	}

	public void updateResultData() {
		try {
			SimpleLogger.debug("update result data with query: " + goal.getQuery());
			data = process.queryAll(goal.getQuery());
			System.out.println(data);
		} catch (PrologProcessException e) {
			e.printStackTrace();
		}
	}
	

	@Override
	public int getColumnCount() {
		return variables.length;
	}

	@Override
	public int getRowCount() {
		return data.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int colIndex) {
		Map<String, Object> map = data.get(rowIndex);
		if (map != null) {
			return map.get(variables[colIndex]);
		}
		return null;
	}
	
	@Override
	public String getColumnName(int col) {
        return variables[col];
    }

	@Override
    public Class<?> getColumnClass(int c) {
		return String.class;
    }

	@Override
	public boolean isCellEditable(int row, int col) {
		return false;
	}

	public int getPositionOfId(String id) {
		// TODO: might be slow, use indexing
		if (id != null) {
			int i=0;
			for(Map<String, Object> result : data) {
				if (id.equals(result.get("ID").toString())) {
					return i;
				}
				i++;
			}
		}
		return -1;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PrologTableData) {
			PrologTableData compData = (PrologTableData) obj;
			return (getGoal().equals(compData.getGoal()));
		}
		return false;
	}

	public PrologGoal getGoal() {
		return goal;
	}

	public void setGoal(PrologGoal goal) {
		this.goal = goal;
	}

}
