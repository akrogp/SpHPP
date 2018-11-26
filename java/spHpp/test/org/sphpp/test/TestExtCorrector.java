package org.sphpp.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.sphpp.workflow.data.ScoreItem;
import org.sphpp.workflow.file.ScoreFile;
import org.sphpp.workflow.module.ExtCorrector;

public class TestExtCorrector {
	@Test
	public void testLpfm() throws Exception {
		String[] args = {
			"-i", res("FdrPepTarget.tsv"),
			"-r", res("Pep2GenTarget.tsv"),
			"-o", res("LPCorrGenTarget.tsv"),
			"--mode", "LPFM"
		};
		assertEquals(0, ExtCorrector.mainTest(args));
		ScoreFile<ScoreItem> output = ScoreFile.load(res("LPCorrGenTarget.tsv"));
		int count = 0;
		for( ScoreItem item : output.getItems() )
			if( item.getScores().iterator().next().getValue() == 0.0 )
				count++;
		assertEquals(1, count);
	}
	
	private String res(String path) {
		//return getClass().getResource(String.format("res/%s", path)).getPath();
		return String.format("%s/%s", "/home/gorka/MyProjects/spHPP/java/spHpp/test/res", path);
	}
}
