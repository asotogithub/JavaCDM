package trueffect.truconnect.api.publik.client.proxy;

import trueffect.truconnect.api.commons.model.Package;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.publik.client.base.ServiceProxyImpl;
import trueffect.truconnect.api.publik.client.proxy.impl.PackageProxyImpl;

import java.util.concurrent.ExecutionException;

/**
 * @author Thomas Barjou on 08/18/2015
 */
public class PackagePublicServiceProxy extends GenericPublicServiceProxy<Package> {

    public PackagePublicServiceProxy(String baseUrl, String authenticationUrl, String contentType, String userName, String password) throws Exception {
        super(baseUrl, authenticationUrl, "Packages", contentType, userName, password);
    }

    public Package getById(int id) throws Exception {
        ServiceProxyImpl<Package> proxy = getProxy();
        return proxy.getByIdThrowNewError(id);
    }

    public Package update(Object id, Package pakage) throws Exception {
        ServiceProxyImpl<Package> proxy = getProxy();
        proxy.path(id.toString());
        return proxy.updateThrowNewError(pakage);
    }

    public SuccessResponse bulkPackageDelete(RecordSet<Long> packageIds) throws Exception {
        PackageProxyImpl proxy = getProxy();
        return proxy.bulkPackageDelete(packageIds);
    }
    public RecordSet<Package> find(String query, Long startIndex, Long pageSize) throws Exception {
        ServiceProxyImpl<Package> proxy = getProxy();
        return proxy.find(query, startIndex, pageSize);
    }

    public RecordSet<Package> find(String query) throws Exception {
        return this.find(query, null, null);
    }

    public RecordSet<Package> find() throws Exception {
        return this.find(null, null, null);
    }

    protected PackageProxyImpl getProxy() {
        PackageProxyImpl proxy = new PackageProxyImpl(Package.class, baseUrl, servicePath, authenticator);
        proxy.setContentType(contentType);
        return proxy;
    }
}
