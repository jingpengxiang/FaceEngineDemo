package com.lu.face.faceenginedemo.engine.utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.lu.face.faceenginedemo.engine.data.ConstantValues;
import com.lu.face.faceenginedemo.enginesdk.utils.StringUtil;
import com.lu.face.faceenginedemo.enginesdk.utils.ValueUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("NewApi")
@SuppressWarnings("deprecation")
public class SysCameraControl implements SurfaceHolder.Callback, PreviewCallback, AutoFocusCallback {

	private static final String TAG = "SysCameraControl";
	public int WIDTH = 640; // 要保存图片的分辨率
	public int HEIGHT = 480;
	public OnCameraStatusListener eventListener;// 监听接口

	private SurfaceHolder g_Holder = null;

	public SurfaceHolder getG_Holder() {
		return g_Holder;
	}

	private Camera mCamera;
	private String picSavePath = "/sdcard/FaceMatch4Glass/"; // 拍照图片的保存路径
	private boolean safeToTakePicture = true; // 防止连续拍照导致的 RuntimeException:
												// takePicture failed 错误
	private boolean firstOpenCamera = true;
	private int cameraIndex = 0;
	private boolean isPreviewing = false;
	private boolean isAutoSupported;
	private int previewWidth;
	private int previewHeight;

	/** 构造函数 */
	public SysCameraControl(int cameraIndex, SurfaceView previewWall, int preWidth, int preHeight, int picWidth, int picHeight) {
		WIDTH = preWidth;
		HEIGHT = preHeight;
		LogUtil.getInstance().i(TAG, "wishing width height: " + WIDTH + " " + HEIGHT);
		this.cameraIndex = cameraIndex;
		if (previewWall != null) {
			g_Holder = previewWall.getHolder();
			g_Holder.addCallback(this);
			g_Holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
	}

	/** 自动聚焦 */
	public void camFocus() {
		try {
			mCamera.autoFocus(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** 相机支持的最大变焦数值 */
	public int getMaxZoomNum() {
		if (mCamera != null) {
			Camera.Parameters paramsBefore = mCamera.getParameters();
			return paramsBefore.getMaxZoom();
		}
		return 0;
	}

	/** 相机支持的最大变焦数值 */
	public int getZoomNum() {
		Camera.Parameters paramsBefore = mCamera.getParameters();
		return paramsBefore.getZoom();
	}

	/**
	 * 设置相机焦距
	 * 
	 * @param zoomLevel
	 *            调节焦距的值
	 *  设置成功后的焦距值
	 */
	public int setCameraZoom(int zoomLevel) {
		Camera.Parameters paramsBefore = mCamera.getParameters();
		if (!paramsBefore.isZoomSupported()) {
			return -1;
		}

		LogUtil.getInstance().i(TAG, "zoomlevel set: " + zoomLevel);
		if (zoomLevel < 0) {
			zoomLevel = 0;
		} else if (zoomLevel > paramsBefore.getMaxZoom()) {
			zoomLevel = paramsBefore.getMaxZoom();
		}

		paramsBefore.setZoom(zoomLevel);
		mCamera.setParameters(paramsBefore);
		LogUtil.getInstance().i(TAG, "zoomlevel set leave: " + zoomLevel);
		return zoomLevel;
	}

	////////////////////////////////////////////////////////////////////////////////////// V
	/**
	 * 设置想要保存图片的路径
	 * 
	 * @param path
	 */
	public void setPicSavePath(String path) {
		picSavePath = path;
	}

	/**
	 * 相机拍照监听接口 (拍完照后执行该函数)
	 */
	public interface OnCameraStatusListener {
		/* 拍照动作完成，则执行该函数（需要再调用者类中重写该函数） */
		public void onSnapPhotoDone(String picSavePath);

		/* 获得uvc格式的预览图像数据 */
		public void onPreviewUVCData(byte[] uvcData, Camera camera);

		/* 点击该控件，则执行该函数（需要再调用者类中重写该函数） */
		public void onActionDown();

		/* 得到实际的预览尺寸 */
		public void getRealPreivewResolution(int width, int height);
	}

	/**
	 * 停止拍照，并将拍摄的照片传入PictureCallback接口的onPictureTaken方法
	 */
	public void takePicture() {
		if (mCamera != null && safeToTakePicture && isPreviewing) {
			LogUtil.getInstance().i(TAG, "enter takePicture");
			safeToTakePicture = false;
			mCamera.takePicture(null, null, pictureCallback);
		}
	}

	/** 拍照后默认停止视频预览，该函数可以再次启用视频预览 */
	public void startPreview() {
		mCamera.startPreview();
		isPreviewing = true;
	}

	/** 拍照后默认停止视频预览，该函数可以再次启用视频预览 */
	public void stopPreview() {
		mCamera.stopPreview();
		isPreviewing = false;
	}

	public int getCameraIndex() {
		return cameraIndex;
	}

	/** 打开摄像头（打开摄像头操作较为耗时，单独调用可避免程序启动时该操作占用UI线程的时间） */
	public void initCamera() {
		/* 打开摄像头 */
		try {
			mCamera = Camera.open(cameraIndex);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (null == mCamera) {
			return;
		}
		if (isPreviewing) {
			mCamera.stopPreview();
			return;
		}
		// 摄像头个数
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		int orientation = cameraInfo.orientation;
		LogUtil.getInstance().i(TAG, "orientation:" + orientation);
		int cameraNum = Camera.getNumberOfCameras();
		LogUtil.getInstance().i(TAG, "cameraNum :" + cameraNum);
		if (cameraNum == 1 && Camera.CameraInfo.CAMERA_FACING_FRONT == cameraInfo.facing) {
			cameraIndex = 0;
		}
		try {
			// 设置用于显示拍照摄像的SurfaceHolder对象
			mCamera.setPreviewDisplay(g_Holder);
			/*int degrees = 0;
			int rotation = ValueUtil.getInstance().getmActivity().getWindowManager().getDefaultDisplay().getRotation();
			switch (rotation) {
			case Surface.ROTATION_0:
				degrees = 0;
				break;
			case Surface.ROTATION_90:
				degrees = 90;
				break;
			case Surface.ROTATION_180:
				degrees = 180;
				break;
			case Surface.ROTATION_270:
				degrees = 270;
				break;
			default:
				break;
			}
			int result = 0;
			if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
				result = (cameraInfo.orientation + degrees) % 360;
				result = (360 - result) % 360;// compensate the mirror
			} else {
				result = (cameraInfo.orientation - degrees + 360) % 360;
			}
			mCamera.setDisplayOrientation(result);*/
			mCamera.setDisplayOrientation((Integer) SharedPreferencesUtil.get(ValueUtil.getInstance().getmActivity(), ConstantValues.getDisplayOrientation(), 0));
		
		} catch (IOException e) {
			e.printStackTrace();
			// 释放手机摄像头
			mCamera.release();
			mCamera = null;
		}

		if (mCamera == null)
			return;
		try {
//			setCamParam(WIDTH, HEIGHT, mCamera);
			setCameraParameters(WIDTH);
			mCamera.setPreviewCallback(this);
			mCamera.startPreview();
			isPreviewing = true;
			// 开始视频预览
			// setCameraZoom(2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		firstOpenCamera = false;

	}

	/**
	 * Camera参数设置
	 * @Title: setCameraParameters   
	 * @Description: TODO 
	 * @param: @param needWidth      
	 * @return: void      
	 * @throws   
	 * @author: lvwenyan     
	 * @date:   Jul 23, 2015 3:42:28 PM
	 */
	public void setCameraParameters(int needWidth) {
		if (null == mCamera) {
			return;
		} else {
			Camera.Parameters parametersOrignal = mCamera.getParameters();
			List<String> focusModes = parametersOrignal.getSupportedFocusModes();
			if (null != focusModes) {
//				for (int i = 0; i < focusModes.size(); i++) {
//					Log.i(TAG, "540 focusModes " + i + " : " + focusModes.get(i));
//				}
				// 查询摄像头是否支持Camera.Parameters.FOCUS_MODE_AUTO，只要在支持时才调用Camera.autoFocus()
				if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
					parametersOrignal.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
					isAutoSupported = true;
				}
			}
			/* 预览大小 */
			String preSizesVals = parametersOrignal.get("preview-size-values");
			if (StringUtil.isNotNull(preSizesVals)) {
				getSuitableWidth(preSizesVals, WIDTH, HEIGHT);
			}
			parametersOrignal.setPreviewSize(previewWidth, previewHeight);
			/* 照片大小 */
			String picSizesVals = parametersOrignal.get("picture-size-values");
			if (StringUtil.isNotNull(picSizesVals)) {
				getSuitableWidth(picSizesVals, WIDTH, HEIGHT);
			}
			parametersOrignal.setPictureSize(previewWidth, previewHeight);
//			parametersOrignal.setRotation(90);
			// parametersOrignal.setPreviewFrameRate( 25 );
			/* Camera参数设置 */
			mCamera.setParameters(parametersOrignal);
		}
	}

	/**
	 * 寻找合适的照片大小，与 needWidth最接近的分辨率
	 * @Title: getSuitableWidth   
	 * @Description: TODO 
	 * @param: @param strSizesVals
	 * @param: @param needWidth      
	 * @return: void      
	 * @throws   
	 * @author: lvwenyan     
	 * @date:   Jul 23, 2015 3:42:43 PM
	 */
	private void getSuitableWidth(String strSizesVals, int needWidth, int needHeight) {
		int modeWidth = 0;
		int modeHeight = 0;
		int modeDiff = 1000;
		String tokens[] = strSizesVals.split(",");
		List<ResultData> list = new ArrayList<ResultData>();
		for (int i = 0; i < tokens.length; i++) {
			String tokens2[] = tokens[i].split("x");
			int tmpdiff = Math.abs(Integer.parseInt(tokens2[0]) - needWidth);
			if (modeDiff > tmpdiff) {
				modeDiff = tmpdiff;
				modeWidth = Integer.parseInt(tokens2[0]);
				modeHeight = Integer.parseInt(tokens2[1]);
			}
			if (0 == tmpdiff) {
				ResultData ret = new ResultData();
				ret.targetWidth = Integer.parseInt(tokens2[0]);
				ret.targetHeight = Integer.parseInt(tokens2[1]);
				list.add(ret);
			}
		}
		previewWidth = modeWidth;
		previewHeight = modeHeight;
		int size = list.size();
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				int heightTmp = list.get(i).targetHeight;
				if (needHeight == heightTmp) {
					previewHeight = heightTmp;
				}
			}
		}
	}

	public class ResultData {
		int targetWidth;
		int targetHeight;
	}

	/** 释放摄像头资源 */
	public void releaseCam() {
		if (mCamera == null) {
			mCamera.release();
			mCamera = null;
		}
	}

	//////////////////////////////////////////////////////////////////////////////////// A

	/** 创建一个PictureCallback对象，并实现其中的onPictureTaken方法 */
	private PictureCallback pictureCallback = new PictureCallback() {
		// 该方法用于处理拍摄后的照片数据
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			LogUtil.getInstance().i(TAG, "enter onPictureTaken");
			if (data != null) {
				mCamera.stopPreview();
				isPreviewing = false;
			}
			new Thread(new SaveThread(data)).start();
			safeToTakePicture = true;
			// if (null != eventListener) {
			// eventListener.onSnapPhotoDone(picSavePath);
			// }
			mCamera.setPreviewCallback(SysCameraControl.this);
			mCamera.startPreview(); // 拍完照后继续预览
			isPreviewing = true;
			LogUtil.getInstance().i(TAG, "saved file onPictureTaken");
		}
	};

	/***
	 * 为了不影响比对的效率，takepicture后另开一个线程保存，保存的地址自己指定
	 */
	private class SaveThread implements Runnable {
		private byte[] data;

		public SaveThread(byte[] data) {
			this.data = data;
		}

		@Override
		public void run() {
			saveCamDataToJPEG(picSavePath + "liulv1.jpg", data);
		}

	}

	/** 设置监听事件 */
	public void setOnCameraStatusListener(OnCameraStatusListener listener) {
		this.eventListener = listener;
	}

	/** 把摄像头得到的图像保存成jpg格式图片 */
	private void saveCamDataToJPEG(String filename, byte[] jpegData) {
		if (filename == null || jpegData == null)
			return;
		Bitmap source = BitmapFactory.decodeByteArray(jpegData, 0, jpegData.length);
		OutputStream outputStream = null;
		try {
			File file = new File(filename);
			if (file.exists()) {
				file.delete();
			}
			outputStream = new FileOutputStream(file);
			if (source != null) {
				source.compress(CompressFormat.JPEG, 75, outputStream);
			} else {
				outputStream.write(jpegData);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (Throwable t) {
				}
			}
		}
	}

	private void setCamParam(int width, int height, Camera camera_) {
		/* 获取摄像头支持的预览、保存参数 */
		Camera.Parameters paramsBefore = camera_.getParameters();
		LogUtil.getInstance().i(TAG, "camera params" + paramsBefore.flatten());
		// 看是否自动曝光，ture为自动，flase为不自动曝光
//		boolean expLck=paramsBefore.getAutoExposureLock();
		// 获取曝光补偿级别
//		int expIndex=paramsBefore.getExposureCompensation();
//		int maxIndex=paramsBefore.getMaxExposureCompensation();
//		int minIndex=paramsBefore.getMinExposureCompensation();
//		float expStep=paramsBefore.getExposureCompensationStep();
//		Log.d(TAG, "expLck:"+expLck+"--expIndex:"+expIndex+"--expStep:"+expStep+"--maxIndex:"+maxIndex+"--minIndex:"+minIndex);
		// 设置曝光补偿级别
		// paramsBefore.setExposureCompensation(3);
		/* 设置拍照尺寸大小 */
		String strPrevSizesVals = paramsBefore.get("picture-size-values");
		int[] pictureSize = new int[2];
		if (strPrevSizesVals != null) {
			getSuitableWidth(strPrevSizesVals, width, height, pictureSize);
		}
		LogUtil.getInstance().i(TAG, "take pic size: " + pictureSize[0] + " " + pictureSize[1]);
		paramsBefore.setPictureSize(pictureSize[0], pictureSize[1]);
		// paramsBefore.setPictureSize(1280, 720);//请根据自己的所需的分辨率调整
//		paramsBefore.setPictureFormat(ImageFormat.JPEG);//设置拍照后照片的存储模式

		/* 设置预览尺寸大小 */
		strPrevSizesVals = paramsBefore.get("preview-size-values");
		if (strPrevSizesVals != null) {
			getSuitableWidth(strPrevSizesVals, width, height, pictureSize);
		}
		LogUtil.getInstance().i(TAG, "preview size: " + pictureSize[0] + " " + pictureSize[1]);
		if (eventListener != null)
			eventListener.getRealPreivewResolution(pictureSize[0], pictureSize[1]);
		paramsBefore.setPreviewSize(pictureSize[0], pictureSize[1]);
		camera_.setParameters(paramsBefore);
	}

	/** 寻找合适的照片大小，与 needWidth最接近的分辨率，如果没有640*480的分辨，为了更好的适配，请用此方法
	 * strSizesVals 摄像头支持的所有分辨率（字符串形式）
	 * needWidth    希望设置的分辨率宽度
	 * size_out     传去参数、当不支持期望的分辨率时，找到的与之最相近的分辨率
	 */
	private void getSuitableWidth(String strSizesVals, int needWidth, int[] size_out) {
		int modeWidth = 0;
		int modeHeight = 0;
		int modeDiff = 1000;
		String tokens[] = strSizesVals.split(",");
		for (int i = 0; i < tokens.length; i++) {
			String tokens2[] = tokens[i].split("x");
			int tmpdiff = Math.abs(Integer.parseInt(tokens2[0]) - needWidth);
			if (modeDiff > tmpdiff) {
				modeDiff = tmpdiff;
				modeWidth = Integer.parseInt(tokens2[0]);
				modeHeight = Integer.parseInt(tokens2[1]);
			}
		}
		size_out[0] = modeWidth;
		size_out[1] = modeHeight;
	}

	/**
	 * 寻找合适的照片大小，与 needWidth最接近的分辨率 strSizesVals 摄像头支持的所有分辨率（字符串形式） needWidth
	 * 希望设置的分辨率宽度 size_out 传去参数、当不支持期望的分辨率时，找到的与之最相近的分辨率
	 * 匹配的方式更严格，但是更难接近所预期的分辨率
	 */
	private void getSuitableWidth(String strSizesVals, int needWidth, int needHeight, int[] size_out) {
		int modeWidth = 0;
		int modeHeight = 0;
		int modeDiff = 1000;
		String tokens[] = strSizesVals.split(",");
		for (int i = 0; i < tokens.length; i++) {
			String tokens2[] = tokens[i].split("x");
			// int tmpdiff = Math.abs(Integer.parseInt(tokens2[0]) - needWidth);
			int tmpdiff = Math.abs(Integer.parseInt(tokens2[0]) - needWidth) * 10 + Math.abs(Integer.parseInt(tokens2[1]) - needHeight);
			if (modeDiff > tmpdiff) {
				modeDiff = tmpdiff;
				modeWidth = Integer.parseInt(tokens2[0]);
				modeHeight = Integer.parseInt(tokens2[1]);
			}
		}
		size_out[0] = modeWidth;
		size_out[1] = modeHeight;
	}

	@Override
	public void surfaceCreated(final SurfaceHolder holder) {
		LogUtil.getInstance().i(TAG, "enter SurfaceCreated--");
		initCamera();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		LogUtil.getInstance().i(TAG, "enter surfaceDestroyed--");
		if (mCamera == null)
			return;
		mCamera.stopPreview();
		mCamera.setPreviewCallback(null);
		mCamera.release();
		mCamera = null;
	}

	@Override
	public void surfaceChanged(final SurfaceHolder holder, int format, int w, int h) {
		LogUtil.getInstance().i(TAG, "enter surfaceChanged--");
		if (firstOpenCamera) {
			return;
		}
//		try {
//			setCamParam(WIDTH, HEIGHT, mCamera);
//			mCamera.setPreviewCallback(this);
//			mCamera.startPreview();
//			isPreviewing = true;
//			// 开始视频预览
//			// setCameraZoom(2);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		// TODO Auto-generated method stub
		if (eventListener != null) {
			eventListener.onPreviewUVCData(data, camera);
		}
	}

	@Override
	public void onAutoFocus(boolean success, Camera camera) {
		// TODO Auto-generated method stub

	}

}