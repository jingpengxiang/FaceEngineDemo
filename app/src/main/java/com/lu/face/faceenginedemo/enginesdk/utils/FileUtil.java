package com.lu.face.faceenginedemo.enginesdk.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.MemoryFile;

import com.THID.FaceSDK.THIDFaceRect;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

public class FileUtil {
	private static final String TAG = "FileUtil";
	private static volatile FileUtil mFileUtil;
	private LinkedList<MemoryFile> mfList = new LinkedList<MemoryFile>();

	public static FileUtil getInstance() {
		if (null == mFileUtil) {
			synchronized (FileUtil.class) {
				if (null == mFileUtil) {
					mFileUtil = new FileUtil();
				}
			}
		}
		return mFileUtil;
	}

	/**copy算法SDK需要的数据文件
	 * @Title: copySdkData   
	 * @param context	Context对象
	 * @param folder	算法sdkdata文件夹名字
	 * @param path      copy目标文件夹
	 * @return: void   
	 * @date:May 24, 2017 9:46:54 AM	@author:lvwenyan
	 */
	public boolean copySdkData(Context context, String folder, String path) {
		boolean ret = false;
		try {
			mkDir(path);
			String[] sdkFiles = null;
			sdkFiles = context.getAssets().list(folder);
			if (null != sdkFiles && 0 < sdkFiles.length) {
				for (String fileName : sdkFiles) {
					inputstream2File(context.getAssets().open(folder + File.separator + fileName), path + File.separator + fileName);
				}
				ret = true;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ret = false;
		}
		return ret;
	}

	/**创建目录
	 * @Title: mkDir   
	 * @param path
	 * @return      
	 * @return: File   
	 * @date:May 24, 2017 9:45:11 AM	@author:lvwenyan
	 */
	public File mkDir(String path) {
		File dirFile = new File(path.trim());
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
		return dirFile;
	}

	/**
	 * 拷贝文件
	 * 
	 * @Title: copyFile
	 * @Description: TODO
	 * @param: @param
	 *             srcPath
	 * @param: @param
	 *             desPath
	 * @return: void
	 * @throws @author:
	 *             lvwenyan
	 * @date: Jul 23, 2015 3:35:11 PM
	 */
	public void copyFile(String srcPath, String desPath) {
		if (null == srcPath || null == desPath) {
			return;
		}
		delAndMkdir(desPath);
		try {

			File srcFile = new File(srcPath);
			FileInputStream fin = new FileInputStream(srcFile);

			File desFile = new File(desPath);
//			desFile.delete();
			FileOutputStream fout = new FileOutputStream(desFile);

			int length = fin.available();
			byte[] buffer = new byte[length];
			fin.read(buffer);
			fout.write(buffer);

			fout.close();
			fin.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**删除path路径文件，并根据path是否有分隔符创建目录
	 * @Title: delAndMkdir   
	 * @param path      
	 * @return: void   
	 * @date:May 24, 2017 9:44:45 AM	@author:lvwenyan
	 */
	private void delAndMkdir(String path) {
		if (StringUtil.isNotNull(path)) {
			delFile(path);
			if (path.contains(File.separator)) {
				String dirPath = path.substring(0, path.lastIndexOf(File.separator));
				mkDir(dirPath);
			}
		}
	}

	/**删除目录或文件
	 * @Title: delFile   
	 * @param path      
	 * @return: void   
	 * @date:May 24, 2017 9:44:20 AM	@author:lvwenyan
	 */
	public void delFile(String path) {
		if (StringUtil.isNotNull(path)) {
			String pathTmp = path.trim();
			File file = new File(pathTmp);
			if (null != file && file.exists()) {
				if (file.isFile()) {
					file.delete();
				} else if (file.isDirectory()) {
					File[] childFiles = file.listFiles();
					int length = childFiles.length;
					if (0 < length) {
						for (int i = 0; i < length; i++) {
							childFiles[i].delete();
						}
					}
					file.delete();
				}
			}
		}
	}

	/**InputStream数据保存为文件
	 * @Title: inputstream2File   
	 * @param ins	输入数据流
	 * @param path  文件的保存路径     
	 * @return: void   
	 * @date:May 24, 2017 9:43:20 AM	@author:lvwenyan
	 */
	public void inputstream2File(InputStream ins, String path) {
		if (null != ins && StringUtil.isNotNull(path)) {
			String pathTmp = path.trim();
			delAndMkdir(pathTmp);
			try {
				FileOutputStream fos = new FileOutputStream(pathTmp);
				// 创建一个4*1024大小的缓冲区
				byte buffer[] = new byte[4096];
				int tmp = 0;
				// 循环读取InputStream里的数据
				while (-1 != (tmp = ins.read(buffer))) {
					// 把流按照buffer的大小写入到文件中
					fos.write(buffer, 0, tmp);
				}
				// 提交缓冲区的内容
				fos.flush();
				fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**文件转byte数组
	 * @Title: file2Bytes   
	 * @param path	文件路径
	 * @return      
	 * @return: byte[]   
	 * @date:May 24, 2017 11:23:09 AM	@author:lvwenyan
	 */
	public byte[] file2Bytes(String path) {
		byte[] ret = null;
		if (StringUtil.isNotNull(path)) {
			String pathTmp = path.trim();
			try {
				FileInputStream fis = new FileInputStream(pathTmp);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buffer = new byte[4096];
				int tmp = 0;
				while (-1 != (tmp = fis.read(buffer))) {
					baos.write(buffer, 0, tmp);
				}
				ret = baos.toByteArray();
				baos.flush();
				baos.close();
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return ret;
	}

	public Bitmap yuv2Bitmap(byte[] data, int width, int height) {
		int frameSize = width * height;
		int[] rgba = new int[frameSize];

		for (int i = 0; i < height; i++)
			for (int j = 0; j < width; j++) {
				int y = (0xff & ((int) data[i * width + j]));
				int u = (0xff & ((int) data[frameSize + (i >> 1) * width + (j & ~1) + 0]));
				int v = (0xff & ((int) data[frameSize + (i >> 1) * width + (j & ~1) + 1]));
				y = y < 16 ? 16 : y;

				int r = Math.round(1.164f * (y - 16) + 1.596f * (v - 128));
				int g = Math.round(1.164f * (y - 16) - 0.813f * (v - 128) - 0.391f * (u - 128));
				int b = Math.round(1.164f * (y - 16) + 2.018f * (u - 128));

				r = r < 0 ? 0 : (r > 255 ? 255 : r);
				g = g < 0 ? 0 : (g > 255 ? 255 : g);
				b = b < 0 ? 0 : (b > 255 ? 255 : b);

				rgba[i * width + j] = 0xff000000 + (b << 16) + (g << 8) + r;
			}

		Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		bmp.setPixels(rgba, 0, width, 0, 0, width, height);
		return bmp;
	}

	/**
	 * 传入图像，按人脸区域裁剪
	 * 
	 * @Title: getCutImage
	 * @Description: TODO
	 * @param: @param  inputFilePath 传入的图像文件
	 * @param: @param   rect 人脸框位置
	 * @param: @return
	 * @return: Bitmap 输出bitmap 图像文件
	 * @throws @author:   lvwenyan
	 * @date: Sep 8, 2015 3:34:03 PM
	 */
	public Bitmap getCutImage(Bitmap inputBitmap, THIDFaceRect rect) {
		if (null == inputBitmap || inputBitmap.isRecycled()) {
			return null;
		}
		int width = rect.right - rect.left;
		int height = rect.bottom - rect.top;

		int enlargeWidth = (int) (width * 2F);
		int enlargeHeight = (int) (height * 3F);
		int enlargeLeft = rect.left - (enlargeWidth - width) / 2;
		int enlargeRight = rect.right + (enlargeWidth - width) / 2;
		int enlargeTop = rect.top - (enlargeHeight - height) / 2;
		int enlargeBottom = rect.bottom + (enlargeHeight - height) / 2;
		if (enlargeTop < 0) {
			enlargeTop = 0;
			enlargeBottom = enlargeHeight;
		}
		if (enlargeLeft < 0) {
			enlargeLeft = 0;
			enlargeRight = enlargeWidth;
		}
		if (inputBitmap.getWidth() > inputBitmap.getHeight()) {
			if (enlargeBottom > 480) {
				enlargeBottom = 480;
				enlargeTop = enlargeBottom - enlargeHeight;
				if (enlargeTop < 0) {
					enlargeTop = 0;
				}
			}
			if (enlargeRight > 640) {
				enlargeRight = 640;
				enlargeLeft = enlargeRight - enlargeWidth;
				if (enlargeLeft < 0) {
					enlargeLeft = 0;
				}
			}
		} else {
			if (enlargeBottom > 640) {
				enlargeBottom = 640;
				enlargeTop = enlargeBottom - enlargeHeight;
				if (enlargeTop < 0) {
					enlargeTop = 0;
				}
			}
			if (enlargeRight > 480) {
				enlargeRight = 480;
				enlargeLeft = enlargeRight - enlargeWidth;
				if (enlargeLeft < 0) {
					enlargeLeft = 0;
				}
			}
		}
		int widthTmp = enlargeRight - enlargeLeft;
		int heightTmp = enlargeBottom - enlargeTop;
		Bitmap bitmap = Bitmap.createBitmap(inputBitmap, enlargeLeft, enlargeTop, widthTmp, heightTmp);
		return bitmap;
	}

	/**
	 * bitmap转jpeg格式，并保存图片
	 * 
	 * @Title: bitmapToJpeg
	 * @Description: TODO
	 * @param: @param
	 *             bitmap input bitmap
	 * @param: @param
	 *             path 保存路径
	 * @return: void
	 * @throws @author:
	 *             lvwenyan
	 * @date: Sep 10, 2015 3:40:17 PM
	 */
	public void bitmapToJpeg(Bitmap bitmap, String path) {
		if (bitmap == null || path == null)
			return;
		delAndMkdir(path);
		File file = new File(path);
		if (file.exists()) {
			file.delete();
		}
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			if (!bitmap.isRecycled()) {
				if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {// Bitmap.CompressFormat.PNG
					out.flush();
					out.close();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (null != out) {
			try {
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			out = null;
		}
	}

	/**
	 * 把byte数组写入文件
	 * @param srcByte  需要写入的byte
	 * @param imagePath 文件目录
	 */
	public void saveImageByByteAndPath(byte[] srcByte, String imagePath) {
		if (srcByte == null || imagePath == null) {
			return;
		}
		delAndMkdir(imagePath);
		ByteArrayInputStream bis = null;
		FileOutputStream fos = null;
		byte[] buffer = new byte[8019];
		int len = -1;

		try {
			bis = new ByteArrayInputStream(srcByte);
			fos = new FileOutputStream(imagePath);
			while ((len = bis.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
			}
			fos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (Exception e2) {
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (Exception e2) {
				}
			}
		}

	}

	/**
	 * 由图片路径得到图片字节数组
	 * 
	 * @Title: imageToByte
	 * @Description: TODO
	 * @param: @param
	 *             path
	 * @param: @return
	 * @return: byte[]
	 * @throws @author:
	 *             lvwenyan
	 * @date: Jul 23, 2015 3:38:43 PM
	 */
	public byte[] imageToByte(String path) {

		byte[] data = null;
		FileInputStream input = null;
		try {
			input = new FileInputStream(new File(path));
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int numBytesRead = 0;
			while ((numBytesRead = input.read(buf)) != -1) {
				output.write(buf, 0, numBytesRead);
			}
			data = output.toByteArray();
			output.close();
			input.close();
		} catch (FileNotFoundException ex1) {
			ex1.printStackTrace();
		} catch (IOException ex1) {
			ex1.printStackTrace();
		}

		return data;
	}

	public byte[] bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		return baos.toByteArray();
	}

	public byte[] bitmap2BytesNew(Bitmap bm) {
		if (bm == null)
			return null;
		int width = bm.getWidth();
		int height = bm.getHeight();
		int[] pixels = new int[width * height];
		byte[] bmp = new byte[width * height];
		bm.getPixels(pixels, 0, width, 0, 0, width, height);
		for (int i = 0; i < width * height; i++) {
			bmp[i] = (byte) (pixels[i] & 0xff);
		}
		return bmp;
	}

	public String getNameByPath(String path) {
		String name = null;
		if (StringUtil.isNotNull(path)) {
			File file = new File(path);
			if (null != file && file.exists()) {
				name = file.getName();
			}
		}
		return name;
	}
}
