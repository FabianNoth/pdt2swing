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
		String outputPredicateFunctor = "output_to_file";
		if (isMainPredicate) {
			outputPredicateFunctor = "output_to_file_plus_id";
		}
		outputQuery = QueryUtils.bT(outputPredicateFunctor, Util.quoteAtomIfNeeded(prologFilename), functor + "/" + (args.length + 1));
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
