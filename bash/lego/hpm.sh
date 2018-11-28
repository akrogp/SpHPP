#!/bin/bash

export TARGET_FASTA=/media/gorka/EhuBio/Fasta/gencode25.target.IL.gorka.fasta
export DECOY_FASTA=/media/gorka/EhuBio/Fasta/gencode25.decoy.IL.gorka.fasta
INPUT_DIR=/media/gorka/EhuBio/Search
OUTPUT_DIR=/media/gorka/EhuBio/Lego
ENGINES="XTandem"
#ENGINES="XTandem Comet Fragger"
#ENGINES="Sequest XTandem-MGF XTandem-mzML Comet"
#ENGINES="XTandem+Comet-NoCal XTandem+Comet-Cal"
#ENGINES="Comet"
#ENGINES="Comet2"
#ENGINES="Comet3"
#ENGINES="Comet5"
#ENGINES="Fragger"
#ENGINES="XTandem+Fragger-NoCal XTandem+Fragger-Cal"
#SCORES="LPF LPM LPG LPG1 LPGN LPGB"
SCORES="LP"
#FDRS="NORMAL MAYU PICKED REFINED"
FDRS="NORMAL"

#for T in "$INPUT_DIR"/Adult* "$INPUT_DIR"/Fetal*; do
#for T in All_Tissues; do
#for T in Adult_Heart; do
for T in Adult_Liver; do
	T=`basename "$T"`
	for E in $ENGINES; do
		for S in $SCORES; do
			for F in $FDRS; do
				echo
				echo "----- $T + $S + $F -----"
				TARGET_DATA="$INPUT_DIR/$T/$E/target" DECOY_DATA="$INPUT_DIR/$T/$E/decoy" RESULTS="$OUTPUT_DIR/$T/$E" SCORE=$S FDR=$F make pep gen
				#TARGET_DATA="$INPUT_DIR/$T/$E/target" DECOY_DATA="$INPUT_DIR/$T/$E/decoy" RESULTS="$OUTPUT_DIR/$T/$E" SCORE=$S FDR=$F make parse
			done
		done
	done
done
