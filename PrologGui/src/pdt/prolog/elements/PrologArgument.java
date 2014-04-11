package pdt.prolog.elements;

public class PrologArgument {

	public static final int ID = 0;
	public static final int ATOM = 1;
	public static final int NUMBER = 2;
	public static final int BOOLEAN = 3;
	public static final int NUMBER_LIMIT = 4;
	
	private String name;
	private int type;

	// Factory methods
	
	public static PrologArgument createId() {
		return new PrologArgument("ID", ID);
	}
	
	public static PrologArgument createAtom(String name) {
		return new PrologArgument(name, ATOM);
	}

	public static PrologArgument createNumber(String name) {
		return new PrologArgument(name, NUMBER);
	}
	
	public static PrologArgument createBoolean(String name) {
		return new PrologArgument(name, BOOLEAN);
	}
	
	public static PrologArgument createLimitedNumber(String name, int limitMin, int limitMax) {
		return new PrologNumberRangeArgument(name, limitMin, limitMax);
	}
	
	// constructor
	
	protected PrologArgument(String name, int type) {
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public int getType() {
		return type;
	}
	
}
