package trueffect.truconnect.api.tpasapi.proxy;

import javax.ws.rs.core.HttpHeaders;

import com.sun.jersey.api.client.ClientResponse;

import trueffect.truconnect.api.commons.exceptions.ProxyException;
import trueffect.truconnect.api.commons.model.Column;
import trueffect.truconnect.api.commons.model.Columns;

/**
 *
 * @author Richard Jaldin
 */
public class HardDeleteProxy<T> extends BaseProxy<T> {

    public HardDeleteProxy(Class<T> type, HttpHeaders headers) {
        super(type, headers);
        path("UnitTestHelper");
    }

	public void removeRowWithSingleKey(String table, Column column) throws ProxyException {
		path("RemoveRowWithSingleKey");
		query("table", table);
		ClientResponse response = header().type(getContentType())
                .accept(getContentType()).post(ClientResponse.class, column);
        this.setLastResponse(response);
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            handleErrors(response);
        }
	}

	public void removeRowWithComposedKey(String table, Columns columns) throws ProxyException {
		path("RemoveRowWithComposedKey");
		query("table", table);
		ClientResponse response = header().type(getContentType())
                .accept(getContentType()).post(ClientResponse.class, columns);
        this.setLastResponse(response);
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            handleErrors(response);
        }
	}
}
