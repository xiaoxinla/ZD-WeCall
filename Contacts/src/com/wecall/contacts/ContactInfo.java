package com.wecall.contacts;

import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
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
import com.wecall.contacts.util.EncodeUtil;
import com.wecall.contacts.util.ImageUtil;
import com.wecall.contacts.util.StringUtil;
import com.wecall.contacts.view.FlowLayout;
import com.wecall.contacts.view.TextViewWithTitle;

/**
 * ��ϵ������
 * 
 * @author xiaoxin 2014-3-30
 */
public class ContactInfo extends Activity {

	private static final int REQUEST_CODE = 3;

	// ������Ϣ����ʾ��ǩ
	private TextView nameTV;
	private ImageButton callImageButton, msgImageButton;
	private TextViewWithTitle addressTVT, noteTVT;
	private TextView phoneTV;
	// ��ϵ��ͷ��
	private ImageView photoImg;
	// ��ǩ��
	private FlowLayout labelLayout;
	private ImageButton showQRCode;
	// ���ݿ�������
	private DatabaseManager mManager;

	private int cid;
	private ContactItem contact;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_info);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(false);

		// ��ʼ���ؼ�
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

	// ��ʼ���ؼ�
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
				if (phoneNumber.equals("��")) {
					Toast.makeText(ContactInfo.this, "����Ϊ�գ��޷�����绰",
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
				if (phoneNumber.equals("��")) {
					Toast.makeText(ContactInfo.this, "�绰Ϊ�գ��޷����Ͷ���",
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
				Toast.makeText(this, "�༭�ɹ�", Toast.LENGTH_SHORT).show();
			}
		}
	}

	private void updateView(int cid) {
		contact = mManager.queryContactById(cid);

		SpannableStringBuilder styled = StringUtil.colorString(
				contact.getName(), 0, 1, Color.RED);
		nameTV.setText(styled);
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
		// ������ҵ��û��趨��ͷ����ͷ������Ϊ�û��Զ����ͷ�񣬷�������ΪĬ��ͼƬ
		Bitmap userPhoto = ImageUtil.getLocalBitmap(Constants.ALBUM_PATH, "pic"
				+ cid + ".jpg");
		if (userPhoto == null) {
			photoImg.setImageResource(R.drawable.ic_contact_picture);
		} else {
			photoImg.setImageBitmap(userPhoto);
		}
	}

	/**
	 * ������ϵ�˵ı�ǩ
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
		Bitmap bitmap = null;
		String name = contact.getName();
		String phone = "";
		//TODO Multi Case
		Set<String> phoneSet = contact.getPhoneNumber();
		for(String str:phoneSet){

			phone = str;
		}
		try {
			JSONObject jsonObject = new JSONObject();
			String codedJson;
			try {
				jsonObject.put("name", name);
				jsonObject.put("phone", phone);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Log.v("TAG", jsonObject.toString());
			try {
				codedJson = EncodeUtil.encrypt(Constants.AESKEY,
						jsonObject.toString());
			} catch (Exception e) {
				codedJson = jsonObject.toString();
				e.printStackTrace();
			}
			bitmap = ImageUtil.CreateQRCode(codedJson, 300);
		} catch (WriterException e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	/**
	 * ��ʾɾ���Ի���
	 */
	private void showDeleteDialog() {
		new AlertDialog.Builder(this)
		.setTitle("�Ƿ�ȷ��ɾ����")
		.setPositiveButton("��", new DialogInterface.OnClickListener() {

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
		.setNegativeButton("��", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).show();
	}

	/**
	 * ��ʾ��ά��Ի���
	 */
	protected void showQRDialog() {
		ImageView tempImageView = new ImageView(this);
		tempImageView.setImageBitmap(getQRCode());
		new AlertDialog.Builder(this).setTitle(contact.getName())
		.setView(tempImageView)
		.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

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
