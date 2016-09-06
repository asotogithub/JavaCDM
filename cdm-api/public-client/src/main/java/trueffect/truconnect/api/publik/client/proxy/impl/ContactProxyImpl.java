package trueffect.truconnect.api.publik.client.proxy.impl;

import com.sun.jersey.api.client.ClientResponse;

import trueffect.truconnect.api.commons.model.AgencyContact;
import trueffect.truconnect.api.commons.model.Contact;
import trueffect.truconnect.api.commons.model.SiteContact;
import trueffect.truconnect.api.publik.client.base.ContentType;
import trueffect.truconnect.api.publik.client.base.ServiceProxyImpl;
import trueffect.truconnect.api.publik.client.proxy.AuthenticationPublicServiceProxy;

public class ContactProxyImpl extends ServiceProxyImpl<Contact> {

    public ContactProxyImpl(Class<Contact> type, String baseUrl, String servicePath, AuthenticationPublicServiceProxy authenticator) {
        super(type, baseUrl, servicePath, authenticator);
    }
    
    public Contact getByUser() throws Exception {
        path("ByUser");
        String type = getContentType().getType();
        ClientResponse response = header().type(type).accept(type).get(ClientResponse.class);
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleErrors(response);
            } catch (Exception e) {
                if(!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    Contact result = getByUser();
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return getEntity(response);
    }

    public AgencyContact addContactAgencyRef(AgencyContact agencyContact) throws Exception {
        path("AgencyContactRef");
        String type = getContentType().getType();
        ClientResponse response = header().type(type).accept(type).post(ClientResponse.class, getPostData(agencyContact));
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleErrors(response);
            } catch (Exception e) {
                if(!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    AgencyContact result = addContactAgencyRef(agencyContact);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return getAgencyContactEntity(response);
    }

    public SiteContact addSiteContactRef(SiteContact siteContact) throws Exception {
        path("siteContactRef");
        String type = getContentType().getType();
        ClientResponse response = header().type(type).accept(type).post(ClientResponse.class, getPostData(siteContact));
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleErrors(response);
            } catch (Exception e) {
                if(!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    SiteContact result = addSiteContactRef(siteContact);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return getSiteContactEntity(response);
    }
    
    public void removeContactAgencyRef(AgencyContact agencyContact) throws Exception {
        path("AgencyContactRef");
        query("contactId", agencyContact.getContactId().toString());
        query("agencyId", agencyContact.getAgencyId().toString());
        this.delete();
    }
    
    private AgencyContact getAgencyContactEntity(ClientResponse response) throws Exception {
        if (getContentType().getType().equalsIgnoreCase(ContentType.JSON.getType())) {
            String output = response.getEntity(String.class);
            return getJsonMapper().readValue(output, AgencyContact.class);
        }
        return response.getEntity(AgencyContact.class);
    }

    private SiteContact getSiteContactEntity(ClientResponse response) throws Exception {
        if (getContentType().getType().equalsIgnoreCase(ContentType.JSON.getType())) {
            String output = response.getEntity(String.class);
            return getJsonMapper().readValue(output, SiteContact.class);
        }
        return response.getEntity(SiteContact.class);
    }
}
