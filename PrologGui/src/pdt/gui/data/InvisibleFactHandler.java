package pdt.gui.data;

import java.io.File;

import pdt.prolog.elements.PrologGoal;

public class InvisibleFactHandler extends PrologFactHandler {
	
	public InvisibleFactHandler(PrologConnection con, String name, File outputFile, PrologGoal goal) {
		this(con, name, outputFile, false, goal);
	}
	
	public InvisibleFactHandler(PrologConnection con, String name, File outputFile, boolean isMainPredicate, PrologGoal goal) {
		super(con, name, outputFile, isMainPredicate, goal);
	}

	@Override
	public void showData() {

	}

}
