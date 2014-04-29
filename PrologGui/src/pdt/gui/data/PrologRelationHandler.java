package pdt.gui.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.cs3.prolog.common.QueryUtils;
import org.cs3.prolog.common.Util;
import org.cs3.prolog.pif.PrologInterfaceException;

import pdt.gui.RelationPanel;
import pdt.gui.utils.SimpleLogger;
import pdt.prolog.elements.PrologGoal;

public class PrologRelationHandler extends PrologDataHandler {

	private RelationPanel editPanel;
	private List<String> autoCompletionList;
	private String functor;
	
	public PrologRelationHandler(PrologConnection con, String name, File outputFile, PrologGoal goal) {
		this(con, name, outputFile, goal, false);
	}
	
	public PrologRelationHandler(PrologConnection con, String name, File outputFile, PrologGoal goal, boolean autoCompletion) {
		super(con, name, outputFile, false, goal);
		this.functor = goal.getFunctor();
		if (autoCompletion) {
			updateAutoCompletion();
		}
	}

	@SuppressWarnings("unchecked")
	private void updateAutoCompletion() {
		autoCompletionList = new ArrayList<String>();
		try {
			Map<String, Object> results = pif.queryOnce(QueryUtils.bT(AUTO_COMPLETION, functor, "Result"));
			autoCompletionList = (List<String>) results.get("Result");
			SimpleLogger.println(autoCompletionList);
		} catch (PrologInterfaceException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isAutoCompletion() {
		return autoCompletionList != null;
	}

	@Override
	public void showData() {
		try {
			List<Map<String, Object>> results = pif.queryAll(getQuery());
			if (editPanel != null) {
				List<String> entries = new ArrayList<String>();
				for (Map<String, Object> m : results) {
					String entry = m.get(getArgNames()[1]).toString();
					entries.add(entry);
				}
				
				editPanel.setData(entries);
			}
		} catch (PrologInterfaceException e) {
			e.printStackTrace();
		}
	}

	public void addValue(String newValue) {
		String assertValue = Util.quoteAtomIfNeeded(newValue);
		
		try {
			boolean updateAutoCompletionFlag = false;
			if (isAutoCompletion()) {
				Map<String, Object> check = pif.queryOnce(QueryUtils.bT(CHECK_FOR_VALUE, getFunctor(), assertValue));
				if (check == null) {
					int answer = JOptionPane.showConfirmDialog(editPanel, "Eintrag mit dem Wert \"" + newValue + "\" existiert nicht. Soll er hinzugefügt werden?", "Neuen Eintrag hinzufügen", JOptionPane.YES_NO_OPTION);
					if (answer == JOptionPane.NO_OPTION) {
						return;
					} else if (answer == JOptionPane.YES_OPTION) {
						updateAutoCompletionFlag = true;
					}
				}
			}

			String assertQuery = QueryUtils.bT(getFunctor(), currentId, assertValue);
			SimpleLogger.println(assertQuery);
			try {
				pif.queryOnce(QueryUtils.bT(ADD_RELATION, assertQuery));
			} catch (PrologInterfaceException e) {
				e.printStackTrace();
			}
			

			if (updateAutoCompletionFlag) {
				updateAutoCompletion();
			}
			
			// update table
			updateVisualizer();
			
		} catch (PrologInterfaceException e1) {
			e1.printStackTrace();
		}
	}
	
	public void setEditPanel(RelationPanel editPanel) {
		this.editPanel = editPanel;
	}

	public void removeValue(String value) {
		String retractQuery = QueryUtils.bT(getFunctor(), currentId, Util.quoteAtomIfNeeded(value));
		SimpleLogger.println("retractQuery: " + retractQuery);
		try {
			pif.queryOnce(QueryUtils.bT(REMOVE_RELATION, retractQuery));
		} catch (PrologInterfaceException e) {
			e.printStackTrace();
		}
		
		// update table
		updateVisualizer();
	}

	public List<String> getAutoCompletionList() {
		return autoCompletionList;
	}

	
}
