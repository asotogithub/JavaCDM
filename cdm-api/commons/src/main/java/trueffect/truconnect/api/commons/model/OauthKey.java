package trueffect.truconnect.api.commons.model;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlRootElement;
import trueffect.truconnect.api.commons.util.EncryptUtil;

/**
 *
 * @author Rambert Rioja
 */
@XmlRootElement
public class OauthKey {

    private String userId;
    private String tpws;

    public OauthKey() {
    }

    public OauthKey(String userKey, String tpws) {
        this.userId = userKey;
        this.tpws = tpws;
    }

    public String getUserId() {
        try {
            return EncryptUtil.decryptAES(userId);
        } catch (Exception ex) {
            Logger.getLogger(OauthKey.class.getName()).log(Level.SEVERE, null, ex);
        }
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTpws() {
        return tpws;
    }

    public void setTpws(String tpws) {
        this.tpws = tpws;
    }

    @Override
    public String toString() {
        return "OauthKey{" + "userId=" + userId + ", tpws=" + tpws + '}';
    }
}
