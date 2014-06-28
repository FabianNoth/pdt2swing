package pdt.gui.datapanels.handler;

import java.io.File;
import java.util.Map;

import org.cs3.prolog.connector.common.QueryUtils;
import org.cs3.prolog.connector.process.PrologProcessException;

import pdt.gui.data.PrologConnection;
import pdt.gui.datapanels.RatingPanel;
import pdt.gui.datapanels.RatingTableModel;
import pdt.gui.utils.SimpleLogger;
import pdt.prolog.elements.PrologGoal;

public class PrologRatingHandler extends PrologDataHandler<RatingPanel> {

	private Map<String, Object> result;
	
	public PrologRatingHandler(PrologConnection con, String name, File outputFile, PrologGoal goal) {
		super(con, name, outputFile, false, goal);
	}

	@Override
	public void showData() {
		SimpleLogger.debug(getQuery());
		try {
			result = process.queryOnce(getQuery());
			result.put("ID", currentId);
			if (getEditPanel() != null) {
				getEditPanel().setData(result);
			}
		} catch (PrologProcessException e) {
			e.printStackTrace();
		}
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
			process.queryOnce(QueryUtils.bT(UPDATE_FACT, goal));
//			process.queryOnce("retractall(" + retractQuery + ")");
//			process.queryOnce("assert(" + assertQuery + ")");
		} catch (PrologProcessException e) {
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
