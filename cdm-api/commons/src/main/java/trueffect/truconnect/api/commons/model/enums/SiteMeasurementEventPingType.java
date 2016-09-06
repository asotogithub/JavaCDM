package trueffect.truconnect.api.commons.model.enums;

/**
 * Created by rodrigo.alarcon on 8/15/2016.
 */
public enum SiteMeasurementEventPingType {
    BROADCAST(1),
    SELECTIVE(2);

    private int value;

    private SiteMeasurementEventPingType(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }
}
