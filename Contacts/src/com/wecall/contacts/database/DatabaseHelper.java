package com.wecall.contacts.database;

import com.wecall.contacts.constants.Constants;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	public DatabaseHelper(Context context) {
		super(context, Constants.DATABASE_NAME, null,
				Constants.DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS " + Constants.MAIN_TABLE_NAME
				+ "(cid INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "name VARCHAR, phone VARCHAR, address TEXT, note TEXT, "
				+ "pinyin VARCHAR, simplepinyin VARCHAR, sortletter VARCHAR);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
//		db.execSQL("ALTER TABLE person ADD COLUMN other STRING");
	}

}
