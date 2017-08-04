package trueffect.truconnect.api.commons.model;

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.QueryParam;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Abel Soto
 */
@XmlRootElement(name = "TraffickingArrayOfint")
public class TraffickingArrayOfint {

    @QueryParam("selectedAgencyContacts")
    private List<Integer> agencyContacts;
    @QueryParam("selectedSiteContacts")
    private List<Integer> siteContacts;

    public List<Integer> getAgencyContacts() {
        if (agencyContacts == null) {
            setAgencyContacts(new ArrayList<Integer>());
        }
        return this.agencyContacts;
    }

    public List<Integer> getSiteContacts() {
        if (siteContacts == null) {
            setSiteContacts(new ArrayList<Integer>());
        }
        return this.siteContacts;
    }

    public void setAgencyContacts(List<Integer> agencyContacts) {
        this.agencyContacts = agencyContacts;
    }

    public void setSiteContacts(List<Integer> siteContacts) {
        this.siteContacts = siteContacts;
    }

    @Override
    public String toString() {
        return "ArrayOfint{" + "agencyContacts=" + agencyContacts + ", siteContacts=" + siteContacts + '}';
    }
}
