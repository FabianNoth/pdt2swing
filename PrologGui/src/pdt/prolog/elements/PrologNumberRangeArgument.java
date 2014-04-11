package pdt.prolog.elements;

public class PrologNumberRangeArgument extends PrologArgument {
	
	private int limitMin;
	private int limitMax;

	public PrologNumberRangeArgument(String name, int limitMin, int limitMax) {
		super(name, NUMBER_LIMIT);
		this.limitMin = limitMin;
		this.limitMax = limitMax;
	}

	public int getLimitMin() {
		return limitMin;
	}
	
	public int getLimitMax() {
		return limitMax;
	}

}
