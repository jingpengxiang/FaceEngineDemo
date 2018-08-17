package com.lu.face.faceenginedemo.engine.utils;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.hisign.face.match.engine.verify.HSFaceEngine;
import com.lu.face.faceenginedemo.R;
import com.lu.face.faceenginedemo.engine.data.ConstantValues;
import com.lu.face.faceenginedemo.engine.db.LibDao;
import com.lu.face.faceenginedemo.engine.models.PersonFeature;
import com.lu.face.faceenginedemo.enginesdk.models.FeatureCommon;
import com.lu.face.faceenginedemo.enginesdk.models.PersonCommon;
import com.lu.face.faceenginedemo.enginesdk.utils.FeatureUtilSdk;
import com.lu.face.faceenginedemo.enginesdk.utils.StringUtil;
import com.lu.face.faceenginedemo.enginesdk.utils.ValueUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FeatureUtil {

	private static final String TAG = "FeatureUtil";
	private Handler mHandler;
	private Context mContext;
	private int successNum = -1, totalNum;
	private LibDao mLibDao;
	private FeatureUtilSdk mFeatureUtilSdk;

	private FeatureUtilSdk.FeatureCallback mFeatureCallback = new FeatureUtilSdk.FeatureCallback() {
		@Override
		public void onInsert(PersonCommon pc, FeatureCommon fc) {
			// TODO Auto-generated method stub
			PersonFeature person = new PersonFeature();
			person.setPersonCommon(pc);
			person.setFeatureCommon(fc);
			mLibDao.insert(person);
		}

		@Override
		public void onDelete(PersonCommon pc, FeatureCommon fc) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onRescan() {
			// TODO Auto-generated method stub
			rescanFeature();
		}

		@Override
		public void onFail(String name) {
			// TODO Auto-generated method stub
			mHandler.obtainMessage(ConstantValues.ID_FEATURE_FAIL, name).sendToTarget();
		}

		@Override
		public void onUpdate(PersonCommon pc, FeatureCommon fc) {
			// TODO Auto-generated method stub
			PersonFeature person = new PersonFeature();
			person.setPersonCommon(pc);
			person.setFeatureCommon(fc);
			mLibDao.update(person);
		}

		@Override
		public void onSetNum(int insertNum, int failNum) {
			// TODO Auto-generated method stub
			FeatureUtil.this.successNum = insertNum;
			mHandler.obtainMessage(ConstantValues.ID_SET_DATA, insertNum, failNum).sendToTarget();
		}

		@Override
		public void onDelete(int personId) {
			// TODO Auto-generated method stub
			mLibDao.delete(personId);
		}
	};

	public FeatureUtil(Context context, HSFaceEngine engine, Handler mHandler) {
		this.mHandler = mHandler;
		this.mContext = context;
		mLibDao = new LibDao(context);
		mFeatureUtilSdk = new FeatureUtilSdk(context, engine, mFeatureCallback);
	}

	public void extractFeatureByPath(String path, final int personId) {
		mHandler.obtainMessage(ConstantValues.SCAN_START).sendToTarget();
		boolean isToastShowed = false;
		totalNum = 0;
		successNum = 0;
		if (StringUtil.isNotNull(path)) {
			File file = new File(path);
			if (null != file && file.exists()) {
				File[] imgs = null;
				if (file.isDirectory()) {
					if (0 > personId) {
						imgs = file.listFiles(new MyFilter(true));
					} else {
						imgs = file.listFiles(new MyFilter(false));
					}
				} else if (file.isFile()) {
					String name = file.getName();
					if (name.endsWith(".jpg") || name.endsWith(".bmp") || name.endsWith(".JPG") || name.endsWith(".BMP")) {
						imgs = new File[] { file };
					}
				}
				if (null != imgs && 0 < imgs.length) {
					int length = imgs.length;
					if (0 <= personId && ValueUtil.getInstance().getMAX_NUM() < imgs.length) {
						length = ValueUtil.getInstance().getMAX_NUM();
						if (!isToastShowed) {
							isToastShowed = true;
							mHandler.obtainMessage(ConstantValues.ID_HINT, mContext.getResources().getString(R.string.face_limit_80)).sendToTarget();
						}
					}
					totalNum = length;
					final List<String> list = new ArrayList<String>();
					for (int i = 0; i < length; i++) {
						if (imgs[i].isDirectory()) {
							if (StringUtil.isEqual(imgs[i].getAbsolutePath(), ValueUtil.getInstance().getFACE_PATH())) {
								totalNum--;
								File[] imgsTmp = imgs[i].listFiles(new MyFilter(false));
								if (null != imgsTmp && 0 < imgsTmp.length) {
									for (int j = 0; j < imgsTmp.length; j++) {
										totalNum++;
										String name = imgsTmp[j].getName();
										if (name.endsWith(".jpg") || name.endsWith(".bmp") || name.endsWith(".JPG") || name.endsWith(".BMP")) {
											list.add(imgsTmp[j].getAbsolutePath());
										} else {
											totalNum--;
										}
									}
								}
							} else {
								File[] imgsTmp = imgs[i].listFiles(new MyFilter(false));
								if (null != imgsTmp && 0 < imgsTmp.length) {
									list.add(imgs[i].getAbsolutePath());
									int lengthTmp = imgsTmp.length;
									if (ValueUtil.getInstance().getMAX_NUM() < imgsTmp.length) {
										lengthTmp = ValueUtil.getInstance().getMAX_NUM();
										if (!isToastShowed) {
											isToastShowed = true;
											mHandler.obtainMessage(ConstantValues.ID_HINT, mContext.getResources().getString(R.string.face_limit_80)).sendToTarget();
										}
									}
									totalNum += (lengthTmp - 1);
								} else {
									totalNum--;
								}
							}
						} else if (imgs[i].isFile()) {
							String name = imgs[i].getName();
							if (name.endsWith(".jpg") || name.endsWith(".bmp") || name.endsWith(".JPG") || name.endsWith(".BMP")) {
								list.add(imgs[i].getAbsolutePath());
							} else {
								totalNum--;
							}
						}
					}
					if (0 < totalNum) {
						new Thread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
//							doExtractFeature(list);
								mFeatureUtilSdk.doExtractFeature(list, personId);
							}
						}).start();
					} else {
						mHandler.obtainMessage(ConstantValues.ID_HINT, mContext.getResources().getString(R.string.face_no_files)).sendToTarget();
					}
				} else {
					if (file.isDirectory()) {
						mHandler.obtainMessage(ConstantValues.ID_HINT, mContext.getResources().getString(R.string.face_no_files)).sendToTarget();
					} else if (file.isFile()) {
						mHandler.obtainMessage(ConstantValues.ID_HINT, mContext.getResources().getString(R.string.formate_invalide)).sendToTarget();
					}

//					numTotal++;
//					numFail++;
//					mHandler.obtainMessage(ConstantValues.ID_PROGRESS, numTotal, numTmp).sendToTarget();
//					mHandler.obtainMessage(ConstantValues.ID_FEATURE_FAIL, numTotal, numFail, file.getName()).sendToTarget();
				}
			}
		}
		mHandler.obtainMessage(ConstantValues.ID_SET_TOTAL, totalNum, 0).sendToTarget();
	}

	public void scanFeature() {
		mHandler.obtainMessage(ConstantValues.SCAN_START).sendToTarget();
		mFeatureUtilSdk.scanFeature();
	}

	private void rescanFeature() {
		Map<Integer, PersonFeature> map = mLibDao.queryAll();
		LogUtil.getInstance().i(TAG, "194 query rescanFeature start");
		ValueUtil.getInstance().getPersonList().clear();
		ValueUtil.getInstance().getPersonList().addAll(new ArrayList(map.values()));
//		for (PersonFeature pf : map.values()) {
//		ValueUtil.getInstance().getPersonList().add(pf);
//	}
		LogUtil.getInstance().i(TAG, "194 query rescanFeature end");
		Bundle bundle = new Bundle();
//		bundle.putParcelableArrayList("list", listDir);
		if (-1 == successNum) {
			bundle.putInt("num", ValueUtil.getInstance().getPersonList().size());
		} else {
			bundle.putInt("num", successNum);
		}
		Message message = new Message();
		message.what = ConstantValues.ID_FEATURE_SUCCESS;
		message.setData(bundle);
		if (!mHandler.sendMessage(message)) {
			mHandler.sendMessage(message);
		}
	}

	public void deleteFeatureById(int personId) {
		mLibDao.delete(personId);
		mFeatureUtilSdk.deleteFeatureById(personId);
	}

	public void deleteAll() {
		mLibDao.delete(-1);
		mFeatureUtilSdk.deleteAll();
	}

	public void deleteMapById(int personId) {
		mFeatureUtilSdk.deleteMapById(personId);
	}

	public int getMaxIndexByNameAndCard(String name, String cardId) {
		return mFeatureUtilSdk.getMaxIndexByNameAndCard(name, cardId);
	}

	public int getPersonIdByNameAndCard(String name, String cardId) {
		int ret = -1;
		PersonCommon pc = mFeatureUtilSdk.queryPersonCommonByNameAndCard(name, cardId);
		if (null != pc) {
			ret = pc.getPersonId();
		}
		return ret;
	}
}
