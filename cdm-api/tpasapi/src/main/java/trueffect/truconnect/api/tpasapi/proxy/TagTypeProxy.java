package trueffect.truconnect.api.tpasapi.proxy;

import javax.ws.rs.core.HttpHeaders;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.ClientResponse;
import trueffect.truconnect.api.commons.exceptions.ProxyException;
import trueffect.truconnect.api.tpasapi.model.TagType;
import trueffect.truconnect.api.tpasapi.model.RecordSet;
import trueffect.truconnect.api.tpasapi.factory.TagTypeFactory;

/**
 *
 * @author Rambert Rioja
 */
public class TagTypeProxy extends BaseProxy<trueffect.truconnect.api.commons.model.delivery.TagType> {

    public TagTypeProxy(HttpHeaders headers) {
        super(trueffect.truconnect.api.commons.model.delivery.TagType.class, headers);
        path("Advertisers");
    }

    public RecordSet<TagType> getTagType() throws ProxyException {
        WebResource.Builder builder = header().accept(getContentType());
        ClientResponse response = builder.get(ClientResponse.class);
        this.setLastResponse(response);
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            handleErrors(response);
        }
        return TagTypeFactory.toTpasapiObject(getEntities(response));
    }
}
