package trueffect.truconnect.api.oauth.gson;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by richard.jaldin on 11/19/2015.
 */
public class AccessTokenSerializer implements JsonSerializer<OAuth2AccessToken> {

    @Override
    public JsonElement serialize(OAuth2AccessToken src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        DefaultOAuth2AccessToken accessTokenSrc = (DefaultOAuth2AccessToken)src;
        result.addProperty("value", accessTokenSrc.getValue());
        result.addProperty("expiration", accessTokenSrc.getExpiration().getTime());
        result.addProperty("tokenType", accessTokenSrc.getTokenType());
        result.add("refreshToken", context.serialize(accessTokenSrc.getRefreshToken(), OAuth2RefreshToken.class));
        JsonArray scopes = new JsonArray();
        for (String scope : accessTokenSrc.getScope()) {
            scopes.add(scope);
        }
        result.add("scope", scopes);
        JsonArray additionalInformation = new JsonArray();
        for (Map.Entry<String, Object> entry : accessTokenSrc.getAdditionalInformation().entrySet()) {
            JsonObject item = new JsonObject();
            item.addProperty("key", entry.getKey());
            item.addProperty("value", entry.getValue().toString());
            additionalInformation.add(item);
        }
        result.add("additionalInformation", additionalInformation);
        return result;
    }
}
