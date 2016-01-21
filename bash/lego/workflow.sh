#!/bin/bash

# ======
# Config
# ======

#RESULTS=UPV-MCF7
RESULTS=CBM-MCF7-XT2

#TARGET_DATA="/home/gorka/Descargas/Temp/CIMA/UPV-MCF7"
#TARGET_DATA="/home/gorka/Descargas/GoogleDrive/DatosHPP2014-Analisis2015/CBM-MCF7"
TARGET_DATA="/home/gorka/Descargas/Temp/CIMA/CBM-MCF7-XT"
TARGET_FASTA="/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/Pandey/ensemblCrap.fasta.gz"

#DECOY_DATA="/home/gorka/Descargas/Temp/CIMA/UPV-MCF7-D"
#DECOY_DATA="/home/gorka/Descargas/GoogleDrive/DatosHPP2014-Analisis2015/CBM-MCF7-D"
DECOY_DATA="/home/gorka/Descargas/Temp/CIMA/CBM-MCF7-XT-D"
DECOY_FASTA="/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/letter/Pandey/ensemblCrapDecoy.fasta.gz"

PROT2GEN=Prot2Gen.tsv.gz

PREFIX="decoy-"
OCCAM_DIFF="0.1"
OCCAM_ITER="300"

ENZYME=TRYPSIN
#ENZYME=TRYPSINP
MISS_CLE=2
NTERM=2
USE_DP=true

#MIN_PEP_LEN=6
MIN_PEP_LEN=7
MAX_PEP_LEN=50
VAR_MODS=M
#MAX_PEP_MODS=6
MAX_PEP_MODS=0

# =======
# Runtime
# =======

JAR=EhuBio.jar
OPTS="-Xmx10g -Djava.util.logging.config.file=logging.properties -Djava.awt.headless=true"

module() {
	java -cp "$JAR" $OPTS org.sphpp.workflow.module.$@ 2>&1 | tee -a "$RESULTS/log.txt"
}

# ========
# Database
# ========

database() {
	module Digester --fasta "$TARGET_FASTA" --output $RESULTS/Seq2ProtTarget.tsv.gz --enzyme "$ENZYME" --missed "$MISS_CLE" --nterm "$NTERM" --dp "$USE_DP" --minPepLen "$MIN_PEP_LEN" --maxPepLen "$MAX_PEP_LEN"
	module Digester --fasta "$DECOY_FASTA" --output $RESULTS/Seq2ProtDecoy.tsv.gz --enzyme "$ENZYME" --missed "$MISS_CLE" --nterm "$NTERM" --dp "$USE_DP" --minPepLen "$MIN_PEP_LEN" --maxPepLen "$MAX_PEP_LEN"

	module Modeller --input $RESULTS/Seq2ProtTarget.tsv.gz --output $RESULTS/MdbProtTarget.tsv.gz --varMods "$VAR_MODS" --maxPepMods "$MAX_PEP_MODS"
	module Modeller --input $RESULTS/Seq2ProtDecoy.tsv.gz --output $RESULTS/MdbProtDecoy.tsv.gz --varMods "$VAR_MODS" --maxPepMods "$MAX_PEP_MODS"
}

# ======
# Parser
# ======

parser() {
	module Parser --input "$TARGET_DATA" --outputPsm $RESULTS/PsmTarget.tsv.gz
	module Parser --input "$DECOY_DATA" --outputPsm $RESULTS/PsmDecoy.tsv.gz

	module Competitor --inTarget $RESULTS/PsmTarget.tsv.gz --inDecoy $RESULTS/PsmDecoy.tsv.gz --outTarget $RESULTS/CompPsmTarget.tsv.gz --outDecoy $RESULTS/CompPsmDecoy.tsv.gz
}

# ============
# PSM->Peptide
# ============

psm2pep() {
	module PsmFilter --input $RESULTS/CompPsmTarget.tsv.gz --outputPsm $RESULTS/FilterPsmTarget.tsv.gz --outputPep $RESULTS/Seq2PepTarget.tsv.gz --relations $RESULTS/Feat2PepTarget.tsv.gz --rank 1 --features true
	module PsmFilter --input $RESULTS/CompPsmDecoy.tsv.gz --outputPsm $RESULTS/FilterPsmDecoy.tsv.gz --outputPep $RESULTS/Seq2PepDecoy.tsv.gz --relations $RESULTS/Feat2PepDecoy.tsv.gz --rank 1 --features true

	module LPCalculator --inTarget $RESULTS/FilterPsmTarget.tsv.gz --inDecoy $RESULTS/FilterPsmDecoy.tsv.gz --outTarget $RESULTS/LPPsmTarget.tsv.gz --outDecoy $RESULTS/LPPsmDecoy.tsv.gz

	module Integrator --input $RESULTS/LPPsmTarget.tsv.gz --relations $RESULTS/Feat2PepTarget.tsv.gz --output $RESULTS/LPPepTarget.tsv.gz
	module Integrator --input $RESULTS/LPPsmDecoy.tsv.gz --relations $RESULTS/Feat2PepDecoy.tsv.gz --output $RESULTS/LPPepDecoy.tsv.gz

	module FdrCalculator --inTarget $RESULTS/LPPepTarget.tsv.gz --inDecoy $RESULTS/LPPepDecoy.tsv.gz --outTarget $RESULTS/FdrPepTarget.tsv.gz --outDecoy $RESULTS/FdrPepDecoy.tsv.gz
}

# ================
# Peptide->Protein
# ================


pep2protStart() {
	module Relator --upper $RESULTS/Seq2ProtTarget.tsv.gz --lower $RESULTS/Seq2PepTarget.tsv.gz --output $RESULTS/Pep2ProtTarget.tsv.gz
	module Relator --upper $RESULTS/Seq2ProtDecoy.tsv.gz --lower $RESULTS/Seq2PepDecoy.tsv.gz --output $RESULTS/Pep2ProtDecoy.tsv.gz

	module Integrator --input $RESULTS/LPPepTarget.tsv.gz --relations $RESULTS/Pep2ProtTarget.tsv.gz --output $RESULTS/LPProtTarget.tsv.gz
	module Integrator --input $RESULTS/LPPepDecoy.tsv.gz --relations $RESULTS/Pep2ProtDecoy.tsv.gz --output $RESULTS/LPProtDecoy.tsv.gz

	ALPHA=`module Normalizer --relations $RESULTS/Pep2ProtDecoy.tsv.gz -m $RESULTS/MdbProtDecoy.tsv.gz --output $RESULTS/MProtDecoy.tsv.gz 2>&1 | grep alpha | cut -d '=' -f 2`
	module Normalizer --relations $RESULTS/Pep2ProtTarget.tsv.gz -m $RESULTS/MdbProtTarget.tsv.gz --output $RESULTS/MProtTarget.tsv.gz --alpha $ALPHA
}

pep2protEqui() {
	pep2protStart

	module Corrector --input $RESULTS/LPProtTarget.tsv.gz -m $RESULTS/MProtTarget.tsv.gz --output $RESULTS/LPCorrProtTarget.tsv.gz
	module Corrector --input $RESULTS/LPProtDecoy.tsv.gz -m $RESULTS/MProtDecoy.tsv.gz --output $RESULTS/LPCorrProtDecoy.tsv.gz

	pep2protEnd
}

pep2protOccam() {
	pep2protStart

	module OccamIntegrator --inputScores $RESULTS/LPPepTarget.tsv.gz --inputRelations $RESULTS/Pep2ProtTarget.tsv.gz -m $RESULTS/MProtTarget.tsv.gz --outputScores $RESULTS/LPProtTarget.tsv.gz --outputCorrScores $RESULTS/LPCorrProtTarget.tsv.gz --outputRelations $RESULTS/Pep2ProtTarget.tsv.gz --maxDiff $OCCAM_DIFF --maxIters $OCCAM_ITER
	module OccamIntegrator --inputScores $RESULTS/LPPepDecoy.tsv.gz --inputRelations $RESULTS/Pep2ProtDecoy.tsv.gz -m $RESULTS/MProtDecoy.tsv.gz --outputScores $RESULTS/LPProtDecoy.tsv.gz --outputCorrScores $RESULTS/LPCorrProtDecoy.tsv.gz --outputRelations $RESULTS/Pep2ProtDecoy.tsv.gz --maxDiff $OCCAM_DIFF --maxIters $OCCAM_ITER

	pep2protEnd
}

pep2protEnd() {
	module FdrCalculator --inTarget $RESULTS/LPCorrProtTarget.tsv.gz --inDecoy $RESULTS/LPCorrProtDecoy.tsv.gz --outTarget $RESULTS/FdrProtTarget.tsv.gz --outDecoy $RESULTS/FdrProtDecoy.tsv.gz
}

# =============
# Protein->Gene
# =============

prot2gen() {
	module Integrator --input $RESULTS/LPProtTarget.tsv.gz --relations "$PROT2GEN" --output $RESULTS/LPGenTarget.tsv.gz
	module Integrator --input $RESULTS/LPProtDecoy.tsv.gz --relations "$PROT2GEN" --prefix "$PREFIX" --output $RESULTS/LPGenDecoy.tsv.gz

	module Integrator --input $RESULTS/MProtTarget.tsv.gz --relations "$PROT2GEN" --output $RESULTS/MGenTarget.tsv.gz
	module Integrator --input $RESULTS/MProtDecoy.tsv.gz --relations "$PROT2GEN" --prefix "$PREFIX" --output $RESULTS/MGenDecoy.tsv.gz

	module Corrector --input $RESULTS/LPGenTarget.tsv.gz -m $RESULTS/MGenTarget.tsv.gz --output $RESULTS/LPGenCorrTarget.tsv.gz
	module Corrector --input $RESULTS/LPGenDecoy.tsv.gz -m $RESULTS/MGenDecoy.tsv.gz --output $RESULTS/LPGenCorrDecoy.tsv.gz

	module FdrCalculator --inTarget $RESULTS/LPGenCorrTarget.tsv.gz --inDecoy $RESULTS/LPGenCorrDecoy.tsv.gz --outTarget $RESULTS/FdrGenTarget.tsv.gz --outDecoy $RESULTS/FdrGenDecoy.tsv.gz
}

# ===========
# Gene->Group
# ===========

gen2grp() {
	module Relator --upper "$PROT2GEN" --lower $RESULTS/Pep2ProtTarget.tsv.gz --output $RESULTS/Pep2GenTarget.tsv.gz 
	module Relator --upper "$PROT2GEN" --upperPrefix "$PREFIX" --lower $RESULTS/Pep2ProtDecoy.tsv.gz --output $RESULTS/Pep2GenDecoy.tsv.gz 

	module FdrTagger --inputRelations $RESULTS/Pep2GenTarget.tsv.gz --scores $RESULTS/FdrPepTarget.tsv.gz --outputRelations $RESULTS/Pep2GenTarget.tsv.gz
	module FdrTagger --inputRelations $RESULTS/Pep2GenDecoy.tsv.gz --scores $RESULTS/FdrPepDecoy.tsv.gz --outputRelations $RESULTS/Pep2GenDecoy.tsv.gz

	module Grouper --input $RESULTS/Pep2GenTarget.tsv.gz --discard LowFdr --output $RESULTS/Gen2GrpTarget.tsv.gz
	module Grouper --input $RESULTS/Pep2GenDecoy.tsv.gz --discard LowFdr --output $RESULTS/Gen2GrpDecoy.tsv.gz

	module Integrator --input $RESULTS/MGenTarget.tsv.gz --relations $RESULTS/Gen2GrpTarget.tsv.gz --output $RESULTS/MGrpTarget.tsv.gz
	module Integrator --input $RESULTS/MGenDecoy.tsv.gz --relations $RESULTS/Gen2GrpDecoy.tsv.gz --output $RESULTS/MGrpDecoy.tsv.gz

	module Integrator --input $RESULTS/LPGenTarget.tsv.gz --relations $RESULTS/Gen2GrpTarget.tsv.gz --discard NON_CONCLUSIVE --output $RESULTS/LPGrpTarget.tsv.gz
	module Integrator --input $RESULTS/LPGenDecoy.tsv.gz --relations $RESULTS/Gen2GrpDecoy.tsv.gz --discard NON_CONCLUSIVE --output $RESULTS/LPGrpDecoy.tsv.gz

	module Corrector --input $RESULTS/LPGrpTarget.tsv.gz -m $RESULTS/MGrpTarget.tsv.gz --output $RESULTS/LPCorrGrpTarget.tsv.gz
	module Corrector --input $RESULTS/LPGrpDecoy.tsv.gz -m $RESULTS/MGrpDecoy.tsv.gz --output $RESULTS/LPCorrGrpDecoy.tsv.gz

	module FdrCalculator --inTarget $RESULTS/LPCorrGrpTarget.tsv.gz --inDecoy $RESULTS/LPCorrGrpDecoy.tsv.gz --outTarget $RESULTS/FdrGrpTarget.tsv.gz --outDecoy $RESULTS/FdrGrpDecoy.tsv.gz
}

# ========
# Workflow
# ========

mkdir -p "$RESULTS"

if [ "$1" = "-d" ]; then
	module ConfigDetector --input "$TARGET_DATA" --fasta "$TARGET_FASTA" --config "$RESULTS/config.ini" --max 10000
else
	database
	parser
	psm2pep
	pep2protEqui
	#pep2protOccam
	prot2gen
	gen2grp
fi
