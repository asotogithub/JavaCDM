package trueffect.truconnect.api.tpasapi.proxy;

import trueffect.truconnect.api.commons.exceptions.ProxyException;
import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.commons.model.CreativeGroupCreative;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.tpasapi.factory.CreativeGroupFactory;
import trueffect.truconnect.api.tpasapi.model.CreativeGroup;
import trueffect.truconnect.api.tpasapi.model.CreativeGroupCreatives;
import trueffect.truconnect.api.tpasapi.model.RecordSet;
import trueffect.truconnect.api.tpasapi.validation.CreativeGroupValidator;

import com.sun.jersey.api.client.ClientResponse;
import org.springframework.validation.BeanPropertyBindingResult;

import javax.ws.rs.core.HttpHeaders;

/**
 *
 * @author Rambert Rioja
 */
public class CreativeGroupProxy extends BaseProxy<trueffect.truconnect.api.commons.model.CreativeGroup> {

    public CreativeGroupProxy(HttpHeaders headers) {
        super(trueffect.truconnect.api.commons.model.CreativeGroup.class, headers);
        path("CreativeGroups");
    }

    public CreativeGroup getCreativeGroup() throws ProxyException {
        CreativeGroup creativeGroup = CreativeGroupFactory.createTpasapiObject(get());
        return creativeGroup;
    }

    public CreativeGroup saveCreativeGroup(CreativeGroup creativeGroup) throws Exception {
        trueffect.truconnect.api.commons.model.CreativeGroup commonsCG = CreativeGroupFactory.createPublicObject(creativeGroup);
        CreativeGroupValidator validator = new CreativeGroupValidator();
        String className = commonsCG.getClass().getSimpleName();
        BeanPropertyBindingResult result = new BeanPropertyBindingResult(commonsCG, className);
        validator.validate(commonsCG, result);
        if (result.hasErrors()) {
            throw new ValidationException(result);
        }
        commonsCG = post(commonsCG);
        creativeGroup = CreativeGroupFactory.createTpasapiObject(commonsCG);
        return creativeGroup;
    }

    public CreativeGroup updateCreativeGroup(Long id, CreativeGroup creativeGroup) throws Exception {
        trueffect.truconnect.api.commons.model.CreativeGroup commonsCG = CreativeGroupFactory.createPublicObject(creativeGroup);
        path(Long.toString(id));
        CreativeGroupValidator validator = new CreativeGroupValidator();
        String className = commonsCG.getClass().getSimpleName();
        BeanPropertyBindingResult result = new BeanPropertyBindingResult(commonsCG, className);
        validator.validate(commonsCG, result);
        if (result.hasErrors()) {
            throw new ValidationException(result);
        }
        commonsCG = put(commonsCG);
        creativeGroup = CreativeGroupFactory.createTpasapiObject(commonsCG);
        return creativeGroup;
    }

    public RecordSet<CreativeGroup> getCreativeGroups(SearchCriteria searchCriteria) throws ProxyException {
        trueffect.truconnect.api.commons.model.RecordSet<trueffect.truconnect.api.commons.model.CreativeGroup> records = get(searchCriteria);
        RecordSet<CreativeGroup> creativeGroups;
        creativeGroups = CreativeGroupFactory.createTpasapiObjects(records);
        return creativeGroups;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public CreativeGroupCreatives getCreativeGroupCreatives(Long id) throws ProxyException {
        path(id.toString());
        path("creatives");
        ClientResponse response = header().get(ClientResponse.class);
        this.setLastResponse(response);
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            handleErrors(response);
        }

        trueffect.truconnect.api.commons.model.RecordSet<CreativeGroupCreative> records
                = (trueffect.truconnect.api.commons.model.RecordSet) response.getEntity(trueffect.truconnect.api.commons.model.RecordSet.class);

        CreativeGroupCreatives result = CreativeGroupFactory.getTpasapiCreativeGroupCreative(id, records.getRecords());
        return result;
    }
}
