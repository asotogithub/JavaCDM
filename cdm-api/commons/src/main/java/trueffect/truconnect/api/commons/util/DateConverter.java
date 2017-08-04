package trueffect.truconnect.api.commons.util;

import static org.apache.commons.lang.time.DateUtils.truncate;

import trueffect.truconnect.api.commons.validation.ValidationConstants;
import trueffect.truconnect.api.resources.ResourceBundleUtil;

import org.apache.commons.lang.time.DateUtils;
import org.apache.poi.ss.usermodel.DateUtil;
import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Logger;

/**
 *
 * @author Rambert Rioja
 */
public class DateConverter {
    // TODO review if this logs gets added into wherever other logs are reporting. Will be
    // done once we have bandwidth to do so
    private final static Logger LOG = Logger.getLogger(DateConverter.class.getName());

    private static ThreadLocal<DateFormat> df = new ThreadLocal<DateFormat> () {
        @Override
        public DateFormat get() {
            return super.get();
        }

        @Override
        protected DateFormat initialValue() {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DEFAULT_IMPORT_EXPORT_FORMAT);
            simpleDateFormat.setLenient(false);
            return simpleDateFormat;
        }

        @Override
        public void remove() {
            super.remove();
        }

        @Override
        public void set(DateFormat value) {
            super.set(value);
        }
    };
    /**
     * Import utility supported Date formats.
     * Examples of supported formats:
     * <pre>
     * "11/27/2015 10:48:59 PM MST",
     * "11/27/2015 10:48:59 PM",
     * "11/27/2015 10:48:59",
     * "11/27/2015"
     * </pre>
     */
    public static final String[] SUPPORTED_FORMATS = {
            "MM/dd/yyyy HH:mm:ss z", // This is the default import/export format
            "MM/dd/yyyy hh:mm:ss a z",
            "MM/dd/yyyy hh:mm:ss a",
            "MM/dd/yyyy HH:mm:ss",
            "MM/dd/yyyy"
    };

    public static final String DEFAULT_IMPORT_EXPORT_FORMAT = SUPPORTED_FORMATS[0];

    /**
     * Tries to parse a Date string with any of the defined formats of {@link DateConverter#SUPPORTED_FORMATS}
     * @param dateStr The String date to parse
     * @return The Date object parsed in case the date string was parsed
     * @throws ParseException in case the String date cannot be parse with any of the supported formats
     */
    public static Date parseForImport(String dateStr) throws ParseException {
        if(dateStr == null) {
            return null;
        }
        return DateUtils.parseDateStrictly(dateStr, SUPPORTED_FORMATS);
    }

    public static Date importExportParse(String dateStr){
        if(dateStr == null) {
            return null;
        }
        try {
            return df.get().parse(dateStr);
        } catch (ParseException e) {
            LOG.warning("Could not parse " + dateStr);
        }
        return null;
    }

    public static String importExportFormat(Date date) {
        return df.get().format(date);
    }

    public static final String ISO8601 = "yyyy-MM-dd HH:mm:ss";
    public static final SimpleDateFormat ISO8601_FORMATTER = new SimpleDateFormat(ISO8601);
    // We should not use this way of parsing dates as it is not thread safe
    @Deprecated
    public static List<SimpleDateFormat> DATE_FORMATS = new ArrayList<SimpleDateFormat>() {
        {
            add(new SimpleDateFormat("MM-dd-yyyy"));
            add(new SimpleDateFormat("MM-dd-yyyy hh:mm:ss"));
            add(new SimpleDateFormat("yyyy-MM-dd"));
            add(ISO8601_FORMATTER);
            add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
            add(new SimpleDateFormat("MM/dd/yyyy hh:mm aa zzz"));
            add(new SimpleDateFormat("dd/MM/yyyy"));
            add(new SimpleDateFormat("yyyy/MM/dd"));
            add(new SimpleDateFormat("dd-MMM-yyyy"));
        }
    };

    public static Date tryParseDate(String dateStr) throws ParseException {
        if (dateStr != null) {
            for (SimpleDateFormat format : DATE_FORMATS) {
                try {
                    format.setLenient(false);
                    return format.parse(dateStr);
                } catch (ParseException e) {
                }
            }
        }
        throw new ParseException("Invalid date format", 0);
    }

    public static Date today() {
        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }

    public static Date todayPlusDays(int days) {
        Date date = today();
        DateTime dtOrg = new DateTime(date);
        return dtOrg.plusDays(days).toDate();
    }
    public static Date todayPlusDays2(int days){
        Date date = today();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, days);
        return calendar.getTime();

    }
    public static Date todayEndPlusDays(int days){
        Date date = today();
        DateTime dtOrg = new DateTime(date);
        date= dtOrg.plusDays(days).toDate();
        return endDate(date);
    }
    public static Date todayPlusMonths(int months) {
        Date date = today();
        DateTime dtOrg = new DateTime(date);
        return dtOrg.plusMonths(months).toDate();
    }

    public static Date todayPlusYears(int years) {
        Date date = today();
        DateTime dtOrg = new DateTime(date);
        return dtOrg.plusYears(years).toDate();
    }

    public static Date startDate(Date date) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return truncate(calendar.getTime(), Calendar.SECOND);
    }

    public static boolean datesConsecutive(Date current, Date previous) {
        if(current == null || previous == null) {
            return true;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(current);
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        return endDate(calendar.getTime()).compareTo(previous) == 0;
    }

    public static Date parseXlsStringToDate(String value) throws ParseException {
        if(value == null) {
            return null;
        }
        Date result;
        String date = value;
        date = date.trim();
        if(date.matches(ValidationConstants.XLS_DATE_VALUE_REGEXP)){
            result = DateUtil.getJavaDate(Double.valueOf(date));
        } else {
            result = DateConverter.parseForImport(date);
        }
        return result;
    }

    public static Date endDate(Date date) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return truncate(calendar.getTime(), Calendar.SECOND);
    }

    public static String formatToISO8601(Date date) {
        return ISO8601_FORMATTER.format(date);
    }

    public static Date toDefaultTimezone(Date d){
        if(d == null) {
            throw new IllegalArgumentException(ResourceBundleUtil.getString("global.error.null", "d"));
        }
        // API runs over MST
        Calendar defaultCalendar = Calendar.getInstance(TimeZone.getDefault());
        Calendar incomingCalendar = Calendar.getInstance();
        incomingCalendar.setTime(d);
        for (int i = 0; i < Calendar.FIELD_COUNT; i++) {
            defaultCalendar.set(i, incomingCalendar.get(i));
        }
        return defaultCalendar.getTime();
    }
}
