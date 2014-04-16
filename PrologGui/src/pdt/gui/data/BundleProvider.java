package pdt.gui.data;

import java.util.HashSet;
import java.util.Set;

import pdt.gui.QueryNode;

public abstract class BundleProvider {

	private final Set<IdListener> listeners = new HashSet<IdListener>();
	
	/**
	 * Create the complete node hierarchy and return the root node
	 * @return the root element for all possible queries
	 */
	public abstract QueryNode createRoot();
	public abstract PrologGuiBundle getDefault();
	
	public void addListener(PrologGuiBundle bundle) {
		for (PrologFactHandler f : bundle.getFactHandlers()) {
			addListener(f);
		}
	}
	
	public void addListener(IdListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(IdListener listener) {
		listeners.remove(listener);
	}
	
	public void clearListeners() {
		listeners.clear();
	}
	
	public void persistFacts() {
		for(IdListener l : listeners) {
			l.persistFacts();
		}
	}
	
}
