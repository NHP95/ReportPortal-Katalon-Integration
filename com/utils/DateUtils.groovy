package com.utils

import java.text.DateFormat
import java.text.SimpleDateFormat

public class DateUtils {
	public static final String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"
	public static final String ISO_FORMAT_DATE_ONLY = "yyyy-MM-dd"
	public static getISOCurrentDate(String timezone='',String format=ISO_FORMAT) {
		return convertToISODate(new Date(), timezone, format)
	}

	public static convertToISODate(Date date,String timezone='', String format=ISO_FORMAT) {
		TimeZone tz = TimeZone.getTimeZone(timezone)
		if (timezone.isEmpty()) {
			def zone = TimeZone.getDefault()
			tz = TimeZone.getTimeZone(zone.ID)
		}
		DateFormat df = new SimpleDateFormat(format)
		df.setTimeZone(tz)
		return df.format(date)
	}
}
