package trueffect.truconnect.api.crud.service.importexport;

import trueffect.truconnect.api.commons.AdminFile;
import trueffect.truconnect.api.commons.exceptions.business.enums.BusinessCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.SecurityCode;
import trueffect.truconnect.api.commons.model.Campaign;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.importexport.XLSTemplateDescriptor;
import trueffect.truconnect.api.commons.model.importexport.XlsErrorTemplateDescriptor;
import trueffect.truconnect.api.commons.util.ExportImportUtil;
import trueffect.truconnect.api.commons.util.SerializationUtil;
import trueffect.truconnect.api.commons.validation.BusinessValidationUtil;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.CampaignDao;
import trueffect.truconnect.api.crud.service.ImportExportManager;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by richard.jaldin on 7/4/2016.
 */
public class IssuesExporter extends Exporter {

    private String uuid;

    public IssuesExporter(CampaignDao campaignDao, Long campaignId, String uuid, OauthKey key,
                          String objectType, String templatePath,
                          Class<? extends XLSTemplateDescriptor> clazz, AccessControl accessControl) {
        super(campaignDao, campaignId, key, objectType, templatePath, clazz, accessControl);
        this.uuid = uuid;
    }

    @Override
    public File export() {
        SqlSession session = campaignDao.openSession();
        List<? extends XlsErrorTemplateDescriptor> records = null;
        String path;
        String filename;
        String tempFilename = null;
        File file = null;
        ExportImportUtil eiu = new ExportImportUtil();
        try {
            //check access control
            if (!userValidFor(AccessStatement.CAMPAIGN, Collections.singletonList(campaignId), key.getUserId(), session)) {
                throw BusinessValidationUtil
                        .buildAccessSystemException(SecurityCode.NOT_FOUND_FOR_USER);
            }
            //obtain campaign to set file name
            Campaign campaign = campaignDao.get(campaignId, session);
            path = ImportExportManager.buildExportPath(campaignId, objectType);
            File folder = new File(path);
            // Create create directory if needed
            if (!folder.exists()) {
                folder.mkdirs();
            }
            filename = path + ImportExportManager.getTempFilename(campaign.getName(), campaignId) + "_errors" + "." + AdminFile.FileType.XLSX.getFileType();
            tempFilename = filename + "issues";

            String tempPathExcelByCampaign = ImportExportManager.buildImportPath(campaignId,objectType);
            String tempPathObjFile = tempPathExcelByCampaign + uuid + "." + AdminFile.FileType.OBJ.getFileType();
            records = getData(tempPathObjFile);

            SXSSFWorkbook wb = null;
            InputStream inp = null;
            try {
                inp = getClass().getResourceAsStream(templatePath);
                wb = eiu.fromList(inp, records, clazz);
                ExportImportUtil.saveFile(wb, tempFilename);
                inp = new FileInputStream(tempFilename);
                final Path destination = Paths.get(filename);
                Files.copy(inp, destination);
                inp.close();
                file = new File(filename);
            } catch (Exception ex) {
                throw BusinessValidationUtil.buildBusinessSystemException(ex, BusinessCode.INTERNAL_ERROR, "");
            } finally {
                try {
                    if (inp != null) {
                        inp.close();
                    }
                    if (wb != null) {
                        wb.close();
                    }
                } catch (IOException e) {
                    log.warn("Could not close the input stream or workbook for errors", e);
                }
            }
        } finally {
            campaignDao.close(session);
            if (tempFilename != null) {
                AdminFile.deleteFile(tempFilename);
            }
            if (records != null) {
                records.clear();
            }
        }
        return file;
    }

    @Override
    public final List<? extends XLSTemplateDescriptor> getData(SqlSession session) {
        return null;
    }

    public List<? extends XlsErrorTemplateDescriptor> getData(String tempPathObjFile) {
        List<XlsErrorTemplateDescriptor> result = new ArrayList<>();
        List<? extends XlsErrorTemplateDescriptor> records =
                SerializationUtil.deserialize(tempPathObjFile);
        try {
            // get only the records with errors
            if (records != null && !records.isEmpty()) {
                for (XlsErrorTemplateDescriptor record : records) {
                    if (StringUtils.isNotBlank(record.getReason())) {
                        result.add(record);
                    }
                }
            } else { // There is no issues to export
                throw BusinessValidationUtil.buildAccessSystemException(BusinessCode.NOT_FOUND);
            }
        } finally {
            if(records != null) {
                records.clear();
            }
        }
        return result;
    }
}
