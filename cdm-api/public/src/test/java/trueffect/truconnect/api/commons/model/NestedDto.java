package trueffect.truconnect.api.commons.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Nested DTO for testing purposes
 * Created by marcelo.heredia on 6/6/2015.
 * @author Marcelo Heredia
 */
@XmlRootElement
public class NestedDto{
    private String name;
    private int value;

    public NestedDto() {
    }

    public NestedDto(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}