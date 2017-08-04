package trueffect.truconnect.api.commons.model.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "DatedSummary")
public class DatedSummary extends Summary {
    
    private String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public DatedSummary() {}
    
    public DatedSummary(String date, Long engagements, Long cached, Long matched, Long updated, Double matchRate) {
        super(engagements, cached, matched, updated, matchRate);
        this.date = date;
    }
    
}
