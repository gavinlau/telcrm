package org.eredlab.g4.telcrm.service.util;

import java.util.Calendar;
public class DateUtil {
	//
	public static String convertCalendarToDateString(Calendar c)
	{
		int year=c.get(java.util.Calendar.YEAR);
		int mon=c.get(java.util.Calendar.MONTH)+1;
		int day=c.get(java.util.Calendar.DAY_OF_MONTH);
		return year+"-"+mon+"-"+day;
	}
}
