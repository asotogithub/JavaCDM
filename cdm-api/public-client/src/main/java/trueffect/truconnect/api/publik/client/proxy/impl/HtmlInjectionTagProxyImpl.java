package trueffect.truconnect.api.publik.client.proxy.impl;

import trueffect.truconnect.api.commons.model.HtmlInjectionTags;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.commons.model.dto.PlacementSearchOptions;
import trueffect.truconnect.api.commons.model.dto.PlacementView;
import trueffect.truconnect.api.publik.client.base.ServiceProxyImpl;
import trueffect.truconnect.api.publik.client.proxy.AuthenticationPublicServiceProxy;
import com.sun.jersey.api.client.ClientResponse;

/**
 *  Created by Siamak Marjouei on 6/23/2016
 */
public class HtmlInjectionTagProxyImpl extends ServiceProxyImpl<HtmlInjectionTags> {

    public HtmlInjectionTagProxyImpl(Class<HtmlInjectionTags> type, String baseUrl, String servicePath,
                                     AuthenticationPublicServiceProxy authenticator) {
        super(type, baseUrl, servicePath, authenticator);
    }
    //TODO this method needs to disappear, handleErrors needs to be updated in parent to support new errors
    public HtmlInjectionTags updateWithId(Long id, HtmlInjectionTags input) throws Exception {
        path(id.toString());
        ClientResponse response = header().type(getContentType().getType()).accept(getContentType().getType())
                .put(ClientResponse.class, getPostData(input));
        this.setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleNewErrors(response);
            } catch (Exception e) {
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    HtmlInjectionTags result = updateWithId(id, input);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return getEntity(response);
    }

    public HtmlInjectionTags getById(Long id) throws Exception {
        path(id.toString());
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
                    HtmlInjectionTags result = getById(id);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return getEntity(response);
    }

    public RecordSet<PlacementView> getPlacementsByTagId(Long id) throws Exception {
        path(id.toString());
        path("placementAssociated");
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
                    RecordSet<PlacementView> result = getPlacementsByTagId(id);
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

    public RecordSet<PlacementView> getTagPlacementsByFilters(Long tagId, String pattern,
                                                              PlacementSearchOptions searchOptions) throws Exception {
        path(tagId.toString());
        path("searchPlacementsAssociated");
        query("soCampaign", String.valueOf(searchOptions.isCampaign()));
        query("soSite", String.valueOf(searchOptions.isSite()));
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
                    RecordSet<PlacementView> result = getTagPlacementsByFilters(tagId, pattern, searchOptions);
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

    public SuccessResponse deleteHtmlInjectionTagsBulk(RecordSet<Long> tagIds) throws Exception {
        path("bulkDelete");
        ClientResponse response = header().accept(getContentType().getType()).type(getContentType().getType())
                .put(ClientResponse.class, tagIds);
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleNewErrors(response);
            } catch (Exception e) {
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    SuccessResponse result = deleteHtmlInjectionTagsBulk(tagIds);
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

    public SuccessResponse physicalDelete(Long id) throws Exception {
        path(id.toString());
        path("physical");
        return this.delete();
    }
}
