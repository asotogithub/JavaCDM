package trueffect.truconnect.api.tpasapi.model;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author rambert.rioja
 */
@XmlRootElement(name = "RecordSet")
@XmlType(propOrder = {"startIndex", "pageSize", "totalNumberOfRecords", "records"})
@XmlSeeAlso({Creative.class, CreativeGroup.class, Cookie.class, TargetType.class,
    TargetValue.class, Site.class, Size.class, Placement.class, Publisher.class,
    TagType.class})
public class RecordSet<T> implements Serializable {

    private Integer startIndex;
    private Integer pageSize;
    private Integer totalNumberOfRecords;
    private List<T> records;

    public RecordSet() {
    }

    public RecordSet(Integer startIndex, Integer pageSize, List<T> records) {
        this.startIndex = startIndex;
        this.pageSize = pageSize;
        this.records = records;
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

    @XmlElementWrapper(name = "records")
    @XmlAnyElement(lax = true)
    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }

    @Override
    public String toString() {
        return "RecordSet [startIndex=" + startIndex + ", pageSize=" + pageSize
                + ", records=" + records + "]";
    }
}