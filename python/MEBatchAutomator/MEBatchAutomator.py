#!/usr/bin/env python
# -*- coding: utf-8 -*-
#
'''
Created on 2013/04/30

@authors: Óscar Gallardo (ogallard@gmail.com) at LP CSIC/UAB (lp.csic@uab.cat)
'''

#===============================================================================
# Imports
#===============================================================================
import sys
import os
from collections import defaultdict


#===============================================================================
# Functions
#===============================================================================
def get_filepairs(files=None):
    file_pairs = defaultdict(dict)
    
    cwd = os.getcwd()
    if not files: #If no files specified, assume all files in current working directory
        files = os.listdir(cwd)
    
    for i_file in files:
        if not os.path.dirname(i_file): #If no path in filename, assume the current working directory
            i_file = os.path.join(cwd, i_file)
        if os.path.exists(i_file): #Test if filename really exists in the filesystem
            root, ext = os.path.splitext(i_file)
            ext_upper = ext.upper()
            if ext_upper in ('.MGF','.MZID'):
                file_pairs[root][ext_upper] = ext
        
    return file_pairs

def create_batch(batch_name, file_pairs, project, metadata):
    project = 'MIAPE_PROJECT\t{0}\n'.format(project)
    metadata = 'METADATA\t{0}\n'.format(metadata)
    not_processed = list()
    job = 0
    with open(batch_name, 'w') as io_file:
        for root, exts in sorted(file_pairs.items()):
            if len(exts) == 2:
                job += 1
                io_file.write('START_MIAPE_EXTRACTION\t{0}\n'.format(job))
                io_file.write('MZIDENTML\t{0}{1}\n'.format(root, exts['.MZID']))
                io_file.write('MGF\t{0}{1}\n'.format(root, exts['.MGF']))
                io_file.write(project)
                io_file.write(metadata)
                io_file.write('END_MIAPE_EXTRACTION\n\n')
            else:
                not_processed.extend([root+ext for ext in exts.values()])
            
    return job, not_processed


#===============================================================================
# Main program control
#===============================================================================
def main(batch_name, project, metadata, *files):
    file_pairs = get_filepairs(files)
    
    if file_pairs:
        jobs, not_processed = create_batch(batch_name, file_pairs, project, metadata)
        print('\n{0} jobs processed into file {1}\n'.format(jobs, batch_name))
        if not_processed:
            print('\nThe following files were not processed because of not matching file-pair:\n')
            for file_name in not_processed:
                print(' - {0}\n'.format(file_name))
    else:
        print('\nSorry, no file-pairs to process...\n')
    
    return


if __name__ == '__main__':
    if len(sys.argv) > 1:
        main(*sys.argv[1:])
        print('\nEnd of program execution.\n')
    else:
        print('Use:\n./MEBatchAutomator Batch_file_name MIAPE_Project_name MIAPE_Extractor_Metadata_name\n')
