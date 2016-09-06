package trueffect.truconnect.api.commons.model.importexport;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Created by richard.jaldin on 4/26/2016.
 */
public class Column {
    private String property;
    private int index;

    public Column() {
    }

    public Column(String property, int index) {
        this.property = property;
        this.index = index;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
