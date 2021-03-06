#!/bin/bash

export TARGET_FASTA=/media/gorka/EhuBio/Fasta/gencode25.target.IL.gorka.fasta
export DECOY_FASTA=/media/gorka/EhuBio/Fasta/gencode25.decoy.IL.gorka.fasta
INPUT_DIR=/media/gorka/EhuBio/Search
OUTPUT_DIR=/media/gorka/EhuBio/Lego

#ENGINES="XTandem"
#ENGINES="XTandem Comet Fragger"
#ENGINES="Sequest XTandem-MGF XTandem-mzML Comet"
#ENGINES="XTandem+Comet-NoCal XTandem+Comet-Cal"
#ENGINES="Comet"
ENGINES="Comet2 Fragger"
#ENGINES="Comet3"
#ENGINES="Comet5"
#ENGINES="Fragger"
#ENGINES="XTandem+Fragger-NoCal XTandem+Fragger-Cal"

#SCORES="LPF LPM LPG LPG1 LPGN LPGB"
#SCORES="LPM LPF LPGN"
SCORES="LP"

#FDRS="NORMAL MAYU PICKED REFINED"
#FDRS="NORMAL PICKED REFINED"
FDRS="NORMAL"

TISSUES="Adult_Heart Adult_Liver Adult_Testis"

#for T in "$INPUT_DIR"/Adult* "$INPUT_DIR"/Fetal*; do
for T in $TISSUES; do
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
