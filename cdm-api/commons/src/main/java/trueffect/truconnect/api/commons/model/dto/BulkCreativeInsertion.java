package trueffect.truconnect.api.commons.model.dto;

import javax.xml.bind.annotation.XmlRootElement;
import trueffect.truconnect.api.commons.model.CreativeInsertion;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.List;

/**
 *
 * @author abel.soto
 */
@XmlRootElement(name = "BulkCreativeInsertion")
public class BulkCreativeInsertion {

    private List<CreativeInsertion> creativeInsertions;

    public List<CreativeInsertion> getCreativeInsertions() {
        return creativeInsertions;
    }

    public void setCreativeInsertions(List<CreativeInsertion> creativeInsertions) {
        this.creativeInsertions = creativeInsertions;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
