package org.sphpp.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.sphpp.nextprot.ProteinEntry;
import org.sphpp.nextprot.xml.Annotation;
import org.sphpp.nextprot.xml.AnnotationList;
import org.sphpp.nextprot.xml.CvTerm;
import org.sphpp.nextprot.xml.DataSource;
import org.sphpp.nextprot.xml.Header;
import org.sphpp.nextprot.xml.NextprotExport;
import org.sphpp.nextprot.xml.Protein;

import es.ehubio.tools.Command.Interface;

public class ChrOntology implements Interface {
	@Override
	public String getUsage() {
		return "</path/to/nextprot_chromosome_16.xml>";
	}

	@Override
	public int getMinArgs() {
		return 1;
	}

	@Override
	public int getMaxArgs() {
		return 1;
	}

	@Override
	public void run(String[] args) throws Exception {		
		Logger logger = Logger.getLogger(ChrOntology.class.getName());
		logger.info("Loading XML, please wait ...");
		loadXml(args[0]);
		logger.info("XML file loaded");
		showHeader();
		/*List<String> categories = new ArrayList<>();
		categories.add("go cellular component");
		categories.add("subcellular location" );
		showCategories(categories);*/
		//build("go cellular component",true);
		build("subcellular location",true);
		showReport();
	}
	
	public void loadXml( String path ) {
		try {
			JAXBContext context = JAXBContext.newInstance(NextprotExport.class);
			Unmarshaller um = context.createUnmarshaller();
			nextprot = (NextprotExport)um.unmarshal(new File(path));
		} catch (JAXBException e) {
			nextprot = null;
			e.printStackTrace();
		} 
	}
	
	public void showHeader() {
		Header header = nextprot.getHeader();
		System.out.println(
			"--- neXtProt release " + header.getRelease().getNeXtProt().getDatabaseRelease() +
			" (" + header.getNumberOfEntries() + " entries) ---" );
		System.out.println("\nData sources:");
		for( DataSource source : header.getRelease().getDataSources().getDataSource() )
			System.out.println("\t" + source.getSource() + " (" + source.getLastImportDate() + ")");
	}
	
	public boolean isMissing( Protein protein ) {
		String evidence = protein.getProteinExistence().getValue(); 
		if( evidence.contains("protein level") || evidence.contains("Uncertain") )
			return false;
		return true;
	}
	
	public void build( String category, boolean onlyMissing ) {
		entries.clear();
		counts.clear();
		this.category = category;
		ProteinEntry entry;
		Set<String> categories;
		for( Protein protein : nextprot.getProteins().getProtein() ) {
			entry = new ProteinEntry();
			entry.setAccession(protein.getUniqueName());
			entry.setEvidence(protein.getProteinExistence().getValue().replaceAll("Evidence at ", ""));
			entry.setMissing(isMissing(protein));
			categories = new TreeSet<>();
			for( AnnotationList annotationList : protein.getAnnotations().getAnnotationList() ) {
				if( !category.equalsIgnoreCase(annotationList.getCategory().value()) )
					continue;
				for( Annotation annotation : annotationList.getAnnotation() ) {
					CvTerm cv = annotation.getCvTerm();
					if( cv == null )
						continue;
					String name = cv.getCvName().trim().replaceAll("[\n\t]", "").replaceAll(" +", " ");
					categories.add(name);
					Integer count = counts.get(name);
					if( onlyMissing && !entry.isMissing() ) {
						if( count == null )
							counts.put(name, 0);
						continue;					
					}
					if( count == null )
						counts.put(name, 1);
					else
						counts.put(name, count+1);
				}				
			}
			entry.setCategories(categories);
			entries.add(entry);
		}
	}
	
	public void showReport() {
		System.out.println("Protein:Evidence:Missing:"+category);
		for( ProteinEntry entry : entries )
			System.out.println(entry.toString());
		System.out.println(category+":count");
		for( String string : counts.keySet() )
			System.out.println(string+":"+counts.get(string));
	}
	
	public void showCategories( List<String> categories ) {
		String line;
		Set<String> fields = new TreeSet<>();
		System.out.print("Protein:Evidence");
		for( String category : categories )
			System.out.print(":"+category);
		System.out.println();
		for( Protein protein : nextprot.getProteins().getProtein() ) {
			line = protein.getUniqueName()+":"+protein.getProteinExistence().getValue().replaceAll("Evidence at ", "");
			for( String category : categories ) {
				line+=":";
				for( AnnotationList annotationList : protein.getAnnotations().getAnnotationList() )
					if( category.equalsIgnoreCase(annotationList.getCategory().value()) ) {
						fields.clear();
						for( Annotation annotation : annotationList.getAnnotation() ) {
							CvTerm cv = annotation.getCvTerm();
							if( cv != null ) {
								String name = cv.getCvName().trim().replaceAll("[\n\t]", "").replaceAll(" +", " ");
								fields.add(name);
							}								
						}
						boolean first = true;
						for( String field : fields )
							if( first ) {
								line += field;
								first = false;
							} else
								line += ";" + field;
					}
			}
			System.out.println(line);
		}		
	}
	
	private NextprotExport nextprot;
	private List<ProteinEntry> entries = new ArrayList<>();
	private Map<String,Integer> counts = new HashMap<>();
	private String category;
}