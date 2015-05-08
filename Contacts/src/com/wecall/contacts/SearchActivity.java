package com.wecall.contacts;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

import com.wecall.contacts.adapter.SearchAdapter;
import com.wecall.contacts.database.DatabaseManager;
import com.wecall.contacts.entity.ContactItem;

/**
 * 搜索页
 * 
 * @author xiaoxin 2015-4-16
 */
public class SearchActivity extends Activity {

	private static final String TAG = "SearchActivity";
	private static final int INFO_REQUEST_CODE = 1;
	private static final int EDIT_REQUEST_CODE = 2;

	private SearchView mSearchView;
	private TextView mResultText;
	private ListView mContactListView;
	private SearchAdapter adapter;
	private DatabaseManager mManager;
	

	// 联系人信息
	private List<List<Object>> contactList = new ArrayList<List<Object>>();

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
	private void init() {
		mSearchView = (SearchView) findViewById(R.id.sv_search);
		mResultText = (TextView) findViewById(R.id.tv_result_search);
		mContactListView = (ListView) findViewById(R.id.lv_search_contact_list);
		mManager = new DatabaseManager(this);
		contactList = new ArrayList<List<Object>>();
		adapter = new SearchAdapter(this, null);
		mContactListView.setAdapter(adapter);

		mContactListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(SearchActivity.this,
						ContactInfo.class);
				intent.putExtra("cid",
						((ContactItem) adapter.getItem(arg2)).getId());
				startActivityForResult(intent, INFO_REQUEST_CODE);
			}
		});

		mContactListView
				.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, int arg2, long arg3) {
						showOperationDialog(arg2);
						return false;
					}
				});
		int amount = getIntent().getIntExtra("amount", 0);

		mSearchView.setQueryHint("可搜索" + amount + "位联系人");
		mSearchView.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String arg0) {
				filterData(arg0);
				return false;
			}

			@Override
			public boolean onQueryTextChange(String arg0) {
				filterData(arg0);
				return false;
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == INFO_REQUEST_CODE) {
			updateContacts();
		}
		if (requestCode == EDIT_REQUEST_CODE) {
			updateContacts();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 根据输入框中的值来过滤数据并更新ListView 可根据拼音，汉字，缩写来过滤
	 * 
	 * @param filterStr
	 */
	@SuppressLint("DefaultLocale")
	private void filterData(final String filterStr) {
		contactList.clear();
		contactList =  mManager.ftsSearch(filterStr);
		Log.v(TAG, "contactList:" + contactList.toString());
		mResultText.setText("搜索到" + contactList.size() + "位联系人");
		adapter.updateListView(contactList, filterStr.length(),filterStr);
	}

	protected void showOperationDialog(final int position) {
		new AlertDialog.Builder(this)
				.setTitle(((ContactItem) adapter.getItem(position)).getName())
				.setPositiveButton("编辑", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Log.v(TAG, "edit");
						dialog.dismiss();
						Intent intent = new Intent(SearchActivity.this,
								ContactEditor.class);
						Bundle bundle = new Bundle();
						bundle.putInt("cid", ((ContactItem) adapter
								.getItem(position)).getId());
						bundle.putInt("type", 2);
						intent.putExtras(bundle);
						startActivityForResult(intent, EDIT_REQUEST_CODE);
					}

				})
				.setNegativeButton("删除", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Log.v(TAG, "delete");
						dialog.dismiss();
						showDeleteDialog(position);
					}
				}).show();
	}

	private void showDeleteDialog(final int position) {
		new AlertDialog.Builder(this)
				.setTitle("是否确认删除？")
				.setPositiveButton("是", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 从数据库中删除来记录
						mManager.deleteContactById(((ContactItem) adapter
								.getItem(position)).getId());
						// 删除之后获取最新的联系人信息
						updateContacts();
						Toast.makeText(SearchActivity.this, "联系人删除成功",
								Toast.LENGTH_SHORT).show();
					}
				})
				.setNegativeButton("否", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();
	}

	private void updateContacts() {
		contactList =  mManager.ftsSearch(
				mSearchView.getQuery().toString());
		filterData(mSearchView.getQuery().toString());
	}
}
