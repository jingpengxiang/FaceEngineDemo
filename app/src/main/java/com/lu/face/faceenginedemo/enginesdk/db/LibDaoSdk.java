package com.lu.face.faceenginedemo.enginesdk.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.hisign.face.match.engine.model.HsfeFeature;
import com.hisign.face.match.engine.verify.HSFaceEngine;
import com.lu.face.faceenginedemo.enginesdk.models.FeatureCommon;
import com.lu.face.faceenginedemo.enginesdk.models.FeatureSdk;
import com.lu.face.faceenginedemo.enginesdk.models.PersonCommon;
import com.lu.face.faceenginedemo.enginesdk.models.PersonSdk;
import com.lu.face.faceenginedemo.enginesdk.utils.FeatureUtilSdk;
import com.lu.face.faceenginedemo.enginesdk.utils.StringUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称： FaceEngineDemo
 * 创建人： Jing
 * 创建时间： 2018/7/21  13:45
 * 修改备注：
 */
public class LibDaoSdk {
    private final String TAG = "LibDaoSdk";
    private final String DB_NAME = Environment.getExternalStorageDirectory().getPath() + File.separator + "HisignEngine" + File.separator + "lib_feature_sdk.db";
    private final String TABLE_PERSON_FIELD = "(person_id,name,card_id) values(?,?,?)";
    private final String TABLE_FEATURE_FIELD = "(person_id,feature_id,path,feature_data) values(?,?,?,?)";
    private LibOpenHelperSdk mLibOpenHelper;
    private HSFaceEngine mHSFaceEngine;
    private FeatureUtilSdk.FeatureCallback mFeatureCallback;

    public LibDaoSdk(Context context, HSFaceEngine hSFaceEngine, FeatureUtilSdk.FeatureCallback mFeatureCallback) {
        mLibOpenHelper = new LibOpenHelperSdk(context, DB_NAME, null, 1);
        mHSFaceEngine = hSFaceEngine;
        this.mFeatureCallback = mFeatureCallback;
    }

    public void insert(PersonSdk person) {
        synchronized (mLibOpenHelper) {
            if (null == person) {
                return;
            }
            SQLiteDatabase mSQLiteDatabase = mLibOpenHelper.getWritableDatabase();
            mSQLiteDatabase.beginTransaction();
            try {
                String sql = "insert or ignore into " + mLibOpenHelper.getTABLE_PERSON() + TABLE_PERSON_FIELD;
                Object[] values = new Object[] { person.getPersonCommon().getPersonId(), person.getPersonCommon().getName(), person.getPersonCommon().getCardId() };
                mSQLiteDatabase.execSQL(sql, values);
                List<FeatureSdk> list = person.getFeatureList();
                if (null != list && 0 < list.size()) {
                    int size = list.size();
                    for (int i = 0; i < size; i++) {
                        FeatureSdk feature = list.get(i);
                        String sqlTmp = "insert or ignore into " + mLibOpenHelper.getTABLE_FEATURE() + TABLE_FEATURE_FIELD;
                        Object[] valuesTmp = new Object[] { feature.getFeatureCommon().getPersonId(), feature.getFeatureCommon().getFeatureId(),
                                feature.getFeatureCommon().getPath(), feature.getFeatures() };
                        mSQLiteDatabase.execSQL(sqlTmp, valuesTmp);
                    }
                }
                mSQLiteDatabase.setTransactionSuccessful(); // 设置事务处理成功，不设置会自动回滚不提交
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (mSQLiteDatabase.isOpen()) {
                    mSQLiteDatabase.endTransaction(); // 处理完成
                }
            }
            mSQLiteDatabase.close();
        }
    }

    public void update(PersonSdk person) {
        synchronized (mLibOpenHelper) {
            if (null == person) {
                return;
            }
            SQLiteDatabase mSQLiteDatabase = mLibOpenHelper.getWritableDatabase();
            mSQLiteDatabase.beginTransaction();
            try {
                ContentValues values = new ContentValues();
                values.put("person_id", person.getPersonCommon().getPersonId());
                values.put("name", person.getPersonCommon().getName());
                values.put("card_id", person.getPersonCommon().getCardId());
                mSQLiteDatabase.update(mLibOpenHelper.getTABLE_PERSON(), values, "person_id=?", new String[] { String.valueOf(person.getPersonCommon().getPersonId()) });
                List<FeatureSdk> list = person.getFeatureList();
                if (null != list && 0 < list.size()) {
                    int size = list.size();
                    for (int i = 0; i < size; i++) {
                        FeatureSdk feature = list.get(i);
//				String sql = "update " + mLibOpenHelper.getTABLE_FEATURE() + " set userId=" + libParam.getUserId() + ", id=" + item.getId() + ", name="
//						+ sqliteEscape(item.getName()) + ", path=" + sqliteEscape(item.getPath()) + ", isChecked=" + (item.isChecked() ? 1 : 0) + ", features" + item.getFeatures()
//						+ " where name=" + sqliteEscape(item.getName());
                        String sql = "update " + mLibOpenHelper.getTABLE_FEATURE() + " set " + TABLE_FEATURE_FIELD;
//				Object[] values = new Object[] { libParam.getUserId(), item.getId(), sqliteEscape(item.getName()), sqliteEscape(item.getPath()), (item.isChecked() ? 1 : 0),
//						item.getFeatures() };
//				mSQLiteDatabase.execSQL(sql, values);
                        ContentValues valuesTmp = new ContentValues();
                        valuesTmp.put("person_id", feature.getFeatureCommon().getPersonId());
                        valuesTmp.put("feature_id", feature.getFeatureCommon().getFeatureId());
                        valuesTmp.put("path", feature.getFeatureCommon().getPath());
                        valuesTmp.put("feature_data", feature.getFeatures());
                        int ret = mSQLiteDatabase.update(mLibOpenHelper.getTABLE_FEATURE(), valuesTmp, "person_id=? and feature_id=?",
                                new String[] { String.valueOf(feature.getFeatureCommon().getPersonId()), String.valueOf(feature.getFeatureCommon().getFeatureId()) });
//				LogUtil.getInstance().i(TAG, "65 update ret = " + ret); // ret > 0:update success
                    }
                }
                mSQLiteDatabase.setTransactionSuccessful(); // 设置事务处理成功，不设置会自动回滚不提交
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (mSQLiteDatabase.isOpen()) {
                    mSQLiteDatabase.endTransaction(); // 处理完成
                }
            }
            mSQLiteDatabase.close();
        }
    }

    public void update(FeatureSdk fs) {
        // TODO Auto-generated method stub
        synchronized (mLibOpenHelper) {
            SQLiteDatabase mSQLiteDatabase = mLibOpenHelper.getWritableDatabase();
            ContentValues valuesTmp = new ContentValues();
            valuesTmp.put("person_id", fs.getFeatureCommon().getPersonId());
            valuesTmp.put("feature_id", fs.getFeatureCommon().getFeatureId());
            valuesTmp.put("path", fs.getFeatureCommon().getPath());
            valuesTmp.put("feature_data", fs.getFeatures());
            int ret = mSQLiteDatabase.update(mLibOpenHelper.getTABLE_FEATURE(), valuesTmp, "person_id=? and feature_id=?",
                    new String[] { String.valueOf(fs.getFeatureCommon().getPersonId()), String.valueOf(fs.getFeatureCommon().getFeatureId()) });
//		LogUtil.getInstance().i(TAG, "65 update ret = " + ret); // ret > 0:update success
            mSQLiteDatabase.close();
        }
    }

    /**
     * @Title: delete
     * @param personId   personId<0 : 删除所有特征数据    personId>=0 : 删除personId特征信息
     * @return: void
     * @date:Sep 11, 2017 4:10:43 PM	@author:lvwenyan
     */
    public void delete(int personId) {
        synchronized (mLibOpenHelper) {
            SQLiteDatabase mSQLiteDatabase = mLibOpenHelper.getWritableDatabase();
            if (0 > personId) {
                String sql = "delete from " + mLibOpenHelper.getTABLE_PERSON();
                mSQLiteDatabase.execSQL(sql);
                String sqlTmp = "delete from " + mLibOpenHelper.getTABLE_FEATURE();
                mSQLiteDatabase.execSQL(sqlTmp);
            } else {
                String sql = "delete from " + mLibOpenHelper.getTABLE_PERSON() + " where person_id=" + personId;
                mSQLiteDatabase.execSQL(sql);
                String sqlTmp = "delete from " + mLibOpenHelper.getTABLE_FEATURE() + " where person_id=" + personId;
                mSQLiteDatabase.execSQL(sqlTmp);
            }
            mSQLiteDatabase.close();
        }
    }

    public void queryAll() {
        synchronized (mLibOpenHelper) {
            SQLiteDatabase mSQLiteDatabase = mLibOpenHelper.getWritableDatabase();
            String sql = "select * from " + mLibOpenHelper.getTABLE_PERSON() + " as a left outer join " + mLibOpenHelper.getTABLE_FEATURE() + " as b on a.person_id = b.person_id";
            Cursor cursor = mSQLiteDatabase.rawQuery(sql, null);
            boolean isAddable = false;
            int prePersonId = -1;
            int count = 0;
            mHSFaceEngine.hsfeClearDb();
            while (cursor.moveToNext()) {
                int personId = cursor.getInt(cursor.getColumnIndex("person_id"));
                if (prePersonId == personId) {
                    if (5 < count) {
                        continue;
                    } else {
                        isAddable = true;
                    }
                } else {
                    isAddable = true;
                    count = 0;
                    prePersonId = personId;
                }
                if (isAddable) {
                    isAddable = false;
                    count++;
                    HsfeFeature hsfeFeature = new HsfeFeature();
                    hsfeFeature.persionId = personId;
                    hsfeFeature.feat = cursor.getBlob(cursor.getColumnIndex("feature_data"));
                    if (0 != mHSFaceEngine.hsfeAddFeatureToDb(hsfeFeature)) {
                        delete(personId);
                        mFeatureCallback.onDelete(personId);
                    }
                }
            }
            cursor.close();
            mSQLiteDatabase.close();
        }
    }

    public FeatureCommon queryFeatureCommonByPath(String path) {
        synchronized (mLibOpenHelper) {
            FeatureCommon fc = null;
            SQLiteDatabase mSQLiteDatabase = mLibOpenHelper.getWritableDatabase();
            String sql = "select * from " + mLibOpenHelper.getTABLE_FEATURE() + " where path=?";
            Cursor cursor = mSQLiteDatabase.rawQuery(sql, new String[] { path });
            while (cursor.moveToNext()) {
                fc = new FeatureCommon();
                fc.setPersonId(cursor.getInt(cursor.getColumnIndex("person_id")));
                fc.setFeatureId(cursor.getInt(cursor.getColumnIndex("feature_id")));
                fc.setPath(cursor.getString(cursor.getColumnIndex("path")));
                break;
            }
            cursor.close();
            mSQLiteDatabase.close();
            return fc;
        }
    }

    /**获取含有path路径的所有特征数据
     * @Title: queryByParentPath
     * @param path
     * @return
     * @return: List<FeatureCommon>
     * @date:Sep 11, 2017 4:17:23 PM	@author:lvwenyan
     */
    public List<FeatureCommon> queryByParentPath(String path) {
        synchronized (mLibOpenHelper) {
            List<FeatureCommon> list = new ArrayList<FeatureCommon>();
            SQLiteDatabase mSQLiteDatabase = mLibOpenHelper.getWritableDatabase();
            Cursor cursor = mSQLiteDatabase.query(mLibOpenHelper.getTABLE_FEATURE(), new String[] { "person_id,feature_id,path" }, "path like ?", new String[] { "%" + path + "%" },
                    null, null, null, null);
//		Cursor cursor = mSQLiteDatabase.query(mLibOpenHelper.getTABLE_FEATURE(), new String[] { "person_id", "feature_id", "path" }, "path like '%" + path + "%'", null, null,
//				null, null, "1");
            while (cursor.moveToNext()) {
                FeatureCommon fc = new FeatureCommon();
                fc.setPersonId(cursor.getInt(cursor.getColumnIndex("person_id")));
                fc.setFeatureId(cursor.getInt(cursor.getColumnIndex("feature_id")));
                fc.setPath(cursor.getString(cursor.getColumnIndex("path")));
                list.add(fc);
            }
            cursor.close();
            mSQLiteDatabase.close();
            return list;
        }
    }

    public FeatureCommon queryFeatureCommonById(int personId, int featureId) {
        synchronized (mLibOpenHelper) {
            FeatureCommon fc = null;
            SQLiteDatabase mSQLiteDatabase = mLibOpenHelper.getWritableDatabase();
            String sql = "select * from " + mLibOpenHelper.getTABLE_FEATURE() + " where person_id=" + personId + " and feature_id=" + featureId;
            Cursor cursor = mSQLiteDatabase.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                fc = new FeatureCommon();
                fc.setPersonId(cursor.getInt(cursor.getColumnIndex("person_id")));
                fc.setFeatureId(cursor.getInt(cursor.getColumnIndex("feature_id")));
                fc.setPath(cursor.getString(cursor.getColumnIndex("path")));
                break;
            }
            cursor.close();
            mSQLiteDatabase.close();
            return fc;
        }
    }

    public PersonCommon queryPersonCommonByPersonId(int personId) {
        synchronized (mLibOpenHelper) {
            PersonCommon pc = null;
            SQLiteDatabase mSQLiteDatabase = mLibOpenHelper.getWritableDatabase();
            String sql = "select * from " + mLibOpenHelper.getTABLE_PERSON() + " where person_id=" + personId;
            Cursor cursor = mSQLiteDatabase.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                pc = new PersonCommon();
                pc.setPersonId(cursor.getInt(cursor.getColumnIndex("person_id")));
                pc.setName(cursor.getString(cursor.getColumnIndex("name")));
                pc.setCardId(cursor.getString(cursor.getColumnIndex("card_id")));
                break;
            }
            cursor.close();
            mSQLiteDatabase.close();
            return pc;
        }
    }

    public PersonCommon queryPersonCommonByNameAndCard(String name, String cardId) {
        synchronized (mLibOpenHelper) {
            PersonCommon pc = null;
            SQLiteDatabase mSQLiteDatabase = mLibOpenHelper.getWritableDatabase();
            String sql = null;
            Cursor cursor = null;
            if (StringUtil.isNotNull(cardId)) {
                sql = "select * from " + mLibOpenHelper.getTABLE_PERSON() + " where name=?" + " and card_id=?";
                cursor = mSQLiteDatabase.rawQuery(sql, new String[] { name, cardId });
            } else {
                sql = "select * from " + mLibOpenHelper.getTABLE_PERSON() + " where name=?";
                cursor = mSQLiteDatabase.rawQuery(sql, new String[] { name });
            }
            while (cursor.moveToNext()) {
                pc = new PersonCommon();
                pc.setPersonId(cursor.getInt(cursor.getColumnIndex("person_id")));
                pc.setName(cursor.getString(cursor.getColumnIndex("name")));
                pc.setCardId(cursor.getString(cursor.getColumnIndex("card_id")));
                break;
            }
            cursor.close();
            mSQLiteDatabase.close();
            return pc;
        }
    }

    public int queryMaxFeatureIdByPersonId(int personId) {
        synchronized (mLibOpenHelper) {
            int ret = 0;
            SQLiteDatabase mSQLiteDatabase = mLibOpenHelper.getWritableDatabase();
            String sql = "select * from " + mLibOpenHelper.getTABLE_FEATURE() + " where person_id=" + personId + " order by feature_id desc";
            Cursor cursor = mSQLiteDatabase.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                ret = cursor.getInt(cursor.getColumnIndex("feature_id"));
                break;
            }
            cursor.close();
            mSQLiteDatabase.close();
            return ret;
        }
    }

    public int queryMaxPersonId() {
        synchronized (mLibOpenHelper) {
            int ret = -1;
            SQLiteDatabase mSQLiteDatabase = mLibOpenHelper.getWritableDatabase();
            String sql = "select * from " + mLibOpenHelper.getTABLE_PERSON() + " order by person_id desc";
            Cursor cursor = mSQLiteDatabase.rawQuery(sql, null);
            if (null != cursor) {
                while (cursor.moveToNext()) {
                    ret = cursor.getInt(cursor.getColumnIndex("person_id"));
                    break;
                }
            }
            cursor.close();
            mSQLiteDatabase.close();
            return ret;
        }
    }

    public String sqliteEscape(String keyWord) {
        keyWord = keyWord.replace("/", "//");
        keyWord = keyWord.replace("'", "''");
        keyWord = keyWord.replace("[", "/[");
        keyWord = keyWord.replace("]", "/]");
        keyWord = keyWord.replace("%", "/%");
        keyWord = keyWord.replace("&", "/&");
        keyWord = keyWord.replace("_", "/_");
        keyWord = keyWord.replace("(", "/(");
        keyWord = keyWord.replace(")", "/)");
        return keyWord;
    }

    public String sqliteUnEscape(String keyWord) {
        keyWord = keyWord.replace("//", "/");
        keyWord = keyWord.replace("''", "'");
        keyWord = keyWord.replace("/[", "[");
        keyWord = keyWord.replace("/]", "]");
        keyWord = keyWord.replace("/%", "%");
        keyWord = keyWord.replace("/&", "&");
        keyWord = keyWord.replace("/_", "_");
        keyWord = keyWord.replace("/(", "(");
        keyWord = keyWord.replace("/)", ")");
        return keyWord;
    }
}
