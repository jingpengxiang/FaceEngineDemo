package com.lu.face.faceenginedemo.engine.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.List;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.os.Build;
import android.os.Debug;

public class MemoryUtil {
	private static final String TAG = "MemoryUtil";
	private static long preAvailMem;

	/** 
	 * 打印当前手机内存信息应用的内存信息 
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public static void printMemoryInfo() {
		final String TAG = "MemoryUtils.printMemoryInfo()";
		// 打印当前APP内存信息

		// 开启了 android:largeHeap="true" 后,就是启用了流氓应用的内存限制
		// 打印当前应用内存信息
		Runtime rt = Runtime.getRuntime();
		LogUtil.getInstance().i(TAG,
				"APP当前内存状态: 最大可申请内存:" + rt.maxMemory() / 1024 / 1024 + "MB 已申请内存:" + rt.totalMemory() / 1024 / 1024 + "MB 空闲内存:" + rt.freeMemory() / 1024 / 1024 + "MB");

	}

	/** 
	 * 获得app可用内存的字节数  这个类不需要try,catch理论上不会报错 
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static long getAppSurplusMe(Context context, String packageName, String path) {
		final String TAG = "MemoryUtils.getAppSurplusMe()";

		Runtime rt = Runtime.getRuntime();
		// 一下参数单位为字节数
		long totalMemory = rt.totalMemory();// 这个是已经申请的内存,等于已经使用的内存加上空闲内存
		long maxMemory = rt.maxMemory();// 最大内存限制
		long freeMemory = rt.freeMemory();

		// 假如最大内存限制是64M,已经申请了34M,空闲4M,那么其实当前使用的是:(34-4)M,而实际当前有效可使用的内存是:64-(34-4)=34;
		// 64-(34-4)=34 请允许我引用高数老师的那句话:"同理可得" 64-34+4
		// so
		long surplusMemory = maxMemory - totalMemory + freeMemory;
//		LogUtil.getInstance().i(TAG, "系统当前内存状态: 最大可申请内存:" + rt.maxMemory() / 1024 / 1024 + "MB 已申请内存:" + rt.totalMemory() / 1024 / 1024 + "MB 空闲内存:" + rt.freeMemory() / 1024 / 1024
//				+ "MB" + " cpu : " + (float) (100F * getAppCpuTime() / getTotalCpuTime()) + "%");
//		LogUtil.getInstance().writeTxtToFile("maxMemory:" + rt.maxMemory() / 1024 / 1024 + "MB totalMemory:" + rt.totalMemory() / 1024 / 1024 + "MB freeMemory:"
//				+ rt.freeMemory() / 1024 / 1024 + "MB" + " cpu : " + (float) (100F * getAppCpuTime() / getTotalCpuTime()) + "%", path);
//		long curAvailMem = getAvailMemory(context);
//		LogUtil.getInstance().i(TAG, "curAvailMem = " + String.valueOf(curAvailMem - preAvailMem));
//		preAvailMem = curAvailMem;
		getRunningAppProcessInfo(context, android.os.Process.myPid(), path);
		return surplusMemory;
	}

	private static void getRunningAppProcessInfo(Context context, int pid, String path) {
		ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

		// 获得系统里正在运行的所有进程
		List<RunningAppProcessInfo> runningAppProcessesList = mActivityManager.getRunningAppProcesses();

		for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcessesList) {
			if (pid == runningAppProcessInfo.pid) {
				int[] pids = new int[] { pid };
				Debug.MemoryInfo[] memoryInfo = mActivityManager.getProcessMemoryInfo(pids);
				int memorySize = memoryInfo[0].dalvikPrivateDirty;
				int totalPss = memoryInfo[0].getTotalPss();
				LogUtil.getInstance().i(TAG, "totalPss = " + String.valueOf(totalPss / 1024) + "mb");
				LogUtil.getInstance().writeTxtToFile("totalPss = " + String.valueOf(totalPss / 1024) + "mb", path);
				break;
			}
//	        // 进程ID号  
//	        int pid = runningAppProcessInfo.pid;  
//	        // 用户ID  
//	        int uid = runningAppProcessInfo.uid;  
//	        // 进程名  
//	        String processName = runningAppProcessInfo.processName;  
//	        // 占用的内存  
//	        int[] pids = new int[] { pid };  
//	        Debug.MemoryInfo[] memoryInfo = mActivityManager  
//	                .getProcessMemoryInfo(pids);  
//	        int memorySize = memoryInfo[0].dalvikPrivateDirty;  
//	        st = st + "processName=" + processName + ",pid=" + pid + ",uid="  
//	                + uid + ",memorySize=" + memorySize + "kb" + "\n";  
//	        System.out.println("processName=" + processName + ",pid=" + pid  
//	                + ",uid=" + uid + ",memorySize=" + memorySize + "kb");  
		}

	}

	public static long getAvailMemory(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
		am.getMemoryInfo(mi);
		return mi.availMem;
	}

	/** 
	 * 获得手机可用内存的字节数  这个类不需要try,catch,理论上不会报错 
	 * 
	 * 这个方法要慎用  容易导致崩溃  特别在引导页的时候  低内存手机容易发生崩溃 
	 * 
	 * @param context 
	 * @return 
	 */
	public static long getPhoneSurplusMe(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
		ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
		am.getMemoryInfo(mi);
		return mi.availMem;
	}

	private static String PID(String PackageName) {

		Process proc = null;
		String str3 = null;
		try {
			Runtime runtime = Runtime.getRuntime();
			proc = runtime.exec("adb shell ps |grep  " + PackageName);

			if (proc.waitFor() != 0) {
				System.err.println("exit value = " + proc.exitValue());
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			StringBuffer stringBuffer = new StringBuffer();
			String line = null;
			while ((line = in.readLine()) != null) {
				stringBuffer.append(line + " ");

			}
			String str1 = stringBuffer.toString();
			String str2 = str1.substring(str1.indexOf(" " + PackageName) - 46, str1.indexOf(" " + PackageName));
			String PID = str2.substring(0, 7);
			PID = PID.trim();

			str3 = PID;
		} catch (Exception e) {
			System.err.println(e);
		} finally {
			try {
				proc.destroy();
			} catch (Exception e2) {
			}
		}

		return str3;
	}

	public static double getFlow(String PackageName) {

		double flow = 0;
		try {

			String Pid = PID(PackageName);

			Runtime runtime = Runtime.getRuntime();
			Process proc = runtime.exec("adb shell cat /proc/" + Pid + "/net/dev");
			try {
				if (proc.waitFor() != 0) {
					System.err.println("exit value = " + proc.exitValue());
				}
				BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
				StringBuffer stringBuffer = new StringBuffer();
				String line = null;
				while ((line = in.readLine()) != null) {
					stringBuffer.append(line + " ");

				}
				String str1 = stringBuffer.toString();
				String str2 = str1.substring(str1.indexOf("wlan0:"), str1.indexOf("wlan0:") + 90);
				String str4 = str2.substring(7, 16);
				str4 = str4.trim();
				String str6 = str2.substring(67, 75);
				str6 = str6.trim();
				int b = Integer.parseInt(str4);
				int a = Integer.parseInt(str6);

				double sendFlow = a / 1024;
				double revFlow = b / 1024;
				flow = sendFlow + revFlow;

			} catch (InterruptedException e) {
				System.err.println(e);
			} finally {
				try {
					proc.destroy();
				} catch (Exception e2) {
				}
			}
		} catch (Exception StringIndexOutOfBoundsException) {
			System.out.println("请检查设备是否连接");

		}

		return flow;
	}

	public static double getCPU(String PackageName) {

		double Cpu = 0;
		try {

			Runtime runtime = Runtime.getRuntime();
			Process proc = runtime.exec("adb shell top -n 1| grep " + PackageName);
			try {
				if (proc.waitFor() != 0) {
					System.err.println("exit value = " + proc.exitValue());
				}
				BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
				StringBuffer stringBuffer = new StringBuffer();
				String line = null;
				while ((line = in.readLine()) != null) {
					stringBuffer.append(line + " ");

				}

				String str1 = stringBuffer.toString();
				String str3 = str1.substring(str1.indexOf(PackageName) - 43, str1.indexOf(PackageName)).trim();
				String cpu = str3.substring(0, 2);
				cpu = cpu.trim();
				Cpu = Double.parseDouble(cpu);

			} catch (InterruptedException e) {
				System.err.println(e);
			} finally {
				try {
					proc.destroy();
				} catch (Exception e2) {
				}
			}
		} catch (Exception StringIndexOutOfBoundsException) {

			System.out.println("请检查设备是否连接");

		}
		LogUtil.getInstance().i(TAG, "app totalCpu = " + Cpu);
		return Cpu;

	}

	public static double getMemory(String PackageName) {

		double Heap = 0;

		try {
			Runtime runtime = Runtime.getRuntime();
			Process proc = runtime.exec("adb shell dumpsys meminfo " + PackageName);
			try {
				if (proc.waitFor() != 0) {
					System.err.println("exit value = " + proc.exitValue());
				}
				BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
				StringBuffer stringBuffer = new StringBuffer();
				String line = null;
				while ((line = in.readLine()) != null) {
					stringBuffer.append(line + " ");

				}

				String str1 = stringBuffer.toString();
				String str2 = str1.substring(str1.indexOf("Objects") - 60, str1.indexOf("Objects"));
				String str3 = str2.substring(0, 10);
				str3 = str3.trim();
				Heap = Double.parseDouble(str3) / 1024;
				DecimalFormat df = new DecimalFormat("#.000");
				String memory = df.format(Heap);
				Heap = Double.parseDouble(memory);

			} catch (InterruptedException e) {
				System.err.println(e);
			} finally {
				try {
					proc.destroy();
				} catch (Exception e2) {
				}
			}
		}

		catch (Exception StringIndexOutOfBoundsException) {
			System.out.print("请检查设备是否连接");

		}
		return Heap;
	}

	private static long getTotalCpuTime() { // 获取系统总CPU使用时间
		String[] cpuInfos = null;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("/proc/stat")), 1000);
			String load = reader.readLine();
			reader.close();
			cpuInfos = load.split(" ");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		long totalCpu = Long.parseLong(cpuInfos[2]) + Long.parseLong(cpuInfos[3]) + Long.parseLong(cpuInfos[4]) + Long.parseLong(cpuInfos[6]) + Long.parseLong(cpuInfos[5])
				+ Long.parseLong(cpuInfos[7]) + Long.parseLong(cpuInfos[8]);
		LogUtil.getInstance().i(TAG, "totalCpu = " + totalCpu);
		return totalCpu;
	}

	private static long getAppCpuTime() { // 获取应用占用的CPU时间
		String[] cpuInfos = null;
		try {
			int pid = android.os.Process.myPid();
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("/proc/" + pid + "/stat")), 1000);
			String load = reader.readLine();
			reader.close();
			cpuInfos = load.split(" ");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		long appCpuTime = Long.parseLong(cpuInfos[13]) + Long.parseLong(cpuInfos[14]) + Long.parseLong(cpuInfos[15]) + Long.parseLong(cpuInfos[16]);
		LogUtil.getInstance().i(TAG, "app totalCpu = " + appCpuTime);
		return appCpuTime;
	}
}
