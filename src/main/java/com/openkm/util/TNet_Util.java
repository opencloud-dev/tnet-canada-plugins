package com.openkm.util;

import java.util.Calendar;
import com.openkm.util.FileLogger;

public class TNet_Util {
	
	public static String TNET_CONFIG_LOG_INFO = "tnet.cfg_log_info";
	public static String TNET_CONFIG_LOG_ERROR = "tnet.cfg_log_error";
	public static String TNET_CONFIG_LOG_WARN = "tnet.cfg_log_warn";
	
	
	public static String addYearMonthDayToPath(String path) {
		
	    Calendar calendar = Calendar.getInstance();
	    int month = calendar.get(2) + 1;
	    return path + "/" + calendar
	      .get(1) + "/" + 
	      String.format("%02d", new Object[] { Integer.valueOf(month) }) + "/" + 
	      String.format("%02d", new Object[] { Integer.valueOf(calendar.get(5)) });
	  }

	public static void infoLogger(String BASE_NAME, String token, String msj) {
		FileLogger.info(BASE_NAME, String.valueOf("[OC:"+token+"]"+msj), new Object[0]);
	  }

	public static void errorLogger(String BASE_NAME, String token, String msj) {
		FileLogger.error(BASE_NAME, String.valueOf("[OC:"+token+"]"+msj), new Object[0]);
	  }

	public static void warnLogger(String BASE_NAME, String token, String msj) {
		FileLogger.warn (BASE_NAME, String.valueOf("[OC:"+token+"]"+msj), new Object[0]);
	}

	
}
