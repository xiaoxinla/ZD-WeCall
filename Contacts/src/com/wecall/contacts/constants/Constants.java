package com.wecall.contacts.constants;

import android.os.Environment;

/**
 * 定义程序运行过程中所需要用到的一些常量
 * @author xiaoxin
 *	2015-4-2
 */
public class Constants {

	//数据库名
	public static final String DATABASE_NAME = "test1.db";
	//主表名
	public static final String MAIN_TABLE_NAME = "contact";
	//数据库版本
	public static final int DATABASE_VERSION = 1;
	//保存图片的目录
	public static final String ALBUM_PATH = Environment.getExternalStorageDirectory()
			+ "/wecall/picture/";
	
	
	//首页侧滑菜单栏与屏幕差
	public static final int INIT_MENU_PADDING = 100;
}
