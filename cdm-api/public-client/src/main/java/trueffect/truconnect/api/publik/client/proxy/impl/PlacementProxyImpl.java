package trueffect.truconnect.api.publik.client.proxy.impl;

import com.sun.jersey.api.client.ClientResponse;
import trueffect.truconnect.api.commons.model.HtmlInjectionTags;
import trueffect.truconnect.api.commons.model.Placement;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.commons.model.delivery.Tag;
import trueffect.truconnect.api.commons.model.delivery.TagEmail;
import trueffect.truconnect.api.commons.model.delivery.TagEmailResponse;
import trueffect.truconnect.api.commons.model.delivery.TagPlacement;
import trueffect.truconnect.api.publik.client.base.ServiceProxyImpl;
import trueffect.truconnect.api.publik.client.proxy.AuthenticationPublicServiceProxy;

/**
 * Created by Siamak Marjouei october/12/2015
 */
public class PlacementProxyImpl extends ServiceProxyImpl<Placement> {

    public PlacementProxyImpl(Class<Placement> type, String baseUrl, String servicePath, AuthenticationPublicServiceProxy authenticator) {
        super(type, baseUrl, servicePath, authenticator);
    }

    public RecordSet<Placement> bulkCreatePlacementForPackage(RecordSet<Placement> list, Long ioId, Long packageId) throws Exception {
        path("bulk");
        query("ioId", ioId.toString());
        query("packageId", packageId.toString());
        String type = getContentType().getType();
        ClientResponse response = header().type(type).accept(type).post(ClientResponse.class, list);
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleNewErrors(response);
            } catch (Exception e) {
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    RecordSet<Placement> result = bulkCreatePlacementForPackage(list, ioId, packageId);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return getEntities(response);
    }

    public RecordSet<Placement> bulkUpdatePlacements(RecordSet<Placement> list, Long ioId) throws Exception {
        path("status");
        query("ioId", ioId.toString());
        String type = getContentType().getType();
        ClientResponse response = header().type(type).accept(type).put(ClientResponse.class, list);
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleNewErrors(response);
            } catch (Exception e) {
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    RecordSet<Placement> result = bulkUpdatePlacements(list, ioId);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return getEntities(response);
    }

    public SuccessResponse bulkRemoveTagAssociations(RecordSet<Long> tagIds, Long placementId) throws Exception {
        path(placementId.toString());
        path("deleteHtmlInjectionTagsBulk");
        String type = getContentType().getType();
        ClientResponse response = header().type(type).accept(type).put(ClientResponse.class, tagIds);
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleNewErrors(response);
            } catch (Exception e) {
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    SuccessResponse result = bulkRemoveTagAssociations(tagIds, placementId);
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

    public SuccessResponse removePlacementFromPackage(Long placementId) throws Exception {
        path(placementId.toString());
        path("disassociateFromPackage");
        String type = getContentType().getType();
        ClientResponse response = header().type(type).accept(type).put(ClientResponse.class);
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleNewErrors(response);
            } catch (Exception e) {
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    SuccessResponse result = removePlacementFromPackage(placementId);
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

    public RecordSet<HtmlInjectionTags> getInjectionTagsById(Long id) throws Exception {
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
                    RecordSet<HtmlInjectionTags> result = getInjectionTagsById(id);
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

    public TagEmailResponse sendAdTagEmail(TagEmail te) throws Exception {
        path("sendTagEmail");
        String type = getContentType().getType();
        ClientResponse response = header().type(type).accept(type).post(ClientResponse.class, te);
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleNewErrors(response);
            } catch (Exception e) {
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    TagEmailResponse result = sendAdTagEmail(te);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return getEntity(response, TagEmailResponse.class);
    }

    public Tag getTagByPlacementAndType(Long placementId, Long tagId) throws Exception {
        path(Long.toString(placementId));
        path("Tag");
        path(Long.toString(tagId));
        String type = getContentType().getType();
        ClientResponse response = header().type(type).accept(type).get(ClientResponse.class);
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleErrors(response);
            } catch (Exception e) {
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    Tag result = getTagByPlacementAndType(placementId, tagId);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return getEntity(response, Tag.class);
    }

    public TagPlacement getAdTagsByPlacementId(Long placementId) throws Exception {
        path(Long.toString(placementId));
        path("getAdTagsByPlacement");
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
                    TagPlacement result = getAdTagsByPlacementId(placementId);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return getEntity(response, TagPlacement.class);
    }
}
