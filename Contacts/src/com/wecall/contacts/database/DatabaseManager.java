package com.wecall.contacts.database;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.lucene.util.WeCallAnalyzer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.chenlb.mmseg4j.analysis.ComplexAnalyzer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wecall.contacts.constants.Constants;
import com.wecall.contacts.entity.ContactItem;
import com.wecall.contacts.entity.SimpleContact;
import com.wecall.contacts.util.PinYin;

/**
 * 管理数据库类，隐藏SQL语句细节，提供插入删除查询接口
 * 
 * @author KM
 */

public class DatabaseManager {

	private static final String LOG_TAG = "DatabaseHelper Error";
	private static Gson gson = new Gson();
	private static Analyzer analyzer = null;

	private DatabaseHelper mHelper;

	public DatabaseManager(Context context) {
		mHelper = new DatabaseHelper(context);
		if (gson == null)
			gson = new Gson();

	}

	/**
	 * 向数据库插入新的一个联系人 如果插入成功会返回数据库对应的id.
	 */
	public int addContact(ContactItem item) {
		SQLiteDatabase db = mHelper.getWritableDatabase();
		int id = addContact(item, db);
		db.close();
		return id;
	}

	/**
	 * 重载版本，可以指定db的参数
	 */
	private int addContact(ContactItem item, SQLiteDatabase db) {
		try {
			// 插入main表
			ContentValues values = new ContentValues();
			values.put(Constants.MAIN_COL_NAME, item.getName());
			values.put(Constants.MAIN_COL_NOTE, item.getNote());
			values.put(Constants.MAIN_COL_ADDRESS, item.getAddress());
			// 是其他类型的域先转换为json
			values.put(Constants.MAIN_COl_PHONE,
					gson.toJson(item.getPhoneNumber()));
			values.put(Constants.MAIN_COL_TAG, gson.toJson(item.getLabels()));
			// 未来使用,先留空
			values.put(Constants.MAIN_COL_OTHER, "");
			// 得到插入到的数据库列数，即id
			int id = (int) db.insert(Constants.TABLE_NAME_MAIN, null, values);
			item.setId(id);

			// 插入search表
			addContactToSearch(db, item);

			values = null;
			return id;
		} catch (SQLException e) {
			e.printStackTrace();
			Log.e(LOG_TAG, "data insert failed.");
		}
		return -1;
	}

	/**
	 * 批量导入联系人
	 * 
	 * @param list
	 */
	public void addContacts(List<ContactItem> list) {
		SQLiteDatabase db = mHelper.getWritableDatabase();
		db.beginTransaction();
		try {
			for (ContactItem item : list) {
				addContact(item, db);
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		// 优化b树的指令
		db.execSQL("INSERT INTO " + Constants.TABLE_NAME_SEARCH + "("
				+ Constants.TABLE_NAME_SEARCH + ")" + " VALUES('optimize');");
		db.close();
	}

	/**
	 * 通过联系人标识号删除一条联系人记录
	 */
	public void deleteContactById(int id) {
		if (!isId(id))
			return;
		SQLiteDatabase db = mHelper.getWritableDatabase();
		try {
			db.delete(Constants.TABLE_NAME_MAIN, Constants.MAIN_COL_CID + "=?",
					new String[] { id + "" });
			db.execSQL("DELETE FROM " + Constants.TABLE_NAME_SEARCH + " WHERE "
					+ Constants.SEARCH_COL_CID + "=?;", new Object[] { id });
		} catch (SQLException e) {
			e.printStackTrace();
			Log.e(LOG_TAG, "delete based on id failed.");
		}
		db.close();
	}

	/**
	 * 取数据库全部行的名字和id列
	 * 
	 * @return
	 */
	public List<SimpleContact> queryAllContacts() {
		ArrayList<SimpleContact> list = new ArrayList<SimpleContact>();
		SQLiteDatabase db = mHelper.getReadableDatabase();
		Cursor cursor = null;

		try {
			// 先在main表搜不同id的所有列
			cursor = db.query(Constants.TABLE_NAME_MAIN, new String[] {
					Constants.MAIN_COL_CID, Constants.MAIN_COL_NAME }, null,
					null, null, null, null);
			int idIndex = cursor.getColumnIndex(Constants.MAIN_COL_CID);
			int nameIndex = cursor.getColumnIndex(Constants.MAIN_COL_NAME);

			while (cursor.moveToNext()) {
				SimpleContact item = new SimpleContact();
				item.setId(cursor.getInt(idIndex));
				item.setName(cursor.getString(nameIndex));
				list.add(item);
			}
			cursor.close();

		} catch (SQLException se) {
			se.printStackTrace();
			Log.e(LOG_TAG, "selectAll failed.");
		}
		db.close();
		return list;
	}

	/**
	 * 通过id搜单个联系人，不存在返回null
	 * 
	 * @param id
	 * @return
	 */
	public ContactItem queryContactById(int id) {
		if (!isId(id))
			return null;
		ContactItem item = new ContactItem();
		SQLiteDatabase db = mHelper.getReadableDatabase();

		try {
			Cursor cursor = db.query(Constants.TABLE_NAME_MAIN,
					new String[] { "*" }, Constants.MAIN_COL_CID + " = ? ",
					new String[] { id + "" }, null, null, null);
			int nameIndex = cursor.getColumnIndex(Constants.MAIN_COL_NAME);
			int noteIndex = cursor.getColumnIndex(Constants.MAIN_COL_NOTE);
			int phoneIndex = cursor.getColumnIndex(Constants.MAIN_COl_PHONE);
			int addressIndex = cursor
					.getColumnIndex(Constants.MAIN_COL_ADDRESS);
			int tagIndex = cursor.getColumnIndex(Constants.MAIN_COL_TAG);
			// 未来使用,留空
			// int otherIndex = cursor.getColumnIndex(Constants.MAIN_COL_OTHER);

			// 如果找到，取出内容，否则返回null
			if (cursor.moveToFirst()) {
				item.setId(id);
				item.setName(cursor.getString(nameIndex));
				item.setNote(cursor.getString(noteIndex));
				item.setAddress(cursor.getString(addressIndex));

				HashSet<String> phoneList = gson.fromJson(
						cursor.getString(phoneIndex),
						new TypeToken<HashSet<String>>() {
						}.getType());
				item.setPhoneNumber(phoneList);

				HashSet<String> tagList = gson.fromJson(
						cursor.getString(tagIndex),
						new TypeToken<HashSet<String>>() {
						}.getType());
				item.setLabels(tagList);

			} else {
				item = null;
			}
			cursor.close();

		} catch (Exception e) {
			e.printStackTrace();
			Log.e(LOG_TAG, "queryContactById error.");
		}
		db.close();
		return item;
	}

	/**
	 * 返回所有现存标签
	 * 
	 * @return
	 */
	public Set<String> queryAllTags() {
		Set<String> labels = new HashSet<String>();
		SQLiteDatabase db = mHelper.getReadableDatabase();

		Cursor cursor = db.query(Constants.TABLE_NAME_TAG,
				new String[] { "*" }, null, null, null, null, null, null);
		int index = cursor.getColumnIndex(Constants.TAG_COL_TAG_NAME);

		try {
			while (cursor.moveToNext()) {
				String tag = cursor.getString(index);
				if(tag!=null&&!tag.isEmpty()){
					labels.add(tag);
				}
			}
		} catch (SQLException se) {
			se.printStackTrace();
			Log.e(LOG_TAG, "queryAllTags failed");
		}

		cursor.close();
		db.close();
		return labels;
	}

	/**
	 * 增加一个不对应任何人的新标签
	 * 
	 * @param tagName
	 */
	public void addTag(String tagName) {
		SQLiteDatabase db = mHelper.getWritableDatabase();
		getTagId(db, tagName);
		db.close();
	}

	/**
	 * 通过tag名称搜联系人
	 * 
	 * @param String
	 *            标签名
	 * @return
	 */
	public List<SimpleContact> queryContactByTag(String tagName) {
		List<SimpleContact> list = new ArrayList<SimpleContact>();
		SQLiteDatabase db = mHelper.getReadableDatabase();

		int tid = getTagId(db, tagName);

		// 用了MATCH作全文搜索
		Cursor cursor = db.query(true, Constants.TABLE_NAME_SEARCH,
				new String[] { Constants.SEARCH_COL_CID },
				Constants.SEARCH_COL_DATA2 + " MATCH ?", new String[] { tid
						+ "" }, null, null, null, null);

		while (cursor.moveToNext()) {
			int id = cursor.getInt(0);
			// FIXME 用ContactItem构造SimpleContact
			ContactItem citem = queryContactById(id);
			SimpleContact sitem = new SimpleContact(citem);
			list.add(sitem);
		}
		cursor.close();

		db.close();
		return list;
	}

	/**
	 * 通过id查属于联系人的所有标签
	 * 
	 * @param cid
	 * @return
	 */
	public Set<String> queryTagsByContactId(int cid) {
		Set<String> tags = new HashSet<String>();
		SQLiteDatabase db = mHelper.getReadableDatabase();

		try {
			Cursor cursor = db.query(Constants.TABLE_NAME_MAIN,
					new String[] { Constants.MAIN_COL_TAG },
					Constants.MAIN_COL_CID + " = ? ",
					new String[] { cid + "" }, null, null, null, null);
			int index = cursor.getColumnIndex(Constants.MAIN_COL_TAG);

			if (cursor.moveToFirst()) {
				tags = gson.fromJson(cursor.getString(index),
						new TypeToken<HashSet<String>>() {
						}.getType());
			}
			cursor.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Log.e(LOG_TAG, "queryTagsByContactId error.");
		}

		db.close();
		return tags;
	}

	/**
	 * 更新联系人信息，通过ContactItem中的id指定特定联系人
	 * 
	 * @param item
	 */
	public void updateContact(ContactItem item) {
		SQLiteDatabase db = mHelper.getWritableDatabase();
		int id = item.getId();

		// 更新main表
		ContentValues values = new ContentValues();
		values.put(Constants.MAIN_COL_NAME, item.getName());
		values.put(Constants.MAIN_COL_NOTE, item.getNote());
		values.put(Constants.MAIN_COL_ADDRESS, item.getAddress());

		// 是其他类型的域先转换为json
		values.put(Constants.MAIN_COl_PHONE, gson.toJson(item.getPhoneNumber()));
		values.put(Constants.MAIN_COL_TAG, gson.toJson(item.getLabels()));
		// 未来使用,先留空
		values.put(Constants.MAIN_COL_OTHER, "");
		// 更新cid=item.id的列
		db.update(Constants.TABLE_NAME_MAIN, values, Constants.MAIN_COL_CID
				+ " = ?", new String[] { id + "" });

		// 更新search表

		// FIXME 先删除原有表内容
		db.delete(Constants.TABLE_NAME_SEARCH, Constants.SEARCH_COL_CID + " = "
				+ id, null);

		// 插入新内容
		addContactToSearch(db, item);

		values = null;

		db.close();
	}

	/**
	 * 为联系人批量加标签
	 * 
	 * @param tagName
	 *            要加的标签名
	 * @param cids
	 *            存id的数组
	 */
	public void addTagToIds(String tagName, Set<Integer> cids) {
		SQLiteDatabase db = mHelper.getWritableDatabase();
		int tid = getTagId(db, tagName);
		for (int id : cids) {
			if (!isId(id))
				continue;
			try {
				Cursor cursor = db.query(Constants.TABLE_NAME_MAIN,
						new String[] { Constants.MAIN_COL_TAG },
						Constants.MAIN_COL_CID + " = ?",
						new String[] { id + "" }, null, null, null);

				if (cursor.moveToFirst()) {
					HashSet<String> tags = gson.fromJson(cursor.getString(0),
							new TypeToken<HashSet<String>>() {
							}.getType());
					// 如果不存在，需要更新
					if (tags != null && !tags.contains(tagName)) {
						// 更新main表
						tags.add(tagName);
						ContentValues values = new ContentValues();
						values.put(Constants.MAIN_COL_TAG, gson.toJson(tags));

						db.update(Constants.TABLE_NAME_MAIN, values,
								Constants.MAIN_COL_CID + " = ?",
								new String[] { id + "" });

						// 更新search表
						values.clear();
						values.put(Constants.SEARCH_COL_CID, id);
						values.put(Constants.SEARCH_COL_TYPEID,
								Constants.TYPE_TAG);
						values.put(Constants.SEARCH_COL_DATA1, tagName);
						values.put(Constants.SEARCH_COL_DATA2, tid);
						db.insert(Constants.TABLE_NAME_SEARCH, null, values);
					}
				}
				cursor.close();

			} catch (SQLException se) {
				se.printStackTrace();
			}
		}

		db.close();
	}

	/**
	 * 更新标签对应的联系人
	 * 
	 * @param tagName
	 * @param cids
	 */
	public void updateTagByName(String tagName, Set<Integer> cids) {
		SQLiteDatabase db = mHelper.getWritableDatabase();

		int tid = getTagId(db, tagName);

		// 搜目前带tagName的所有id
		Cursor cursor = db.query(true, Constants.TABLE_NAME_SEARCH,
				new String[] { Constants.SEARCH_COL_CID },
				Constants.SEARCH_COL_DATA2 + " = " + tid, null, null, null,
				null, null);

		while (cursor.moveToNext()) {
			int cid = cursor.getInt(0);
			// 如果在list中，不必处理，从list中删除
			if (cids.contains(cid)) {
				cids.remove(cid);
			}
			// 不在列表中则需要为这个联系人删去这个标签
			else {
				// 从search表删
				db.delete(Constants.TABLE_NAME_SEARCH, Constants.SEARCH_COL_CID
						+ " = " + cid + " AND " + Constants.SEARCH_COL_DATA2
						+ " = " + tid, null);
				// 从main表删
				// 搜这个人现有tag
				Cursor searchCursor = db.query(Constants.TABLE_NAME_MAIN,
						new String[] { Constants.MAIN_COL_TAG },
						Constants.SEARCH_COL_CID + " = " + cid, null, null,
						null, null, null);
				if (searchCursor.moveToNext()) {
					HashSet<String> tags = gson.fromJson(
							searchCursor.getString(0),
							new TypeToken<HashSet<String>>() {
							}.getType());
					tags.remove(tagName);
					ContentValues values = new ContentValues();
					values.put(Constants.MAIN_COL_TAG, gson.toJson(tags));
					db.update(Constants.TABLE_NAME_MAIN, values,
							Constants.MAIN_COL_CID + " = " + cid, null);
				}
			}
		}

		// 为还未处理的ids加上这个标签
		addTagToIds(tagName, cids);

		cursor.close();
		db.close();
	}

	/**
	 * 更新一个联系人的所属标签
	 * 
	 * @param cid
	 * @param tagNames
	 */
	public void updateContactTags(int cid, Set<String> tagNames) {
		SQLiteDatabase db = mHelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(Constants.MAIN_COL_TAG, gson.toJson(tagNames));

		db.update(Constants.TABLE_NAME_MAIN, values, Constants.MAIN_COL_CID
				+ " = ?", new String[] { cid + "" });

		// FIXME 删掉原有的再重新插入
		db.delete(Constants.TABLE_NAME_SEARCH, Constants.SEARCH_COL_CID + " = "
				+ cid + " AND " + Constants.SEARCH_COL_TYPEID + " = "
				+ Constants.TYPE_TAG, null);

		Iterator<String> it = tagNames.iterator();
		while (it.hasNext()) {
			String tag = it.next();
			int tid = getTagId(db, tag);
			values.clear();
			values.put(Constants.SEARCH_COL_CID, cid);
			values.put(Constants.SEARCH_COL_TYPEID, Constants.TYPE_TAG);
			values.put(Constants.SEARCH_COL_DATA1, tag);
			values.put(Constants.SEARCH_COL_DATA2, tid);
			db.insert(Constants.TABLE_NAME_SEARCH, null, values);
		}

		db.close();
	}

	/**
	 * 删除一个标签，同时为所有联系人删除这个标签
	 * 
	 * @param tagName
	 */
	public void deleteTagByName(String tagName) {
		SQLiteDatabase db = mHelper.getWritableDatabase();

		db.delete(Constants.TABLE_NAME_TAG,
				Constants.TAG_COL_TAG_NAME + " = ?", new String[] { tagName });

		try {
			// 搜含有这个tag的所有id
			Cursor cursor = db.query(Constants.TABLE_NAME_SEARCH,
					new String[] { Constants.SEARCH_COL_CID },
					Constants.SEARCH_COL_TYPEID + " = " + Constants.TYPE_TAG
							+ " AND " + Constants.SEARCH_COL_DATA1 + " = "
							+ "'" + tagName + "'", null, null, null, null);

			// 为main表所有联系人删去这个tag
			while (cursor.moveToNext()) {
				int id = cursor.getInt(0);
				Cursor mainCursor = db.query(Constants.TABLE_NAME_MAIN,
						new String[] { Constants.MAIN_COL_TAG },
						Constants.MAIN_COL_CID + " = " + id, null, null, null,
						null);
				if (mainCursor.moveToFirst()) {
					// 取tag的集合，除去tagName，再更新表
					HashSet<String> tags = gson.fromJson(
							mainCursor.getString(0),
							new TypeToken<HashSet<String>>() {
							}.getType());
					tags.remove(tagName);
					ContentValues values = new ContentValues();
					values.put(Constants.MAIN_COL_TAG, gson.toJson(tags));
					db.update(Constants.TABLE_NAME_MAIN, values,
							Constants.MAIN_COL_CID + " = " + id, null);
				}
				mainCursor.close();
			}

			cursor.close();

			// 从search表中删有这个tag的项
			db.delete(Constants.TABLE_NAME_SEARCH, Constants.SEARCH_COL_DATA1
					+ " = '" + tagName + "' AND " + Constants.SEARCH_COL_TYPEID
					+ " = " + Constants.TYPE_TAG, null);

		} catch (Exception e) {
			e.printStackTrace();
			Log.e(LOG_TAG, "deleteTagByName failed");
		}

		db.close();
	}

	/**
	 * 判断是否合法的id
	 */
	private boolean isId(int id) {
		if (id <= 0) {
			Log.e(LOG_TAG, "ilegal id.");
			return false;
		}
		return true;
	}

	/**
	 * 获得tag的tid，如果不在表中则插入
	 * 
	 * @param db
	 * @param tag
	 * @return
	 */
	private int getTagId(SQLiteDatabase db, String tag) {
		int tid = 0;
		try {
			Cursor cursor = db.query(Constants.TABLE_NAME_TAG,
					new String[] { Constants.TAG_COL_TAG_ID },
					Constants.TAG_COL_TAG_NAME + " = ?", new String[] { tag },
					null, null, null);
			if (cursor.moveToFirst()) {
				tid = cursor.getInt(0);
				Log.i(LOG_TAG, "tag exist: " + tag);
			} else {
				// 插入tag表
				ContentValues tagValues = new ContentValues();
				tagValues.put(Constants.TAG_COL_TAG_NAME, tag);
				tid = (int) db
						.insert(Constants.TABLE_NAME_TAG, null, tagValues);
			}
			cursor.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Log.i(LOG_TAG, "getTagId error");
		}

		return tid;
	}

	/**
	 * 向search表插入联系人信息
	 * 
	 * @param db
	 * @param item
	 */
	private void addContactToSearch(SQLiteDatabase db, ContactItem item) {
		int id = item.getId();
		ContentValues values = new ContentValues();
		String input;
		List<String> token;
		// 插入search表
		// name
		if ((input = item.getName()) != null) {
			token = tokenizer(input);
			values.clear();
			values.put(Constants.SEARCH_COL_CID, id);
			values.put(Constants.SEARCH_COL_TYPEID, Constants.TYPE_NAME);
			values.put(Constants.SEARCH_COL_DATA1, listToString(token));
			values.put(Constants.SEARCH_COL_DATA2,
					listToString(toPinyin(token)));
			values.put(Constants.SEARCH_COL_DATA3,
					PinYin.getSimplePinYin(input));
			db.insert(Constants.TABLE_NAME_SEARCH, null, values);
		}

		// note
		if ((input = item.getNote()) != null) {
			token = tokenizer(input);
			values.clear();
			values.put(Constants.SEARCH_COL_CID, id);
			values.put(Constants.SEARCH_COL_TYPEID, Constants.TYPE_NOTE);
			values.put(Constants.SEARCH_COL_DATA1, listToString(token));
			db.insert(Constants.TABLE_NAME_SEARCH, null, values);
		}

		// address
		if ((input = item.getAddress()) != null) {
			token = tokenizer(input);
			values.clear();
			values.put(Constants.SEARCH_COL_CID, id);
			values.put(Constants.SEARCH_COL_TYPEID, Constants.TYPE_ADDRESS);
			values.put(Constants.SEARCH_COL_DATA1, listToString(token));
			db.insert(Constants.TABLE_NAME_SEARCH, null, values);
		}

		// phone
		Set<String> phoneSet = item.getPhoneNumber();
		if (!phoneSet.isEmpty()) {
			Iterator<String> phoneIt = phoneSet.iterator();
			while (phoneIt.hasNext()) {
				input = phoneIt.next();
				token = tokenizer(input);
				values.clear();
				values.put(Constants.SEARCH_COL_CID, id);
				values.put(Constants.SEARCH_COL_TYPEID, Constants.TYPE_PHONE);
				values.put(Constants.SEARCH_COL_DATA1, listToString(token));
				db.insert(Constants.TABLE_NAME_SEARCH, null, values);
			}
		}

		// tag
		Set<String> tagSet = item.getLabels();
		if (!tagSet.isEmpty()) {
			Iterator<String> tagIt = tagSet.iterator();
			while (tagIt.hasNext()) {
				input = tagIt.next();
				int tid = getTagId(db, input);
				token = tokenizer(input);
				values.clear();
				values.put(Constants.SEARCH_COL_CID, id);
				values.put(Constants.SEARCH_COL_TYPEID, Constants.TYPE_TAG);
				values.put(Constants.SEARCH_COL_DATA1, listToString(token));
				values.put(Constants.SEARCH_COL_DATA2, tid);
				db.insert(Constants.TABLE_NAME_SEARCH, null, values);
			}
		}
		input = null;
		values = null;
		token = null;
	}

	/**
	 * 全文搜索接口
	 * 
	 * @param text
	 * @return 
	 */
	public List< List<Object> > ftsSearch(String text) {
		List< List<Object> > ret = new ArrayList<List<Object>>();
		SQLiteDatabase db = mHelper.getWritableDatabase();
		Cursor cursor = null;
		
		if (isNumeric(text) ) {
			
			cursor = db.rawQuery("SELECT " + Constants.SEARCH_COL_CID
					+ " , " + Constants.SEARCH_COL_TYPEID
					+ " FROM " + Constants.TABLE_NAME_SEARCH + " WHERE "
					+ Constants.SEARCH_COL_DATA1 + " MATCH '" + text + "*';",
					null);
			
		} else if (isLetter(text)) {

			cursor = db.rawQuery("SELECT " + Constants.SEARCH_COL_CID
					+ " , " + Constants.SEARCH_COL_TYPEID
					+ " FROM " + Constants.TABLE_NAME_SEARCH + " WHERE "
					+ Constants.TABLE_NAME_SEARCH + " MATCH '" + text + "*';",
					null);			
			
		} else {
			List<String> token = tokenizer(text);
			String query = listToString(token);
			cursor = db.rawQuery("SELECT " + Constants.SEARCH_COL_CID
					+ " , " + Constants.SEARCH_COL_TYPEID + " FROM "
					+ Constants.TABLE_NAME_SEARCH + " WHERE "
					+ Constants.TABLE_NAME_SEARCH + " MATCH '" + query + "*';",
					null);
			
		}	
				
		if (cursor != null) {
			while (cursor.moveToNext()) {
				List<Object> item = new ArrayList<Object>();
				
				int id = cursor.getInt(0);
				Cursor mainCursor = db.rawQuery("SELECT * "
						+ " FROM " + Constants.TABLE_NAME_MAIN + " WHERE "
						+ Constants.MAIN_COL_CID + " = " + id, null);
				int nameIndex = mainCursor.getColumnIndex(Constants.MAIN_COL_NAME);
				int phoneIndex = mainCursor.getColumnIndex(Constants.MAIN_COl_PHONE);
				int addressIndex = mainCursor.getColumnIndex(Constants.MAIN_COL_ADDRESS);
				int tagIndex = mainCursor.getColumnIndex(Constants.MAIN_COL_TAG);
				int noteIndex = mainCursor.getColumnIndex(Constants.MAIN_COL_NOTE);

				if (mainCursor.moveToFirst()) {
					int typeId = cursor.getInt(1);
					item.add(id);
					item.add(mainCursor.getString(nameIndex));
					item.add(typeId);
					
					if (typeId == Constants.TYPE_NAME){
						item.add(mainCursor.getString(nameIndex));
						
					} else if (typeId == Constants.TYPE_NOTE) {
						item.add(mainCursor.getString(noteIndex));
						
					} else if (typeId == Constants.TYPE_PHONE) {
						String phone = mainCursor.getString(phoneIndex);
						HashSet<String> phoneList = gson.fromJson( phone,
								new TypeToken<HashSet<String>>() {
								}.getType());
						item.add(phoneList);
						
					} else if (typeId == Constants.TYPE_ADDRESS) {
						item.add(mainCursor.getString(addressIndex));
						
					} else if (typeId == Constants.TYPE_TAG) {
						String tag = mainCursor.getString(tagIndex);
						HashSet<String> tagList = gson.fromJson( tag,
								new TypeToken<HashSet<String>>() {
								}.getType());
						item.add(tagList);
					} 
				}

				ret.add(item);
				mainCursor.close();
			}

			cursor.close();
		}

		db.close();
		
		return ret;
	}

	/**
	 * 测试接口
	 * 
	 * @deprecated
	 */
	public void test() {
		
	}

	// 判断字符串是否纯数字
	private static boolean isNumeric(String str) {
		for (int i = 0; i < str.length(); i++) {
			// System.out.println(str.charAt(i));
			Log.v(LOG_TAG, "isNumeric:" + str.charAt(i));
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	// 判断字符串是否纯英文字母
	private static boolean isLetter(String str) {

		for (int i = 0; i < str.length(); i++) {
			// System.out.println(str.charAt(i));
			Log.v(LOG_TAG, "isLetter:" + str.charAt(i));
			char ch = str.charAt(i);
			if (!((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z'))) {
				return false;
			}
		}
		return true;
	}

	// 分词器，以列表返回分词后的结果
	private List<String> tokenizer(String str) {
		if (analyzer == null) {
			analyzer = new ComplexAnalyzer();
		}
		WeCallAnalyzer wca = new WeCallAnalyzer(analyzer);
		List<String> tokenList = wca.getTokenList(str);
		return tokenList;
	}

	// 将list中的没一项转为拼音
	private List<String> toPinyin(List<String> list) {
		List<String> newList = new ArrayList<String>();

		for (String term : list) {
			newList.add(PinYin.getPinYin(term));
		}

		return newList;
	}

	// 将list转为空格分割的字符串
	private String listToString(List<String> list) {
		StringBuffer strBuf = new StringBuffer();

		for (int i = 0; i < list.size(); i++) {
			strBuf.append(list.get(i));
			if (i < list.size() - 1)
				strBuf.append(' ');
		}

		return strBuf.toString();
	}	

	// ftsSearch的测试函数
	private void toLog(List<Object> list) {
		List<Object> objects = list;
		List<ContactItem> contacts = (List<ContactItem>) objects.get(0);
		List<Integer> tagList = (List<Integer>) objects.get(1);
		for (int i = 0; i < contacts.size(); i++) {
			Log.i("", contacts.get(i).toString());
			Log.i("", tagList.get(i).toString());
		}
	}

}
