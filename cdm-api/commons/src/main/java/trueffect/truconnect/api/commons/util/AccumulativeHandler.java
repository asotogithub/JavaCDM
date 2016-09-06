package trueffect.truconnect.api.commons.util;

import java.util.List;

/**
 * Method definition for an XLS Handler capable of accumulate the data
 * Created by marcelo.heredia on 12/17/2015.
 * @author Marcelo Heredia
 */
public interface AccumulativeHandler<E> {
    /**
     * Gets the list of elements that got accumulated after
     * iterating over all rows in the XLS sheet
     * @return The list with elements taken from the XLS file
     */
    List<E> getList();
}
