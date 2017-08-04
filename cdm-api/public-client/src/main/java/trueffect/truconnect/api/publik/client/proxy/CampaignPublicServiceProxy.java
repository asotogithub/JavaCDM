package trueffect.truconnect.api.publik.client.proxy;

import trueffect.truconnect.api.commons.model.Campaign;
import trueffect.truconnect.api.commons.model.Creative;
import trueffect.truconnect.api.commons.model.Metrics;
import trueffect.truconnect.api.commons.model.Package;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.commons.model.dto.CampaignDetailsDTO;
import trueffect.truconnect.api.commons.model.dto.CreativeGroupCreativeView;
import trueffect.truconnect.api.commons.model.dto.CreativeGroupDtoForCampaigns;
import trueffect.truconnect.api.commons.model.dto.CreativeInsertionFilterParam;
import trueffect.truconnect.api.commons.model.dto.CreativeInsertionView;
import trueffect.truconnect.api.commons.model.dto.PackagePlacementView;
import trueffect.truconnect.api.commons.model.dto.PlacementView;
import trueffect.truconnect.api.commons.model.dto.SiteContactView;
import trueffect.truconnect.api.commons.model.importexport.Action;
import trueffect.truconnect.api.publik.client.proxy.impl.CampaignProxyImpl;

import java.io.File;
import java.io.FileInputStream;

public class CampaignPublicServiceProxy extends GenericPublicServiceProxy<Campaign> {

    public CampaignPublicServiceProxy(String baseUrl, String authenticationUrl, String contentType, String userName, String password) throws Exception {
        super(baseUrl, authenticationUrl, "Campaigns", contentType, userName, password);
    }

    public Campaign getById(Long id) throws Exception {
        CampaignProxyImpl proxy = getProxy();
        return proxy.getById(id);
    }

    public Campaign create(Campaign input) throws Exception {
        CampaignProxyImpl proxy = getProxy();
        return proxy.save(input);
    }

    public Campaign createOldPathId(Campaign input) throws Exception {
        CampaignProxyImpl proxy = getProxy();
        return proxy.createOldPathId(input);
    }

    public Campaign update(Object id, Campaign input) throws Exception {
        CampaignProxyImpl proxy = getProxy();
        proxy.path(id.toString());
        return proxy.update(input);
    }

    public Creative createCreativeImage(Long id, String imageFolderBasePath, String filename, boolean isExpandable, Long height, Long width) throws Exception {
        CampaignProxyImpl proxy = getProxy();
        File file = new File(imageFolderBasePath + filename);
        try (FileInputStream inputStream = new FileInputStream(file)) {
            return proxy.createCreativeImage(id, inputStream, filename, isExpandable, height, width);
        }
    }

    public Creative uploadCreativeImageToTmp(Long id, String imageFolderBasePath, String filename, boolean isExpandable, Long height, Long width) throws Exception {
        CampaignProxyImpl proxy = getProxy();
        File file = new File(imageFolderBasePath + filename);
        try (FileInputStream inputStream = new FileInputStream(file)) {
            return proxy.uploadCreativeImageToTmp(id, inputStream, filename, isExpandable, height, width);
        }
    }

    public RecordSet<CreativeGroupCreativeView> getGroupCreatives(Long campaignId, String pivotType, String type,
                                                  String siteId, String sectionId, String placementId, String groupId, String creativeId) throws Exception {
        CampaignProxyImpl proxy = getProxy();
        return proxy.getGroupCreatives(campaignId, pivotType, type, siteId, sectionId, placementId, groupId, creativeId);
    }

    public RecordSet<PlacementView> getPlacements(Long campaignId, String pivotType, String type,
                                                  String siteId, String sectionId, String placementId, String groupId, String creativeId) throws Exception {
        return getProxy().getPlacements(campaignId, pivotType, type, siteId, sectionId, placementId, groupId, creativeId);
    }

    public RecordSet<CreativeGroupDtoForCampaigns> getCreativeGroupsForCampaign(Long campaignId) throws Exception {
        CampaignProxyImpl proxy = getProxy();
        return proxy.getCreativeGroupsForCampaignId(campaignId);
    }

    public RecordSet<Metrics> getCampaignMetrics(Long campaignId) throws Exception {
        CampaignProxyImpl proxy = getProxy();
        return proxy.getCampaignMetrics(campaignId);
    }

    public RecordSet<SiteContactView> getSiteContacts(Long id) throws Exception {
        return getProxy().getSiteContacts(id);
    }

    public RecordSet<PackagePlacementView> findPackPlacementView(Long id) throws Exception {
        return getProxy().findPackPlacementView(id);
    }

    public CampaignDetailsDTO getDetails(Long id) throws Exception {
        return getProxy().getDetails(id);
    }

    public SuccessResponse deleteCreativeInsertionsBulk(Long campaignId, RecordSet<CreativeInsertionFilterParam> input) throws Exception {
        return getProxy().deleteCreativeInsertionsBulk(campaignId, input);
    }

    public RecordSet<CreativeInsertionView> getCreativeInsertions(long campaignId, String pivotType, String type) throws Exception {
        return getProxy().getCreativeInsertions(campaignId, pivotType, type);
    }

    public RecordSet<CreativeInsertionView> getCreativeInsertions(long campaignId, String pivotType, String type, String siteId, String sectionId, String placementId, String groupId, String creativeId) throws Exception {
        return getProxy().getCreativeInsertions(campaignId, pivotType, type, siteId, sectionId, placementId, groupId, creativeId);
    }

    public RecordSet<CreativeInsertionView> getSearchCreativeInsertions(long campaignId, String pivotType, Boolean soSite, Boolean soSection, Boolean soPlacement,
                                                                        Boolean soCreativeGroup, Boolean soCreative, String siteId, String sectionId, String placementId,
                                                                        String groupId, String creativeId, String type, String pattern) throws Exception {
        return getProxy().getSearchCreativeInsertions(campaignId, pivotType, soSite, soSection,
                soPlacement, soCreativeGroup, soCreative, siteId, sectionId, placementId, groupId,
                creativeId, type, pattern);
    }

    public File exportExcelFile(Long id, String fileType) throws Exception {
        return getProxy().exportExcelFile(id, fileType);
    }

    public RecordSet<CreativeGroupDtoForCampaigns> getCreativeGroups(Long campaignId) throws Exception {
        return getProxy().getCreativeGroupsForCampaignId(campaignId);
    }

    public SuccessResponse uploadExcelFile(Long campaignId, File file, String fileType) throws Exception {
        return getProxy().uploadExcelFile(campaignId, file, fileType);
    }

    public SuccessResponse importExcelFile(Long id, String uuid, String ignoreErrors, String fileType, RecordSet<Action> actions) throws Exception {
        return getProxy().importExcelFile(id, uuid, ignoreErrors, fileType, actions);
    }

    public File exportExcelImportIssuesFile(Long id, String uuid, String fileType) throws Exception {
        return getProxy().exportExcelImportIssuesFile(id, uuid, fileType);
    }

    public RecordSet<Creative> getCreativesForCampaign(Long campaignId) throws Exception {
        return getProxy().getCreatives(campaignId);
    }

    public RecordSet<Package> getPackages(Long campaignId, Long ioId) throws Exception {
        return getProxy().getPackages(campaignId, ioId);
    }

    protected CampaignProxyImpl getProxy() {
        CampaignProxyImpl proxy = new CampaignProxyImpl(Campaign.class, baseUrl, servicePath, authenticator);
        proxy.setContentType(contentType);
        return proxy;
    }
}
