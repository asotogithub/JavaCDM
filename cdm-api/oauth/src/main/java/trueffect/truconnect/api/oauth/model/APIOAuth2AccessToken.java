package trueffect.truconnect.api.oauth.model;

import javax.xml.bind.annotation.XmlRootElement;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

/**
 *
 * @author Rambert Rioja
 */
@XmlRootElement(name = "AccessToken")
public class APIOAuth2AccessToken {

    private String accessToken;
    private String tokenType;
    private String refreshToken;
    private Integer expiresIn;
    private String scope;
    private String userId;

    public APIOAuth2AccessToken() {
    }

    public APIOAuth2AccessToken(OAuth2AccessToken token) {
        this.accessToken = token.getValue();
        this.tokenType = token.getTokenType();
        this.refreshToken = token.getRefreshToken().getValue();
        this.expiresIn = token.getExpiresIn();
        this.scope = token.getScope().toString();
        this.userId = token.getAdditionalInformation() != null && token.getAdditionalInformation().get("userId") != null ?
                token.getAdditionalInformation().get("userId").toString() : null;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
