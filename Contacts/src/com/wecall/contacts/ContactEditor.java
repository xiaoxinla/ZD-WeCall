package com.wecall.contacts;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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
import com.wecall.contacts.view.DetailBar;
import com.wecall.contacts.view.DetailBar.DetailBarClickListener;
import com.wecall.contacts.view.FlowLayout;

/**
 * 联系人编辑类，处理联系人新建或者修改事件
 * 
 * @author xiaoxin 2015-4-3
 */
public class ContactEditor extends Activity {

	private static final String TAG = "ContactEditor";

	// 二维码扫码按钮
	private ImageButton scanBtn;
	// 顶部导航栏
	private DetailBar topbar;
	// 各种编辑框
	private EditText nameET, phoneET, addressET, noteET;
	private ImageView photoImg;
	private FlowLayout labelLayout;
	// 数据库管理对象
	private DatabaseManager mManager;

	// 标记操作类型，1为新建，2为修改
	private int mType = 1;
	// 联系人id
	private int mCid = -1;
	private String mName;
	private String mPhone;
	
	private static final int ALBUM_REQUEST_CODE = 1;
	private static final int CAMERA_REQUEST_CODE = 2;
	private static final int CROP_REQUEST_CODE = 3;
	private static final int SCAN_REQUEST_CODE = 4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_editor);
		// 判断是新建还是修改联系人
		confireType();
		// 初始化控件
		initView();
	}

	// 初始化控件
	private void initView() {
		scanBtn = (ImageButton) findViewById(R.id.btn_scan);
		nameET = (EditText) findViewById(R.id.et_name_add);
		phoneET = (EditText) findViewById(R.id.et_phone_add);
		addressET = (EditText) findViewById(R.id.et_address_add);
		noteET = (EditText) findViewById(R.id.et_note_add);
		topbar = (DetailBar) findViewById(R.id.db_topbar);
		photoImg = (ImageView) findViewById(R.id.img_photo_add);
		labelLayout = (FlowLayout) findViewById(R.id.fl_editor_label);
		mManager = new DatabaseManager(this);

		// 分新建和修改进行不同的初始化
		if (mType == 1) {
			topbar.setInfo("新建联系人");
			if(mName!=null&&!mName.isEmpty()){
				nameET.setText(mName);
			}
			if(mPhone!=null&&!mPhone.isEmpty()){
				phoneET.setText(mPhone);
			}
		} else if (mType == 2) {
			topbar.setInfo("编辑联系人");
			ContactItem item = mManager.queryContactById(mCid);
			nameET.setText(item.getName());
			phoneET.setText(item.getPhoneNumber());
			addressET.setText(item.getAddress());
			noteET.setText(item.getNote());
			Bitmap bitmap = ImageUtil.getLocalBitmap(Constants.ALBUM_PATH,
					"pic" + mCid + ".jpg");
			if (bitmap == null) {
				photoImg.setImageResource(R.drawable.ic_contact_picture);
			} else {
				photoImg.setImageBitmap(bitmap);
			}
			setLabels();
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
						int last = mManager.addContact(getContactFromView());
						Log.v(TAG, "insetid:" + last);
						ImageUtil.renameImage(Constants.ALBUM_PATH
								+ "showpic.jpg", Constants.ALBUM_PATH + "pic"
								+ last + ".jpg");
						setResult(RESULT_OK);
						finish();
					} else if (mType == 2) {
						ContactItem item = getContactFromView();
						item.setId(mCid);
						mManager.updateContact(item);
						ImageUtil.renameImage(Constants.ALBUM_PATH
								+ "showpic.jpg", Constants.ALBUM_PATH + "pic"
								+ mCid + ".jpg");
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
				startActivityForResult(intent, SCAN_REQUEST_CODE);
			}
		});

		photoImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showPicDialog();
			}
		});
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
		if(mType==1){
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
		item.setPhoneNumber(phoneET.getText().toString());
		item.setAddress(addressET.getText().toString());
		item.setNote(noteET.getText().toString());
		return item;
	}
	
	private void setLabels() {
		List<String> labelNames = new ArrayList<String>();
		labelNames.add("逗比");
		labelNames.add("什么鬼");
		labelNames.add("幼儿园同床");
		labelNames.add("作死星人");
		labelNames.add("你来咬我呀！");
		labelNames.add("柔情信仰战");
		labelNames.add("小猫咪");
		labelNames.add("一直跟我抢麦");
		labelNames.add("微讯团队");

		for (int i = 0; i < labelNames.size(); i++) {
			TextView tv = new TextView(this);
			MarginLayoutParams lp = new MarginLayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lp.setMargins(5, 8, 0, 0);
			tv.setText(labelNames.get(i));
			tv.setBackgroundResource(R.drawable.label_bg);
			labelLayout.addView(tv,lp);
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
