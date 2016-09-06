package trueffect.truconnect.api.tpasapi.client.proxy;

import trueffect.truconnect.api.tpasapi.client.base.ServiceProxyImpl;

public abstract class GenericTpasapiServiceProxy<T> {

    protected String baseUrl;
    protected String authenticationUrl;
    protected String servicePath;
    protected String contentType;
    protected String userName;
    protected String password;
    protected AuthenticationTpasapiServiceProxy authenticator;
    protected volatile int status;

    public GenericTpasapiServiceProxy(String baseUrl, String authenticationUrl, String servicePath, String contentType, String userName, String password) throws Exception {
	this.baseUrl = baseUrl;
	this.authenticationUrl = authenticationUrl;
	this.servicePath = servicePath;
	this.contentType = contentType;
	this.userName = userName;
	this.password = password;
	this.authenticator = new AuthenticationTpasapiServiceProxy(authenticationUrl, userName, password);
    }

    protected abstract ServiceProxyImpl<T> getProxy();

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public int getStatus(){
        return status;
    }
}
