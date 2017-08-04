package trueffect.truconnect.api.publik.client.proxy;

import trueffect.truconnect.api.commons.model.Agency;
import trueffect.truconnect.api.commons.model.HtmlInjectionTags;
import trueffect.truconnect.api.commons.model.Metrics;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.commons.model.dto.BulkPublisherSiteSectionSize;
import trueffect.truconnect.api.commons.model.dto.CampaignDTO;
import trueffect.truconnect.api.commons.model.dto.HtmlInjectionTagAssociationDTO;
import trueffect.truconnect.api.commons.model.dto.PlacementActionTagAssocParam;
import trueffect.truconnect.api.commons.model.dto.PlacementFilterParam;
import trueffect.truconnect.api.commons.model.dto.PlacementSearchOptions;
import trueffect.truconnect.api.commons.model.dto.PlacementView;
import trueffect.truconnect.api.commons.model.dto.adm.DatasetConfigView;
import trueffect.truconnect.api.commons.model.dto.UserView;
import trueffect.truconnect.api.commons.model.htmlinjection.AdChoicesHtmlInjectionType;
import trueffect.truconnect.api.commons.model.htmlinjection.CustomInjectionType;
import trueffect.truconnect.api.commons.model.htmlinjection.FacebookCustomTrackingInjectionType;
import trueffect.truconnect.api.publik.client.proxy.impl.AgencyProxyImpl;

/**
 * @author michelle.bowman
 */
public class AgencyPublicServiceProxy extends GenericPublicServiceProxy<Agency> {

    public AgencyPublicServiceProxy(String baseUrl, String authenticationUrl, String contentType,
                                    String userName, String password) throws Exception {
        super(baseUrl, authenticationUrl, "Agencies", contentType, userName, password);
    }

    public Agency getById(Long id) throws Exception {
        AgencyProxyImpl proxy = getProxy();
        return proxy.getById(id);
    }

    public Agency create(Agency input) throws Exception {
        AgencyProxyImpl proxy = getProxy();
        return proxy.save(input);
    }

    public Agency update(Object id, Agency input) throws Exception {
        AgencyProxyImpl proxy = getProxy();
        proxy.path(id.toString());
        return proxy.update(input);
    }

    public SuccessResponse delete(Agency input) throws Exception {
        AgencyProxyImpl proxy = getProxy();
        return proxy.delete(input);
    }

    public void physicalDelete(Long id) throws Exception {
        AgencyProxyImpl proxy = getProxy();
        proxy.physicalDelete(id);
    }

    public RecordSet<CampaignDTO> getCampaigns(Long id) throws Exception {
        AgencyProxyImpl proxy = getProxy();
        return proxy.getCampaigns(id);
    }

    public RecordSet<Metrics> getCampaignMetrics(Long id) throws Exception {
        AgencyProxyImpl proxy = getProxy();
        return proxy.getCampaignMetrics(id);
    }

    public RecordSet<UserView> listTraffickingContacts(Long id) throws Exception {
        AgencyProxyImpl proxy = getProxy();
        return proxy.listTraffickingContacts(id);
    }

    public BulkPublisherSiteSectionSize createBulkPublisherSiteSectionSize(
            BulkPublisherSiteSectionSize bulk, String ignoreDupSite) throws Exception {
        AgencyProxyImpl proxy = getProxy();
        return proxy.SaveBulkPublisherSiteSectionSize(bulk, ignoreDupSite);
    }

    protected AgencyProxyImpl getProxy() {
        AgencyProxyImpl proxy =
                new AgencyProxyImpl(Agency.class, baseUrl, servicePath, authenticator);
        proxy.setContentType(contentType);
        return proxy;
    }

    public RecordSet<DatasetConfigView> getDatasets(Long agencyId) throws Exception {
        AgencyProxyImpl proxy = getProxy();
        return proxy.getDatasets(agencyId);
    }

    public RecordSet<HtmlInjectionTags> getHtmlInjectionTags(Long agencyId) throws Exception {
        AgencyProxyImpl proxy = getProxy();
        return proxy.getHtmlInjectionTags(agencyId);
    }

    public HtmlInjectionTagAssociationDTO getHtmlInjectionTagAssociation(Long agencyId,
                                                                         PlacementFilterParam params)
            throws Exception {
        AgencyProxyImpl proxy = getProxy();
        return proxy.getHtmlInjectionTagAssociation(agencyId, params);
    }

    public SuccessResponse createHtmlInjectionTagAssociation(Long agencyId, String advertiserId, String brandId,
                                                             PlacementActionTagAssocParam bulkCreates)
            throws Exception {
        AgencyProxyImpl proxy = getProxy();
        return proxy.createHtmlInjectionTagAssociation(agencyId, advertiserId, brandId, bulkCreates);
    }

    public RecordSet<PlacementView> getServerSearchTagInjection(Long agencyId, Long advertiserId, Long brandId,
                                                                PlacementSearchOptions searchOptions, String pattern)
            throws Exception {
        AgencyProxyImpl proxy = getProxy();
        return proxy.getServerSearchTagInjection(agencyId, advertiserId, brandId, searchOptions, pattern);
    }

    public HtmlInjectionTags createHtmlInjectionAdChoices(Long agencyId,
                                                          AdChoicesHtmlInjectionType htmlInjectionAdChoices)
            throws Exception {
        AgencyProxyImpl proxy = getProxy();
        return proxy.createHtmlInjectionAdChoices(agencyId, htmlInjectionAdChoices);
    }

    public HtmlInjectionTags createHtmlInjectionFacebook(
            Long agencyId,
            FacebookCustomTrackingInjectionType htmlInjectionFacebook) throws Exception {
        AgencyProxyImpl proxy = getProxy();
        return proxy.createHtmlInjectionFacebook(agencyId, htmlInjectionFacebook);
    }

    public HtmlInjectionTags createHtmlInjectionCustomTag(
            Long agencyId,
            CustomInjectionType htmlInjectionTypeCustom) throws Exception {
        AgencyProxyImpl proxy = getProxy();
        return proxy.createHtmlInjectionCustomTag(agencyId, htmlInjectionTypeCustom);
    }

    public RecordSet<PlacementView> getPlacements(Long agencyId, String advertiserId,
                                                  String brandId, String campaignId,
                                                  String siteId, String sectionId,
                                                  String placementId, String levelType)
            throws Exception {
        AgencyProxyImpl proxy = getProxy();
        return proxy.getPlacements(agencyId, advertiserId, brandId, campaignId, siteId, sectionId,
                placementId, levelType);
    }
}
