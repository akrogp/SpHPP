#!/bin/bash

# ======
# Config
# ======

LEGO=/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/lego
#TARGET_FASTA="/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/cima/HPP2014/ensemblCrap.fasta"
#DECOY_FASTA="/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/cima/HPP2014/ensemblCrapDecoy.fasta"
TARGET_FASTA="/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/datasets/gencode24-principal-unique.target.fasta"
DECOY_FASTA="/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/datasets/gencode24-principal-unique.decoy.fasta"
#PROT2GEN="$LEGO/Prot2Gen.tsv.gz"
PROT2GEN="$LEGO/Prot2Gencode24.tsv.gz"

if [ $# -eq 5 ]; then
	MODE="$1"
	if [ "$2" = "occam" ]; then
		OCCAM=1
	else
		OCCAM=0
	fi
	TARGET_DATA="$3"
	DECOY_DATA="$4"
	RESULTS="$5"
else
	echo -e "Usage:\n\t$0 cfg|lpc|lpc2|lpm|lpm2 occam|equi <target-data> <decoy-data> <results>"
	exit
fi

PREFIX="decoy-"
OCCAM_DIFF="0.1"
OCCAM_ITER="300"

ENZYME=TRYPSIN
MISS_CLE=2
NTERM=2
USE_DP=true

MIN_PEP_LEN=7
MAX_PEP_LEN=70
VAR_MODS=M
#MAX_PEP_MODS=6
MAX_PEP_MODS=0

# =======
# Runtime
# =======

JAR="$LEGO/EhuBio.jar"
LOGGING="$LEGO/logging.properties"
OPTS="-Xmx10g -Djava.util.logging.config.file=$LOGGING -Djava.awt.headless=true"

module() {
	echo
	java -cp "$JAR" $OPTS org.sphpp.workflow.module.$@ 2>&1 | tee -a "$RESULTS/log.txt"
}

# ========
# Database
# ========

database() {
	module Digester --fasta "$TARGET_FASTA" --output $RESULTS/Seq2ProtTarget.tsv.gz --enzyme "$ENZYME" --missed "$MISS_CLE" --nterm "$NTERM" --dp "$USE_DP" --minPepLen "$MIN_PEP_LEN" --maxPepLen "$MAX_PEP_LEN"
	module Digester --fasta "$DECOY_FASTA" --output $RESULTS/Seq2ProtDecoy.tsv.gz --enzyme "$ENZYME" --missed "$MISS_CLE" --nterm "$NTERM" --dp "$USE_DP" --minPepLen "$MIN_PEP_LEN" --maxPepLen "$MAX_PEP_LEN"

	if [ "$MODE" != "lpm" ]; then
		module Modeller --input $RESULTS/Seq2ProtTarget.tsv.gz --output $RESULTS/MdbProtTarget.tsv.gz --varMods "$VAR_MODS" --maxPepMods "$MAX_PEP_MODS"
		module Modeller --input $RESULTS/Seq2ProtDecoy.tsv.gz --output $RESULTS/MdbProtDecoy.tsv.gz --varMods "$VAR_MODS" --maxPepMods "$MAX_PEP_MODS"
	fi
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
	module PsmFilter --input $RESULTS/CompPsmTarget.tsv.gz --outputPsm $RESULTS/FilterPsmTarget.tsv.gz --outputPep $RESULTS/Seq2PepTarget.tsv.gz --relations $RESULTS/Feat2PepTarget.tsv.gz --rank 1 $FEAT
	module PsmFilter --input $RESULTS/CompPsmDecoy.tsv.gz --outputPsm $RESULTS/FilterPsmDecoy.tsv.gz --outputPep $RESULTS/Seq2PepDecoy.tsv.gz --relations $RESULTS/Feat2PepDecoy.tsv.gz --rank 1 $FEAT

	module LPCalculator --inTarget $RESULTS/FilterPsmTarget.tsv.gz --inDecoy $RESULTS/FilterPsmDecoy.tsv.gz --outTarget $RESULTS/LPPsmTarget.tsv.gz --outDecoy $RESULTS/LPPsmDecoy.tsv.gz

	module Integrator --input $RESULTS/LPPsmTarget.tsv.gz --relations $RESULTS/Feat2PepTarget.tsv.gz --output $RESULTS/LPPepTarget.tsv.gz --mode $INTEG
	module Integrator --input $RESULTS/LPPsmDecoy.tsv.gz --relations $RESULTS/Feat2PepDecoy.tsv.gz --output $RESULTS/LPPepDecoy.tsv.gz --mode $INTEG

	module FdrCalculator --inTarget $RESULTS/LPPepTarget.tsv.gz --inDecoy $RESULTS/LPPepDecoy.tsv.gz --outTarget $RESULTS/FdrPepTarget.tsv.gz --outDecoy $RESULTS/FdrPepDecoy.tsv.gz
}

# ================
# Peptide->Protein
# ================


pep2prot() {
	module Relator --upper $RESULTS/Seq2ProtTarget.tsv.gz --lower $RESULTS/Seq2PepTarget.tsv.gz --output $RESULTS/Pep2ProtTarget.tsv.gz
	module Relator --upper $RESULTS/Seq2ProtDecoy.tsv.gz --lower $RESULTS/Seq2PepDecoy.tsv.gz --output $RESULTS/Pep2ProtDecoy.tsv.gz

	module Integrator --input $RESULTS/LPPepTarget.tsv.gz --relations $RESULTS/Pep2ProtTarget.tsv.gz --output $RESULTS/LPProtTarget.tsv.gz --mode $INTEG
	module Integrator --input $RESULTS/LPPepDecoy.tsv.gz --relations $RESULTS/Pep2ProtDecoy.tsv.gz --output $RESULTS/LPProtDecoy.tsv.gz --mode $INTEG

	if [ "$MODE" != "lpm" ]; then

		ALPHA=`module Normalizer --relations $RESULTS/Pep2ProtDecoy.tsv.gz -m $RESULTS/MdbProtDecoy.tsv.gz --output $RESULTS/MProtDecoy.tsv.gz 2>&1 | grep alpha | cut -d '=' -f 2`
		module Normalizer --relations $RESULTS/Pep2ProtTarget.tsv.gz -m $RESULTS/MdbProtTarget.tsv.gz --output $RESULTS/MProtTarget.tsv.gz --alpha $ALPHA

		if [ "$OCCAM" -eq 0 ]; then
			module Corrector --input $RESULTS/LPProtTarget.tsv.gz -m $RESULTS/MProtTarget.tsv.gz --output $RESULTS/LPCorrProtTarget.tsv.gz --mode $CORR
			module Corrector --input $RESULTS/LPProtDecoy.tsv.gz -m $RESULTS/MProtDecoy.tsv.gz --output $RESULTS/LPCorrProtDecoy.tsv.gz --mode $CORR
		else
			if [ "$MODE" = "lpm2" ]; then
				echo "Occam in lpm2 still not implemented"
				exit
			fi
			module OccamIntegrator --inputScores $RESULTS/LPPepTarget.tsv.gz --inputRelations $RESULTS/Pep2ProtTarget.tsv.gz -m $RESULTS/MProtTarget.tsv.gz --outputScores $RESULTS/LPProtTarget.tsv.gz --outputCorrScores $RESULTS/LPCorrProtTarget.tsv.gz --outputRelations $RESULTS/Pep2ProtTarget.tsv.gz --maxDiff $OCCAM_DIFF --maxIters $OCCAM_ITER
			module OccamIntegrator --inputScores $RESULTS/LPPepDecoy.tsv.gz --inputRelations $RESULTS/Pep2ProtDecoy.tsv.gz -m $RESULTS/MProtDecoy.tsv.gz --outputScores $RESULTS/LPProtDecoy.tsv.gz --outputCorrScores $RESULTS/LPCorrProtDecoy.tsv.gz --outputRelations $RESULTS/Pep2ProtDecoy.tsv.gz --maxDiff $OCCAM_DIFF --maxIters $OCCAM_ITER
		fi

		module FdrCalculator --inTarget $RESULTS/LPCorrProtTarget.tsv.gz --inDecoy $RESULTS/LPCorrProtDecoy.tsv.gz --outTarget $RESULTS/FdrProtTarget.tsv.gz --outDecoy $RESULTS/FdrProtDecoy.tsv.gz
	else
		module FdrCalculator --inTarget $RESULTS/LPProtTarget.tsv.gz --inDecoy $RESULTS/LPProtDecoy.tsv.gz --outTarget $RESULTS/FdrProtTarget.tsv.gz --outDecoy $RESULTS/FdrProtDecoy.tsv.gz
	fi
}

# =============
# Protein->Gene
# =============

prot2gen() {
	module Integrator --input $RESULTS/LPProtTarget.tsv.gz --relations "$PROT2GEN" --output $RESULTS/LPGenTarget.tsv.gz --mode $INTEG
	module Integrator --input $RESULTS/LPProtDecoy.tsv.gz --relations "$PROT2GEN" --prefix "$PREFIX" --output $RESULTS/LPGenDecoy.tsv.gz --mode $INTEG

	if [ "$MODE" != "lpm" ]; then

		module Integrator --input $RESULTS/MProtTarget.tsv.gz --relations "$PROT2GEN" --output $RESULTS/MGenTarget.tsv.gz
		module Integrator --input $RESULTS/MProtDecoy.tsv.gz --relations "$PROT2GEN" --prefix "$PREFIX" --output $RESULTS/MGenDecoy.tsv.gz

		module Corrector --input $RESULTS/LPGenTarget.tsv.gz -m $RESULTS/MGenTarget.tsv.gz --output $RESULTS/LPGenCorrTarget.tsv.gz --mode $CORR
		module Corrector --input $RESULTS/LPGenDecoy.tsv.gz -m $RESULTS/MGenDecoy.tsv.gz --output $RESULTS/LPGenCorrDecoy.tsv.gz --mode $CORR

		module FdrCalculator --inTarget $RESULTS/LPGenCorrTarget.tsv.gz --inDecoy $RESULTS/LPGenCorrDecoy.tsv.gz --outTarget $RESULTS/FdrGenTarget.tsv.gz --outDecoy $RESULTS/FdrGenDecoy.tsv.gz
	else
		module FdrCalculator --inTarget $RESULTS/LPGenTarget.tsv.gz --inDecoy $RESULTS/LPGenDecoy.tsv.gz --outTarget $RESULTS/FdrGenTarget.tsv.gz --outDecoy $RESULTS/FdrGenDecoy.tsv.gz
	fi
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

	module Integrator --input $RESULTS/LPGenTarget.tsv.gz --relations $RESULTS/Gen2GrpTarget.tsv.gz --discard NON_CONCLUSIVE --output $RESULTS/LPGrpTarget.tsv.gz --mode $INTEG
	module Integrator --input $RESULTS/LPGenDecoy.tsv.gz --relations $RESULTS/Gen2GrpDecoy.tsv.gz --discard NON_CONCLUSIVE --output $RESULTS/LPGrpDecoy.tsv.gz --mode $INTEG

	if [ "$MODE" != "lpm" ]; then

		module Integrator --input $RESULTS/MGenTarget.tsv.gz --relations $RESULTS/Gen2GrpTarget.tsv.gz --output $RESULTS/MGrpTarget.tsv.gz
		module Integrator --input $RESULTS/MGenDecoy.tsv.gz --relations $RESULTS/Gen2GrpDecoy.tsv.gz --output $RESULTS/MGrpDecoy.tsv.gz

		module Corrector --input $RESULTS/LPGrpTarget.tsv.gz -m $RESULTS/MGrpTarget.tsv.gz --output $RESULTS/LPCorrGrpTarget.tsv.gz --mode $CORR
		module Corrector --input $RESULTS/LPGrpDecoy.tsv.gz -m $RESULTS/MGrpDecoy.tsv.gz --output $RESULTS/LPCorrGrpDecoy.tsv.gz --mode $CORR

		module FdrCalculator --inTarget $RESULTS/LPCorrGrpTarget.tsv.gz --inDecoy $RESULTS/LPCorrGrpDecoy.tsv.gz --outTarget $RESULTS/FdrGrpTarget.tsv.gz --outDecoy $RESULTS/FdrGrpDecoy.tsv.gz
	else
		module FdrCalculator --inTarget $RESULTS/LPGrpTarget.tsv.gz --inDecoy $RESULTS/LPGrpDecoy.tsv.gz --outTarget $RESULTS/FdrGrpTarget.tsv.gz --outDecoy $RESULTS/FdrGrpDecoy.tsv.gz
	fi
}

# ========
# Workflow
# ========

mkdir -p "$RESULTS"

if [ "$MODE" = "cfg" ]; then
	module ConfigDetector --input "$TARGET_DATA" --fasta "$TARGET_FASTA" --config "$RESULTS/config.ini" --max 1000
else
	if [ "$MODE" = "lpc" ]; then
		INTEG="OR"
		FEAT="--features true"
		CORR="POISSON"
	elif [ "$MODE" = "lpc2" ]; then
		INTEG="OR"
		FEAT="--features true"
		CORR="GAMMA"
	elif [ "$MODE" = "lpm" ]; then
		INTEG="MIN"
		FEAT="--bestPsm true"
	elif [ "$MODE" = "lpm2" ]; then
		INTEG="MIN"
		FEAT="--bestPsm true"
		CORR="LOGN"
	else
		echo "Mode not recognized"
		exit
	fi
	database
	parser
	psm2pep
	pep2prot
	prot2gen
	gen2grp
fi
