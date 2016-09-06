package trueffect.truconnect.api.tpasapi.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "CreativeGroupCreatives")
@XmlType(propOrder = {"creativeGroupId", "creatives"})
@XmlSeeAlso({Creative.class})
public class CreativeGroupCreatives{

    private Long creativeGroupId;
    private List<Creative> creatives;

    public CreativeGroupCreatives() {
    }

	public Long getCreativeGroupId() {
		return creativeGroupId;
	}

	public void setCreativeGroupId(Long creativeGroupId) {
		this.creativeGroupId = creativeGroupId;
	}

	@XmlElementWrapper(name = "creatives")
    @XmlAnyElement(lax = true)
	public List<Creative> getCreatives() {
		return creatives;
	}

	public void setCreatives(List<Creative> creatives) {
		this.creatives = creatives;
	}

	@Override
	public String toString() {
		return "CreativeGroupCreatives [creativeGroupId=" + creativeGroupId
				+ ", creatives=" + creatives + "]";
	}

}
