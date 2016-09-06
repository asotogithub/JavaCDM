package trueffect.truconnect.api.publik.client.proxy.impl;

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
import trueffect.truconnect.api.publik.client.base.ContentType;
import trueffect.truconnect.api.publik.client.base.ServiceProxyImpl;
import trueffect.truconnect.api.publik.client.proxy.AuthenticationPublicServiceProxy;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.file.FileDataBodyPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import javax.ws.rs.core.MediaType;

public class CampaignProxyImpl extends ServiceProxyImpl<Campaign> {

    private Logger log;
    public CampaignProxyImpl(Class<Campaign> type, String baseUrl, String servicePath, AuthenticationPublicServiceProxy authenticator) {
        super(type, baseUrl, servicePath, authenticator);
        log = LoggerFactory.getLogger(this.getClass());
    }

    /**
     * Added 3rd test purpose endpoint
     */
    public Creative createCreativeImage(Long id, InputStream inputStream, String filename, boolean isExpandable, Long height, Long width) throws Exception {
        path(id.toString());
        path("Creative");
        query("filename", filename);
        if (isExpandable) {
            query("isExpandable", "true");
        }
        if ((height > 0L) && (width > 0L)) {
            query("height", Long.toString(height));
            query("width", Long.toString(width));
        }
        return uploadFileAndGetCreative(id, inputStream, filename, true);
    }

    public Creative uploadCreativeImageToTmp(Long id, InputStream inputStream, String filename, boolean isExpandable, Long height, Long width) throws Exception {
        path(id.toString());
        path("creativeUpload");
        query("filename", filename);
        if (isExpandable) {
            query("isExpandable", "true");
        }
        if ((height > 0L) && (width > 0L)) {
            query("height", Long.toString(height));
            query("width", Long.toString(width));
        }
        return uploadFileAndGetCreative(id, inputStream, filename, false);
    }

    private Creative uploadFileAndGetCreative(Long id, InputStream inputStream, String filename, boolean handleOldErrors)
            throws Exception {
        String disposition = "attachment; filename=\"" + filename + "\"";
        ClientResponse response = header().header("statusId", "1").accept(getContentType().getType())
                                          .type(MediaType.APPLICATION_OCTET_STREAM)
                                          .header("Content-Disposition", disposition)
                                          .post(ClientResponse.class, inputStream);
        setLastResponse(response);
        if (response.getStatus() != ClientResponse.Status.CREATED.getStatusCode()) {
            try {
                if (handleOldErrors) {
                    handleErrors(response);
                } else {
                    handleNewErrors(response);
                }
            } catch (Exception e) {
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    Creative result = uploadFileAndGetCreative(id, inputStream, filename, handleOldErrors);
                    isRetry = false; //clearing it after retry call has been done
                    return result;
                } else {
                    throw e;
                }
            }
        }
        clearAll();
        return getCreativeEntity(response);
    }

    private Creative getCreativeEntity(ClientResponse response) throws Exception {
        if (getContentType().getType().equalsIgnoreCase(ContentType.JSON.getType())) {
            String output = response.getEntity(String.class);
            return getJsonMapper().readValue(output, Creative.class);
        }
        return response.getEntity(Creative.class);
    }

    public RecordSet<SiteContactView> getSiteContacts(Long id) throws Exception {
        path(id.toString());
        path("siteContacts");
        ClientResponse response = header().accept(getContentType().getType()).get(ClientResponse.class);
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleNewErrors(response);
            } catch (Exception e) {
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    RecordSet<SiteContactView> result = getSiteContacts(id);
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

    public RecordSet<CreativeGroupCreativeView> getGroupCreatives(Long campaignId, String pivotType, String type,
            String siteId, String sectionId, String placementId, String groupId, String creativeId) throws Exception {
        path(campaignId.toString());
        path("creativeInsertions");
        path("groupCreatives");
        if (pivotType != null) {
            query("pivotType", pivotType);
        }
        if (type != null) {
            query("type", type);
        }
        if (siteId != null) {
            query("siteId", siteId);
        }
        if (sectionId != null) {
            query("sectionId", sectionId);
        }
        if (placementId != null) {
            query("placementId", placementId);
        }
        if (groupId != null) {
            query("groupId", groupId);
        }
        if (creativeId != null) {
            query("creativeId", creativeId);
        }
        ClientResponse response = header().accept(getContentType().getType()).get(ClientResponse.class);
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleNewErrors(response);
            } catch (Exception e) {
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    RecordSet<CreativeGroupCreativeView> result = getGroupCreatives(campaignId, pivotType, type, siteId, sectionId, placementId, groupId, creativeId);
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

    public RecordSet<PlacementView> getPlacements(Long campaignId, String pivotType, String type,
            String siteId, String sectionId, String placementId, String groupId, String creativeId) throws Exception {
        path(campaignId.toString());
        path("creativeInsertions");
        path("placements");
        if (pivotType != null) {
            query("pivotType", pivotType);
        }
        if (type != null) {
            query("type", type);
        }
        if (siteId != null) {
            query("siteId", siteId);
        }
        if (sectionId != null) {
            query("sectionId", sectionId);
        }
        if (placementId != null) {
            query("placementId", placementId);
        }
        if (groupId != null) {
            query("groupId", groupId);
        }
        if (creativeId != null) {
            query("creativeId", creativeId);
        }
        ClientResponse response = header().accept(getContentType().getType()).get(ClientResponse.class);
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleNewErrors(response);
            } catch (Exception e) {
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    RecordSet<PlacementView> result = getPlacements(campaignId, pivotType, type, siteId, sectionId, placementId, groupId, creativeId);
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

    public RecordSet<CreativeInsertionView> getCreativeInsertions(Long campaignId, String pivotType, String type) throws Exception {
        path(campaignId.toString());
        path("creativeInsertions");
        query("pivotType", pivotType);
        query("type", type);
        ClientResponse response = header().accept(getContentType().getType()).get(ClientResponse.class);
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleNewErrors(response);
            } catch (Exception e) {
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    RecordSet<CreativeInsertionView> result = getCreativeInsertions(campaignId, pivotType, type);
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

    public RecordSet<CreativeInsertionView> getCreativeInsertions(Long campaignId, String pivotType, String type, String siteId, String sectionId, String placementId, String groupId, String creativeId) throws Exception {
        path(campaignId.toString());
        path("creativeInsertions");
        query("pivotType", pivotType);
        query("type", type);
        if (siteId != null) {
            query("siteId", siteId);
        }
        if (sectionId != null) {
            query("sectionId", sectionId);
        }
        if (placementId != null) {
            query("placementId", placementId);
        }
        if (groupId != null) {
            query("groupId", groupId);
        }
        if (creativeId != null) {
            query("creativeId", creativeId);
        }
        ClientResponse response = header().accept(getContentType().getType()).get(ClientResponse.class);
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleNewErrors(response);
            } catch (Exception e) {
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    RecordSet<CreativeInsertionView> result = getCreativeInsertions(campaignId, pivotType, type, siteId, sectionId, placementId, groupId, creativeId);
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

    public RecordSet<CreativeInsertionView> getSearchCreativeInsertions(Long campaignId, String pivotType, Boolean soSite,
            Boolean soSection, Boolean soPlacement, Boolean soCreativeGroup,
            Boolean soCreative, String siteId, String sectionId, String placementId,
            String groupId, String creativeId, String type, String pattern) throws Exception {
        path(campaignId.toString());
        path("searchCreativeInsertions");
        query("pivotType", pivotType);
        query("soSite", soSite.toString());
        query("soSection", soSection.toString());
        query("soPlacement", soPlacement.toString());
        query("soCreativeGroup", soCreativeGroup.toString());
        query("soCreative", soCreative.toString());
        query("pattern", pattern.toString());
        if (siteId != null) {
            query("siteId", siteId);
        }
        if (sectionId != null) {
            query("sectionId", sectionId);
        }
        if (placementId != null) {
            query("placementId", placementId);
        }
        if (groupId != null) {
            query("groupId", groupId);
        }
        if (creativeId != null) {
            query("creativeId", creativeId);
        }
        if (type != null) {
            query("type", type);
        }
        ClientResponse response = header().accept(getContentType().getType()).get(ClientResponse.class);
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleNewErrors(response);
            } catch (Exception e) {
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    RecordSet<CreativeInsertionView> result = getSearchCreativeInsertions(campaignId, pivotType, soSite, soSection,
                            soPlacement, soCreativeGroup, soCreative, siteId, sectionId, placementId, groupId, creativeId, type, pattern);
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

    public SuccessResponse deleteCreativeInsertionsBulk(Long campaignId, RecordSet<CreativeInsertionFilterParam> input) throws Exception {
        path(campaignId.toString());
        path("creativeInsertionsBulkDelete");
        ClientResponse response = header().accept(getContentType().getType()).type(getContentType().getType()).put(ClientResponse.class, input);
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleNewErrors(response);
            } catch (Exception e) {
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    SuccessResponse result = deleteCreativeInsertionsBulk(campaignId, input);
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

    public RecordSet<CreativeGroupDtoForCampaigns> getCreativeGroupsForCampaignId(Long campaignId) throws Exception {
        path(campaignId.toString());
        path("creativeGroups");
        ClientResponse response = header().accept(getContentType().getType()).get(ClientResponse.class);
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleErrors(response);
            } catch (Exception e) {
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    RecordSet<CreativeGroupDtoForCampaigns> result = getCreativeGroupsForCampaignId(campaignId);
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

    public CampaignDetailsDTO getDetails(Long id) throws Exception {
        path(id.toString());
        path("detail");
        ClientResponse response = header().accept(getContentType().getType()).get(ClientResponse.class);
        return getOtherEntity(response, CampaignDetailsDTO.class);
    }

    public RecordSet<PackagePlacementView> findPackPlacementView(Long id) throws Exception {
        path(id.toString());
        path("packagePlacementView");
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
                    RecordSet<PackagePlacementView> result = findPackPlacementView(id);
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

    public RecordSet<Metrics> getCampaignMetrics(Long id) throws Exception {
        path(id.toString());
        path("metrics");
        ClientResponse response = header().accept(getContentType().getType()).get(ClientResponse.class);
        return getOtherEntities(response);
    }

    public File exportExcelFile(Long id, String fileType) throws Exception {
        path(id.toString());
        path("export");
        query("type", fileType);
        query("format", "xlsx");
        String type = getContentType().getType();
        ClientResponse response = header().accept(type)
                .accept("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .get(ClientResponse.class);
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleNewErrors(response);
            } catch (Exception e) {
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    File result = exportExcelFile(id, fileType);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return getFileEntity(response);
    }

    private File getFileEntity(ClientResponse response) throws Exception {
        if (getContentType().getType().equalsIgnoreCase(ContentType.JSON.getType())) {
            String output = response.getEntity(String.class);
            return getJsonMapper().readValue(output, File.class);
        }
        return response.getEntity(File.class);
    }

    public SuccessResponse uploadExcelFile(Long campaignId, File fileToUpload, String fileType) throws Exception {
        path(campaignId.toString());
        path("upload");
        query("type", fileType);
        FormDataMultiPart part = new FormDataMultiPart();
        if (fileToUpload != null) {
            part.bodyPart(new FileDataBodyPart("file", fileToUpload,
                    MediaType.APPLICATION_OCTET_STREAM_TYPE));
        }
        FormDataBodyPart p = new FormDataBodyPart(
                FormDataContentDisposition.name("file").fileName(fileToUpload.getName()).build(), "CONTENT");
        part.bodyPart(p);
        ClientResponse response = header()
                .accept(getContentType().getType())
                .type(MediaType.MULTIPART_FORM_DATA_TYPE)
                .post(ClientResponse.class, part);
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.CREATED.getStatusCode()) {
            try {
                handleNewErrors(response);
            } catch (Exception e) {
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    SuccessResponse result = uploadExcelFile(campaignId, fileToUpload, fileType);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return response.getEntity(SuccessResponse.class);
    }

    public SuccessResponse importExcelFile(Long id, String uuid, String ignoreErrors, String fileType, RecordSet<Action> actions) throws Exception {
        path(id.toString());
        path("import");
        query("type", fileType);
        query("uuid", uuid);
        query("ignoreErrors", ignoreErrors);
        String type = getContentType().getType();
        ClientResponse response = header().type(type).accept(type).put(ClientResponse.class, actions);
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                if (ignoreErrors.equals("true")) {
                    handleNewErrors(response);
                } else {
                    handleImportErrors(response);
                }
            } catch (Exception e) {
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    SuccessResponse result = importExcelFile(id, uuid, ignoreErrors, fileType, actions);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return response.getEntity(SuccessResponse.class);
    }

    public void handleImportErrors(ClientResponse response) throws Exception {
        String errorsAsString = "";
        try {
            List<Integer> failCodes = Arrays.asList(304, 400, 403, 404, 406, 409, 500, 503);
            if (response.hasEntity() && failCodes.contains(response.getStatus())) {
                errorsAsString = getErrorsAsString(response.getEntity(
                        trueffect.truconnect.api.commons.exceptions.business.ImportExportErrors.class));
            } else if (response.hasEntity()) {
                errorsAsString = response.getEntity(new GenericType<String>(String.class));
            }
        } catch (Exception e) {
            errorsAsString = "Something went wrong recovering the error. Status Code: " + response.getStatus();
        }

        throw getExceptionPerStatus(response.getStatus(), errorsAsString);
    }

    public File exportExcelImportIssuesFile(Long id, String uuid, String fileType) throws Exception {
        path(id.toString());
        path("issues");
        query("type", fileType);
        query("format", "xlsx");
        query("uuid", uuid);
        String type = getContentType().getType();
        ClientResponse response = header().accept(type)
                .accept("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .get(ClientResponse.class);
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleNewErrors(response);
            } catch (Exception e) {
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    File result = exportExcelImportIssuesFile(id, uuid, fileType);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return getFileEntity(response);
    }

    public Campaign createOldPathId(Object campaign) throws Exception {
        path("test");
        return save(campaign);
    }

    public RecordSet<Creative> getCreatives(Long campaignId) throws Exception {
        path(campaignId.toString());
        path("creatives");
        ClientResponse response = header().accept(getContentType().getType()).get(ClientResponse.class);
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleNewErrors(response);
            } catch (Exception e) {
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    RecordSet<Creative> result = getCreatives(campaignId);
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

    public RecordSet<Package> getPackages(Long campaignId, Long ioId) throws Exception {
        path(campaignId.toString());
        path("packages");
        query("ioId", ioId.toString());
        ClientResponse response =
                header().accept(getContentType().getType()).get(ClientResponse.class);
        setLastResponse(response);
        clearAll();
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleNewErrors(response);
            } catch (Exception e) {
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    RecordSet<Package> result = getPackages(campaignId, ioId);
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
}
