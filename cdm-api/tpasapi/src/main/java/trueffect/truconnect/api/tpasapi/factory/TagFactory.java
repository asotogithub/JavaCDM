package trueffect.truconnect.api.tpasapi.factory;

import trueffect.truconnect.api.tpasapi.model.Tag;


/**
 *
 * @author Abel Soto
 */
public class TagFactory {

    public static Tag toTpasapiObject(trueffect.truconnect.api.commons.model.delivery.Tag tag) {
        Tag type = new Tag();
        type.setTypeId(tag.getTypeId());
        type.setText(tag.getText());
        return type;
    }
}
