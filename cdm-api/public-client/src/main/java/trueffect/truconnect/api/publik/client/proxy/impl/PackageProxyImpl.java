package trueffect.truconnect.api.publik.client.proxy.impl;

import trueffect.truconnect.api.commons.model.Package;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.publik.client.base.ServiceProxyImpl;
import trueffect.truconnect.api.publik.client.proxy.AuthenticationPublicServiceProxy;
import com.sun.jersey.api.client.ClientResponse;

/**
 * Created by Siamak Marjouei october/12/2015
 */
public class PackageProxyImpl extends ServiceProxyImpl<Package> {

    public PackageProxyImpl(Class<Package> type, String baseUrl, String servicePath, AuthenticationPublicServiceProxy authenticator) {
        super(type, baseUrl, servicePath, authenticator);
    }

    public SuccessResponse bulkPackageDelete(RecordSet<Long> packageIds) throws Exception {
        path("bulkDelete");
        String type = getContentType().getType();
        ClientResponse response = header().type(type).accept(type).put(ClientResponse.class, packageIds);
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleNewErrors(response);
            } catch (Exception e) {
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    SuccessResponse result = bulkPackageDelete(packageIds);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return getSuccessEntity(response);
    }
}
