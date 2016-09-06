package trueffect.truconnect.api.commons.model.dto.adm;

import org.apache.commons.lang.builder.ToStringBuilder;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

/**
 * CookieList representation for an ADM context
 * Created by marcelo.heredia on 3/24/2016.
 */
public class CookieList {
    private boolean enabled;
    private List<String> cookies;

    public CookieList() {
    }

    public CookieList(boolean enabled, List<String> cookies) {
        this.enabled = enabled;
        this.cookies = cookies;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    // TODO to be reenabled in next PR
//    @XmlElementWrapper(name = "cookies")
//    @XmlElement(name = "cookie", type = String.class)
    public List<String> getCookies() {
        return cookies;
    }

    public void setCookies(List<String> cookies) {
        this.cookies = cookies;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).toString();
    }
}
