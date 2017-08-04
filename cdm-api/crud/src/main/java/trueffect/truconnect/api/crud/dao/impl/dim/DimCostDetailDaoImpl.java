package trueffect.truconnect.api.crud.dao.impl.dim;

import trueffect.truconnect.api.commons.Constants;
import trueffect.truconnect.api.commons.exceptions.business.enums.BusinessCode;
import trueffect.truconnect.api.commons.model.CostDetail;
import trueffect.truconnect.api.commons.model.PackagePlacement;
import trueffect.truconnect.api.commons.model.dim.DimPackageCostDetail;
import trueffect.truconnect.api.commons.model.dim.DimPlacementCost;
import trueffect.truconnect.api.commons.model.dim.DimPlacementCostDetail;
import trueffect.truconnect.api.commons.model.dim.DimProductBuyCost;
import trueffect.truconnect.api.commons.model.enums.CostDetailType;
import trueffect.truconnect.api.commons.validation.BusinessValidationUtil;
import trueffect.truconnect.api.crud.dao.CostDetailDaoBase;
import trueffect.truconnect.api.crud.dao.DimCostDetailDao;
import trueffect.truconnect.api.crud.mybatis.AbstractGenericDao;
import trueffect.truconnect.api.crud.mybatis.PersistenceContext;
import trueffect.truconnect.api.crud.mybatis.reshandler.ResultSetAccumulator;
import trueffect.truconnect.api.crud.mybatis.reshandler.ResultSetAccumulatorImpl;
import trueffect.truconnect.api.crud.mybatis.reshandler.accumulator.Accumulator;

import org.apache.ibatis.session.SqlSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Gustavo Claure
 */
public class DimCostDetailDaoImpl extends AbstractGenericDao implements CostDetailDaoBase, DimCostDetailDao {

    // Placement queries
    private static final String STATEMENT_UPDATE_PLACEMENT_COST_DETAIL = "CostDetail.updatePlacementCostDetail";

    // Package queries
    private static final String STATEMENT_UPDATE_PACKAGE_COST_DETAIL = "CostDetail.updatePackageCostDetail";

    public static final String STATEMENT_GET_PACKAGE_PLACEMENT = "CostDetail.getPackagePlacement";
    public static final String STATEMENT_GET_PACKAGE_COST_DETAIL = "CostDetail.getPackageCostDetail";
    public static final String STATEMENT_GET_PLACEMENT_COST = "CostDetail.getPlacementCost";
    public static final String STATEMENT_GET_PLACEMENT_COST_DETAIL = "CostDetail.getPlacementCostDetail";
    public static final String STATEMENT_GET_PRODUCT_BUY_COST = "CostDetail.getProductBuyCost";
    public static final String STATEMENT_REMOVE_FROM_PACKAGE = "CostDetail.removeFromPackage";
    public static final String STATEMENT_REMOVE_FROM_PACKAGE_PLACEMENT = "CostDetail.removeFromPackagePlacement";
    public static final String STATEMENT_REMOVE_FROM_PACKAGE_COST_DETAIL = "CostDetail.removeFromPackageCostDetail";
    public static final String STATEMENT_REMOVE_FROM_PLACEMENT_COST = "CostDetail.removeFromPlacementCost";
    public static final String STATEMENT_REMOVE_FROM_PLACEMENT_COST_DETAIL = "CostDetail.removeFromPlacementCostDetail";
    public static final String STATEMENT_REMOVE_FROM_PRODUCT_BUY_COST = "CostDetail.removeFromProductBuyCost";

    private CostDetailType costDetailType;

    public enum DIM_ACTION {

        INSERT, UPDATE, DELETE;

        @Override
        public String toString() {
            return String.valueOf(name().charAt(0));
        }
    }

    public DimCostDetailDaoImpl(PersistenceContext persistenceContext) {
        super(persistenceContext);
    }

    public DimCostDetailDaoImpl(CostDetailType costDetailType, PersistenceContext persistenceContext) {
        this(persistenceContext);
        this.costDetailType = costDetailType;
    }

    @Override
    public CostDetail create(CostDetail costDetail, SqlSession session) {
        HashMap<String, Object> parameter = createParameters(costDetail, DIM_ACTION.INSERT);
        String statement = costDetailType == CostDetailType.PACKAGE ? STATEMENT_UPDATE_PACKAGE_COST_DETAIL : STATEMENT_UPDATE_PLACEMENT_COST_DETAIL;
        getPersistenceContext().execute(statement, parameter, session);
        if (!parameter.get("error").equals("SUCCESS")) {
            Exception e = new Exception(parameter.get("error").toString());
            throw BusinessValidationUtil.buildBusinessSystemException(e, BusinessCode.INTERNAL_ERROR, null);
        }
        return costDetail;
    }

    @Override
    public CostDetail update(CostDetail costDetail, SqlSession session) {
        HashMap<String, Object> parameter = createParameters(costDetail, DIM_ACTION.UPDATE);
        String statement = costDetailType == CostDetailType.PACKAGE ? STATEMENT_UPDATE_PACKAGE_COST_DETAIL : STATEMENT_UPDATE_PLACEMENT_COST_DETAIL;
        getPersistenceContext().execute(statement, parameter, session);
        if (!parameter.get("error").equals("SUCCESS")) {
            Exception e = new Exception(parameter.get("error").toString());
            throw BusinessValidationUtil.buildBusinessSystemException(e, BusinessCode.INTERNAL_ERROR, null);
        }
        return costDetail;
    }

    @Override
    public void remove(CostDetail costDetail, String key, SqlSession session) {
        costDetail.setLogicalDelete("Y");
        HashMap<String, Object> parameter = createParameters(costDetail,
                costDetailType == CostDetailType.PACKAGE
                        ? DIM_ACTION.UPDATE
                        : DIM_ACTION.DELETE);
        String statement = costDetailType == CostDetailType.PACKAGE ? STATEMENT_UPDATE_PACKAGE_COST_DETAIL : STATEMENT_UPDATE_PLACEMENT_COST_DETAIL;
        getPersistenceContext().execute(statement, parameter, session);
        if (!parameter.get("error").equals("SUCCESS")) {
            Exception e = new Exception(parameter.get("error").toString());
            throw BusinessValidationUtil.buildBusinessSystemException(e, BusinessCode.INTERNAL_ERROR, null);

        }
    }

    @Override
    public List<PackagePlacement> getPackagePlacement(Long packageId, SqlSession session) {
        return getPersistenceContext().selectMultiple(STATEMENT_GET_PACKAGE_PLACEMENT, packageId, session);
    }

    @Override
    public List<DimPackageCostDetail> getDimPackageCostDetail(Long packageId, SqlSession session) {
        return getPersistenceContext().selectMultiple(STATEMENT_GET_PACKAGE_COST_DETAIL, packageId, session);
    }

    @Override
    public List<DimPlacementCost> getDimPlacementCost(Long placementId, SqlSession session) {
        return getPersistenceContext().selectMultiple(STATEMENT_GET_PLACEMENT_COST, placementId, session);
    }

    @Override
    public List<DimPlacementCostDetail> getDimPlacementCostDetail(Long placementId, SqlSession session) {
        return getPersistenceContext().selectMultiple(STATEMENT_GET_PLACEMENT_COST_DETAIL, placementId, session);
    }

    @Override
    public List<DimProductBuyCost> getDimProductBuyCost(Long productBuyId, SqlSession session) {
        return getPersistenceContext().selectMultiple(STATEMENT_GET_PRODUCT_BUY_COST, productBuyId, session);
    }

    @Override
    public void dimHardRemoveCostDetails(Set<Long> packageIds, Set<Long> placementsIds, SqlSession session) {
        executeMultiple(STATEMENT_REMOVE_FROM_PACKAGE, "ids", new ArrayList(packageIds), session);
        executeMultiple(STATEMENT_REMOVE_FROM_PACKAGE_PLACEMENT, "ids", new ArrayList(packageIds), session);
        executeMultiple(STATEMENT_REMOVE_FROM_PACKAGE_COST_DETAIL, "ids", new ArrayList(packageIds), session);
        executeMultiple(STATEMENT_REMOVE_FROM_PLACEMENT_COST, "ids", new ArrayList(placementsIds), session);
        executeMultiple(STATEMENT_REMOVE_FROM_PLACEMENT_COST_DETAIL, "ids", new ArrayList(placementsIds), session);
        executeMultiple(STATEMENT_REMOVE_FROM_PRODUCT_BUY_COST, "ids", new ArrayList(placementsIds), session);
    }

    private HashMap<String, Object> createParameters(CostDetail costDetail, DIM_ACTION action) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("action", action.toString());
        parameter.put("id", costDetail.getId());
        parameter.put("startDate", costDetail.getStartDate());
        parameter.put("endDate", costDetail.getEndDate());
        parameter.put("inventory", costDetail.getInventory());
        parameter.put("rateType", costDetail.getRateType());
        parameter.put("plannedNetRate", costDetail.getPlannedNetRate());
        parameter.put("plannedGrossRate", costDetail.getPlannedGrossRate());
        parameter.put("plannedNetAdSpend", costDetail.getPlannedNetAdSpend());
        parameter.put("plannedGrossAdSpend", costDetail.getPlannedGrossAdSpend());
        parameter.put("actualNetRate", costDetail.getActualNetRate() != null ? costDetail.getActualNetRate() : Constants.COST_DETAIL_NET_RATE_MIN_VALUE);
        parameter.put("actualGrossRate", costDetail.getActualGrossRate() != null ? costDetail.getActualGrossRate() : Constants.COST_DETAIL_GROSS_RATE_MIN_VALUE);
        parameter.put("actualNetAdSpend", costDetail.getActualNetAdSpend() != null ? costDetail.getActualNetAdSpend() : Constants.COST_DETAIL_NET_AD_SPEND_MIN_VALUE);
        parameter.put("actualGrossAdSpend", costDetail.getActualGrossAdSpend() != null ? costDetail.getActualGrossAdSpend() : Constants.COST_DETAIL_GROSS_AD_SPEND_MIN_VALUE);
        switch (costDetailType) {
            case PACKAGE:
                parameter.put("packageId", costDetail.getForeignId());
                parameter.put("logicalDelete", costDetail.getLogicalDelete());
                parameter.put("created", costDetail.getCreatedDate());
                parameter.put("createdTpwsKey", costDetail.getCreatedTpwsKey());
                parameter.put("modified", costDetail.getModifiedDate());
                parameter.put("modifiedTpwsKey", costDetail.getModifiedTpwsKey());
                break;
            case PLACEMENT:
                parameter.put("placementId", costDetail.getForeignId());
                parameter.put("tpwsKey", costDetail.getModifiedTpwsKey());
                break;
        }
        return parameter;
    }

    private void executeMultiple(final String statement, String idsKey, List<Long> ids, final SqlSession session) {
        Accumulator<Void> emptyAccumulator = new Accumulator<Void>() {
            @Override
            public void accumulate(Void partialResult) {
                // Do nothing
            }

            @Override
            public Void getAccumulatedResults() {
                // Do nothing;
                return null;
            }
        };
        HashMap<String, Object> params = new HashMap<>();
        params.put(idsKey, ids);
        ResultSetAccumulator accumulatorToRemove = new ResultSetAccumulatorImpl<Void>(
                "ids",
                ids,
                emptyAccumulator,
                params) {
                    @Override
                    public Void execute(Object parameters) {
                        getPersistenceContext().execute(statement, parameters, session);
                        return null;
                    }
                };
        accumulatorToRemove.getResults();
    }
}
