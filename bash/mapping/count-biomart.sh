#!/bin/bash

IFS='
'

COUNT=0
MAPPED=0
for A in `cat ~/MyProjects/S-CHPP/java/downloads/human.ids`; do
    A=`echo "$A" | cut -f 1 -d '-'`
    echo "$A ..."
    if [ `grep -c "$A" mart_export.txt` -ne 0 ]; then
        MAPPED=$((MAPPED+1))
    fi
    COUNT=$((COUNT+1))
done

echo "$MAPPED/$COUNT mapped to ENSG"
