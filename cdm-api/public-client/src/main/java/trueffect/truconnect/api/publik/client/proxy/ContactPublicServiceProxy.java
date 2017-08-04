package trueffect.truconnect.api.publik.client.proxy;

import trueffect.truconnect.api.commons.model.AgencyContact;
import trueffect.truconnect.api.commons.model.Contact;
import trueffect.truconnect.api.commons.model.SiteContact;
import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.publik.client.proxy.impl.ContactProxyImpl;

/**
 *
 * @author michelle.bowman
 */
public class ContactPublicServiceProxy extends GenericPublicServiceProxy<Contact> {

    public ContactPublicServiceProxy(String baseUrl, String authenticationUrl, String contentType, String userName, String password) throws Exception {
        super(baseUrl, authenticationUrl, "Contacts", contentType, userName, password);
    }

    public Contact getById(int id) throws Exception {
        ContactProxyImpl proxy = getProxy();
        return proxy.getById(id);
    }

    public Contact getByUser() throws Exception {
        ContactProxyImpl proxy = getProxy();
        return ((ContactProxyImpl) proxy).getByUser();
    }

    public Contact create(Contact input) throws Exception {
        ContactProxyImpl proxy = getProxy();
        return proxy.save(input);
    }

    public Contact createSiteContact(SiteContact input) throws Exception {
        ContactProxyImpl proxy = getProxy();
        return proxy.save(input);
    }

    public Contact update(Object id, Contact input) throws Exception {
        ContactProxyImpl proxy = getProxy();
        proxy.path(id.toString());
        return proxy.update(input);
    }

    public SuccessResponse delete(Long id) throws Exception {
        ContactProxyImpl proxy = getProxy();
        return proxy.delete(id);
    }

    public AgencyContact addContactAgencyRef(AgencyContact agencyContact) throws Exception {
        ContactProxyImpl proxy = getProxy();
        return proxy.addContactAgencyRef(agencyContact);
    }

    public SiteContact addSiteContactRef(SiteContact siteContact) throws Exception {
        ContactProxyImpl proxy = getProxy();
        return proxy.addSiteContactRef(siteContact);
    }

    public void removeContactAgencyRef(AgencyContact agencyContact) throws Exception {
        ContactProxyImpl proxy = getProxy();
        proxy.removeContactAgencyRef(agencyContact);
    }

    protected ContactProxyImpl getProxy() {
        ContactProxyImpl proxy = new ContactProxyImpl(Contact.class, baseUrl, servicePath, authenticator);
        proxy.setContentType(contentType);
        return proxy;
    }
}
