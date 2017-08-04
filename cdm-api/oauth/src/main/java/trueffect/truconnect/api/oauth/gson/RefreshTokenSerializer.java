package trueffect.truconnect.api.oauth.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;

import java.lang.reflect.Type;

/**
 * Created by richard.jaldin on 11/19/2015.
 */
public class RefreshTokenSerializer implements JsonSerializer<OAuth2RefreshToken> {

    @Override
    public JsonElement serialize(OAuth2RefreshToken src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        ExpiringOAuth2RefreshToken refreshTokenSrc = (ExpiringOAuth2RefreshToken)src;
        result.addProperty("value", refreshTokenSrc.getValue());
        result.addProperty("expiration", refreshTokenSrc.getExpiration().getTime());
        return result;
    }
}
