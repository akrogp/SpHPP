#!/bin/bash

DBDIR=/media/data/Sequences
TOBASE=/home/www/hpp
LINK=downloads
TODIR="${LINK}_`date +%F`"
TARGET=uniprot_sprot_human.fasta
INI=db_metadata.ini
TOTAL=uniprot_sprot_human_target_decoy.fasta
SPHPP=./sphpp.sh
DECOY=decoy.pl

if [ ! -d "$DBDIR" ]; then
	echo "Databases base directory not found"
	exit
fi

if [ ! -d "$TOBASE" ]; then
	echo -n "$TOBASE does not exist, use current directory? (y/n) "
	read OPT
	if [ "$OPT" != "y" ]; then
		exit
	fi
	TOBASE=`pwd`
fi
LINK="$TOBASE/$LINK"

if [ -f "$LINK" ]; then
	echo "$LINK is not a symbolic link"
	exit
fi

TODIR="$TOBASE/$TODIR"
if [ -d "$TODIR" ]; then
	echo -n "$TODIR already exists, remove? (y/n) "
	read OPT
	if [ "$OPT" != "y" ]; then
		exit
	fi
	rm -rf "$TODIR"
fi
mkdir -p "$TODIR"

if [ ! -f "$SPHPP" ]; then
	echo "SpHpp software not found"
	exit
fi
$SPHPP FastaDb "$DBDIR/UniProt/current" "$DBDIR/neXtProt/current"
if [ ! -f "$TARGET" -o ! -f "$INI" ]; then
	echo "Fasta creation failed!"
	exit
fi
mv "$TARGET" "$INI" "$TODIR"
TARGET="$TODIR/$TARGET"
INI="$TODIR/$INI"

echo "Generating decoys ..."
$DECOY 2 "$TARGET" "$TOTAL" 1 sp
TOTAL="$TODIR/$TOTAL"

echo "GZipping files"
gzip "$TARGET"
gzip "$TOTAL"

echo -n "Update link? (y/n) "
read OPT
if [ "$OPT" = "y" ]; then
	rm -f "$LINK"
	ln -s "$TODIR" "$LINK"
fi
echo "Finished!!"
