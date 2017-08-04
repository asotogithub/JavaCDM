package trueffect.truconnect.api.tpasapi.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 *
 * @author Richard Jaldin
 */
@XmlRootElement(name = "Cookie")
@XmlSeeAlso({Value.class})
public class Cookie {

    private Long id;
    private Long cookieDomainId;
    private String name;
    private String type;
    private List<Value> values;

    public Cookie() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCookieDomainId() {
        return cookieDomainId;
    }

    public void setCookieDomainId(Long cookieDomainId) {
        this.cookieDomainId = cookieDomainId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @XmlElementWrapper(name = "values")
    @XmlAnyElement(lax = true)
    public List<Value> getValues() {
        return values;
    }

    public void setValues(List<Value> values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return "Cookie [id=" + id + ", cookieDomainId=" + cookieDomainId
                + ", name=" + name + ", type=" + type + "]";
    }
}
