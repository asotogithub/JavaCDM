package trueffect.truconnect.api.crud.dao.impl.dim;

import trueffect.truconnect.api.commons.exceptions.business.enums.BusinessCode;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.Package;
import trueffect.truconnect.api.commons.model.dto.dim.DimPackageDTO;
import trueffect.truconnect.api.commons.validation.BusinessValidationUtil;
import trueffect.truconnect.api.crud.dao.PackageDaoBase;
import trueffect.truconnect.api.crud.mybatis.AbstractGenericDao;
import trueffect.truconnect.api.crud.mybatis.PersistenceContext;

import org.apache.ibatis.session.SqlSession;

import java.util.HashMap;
import java.util.List;

/**
 *
 * @author marleny.patsi
 */
public class DimPackageDaoImpl extends AbstractGenericDao implements PackageDaoBase {

    private static final String STATEMENT_UPDATE_PACKAGE = "Package.package";
    private static final String STATEMENT_GET_BY_ID = "Package.getPackage";

    public DimPackageDaoImpl(PersistenceContext context) {
        super(context);
    }

    @Override
    public DimPackageDTO get(Long id, SqlSession session) {
        return getPersistenceContext().selectSingle(STATEMENT_GET_BY_ID, id, session, DimPackageDTO.class);
    }

    @Override
    public Package create(Package pkg, SqlSession session) {
        HashMap<String, Object> parameter = setParameters(pkg, DimCostDetailDaoImpl.DIM_ACTION.INSERT);
        getPersistenceContext().execute(STATEMENT_UPDATE_PACKAGE, parameter, session);
        if (!parameter.get("error").equals("SUCCESS")) {
            Exception e = new Exception(parameter.get("error").toString());
            throw BusinessValidationUtil.buildBusinessSystemException(e, BusinessCode.INTERNAL_ERROR, null);
        }
        return pkg;
    }

    @Override
    public void update(Package pkg, SqlSession session) {
        HashMap<String, Object> parameter = setParameters(pkg, DimCostDetailDaoImpl.DIM_ACTION.UPDATE);
        getPersistenceContext().execute(STATEMENT_UPDATE_PACKAGE, parameter, session);
        if (!parameter.get("error").equals("SUCCESS")) {
            Exception e = new Exception(parameter.get("error").toString());
            throw BusinessValidationUtil.buildBusinessSystemException(e, BusinessCode.INTERNAL_ERROR, null);
        }
    }

    @Override
    public void delete(Long packageId, OauthKey key, SqlSession session) {
        DimPackageDTO packageDTO = this.get(packageId, session);
        packageDTO.setLogicalDelete("N");
        HashMap<String, Object> parameter = setParameters(packageDTO, DimCostDetailDaoImpl.DIM_ACTION.UPDATE);
        getPersistenceContext().execute(STATEMENT_UPDATE_PACKAGE, parameter, session);
        if (!parameter.get("error").equals("SUCCESS")) {
            Exception e = new Exception(parameter.get("error").toString());
            throw BusinessValidationUtil.buildBusinessSystemException(e, BusinessCode.INTERNAL_ERROR, null);
        }
    }

    @Override
    public Integer delete(List<Long> ids, String tpwsKey, final SqlSession session) {
        for (Long packageId : ids) {
            DimPackageDTO packageDTO = this.get(packageId, session);
            packageDTO.setLogicalDelete("N");
            packageDTO.setModifiedTpwsKey(tpwsKey);
            HashMap<String, Object> parameter = setParameters(packageDTO, DimCostDetailDaoImpl.DIM_ACTION.UPDATE);
            getPersistenceContext().execute(STATEMENT_UPDATE_PACKAGE, parameter, session);
            if (!parameter.get("error").equals("SUCCESS")) {
                Exception e = new Exception(parameter.get("error").toString());
                throw BusinessValidationUtil.buildBusinessSystemException(e, BusinessCode.INTERNAL_ERROR, null);
            }
        }
        return null;
    }

    private HashMap<String, Object> setParameters(Package pkg, DimCostDetailDaoImpl.DIM_ACTION action) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("action", action.toString());
        parameter.put("packageId", pkg.getId());
        parameter.put("packageName", pkg.getName());
        parameter.put("packageDesc", pkg.getDescription());
        parameter.put("campaignId", pkg.getCampaignId());
        parameter.put("extPackageId", pkg.getExternalId());
        parameter.put("logicalDelete", pkg.getLogicalDelete());
        parameter.put("created", pkg.getCreatedDate());
        parameter.put("createdTpwsKey", pkg.getCreatedTpwsKey());
        parameter.put("modified", pkg.getModifiedDate());
        parameter.put("modifiedTpwsKey", pkg.getModifiedTpwsKey());
        return parameter;
    }
}
