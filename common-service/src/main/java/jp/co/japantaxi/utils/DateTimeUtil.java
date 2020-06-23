package jp.co.japantaxi.utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateTimeUtil {

	static Logger LOGGER = LoggerFactory.getLogger(DateTimeUtil.class);

	public static final TimeZone TIMEZONE_TOKYO = TimeZone.getTimeZone("Asia/Tokyo");
	public static final TimeZone TIMEZONE_UTC = TimeZone.getTimeZone("UTC");
	public static final String DATE_FM_S = "EEE MMM dd HH:mm:ss zzz yyyy";
	public static final String TIME_FM_S = "HH:mm:ss.SSSSSS";
	public static final String DATE_TIME_FM = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_TIME_FM_S = "yyyy-MM-dd HH:mm:ss.SSSSSS";
	public static final String MN_FM = "yyyy-MM-dd HH:mm";
	public static final String HH_FM = "yyyy-MM-dd HH";
	public static final String DD_FM = "yyyy-MM-dd";
	public static final String DD_FM_S = "yyyy/MM/dd";
	public static final String MM_FM = "yyyy-MM";
	public static final String YYYY_FM = "yyyy";

	public static Date getDateFromString(String date, String format) {
		Date dt = null;
		if (!"".equalsIgnoreCase(date)) {
			SimpleDateFormat sp = new SimpleDateFormat(format);
			try {
				dt = sp.parse(date);
			} catch (ParseException e) {
				LOGGER.error("Error ParseException getDateFromString: {} ", e.getMessage());
			}
		}
		return dt;
	}

	public static Date getDateFromString(String date, String format, TimeZone timeZone) {
		Date dt = null;
		if (!"".equalsIgnoreCase(date)) {
			SimpleDateFormat sp = new SimpleDateFormat(format);
			sp.setTimeZone(timeZone);
			try {
				dt = sp.parse(date);
			} catch (ParseException e) {
				LOGGER.error("Error ParseException getDateFromString: {} ", e.getMessage());
			}
		}
		return dt;
	}

	public static String getStringFromDate(Date date, String format) {
	    if (date != null) {
	      SimpleDateFormat sp = new SimpleDateFormat(format);
	      return sp.format(date);
        }
		return new StringBuilder().toString();
	}

	public static String getStringFromTimestamp(Timestamp timestamp, String format) {
		SimpleDateFormat sp = new SimpleDateFormat(format);
		return sp.format(timestamp);
	}

	public static LocalDateTime convertLongToDateTime(long date) {
		if (date != 0) {
			return LocalDateTime.ofInstant(Instant.ofEpochSecond(date), TIMEZONE_UTC.toZoneId());
		}
		return null;
	}

	public static Timestamp getTimestampFromDate(Date date, String format) {
		Timestamp timestamp = null;
		if (date != null) {
			String dateString = getStringFromDate(date, format);
			if (!dateString.contains("{}")) {
				long dateLong = Timestamp.valueOf(dateString).getTime();
				LocalDateTime dateLD = convertLongToDateTime(dateLong);
				timestamp = Timestamp.valueOf(dateLD);
			}
		}
		return timestamp;
	}

	public static Timestamp getTimestampFromString(String strDate, String format) {
		try {
			if (strDate.isEmpty())
				return null;
			SimpleDateFormat sp = new SimpleDateFormat(format);
			Date date = sp.parse(strDate);
			Timestamp timeStampDate = new Timestamp(date.getTime());
			return timeStampDate;
		} catch (ParseException e) {
			LOGGER.error("Error parseexception convertStringToTimestamp: {} ", e.getMessage());
			return null;
		}
	}

	public static boolean isValid(String dateStr) {
		DateFormat sdf = new SimpleDateFormat(DATE_TIME_FM);
		sdf.setLenient(false);
		try {
			sdf.parse(dateStr);
		} catch (ParseException e) {
			return false;
		}
		return true;
	}

    public static Date trim(Date date) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(date);
      calendar.set(Calendar.HOUR_OF_DAY, date.getHours() + 2);
      return calendar.getTime();
    }
    
    public static String resetDateTime(String time) {
    	Date dt = getDateFromString(time, DATE_TIME_FM, TIMEZONE_TOKYO);
	    String str = getStringFromDate(dt, DATE_TIME_FM);
	    Date date = trim(getDateFromString(str, DATE_TIME_FM));
	    return getStringFromDate(date, DATE_TIME_FM);
    }
    
}
