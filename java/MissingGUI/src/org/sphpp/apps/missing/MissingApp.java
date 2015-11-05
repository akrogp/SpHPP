package org.sphpp.apps.missing;

public class MissingApp {
	public static final char cellDelimiter = ';';
	public static final char listDelimiter = ',';
	public static final String notAvailable = "?";
	
	public static int compareNulls( Object okNull, Object okNotNull ) {
		if( okNull == null && okNotNull != null )
			return -1;
		if( okNull != null && okNotNull == null )
			return 1;
		return 0;
	}
	
	public static int compareBools( Boolean okFalse, Boolean okTrue ) {
		int res = compareNulls(okFalse, okTrue);
		if( res != 0 )
			return res;
		if( okFalse == null && okTrue == null )
			return 0;
		if( okFalse == false && okTrue == true )
			return -1;
		if( okFalse == true && okTrue == false )
			return 1;
		return 0;
	}
	
	public static int compareDoubles( Double okBig, Double okSmall ) {
		int res = compareNulls(okSmall, okBig);
		if( res != 0 )
			return res;
		if( okBig > okSmall )
			return -1;
		if( okSmall > okBig )
			return 1;
		return 0;
	}
	
	public static int compareIntegers( Integer okBig, Integer okSmall ) {
		int res = compareNulls(okSmall, okBig);
		if( res != 0 )
			return res;
		if( okBig > okSmall )
			return -1;
		if( okSmall > okBig )
			return 1;
		return 0;
	}
}
