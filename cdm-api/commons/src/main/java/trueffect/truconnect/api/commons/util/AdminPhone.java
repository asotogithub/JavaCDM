package trueffect.truconnect.api.commons.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Gustavo Claure
 */
public class AdminPhone {
    public static boolean validatePhone(String phone) {
        String PHONE_PATTERN = "^(?:(?:\\+?1\\s*(?:[.-]\\s*)?)?(?:\\(\\s*([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9])\\s*\\)|([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9]))\\s*(?:[.-]\\s*)?)?([2-9]1[02-9]|[2-9][02-9]1|[2-9][02-9]{2})\\s*(?:[.-]\\s*)?([0-9]{4})(?:\\s*(?:#|x\\.?|ext\\.?|extension)\\s*(\\d+))?$";
        Pattern p = Pattern.compile(PHONE_PATTERN);
        Matcher matcher = p.matcher(phone);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }
}
