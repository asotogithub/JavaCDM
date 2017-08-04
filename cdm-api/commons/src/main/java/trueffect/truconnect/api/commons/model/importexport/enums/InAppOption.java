package trueffect.truconnect.api.commons.model.importexport.enums;

import java.io.Serializable;

/**
 * Created by richard.jaldin on 6/9/2016.
 */
public enum InAppOption  implements Serializable {
    DUPLICATE,
    DO_NOT_IMPORT;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
