package pdt.prolog.elements;

import java.util.List;
import java.util.Map;

import org.cs3.prolog.connector.common.QueryUtils;
import org.cs3.prolog.connector.cterm.CCompound;

import pdt.gui.data.PrologAdapter;

public class PrologDisplayGoal extends PrologGoal {

	
	String filter = "Filter";
	
	public PrologDisplayGoal(String factName, List<CCompound> displayArgs, PrologAdapter prolog) {
		super(factName, displayArgs, prolog);
	}
	
	public PrologDisplayGoal(String factName, List<CCompound> displayArgs, PrologAdapter prolog, String filter) {
		super(factName, displayArgs, prolog);
		this.filter = filter;
	}

	public PrologDisplayGoal(PrologGoal goal, String filter) {
		super(goal.getFunctor(), goal.getArgs());
		this.filter = filter;
	}

	public String getQuery() {
		String term = QueryUtils.bT(getFunctor(), (Object[]) argNames);
		return QueryUtils.bT("db_controller::display", term, filter);
	}

	public boolean isTrue(PrologAdapter prolog) {
		String query = QueryUtils.bT("once", getQuery());
		Map<String, Object> result = prolog.queryOnce(query);
		return result != null;
	}
	
}
