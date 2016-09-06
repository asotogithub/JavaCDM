package trueffect.truconnect.api.commons.util;

import java.text.ParseException;

/**
 *
 * @author Rambert Rioja
 */
public class ValueParser {

    public static Object parse(String classType, String value) throws ParseException {
        if (classType.matches("Long")) {
            return Long.parseLong(value);
        } else if (classType.matches("Integer")) {
            return Integer.parseInt(value);
        } else if (classType.matches("Date")) {
            return DateConverter.tryParseDate(value);
        } else if (classType.matches("Byte")) {
            return Byte.valueOf(value);
        } else if (classType.matches("Character")) {
            return Character.valueOf(value.charAt(0));
        } else if (classType.matches("Boolean")) {
            return Boolean.parseBoolean(value);
        }

        return value;
    }
}
