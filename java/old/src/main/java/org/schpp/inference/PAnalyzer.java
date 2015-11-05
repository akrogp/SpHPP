package org.schpp.inference;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.proteored.miapeapi.cv.ControlVocabularyManager;
import org.proteored.miapeapi.cv.LocalOboTestControlVocabularyManager;
import org.proteored.miapeapi.experiment.model.ExtendedIdentifiedPeptide;
import org.proteored.miapeapi.experiment.model.ExtendedIdentifiedProtein;
import org.proteored.miapeapi.experiment.model.Replicate;
import org.proteored.miapeapi.interfaces.msi.MiapeMSIDocument;
import org.proteored.miapeapi.xml.msi.MIAPEMSIXmlFile;
import org.proteored.miapeapi.xml.msi.MiapeMSIXmlFactory;

/**
 *
 * @author gorka
 */
public class PAnalyzer {
    public class Stats {
        public int PeptideCount;
        public int ProteinCount;
        public int ProteinMaxCount;
        public int ProteinMinCount;
        public int ConclusiveCount;
        public int NonConclusiveCount;
        public int GroupedCount;
        public int AmbiguousGroupCount;        
        public int IndistinguishableGroupCount;
        public int FilteredCount;
        
        public void dump( PrintStream stream ) {
            stream.println( "Peptide count: " + PeptideCount );
            stream.println( "Protein count: " + ProteinCount );
            stream.println( "\tMaximum: " + ProteinMaxCount );
            stream.println( "\tConclusive: " + ConclusiveCount );
            stream.println( "\tNon-conclusive: " + NonConclusiveCount );
            stream.println( "\tFiltered: " + FilteredCount );
            stream.println( "\tGrouped: " + GroupedCount );
            stream.println( "\t\tIndistinguishable groups: " + IndistinguishableGroupCount );
            stream.println( "\t\tAmbiguous groups: " + AmbiguousGroupCount );
        }
    }
    
    public PAnalyzer() {
        mProts = new HashMap<Integer,InferenceProtein>();
        mPepts = new HashMap<Integer,InferencePeptide>();
        mGroups = new ArrayList<ProteinGroup>();
    }
    
    public List<ProteinGroup> run( List<ExtendedIdentifiedProtein> proteins ) {        
        createInferenceMaps(proteins);
        classifyPeptides();
        classifyProteins();
        createGroups();
        markIndistinguishable();
        doStats();
        return mGroups;
    }   
    
    private void createInferenceMaps( List<ExtendedIdentifiedProtein> proteins ) {
        InferenceProtein iprot;
        InferencePeptide ipept;
        
        for( ExtendedIdentifiedProtein prot : proteins ) {
            iprot = mProts.get(prot.getId());
            if( iprot == null ) {
                iprot = new InferenceProtein(prot);
                mProts.put(iprot.getId(), iprot);
            }
            for( ExtendedIdentifiedPeptide pept : prot.getPeptides() ) {
                ipept = mPepts.get(pept.getId());
                if( ipept == null ) {
                    ipept = new InferencePeptide(pept);                    
                    mPepts.put(ipept.getId(), ipept);
                }
                ipept.Proteins.add(iprot);
                iprot.Peptides.add(ipept);
            }            
        }
    }
    
    private void classifyPeptides() {
        // Locate unique peptides
        for( InferencePeptide pept : mPepts.values() )
            if( pept.Proteins.size() == 1 ) {
                pept.Relation = PeptideRelation.Unique;
                pept.Proteins.get(0).Evidence = ProteinEvidence.Conclusive;
            }
            else
                pept.Relation = PeptideRelation.Discriminating;
        
        // Locate non-meaningful peptides (first round)
        for( InferenceProtein prot : mProts.values() )
            if( prot.Evidence == ProteinEvidence.Conclusive )
                for( InferencePeptide pept : prot.Peptides )
                    if( pept.Relation != PeptideRelation.Unique )
                        pept.Relation = PeptideRelation.NonDiscriminating;
        
        // Locate non-meaningful peptides (second round)
        boolean shared;
        for( InferencePeptide pept : mPepts.values() ) {
            if( pept.Relation != PeptideRelation.Discriminating )
                continue;
            for( InferencePeptide pept2 : pept.Proteins.get(0).Peptides ) {
                if( pept2.Relation == PeptideRelation.NonDiscriminating )
                    continue;
                if( pept2.Proteins.size() <= pept.Proteins.size() )
                    continue;
                shared = true;
                for( InferenceProtein p : pept.Proteins )
                    if( !p.Peptides.contains(pept2) ) {
                        shared = false;
                        break;
                    }
                if( shared )
                    pept2.Relation = PeptideRelation.NonDiscriminating;
            }
        }
    }
    
    private void classifyProteins() {        
        boolean group;
        
        for( InferenceProtein prot : mProts.values() ) {
            if( prot.Evidence == ProteinEvidence.Conclusive )
                continue;
            if( prot.Peptides.isEmpty() ) {
                prot.Evidence = ProteinEvidence.Filtered;
                continue;
            }
            group = false;
            for( InferencePeptide pept : prot.Peptides )
                if( pept.Relation == PeptideRelation.Discriminating ) {
                    group = true;
                    break;
                }
            prot.Evidence = group ? ProteinEvidence.AmbiguousGroup : ProteinEvidence.NonConclusive;
        }                
    }
    
    private void createGroups() {
        ProteinGroup group;
        
        for( InferenceProtein prot : mProts.values() ) {
            if( prot.Group == null ) {
                group = new ProteinGroup(prot.Evidence);
                group.add(prot);
                prot.Group = group;
                mGroups.add(group);                
            }
            if( prot.Evidence != ProteinEvidence.AmbiguousGroup ) 
                continue;
            for( InferencePeptide pept : prot.Peptides ) {
                if( pept.Relation != PeptideRelation.Discriminating )
                    continue;
                for( InferenceProtein subp : pept.Proteins ) {
                    if( subp.Evidence != ProteinEvidence.AmbiguousGroup || subp.Group != null )
                        continue;
                    prot.Group.add(subp);
                    subp.Group = prot.Group;
                }
            }
        }                
    }
    
    private void markIndistinguishable() {
        boolean indistinguishable;
        for( ProteinGroup group : mGroups ) {
            if( group.Evidence != ProteinEvidence.AmbiguousGroup )
                continue;
            indistinguishable = true;
            for( InferencePeptide pept : group.get(0).Peptides ) {
                if( pept.Relation != PeptideRelation.Discriminating )
                    continue;
                for( InferenceProtein prot : group ) {
                    if( !prot.Peptides.contains(pept) ) {
                        indistinguishable = false;
                        break;
                    }
                }
                if( !indistinguishable )
                    break;
            }
            if( indistinguishable )
                group.Evidence = ProteinEvidence.Indistinguishable;
        }
    }
    
    public Stats doStats() {
        mStats = new Stats();
        for( ProteinGroup group : mGroups ) {            
            switch( group.Evidence ) {
                case AmbiguousGroup:
                    mStats.AmbiguousGroupCount++;
                    mStats.GroupedCount += group.size();
                    mStats.ProteinCount++;
                    break;
                case Conclusive:
                    mStats.ConclusiveCount++;
                    mStats.ProteinCount++;
                    break;
                case Filtered:
                    mStats.FilteredCount++;
                    break;
                case Indistinguishable:
                    mStats.IndistinguishableGroupCount++;
                    mStats.GroupedCount += group.size();
                    mStats.ProteinCount++;
                    break;
                case NonConclusive:
                    mStats.NonConclusiveCount++;
                    break;
            }
            if( group.Evidence != ProteinEvidence.Filtered )
                mStats.ProteinMaxCount += group.size();
        }
        mStats.PeptideCount = mPepts.size();
        mStats.ProteinMinCount = -1;        
        return mStats;
    }
    
    private Map<Integer,InferenceProtein> mProts;
    private Map<Integer,InferencePeptide> mPepts;
    private List<ProteinGroup> mGroups;
    private Stats mStats;
    
    //
    // MIAPE-PAnalyzer Tester
    //
    public static void main( String[] args ) {
        try {            
            // MIAPE
            File file = new File("downloads/MIAPE_MSI_4100.xml");
            MiapeMSIDocument miapeMSI = MiapeMSIXmlFactory.getFactory().toDocument(
                new MIAPEMSIXmlFile(file), cv, null, null, null);
            List<MiapeMSIDocument> miapeMSIs = new ArrayList<MiapeMSIDocument>();
            miapeMSIs.add(miapeMSI);
            Replicate replicate = new Replicate("replicate", "experiment", null, miapeMSIs, null);
            final List<ExtendedIdentifiedProtein> identifiedProteins = replicate
                    .getIdentifiedProteins();            
            System.out.println("Tenemos " + identifiedProteins.size() + " proteínas");            
            
            // PAnalyzer
            System.out.println( "Ejecutando PAnalyzer ..." );
            PAnalyzer panalyzer = new PAnalyzer();
            List<ProteinGroup> groups = panalyzer.run(identifiedProteins);
            PAnalyzer.Stats stats = panalyzer.doStats();
            stats.dump(System.out);
            for( ProteinGroup group : groups )
                group.dump(System.out);
	} catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static ControlVocabularyManager cv = new LocalOboTestControlVocabularyManager();
}
