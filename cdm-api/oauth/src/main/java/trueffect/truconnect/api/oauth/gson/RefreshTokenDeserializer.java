package trueffect.truconnect.api.oauth.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.springframework.security.oauth2.common.DefaultExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;

import java.lang.reflect.Type;
import java.util.Date;

/**
 * Created by richard.jaldin on 11/19/2015.
 */
public class RefreshTokenDeserializer implements JsonDeserializer<OAuth2RefreshToken> {

    @Override
    public OAuth2RefreshToken deserialize(JsonElement json, Type type,
                                             JsonDeserializationContext context) {
        JsonObject jsonObject = (JsonObject) json;
        OAuth2RefreshToken result = new DefaultExpiringOAuth2RefreshToken(jsonObject.get("value").getAsString(), new Date(jsonObject.get("expiration").getAsLong()));
        return result;
    }
}
