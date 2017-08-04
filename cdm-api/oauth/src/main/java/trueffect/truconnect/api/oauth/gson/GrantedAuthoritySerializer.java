package trueffect.truconnect.api.oauth.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.springframework.security.core.GrantedAuthority;

import java.lang.reflect.Type;

/**
 * Created by richard.jaldin on 11/19/2015.
 */
public class GrantedAuthoritySerializer implements JsonSerializer<GrantedAuthority> {

    @Override
    public JsonElement serialize(GrantedAuthority src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.addProperty("authority", src.getAuthority());
        return result;
    }
}
