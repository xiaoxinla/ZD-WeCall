package com.wecall.contacts.database;

import java.util.ArrayList;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import com.wecall.contacts.entity.ContactItem;
import android.util.Log;
import com.wecall.contacts.constants.Constants;

/*
 * 管理数据库类，隐藏SQL语句细节，提供插入删除查询接口
 * 
 * @author KM
 */

public class DatabaseManager {
	
	private SQLiteDatabase db = null;
	private DatabaseHelper helper = null;
	// 为联系人分配的唯一标识
	private static int id;
	// id保存到preference中
	private static SharedPreferences preferences;
	
	public DatabaseManager(Context context)
	{
		helper = new DatabaseHelper(context);
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
	public void insertContact(ContactItem item)
	{		
		db.beginTransaction();
		try {
			// 插入main表
			ContentValues values = new ContentValues();
			values.put("c_id", id);
			values.put("name", item.getName());
			values.put("fullPinyin", item.getFullPinyin());
			values.put("simplePinyin", item.getSimplePinyin());
			values.put("sortLetter", item.getSortLetter());
			values.put("note", item.getNote());		
			db.insert(Constants.MAIN_TABLE_NAME, null, values);
			
			// 插入tag表
			ArrayList<String> list = item.getLabels();
			if (list != null) {
				for (String l : list) {
					values.clear();
					values.put("c_id", id);
					values.put("tag", l);
					db.insert(Constants.TAG_TABLE_NAME, null, values);
				}
			}
			
			// TODO:插入multi表
			
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
	 * 插入单个新tag，必须保证与原有不重复
	 */
	public void insertTagById(int id, String tag)
	{
		try
		{
			ContentValues values = new ContentValues();
			values.put("c_id", id);
			values.put("tag", tag);
			db.insert(Constants.TAG_TABLE_NAME, null, values);
		} catch(SQLException se) {
			se.printStackTrace();
			Log.i("err", "insertTagById failed.");
		}
	}

	/*
	 * 取数据库全部内容
	 */
	public ArrayList<ContactItem> selectAll()
	{
		ArrayList<ContactItem> list = new ArrayList<ContactItem>();
		Cursor main_cursor = null;
		Cursor tag_cursor = null;
		Cursor multi_cursor = null;

		try{
			main_cursor = db.query(Constants.MAIN_TABLE_NAME, new String[] {"*"}, 
					null, null, null, null, null);
			while(main_cursor.moveToNext())
			{
				ContactItem it = new ContactItem();
				it.setId(main_cursor.getInt(Constants.MAIN_COL_CID));
				it.setName(main_cursor.getString(Constants.MAIN_COL_NAME));
				it.setNote(main_cursor.getString(Constants.MAIN_COL_NOTE));				
				
				// 搜tag表
				tag_cursor = db.query(Constants.TAG_TABLE_NAME, new String[] {"*"}, 
						"c_id=?", new String[] {it.getId() + ""}, null, null, null);
				
				ArrayList<String> labels = new ArrayList<String>();
				while(tag_cursor.moveToNext())
				{
					labels.add(tag_cursor.getString(Constants.TAG_COL_TAG));
				}
				it.setLabels(labels);
				tag_cursor.close();
				
				// TODO 增加多值表其他属性的搜索
//				multi_cursor = db.query(Constants.MULTI_TABLE_NAME, new String[] {"*"}, 
//						"c_id=?", new String[] {it.getId() + ""}, null, null, null);
//				while(tag_cursor.moveToNext())
//				{
//					
//				}
				
				list.add(it);
			}		
			main_cursor.close();
		} catch (SQLException se) {
			se.printStackTrace();
			Log.i("err", "selectAll failed.");
		}
				
		return list;
	}
	
	/*
	 * 通过联系人标识号删除一条联系人记录
	 * @parameter id， 联系人标识号
	 */
	public void deleteById(int id)
	{			
		try {
			// 打开外键约束，确保级联删除，据说Android2.2后才支持
			db.execSQL("PRAGMA foreign_keys=ON");
			db.delete(Constants.MAIN_TABLE_NAME, "c_id = ?", new String[]{ id + "" } );
		} catch (SQLException e) {
			e.printStackTrace();
			Log.i("err", "delete based on id failed.");
		}
	}
	
	private void updateBasicById(ContactItem item)
	{
		// 检查是否传入合法id
		if(item.getId() <= 0)
		{
			Log.i("err", "updateBasicById: id illegal.");
			return;
		}
		try {
			ContentValues values = new ContentValues();
			values.put("name", item.getName());
			values.put("fullPinyin", item.getFullPinyin());
			values.put("simplePinyin", item.getSimplePinyin());
			values.put("sortLetter", item.getSortLetter());
			values.put("note", item.getNote());	
			db.update(Constants.MAIN_TABLE_NAME, values, "c_id=?", 
					new String[]{ item.getId() + "" });
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.i("err", "updateBasicById failed.");
		}
	}
	
	public void updateTagById(int id, ArrayList<String> tags)
	{		
		// 检查是否传入合法id
		if(id <= 0)
		{
			Log.i("err", "updateTagById: id illegal.");
			return;
		}
		// 先删掉与当前id关联的所有tag
		db.delete(Constants.TAG_TABLE_NAME, "c_id = ?", new String[] {id + ""});
		// 再插入全部新的tag
		for(String s: tags)
		{
			ContentValues values = new ContentValues();
			values.put("c_id", id + "");
			values.put("tag", s);
			db.insert(Constants.TAG_TABLE_NAME, null, values);
		}
	}
	
	/*
	 * 通过联系人标识号更新记录
	 */
	public void updateContact(ContactItem item)
	{		
		db.beginTransaction();
		try {
			// 更新main表
			updateBasicById(item);
						
			// 更新tag表
			updateTagById(item.getId(), item.getLabels());
			
			// TODO 更新multi表
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
}
