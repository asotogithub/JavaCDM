package trueffect.truconnect.api.commons.exceptions.business;


import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * XML adapter that allows un/marshall any object type.
 * Created by Marcelo Heredia on 11/21/2014.
 */
public class AnyTypeAdapter extends XmlAdapter<Object,Object> {
    public Object unmarshal(Object v) { return v; }
    public Object marshal(Object v) { return v; }
}