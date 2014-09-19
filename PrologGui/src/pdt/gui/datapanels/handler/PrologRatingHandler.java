package pdt.gui.datapanels.handler;

import java.util.Map;

import javax.swing.JOptionPane;

import org.cs3.prolog.connector.common.QueryUtils;
import org.cs3.prolog.connector.process.PrologProcessException;

import pdt.gui.data.PrologAdapter;
import pdt.gui.datapanels.RatingPanel;
import pdt.gui.datapanels.RatingTableModel;
import pdt.gui.utils.SimpleLogger;
import pdt.prolog.elements.PrologGoal;
import pdt.prolog.elements.PrologTransactionResult;

public class PrologRatingHandler extends PrologDataHandler<RatingPanel> {

	private Map<String, Object> result;

	public PrologRatingHandler(PrologAdapter con, String name, PrologGoal goal) {
		super(con, name, false, goal);
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
	
	public boolean updateFromPanel(RatingTableModel model) {
		if (currentId == null) {
			return false;
		}
		
		// get goal for assertion, use current id
		String goal = getGoalWithData(model, currentId);

		PrologTransactionResult result = executeTransaction(UPDATE_FACT, goal);
		
		if (result == null) {
			// query failed
			return false;
		}
		
		if (result.isError()) {
			JOptionPane.showMessageDialog(getEditPanel(), result.getDialogMessage(),  "Fehler beim Update", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		updateVisualizer();
		return true;
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
