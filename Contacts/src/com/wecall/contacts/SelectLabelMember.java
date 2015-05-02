package com.wecall.contacts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wecall.contacts.adapter.SortAdapter;
import com.wecall.contacts.database.DatabaseManager;
import com.wecall.contacts.entity.SimpleContact;
import com.wecall.contacts.view.SideBar;
import com.wecall.contacts.view.SideBar.onTouchLetterChangeListener;

public class SelectLabelMember extends Activity {

	private static final String TAG = "SelectLabelMember";
	private ListView contactListView;
	private TextView letterTextView;
	private EditText inputText;
	// ����������ؼ�
	private SideBar sideBar;
	// �����������
	private SortAdapter adapter;
	// ��ϵ����Ϣ
	private List<SimpleContact> contactList = new ArrayList<SimpleContact>();
	private List<Boolean> checkList = new ArrayList<Boolean>();
	// ���ݿ����ʵ��
	private DatabaseManager mManager;
	private String tagName = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_label_member);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(false);

		tagName = getIntent().getStringExtra("tagName");

		findView();
	}

	/**
	 * ��ʼ���ؼ�
	 * 
	 * @param view
	 */
	@SuppressWarnings("unchecked")
	private void findView() {
		contactListView = (ListView) findViewById(R.id.lv_select_member);
		letterTextView = (TextView) findViewById(R.id.tv_show_letter);
		sideBar = (SideBar) findViewById(R.id.sidebar_mem);
		inputText = (EditText) findViewById(R.id.et_label_input);

		inputText.setText(tagName);

		sideBar.setTouchLetterChangeListener(new onTouchLetterChangeListener() {

			@Override
			public void onTouchLetterChange(String letter) {
				int position = adapter.getPositionForSection(letter.charAt(0));
				if (position != -1) {
					contactListView.setSelection(position);
				}
			}
		});
		contactListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Log.v(TAG, "position:" + arg2);
				if (!checkList.get(arg2)) {
					checkList.set(arg2, true);
				} else {
					checkList.set(arg2, false);
				}
				adapter.updateListView(contactList, checkList);
			}
		});

		sideBar.setLetterShow(letterTextView);
		mManager = new DatabaseManager(this);
		contactList = mManager.queryAllContacts();
		Collections.sort(contactList);
		List<SimpleContact> checkedSet = mManager.queryContactByTag(tagName);
		Log.v(TAG, "checkedSet:" + checkedSet.size());
		for (int i = 0; i < contactList.size(); i++) {
			if (checkedSet.contains(contactList.get(i))) {
				checkList.add(true);
				Log.v(TAG, "index:" + i);
			} else {
				checkList.add(false);
			}
		}
		adapter = new SortAdapter(contactList, this, true);
		contactListView.setAdapter(adapter);
		adapter.updateListView(contactList, checkList);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.label_editor_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.action_save_label:
			if (inputText.getText().toString().equals("")) {
				Toast.makeText(SelectLabelMember.this, "�������ǩ��",
						Toast.LENGTH_SHORT).show();
			} else {
				addTag();
				finish();
			}
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void addTag() {
		Set<Integer> tagSet = new HashSet<Integer>();
		for (int i = 0; i < checkList.size(); i++) {
			if (checkList.get(i)) {
				tagSet.add(contactList.get(i).getId());
			}
		}
		String tagName = inputText.getText().toString();
		mManager.addTagToIds(tagName, tagSet);
		setResult(RESULT_OK);
		Toast.makeText(this, "�༭�ɹ�", Toast.LENGTH_SHORT).show();
	}
}
