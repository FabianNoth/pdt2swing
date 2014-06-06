package pdt.prolog.elements;

import java.util.Arrays;

public class PrologFilteredGoal extends PrologGoal {

	public PrologFilteredGoal(PrologGoal goal, String filter) {
		super(goal.getFunctor() + "_filtered", goal.getArgs());
		
		int length = argNames.length;
		argNames = Arrays.copyOf(argNames, length+1);
		args = Arrays.copyOf(args, args.length+1);
		argNames[length] = filter;
		args[length] = PrologArgument.createAtom("Filter");
	}
	
}
