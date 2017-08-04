package trueffect.truconnect.api.tpasapi.model;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Gustavo Claure
 */
@XmlRootElement(name = "Clickthrough")
public class Clickthrough implements Serializable {

    private Long sequence;
    private String url;

    public Clickthrough() {
    }

    public Clickthrough(Long sequence, String url) {
        this.sequence = sequence;
        this.url = url;
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
