package trueffect.truconnect.api.crud.dao;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import trueffect.truconnect.api.commons.model.Column;
import trueffect.truconnect.api.crud.mybatis.MyBatisUtil;

/**
 * Utilities used only for Unit Test purposes.
 * 
 *
 * @author Richard Jaldin
 */
public class UnitTestCleanerDao {

	/**
	 * Deletes a record from the database based on its table, primary key column and the ID.
	 * Note that this method not removes records with a composed primary keys.
	 * 
	 * @param table The table name
	 * @param pkColumn The primary key column name
	 * @param recordId The ID of the record.
	 * @throws Exception Throws error if something is wrong. 
	 */
    public void removeRecord(String table, Column column) throws Exception {
    	if(table == null || column == null) {
    		throw new Exception("Invalid input parameters");
    	}
    	
    	SqlSession session = null;
		try {
			session = MyBatisUtil.beginTransaction();
			HashMap<String, Object> parameter = new HashMap<String, Object>();
			parameter.put("table", table);
			parameter.put("column", column);
			MyBatisUtil.selectOne("deleteRecord", parameter, session);
			MyBatisUtil.commitTransaction(session);
		} catch (Exception e) {
			MyBatisUtil.rollbackTransaction(session);
		}
    }
    
    /**
	 * Deletes a record from the database based on its table, and the composed keys.
	 * Note that this method not removes records with a composed primary keys.
	 * 
	 * @param table The table name
	 * @param params parameters in pairs (key, value)
	 * @throws Exception Throws error if something is wrong. 
	 */
    public void removeRecord(String table, List<Column> params) throws Exception {
    	if(table == null || params == null || params.size() <= 0) {
    		throw new Exception("Invalid input parameters");
    	}
    	
    	SqlSession session = null;
		try {
			session = MyBatisUtil.beginTransaction();
			HashMap<String, Object> parameter = new HashMap<String, Object>();
			parameter.put("table", table);
			parameter.put("columns", params);
			MyBatisUtil.selectOne("deleteRecordMultiKey", parameter, session);
			MyBatisUtil.commitTransaction(session);
		} catch (Exception e) {
			MyBatisUtil.rollbackTransaction(session);
		}
    }
}
