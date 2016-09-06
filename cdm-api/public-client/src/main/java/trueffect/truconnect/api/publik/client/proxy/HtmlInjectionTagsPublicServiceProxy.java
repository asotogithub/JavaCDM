package trueffect.truconnect.api.publik.client.proxy;

import trueffect.truconnect.api.commons.model.HtmlInjectionTags;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.commons.model.dto.PlacementSearchOptions;
import trueffect.truconnect.api.commons.model.dto.PlacementView;
import trueffect.truconnect.api.publik.client.proxy.impl.HtmlInjectionTagProxyImpl;

/**
 * @author Siamak Marjouei
 */
public class HtmlInjectionTagsPublicServiceProxy extends GenericPublicServiceProxy<HtmlInjectionTags> {

    public HtmlInjectionTagsPublicServiceProxy(String baseUrl, String authenticationUrl, String contentType,
                                               String userName, String password) throws Exception {
        super(baseUrl, authenticationUrl, "HtmlInjectionTags", contentType, userName, password);
    }

    public HtmlInjectionTags getById(Long id) throws Exception {
        HtmlInjectionTagProxyImpl proxy = getProxy();
        return proxy.getById(id);
    }

    public RecordSet<PlacementView> getPlacementsByTagId(Long id) throws Exception {
        HtmlInjectionTagProxyImpl proxy = getProxy();
        return proxy.getPlacementsByTagId(id);
    }

    public RecordSet<PlacementView> getPlacementsByCriteria(Long tagId, String pattern,
                                                            PlacementSearchOptions searchOptions) throws Exception {
        HtmlInjectionTagProxyImpl proxy = getProxy();
        return proxy.getTagPlacementsByFilters(tagId, pattern, searchOptions);
    }

    public HtmlInjectionTags updateWithId(Long id, HtmlInjectionTags input) throws Exception {
        HtmlInjectionTagProxyImpl proxy = getProxy();
        return proxy.updateWithId(id, input);
    }

    public SuccessResponse deleteHtmlInjectionTagsBulk(RecordSet<Long> tagIds) throws Exception {
        HtmlInjectionTagProxyImpl proxy = getProxy();
        return proxy.deleteHtmlInjectionTagsBulk(tagIds);
    }
    protected HtmlInjectionTagProxyImpl getProxy() {
        HtmlInjectionTagProxyImpl proxy =
                new HtmlInjectionTagProxyImpl(HtmlInjectionTags.class, baseUrl, servicePath, authenticator);
        proxy.setContentType(contentType);
        return proxy;
    }


}
