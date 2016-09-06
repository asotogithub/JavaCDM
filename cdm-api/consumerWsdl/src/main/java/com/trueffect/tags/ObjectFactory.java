
package com.trueffect.tags;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


@XmlRegistry
public class ObjectFactory {

    private final static QName _InsertMessageToQueueSessionKey_QNAME = new QName("http://tags.trueffect.com/", "SessionKey");

    public ObjectFactory() {
    }

    public InsertMessageToQueueResponse createInsertMessageToQueueResponse() {
        return new InsertMessageToQueueResponse();
    }

    public InsertMessageToQueue createInsertMessageToQueue() {
        return new InsertMessageToQueue();
    }

    @XmlElementDecl(namespace = "http://tags.trueffect.com/", name = "SessionKey", scope = InsertMessageToQueue.class)
    public JAXBElement<String> createInsertMessageToQueueSessionKey(String value) {
        return new JAXBElement<String>(_InsertMessageToQueueSessionKey_QNAME, String.class, InsertMessageToQueue.class, value);
    }

}
