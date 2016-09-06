package trueffect.truconnect.api.commons.model.delivery;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author rodrigo.alarcon
 */
@XmlRootElement(name = "KeyValueOfintArrayOfstringty7Ep6D1")
@XmlAccessorType(XmlAccessType.FIELD)
public class TagEmailRecipientsWrapper {
    
    @XmlElement(name = "Key")
    @JsonProperty("Key")
    private Integer key;
    
    @XmlElementWrapper(name = "Value")
    @XmlElement(name = "string")
    @JsonProperty("Value")
    private List<String> value;
    
    public TagEmailRecipientsWrapper(){
    }
    
    public TagEmailRecipientsWrapper(Integer key, List<String> value){
        this.key = key;
        this.value = value;
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public List<String> getValue() {
        return value;
    }

    public void setValue(List<String> value) {
        this.value = value;
    }
    
}
