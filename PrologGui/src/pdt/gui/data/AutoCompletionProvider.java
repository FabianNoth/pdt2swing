package pdt.gui.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AutoCompletionProvider {

	private final Map<String, List<String>> completions = new HashMap<>();
	private PrologAdapter adapter;
		
	public AutoCompletionProvider(PrologAdapter adapter) {
		this.adapter = adapter;
	}
	
	public List<String> getCompletionList(String functor) {
		String functorLc = functor.toLowerCase();
		if (completions.get(functorLc) == null) {
			add(functorLc);
		}
		return completions.get(functorLc);
	}
	
	public void update(String functor) {
		String functorLc = functor.toLowerCase();
		if (completions.get(functorLc) != null) {
			add(functorLc);
		}
	}
	
	private void add(String functor) {
		completions.put(functor, adapter.getAutoCompletions(functor));
	}
	
}
