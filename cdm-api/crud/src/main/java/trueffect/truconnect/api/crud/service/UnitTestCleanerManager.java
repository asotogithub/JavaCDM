package trueffect.truconnect.api.crud.service;

import trueffect.truconnect.api.commons.model.Column;
import trueffect.truconnect.api.commons.model.Columns;
import trueffect.truconnect.api.crud.dao.UnitTestCleanerDao;

/**
 *
 * @author Richard Jaldin
 */
public class UnitTestCleanerManager {

    protected UnitTestCleanerDao dao;

    public UnitTestCleanerManager() {
        this.dao = new UnitTestCleanerDao();
    }

    /**
     * 
     * @param table
     * @param pkColumn
     * @param recordId
     * @return
     * @throws Exception 
     */
    public void removeRowWithSingleKey(String table, Column column) throws Exception {
        dao.removeRecord(table, column);
    }

    /**
     * 
     * @param table
     * @param params
     * @return
     * @throws Exception 
     */
    public void removeRowWithComposedKey( String table, Columns params) throws Exception {
        dao.removeRecord(table, params.getColumns());
    }
}
