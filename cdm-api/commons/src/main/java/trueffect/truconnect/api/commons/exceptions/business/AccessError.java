package trueffect.truconnect.api.commons.exceptions.business;

import org.apache.commons.lang.builder.ToStringBuilder;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Error class for Access-related errors.
 * Created by Marcelo Heredia on 11/25/2014.
 */
@XmlRootElement
public class AccessError extends Error{

    private String user;
    private String oauthErrorCode;
    private String wwwAuthenticate;

    public AccessError() {
        super();
    }

    public AccessError(String message, ErrorCode code, String user) {
        super(message, code);
        this.user = user;
    }

    public AccessError(String message, ErrorCode code) {
        super(message, code);
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getOauthErrorCode() {
        return oauthErrorCode;
    }

    public void setOauthErrorCode(String oauthErrorCode) {
        this.oauthErrorCode = oauthErrorCode;
    }


    public String getWwwAuthenticate() {
        return wwwAuthenticate;
    }

    public void setWwwAuthenticate(String wwwAuthenticate) {
        this.wwwAuthenticate = wwwAuthenticate;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
