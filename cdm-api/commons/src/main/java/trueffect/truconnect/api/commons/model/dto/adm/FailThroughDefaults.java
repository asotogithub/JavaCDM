package trueffect.truconnect.api.commons.model.dto.adm;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.List;

/**
 * DTO for FailThroughDefaults
 * Created by marcelo.heredia on 3/28/2016.
 */
public class FailThroughDefaults{
    private boolean enabled;
    private String defaultType;
    private String defaultKey;
    private List<Cookie> defaultCookieList;

    public FailThroughDefaults() {
    }

    public FailThroughDefaults(boolean enabled, String defaultType, String defaultKey, List<Cookie> defaultCookieList) {
        this.enabled = enabled;
        this.defaultType = defaultType;
        this.defaultKey = defaultKey;
        this.defaultCookieList = defaultCookieList;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled= enabled;
    }

    public String getDefaultType() {
        return defaultType;
    }

    public void setDefaultType(String defaultType) {
        this.defaultType = defaultType;
    }

    public String getDefaultKey() {
        return defaultKey;
    }

    public void setDefaultKey(String defaultKey) {
        this.defaultKey = defaultKey;
    }

    public List<Cookie> getDefaultCookieList() {
        return defaultCookieList;
    }

    public void setDefaultCookieList(List<Cookie> defaultCookieList) {
        this.defaultCookieList = defaultCookieList;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).toString();
    }
}
