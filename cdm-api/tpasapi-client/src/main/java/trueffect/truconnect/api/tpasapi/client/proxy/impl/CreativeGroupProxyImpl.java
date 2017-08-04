package trueffect.truconnect.api.tpasapi.client.proxy.impl;

import com.sun.jersey.api.client.ClientResponse;

import trueffect.truconnect.api.commons.model.SuccessResponse;
import trueffect.truconnect.api.tpasapi.model.CreativeGroup;
import trueffect.truconnect.api.tpasapi.model.CreativeGroupCreatives;
import trueffect.truconnect.api.tpasapi.model.Schedule;
import trueffect.truconnect.api.tpasapi.client.base.ContentType;
import trueffect.truconnect.api.tpasapi.client.base.ServiceProxyImpl;
import trueffect.truconnect.api.tpasapi.client.proxy.AuthenticationTpasapiServiceProxy;

public class CreativeGroupProxyImpl extends ServiceProxyImpl<CreativeGroup> {

    public CreativeGroupProxyImpl(Class<CreativeGroup> type, String baseUrl, String servicePath, AuthenticationTpasapiServiceProxy authenticator) {
        super(type, baseUrl, servicePath, authenticator);
    }

    public SuccessResponse delete(Object id, Boolean recursiveDelete) throws Exception {
        path(id.toString());
        query("recursiveDelete", recursiveDelete.toString());
        return this.delete();
    }

    public CreativeGroupCreatives getCreativeGroupCreatives(Object id) throws Exception {
        path(id.toString());
        path("creatives");
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
                    CreativeGroupCreatives result = getCreativeGroupCreatives(id);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return getCreativeGroupCreativesEntity(response);
    }

    public CreativeGroupCreatives updateCreativeGroupCreatives(Object id, CreativeGroupCreatives cgc) throws Exception {
        path(id.toString());
        path("creatives");
        String type = getContentType().getType();
        ClientResponse response = header().type(type).accept(type).put(ClientResponse.class, getPostData(cgc));
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleErrors(response);
            } catch (Exception e) {
                if(!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    CreativeGroupCreatives result = updateCreativeGroupCreatives(id, cgc);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return getCreativeGroupCreativesEntity(response);
    }

    private CreativeGroupCreatives getCreativeGroupCreativesEntity(ClientResponse response) throws Exception {
        if (getContentType().getType().equalsIgnoreCase(ContentType.JSON.getType())) {
            String output = response.getEntity(String.class);
            return getJsonMapper().readValue(output, CreativeGroupCreatives.class);
        }
        return response.getEntity(CreativeGroupCreatives.class);
    }

    public Schedule getSchedule(Object id) throws Exception {
        path(id.toString());
        path("schedule");
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
                    Schedule result = getSchedule(id);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return result;
                } else {
                    throw e;
                }
            }
        }
        return getScheduleEntity(response);
    }

    public Schedule updateSchedule(Object id, Schedule schedule) throws Exception {
        Schedule result = null;
        path(id.toString());
        path("schedule");
        String type = getContentType().getType();
        ClientResponse response = header().type(type).accept(type).put(ClientResponse.class, getPostData(schedule));
        setLastResponse(response);
        clearAll();
        if (response.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
            try {
                handleErrors(response);
            } catch (Exception e) {
                if(!isRetry && tokenExpired(response, e.getMessage())) { // Retry
                    isRetry = true;
                    Schedule retryResult = updateSchedule(id, schedule);
                    isRetry = false; //clearing it after retry call has been done
                    clearAll();
                    return retryResult;
                } else {
                    throw e;
                }
            }
        }

        if ((schedule.getPlacements() != null && !schedule.getPlacements().isEmpty())
                && (schedule.getCreatives() != null && !schedule.getCreatives().isEmpty())) {
            result = getScheduleEntity(response);
        }
        return result;
    }
    
    private Schedule getScheduleEntity(ClientResponse response) throws Exception {
        if (getContentType().getType().equalsIgnoreCase(ContentType.JSON.getType())) {
            String output = response.getEntity(String.class);
            return getJsonMapper().readValue(output, Schedule.class);
        }
        return response.getEntity(Schedule.class);
    }
}
