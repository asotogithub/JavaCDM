package trueffect.truconnect.api.publik.client.proxy.impl;

import trueffect.truconnect.api.commons.model.Agency;
import trueffect.truconnect.api.commons.model.HtmlInjectionTags;
import trueffect.truconnect.api.commons.model.dto.PlacementActionTagAssocParam;
import trueffect.truconnect.api.commons.model.dto.PlacementSearchOptions;
import trueffect.truconnect.api.commons.model.htmlinjection.AdChoicesHtmlInjectionType;
import trueffect.truconnect.api.commons.model.htmlinjection.CustomInjectionType;
import trueffect.truconnect.api.commons.model.htmlinjection.FacebookCustomTrackingInjectionType;
import trueffect.truconnect.api.commons.model.Metrics;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.dto.BulkPublisherSiteSectionSize;
import trueffect.truconnect.api.commons.model.dto.CampaignDTO;
import trueffect.truconnect.api.commons.model.dto.HtmlInjectionTagAssociationDTO;
import trueffect.truconnect.api.commons.model.dto.PlacementFilterParam;
import trueffect.truconnect.api.commons.model.dto.PlacementView;
import trueffect.truconnect.api.commons.model.dto.adm.DatasetConfigView;
import trueffect.truconnect.api.commons.model.dto.UserView;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.publik.client.base.ContentType;
import trueffect.truconnect.api.publik.client.base.ServiceProxyImpl;
import trueffect.truconnect.api.publik.client.proxy.AuthenticationPublicServiceProxy;

import com.sun.jersey.api.client.ClientResponse;

public class AgencyProxyImpl extends ServiceProxyImpl<Agency> {

    public AgencyProxyImpl(Class<Agency> type, String baseUrl, String servicePath,
                           AuthenticationPublicServiceProxy authenticator) {
        super(type, baseUrl, servicePath, authenticator);
    }

    public BulkPublisherSiteSectionSize SaveBulkPublisherSiteSectionSize(
            BulkPublisherSiteSectionSize bulk, String ignoreDupSite) throws Exception {
        path("bulkPublisherSiteSectionSize");
        query("ignoreDupSite", ignoreDupSite);
        ClientResponse response =
                header().type(getContentType().getType()).accept(getContentType().getType())
                        .post(ClientResponse.class, bulk);
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.CREATED.getStatusCode()) {
            try {
                handleErrors(response);
            } catch (Exception e) {
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    BulkPublisherSiteSectionSize result =
                            SaveBulkPublisherSiteSectionSize(bulk, ignoreDupSite);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return getBulkPublisherSiteSectionSizeEntity(response);
    }

    public RecordSet<UserView> listTraffickingContacts(Long id) throws Exception {
        path(id.toString());
        path("traffickingUsers");
        String type = getContentType().getType();
        ClientResponse response = header().type(type).accept(type).get(ClientResponse.class);
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleNewErrors(response);
            } catch (Exception e) {
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    RecordSet<UserView> result = listTraffickingContacts(id);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return getOtherEntities(response);
    }

    private BulkPublisherSiteSectionSize getBulkPublisherSiteSectionSizeEntity(
            ClientResponse response) throws Exception {
        if (getContentType().getType().equalsIgnoreCase(ContentType.JSON.getType())) {
            String output = response.getEntity(String.class);
            return getJsonMapper().readValue(output, BulkPublisherSiteSectionSize.class);
        }
        return response.getEntity(BulkPublisherSiteSectionSize.class);
    }

    public RecordSet<CampaignDTO> getCampaigns(Long id) throws Exception {
        path(id.toString());
        path("campaigns");
        ClientResponse response =
                header().accept(getContentType().getType()).get(ClientResponse.class);
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleErrors(response);
            } catch (Exception e) {
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    RecordSet<CampaignDTO> result = getCampaigns(id);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return getCampaignsDtoListEntity(response);
    }

    private RecordSet<CampaignDTO> getCampaignsDtoListEntity(ClientResponse response)
            throws Exception {
        if (getContentType().getType().equalsIgnoreCase(ContentType.JSON.getType())) {
            String output = response.getEntity(String.class);
            return getJsonMapper().readValue(output, RecordSet.class);
        }
        return response.getEntity(RecordSet.class);
    }

    public RecordSet<Metrics> getCampaignMetrics(Long id) throws Exception {
        path(id.toString());
        path("metrics");
        path("campaigns");
        ClientResponse response =
                header().accept(getContentType().getType()).get(ClientResponse.class);
        return getOtherEntities(response);
    }

    public RecordSet<DatasetConfigView> getDatasets(Long agencyId) throws Exception {
        path(agencyId.toString());
        path("datasets");
        ClientResponse response =
                header().accept(getContentType().getType()).get(ClientResponse.class);
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleNewErrors(response);
            } catch (Exception e) {
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    RecordSet<DatasetConfigView> result = getDatasets(agencyId);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return getEntity(response, RecordSet.class);
    }

    public RecordSet<PlacementView> getPlacements(Long agencyId, String advertiserId,
                                                  String brandId, String campaignId,
                                                  String siteId, String sectionId,
                                                  String placementId, String levelType)
            throws Exception {
        path(agencyId.toString());
        path("placementView");
        query("levelType", levelType);
        query("advertiserId", advertiserId);
        query("brandId", brandId);
        if (siteId != null) {
            query("siteId", siteId);
        }
        if (sectionId != null) {
            query("sectionId", sectionId);
        }
        if (placementId != null) {
            query("placementId", placementId);
        }
        if (campaignId != null) {
            query("campaignId", campaignId);
        }
        ClientResponse response =
                header().accept(getContentType().getType()).get(ClientResponse.class);
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleNewErrors(response);
            } catch (Exception e) {
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    RecordSet<PlacementView> result =
                            getPlacements(agencyId, advertiserId, brandId, campaignId,
                                    siteId, sectionId, placementId, levelType);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return getOtherEntities(response);
    }

    public RecordSet<HtmlInjectionTags> getHtmlInjectionTags(Long id) throws Exception {
        path(id.toString());
        path("htmlInjectionTags");
        String type = getContentType().getType();
        ClientResponse response = header().type(type).accept(type).get(ClientResponse.class);
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleNewErrors(response);
            } catch (Exception e) {
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    RecordSet<HtmlInjectionTags> result = getHtmlInjectionTags(id);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return getOtherEntities(response);
    }

    public RecordSet<PlacementView> getServerSearchTagInjection(Long id, Long advertiserId, Long brandId,
                                                                PlacementSearchOptions searchOptions,
                                                                String pattern) throws Exception {
        path(id.toString());
        path("searchPlacementView");
        query("advertiserId", advertiserId.toString());
        query("brandId", brandId.toString());
        query("soCampaign", String.valueOf(searchOptions.isCampaign()));
        query("soSite", String.valueOf(searchOptions.isSite()));
        query("soSection", String.valueOf(searchOptions.isSection()));
        query("soPlacement", String.valueOf(searchOptions.isPlacement()));
        query("pattern", pattern);
        String type = getContentType().getType();
        ClientResponse response = header().type(type).accept(type).get(ClientResponse.class);
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleNewErrors(response);
            } catch (Exception e) {
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    RecordSet<PlacementView> result = getServerSearchTagInjection(id,advertiserId, brandId,
                            searchOptions, pattern);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return getOtherEntities(response);
    }

    public HtmlInjectionTagAssociationDTO getHtmlInjectionTagAssociation(Long id,
                                                                         PlacementFilterParam params)
            throws Exception {
        path(id.toString());
        path("htmlInjectionTagAssociation");
        query("levelType", params.getLevelType());
        query("campaignId", params.getCampaignId().toString());
        if (params.getSiteId() != null) {
            query("siteId", params.getSiteId().toString());
        }
        if (params.getSectionId() != null) {
            query("sectionId", params.getSectionId().toString());
        }
        if (params.getPlacementId() != null) {
            query("placementId", params.getPlacementId().toString());
        }
        String type = getContentType().getType();
        ClientResponse response = header().type(type).accept(type).get(ClientResponse.class);
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleNewErrors(response);
            } catch (Exception e) {
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    HtmlInjectionTagAssociationDTO result =
                            getHtmlInjectionTagAssociation(id, params);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return getEntity(response, HtmlInjectionTagAssociationDTO.class);
    }

    public SuccessResponse createHtmlInjectionTagAssociation(Long id, String advertiserId, String brandId,
                                                             PlacementActionTagAssocParam bulkCreate)
            throws Exception {
        path(id.toString());
        path("htmlInjectionTagAssociationsBulk");
        query("advertiserId", advertiserId);
        query("brandId", brandId);
        String type = getContentType().getType();
        ClientResponse response = header().type(type).accept(type).put(ClientResponse.class, bulkCreate);
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleNewErrors(response);
            } catch (Exception e) {
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    SuccessResponse result = createHtmlInjectionTagAssociation(id, advertiserId, brandId, bulkCreate);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return getSuccessEntity(response);
    }

    public HtmlInjectionTags createHtmlInjectionAdChoices(Long id,
                                                          AdChoicesHtmlInjectionType htmlInjectionAdChoices)
            throws Exception {
        path(id.toString());
        path("htmlInjectionTypeAdChoices");
        String type = getContentType().getType();
        ClientResponse response =
                header().type(type).accept(type).post(ClientResponse.class, htmlInjectionAdChoices);
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.CREATED.getStatusCode()) {
            try {
                handleNewErrors(response);
            } catch (Exception e) {
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    HtmlInjectionTags result =
                            createHtmlInjectionAdChoices(id, htmlInjectionAdChoices);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return getOtherEntity(response, HtmlInjectionTags.class);
    }

    public HtmlInjectionTags createHtmlInjectionFacebook(Long id,
                                                         FacebookCustomTrackingInjectionType htmlInjectionFacebook)
            throws Exception {
        path(id.toString());
        path("htmlInjectionTypeFacebook");
        String type = getContentType().getType();
        ClientResponse response =
                header().type(type).accept(type).post(ClientResponse.class, htmlInjectionFacebook);
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.CREATED.getStatusCode()) {
            try {
                handleNewErrors(response);
            } catch (Exception e) {
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    HtmlInjectionTags result =
                            createHtmlInjectionFacebook(id, htmlInjectionFacebook);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return getOtherEntity(response, HtmlInjectionTags.class);
    }

    public HtmlInjectionTags createHtmlInjectionCustomTag(Long id,
                                                          CustomInjectionType htmlInjectionTypeCustom)
            throws Exception {
        path(id.toString());
        path("htmlInjectionTypeCustom");
        String type = getContentType().getType();
        ClientResponse response =
                header().type(type).accept(type).post(ClientResponse.class, htmlInjectionTypeCustom);
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.CREATED.getStatusCode()) {
            try {
                handleNewErrors(response);
            } catch (Exception e) {
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    HtmlInjectionTags result =
                            createHtmlInjectionCustomTag(id, htmlInjectionTypeCustom);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return getOtherEntity(response, HtmlInjectionTags.class);
    }

    public SuccessResponse physicalDelete(Long id) throws Exception {
        path(id.toString());
        path("physical");
        return this.delete();
    }
}
