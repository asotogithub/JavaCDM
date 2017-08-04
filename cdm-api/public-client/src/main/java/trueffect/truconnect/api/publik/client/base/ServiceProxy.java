package trueffect.truconnect.api.publik.client.base;

import javax.ws.rs.core.MultivaluedMap;

import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SuccessResponse;

public interface ServiceProxy<T> {

    public T getById(Object id) throws Exception;

    /**
     * This performs an insert or update, based on whether the target object
     * exists. Generally, the system will assume that the target exists if there
     * is a non-zero "id" on the target.
     */
    public T save(T target) throws Exception;

    public T update(T target) throws Exception;

    public SuccessResponse delete(Object id) throws Exception;

    public SuccessResponse delete() throws Exception;

    public boolean hardDelete(Object id) throws Exception;

    public MultivaluedMap<String, String> getResponseHeaders();

    public RecordSet<T> find(String query, Long startIndex, Long pageSize) throws Exception;
}