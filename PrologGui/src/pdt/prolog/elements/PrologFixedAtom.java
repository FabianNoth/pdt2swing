package pdt.prolog.elements;

public class PrologFixedAtom extends PrologArgument {
	
	private String[] values;

	public PrologFixedAtom(String name, String[] values) {
		super(name, ATOM_FIXED);
		this.values = values;
	}

	public String[] getValues() {
		return values;
	}
	
}
