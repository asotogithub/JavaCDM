package trueffect.truconnect.api.commons.model.dto;

import trueffect.truconnect.api.commons.model.Publisher;
import trueffect.truconnect.api.commons.model.Site;
import trueffect.truconnect.api.commons.model.SiteSection;
import trueffect.truconnect.api.commons.model.Size;

import org.apache.commons.lang.builder.ToStringBuilder;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by richard.jaldin on 10/20/2015.
 */
@XmlRootElement(name = "BulkPublisherSiteSectionSize")
public class BulkPublisherSiteSectionSize {

    private Publisher publisher;
    private Site site;
    private SiteSection section;
    private Size size;

    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public SiteSection getSection() {
        return section;
    }

    public void setSection(SiteSection section) {
        this.section = section;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
