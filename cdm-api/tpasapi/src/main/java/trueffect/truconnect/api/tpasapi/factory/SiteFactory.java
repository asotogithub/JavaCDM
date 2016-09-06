package trueffect.truconnect.api.tpasapi.factory;

import trueffect.truconnect.api.tpasapi.model.Site;

/**
 *
 * @author Abel Soto
 */
public class SiteFactory {

    private Site toTpasapiObject(trueffect.truconnect.api.commons.model.Site site) {
        Site result = new Site();
        result.setId(site.getId());
        result.setPublisherId(site.getPublisherId());
        result.setName(site.getName());
        result.setUrl(site.getUrl());
        result.setExternalId(site.getExternalId());
        result.setPreferredTag(site.getPreferredTag());
        result.setRichMedia(site.getRichMedia() != null && "Y".equals(site.getRichMedia()));
        result.setAcceptsFlash(site.getAcceptsFlash() != null && "Y".equals(site.getAcceptsFlash()));
        result.setClickTrack(site.getClickTrack() != null && "Y".equals(site.getClickTrack()));
        result.setEncode(site.getEncode() != null && "Y".equals(site.getEncode()));
        result.setTargetWin(site.getTargetWin());
        result.setAgencyNotes(site.getAgencyNotes());
        result.setPublisherNotes(site.getPublisherNotes());
        result.setCreatedDate(site.getCreatedDate());
        result.setModifiedDate(site.getModifiedDate());
        return result;
    }

    private trueffect.truconnect.api.commons.model.Site toPublicObject(Site site) {
        trueffect.truconnect.api.commons.model.Site result = new trueffect.truconnect.api.commons.model.Site();
        result.setId(site.getId());
        result.setPublisherId(site.getPublisherId());
        result.setName(site.getName());
        result.setUrl(site.getUrl());
        result.setExternalId(site.getExternalId());
        result.setPreferredTag(site.getPreferredTag() != null ? site.getPreferredTag() : "IFRAME");
        result.setRichMedia(site.getRichMedia() != null && site.getRichMedia() ? "Y" : "N");
        result.setAcceptsFlash(site.getAcceptsFlash() != null && site.getAcceptsFlash() ? "Y" : "N");
        result.setClickTrack(site.getClickTrack() != null && site.getClickTrack() ? "Y" : "N");
        result.setEncode(site.getEncode() != null && site.getEncode() ? "Y" : "N");
        result.setTargetWin(site.getTargetWin() != null ? site.getTargetWin() : "_blank");
        result.setAgencyNotes(site.getAgencyNotes());
        result.setPublisherNotes(site.getPublisherNotes());
        result.setCreatedDate(site.getCreatedDate());
        result.setModifiedDate(site.getModifiedDate());
        return result;
    }

    public static Site createTpasapiObject(trueffect.truconnect.api.commons.model.Site site) {
        SiteFactory factory = new SiteFactory();
        return factory.toTpasapiObject(site);
    }

    public static trueffect.truconnect.api.commons.model.Site createPublicObject(Site site) {
        SiteFactory factory = new SiteFactory();
        return factory.toPublicObject(site);
    }
}
