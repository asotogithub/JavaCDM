package trueffect.truconnect.api.crud.service.importexport;

import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.enums.RateTypeEnum;
import trueffect.truconnect.api.commons.model.importexport.MediaRawDataView;
import trueffect.truconnect.api.commons.model.importexport.XLSTemplateDescriptor;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.CampaignDao;
import trueffect.truconnect.api.crud.dao.CostDetailDaoExtended;
import trueffect.truconnect.api.crud.dao.PlacementDao;

import org.apache.ibatis.session.SqlSession;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by richard.jaldin on 6/20/2016.
 */
public class MediaExporter extends Exporter {
    private PlacementDao placementDao;
    private CostDetailDaoExtended placementCostDetailDao;
    private CostDetailDaoExtended packageCostDetailDao;

    public MediaExporter(CampaignDao campaignDao,
                         PlacementDao placementDao,
                         CostDetailDaoExtended placementCostDetailDao,
                         CostDetailDaoExtended packageCostDetailDao,
                         Long campaignId,
                         OauthKey key,
                         String objectType, String templatePath,
                         Class<? extends XLSTemplateDescriptor> clazz,
                         AccessControl accessControl) {
        super(campaignDao, campaignId, key, objectType, templatePath, clazz, accessControl);
        this.placementDao = placementDao;
        this.placementCostDetailDao = placementCostDetailDao;
        this.packageCostDetailDao = packageCostDetailDao;
    }

    @Override
    public List<? extends XLSTemplateDescriptor> getData(SqlSession session) {
        List<MediaRawDataView> result = new ArrayList<>();

        // Get Packages and Placements
        List<MediaRawDataView> media = placementDao.getMediaForExport(campaignId, session);
        Set<Long> pIds = new HashSet<>();
        Set<Long> pkgIds = new HashSet<>();
        if(media != null && !media.isEmpty()) {
            for (MediaRawDataView mrdv : media) {
                if(mrdv.getMediaPackageId() != null) {
                    pkgIds.add(Long.valueOf(mrdv.getMediaPackageId()));
                } else if(mrdv.getPlacementId() != null) {
                    pIds.add(Long.valueOf(mrdv.getPlacementId()));
                }
            }

            // Get CostDetails
            List<MediaRawDataView> placementCosts =
                    placementCostDetailDao.getCostsForExport(new ArrayList<>(pIds), session);
            List<MediaRawDataView> packageCosts =
                    packageCostDetailDao.getCostsForExport(new ArrayList<>(pkgIds), session);

            // Populate de plain structure
            for (MediaRawDataView mrdv : media) {
                boolean isTheFirstRow = true;
                MediaRawDataView record = mrdv;
                if(mrdv.getMediaPackageId() != null) {
                    for (MediaRawDataView cost : packageCosts) {
                        if(cost.getMediaPackageId().equals(mrdv.getMediaPackageId())) {
                            if (!isTheFirstRow) {
                                record = new MediaRawDataView();
                            }
                            copyCosts(cost, record);
                            result.add(record);
                            isTheFirstRow = false;
                        }
                    }
                } else if(mrdv.getPlacementId() != null) {
                    for (MediaRawDataView cost : placementCosts) {
                        if(cost.getPlacementId().equals(mrdv.getPlacementId())) {
                            if (!isTheFirstRow) {
                                record = new MediaRawDataView();
                            }
                            copyCosts(cost, record);
                            result.add(record);
                            isTheFirstRow = false;
                        }
                    }
                }
            }
        }
        return result;
    }

    private void copyCosts(MediaRawDataView src, MediaRawDataView dst) {
        dst.setPlannedAdSpend(src.getPlannedAdSpend());
        dst.setInventory(src.getInventory());
        dst.setRate(src.getRate());
        dst.setRateType(RateTypeEnum.typeOf(Long.valueOf(src.getRateType())).name());
        dst.setStartDate(src.getStartDate());
        dst.setEndDate(src.getEndDate());
    }
}
