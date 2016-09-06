package trueffect.truconnect.api.commons.service.proxy;

import trueffect.truconnect.api.commons.exceptions.ProxyException;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SearchCriteria;
import trueffect.truconnect.api.commons.model.SuccessResponse;

/**
 *
 * @author Rambert Rioja
 */
public interface ServiceProxy<T> {

	public T get() throws ProxyException;
	
    public RecordSet<T> get(SearchCriteria criteria) throws ProxyException;

    public T post(T t) throws ProxyException;

    public T put(T t) throws ProxyException;

    public SuccessResponse delete() throws ProxyException;
}
