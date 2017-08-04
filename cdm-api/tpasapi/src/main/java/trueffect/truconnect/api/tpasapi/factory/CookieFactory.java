package trueffect.truconnect.api.tpasapi.factory;

import java.util.List;
import java.util.ArrayList;

import trueffect.truconnect.api.tpasapi.model.Cookie;
import trueffect.truconnect.api.tpasapi.model.RecordSet;
import trueffect.truconnect.api.tpasapi.model.Value;

/**
 *
 * @author Richard Jaldin
 */
public class CookieFactory {

    public static RecordSet<Cookie> createTpasapiObjects(trueffect.truconnect.api.commons.model.RecordSet<trueffect.truconnect.api.commons.model.CookieTargetTemplate> cookies) {
        RecordSet<Cookie> records = new RecordSet<Cookie>();
        records.setPageSize(cookies.getPageSize());
        records.setStartIndex(cookies.getStartIndex());
        records.setTotalNumberOfRecords(cookies.getTotalNumberOfRecords());
        List<Cookie> aux = new ArrayList<Cookie>();
        Cookie cookie;
        CookieFactory factory = new CookieFactory();
        if (cookies.getRecords() != null) {
            for (trueffect.truconnect.api.commons.model.CookieTargetTemplate record : cookies.getRecords()) {
                cookie = factory.toTpasapiObject(record);
                aux.add(cookie);
            }
        }
        records.setRecords(aux);
        return records;
    }

    private Cookie toTpasapiObject(trueffect.truconnect.api.commons.model.CookieTargetTemplate cookieTargetTemplate) {
        Cookie cookie = new Cookie();
        cookie.setId(cookieTargetTemplate.getCookieTargetTemplateId());
        cookie.setCookieDomainId(cookieTargetTemplate.getCookieDomainId());
        cookie.setName(cookieTargetTemplate.getCookieName());
        
        //Build the type
        String type = null;
        switch (cookieTargetTemplate.getCookieContentType()) {
            case 1:
                type = "string";
                break;
            case 2:
                type = "string_list";
                break;
            case 3:
                type = "number";
                break;
            case 4:
                type = "number_list";
                break;
            case 5:
                type = "number_range";
                break;
        }
        cookie.setType(type);
        
        //Build the Values
        List<Value> values = new ArrayList<Value>();
        if(cookieTargetTemplate.getContentPossibleValues() != null) {
            String[] currentValues = cookieTargetTemplate.getContentPossibleValues().split("`");
            for (String cValue : currentValues) {
                Value val = new Value(cValue);
                values.add(val);
            }
        }
        cookie.setValues(values);

        return cookie;
    }

    private trueffect.truconnect.api.commons.model.CookieTargetTemplate toPublicObject(Cookie cookie) {
        trueffect.truconnect.api.commons.model.CookieTargetTemplate cookieTargetTemplate = new trueffect.truconnect.api.commons.model.CookieTargetTemplate();
        cookieTargetTemplate.setCookieTargetTemplateId(cookie.getId());
        cookieTargetTemplate.setCookieName(cookie.getName());
        cookieTargetTemplate.setCookieDomainId(cookie.getCookieDomainId());
        
        // Build the type
        Byte type = null;
        if("string".equals(cookie.getType())) {
            type = 1;
        } else if("string_list".equals(cookie.getType())) {
            type = 2;
        } else if("number".equals(cookie.getType())) {
            type = 3;
        } else if("number_list".equals(cookie.getType())) {
            type = 4;
        } else if("number_range".equals(cookie.getType())) {
            type = 5;
        }
        cookieTargetTemplate.setCookieContentType(type);
        
        // Build the Values
        String values = "";
        if(cookie.getValues() != null) {
            for (Value val : cookie.getValues()) {
                if(values.length() > 0) {
                    values += "`";
                }
                values += val.getText();
            }
        }
        cookieTargetTemplate.setContentPossibleValues(values);
        return cookieTargetTemplate;
    }
}
