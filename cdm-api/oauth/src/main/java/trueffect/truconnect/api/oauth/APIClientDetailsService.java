package trueffect.truconnect.api.oauth;

import java.util.ArrayList;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.provider.BaseClientDetails;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import trueffect.truconnect.api.oauth.model.User;

/**
 *
 * @author Rambert Rioja
 */
public class APIClientDetailsService implements ClientDetailsService {

    public APIClientDetailsService() {
    }

    @Override
    public ClientDetails loadClientByClientId(String username) throws ClientRegistrationException {
        try {
            User user = UserProvider.getUser(username);
            if (user == null) {
                throw new UsernameNotFoundException(username);
            }
            ClientDetails userD = new BaseClientDetails(user.getUserId(),
                    "/*",
                    "read,write,trust",
                    "password,authorization_code,refresh_token,implicit",
                    getGrantedAuthorities(user));
            return userD;
        } catch (Exception e) {
            throw new UsernameNotFoundException(username + " not found!", e);
        }
    }

    public String getGrantedAuthorities(User user) {
        return StringUtils.join(user.getRoles(), ", ");
    }

    public ArrayList<GrantedAuthority> getGrantedAuthorities(String username) {
        try {
            User user = UserProvider.getUser(username);
            if (user == null) {
                throw new UsernameNotFoundException(username);
            }
            ArrayList<GrantedAuthority> list = new ArrayList<GrantedAuthority>();
            for (String role : user.getRoles()) {
                list.add(new SimpleGrantedAuthority(role));
            }
            return list;
        } catch (Exception e) {
            throw new UsernameNotFoundException(username + " not found!", e);
        }
    }
}
