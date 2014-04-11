package pdt.gui;

import javax.swing.tree.DefaultMutableTreeNode;

import pdt.prolog.elements.PrologGoal;

public class QueryNode extends DefaultMutableTreeNode {

	private static final long serialVersionUID = 1L;
	
	private String name;
	private PrologGoal goal;

	public QueryNode(String name, PrologGoal goal) {
		super(name);
		this.name = name;
		this.goal = goal;
	}

	public String getName() {
		return name;
	}

	public PrologGoal getGoal() {
		return goal;
	}
	
	
}
