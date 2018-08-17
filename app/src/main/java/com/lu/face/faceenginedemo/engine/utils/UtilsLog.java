package com.lu.face.faceenginedemo.engine.utils;

import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
/**
 * 
 * @author liulv
 * @version create：2016年7月29日 上午11:10:46
 * @company 海鑫科金技术股份有限公司
 */
public class UtilsLog {
	public static boolean isTest=true;
	public static boolean isLog=false;//是否保存在本地

	public static void d(String key, String value) {
		if (isTest) {
			android.util.Log.d(key, value);
		}
	}

	public static void i(String key, String value) {
		if (isTest) {
			android.util.Log.i(key, value);
		}
	}

	public static void e(String key, String value) {
		if (isTest) {
			android.util.Log.e(key, value);
		}
	}

	public static void w(String key, String value) {
		if (isTest) {
			android.util.Log.w(key, value);
		}
	}

	public static void w(String key, Throwable tr) {
		if (isTest) {
			android.util.Log.w(key, tr);
		}
	}

	public static void w(String key, String value, Throwable tr) {
		if (isTest) {
			android.util.Log.w(key, value, tr);
		}
	}

	public static void log(String tag, String info) {
		StackTraceElement[] ste = new Throwable().getStackTrace();
		int i = 1;
		if (isTest) {
			StackTraceElement s = ste[i];
			android.util.Log.e(tag, String.format("======[%s][%s][%s]=====%s", s.getClassName(),
					s.getLineNumber(), s.getMethodName(), info));
		}
	}

	/**
	 * 
	 * @param content
	 *            保存日志
	 */
	public static void writeLog(String content) {
		if (isTest && isLog) {
			// log("write", "=====");
			int day, month;
			File localFile;
			Date date = new Date(System.currentTimeMillis());
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
					Locale.getDefault());
			content += "====" + formatter.format(date) + "====";
			Calendar calendar = Calendar.getInstance();
			day = calendar.get(Calendar.DAY_OF_MONTH);
			month = calendar.get(Calendar.MONTH);
			localFile = new File(getTxtFile(), month + "月"+ day + "日" + ".txt");
			try {
				// if (localFile.exists()) {
				FileWriter fileWriter = new FileWriter(localFile, true);
				BufferedWriter bWriter = new BufferedWriter(fileWriter);
				bWriter.write(content);
				bWriter.newLine();
				bWriter.flush();
				bWriter.close();
				// }
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	

	public static File getTxtFile() {
		File voiceFile = new File(Environment.getExternalStorageDirectory().toString() + "/"
				+ "Hisign" + "/" + "Log" + "/");

		if (!voiceFile.exists()) {
			voiceFile.mkdirs();
		}
		return voiceFile;
	}
}
