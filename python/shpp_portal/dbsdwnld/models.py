#from django.db import models
#
#class DataBase(models.Model):
#    rel = models.CharField(max_length=35, default='')
#    version = models.CharField(max_length=35, default='')
#    fasta_name = models.CharField(max_length=100, default='')
#
#    class Meta:
#        abstract = True
#
#class NextProt(DataBase):
#    all = models.IntegerField(default=0)
#    chr16 = models.IntegerField(default=0)
#
#    @property
#    def allx2(self):
#        return self.all*2
#
#    @property
#    def chr16x2(self):
#        return self.chr16*2
#
#
#class UniProt(DataBase):
#    sprot_all = models.IntegerField()
#    sprot_chr16 = models.IntegerField()
#    trembl_all = models.IntegerField()
#    trembl_chr16 = models.IntegerField()
#
#    @property
#    def sprot_allx2(self):
#        return self.sprot_all*2
#
#    @property
#    def sprot_chr16x2(self):
#        return self.sprot_chr16*2
#
#    @property
#    def trembl_allx2(self):
#        return self.trembl_all*2
#
#    @property
#    def trembl_chr16x2(self):
#        return self.trembl_chr16*2
