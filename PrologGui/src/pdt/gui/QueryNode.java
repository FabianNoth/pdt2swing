package pdt.gui;

import javax.swing.tree.DefaultMutableTreeNode;

import pdt.gui.data.PrologFilter;
import pdt.gui.data.PrologFilterUtils;
import pdt.gui.data.PrologGuiBundle;

public class QueryNode extends DefaultMutableTreeNode {

	private static final long serialVersionUID = 1L;
	
	private String name;
	private PrologGuiBundle bundle;
	private PrologFilter filter;

	public QueryNode(String name, PrologGuiBundle bundle) {
		this(name, bundle, PrologFilterUtils.createFullFilter());
	}
	
	public QueryNode(String name, PrologGuiBundle bundle, PrologFilter filter) {
		super(name);
		this.name = name;
		this.bundle = bundle;
		this.filter = filter;
	}

	public String getName() {
		return name;
	}

	public PrologGuiBundle getBundle() {
		return bundle;
	}
	
	public PrologFilter getFilter() {
		return filter;
	}
	
}
