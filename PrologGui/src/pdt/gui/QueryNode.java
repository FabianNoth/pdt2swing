package pdt.gui;

import javax.swing.tree.DefaultMutableTreeNode;

import pdt.gui.data.PrologGuiBundle;
import pdt.prolog.elements.PrologGoal;

public class QueryNode extends DefaultMutableTreeNode {

	private static final long serialVersionUID = 1L;
	
	private String name;
	private PrologGuiBundle bundle;
//	private PrologGoal goal;

	public QueryNode(String name, PrologGuiBundle bundle) {
		super(name);
		this.name = name;
		this.bundle = bundle;
//		this.goal = goal;
	}

	public String getName() {
		return name;
	}

	public PrologGuiBundle getBundle() {
		return bundle;
	}
//	public PrologGoal getGoal() {
//		return goal;
//	}
//	
	
}
