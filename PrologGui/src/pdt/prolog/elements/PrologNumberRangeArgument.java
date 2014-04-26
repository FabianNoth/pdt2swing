package pdt.prolog.elements;

public class PrologNumberRangeArgument extends PrologArgument {
	
	private int limitMin;
	private int limitMax;
	private boolean canBeUnsure;
	
	public PrologNumberRangeArgument(String name, int limitMin, int limitMax) {
		this(name, limitMin, limitMax, false);
	}
	
	public PrologNumberRangeArgument(String name, int limitMin, int limitMax, boolean canBeUnsure) {
		super(name, NUMBER_LIMIT);
		this.limitMin = limitMin;
		this.limitMax = limitMax;
		this.canBeUnsure = canBeUnsure;
	}

	public int getLimitMin() {
		return limitMin;
	}
	
	public int getLimitMax() {
		return limitMax;
	}
	
	public boolean canBeUnsure() {
		return canBeUnsure;
	}

}
