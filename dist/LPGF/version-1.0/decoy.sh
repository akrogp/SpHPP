#!/bin/bash
#
# Sample bash script for creating a decoy database as used in:
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

if [ $# -ne 2 ]; then
	echo
	echo "Usage:"
	echo
	echo "  $0 input.fasta decoy_prefix"
	echo
	echo "  Where:"
	echo "    input.fasta : input fasta file with target sequences using UniProt headers"
	echo "    decoy_prefix: prefix to be used for decoy names"
	echo
	echo "  Outputs:"
	echo "    target.fasta: output target fasta file replacing isoleucines in input.fasta"
	echo "    decoy.fasta : output decoy fasta file generated with decoyPYrat"
	echo
	exit 1
fi

INPUT_FASTA="$1"
PREFIX="$2"
TARGET_FASTA="target.fasta"
DECOY_FASTA="decoy.fasta"
TMP_FASTA="$$.fasta"
LEUNIZER="libs/leunize.py"
STRIP="libs/stripFasta.py"
PYRAT="libs/decoyPYrat.py"


if [ ! -f "$INPUT_FASTA" ]; then
	echo "Input fasta file not fount: $INPUT_FASTA"
	exit 1
fi

echo "Generating $TARGET_FASTA ..."
python "$LEUNIZER" "$INPUT_FASTA" > "$TARGET_FASTA"

echo "Generating $DECOY_FASTA ..."
python "$STRIP" --output_fasta "$TMP_FASTA" "$TARGET_FASTA"
python "$PYRAT" -l 7 -d "$PREFIX" -o "$DECOY_FASTA" "$TMP_FASTA"
rm -f "$TMP_FASTA"
