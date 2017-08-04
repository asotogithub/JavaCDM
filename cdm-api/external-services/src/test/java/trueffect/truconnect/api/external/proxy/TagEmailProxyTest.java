package trueffect.truconnect.api.external.proxy;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import trueffect.truconnect.api.commons.exceptions.ProxyException;
import trueffect.truconnect.api.commons.model.delivery.TagEmailWrapper;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import org.junit.Test;

import javax.ws.rs.core.HttpHeaders;

/**
 *
 * @author rodrigo.alarcon
 */
public class TagEmailProxyTest {
    
    @Test
    public void postSuccessful() throws ProxyException {
        HttpHeaders headers = mock(HttpHeaders.class);
        TagEmailProxy.UtilityWrapper utilityWrapper = mock(TagEmailProxy.UtilityWrapper.class);
        WebResource.Builder builder = mock(WebResource.Builder.class);
        ClientResponse clientResponse = mock(ClientResponse.class);
        
        when(clientResponse.getStatus()).thenReturn(ClientResponse.Status.OK.getStatusCode());
        when(builder.post(eq(ClientResponse.class), anyString())).thenReturn(clientResponse);
        when(utilityWrapper.getWebResourceBuilder()).thenReturn(builder);
        when(clientResponse.getEntity(eq(String.class))).thenReturn("{\"IsSuccess\":true,\"Message\":\"\"}");
        
        TagEmailProxy proxy = new TagEmailProxy(headers, utilityWrapper);
        TagEmailWrapper wrapper = new TagEmailWrapper();
        
        TagEmailWrapper response = proxy.post(wrapper);
        
        assertThat(response, not(nullValue()));
        assertThat(response.isIsSuccess(), is(true));
    }
}
