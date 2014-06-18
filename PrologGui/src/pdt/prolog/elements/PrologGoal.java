package pdt.prolog.elements;

import org.cs3.prolog.connector.common.QueryUtils;

public class PrologGoal {

	private String functor;
	protected PrologArgument[] args;
	protected String[] argNames;

	public PrologGoal(String functor, PrologArgument... args) {
		this.functor = functor;
		this.args = args;
		argNames = new String[args.length];
		for (int i=0; i<args.length; i++) {
			argNames[i] = args[i].getName();
		}
	}
	
	public PrologGoal(String functor, String... args) {
		this.functor = functor;
		this.argNames = args;
		this.args = new PrologArgument[args.length];
		for (int i=0; i<args.length; i++) {
			this.args[i] = new PrologArgument(args[i], PrologArgument.ATOM);
		}
	}
	
	public String getFunctor() {
		return functor;
	}

	public PrologArgument[] getArgs() {
		return args;
	}
	
	public String[] getArgNames() {
		return argNames;
	}

	public String getQuery() {
		return QueryUtils.bT(functor, (Object[]) argNames);
	}
	
}
