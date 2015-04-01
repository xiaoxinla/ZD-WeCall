package com.wecall.contacts.entity;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/*
 * 管理数据库类，隐藏SQL语句细节，提供插入删除查询接口
 * 
 * @author KM
 */

public class Database {
	private SQLiteDatabase db = null;
	private DatabaseHelper helper;
	private final static String TB_NAME = "Contacts";
	
	public Database(Context context)
	{
		helper = new DatabaseHelper(context, "Contacts.db");
	}
	
	public void createTable(String tableName)
	{
		db = helper.getWritableDatabase();
		String sql = "create table if not exists "
				+ tableName
				+ "(_id integer primary key, "
				+ "number varchar(20) "
				+ ");";
		try
		{
			db.execSQL(sql);	
		} catch (SQLException se)
		{
			Log.i("err", "create table failed");
		}
	}
	
	public void insert(ContactItem item)
	{
		// 建立到数据库的可写连接
		db = helper.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put("name", item.getName());
		values.put("phoneNumber", item.getPhoneNumber());
		values.put("address", item.getAddress());
		values.put("fullPinyin", item.getFullPinyin());
		values.put("simplePinyin", item.getSimplePinyin());
		
		db.insert(TB_NAME, null, values);
	}
	
	public void delete(int id)
	{
		db = helper.getWritableDatabase();
		
		db.delete(TB_NAME, "_id = ?", new String[]{ Integer.toString(id) } );
	}
	
	public void update(ContactItem item)
	{
		db = helper.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put("name", item.getName());
		values.put("phoneNumber", item.getPhoneNumber());
		values.put("address", item.getAddress());
		values.put("fullPinyin", item.getFullPinyin());
		values.put("simplePinyin", item.getSimplePinyin());
		
		db.update(TB_NAME, values, "name = ?", new String[]{item.getName()});
	}
	
	public ArrayList<ContactItem> query(String name)
	{
		ArrayList<ContactItem> list = null;
		try {
			db = helper.getReadableDatabase();
			
			Cursor cursor = db.query(TB_NAME, new String[] {"_id,name"}, "name=?", 
					new String[] {name}, null, null, null);
			Log.i("Cursor", Integer.toString(cursor.getCount()));
			list = convertCursorToItem(cursor);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return list;
	}
	
	/*
	 * 提供直接执行sql语句的接口，调试用
	 * @Deprecated
	 */	
	public void execSQL(String sql)
	{
		db = helper.getWritableDatabase();
		db.execSQL(sql);
	}
	
	public void close()
	{
		db.close();
		helper.close();
	}
	
	private ArrayList<ContactItem> convertCursorToItem(Cursor cursor)
	{
		ArrayList<ContactItem> list = new ArrayList<ContactItem>();
		
		while(cursor.moveToNext())
		{
			ContactItem it = new ContactItem();
			it.setId(cursor.getInt(0));
			it.setName(cursor.getString(1));
			
			list.add(it);
		}
		
		Log.i("size", Integer.toString(list.size()) );
		
		return list;
	}
}
