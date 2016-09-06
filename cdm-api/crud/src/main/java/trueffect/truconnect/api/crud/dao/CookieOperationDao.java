package trueffect.truconnect.api.crud.dao;

import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.session.SqlSession;
import trueffect.truconnect.api.commons.model.OauthKey;
import trueffect.truconnect.api.commons.model.CookieOperation;
import trueffect.truconnect.api.crud.mybatis.MyBatisUtil;

/**
 *
 * @author Abel Soto
 * @edited Richard Jaldin (Jh@ley)
 */
public class CookieOperationDao {

	public CookieOperation save(String cookieName, Long cookieDomainId,
			Long expirationDays, Long cookieOverwriteBehave, 
			OauthKey key) throws Exception {
		SqlSession session = null;
		CookieOperation result = new CookieOperation();
		try {
			session = MyBatisUtil.beginTransaction();

			//for the nextval of cookie domain
			Long seqCookieOperationId = (Long) MyBatisUtil.selectOne(
					"GenericQueries.getNextId", "SEQ_GBL_DIM", session); //TODO: the sequence not it is of the table
			//Parsing the input parameters of the process
			HashMap<String, Object> parameter = new HashMap<String, Object>();
			parameter.put("p_cookie_operation_id",seqCookieOperationId );
			parameter.put("p_cookie_name", cookieName);
			parameter.put("p_cookie_domain_id",cookieDomainId);
			parameter.put("p_expiration_days", expirationDays);
			parameter.put("p_cookie_overwrite_behave", cookieOverwriteBehave);
			parameter.put("p_tpws_key", key.getTpws());
			MyBatisUtil.callProcedurePlSql("CookiePkg.insertCookieOperation", parameter, session);
			result = get(seqCookieOperationId,session);
			MyBatisUtil.commitTransaction(session);
		} catch (Exception e) {
			MyBatisUtil.rollbackTransaction(session);
			throw e;
		} 
		return result;
	}

	public CookieOperation update(Long cookieOperationId, String cookieName,
			Long cookieDomainId, Long expirationDays, Long cookieOverwriteBehave,
			OauthKey key) throws Exception {
		SqlSession session = null;
		CookieOperation result = new CookieOperation();
		try {
			session = MyBatisUtil.beginTransaction();

			//Parsing the input parameters of the process
			HashMap<String, Object> parameter = new HashMap<String, Object>();
			parameter.put("p_cookie_operation_id",cookieOperationId );
			parameter.put("p_cookie_name", cookieName);
			parameter.put("p_cookie_domain_id",cookieDomainId);
			parameter.put("p_expiration_days", expirationDays);
			parameter.put("p_cookie_overwrite_behave", cookieOverwriteBehave);
			parameter.put("p_tpws_key", key.getTpws());
			MyBatisUtil.callProcedurePlSql("CookiePkg.updateCookieOperation", parameter, session);
			result = get(cookieOperationId,session);
			MyBatisUtil.commitTransaction(session);
		} catch (Exception e) {
			MyBatisUtil.rollbackTransaction(session);
			throw e;
		} 
		return result;
	}

	public CookieOperation delete(Long cookieOperationId, OauthKey key) throws Exception {
		SqlSession session = null;
		CookieOperation result = new CookieOperation();
		try {
			session = MyBatisUtil.beginTransaction();

			//Parsing the input parameters of the process
			HashMap<String, Object> parameter = new HashMap<String, Object>();
			parameter.put("cookieOperationId", cookieOperationId);
			parameter.put("tpwsKey", key.getTpws());
			MyBatisUtil.callProcedurePlSql("CookiePkg.deleteCookieOperation", parameter, session);
			result = get(cookieOperationId, session);
			MyBatisUtil.commitTransaction(session);
		} catch (Exception e) {
			MyBatisUtil.rollbackTransaction(session);
			throw e;
		} 
		return result;
	}

	protected CookieOperation get(Long cookieOperationId, SqlSession session) throws Exception {
		return (CookieOperation) MyBatisUtil.selectOne("CookiePkg.getCookieOperation", cookieOperationId, session);
	}
	
	public CookieOperation get(Long cookieOperationId) throws Exception {
		SqlSession session = null;
		CookieOperation result = new CookieOperation();
		try {
			session = MyBatisUtil.beginTransaction();
			result = get(cookieOperationId, session);
			MyBatisUtil.commitTransaction(session);
		} catch (Exception e) {
			MyBatisUtil.rollbackTransaction(session);
			throw e;
		} 
		return result;
	}

	/**
	 * Gets a list with the cookies that belongs to the Cookie Domain Id
	 * @param cookieDomainId the CookieDomain's Id
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<CookieOperation> getByCookieDomain(Long cookieDomainId) throws Exception {
		SqlSession session = null;
    	List<CookieOperation> result = null;
		try {
			session = MyBatisUtil.beginTransaction();
			HashMap<String, Object> parameter = new HashMap<String, Object>();
			parameter.put("cookieDomainId", cookieDomainId);
			MyBatisUtil.callProcedurePlSql("CookiePkg.getCookieOperationsByCookieDomain", parameter, session);
			if(parameter.get("refCursor") != null){
				result = (List<CookieOperation>)parameter.get("refCursor");
			}
			MyBatisUtil.commitTransaction(session);
		} catch (Exception e) {
			MyBatisUtil.rollbackTransaction(session);
			throw e;
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<CookieOperation> getByAgency(Long agencyId) throws Exception {
		SqlSession session = null;
    	List<CookieOperation> result = null;
		try {
			session = MyBatisUtil.beginTransaction();
			HashMap<String, Object> parameter = new HashMap<String, Object>();
			parameter.put("agencyId", agencyId);
			MyBatisUtil.callProcedurePlSql("CookiePkg.getCookieOperationsByAgency", parameter, session);
			if(parameter.get("refCursor") != null){
				result = (List<CookieOperation>)parameter.get("refCursor");
			}
			MyBatisUtil.commitTransaction(session);
		} catch (Exception e) {
			MyBatisUtil.rollbackTransaction(session);
			throw e;
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<CookieOperation> getCookieOperationtByCampaign(Long campaignId) throws Exception {
		SqlSession session = null;
    	List<CookieOperation> result = null;
		try {
			session = MyBatisUtil.beginTransaction();
			HashMap<String, Object> parameter = new HashMap<String, Object>();
			parameter.put("campaignId", campaignId);
			MyBatisUtil.callProcedurePlSql("CookiePkg.getCookieOperationsByCampaign", parameter, session);
			if(parameter.get("refCursor") != null){
				result = (List<CookieOperation>)parameter.get("refCursor");
			}
			MyBatisUtil.commitTransaction(session);
		} catch (Exception e) {
			MyBatisUtil.rollbackTransaction(session);
			throw e;
		}
		return result;
	}
	
	public CookieOperation findByName(String cookieName) {
		SqlSession session = null;
		CookieOperation result = null;
		try {
			session = MyBatisUtil.beginTransaction();
			result = (CookieOperation) MyBatisUtil.selectOne("CookiePkg.cookieOperationAlreadyExists", cookieName, session);
			MyBatisUtil.commitTransaction(session);
		} catch (Exception e) {
			try {
				MyBatisUtil.rollbackTransaction(session);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		return result;
	}
}
