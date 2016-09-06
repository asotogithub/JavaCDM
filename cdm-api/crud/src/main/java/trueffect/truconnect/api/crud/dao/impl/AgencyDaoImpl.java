package trueffect.truconnect.api.crud.dao.impl;

import trueffect.truconnect.api.commons.model.Agency;
import trueffect.truconnect.api.commons.model.Organization;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.dto.CampaignDTO;
import trueffect.truconnect.api.crud.dao.AgencyDao;
import trueffect.truconnect.api.crud.mybatis.AbstractGenericDao;
import trueffect.truconnect.api.crud.mybatis.PersistenceContext;

import org.apache.ibatis.session.SqlSession;

import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Abel Soto
 */
public class AgencyDaoImpl extends AbstractGenericDao implements AgencyDao {

    private static final String STATEMENT_GET_AGENCY = "Agency.getAgency";
    private static final String STATEMENT_DELETE_AGENCY_PHISICALLY = "DeletePkg.removeAgencyPhysically";
    private static final String STATEMENT_SAVE_AGENCY = "Agency.saveCompleteAgency";
    private static final String STATEMENT_SAVE_ORGANIZATION = "Agency.saveOrganization";
    private static final String STATEMENT_GET_EXISTS_AGENCY = "Agency.getExistsAgency";
    private static final String STATEMENT_UPDATE_COMPLETE_AGENCY = "Agency.updateCompleteAgency";
    private static final String STATEMENT_GET_CAMPAIGNS = "Agency.getCampaigns";
    private static final String STATEMENT_GET_ALL_PACKAGES_IDS = "Agency.getAllPackagesIds";
    private static final String STATEMENT_GET_ALL_PLACEMENT_IDS = "Agency.getAllPlacementsIds";
    
    public AgencyDaoImpl(PersistenceContext persistenceContext) {
        super(persistenceContext);
    }

    @Override
    public Agency get(Long id, SqlSession session) throws Exception {
        return getPersistenceContext().selectOne(STATEMENT_GET_AGENCY, id, session, Agency.class);
    }


    @SuppressWarnings("unused")
    @Override
    public Agency save(Agency agency, Long idOrganization, SqlSession session) throws Exception {
        Long enableHtmlInjection = agency.getEnableHtmlInjection() != null && agency.getEnableHtmlInjection() ? 1L : 0L;
        Long id = getNextId(session);
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("id", id);
        parameter.put("name", agency.getName());
        parameter.put("organizationId", idOrganization);
        parameter.put("enableHtmlInjection", enableHtmlInjection);
        parameter.put("createdTpwsKey", agency.getCreatedTpwsKey());
        parameter.put("address1", agency.getAddress1());
        parameter.put("address2", agency.getAddress2());
        parameter.put("city", agency.getCity());
        parameter.put("state", agency.getState());
        parameter.put("zip", agency.getZipCode());
        parameter.put("country", agency.getCountry());
        parameter.put("phone", agency.getPhoneNumber());
        parameter.put("url", agency.getUrl());
        parameter.put("fax", agency.getFaxNumber());
        parameter.put("contactDefault", agency.getContactDefault());
        parameter.put("notes", agency.getNotes());
        getPersistenceContext().callPlSqlStoredProcedure(STATEMENT_SAVE_AGENCY, parameter, session);
        return get(id, session);
    }

    @SuppressWarnings("unused")
    @Override
    public Agency update(Agency agency, SqlSession session) throws Exception {
        Long enableHtmlInjection = agency.getEnableHtmlInjection() != null && agency.getEnableHtmlInjection() ? 1L : 0L;
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("id", agency.getId());
        parameter.put("name", agency.getName());
        parameter.put("htmlInjectionEnable", enableHtmlInjection);
        parameter.put("modifiedTpwsKey", agency.getModifiedTpwsKey());
        parameter.put("address1", agency.getAddress1());
        parameter.put("address2", agency.getAddress2());
        parameter.put("city", agency.getCity());
        parameter.put("state", agency.getState());
        parameter.put("zip", agency.getZipCode());
        parameter.put("country", agency.getCountry());
        parameter.put("phone", agency.getPhoneNumber());
        parameter.put("url", agency.getUrl());
        parameter.put("fax", agency.getFaxNumber());
        parameter.put("contactDefault", agency.getContactDefault());
        parameter.put("notes", agency.getNotes());
        getPersistenceContext().callPlSqlStoredProcedure(STATEMENT_UPDATE_COMPLETE_AGENCY, parameter, session);
        return get(agency.getId(), session);
    }
    
    @SuppressWarnings("unused")
    @Override
    public void hardRemove(Long id, SqlSession session) throws Exception {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("id", id);
        getPersistenceContext().selectOne(STATEMENT_DELETE_AGENCY_PHISICALLY, parameter, session);
    }

    @Override
    @SuppressWarnings("unchecked")
    public RecordSet<CampaignDTO> getCampaigns(Long id, String userId, SqlSession session) throws Exception {
        RecordSet<CampaignDTO> result;
        HashMap<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("userId", userId);
        List<CampaignDTO> campaigns = (List<CampaignDTO>) getPersistenceContext().selectList(STATEMENT_GET_CAMPAIGNS, params, session);
        result = new RecordSet<>(0, campaigns.size(), campaigns.size(), campaigns);
        return result;
    }    

    @Override
    public Long saveOrganization(Organization organization, SqlSession session) throws Exception {
        Long idOrganization = getNextId(session);
        HashMap<String, Object> parameterOrganization = new HashMap<>();
        parameterOrganization.put("id", idOrganization);
        parameterOrganization.put("name", organization.getName());
        parameterOrganization.put("createdTpwsKey", organization.getCreatedTpwsKey());
        getPersistenceContext().callPlSqlStoredProcedure(STATEMENT_SAVE_ORGANIZATION, parameterOrganization, session);
        return idOrganization;
    }

    @Override
    public boolean getExistsAgency(String name, SqlSession session) throws Exception {
        Long result = getPersistenceContext().selectOne(STATEMENT_GET_EXISTS_AGENCY, name, session, Long.class);
        return result == 0L;
    }

    @Override
    public List<Long> getAllPackageIds(Long agencyId, SqlSession session) {
        return getPersistenceContext().selectMultiple(STATEMENT_GET_ALL_PACKAGES_IDS, agencyId, session);
    }

    @Override
    public List<Long> getAllPlacementIds(Long agencyId, SqlSession session) {
        return getPersistenceContext().selectMultiple(STATEMENT_GET_ALL_PLACEMENT_IDS, agencyId, session);
    }
}
