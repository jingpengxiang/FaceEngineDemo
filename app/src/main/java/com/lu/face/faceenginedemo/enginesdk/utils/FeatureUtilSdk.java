package com.lu.face.faceenginedemo.enginesdk.utils;

import android.content.Context;

import com.hisign.face.match.engine.model.HSFEImage;
import com.hisign.face.match.engine.model.HSFEImageFormat;
import com.hisign.face.match.engine.verify.HSFaceEngine;
import com.lu.face.faceenginedemo.engine.utils.CardUtil;
import com.lu.face.faceenginedemo.engine.utils.MyFilter;
import com.lu.face.faceenginedemo.enginesdk.db.LibDaoSdk;
import com.lu.face.faceenginedemo.enginesdk.models.FeatureCommon;
import com.lu.face.faceenginedemo.enginesdk.models.FeatureSdk;
import com.lu.face.faceenginedemo.enginesdk.models.PersonCommon;
import com.lu.face.faceenginedemo.enginesdk.models.PersonSdk;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeatureUtilSdk {
	private final String TAG = "FeatureUtilSdk";
	private boolean isAdding;
	private LibDaoSdk mLibDao;
	private HSFaceEngine mHSFaceEngine;
	private FeatureCallback mFeatureCallback;
	private int counter;
	private int successNum, failNum;
	private int maxFeatureId = -1, maxFeaturePersonId = -1;
	private Map<Integer, Integer> map = new HashMap<Integer, Integer>();

	public FeatureUtilSdk(Context context, HSFaceEngine engine, FeatureCallback mFeatureCallback) {
		mHSFaceEngine = engine;
		mLibDao = new LibDaoSdk(context, mHSFaceEngine, mFeatureCallback);
		this.mFeatureCallback = mFeatureCallback;
		LogUtilSdk.getInstance().setDebug(true);
	}

	private boolean isNameValid(String name) {
		// TODO Auto-generated method stub
		boolean ret = false;

		return ret;
	}

	public void doExtractFeature(List<String> list, int personId) {
		// TODO Auto-generated method stub
		setAdding(true);
		failNum = 0;
		successNum = 0;
		for (int i = 0; i < list.size(); i++) {
			String path = list.get(i);
			if (StringUtil.isNotNull(path)) {
				String fileName = FileUtil.getInstance().getNameByPath(path);
				File file = new File(path);
				if (file.isFile()) {
					extractFeatureByFile(path, personId);
				} else if (file.isDirectory()) {
					extractFeatureByDirectory(path, personId);
				}
			}
		}
		list.clear();
		rescanFeature();
		setAdding(false);
	}

	private void extractFeatureByFile(String path, int personId) {
		String fileName = FileUtil.getInstance().getNameByPath(path);
		String fileNameTmp = fileName.substring(0, fileName.indexOf("."));
		String name = fileNameTmp;
		String cardId = null;
		counter = 0;
		if (2 == countStr(fileNameTmp, "_")) {
			String nameTmp = getStr(fileNameTmp, 2);
			cardId = nameTmp.substring(0, fileName.indexOf("_"));
			if (CardUtil.isIdValid(cardId)) {
				name = nameTmp.substring(fileName.indexOf("_") + 1);
			} else {
				cardId = null;
			}
		}
		counter = 0;
		if (1 == countStr(fileNameTmp, "_")) {
			cardId = fileNameTmp.substring(0, fileName.indexOf("_"));
			if (CardUtil.isIdValid(cardId)) {
				name = fileNameTmp.substring(fileName.indexOf("_") + 1);
			} else {
				cardId = null;
			}
		}
		long startTime = System.currentTimeMillis();
		byte[] features = getFeaturesByPath(path);
		LogUtilSdk.getInstance().i(TAG, "timelog 95 feature_time = " + (System.currentTimeMillis() - startTime));
		startTime = System.currentTimeMillis();
		if (null != features && 0 < features.length) {
			FeatureCommon featureCommon = mLibDao.queryFeatureCommonByPath(path);
			if (null == featureCommon) {
				PersonSdk person = new PersonSdk();
				PersonCommon pc = null;
				if (0 > personId) {
					pc = mLibDao.queryPersonCommonByNameAndCard(name, cardId);
				} else {
					pc = mLibDao.queryPersonCommonByPersonId(personId);
				}
				if (null == pc) {
					pc = new PersonCommon();
					pc.setPersonId(1 + mLibDao.queryMaxPersonId());
					pc.setName(name);
					pc.setCardId(cardId);
					person.setPersonCommon(pc);
					FeatureSdk fs = new FeatureSdk();
					FeatureCommon fc = new FeatureCommon();
					List<FeatureSdk> featureList = new ArrayList<FeatureSdk>();
					fc.setPersonId(pc.getPersonId());
					fc.setFeatureId(0);
					fc.setPath(path);
					fs.setFeatureCommon(fc);
					fs.setFeatures(features);
					featureList.add(fs);
					person.setFeatureList(featureList);
					mLibDao.insert(person);
					mFeatureCallback.onInsert(pc, fc);
				} else {
					person.setPersonCommon(pc);
					FeatureSdk fs = new FeatureSdk();
					FeatureCommon fc = new FeatureCommon();
					List<FeatureSdk> featureList = new ArrayList<FeatureSdk>();
					fc.setPersonId(pc.getPersonId());
					fc.setFeatureId(getMaxIndexByPersonId(pc.getPersonId()));
					fc.setPath(path);
					fs.setFeatureCommon(fc);
					fs.setFeatures(features);
					featureList.add(fs);
					person.setFeatureList(featureList);
					FeatureCommon featureCommonTmp = mLibDao.queryFeatureCommonById(pc.getPersonId(), fc.getFeatureId());
					if (null == featureCommonTmp) {
						mLibDao.insert(person);
						mFeatureCallback.onInsert(pc, fc);
					} else {
						mLibDao.update(fs);
						mFeatureCallback.onUpdate(pc, fs.getFeatureCommon());
					}
				}
			} else {
				FeatureSdk fs = new FeatureSdk();
				fs.setFeatureCommon(featureCommon);
				fs.setFeatures(features);
				mLibDao.update(fs);
				mFeatureCallback.onUpdate(mLibDao.queryPersonCommonByPersonId(featureCommon.getPersonId()), featureCommon);
			}
			successNum++;
			if (-1 != maxFeaturePersonId && -1 != maxFeatureId) {
				map.put(maxFeaturePersonId, maxFeatureId);
			}
		} else {
			LogUtilSdk.getInstance().i(TAG, "163 ex features failed!" + path);
			mFeatureCallback.onFail(fileName);
			failNum++;
		}
		LogUtilSdk.getInstance().i(TAG, "timelog 161 db_time = " + (System.currentTimeMillis() - startTime));
		mFeatureCallback.onSetNum(successNum, failNum);
	}

	private void extractFeatureByDirectory(String path, int personId) {
		boolean isNewPerson = true;
		int tmpPersonId = -1;
		if (0 <= personId) {
			isNewPerson = false;
			tmpPersonId = personId;
		} else {
			String dirName = path.substring(1 + path.lastIndexOf("/"));
			String name = null;
			String cardId = null;
			counter = 0;
			if (1 == countStr(dirName, "_")) {
				cardId = dirName.substring(0, dirName.indexOf("_"));
				if (CardUtil.isIdValid(cardId)) {
					name = dirName.substring(dirName.indexOf("_") + 1);
				} else {
					cardId = null;
				}
			}
			List<FeatureCommon> list = null;
			if (StringUtil.isNotNull(cardId)) {
				list = mLibDao.queryByParentPath(cardId + "_" + name);
			} else {
				list = mLibDao.queryByParentPath(path);
			}
			for (int i = 0; i < list.size(); i++) {
				FeatureCommon featureCommon = list.get(i);
				if (null != featureCommon) {
					PersonCommon pc = mLibDao.queryPersonCommonByPersonId(featureCommon.getPersonId());
					File queryFile = new File(featureCommon.getPath());
					if (StringUtil.isNotNull(pc.getCardId())) {
						if (StringUtil.isEqual(pc.getCardId() + "_" + pc.getName(), queryFile.getParentFile().getName())) {
							isNewPerson = false;
							tmpPersonId = featureCommon.getPersonId();
							break;
						}
					} else {
						if (StringUtil.isEqual(pc.getName(), queryFile.getParentFile().getName())) {
							isNewPerson = false;
							tmpPersonId = featureCommon.getPersonId();
							break;
						}
					}
				}
			}
		}
		PersonSdk person = new PersonSdk();
		PersonCommon pc = null;
		String fileName = FileUtil.getInstance().getNameByPath(path);
		if (isNewPerson) {
			boolean isInsertable = false;
			String cardId = null;
			String name = fileName;
			counter = 0;
			if (1 == countStr(fileName, "_")) {
				String nameTmp = getStr(fileName, 2);
				cardId = nameTmp.substring(0, fileName.indexOf("_"));
				if (CardUtil.isIdValid(cardId)) {
					name = nameTmp.substring(fileName.indexOf("_") + 1);
				} else {
					cardId = null;
				}
			}
			pc = new PersonCommon();
			pc.setPersonId(1 + mLibDao.queryMaxPersonId());
			pc.setName(name);
			pc.setCardId(cardId);
			person.setPersonCommon(pc);
			List<FeatureSdk> featureList = new ArrayList<FeatureSdk>();
			File fileTmp = new File(path);
			File[] imgs = fileTmp.listFiles(new MyFilter(false));
			if (null != imgs && 0 < imgs.length) {
				int length = imgs.length;
				if (length > ValueUtil.getInstance().getMAX_NUM()) {
					length = ValueUtil.getInstance().getMAX_NUM();
				}
				for (int j = 0; j < length; j++) {
					long startTime = System.currentTimeMillis();
					String pathTmp = imgs[j].getAbsolutePath();
					byte[] features = getFeaturesByPath(pathTmp);
					LogUtilSdk.getInstance().i(TAG, "timelog 244 feature_time = " + (System.currentTimeMillis() - startTime));
					startTime = System.currentTimeMillis();
					if (null != features && 0 < features.length) {
						FeatureCommon featureCommonTmp = mLibDao.queryFeatureCommonByPath(pathTmp);
						if (null == featureCommonTmp) {
							isInsertable = true;
							FeatureSdk fs = new FeatureSdk();
							FeatureCommon fc = new FeatureCommon();
							fc.setPersonId(pc.getPersonId());
							fc.setFeatureId(j);
							fc.setPath(pathTmp);
							fs.setFeatureCommon(fc);
							fs.setFeatures(features);
							featureList.add(fs);
							mFeatureCallback.onInsert(pc, fc);
						}
						successNum++;
					} else {
						mFeatureCallback.onFail(FileUtil.getInstance().getNameByPath(pathTmp));
						failNum++;
					}
					LogUtilSdk.getInstance().i(TAG, "timelog 261 db_time = " + (System.currentTimeMillis() - startTime));
					mFeatureCallback.onSetNum(successNum, failNum);
				}
			}
			if (isInsertable) {
				person.setFeatureList(featureList);
				mLibDao.insert(person);
			}
		} else {
			pc = mLibDao.queryPersonCommonByPersonId(tmpPersonId);
			person.setPersonCommon(pc);
			File fileTmp = new File(path);
			File[] imgs = fileTmp.listFiles(new MyFilter(false));
			if (null != imgs && 0 < imgs.length) {
				int length = imgs.length;
				if (length > ValueUtil.getInstance().getMAX_NUM()) {
					length = ValueUtil.getInstance().getMAX_NUM();
				}
				for (int j = 0; j < length; j++) {
					long startTime = System.currentTimeMillis();
					String pathTmp = imgs[j].getAbsolutePath();
					byte[] features = getFeaturesByPath(pathTmp);
					LogUtilSdk.getInstance().i(TAG, "timelog 244 feature_time = " + (System.currentTimeMillis() - startTime));
					startTime = System.currentTimeMillis();
					if (null != features && 0 < features.length) {
						maxFeaturePersonId = -1;
						maxFeatureId = -1;
						FeatureCommon featureCommonTmp = mLibDao.queryFeatureCommonByPath(pathTmp);
						if (null == featureCommonTmp) {
							FeatureSdk fs = new FeatureSdk();
							FeatureCommon fc = new FeatureCommon();
							fc.setPersonId(pc.getPersonId());
							fc.setFeatureId(getMaxIndexByPersonId(pc.getPersonId()));
							fc.setPath(pathTmp);
							fs.setFeatureCommon(fc);
							fs.setFeatures(features);
							FeatureCommon featureCommonTmpTmp = mLibDao.queryFeatureCommonById(pc.getPersonId(), fc.getFeatureId());
							if (null == featureCommonTmpTmp) {
								List<FeatureSdk> featureList = new ArrayList<FeatureSdk>();
								featureList.add(fs);
								person.setFeatureList(featureList);
								mLibDao.insert(person);
								mFeatureCallback.onInsert(pc, fc);
							} else {
								mLibDao.update(fs);
								mFeatureCallback.onUpdate(pc, fs.getFeatureCommon());
							}
						} else {
							FeatureSdk fs = new FeatureSdk();
							fs.setFeatureCommon(featureCommonTmp);
							fs.setFeatures(features);
							mLibDao.update(fs);
							mFeatureCallback.onUpdate(pc, featureCommonTmp);
						}
						successNum++;
						if (-1 != maxFeaturePersonId && -1 != maxFeatureId) {
							map.put(maxFeaturePersonId, maxFeatureId);
						}
					} else {
						mFeatureCallback.onFail(FileUtil.getInstance().getNameByPath(pathTmp));
						failNum++;
					}
					LogUtilSdk.getInstance().i(TAG, "timelog 321 db_time = " + (System.currentTimeMillis() - startTime));
					mFeatureCallback.onSetNum(successNum, failNum);
				}
			}
		}
	}

	public void scanFeature() {
		rescanFeature();
	}

	private void rescanFeature() {
		mLibDao.queryAll();
		mFeatureCallback.onRescan();
	}

	public boolean isAdding() {
		return isAdding;
	}

	public void setAdding(boolean isAdding) {
		this.isAdding = isAdding;
	}

	public void deleteFeatureById(int personId) {
		mLibDao.delete(personId);
		mHSFaceEngine.hsfeRemoveFeatureById(personId);
	}

	public void deleteAll() {
		mLibDao.delete(-1);
		mHSFaceEngine.hsfeClearDb();
		map.clear();
	}

	private String getStr(String str, int n) {
		int i = 0;
		int s = 0;
		while (i++ < n) {
			s = str.indexOf("_", s + 1);
			if (s == -1) {
				return str;
			}
		}
		return str.substring(0, s);
	}

	/** 
	* 判断str1中包含str2的个数 
	 * @param str1 
	* @param str2 
	* @return counter 
	*/
	private int countStr(String str1, String str2) {
		if (str1.indexOf(str2) != -1) {
			counter++;
			countStr(str1.substring(str1.indexOf(str2) + str2.length()), str2);
		}
		return counter;
	}

	private String getText(String str, int start, int end) {
		int n = 0;
		int pos = -1;
		while (n < start) {
			pos = str.indexOf("\n", pos + 1);
			if (pos == -1) {
				return "";
			}
			n++;
		}
		int st_pos = pos;
		while (n < end) {
			pos = str.indexOf("\n", pos + 1);
			if (pos == -1) {
				return str.substring(pos + 1);
			}
			n++;
		}
		return str.substring(st_pos + 1, pos);
	}

	private String getIndex(String str) {
		int i = 0;
		int s = 0;
		while (i++ < 2) {
			s = str.indexOf("_", s + 1);
			if (s == -1) {
				return str;
			}
		}
		return str.substring(s + 1, str.indexOf("."));
	}

	public int getMaxIndexByNameAndCard(String name, String cardId) {
		maxFeaturePersonId = -1;
		maxFeatureId = -1;
		int ret = 0;
		PersonCommon pc = mLibDao.queryPersonCommonByNameAndCard(name, cardId);
		if (null != pc) {
			ret = getMaxIndexByPersonId(pc.getPersonId());
		}
		return ret;
	}

	private int getMaxIndexByPersonId(int personId) {
		maxFeaturePersonId = personId;
		maxFeatureId = 1 + mLibDao.queryMaxFeatureIdByPersonId(personId);
		if (maxFeatureId >= ValueUtil.getInstance().getMAX_NUM()) {
			if (null == map.get(personId)) {
				maxFeatureId = 0;
			} else {
				maxFeatureId = 1 + map.get(personId);
				if (maxFeatureId >= ValueUtil.getInstance().getMAX_NUM()) {
					maxFeatureId = 0;
				}
			}
		}
		return maxFeatureId;
	}

	private byte[] getFeaturesByPath(String path) {
		byte[] features = null;
		HSFEImage image = new HSFEImage();
		image.imageBuf = FileUtil.getInstance().file2Bytes(path);
		image.format = HSFEImageFormat.HSFE_IMG_FORMAT_JPG;
		image.width = 640;
		image.height = 480;
		if (null != image.imageBuf && 0 < image.imageBuf.length) {
			features = mHSFaceEngine.hsfeExtractFaceFeature(image);
		}
		return features;
	}

	public void deleteMapById(int personId) {
		map.remove(personId);
	}

	public PersonCommon queryPersonCommonByNameAndCard(String name, String cardId) {
		// TODO Auto-generated method stub
		return mLibDao.queryPersonCommonByNameAndCard(name, cardId);
	}

	public interface FeatureCallback {
		public void onInsert(PersonCommon pc, FeatureCommon fc);

		public void onUpdate(PersonCommon pc, FeatureCommon fc);

		public void onDelete(PersonCommon pc, FeatureCommon fc);

		public void onDelete(int personId);

		public void onRescan();

		public void onFail(String name);

		public void onSetNum(int insertNum, int failNum);
	}
}
