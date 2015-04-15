package com.wecall.contacts.database;

import java.util.ArrayList;
import java.util.List;
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
	public int addContact(ContactItem item)
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
			
			id++;
			Editor editor = preferences.edit();
			editor.putInt("id_count", id);
			editor.commit();
			return id;
		} catch (SQLException e) {
			e.printStackTrace();
			Log.i("err", "data insert failed.");
		} finally {
			db.endTransaction();
		}
		return -1;
	}
	
	public void addContacts(List<ContactItem> list)
	{
		for(ContactItem item: list)
		{
			addContact(item);
		}
	}
	
	/*
	 * 插入单个新tag，必须保证与原有不重复
	 */
	public void addTagById(int id, String tag)
	{
		if( !isId(id) )
			return;
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
	public ArrayList<ContactItem> queryAllContact()
	{
		ArrayList<ContactItem> list = new ArrayList<ContactItem>();
		Cursor main_cursor = null;

		try{
			// 先在main表搜不同id的所有
			main_cursor = db.query(Constants.MAIN_TABLE_NAME, new String[] {"*"}, 
					null, null, null, null, null);
			while(main_cursor.moveToNext())
			{
				ContactItem it = new ContactItem();
				it.setId(main_cursor.getInt(Constants.MAIN_COL_CID));
				it.setName(main_cursor.getString(Constants.MAIN_COL_NAME));
				it.setNote(main_cursor.getString(Constants.MAIN_COL_NOTE));				
				
				it.setLabels(queryTagById(it.getId()));
				
				// TODO 对应每个id搜multi表
				
				list.add(it);
			}		
			main_cursor.close();
		} catch (SQLException se) {
			se.printStackTrace();
			Log.i("err", "selectAll failed.");
		}
				
		return list;
	}
	
	public ContactItem queryContactById(int id)
	{
		ContactItem item = new ContactItem();
		try
		{
			Cursor cursor = db.query(Constants.MAIN_TABLE_NAME, new String[] {"*"}, 
					"c_id = ?", new String[] {id+""}, null, null, null);
			item.setId(id);
			item.setName(cursor.getString(Constants.MAIN_COL_CID));
			item.setName(cursor.getString(Constants.MAIN_COL_NOTE));
			cursor.close();
			
			item.setLabels(queryTagById(id));
			
			// TODO 对应每个id搜multi表
		} catch(Exception e)
		{
			e.printStackTrace();
			Log.i("err", "queryContactById error.");
		}

		return item;
	}
	
	/*
	 * 通过联系人标识号删除一条联系人记录
	 */
	public void deleteContact(int id)
	{			
		if( !isId(id) )
			return;
		try {
			// 打开外键约束，确保级联删除，据说Android2.2后才支持
			db.execSQL("PRAGMA foreign_keys=ON");
			db.delete(Constants.MAIN_TABLE_NAME, "c_id = ?", new String[]{ id + "" } );
		} catch (SQLException e) {
			e.printStackTrace();
			Log.i("err", "delete based on id failed.");
		}
	}
	
	/*
	 * 根据id更新tag表
	 */
	public void updateTagById(int id, ArrayList<String> tags)
	{		
		// 检查是否传入合法id
		if( !isId(id) )
			return;
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
	 * 根据id更新一个联系人的全部记录
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
	
	public void printToLog()
	{
		ArrayList<ContactItem> list = queryAllContact();
		
	}
	
	/*
	 * 判断是否合法的id
	 */
	private boolean isId(int id)
	{
		if(id <= 0)
		{
			Log.i("err", "ilegal id.");
			return false;
		}
		return true;
	}
	
	/*
	 * 根据id查询对应的所有tag
	 */
	private ArrayList<String> queryTagById(int id) throws SQLException
	{		
		// 对应每个id搜tag表
		Cursor tag_cursor = db.query(Constants.TAG_TABLE_NAME, new String[] {"*"}, 
				"c_id=?", new String[] {id + ""}, null, null, null);
		
		ArrayList<String> labels = new ArrayList<String>();
		while(tag_cursor.moveToNext())
		{
			labels.add(tag_cursor.getString(Constants.TAG_COL_TAG));
		}
		tag_cursor.close();
		return labels;
	}
	
	/*
	 * 根据id更新Main表
	 */
	private void updateBasicById(ContactItem item) throws SQLException
	{
		// 检查是否传入合法id
		if( !isId(item.getId()) )
			return;
		ContentValues values = new ContentValues();
		values.put("name", item.getName());
		values.put("fullPinyin", item.getFullPinyin());
		values.put("simplePinyin", item.getSimplePinyin());
		values.put("sortLetter", item.getSortLetter());
		values.put("note", item.getNote());	
		db.update(Constants.MAIN_TABLE_NAME, values, "c_id=?", 
				new String[]{ item.getId() + "" });
	}
}
