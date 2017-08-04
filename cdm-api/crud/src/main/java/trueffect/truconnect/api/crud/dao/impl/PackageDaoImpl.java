package trueffect.truconnect.api.crud.dao.impl;

import trueffect.truconnect.api.commons.exceptions.SearchApiException;
import trueffect.truconnect.api.commons.exceptions.SystemException;
import trueffect.truconnect.api.commons.exceptions.business.enums.BusinessCode;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.Package;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.importexport.MediaRawDataView;
import trueffect.truconnect.api.crud.dao.PackageDaoExtended;
import trueffect.truconnect.api.crud.mybatis.AbstractGenericDao;
import trueffect.truconnect.api.crud.mybatis.PersistenceContext;
import trueffect.truconnect.api.crud.mybatis.reshandler.ResultSetAccumulatorImpl;
import trueffect.truconnect.api.crud.mybatis.reshandler.accumulator.Accumulator;
import trueffect.truconnect.api.crud.mybatis.reshandler.accumulator.CollectionAccumulatorImpl;
import trueffect.truconnect.api.crud.mybatis.reshandler.accumulator.SumAccumulatorImpl;
import trueffect.truconnect.api.search.Searcher;

import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

/**
 * Persistence Methods for Package
 * @author Gustavo Claure, Marcelo Heredia
 */
public class PackageDaoImpl extends AbstractGenericDao implements PackageDaoExtended {

    private static final String STATEMENT_GET_PACKAGE = "Package.getPackage";
    private static final String STATEMENT_GET_PACKAGES_BY_CRITERIA = "Package.getPackagesByCriteria";
    private static final String STATEMENT_GET_NUMBER_OF_PACKAGES_BY_CRITERIA = "Package.getPackagesNumberOfRecordsByCriteria";
    private static final String STATEMENT_GET_PACKAGES_BY_CAMPAIGN_AND_IO_ID = "Package.getPackagesByCampaignAndIoId";
    private static final String STATEMENT_GET_COUNT_PACKAGES_BY_CAMPAIGN_AND_IO_ID = "Package.getCountPackagesByCampaignAndIoId";
    private static final String STATEMENT_GET_EXITING_ACTIVE_CAMPAIGN = "Package.getExistActiveCampaign";
    private static final String STATEMENT_INSERT_PACKAGE = "Package.insertPackage";
    private static final String STATEMENT_UPDATE_PACKAGE = "Package.updatePackage";
    private static final String STATEMENT_UPDATE_PACKAGE_ON_IMPORT = "Package.updatePackageOnImport";
    private static final String STATEMENT_DELETE_PACKAGE = "Package.deletePackage";
    private static final String STATEMENT_BULK_DELETE_BY_IDS = "Package.bulkDeleteByIds";
    private static final String STATEMENT_PACKAGE_NAME_EXISTS = "Package.packageNameAlreadyExists";
    private static final String STATEMENT_GET_COSTDETAILS_BY_PACKAGE_NAME = "Package.getPackagesCostDetailsByPkgNames";

    public PackageDaoImpl(PersistenceContext context) {
        super(context);
    }

    @Override
    public Package get(Long id, SqlSession session) {
        return getPersistenceContext().selectSingle(STATEMENT_GET_PACKAGE, id, session, Package.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public RecordSet<Package> get(SearchCriteria criteria, OauthKey key, SqlSession session) {
        Searcher<Package> searcher;
        try{
            searcher = new Searcher<>(criteria.getQuery(), Package.class);
        } catch(SearchApiException e) {
            throw new SystemException(e.getMessage(), e, BusinessCode.INVALID);
        }
        HashMap<String, String> parameter = new HashMap<>();
        parameter.put("condition", searcher.getMybatisWheresCondition());
        parameter.put("order", searcher.getMybatisOrderBySection());
        parameter.put("userId", key.getUserId());

        // Getting data only for the page
        List<Package> aux = getPersistenceContext().selectMultiple(STATEMENT_GET_PACKAGES_BY_CRITERIA,
            parameter, new RowBounds(criteria.getStartIndex(), criteria.getPageSize()), session);

        if (aux.size() == 0) { // empty result.
            return new RecordSet<>(0, 0, 0, aux);
        }

        //Getting the total number of records available
        int totalRecords = getPersistenceContext().selectSingle(STATEMENT_GET_NUMBER_OF_PACKAGES_BY_CRITERIA, parameter, session, Integer.class);
        if (criteria.getStartIndex() > totalRecords) {
            throw new SystemException("Cannot retrieve the set of records starting by " + criteria.getStartIndex()
                + " because the starting index should be less than " + totalRecords, BusinessCode.INVALID);
        }
        return new RecordSet<>(criteria.getStartIndex(), criteria.getPageSize(), totalRecords, aux);
    }

    @Override
    public List<Package> getPackageByCampaignAndIoId(Long campaignId, Long ioId, Long startIndex,
                                                     Long pageSize, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("campaignId", campaignId);
        parameter.put("ioId", ioId);
        parameter.put("superiorLimit", startIndex + pageSize);
        parameter.put("inferiorLimit", startIndex);

        List<Package> result = getPersistenceContext()
                .selectMultiple(STATEMENT_GET_PACKAGES_BY_CAMPAIGN_AND_IO_ID, parameter, session);
        return result;
    }

    @Override
    public Long getCountPackageByCampaignAndIoId(Long campaignId, Long ioId, SqlSession session) {

        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("campaignId", campaignId);
        parameter.put("ioId", ioId);

        return getPersistenceContext().selectSingle(
                STATEMENT_GET_COUNT_PACKAGES_BY_CAMPAIGN_AND_IO_ID, parameter, session, Long.class);
    }

    @Override
    public Package getExistActiveCampaign(Long id, SqlSession session) {
        return getPersistenceContext().selectSingle(STATEMENT_GET_EXITING_ACTIVE_CAMPAIGN, id, session, Package.class);
    }

    @Override
    public Package create(Package pkg, SqlSession session) {
        Long id = getNextId(session);
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("packageId", id);
        parameter.put("name", pkg.getName());
        parameter.put("description", pkg.getDescription());
        parameter.put("campaignId", pkg.getCampaignId());
        parameter.put("externalId", pkg.getExternalId());
        parameter.put("logicalDelete", "N");
        parameter.put("created", pkg.getCreatedDate());
        parameter.put("createdTpwsKey", pkg.getCreatedTpwsKey());
        parameter.put("modified", pkg.getModifiedDate());
        parameter.put("modifiedTpwsKey", pkg.getModifiedTpwsKey());
        getPersistenceContext().execute(STATEMENT_INSERT_PACKAGE, parameter, session);
        return get(id, session);
    }

    @Override
    public void update(Package pkg, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("packageId", pkg.getId());
        parameter.put("name", pkg.getName());
        parameter.put("description", pkg.getDescription());
        parameter.put("externalId", pkg.getExternalId());
        parameter.put("modified", pkg.getModifiedDate());
        parameter.put("modifiedTpwsKey", pkg.getModifiedTpwsKey());
        getPersistenceContext().execute(STATEMENT_UPDATE_PACKAGE, parameter, session);
    }

    @Override
    public Package updateOnImport(Package pkg, SqlSession session) {
        getPersistenceContext().execute(STATEMENT_UPDATE_PACKAGE_ON_IMPORT, pkg, session);
        return get(pkg.getId(), session);
    }

    @Override
    public void delete(Long packageId, OauthKey key, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("packageId", packageId);
        parameter.put("createdTpwsKey", key.getTpws());
        getPersistenceContext().execute(STATEMENT_DELETE_PACKAGE, parameter, session);
    }

    @Override
    public Integer delete(List<Long> ids, String tpwsKey, final SqlSession session) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("tpwsKey", tpwsKey);
        int result = 0;
        Accumulator<Integer> resultAccumulator = new SumAccumulatorImpl<>(result);
        return new ResultSetAccumulatorImpl<Integer>("ids", ids, resultAccumulator, params) {
            @Override
            public Integer execute(Object parameters) {
                return getPersistenceContext()
                        .update(STATEMENT_BULK_DELETE_BY_IDS,
                                parameters,
                                session);
            }
        }.getResults();
    }

    @Override
    public boolean packageNameExists(Package pkg, SqlSession session) {
        Long numberOfCreatives = getPersistenceContext().selectSingle(STATEMENT_PACKAGE_NAME_EXISTS, pkg, session, Long.class);
        return numberOfCreatives.compareTo(0L) > 0;
    }

    @Override
    public List<MediaRawDataView> getMediaPackageByPackageNames(List<String> names, Long campaignId,
                                                                final SqlSession session) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("campaignId", campaignId);
        parameters.put("timezone", TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT));
        List<MediaRawDataView> result = new ArrayList<>();
        Accumulator<List<MediaRawDataView>> resultAccumulator =
                new CollectionAccumulatorImpl<>(result);
        return new ResultSetAccumulatorImpl<List<MediaRawDataView>>(
                "names",
                names,
                resultAccumulator,
                parameters) {
            @Override
            public List<MediaRawDataView> execute(Object parameters) {
                return getPersistenceContext()
                        .selectMultiple(STATEMENT_GET_COSTDETAILS_BY_PACKAGE_NAME,
                                parameters, session);
            }
        }.getResults();
    }

    @Override
    public List<MediaRawDataView> getMediaPackagesByUserAndIds(List<Long> ids, Long campaignId,
                                                               String userId,
                                                               final SqlSession session) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("userId", userId);
        parameters.put("campaignId", campaignId);
        parameters.put("timezone", TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT));
        List<MediaRawDataView> result = new ArrayList<>();
        Accumulator<List<MediaRawDataView>> resultAccumulator =
                new CollectionAccumulatorImpl<>(result);
        return new ResultSetAccumulatorImpl<List<MediaRawDataView>>(
                "ids",
                ids,
                resultAccumulator,
                parameters) {
            @Override
            public List<MediaRawDataView> execute(Object parameters) {
                return getPersistenceContext()
                        .selectMultiple(STATEMENT_GET_COSTDETAILS_BY_PACKAGE_NAME,
                                parameters, session);
            }
        }.getResults();
    }
}
