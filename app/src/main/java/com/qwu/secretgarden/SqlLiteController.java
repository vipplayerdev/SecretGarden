package com.qwu.secretgarden;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SqlLiteController extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "User.db";
    public static final String TABLE_NAME = "user_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "ACCOUNT";
    public static final String COL_3 = "PASSWORD";
    public static final String COL_4 = "CODE";
    public static final String COL_5 = "AVATAR";
    public static final String COL_6 = "NAME";
    public static String FirebaseUserId = "";
    public static String FamilyCode = "";

    public SqlLiteController(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " " +
                "(ID TEXT PRIMARY KEY," +
                "ACCOUNT TEXT," +
                "PASSWORD TEXT," +
                "CODE TEXT," +
                "AVATAR TEXT," +
                "NAME TEXT," +
                "MARKS INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String id, String account, String code, String avatar, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, id);
        contentValues.put(COL_2, account);
        contentValues.put(COL_4, code);
        contentValues.put(COL_5, avatar);
        contentValues.put(COL_6, name);
        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        return res;
    }

    public HashMap<String,String> getUserByFirebaseUid(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " where id = '"+id+"'", null);

        StringBuffer buffer = new StringBuffer();

        HashMap<String,String> m = new HashMap<>();
        while (res.moveToNext()) { //cycle thru result set
            m.put("id",res.getString(0));
            m.put("account",res.getString(1));
            m.put("code",res.getString(3));
            m.put("avatar",res.getString(4));
            m.put("name",res.getString(5));
        }

        System.out.println("本地数据结果:"+m.toString());

        return m;
    }

    public boolean updateData(String  firebaseUserId,String account,String name,String code,String avatar) {
        Log.i("firebaseUserId:",firebaseUserId+" account:"+account+ " name:"+name+ " code:"+code+" avatar:"+avatar);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, account);
        contentValues.put(COL_4, code);
        contentValues.put(COL_5, avatar);
        contentValues.put(COL_6, name);
        db.update(TABLE_NAME, contentValues, "ID = ?", new String[]{firebaseUserId});
        return true;

    }

    public Integer deleteData(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?", new String[]{id});
    }
}