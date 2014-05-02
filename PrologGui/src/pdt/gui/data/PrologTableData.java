package pdt.gui.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import org.cs3.prolog.pif.PrologInterface;
import org.cs3.prolog.pif.PrologInterfaceException;

import pdt.prolog.elements.PrologArgument;
import pdt.prolog.elements.PrologGoal;

public class PrologTableData extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	
	private PrologGoal goal;
	
	private String[] variables;

	private PrologFilter filter;
	private List<Map<String, Object>> fullData;
	private List<Map<String, Object>> filteredData;
//	private List<Map<String, Object>> data;
	private PrologInterface pif;

	private String query;


	public PrologTableData(PrologConnection con, PrologGoal goal) {
		this(con, goal, new PrologFilter() {
			@Override public boolean accept(Map<String, Object> entry) {
				return true;
			}
		});
	}
	
	public PrologTableData(PrologConnection con, PrologGoal goal, PrologFilter filter) {
		this.filter = filter;
		this.goal = goal;
		
		pif = con.getPif();
		
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
	
	public void setFilter(PrologFilter filter) {
		this.filter = filter;
	}
	
	public String getQuery() {
		return query;
	}
	
	public PrologInterface getPif() {
		return pif;
	}

	public void updateResultData() {
//		String query = QueryUtils.bT(goal.getFunctor(), (Object[]) args);
		try {
			fullData = pif.queryAll(goal.getQuery());
			filteredData = filter.getFilteredList(fullData);
		} catch (PrologInterfaceException e) {
			e.printStackTrace();
		}
	}
	

	@Override
	public int getColumnCount() {
		return variables.length;
	}

	@Override
	public int getRowCount() {
		return filteredData.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int colIndex) {
		Map<String, Object> map = filteredData.get(rowIndex);
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
			for(Map<String, Object> result : filteredData) {
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
			return (getQuery().equals(compData.getQuery()));
		}
		return false;
	}
	
}
