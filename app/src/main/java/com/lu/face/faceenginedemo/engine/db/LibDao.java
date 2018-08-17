package com.lu.face.faceenginedemo.engine.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.lu.face.faceenginedemo.engine.models.PersonFeature;
import com.lu.face.faceenginedemo.engine.utils.LogUtil;
import com.lu.face.faceenginedemo.enginesdk.models.FeatureCommon;
import com.lu.face.faceenginedemo.enginesdk.models.PersonCommon;

import java.io.File;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class LibDao {
	private final String TAG = "LibDao";
	private final String DB_NAME = Environment.getExternalStorageDirectory().getPath() + File.separator + "HisignEngine" + File.separator + "lib_feature.db";
	private final String TABLE_PERSON_FIELD = "(person_id,name,card_id) values(?,?,?)";
	private final String TABLE_FEATURE_FIELD = "(person_id,feature_id,path,is_show) values(?,?,?,?)";
	private LibOpenHelper mLibOpenHelper;

	public LibDao(Context context) {
		mLibOpenHelper = new LibOpenHelper(context, DB_NAME, null, 1);
	}

	public void insert(PersonFeature person) {
		synchronized (mLibOpenHelper) {
			if (null == person) {
				return;
			}
			SQLiteDatabase mSQLiteDatabase = mLibOpenHelper.getWritableDatabase();
			String sql = "insert or ignore into " + mLibOpenHelper.getTABLE_PERSON() + TABLE_PERSON_FIELD;
			Object[] values = new Object[] { person.getPersonCommon().getPersonId(), person.getPersonCommon().getName(), person.getPersonCommon().getCardId() };
			mSQLiteDatabase.execSQL(sql, values);
			String sqlTmp = "insert or ignore into " + mLibOpenHelper.getTABLE_FEATURE() + TABLE_FEATURE_FIELD;
			Object[] valuesTmp = new Object[] { person.getFeatureCommon().getPersonId(), person.getFeatureCommon().getFeatureId(), person.getFeatureCommon().getPath(), null };
			mSQLiteDatabase.execSQL(sqlTmp, valuesTmp);
			mSQLiteDatabase.close();
		}
	}

	public void update(PersonFeature person) {
		synchronized (mLibOpenHelper) {
			if (null == person) {
				return;
			}
			SQLiteDatabase mSQLiteDatabase = mLibOpenHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put("person_id", person.getPersonCommon().getPersonId());
			values.put("name", person.getPersonCommon().getName());
			values.put("card_id", person.getPersonCommon().getCardId());
			mSQLiteDatabase.update(mLibOpenHelper.getTABLE_PERSON(), values, "person_id=?", new String[] { String.valueOf(person.getPersonCommon().getPersonId()) });
			ContentValues valuesTmp = new ContentValues();
			valuesTmp.put("person_id", person.getFeatureCommon().getPersonId());
			valuesTmp.put("feature_id", person.getFeatureCommon().getFeatureId());
			valuesTmp.put("path", person.getFeatureCommon().getPath());
			int ret = mSQLiteDatabase.update(mLibOpenHelper.getTABLE_FEATURE(), valuesTmp, "person_id=?", new String[] { String.valueOf(person.getPersonCommon().getPersonId()) });
			mSQLiteDatabase.close();
		}
	}

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

	public Map queryAll() {
		synchronized (mLibOpenHelper) {
			Map<Integer, PersonFeature> map = new TreeMap<Integer, PersonFeature>(new Comparator<Integer>() {
				public int compare(Integer obj1, Integer obj2) {
					return obj2.compareTo(obj1);// 降序排序
				}
			});
			SQLiteDatabase mSQLiteDatabase = mLibOpenHelper.getWritableDatabase();
			String sql = "select * from " + mLibOpenHelper.getTABLE_PERSON() + " as a left outer join " + mLibOpenHelper.getTABLE_FEATURE()
					+ " as b on a.person_id = b.person_id order by person_id desc, feature_id asc";
			LogUtil.getInstance().i(TAG, "194 query rawQuery start");
			Cursor cursor = mSQLiteDatabase.rawQuery(sql, null);
			LogUtil.getInstance().i(TAG, "194 query rawQuery end");
			while (cursor.moveToNext()) {
				PersonFeature pf = new PersonFeature();
				PersonCommon pc = new PersonCommon();
				pc.setPersonId(cursor.getInt(0));
				pc.setName(cursor.getString(1));
				pc.setCardId(cursor.getString(2));
				pf.setPersonCommon(pc);
				FeatureCommon fc = new FeatureCommon();
				fc.setPersonId(cursor.getInt(4));
				fc.setFeatureId(cursor.getInt(5));
				fc.setPath(cursor.getString(6));
				pf.setFeatureCommon(fc);
				map.put(pc.getPersonId(), pf);
			}
			LogUtil.getInstance().i(TAG, "194 query rawQuery end  end");
			cursor.close();
			mSQLiteDatabase.close();
			return map;
		}
	}
}
