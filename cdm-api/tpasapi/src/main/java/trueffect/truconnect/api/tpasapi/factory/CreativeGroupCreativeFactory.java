package trueffect.truconnect.api.tpasapi.factory;

import java.util.ArrayList;
import java.util.List;
import trueffect.truconnect.api.tpasapi.model.CreativeGroupCreatives;
import trueffect.truconnect.api.tpasapi.model.Creative;

/**
 *
 * @author Gustavo Claure
 */
public class CreativeGroupCreativeFactory {

    public static trueffect.truconnect.api.commons.model.CreativeGroupCreative createPublicObject(CreativeGroupCreatives creativeGroupCreative) {
        CreativeGroupCreativeFactory factory = new CreativeGroupCreativeFactory();
        return factory.toPublicObject(creativeGroupCreative);
    }

    private trueffect.truconnect.api.commons.model.CreativeGroupCreative toPublicObject(CreativeGroupCreatives cgc) {
        trueffect.truconnect.api.commons.model.CreativeGroupCreative creativeGroupCreative = new trueffect.truconnect.api.commons.model.CreativeGroupCreative();

        creativeGroupCreative.setCreativeGroupId(cgc.getCreativeGroupId());
        creativeGroupCreative.setCreatives(getCommonsCreatives(cgc.getCreatives()));
        return creativeGroupCreative;
    }

    private List<trueffect.truconnect.api.commons.model.Creative> getCommonsCreatives(List<trueffect.truconnect.api.tpasapi.model.Creative> creatives) {

        List<trueffect.truconnect.api.commons.model.Creative> result = null;
        if (creatives != null) {
            result = new ArrayList<trueffect.truconnect.api.commons.model.Creative>();
            for (trueffect.truconnect.api.tpasapi.model.Creative cgc : creatives) {
                result.add(toCommonsObject(cgc));
            }
        }
        return result;
    }

    private trueffect.truconnect.api.commons.model.Creative toCommonsObject(Creative creative) {
        trueffect.truconnect.api.commons.model.Creative result = new trueffect.truconnect.api.commons.model.Creative();
        result.setId(creative.getId());
        return result;
    }
    
    
    public static CreativeGroupCreatives createTpasapiObject(trueffect.truconnect.api.commons.model.CreativeGroupCreative creativeGroupCreative) {
        CreativeGroupCreativeFactory factory = new CreativeGroupCreativeFactory();
        return factory.toTpasapiObject(creativeGroupCreative);
    }

    private CreativeGroupCreatives toTpasapiObject(trueffect.truconnect.api.commons.model.CreativeGroupCreative cgc) {
        CreativeGroupCreatives creativeGroup = new CreativeGroupCreatives();
        creativeGroup.setCreativeGroupId(cgc.getCreativeGroupId());
        List<trueffect.truconnect.api.tpasapi.model.Creative> result = null;
        if (cgc.getCreatives() != null) {
            result = new ArrayList<trueffect.truconnect.api.tpasapi.model.Creative>();
            for (trueffect.truconnect.api.commons.model.Creative creative : cgc.getCreatives()) {
                result.add(toTpasapiCreative(creative));
            }
        }
        creativeGroup.setCreatives(result);
        return creativeGroup;
    }
    
    private List<trueffect.truconnect.api.tpasapi.model.Creative> getTpasapiCreatives(List<trueffect.truconnect.api.commons.model.Creative> creatives) {
        List<trueffect.truconnect.api.tpasapi.model.Creative> result = null;
        if (creatives != null) {
            result = new ArrayList<trueffect.truconnect.api.tpasapi.model.Creative>();
            for (trueffect.truconnect.api.commons.model.Creative cgc : creatives) {
                result.add(toTpasapiCreative(cgc));
            }
        }
        return result;
    }
    
    private Creative toTpasapiCreative(trueffect.truconnect.api.commons.model.Creative creative) {
        return CreativeFactory.createTpasapiObject(creative);
    }
}
