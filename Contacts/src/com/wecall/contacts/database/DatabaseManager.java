package com.wecall.contacts.database;

import java.util.ArrayList;
import java.util.List;

import com.wecall.contacts.constants.Constants;
import com.wecall.contacts.entity.ContactItem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 数据库管理类
 * 
 * @author xiaoxin 2015-4-2
 */
public class DatabaseManager {

	private DatabaseHelper mHelper;
	private SQLiteDatabase mDatabase;

	public DatabaseManager(Context context) {
		mHelper = new DatabaseHelper(context);
		mDatabase = mHelper.getWritableDatabase();
	}

	public int addContact(ContactItem item) {
		ContentValues cv = new ContentValues();
		cv.put("name", item.getName());
		cv.put("phone", item.getPhoneNumber());
		cv.put("address", item.getAddress());
		cv.put("note", item.getNote());
		cv.put("pinyin", item.getFullPinyin());
		cv.put("simplepinyin", item.getSimplePinyin());
		cv.put("sortletter", item.getSortLetter());
		mDatabase.insert(Constants.MAIN_TABLE_NAME, null, cv);

		Cursor c = mDatabase.rawQuery("SELECT MAX(cid) AS maxid FROM "
				+ Constants.MAIN_TABLE_NAME, null);
		c.moveToNext();
		int last = c.getInt(c.getColumnIndex("maxid"));
		c.close();
		return last;
	}

	public void addContacts(List<ContactItem> contacts) {
		mDatabase.beginTransaction();
		try {
			for (ContactItem item : contacts) {
				mDatabase.execSQL(
						"INSERT INTO " + Constants.MAIN_TABLE_NAME
								+ " VALUES(null, ?, ?, ?, ?, ?, ?, ?)",
						new Object[] { item.getName(), item.getPhoneNumber(),
								item.getAddress(), item.getNote(),
								item.getFullPinyin(), item.getSimplePinyin(),
								item.getSortLetter() });
			}
			mDatabase.setTransactionSuccessful();
		} finally {
			mDatabase.endTransaction();
		}
	}

	public void updateContact(ContactItem item) {
		ContentValues cv = new ContentValues();
		cv.put("name", item.getName());
		cv.put("phone", item.getPhoneNumber());
		cv.put("address", item.getAddress());
		cv.put("note", item.getNote());
		cv.put("pinyin", item.getFullPinyin());
		cv.put("simplepinyin", item.getSimplePinyin());
		cv.put("sortletter", item.getSortLetter());

		mDatabase.update(Constants.MAIN_TABLE_NAME, cv, "cid=?",
				new String[] { String.valueOf(item.getId()) });
	}

	public void deleteContact(int cid) {
		mDatabase.delete(Constants.MAIN_TABLE_NAME, "cid = ?",
				new String[] { String.valueOf(cid) });
	}

	public List<ContactItem> queryAllContact() {
		ArrayList<ContactItem> contacts = new ArrayList<ContactItem>();
		Cursor c = queryCursor();
		while (c.moveToNext()) {
			ContactItem item = new ContactItem();
			item.setId(c.getInt(c.getColumnIndex("cid")));
			item.setName(c.getString(c.getColumnIndex("name")));
			item.setPhoneNumber(c.getString(c.getColumnIndex("phone")));
			item.setAddress(c.getString(c.getColumnIndex("address")));
			item.setNote(c.getString(c.getColumnIndex("note")));
			contacts.add(item);
		}
		c.close();
		return contacts;
	}

	public ContactItem queryContactById(int cid) {
		ContactItem item = new ContactItem();

		Cursor c = mDatabase.rawQuery("SELECT * FROM "
				+ Constants.MAIN_TABLE_NAME + " WHERE cid=?",
				new String[] { String.valueOf(cid) });
		if (c.moveToFirst()) {
			item.setId(c.getInt(c.getColumnIndex("cid")));
			item.setName(c.getString(c.getColumnIndex("name")));
			item.setPhoneNumber(c.getString(c.getColumnIndex("phone")));
			item.setAddress(c.getString(c.getColumnIndex("address")));
			item.setNote(c.getString(c.getColumnIndex("note")));
		}
		c.close();
		;
		return item;
	}

	private Cursor queryCursor() {
		Cursor c = mDatabase.rawQuery("SELECT * FROM "
				+ Constants.MAIN_TABLE_NAME, null);
		return c;
	}
}
