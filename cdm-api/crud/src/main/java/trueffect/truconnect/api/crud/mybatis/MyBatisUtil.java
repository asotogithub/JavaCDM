package trueffect.truconnect.api.crud.mybatis;


import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;

/**
 *
 * @author Abel Soto
 * @edited Richard Jaldin
 */
public class MyBatisUtil {
	
    private static final PersistenceContextMyBatis MY_BATIS_BUSSINESS_LOGIC = new PersistenceContextMyBatis();
    
    public static List<?> selectList(String xmlMethod) throws Exception {
        return MY_BATIS_BUSSINESS_LOGIC.selectList(xmlMethod);
    }

    public static List<?> selectList(String xmlMethod, Object parameter) throws Exception {
        return MY_BATIS_BUSSINESS_LOGIC.selectList(xmlMethod, parameter);
    }

    public static Object selectOne(String xmlMethod) throws Exception {
        return MY_BATIS_BUSSINESS_LOGIC.selectOne(xmlMethod);
    }

    public static Object selectOne(String xmlMethod, Object parameter) throws Exception {
        return MY_BATIS_BUSSINESS_LOGIC.selectOne(xmlMethod, parameter);
    }

    public static List<?> selectList(String xmlMethod, SqlSession session) throws Exception {
        return MY_BATIS_BUSSINESS_LOGIC.selectList(xmlMethod, session);
    }
    
    public static List<?> selectList(String xmlMethod, Object parameter, RowBounds limits, SqlSession session) throws Exception {
    	return MY_BATIS_BUSSINESS_LOGIC.selectList(xmlMethod, parameter, limits, session);
	}

    public static List<?> selectList(String xmlMethod, Object parameter, SqlSession session) throws Exception {
        return MY_BATIS_BUSSINESS_LOGIC.selectList(xmlMethod, parameter, session);
    }

    public static Object selectOne(String xmlMethod, SqlSession session) throws Exception {
        return MY_BATIS_BUSSINESS_LOGIC.selectOne(xmlMethod, session);
    }

    public static Object selectOne(String xmlMethod, Object parameter, SqlSession session) throws Exception {
        return MY_BATIS_BUSSINESS_LOGIC.selectOne(xmlMethod, parameter, session);
    }

    public static void callProcedurePlSql(String xmlMethod, HashMap<String, Object> parameterMap, SqlSession session) throws Exception {
        MY_BATIS_BUSSINESS_LOGIC.callPlSqlStoredProcedure(xmlMethod, parameterMap, session);
    }

    public static void callProcedurePlSql(String xmlMethod, HashMap<String, Object> parameterMap) throws Exception {
        MY_BATIS_BUSSINESS_LOGIC.callPlSqlStoredProcedure(xmlMethod, parameterMap);
    }

    public static SqlSession beginTransaction() throws Exception {
        return MY_BATIS_BUSSINESS_LOGIC.beginTransaction();
    }

    public static void commitTransaction(SqlSession session) throws Exception {
        MY_BATIS_BUSSINESS_LOGIC.commitTransaction(session);
    }

    public static void rollbackTransaction(SqlSession session) throws Exception {
        MY_BATIS_BUSSINESS_LOGIC.rollbackTransaction(session);
    }

    public static void endTransaction(SqlSession session) throws Exception {
        MY_BATIS_BUSSINESS_LOGIC.endTransaction(session);
    }
}
