package trueffect.truconnect.api.commons.model;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.List;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Testing DTO to verify natural JSON notation
 * Created by marcelo.heredia on 6/3/2015.
 * @author Marcelo Heredia
 */
@XmlRootElement
public class TestDto {
    private int intValue;
    private long longValue;
    private double doubleValue;
    private String stringValue;

    private List<String> stringList;
    private List<Integer> intList;
    private List<Long> longList;
    private List<Double> doubleList;

    private List<Integer> singleIntList;
    private List<Double> singleDoubleList;
    private List<Long> singleLongList;
    private List<NestedDto> nestedList;
    private Type type;

    @XmlType
    @XmlEnum
    public enum Type {
        @XmlEnumValue("first")
        FIRST,
        @XmlEnumValue("second")
        SECOND;
        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    public TestDto() {
    }

    public TestDto(int intValue, long longValue, double doubleValue, String stringValue,
                   List<String> stringList, List<Integer> intList, List<Long> longList,
                   List<Double> doubleList, List<Integer> singleIntList, List<Long> singleLongList,
                   List<Double> singleDoubleList, List<NestedDto> nestedList, Type type) {
        this.intValue = intValue;
        this.longValue = longValue;
        this.doubleValue = doubleValue;
        this.stringValue = stringValue;
        this.stringList = stringList;
        this.intList = intList;
        this.longList = longList;
        this.doubleList = doubleList;
        this.singleIntList = singleIntList;
        this.singleDoubleList = singleDoubleList;
        this.singleLongList = singleLongList;
        this.nestedList = nestedList;
        this.type = type;
    }


    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public List<Integer> getSingleIntList() {
        return singleIntList;
    }

    public void setSingleIntList(List<Integer> singleIntList) {
        this.singleIntList = singleIntList;
    }

    public List<Double> getSingleDoubleList() {
        return singleDoubleList;
    }

    public void setSingleDoubleList(List<Double> singleDoubleList) {
        this.singleDoubleList = singleDoubleList;
    }

    public List<Long> getSingleLongList() {
        return singleLongList;
    }

    public void setSingleLongList(List<Long> singleLongList) {
        this.singleLongList = singleLongList;
    }

    public int getIntValue() {
        return intValue;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }

    public long getLongValue() {
        return longValue;
    }

    public void setLongValue(long longValue) {
        this.longValue = longValue;
    }

    public double getDoubleValue() {
        return doubleValue;
    }

    public void setDoubleValue(double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public List<String> getStringList() {
        return stringList;
    }

    public void setStringList(List<String> stringList) {
        this.stringList = stringList;
    }

    public List<Integer> getIntList() {
        return intList;
    }

    public void setIntList(List<Integer> intList) {
        this.intList = intList;
    }

    public List<Long> getLongList() {
        return longList;
    }

    public void setLongList(List<Long> longList) {
        this.longList = longList;
    }

    public List<Double> getDoubleList() {
        return doubleList;
    }

    public void setDoubleList(List<Double> doubleList) {
        this.doubleList = doubleList;
    }

    public List<NestedDto> getNestedList() {
        return nestedList;
    }

    public void setNestedList(List<NestedDto> nestedList) {
        this.nestedList = nestedList;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
