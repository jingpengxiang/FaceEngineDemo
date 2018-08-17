package com.lu.face.faceenginedemo.engine.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.RandomAccessFile;

public class LogUtil {
	private boolean isDebug;
	public boolean isDebug() {
		return isDebug;
	}

	public void setDebug(boolean isDebug) {
		this.isDebug = isDebug;
	}

	private static volatile LogUtil mLogUtil;

	public static LogUtil getInstance() {
		if (null == mLogUtil) {
			synchronized (LogUtil.class) {
				if (null == mLogUtil) {
					mLogUtil = new LogUtil();
				}
			}
		}
		return mLogUtil;
	}

	private final String LOG_CARD_PATH = Environment.getExternalStorageDirectory() + File.separator + "HisignEngine" + File.separator + "log_read_card.txt";
	private final String LOG_FEAT_PATH = Environment.getExternalStorageDirectory() + File.separator + "HisignEngine" + File.separator + "log_HS_ExtratFeat.txt";
	private final String LOG_VERIFY_PATH = Environment.getExternalStorageDirectory() + File.separator + "HisignEngine" + File.separator + "log_HS_FeatVerifyFeat.txt";
	private final String LOG_FILELIST_PATH = Environment.getExternalStorageDirectory() + File.separator + "HisignEngine" + File.separator + "log_FileList.txt";

	/**
	 * 将字符串写入到文本文件中
	 * @Title: writeTxtToFile   
	 * @Description: TODO 
	 * @param: @param strcontent
	 * @param: @param filePath
	 * @param: @param fileName      
	 * @return: void      
	 * @throws   
	 * @author: lvwenyan     
	 * @date:   May 16, 2017 2:44:45 PM
	 */
	public synchronized void writeTxtToFile(String strcontent, String path) {
		// 生成文件夹之后，再生成文件，不然会出错
		String dirPath = null;
		if (path.contains(File.separator)) {
			dirPath = path.substring(0, path.lastIndexOf(File.separator));
		}
		if (null != dirPath) {
			makeRootDirectory(dirPath);
		}
		File file = null;
		try {
			file = new File(path);
			if (!file.exists()) {
				file.createNewFile();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (null != file) {
			// 每次写入时，都换行写
			String strContent = strcontent + "\r\n";
			try {
				RandomAccessFile raf = new RandomAccessFile(file, "rwd");
				raf.seek(file.length());
				raf.write(strContent.getBytes());
				raf.close();
			} catch (Exception e) {
				Log.e("TestFile", "Error on write File:" + e);
			}
		}
	}

	/**
	 * 生成文件夹
	 * @Title: makeRootDirectory   
	 * @Description: TODO 
	 * @param: @param filePath      
	 * @return: void      
	 * @throws   
	 * @author: lvwenyan     
	 * @date:   May 16, 2017 2:52:03 PM
	 */
	public void makeRootDirectory(String filePath) {
		File file = null;
		try {
			file = new File(filePath);
			if (!file.exists()) {
				file.mkdir();
			}
		} catch (Exception e) {
			Log.i("error:", e + "");
		}
	}

	public String getLOG_FEAT_PATH() {
		return LOG_FEAT_PATH;
	}

	public String getLOG_VERIFY_PATH() {
		return LOG_VERIFY_PATH;
	}

	public String getLOG_FILELIST_PATH() {
		return LOG_FILELIST_PATH;
	}
	// --------------------------------------------show
	// log----------------------------------

	/**
	 * log日志打印
	 * @param tag   TAG
	 * @param msg   需要打印的字符串
	 */
	public void i(String tag, String msg) {

		if (isDebug) {
			Log.i(tag, msg);
		}
	}

	/**
	 * log日志打印
	 * @param tag   TAG
	 * @param msg   需要打印的字符串
	 */
	public void e(String tag, String msg) {

		if (isDebug) {
			Log.e(tag, msg);
		}
	}

	/**
	 * log日志打印
	 * @param tag   TAG
	 * @param msg   需要打印的字符串
	 */
	public void w(String tag, String msg) {

		if (isDebug) {
			Log.w(tag, msg);
		}
	}

	public String getLOG_CARD_PATH() {
		return LOG_CARD_PATH;
	}
}
