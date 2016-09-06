package trueffect.truconnect.api.oauth;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author Rambert Rioja
 */
public class OauthAuthorization {

    public OauthAuthorization() {
    }

    public boolean includeAuthority(String roles, User user, APIClientDetailsService apiClientDetailsService) {
        String[] listRoles = StringUtils.split(roles, ",");
        ArrayList<GrantedAuthority> authorities = apiClientDetailsService
                .getGrantedAuthorities(user.getUsername());
        for (String role : listRoles) {
            if (containsAuthority(authorities, role)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsAuthority(Collection<GrantedAuthority> authorities, String role) {
        for (GrantedAuthority auth : authorities) {
            if (auth.getAuthority().compareTo(role) == 0) {
                return true;
            }
        }
        return false;
    }

    public boolean isValidAccessToken(String accessToken, DefaultTokenServices tokenServices) {
        OAuth2AccessToken token = tokenServices.readAccessToken(accessToken);
        return token != null
                && token.getValue().compareTo(accessToken) == 0;
    }

    public boolean isExpiredAccessToken(String accessToken, DefaultTokenServices tokenServices) {
        OAuth2AccessToken token = tokenServices.readAccessToken(accessToken);
        return token != null && token.isExpired();
    }
}
