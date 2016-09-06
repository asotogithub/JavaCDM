package trueffect.truconnect.api.crud.service.importexport;

import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.importexport.XLSTemplateDescriptor;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.CampaignDao;
import trueffect.truconnect.api.crud.dao.CreativeInsertionDao;

import org.apache.ibatis.session.SqlSession;

import java.util.List;

/**
 * Created by richard.jaldin on 6/17/2016.
 */
public class CreativeInsertionExporter extends Exporter {
    private CreativeInsertionDao creativeInsertionDao;

    public CreativeInsertionExporter(CampaignDao campaignDao,
                                     CreativeInsertionDao creativeInsertionDao,
                                     Long campaignId,
                                     OauthKey key,
                                     String objectType, String templatePath,
                                     Class<? extends XLSTemplateDescriptor> clazz,
                                     AccessControl accessControl) {
        super(campaignDao, campaignId, key, objectType, templatePath, clazz, accessControl);
        this.creativeInsertionDao = creativeInsertionDao;
    }

    @Override
    public List<? extends XLSTemplateDescriptor> getData(SqlSession session) {
        return creativeInsertionDao.getCreativeInsertionsToExport(this.campaignId, session);
    }
}
