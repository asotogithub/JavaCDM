package trueffect.truconnect.api.crud.service;

import trueffect.truconnect.api.commons.exceptions.DataNotFoundForUserException;
import trueffect.truconnect.api.commons.exceptions.NotFoundException;
import trueffect.truconnect.api.commons.exceptions.ValidationException;
import trueffect.truconnect.api.commons.exceptions.business.Error;
import trueffect.truconnect.api.commons.exceptions.business.enums.BusinessCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.SecurityCode;
import trueffect.truconnect.api.commons.exceptions.business.enums.ValidationCode;
import trueffect.truconnect.api.commons.model.Advertiser;
import trueffect.truconnect.api.commons.model.Agency;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.Organization;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.commons.model.dto.CampaignDTO;
import trueffect.truconnect.api.commons.model.dto.adm.DatasetConfigView;
import trueffect.truconnect.api.commons.util.Either;
import trueffect.truconnect.api.crud.dao.AccessControl;
import trueffect.truconnect.api.crud.dao.AccessStatement;
import trueffect.truconnect.api.crud.dao.AdmTransactionDao;
import trueffect.truconnect.api.crud.dao.AdvertiserDao;
import trueffect.truconnect.api.crud.dao.AgencyDao;
import trueffect.truconnect.api.crud.dao.DatasetConfigDao;
import trueffect.truconnect.api.crud.dao.DimCostDetailDao;
import trueffect.truconnect.api.crud.dao.PackageDaoBase;
import trueffect.truconnect.api.crud.validation.AgencyValidator;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import com.trueffect.delivery.formats.adm.DatasetConfig;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.springframework.validation.BeanPropertyBindingResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Gustavo Claure
 */
public class AgencyManager extends AbstractGenericManager {

    private AgencyValidator validator;
    private AgencyDao agencyDao;
    private DatasetConfigDao datasetConfigDao;
    private AdmTransactionDao admTransactionDao;
    private AdvertiserDao advertiserDao;
    private DimCostDetailManager dimCostDetailManager;

    public AgencyManager(AgencyDao agencyDao, DatasetConfigDao datasetConfigDao,
            AdmTransactionDao admTransactionDao, AdvertiserDao advertiserDao,
            DimCostDetailDao dimCostDetailDao, PackageDaoBase dimPackageDao,
            AccessControl accessControl) {
        super(accessControl);
        validator = new AgencyValidator();
        this.agencyDao = agencyDao;
        this.datasetConfigDao = datasetConfigDao;
        this.admTransactionDao = admTransactionDao;
        this.advertiserDao = advertiserDao;
        dimCostDetailManager = new DimCostDetailManager(dimPackageDao, dimCostDetailDao,
                agencyDao, accessControl);
    }

    /**
     * Gets an Advertiser based on its ID
     *
     * @param id the ID of the Advertiser.
     * @param key
     * @return the Advertiser
     * @throws java.lang.Exception
     */
    public Agency get(Long id, OauthKey key) throws Exception {
        //null validations
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }

        if (key == null) {
            throw new IllegalArgumentException("OauthKey cannot be null");
        }
        //obtain session
        SqlSession session = agencyDao.openSession();
        Agency result = null;
        try {
            // Check access control
            if (!isAdminUser(key.getUserId(), session)) {
                if (!userValidFor(AccessStatement.AGENCY, id, key.getUserId(), session)) {
                    throw new DataNotFoundForUserException(DataNotFoundForUserException.HEADER_MESSAGE + key.getUserId());
                }
            }
            //call dao
            result = agencyDao.get(id, session);
        } finally {
            agencyDao.close(session);
        }
        if (result == null){
            throw new DataNotFoundForUserException(ResourceBundleUtil.getString("dataAccessControl.notFoundForUser", "AgencyId", Long.toString(id), key.getUserId()));
        }
        return result;
    }

    public Agency save(Agency agency, OauthKey key) throws Exception {
        //null validations
        if (agency == null) {
            throw new IllegalArgumentException("Payload cannot be null");
        }
        if (key == null) {
            throw new IllegalArgumentException("OauthKey cannot be null");
        }
        
        String className = agency.getClass().getSimpleName();
        BeanPropertyBindingResult bpResult = new BeanPropertyBindingResult(agency, className);
        validator.validate(agency, bpResult);
        if (bpResult.hasErrors()) {
            throw new ValidationException(bpResult);
        }
        
        //obtain session
        SqlSession session = agencyDao.openSession();
        Agency result = null;
        try {
            //check access control
            isAdminUser(key.getUserId(), session);
            if (!agencyDao.getExistsAgency(agency.getName(), session)) {
                throw new ValidationException("Agency name already exists.");
            }
            // Before the agency gets created, create a new "Organization".
            Organization organization = new Organization();
            String name = agency.getName();
            name = name + (new Date()).getTime();
            organization.setName(name);
            organization.setCreatedTpwsKey(key.getTpws());
            Long idOrganization = agencyDao.saveOrganization(organization, session);
            
            //set values
            agency.setCreatedTpwsKey(key.getTpws());
            //call dao
            result = agencyDao.save(agency, idOrganization, session);
            agencyDao.commit(session);
        } catch (Exception e) {
            agencyDao.rollback(session);
            throw e;
        } finally {
            agencyDao.close(session);
        }
        return result;
    }

    public Agency update(Agency agency, Long id, OauthKey key) throws Exception {
        //null validations
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }
        if (agency == null) {
            throw new IllegalArgumentException("Payload cannot be null");
        }
        if (key == null) {
            throw new IllegalArgumentException("OauthKey cannot be null");
        }
        
        String className = agency.getClass().getSimpleName();
        BeanPropertyBindingResult bpResult = new BeanPropertyBindingResult(agency, className);
        validator.validate(agency, id, bpResult);
        if (bpResult.hasErrors()) {
            throw new ValidationException(bpResult);
        }

        //obtain session
        SqlSession session = agencyDao.openSession();
        Agency result = null;
        try {
            //check access control
            isAdminUser(key.getUserId(), session);
            Agency existentAgency = agencyDao.get(agency.getId(), session);
            if (existentAgency == null) {
                throw new NotFoundException("Can't update record if it doesn't exist");
            }
            if (StringUtils.isEmpty(agency.getName())) {
                agency.setName(existentAgency.getName());
            }
            if (!agencyDao.getExistsAgency(agency.getName(), session)) {
                throw new ValidationException("Agency name already exists.");
            }
            //set values
            agency.setModifiedTpwsKey(key.getTpws());
            //call dao
            result = agencyDao.update(agency, session);
            agencyDao.commit(session);
        } catch (Exception e) {
            agencyDao.rollback(session);
            throw e;
        } finally {
            agencyDao.close(session);
        }
        return result;
    }

    public SuccessResponse hardRemove(Long id, OauthKey key) throws Exception {
        //null validations
        if (id == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "id"));
        }
        if (key == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "OauthKey"));
        }
        //obtain session
        SqlSession session = agencyDao.openSession();
        try {
            //check access control
            if (!userValidFor(AccessStatement.AGENCY, id, key.getUserId(), session)) {
                throw new DataNotFoundForUserException(DataNotFoundForUserException.HEADER_MESSAGE + key.getUserId());
            }

            // Remove dim CostDetail elements
            Either<Error, Void> dimResult = dimCostDetailManager.dimHardRemoveCostDetails(id, key, session);
            if(dimResult.isError()){
                throw new Exception(dimResult.error().getMessage());
            }

            //call dao --> Remove items from our database
            agencyDao.hardRemove(id, session);
            agencyDao.commit(session);
        } catch (Exception e) {
            agencyDao.rollback(session);
            throw e;
        } finally {
            agencyDao.close(session);
        }
        return new SuccessResponse("Agency " + id + " successfully deleted physically");
    }

    public RecordSet<CampaignDTO> getCampaigns(Long id, OauthKey key) throws Exception {
        //null validations
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }

        if (key == null) {
            throw new IllegalArgumentException("OauthKey cannot be null");
        }

        //obtain session
        SqlSession session = agencyDao.openSession();
        RecordSet<CampaignDTO> result;
        try {
            //check access control
            if (!userValidFor(AccessStatement.AGENCY, id, key.getUserId(), session)) {
                throw new DataNotFoundForUserException(DataNotFoundForUserException.HEADER_MESSAGE + key.getUserId());
            }
            //call dao
            result = agencyDao.getCampaigns(id, key.getUserId(), session);
        } finally {
            agencyDao.close(session);
        }
        return result;
    }

    public Either<Error, RecordSet<DatasetConfigView>> getDatasets(Long agencyId, OauthKey key) {
        if (agencyId == null) {
            return Either.error(new Error(ResourceBundleUtil.getString("error.generic.empty", "agency ID"), ValidationCode.REQUIRED));
        }
        if (key == null) {
            return Either.error(new Error(ResourceBundleUtil.getString("error.oauth.null"), ValidationCode.REQUIRED));
        }

        SqlSession session = agencyDao.openSession();
        List<DatasetConfigView> result = new ArrayList<>();
        try {
            // If the user does not have access to
            if(!userValidFor(AccessStatement.AGENCY, agencyId, key.getUserId(), session)) {
                return Either.error(new Error("User is not authorized to work on this agency.", SecurityCode.PERMISSION_DENIED));
            }

            List<DatasetConfig> datasets = datasetConfigDao.getDatasets(agencyId);
            for(DatasetConfig dataset : datasets) {
                // Only return the datasets for advertisers the user has access to
                if(userValidFor(AccessStatement.ADVERTISER, dataset.advertiserId(), key.getUserId(), session)) {
                    Advertiser advertiser = advertiserDao.get(dataset.advertiserId(), session);
                    DatasetConfigView datasetConfigView = new DatasetConfigView(dataset);
                    if(advertiser != null) {
                        datasetConfigView.setAdvertiserName(advertiser.getName());
                    }
                    result.add(datasetConfigView);
                }
            }
        } catch(Exception e) {
            String message = String.format("Unable to get the datasets for agency %d", agencyId);
            log.warn(message, e);
            return Either.error(new Error(message, BusinessCode.INTERNAL_ERROR));
        } finally {
            agencyDao.close(session);
        }
        return Either.success(new RecordSet<>(result));
    }
}
