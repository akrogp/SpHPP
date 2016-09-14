#
# Makefile for executing Lego Workflow modules
#

# General parameters
#MODE=lpc
USE_OCCAM=false
OCCAM_DIFF=0.1
OCCAM_ITER=300
USE_MAYU=true
BIN_SIZE=0

# Path parameters
LEGO=/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/lego
PROT2GEN=$(LEGO)/Prot2Gencode24.tsv.gz
TARGET_FASTA=/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/datasets/gencode24-principal-unique.target.fasta
DECOY_FASTA=/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/datasets/gencode24-principal-unique.decoy.fasta
#TARGET_DATA=/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/datasets/Adult_Frontalcortex/xtandem/target
#DECOY_DATA=/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/datasets/Adult_Frontalcortex/xtandem/decoy
#RESULTS=.

# Digestion parameters
ENZYME=TRYPSIN
MISS_CLE=2
NTERM=2
USE_DP=true

# Search parameters
PREFIX=decoy-
MIN_PEP_LEN=7
MAX_PEP_LEN=70
VAR_MODS=M
MAX_PEP_MODS=0

# Java settings
JAR=$(LEGO)/EhuBio.jar
LOGGING=$(LEGO)/logging.properties
OPTS=-Xmx10g -Djava.util.logging.config.file=$(LOGGING) -Djava.awt.headless=true

# Aux variables
USAGE=Type "make help" for usage information
ifeq ($(MODE),lpc)
CORR=POISSON
INTEG=OR
FEAT=--features true
endif
ifeq ($(MODE),lpc2)
CORR=GAMMA
INTEG=OR
FEAT=--features true
endif
ifeq ($(MODE),lpm)
INTEG=MIN
FEAT=--bestPsm true
endif
ifeq ($(MODE),lpm2)
CORR=LOGN
INTEG=OR
FEAT=--bestPsm true
endif

# Functions
module=echo; java -cp $(JAR) $(OPTS) org.sphpp.workflow.module.$1 $2 2>&1 | tee -a $(RESULTS)/log.txt

# Main targets
.PHONY: all clean cfg parse psm pep prot gen grp install help
all: params parse psm pep prot gen grp
help:
	@echo
	@echo "Usage:"
	@echo "\tmake [<PARAM1>=<value1> <PARAM2>=<value2> ...] [<target>]"
	@echo
	@echo "Where <target> can be any of:"
	@echo "\tall:\t\tExecutes the complete workflow (default)"
	@echo "\tcfg:\t\tDetects digestion and search parameter from TARGET_DATA"
	@echo "\tparse:\t\tParses search engine output files"
	@echo "\tpsm:\t\tExecutes workflow until PSM level"
	@echo "\tpep:\t\tExecutes workflow until peptide level"
	@echo "\tprot:\t\tExecutes workflow until protein level"
	@echo "\tgen:\t\tExecutes workflow until gene level"
	@echo "\tgrp:\t\tExecutes workflow until gene group level"
	@echo "\tclean:\t\tCleans workflow output files"
	@echo "\tinstall:\tMoves MODE dependent files to a separate directory"
	@echo
	@echo "General parameters:"
	@echo "\tMODE:\t\tAny of lpc|lpc2|lpm|lpm2"
	@echo "\tUSE_OCCAM:\tUse OCCAM (true) or equitative (false) pep->prot distribution (default: $(USE_OCCAM))"
	@echo "\tUSE_MAYU:\tUse MAYU (true) instead of D/T (false) for FDR calculation (default: $(USE_MAYU))"
	@echo
	@echo "Path parameters:"
	@echo "\tTARGET_DATA:\tDirectory with search engine output files for target DB"
	@echo "\tDECOY_DATA:\tDirectory with search engine output files for decoy DB"
	@echo "\tRESULTS:\tDirectory for saving output files of the identification workflow"
	@echo "\tTARGET_FASTA:\tPath to target fasta file"
	@echo "\tDECOY_FASTA:\tPath to decoy fasta file"
	@echo "\tPROT2GEN:\tPath to protein accession to gene accession relations file"
	@echo "\tLEGO:\t\tPath to Workflow libraries"
	@echo
	@echo "Digestion parameters:"
	@echo "\tENZYME:\t\tTRYPSIN|TRYPSINP (default: $(ENZYME))"
	@echo "\tMISS_CLE:\tNumber of missed cleavages allowed (default: $(MISS_CLE))"
	@echo "\tNTERM:\t\tMaximum N-terminal methionine cleavage length (default: $(NTERM))"
	@echo "\tUSE_DP:\t\tAsp-Pro cleavage: true|false (default: $(USE_DP))"
	@echo
	@echo "Search parameters:"
	@echo "\tPREFIX:\t\tPrefix of decoy entries (default: $(PREFIX))"
	@echo "\tMIN_PEP_LEN:\tMinimum peptide length (default: $(MIN_PEP_LEN))"
	@echo "\tMAX_PEP_LEN:\tMaximum peptide length (default: $(MAX_PEP_LEN))"
	@echo "\tVAR_MODS:\tList of aa with variable modifications (default: $(VAR_MODS))"
	@echo "\tMAX_PEP_MODS:\tMaximum number of modifications in the same peptide (default: $(MAX_PEP_MODS))"
	@echo
clean: params
	rm -f $(RESULTS)/*.tsv.gz $(RESULTS)/*.pdf
cfg: params $(RESULTS)/config.ini
parse: params $(RESULTS)/PsmTarget.tsv.gz $(RESULTS)/PsmDecoy.tsv.gz
psm: params $(RESULTS)/LPPsmTarget.tsv.gz $(RESULTS)/LPPsmDecoy.tsv.gz
pep: params $(RESULTS)/FdrPepTarget.tsv.gz $(RESULTS)/FdrPepDecoy.tsv.gz
prot: params $(RESULTS)/FdrProtTarget.tsv.gz $(RESULTS)/FdrProtDecoy.tsv.gz
gen: params $(RESULTS)/FdrGenTarget.tsv.gz $(RESULTS)/FdrGenDecoy.tsv.gz
grp: params $(RESULTS)/FdrGrpTarget.tsv.gz $(RESULTS)/FdrGrpDecoy.tsv.gz
install: params
	mkdir -p $(RESULTS)/$(MODE)
	mv -f $(RESULTS)/*Fdr* $(RESULTS)/Psm2* $(RESULTS)/Filter* $(RESULTS)/Feat* $(RESULTS)/LP* $(RESULTS)/Seq2Pep* $(RESULTS)/Pep2* $(RESULTS)/MProt* $(RESULTS)/MGen* $(RESULTS)/Gen2* $(RESULTS)/MGrp* $(RESULTS)/$(MODE) 2>/dev/null || true

#
# Params
#

ifneq ($(MAKECMDGOALS),help)
params:	$(RESULTS)
ifneq ($(MAKECMDGOALS),clean)
ifneq ($(MAKECMDGOALS),cfg)
ifndef MODE
    $(error MODE is undefined. $(USAGE))
endif
endif
endif
ifneq ($(MAKECMDGOALS),clean)
ifneq ($(MAKECMDGOALS),install)
ifndef TARGET_DATA
    $(error TARGET_DATA is undefined. $(USAGE))
endif
ifneq ($(MAKECMDGOALS),cfg)
ifndef DECOY_DATA
    $(error DECOY_DATA is undefined. $(USAGE))
endif
endif
endif
endif
ifndef RESULTS
    $(error RESULTS is undefined. $(USAGE))
endif
endif

ifdef RESULTS
$(RESULTS):
	mkdir -p $(RESULTS)
endif

#
# Config
#

$(RESULTS)/config.ini: $(TARGET_DATA) $(TARGET_FASTA)
	@$(call module,ConfigDetector,--input $(TARGET_DATA) --fasta $(TARGET_FASTA) --config $(RESULTS)/config.ini --max 1000)

#
# Parser
#

$(RESULTS)/PsmTarget.tsv.gz: $(TARGET_DATA)
	@$(call module,Parser,--input $(TARGET_DATA) --outputPsm $(RESULTS)/PsmTarget.tsv.gz)
$(RESULTS)/PsmDecoy.tsv.gz: $(DECOY_DATA)
	@$(call module,Parser,--input $(DECOY_DATA) --outputPsm $(RESULTS)/PsmDecoy.tsv.gz)

#
# PSM level
#

$(RESULTS)/CompPsmTarget.tsv.gz $(RESULTS)/CompPsmDecoy.tsv.gz: $(RESULTS)/PsmTarget.tsv.gz $(RESULTS)/PsmDecoy.tsv.gz
	@$(call module,Competitor,--inTarget $(RESULTS)/PsmTarget.tsv.gz --inDecoy $(RESULTS)/PsmDecoy.tsv.gz --outTarget $(RESULTS)/CompPsmTarget.tsv.gz --outDecoy $(RESULTS)/CompPsmDecoy.tsv.gz)

$(RESULTS)/FilterPsmTarget.tsv.gz $(RESULTS)/Seq2PepTarget.tsv.gz $(RESULTS)/Feat2PepTarget.tsv.gz: $(RESULTS)/CompPsmTarget.tsv.gz
	@$(call module,PsmFilter,--input $(RESULTS)/CompPsmTarget.tsv.gz --outputPsm $(RESULTS)/FilterPsmTarget.tsv.gz --outputPep $(RESULTS)/Seq2PepTarget.tsv.gz --relations $(RESULTS)/Feat2PepTarget.tsv.gz --rank 1 $(FEAT))
$(RESULTS)/FilterPsmDecoy.tsv.gz $(RESULTS)/Seq2PepDecoy.tsv.gz $(RESULTS)/Feat2PepDecoy.tsv.gz: $(RESULTS)/CompPsmDecoy.tsv.gz
	@$(call module,PsmFilter --input $(RESULTS)/CompPsmDecoy.tsv.gz --outputPsm $(RESULTS)/FilterPsmDecoy.tsv.gz --outputPep $(RESULTS)/Seq2PepDecoy.tsv.gz --relations $(RESULTS)/Feat2PepDecoy.tsv.gz --rank 1 $(FEAT))

$(RESULTS)/LPPsmTarget.tsv.gz $(RESULTS)/LPPsmDecoy.tsv.gz: $(RESULTS)/FilterPsmTarget.tsv.gz $(RESULTS)/FilterPsmDecoy.tsv.gz
	@$(call module,LPCalculator,--inTarget $(RESULTS)/FilterPsmTarget.tsv.gz --inDecoy $(RESULTS)/FilterPsmDecoy.tsv.gz --outTarget $(RESULTS)/LPPsmTarget.tsv.gz --outDecoy $(RESULTS)/LPPsmDecoy.tsv.gz)

#
# Pepide level
#

$(RESULTS)/LPPepTarget.tsv.gz: $(RESULTS)/LPPsmTarget.tsv.gz $(RESULTS)/Feat2PepTarget.tsv.gz
	@$(call module,Integrator,--input $(RESULTS)/LPPsmTarget.tsv.gz --relations $(RESULTS)/Feat2PepTarget.tsv.gz --output $(RESULTS)/LPPepTarget.tsv.gz --mode $(INTEG))
$(RESULTS)/LPPepDecoy.tsv.gz: $(RESULTS)/LPPsmDecoy.tsv.gz $(RESULTS)/Feat2PepDecoy.tsv.gz
	@$(call module,Integrator,--input $(RESULTS)/LPPsmDecoy.tsv.gz --relations $(RESULTS)/Feat2PepDecoy.tsv.gz --output $(RESULTS)/LPPepDecoy.tsv.gz --mode $(INTEG))

$(RESULTS)/FdrPepTarget.tsv.gz $(RESULTS)/FdrPepDecoy.tsv.gz: $(RESULTS)/LPPepTarget.tsv.gz $(RESULTS)/LPPepDecoy.tsv.gz
	@$(call module,FdrCalculator,--inTarget $(RESULTS)/LPPepTarget.tsv.gz --inDecoy $(RESULTS)/LPPepDecoy.tsv.gz --outTarget $(RESULTS)/FdrPepTarget.tsv.gz --outDecoy $(RESULTS)/FdrPepDecoy.tsv.gz)

#
# Protein level
#

$(RESULTS)/Seq2ProtTarget.tsv.gz: $(TARGET_FASTA)
	@$(call module,Digester,--fasta $(TARGET_FASTA) --output $(RESULTS)/Seq2ProtTarget.tsv.gz --enzyme $(ENZYME) --missed $(MISS_CLE) --nterm $(NTERM) --dp $(USE_DP) --minPepLen $(MIN_PEP_LEN) --maxPepLen $(MAX_PEP_LEN))
$(RESULTS)/Seq2ProtDecoy.tsv.gz: $(DECOY_FASTA)
	@$(call module,Digester,--fasta $(DECOY_FASTA) --output $(RESULTS)/Seq2ProtDecoy.tsv.gz --enzyme $(ENZYME) --missed $(MISS_CLE) --nterm $(NTERM) --dp $(USE_DP) --minPepLen $(MIN_PEP_LEN) --maxPepLen $(MAX_PEP_LEN))

ifneq ($(MODE),lpm)
$(RESULTS)/MdbProtTarget.tsv.gz: $(RESULTS)/Seq2ProtTarget.tsv.gz
	@$(call module,Modeller,--input $(RESULTS)/Seq2ProtTarget.tsv.gz --output $(RESULTS)/MdbProtTarget.tsv.gz --varMods $(VAR_MODS) --maxPepMods $(MAX_PEP_MODS))
$(RESULTS)/MdbProtDecoy.tsv.gz: $(RESULTS)/Seq2ProtDecoy.tsv.gz
	@$(call module,Modeller,--input $(RESULTS)/Seq2ProtDecoy.tsv.gz --output $(RESULTS)/MdbProtDecoy.tsv.gz --varMods $(VAR_MODS) --maxPepMods $(MAX_PEP_MODS))
USAGE=Type "make help" for usage information
endif

$(RESULTS)/Pep2ProtTarget.tsv.gz: $(RESULTS)/Seq2ProtTarget.tsv.gz $(RESULTS)/Seq2PepTarget.tsv.gz
	@$(call module,Relator,--upper $(RESULTS)/Seq2ProtTarget.tsv.gz --lower $(RESULTS)/Seq2PepTarget.tsv.gz --output $(RESULTS)/Pep2ProtTarget.tsv.gz)
$(RESULTS)/Pep2ProtDecoy.tsv.gz: $(RESULTS)/Seq2ProtDecoy.tsv.gz $(RESULTS)/Seq2PepDecoy.tsv.gz
	@$(call module,Relator,--upper $(RESULTS)/Seq2ProtDecoy.tsv.gz --lower $(RESULTS)/Seq2PepDecoy.tsv.gz --output $(RESULTS)/Pep2ProtDecoy.tsv.gz)

ifneq ($(MODE),lpm)
$(RESULTS)/MProtDecoy.tsv.gz $(RESULTS)/MProtTarget.tsv.gz: $(RESULTS)/Pep2ProtDecoy.tsv.gz $(RESULTS)/MdbProtDecoy.tsv.gz $(RESULTS)/Pep2ProtTarget.tsv.gz $(RESULTS)/MdbProtTarget.tsv.gz
	$(eval ALPHA=$(shell $(call module,Normalizer,--relations $(RESULTS)/Pep2ProtDecoy.tsv.gz -m $(RESULTS)/MdbProtDecoy.tsv.gz --output $(RESULTS)/MProtDecoy.tsv.gz) 2>&1 | grep alpha | cut -d '=' -f 2))
	@$(call module,Normalizer,--relations $(RESULTS)/Pep2ProtTarget.tsv.gz -m $(RESULTS)/MdbProtTarget.tsv.gz --output $(RESULTS)/MProtTarget.tsv.gz --alpha $(ALPHA))
endif

ifeq ($(USE_OCCAM),false)
$(RESULTS)/LPProtTarget.tsv.gz: $(RESULTS)/LPPepTarget.tsv.gz $(RESULTS)/Pep2ProtTarget.tsv.gz
	@$(call module,Integrator,--input $(RESULTS)/LPPepTarget.tsv.gz --relations $(RESULTS)/Pep2ProtTarget.tsv.gz --output $(RESULTS)/LPProtTarget.tsv.gz --mode $(INTEG))
$(RESULTS)/LPProtDecoy.tsv.gz: $(RESULTS)/LPPepDecoy.tsv.gz $(RESULTS)/Pep2ProtDecoy.tsv.gz
	@$(call module,Integrator,--input $(RESULTS)/LPPepDecoy.tsv.gz --relations $(RESULTS)/Pep2ProtDecoy.tsv.gz --output $(RESULTS)/LPProtDecoy.tsv.gz --mode $(INTEG))
endif

ifneq ($(MODE),lpm)
ifeq ($(USE_OCCAM),false)
$(RESULTS)/LPCorrProtTarget.tsv.gz: $(RESULTS)/LPProtTarget.tsv.gz $(RESULTS)/MProtTarget.tsv.gz
	@$(call module,Corrector,--input $(RESULTS)/LPProtTarget.tsv.gz -m $(RESULTS)/MProtTarget.tsv.gz --output $(RESULTS)/LPCorrProtTarget.tsv.gz --mode $(CORR))
$(RESULTS)/LPCorrProtDecoy.tsv.gz: $(RESULTS)/LPProtDecoy.tsv.gz $(RESULTS)/MProtDecoy.tsv.gz
	@$(call module,Corrector,--input $(RESULTS)/LPProtDecoy.tsv.gz -m $(RESULTS)/MProtDecoy.tsv.gz --output $(RESULTS)/LPCorrProtDecoy.tsv.gz --mode $(CORR))
else
$(RESULTS)/LPProtTarget.tsv.gz $(RESULTS)/LPCorrProtTarget.tsv.gz: $(RESULTS)/LPPepTarget.tsv.gz $(RESULTS)/Pep2ProtTarget.tsv.gz $(RESULTS)/MProtTarget.tsv.gz
	@$(call module,OccamIntegrator,--inputScores $(RESULTS)/LPPepTarget.tsv.gz --inputRelations $(RESULTS)/Pep2ProtTarget.tsv.gz -m $(RESULTS)/MProtTarget.tsv.gz --outputScores $(RESULTS)/LPProtTarget.tsv.gz --outputCorrScores $(RESULTS)/LPCorrProtTarget.tsv.gz --outputRelations $(RESULTS)/Pep2ProtTarget.tsv.gz --maxDiff $(OCCAM_DIFF) --maxIters $(OCCAM_ITER))
$(RESULTS)/LPProtDecoy.tsv.gz $(RESULTS)/LPCorrProtDecoy.tsv.gz: $(RESULTS)/LPPepDecoy.tsv.gz $(RESULTS)/Pep2ProtDecoy.tsv.gz $(RESULTS)/MProtDecoy.tsv.gz
	@$(call module,OccamIntegrator,--inputScores $(RESULTS)/LPPepDecoy.tsv.gz --inputRelations $(RESULTS)/Pep2ProtDecoy.tsv.gz -m $(RESULTS)/MProtDecoy.tsv.gz --outputScores $(RESULTS)/LPProtDecoy.tsv.gz --outputCorrScores $(RESULTS)/LPCorrProtDecoy.tsv.gz --outputRelations $(RESULTS)/Pep2ProtDecoy.tsv.gz --maxDiff $(OCCAM_DIFF) --maxIters $(OCCAM_ITER))
endif
endif

ifneq ($(MODE),lpm)
$(RESULTS)/FdrProtTarget.tsv.gz $(RESULTS)/FdrProtDecoy.tsv.gz: $(RESULTS)/LPCorrProtTarget.tsv.gz $(RESULTS)/LPCorrProtDecoy.tsv.gz $(RESULTS)/MdbProtTarget.tsv.gz $(RESULTS)/MdbProtDecoy.tsv.gz
	@$(call module,FdrCalculator,--inTarget $(RESULTS)/LPCorrProtTarget.tsv.gz --inDecoy $(RESULTS)/LPCorrProtDecoy.tsv.gz --outTarget $(RESULTS)/FdrProtTarget.tsv.gz --outDecoy $(RESULTS)/FdrProtDecoy.tsv.gz --useMayu $(USE_MAYU) --mTarget $(RESULTS)/MdbProtTarget.tsv.gz --mDecoy $(RESULTS)/MdbProtDecoy.tsv.gz --binSize $(BIN_SIZE))
else
$(RESULTS)/FdrProtTarget.tsv.gz $(RESULTS)/FdrProtDecoy.tsv.gz: $(RESULTS)/LPProtTarget.tsv.gz $(RESULTS)/LPProtDecoy.tsv.gz $(RESULTS)/MdbProtTarget.tsv.gz $(RESULTS)/MdbProtDecoy.tsv.gz
	@$(call module,FdrCalculator,--inTarget $(RESULTS)/LPProtTarget.tsv.gz --inDecoy $(RESULTS)/LPProtDecoy.tsv.gz --outTarget $(RESULTS)/FdrProtTarget.tsv.gz --outDecoy $(RESULTS)/FdrProtDecoy.tsv.gz --useMayu $(USE_MAYU) --mTarget $(RESULTS)/MdbProtTarget.tsv.gz --mDecoy $(RESULTS)/MdbProtDecoy.tsv.gz --binSize $(BIN_SIZE))
endif

#
# Gene level
#

$(RESULTS)/LPGenTarget.tsv.gz: $(PROT2GEN) $(RESULTS)/LPProtTarget.tsv.gz
	@$(call module,Integrator,--input $(RESULTS)/LPProtTarget.tsv.gz --relations $(PROT2GEN) --output $(RESULTS)/LPGenTarget.tsv.gz --mode $(INTEG))
$(RESULTS)/LPGenDecoy.tsv.gz: $(PROT2GEN) $(RESULTS)/LPProtDecoy.tsv.gz
	@$(call module,Integrator,--input $(RESULTS)/LPProtDecoy.tsv.gz --relations $(PROT2GEN) --prefix $(PREFIX) --output $(RESULTS)/LPGenDecoy.tsv.gz --mode $(INTEG))

ifneq ($(MODE),lpm)
$(RESULTS)/MGenTarget.tsv.gz: $(PROT2GEN) $(RESULTS)/MProtTarget.tsv.gz
	@$(call module,Integrator,--input $(RESULTS)/MProtTarget.tsv.gz --relations $(PROT2GEN) --output $(RESULTS)/MGenTarget.tsv.gz)
$(RESULTS)/MGenDecoy.tsv.gz: $(PROT2GEN) $(RESULTS)/MProtDecoy.tsv.gz
	@$(call module,Integrator,--input $(RESULTS)/MProtDecoy.tsv.gz --relations $(PROT2GEN) --prefix $(PREFIX) --output $(RESULTS)/MGenDecoy.tsv.gz)

$(RESULTS)/LPCorrGenTarget.tsv.gz: $(RESULTS)/LPGenTarget.tsv.gz $(RESULTS)/MGenTarget.tsv.gz
	@$(call module,Corrector,--input $(RESULTS)/LPGenTarget.tsv.gz -m $(RESULTS)/MGenTarget.tsv.gz --output $(RESULTS)/LPCorrGenTarget.tsv.gz --mode $(CORR))
$(RESULTS)/LPCorrGenDecoy.tsv.gz: $(RESULTS)/LPGenDecoy.tsv.gz $(RESULTS)/MGenDecoy.tsv.gz
	@$(call module,Corrector,--input $(RESULTS)/LPGenDecoy.tsv.gz -m $(RESULTS)/MGenDecoy.tsv.gz --output $(RESULTS)/LPCorrGenDecoy.tsv.gz --mode $(CORR))

$(RESULTS)/FdrGenTarget.tsv.gz $(RESULTS)/FdrGenDecoy.tsv.gz: $(RESULTS)/LPCorrGenTarget.tsv.gz $(RESULTS)/LPCorrGenDecoy.tsv.gz
	@$(call module,FdrCalculator,--inTarget $(RESULTS)/LPCorrGenTarget.tsv.gz --inDecoy $(RESULTS)/LPCorrGenDecoy.tsv.gz --outTarget $(RESULTS)/FdrGenTarget.tsv.gz --outDecoy $(RESULTS)/FdrGenDecoy.tsv.gz --useMayu $(USE_MAYU))
else
$(RESULTS)/FdrGenTarget.tsv.gz $(RESULTS)/FdrGenDecoy.tsv.gz: $(RESULTS)/LPGenTarget.tsv.gz $(RESULTS)/LPGenDecoy.tsv.gz
	@$(call module,FdrCalculator,--inTarget $(RESULTS)/LPGenTarget.tsv.gz --inDecoy $(RESULTS)/LPGenDecoy.tsv.gz --outTarget $(RESULTS)/FdrGenTarget.tsv.gz --outDecoy $(RESULTS)/FdrGenDecoy.tsv.gz --useMayu $(USE_MAYU))
endif

#
# Group level
#

$(RESULTS)/Pep2GenTarget.tsv.gz: $(PROT2GEN) $(RESULTS)/Pep2ProtTarget.tsv.gz $(RESULTS)/FdrPepTarget.tsv.gz
	@$(call module,Relator,--upper $(PROT2GEN) --lower $(RESULTS)/Pep2ProtTarget.tsv.gz --output $(RESULTS)/Pep2GenTarget.tsv.gz)
	@$(call module,FdrTagger,--inputRelations $(RESULTS)/Pep2GenTarget.tsv.gz --scores $(RESULTS)/FdrPepTarget.tsv.gz --outputRelations $(RESULTS)/Pep2GenTarget.tsv.gz)
$(RESULTS)/Pep2GenDecoy.tsv.gz: $(PROT2GEN) $(RESULTS)/Pep2ProtDecoy.tsv.gz $(RESULTS)/FdrPepDecoy.tsv.gz
	@$(call module,Relator,--upper $(PROT2GEN) --upperPrefix $(PREFIX) --lower $(RESULTS)/Pep2ProtDecoy.tsv.gz --output $(RESULTS)/Pep2GenDecoy.tsv.gz)
	@$(call module,FdrTagger,--inputRelations $(RESULTS)/Pep2GenDecoy.tsv.gz --scores $(RESULTS)/FdrPepDecoy.tsv.gz --outputRelations $(RESULTS)/Pep2GenDecoy.tsv.gz)

$(RESULTS)/Gen2GrpTarget.tsv.gz: $(RESULTS)/Pep2GenTarget.tsv.gz
	@$(call module,Grouper,--input $(RESULTS)/Pep2GenTarget.tsv.gz --discard LowFdr --output $(RESULTS)/Gen2GrpTarget.tsv.gz)
$(RESULTS)/Gen2GrpDecoy.tsv.gz: $(RESULTS)/Pep2GenDecoy.tsv.gz
	@$(call module,Grouper,--input $(RESULTS)/Pep2GenDecoy.tsv.gz --discard LowFdr --output $(RESULTS)/Gen2GrpDecoy.tsv.gz)

$(RESULTS)/LPGrpTarget.tsv.gz: $(RESULTS)/LPGenTarget.tsv.gz $(RESULTS)/Gen2GrpTarget.tsv.gz
	@$(call module,Integrator,--input $(RESULTS)/LPGenTarget.tsv.gz --relations $(RESULTS)/Gen2GrpTarget.tsv.gz --discard NON_CONCLUSIVE --output $(RESULTS)/LPGrpTarget.tsv.gz --mode $(INTEG))
$(RESULTS)/LPGrpDecoy.tsv.gz: $(RESULTS)/LPGenDecoy.tsv.gz $(RESULTS)/Gen2GrpDecoy.tsv.gz
	@$(call module,Integrator,--input $(RESULTS)/LPGenDecoy.tsv.gz --relations $(RESULTS)/Gen2GrpDecoy.tsv.gz --discard NON_CONCLUSIVE --output $(RESULTS)/LPGrpDecoy.tsv.gz --mode $(INTEG))

ifneq ($(MODE),lpm)
$(RESULTS)/MGrpTarget.tsv.gz: $(RESULTS)/MGenTarget.tsv.gz $(RESULTS)/Gen2GrpTarget.tsv.gz
	@$(call module,Integrator,--input $(RESULTS)/MGenTarget.tsv.gz --relations $(RESULTS)/Gen2GrpTarget.tsv.gz --output $(RESULTS)/MGrpTarget.tsv.gz)
$(RESULTS)/MGrpDecoy.tsv.gz: $(RESULTS)/MGenDecoy.tsv.gz $(RESULTS)/Gen2GrpDecoy.tsv.gz
	@$(call module,Integrator,--input $(RESULTS)/MGenDecoy.tsv.gz --relations $(RESULTS)/Gen2GrpDecoy.tsv.gz --output $(RESULTS)/MGrpDecoy.tsv.gz)

$(RESULTS)/LPCorrGrpTarget.tsv.gz: $(RESULTS)/LPGrpTarget.tsv.gz $(RESULTS)/MGrpTarget.tsv.gz
	@$(call module,Corrector,--input $(RESULTS)/LPGrpTarget.tsv.gz -m $(RESULTS)/MGrpTarget.tsv.gz --output $(RESULTS)/LPCorrGrpTarget.tsv.gz --mode $(CORR))
$(RESULTS)/LPCorrGrpDecoy.tsv.gz: $(RESULTS)/LPGrpDecoy.tsv.gz $(RESULTS)/MGrpDecoy.tsv.gz
	@$(call module,Corrector,--input $(RESULTS)/LPGrpDecoy.tsv.gz -m $(RESULTS)/MGrpDecoy.tsv.gz --output $(RESULTS)/LPCorrGrpDecoy.tsv.gz --mode $(CORR))

$(RESULTS)/FdrGrpTarget.tsv.gz $(RESULTS)/FdrGrpDecoy.tsv.gz: $(RESULTS)/LPCorrGrpTarget.tsv.gz $(RESULTS)/LPCorrGrpDecoy.tsv.gz
	@$(call module,FdrCalculator,--inTarget $(RESULTS)/LPCorrGrpTarget.tsv.gz --inDecoy $(RESULTS)/LPCorrGrpDecoy.tsv.gz --outTarget $(RESULTS)/FdrGrpTarget.tsv.gz --outDecoy $(RESULTS)/FdrGrpDecoy.tsv.gz --useMayu $(USE_MAYU))
else
$(RESULTS)/FdrGrpTarget.tsv.gz $(RESULTS)/FdrGrpDecoy.tsv.gz: $(RESULTS)/LPGrpTarget.tsv.gz $(RESULTS)/LPGrpDecoy.tsv.gz
	@$(call module,FdrCalculator,--inTarget $(RESULTS)/LPGrpTarget.tsv.gz --inDecoy $(RESULTS)/LPGrpDecoy.tsv.gz --outTarget $(RESULTS)/FdrGrpTarget.tsv.gz --outDecoy $(RESULTS)/FdrGrpDecoy.tsv.gz --useMayu $(USE_MAYU))
endif
