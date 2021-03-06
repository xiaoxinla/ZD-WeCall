package com.wecall.contacts;

import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.wecall.contacts.constants.Constants;
import com.wecall.contacts.database.DatabaseManager;
import com.wecall.contacts.entity.ContactItem;
import com.wecall.contacts.util.AESUtil;
import com.wecall.contacts.util.ImageUtil;
import com.wecall.contacts.util.SPUtil;
import com.wecall.contacts.util.StringUtil;
import com.wecall.contacts.view.FlowLayout;
import com.wecall.contacts.view.TextViewWithTitle;

/**
 * 联系人详情
 * 
 * @author xiaoxin 2014-3-30
 */
public class ContactInfo extends Activity {

	private static final int REQUEST_CODE = 3;

	// 各种信息的显示标签
	private TextView nameTV;
	private ImageButton callImageButton, msgImageButton;
	private TextViewWithTitle addressTVT, noteTVT;
	private TextView phoneTV;
	// 联系人头像
	private ImageView photoImg;
	// 标签栏
	private FlowLayout labelLayout;
	private ImageButton showQRCode;
	// 数据库管理对象
	private DatabaseManager mManager;

	private int cid;
	private ContactItem contact;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_info);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(false);

		// 初始化控件
		initView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.contact_info_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.action_contact_edit:
			Intent intent = new Intent(ContactInfo.this, ContactEditor.class);
			Bundle bund = new Bundle();
			bund.putInt("type", 2);
			bund.putInt("cid", cid);
			intent.putExtras(bund);
			startActivityForResult(intent, REQUEST_CODE);
			break;
		case R.id.action_contact_delete:
			showDeleteDialog();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	// 初始化控件
	private void initView() {
		nameTV = (TextView) findViewById(R.id.tv_contact_name);
		addressTVT = (TextViewWithTitle) findViewById(R.id.tvt_address);
		noteTVT = (TextViewWithTitle) findViewById(R.id.tvt_note);
		phoneTV = (TextView) findViewById(R.id.tv_phone_info);
		callImageButton = (ImageButton) findViewById(R.id.ibtn_phone_call);
		msgImageButton = (ImageButton) findViewById(R.id.ibtn_phone_msg);
		photoImg = (ImageView) findViewById(R.id.img_contact_photo);
		labelLayout = (FlowLayout) findViewById(R.id.fl_label_show);
		showQRCode = (ImageButton) findViewById(R.id.ibtn_qrcode_show);

		mManager = new DatabaseManager(this);

		Bundle bundle = getIntent().getExtras();
		cid = bundle.getInt("cid");

		updateView(cid);

		final String phoneNumber = phoneTV.getText().toString();

		callImageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (phoneNumber.equals("无")) {
					Toast.makeText(ContactInfo.this, "号码为空，无法拨打电话",
							Toast.LENGTH_SHORT).show();
				} else {
					Intent intent = new Intent(Intent.ACTION_CALL, Uri
							.parse("tel:" + phoneNumber));
					ContactInfo.this.startActivity(intent);
				}
			}
		});

		msgImageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (phoneNumber.equals("无")) {
					Toast.makeText(ContactInfo.this, "电话为空，无法发送短信",
							Toast.LENGTH_SHORT).show();
				} else {
					Intent intent = new Intent(Intent.ACTION_SENDTO, Uri
							.parse("smsto:" + phoneNumber));
					ContactInfo.this.startActivity(intent);
				}
			}
		});

		showQRCode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showQRDialog();
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				updateView(cid);
				Toast.makeText(this, "编辑成功", Toast.LENGTH_SHORT).show();
			}
		}
	}

	private void updateView(int cid) {
		contact = mManager.queryContactById(cid);
		nameTV.setText(contact.getName());
		// nameTV.setText(bundle.getString("cname"));
		if(StringUtil.formatString(contact.getAddress()).length()>14){
			addressTVT.setText(StringUtil.formatString(contact.getAddress()).substring(0, 13)+"...");
		}
		else{
			addressTVT.setText(StringUtil.formatString(contact.getAddress()));
		}
		if(StringUtil.formatString(contact.getNote()).length()>14){
			noteTVT.setText(StringUtil.formatString(contact.getNote()).substring(0, 13)+"...");
		}
		else{
			noteTVT.setText(StringUtil.formatString(contact.getNote()));
		}
		Set<String> phoneSet = contact.getPhoneNumber();
		for(String str:phoneSet){
			phoneTV.setText(StringUtil.formatString(str));
		}
		showContactPhoto();
		setLabels();
	}

	private void showContactPhoto() {
		// 如果能找到用户设定的头像，则将头像设置为用户自定义的头像，否则，设置为默认图片
		Bitmap userPhoto = ImageUtil.getLocalBitmap(Constants.ALBUM_PATH, "pic"
				+ cid + ".jpg");
		if (userPhoto == null) {
			photoImg.setImageResource(R.drawable.ic_contact_picture);
		} else {
			photoImg.setImageBitmap(userPhoto);
		}
	}

	/**
	 * 设置联系人的标签
	 */
	private void setLabels() {

		labelLayout.removeAllViews();

		Set<String> tagSet = mManager.queryTagsByContactId(cid);
		Log.v("tagsize",String.valueOf( tagSet.size()));
		for(String str:tagSet){
			TextView tv = new TextView(this);
			MarginLayoutParams lp = new MarginLayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lp.setMargins(7, 10, 0, 0);
			tv.setText(str);
			tv.setBackgroundResource(R.drawable.label_bg_selected);
			tv.setTextSize(15);
			labelLayout.addView(tv, lp);
			Log.v("contact_labels", str);
		}
	}

	private Bitmap getQRCode() {
		int did = (Integer) SPUtil.get(this, "did", -1);
		String aesKey = (String) SPUtil.get(this, "aid",
				Constants.DEFAULT_AESKEY);
		Bitmap bitmap = null;
		String name = contact.getName();
		String phone = "";
		//TODO Multi Case
		Set<String> phoneSet = contact.getPhoneNumber();
		for(String str:phoneSet){
			phone = str;
		}
		JSONObject jsonObject = new JSONObject();
		String codedJson = "";
		try {
			jsonObject.put("did", did);
			JSONObject jsonObject2 = new JSONObject();
			jsonObject2.put("name", name);
			jsonObject2.put("phone", phone);
			String data = "";
			try {
				data=AESUtil.encrypt(aesKey, jsonObject2.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			jsonObject.put("data", data);
			codedJson = jsonObject.toString();
			try {
				bitmap = ImageUtil.CreateQRCode(codedJson, 300);
			} catch (WriterException e) {
				e.printStackTrace();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	/**
	 * 显示删除对话框
	 */
	private void showDeleteDialog() {
		new AlertDialog.Builder(this)
		.setTitle("是否确认删除？")
		.setPositiveButton("是", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				arg0.dismiss();
				mManager.deleteContactById(cid);
				ImageUtil.deleteImage(Constants.ALBUM_PATH, "pic" + cid
						+ ".jpg");
				setResult(RESULT_OK);
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

	/**
	 * 显示二维码对话框
	 */
	protected void showQRDialog() {
		ImageView tempImageView = new ImageView(this);
		tempImageView.setImageBitmap(getQRCode());
		new AlertDialog.Builder(this).setTitle(contact.getName())
		.setView(tempImageView)
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				arg0.dismiss();
			}
		}).show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
