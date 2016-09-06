package trueffect.truconnect.api.publik.client.proxy;

import trueffect.truconnect.api.commons.model.HtmlInjectionTags;
import trueffect.truconnect.api.commons.model.Placement;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.commons.model.delivery.Tag;
import trueffect.truconnect.api.commons.model.delivery.TagEmail;
import trueffect.truconnect.api.commons.model.delivery.TagEmailResponse;
import trueffect.truconnect.api.commons.model.delivery.TagPlacement;
import trueffect.truconnect.api.publik.client.base.ServiceProxyImpl;
import trueffect.truconnect.api.publik.client.proxy.impl.PlacementProxyImpl;

public class PlacementPublicServiceProxy extends GenericPublicServiceProxy<Placement> {

    public PlacementPublicServiceProxy(String baseUrl, String authenticationUrl, String contentType, String userName, String password) throws Exception {
        super(baseUrl, authenticationUrl, "Placements", contentType, userName, password);
    }

    public Placement getById(int id) throws Exception {
        ServiceProxyImpl<Placement> proxy = getProxy();
        return proxy.getById(id);
    }

    public Placement create(Placement input) throws Exception {
        ServiceProxyImpl<Placement> proxy = getProxy();
        return proxy.save(input);
    }

    public RecordSet<Placement> bulkCreate(RecordSet<Placement> input, Long ioId, Long packageId) throws Exception {
        PlacementProxyImpl proxy = getProxy();
        return proxy.bulkCreatePlacementForPackage(input, ioId, packageId);
    }

    public SuccessResponse bulkRemoveAssociations(RecordSet<Long> tagIds, Long placementId) throws Exception {
        PlacementProxyImpl proxy = getProxy();
        return proxy.bulkRemoveTagAssociations(tagIds, placementId);
    }

    public RecordSet<Placement> bulkUpdate(RecordSet<Placement> input, Long ioId) throws Exception {
        PlacementProxyImpl proxy = getProxy();
        return proxy.bulkUpdatePlacements(input, ioId);
    }

    public SuccessResponse removePlacementFromPackage(Long placementId) throws Exception {
        PlacementProxyImpl proxy = getProxy();
        return proxy.removePlacementFromPackage(placementId);
    }

    public Placement update(Object id, Placement input) throws Exception {
        ServiceProxyImpl<Placement> proxy = getProxy();
        proxy.path(id.toString());
        return proxy.update(input);
    }

    public SuccessResponse delete(Placement input) throws Exception {
        ServiceProxyImpl<Placement> proxy = getProxy();
        return proxy.delete(input);
    }

    public RecordSet<Placement> find(String query, Long startIndex, Long pageSize) throws Exception {
        ServiceProxyImpl<Placement> proxy = getProxy();
        return proxy.find(query, startIndex, pageSize);
    }

    public RecordSet<Placement> find(String query) throws Exception {
        return this.find(query, null, null);
    }

    public RecordSet<Placement> find() throws Exception {
        return this.find(null);
    }

    public RecordSet<HtmlInjectionTags> getInjectionTagsById(Long placementId) throws Exception {
        PlacementProxyImpl proxy = getProxy();
        return proxy.getInjectionTagsById(placementId);
    }

    public TagEmailResponse sendAdTagEmail(TagEmail te) throws Exception {
        PlacementProxyImpl proxy = getProxy();
        return proxy.sendAdTagEmail(te);
    }

    public Tag getTagByPlacementAndType(Long placementId, Long tagId) throws Exception {
        PlacementProxyImpl proxy = getProxy();
        return proxy.getTagByPlacementAndType(placementId, tagId);
    }

    public TagPlacement getAdTagsByPlacementId(Long placementId) throws Exception {
        PlacementProxyImpl proxy = getProxy();
        return proxy.getAdTagsByPlacementId(placementId);
    }

    protected PlacementProxyImpl getProxy() {
        PlacementProxyImpl proxy = new PlacementProxyImpl(Placement.class, baseUrl, servicePath, authenticator);
        proxy.setContentType(contentType);
        return proxy;
    }
}
