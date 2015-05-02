package com.wecall.contacts.constants;

import android.os.Environment;

/**
 * ����������й���������Ҫ�õ���һЩ����
 * @author xiaoxin
 *	2015-4-2
 */
public class Constants {

	//���ݿ���
	public static final String DATABASE_NAME = "contact.db";
	
	//������
	public static final String TABLE_NAME_MAIN = "main";
	//���� ������
	public static final String MAIN_COL_CID = "c_id";
	public static final String MAIN_COL_NAME = "name";	
	public static final String MAIN_COL_NOTE = "note";
	public static final String MAIN_COl_PHONE = "phone";
	public static final String MAIN_COL_TAG = "tag";
	public static final String MAIN_COL_ADDRESS = "address";
	public static final String MAIN_COL_OTHER = "other";
	
	//��������
	public static final String TABLE_NAME_SEARCH = "search";
	//�����������
	public static final String SEARCH_COL_CID = "c_id";
	public static final String SEARCH_COL_TYPEID = "type_id";
	public static final String SEARCH_COL_DATA1 = "data1";
	public static final String SEARCH_COL_DATA2 = "data2";
	public static final String SEARCH_COL_DATA3 = "data3";
	public static final String SEARCH_COL_DATA4 = "data4";
	
	//��ǩ��
	public static final String TABLE_NAME_TAG = "tag";
	//��ǩ������
	public static final String TAG_COL_TAG_ID = "t_id";
	public static final String TAG_COL_TAG_NAME = "tagName";
	
//	//tagToContact��
//	public static final String TABLE_NAME_T2C = "t2c";
//	//����
//	public static final String T2C_COL_TAG_ID = "t_id";
//	public static final String T2C_COL_CID = "c_id";
	
	//������typeId
	public static final int TYPE_NAME = 1;
	public static final int TYPE_NOTE = 2;
	public static final int TYPE_PHONE = 3;
	public static final int TYPE_ADDRESS = 4;
	public static final int TYPE_TAG = 5;
	public static final int TYPE_OTHER = 6;
		
	//���ݿ�汾
	public static final int DATABASE_VERSION = 1;
	//����ͼƬ��Ŀ¼
	public static final String ALBUM_PATH = Environment.getExternalStorageDirectory()
			+ "/wecall/picture/";
	//AES��Կ
	public static final String AESKEY = "xiaoxin";
	
	//��ҳ�໬�˵�������Ļ��
	public static final int INIT_MENU_PADDING = 100;
}
