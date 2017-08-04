package trueffect.truconnect.api.oauth;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 *
 * @author Rambert Rioja
 */
public class APIUserDetailsService implements UserDetailsService {
    private final Logger logger = LoggerFactory.getLogger(APIUserDetailsService.class);

    public APIUserDetailsService() {
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            logger.info("loadUserByUsername for {}", username);
            trueffect.truconnect.api.oauth.model.User user = UserProvider.getUser(username);
            if (user == null) {
                throw new UsernameNotFoundException(username);
            }
            UserDetails userD = new User(user.getUserId(), user.getPassword(), getGrantedAuthorities(user));
            return userD;
        } catch (Exception e) {
            logger.error("Unexpected exception", e);
            throw new UsernameNotFoundException(username + " not found!", e);
        }
    }

    private ArrayList<GrantedAuthority> getGrantedAuthorities(trueffect.truconnect.api.oauth.model.User user) {
        ArrayList<GrantedAuthority> list = new ArrayList<GrantedAuthority>();
        for (String role : user.getRoles()) {
            list.add(new SimpleGrantedAuthority(role));
        }
        return list;
    }
}
