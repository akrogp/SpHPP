package org.sphpp.workflow.data;

public enum Degeneracy {
	UNIQUE(0),
	DISCRIMINATING(1),
	NON_DISCRIMINATING(2);

	private Degeneracy( int value ) {
		this.value = value;
	}
	
	public int value() {
		return value;
	}
	
	public static Degeneracy parse( double value ) {
		if( value < 0 )
			return null;
		if( value < 0.5 )
			return UNIQUE;
		if( value < 1.5 )
			return DISCRIMINATING;
		return NON_DISCRIMINATING;
	}

	private final int value;
}
