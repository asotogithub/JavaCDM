
package com.microsoft.schemas._2003._10.serialization.arrays;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;



@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfint", propOrder = {
    "_int"
})
public class ArrayOfint {
    
    @XmlElement(name = "int", type = Integer.class)
    private List<Integer> _int;

    
    public List<Integer> getInt() {
        if (_int == null) {
            setInt(new ArrayList<Integer>());
        }
        return this._int;
    }

    public void setInt(List<Integer> _int) {
        this._int = _int;
    }
    

}
