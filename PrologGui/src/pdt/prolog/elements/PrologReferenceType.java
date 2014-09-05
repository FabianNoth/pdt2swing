package pdt.prolog.elements;

public class PrologReferenceType extends PrologArgument {
	
	private String refType;

	public PrologReferenceType(String name, String refType) {
		super(name, REFERENCE);
		this.refType = refType;
	}

	public String getRefType() {
		return refType;
	}
	
}
