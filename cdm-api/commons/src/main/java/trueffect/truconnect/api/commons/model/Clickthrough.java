package trueffect.truconnect.api.commons.model;

import org.apache.commons.lang.builder.ToStringBuilder;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Rambert Rioja
 */
@XmlRootElement(name = "Clickthrough")
public class Clickthrough {

    private Long sequence;
    private String url;
    private Long creativeInsertionId;
    private String modifiedTpwsKey;

    public Clickthrough() {
    }

    public Clickthrough(Long sequence, String clickthrough) {
        this.sequence = sequence;
        this.url = clickthrough;
    }

    public Clickthrough(Long sequence, String clicktrhough, String modifiedTpwKey, Long creativeInsertionId ) {
        this(sequence, clicktrhough);
        this.modifiedTpwsKey = modifiedTpwKey;
        this.creativeInsertionId = creativeInsertionId;
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

    public Long getCreativeInsertionId() {
        return creativeInsertionId;
    }

    public void setCreativeInsertionId(Long creativeInsertionId) {
        this.creativeInsertionId = creativeInsertionId;
    }

    public String getModifiedTpwsKey() {
        return modifiedTpwsKey;
    }

    public void setModifiedTpwsKey(String modifiedTpwsKey) {
        this.modifiedTpwsKey = modifiedTpwsKey;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
