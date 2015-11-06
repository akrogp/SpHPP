package org.sphpp.workflow;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import es.ehubio.cli.ArgException;
import es.ehubio.cli.ArgParser;
import es.ehubio.cli.Argument;
import es.ehubio.io.CsvReader;

public abstract class TsvModule {	
	private static final int OPT_DISCARD = 100;
	protected static final int OPT_BASE = OPT_DISCARD+1;	
	private final static Logger logger = Logger.getLogger(TsvModule.class.getName());
	private final ArgParser args;
	private String discard = null;
	
	protected TsvModule( boolean discard, String description ) {
		args = new ArgParser(getClass().getSimpleName(), description);
		if( discard ) {
			Argument opt = new Argument(OPT_DISCARD,null,"discard");
			opt.setParamName("expression");
			opt.setDescription("discards lines from TSV input(s) containing the given expression");
			opt.setOptional(true);
			args.addOption(opt);
		}
	}
	
	protected boolean nextLine( CsvReader reader) throws IOException {
		do {
			if( reader.readLine() == null )
				return false;
		} while( discard != null && reader.getLine().contains(discard) );
		return true;
	}
	
	public void run(String[] args) {
		try {			
			List<Argument> opts = this.args.parseArgs(args);			
			if( !opts.isEmpty() ) {
				StringBuilder str = new StringBuilder("Using the following argument(s): ");
				for( Argument opt : opts ) {
					str.append(opt.toString());
					str.append(' ');
				}
				logger.info(str.toString());
			}
			logger.info("Running ...");
			Argument discard = this.args.getArgument(OPT_DISCARD);
			if( discard != null ) {
				opts.remove(discard);
				this.discard = discard.getValue();
			}						
			run(opts);
			logger.info("Finished successfully!!");
		} catch( ArgException e ) {
			logger.warning(e.getMessage());
			logger.info("Usage:\n"+this.args.getUsage());
		} catch( Exception e ) {
			logger.severe(e.getMessage());
		}
	}
	
	protected void addOption( Argument opt ) {
		args.addOption(opt);
	}
	
	protected Argument getArgument( int id ) {
		return args.getArgument(id);
	}
	
	protected String getValue( int id ) {
		return args.getValue(id);
	}
	
	protected abstract void run( List<Argument> args ) throws Exception;
}