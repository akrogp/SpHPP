#!/bin/bash

for ID in `cat human.ids`; do
    echo -n "$ID,"
    wget -q -O - http://www.uniprot.org/uniprot/$ID.xml | grep 'gene ID' | grep 'ENSG' | cut -d '"' -f 4 | uniq | tr '\n' ','
    echo
done
