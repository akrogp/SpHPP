#!/bin/bash
#
# Sample bash script for executing the workflow used in the paper:
# * Title  : A protein probability model for high-throughput protein
#            identification by mass spectrometry-based proteomics
# * Authors: Gorka Prieto and Jesús Vázquez
# * Contact: Gorka Prieto <gorka.prieto@ehu.eus>
# * Journal: Journal of Proteome Research
# * Year   : 2019
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <https://www.gnu.org/licenses/>.
#

if [ $# -ne 6 ]; then
	echo
	echo "Usage:"
	echo
	echo "  $0 target.fasta decoy.fasta decoy_prefix target_dir decoy_dir output_dir"
	echo
	echo "  Where:"
	echo "    target.fasta: fasta file with target sequences using UniProt headers"
	echo "    decoy.fasta : fasta file with decoy sequences which headers consists ONLY"
	echo "                  in a decoy prefix prepended to the target protein accession"
	echo "    decoy_prefix: prefix used for decoy names"
	echo "    target_dir  : directory with output files using a separated target database search"
	echo "    decoy_dir   : directory with output files using a separated decoy database search"
	echo "    output_dir  : directory for saving the results after executing this workflow"
	echo
	exit 1
fi

export TARGET_FASTA="$1"
export DECOY_FASTA="$2"
export PREFIX="$3"
export TARGET_DATA="$4"
export DECOY_DATA="$5"
RESULTS="$6"
ALL_RESULTS="$RESULTS/all"

if [ ! -f "$TARGET_FASTA" ]; then
	echo "Target fasta file not fount: $TARGET_FASTA"
	exit 1
fi

if [ ! -f "$DECOY_FASTA" ]; then
	echo "Decoy fasta file not found: $DECOY_FASTA"
	exit 1
fi

if [ ! -d "$TARGET_DATA" ]; then
	echo "Target search results directory not found: $TARGET_DATA"
	exit 1
fi

if [ ! -d "$DECOY_DATA" ]; then
	echo "Decoy search results directory not found: $DECOY_DATA"
	exit 1
fi

mkdir -p "$RESULTS"
if [ $? -ne 0 ]; then
	echo "Error creating output directory: $RESULTS"
	exit 1
fi

SCORES="LPM LPS LPF LPGM LPGS LPGF"
FDRS="NORMAL MAYU PICKED REFINED"

for S in $SCORES; do
	for F in $FDRS; do
		echo
		echo "----- $S + $F -----"
		RESULTS="$ALL_RESULTS" SCORE=$S FDR=$F make -f workflow.cfg pep gen
	done
	for R in "$ALL_RESULTS/$S-"*; do
		NAME=`basename $R`
		zcat "$R/FdrGenTarget.tsv.gz" > "$RESULTS/$NAME.tsv"
	done
	cp -f "$ALL_RESULTS/$S-FDRn/LPCorrGenDecoy-log-all.pdf" "$RESULTS/$S.pdf"
done
cp -f "$ALL_RESULTS/"*.txt "$RESULTS"
