# -*- coding: utf-8 -*-

# django.wsgi

'''
Created on 25/06/2012
'''

#==============================================================================
# Change these variables according to server and project
#==============================================================================
_home_folder = '/home/www/'
_web_folder = '/home/www/'
_poject_name = 'hpp'
_venv_libs = '/home/www/hpp/lib/python2.6/site-packages'
#==============================================================================
# WSGI application handler definition, environment configuration and debugging
# options: 
#==============================================================================
import os, sys, site
#
sys.path.insert(1, _web_folder)
sys.path.append(_venv_libs)
sys.path.append(_web_folder + _poject_name)
#
os.environ['DJANGO_SETTINGS_MODULE'] = _poject_name + '.settings'
#os.environ['PYTHON_EGG_CACHE'] = _home_folder + 'tmp'

import django.core.handlers.wsgi
#
application = django.core.handlers.wsgi.WSGIHandler()

