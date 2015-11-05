package org.sphpp.tools;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import es.ehubio.db.fasta.Fasta;
import es.ehubio.db.fasta.Fasta.SequenceType;
import es.ehubio.tools.Command.Interface;


public class Up2Sp implements Interface {
	@Override
	public String getUsage() {
		return "</path/to/HUMAN.fasta.gz> </path/to/output.fasta>";
	}

	@Override
	public int getMinArgs() {
		return 2;
	}

	@Override
	public int getMaxArgs() {
		return 2;
	}

	@Override
	public void run(String[] args) throws Exception {
		Reader rd = new InputStreamReader(new GZIPInputStream(new FileInputStream(args[0])));
		List<Fasta> up = Fasta.readEntries(rd, SequenceType.PROTEIN);
		List<Fasta> sp = new ArrayList<>();
		rd.close();
		for( Fasta fasta : up ) {
			if( !fasta.getHeader().startsWith("sp") )
				continue;
			sp.add(fasta);
		}
		Writer wr = new FileWriter(args[1]);
		Fasta.writeEntries(wr, sp);
		wr.close();
	}
}
