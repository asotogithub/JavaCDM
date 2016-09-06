package trueffect.truconnect.api.commons.util;

import trueffect.truconnect.api.commons.model.CostDetail;

import java.util.Comparator;

/**
 * Compares two {@code CostDetail} objects
 * Created by marcelo.heredia on 10/9/2015.
 */
public class CostDetailComparator implements Comparator<CostDetail> {

    @Override
    public int compare(CostDetail cd1, CostDetail cd2) {
        if (cd1 == null) {
            if (cd2 == null) {
                return 0;
            } else {
                return Integer.MIN_VALUE;
            }
        } else if (cd2 == null) {
            return Integer.MAX_VALUE;
        }
        // Compare by Cost Detail ID
        int compare = 0;
        if (cd1.getId() == null) {
            compare = (cd2.getId() == null) ? 0 : Integer.MAX_VALUE;
        } else if (cd2.getId() == null) {
            compare = Integer.MIN_VALUE;
        }

        if (compare != 0) {
            return compare;
        }
        // Compare by null Start Dates
        if (cd1.getStartDate() == null) {
            compare = (cd2.getStartDate() == null) ? 0 : Integer.MIN_VALUE;
        } else if (cd2.getStartDate() == null) {
            compare = Integer.MAX_VALUE;
        }

        if (compare != 0) {
            return compare;
        }

        // Start Dates for both are non null, compare them
        return cd1.getStartDate().compareTo(cd2.getStartDate());
    }
}
