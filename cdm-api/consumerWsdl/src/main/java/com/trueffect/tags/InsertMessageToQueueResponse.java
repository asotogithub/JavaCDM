
package com.trueffect.tags;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "insertMessageToQueueResult"
})
@XmlRootElement(name = "InsertMessageToQueueResponse")
public class InsertMessageToQueueResponse {

    @XmlElement(name = "InsertMessageToQueueResult")
    protected Boolean insertMessageToQueueResult;

    public Boolean isInsertMessageToQueueResult() {
        return insertMessageToQueueResult;
    }

    public void setInsertMessageToQueueResult(Boolean value) {
        this.insertMessageToQueueResult = value;
    }

}
