package trueffect.truconnect.api.commons.service.proxy;

import trueffect.truconnect.api.commons.exceptions.ProxyException;
import trueffect.truconnect.api.commons.model.Errors;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.SuccessResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.LoggingFilter;

import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;



/**
 *
 * @author Rambert Rioja
 */
public abstract class ServiceProxyImpl<T> implements ServiceProxy<T> {

    protected ObjectMapper mapper;
    protected String contetType = MediaType.TEXT_XML;
    protected ClientResponse lastResponse;
    protected Class<T> type;
    protected WebResource webRsc;
    protected HttpHeaders headers;
    private static final Logger HTTP_CLIENT_LOGGER = Logger.getLogger(ServiceProxyImpl.class.getName());

    public ServiceProxyImpl(Class<T> type, HttpHeaders headers) {
        this.type = type;
        this.headers = headers;
        this.webRsc = makeWebResource();
    }

    private WebResource makeWebResource() {
        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        // Use this log only for debugging purposes
        client.addFilter(new LoggingFilter(HTTP_CLIENT_LOGGER));
        String baseUrl = getBaseUrl();
        return client.resource(baseUrl);
    }

    public abstract String getBaseUrl();

    @Override
    public T get() throws ProxyException {
        WebResource.Builder builder = header().accept(getContentType());
        ClientResponse response = builder.get(ClientResponse.class);
        this.setLastResponse(response);
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            handleErrors(response);
        }
        return getEntity(response);
    }

    @Override
    public RecordSet<T> get(SearchCriteria criteria) throws ProxyException {
        query("query", criteria.getQuery());
        query("pageSize", criteria.getPageSize().toString());
        query("startIndex", criteria.getStartIndex().toString());
        WebResource.Builder builder = header().accept(getContentType());
        ClientResponse response = builder.get(ClientResponse.class);
        this.setLastResponse(response);
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            handleErrors(response);
        }
        return getEntities(response);
    }

    public T post(T t) throws ProxyException {
        ClientResponse response = header().type(getContentType()).accept(getContentType())
                .post(ClientResponse.class, getPostData(t));
        this.setLastResponse(response);
        if (response.getStatus() != ClientResponse.Status.CREATED.getStatusCode()) {
            handleErrors(response);
        }
        return getEntity(response);
    }

    public T put(T t) throws ProxyException {
        ClientResponse response = header().type(getContentType()).accept(getContentType())
                .put(ClientResponse.class, getPostData(t));
        this.setLastResponse(response);
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            handleErrors(response);
        }
        return getEntity(response);
    }

    public SuccessResponse delete() throws ProxyException {
        WebResource.Builder builder = header().accept(getContentType());
        ClientResponse response = builder.delete(ClientResponse.class);
        this.setLastResponse(response);
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            handleErrors(response);
        }
        return getSuccessEntity(response);
    }

    protected void setLastResponse(ClientResponse response) {
        this.lastResponse = response;
    }

    public ClientResponse getLastResponse() {
        return this.lastResponse;
    }

    public void setContentType(String contentType) {
        this.contetType = contentType;
    }

    public String getContentType() {
        return this.contetType;
    }

    protected void handleErrors(ClientResponse response) throws ProxyException {
        Errors errors = response.getEntity(Errors.class);
        System.out.println("================ Error Output ================");
        System.out.println("Failed : HTTP error code : " + response.getStatus());
        System.out.println("Failed : HTTP error message : " + errors);
        System.out.println("==============================================");
        throw new ProxyException(errors, response);
    }

    protected ObjectMapper getJsonMapper() {
        if (this.mapper == null) {
            this.mapper = new ObjectMapper();
            return this.mapper;
        }
        return this.mapper;
    }

    private Object getPostData(Object target) throws ProxyException {
        if (getContentType().equalsIgnoreCase(MediaType.APPLICATION_JSON)) {
            try {
                return getJsonMapper().writeValueAsString(target);
            } catch (Exception e) {
                throw new ProxyException(e.getMessage());
            }
        }
        return target;
    }

    protected Class<T> getEntityType() {
        return this.type;
    }

    protected T getEntity(ClientResponse response) throws ProxyException {
        if (getContentType().equalsIgnoreCase(MediaType.APPLICATION_JSON)) {
            String output = response.getEntity(String.class);
            try {
                return getJsonMapper().readValue(output, getEntityType());
            } catch (Exception e) {
                throw new ProxyException(e.getMessage());
            }
        }
        return response.getEntity(getEntityType());
    }

    @SuppressWarnings("unchecked")
    protected RecordSet<T> getEntities(ClientResponse response) throws ProxyException {
        if (getContentType().equalsIgnoreCase(MediaType.APPLICATION_JSON)) {
            String output = response.getEntity(String.class);
            try {
                return getJsonMapper().readValue(output, RecordSet.class);
            } catch (Exception e) {
                throw new ProxyException(e.getMessage());
            }
        }
        return response.getEntity(RecordSet.class);
    }

    protected SuccessResponse getSuccessEntity(ClientResponse response) throws ProxyException {
        if (getContentType().equalsIgnoreCase(MediaType.APPLICATION_JSON)) {
            String output = response.getEntity(String.class);
            try {
                return getJsonMapper().readValue(output, SuccessResponse.class);
            } catch (Exception e) {
                throw new ProxyException(e.getMessage());
            }
        }
        return response.getEntity(SuccessResponse.class);
    }

    public void path(String path) {
        webRsc = webRsc.path(path);
    }

    public void query(String key, String val) {
        webRsc = webRsc.queryParam(key, val);
    }

    public WebResource.Builder header() {
        WebResource.Builder builder = webRsc.getRequestBuilder();
        try {
            MultivaluedMap<String, String> map = headers.getRequestHeaders();
            if (map != null) {
                for (String key : map.keySet()) {
                    String value = map.getFirst(key);
                    builder = builder.header(key, value);
                }
            }
        } catch (Exception e) {
        }
        return builder;
    }

    public void copyHeaders(ResponseBuilder builder) {
        try {
            String contentLenght = "";
            for (Entry<String, List<String>> entry : getLastResponse().getHeaders().entrySet()) {
                for (String value : entry.getValue()) {
                    if (!"Content-Length".equalsIgnoreCase(entry.getKey())) {
                        builder.header(entry.getKey(), value);
                    } else {
                        contentLenght = value;
                    }
                }
            }
            if (getLastResponse().getHeaders().containsKey("Content-Disposition")) {
                builder.header("Content-Length", contentLenght);
            }
        } catch (Exception e) {
        }
    }
    
    public void reset() {
        this.webRsc = makeWebResource();
    }
}
