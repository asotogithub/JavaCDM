package trueffect.truconnect.api.commons.model.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "DatasetMetrics")
public class DatasetMetrics {
    private Summary summary;
    private List<DatedSummary> dates;

    public Summary getSummary() {
        return summary;
    }

    public void setSummary(Summary summary) {
        this.summary = summary;
    }

    public void setDates(List<DatedSummary> dates) {
        this.dates = dates;
    }

    @XmlElementWrapper(name = "dates")
    @XmlElement(name = "date", type = DatedSummary.class)
    public List<DatedSummary> getDates() {
        return dates;
    }

    public DatasetMetrics() {}
    
    public DatasetMetrics(Summary summary, List<DatedSummary> dates) {
        this.summary = summary;
        this.dates = dates;
    }
}
