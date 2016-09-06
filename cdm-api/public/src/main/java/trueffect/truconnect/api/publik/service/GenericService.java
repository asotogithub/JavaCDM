package trueffect.truconnect.api.publik.service;

import trueffect.truconnect.api.commons.exceptions.AccessDeniedException;
import trueffect.truconnect.api.commons.exceptions.business.AccessError;
import trueffect.truconnect.api.commons.exceptions.business.enums.BusinessCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.SecurityCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.ValidationCode;
import trueffect.truconnect.api.commons.model.Error;
import trueffect.truconnect.api.commons.model.Errors;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.enums.AccessPermission;
import trueffect.truconnect.api.commons.service.BasicService;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.commons.validation.ApiValidationUtils;
import trueffect.truconnect.api.commons.validation.BusinessValidationUtil;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.UserDao;
import trueffect.truconnect.api.crud.dao.impl.AccessControlImpl;
import trueffect.truconnect.api.crud.dao.impl.UserDaoImpl;
import trueffect.truconnect.api.crud.mybatis.PersistenceContext;
import trueffect.truconnect.api.crud.mybatis.PersistenceContextMyBatis;
import trueffect.truconnect.api.crud.service.UserManager;
import trueffect.truconnect.api.resources.ResourceBundleUtil;
import trueffect.truconnect.api.resources.ResourceUtil;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

/**
 *
 * @author Rambert Rioja
 */
public class GenericService extends BasicService {

    private OauthKey currentUserId;
    protected Logger log;
    protected UserManager userManager;

    public GenericService() {
        super();
        PersistenceContext context = new PersistenceContextMyBatis();
        AccessControl accessControl = new AccessControlImpl(context);
        UserDao userDao = new UserDaoImpl(context, accessControl);
        userManager = new UserManager(userDao, accessControl);
        log = LoggerFactory.getLogger(this.getClass());
    }

    protected Response handleErrorCodes(trueffect.truconnect.api.commons.exceptions.business.Error error) {
        Response.ResponseBuilder response = getResponseBuilder(error);
        trueffect.truconnect.api.commons.exceptions.business.Errors errors = new trueffect.truconnect.api.commons.exceptions.business.Errors();
        errors.addError(error);
        return response.entity(errors).build();
    }

    protected Response handleErrorCodes(trueffect.truconnect.api.commons.exceptions.business.Errors errors) {
        if(errors == null || errors.getErrors() == null || errors.getErrors().isEmpty()) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "errors"));
        }
        Response.ResponseBuilder response = getResponseBuilder(errors.getErrors().iterator().next());
        return response.entity(errors).build();
    }

    protected Response handleErrorCodesAsLegacyErrors(trueffect.truconnect.api.commons.exceptions.business.Errors errors) {
        if(errors == null || errors.getErrors() == null || errors.getErrors().isEmpty()) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "errors"));
        }
        Response.ResponseBuilder response = getResponseBuilder(errors.getErrors().iterator().next());
        return response.entity(ApiValidationUtils.parseToLegacyErrors(errors)).build();
    }

    private Response.ResponseBuilder getResponseBuilder(trueffect.truconnect.api.commons.exceptions.business.Error error) {
        Response.Status status = null;
        if (error.getCode() instanceof ValidationCode) {
            ValidationCode code = (ValidationCode) error.getCode();
            switch (code) {
                case REQUIRED:
                    status = Response.Status.BAD_REQUEST;
                    break;
                case INVALID:
                    status = Response.Status.BAD_REQUEST;
                    break;
                default:
                    status = Response.Status.BAD_REQUEST;
                    break;
            }
        } else if (error.getCode() instanceof SecurityCode) {
            SecurityCode code = (SecurityCode) error.getCode();
            switch (code) {
                case PERMISSION_DENIED:
                    status = Response.Status.FORBIDDEN;
                    break;
                case NOT_FOUND_FOR_USER:
                    status = Response.Status.NOT_FOUND;
                    break;
                case INVALID_TOKEN:
                    status = Response.Status.UNAUTHORIZED;
                    break;
                default:
                    status = Response.Status.FORBIDDEN;
                    break;
            }
        } else if (error.getCode() instanceof BusinessCode) {
            BusinessCode code = (BusinessCode) error.getCode();
            switch (code) {
                case INTERNAL_ERROR:
                    status = Response.Status.INTERNAL_SERVER_ERROR;
                    break;
                case NOT_FOUND:
                    status = Response.Status.NOT_FOUND;
                    break;
                case INVALID:
                    status = Response.Status.BAD_REQUEST;
                    break;
                default:
                    status = Response.Status.INTERNAL_SERVER_ERROR;
                    break;
            }
        }

        Response.ResponseBuilder responseBuilder = Response.status(status);
        if(error  instanceof AccessError) {
            AccessError accessError = (AccessError) error;
            if(accessError.getWwwAuthenticate() != null) {
                // TODO, whenever it is needed, instead of a single methdod
                // we can have a Map of Headers to be added in the final response
                responseBuilder.header("WWW-Authenticate", accessError.getWwwAuthenticate());
            }
        }
        return responseBuilder;
    }

    protected Either<trueffect.truconnect.api.commons.exceptions.business.Errors, Boolean>
        permissionsValid(AccessPermission... permissions) {

        trueffect.truconnect.api.commons.exceptions.business.Errors errors =
                new trueffect.truconnect.api.commons.exceptions.business.Errors();

        if (permissions.length > 0) {
            //1. Get User's permission
            List<String> userPermissions;
            try {
                userPermissions =
                        userManager.getPermissionsByUser(currentUserId.getUserId(), currentUserId);
            } catch (Exception e) {
                String message = ResourceBundleUtil.getString("SecurityCode.ERROR_GETTING_PERMISSIONS");
                AccessError error =
                        new AccessError(message, SecurityCode.ERROR_GETTING_PERMISSIONS, currentUserId.getUserId());
                errors.addError(error);
                return Either.error(errors);
            }

            //2. Check if at least one of the permissions is in the User's permissions list
            //   If not throw 403 exception
            boolean permissionAvailable = false;
            if (userPermissions != null && userPermissions.size() > 0) {
                for (AccessPermission permission : permissions) {
                    for (String userPermission : userPermissions) {
                        try {
                            AccessPermission userPermissionKey = AccessPermission.valueOf(userPermission);
                            if (userPermissionKey == permission) {
                                permissionAvailable = true;
                                break;
                            }
                        } catch (IllegalArgumentException e) {
                            // do nothing.  it is ok to have a permission in the database that does not map to an enum.
                        }
                    }
                }
            }

            if (!permissionAvailable) { // User does not have valid permission
                String message = ResourceBundleUtil.getString("SecurityCode.PERMISSION_DENIED");
                AccessError error =
                        new AccessError(message, SecurityCode.PERMISSION_DENIED, currentUserId.getUserId());
                errors.addError(error);
                return Either.error(errors);
            } else {
                return Either.success(Boolean.TRUE);
            }
        } else {
            AccessError error =
                    new AccessError("err", SecurityCode.PERMISSION_DENIED, currentUserId.getUserId());
            errors.addError(error);
            return Either.error(errors);
        }
    }

    protected void checkPermissions(AccessPermission... permissions) {
        Either<trueffect.truconnect.api.commons.exceptions.business.Errors, Boolean> result =
                permissionsValid(permissions);
        if (result.isError()) {
            trueffect.truconnect.api.commons.exceptions.business.AccessError error =
                    (AccessError) result.error().getErrors().iterator().next();
            throw BusinessValidationUtil
                        .buildAccessSystemException(null, error.getCode(), error.getUser());
        }
    }

    protected Either<trueffect.truconnect.api.commons.exceptions.business.Errors, Boolean> checkToken() {
        disableSSLPolicy();
        String accessToken = getAccessToken();
        trueffect.truconnect.api.commons.exceptions.business.Errors errors =
                new trueffect.truconnect.api.commons.exceptions.business.Errors();
        if (StringUtils.isNotBlank(accessToken)) {
            Client client = Client.create();
            String authorizationUri = ResourceUtil.get("oauth.url");
            WebResource
                    webResource =
                    client.resource(authorizationUri).path("checktoken");
            response = webResource.type(MediaType.APPLICATION_JSON)
                    .header("Authorization", accessToken).post(ClientResponse.class);
            if (Response.Status.fromStatusCode(response.getStatus()) == Response.Status.OK) {
                OauthKey userKey = response.getEntity(OauthKey.class);
                setOauthKey(userKey);
            } else {
                String wwwAuthenticate = null;
                if (response.getHeaders() != null && response.getHeaders().getFirst("WWW-Authenticate") != null) {
                    wwwAuthenticate = response.getHeaders().getFirst("WWW-Authenticate");
                }

                String msg = ResourceBundleUtil.getString("SecurityCode.INVALID_TOKEN");
                AccessError accessError = new AccessError(msg, SecurityCode.INVALID_TOKEN);
                accessError.setOauthErrorCode("invalid_token");
                accessError.setWwwAuthenticate(wwwAuthenticate);
                errors.addError(accessError);
                return Either.error(errors);
            }
        } else {
            String msg = ResourceBundleUtil.getString("SecurityCode.INVALID_REQUEST");
            AccessError accessError = new AccessError(msg, SecurityCode.INVALID_REQUEST);
            accessError.setOauthErrorCode("invalid_request");
            errors.addError(accessError);
            return Either.error(errors);
        }
        return Either.success(Boolean.TRUE);
    }

    protected void checkValidityOfToken() {
        Either<trueffect.truconnect.api.commons.exceptions.business.Errors, Boolean> result =
                checkToken();
        if (result.isError()) {
            trueffect.truconnect.api.commons.exceptions.business.Errors errors = result.error();
            trueffect.truconnect.api.commons.exceptions.business.AccessError error =
                    (AccessError) errors.getErrors().iterator().next();
            throw BusinessValidationUtil
                    .buildAccessSystemException(error.getCode(), error.getOauthErrorCode(),
                            error.getWwwAuthenticate());
        }
    }

    /**
     * Verifies if the current user logged is allowed to access to this resource
     * using the accessToken
     *
     * @param roles Rules allowed to this resource
     *
     */
    void rolesAllowed(String roles) throws AccessDeniedException {
        disableSSLPolicy();
        String accessToken = getAccessToken();
        if (StringUtils.isNotBlank(accessToken)) {
            Client client = Client.create();
            String authoriazationUri = ResourceUtil.get("oauth.url");
            WebResource webResource = client.resource(authoriazationUri).path("authorizetoken").path(roles);
            response = webResource.type("application/json")
                    .header("Authorization", accessToken).post(ClientResponse.class);
            if (response.getStatus() == ClientResponse.Status.OK.getStatusCode()) {
                OauthKey userKey = response.getEntity(OauthKey.class);
                setOauthKey(userKey);
            } else {
                MultivaluedMap<String, String> map = response.getHeaders();
                String error = "";
                if (map != null) {
                    error = map.getFirst("WWW-Authenticate");
                }
                throw new AccessDeniedException(error);
            }
        } else {

            throw new AccessDeniedException("Invalid access token.");
        }
    }

    /**
     * Verifies if the current user logged in is allowed to access to this REST resource
     * using the provided accessToken through {@code Authorization} header
     *
     * @param roles Rules allowed to this resource
     *
     */
    void checkAccess(String roles) {
        disableSSLPolicy();
        String accessToken = getAccessToken();
        if (StringUtils.isNotBlank(accessToken)) {
            Client client = Client.create();
            String authorizationUri = ResourceUtil.get("oauth.url");
            WebResource webResource = client.resource(authorizationUri).path("authorizetoken").path(roles);
            response = webResource.type(MediaType.APPLICATION_JSON)
                    .header("Authorization", accessToken).post(ClientResponse.class);
            if (Response.Status.fromStatusCode(response.getStatus()) == Response.Status.OK) {
                OauthKey userKey = response.getEntity(OauthKey.class);
                setOauthKey(userKey);
            } else {
                String wwwAuthenticate = null;
                if (response.getHeaders() != null && response.getHeaders().getFirst("WWW-Authenticate") != null) {
                    wwwAuthenticate = response.getHeaders().getFirst("WWW-Authenticate");
                }
                throw BusinessValidationUtil.buildAccessSystemException(SecurityCode.INVALID_TOKEN, "invalid_token", wwwAuthenticate);
            }
        } else {
            throw BusinessValidationUtil.buildAccessSystemException(SecurityCode.INVALID_REQUEST, "invalid_request", null);
        }
    }

    /**
     * Disables SSL java client certificates to work with HTTPS protocol
     */
    private void disableSSLPolicy() {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }
        };
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
        }
    }

    private String getAccessToken() {
        if (headers != null) {
            List<String> authorizations = headers.getRequestHeader(HttpHeaders.AUTHORIZATION);
            log.info("Access token in Public: " + authorizations);
            if (authorizations != null && authorizations.size() > 0) {
                return authorizations.get(0);
            }
        }
        return null;
    }

    protected Response handleRolesAllowedException(Exception e) {
        log.info("Public Headers: " + headers);
        MultivaluedMap<String, String> headersMap = headers.getRequestHeaders();
        String message = "";
        if (headersMap != null) {
            message = headersMap.getFirst("WWW-Authenticate");
        }
        Error error = new Error(message, ApiValidationUtils.TYPE_INVALID);
        Errors errors = new Errors(Arrays.asList(error));
        return Response.status(Response.Status.FORBIDDEN)
                .header("WWW-Authenticate", error)
                .entity(errors).build();
    }

    protected OauthKey oauthKey() {
        return this.currentUserId;
    }

    protected void setOauthKey(OauthKey userId) {
        this.currentUserId = userId;
    }

    protected String getUserId() {
        return this.currentUserId.getUserId();
    }

    protected Builder setRequestHeaders(WebResource service, HttpHeaders headers) {
        WebResource.Builder builder = service.getRequestBuilder();
        MultivaluedMap<String, String> mapHeaders = headers.getRequestHeaders();
        String accept = null;
        for (String key : mapHeaders.keySet()) {
            for (String header : mapHeaders.get(key)) {
                log.info("Request Header Key: " + key + ", Value: " + header);
                builder = builder.header(key, header);
                if (key.equalsIgnoreCase("accept")) {
                    accept = header;
                }
            }
        }
        if ("text/xml".equalsIgnoreCase(accept)) {
            return builder.accept(MediaType.TEXT_XML);
        } else {
            return builder.accept(MediaType.APPLICATION_JSON);
        }
    }

    public void setHeader(HttpHeaders headers) {
        this.headers = headers;
    }
}
