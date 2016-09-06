package trueffect.truconnect.api.commons.model.dto;

import trueffect.truconnect.api.commons.model.HtmlInjectionTagAssociation;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author marleny.patsi
 */
@XmlRootElement(name = "HtmlInjectionTagAssociationDTO")
public class HtmlInjectionTagAssociationDTO implements Serializable {

    private List<HtmlInjectionTagAssociation> inheritedAssociations;
    private List<HtmlInjectionTagAssociation> directAssociations;
    private Integer startIndex;
    private Integer pageSize;
    private Integer totalNumberOfRecords;

    public List<HtmlInjectionTagAssociation> getInheritedAssociations() {
        return inheritedAssociations;
    }

    public void setInheritedAssociations(List<HtmlInjectionTagAssociation> inheritedAssociations) {
        this.inheritedAssociations = inheritedAssociations;
    }

    public List<HtmlInjectionTagAssociation> getDirectAssociations() {
        return directAssociations;
    }

    public void setDirectAssociations(List<HtmlInjectionTagAssociation> directAssociations) {
        this.directAssociations = directAssociations;
    }

    public Integer getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalNumberOfRecords() {
        return totalNumberOfRecords;
    }

    public void setTotalNumberOfRecords(Integer totalNumberOfRecords) {
        this.totalNumberOfRecords = totalNumberOfRecords;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
