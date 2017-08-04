package trueffect.truconnect.api.standalone.model;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Richard Jaldin
 */
@XmlRootElement(name = "Clickthrough")
@XmlType(propOrder = {"sequence", "url"})
public class Clickthrough {

    private Long sequence;
    private String url;

    public Clickthrough() {
    }

    public Clickthrough(Long sequence, String clickthrough) {
        this.sequence = sequence;
        this.url = clickthrough;
    }

    public Long getSequence() {
        return sequence;
    }

    public void setSequence(Long sequence) {
        this.sequence = sequence;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Clickthrough{" + "sequence=" + sequence + ", url=" + url + '}';
    }
}
