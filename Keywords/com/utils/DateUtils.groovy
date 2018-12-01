package com.utils

import java.text.DateFormat
import java.text.SimpleDateFormat

public class DateUtils {
	public static final String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"
	public static getISOCurrentDate() {
		TimeZone tz = TimeZone.getTimeZone("UTC")
		DateFormat df = new SimpleDateFormat(ISO_FORMAT)
		df.setTimeZone(tz)
		return df.format(new Date())
	}
}
