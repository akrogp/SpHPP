#!/bin/bash

trim-isoforms() {
    local ORG="$1"
    local DST="$2"

    echo "Accession,Isoforms" > "$DST"
    for ACC in `grep '^>' "$ORG" | cut -f 2 -d '|' | sed 's/-.*//g'`; do
        if [ `grep -c "^$ACC," "$DST"` -ne 0 ]; then
            continue;
        fi
        echo "$ACC,`grep -c "$ACC" "$ORG"`" >> "$DST"
    done
}

if [ $# -ne 2 ]; then
    echo -e "Usage: $0 <db1.fasta> <db2.fasta>"
    exit 1
fi

DB1="$1"
DB1ID="$1.id"
DB2="$2"
DB2ID="$2.id"
MAP="map.txt"
TMP="$$.txt"

trim-isoforms "$DB1" "$DB1ID"
trim-isoforms "$DB2" "$DB2ID"
cat "$DB1ID" "$DB2ID" | sort > "$TMP"

echo "Accession,$DB1,$DB2" > "$MAP"
for ACC in `cut -f 1 -d ',' "$TMP"`; do
    if [ `grep -c "^$ACC," "$MAP"` -ne 0 ]; then
        continue
    fi
    COUNT1=`grep "^$ACC," "$DB1ID" | cut -f 2 -d ','`
    if [ -z "$COUNT1" ]; then
        COUNT1=0
    fi
    COUNT2=`grep "^$ACC," "$DB2ID" | cut -f 2 -d ','`
    if [ -z "$COUNT2" ]; then
        COUNT2=0
    fi
    echo "$ACC,$COUNT1,$COUNT2" >> "$MAP"
done
rm "$TMP"
