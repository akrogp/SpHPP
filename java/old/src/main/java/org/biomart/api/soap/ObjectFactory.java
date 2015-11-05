
package org.biomart.api.soap;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.biomart.api.soap package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _GetLinkables_QNAME = new QName("http://soap.api.biomart.org/", "getLinkables");
    private final static QName _GetContainersResponse_QNAME = new QName("http://soap.api.biomart.org/", "getContainersResponse");
    private final static QName _Config_QNAME = new QName("http://soap.api.biomart.org/", "config");
    private final static QName _GetResultsResponse_QNAME = new QName("http://soap.api.biomart.org/", "getResultsResponse");
    private final static QName _GetContainers_QNAME = new QName("http://soap.api.biomart.org/", "getContainers");
    private final static QName _GetResults_QNAME = new QName("http://soap.api.biomart.org/", "getResults");
    private final static QName _Attribute_QNAME = new QName("http://soap.api.biomart.org/", "attribute");
    private final static QName _GetAttributes_QNAME = new QName("http://soap.api.biomart.org/", "getAttributes");
    private final static QName _Filter_QNAME = new QName("http://soap.api.biomart.org/", "filter");
    private final static QName _GetDatasets_QNAME = new QName("http://soap.api.biomart.org/", "getDatasets");
    private final static QName _GetFilterValues_QNAME = new QName("http://soap.api.biomart.org/", "getFilterValues");
    private final static QName _GetFilters_QNAME = new QName("http://soap.api.biomart.org/", "getFilters");
    private final static QName _GetLinkablesResponse_QNAME = new QName("http://soap.api.biomart.org/", "getLinkablesResponse");
    private final static QName _GetMartsResponse_QNAME = new QName("http://soap.api.biomart.org/", "getMartsResponse");
    private final static QName _Container_QNAME = new QName("http://soap.api.biomart.org/", "container");
    private final static QName _GetDatasetsResponse_QNAME = new QName("http://soap.api.biomart.org/", "getDatasetsResponse");
    private final static QName _GuiContainer_QNAME = new QName("http://soap.api.biomart.org/", "guiContainer");
    private final static QName _Dataset_QNAME = new QName("http://soap.api.biomart.org/", "dataset");
    private final static QName _GetRootGuiContainerResponse_QNAME = new QName("http://soap.api.biomart.org/", "getRootGuiContainerResponse");
    private final static QName _Mart_QNAME = new QName("http://soap.api.biomart.org/", "mart");
    private final static QName _FilterData_QNAME = new QName("http://soap.api.biomart.org/", "filterData");
    private final static QName _GetAttributesResponse_QNAME = new QName("http://soap.api.biomart.org/", "getAttributesResponse");
    private final static QName _GetRootGuiContainer_QNAME = new QName("http://soap.api.biomart.org/", "getRootGuiContainer");
    private final static QName _GetMarts_QNAME = new QName("http://soap.api.biomart.org/", "getMarts");
    private final static QName _GetFilterValuesResponse_QNAME = new QName("http://soap.api.biomart.org/", "getFilterValuesResponse");
    private final static QName _GetFiltersResponse_QNAME = new QName("http://soap.api.biomart.org/", "getFiltersResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.biomart.api.soap
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GuiContainer.GuiContainers }
     * 
     */
    public GuiContainer.GuiContainers createGuiContainerGuiContainers() {
        return new GuiContainer.GuiContainers();
    }

    /**
     * Create an instance of {@link GetFiltersResponse }
     * 
     */
    public GetFiltersResponse createGetFiltersResponse() {
        return new GetFiltersResponse();
    }

    /**
     * Create an instance of {@link GetAttributes }
     * 
     */
    public GetAttributes createGetAttributes() {
        return new GetAttributes();
    }

    /**
     * Create an instance of {@link Filter.Filters }
     * 
     */
    public Filter.Filters createFilterFilters() {
        return new Filter.Filters();
    }

    /**
     * Create an instance of {@link GetFilterValuesResponse }
     * 
     */
    public GetFilterValuesResponse createGetFilterValuesResponse() {
        return new GetFilterValuesResponse();
    }

    /**
     * Create an instance of {@link GetMartsResponse }
     * 
     */
    public GetMartsResponse createGetMartsResponse() {
        return new GetMartsResponse();
    }

    /**
     * Create an instance of {@link GuiContainer.Marts }
     * 
     */
    public GuiContainer.Marts createGuiContainerMarts() {
        return new GuiContainer.Marts();
    }

    /**
     * Create an instance of {@link Attribute }
     * 
     */
    public Attribute createAttribute() {
        return new Attribute();
    }

    /**
     * Create an instance of {@link GetContainersResponse }
     * 
     */
    public GetContainersResponse createGetContainersResponse() {
        return new GetContainersResponse();
    }

    /**
     * Create an instance of {@link GetDatasetsResponse }
     * 
     */
    public GetDatasetsResponse createGetDatasetsResponse() {
        return new GetDatasetsResponse();
    }

    /**
     * Create an instance of {@link Container }
     * 
     */
    public Container createContainer() {
        return new Container();
    }

    /**
     * Create an instance of {@link GetLinkablesResponse }
     * 
     */
    public GetLinkablesResponse createGetLinkablesResponse() {
        return new GetLinkablesResponse();
    }

    /**
     * Create an instance of {@link GetResults }
     * 
     */
    public GetResults createGetResults() {
        return new GetResults();
    }

    /**
     * Create an instance of {@link Filter }
     * 
     */
    public Filter createFilter() {
        return new Filter();
    }

    /**
     * Create an instance of {@link GetRootGuiContainerResponse }
     * 
     */
    public GetRootGuiContainerResponse createGetRootGuiContainerResponse() {
        return new GetRootGuiContainerResponse();
    }

    /**
     * Create an instance of {@link GetLinkables }
     * 
     */
    public GetLinkables createGetLinkables() {
        return new GetLinkables();
    }

    /**
     * Create an instance of {@link GetAttributesResponse }
     * 
     */
    public GetAttributesResponse createGetAttributesResponse() {
        return new GetAttributesResponse();
    }

    /**
     * Create an instance of {@link GetRootGuiContainer }
     * 
     */
    public GetRootGuiContainer createGetRootGuiContainer() {
        return new GetRootGuiContainer();
    }

    /**
     * Create an instance of {@link Container.Containers }
     * 
     */
    public Container.Containers createContainerContainers() {
        return new Container.Containers();
    }

    /**
     * Create an instance of {@link Mart }
     * 
     */
    public Mart createMart() {
        return new Mart();
    }

    /**
     * Create an instance of {@link Container.Attributes }
     * 
     */
    public Container.Attributes createContainerAttributes() {
        return new Container.Attributes();
    }

    /**
     * Create an instance of {@link Filter.Values }
     * 
     */
    public Filter.Values createFilterValues() {
        return new Filter.Values();
    }

    /**
     * Create an instance of {@link FilterData }
     * 
     */
    public FilterData createFilterData() {
        return new FilterData();
    }

    /**
     * Create an instance of {@link GetContainers }
     * 
     */
    public GetContainers createGetContainers() {
        return new GetContainers();
    }

    /**
     * Create an instance of {@link Dataset }
     * 
     */
    public Dataset createDataset() {
        return new Dataset();
    }

    /**
     * Create an instance of {@link Container.Filters }
     * 
     */
    public Container.Filters createContainerFilters() {
        return new Container.Filters();
    }

    /**
     * Create an instance of {@link GetFilters }
     * 
     */
    public GetFilters createGetFilters() {
        return new GetFilters();
    }

    /**
     * Create an instance of {@link GetFilterValues }
     * 
     */
    public GetFilterValues createGetFilterValues() {
        return new GetFilterValues();
    }

    /**
     * Create an instance of {@link Attribute.Attributes }
     * 
     */
    public Attribute.Attributes createAttributeAttributes() {
        return new Attribute.Attributes();
    }

    /**
     * Create an instance of {@link GetResultsResponse }
     * 
     */
    public GetResultsResponse createGetResultsResponse() {
        return new GetResultsResponse();
    }

    /**
     * Create an instance of {@link GetMarts }
     * 
     */
    public GetMarts createGetMarts() {
        return new GetMarts();
    }

    /**
     * Create an instance of {@link GetDatasets }
     * 
     */
    public GetDatasets createGetDatasets() {
        return new GetDatasets();
    }

    /**
     * Create an instance of {@link GuiContainer }
     * 
     */
    public GuiContainer createGuiContainer() {
        return new GuiContainer();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetLinkables }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soap.api.biomart.org/", name = "getLinkables")
    public JAXBElement<GetLinkables> createGetLinkables(GetLinkables value) {
        return new JAXBElement<GetLinkables>(_GetLinkables_QNAME, GetLinkables.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetContainersResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soap.api.biomart.org/", name = "getContainersResponse")
    public JAXBElement<GetContainersResponse> createGetContainersResponse(GetContainersResponse value) {
        return new JAXBElement<GetContainersResponse>(_GetContainersResponse_QNAME, GetContainersResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LiteMartConfiguratorObject }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soap.api.biomart.org/", name = "config")
    public JAXBElement<LiteMartConfiguratorObject> createConfig(LiteMartConfiguratorObject value) {
        return new JAXBElement<LiteMartConfiguratorObject>(_Config_QNAME, LiteMartConfiguratorObject.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetResultsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soap.api.biomart.org/", name = "getResultsResponse")
    public JAXBElement<GetResultsResponse> createGetResultsResponse(GetResultsResponse value) {
        return new JAXBElement<GetResultsResponse>(_GetResultsResponse_QNAME, GetResultsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetContainers }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soap.api.biomart.org/", name = "getContainers")
    public JAXBElement<GetContainers> createGetContainers(GetContainers value) {
        return new JAXBElement<GetContainers>(_GetContainers_QNAME, GetContainers.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetResults }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soap.api.biomart.org/", name = "getResults")
    public JAXBElement<GetResults> createGetResults(GetResults value) {
        return new JAXBElement<GetResults>(_GetResults_QNAME, GetResults.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Attribute }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soap.api.biomart.org/", name = "attribute")
    public JAXBElement<Attribute> createAttribute(Attribute value) {
        return new JAXBElement<Attribute>(_Attribute_QNAME, Attribute.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAttributes }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soap.api.biomart.org/", name = "getAttributes")
    public JAXBElement<GetAttributes> createGetAttributes(GetAttributes value) {
        return new JAXBElement<GetAttributes>(_GetAttributes_QNAME, GetAttributes.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Filter }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soap.api.biomart.org/", name = "filter")
    public JAXBElement<Filter> createFilter(Filter value) {
        return new JAXBElement<Filter>(_Filter_QNAME, Filter.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDatasets }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soap.api.biomart.org/", name = "getDatasets")
    public JAXBElement<GetDatasets> createGetDatasets(GetDatasets value) {
        return new JAXBElement<GetDatasets>(_GetDatasets_QNAME, GetDatasets.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetFilterValues }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soap.api.biomart.org/", name = "getFilterValues")
    public JAXBElement<GetFilterValues> createGetFilterValues(GetFilterValues value) {
        return new JAXBElement<GetFilterValues>(_GetFilterValues_QNAME, GetFilterValues.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetFilters }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soap.api.biomart.org/", name = "getFilters")
    public JAXBElement<GetFilters> createGetFilters(GetFilters value) {
        return new JAXBElement<GetFilters>(_GetFilters_QNAME, GetFilters.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetLinkablesResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soap.api.biomart.org/", name = "getLinkablesResponse")
    public JAXBElement<GetLinkablesResponse> createGetLinkablesResponse(GetLinkablesResponse value) {
        return new JAXBElement<GetLinkablesResponse>(_GetLinkablesResponse_QNAME, GetLinkablesResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMartsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soap.api.biomart.org/", name = "getMartsResponse")
    public JAXBElement<GetMartsResponse> createGetMartsResponse(GetMartsResponse value) {
        return new JAXBElement<GetMartsResponse>(_GetMartsResponse_QNAME, GetMartsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Container }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soap.api.biomart.org/", name = "container")
    public JAXBElement<Container> createContainer(Container value) {
        return new JAXBElement<Container>(_Container_QNAME, Container.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDatasetsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soap.api.biomart.org/", name = "getDatasetsResponse")
    public JAXBElement<GetDatasetsResponse> createGetDatasetsResponse(GetDatasetsResponse value) {
        return new JAXBElement<GetDatasetsResponse>(_GetDatasetsResponse_QNAME, GetDatasetsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GuiContainer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soap.api.biomart.org/", name = "guiContainer")
    public JAXBElement<GuiContainer> createGuiContainer(GuiContainer value) {
        return new JAXBElement<GuiContainer>(_GuiContainer_QNAME, GuiContainer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Dataset }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soap.api.biomart.org/", name = "dataset")
    public JAXBElement<Dataset> createDataset(Dataset value) {
        return new JAXBElement<Dataset>(_Dataset_QNAME, Dataset.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetRootGuiContainerResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soap.api.biomart.org/", name = "getRootGuiContainerResponse")
    public JAXBElement<GetRootGuiContainerResponse> createGetRootGuiContainerResponse(GetRootGuiContainerResponse value) {
        return new JAXBElement<GetRootGuiContainerResponse>(_GetRootGuiContainerResponse_QNAME, GetRootGuiContainerResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Mart }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soap.api.biomart.org/", name = "mart")
    public JAXBElement<Mart> createMart(Mart value) {
        return new JAXBElement<Mart>(_Mart_QNAME, Mart.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FilterData }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soap.api.biomart.org/", name = "filterData")
    public JAXBElement<FilterData> createFilterData(FilterData value) {
        return new JAXBElement<FilterData>(_FilterData_QNAME, FilterData.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAttributesResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soap.api.biomart.org/", name = "getAttributesResponse")
    public JAXBElement<GetAttributesResponse> createGetAttributesResponse(GetAttributesResponse value) {
        return new JAXBElement<GetAttributesResponse>(_GetAttributesResponse_QNAME, GetAttributesResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetRootGuiContainer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soap.api.biomart.org/", name = "getRootGuiContainer")
    public JAXBElement<GetRootGuiContainer> createGetRootGuiContainer(GetRootGuiContainer value) {
        return new JAXBElement<GetRootGuiContainer>(_GetRootGuiContainer_QNAME, GetRootGuiContainer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMarts }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soap.api.biomart.org/", name = "getMarts")
    public JAXBElement<GetMarts> createGetMarts(GetMarts value) {
        return new JAXBElement<GetMarts>(_GetMarts_QNAME, GetMarts.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetFilterValuesResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soap.api.biomart.org/", name = "getFilterValuesResponse")
    public JAXBElement<GetFilterValuesResponse> createGetFilterValuesResponse(GetFilterValuesResponse value) {
        return new JAXBElement<GetFilterValuesResponse>(_GetFilterValuesResponse_QNAME, GetFilterValuesResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetFiltersResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://soap.api.biomart.org/", name = "getFiltersResponse")
    public JAXBElement<GetFiltersResponse> createGetFiltersResponse(GetFiltersResponse value) {
        return new JAXBElement<GetFiltersResponse>(_GetFiltersResponse_QNAME, GetFiltersResponse.class, null, value);
    }

}
