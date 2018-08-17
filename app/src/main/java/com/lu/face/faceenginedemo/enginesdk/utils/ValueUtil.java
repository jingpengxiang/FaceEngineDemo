package com.lu.face.faceenginedemo.enginesdk.utils;

import android.app.Activity;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;

import com.hisign.face.match.engine.verify.HSFaceEngine;
import com.lu.face.faceenginedemo.engine.models.FingerPrint;
import com.lu.face.faceenginedemo.engine.models.PersonFeature;
import com.lu.face.faceenginedemo.engine.utils.LogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 公共数值类
 * @ClassName:	ValueUtils 
 * @Description:TODO 
 * @author:	lvwenyan
 * @date: Mar 7, 2016 4:07:43 PM
 */
public class ValueUtil {
	private static volatile ValueUtil mValueUtil;
	private final int MAX_NUM = 80;
	private final String FACE_PATH = Environment.getExternalStorageDirectory().getPath() + File.separator + "HisignEngine" + File.separator + "Photo";
	private final String FP_PATH = Environment.getExternalStorageDirectory().getPath() + File.separator + "HisignEngine" + File.separator + "FingerPrint";
	private Activity mActivity;
	private Handler mHandler;
	private DisplayMetrics mDisplayMetrics;
	private int textSize;
	private HSFaceEngine mHSFaceEngine;
	private List<PersonFeature> personList = new ArrayList<PersonFeature>();
	private int num;
	private String featurePostfix;
	private int width, height;

	public int getTextSize() {
		return textSize;
	}

	public void setTextSize(int textSize) {
		this.textSize = textSize;
	}

	private String[] cameras = new String[2];
	private String[] verifys = new String[3];
	private TreeMap<Integer, FingerPrint> fpMap = new TreeMap<Integer, FingerPrint>(new Comparator<Integer>() {
		public int compare(Integer obj1, Integer obj2) {
			return obj2.compareTo(obj1);// 降序排序
		}
	});
	private Map<String, Float> fpScoreMap = new HashMap<String, Float>();
	private boolean isFpFeaturesInited;
	private boolean isFaceFeaturesInited;

	public static ValueUtil getInstance() {
		if (null == mValueUtil) {
			synchronized (ValueUtil.class) {
				if (null == mValueUtil) {
					mValueUtil = new ValueUtil();
				}
			}
		}
		return mValueUtil;
	}

	public Activity getmActivity() {
		return mActivity;
	}

	public void setmActivity(Activity mActivity) {
		this.mActivity = mActivity;
	}

	public Handler getmHandler() {
		return mHandler;
	}

	public void setmHandler(Handler mHandler) {
		this.mHandler = mHandler;
	}

	public DisplayMetrics getmDisplayMetrics() {
		return mDisplayMetrics;
	}

	public void setmDisplayMetrics(DisplayMetrics mDisplayMetrics) {
		this.mDisplayMetrics = mDisplayMetrics;
	}

	public String[] getCameras() {
		return cameras;
	}

	public void setCameras(String[] cameras) {
		this.cameras = cameras;
	}

	public String[] getVerifys() {
		return verifys;
	}

	public void setVerifys(String[] verifys) {
		this.verifys = verifys;
	}

	public HSFaceEngine getmHSFaceEngine() {
		if (null == mHSFaceEngine) {
			LogUtil.getInstance().i("ValueUtil", "104  new HSFaceEngine()  !!!");
			mHSFaceEngine = new HSFaceEngine();
		}
		return mHSFaceEngine;
	}

	public void setmHSFaceEngine(HSFaceEngine mHSFaceEngine) {
		this.mHSFaceEngine = mHSFaceEngine;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public String getFeaturePostfix() {
		return featurePostfix;
	}

	public void setFeaturePostfix(String featurePostfix) {
		this.featurePostfix = featurePostfix;
	}

	public List<PersonFeature> getPersonList() {
		return personList;
	}

	public void setPersonList(List<PersonFeature> personList) {
		this.personList = personList;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getMAX_NUM() {
		return MAX_NUM;
	}

	public String getFP_PATH() {
		return FP_PATH;
	}

	public String getFACE_PATH() {
		return FACE_PATH;
	}

	public TreeMap<Integer, FingerPrint> getFpMap() {
		return fpMap;
	}

	public void setFpMap(TreeMap<Integer, FingerPrint> fpMap) {
		this.fpMap = fpMap;
	}

	public boolean isFpFeaturesInited() {
		return isFpFeaturesInited;
	}

	public void setFpFeaturesInited(boolean isFpFeaturesInited) {
		this.isFpFeaturesInited = isFpFeaturesInited;
	}

	public Map<String, Float> getFpScoreMap() {
		return fpScoreMap;
	}

	public void setFpScoreMap(Map<String, Float> fpScoreMap) {
		this.fpScoreMap = fpScoreMap;
	}

	public boolean isFaceFeaturesInited() {
		return isFaceFeaturesInited;
	}

	public void setFaceFeaturesInited(boolean isFaceFeaturesInited) {
		this.isFaceFeaturesInited = isFaceFeaturesInited;
	}
}
