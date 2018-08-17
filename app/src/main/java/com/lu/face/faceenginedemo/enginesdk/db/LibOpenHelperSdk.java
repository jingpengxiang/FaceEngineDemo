package com.lu.face.faceenginedemo.enginesdk.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 项目名称： FaceEngineDemo
 * 创建人： Jing
 * 创建时间： 2018/7/21  13:45
 * 修改备注：
 */
public class LibOpenHelperSdk extends SQLiteOpenHelper {
    private final String TABLE_PERSON = "TABLE_PERSON";

    public String getTABLE_PERSON() {
        return TABLE_PERSON;
    }

    private final String TABLE_PERSON_TMP = "TABLE_PERSON_TMP;";
    private final String TABLE_PERSON_FIELD = " (person_id integer primary key autoincrement, name text, card_id text);";

    private final String TABLE_FEATURE = "TABLE_FEATURE";
    private final String TABLE_FEATURE_TMP = "TABLE_FEATURE_TMP;";
    private final String TABLE_FEATURE_FIELD = " (_id integer primary key autoincrement, person_id integer, feature_id integer, path text, feature_data blob);";

    public LibOpenHelperSdk(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
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
