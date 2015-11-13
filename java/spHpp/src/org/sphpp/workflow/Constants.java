package org.sphpp.workflow;

import es.ehubio.proteomics.ScoreType;

public final class Constants {
	public static final String SEP = "\t";
	public static final String SUB_SEP = ",";
	public static final int DETECT_COUNT=1000;
	public static final ScoreType[] SCORES = {
		ScoreType.SEQUEST_XCORR, ScoreType.MASCOT_SCORE, ScoreType.XTANDEM_EVALUE,
		ScoreType.LP_SCORE, ScoreType.LPCORR_SCORE, ScoreType.N_EVALUE, ScoreType.N_OVALUE, ScoreType.M_EVALUE, ScoreType.M_OVALUE,
		ScoreType.OTHER_LARGER, ScoreType.OTHER_SMALLER};  
}
