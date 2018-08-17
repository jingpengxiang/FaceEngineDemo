package com.lu.face.faceenginedemo.engine.utils;

import android.content.Context;
import android.os.Handler;

import com.hisign.AS60xSDK.AS60xIO;
import com.hisign.AS60xSDK.SDKUtilty;
import com.lu.face.faceenginedemo.R;
import com.lu.face.faceenginedemo.engine.data.ConstantValues;
import com.lu.face.faceenginedemo.engine.models.FingerPrint;
import com.lu.face.faceenginedemo.enginesdk.utils.FileUtil;
import com.lu.face.faceenginedemo.enginesdk.utils.LogUtilSdk;
import com.lu.face.faceenginedemo.enginesdk.utils.StringUtil;
import com.lu.face.faceenginedemo.enginesdk.utils.ValueUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FpFeatureUtil {

	private static final String TAG = "FpFeatureUtil";
//	-------------------------begin------------------------------------------------------
	private Context mContext;
	private Handler mHandler;
	private boolean isAdding;
	private int successNum, totalNum, failNum;
	private int fingerQuality;

	public FpFeatureUtil(Context mContext, Handler mHandler) {
		this.mContext = mContext;
		this.mHandler = mHandler;
		fingerQuality = (Integer) SharedPreferencesUtil.get(mContext, ConstantValues.getFingerQuality(), 0);
	}

	public void extractFeatureByPath(String path) {
		totalNum = 0;
		failNum = 0;
		successNum = 0;
		if (StringUtil.isNotNull(path)) {
			File file = new File(path);
			if (null != file && file.exists()) {
				File[] imgs = null;
				if (file.isDirectory()) {
					imgs = file.listFiles(new MyFilterBmp(false));
				} else if (file.isFile()) {
					String name = file.getName();
					if (name.endsWith(".bmp") || name.endsWith(".BMP")) {
						imgs = new File[] { file };
					}
				}
				if (null != imgs && 0 < imgs.length) {
					totalNum = imgs.length;
					final List<FingerPrint> list = new ArrayList<FingerPrint>();
					for (int i = 0; i < imgs.length; i++) {
						FingerPrint fp = new FingerPrint();
						fp.setName(imgs[i].getName());
						fp.setPath(imgs[i].getAbsolutePath());
						list.add(fp);
					}
					new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							doExtractFeature(list);
						}
					}).start();
				} else {
					if (file.isDirectory()) {
						mHandler.obtainMessage(ConstantValues.ID_HINT, mContext.getResources().getString(R.string.fp_no_files)).sendToTarget();
					} else if (file.isFile()) {
						mHandler.obtainMessage(ConstantValues.ID_HINT, mContext.getResources().getString(R.string.formate_invalide)).sendToTarget();
					}
				}
			}
		}
		mHandler.obtainMessage(ConstantValues.ID_SET_TOTAL, totalNum, 0).sendToTarget();
	}

	private void doExtractFeature(List<FingerPrint> list) {
		// TODO Auto-generated method stub
		setAdding(true);
		for (int i = 0; i < list.size(); i++) {
			final FingerPrint fp = list.get(i);
			if (null != fp) {
				boolean isSuccess = false;
				String successPath = "";
				byte[] raws = new byte[256 * 360];
//				String dir = fp.getPath().substring(0, fp.getPath().lastIndexOf(File.separator));
//				String featurePath = dir + File.separator + fp.getName() + ValueUtil.getInstance().getFeaturePostfix();
				String featurePath = fp.getPath() + ValueUtil.getInstance().getFeaturePostfix();
				File file = new File(featurePath);
				if (null != file && file.exists()) {
					String desPath = ValueUtil.getInstance().getFP_PATH() + File.separator + fp.getName();
					String desPathFeature = desPath + ValueUtil.getInstance().getFeaturePostfix();
					File featFile = new File(desPathFeature);
					if (null == featFile || !featFile.exists()) {
						isSuccess = true;
						successPath = desPath;
						FileUtil.getInstance().copyFile(fp.getPath(), desPath);
						FileUtil.getInstance().copyFile(featurePath, desPathFeature);
					}
				} else {
					SDKUtilty.ReadBmpToRaw(raws, fp.getPath(), true);
					if (null != raws && 0 < raws.length) {
						int retScore = AS60xIO.FCV_GetQualityScore(raws);
						if (retScore >= fingerQuality) {
							byte[] feature = new byte[512];
							long startTime = System.currentTimeMillis();
							int nRet = AS60xIO.FCV_ExtractFeature(raws, feature);
							LogUtilSdk.getInstance().i(TAG, "timelog 111 fp_feature_time = " + (System.currentTimeMillis() - startTime));
							if (0 == nRet) {
								FileUtil.getInstance().saveImageByByteAndPath(feature, featurePath);
								String desPath = ValueUtil.getInstance().getFP_PATH() + File.separator + fp.getName();
								String desPathFeature = desPath + ValueUtil.getInstance().getFeaturePostfix();
								File featFile = new File(desPathFeature);
								if (StringUtil.isEqual(featurePath, desPathFeature)) {
									isSuccess = true;
									successPath = desPath;
								} else {
									if (null == featFile || !featFile.exists()) {
										isSuccess = true;
										successPath = desPath;
										FileUtil.getInstance().copyFile(fp.getPath(), desPath);
										FileUtil.getInstance().copyFile(featurePath, desPathFeature);
									}
								}
							}
						}
					}
				}
				if (isSuccess) {
					successNum++;
				} else {
					failNum++;
					mHandler.obtainMessage(ConstantValues.ID_FEATURE_FAIL, fp.getName()).sendToTarget();
				}
				mHandler.obtainMessage(ConstantValues.ID_SET_DATA, successNum, failNum).sendToTarget();
				if (isSuccess) {
					rescanFeature(successPath);
				}
			}
		}
		mHandler.sendEmptyMessageDelayed(ConstantValues.ID_FP_FEATURE_SUCCESS, 1000);
		list.clear();
		setAdding(false);
	}

	public void rescanFeature(String path) {
		int successNum = 0;
		File parentFile = new File(ValueUtil.getInstance().getFP_PATH());
		File[] imgs = null;
		if (StringUtil.isNotNull(path)) {
			imgs = new File[] { new File(path) };
		} else {
			imgs = parentFile.listFiles(new MyFilterBmp(false));
		}
		if (null != imgs && 0 < imgs.length) {
			for (int i = 0; i < imgs.length; i++) {
				String featurePath = imgs[i].getAbsolutePath() + ValueUtil.getInstance().getFeaturePostfix();
				File featureFile = new File(featurePath);
				if (null != featureFile && featureFile.exists()) {
					byte[] feature = FileUtil.getInstance().imageToByte(featurePath);
					if (null != feature && 0 < feature.length) {
						FingerPrint fp = new FingerPrint();
						int id = 0;
						if (0 < ValueUtil.getInstance().getFpMap().size()) {
							id = ValueUtil.getInstance().getFpMap().firstKey();
							id++;
						}
						fp.setId(id);
						String name = imgs[i].getName();
						name = name.substring(0, name.indexOf("."));
						fp.setName(name);
						fp.setPath(imgs[i].getAbsolutePath());
						fp.setChecked(false);
						fp.setFeature(feature);
						ValueUtil.getInstance().getFpMap().put(fp.getId(), fp);
						successNum++;
					} else {
						FileUtil.getInstance().delFile(featurePath);
						FileUtil.getInstance().delFile(imgs[i].getAbsolutePath());
					}
				}
			}
		}
		if (!StringUtil.isNotNull(path)) {
			mHandler.obtainMessage(ConstantValues.ID_FP_FEATURE_SUCCESS, successNum, 0).sendToTarget();
		}
		/*Bundle bundle = new Bundle();
		bundle.putParcelableArrayList("list", listDir);
		bundle.putInt("num", successNum);
		Message message = new Message();
		message.what = ConstantValues.ID_FP_FEATURE_SUCCESS;
		message.setData(bundle);
		mHandler.sendMessage(message);*/
	}

	public boolean isAdding() {
		return isAdding;
	}

	public void setAdding(boolean isAdding) {
		this.isAdding = isAdding;
	}

	/*private boolean isPathExist(String path) {
		boolean ret = false;
		if (0 < ValueUtil.getInstance().getFpPathMap().size()) {
		ret = ValueUtil.getInstance().getFpPathMap().contains(path);
			for (String pathTmp : ValueUtil.getInstance().getFpPathMap().values()) {
				if (StringUtil.isEqual(path, pathTmp)) {
					ret = true;
					break;
				}
			}
		}
		return ret;
	}*/
}
