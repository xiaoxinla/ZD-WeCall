package com.wecall.contacts;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.wecall.contacts.database.DatabaseManager;
import com.wecall.contacts.entity.ContactItem;
import com.wecall.contacts.util.ImageUtil;
import com.wecall.contacts.util.StringUtil;
import com.wecall.contacts.view.DetailBar;
import com.wecall.contacts.view.DetailBar.DetailBarClickListener;
import com.wecall.contacts.view.IconTextView;

/**
 * 联系人详情
 * @author xiaoxin
 * 2014-3-30
 */
public class ContactInfo extends Activity {

	private static final int REQUEST_CODE = 3;
	
	//各种信息的显示标签
	private TextView nameTV,addressTV,noteTV;
	//返回键
	private ImageButton backIB;
	//电话号码所在的条，集成打电话，发短信功能
	private DetailBar phoneNumBar;
	//显示二维码
	private ImageView testImg;
	//数据库管理对象
	private DatabaseManager mManager;
	private IconTextView editITV,deleteITV;
	
	private int cid;
	private ContactItem contact;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_info);
		//初始化控件
		initView();
	}

	//初始化控件
	private void initView() {
		nameTV = (TextView) findViewById(R.id.tv_contact_name);
		addressTV = (TextView) findViewById(R.id.tv_adress_show);
		noteTV = (TextView) findViewById(R.id.tv_note_show);
		backIB = (ImageButton) findViewById(R.id.back_to_homepage);
		phoneNumBar = (DetailBar) findViewById(R.id.phone_num);
		testImg = (ImageView) findViewById(R.id.iv_test);
		editITV = (IconTextView) findViewById(R.id.itv_edit);
		deleteITV = (IconTextView) findViewById(R.id.itv_delete);
		mManager = new DatabaseManager(this);
		
		Bundle bundle = getIntent().getExtras();
		cid = bundle.getInt("cid");
		
		updateView(cid);

		backIB.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		final String phoneNumber = phoneNumBar.getInfo().toString();

		phoneNumBar.setOnDetailBarClickListener(new DetailBarClickListener() {

			@Override
			public void leftClick() {
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
						+ phoneNumber));
				ContactInfo.this.startActivity(intent);
			}

			@Override
			public void rightClick() {
				Intent intent = new Intent(Intent.ACTION_SENDTO, Uri
						.parse("smsto:" + phoneNumber));
				ContactInfo.this.startActivity(intent);
			}

			@Override
			public void infoClick() {
				
			}
		});
		
		editITV.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(ContactInfo.this,ContactEditor.class);
				Bundle bund = new Bundle();
				bund.putInt("type", 2);
				bund.putInt("cid", cid);
				intent.putExtras(bund);
				startActivityForResult(intent, REQUEST_CODE);
			}
		});
		
		deleteITV.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mManager.deleteContact(cid);
				finish();
			}
		});

		try {
			JSONObject jsonObject = new JSONObject();
			String name = contact.getName();
			try {
				jsonObject.put("name", name);
				jsonObject.put("phone", phoneNumber);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Log.v("TAG", jsonObject.toString());
			Bitmap bitmap = ImageUtil.CreateQRCode(jsonObject.toString());
			testImg.setImageBitmap(bitmap);
		} catch (WriterException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==REQUEST_CODE){
			if(resultCode==RESULT_OK){
				updateView(cid);
				Toast.makeText(this, "编辑成功", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	private void updateView(int cid){
		contact = mManager.queryContactById(cid);

		SpannableStringBuilder styled = StringUtil.colorString(
				contact.getName(), 1, 2, Color.RED);
		nameTV.setText(styled);
		// nameTV.setText(bundle.getString("cname"));
		addressTV.setText(StringUtil.formatString(contact.getAddress()));
		noteTV.setText(StringUtil.formatString(contact.getNote()));
		phoneNumBar.setInfo(contact.getPhoneNumber());
	}
}
