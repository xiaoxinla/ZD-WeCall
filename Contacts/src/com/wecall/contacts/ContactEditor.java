package com.wecall.contacts;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wecall.contacts.database.DatabaseManager;
import com.wecall.contacts.entity.ContactItem;
import com.wecall.contacts.view.DetailBar;
import com.wecall.contacts.view.DetailBar.DetailBarClickListener;

/**
 * 联系人编辑类，处理联系人新建或者修改事件
 * 
 * @author xiaoxin 2015-4-3
 */
public class ContactEditor extends Activity {

	//private static final String TAG = "ContactEditor";

	// 二维码扫码按钮
	private Button scanBtn;
	// 顶部导航栏
	private DetailBar topbar;
	// 各种编辑框
	private EditText nameET, phoneET, addressET, noteET;
	// 数据库管理对象
	private DatabaseManager mManager;

	// 标记操作类型，1为新建，2为修改
	private int mType = 1;
	// 联系人id
	private int mCid = -1;

	private static final int REQUEST_CODE = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_editor);

		confireType();
		initView();
	}

	private void initView() {
		scanBtn = (Button) findViewById(R.id.btn_scan);
		nameET = (EditText) findViewById(R.id.et_name_add);
		phoneET = (EditText) findViewById(R.id.et_phone_add);
		addressET = (EditText) findViewById(R.id.et_address_add);
		noteET = (EditText) findViewById(R.id.et_note_add);
		topbar = (DetailBar) findViewById(R.id.db_topbar);

		mManager = new DatabaseManager(this);

		if (mType == 1) {
			topbar.setInfo("新建联系人");
		} else if (mType == 2) {
			topbar.setInfo("编辑联系人");
			ContactItem item = mManager.queryContactById(mCid);
			nameET.setText(item.getName());
			phoneET.setText(item.getPhoneNumber());
			addressET.setText(item.getAddress());
			noteET.setText(item.getNote());
		}

		topbar.setOnDetailBarClickListener(new DetailBarClickListener() {

			@Override
			public void rightClick() {
				String name = nameET.getText().toString();
				if (name.isEmpty()) {
					Toast.makeText(ContactEditor.this, "请填写姓名",
							Toast.LENGTH_SHORT).show();
				} else {
					if (mType == 1) {
						ArrayList<ContactItem> contacts = new ArrayList<ContactItem>();
						contacts.add(getContactFromView());
						mManager.addContact(contacts);
						setResult(RESULT_OK);
						finish();
					} else if(mType==2){
						ContactItem item = getContactFromView();
						item.setId(mCid);
						mManager.updateContact(item);
						setResult(RESULT_OK);
						finish();
					}
				}
			}

			@Override
			public void leftClick() {
				finish();
			}

			@Override
			public void infoClick() {

			}
		});

		scanBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(ContactEditor.this,
						MipcaActivityCapture.class);
				startActivityForResult(intent, REQUEST_CODE);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				Bundle bundle = data.getExtras();
				try {
					JSONObject jsonObject = new JSONObject(
							bundle.getString("result"));
					String name = jsonObject.getString("name");
					String phone = jsonObject.getString("phone");
					nameET.setText(name);
					phoneET.setText(phone);
				} catch (JSONException e) {
					e.printStackTrace();
					Toast.makeText(this, "无效联系人：" + bundle.getString("result"),
							Toast.LENGTH_LONG).show();
				}

			}
		}
	}

	private void confireType() {
		Bundle bundle = getIntent().getExtras();
		mType = bundle.getInt("type");
		if (mType == 2) {
			mCid = bundle.getInt("cid");
		}
	}

	private ContactItem getContactFromView(){
		ContactItem item = new ContactItem();
		item.setName(nameET.getText().toString());
		item.setPhoneNumber(phoneET.getText().toString());
		item.setAddress(addressET.getText().toString());
		item.setNote(noteET.getText().toString());
		return item;
	}
}
