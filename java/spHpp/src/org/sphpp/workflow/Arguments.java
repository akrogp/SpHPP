package org.sphpp.workflow;

import es.ehubio.Strings;
import es.ehubio.cli.Argument;
import es.ehubio.proteomics.Enzyme;

public class Arguments {
	private static final int OPT_DEFAULTS = 200;
	public static final int OPT_ENZYME = OPT_DEFAULTS+1;
	public static final int OPT_CLEAVAGES = OPT_DEFAULTS+2;
	public static final int OPT_ASP_PRO = OPT_DEFAULTS+3;
	public static final int OPT_CUT_NTERM = OPT_DEFAULTS+4;
	public static final int OPT_MIN_PEP_LEN = OPT_DEFAULTS+5;
	public static final int OPT_MAX_PEP_LEN = OPT_DEFAULTS+6;
	public static final int OPT_MAX_PEP_MODS = OPT_DEFAULTS+7;
	public static final int OPT_DISCARD = OPT_DEFAULTS+8;
	
	public static Argument getDiscard() {
		Argument arg = new Argument(OPT_DISCARD,null,"discard",true);
		arg.setParamName("expression");
		arg.setDescription("discards lines from TSV input(s) containing the given expression");
		return arg;
	}
	
	public static Argument getEnzyme() {
		Argument arg = new Argument(OPT_ENZYME, null, "enzyme");
		arg.setChoices(Strings.fromArray(Enzyme.values()));
		arg.setDescription("Enzyme used for digestion.");
		arg.setDefaultValue(Enzyme.TRYPSIN);
		return arg;
	}
	
	public static Argument getMissedCleavages() {
		Argument arg = new Argument(OPT_CLEAVAGES, null, "missed");
		arg.setParamName("missedCleavages");
		arg.setDescription("Number of missed cleavages allowed in the search.");
		arg.setDefaultValue(2);
		return arg;
	}
	
	public static Argument getAspPro() {
		Argument arg = new Argument(OPT_ASP_PRO, null, "dp", true);
		arg.setDescription("Enable adventitious cleavage at Asp-Pro residues. Disabled by default.");
		return arg;
	}
	
	public static Argument getCutNterm() {
		Argument arg = new Argument(OPT_CUT_NTERM, null, "nterm");
		arg.setParamName("maximum");
		arg.setDescription("Maximum number of N-term residues that might be cleaved off in vivo when the first amino acid is methionine.");
		arg.setDefaultValue(2);
		return arg;
	}
	
	public static Argument getMinPepLen() {
		Argument arg = new Argument(OPT_MIN_PEP_LEN, null, "minPepLen");
		arg.setParamName("length");
		arg.setDescription("Minimum peptide length in amino acids.");
		arg.setDefaultValue(7);
		return arg;
	}
	
	public static Argument getMaxPepLen() {
		Argument arg = new Argument(OPT_MAX_PEP_LEN, null, "maxPepLen");
		arg.setParamName("length");
		arg.setDescription("Maximum peptide length in amino acids.");
		arg.setDefaultValue(50);
		return arg;
	}
	
	public static Argument getMaxPepMods() {
		Argument arg = new Argument(OPT_MAX_PEP_MODS, null, "maxPepMods");
		arg.setParamName("max");
		arg.setDescription("Maximum number of modifications per peptide.");
		arg.setDefaultValue(3);
		return arg;
	}
}