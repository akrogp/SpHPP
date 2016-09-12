#!/bin/bash

if [ $# -ne 5 ]; then
	echo -e "Usage:\n\t$0 <inputDir1> <prefixFile1> <inputDir2> <prefixFile2> <outputDir>"
	exit
fi

INPUT1="$1"
PREFIX1="$2"
INPUT2="$3"
PREFIX2="$4"
OUTPUT="$5"

# =======
# Runtime
# =======

LEGO=/home/gorka/Bio/Proyectos/Proteómica/spHPP/Work/Flow/lego
JAR="$LEGO/EhuBio.jar"
LOGGING="$LEGO/logging.properties"
OPTS="-Xmx10g -Djava.util.logging.config.file=$LOGGING -Djava.awt.headless=true"
USE_MAYU=true

module() {
    java -cp "$JAR" $OPTS org.sphpp.workflow.module.$@ 2>&1 | tee -a "$OUTPUT/log.txt"
}


# ===========
# Combination
# ===========

mkdir -p "$OUTPUT"

TARGET="$OUTPUT/CombTarget.tsv.gz"
DECOY="$OUTPUT/CombDecoy.tsv.gz"

FDRTARGET="$OUTPUT/FdrCombTarget.tsv.gz"
FDRDECOY="$OUTPUT/FdrCombDecoy.tsv.gz"

module Combinator --input1 "$INPUT1/${PREFIX1}Target.tsv.gz" --input2 "$INPUT2/${PREFIX2}Target.tsv.gz" --output "$TARGET"
module Combinator --input1 "$INPUT1/${PREFIX1}Decoy.tsv.gz" --input2 "$INPUT2/${PREFIX2}Decoy.tsv.gz" --output "$DECOY"

module FdrCalculator --inTarget "$TARGET" --inDecoy "$DECOY" --outTarget "$FDRTARGET" --outDecoy "$FDRDECOY" --useMayu "$USE_MAYU"
