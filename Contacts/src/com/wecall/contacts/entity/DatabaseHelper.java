package com.wecall.contacts.entity;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

/*
 * 辅助访问数据库
 * 数据库包含4个表：main, phoneNumber, address, tag 
 * 
 * @author KM
 * 
 */

public class DatabaseHelper extends SQLiteOpenHelper{
	// 数据库版本
    private static final int VERSION = 1;
    
    /* Main表，存基本信息
     * 列依次为：c_id, name, fullPinyin, simplePinyin, sortLetter, note
     */    
    private final static 
    String MAIN_TABLE = "CREATE TABLE IF NOT EXISTS main( "
    					+ "c_id INTEGER PRIMARY KEY AUTOINCREMENT, "
    					+ "name VARCHAR(100), "
    					+ "fullPinyin VARCHAR(100), "
    					+ "simplePinyin VARCHAR(20), "
    					+ "sortLetter VARCHAR(5), "
    					+ "note VARCHAR(255)"
    					+ ");";
    
    /*
     * PhoneNumber表，存多个电话
     * 列依次为：c_id, phoneNumber
     */
    private final static 
    String PHONE_NUMBER_TABLE = "CREATE TABLE IF NOT EXISTS phoneNumber(" 
    					+ "c_id INTEGER NOT NULL, "
    					+ "phoneNumber VARCHAR(50), "
    					+ "PRIMARY KEY(c_id, phoneNumber), "
    					+ "FOREIGN KEY(c_id) REFERENCES Main(c_id) ON DELETE CASCADE "
    					+ ");";

    /*
     * address表，存地址(可能多个)
     * 列依次为：c_id, address
     */
    private final static 
    String ADDRESS_TABLE = "CREATE TABLE IF NOT EXISTS address("
    					+ "c_id INTEGER NOT NULL, "
    					+ "address VARCHAR(255), "
    					+ "PRIMARY KEY(c_id, address), "
    					+ "FOREIGN KEY(c_id) REFERENCES main(c_id) ON DELETE CASCADE "
    					+ ");";
    
    /*
     * tag表，存标签，多个
     * 列依次为：c_id, tag
     */
    private final static 
    String TAG_TABLE = "CREATE TABLE IF NOT EXISTS tag( "
    					+ "c_id INTEGER NOT NULL, "
    					+ "tag VARCHAR(50),"
    					+ "PRIMARY KEY(c_id, tag), " 
    					+ "FOREIGN KEY(c_id) REFERENCES main(c_id) ON DELETE CASCADE "
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
    	try {
			// 依次创建四个表
			db.execSQL(MAIN_TABLE);
			db.execSQL(ADDRESS_TABLE);
			db.execSQL(PHONE_NUMBER_TABLE);
			db.execSQL(TAG_TABLE);
		} catch (SQLException se) {
			// TODO Auto-generated catch block
			se.printStackTrace();
			Log.i("err", "create table failed.");
		}
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        
    }
}
