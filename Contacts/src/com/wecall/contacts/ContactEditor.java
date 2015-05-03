package com.wecall.contacts;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wecall.contacts.constants.Constants;
import com.wecall.contacts.database.DatabaseManager;
import com.wecall.contacts.entity.ContactItem;
import com.wecall.contacts.util.EncodeUtil;
import com.wecall.contacts.util.ImageUtil;
import com.wecall.contacts.view.FlowLayout;

/**
 * 联系人编辑类，处理联系人新建或者修改事件
 * 
 * @author xiaoxin 2015-4-3
 */
public class ContactEditor extends Activity {

	private static final String TAG = "ContactEditor";

	// 各种编辑框
	private EditText nameET, phoneET, addressET, noteET;
	private ImageView photoImg;
	private FlowLayout labelLayout;
	private ActionBar actionBar;
	private ImageButton addLabelButton;
	// 数据库管理对象
	private DatabaseManager mManager;

	// 标记操作类型，1为新建，2为修改
	private int mType = 1;
	// 联系人id
	private int mCid = -1;
	private String mName;
	private String mPhone;
	private Set<String> preLabel;

	private static final int ALBUM_REQUEST_CODE = 1;
	private static final int CAMERA_REQUEST_CODE = 2;
	private static final int CROP_REQUEST_CODE = 3;
	private static final int SCAN_REQUEST_CODE = 4;

	private static final int LABEL_EDIT_REQUEST_CODE = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_editor);
		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		// 判断是新建还是修改联系人
		confireType();
		// 初始化控件
		initView();
	}

	// 初始化控件
	private void initView() {
		nameET = (EditText) findViewById(R.id.et_name_add);
		phoneET = (EditText) findViewById(R.id.et_phone_add);
		addressET = (EditText) findViewById(R.id.et_address_add);
		noteET = (EditText) findViewById(R.id.et_note_add);
		photoImg = (ImageView) findViewById(R.id.img_photo_add);
		labelLayout = (FlowLayout) findViewById(R.id.fl_editor_label);
		addLabelButton = (ImageButton) findViewById(R.id.ibtn_label_add);
		mManager = new DatabaseManager(this);

		// 分新建和修改进行不同的初始化
		if (mType == 1) {
			actionBar.setTitle("新建联系人");
			if (mName != null && !mName.isEmpty()) {
				nameET.setText(mName);
			}
			if (mPhone != null && !mPhone.isEmpty()) {
				phoneET.setText(mPhone);
			}
		} else if (mType == 2) {
			actionBar.setTitle("编辑联系人");
			ContactItem item = mManager.queryContactById(mCid);
			nameET.setText(item.getName());
			Set<String> phoneSet = item.getPhoneNumber();
			for(String str:phoneSet){
				phoneET.setText(str);
			}

			addressET.setText(item.getAddress());
			noteET.setText(item.getNote());
			Bitmap bitmap = ImageUtil.getLocalBitmap(Constants.ALBUM_PATH,
					"pic" + mCid + ".jpg");
			if (bitmap == null) {
				photoImg.setImageResource(R.drawable.ic_contact_picture);
			} else {
				photoImg.setImageBitmap(bitmap);
			}
			preLabel = mManager.queryTagsByContactId(mCid);
			setLabels();
		}

		photoImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showPicDialog();
			}
		});

		addLabelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ContactEditor.this,
						ContactLabelEditor.class);
				intent.putExtra("cid", mCid);
				startActivityForResult(intent, LABEL_EDIT_REQUEST_CODE);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.contact_editor_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			showReturnDialog();
			break;
		case R.id.action_editor_scan:
			Intent intent = new Intent(ContactEditor.this,
					MipcaActivityCapture.class);
			startActivityForResult(intent, SCAN_REQUEST_CODE);
			break;
		case R.id.action_editor_save:
			saveContact();
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void saveContact() {
		String name = nameET.getText().toString();
		if (name.isEmpty()) {
			Toast.makeText(ContactEditor.this, "请填写姓名", Toast.LENGTH_SHORT)
			.show();
		} else {
			if (mType == 1) {
				int last = mManager.addContact(getContactFromView());
				Log.v(TAG, "insetid:" + last);
				ImageUtil.renameImage(Constants.ALBUM_PATH + "showpic.jpg",
						Constants.ALBUM_PATH + "pic" + last + ".jpg");
				setResult(RESULT_OK);
				finish();
			} else if (mType == 2) {
				ContactItem item = getContactFromView();
				item.setId(mCid);
				mManager.updateContact(item);
				ImageUtil.renameImage(Constants.ALBUM_PATH + "showpic.jpg",
						Constants.ALBUM_PATH + "pic" + mCid + ".jpg");
				setResult(RESULT_OK);
				finish();
			}
		}
	}

	private void showReturnDialog(){
		new AlertDialog.Builder(this)
		.setTitle("退出此次编辑？")
		.setPositiveButton("是", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				mManager.updateContactTags(mCid, preLabel);
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case SCAN_REQUEST_CODE:
				Bundle bundle = data.getExtras();
				String obtained = bundle.getString("result");
				try {
					JSONObject jsonObject = new JSONObject(obtained);
					String name = jsonObject.getString("name");
					String phone = jsonObject.getString("phone");
					nameET.setText(name);
					phoneET.setText(phone);
				} catch (JSONException e) {
					e.printStackTrace();
					try {
						JSONObject jsonObject = new JSONObject(
								EncodeUtil.decrypt(Constants.AESKEY, obtained));
						String name = jsonObject.getString("name");
						String phone = jsonObject.getString("phone");
						nameET.setText(name);
						phoneET.setText(phone);
					} catch (JSONException e1) {
						e1.printStackTrace();
						Toast.makeText(this,
								"无效联系人：" + bundle.getString("result"),
								Toast.LENGTH_LONG).show();
					} catch (Exception e1) {
						e1.printStackTrace();
						Toast.makeText(this,
								"无效联系人：" + bundle.getString("result"),
								Toast.LENGTH_LONG).show();
					}
				}
				break;
				// 从相册返回
			case ALBUM_REQUEST_CODE:
				startPhotoZoom(data.getData());
				break;
				// 从相机返回
			case CAMERA_REQUEST_CODE:
				File tmp = new File(Constants.ALBUM_PATH + "tmppic.jpg");
				startPhotoZoom(Uri.fromFile(tmp));
				break;
				// 从裁剪后返回
			case CROP_REQUEST_CODE:
				if (data != null) {
					setPicToView(data);
				}
				break;
			case LABEL_EDIT_REQUEST_CODE:
				setLabels();
				Toast.makeText(this, "标签编辑成功", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		}

	}

	@Override
	protected void onDestroy() {
		ImageUtil.deleteImage(Constants.ALBUM_PATH, "tmppic.jpg");
		ImageUtil.deleteImage(Constants.ALBUM_PATH, "showpic.jpg");
		super.onDestroy();
	}

	private void confireType() {
		Bundle bundle = getIntent().getExtras();
		mType = bundle.getInt("type");
		if (mType == 1) {
			mName = bundle.getString("name");
			mPhone = bundle.getString("phone");
		}
		if (mType == 2) {
			mCid = bundle.getInt("cid");

		}
	}

	private ContactItem getContactFromView() {
		ContactItem item = new ContactItem();
		item.setName(nameET.getText().toString());
		Set<String> phoneSet = new HashSet<String>();
		phoneSet.add(phoneET.getText().toString());
		item.setPhoneNumber(phoneSet);
		item.setAddress(addressET.getText().toString());
		item.setNote(noteET.getText().toString());
		Set<String> tagSet = mManager.queryTagsByContactId(mCid);
		item.setLabels(tagSet);
		return item;
	}

	private void setLabels() {		
		labelLayout.removeAllViews();
		Set<String> tagSet = mManager.queryTagsByContactId(mCid);
		for(String str:tagSet){
			TextView tv = new TextView(this);
			MarginLayoutParams lp = new MarginLayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lp.setMargins(7, 10, 0, 0);
			tv.setText(str);
			tv.setBackgroundResource(R.drawable.label_bg_selected);
			tv.setTextSize(15);
			labelLayout.addView(tv, lp);

			Log.v("labels", str);
		}
	}

	// 显示对话框
	// TODO: 将该函数复用
	private void showPicDialog() {
		new AlertDialog.Builder(this)
		.setTitle("设置头像")
		.setNegativeButton("相册", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 让对话框消失
				dialog.dismiss();
				// ACTION_PICK，从数据集合中选择一个返回，官方文档解释如下
				// Activity Action:
				// Pick an item from the data, returning what was
				// selected.
				Intent intent = new Intent(Intent.ACTION_PICK, null);
				// 设置数据来源和类型
				intent.setDataAndType(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
						"image/*");
				startActivityForResult(intent, ALBUM_REQUEST_CODE);
			}
		})
		.setPositiveButton("拍照", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				dialog.dismiss();
				/**
				 * 下面这句还是老样子，调用快速拍照功能，至于为什么叫快速拍照，大家可以参考如下官方
				 * 文档，you_sdk_path/docs/guide/topics/media/camera.html
				 */
				Intent intent = new Intent(
						MediaStore.ACTION_IMAGE_CAPTURE);
				// 打开图片所在目录，如果该目录不存在，则创建该目录
				File dirFile = new File(Constants.ALBUM_PATH);
				if (!dirFile.exists()) {
					dirFile.mkdirs();
				}
				// 将图片保存到该目录下
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri
						.fromFile(new File(Constants.ALBUM_PATH,
								"tmppic.jpg")));
				startActivityForResult(intent, CAMERA_REQUEST_CODE);
			}
		}).show();
	}

	// 将取得的图片设置到控件上
	// TODO：实现复用
	private void setPicToView(Intent data) {
		// 取得返回的数据
		Bundle bundle = data.getExtras();
		// 不为空则保存图片到本地并设置到控件上
		if (bundle != null) {
			Bitmap picture = bundle.getParcelable("data");
			try {
				ImageUtil.saveImage(picture, Constants.ALBUM_PATH,
						"showpic.jpg");
			} catch (IOException e) {
				e.printStackTrace();
			}
			photoImg.setImageBitmap(picture);
		}
	}

	/**
	 * 将图片裁剪
	 * 
	 * @param uri
	 *            图片的uri地址
	 */
	// TODO: 实现复用
	private void startPhotoZoom(Uri uri) {
		Log.v(TAG, "Zoom:" + uri.toString());
		/*
		 * 至于下面这个Intent的ACTION是怎么知道的，大家可以看下自己路径下的如下网页
		 * yourself_sdk_path/docs/reference/android/content/Intent.html
		 */
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 150);
		intent.putExtra("outputY", 150);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, CROP_REQUEST_CODE);
	}
}
