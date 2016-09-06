package trueffect.truconnect.api.oauth.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by richard.jaldin on 11/19/2015.
 */
public class AuthenticationDeserializer implements JsonDeserializer<Authentication> {

    @Override
    public Authentication deserialize(JsonElement json, Type type,
                                             JsonDeserializationContext context) {
        JsonObject jsonObject = (JsonObject) json;
        User principal = context.deserialize(jsonObject.get("principal"), User.class);
        Object credentials = jsonObject.get("credentials") != null ? context.deserialize(jsonObject.get("credentials"), Object.class) : null;
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        for (JsonElement authority : jsonObject.getAsJsonArray("authorities")) {
            authorities.add(context.<GrantedAuthority>deserialize(authority, GrantedAuthority.class));
        }
        UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(principal, credentials, authorities);
        return result;
    }
}
