package com.wecall.contacts.constants;

import android.os.Environment;

/**
 * 定义程序运行过程中所需要用到的一些常量
 * @author xiaoxin
 *	2015-4-2
 */
public class Constants {

	//数据库名
	public static final String DATABASE_NAME = "contact.db";
	//主表名
	public static final String MAIN_TABLE_NAME = "main";
	//主表 cid列名
	public static final String MAIN_COL_CID = "c_id";
	//主表 name列名
	public static final String MAIN_COL_NAME = "name";
	//主表fullPinyin列名
	public static final String MAIN_COL_FULL_PINYIN = "fullPinyin";
	//主表simplePinyin列名
	public static final String MAIN_COL_SIM_PINYIN = "simplePinyin";
	//主表note列名
	public static final String MAIN_COL_NOTE = "note";
	//主表phoneNumber列名
	public static final String MAIN_COl_PHONE = "phoneNumber";
	//主表address列名
	public static final String MAIN_COL_ADDRESS = "address";
	
	//标签表名
	public static final String TAG_TABLE_NAME = "tag";
	//标签表cid列名
	public static final String TAG_COL_CID = "c_id";
	//标签表tag列名
	public static final String TAG_COL_TAG = "tag";
	
	//数据库版本
	public static final int DATABASE_VERSION = 1;
	//保存图片的目录
	public static final String ALBUM_PATH = Environment.getExternalStorageDirectory()
			+ "/wecall/picture/";
	//AES秘钥
	public static final String AESKEY = "xiaoxin";
	
	//首页侧滑菜单栏与屏幕差
	public static final int INIT_MENU_PADDING = 100;
}
