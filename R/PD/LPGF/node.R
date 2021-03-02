#
# Required libraries (install them before executing the script):
# > install.packages(c("rjson","data.table","rlist"))
#

library(rjson)
library(data.table)
library(rlist)

#
# Workflow functions
#

calibrate <- function(target, decoy, score.in, score.out) {
  target <- target[order(-target[score.in]),]
  decoy <- decoy[order(-decoy[score.in]),]
  
  D = nrow(decoy)
  target[,score.out] = 0.5/D
  for( th in decoy[,score.in] ) {
    d = sum(decoy[,score.in] >= th)
    target[target[score.in] <= th, score.out] = (d+0.5)/D
    decoy[decoy[score.in] <= th, score.out] = (d-0.5)/D
  }
  
  return(list(target=target, decoy=decoy))
}

lp <- function(df, score.in, score.out) {
  df[,score.out] = -log10(df[,score.in])
  return(df)
}

uniquer <- function(df) {
  df[df["Number of Proteins"]==1,]
}

lpg <- function(df) {
  accs = unique(df[,"Protein Accessions"])
  prot = data.frame()
  for( acc in accs ) {
    pep = df[df["Protein Accessions"] == acc,]
    pepf = pep[pep[filter.col] <= filter.th,]
    n = nrow(pep)
    m = nrow(pepf)
    
    LPM = max(pep[,score.lp])
    LPS = sum(pep[,score.lp])
    LPF = sum(pepf[,score.lp])
    
    LPGM = -log10( 1 - (1 - 10^(-LPM))^n )
    LPGS = -log10( 1 - pgamma(LPS*log(10),n) )
    if( m == 0 ) {
      LPGF = LPGM
    } else {
      LPGF = -log10( (1 - pgamma(LPF*log(10),m)) * choose(n, m) )
    }
    
    prot[acc, c("LPM", "LPS", "LPF", "LPGM", "LPGS", "LPGF")] = c(LPM, LPS, LPF, LPGM, LPGS, LPGF)
  }
  
  return(prot)
}

competition <- function(target, decoy, score.in) {
  accs = unique(c(rownames(target),rownames(decoy)))
  comp = data.frame()
  for( acc in accs ) {
    score.target = target[acc, score.in]
    score.decoy = decoy[acc, score.in]
    if( is.na(score.decoy) ) {
      comp[acc,c(score.in, "target")] = c(score.target, 1)
    } else if( is.na(score.target) ) {
      comp[acc,c(score.in, "target")] = c(score.decoy, 0)
    } else if( score.target >= score.decoy ) {
      comp[acc,c(score.in, "target")] = c(score.target, 1)
    } else {
      comp[acc,c(score.in, "target")] = c(score.decoy, 0)
    }
  }
  return(comp)
}

picked <- function(comp, df, score.in) {
  df <- df[order(df[score.in]),]
  qval = 1
  for( i in 1:nrow(df) ) {
    th = df[i, score.in]
    t = sum(comp[score.in] >= th & comp$target == 1)
    d = sum(comp[score.in] >= th & comp$target == 0)
    if( t == 0 ) {
      df[i, "FDRp"] = qval
    } else {
      qval = min(qval, d/t)
      df[i, "FDRp"] = qval
    }
  }
  return(df)
}

#
# Parse input json and load data
#

args <- commandArgs(trailingOnly = TRUE)
#input.file <- arg[1]
input.file <- "node_args.json"
input.json <- fromJSON(file=input.file)
input.pep.target <- list.first(input.json$Tables, TableName == "Peptide Groups")$DataFile
input.pep.decoy <- list.first(input.json$Tables, TableName == "Decoy Peptide Groups")$DataFile
input.prot.target <- list.first(input.json$Tables, TableName == "Proteins")$DataFile
input.prot.decoy <- list.first(input.json$Tables, TableName == "Decoy Proteins")$DataFile

pep.target <- fread(input.pep.target, integer64 = "character", header = TRUE, data.table = FALSE)
pep.decoy <- fread(input.pep.decoy, integer64 = "character", header = TRUE, data.table = FALSE)
rel.target <- fread(input.prot.target, integer64 = "character", header = TRUE, data.table = FALSE, select = c("Proteins Unique Sequence ID", "Accession"))
rownames(rel.target) <- rel.target[,2]
rel.decoy <- fread(input.prot.decoy, integer64 = "character", header = TRUE, data.table = FALSE, select = c("Decoy Proteins Unique Sequence ID", "Accession"))
rownames(rel.decoy) <- rel.decoy[,2]

#
# Workflow parameters
#

score.in = if( "SVM_Score" %in% colnames(pep.target) ) "SVM_Score" else "XCorr by Search Engine Sequest HT"
score.pvalue = paste(score.in, " p-value")
score.lp = paste(score.in, " LP")

filter.col = "Qvality q-value"
filter.th = 0.01

#
# Write output json
#

output.template <- '
    {
      "CurrentWorkflowID": $CWFID$,
      "Tables": [
        {
          "TableName": "Peptide Groups",
          "DataFile": "$PATH$/lpgf_target_peptides.txt",
          "DataFormat": "CSV",
          "Options": {},
          "ColumnDescriptions": [
            {
              "ColumnName": "Peptide Groups Peptide Group ID",
              "ID": "ID",
              "DataType": "Int",
              "Options": {}
            },
            {
              "ColumnName": "$PVALUE$",
              "ID": "",
              "DataType": "Float",
              "Options": {}
            },
            {
              "ColumnName": "$LP$",
              "ID": "",
              "DataType": "Float",
              "Options": {}
            }
          ]
        },
        {
          "TableName": "Decoy Peptide Groups",
          "DataFile": "$PATH$/lpgf_decoy_peptides.txt",
          "DataFormat": "CSV",
          "Options": {},
          "ColumnDescriptions": [
            {
              "ColumnName": "Decoy Peptide Groups Peptide Group ID",
              "ID": "ID",
              "DataType": "Int",
              "Options": {}
            },
            {
              "ColumnName": "$PVALUE$",
              "ID": "",
              "DataType": "Float",
              "Options": {}
            },
            {
              "ColumnName": "$LP$",
              "ID": "",
              "DataType": "Float",
              "Options": {}
            }
          ]
        },
        {
          "TableName": "Proteins",
          "DataFile": "$PATH$/lpgf_target_proteins.txt",
          "DataFormat": "CSV",
          "Options": {},
          "ColumnDescriptions": [
            {
              "ColumnName": "Proteins Unique Sequence ID",
              "ID": "ID",
              "DataType": "Long",
              "Options": {}
            },
            {
              "ColumnName": "LPM",
              "ID": "",
              "DataType": "Float",
              "Options": {}
            },
            {
              "ColumnName": "LPS",
              "ID": "",
              "DataType": "Float",
              "Options": {}
            },
            {
              "ColumnName": "LPF",
              "ID": "",
              "DataType": "Float",
              "Options": {}
            },
            {
              "ColumnName": "LPGM",
              "ID": "",
              "DataType": "Float",
              "Options": {}
            },
            {
              "ColumnName": "LPGS",
              "ID": "",
              "DataType": "Float",
              "Options": {}
            },
            {
              "ColumnName": "LPGF",
              "ID": "",
              "DataType": "Float",
              "Options": {}
            },
            {
              "ColumnName": "FDRp",
              "ID": "",
              "DataType": "Float",
              "Options": {}
            }
          ]
        },
        {
          "TableName": "Decoy Proteins",
          "DataFile": "$PATH$/lpgf_decoy_proteins.txt",
          "DataFormat": "CSV",
          "Options": {},
          "ColumnDescriptions": [
            {
              "ColumnName": "Decoy Proteins Unique Sequence ID",
              "ID": "ID",
              "DataType": "Long",
              "Options": {}
            },
            {
              "ColumnName": "LPM",
              "ID": "",
              "DataType": "Float",
              "Options": {}
            },
            {
              "ColumnName": "LPS",
              "ID": "",
              "DataType": "Float",
              "Options": {}
            },
            {
              "ColumnName": "LPF",
              "ID": "",
              "DataType": "Float",
              "Options": {}
            },
            {
              "ColumnName": "LPGM",
              "ID": "",
              "DataType": "Float",
              "Options": {}
            },
            {
              "ColumnName": "LPGS",
              "ID": "",
              "DataType": "Float",
              "Options": {}
            },
            {
              "ColumnName": "LPGF",
              "ID": "",
              "DataType": "Float",
              "Options": {}
            },
            {
              "ColumnName": "FDRp",
              "ID": "",
              "DataType": "Float",
              "Options": {}
            }
          ]
        }
      ]
    }
'
output.file <- input.json$ExpectedResponsePath
output.path <- dirname(output.file)
output.json <- sub('\\$CWFID\\$', input.json$CurrentWorkflowID, output.template)
output.json <- gsub("\\$PATH\\$", output.path, output.json)
output.json <- gsub("\\$PVALUE\\$", score.pvalue, output.json)
output.json <- gsub("\\$LP\\$", score.lp, output.json)
output.conn <- file(output.file)
writeLines(output.json, output.conn)
close(output.conn)
output.json <- fromJSON(file=output.file)

#
# Workflow execution
#

cal = calibrate(pep.target, pep.decoy, score.in = score.in, score.out = score.pvalue)
pep.target = uniquer(lp(cal$target, score.pvalue, score.lp))
pep.decoy = uniquer(lp(cal$decoy, score.pvalue, score.lp))

prot.target = lpg(pep.target)
prot.decoy = lpg(pep.decoy)

prot.comp = competition(prot.target, prot.decoy, "LPGF")
prot.target = picked(prot.comp, prot.target, "LPGF")
prot.decoy = picked(prot.comp, prot.decoy, "LPGF")

prot.target$Accession <- rownames(prot.target)
prot.target[colnames(rel.target)[1]] <- rel.target[prot.target$Accession,1]
prot.decoy$Accession <- rownames(prot.decoy)
prot.decoy[colnames(rel.decoy)[1]] <- rel.decoy[prot.decoy$Accession,1]

write.table(pep.target, file = output.json$Tables[[1]]$DataFile, sep='\t', row.names=FALSE)
write.table(pep.decoy, file = output.json$Tables[[2]]$DataFile, sep='\t', row.names=FALSE)
write.table(prot.target, file = output.json$Tables[[3]]$DataFile, sep='\t', row.names=FALSE)
write.table(prot.decoy, file = output.json$Tables[[4]]$DataFile, sep='\t', row.names=FALSE)