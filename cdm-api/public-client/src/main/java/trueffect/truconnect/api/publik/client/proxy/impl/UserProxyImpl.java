package trueffect.truconnect.api.publik.client.proxy.impl;

import trueffect.truconnect.api.commons.model.AgencyUser;
import trueffect.truconnect.api.commons.model.Advertiser;
import trueffect.truconnect.api.commons.model.RecordSet;
import trueffect.truconnect.api.commons.model.User;
import trueffect.truconnect.api.commons.model.UserAdvertiser;
import trueffect.truconnect.api.commons.model.UserDomain;
import trueffect.truconnect.api.commons.model.dto.CookieDomainDTO;
import trueffect.truconnect.api.publik.client.base.ContentType;
import trueffect.truconnect.api.publik.client.base.ServiceProxyImpl;
import trueffect.truconnect.api.publik.client.proxy.AuthenticationPublicServiceProxy;

import com.sun.jersey.api.client.ClientResponse;

public class UserProxyImpl extends ServiceProxyImpl<User> {

    private final String servicePath;

    public UserProxyImpl(Class<User> type, String baseUrl, String servicePath, AuthenticationPublicServiceProxy authenticator) {
        super(type, baseUrl, servicePath, authenticator);
        this.servicePath = servicePath;
    }

    public AgencyUser getAgencyUserByTpwsKey(String tpwsKey) throws Exception {
        query("tpws", tpwsKey);
        String type = getContentType().getType();
        ClientResponse response = header().type(type).accept(type).get(ClientResponse.class);
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleErrors(response);
            } catch (Exception e) {
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    AgencyUser result = getAgencyUserByTpwsKey(tpwsKey);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return getAgencyUserEntity(response);
    }

    private AgencyUser getAgencyUserEntity(ClientResponse response) throws Exception {
        if (getContentType().getType().equalsIgnoreCase(ContentType.JSON.getType())) {
            String output = response.getEntity(String.class);
            return getJsonMapper().readValue(output, AgencyUser.class);
        }
        return response.getEntity(AgencyUser.class);
    }

    public void setSecurityLimit(String username, User user) throws Exception {
        makeWebResourceNotFollow();
        path(username);
        path("limit");
        String type = getContentType().getType();
        ClientResponse response = header().type(type).accept(type).put(ClientResponse.class, user);
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.SEE_OTHER.getStatusCode()) {
            try {
                handleNewErrors(response);
            } catch (Exception e) {
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    setSecurityLimit(username, user);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                } else {
                    throw e;
                }
            }
        }
    }

    public RecordSet<CookieDomainDTO> getUserDomains(String username) throws Exception {
        path(username);
        path("domains");
        ClientResponse response = header().accept(getContentType().getType()).get(ClientResponse.class);
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleErrors(response);
            } catch (Exception e) {
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    RecordSet<CookieDomainDTO> result = getUserDomains(username);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return getOtherEntities(response);
    }

    public RecordSet<UserDomain> updateUserDomainsList(String username, RecordSet<UserDomain> dom) throws Exception {
        path(username);
        path("userDomains");
        String type = getContentType().getType();
        ClientResponse response = header().type(type).accept(type).put(ClientResponse.class, dom);
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleNewErrors(response);
            } catch (Exception e) {
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    RecordSet<UserDomain> result = updateUserDomainsList(username, dom);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return getOtherEntities(response);
    }

    public RecordSet<Advertiser> getUserAdvertisers(String username) throws Exception {
        path(username);
        path("advertisers");
        ClientResponse response = header().accept(getContentType().getType()).get(ClientResponse.class);
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleNewErrors(response);
            } catch (Exception e) {
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    RecordSet<Advertiser> result = getUserAdvertisers(username);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return getOtherEntities(response);
    }

    public RecordSet<UserAdvertiser> updateUserAdvertisersList(String username, RecordSet<UserAdvertiser> adv) throws Exception {
        path(username);
        path("userAdvertisers");
        String type = getContentType().getType();
        ClientResponse response = header().type(type).accept(type).put(ClientResponse.class, adv);
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleErrors(response);
            } catch (Exception e) {
                if (!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    RecordSet<UserAdvertiser> result = updateUserAdvertisersList(username, adv);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return getOtherEntities(response);
    }
}
