package trueffect.truconnect.api.crud.service.importexport;

import trueffect.truconnect.api.commons.AdminFile;
import trueffect.truconnect.api.commons.exceptions.business.enums.BusinessCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.SecurityCode;
import trueffect.truconnect.api.commons.model.Campaign;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.importexport.XLSTemplateDescriptor;
import trueffect.truconnect.api.commons.util.ExportImportUtil;
import trueffect.truconnect.api.commons.validation.BusinessValidationUtil;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.CampaignDao;
import trueffect.truconnect.api.crud.service.AbstractGenericManager;
import trueffect.truconnect.api.crud.service.ImportExportManager;

import org.apache.ibatis.session.SqlSession;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

/**
 * Created by richard.jaldin on 6/17/2016.
 */
public abstract class Exporter extends AbstractGenericManager {

    protected CampaignDao campaignDao;
    protected Long campaignId;
    protected OauthKey key;
    protected String objectType;
    protected String templatePath;
    protected Class<? extends XLSTemplateDescriptor> clazz;

    public Exporter(CampaignDao campaignDao, Long campaignId, OauthKey key, String objectType,
                    String templatePath, Class<? extends XLSTemplateDescriptor> clazz,
                    AccessControl accessControl) {
        super(accessControl);
        this.campaignDao = campaignDao;
        this.campaignId = campaignId;
        this.key = key;
        this.objectType = objectType;
        this.templatePath = templatePath;
        this.clazz = clazz;
    }

    public File export() {
        SqlSession session = campaignDao.openSession();
        List<? extends XLSTemplateDescriptor> records = null;
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
            String path = ImportExportManager.buildExportPath(campaignId, objectType);
            File folder = new File(path);
            // Create create directory if needed
            if (!folder.exists()) {
                folder.mkdirs();
            }
            filename = path + ImportExportManager.getTempFilename(campaign.getName(), campaignId) + "." + AdminFile.FileType.XLSX.getFileType();
            tempFilename = filename + "temp";

            SXSSFWorkbook wb = null;
            InputStream inp = null;
            try {
                inp = getClass().getResourceAsStream(templatePath);
                records = getData(session);
                wb = eiu.fromList(inp, records, clazz);
                ExportImportUtil.saveFile(wb, tempFilename);
                inp = new FileInputStream(tempFilename);
                final Path destination = Paths.get(filename);
                Files.copy(inp, destination);
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
                    log.warn("Could not close the input stream or workbook", e);
                }
            }
        } finally {
            campaignDao.close(session);
            if (tempFilename != null) {
                AdminFile.deleteFile(tempFilename);
            }
        }
        return file;
    }

    public abstract List<? extends XLSTemplateDescriptor> getData(SqlSession session);
}
