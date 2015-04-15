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
	//主表 cid列数
	public static final int MAIN_COL_CID = 0;
	//主表 name列数
	public static final int MAIN_COL_NAME = 1;
	//主表fullPinyin列数
	public static final int MAIN_COL_FULL_PINYIN = 2;
	//主表simplePinyin列数
	public static final int MAIN_COL_SIM_PINYIN = 3;
	//主表sortLetter列数
	public static final int MAIN_COL_SORT = 4;
	//主表note列数
	public static final int MAIN_COL_NOTE = 5;
	
	//标签表名
	public static final String TAG_TABLE_NAME = "tag";
	//标签表cid列数
	public static final int TAG_COL_CID = 0;
	//标签表tag列数
	public static final int TAG_COL_TAG = 1;
	
	//多值表名
	public static final String MULTI_TABLE_NAME = "multiValue";
	//多值表cid列数
	public static final int MULTI_COL_CID = 0;
	//多值表key列数
	public static final int MULTI_COL_KEY = 1;
	//多值表value列数
	public static final int MULTI_COL_VALUE = 2;
	
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
