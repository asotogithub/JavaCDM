package trueffect.truconnect.api.oauth.gson;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.lang.reflect.Type;

/**
 * Created by richard.jaldin on 11/19/2015.
 */
public class AuthenticationSerializer implements JsonSerializer<Authentication> {

    @Override
    public JsonElement serialize(Authentication src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("principal", context.serialize(src.getPrincipal(), User.class));
        result.add("credentials", context.serialize(src.getCredentials()));
        JsonArray authorities = new JsonArray();
        for (GrantedAuthority grantedAuthority : src.getAuthorities()) {
            authorities.add(context.serialize(grantedAuthority, GrantedAuthority.class));
        }
        result.add("authorities", authorities);
        return result;
    }
}
