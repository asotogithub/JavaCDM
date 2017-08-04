package trueffect.truconnect.api.crud.dao.impl;

import trueffect.truconnect.api.commons.model.InsertionOrderStatus;
import trueffect.truconnect.api.crud.mybatis.AbstractGenericDao;
import trueffect.truconnect.api.crud.dao.InsertionOrderStatusDao;
import trueffect.truconnect.api.crud.mybatis.PersistenceContext;

import org.apache.ibatis.session.SqlSession;

import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Gustavo Claure
 */
public class InsertionOrderStatusDaoImpl extends AbstractGenericDao implements InsertionOrderStatusDao {

    private static final String STATEMENT_INSERT_IO_STATUS = "IOPkg.insertIOStatus";
    private static final String STATEMENT_GET_IO_STATUS = "IOPkg.getCurrentIOStatus";

    /**
     * Creates a new {@code GenericDaoImpl} with a specific
     * {@code PersistentContext}
     *
     * @param persistenceContext The specific {@code PersistentContext} for this
     * DAO
     */
    public InsertionOrderStatusDaoImpl(PersistenceContext persistenceContext) {
        super(persistenceContext);
        
    }

    @Override
    public InsertionOrderStatus get(Long ioId, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("ioId", ioId);
        getPersistenceContext().execute(STATEMENT_GET_IO_STATUS, parameter, session);
        InsertionOrderStatus result = null;
        if (parameter.get("refCursor") != null) {
            List<InsertionOrderStatus> ios = (List<InsertionOrderStatus>) parameter.get("refCursor");
            result = ios.get(0);
        }
        return result;
    }
    
    @Override
    public void create(InsertionOrderStatus ioStatus, SqlSession session) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("ioId", ioStatus.getIoId());
        parameter.put("statusId", ioStatus.getStatusId());
        parameter.put("contactId", ioStatus.getContactId());
        parameter.put("createdTpwsKey", ioStatus.getCreatedTpwsKey()); 
        getPersistenceContext().execute(STATEMENT_INSERT_IO_STATUS, parameter, session);
    }
}
