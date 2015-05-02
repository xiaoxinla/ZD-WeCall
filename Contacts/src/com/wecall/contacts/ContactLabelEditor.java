package com.wecall.contacts;

import java.util.Set;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;

import com.wecall.contacts.database.DatabaseManager;
import com.wecall.contacts.view.FlowLayout;

public class ContactLabelEditor extends Activity {

	private static final String TAG = "ContactLabelEditor";
	private EditText input;
	private FlowLayout labelAdded,labelOther;
	
	private Set<String> addedList,otherList;
	private int cid;
	private DatabaseManager mManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.actvity_contact_label_editor);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(false);
		
		initData();
		initView();
	}

	private void initData() {
		mManager = new DatabaseManager(this);
		
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		cid = bundle.getInt("cid");
		
		addedList = mManager.queryTagsByContactId(cid);
		Log.v(TAG, addedList.toString());
	}

	private void initView() {
		input = (EditText) findViewById(R.id.et_contact_label_input);
		labelAdded = (FlowLayout) findViewById(R.id.fl_label_added);
		labelOther = (FlowLayout) findViewById(R.id.fl_label_other);
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
}
