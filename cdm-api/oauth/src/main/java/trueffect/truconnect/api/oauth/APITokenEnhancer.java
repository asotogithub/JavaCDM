package trueffect.truconnect.api.oauth;

import java.util.Collections;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

/**
 *
 * @author Rambert Rioja
 */
public class APITokenEnhancer implements TokenEnhancer {

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        DefaultOAuth2AccessToken result = new DefaultOAuth2AccessToken(accessToken);
        result.setAdditionalInformation(Collections.singletonMap("client_id", (Object) authentication.getOAuth2Request().getClientId()));
        return result;
    }
}
