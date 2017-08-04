package trueffect.truconnect.api.oauth.encoding;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Base64;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;

/**
 *
 * @author Rambert Rioja
 */
public class ShaPasswordEncoder extends MessageDigestPasswordEncoder  {

    public ShaPasswordEncoder() {
        this(1);
    }

    public ShaPasswordEncoder(int strength) {
        super("SHA-" + strength);
    }

    @Override
    public String encodePassword(String rawPass, Object salt) {
        try {
            MessageDigest md = MessageDigest.getInstance(this.getAlgorithm());
            byte[] digest = md.digest(rawPass.getBytes());
            return new String(Base64.encodeBase64(digest));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(this.getAlgorithm() + " not supported!");
        }
    }
}
