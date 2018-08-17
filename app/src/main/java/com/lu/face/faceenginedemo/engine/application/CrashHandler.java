package com.lu.face.faceenginedemo.engine.application;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.lu.face.faceenginedemo.R;
import com.lu.face.faceenginedemo.engine.utils.TimeUtil;
import com.lu.face.faceenginedemo.enginesdk.utils.FileUtil;
import com.lu.face.faceenginedemo.enginesdk.utils.StringUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告.
 * @ClassName:	CrashHandler 
 * @Description:TODO 
 * @author:	lvwenyan
 * @date: Sep 17, 2015 2:55:34 PM
 */
public class CrashHandler implements UncaughtExceptionHandler {

	public static CrashHandler getInstance() {
		if (null == mCrashHandler) {
			mCrashHandler = new CrashHandler();
		}
		return mCrashHandler;
	}

	/**
	 * 初始化
	 * @Title: init   
	 * @Description: TODO 
	 * @param: @param context      
	 * @return: void      
	 * @throws   
	 * @author: lvwenyan     
	 * @date:   Sep 17, 2015 2:57:33 PM
	 */
	public void init(Context context) {
		mContext = context;
		// 获取系统默认的UncaughtException处理器
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		// 设置该CrashHandler为程序的默认处理器
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * 当UncaughtException发生时会转入该函数来处理
	 * Title: uncaughtException
	 * Description: 
	 * @param thread
	 * @param ex 
	 * @see UncaughtExceptionHandler#uncaughtException(Thread, Throwable)
	 * @author: lvwenyan     
	 * @date:   Sep 17, 2015 2:57:53 PM
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
			// 如果用户没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				Log.e(TAG, "error : ", e);
			}
			// 退出程序
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(1);
		}
	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
	 * @Title: handleException   
	 * @Description: TODO 
	 * @param: @param ex
	 * @param: @return      
	 * @return: boolean  true:如果处理了该异常信息;否则返回false.    
	 * @throws   
	 * @author: lvwenyan     
	 * @date:   Sep 17, 2015 2:58:45 PM
	 */
	private boolean handleException(Throwable ex) {
		if (null == ex)
			return false;
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				Toast.makeText(mContext, mContext.getResources().getString(R.string.crash_toast), Toast.LENGTH_LONG).show();
				Looper.loop();
			}
		}.start();
		// 收集设备参数信息
		collectDeviceInfo(mContext);
		// 保存日志文件
		saveCrashInfo2File(ex);
		return true;
	}

	/**
	 * 收集设备参数信息
	 * @Title: collectDeviceInfo   
	 * @Description: TODO 
	 * @param: @param ctx      
	 * @return: void      
	 * @throws   
	 * @author: lvwenyan     
	 * @date:   Sep 17, 2015 3:02:58 PM
	 */
	public void collectDeviceInfo(Context ctx) {
		try {
			PackageInfo pi = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				String versionName = pi.versionName == null ? "null" : pi.versionName;
				String versionCode = pi.versionCode + "";
				infos.put("versionName", versionName);
				infos.put("versionCode", versionCode);
			}
		} catch (NameNotFoundException e) {
			Log.e(TAG, "an error occured when collect package info", e);
		}
		for (Field field : Build.class.getDeclaredFields()) {
			try {
				field.setAccessible(true);
				infos.put(field.getName(), field.get(null).toString());
				Log.d(TAG, field.getName() + " : " + field.get(null));
			} catch (Exception e) {
				Log.e(TAG, "an error occured when collect crash info", e);
			}
		}
	}

	/**
	 * 保存错误信息到文件中
	 * @Title: saveCrashInfo2File   
	 * @Description: TODO 
	 * @param: @param ex
	 * @param: @return      
	 * @return: String    返回文件名称,便于将文件传送到服务器  
	 * @throws   
	 * @author: lvwenyan     
	 * @date:   Sep 17, 2015 3:06:01 PM
	 */
	private String saveCrashInfo2File(Throwable ex) {
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : infos.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key + "=" + value + "\n");
		}

		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		String result = writer.toString();
		sb.append(result);
		try {
			long timestamp = System.currentTimeMillis();
			String fileName = "crash-" + TimeUtil.getDateFormat(0) + "-" + timestamp + ".txt";
			if (StringUtil.isEqual(Environment.MEDIA_MOUNTED, Environment.getExternalStorageState())) {
				String path = Environment.getExternalStorageDirectory().getPath() + File.separator + "HisignEngineLog" + File.separator + "Log" + File.separator;
				FileUtil.getInstance().mkDir(path);
				FileOutputStream fos = new FileOutputStream(path + fileName);
				fos.write(sb.toString().getBytes());
				fos.close();
			}
			return fileName;
		} catch (Exception e) {
			Log.e(TAG, "an error occured while writing file...", e);
		}
		return null;
	}

	private Context mContext;
	private static CrashHandler mCrashHandler;
	private UncaughtExceptionHandler mDefaultHandler;
	private Map<String, String> infos = new HashMap<String, String>();
	private static final String TAG = CrashHandler.class.getSimpleName();
}
