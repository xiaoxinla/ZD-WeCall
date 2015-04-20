package com.wecall.contacts.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import com.wecall.contacts.entity.ContactItem;
import com.wecall.contacts.entity.Label;

import android.util.Log;
import com.wecall.contacts.constants.Constants;

/**
 * 管理数据库类，隐藏SQL语句细节，提供插入删除查询接口
 * 
 * @author KM
 */

public class DatabaseManager {
	
	private SQLiteDatabase db = null;
	private DatabaseHelper mHelper = null;
	// 为联系人分配的唯一标识
	private static int id;
	// id保存到preference中
	private static SharedPreferences preferences;
	
	public DatabaseManager(Context context)
	{
		mHelper = new DatabaseHelper(context);
		db = mHelper.getWritableDatabase();
		// 通过shared preference初始化id值
		preferences = context.getSharedPreferences("database", Context.MODE_PRIVATE);
		id = preferences.getInt("id_count", 1);
		
//		// 加入测试数据
//		ArrayList<ContactItem> list = queryAllContact();
//		Log.i("databaseManager", list.size() + "");
//		if (list.size() == 0)
//		{
//			list = new ArrayList<ContactItem>();
//			
//			ContactItem item = new ContactItem();
//			item.setName("xiaoxinla");
//			item.setPhoneNumber("123123123");
//			item.setNote("Group leader");
//			item.setAddress("China GD GZ");
//			ArrayList<Label> labels = new ArrayList<Label>();
//			labels.add(new Label("Tall"));
//			labels.add(new Label("Rich"));
//			labels.add(new Label("Handsome"));		
//			item.setLabels(labels);
//			for(int i = 0; i < 500; i++)
//				list.add(item);
//			
//			addContacts(list);
//		}
	}
	
	/**
	 * 向数据库插入新的一个联系人
	 * 如果插入成功会返回数据库对应的id.
	 */
	public int addContact(ContactItem item)
	{		
		try {
			// 插入main表
			ContentValues values = new ContentValues();
			values.put(Constants.MAIN_COL_CID, id);
			values.put(Constants.MAIN_COL_NAME, item.getName());
			values.put(Constants.MAIN_COL_FULL_PINYIN, item.getFullPinyin());
			values.put(Constants.MAIN_COL_SIM_PINYIN, item.getSimplePinyin());
			values.put(Constants.MAIN_COL_NOTE, item.getNote());
			values.put(Constants.MAIN_COl_PHONE, item.getPhoneNumber());
			values.put(Constants.MAIN_COL_ADDRESS, item.getAddress());
			db.insert(Constants.MAIN_TABLE_NAME, null, values);
			
			// 插入tag表
			ArrayList<Label> list = item.getLabels();
			if (list != null) {
				for (Label label : list) {
					values.clear();
					values.put(Constants.TAG_COL_CID, id);
					values.put(Constants.TAG_COL_TAG, label.getLname());
					values.put(Constants.TAG_COL_TAG_FULLPY, label.getLabelFullPinyin());
					values.put(Constants.TAG_COL_TAG_SIM_PINYIN, label.getLabelSimplePinyin());
					db.insert(Constants.TAG_TABLE_NAME, null, values);
				}
			}		
			
			id++;
			Editor editor = preferences.edit();
			editor.putInt("id_count", id);
			editor.commit();
			return id;
		} catch (SQLException e) {
			e.printStackTrace();
			Log.e("err", "data insert failed.");
		}
		return -1;
	}
	
	/**
	 * 批量导入联系人
	 * @param list
	 */
	public void addContacts(List<ContactItem> list)
	{
		db.beginTransaction();
		try
		{
			for(ContactItem item: list)
			{
				addContact(item);
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
	
	/**
	 * 插入单个新tag，必须保证与原有不重复
	 */
	public void addTagById(int id, String tag)
	{
		if( !isId(id) )
			return;
		try
		{
			ContentValues values = new ContentValues();
			values.put(Constants.TAG_COL_CID, id);
			values.put(Constants.TAG_COL_TAG, tag);
			db.insert(Constants.TAG_TABLE_NAME, null, values);
		} catch(SQLException se) {
			se.printStackTrace();
			Log.i("err", "insertTagById failed.");
		}
	}

	/**
	 * 取数据库全部内容
	 */
	public ArrayList<ContactItem> queryAllContact()
	{
		ArrayList<ContactItem> list = new ArrayList<ContactItem>();
		Cursor cursor = null;

		try{
			// 先在main表搜不同id的所有
			cursor = db.query(Constants.MAIN_TABLE_NAME, new String[] {"*"}, 
					null, null, null, null, null);
			int idIndex = cursor.getColumnIndex(Constants.MAIN_COL_CID);
			int nameIndex = cursor.getColumnIndex(Constants.MAIN_COL_NAME);
			int noteIndex = cursor.getColumnIndex(Constants.MAIN_COL_NOTE);
			int phoneIndex = cursor.getColumnIndex(Constants.MAIN_COl_PHONE);
			int addressIndex = cursor.getColumnIndex(Constants.MAIN_COL_ADDRESS);
			try
			{
				while(cursor.moveToNext())
				{
					ContactItem item = new ContactItem();
					item.setId(cursor.getInt(idIndex));
					item.setName(cursor.getString(nameIndex));
					item.setNote(cursor.getString(noteIndex));	
					item.setPhoneNumber(cursor.getString(phoneIndex));
					item.setAddress(cursor.getString(addressIndex));
					
					item.setLabels(queryTagById(item.getId()));
					
					list.add(item);
				}
			} finally{ 
				cursor.close();
			}
		} catch (SQLException se) {
			se.printStackTrace();
			Log.e("err", "selectAll failed.");
		}	
		return list;
	}
	
	/**
	 * 通过id搜单个联系人
	 * @param id
	 * @return
	 */
	public ContactItem queryContactById(int id)
	{
		if ( !isId(id) )
			return null;
		ContactItem item = new ContactItem();
		try
		{
			Cursor cursor = db.query(Constants.MAIN_TABLE_NAME, new String[] {"*"}, 
					Constants.MAIN_COL_CID + "=?", new String[] {id+""}, null, null, null);
			int nameIndex = cursor.getColumnIndex(Constants.MAIN_COL_NAME);
			int noteIndex = cursor.getColumnIndex(Constants.MAIN_COL_NOTE);
			int idIndex = cursor.getColumnIndex(Constants.MAIN_COL_CID);
			int phoneIndex = cursor.getColumnIndex(Constants.MAIN_COl_PHONE);
			int addressIndex = cursor.getColumnIndex(Constants.MAIN_COL_ADDRESS);
			try
			{
				if(cursor.moveToFirst())
				{
					item.setId(cursor.getInt(idIndex));
					item.setName(cursor.getString(nameIndex));
					item.setNote(cursor.getString(noteIndex));
					item.setPhoneNumber(cursor.getString(phoneIndex));
					item.setAddress(cursor.getString(addressIndex));
					Log.e("Contact", item+"");
				}
			} finally {
				cursor.close();
			}
				
			item.setLabels(queryTagById(id));
			
		} catch(Exception e)
		{
			e.printStackTrace();
			Log.e("err", "queryContactById error.");
		}

		return item;
	}
	
	/**
	 * 通过tag名称搜联系人
	 */
	public ArrayList<ContactItem> queryContactByTag(String tagName)
	{
		ArrayList<ContactItem> list = new ArrayList<ContactItem>();
		
		Cursor cursor = db.query(true, Constants.TAG_TABLE_NAME, new String[] {"*"},
				Constants.TAG_COL_TAG + "=?", new String[] {tagName}, null, null, null, null);
		int idIndex = cursor.getColumnIndex(Constants.TAG_COL_CID);
		
		try
		{
			while(cursor.moveToNext())
			{
				int id = cursor.getInt(idIndex);
				ContactItem item = queryContactById(id);
				list.add(item);
			}
		} finally
		{
			cursor.close();
		}
		
		return list;
	}
	
	/**
	 * 通过联系人标识号删除一条联系人记录
	 */
	public void deleteContactById(int id)
	{			
		if( !isId(id) )
			return;
		try {
			// 打开外键约束，确保级联删除，据说Android2.2后才支持
			db.execSQL("PRAGMA foreign_keys=ON");
			db.delete(Constants.MAIN_TABLE_NAME, Constants.MAIN_COL_CID + " = ?", new String[]{ id + "" } );
		} catch (SQLException e) {
			e.printStackTrace();
			Log.e("err", "delete based on id failed.");
		}
	}
	
	/**
	 * 根据id更新tag表
	 */
	public void updateTagById(int id, ArrayList<Label> labels)
	{		
		// 检查参数是否合法
		if( !isId(id) || labels == null)
			return;
		// 先删掉与当前id关联的所有tag
		db.delete(Constants.TAG_TABLE_NAME, Constants.TAG_COL_CID + "= ?", new String[] {id + ""});
		// 再插入全部新的tag
		for(Label s: labels)
		{
			ContentValues values = new ContentValues();
			values.put(Constants.TAG_COL_CID, id + "");
			values.put(Constants.TAG_COL_TAG, s.getLname());
			values.put(Constants.TAG_COL_TAG_FULLPY, s.getLabelFullPinyin());
			values.put(Constants.TAG_COL_TAG_SIM_PINYIN, s.getLabelSimplePinyin());
			db.insert(Constants.TAG_TABLE_NAME, null, values);
		}
	}
	
	/**
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
						
			db.setTransactionSuccessful();
		} catch (SQLException e) {
			e.printStackTrace();
			Log.e("err", "data update failed.");
		} finally
		{
			db.endTransaction();
		}
	}

	/**
	 * 提供直接执行sql语句的接口，仅供调试用
	 * @Deprecated
	 */	
	public void execSQL(String sql)
	{
		db = mHelper.getWritableDatabase();
		db.execSQL(sql);
	}
	
	/**
	 * 释放资源，应在Activity onDestory时调用
	 */
	public void close()
	{
		if (mHelper != null)
			mHelper.close();
		if (db != null)
			db.close();
	}
	
	/**
	 * 判断是否合法的id
	 */
	private boolean isId(int id)
	{
		if(id <= 0)
		{
			Log.e("err", "ilegal id.");
			return false;
		}
		return true;
	}
	
	/**
	 * 根据id查询对应的所有tag
	 */
	public ArrayList<Label> queryTagById(int id) throws SQLException
	{		
		// 对应每个id搜tag表
		Cursor tag_cursor = db.query(Constants.TAG_TABLE_NAME, new String[] {"*"}, 
				Constants.MAIN_COL_CID + "=?", new String[] {id + ""}, null, null, null);
		
		ArrayList<Label> labels = new ArrayList<Label>();
		int tagIndex = tag_cursor.getColumnIndex(Constants.TAG_COL_TAG);
		try {
			while(tag_cursor.moveToNext())
			{
				Label label = new Label(tag_cursor.getString(tagIndex));				
				labels.add(label);
			}
		} finally {
			tag_cursor.close();
		}
		return labels;
	}
	
	/**
	 * 获取所有现存标签
	 */
	public ArrayList<Label> queryAllTags()
	{
		ArrayList<Label> labels = new ArrayList<Label>();
		
		Cursor cursor = db.query(true, Constants.TAG_TABLE_NAME, new String[] {"*"}, 
				null, null, null, null, null, null);
		int tagNameIndex = cursor.getColumnIndex(Constants.TAG_COL_TAG);
		
		try {
			while(cursor.moveToNext())
			{
				Label label = new Label();
				label.setLname(cursor.getString(tagNameIndex));
				labels.add(label);
			}
		} finally {
			cursor.close();
		}
		
		return labels;
	}
	
	/**
	 * 删除所有联系人的某个标签
	 * @param tagName
	 */
	public void deleteTagForAllContact(String tagName)
	{
		try {
			db.delete(Constants.TAG_TABLE_NAME, Constants.TAG_COL_TAG + "=?", 
					new String[] {tagName});
		} catch (SQLException se) {
			se.printStackTrace();
			Log.e("err", "deleteTagForAllContact failed.");
		}
	}

	
	/**
	 * 根据id更新Main表
	 */
	private void updateBasicById(ContactItem item) throws SQLException
	{
		// 检查是否传入合法id
		if( item == null || !isId(item.getId()) )
			return;
		ContentValues values = new ContentValues();
		values.put(Constants.MAIN_COL_NAME, item.getName());
		values.put(Constants.MAIN_COL_FULL_PINYIN, item.getFullPinyin());
		values.put(Constants.MAIN_COL_SIM_PINYIN, item.getSimplePinyin());
		values.put(Constants.MAIN_COL_NOTE, item.getNote());
		values.put(Constants.MAIN_COl_PHONE, item.getPhoneNumber());
		values.put(Constants.MAIN_COL_ADDRESS, item.getAddress());
		db.update(Constants.MAIN_TABLE_NAME, values, "c_id=?", 
				new String[]{ item.getId() + "" });
	}
}
