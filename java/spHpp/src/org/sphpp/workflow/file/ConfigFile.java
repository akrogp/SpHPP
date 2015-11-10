package org.sphpp.workflow.file;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import org.sphpp.workflow.Arguments;

import es.ehubio.io.Streams;
import es.ehubio.model.Aminoacid;
import es.ehubio.proteomics.pipeline.Digester;
import es.ehubio.proteomics.pipeline.Searcher;

public class ConfigFile {
	private final Digester.Config digestConfig;
	private final Searcher.Config searchConfig;
	
	public ConfigFile(Digester.Config digestConfig, Searcher.Config searchConfig) {
		this.digestConfig = digestConfig;
		this.searchConfig = searchConfig;
	}
	
	public void save( String title, String path ) throws FileNotFoundException, IOException {
		try(PrintWriter pw = new PrintWriter(Streams.getTextWriter(path))) {
			if( title != null ) {
				pw.println(String.format("# %s", title));
				pw.println();
			}
			if( digestConfig != null ) {
				pw.println("[Digestion]");
				pw.println(String.format("%s=%s", Arguments.getEnzyme().getLongOption(), digestConfig.getEnzyme()));
				pw.println(String.format("%s=%s", Arguments.getMissedCleavages().getLongOption(), digestConfig.getMissedCleavages()));
				pw.println(String.format("%s=%s", Arguments.getCutNterm().getLongOption(), digestConfig.getCutNterm()));
				pw.println(String.format("%s=%s", Arguments.getAspPro().getLongOption(), digestConfig.isUsingDP()));			
				pw.println();
			}
			if( searchConfig != null ) {
				pw.println("[Search]");
				pw.println(String.format("%s=%s", Arguments.getMinPepLen().getLongOption(), searchConfig.getMinLength()));
				pw.println(String.format("%s=%s", Arguments.getMaxPepLen().getLongOption(), searchConfig.getMaxLength()));
				pw.println(String.format("%s=%s", Arguments.getMaxPepMods().getLongOption(), searchConfig.getMaxMods()));
				pw.println(String.format("%s=%s", Arguments.getVarMods().getLongOption(), getModsString()));
			}
		}
	}

	private Object getModsString() {
		StringBuilder str = new StringBuilder();
		for( Aminoacid aa : searchConfig.getVarMods() )
			str.append(aa.letter);
		return str.toString();
	}
}