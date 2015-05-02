package com.wecall.contacts.database;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wecall.contacts.constants.Constants;
import com.wecall.contacts.entity.ContactItem;
import com.wecall.contacts.entity.SimpleContact;
import com.wecall.contacts.util.PinYin;

/**
 * �������ݿ��࣬����SQL���ϸ�ڣ��ṩ����ɾ����ѯ�ӿ�
 * 
 * @author KM
 */

public class DatabaseManager {

	private DatabaseHelper mHelper = null;
	private static final String LOG_TAG = "DatabaseHelper Error";
	private static Gson gson = new Gson();

	public DatabaseManager(Context context) {
		mHelper = new DatabaseHelper(context);
	}

	/**
	 * �����ݿ�����µ�һ����ϵ�� �������ɹ��᷵�����ݿ��Ӧ��id.
	 */
	public int addContact(ContactItem item) {
		SQLiteDatabase db = mHelper.getWritableDatabase();
		int id = addContact(item, db);
		db.close();
		return id;
	}

	/**
	 * ���ذ汾������ָ��db�Ĳ���
	 */
	private int addContact(ContactItem item, SQLiteDatabase db) {
		try {
			// ����main��
			ContentValues values = new ContentValues();
			values.put(Constants.MAIN_COL_NAME, item.getName());
			values.put(Constants.MAIN_COL_NOTE, item.getNote());
			values.put(Constants.MAIN_COL_ADDRESS, item.getAddress());
			// ���������͵�����ת��Ϊjson	
			values.put(Constants.MAIN_COl_PHONE, gson.toJson(item.getPhoneNumber()));	
			values.put(Constants.MAIN_COL_TAG, gson.toJson(item.getLabels()));	
			// δ��ʹ��,������
			values.put(Constants.MAIN_COL_OTHER, "");
			// �õ����뵽�����ݿ���������id
			int id = (int) db.insert(Constants.TABLE_NAME_MAIN, null, values);
			item.setId(id);

			// ����search��
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
	 * ����������ϵ��
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
		db.close();
	}

	/**
	 * ͨ����ϵ�˱�ʶ��ɾ��һ����ϵ�˼�¼
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
	 * ȡ���ݿ�ȫ���е����ֺ�id��
	 * 
	 * @return
	 */
	public List<SimpleContact> queryAllContacts() {
		ArrayList<SimpleContact> list = new ArrayList<SimpleContact>();
		SQLiteDatabase db = mHelper.getReadableDatabase();
		Cursor cursor = null;

		try {
			// ����main���Ѳ�ͬid��������
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
	 * ͨ��id�ѵ�����ϵ�ˣ������ڷ���null

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
			// δ��ʹ��,����
			// int otherIndex = cursor.getColumnIndex(Constants.MAIN_COL_OTHER);

			// ����ҵ���ȡ�����ݣ����򷵻�null
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
	 * ���������ִ��ǩ
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
				labels.add(cursor.getString(index));
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
	 * ����һ������Ӧ�κ��˵��±�ǩ
	 * 
	 * @param tagName
	 */
	public void addTag(String tagName) {
		SQLiteDatabase db = mHelper.getWritableDatabase();
		getTagId(db, tagName);
		db.close();
	}

	/**
	 * ͨ��tag��������ϵ��
	 * 
	 * @param String
	 *            ��ǩ��
	 * @return
	 */
	public List<SimpleContact> queryContactByTag(String tagName) {
		List<SimpleContact> list = new ArrayList<SimpleContact>();
		SQLiteDatabase db = mHelper.getReadableDatabase();

		// ����MATCH��ȫ������
		Cursor cursor = db.query(true, Constants.TABLE_NAME_SEARCH,
				new String[] { Constants.SEARCH_COL_CID },
				Constants.SEARCH_COL_DATA1 + " MATCH ?", new String[] { "'"
						+ tagName + "'" }, null, null, null, null);

		while (cursor.moveToNext()) {
			int id = cursor.getInt(0);
			// FIXME ��ContactItem����SimpleContact
			ContactItem citem = queryContactById(id);
			SimpleContact sitem = new SimpleContact(citem);
			list.add(sitem);
		}
		cursor.close();

		db.close();
		return list;
	}

	/**
	 * ͨ��id��������ϵ�˵����б�ǩ
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
	 * ������ϵ����Ϣ��ͨ��ContactItem�е�idָ���ض���ϵ��
	 * FIXME ��Ϊ��ȫ����Ϣ���£��п��ܻ�ϴ��ԭ�����ϵ�tag
	 * 
	 * @param item
	 */
	public void updateContact(ContactItem item) {
		SQLiteDatabase db = mHelper.getWritableDatabase();
		int id = item.getId();

		// ����main��
		ContentValues values = new ContentValues();
		values.put(Constants.MAIN_COL_NAME, item.getName());
		values.put(Constants.MAIN_COL_NOTE, item.getNote());
		values.put(Constants.MAIN_COL_ADDRESS, item.getAddress());

		// ���������͵�����ת��Ϊjson
		values.put(Constants.MAIN_COl_PHONE, gson.toJson(item.getPhoneNumber()));
		values.put(Constants.MAIN_COL_TAG, gson.toJson(item.getLabels()));
		// δ��ʹ��,������
		values.put(Constants.MAIN_COL_OTHER, "");
		// ����cid=item.id����
		db.update(Constants.TABLE_NAME_MAIN, values, Constants.MAIN_COL_CID
				+ " = ?", new String[] { id + "" });

		// ����search��

		// FIXME ��ɾ��ԭ�б�����
		db.delete(Constants.TABLE_NAME_SEARCH, Constants.SEARCH_COL_CID + " = "
				+ id, null);

		// ����������
		addContactToSearch(db, item);

		values = null;

		db.close();
	}

	/**
	 * Ϊ��ϵ�������ӱ�ǩ
	 * 
	 * @param tagName
	 *            Ҫ�ӵı�ǩ��
	 * @param cids
	 *            ��id������
	 */
	public void addTagToIds(String tagName, Set<Integer> cids) {
		Log.i("in", tagName);
		Log.i("cids", cids.toString());
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
							new TypeToken<HashSet<String>>(){}.getType());
					// ��������ڣ���Ҫ����
					if (tags != null && !tags.contains(tagName)) {
						// ����main��
						tags.add(tagName);
						ContentValues values = new ContentValues();
						values.put(Constants.MAIN_COL_TAG, gson.toJson(tags));

						db.update(Constants.TABLE_NAME_MAIN, values,
								Constants.MAIN_COL_CID + " = ?",
								new String[] { id + "" });

						// ����search��
						values.clear();
						values.put(Constants.SEARCH_COL_CID, id);
						values.put(Constants.SEARCH_COL_TYPEID,
								Constants.TYPE_TAG);
						values.put(Constants.SEARCH_COL_DATA1, tagName);
						values.put(Constants.SEARCH_COL_DATA2,
								PinYin.getPinYin(tagName));
						values.put(Constants.SEARCH_COL_DATA3,
								PinYin.getSimplePinYin(tagName));
						values.put(Constants.SEARCH_COL_DATA4, tid);
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
	 * ���±�ǩ��Ӧ����ϵ��
	 * 
	 * @param tagName
	 * @param cids
	 */
	public void updateTagByName(String tagName, Set<Integer> cids) {
		SQLiteDatabase db = mHelper.getWritableDatabase();

		int tid = getTagId(db, tagName);

		// ��Ŀǰ��tagName������id
		Cursor cursor = db.query(true, Constants.TABLE_NAME_SEARCH,
				new String[] { Constants.SEARCH_COL_CID },
				Constants.SEARCH_COL_DATA4 + " = " + tid, null, null, null,
				null, null);

		while (cursor.moveToNext()) {
			int cid = cursor.getInt(0);
			// �����list�У����ش�����list��ɾ��
			if (cids.contains(cid)) {
				cids.remove(cid);
			}
			// �����б�������ҪΪ�����ϵ��ɾȥ�����ǩ
			else {
				// ��search��ɾ
				db.delete(Constants.TABLE_NAME_SEARCH, Constants.SEARCH_COL_CID
						+ " = " + cid + " AND " + Constants.SEARCH_COL_DATA4
						+ " = " + tid, null);
				// ��main��ɾ
				// �����������tag
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

		// Ϊ��δ�����ids���������ǩ
		addTagToIds(tagName, cids);

		cursor.close();
		db.close();
	}

	/**
	 * ����һ����ϵ�˵�������ǩ
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

		// FIXME ɾ��ԭ�е������²���
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
			values.put(Constants.SEARCH_COL_DATA2, PinYin.getPinYin(tag));
			values.put(Constants.SEARCH_COL_DATA3, PinYin.getSimplePinYin(tag));
			values.put(Constants.SEARCH_COL_DATA4, tid);
			db.insert(Constants.TABLE_NAME_SEARCH, null, values);
		}

		db.close();
	}

	/**
	 * ɾ��һ����ǩ��ͬʱΪ������ϵ��ɾ�������ǩ
	 * 
	 * @param tagName
	 */
	public void deleteTagByName(String tagName) {
		SQLiteDatabase db = mHelper.getWritableDatabase();

		db.delete(Constants.TABLE_NAME_TAG,
				Constants.TAG_COL_TAG_NAME + " = ?", new String[] { tagName });

		try {
			// �Ѻ������tag������id
			Cursor cursor = db.query(Constants.TABLE_NAME_SEARCH,
					new String[] { Constants.SEARCH_COL_CID },
					Constants.SEARCH_COL_TYPEID + " = " + Constants.TYPE_TAG
							+ " AND " + Constants.SEARCH_COL_DATA1 + " = "
							+ "'" + tagName + "'", null, null, null, null);

			// Ϊmain��������ϵ��ɾȥ���tag
			while (cursor.moveToNext()) {
				int id = cursor.getInt(0);
				Cursor mainCursor = db.query(Constants.TABLE_NAME_MAIN,
						new String[] { Constants.MAIN_COL_TAG },
						Constants.MAIN_COL_CID + " = " + id, null, null, null,
						null);
				if (mainCursor.moveToFirst()) {
					// ȡtag�ļ��ϣ���ȥtagName���ٸ��±�
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

			// ��search����ɾ�����tag����
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
	 * �ж��Ƿ�Ϸ���id
	 */
	private boolean isId(int id) {
		if (id <= 0) {
			Log.e(LOG_TAG, "ilegal id.");
			return false;
		}
		return true;
	}

	/**
	 * ���tag��tid��������ڱ��������
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
				// ����tag��
				ContentValues tagValues = new ContentValues();
				tagValues.put(Constants.TAG_COL_TAG_NAME, tag);
				tid = (int) db.insert(Constants.TABLE_NAME_TAG, null, tagValues);
			}
			cursor.close();
		} catch (SQLException e) {
			e.printStackTrace();
			Log.i(LOG_TAG, "getTagId error");
		}

		return tid;
	}

	/**
	 * ��search�������ϵ����Ϣ
	 * 
	 * @param db
	 * @param item
	 */
	private void addContactToSearch(SQLiteDatabase db, ContactItem item) {
		int id = item.getId();
		ContentValues values = new ContentValues();
		// ����search��
		// name
		values.clear();
		values.put(Constants.SEARCH_COL_CID, id);
		values.put(Constants.SEARCH_COL_TYPEID, Constants.TYPE_NAME);
		values.put(Constants.SEARCH_COL_DATA1, item.getName());
		values.put(Constants.SEARCH_COL_DATA2, item.getFullPinyin());
		values.put(Constants.SEARCH_COL_DATA3, item.getSimplePinyin());
		db.insert(Constants.TABLE_NAME_SEARCH, null, values);

		// note
		values.clear();
		values.put(Constants.SEARCH_COL_CID, id);
		values.put(Constants.SEARCH_COL_TYPEID, Constants.TYPE_NOTE);
		values.put(Constants.SEARCH_COL_DATA1, item.getNote());
		db.insert(Constants.TABLE_NAME_SEARCH, null, values);

		// address
		values.clear();
		values.put(Constants.SEARCH_COL_CID, id);
		values.put(Constants.SEARCH_COL_TYPEID, Constants.TYPE_ADDRESS);
		values.put(Constants.SEARCH_COL_DATA1, item.getAddress());
		db.insert(Constants.TABLE_NAME_SEARCH, null, values);

		// phone
		Set<String> phoneSet = item.getPhoneNumber();
		if (phoneSet != null) {
			Iterator<String> phoneIt = phoneSet.iterator();
			while (phoneIt.hasNext()) {
				String phone = phoneIt.next();
				values.clear();
				values.put(Constants.SEARCH_COL_CID, id);
				values.put(Constants.SEARCH_COL_TYPEID, Constants.TYPE_PHONE);
				values.put(Constants.SEARCH_COL_DATA1, phone);
				db.insert(Constants.TABLE_NAME_SEARCH, null, values);
			}
		}

		// tag
		Set<String> tagSet = item.getLabels();
		if (tagSet != null) {
			Iterator<String> tagIt = tagSet.iterator();
			while (tagIt.hasNext()) {
				String tag = tagIt.next();
				int tid = getTagId(db, tag);
				values.clear();
				values.put(Constants.SEARCH_COL_CID, id);
				values.put(Constants.SEARCH_COL_TYPEID, Constants.TYPE_TAG);
				values.put(Constants.SEARCH_COL_DATA1, tag);
				values.put(Constants.SEARCH_COL_DATA2, PinYin.getPinYin(tag));
				values.put(Constants.SEARCH_COL_DATA3,
						PinYin.getSimplePinYin(tag));
				values.put(Constants.SEARCH_COL_DATA4, tid);
				db.insert(Constants.TABLE_NAME_SEARCH, null, values);
			}
		}
		values = null;
	}

	/**
	 * ���Խӿ�
	 * 
	 * @deprecated
	 */
	public void test() {
		
	}
}
