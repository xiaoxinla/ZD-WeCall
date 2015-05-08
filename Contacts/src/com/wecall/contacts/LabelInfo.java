package com.wecall.contacts;

import java.util.Collections;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.wecall.contacts.adapter.SortAdapter;
import com.wecall.contacts.database.DatabaseManager;
import com.wecall.contacts.entity.SimpleContact;

/**
 * 标签信息
 * @author xiaoxin
 * 2015-5-1
 */
public class LabelInfo extends Activity {

	private static final int EDIT_REQUEST_CODE = 0;
	private ActionBar actionBar;
	private ListView mListView;
	private List<SimpleContact> memberList;
	private SortAdapter adapter;
	private DatabaseManager mManager;
	private String labelName = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_label_info);

		labelName = getIntent().getStringExtra("label");
		String tmpLabelName = labelName;
		if (labelName.length() > 8) {
			tmpLabelName = labelName.substring(0, 6) + ".";
		}
		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setTitle(tmpLabelName);

		mListView = (ListView) findViewById(R.id.lv_label_memeber);
		initData();
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				SimpleContact item = (SimpleContact) adapter.getItem(arg2);
				Intent intent = new Intent(LabelInfo.this,ContactInfo.class);
				intent.putExtra("cid", item.getId());
				startActivity(intent);
			}
		});
	}

	@SuppressWarnings("unchecked")
	private void initData() {
		mManager = new DatabaseManager(this);
		memberList = mManager.queryContactByTag(labelName);
		Collections.sort(memberList);
		adapter = new SortAdapter(memberList, this, false);
		mListView.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.label_info_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.action_delete_label:
			showDeleteDialog();
			break;
		case R.id.action_edit_label:
			Intent intent = new Intent(LabelInfo.this, SelectLabelMember.class);
			intent.putExtra("tagName", labelName);
			startActivityForResult(intent, EDIT_REQUEST_CODE);
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case EDIT_REQUEST_CODE:
			memberList = mManager.queryContactByTag(labelName);
			Collections.sort(memberList);
			adapter.updateListView(memberList);
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	/**
	 * 显示删除对话框
	 */
	private void showDeleteDialog() {
		new AlertDialog.Builder(this).setTitle("是否确认删除？")
				.setPositiveButton("是", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						mManager.deleteTagByName(labelName);
						finish();
					}
				})
				.setNegativeButton("否", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();
	}
}
