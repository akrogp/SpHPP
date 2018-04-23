package org.sphpp.workflow.module;

import java.util.List;
import java.util.logging.Logger;

import es.ehubio.cli.ArgException;
import es.ehubio.cli.ArgParser;
import es.ehubio.cli.Argument;

public abstract class WorkflowModule {		
	private final static Logger logger = Logger.getLogger(WorkflowModule.class.getName());
	private final ArgParser args;
	
	protected WorkflowModule( String description ) {
		args = new ArgParser(getClass().getSimpleName(), description);
	}
	
	public int run(String[] args) {
		if( args.length == 1 && args[0].equals("?") ) {
			System.out.println(this.args.getDescription());
			return 0;
		}
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
			logger.info(String.format("Running '%s' ...", getClass().getSimpleName()));
			long t1 = System.currentTimeMillis();
			run(opts);
			long t2 = System.currentTimeMillis();
			logger.info(String.format("'%s' finished successfully in %d seconds!!", getClass().getSimpleName(), (t2-t1)/1000));
			return 0;
		} catch( ArgException e ) {
			logger.warning(e.getMessage());
			System.out.println("== Usage ==\n\n"+this.args.getUsage());
		} catch( Exception e ) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
		return 1;
	}
	
	protected void addOption( Argument opt ) {
		args.addOption(opt);
	}
	
	protected Argument getArgument( int id ) {
		return args.getArgument(id);
	}
	
	protected boolean hasArgument( int id ) {
		return args.hasArgument(id);
	}
	
	protected String getValue( int id ) {
		return args.getValue(id);
	}
	
	protected Integer getIntValue( int id ) {
		return args.getIntValue(id);
	}
	
	protected Boolean getBooleanValue( int id ) {
		return args.getBooleanValue(id);
	}
	
	protected Double getDoubleValue( int id ) {
		return args.getDoubleValue(id);
	}
	
	protected abstract void run( List<Argument> args ) throws Exception;
}