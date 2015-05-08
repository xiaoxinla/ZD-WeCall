package com.wecall.contacts;

import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.wecall.contacts.database.DatabaseManager;
import com.wecall.contacts.view.FlowLayout;

/**
 * 联系人标签编辑
 * @author xiaoxin
 * 2015-5-3
 */
public class ContactLabelEditor extends Activity {

	private static final String TAG = "ContactLabelEditor";
	private EditText input;
	private FlowLayout labelAdded, labelOther;
	private ImageButton addBtn;

	private Set<String> addedList, otherList;
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
		Set<String> allList = new HashSet<String>();

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		cid = bundle.getInt("cid");

		addedList = mManager.queryTagsByContactId(cid);
		Log.v(TAG, addedList.toString());

		allList = mManager.queryAllTags();
		otherList = new HashSet<String>();

		otherList.addAll(allList);
		otherList.removeAll(addedList);
		Log.v(TAG, "otherlabel");
	}

	private void initView() {
		input = (EditText) findViewById(R.id.et_contact_label_input);
		labelAdded = (FlowLayout) findViewById(R.id.fl_label_added);
		labelOther = (FlowLayout) findViewById(R.id.fl_label_other);
		addBtn = (ImageButton) findViewById(R.id.ibtn_contact_label_add);

		setAddedLabel();
		setOtherLabel();

		/* 添加联系人标签 监听事件 */
		addBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String str = input.getEditableText().toString();
				if (str.equals("")) {
					Toast.makeText(ContactLabelEditor.this, "请输入标签名",
							Toast.LENGTH_SHORT).show();
				} else {
					addedList.add(str);
					labelAdded.removeAllViews();
					setAddedLabel();
					input.setText("");
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.contact_label_editor_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/* 显示已添加的标签 */
	protected void setAddedLabel() {
		for (final String str : addedList) {
			final TextView tv = new TextView(this);
			MarginLayoutParams lp = new MarginLayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lp.setMargins(7, 10, 0, 0);
			tv.setText(str);
			tv.setBackgroundResource(R.drawable.label_bg_selected);
			tv.setTextSize(15);
			labelAdded.addView(tv, lp);

			tv.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					addedList.remove(str);
					otherList.add(str);
					labelOther.removeAllViews();
					setOtherLabel();
					labelAdded.removeAllViews();
					setAddedLabel();
				}
			});
		}
	}

	/* 重绘没有添加的标签 */
	protected void setOtherLabel() {
		for (final String str : otherList) {
			final TextView tv = new TextView(this);
			MarginLayoutParams lp = new MarginLayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lp.setMargins(7, 10, 0, 0);
			tv.setText(str);
			tv.setBackgroundResource(R.drawable.label_bg);
			tv.setTextSize(15);
			labelOther.addView(tv, lp);
			tv.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					otherList.remove(str);
					addedList.add(str);
					labelOther.removeAllViews();
					setOtherLabel();
					labelAdded.removeAllViews();
					setAddedLabel();
				}
			});

		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			showReturnDialog();
			break;

		case R.id.action_save_contact_label:
			// mManager.updateContactTags(cid, addedList);
			Intent intent = new Intent();
			String[] labels = new String[addedList.size()];
			addedList.toArray(labels);
			Log.v(TAG, "length:" + labels.length);
			intent.putExtra("labels", labels);
			setResult(RESULT_OK,intent);
			finish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void showReturnDialog() {
		new AlertDialog.Builder(this).setTitle("退出此次编辑？")
				.setPositiveButton("是", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			showReturnDialog();
		}
		return super.onKeyDown(keyCode, event);
	}
}
