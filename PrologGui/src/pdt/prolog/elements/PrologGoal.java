package pdt.prolog.elements;

import java.util.List;

import org.cs3.prolog.connector.common.QueryUtils;
import org.cs3.prolog.connector.cterm.CAtom;
import org.cs3.prolog.connector.cterm.CCompound;
import org.cs3.prolog.connector.cterm.CInteger;
import org.cs3.prolog.connector.cterm.CTerm;

import pdt.gui.Queries;

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
	
	public PrologGoal(String functor, List<CCompound> list, Queries queries) {
		this.functor = functor;
		this.args = new PrologArgument[list.size()];
		argNames = new String[args.length];
		for (int i=0; i<args.length; i++) {
			
			CCompound compound = list.get(i);
			String name = compound.getArgument(0).getFunctorValue();
			
			CTerm typeArg = compound.getArgument(1);
			
			if (typeArg instanceof CAtom) {
				String type = typeArg.getFunctorValue();
				switch (type) {
				case "id":
					args[i] = PrologArgument.createId();
					break;
				case "atom":
					args[i] = PrologArgument.createAtom(name);
					break;
				case "number":
					args[i] = PrologArgument.createNumber(name);
					break;
				default:
					args[i] = PrologArgument.createReference(name, type);
					break;
				}
			} else if (typeArg instanceof CCompound) {
				CCompound compType = (CCompound) typeArg;
				switch (compType.getFunctorValue()) {
				case "atom":
					String[] values = queries.fixedAtomValues(compType.getArgument(0).getFunctorValue());
					PrologArgument.createFixedAtom(name, values );
					break;
				case "number":
					int limitMin = ((CInteger) compType.getArgument(0)).getIntValue();
					int limitMax = ((CInteger) compType.getArgument(1)).getIntValue();
					PrologArgument.createLimitedNumber(name, limitMin, limitMax);
					break;
				case "unsure_number":
					int uLimitMin = ((CInteger) compType.getArgument(0)).getIntValue();
					int uLimitMax = ((CInteger) compType.getArgument(1)).getIntValue();
					PrologArgument.createLimitedNumber(name, uLimitMin, uLimitMax, true);
					break;
				}
			}
			argNames[i] = name;
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
