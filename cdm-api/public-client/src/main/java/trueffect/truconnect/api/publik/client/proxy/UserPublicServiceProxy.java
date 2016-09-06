package trueffect.truconnect.api.publik.client.proxy;

import trueffect.truconnect.api.commons.model.Advertiser;
import trueffect.truconnect.api.commons.model.AgencyUser;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.commons.model.User;
import trueffect.truconnect.api.commons.model.UserAdvertiser;
import trueffect.truconnect.api.commons.model.UserDomain;
import trueffect.truconnect.api.commons.model.dto.CookieDomainDTO;
import trueffect.truconnect.api.publik.client.proxy.impl.UserProxyImpl;

/**
 *
 * @author michelle.bowman
 */
public class UserPublicServiceProxy extends GenericPublicServiceProxy<User> {

    public UserPublicServiceProxy(String baseUrl, String authenticationUrl, String contentType, String username, String password) throws Exception {
        super(baseUrl, authenticationUrl, "Users", contentType, username, password);
    }

    public User getByUsername(String username) throws Exception {
        UserProxyImpl proxy = getProxy();
        return proxy.getById(username);
    }

    public User create(User input) throws Exception {
        UserProxyImpl proxy = getProxy();
        return proxy.save(input);
    }

    public User update(String username, User input) throws Exception {
        UserProxyImpl proxy = getProxy();
        proxy.path(username);
        return proxy.update(input);
    }

    public SuccessResponse delete(User input) throws Exception {
        UserProxyImpl proxy = getProxy();
        return proxy.delete(input);
    }

    public AgencyUser getAgencyUserByTpwsKey(String tpwsKey) throws Exception {
        UserProxyImpl proxy = getProxy();
        return proxy.getAgencyUserByTpwsKey(tpwsKey);
    }

    public RecordSet<User> find(String query) throws Exception {
        UserProxyImpl proxy = getProxy();
        return proxy.find(query, null, null);
    }

    public RecordSet<CookieDomainDTO> getUserDomains(String username) throws Exception {
        return getProxy().getUserDomains(username);
    }

    public RecordSet<UserDomain> putUserDomainsList(String username, RecordSet<UserDomain> cookieDomains) throws Exception {
        return getProxy().updateUserDomainsList(username, cookieDomains);
    }

    public void limitUserDomainsAndAdvertisers(String username, User input) throws Exception {
        UserProxyImpl proxy = getProxy();
        proxy.setSecurityLimit(username,input);
    }

    public RecordSet<Advertiser> getUserAdvertisers(String username) throws Exception {
        return getProxy().getUserAdvertisers(username);
    }

    public RecordSet<UserAdvertiser> putUserAdvertisersList(String username, RecordSet<UserAdvertiser> adv) throws Exception {
        return getProxy().updateUserAdvertisersList(username, adv);

    }

    protected UserProxyImpl getProxy() {
        UserProxyImpl proxy = new UserProxyImpl(User.class, baseUrl, servicePath, authenticator);
        proxy.setContentType(contentType);
        return proxy;
    }
}
