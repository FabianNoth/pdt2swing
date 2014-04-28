package pdt.gui;

import javax.swing.tree.DefaultMutableTreeNode;

import pdt.gui.data.PrologGuiBundle;

public class QueryNode extends DefaultMutableTreeNode {

	private static final long serialVersionUID = 1L;
	
	private String name;
	private PrologGuiBundle bundle;

	public QueryNode(String name, PrologGuiBundle bundle) {
		super(name);
		this.name = name;
		this.bundle = bundle;
	}

	public String getName() {
		return name;
	}

	public PrologGuiBundle getBundle() {
		return bundle;
	}
	
}
