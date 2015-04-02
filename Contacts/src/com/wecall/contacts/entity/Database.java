package com.wecall.contacts.entity;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
	private final static String MAIN_TABLE = "main";
	private final static String ADDRESS_TABLE = "address";
	private final static String TAG_TABLE = "tag";
	private final static String PHONE_TABLE = "phoneNumber";
	// 为联系人分配的唯一标识
	private static int id;
	// id保存到preference中
	private static SharedPreferences preferences;
	
	public Database(Context context)
	{
		helper = new DatabaseHelper(context, "Contacts.db");
		// 建立到数据库的可写连接
		db = helper.getWritableDatabase();
		// 通过shared preference初始化id值
		preferences = context.getSharedPreferences("database", Context.MODE_PRIVATE);
		id = preferences.getInt("id_count", 1);
	}
	
	/*
	 * 向数据库插入新的一个联系人
	 * 如果插入成功会同时设置item对象的id.
	 */
	public void insert(ContactItem item)
	{		
		ContentValues values = new ContentValues();
		db.beginTransaction();
		try {
			// 插入main表
			values.put("c_id", id);
			values.put("name", item.getName());
			values.put("fullPinyin", item.getFullPinyin());
			values.put("simplePinyin", item.getSimplePinyin());
			values.put("sortLetter", item.getSortLetter());
			values.put("note", item.getNote());		
			db.insert(MAIN_TABLE, null, values);
			
			// 插入phoneNum表
			// TODO: 多个Number
			values.clear();
			values.put("c_id", id);
			values.put("phoneNumber", item.getPhoneNumber());
			db.insert(PHONE_TABLE, null, values);
			
			// 插入address表
			// TODO: 可能多个address
			values.clear();
			values.put("c_id", id);
			values.put("address", item.getAddress());
			db.insert(ADDRESS_TABLE, null, values);
			
			// 插入tag表
			ArrayList<String> list = item.getLabels();
			if (list != null) {
				for (String l : list) {
					values.clear();
					values.put("c_id", id);
					values.put("tag", l);
					db.insert(TAG_TABLE, null, values);
				}
			}
			db.setTransactionSuccessful();
		} catch (SQLException e) {
			e.printStackTrace();
			Log.i("err", "data insert failed.");
		} finally {
			item.setId(id);
			id++;
			db.endTransaction();
		}
		Editor editor = preferences.edit();
		editor.putInt("id_count", id);
		editor.commit();
	}
	
	/*
	 * 通过联系人标识号删除一条联系人记录
	 * @parameter id， 联系人标识号
	 */
	public void delete(int id)
	{			
		try {
			// 打开外键约束，确保级联删除，据说Android2.2后才支持
			db.execSQL("PRAGMA foreign_keys=ON");
			db.delete(MAIN_TABLE, "c_id = ?", new String[]{ Integer.toString(id) } );
		} catch (SQLException e) {
			e.printStackTrace();
			Log.i("err", "delete based on id failed.");
		}
	}
	
	/*
	 * 通过联系人标识号更新记录
	 * 通过传入的item.id识别需要删除的行
	 * FIXME: 目前只能全部列一起更新，可能效率低，需要重载更多参数的版本，指定条目更新
	 * FIXME: 电话可能也需要加入id，否则不知道旧号码是否要删除
	 */
	public void update(ContactItem item)
	{		
		ContentValues values = new ContentValues();
		db.beginTransaction();
		try {
			// 更新main表
			values.put("name", item.getName());
			values.put("fullPinyin", item.getFullPinyin());
			values.put("simplePinyin", item.getSimplePinyin());
			values.put("sortLetter", item.getSortLetter());
			values.put("note", item.getNote());	
			db.update(MAIN_TABLE, values, "c_id = ?", 
					new String[]{Integer.toString(item.getId())});
			
			// 更新phoneNum表，就是插入新行
			values.clear();
			values.put("c_id", item.getId());
			values.put("phoneNumber", item.getPhoneNumber());
			db.insert(PHONE_TABLE, null, values);
			
			// 更新address表
			values.clear();
			values.put("c_id", item.getId());
			values.put("address", item.getAddress());
			db.update(ADDRESS_TABLE, values, "c_id = ?", 
					new String[]{Integer.toString(item.getId())});
			
			// 插入tag表
			ArrayList<String> list = item.getLabels();
			if (list != null) {
				for (String l : list) {
					values.clear();
					values.put("c_id", item.getId());
					values.put("tag", l);
					db.insert(TAG_TABLE, null, values);
				}
			}
			db.setTransactionSuccessful();
		} catch (SQLException e) {
			e.printStackTrace();
			Log.i("err", "data update failed.");
		} finally
		{
			db.endTransaction();
		}
	}
	
	/*
	 * 根据姓名来搜索
	 * @TODO：提供更多搜索接口
	 */
	public ArrayList<ContactItem> query(String name)
	{
		ArrayList<ContactItem> list = null;
		try {
			String table = MAIN_TABLE + " left outer join " + PHONE_TABLE + 
					" on " + MAIN_TABLE + ".c_id = " + PHONE_TABLE + ".c_id" +
					" left outer join " + ADDRESS_TABLE + 
					" on " + MAIN_TABLE + ".c_id = " + ADDRESS_TABLE + ".c_id" +
					" left outer join " + TAG_TABLE +
					" on " + MAIN_TABLE + ".c_id = " + TAG_TABLE + ".c_id";
			Cursor cursor = db.query(table, new String[] {"*"}, "name=?", 
					new String[] {name}, null, null, null);
			Log.i("Cursor", Integer.toString(cursor.getCount()));
			list = convertCursorToItem(cursor);
		} catch (SQLException e) {
			e.printStackTrace();
			Log.i("err", "query by name failed.");
		}		
		return list;
	}
	
	/*
	 * 提供直接执行sql语句的接口，仅供调试用
	 * @Deprecated
	 */	
	public void execSQL(String sql)
	{
		db = helper.getWritableDatabase();
		db.execSQL(sql);
	}
	
	/*
	 * 释放资源，应在Activity onDestory时调用
	 */
	public void close()
	{
		db.close();
		helper.close();
	}
	
	/*
	 * 将查询结果cursor转化为列表，方便输出
	 */	
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
				
		return list;
	}
}