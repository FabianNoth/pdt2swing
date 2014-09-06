package pdt.gui.datapanels.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.cs3.prolog.connector.common.QueryUtils;
import org.cs3.prolog.connector.process.PrologProcessException;

import pdt.gui.data.PrologConnection;
import pdt.gui.datapanels.RelationPanel;
import pdt.gui.utils.SimpleLogger;
import pdt.prolog.elements.PrologGoal;

public class PrologRelationHandler extends PrologDataHandler<RelationPanel> {

	private final List<String> autoCompletionList = new ArrayList<String>();
	private String functor;
	
	public PrologRelationHandler(PrologConnection con, String name, PrologGoal goal) {
		this(con, name, goal, false);
	}
	
	public PrologRelationHandler(PrologConnection con, String name, PrologGoal goal, boolean autoCompletion) {
		super(con, name, false, goal);
		this.functor = goal.getFunctor();
		if (autoCompletion) {
			updateAutoCompletion();
		}
	}

	public void updateAutoCompletion() {
		autoCompletionList.clear();
		try {
			Map<String, Object> results = process.queryOnce(QueryUtils.bT(AUTO_COMPLETION, functor, "Result"));
			Object o = results.get("Result");
			if (o instanceof List<?>) {
				List<?> dummyList = (List<?>) o;
				for (Object entry : dummyList) {
					autoCompletionList.add(entry.toString());
				}
			}
			SimpleLogger.debug(autoCompletionList);
		} catch (PrologProcessException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isAutoCompletion() {
		return autoCompletionList != null;
	}

	@Override
	public void showData() {
		try {
			List<Map<String, Object>> results = process.queryAll(getQuery());
			if (getEditPanel() != null) {
				List<String> entries = new ArrayList<String>();
				for (Map<String, Object> m : results) {
					String entry = m.get(getArgNames()[1]).toString();
					entries.add(entry);
				}
				
				getEditPanel().setData(entries);
			}
		} catch (PrologProcessException e) {
			e.printStackTrace();
		}
	}
	
	public void addValue(String newValue) {
		if (currentId == null) {
			return;
		}
		
		String assertValue = QueryUtils.quoteAtomIfNeeded(newValue);
		
		try {
			boolean updateAutoCompletionFlag = false;
			if (isAutoCompletion()) {
				Map<String, Object> check = process.queryOnce(QueryUtils.bT(CHECK_FOR_VALUE, getFunctor(), assertValue));
				if (check == null) {
					int answer = JOptionPane.showConfirmDialog(getEditPanel(), "Eintrag mit dem Wert \"" + newValue + "\" existiert nicht. Soll er hinzugefügt werden?", "Neuen Eintrag hinzufügen", JOptionPane.YES_NO_OPTION);
					if (answer == JOptionPane.NO_OPTION) {
						return;
					} else if (answer == JOptionPane.YES_OPTION) {
						updateAutoCompletionFlag = true;
					}
				}
			}

			String assertQuery = QueryUtils.bT(getFunctor(), currentId, assertValue);
			SimpleLogger.debug(assertQuery);
			try {
				process.queryOnce(QueryUtils.bT(ADD_RELATION, assertQuery));
			} catch (PrologProcessException e) {
				e.printStackTrace();
			}
			
			if (updateAutoCompletionFlag) {
				updateAutoCompletion();
			}
			
			// update table
			updateVisualizer();
			
		} catch (PrologProcessException e1) {
			e1.printStackTrace();
		}
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

	public List<String> getAutoCompletionList() {
		return autoCompletionList;
	}
	
}
