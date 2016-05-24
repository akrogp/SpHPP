package org.sphpp.workflow.module;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import org.sphpp.workflow.Arguments;
import org.sphpp.workflow.data.LinkMap;
import org.sphpp.workflow.data.Linkable;
import org.sphpp.workflow.data.ScoreItem;
import org.sphpp.workflow.data.ScoreLink;
import org.sphpp.workflow.file.RelationFile;
import org.sphpp.workflow.file.ScoreFile;

import es.ehubio.MathUtil;
import es.ehubio.Numbers;
import es.ehubio.cli.Argument;
import es.ehubio.proteomics.Decoyable;
import es.ehubio.proteomics.Score;
import es.ehubio.proteomics.ScoreType;
import es.ehubio.proteomics.io.EhubioCsv;

public class Normalizer extends WorkflowModule {
	public Normalizer() {
		super("Calculates a scaling factor between observed and expected M-values.");
		
		Argument arg = new Argument(OPT_REL, 'r', "relations");
		arg.setParamName("Pep2Prot.tsv");
		arg.setDescription("Input TSV file with observed relations.");
		addOption(arg);
		
		arg = new Argument(OPT_DBM, 'm', null);
		arg.setParamName("MdbProt.tsv");
		arg.setDescription("Input TSV file with db M-values.");
		addOption(arg);
		
		arg = new Argument(OPT_OUT, 'o', "output");
		arg.setParamName("MProt.tsv");
		arg.setDescription("Output TSV file for storing M-values.");
		arg.setDefaultValue("MProt.tsv.gz");
		addOption(arg);
		
		arg = new Argument(OPT_ALPHA, 'a', "alpha");
		arg.setParamName("alpha");
		arg.setDescription("Uses the specified correction factor instead of calculating it from observed values.");
		arg.setOptional();
		addOption(arg);
		
		addOption(Arguments.getDiscard());
	}
	
	public static void main(String[] args) {
		new Normalizer().run(args);
	}

	@Override
	protected void run(List<Argument> args) throws Exception {
		ScoreType dbType = ScoreType.M_DVALUE;
		ScoreType obsType = ScoreType.M_OVALUE;
		ScoreType expType = ScoreType.M_EVALUE;
		ScoreFile<ScoreItem> file = ScoreFile.load(getValue(OPT_DBM), dbType);
		LinkMap<ScoreLink,ScoreLink> map = RelationFile
				.load(getValue(OPT_REL),getValue(Arguments.OPT_DISCARD))
				.getScoreLinkMap(file.getItems());
		setObserved(map.getUpperList(), obsType);
		double alpha;
		if( getValue(OPT_ALPHA) != null )
			alpha = Numbers.parseDouble(getValue(OPT_ALPHA));
		else
			alpha = getFactor(map.getUpperList(), obsType, dbType);
		logger.info(String.format(Locale.ENGLISH, "alpha=%f", alpha));
		apply(map.getUpperList(), dbType, expType, alpha);
		//file.save(getValue(OPT_OUT), expType, obsType, dbType);
		//ScoreFile.save(file.getId(), wrapperSet, getValue(OPT_OUT), expType, obsType, dbType);
		ScoreFile.save(file.getId(), map.getUpperList(), getValue(OPT_OUT), expType, obsType);
		//System.out.println(getValue(OPT_OUT).replaceAll("\\..*", ".pdf"));
		EhubioCsv.saveModel(map.getUpperList(), obsType, expType, "Random matching model", getValue(OPT_OUT).replaceAll("\\..*", ".pdf"));
	}

	public static <S extends Decoyable & Linkable<FROM,TO>, FROM extends Linkable<FROM,TO>, TO extends Linkable<TO,FROM>>
	void setObserved( Collection<S> items, ScoreType obsType ) {
		for( S item : items ) {
			double M = 0.0;
			for( TO subitem : item.getLinks() ) {
				double factor = 1.0/subitem.getLinks().size();
				M += factor;
			}
			item.putScore(new Score(obsType, M));
		}
	}
	
	public static double getFactor(Collection<? extends Decoyable> items, ScoreType obsType, ScoreType dbType) {
		List<Double> listM = new ArrayList<>();
		double obsM, dbM;
		for( Decoyable item : items ) {
			obsM = item.getScoreByType(obsType).getValue();
			dbM = item.getScoreByType(dbType).getValue();				
			listM.add(obsM/dbM);
		}
		return MathUtil.median(listM);
	}
	
	public static void apply(Collection<? extends Decoyable> items, ScoreType dbType, ScoreType expType, double factor ) {
		for( Decoyable item : items ) {
			double dbM = item.getScoreByType(dbType).getValue();
			item.putScore(new Score(expType, dbM*factor));
		}
	}
	
	public static <FROM extends Linkable<FROM,TO> & Decoyable, TO extends Linkable<TO,FROM>>
	void run(Collection<FROM> items, ScoreType dbType, ScoreType obsType, ScoreType expType) {
		setObserved(items, obsType);
		double factor = getFactor(items, obsType, dbType);
		apply(items, dbType, expType, factor);
	}
	
	private static final Logger logger = Logger.getLogger(Normalizer.class.getName());
	private static final int OPT_DBM = 1;
	private static final int OPT_REL = 2;
	private static final int OPT_OUT = 3;
	private static final int OPT_ALPHA = 4;
}
