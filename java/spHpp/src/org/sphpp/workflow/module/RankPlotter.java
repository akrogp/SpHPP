package org.sphpp.workflow.module;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.print.PageFormat;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import org.sphpp.workflow.data.ScoreItem;
import org.sphpp.workflow.file.ScoreFile;

import es.ehubio.cli.Argument;
import es.ehubio.proteomics.ScoreType;
import gnu.jpdf.PDFJob;

public class RankPlotter extends WorkflowModule {
	public RankPlotter() {
		super("Generates a chart for checking if the LP scores follow an uniform distribution.");
		
		Argument arg = new Argument(OPT_INPUT, 'i', "input");
		arg.setParamName("LPScores.tsv");
		arg.setDescription("Input TSV file with LP values.");
		addOption(arg);
		
		arg = new Argument(OPT_OUTPUT, 'o', "output");
		arg.setParamName("LPScores");
		arg.setDescription("Output pdf files prefix for LP score vs rank charts.");
		addOption(arg);
	}
	
	public static void main(String[] args) {
		new RankPlotter().run(args);
	}

	@Override
	protected void run(List<Argument> args) throws Exception {
		ScoreFile<ScoreItem> input = ScoreFile.load(getValue(OPT_INPUT));				
		ScoreType lpScore = ScoreFile.selectScore(input.getItems(),ScoreType.LPCORR_SCORE,ScoreType.LP_SCORE);
		File file = new File(getValue(OPT_INPUT));
		File parent = file.getParentFile();
		String title = new File(parent.getName(),file.getName()).getPath();
		plotLog(input.getItems(), lpScore, title, getValue(OPT_OUTPUT)+"-log-all.pdf",0);
		//plotLog(input.getItems(), lpScore, title, getValue(OPT_OUTPUT)+"-log-top.pdf",100);
		plotLin(input.getItems(), lpScore, title, getValue(OPT_OUTPUT)+"-lin-all.pdf",0);
		plotLin(input.getItems(), lpScore, title, getValue(OPT_OUTPUT)+"-lin-top.pdf",100);
	}

	private void plotLog(Collection<? extends ScoreItem> items, final ScoreType lpScore, String title, String path, int count) throws FileNotFoundException {
		List<ScoreItem> list = new ArrayList<>(items);
		Collections.sort(list, new Comparator<ScoreItem>() {
			@Override
			public int compare(ScoreItem o1, ScoreItem o2) {
				return o2.getScoreByType(lpScore).compare(o1.getScoreByType(lpScore).getValue());
			}
		});
		count = count == 0 ? list.size() : Math.min(count, list.size());
		int i = 0;
		double[] x = new double[count];
		double[] y = new double[count];
		for( ScoreItem item : list ) {
			y[i] = item.getScoreByType(lpScore).getValue();
			double lin = ((double)(i+1))/list.size();
			x[i] = lin < 1e-300 ? 300 : -Math.log10(lin);
			i++;
			if( --count <= 0 )
				break;
		}
		PDFJob job = new PDFJob(new FileOutputStream(path),title);
		Graphics g = job.getGraphics(PageFormat.LANDSCAPE);
		plot(g,x,y,title,X_LOG_LABEL,Y_LOG_LABEL);
		g.dispose();
		job.end();
		save(x,y,path.replaceAll("\\.pdf$", ".tsv"));
	}
	
	private void plotLin(Collection<? extends ScoreItem> items, final ScoreType lpScore, String title, String path, int count) throws FileNotFoundException {
		List<ScoreItem> list = new ArrayList<>(items);
		Collections.sort(list, new Comparator<ScoreItem>() {
			@Override
			public int compare(ScoreItem o1, ScoreItem o2) {
				return o2.getScoreByType(lpScore).compare(o1.getScoreByType(lpScore).getValue());
			}
		});
		count = count == 0 ? list.size() : Math.min(count, list.size());
		int i = 0;
		double[] x = new double[count];
		double[] y = new double[count];
		for( ScoreItem item : list ) {
			y[i] = Math.pow(10.0, -item.getScoreByType(lpScore).getValue());
			x[i] = ((double)(i+1))/list.size();
			if( x[i] > 1.0 ) x[i] = 1.0;
			if( y[i] > 1.0 ) y[i] = 1.0;
			i++;
			if( --count <= 0 )
				break;
		}
		PDFJob job = new PDFJob(new FileOutputStream(path),title);
		Graphics g = job.getGraphics(PageFormat.LANDSCAPE);
		plot(g,x,y,title,X_LIN_LABEL,Y_LIN_LABEL);
		g.dispose();
		job.end();
		save(x,y,path.replaceAll("\\.pdf$", ".tsv"));
	}

	private void save(double[] x, double[] y, String path) throws FileNotFoundException {
		try(PrintWriter pw = new PrintWriter(path)) {
			for( int i = 0; i < x.length; i++ )
				pw.println(String.format(Locale.US, "%f\t%f", x[i], y[i]));
		}
	}

	private void plot(Graphics g, double[] x, double[] y, String title, String xLabel, String yLabel) {
		Color cAxes = Color.BLACK;
		Color cRef = Color.RED;
		Color cData = Color.BLUE;
		
		Rectangle bounds = g.getClipBounds();
		g.setClip(new Rectangle(0, 0, bounds.x+bounds.width, bounds.y+bounds.height));
		bounds = g.getClipBounds();
		int w = bounds.width;
		int h = bounds.height;
		
		Font f = new Font("TimesRoman", Font.PLAIN, FONT_SIZE);
		g.setFont(f);
		FontMetrics fm = null;
		try {
			fm = g.getFontMetrics(f);
		} catch(HeadlessException e) {
			fm = null;
			logger.warning("Using approximated font metrics");
		}
		
		int x0 = stringWidth(fm,yLabel);
		int y0 = h-1-getHeight(fm);
		double max = x[0];
		for( int i = 0; i < x.length; i++ ) {
			if( x[i] > max )
				max = x[i];
			if( y[i] > max )
				max = y[i];
		}
		double xmax = max/(w-x0);
		double ymax = max/y0;
		
		g.setColor(cAxes);		
		g.drawLine(x0, 0, x0, y0);
		g.drawLine(x0, y0, w-1, y0);
		g.drawString(xLabel,(w-stringWidth(fm,xLabel))/2,h-1);		
		g.drawString(yLabel, 0, (h-getHeight(fm))/2);
		g.drawString(title, x0+(w-x0-stringWidth(fm,title))/2, getHeight(fm));
		
		g.setColor(cRef);
		g.drawLine(x0, y0, w-1, 0);
		
		g.setColor(cData);				
		String strMax = String.format("%.0f", max);
		g.drawString(strMax, x0-stringWidth(fm,strMax), getHeight(fm));
		g.drawString(strMax, w-stringWidth(fm,strMax)-1, h-1);		
		for( int i = 0; i < x.length; i++ )
			g.fillRect((int)Math.round(x[i]/xmax)+x0-2, y0-(int)Math.round(y[i]/ymax)-2, 4, 4);
	}
	
	private static int stringWidth(FontMetrics fm, String str) {
		return fm != null ? fm.stringWidth(str) : str.length()*FONT_SIZE;
	}
	
	private static int getHeight(FontMetrics fm) {
		return fm != null ? fm.getHeight() : (int)(FONT_SIZE*1.5);
	}

	private static final Logger logger = Logger.getLogger(RankPlotter.class.getName());
	private static final String Y_LOG_LABEL = "LP";
	private static final String X_LOG_LABEL = "-log(rank/N)";
	private static final String Y_LIN_LABEL = "prob";
	private static final String X_LIN_LABEL = "rank/N";
	private static final int FONT_SIZE = 16;
	
	private static final int OPT_INPUT = 1;
	private static final int OPT_OUTPUT = 2;
}
