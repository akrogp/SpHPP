package org.sphpp.workflow.data;

public enum Evidence {
	CONCLUSIVE(1),
	INDISTINGUISABLE_GROUP(2),
	AMBIGUOUS_GROUP(3),
	NON_CONCLUSIVE(4);
	
	private Evidence( int value ) {
		this.value = value;
	}
	
	public int value() {
		return value;
	}
	
	public static Evidence parse( double value ) {
		if( value < 0 )
			return null;
		if( value < 1.5 )
			return CONCLUSIVE;
		if( value < 2.5 )
			return INDISTINGUISABLE_GROUP;
		if( value < 3.5 )
			return AMBIGUOUS_GROUP;
		return NON_CONCLUSIVE;
	}

	private final int value;
}