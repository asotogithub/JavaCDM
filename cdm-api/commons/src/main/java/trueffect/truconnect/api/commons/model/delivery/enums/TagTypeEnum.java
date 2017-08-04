package trueffect.truconnect.api.commons.model.delivery.enums;

import java.io.Serializable;

/**
 *
 * @author rodrigo.alarcon
 */
public enum TagTypeEnum implements Serializable {
    IFRAME,
    SCRIPT,
    CLICK_URL;
    
    @Override
    public String toString() {
        return name().toLowerCase();
    }
    
    public static TagTypeEnum parse(String name) {
        TagTypeEnum result;
        
        try {
            result = valueOf(TagTypeEnum.class, name);
        } catch (Exception e) {
            result = null;
        }
        
        return result;
    }
}
