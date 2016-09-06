package trueffect.truconnect.api.publik.client.proxy;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.core.MultivaluedMap;

import trueffect.truconnect.api.commons.model.Authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.client.filter.LoggingFilter;

import javax.ws.rs.core.MediaType;

public class AuthenticationPublicServiceProxy {

    private Authentication authentication;
    protected WebResource webRsc;
    private String userName;
    private String password;
    private String authenticationUrl;

    private boolean tokenHasExired;
    private boolean tokenIsCloseToExpire;

    private static final Logger
            HTTP_CLIENT_LOGGER = Logger.getLogger(AuthenticationPublicServiceProxy.class.getName());

    public AuthenticationPublicServiceProxy(String authenticationUrl, String userName, String password) throws Exception {
        this.authenticationUrl = authenticationUrl;
        this.userName = userName;
        this.password = password;
        this.webRsc = makeWebResource();
        this.authentication = authenticate();
    }

    protected WebResource makeWebResource() {
        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        // Adding the basic filter parameter with the User Name and Password
        client.addFilter(new HTTPBasicAuthFilter(userName, password));
        // Use this log only for debugging purposes
        client.addFilter(new LoggingFilter(HTTP_CLIENT_LOGGER));
        return client.resource(authenticationUrl).path("");
    }

    protected Authentication authenticate() throws Exception {
        ClientResponse response = webRsc.path("accesstoken").accept(MediaType.APPLICATION_JSON).post(ClientResponse.class);
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            handleErrors(response);
        }
        clearAll();
        return getEntity(response);
    }
    
    protected Authentication getEntity(ClientResponse response) throws Exception {
        String output = response.getEntity(String.class);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(output, Authentication.class);
    }

    protected void handleErrors(ClientResponse response) throws Exception {
        String str = response.getEntity(new GenericType<String>(String.class));
        System.out.println("================ Error Output ================");
        System.out.println(str);
        System.out.println("==============================================");
        throw new Exception("Failed : HTTP error code : " + response.getStatus());
    }

    public String getAccessToken() throws Exception {
        // Check if the token is close to expire, if so, wait until it expires.
        // Check if the token has expired, if so, call to authenticate again.
        // With this approach we are safe from overriding the token when other user is still using it.
        checkTokenExpiration();
        while(tokenIsCloseToExpire) { // Wait until it expires
            try {
                Thread.sleep(5000);
                checkTokenExpiration();
            } catch (Exception e) {     }
        }
        if(tokenHasExired) { // Re-authenticate
            boolean gotInvalidToken = true;
            int count = 0;
            while(gotInvalidToken && count < 2) {     // Tries twice to re-authenticate.
                try {
                    this.authentication = authenticate();
                    gotInvalidToken = false;
                } catch (Exception e) {
                    gotInvalidToken = true;
                    count++;
                }
            }
        }
        this.authentication = authenticate();
        return authentication.getAccessToken();
    }

    private void checkTokenExpiration() {
        disableSSLPolicy();
        if(authentication != null){
            ClientResponse response = webRsc.path("authorizetoken").path("ROLE_CLIENT_ADMIN,ROLE_API_FULL_ACCESS,ROLE_API_TEST_ACCESS").
                    header("Authorization", authentication.getAccessToken()).post(ClientResponse.class);
            MultivaluedMap<String, String> map = response.getHeaders();
            String error = "";
            if (map != null && map.containsKey("WWW-Authenticate")) {
                error = map.getFirst("WWW-Authenticate");
                tokenHasExired = error.contains("The access token has expired");
                tokenIsCloseToExpire = error.contains("The access token is close to expire");
            }
        }
        clearAll();
    }

    /**
     * Disables SSL java client certificates to work with HTTPS protocol
     */
    private void disableSSLPolicy() {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
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
        }
        catch (Exception e) {	}
    }
    
    protected void clearAll(){
        webRsc = makeWebResource();
    }
}
