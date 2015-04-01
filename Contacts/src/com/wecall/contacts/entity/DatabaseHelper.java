package com.wecall.contacts.entity;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

/*
 * 辅助访问数据库
 * 
 * @author KM
 * 
 */

public class DatabaseHelper extends SQLiteOpenHelper{
	// 数据库版本
    private static final int VERSION = 1;
    // 创建表
    private final String CREATE_TABLE = "create table if not exists " 
    								+ "Contacts(_id integer primary key autoincrement, " 
    								+ "name varchar(50), " 
    								+ "phoneNumber varchar(20), "
    								+ "address varchar(100), "
    								+ "fullPinyin varchar(50), "
    								+ "simplePinyin varchar(10) "
    								+ ");";
    
    public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DatabaseHelper(Context context, String name, int version){
        this(context, name, null, version);
    }
    
    public DatabaseHelper(Context context, String name){
        this(context, name, VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        
    }
}
