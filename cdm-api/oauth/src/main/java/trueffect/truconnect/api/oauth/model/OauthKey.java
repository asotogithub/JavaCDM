package trueffect.truconnect.api.oauth.model;

import javax.xml.bind.annotation.XmlRootElement;

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

    public OauthKey(String userId, String tpws) {
        this.userId = userId;
        this.tpws = tpws;
    }

    public String getUserId() {
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
