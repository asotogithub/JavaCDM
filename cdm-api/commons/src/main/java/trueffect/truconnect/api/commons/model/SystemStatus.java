package trueffect.truconnect.api.commons.model;

import trueffect.truconnect.api.commons.util.Either;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "SystemStatus")
public class SystemStatus {

    public static final String VALID_STATUS_MESSAGE = "Success";

    private String version;
    private String cmDbStatusMessage;
    private Boolean cmDbConnectionValid;
    private String metricsDbStatusMessage;
    private Boolean metricsDbConnectionValid;

    public SystemStatus() {}

    public SystemStatus(Either<String, Boolean> cmConnection, Either<String, Boolean> metricsConnection) {
        if(cmConnection.isError()) {
            setCmDbStatusMessage(cmConnection.error());
            setCmDbConnectionValid(false);
        } else {
            setCmDbStatusMessage(VALID_STATUS_MESSAGE);
            setCmDbConnectionValid(true);
        }

        if(metricsConnection.isError()) {
            setMetricsDbStatusMessage(metricsConnection.error());
            setMetricsDbConnectionValid(false);
        } else {
            setMetricsDbStatusMessage(VALID_STATUS_MESSAGE);
            setMetricsDbConnectionValid(true);
        }
    }

    public String getCmDbStatusMessage() {
        return cmDbStatusMessage;
    }

    public void setCmDbStatusMessage(String cmDbStatusMessage) {
        this.cmDbStatusMessage = cmDbStatusMessage;
    }

    public String getMetricsDbStatusMessage() {
        return metricsDbStatusMessage;
    }

    public void setMetricsDbStatusMessage(String metricsDbStatusMessage) {
        this.metricsDbStatusMessage = metricsDbStatusMessage;
    }

    public Boolean getCmDbConnectionValid() {
        return cmDbConnectionValid;
    }

    public void setCmDbConnectionValid(Boolean cmDbConnectionValid) {
        this.cmDbConnectionValid = cmDbConnectionValid;
    }

    public Boolean getMetricsDbConnectionValid() {
        return metricsDbConnectionValid;
    }

    public void setMetricsDbConnectionValid(Boolean metricsDbConnectionValid) {
        this.metricsDbConnectionValid = metricsDbConnectionValid;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
