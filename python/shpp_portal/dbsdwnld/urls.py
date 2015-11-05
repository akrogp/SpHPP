
from django.conf.urls.defaults import patterns, url

# Add the urlpatterns for any custom Django applications here.
# You can also change the ``home`` view to add your own functionality
# to the project's homepage.

urlpatterns = patterns("",
    url("^bases-de-datos/$", "hpp.dbsdwnld.views.table", name="Bases de Datos"),
)
