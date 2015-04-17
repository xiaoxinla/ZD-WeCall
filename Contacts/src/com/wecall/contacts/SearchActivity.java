package com.wecall.contacts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnCloseListener;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;

import com.wecall.contacts.adapter.SortAdapter;
import com.wecall.contacts.database.DatabaseManager;
import com.wecall.contacts.entity.ContactItem;

/**
 * 搜索页
 * @author xiaoxin
 * 2015-4-16
 */
public class SearchActivity extends Activity {

	private static final String TAG = "SearchActivity";
	private SearchView mSearchView;
	private TextView mResultText;
	private ListView mContactListView;
	private SortAdapter adapter;
	private DatabaseManager mManager;

	// 联系人信息
	private List<ContactItem> contactList = new ArrayList<ContactItem>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(false);
		init();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * 初始化数据和控件
	 */
	@SuppressWarnings("unchecked")
	private void init() {
		mSearchView = (SearchView) findViewById(R.id.sv_search);
		mResultText = (TextView) findViewById(R.id.tv_result_search);
		mContactListView = (ListView) findViewById(R.id.lv_search_contact_list);
		mManager = new DatabaseManager(this);
		contactList = mManager.queryAllContact();
		Collections.sort(contactList);
		adapter = new SortAdapter(null, this, false);
		mContactListView.setAdapter(adapter);

		mSearchView.setQueryHint("可搜索" + contactList.size() + "位联系人");
		mSearchView.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String arg0) {
				Log.v(TAG, "onQueryTextSubmit" + arg0);
				filterData(arg0);
				return false;
			}

			@Override
			public boolean onQueryTextChange(String arg0) {
				Log.v(TAG, "onQueryTextChange" + arg0);
				filterData(arg0);
				return false;
			}
		});
		mSearchView.setOnCloseListener(new OnCloseListener() {

			@Override
			public boolean onClose() {
				Log.v(TAG, "onClose");
				return false;
			}
		});
	}

	/**
	 * 根据输入框中的值来过滤数据并更新ListView 可根据拼音，汉字，缩写来过滤
	 * 
	 * @param filterStr
	 */
	@SuppressLint("DefaultLocale")
	private void filterData(String filterStr) {
		List<ContactItem> filterDateList = new ArrayList<ContactItem>();

		if (!TextUtils.isEmpty(filterStr)) {
			filterDateList.clear();
			for (ContactItem contactItem : contactList) {
				String convertStr = filterStr.toLowerCase();
				Map<String, Integer> originMap = contactItem
						.contains(filterStr);
				Map<String, Integer> convertMap = contactItem
						.contains(convertStr);
				if (originMap != null && convertMap != null
						&& originMap.size() != 0 && convertMap.size() != 0) {
					filterDateList.add(contactItem);
				}
			}
		}
		mResultText.setText("搜索到" + filterDateList.size() + "位联系人");
		adapter.updateListView(filterDateList);
	}
}
