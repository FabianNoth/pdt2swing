package pdt.gui.datapanels.handler;

import java.io.File;

import org.cs3.prolog.connector.common.QueryUtils;
import org.cs3.prolog.connector.process.PrologProcess;
import org.cs3.prolog.connector.process.PrologProcessException;

import pdt.gui.PrologDataVisualizer;
import pdt.gui.data.IdListener;
import pdt.gui.data.PrologConnection;
import pdt.gui.datapanels.DataPanel;
import pdt.gui.utils.SimpleLogger;
import pdt.prolog.elements.PrologArgument;
import pdt.prolog.elements.PrologGoal;

public abstract class PrologDataHandler<PanelType extends DataPanel> implements IdListener {
	
	protected static final String ADD_FACT = "add_fact";
	protected static final String ADD_RELATION = "add_relation";
	protected static final String REMOVE_FACT = "remove_fact";
	protected static final String REMOVE_RELATION = "remove_relation";
	protected static final String UPDATE_FACT = "update_fact";
	// DISCUSSION: there is no updating for relations
	protected static final String PERSIST_DATA = "persist_data";
	// CHECK_FOR_VALUE = check if value might be added as relation
	protected static final String CHECK_FOR_VALUE = "check_for_existing_value";
	protected static final String AUTO_COMPLETION = "auto_completion";

	protected PrologProcess process;
	private PrologDataVisualizer visualizer;
	private PanelType panel;

	private String name;
	private String outputQuery;
	private boolean isMainPredicate;
	private String functor;
	private PrologArgument[] args;
	private String[] argNames;
	private Object[] argNamesWithBoundId;
	protected String currentId;
	protected File outputFile;

	public PrologDataHandler(String name) {
		this.name = name;
	}
	
	public PrologDataHandler(PrologConnection con, String name, File outputFile, boolean isMainPredicate, PrologGoal goal) {
		this.process = con.getProcess();
		this.outputFile = outputFile;
		this.name = name;
		this.args = goal.getArgs();
		this.argNames = goal.getArgNames();
		this.functor = goal.getFunctor();
		this.isMainPredicate = isMainPredicate;
		
		String prologFilename = QueryUtils.prologFileName(outputFile);
		String arity = Integer.toString(args.length);
		outputQuery = QueryUtils.bT(PERSIST_DATA, functor, arity, QueryUtils.quoteAtomIfNeeded(prologFilename));
		argNamesWithBoundId = new Object[argNames.length];
		for (int i=0; i<args.length; i++) {
			argNamesWithBoundId[i] = argNames[i];
		}
	}

	public PrologArgument[] getArgs() {
		return args;
	}
	
	public String[] getArgNames() {
		return argNames;
	}
	
	public String getQuery() {
		argNamesWithBoundId[0] = currentId;
		return QueryUtils.buildTerm(functor, argNamesWithBoundId);
	}
	
	public String getFunctor() {
		return functor;
	}
	
	@Override
	public void setId(String id) {
		currentId = id;
		if (id == null) {
			clearData();
		} else {
			showData();
		}
	}

	public abstract void showData();
	
	public void updateVisualizer() {
		updateVisualizer(null);
	}
	
	public void updateVisualizer(String id) {
		if (visualizer != null) {
			visualizer.changedDatabase(id);
		}
	}

	public void setVisualizer(PrologDataVisualizer visualizer) {
		this.visualizer = visualizer;
	}
	
	@Override
	public void persistFacts() {
		SimpleLogger.debug("outputQuery: " + outputQuery );
		try {
			process.queryOnce(outputQuery);
		} catch (PrologProcessException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isMainPredicate() {
		return isMainPredicate;
	}
	
	public String getName() {
		return name;
	}
	
	public int getArity() {
		return argNames.length;
	}
	
	/**
	 * Set the implementation of the data panel
	 */
	public void setEditPanel(PanelType panel) {
		this.panel = panel;
	}
	
	public PanelType getEditPanel() {
		return panel;
	}
	
	@Override
	public final boolean changed() {
		if (panel != null) {
			return panel.changed();
		}
		return false;
	}

	public void clearData() {
		if (panel != null) {
			panel.clearPanel();
		}
	}
	
	public PrologProcess getPrologProcess() {
		return process;
	}
}
