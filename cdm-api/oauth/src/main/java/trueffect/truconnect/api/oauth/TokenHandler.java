package trueffect.truconnect.api.oauth;

import java.util.Set;
import java.util.Map;
import java.util.HashSet;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.security.Principal;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;

/**
 *
 * @author Rambert Rioja
 */
public class TokenHandler {

    public String parseAccessToken(String accessToken) {
        Pattern p = Pattern.compile("Bearer\\s(.+)");
        Matcher matcher = p.matcher(accessToken);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return null;
    }

    public DefaultOAuth2AccessToken generateAccessToken(final Principal principal,
            String host, String agent,
            DefaultTokenServices tokenServices) {
        UsernamePasswordAuthenticationToken upat = (UsernamePasswordAuthenticationToken) principal;
        Set<String> resourceIds = new HashSet<String>();
        resourceIds.add("/*");
        OAuth2Request o2r = new OAuth2Request(getRequestParameters(), "api-client",
                upat.getAuthorities(), true, getScopes(), resourceIds,
                "/unauthorized", null, null);
        OAuth2Authentication authenticationRequest = new OAuth2Authentication(o2r, upat);
        DefaultOAuth2AccessToken accessToken = (DefaultOAuth2AccessToken) tokenServices.createAccessToken(authenticationRequest);
        Map<String, Object> add = new HashMap<String, Object>();
        add.put("userId", principal.getName());
        String tpws = UserProvider.generateTPWSKey(principal.getName(), host, agent);
        add.put("tpws", tpws);
        accessToken.setAdditionalInformation(add);
        return accessToken;
    }

    public DefaultOAuth2AccessToken refreshAccessToken(String refreshTokenValue,
            DefaultTokenServices tokenServices) throws Exception {
        TokenRequest tr = new TokenRequest(getRequestParameters(), "api-client",
                getScopes(), "refresh_token");
        return (DefaultOAuth2AccessToken) tokenServices.refreshAccessToken(refreshTokenValue, tr);
    }

    private Set<String> getScopes() {
        Set<String> scopes = new HashSet<String>();
        scopes.add("read");
        scopes.add("write");
        scopes.add("trust");
        return scopes;
    }

    public Map<String, String> getRequestParameters() {
        Map<String, String> requets = new HashMap<String, String>();
        requets.put("client_id", "api-client");
        requets.put("grant", "refresh_token");
        return requets;
    }
}
