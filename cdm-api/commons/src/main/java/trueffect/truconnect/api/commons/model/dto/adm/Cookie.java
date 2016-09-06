package trueffect.truconnect.api.commons.model.dto.adm;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * DTO that represents a Cookie
 * Created by marcelo.heredia on 3/24/2016.
 */
public class Cookie {
    private String name;
    private String value;

    public Cookie() {
    }

    public Cookie(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).toString();
    }
}
