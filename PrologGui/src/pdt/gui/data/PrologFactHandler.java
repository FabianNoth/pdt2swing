package pdt.gui.data;

import java.io.File;

import org.cs3.prolog.common.QueryUtils;
import org.cs3.prolog.common.Util;
import org.cs3.prolog.pif.PrologInterface;
import org.cs3.prolog.pif.PrologInterfaceException;

import pdt.gui.PrologDataVisualizer;
import pdt.prolog.elements.PrologArgument;
import pdt.prolog.elements.PrologGoal;

public abstract class PrologFactHandler implements IdListener {
	
	protected static final String ADD_FACT = "add_fact";
	protected static final String ADD_RELATION = "add_relation";
	protected static final String REMOVE_FACT = "remove_fact";
	protected static final String REMOVE_RELATION = "remove_relation";
	protected static final String UPDATE_FACT = "update_fact";
	// DISCUSSION: there is no updating for relations
	protected static final String PERSIST_DATA = "persist_data";
	// CHECK_FOR_VALUE = check if value might be added as relation
	protected static final String CHECK_FOR_VALUE = "check_for_existing_value";

	protected PrologInterface pif;
	private PrologDataVisualizer visualizer;

	private String name;
	private String outputQuery;
	private boolean isMainPredicate;
	private String functor;
	private PrologArgument[] args;
	private String[] argNames;
	protected String[] argsWithId;
	
	public PrologFactHandler(PrologConnection con, String name, File outputFile, boolean isMainPredicate, PrologGoal goal) {
		this.pif = con.getPif();
		this.name = name;
		this.args = goal.getArgs();
		this.argNames = goal.getArgNames();
		this.functor = goal.getFunctor();
		this.isMainPredicate = isMainPredicate;
		
		String prologFilename = Util.prologFileName(outputFile);
		String arity = Integer.toString(args.length + 1);
		outputQuery = QueryUtils.bT(PERSIST_DATA, functor, arity, Util.quoteAtomIfNeeded(prologFilename));
		argsWithId = new String[args.length + 1];
		argsWithId[0] = "ID";
		for (int i=0; i<args.length; i++) {
			argsWithId[i+1] = argNames[i];
		}
	}

	public PrologArgument[] getArgs() {
		return args;
	}
	
	public String[] getArgNames() {
		return argNames;
	}
	
	public String getFunctor() {
		return functor;
	}
	
	@Override
	public void setId(String id) {
		argsWithId[0] = id;
		showData();
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
		System.out.println(outputQuery);
		try {
			pif.queryOnce(outputQuery);
		} catch (PrologInterfaceException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isMainPredicate() {
		return isMainPredicate;
	}
	
	public String getName() {
		return name;
	}

}
