package trueffect.truconnect.api.oauth.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by richard.jaldin on 11/19/2015.
 */
public class AccessTokenDeserializer implements JsonDeserializer<OAuth2AccessToken> {

    @Override
    public OAuth2AccessToken deserialize(JsonElement json, Type type,
                                             JsonDeserializationContext context) {
        JsonObject jsonObject = (JsonObject) json;
        DefaultOAuth2AccessToken result = new DefaultOAuth2AccessToken(jsonObject.get("value").getAsString());
        result.setExpiration(new Date(jsonObject.get("expiration").getAsLong()));
        result.setTokenType(jsonObject.get("tokenType").getAsString());
        result.setRefreshToken(context.<OAuth2RefreshToken>deserialize(jsonObject.get("refreshToken"), OAuth2RefreshToken.class));

        Set<String> scopes = new HashSet<>();
        for (JsonElement scope : jsonObject.get("scope").getAsJsonArray()) {
            scopes.add(scope.getAsString());
        }
        result.setScope(scopes);

        HashMap<String, Object> additionalInformation = new HashMap<>();
        for (JsonElement element : jsonObject.get("additionalInformation").getAsJsonArray()) {
            JsonObject item = element.getAsJsonObject();
            String key = item.get("key").getAsString();
            String value = item.get("value").getAsString();
            additionalInformation.put(key, value);
        }
        result.setAdditionalInformation(additionalInformation);

        return result;
    }
}
