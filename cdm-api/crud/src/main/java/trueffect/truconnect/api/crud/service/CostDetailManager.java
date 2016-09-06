package trueffect.truconnect.api.crud.service;

import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.exceptions.business.ValidationError;
import trueffect.truconnect.api.commons.exceptions.business.enums.ValidationCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.BusinessCode;
import trueffect.truconnect.api.commons.model.CostDetail;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.enums.CostDetailType;
import trueffect.truconnect.api.commons.model.enums.DeltaActionEnum;
import trueffect.truconnect.api.commons.model.enums.RateTypeEnum;
import trueffect.truconnect.api.commons.util.CostDetailComparator;
import trueffect.truconnect.api.commons.util.DateConverter;
import trueffect.truconnect.api.commons.exceptions.business.enums.SecurityCode;
import trueffect.truconnect.api.commons.validation.BusinessValidationUtil;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.CostDetailDaoBase;
import trueffect.truconnect.api.crud.dao.CostDetailDaoExtended;
import trueffect.truconnect.api.crud.util.PackagePlacementUtil;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import org.apache.ibatis.session.SqlSession;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Manages Cost Details for both Package and Placement
 * Created by richard.jaldin on 10/8/2015.
 */
public class CostDetailManager extends AbstractGenericManager {

    private static final boolean UPDATE_ON_IMPORT = true;

    private CostDetailDaoExtended costDetailDao;
    private CostDetailDaoBase dimCostDetailDao;
    private AccessStatement childrenAccessStatement;
    private AccessStatement parentAccessStatement;

    public CostDetailManager(CostDetailDaoExtended costDetailDao, CostDetailDaoBase dimCostDetailDao,
            CostDetailType costDetailType, AccessControl accessControl) {
        super(accessControl);
        this.costDetailDao = costDetailDao;
        this.dimCostDetailDao = dimCostDetailDao;
        if(costDetailType == CostDetailType.PACKAGE) {
            this.parentAccessStatement = AccessStatement.PACKAGE;
            this.childrenAccessStatement = AccessStatement.PACKAGE_COST_DETAIL;
        } else {
            this.parentAccessStatement = AccessStatement.PLACEMENT;
            this.childrenAccessStatement = AccessStatement.PLACEMENT_COST_DETAIL;
        }
    }

    public List<CostDetail> updateCostDetails(List<CostDetail> newCosts, Long foreignId, OauthKey key, SqlSession session, SqlSession dimSession){
        return this.updateCostDetails(newCosts, foreignId, key, session, dimSession, !UPDATE_ON_IMPORT);
    }

    public List<CostDetail> updateCostDetailsOnImport(List<CostDetail> newCosts, Long foreignId, OauthKey key, SqlSession session, SqlSession dimSession){
        return this.updateCostDetails(newCosts, foreignId, key, session, dimSession, UPDATE_ON_IMPORT);
    }

    private List<CostDetail> updateCostDetails(List<CostDetail> newCosts, Long foreignId, OauthKey key, SqlSession session, SqlSession dimSession, boolean isUpdateOnImport) {

        // nullability checks
        if (foreignId == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Foreign id"));
        }

        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OauthKey"));
        }

        if (session == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "SqlSession"));
        }

        if (dimSession == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Dim SqlSession"));
        }

        List<CostDetail> result = new ArrayList<>();
        // Get existing CostDetails
        if(!userValidFor(parentAccessStatement, Collections.singletonList(foreignId), key.getUserId(), session)) {
            throw BusinessValidationUtil
                .buildAccessSystemException(SecurityCode.NOT_FOUND_FOR_USER);
        }
        List<CostDetail> existing = costDetailDao.getAll(foreignId, session);

        // 2. Delta processing
        // 2.1. To delete (existing - news)
        final List<CostDetail> toDelete = new ArrayList<>();
        toDelete.addAll(existing);
        toDelete.removeAll(newCosts);

        // 2.2. To Add (news - existing)
        final List<CostDetail> toAdd = new ArrayList<>();
        toAdd.addAll(newCosts);
        toAdd.removeAll(existing);

        // 2.3. To update (news - toAdd)
        final List<CostDetail> toUpdate = new ArrayList<>();
        toUpdate.addAll(newCosts);
        toUpdate.removeAll(toAdd);

        // 3. Perform operations DELETE, ADD, UPDATE
        // 3.1 Sort lists
        CostDetailComparator comparator = new CostDetailComparator();

        Collections.sort(toAdd, comparator);
        Collections.sort(toDelete, comparator);
        Collections.sort(toUpdate, comparator);

        Map<DeltaActionEnum, List<CostDetail>> groups = new HashMap<DeltaActionEnum, List<CostDetail>>() {{
            put(DeltaActionEnum.DELETE, toDelete);
            put(DeltaActionEnum.ADD, toAdd);
            put(DeltaActionEnum.UPDATE, toUpdate);
        }};

        // 3.1 Check access control
        checkAccessControl(groups, key, session);

        // 3.2 Check if elements to be deleted are contiguous
        // Check if subset of Cost Details (CD1) to be deleted are
        // contained in the list of all Cost Details (CD0). Ordering is evaluated.
        // In case it is, CD1 needs to be a subset smaller than the CD0
        if (!isUpdateOnImport) {
            validateIfCostDetailsCanBeDeleted(existing, toDelete);
        }

        for (Map.Entry<DeltaActionEnum, List<CostDetail>> group : groups.entrySet()) {
            for (CostDetail costDetail : group.getValue()) {
                DeltaActionEnum actionType = group.getKey();
                switch (actionType) {
                    case DELETE: {
                        CostDetail existentCostDetail = costDetailDao.get(costDetail.getId(), session);
                        if (existentCostDetail == null) {
                            throw BusinessValidationUtil.buildBusinessSystemException(ValidationCode.INVALID, "id");
                        }
                        costDetailDao.remove(costDetail, costDetail.getModifiedTpwsKey(), session);
                        CostDetail dimCostDetail = this.setValuesDimCostDetails(costDetail);
                        dimCostDetailDao.remove(dimCostDetail, dimCostDetail.getModifiedTpwsKey(), dimSession);
                        break;
                    }
                    case ADD: {
                        costDetail.setInventory(calculateInventoryFor(costDetail));
                        costDetail.setCreatedTpwsKey(key.getTpws());
                        costDetail.setForeignId(foreignId);
                        costDetail.setPlannedNetAdSpend(PackagePlacementUtil.calculatePlannedNetAdSpend(costDetail.getMargin(),
                                costDetail.getPlannedGrossAdSpend()));
                        costDetail.setPlannedNetRate(PackagePlacementUtil.calculatePlannedNetRate(costDetail.getMargin(),
                                costDetail.getPlannedGrossRate()));

                        costDetail = costDetailDao.create(costDetail, session);
                        result.add(costDetail);
                        // Dim costDetails
                        CostDetail dimCostDetail = this.setValuesDimCostDetails(costDetail);
                        dimCostDetailDao.create(dimCostDetail, dimSession);
                        break;
                    }
                    case UPDATE: {
                        costDetail.setInventory(calculateInventoryFor(costDetail));
                        costDetail.setModifiedTpwsKey(key.getTpws());
                        costDetail.setPlannedNetAdSpend(PackagePlacementUtil.calculatePlannedNetAdSpend(costDetail.getMargin(),
                                costDetail.getPlannedGrossAdSpend()));
                        costDetail.setPlannedNetRate(PackagePlacementUtil.calculatePlannedNetRate(costDetail.getMargin(),
                                costDetail.getPlannedGrossRate()));

                        costDetail = costDetailDao.update(costDetail, session);
                        result.add(costDetail);
                        // dim costDetails
                        CostDetail dimCostDetail = this.setValuesDimCostDetails(costDetail);
                        dimCostDetailDao.update(dimCostDetail, dimSession);
                        break;
                    }
                }
            }
        }

        // 4. Add Placeholder Cost Detail
        Collections.sort(result, new CostDetailComparator());
        if ((result.get(result.size() - 1).getEndDate()) != null) {
            CostDetail placeholder = createPlaceholder(result.get(result.size() - 1), session);
            CostDetail dimPlaceholder = this.setValuesDimCostDetails(placeholder);
            dimCostDetailDao.create(dimPlaceholder, dimSession);
            result.add(placeholder);
        }

        // 5. Return results
        return result;
    }

    private void validateIfCostDetailsCanBeDeleted(List<CostDetail> existing, List<CostDetail> toDelete) {
        if(toDelete.isEmpty()){
            return;
        }
        int index = Collections.indexOfSubList(existing, toDelete);
        // Trying to delete items are nonconsecutive order.
        if(index == -1) {
            throw BusinessValidationUtil.buildSystemException(
                    new ValidationError(),
                    ValidationCode.INVALID,
                    ResourceBundleUtil.getString("costDetail.error.deleteNonConsecutive"));
        }
        // Trying to delete the whole thing. Return error
        else if(index ==  0 && toDelete.size() == existing.size()){
            throw BusinessValidationUtil.buildSystemException(
                    new ValidationError(),
                    ValidationCode.INVALID,
                    ResourceBundleUtil.getString("costDetail.error.deleteAll"));
        }
        // Check if those to delete are grouped as last ones
        else if (index + toDelete.size() == existing.size()) {
            // All ok to delete these items
            return;
        }
        // Group to delete is consecutive but not the last ones
        else {
            throw BusinessValidationUtil.buildSystemException(
                    new ValidationError(),
                    ValidationCode.INVALID,
                    ResourceBundleUtil.getString("costDetail.error.deleteNotLastDetail"));
        }
    }

    public void removeCostDetailsByForeignId(Long foreignId, OauthKey key, SqlSession session, SqlSession dimSession) {
        //Null Validations
        if (foreignId == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Foreign id"));
        }
        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OauthKey"));
        }
        if (session == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "SqlSession"));
        }
        if (dimSession == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Dim SqlSession"));
        }

        List<CostDetail> costs = costDetailDao.getAll(foreignId, session);
        for (CostDetail cost : costs) {
            costDetailDao.remove(cost, cost.getModifiedTpwsKey(), session);
            // REMOVE dim costDetails
            CostDetail dimCostDetail = this.setValuesDimCostDetails(cost);
            dimCostDetailDao.remove(dimCostDetail, dimCostDetail.getModifiedTpwsKey(), dimSession);
        }
    }

    public List<CostDetail> setDefaultCostDetails(Long foreignId, List<CostDetail> costDetails, String tpwsKey) {
        //Null Validations
        if (foreignId == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Foreign Id"));
        }
        if (costDetails == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "CostDetails"));
        }
        if (tpwsKey == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "TpwsKey"));
        }

        List<CostDetail> result = new ArrayList<>();
        CostDetail firstCostDetail = costDetails.iterator().next();
        firstCostDetail.setForeignId(foreignId);
        firstCostDetail.setInventory(calculateInventoryFor(firstCostDetail));
        firstCostDetail.setCreatedTpwsKey(tpwsKey);
        firstCostDetail.setPlannedNetAdSpend(PackagePlacementUtil.calculatePlannedNetAdSpend(firstCostDetail.getMargin(),
                firstCostDetail.getPlannedGrossAdSpend()));
        firstCostDetail.setPlannedNetRate(PackagePlacementUtil.calculatePlannedNetRate(firstCostDetail.getMargin(),
                firstCostDetail.getPlannedGrossRate()));

        CostDetail placeHolder = createPlaceholderEntry(firstCostDetail);
        result.add(firstCostDetail);
        result.add(placeHolder);
        return result;
    }

    public List<CostDetail> createDefaultCostDetails(Long foreignId, List<CostDetail> costDetails, OauthKey key, SqlSession session, SqlSession dimSession) {
        // nullability checks
        if (foreignId == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Foreign Id"));
        }
        if (costDetails == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "CostDetails"));
        }
        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OauthKey"));
        }
        if (session == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "SqlSession"));
        }
        if (dimSession == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "Dim SqlSession"));
        }

        CostDetail firstCostDetail = costDetails.iterator().next();
        firstCostDetail.setForeignId(foreignId);
        firstCostDetail.setInventory(calculateInventoryFor(firstCostDetail));
        firstCostDetail.setPlannedNetAdSpend(PackagePlacementUtil.calculatePlannedNetAdSpend(firstCostDetail.getMargin(),
                firstCostDetail.getPlannedGrossAdSpend()));
        firstCostDetail.setPlannedNetRate(PackagePlacementUtil.calculatePlannedNetRate(firstCostDetail.getMargin(),
                firstCostDetail.getPlannedGrossRate()));
        firstCostDetail.setCreatedTpwsKey(key.getTpws());
        // persist first costDetail
        firstCostDetail = costDetailDao.create(firstCostDetail, session);

        // set and persist placeholder
        CostDetail placeholder = createPlaceholder(firstCostDetail, session);

        List<CostDetail> result = new ArrayList<>();
        result.add(firstCostDetail);
        result.add(placeholder);

        // DIM
        CostDetail dimCostDetail = this.setValuesDimCostDetails(firstCostDetail);
        dimCostDetailDao.create(dimCostDetail, dimSession);
        dimCostDetail = this.setValuesDimCostDetails(placeholder);
        dimCostDetailDao.create(dimCostDetail, dimSession);
        return result;
    }

    private Long calculateInventoryFor(CostDetail costDetail) {
        RateTypeEnum rateType = RateTypeEnum.typeOf(costDetail.getRateType());
        return PackagePlacementUtil.calculateInventory(costDetail.getPlannedGrossRate(),
                costDetail.getPlannedGrossAdSpend(), rateType.toString());
    }

    private void checkAccessControl(Map<DeltaActionEnum, List<CostDetail>> groups, OauthKey key, SqlSession session) {
        Set<Long> ids = new HashSet<>();

        for(Map.Entry<DeltaActionEnum, List<CostDetail>> entry : groups.entrySet()) {
            for (CostDetail costDetail : entry.getValue()) {
                if (costDetail.getId() != null) {
                    ids.add(costDetail.getId());
                }
            }
        }

        if (!ids.isEmpty()) {
            if(!userValidFor(childrenAccessStatement, ids, key.getUserId(), session)){
                throw BusinessValidationUtil
                    .buildAccessSystemException(SecurityCode.NOT_FOUND_FOR_USER);
            }
        }
    }

    private CostDetail createPlaceholder(CostDetail costDetail, SqlSession session) {
        try {
            CostDetail placeholder = createPlaceholderEntry(costDetail);
            return costDetailDao.create(placeholder, session);
        } catch (Exception e) {
            throw BusinessValidationUtil.buildBusinessSystemException(e, BusinessCode.INTERNAL_ERROR, "");
        }
    }
    
    private CostDetail createPlaceholderEntry(CostDetail costDetail) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(costDetail.getEndDate());
        calendar.add(Calendar.DAY_OF_YEAR, 1);

        CostDetail detail = new CostDetail();
        detail.setForeignId(costDetail.getForeignId());
        detail.setInventory(Constants.PACKAGE_PLACEMENT_DEFAULT_INVENTORY);
        detail.setRateType(RateTypeEnum.CPM.getCode());
        detail.setPlannedGrossAdSpend(Constants.COST_DETAIL_GROSS_AD_SPEND_MIN_VALUE);
        detail.setPlannedGrossRate(Constants.COST_DETAIL_GROSS_RATE_MIN_VALUE);
        detail.setPlannedNetAdSpend(Constants.COST_DETAIL_GROSS_AD_SPEND_MIN_VALUE);
        detail.setPlannedNetRate(Constants.COST_DETAIL_GROSS_RATE_MIN_VALUE);
        detail.setStartDate(DateConverter.startDate(calendar.getTime()));
        detail.setCreatedTpwsKey(costDetail.getCreatedTpwsKey());
        return detail;
    }

    private CostDetail setValuesDimCostDetails(CostDetail costDetail) {
        CostDetail dimCostDetail = new CostDetail(costDetail);
        if (dimCostDetail.getEndDate() == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.YEAR, 50);
            dimCostDetail.setEndDate(calendar.getTime());
        }        
        return dimCostDetail;
    }
}
