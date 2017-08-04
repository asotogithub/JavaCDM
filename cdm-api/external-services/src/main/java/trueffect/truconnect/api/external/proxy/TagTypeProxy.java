package trueffect.truconnect.api.external.proxy;

import java.util.List;
import java.util.ArrayList;
import javax.ws.rs.core.HttpHeaders;
import com.fasterxml.jackson.databind.ObjectMapper;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.delivery.TagType;
import trueffect.truconnect.api.commons.exceptions.ProxyException;

/**
 *
 * @author Rambert Rioja
 */
public class TagTypeProxy extends BaseDeliveryProxy<List> {

    private ObjectMapper oMapper;

    public TagTypeProxy(HttpHeaders headers) {
        super(List.class, headers);
        path("tag");
        this.oMapper = new ObjectMapper();
    }

    public RecordSet<TagType> getTagTypes() throws Exception, ProxyException {
        setContentType("application/json");
        List list = super.get();
        List<TagType> tags = new ArrayList<TagType>();
        for (Object jsonObject : list) {
            String json = oMapper.writeValueAsString(jsonObject);
            tags.add(oMapper.readValue(json, TagType.class));
        }
        return new RecordSet<TagType>(1, tags.size(), tags.size(), tags);
    }
    
    public RecordSet<TagType> getTagTypes(int advertiserId, int siteId) throws Exception, ProxyException {
        reset();
        
        path("tag");
        path("GetTagTypes");
        query("AdvertiserId", Integer.toString(advertiserId));
        query("SiteId", Integer.toString(siteId));
        
        return getTagTypes();
    }
}
