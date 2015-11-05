#!/bin/bash

if [ $# -ne 1 ]; then
    echo "Usage: $0 </path/to/HUMAN.fasta.gz>"
    exit
fi

zgrep '^>' "$1" | cut -f 2 -d '|'
