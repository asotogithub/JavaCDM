package trueffect.truconnect.api.commons.model;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * Created by jfryer on 7/16/15.
 */
@XmlRootElement(name = "Metrics")
public class Metrics
{
    private Date day;
    private long id;
    private float cost;
    private long impressions;
    private long clicks;
    private long conversions;
    private float ctr;
    private float eCPA;

    public Metrics()
    {
    }

    public Metrics(Date day, long id, float cost, long impressions, long clicks, long conversions, float ctr, float eCPA) {
        this.day = day;
        this.id = id;
        this.cost = cost;
        this.impressions = impressions;
        this.clicks = clicks;
        this.conversions = conversions;
        this.ctr = ctr;
        this.eCPA = eCPA;
    }

    public float getCtr() {
        return ctr;
    }

    public void setCtr(float ctr) {
        this.ctr = ctr;
    }

    public float geteCPA() {
        return eCPA;
    }

    public void seteCPA(float eCPA) {
        this.eCPA = eCPA;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDay() {
        return day;
    }

    public void setDay(Date day) {
        this.day = day;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public long getImpressions() {
        return impressions;
    }

    public void setImpressions(long impressions) {
        this.impressions = impressions;
    }

    public long getClicks() {
        return clicks;
    }

    public void setClicks(long clicks) {
        this.clicks = clicks;
    }

    public long getConversions() {
        return conversions;
    }

    public void setConversions(long conversions) {
        this.conversions = conversions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Metrics metrics = (Metrics) o;

        if (id != metrics.id) return false;
        if (Float.compare(metrics.cost, cost) != 0) return false;
        if (impressions != metrics.impressions) return false;
        if (clicks != metrics.clicks) return false;
        if (conversions != metrics.conversions) return false;
        if (Float.compare(metrics.ctr, ctr) != 0) return false;
        if (Float.compare(metrics.eCPA, eCPA) != 0) return false;
        return day.equals(metrics.day);

    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(this.id)
                .append(this.cost)
                .append(this.impressions)
                .append(this.clicks)
                .append(this.conversions)
                .append(this.ctr)
                .append(this.eCPA)
                .append(this.day)
                .toHashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
