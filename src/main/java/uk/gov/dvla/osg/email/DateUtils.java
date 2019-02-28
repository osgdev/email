package uk.gov.dvla.osg.email;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * The Class DateUtils.
 */
public class DateUtils {

    /**
     * Creates a time stamp in the supplied format.
     *
     * @param format the format
     * @return the string
     */
    public static String timeStamp(String format) {
        return DateFormatUtils.format(new Date(), format);
    }
    
    /**
     * Converts a string from its current format to a different format.
     *
     * @param date - The date string to convert
     * @param currentFormat - The format the date is currently in
     * @param newFormat - The new format to convert the date into
     * @return The modified date
     * @throws ParseException Signals that an error has occured unexpectedly while parsing the date
     */
    public static String convert(String date, String currentFormat, String newFormat) throws ParseException {
        // Format date string is currently in
        DateFormat fromFormat = new SimpleDateFormat(currentFormat);
        // Convert date string to Date object
        Date parse = fromFormat.parse(date);
        // Format to convert date to
        DateFormat toFormat = new SimpleDateFormat(newFormat);
        // Convert the Date object back to a string in the new format
        return toFormat.format(parse);
    }
}
