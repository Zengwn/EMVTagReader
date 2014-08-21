package com.example.nfctagrw.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DbHelperAlpha {
    private static final String TAG = DbHelperAlpha.class.getSimpleName();
    // need to be implemented as a common db class.
    private static final String DB_USERINFO = "db_userinfo";
    private static final String DB_TAGINFO = "db_TAGInfo";

    private SQLiteDatabase mDataBaseUserInfo;
    private SQLiteDatabase mDataBaseTagInfo;
    private Context mContext;

    DbHelperAlpha(Context c) {
        mContext = c;
        try {
            mDataBaseUserInfo = mContext.openOrCreateDatabase(DB_USERINFO,
                    Context.MODE_PRIVATE, null);
            mDataBaseTagInfo = mContext.openOrCreateDatabase(DB_TAGINFO,
                    Context.MODE_PRIVATE, null);
            CreateTable();
            Log.v(TAG, "database1 path:" + mDataBaseUserInfo.getPath());
            Log.v(TAG, "database2 path:" + mDataBaseTagInfo.getPath());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to create database");
        }
    }

    private void CreateTable() {
        try {
            mDataBaseUserInfo.execSQL("CREATE TABLE t_taguser ("
                    + "_USERID TEXT PRIMARY KEY " + "_FIRSTNAME TEXT"
                    + "_LASTNAME TEXT" + ");");
            mDataBaseTagInfo.execSQL("CREATE TABLE t_taginfo ("
                    + "_USERID INTEGER PRIMARY KEY," + "_TAGINFO TEXT" + ");");

        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "Failed to create Tables");
        }
        Log.v(TAG, "Created tables");
    }

    public boolean InsertUserInfo(String uid, String firstname, String lasname) {
        String sql = "";
        try {
            sql = "insert into t_user values('" + uid + "','" + firstname
                    + "','" + lasname + "')";
            mDataBaseUserInfo.execSQL(sql);
            Log.v(TAG, "insert Table t_user ok");
            return true;

        } catch (Exception e) {
            Log.v(TAG, "insert Table t_user err ,sql: " + sql);
            return false;
        }
    }

    public boolean InsertTAGInfo(String uid, String taginfo) {
        String sql = "";
        try {
            sql = "insert into t_user values('" + uid + "','" + taginfo + "')";
            mDataBaseUserInfo.execSQL(sql);
            Log.v(TAG, "insert Table t_user ok");
            return true;

        } catch (Exception e) {
            Log.v(TAG, "insert Table t_user err ,sql: " + sql);
            return false;
        }
    }

    public Cursor loadUserInfo() {

        Cursor cur = mDataBaseUserInfo.query("t_taguser", new String[] {
                "_USERID", "_FIRSTNAME", "_LASTNAME" }, null, null, null, null,
                null);

        return cur;
    }

    public Cursor loadTAGInfo(String uid) {

        Cursor cur = mDataBaseUserInfo.query("t_taginfo",
                new String[] { "_TAGINFO" }, "_USERID = ?",
                new String[] { uid }, null, null, null);

        return cur;
    }

    public void closeUserdb() {
        mDataBaseUserInfo.close();
    }

    public void closeTagdb() {
        mDataBaseTagInfo.close();
    }
}
