package trueffect.truconnect.api.oauth.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.lang.reflect.Type;

/**
 * Created by richard.jaldin on 11/19/2015.
 */
public class GrantedAuthorityDeserializer implements JsonDeserializer<GrantedAuthority> {

    @Override
    public GrantedAuthority deserialize(JsonElement json, Type type,
                                             JsonDeserializationContext context) {
        JsonObject jsonObject = (JsonObject) json;
        SimpleGrantedAuthority result = null;
        if(jsonObject.get("authority") != null) {
            result = new SimpleGrantedAuthority(jsonObject.get("authority").getAsString());
        }
        return result;
    }
}
