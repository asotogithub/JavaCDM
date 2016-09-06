package trueffect.truconnect.api.tpasapi.factory;

import java.util.List;
import java.util.ArrayList;
import org.apache.commons.lang.StringUtils;
import trueffect.truconnect.api.tpasapi.model.Clickthrough;
import trueffect.truconnect.api.tpasapi.model.Creative;

/**
 *
 * @author Rambert Rioja
 */
public class CreativeFactory {

    private Creative toTpasapiObject(trueffect.truconnect.api.commons.model.Creative creative) {
        Creative result = new Creative();
        result.setId(creative.getId());
        result.setAlias(creative.getAlias());
        result.setCampaignId(creative.getCampaignId());
        result.setExtendedProperty1(creative.getExtProp1());
        result.setExtendedProperty2(creative.getExtProp2());
        result.setExtendedProperty3(creative.getExtProp3());
        result.setExtendedProperty4(creative.getExtProp4());
        result.setExtendedProperty5(creative.getExtProp5());
        result.setExternalId(creative.getExternalId());
        result.setFilename(creative.getFilename());
        result.setHeight(creative.getHeight());
        result.setWidth(creative.getWidth());
        result.setIsExpandable(creative.getIsExpandable() == 1);
        result.setPurpose(creative.getPurpose());
        result.setType(creative.getCreativeType());
        result.setCreatedDate(creative.getCreatedDate());
        result.setModifiedDate(creative.getModifiedDate());
        result.setClickthroughs(parseClickthroughs(creative));
        return result;
    }

    private List<Clickthrough> parseClickthroughs(trueffect.truconnect.api.commons.model.Creative creative) {
        List<Clickthrough> clickthroughs = null;
        if (StringUtils.isNotBlank(creative.getClickthrough())) {
            clickthroughs = new ArrayList<Clickthrough>();
            Clickthrough defaultCt = new Clickthrough(1L, creative.getClickthrough());
            clickthroughs.add(defaultCt);
        }
        if (creative.getClickthroughs() != null && creative.getClickthroughs().size() > 0) {
            if (clickthroughs == null) {
                clickthroughs = new ArrayList<Clickthrough>();
            }
            trueffect.truconnect.api.tpasapi.model.Clickthrough tpasapiCt;
            for (trueffect.truconnect.api.commons.model.Clickthrough clickthrough : creative.getClickthroughs()) {
                tpasapiCt = new trueffect.truconnect.api.tpasapi.model.Clickthrough();
                tpasapiCt.setSequence(clickthrough.getSequence());
                tpasapiCt.setUrl(clickthrough.getUrl());
                clickthroughs.add(tpasapiCt);
            }
        }
        return clickthroughs;
    }

    public static Creative createTpasapiObject(trueffect.truconnect.api.commons.model.Creative creative) {
        CreativeFactory factory = new CreativeFactory();
        return factory.toTpasapiObject(creative);
    }
}
