package com.wecall.contacts.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;
import com.wecall.contacts.constants.*;

/*
 * 辅助访问数据库
 * 数据库包含4个表：main, tag, keyValue 
 * 
 * @author KM
 * 
 */

public class DatabaseHelper extends SQLiteOpenHelper{
	    
    /* main表，存基本信息
     * 列依次为：c_id, name, fullPinyin, simplePinyin, sortLetter, note, phoneNumber, address
     */    
    private final static 
    String MAIN_TABLE = "CREATE TABLE IF NOT EXISTS main( "
    					+ "c_id INTEGER PRIMARY KEY, "
    					+ "name VARCHAR(100), "
    					+ "fullPinyin VARCHAR(100), "
    					+ "simplePinyin VARCHAR(20), "
    					+ "note VARCHAR(255), "
    					+ "phoneNumber VARCHAR(20), "
    					+ "address VARCHAR(50)"
    					+ ");";
     
    /*
     * tag表，存标签，多个
     * 列依次为：c_id, tag, tagFullPinyin, tagSimplePinyin
     */
    private final static 
    String TAG_TABLE = "CREATE TABLE IF NOT EXISTS tag( "
    					+ "c_id INTEGER NOT NULL, "
    					+ "tag VARCHAR(50),"
    					+ "tagFullPinyin VARCHAR(150), "
    					+ "tagSimplePinyin VARCHAR(50), "
    					+ "PRIMARY KEY(c_id, tag), " 
    					+ "FOREIGN KEY(c_id) REFERENCES main(c_id) ON DELETE CASCADE "
    					+ ");";   
    
    // 索引
    private final static
    String MAIN_ID_INDEX = "CREATE INDEX main_cid_index on main(c_id);";
    private final static
    String TAG_ID_INDEX = "CREATE INDEX tag_cid_index on tag(c_id);";
    private final static
    String TAG_TAG_INDEX = "CREATE INDEX tag_tag_index on tag(tag);";
    
    public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DatabaseHelper(Context context, String name, int version){
        this(context, name, null, version);
    }
    
    public DatabaseHelper(Context context, String name){
        this(context, name, Constants.DATABASE_VERSION);
    }
    
    public DatabaseHelper(Context context)
    {
    	this(context, Constants.DATABASE_NAME);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
    	try {
			// 依次创建三个表
			db.execSQL(MAIN_TABLE);
			db.execSQL(TAG_TABLE);
			//建立多个索引
			db.execSQL(MAIN_ID_INDEX);
			db.execSQL(TAG_ID_INDEX);
			db.execSQL(TAG_TAG_INDEX);
		} catch (SQLException se) {
			se.printStackTrace();
			Log.e("err", "create table failed.");
		}
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        
    }
}
