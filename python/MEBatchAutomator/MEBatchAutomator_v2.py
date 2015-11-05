#!/usr/bin/env python
# -*- coding: utf-8 -*-
#
"""
:synopsis:   Program to automate the creation of MIAPE Extractor Batch 
             extraction configuration files

:created:    2013-04-30

:author:     Óscar Gallardo (ogallard@gmail.com) at LP-CSIC/UAB (lp.csic@uab.cat)
:copyright:  2013-2014 LP-CSIC/UAB (http://proteomica.uab.cat). Some rights reserved.
:license:    GPLv3 (http://www.gnu.org/licenses/gpl-3.0.html)

:contact:    lp.csic@uab.cat

:version:    1.2
:updated:    2014-05-28
"""

#===============================================================================
# Imports and Global variables
#===============================================================================
import sys
import os
from collections import defaultdict

# Check for launching the easygui interface or the console one:
if os.name == 'posix' and not os.environ.get('DISPLAY', None):
    GUI = False
else:
    try:
        import easygui
        GUI = True
    except ImportError:
        GUI = False

FIX_EXTS = ('.MGF', '.MZID', '.RAW')
SEARCH_EXTS = {'.MSF', '.DAT'}
ALLOWED_EXTS = FIX_EXTS + tuple(SEARCH_EXTS)


#===============================================================================
# Class definitions
#===============================================================================
class ProgramCancelled(Exception):
    """
    Generic sub-class of :class:``Exception`` to raise when program must be 
    cancelled by an user action.
    """
    def __init__(self, msg=None):
        super(ProgramCancelled, self).__init__()
        self._msg = 'Program Cancelled by user'
        if msg:
            self._msg = '{0}: {1}'.format(self._msg, msg)
    
    @property
    def msg(self):
        return self._msg
    
    def __str__(self):
        return self.msg
    
    def __unicode__(self):
        return self.msg


#===============================================================================
# Functions
#===============================================================================
def get_filepairs(folder = None):
    file_pairs = defaultdict(dict)
    
    files = os.listdir(folder) #Assume all files in current working directory
    
    for i_file in files:
        if not os.path.dirname(i_file): #If no path in filename, append the working folder
            i_file = os.path.join(folder, i_file)
        if os.path.exists(i_file): #Test if filename really exists in the filesystem
            root, ext = os.path.splitext(i_file)
            ext_upper = ext.upper()
            if ext_upper in ALLOWED_EXTS:
                file_pairs[root][ext_upper] = ext
        
    return file_pairs

def create_batch(batch_name, file_pairs, project, metadata):
    project = 'MIAPE_PROJECT\t{0}\n'.format(project)
    metadata = 'METADATA\t{0}\n'.format(metadata)
    not_processed = list()
    job = 0
    with open(batch_name, 'w') as io_file:
        for root, exts in sorted(file_pairs.items()):
            if len(exts) >= 2:
                job += 1
                io_file.write('START_MIAPE_EXTRACTION\t{0}\n'.format(job))
                io_file.write('MZIDENTML\t{0}{1}\n'.format(root, exts['.MZID']))
                io_file.write('MGF\t{0}{1}\n'.format(root, exts['.MGF']))
                io_file.write(project)
                io_file.write(metadata)
                io_file.write('RAW\t{0}{1}\n'.format(root, exts['.RAW']))
                search_exts = SEARCH_EXTS.intersection(exts)
                for search_ext in search_exts:
                    io_file.write('SEARCH\t{0}{1}\n'.format(root, 
                                                            exts[search_ext]))
                io_file.write('END_MIAPE_EXTRACTION\n\n')
            else:
                not_processed.extend( [root+ext for ext in exts.values()] )
            
    return job, not_processed

def console_output(text, title = None):
    if title:
        text = '{0} - {1}'.format(title, text)
    
    print('{0}\n'.format(text))
    
    return

def console_input(text = '', title = None, default = None):
    if title:
        text = '{0} - {1}'.format(title, text)
    if default:
        text = '{0} [{1}]'.format(text, default)
    
    user_input = raw_input('{0} :'.format(text)) or default
    
    return user_input
    

#===============================================================================
# Main program control
#===============================================================================
def main(batch_name = None, project = None, metadata = None, folder = None):
    msg_title = 'Configuration'
    try:
        if not folder: #If no folder specified
            def_folder = os.getcwd() #Assume current working directory
            msg_text = 'Please, indicate the folder containing the file-pairs to upload'
            if GUI:
                folder = easygui.diropenbox(msg_text, msg_title, def_folder)
            else:
                folder = console_input(msg_text, msg_title, def_folder)
        if not folder:
            raise ProgramCancelled(folder)
        if not os.path.exists(folder):        
            raise IOError('Folder {0} does NOT exist!'.format(folder))
        
        if not project:
            project = inputbox('Please, give a project name to upload to', msg_title)
        if not project:
            raise ProgramCancelled(project)
            
        if not metadata:
            metadata = inputbox('Please, give a metadata template name to use', msg_title)
        if not metadata:
            raise ProgramCancelled(metadata)
        
        if not batch_name:
            os.chdir(folder) #Fix Windows error with passing a folder+file as default to filesavebox
            def_file = project + '.txt'
            def_name = os.path.join(folder, def_file) #Default name
            msg_text = 'Save the resulting batch-file as'
            if GUI:
                batch_name = easygui.filesavebox(msg_text, msg_title, def_file, ['*.txt', '*.*'])
            else:
                batch_name = console_input(msg_text, msg_title, def_name)
        if not batch_name:
            raise ProgramCancelled(batch_name)
        
        file_pairs = get_filepairs(folder)
        
        if file_pairs:
            jobs, not_processed = create_batch(batch_name, file_pairs, project, metadata)
            msg_text = '{0} file-pairs processed into file {1}'.format(jobs, batch_name)
            if not_processed:
                msg_text += '\n\nThe following files were not processed because of no matching file-pair:\n'
                for file_name in not_processed:
                    msg_text += ' - {0}\n'.format(file_name)
        else:
            msg_text = 'Sorry, no file-pairs to process in folder\n{0}...'.format(folder)
        
        msgbox(msg_text, 'Information')
        
        return 0
    
    except (KeyboardInterrupt, ProgramCancelled) as error:
        ### Handle keyboard interrupt, and program cancellation
        msgbox('Program Cancelled by user!\n\n[ {0} ]'.format(error.msg), 
               'Cancelled!')
        return 1
    
    except IOError as error:
        ### Handle keyboard interrupt, and program cancellation
        msgbox(error.message, 'Input/Output Error!')
        return 1
    
    except SystemExit:
        ### Handle system exit (for example when -h)
        return 0        


#===============================================================================
# Start script execution from CLI
#===============================================================================
if __name__ == '__main__':
    '''
    You can also use the following syntax from the command line:
    MEBatchAutomator Batch_file_name MIAPE_Project_name MIAPE_Extractor_Metadata_name File-Pairs_folder_name
    '''
    if GUI:
        msgbox = easygui.msgbox
        inputbox = easygui.enterbox
    else:
        msgbox = console_output
        inputbox = console_input
        if len(sys.argv) < 4:
            print('''
                  You can also use the following syntax from the command line:
                  MEBatchAutomator Batch_file_name MIAPE_Project_name MIAPE_Extractor_Metadata_name File-Pairs_folder_name
                  
                  ''')
        
    main(*sys.argv[1:])

