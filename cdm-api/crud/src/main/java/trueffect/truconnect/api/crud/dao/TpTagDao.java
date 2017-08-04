package trueffect.truconnect.api.crud.dao;

import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.session.SqlSession;

import trueffect.truconnect.api.commons.model.TpTag;
import trueffect.truconnect.api.crud.mybatis.MyBatisUtil;

/**
 *
 * @author Abel Soto
 */
public class TpTagDao {

    public TpTag save(Long vendorId,
            String tagName,
            String tagDescription,
            String matchExpression,
            String heightExpression,
            String widthExpression) throws Exception {
        SqlSession session = null;
        TpTag result = null;
        try {
            session = MyBatisUtil.beginTransaction();

            //for the nextval of cookie domain
            Long tagId = (Long) MyBatisUtil.selectOne("GenericQueries.getNextId", "SEQ_GBL_DIM", session);//TODO: The sequence it is incorrect 
            //Parsing the input parameters of the process
            HashMap<String, Object> parameter = new HashMap<String, Object>();
            parameter.put("p_tag_id", tagId);
            parameter.put("p_vendor_id", vendorId);
            parameter.put("p_tag_name", tagName);
            parameter.put("p_tag_description", tagDescription);
            parameter.put("p_match_expression", matchExpression);
            parameter.put("p_height_expression", heightExpression);
            parameter.put("p_width_expression", widthExpression);
            MyBatisUtil.callProcedurePlSql("HtmlTagPkg.insertTag", parameter, session);
            result = get(tagId, session);
            MyBatisUtil.commitTransaction(session);
        } catch (Exception e) {
            MyBatisUtil.rollbackTransaction(session);
            throw e;
        }
        return result;
    }

    public TpTag update(Long tagId,
            Long vendorId,
            String tagName,
            String tagDescription,
            String matchExpression,
            String heightExpression,
            String widthExpression) throws Exception {
        SqlSession session = null;
        TpTag result = null;
        try {
            session = MyBatisUtil.beginTransaction();

            //Parsing the input parameters of the process
            HashMap<String, Object> parameter = new HashMap<String, Object>();
            parameter.put("p_tag_id", tagId);
            parameter.put("p_vendor_id", vendorId);
            parameter.put("p_tag_name", tagName);
            parameter.put("p_tag_description", tagDescription);
            parameter.put("p_match_expression", matchExpression);
            parameter.put("p_height_expression", heightExpression);
            parameter.put("p_width_expression", widthExpression);
            MyBatisUtil.callProcedurePlSql("HtmlTagPkg.updateTag", parameter, session);
            result = get(tagId, session);
            MyBatisUtil.commitTransaction(session);
        } catch (Exception e) {
            MyBatisUtil.rollbackTransaction(session);
            throw e;
        }
        return result;
    }

    public void delete(Long tagId) throws Exception {
        SqlSession session = null;
        try {
            session = MyBatisUtil.beginTransaction();

            //Parsing the input parameters of the process
            HashMap<String, Object> parameter = new HashMap<String, Object>();
            parameter.put("p_tag_id", tagId);
            MyBatisUtil.callProcedurePlSql("HtmlTagPkg.deleteTag", parameter, session);
            MyBatisUtil.commitTransaction(session);
        } catch (Exception e) {
            MyBatisUtil.rollbackTransaction(session);
            throw e;
        }
    }

    public TpTag get(Long tpTagId) throws Exception {
        return (TpTag) MyBatisUtil.selectOne("HtmlTagPkg.getTpTag", tpTagId);
    }

    protected TpTag get(Long tpTagId, SqlSession session) throws Exception {
        return (TpTag) MyBatisUtil.selectOne("HtmlTagPkg.getTpTag", tpTagId, session);
    }

    public List<TpTag> getTpTagAll() throws Exception {
        return (List<TpTag>) MyBatisUtil.selectList("HtmlTagPkg.getTpTagAll");
    }
}
