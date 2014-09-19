package prlg.demo;

import org.cs3.prolog.connector.process.PrologProcess;

import pdt.gui.QueryNode;
import pdt.gui.data.BundleProvider;
import pdt.gui.data.PrologAdapter;
import pdt.gui.data.PrologGuiBundle;

public class DemoBundleProvider extends BundleProvider {

	private PrologProcess process;
	private PrologGuiBundle bundle1;
	private PrologGuiBundle bundle2;

	public DemoBundleProvider(PrologAdapter con, PrologGuiBundle bundle1, PrologGuiBundle bundle2) {
		this.process = con.getProcess();
		this.bundle1 = bundle1;
		this.bundle2 = bundle2;
		addListener(bundle1);
		addListener(bundle2);
	}
	
	@Override
	public QueryNode createRoot() {
		QueryNode root = new QueryNode("Root", bundle1);

		QueryNode node1 = new QueryNode("Serien", bundle1);
		QueryNode node2 = new QueryNode("Kategorien", bundle2);
		root.add(node1);
		root.add(node2);
		return root;
	}

	@Override
	public PrologGuiBundle getDefault() {
		return bundle1;
	}

}
