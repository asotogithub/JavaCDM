package trueffect.truconnect.api.tpasapi.client.base;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.api.json.JSONJAXBContext;
import com.sun.jersey.api.json.JSONUnmarshaller;

import trueffect.truconnect.api.commons.exceptions.ProxyException;
import trueffect.truconnect.api.commons.model.Errors;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.tpasapi.client.base.ContentType;
import trueffect.truconnect.api.tpasapi.client.exceptions.APIException;
import trueffect.truconnect.api.tpasapi.client.exceptions.AccessDeniedException;
import trueffect.truconnect.api.tpasapi.client.exceptions.ConflictException;
import trueffect.truconnect.api.tpasapi.client.exceptions.DataNotFoundForUserException;
import trueffect.truconnect.api.tpasapi.client.exceptions.NotAcceptableException;
import trueffect.truconnect.api.tpasapi.client.exceptions.NotFoundException;
import trueffect.truconnect.api.tpasapi.client.exceptions.NotModifiedException;
import trueffect.truconnect.api.tpasapi.client.exceptions.ServiceUnavailableException;
import trueffect.truconnect.api.tpasapi.client.exceptions.ValidationException;
import trueffect.truconnect.api.tpasapi.model.RecordSet;
import trueffect.truconnect.api.tpasapi.client.proxy.AuthenticationTpasapiServiceProxy;

/**
 * 
 * @author Rambert Rioja
 */
public class ServiceProxyImpl<T> implements ServiceProxy<T> {

    private ObjectMapper mapper;
    private String baseUrl;
    private ContentType contentType = ContentType.XML;
    private ClientResponse lastResponse;
    private Class<T> type;
    private String servicePath;
    protected WebResource webRsc;
    protected AuthenticationTpasapiServiceProxy authenticator;
    protected boolean isRetry;

    public ServiceProxyImpl(Class<T> type, String baseUrl,
            AuthenticationTpasapiServiceProxy authenticator) {
        this(type, baseUrl, "", authenticator);
    }

    public ServiceProxyImpl(Class<T> type, String baseUrl, String servicePath,
            AuthenticationTpasapiServiceProxy authenticator) {
        this.type = type;
        this.baseUrl = baseUrl;
        this.servicePath = servicePath;
        this.authenticator = authenticator;
        this.mapper = new ObjectMapper();
        this.webRsc = makeWebResource();
    }

    public MultivaluedMap<String, String> getResponseHeaders() {
        MultivaluedMap<String, String> headers = this.lastResponse.getHeaders();
        if (headers == null) {
            headers = new MultivaluedHashMap<String, String>();
        }
        return headers;
    }

    private String getServicePath() {
        return this.servicePath;
    }

    public void setServicePath(String servicePath) {
        path(servicePath);
        this.servicePath = servicePath;
    }

    protected String getBaseUrl() {
        return this.baseUrl;
    }

    public void setBaseUrl(String url) {
        this.baseUrl = url;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    public void setContentType(String contentType) {
        if (contentType.equalsIgnoreCase(ContentType.JSON.getType())) {
            this.contentType = ContentType.JSON;
        } else if (contentType.equalsIgnoreCase(ContentType.XML.getType())) {
            this.contentType = ContentType.XML;
        }
    }

    public void setAuthenticator(AuthenticationTpasapiServiceProxy authenticator) {
        this.authenticator = authenticator;
    }

    protected WebResource makeWebResource() {
        ClientConfig config = new DefaultClientConfig();
        config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        config.getClasses().add(JacksonJsonProvider.class);
        Client client = Client.create(config);
        return client.resource(getBaseUrl()).path(getServicePath());
    }

    protected void setLastResponse(ClientResponse response) {
        this.lastResponse = response;
    }

    public ClientResponse getLastResponse() {
        return lastResponse;
    }

    public void handleErrors(ClientResponse response) throws Exception {
        String errorsAsString = "";
        try {
            List<Integer> failCodes= Arrays.asList(304, 400, 403, 404, 406, 409, 500, 503);
            if(response.hasEntity() && failCodes.contains(response.getStatus())){
                errorsAsString = getErrorsAsString(response.getEntity(Errors.class));
            } else if(response.hasEntity()) {
                errorsAsString = response.getEntity(new GenericType<String>(String.class));
            }
        } catch (Exception e) {
            errorsAsString = "Something went wrong recovering the error. Status Code: " + response.getStatus();
        }

        System.out.println("================ Error Output ================");
        System.out.println("Failed : HTTP error code : " + response.getStatus());
        System.out.println("Failed : HTTP error message : " + errorsAsString);
        System.out.println("==============================================");
        throw getExceptionPerStatus(response.getStatus(), errorsAsString);
    }

    private String getErrorsAsString(Errors errors) {
        String res = "";
        if(errors.getErrors() != null && errors.getErrors().size() > 0) {
            List<String> aux = new ArrayList<String>();
            for (trueffect.truconnect.api.commons.model.Error error : errors.getErrors()) {
                aux.add(error.getMessage());
            }
            res = StringUtils.join(aux, "\n");
        }
        return res;
    }

    private Exception getExceptionPerStatus(int status, String errorsAsString) {
        Exception result = null;
        switch (status) {
        case 304: //NOT_MODIFIED
            result = new NotModifiedException(errorsAsString);
        case 400: //BAD_REQUEST
            result = new ValidationException(errorsAsString);
            break;
        case 403: //FORBIDDEN
            result = new AccessDeniedException(errorsAsString);
            break;
        case 404: //NOT_FOUND
            if(errorsAsString.contains("Data not found for User"))
                result = new DataNotFoundForUserException(errorsAsString);
            else
                result = new NotFoundException(errorsAsString);
            break;
        case 406: //NOT_ACCEPTABLE
            result = new NotAcceptableException(errorsAsString);
            break;
        case 409: //CONFLICT
            result = new ConflictException(errorsAsString);
            break;
        case 503: //SERVICE_UNAVAILABLE
            result = new ServiceUnavailableException(errorsAsString);
            break;
        default: //INTERNAL_ERROR, UNHANDLED_EXCEPTION
            result = new APIException(errorsAsString);
        }
        return result;
    }

    public WebResource.Builder header() throws Exception {
        return webRsc.header("Authorization", authenticator.getAccessToken());
    }

    @Override
    public T getById(Object id) throws Exception {
        path(id.toString());
        return get();
    }
    
    public T get() throws Exception {
        ClientResponse response = header().accept(getContentType().getType()).get(ClientResponse.class);
        this.setLastResponse(response);
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleErrors(response);
            } catch (Exception e) {
                if(!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    T result = get();
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        } else { // clear all if the call succeeds
            clearAll();
        }
        return getEntity(response);
    }

    @Override
    public RecordSet<T> find(String query, Long startIndex, Long pageSize) throws Exception {
        if(query != null){
            query("query", query);
        }
        if(startIndex != null) {
            query("startIndex", startIndex.toString());
        }
        if(pageSize != null) {
            query("pageSize", pageSize.toString());
        }
        ClientResponse response = header().accept(getContentType().getType())
                .get(ClientResponse.class);
        this.setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleErrors(response);
            } catch (Exception e) {
                if(!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    RecordSet<T> result = find(query, startIndex, pageSize);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return getEntities(response);
    }

    @Override
    public T save(Object target) throws Exception {
        ClientResponse response = header().type(getContentType().getType())
                .accept(getContentType().getType())
                .post(ClientResponse.class, getPostData(target));
        this.setLastResponse(response);
        if (response.getStatus() != ClientResponse.Status.CREATED.getStatusCode()) {
            try {
                handleErrors(response);
            } catch (Exception e) {
                if(!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    T result = save(target);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        } else { // clear all if the call succeeds
            clearAll();
        }
        return getEntity(response);
    }

    @Override
    public T update(Object target) throws Exception {
        ClientResponse response = header().type(getContentType().getType())
                .accept(getContentType().getType())
                .put(ClientResponse.class, getPostData(target));
        this.setLastResponse(response);
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleErrors(response);
            } catch (Exception e) {
                if(!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    T result = update(target);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        } else { // clear all if the call succeeds
            clearAll();
        }
        return getEntity(response);
    }

    @Override
    public SuccessResponse delete(Object id) throws Exception {
        path(id.toString());
        return this.delete();
    }

    @Override
    public SuccessResponse delete() throws Exception {
        ClientResponse response = header().accept(getContentType().getType())
                .delete(ClientResponse.class);
        this.setLastResponse(response);
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleErrors(response);
            } catch (Exception e) {
                if(!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    SuccessResponse result = delete();
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        } else { // clear all if the call succeeds
            clearAll();
        }
        return getSuccessEntity(response);
    }

    @Override
    public boolean hardDelete(Object id) throws Exception {
        path("UnitTestHelper");
        path("RemoveRowWithSingleKey");
        ClientResponse response = header().type(getContentType().getType())
                .accept(getContentType().getType())
                .post(ClientResponse.class, getPostData(id));
        this.setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleErrors(response);
            } catch (Exception e) {
                if(!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    boolean result = hardDelete(id);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return true;
    }

    protected ObjectMapper getJsonMapper() {
        return this.mapper;
    }

    protected T getEntity(ClientResponse response) throws Exception {
        try {
            if (getContentType().getType().equalsIgnoreCase(MediaType.APPLICATION_JSON)) {
                String output = response.getEntity(String.class);
                JSONJAXBContext jctx = new JSONJAXBContext(this.type);
                JSONUnmarshaller unm = jctx.createJSONUnmarshaller();
                return (T)unm.unmarshalFromJSON(new StringReader(output), this.type);
            }
            return response.getEntity(this.type);
        } catch (Exception e) {
            System.err.println("Error getting entity");
            e.printStackTrace();
            throw e;
        }
        
    }

    @SuppressWarnings("unchecked")
    private RecordSet<T> getEntities(ClientResponse response) throws Exception {
        if (getContentType().getType().equalsIgnoreCase(MediaType.APPLICATION_JSON)) {
            String output = response.getEntity(String.class);
            JSONJAXBContext jctx = new JSONJAXBContext(RecordSet.class);
            JSONUnmarshaller unm = jctx.createJSONUnmarshaller();
            return (RecordSet<T>)unm.unmarshalFromJSON(new StringReader(output), RecordSet.class);
        }
        return response.getEntity(RecordSet.class);
    }

    protected SuccessResponse getSuccessEntity(ClientResponse response) throws ProxyException {
        if (getContentType().getType().equalsIgnoreCase(MediaType.APPLICATION_JSON)) {
            String output = response.getEntity(String.class);
            try {
                JSONJAXBContext jctx = new JSONJAXBContext(SuccessResponse.class);
                JSONUnmarshaller unm = jctx.createJSONUnmarshaller();
                return (SuccessResponse)unm.unmarshalFromJSON(new StringReader(output), SuccessResponse.class);
            } catch (Exception e) {
                throw new ProxyException(e.getMessage());
            }
        }
        return response.getEntity(SuccessResponse.class);
    }

    protected Object getPostData(Object target) throws Exception {
        if (getContentType().getType().equalsIgnoreCase(ContentType.JSON.getType())) {
            return getJsonMapper().writeValueAsString(target);
        }
        return target;
    }

    protected boolean tokenExpired(ClientResponse response, String exceptionMessage) {
        return response.getStatus() == ClientResponse.Status.FORBIDDEN.getStatusCode();
    }

    public void path(String path) {
        webRsc = webRsc.path(path);
    }

    public void query(String key, String val) {
        webRsc = webRsc.queryParam(key, val);
    }

    protected void clearAll(){
        webRsc = makeWebResource();
    }
}
