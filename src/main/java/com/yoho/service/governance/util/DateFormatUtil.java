package com.yoho.service.governance.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 负责时间格式转换
 * @author yoho
 *
 */
public class DateFormatUtil {

	private static final String YYYYMMDD = "yyyyMMdd";

	private static final String HHMMSS = "HHmmss";
	
	private static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
	
	private static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
	
	private static final String INFLUXDB_TIME_PATTEN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	private static final String INFLUXDB_TIME_NANO_PATTEN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	
	private static final Logger logger = LoggerFactory.getLogger(DateFormatUtil.class);
	

	public static String getYYYYMMDD() {
		return new SimpleDateFormat(YYYYMMDD).format(new Date());
	}

	public static String getHHmmss() {
		return new SimpleDateFormat(HHMMSS).format(new Date());
	}

	public static String getYYYYMMDDByCalendar(Calendar ca) {
		return new SimpleDateFormat(YYYYMMDD).format(ca.getTime());
	}
	
	public static String getYYYYMMDDHHMMSS() {
		return new SimpleDateFormat(YYYYMMDDHHMMSS).format(new Date());
	}
	public static String getYYYY_MM_DD_HH_MM_SS() {
		return new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS).format(new Date());
	}

	/**
	 * 将influxDB中的时间格式转换为格式yyyy-MM-dd HH:mm:ss
	 * @param date
	 * @return
	 */
	public static String displayFormat(String date) {
		return displayFormatWithDateTail(date);
	}

	public static String displayFormatWithDateTail(String date) {
		String dateWithoutTail = date.substring(0, 19) + "Z";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(INFLUXDB_TIME_NANO_PATTEN);
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		try {
			Date d = simpleDateFormat.parse(dateWithoutTail);
			Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"));
			calendar.setTime(d);

			SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return s.format(calendar.getTime());
		} catch (ParseException e) {
			logger.debug("method:displayFormat args:"+date + "  "+ e.toString());
			return "";
		}
	}
	
	/**
	 * 将毫秒数格式化为influxDB时间格式的字符串 yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
	 * @param timeMsec
	 * @return
	 */
	public static String influxDBTimeFormat(String timeMsec) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(INFLUXDB_TIME_PATTEN, Locale.GERMAN);
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		try{
			Date date = new Date(Long.parseLong(timeMsec));	
			return simpleDateFormat.format(date);
		}catch (Exception e) {
			logger.debug("method:displayFormat timeMsec:"+timeMsec + "  "+ e.toString());
			return "";
		}
	}
	
	public static String influxDBTimeFormat(long timeMsec) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(INFLUXDB_TIME_PATTEN, Locale.GERMAN);
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		try{
			Date date = new Date(timeMsec);	
			return simpleDateFormat.format(date);
		}catch (Exception e) {
			logger.debug("method:displayFormat timeMsec:"+timeMsec + "  "+ e.toString());
			return null;
		}
	}


	
	public static String parseLongTo14(long time){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(YYYYMMDDHHMMSS);
		return simpleDateFormat.format(new Date(time));
	}

	public static String getDateByFormart(Long t,String formart) {

		if(t == null) {
			return null;
		}

		DateFormat sf = new SimpleDateFormat(formart);
		long time = t * 1000L;
		Date date = new Date(time);
		return sf.format(date);
	}
}
