package pdt.gui.data;

import java.io.File;
import java.util.Map;

import org.cs3.prolog.common.QueryUtils;
import org.cs3.prolog.pif.PrologInterfaceException;

import pdt.gui.RatingPanel;
import pdt.gui.RatingTableModel;
import pdt.gui.utils.SimpleLogger;
import pdt.prolog.elements.PrologGoal;

public class PrologRatingHandler extends PrologDataHandler {

	private RatingPanel editPanel;
	private Map<String, Object> result;
	
	public PrologRatingHandler(PrologConnection con, String name, File outputFile, PrologGoal goal) {
		super(con, name, outputFile, false, goal);
	}

	public void setEditPanel(RatingPanel editPanel) {
		this.editPanel = editPanel;
	}

	@Override
	public void showData() {
		SimpleLogger.debug(getQuery());
		try {
			result = pif.queryOnce(getQuery());
			result.put("ID", currentId);
			if (editPanel != null) {
				editPanel.setData(result);
			}
		} catch (PrologInterfaceException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void clearData() {
		editPanel.clearPanel();
	}
	
	public void updateFromPanel(RatingTableModel model) {
		if (currentId == null) {
			return;
		}
		
		// build assert query
//		String retractQuery = getRetractQuery();
		// get goal for assertion, use current id
		String goal = getGoalWithData(model, currentId);
		
		try {
			pif.queryOnce(QueryUtils.bT(UPDATE_FACT, goal));
//			pif.queryOnce("retractall(" + retractQuery + ")");
//			pif.queryOnce("assert(" + assertQuery + ")");
		} catch (PrologInterfaceException e) {
			e.printStackTrace();
		}
		
		updateVisualizer();
	}
	
	private String getGoalWithData(RatingTableModel model, String id) {
		String[] argNames = getArgNames();
		String[] assertArgs = new String[argNames.length];
		assertArgs[0] = id;

		for (int i=1; i<argNames.length; i++) {
			assertArgs[i] = model.getSingleEntry(argNames[i]);
		}

		return QueryUtils.bT(getFunctor(), (Object[]) assertArgs);
	}
	
}
