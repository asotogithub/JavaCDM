package trueffect.truconnect.api.crud.dao.impl;

import trueffect.truconnect.api.commons.model.CostDetail;
import trueffect.truconnect.api.commons.model.enums.CostDetailType;
import trueffect.truconnect.api.commons.model.importexport.MediaRawDataView;
import trueffect.truconnect.api.crud.dao.CostDetailDaoExtended;
import trueffect.truconnect.api.crud.mybatis.AbstractGenericDao;
import trueffect.truconnect.api.crud.mybatis.PersistenceContext;
import trueffect.truconnect.api.crud.mybatis.reshandler.ResultSetAccumulatorImpl;
import trueffect.truconnect.api.crud.mybatis.reshandler.accumulator.Accumulator;
import trueffect.truconnect.api.crud.mybatis.reshandler.accumulator.CollectionAccumulatorImpl;

import org.apache.ibatis.session.SqlSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Cost Detail DAO Implementation
 * Created by richard.jaldin on 10/8/2015.
 */
public class CostDetailDaoImpl extends AbstractGenericDao implements CostDetailDaoExtended {

    //Package Queries
    private static final String STATEMENT_GET_PACKAGE_COST_DETAIL = "Package.getPackageCostDetail";
    private static final String STATEMENT_GET_PACKAGE_COST_DETAIL_BY_PACKAGE_ID = "Package.getPackageCostDetailByPackageId";
    private static final String STATEMENT_GET_PACKAGE_COSTS_FOR_EXPORT = "Package.getPackageCostsForExport";
    private static final String STATEMENT_INSERT_PACKAGE_COST_DETAIL = "Package.insertPackageCostDetail";
    private static final String STATEMENT_UPDATE_PACKAGE_COST_DETAIL = "Package.updatePackageCostDetail";
    private static final String STATEMENT_DELETE_PACKAGE_COST_DETAIL = "Package.deletePackageCostDetail";

    //Placement Queries
    private static final String STATEMENT_GET_PLACEMENT_COST_DETAIL = "Placement.getPlacementCostDetail";
    private static final String STATEMENT_GET_PLACEMENT_COST_DETAIL_BY_PLACEMENT_ID = "Placement.getPlacementCostDetailByPlacementId";
    private static final String STATEMENT_GET_PLACEMENT_COSTS_FOR_EXPORT = "Placement.getPlacementCostsForExport";
    private static final String STATEMENT_INSERT_PLACEMENT_COST_DETAIL = "Placement.insertPlacementCostDetail";
    private static final String STATEMENT_UPDATE_PLACEMENT_COST_DETAIL = "Placement.updatePlacementCostDetail";
    private static final String STATEMENT_DELETE_PLACEMENT_COST_DETAIL = "Placement.deletePlacementCostDetail";

    private CostDetailType costDetailType;

    public CostDetailDaoImpl(CostDetailType costDetailType, PersistenceContext persistenceContext) {
        super(persistenceContext);
        this.costDetailType = costDetailType;
    }

    @Override
    public List<CostDetail> getAll(Long foreignId, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put(costDetailType == CostDetailType.PACKAGE ? "packageId" : "placementId", foreignId);
        List<CostDetail> result = getPersistenceContext().selectMultiple(costDetailType == CostDetailType.PACKAGE ?
                STATEMENT_GET_PACKAGE_COST_DETAIL_BY_PACKAGE_ID :
                STATEMENT_GET_PLACEMENT_COST_DETAIL_BY_PLACEMENT_ID, parameter, session);
        if (parameter.get("refCursor") != null) {
            result = ((List<CostDetail>) parameter.get("refCursor"));
        }
        return result;
    }

    @Override
    public CostDetail get(Long id, SqlSession session) {
        return getPersistenceContext().selectSingle(costDetailType == CostDetailType.PACKAGE ? STATEMENT_GET_PACKAGE_COST_DETAIL : STATEMENT_GET_PLACEMENT_COST_DETAIL, id, session, CostDetail.class);
    }

    @Override
    public CostDetail create(CostDetail costDetail, SqlSession session) {
        Long id = this.getNextId(session);
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("id", id);
        parameter.put(costDetailType == CostDetailType.PACKAGE ? "packageId" : "placementId", costDetail.getForeignId());
        parameter.put("inventory", costDetail.getInventory());
        parameter.put("startDate", costDetail.getStartDate());
        parameter.put("endDate", costDetail.getEndDate());
        parameter.put("rateType", costDetail.getRateType());
        parameter.put("tpwsKey", costDetail.getCreatedTpwsKey());
        parameter.put("plannedNetRate", costDetail.getPlannedNetRate());
        parameter.put("plannedGrossRate", costDetail.getPlannedGrossRate());
        parameter.put("plannedNetAdSpend", costDetail.getPlannedNetAdSpend());
        parameter.put("plannedGrossAdSpend", costDetail.getPlannedGrossAdSpend());
        parameter.put("actualNetRate", costDetail.getActualNetRate());
        parameter.put("actualGrossRate", costDetail.getActualGrossRate());
        parameter.put("actualNetAdSpend", costDetail.getActualNetAdSpend());
        parameter.put("actualGrossAdSpend", costDetail.getActualGrossAdSpend());
        getPersistenceContext().execute(costDetailType == CostDetailType.PACKAGE ? STATEMENT_INSERT_PACKAGE_COST_DETAIL : STATEMENT_INSERT_PLACEMENT_COST_DETAIL, parameter, session);
        return this.get(id, session);
    }

    @Override
    public CostDetail update(CostDetail costDetail, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("id", costDetail.getId());
        parameter.put("inventory", costDetail.getInventory());
        parameter.put("startDate", costDetail.getStartDate());
        parameter.put("endDate", costDetail.getEndDate());
        parameter.put("rateType", costDetail.getRateType());
        parameter.put("tpwsKey", costDetail.getModifiedTpwsKey());
        parameter.put("plannedNetRate", costDetail.getPlannedNetRate());
        parameter.put("plannedGrossRate", costDetail.getPlannedGrossRate());
        parameter.put("plannedNetAdSpend", costDetail.getPlannedNetAdSpend());
        parameter.put("plannedGrossAdSpend", costDetail.getPlannedGrossAdSpend());
        parameter.put("actualNetRate", costDetail.getActualNetRate());
        parameter.put("actualGrossRate", costDetail.getActualGrossRate());
        parameter.put("actualNetAdSpend", costDetail.getActualNetAdSpend());
        parameter.put("actualGrossAdSpend", costDetail.getActualGrossAdSpend());
        getPersistenceContext().execute(costDetailType == CostDetailType.PACKAGE ? STATEMENT_UPDATE_PACKAGE_COST_DETAIL : STATEMENT_UPDATE_PLACEMENT_COST_DETAIL, parameter, session);
        return this.get(costDetail.getId(), session);
    }

    @Override
    public void remove(CostDetail costDetail, String key, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("id", costDetail.getId());
        parameter.put("tpwsKey", key);
        getPersistenceContext().execute(costDetailType == CostDetailType.PACKAGE ? STATEMENT_DELETE_PACKAGE_COST_DETAIL : STATEMENT_DELETE_PLACEMENT_COST_DETAIL, parameter, session);
    }

    @Override
    public List<MediaRawDataView> getCostsForExport(List<Long> ids, final SqlSession session) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("ids", ids);
        List<MediaRawDataView> result = new ArrayList<>();
        return new ResultSetAccumulatorImpl<List<MediaRawDataView>>(
                "ids",
                ids,
                new CollectionAccumulatorImpl<>(result),
                parameters) {
            @Override
            public List<MediaRawDataView> execute(Object parameters) {
                return getPersistenceContext().selectMultiple(
                        costDetailType == CostDetailType.PACKAGE
                                ? STATEMENT_GET_PACKAGE_COSTS_FOR_EXPORT
                                : STATEMENT_GET_PLACEMENT_COSTS_FOR_EXPORT,
                        parameters, session);
            }
        }.getResults();
    }
}
