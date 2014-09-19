package pdt.gui.datapanels.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.cs3.prolog.connector.common.QueryUtils;
import org.cs3.prolog.connector.process.PrologProcessException;

import pdt.gui.data.PrologAdapter;
import pdt.gui.datapanels.RelationPanel;
import pdt.gui.utils.PrologUtils;
import pdt.gui.utils.SimpleLogger;
import pdt.prolog.elements.PrologGoal;
import pdt.prolog.elements.PrologTransactionResult;

public class PrologRelationHandler extends PrologDataHandler<RelationPanel> {

	public PrologRelationHandler(PrologAdapter con, String name, PrologGoal goal) {
		this(con, name, goal, false);
	}
	
	public PrologRelationHandler(PrologAdapter con, String name, PrologGoal goal, boolean autoCompletion) {
		super(con, name, false, goal);
	}

	@Override
	public void showData() {
		try {
			List<Map<String, Object>> results = process.queryAll(getQuery());
			if (getEditPanel() != null) {
				List<String> entries = new ArrayList<String>();
				for (Map<String, Object> m : results) {
					String entry = m.get(getArgNames()[1]).toString();
					entries.add(getDisplayString(entry));
				}
				
				getEditPanel().setData(entries);
			}
		} catch (PrologProcessException e) {
			e.printStackTrace();
		}
	}
	
	public boolean addValue(String newValue) {
		if (currentId == null) {
			return false;
		}

		String assertValue = PrologUtils.quoteIfNecessary(getDataString(newValue));
		String assertQuery = QueryUtils.bT(getFunctor(), currentId, assertValue);
		SimpleLogger.debug("add relation: " + assertQuery);
		
		PrologTransactionResult result = executeTransaction(ADD_FACT, assertQuery);
		if (result == null) {
			// query failed
			return false;
		}
		
		if (result.isError()) {
			JOptionPane.showMessageDialog(getEditPanel(), result.getDialogMessage(),  "Fehler beim Hinzufügen", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		
//			boolean updateAutoCompletionFlag = false;
//			if (isAutoCompletion()) {
//				Map<String, Object> check = process.queryOnce(QueryUtils.bT(CHECK_FOR_VALUE, getFunctor(), assertValue));
//				if (check == null) {
//					int answer = JOptionPane.showConfirmDialog(getEditPanel(), "Eintrag mit dem Wert \"" + newValue + "\" existiert nicht. Soll er hinzugefügt werden?", "Neuen Eintrag hinzufügen", JOptionPane.YES_NO_OPTION);
//					if (answer == JOptionPane.NO_OPTION) {
//						return;
//					} else if (answer == JOptionPane.YES_OPTION) {
//						updateAutoCompletionFlag = true;
//					}
//				}
//			}
//
//			try {
//				process.queryOnce(QueryUtils.bT(ADD_RELATION, assertQuery));
//			} catch (PrologProcessException e) {
//				e.printStackTrace();
//			}
//			
			// update table
			updateVisualizer();
			return true;
			
	}
	

	public void removeValue(String value) {
		if (currentId == null) {
			return;
		}
		
		String retractQuery = QueryUtils.bT(getFunctor(), currentId, QueryUtils.quoteAtomIfNeeded(value));
		SimpleLogger.debug("retractQuery: " + retractQuery);
		try {
			process.queryOnce(QueryUtils.bT(REMOVE_RELATION, retractQuery));
		} catch (PrologProcessException e) {
			e.printStackTrace();
		}
		
		// update table
		updateVisualizer();
	}

	public String getDisplayString(String value) {
		String key = getArgNames()[getArity()-1];
		return getDisplayString(key, value);
	}
	
	private String getDataString(String newValue) {
		String key = getArgNames()[getArity()-1];
		return getDataString(key, newValue);
	}

}
