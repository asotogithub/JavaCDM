package trueffect.truconnect.api.commons.model.delivery;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.List;

/**
 *
 * @author rodrigo.alarcon
 */
public class TruQTagList {
    
    private List<TruQTagMessage> tagMessages;
    
    @JsonProperty("IsError")
    private Boolean isError;
    
    @JsonProperty("SessionKey")
    private String sessionKey;
    
    @JsonProperty("Messages")
    private List<String> messages;
    
    @JsonProperty("Data")
    private List<Boolean> data;

    public TruQTagList() {
        
    }
    
    public TruQTagList(List<TruQTagMessage> tagMessages) {
        this.tagMessages = tagMessages;
    }
    
    public List<TruQTagMessage> getTagMessages() {
        return tagMessages;
    }

    public void setTagMessages(List<TruQTagMessage> tagMessages) {
        this.tagMessages = tagMessages;
    }

    public Boolean getIsError() {
        return isError;
    }

    public void setIsError(Boolean isError) {
        this.isError = isError;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public List<Boolean> getData() {
        return data;
    }

    public void setData(List<Boolean> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
