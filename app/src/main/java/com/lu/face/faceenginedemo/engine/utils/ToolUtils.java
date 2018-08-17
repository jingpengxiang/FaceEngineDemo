package com.lu.face.faceenginedemo.engine.utils;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author liulv
 * @version create：2016年2月29日 上午11:10:46
 * @company 海鑫科金技术股份有限公司
 * 
 */
public class ToolUtils {

	private static String TAG = "FaceDetect";

	/** 检测目录是否存在，如果不存在可选建立目录（包括各级目录） */
	public static boolean dirIsExists(String dirName, boolean isCreate) {
		boolean ret;
		try {
			File f = new File(dirName);
			if (!f.exists()) {
				if (isCreate)
					ret = f.mkdirs();
				else
					ret = false;
			} else
				ret = f.isDirectory();
		} catch (Exception e) {
			ret = false;
		}
		return ret;
	}

	/** 判断文件是否存在 */
	public static boolean fileIsExists(String fileName) {

		try {
			File f = new File(fileName);
			if (!f.exists()) // true if this file exists, false otherwise.
			{
				return false;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/** Copy Files in Assets to Working Path */
	public static Boolean CopyFiles(Context context, String dstFileName, String fileName) {
		Boolean ret = false;
		try {
			InputStream in = context.getAssets().open(fileName);
			ret = copyInputStream(in, dstFileName);
			LogUtil.getInstance().i(TAG, "CopyAssetsFiles ：" + dstFileName + " ret=" + ret);
			in.close();
		} catch (IOException e) {
			LogUtil.getInstance().i(TAG, "CopyAssetsFiles ：" + dstFileName + " IOException!");
			ret = false;
		}

		return ret;
	}

	/** 比较两个文件是否相同（根据文件大小） */
	public static Boolean isSameFile(Context context, String nativeFile, String assetsFile) {
		try {
			InputStream in = context.getAssets().open(assetsFile);
			File file = new File(nativeFile);
			if (in != null && file != null) {
				LogUtil.getInstance().i(TAG, "assetsLen: " + in.available() + " file Size: " + file.length());
				if (in.available() == file.length())
					return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/** 复制Assets文件 */
	public static boolean copyInputStream(InputStream inStream, String filename2) {
		try {
			// TODO:add storege check.
			if (null != inStream && null != filename2) {
				FileOutputStream outStream;
				outStream = new FileOutputStream(filename2); // 源文件

				// 文件头4096字节,每站120字节,必须固定空间,否则无效.
				byte[] buffer = new byte[4096];
				int blocklen;
				while ((blocklen = inStream.read(buffer)) > 0) {
					outStream.write(buffer, 0, blocklen); // 将内容写到新文件当中
				}
				inStream.close();
				outStream.flush();
				outStream.close();
				outStream = null; // 新文件
				return true;
			}

		} catch (Exception e) {
		}
		return false;
	}
	
	public static void writeInfo(String info, String path, String fileName) {
		FileWriter filewriter=null;
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		String filePath = path + File.separator + fileName;
		try {
			filewriter = new FileWriter(new File(filePath));
			filewriter.write(info);
			filewriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(filewriter!=null){
				try{filewriter.close();}catch(IOException e){};
			}
		}
	}
	

}
