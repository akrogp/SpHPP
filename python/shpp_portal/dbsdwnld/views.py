from django.shortcuts import render
from django.conf import settings
import os
import ConfigParser


def get_db_metadata(dbname=''):
    if dbname:
        with open(dbname, 'r') as io_file:
            metadata = ConfigParser.RawConfigParser(dict_type=dict)
            metadata.readfp(io_file)
        nextprot = metadata._sections['nextprot']
        uniprot = metadata._sections['uniprot']
        for db_meta_dict in (nextprot, uniprot):
            keys = db_meta_dict.keys()
            keys = [each for each in keys
                    if each not in ('__name__', 'rel', 'ver', 'info')]
            for key in keys:
                value = int(db_meta_dict[key])
                keyx2 = '{0}x2'.format(key)
                valuex2 = value * 2
                db_meta_dict[keyx2] = str(valuex2)
        return nextprot, uniprot
    else:
        return None, None


def table(request, *args, **kwargs):
    template = 'dbsdwnld_table.html'
    metadata_file = os.path.join(settings.DOWNLOADS_ROOT, 'db_metadata.ini')
    nextprot, uniprot = get_db_metadata(metadata_file)
    return render(request,
                  template,
                  {'nextprot': nextprot, 'uniprot': uniprot},
                  )


