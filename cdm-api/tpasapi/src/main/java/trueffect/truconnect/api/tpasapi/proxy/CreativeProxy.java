package trueffect.truconnect.api.tpasapi.proxy;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.ClientResponse;

import org.apache.commons.lang.StringUtils;

import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.commons.validation.ApiValidationUtils;
import trueffect.truconnect.api.resources.ResourceBundleUtil;
import trueffect.truconnect.api.tpasapi.model.Creative;
import trueffect.truconnect.api.tpasapi.model.Clickthrough;
import trueffect.truconnect.api.tpasapi.model.RecordSet;
import trueffect.truconnect.api.tpasapi.factory.CreativeFactory;
import trueffect.truconnect.api.commons.exceptions.ProxyException;
import trueffect.truconnect.api.commons.model.SearchCriteria;

/**
 *
 * @author Rambert Rioja
 */
public class CreativeProxy extends BaseProxy<trueffect.truconnect.api.commons.model.Creative> {

    public CreativeProxy(HttpHeaders headers) {
        super(trueffect.truconnect.api.commons.model.Creative.class, headers);
        path("Creatives");
    }

    public Creative getCreative() throws ProxyException {
        trueffect.truconnect.api.commons.model.Creative creative = super.get();
        return CreativeFactory.createTpasapiObject(creative);
    }

    public RecordSet<Creative> getCreatives(SearchCriteria criteria) throws ProxyException {
        trueffect.truconnect.api.commons.model.RecordSet<trueffect.truconnect.api.commons.model.Creative> records = this.get(criteria);
        RecordSet<Creative> result = new RecordSet<Creative>();
        result.setPageSize(records.getPageSize());
        result.setStartIndex(records.getStartIndex());
        result.setTotalNumberOfRecords(records.getTotalNumberOfRecords());
        List<Creative> aux = new ArrayList<Creative>();
        for (trueffect.truconnect.api.commons.model.Creative record : records.getRecords()) {
            Creative item = CreativeFactory.createTpasapiObject(record);
            aux.add(item);
        }
        result.setRecords(aux);

        return result;
    }

    private trueffect.truconnect.api.commons.model.Creative translateCreativeTpasapiToCreativeCommons(Creative creative) {
        trueffect.truconnect.api.commons.model.Creative result = new trueffect.truconnect.api.commons.model.Creative();
        result.setId(creative.getId());
        result.setFilename(creative.getFilename());
        result.setAlias(creative.getAlias());
        result.setCreativeType(creative.getType());
        result.setHeight(creative.getHeight());
        result.setWidth(creative.getWidth());
        result.setCampaignId(creative.getCampaignId());
        result.setExtProp1(creative.getExtendedProperty1());
        result.setExtProp2(creative.getExtendedProperty2());
        result.setExtProp3(creative.getExtendedProperty3());
        result.setExtProp4(creative.getExtendedProperty4());
        result.setExtProp5(creative.getExtendedProperty5());
        result.setExternalId(creative.getExternalId());
        result.setPurpose(creative.getPurpose());
        result.setCreatedDate(creative.getCreatedDate());
        result.setModifiedDate(creative.getModifiedDate());
        trueffect.truconnect.api.commons.model.Clickthrough cl = new trueffect.truconnect.api.commons.model.Clickthrough();
        List<trueffect.truconnect.api.commons.model.Clickthrough> lcl = new ArrayList<trueffect.truconnect.api.commons.model.Clickthrough>();
        if(creative.getClickthroughs() != null) {
            for (Clickthrough click : creative.getClickthroughs()) {
                cl = new trueffect.truconnect.api.commons.model.Clickthrough();
                if (click.getSequence().longValue() == 1) {
                    result.setClickthrough(click.getUrl());
                    result.setScheduled(click.getSequence());
                } else {
                    cl.setSequence(click.getSequence());
                    cl.setUrl(click.getUrl());
                    lcl.add(cl);
                }
            }
        }
        result.setClickthroughs(lcl);
        result.setSwfClickCount(new Long(creative.getClickthroughs() != null ? creative.getClickthroughs().size() : 0));
        return result;
    }

    public File getImagen() throws Exception {
        WebResource.Builder builder = header().accept("text/xml");
        ClientResponse response = builder.get(ClientResponse.class);
        this.setLastResponse(response);
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            handleErrors(response);
        }
        File file = response.getEntity(File.class);
        return file;
    }

    public boolean updateImage(InputStream fileInStream, String filename) throws Exception {
        String disposition = "attachment; filename=\"" + filename + "\"";
        ClientResponse response = header().header("statusId", "1").type(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", disposition)
                .put(ClientResponse.class, fileInStream);
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            handleErrors(response);
        }
        return true;
    }

    public Creative updateCreative(Creative creative) throws Exception {
        trueffect.truconnect.api.commons.model.Creative result = translateCreativeTpasapiToCreativeCommons(creative);
        // Apply legacy validation that tpasapi had but public won't have anymore
        // 'filename' is now ignored by public as this field is never updated in DB
        // Still not sure why tpasapi wants this but doesn't make sense to me
        if (StringUtils.contains(result.getFilename(), ' ')) {
            String message = ResourceBundleUtil.getString("tpasapi.creative.error.updateFilenameWithSpaces");
            throw new ValidationException(message);
        }
        result = put(result);
        return CreativeFactory.createTpasapiObject(result);
    }
}
