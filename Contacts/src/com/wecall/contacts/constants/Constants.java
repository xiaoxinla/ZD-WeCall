package com.wecall.contacts.constants;

import android.os.Environment;

/**
 * 定义程序运行过程中所需要用到的一些常量
 * 
 * @author xiaoxin 2015-4-2
 */
public class Constants {

	// 数据库名
	public static final String DATABASE_NAME = "contact.db";

	// 主表名
	public static final String TABLE_NAME_MAIN = "main";
	// 主表 各列名
	public static final String MAIN_COL_CID = "c_id";
	public static final String MAIN_COL_NAME = "name";
	public static final String MAIN_COL_NOTE = "note";
	public static final String MAIN_COl_PHONE = "phone";
	public static final String MAIN_COL_TAG = "tag";
	public static final String MAIN_COL_ADDRESS = "address";
	public static final String MAIN_COL_OTHER = "other";

	// 搜索表名
	public static final String TABLE_NAME_SEARCH = "search";
	// 搜索表各列名
	public static final String SEARCH_COL_CID = "c_id";
	public static final String SEARCH_COL_TYPEID = "type_id";
	public static final String SEARCH_COL_DATA1 = "data1";
	public static final String SEARCH_COL_DATA2 = "data2";
	public static final String SEARCH_COL_DATA3 = "data3";
	public static final String SEARCH_COL_DATA4 = "data4";

	// 标签表
	public static final String TABLE_NAME_TAG = "tag";
	// 标签表列名
	public static final String TAG_COL_TAG_ID = "t_id";
	public static final String TAG_COL_TAG_NAME = "tagName";

	// //tagToContact表
	// public static final String TABLE_NAME_T2C = "t2c";
	// //列名
	// public static final String T2C_COL_TAG_ID = "t_id";
	// public static final String T2C_COL_CID = "c_id";

	// 各类型typeId
	public static final int TYPE_NAME = 1;
	public static final int TYPE_NOTE = 2;
	public static final int TYPE_PHONE = 3;
	public static final int TYPE_ADDRESS = 4;
	public static final int TYPE_TAG = 5;
	public static final int TYPE_OTHER = 6;

	// 数据库版本
	public static final int DATABASE_VERSION = 1;
	// 保存图片的目录
	public static final String ALBUM_PATH = Environment
			.getExternalStorageDirectory() + "/wecall/picture/";
	// AES秘钥
	public static final String DEFAULT_AESKEY = "wecall";
	// RSA公钥
	public static final String PUBLIC_KEY = 
			"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDHzqnXfyy+OWQhXMmY31BxyfV3"
			+ "wE7pYadj5RqtECGX9/abioF0eE7VnQVGaPml2rTT/zCksBS7mzMPCMFEOj0zU7P1"
			+ "nkugVRAadp9EUEArf/DDt/yi0Ryt7UOuL0SmYXksSL1CgEEd0cna373Kj40/PVUE"
			+ "bu6kvEh/qR6v3FJx1QIDAQAB";
	public static final String SERVER_URL="http://iweixun.sinaapp.com";
}
