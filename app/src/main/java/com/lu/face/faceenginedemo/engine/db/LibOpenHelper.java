package com.lu.face.faceenginedemo.engine.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class LibOpenHelper extends SQLiteOpenHelper {
	private final String TABLE_PERSON = "TABLE_PERSON";

	public String getTABLE_PERSON() {
		return TABLE_PERSON;
	}

	private final String TABLE_PERSON_TMP = "TABLE_PERSON_TMP;";
	private final String TABLE_PERSON_FIELD = " (person_id integer primary key autoincrement, name text, card_id integer);";// _id integer primary key autoincrement,

	private final String TABLE_FEATURE = "TABLE_FEATURE";
	private final String TABLE_FEATURE_TMP = "TABLE_FEATURE_TMP;";
	private final String TABLE_FEATURE_FIELD = " (_id integer primary key autoincrement, person_id integer, feature_id integer, path text,is_show integer);";

	public LibOpenHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("create table " + TABLE_PERSON + TABLE_PERSON_FIELD);
		db.execSQL("create table " + TABLE_FEATURE + TABLE_FEATURE_FIELD);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("alter table " + TABLE_PERSON + " rename to " + TABLE_PERSON_TMP);
		db.execSQL("create table " + TABLE_PERSON + TABLE_PERSON_FIELD);
		db.execSQL("insert into " + TABLE_PERSON + " select * from " + TABLE_PERSON_TMP);
		db.execSQL("drop table " + TABLE_PERSON_TMP);

		db.execSQL("alter table " + TABLE_FEATURE + " rename to " + TABLE_FEATURE_TMP);
		db.execSQL("create table " + TABLE_FEATURE + TABLE_FEATURE_FIELD);
		db.execSQL("insert into " + TABLE_FEATURE + " select * from " + TABLE_FEATURE_TMP);
		db.execSQL("drop table " + TABLE_FEATURE_TMP);
	}

	public String getTABLE_FEATURE() {
		return TABLE_FEATURE;
	}
}
