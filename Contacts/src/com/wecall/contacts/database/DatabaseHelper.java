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
 * 
 * @author KM
 * 
 */

public class DatabaseHelper extends SQLiteOpenHelper{
	    
    /* main表，存基本信息
     * 列依次为：c_id, name, note, phone,	address, tag, other
     */    
    private final static 
    String MAIN_TABLE = "CREATE TABLE IF NOT EXISTS main( "
    					+ "c_id INTEGER PRIMARY KEY AUTOINCREMENT, "
    					+ "name TEXT, "
    					+ "note TEXT, "
    					+ "phone TEXT, "
    					+ "tag TEXT, "
    					+ "address TEXT, "
    					+ "other TEXT"
    					+ ");";   
    
    /*
     * tag表，存标签及其标号
     * 列依次为：t_id, tagName
     */
    private final static
    String TAG_TABLE = "CREATE TABLE IF NOT EXISTS tag( " +
    				"t_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
    				"tagName TEXT UNIQUE" +
    				");";
    
    /*
     * search表，fts4虚表，主要用来查询
     * 列依次为：c_id, type_id, data1, data2, data3 （自带rowid）
     */
    private final static
    String SEARCH_TABLE = "CREATE VIRTUAL TABLE search USING" +
    					" fts4(c_id, type_id, data1, data2, data3, data4);";
    
//    /*
//     * t2c表，建立tag表与main表的联系
//     * 列依次为：c_id, t_id
//     */
//    private final static 
//    String T2C_TABLE = "CREATE TABLE IF NOT EXISTS t2c( " +
//    				"c_id INTEGER REFERENCES main(c_id) ON DELETE CASCADE, " + 
//    				"t_id INTEGER REFERENCES tag(t_id) ON DELETE CASCADE, " +
//    				"PRIMARY KEY (c_id, t_id) ); ";
    
    // 索引
    private final static
    String CONTACT_ID_INDEX = "CREATE INDEX contact_cid_index on main(c_id);";
    private final static 
    String TAG_NAME_INDEX = "CREATE INDEX tag_name_index on tag(tagName);";
    private final static
    String TAG_ID_INDEX = "CREATE INDEX tag_id_index on tag(t_id);";
    
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
			// 依次创建表
			db.execSQL(MAIN_TABLE);
			db.execSQL(SEARCH_TABLE);
			db.execSQL(TAG_TABLE);
			//建立索引
			db.execSQL(CONTACT_ID_INDEX);
			db.execSQL(TAG_NAME_INDEX);
			db.execSQL(TAG_ID_INDEX);
		} catch (SQLException se) {
			se.printStackTrace();
			Log.e("err", "create table failed.");
		}
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        
    }
}
