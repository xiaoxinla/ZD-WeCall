package com.wecall.contacts;

import java.io.File;
import java.io.IOException;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.wecall.contacts.constants.Constants;
import com.wecall.contacts.util.ImageUtil;
import com.wecall.contacts.util.SPUtil;
import com.wecall.contacts.view.DetailBar;
import com.wecall.contacts.view.DetailBar.DetailBarClickListener;

/**
 * 设置页Activity
 * 
 * @author xiaoxin 2015-4-5
 */
public class Setting extends Activity {

	private static final String TAG = "Setting";
	// 不同请求的请求码
	private static final int ALBUM_REQUEST_CODE = 1;
	private static final int CAMERA_REQUEST_CODE = 2;
	private static final int CROP_REQUEST_CODE = 3;

	// Activity上的控件们
	private EditText nameET, phoneET;
	private Button confireBTN;
	private DetailBar topBar;
	private LinearLayout aboutLayout;
	private ImageButton pictureIBTN;

	// 用户名和电话
	private String mName;
	private String mPhone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		// 初始化控件
		initView();
		// 初始化数据
		initData();
	}

	// 初始化控件
	private void initView() {
		nameET = (EditText) findViewById(R.id.et_setting_name);
		phoneET = (EditText) findViewById(R.id.et_setting_phone);
		confireBTN = (Button) findViewById(R.id.btn_setting_confire);
		topBar = (DetailBar) findViewById(R.id.db_setting_topbar);
		aboutLayout = (LinearLayout) findViewById(R.id.ll_setting_about);
		pictureIBTN = (ImageButton) findViewById(R.id.ibtn_setting_photo);
		confireBTN.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 向配置文件中写入配置信息
				SPUtil.put(Setting.this, "name", nameET.getText().toString());
				SPUtil.put(Setting.this, "phone", phoneET.getText().toString());
				ImageUtil.renameImage(Constants.ALBUM_PATH + "showuser.jpg",
						Constants.ALBUM_PATH + "user.jpg");
				// 设置成功，并结束
				setResult(RESULT_OK);
				finish();
			}
		});

		// 实现topBar的自定义点击事件
		topBar.setOnDetailBarClickListener(new DetailBarClickListener() {

			@Override
			public void rightClick() {

			}

			@Override
			public void leftClick() {
				finish();
			}

			@Override
			public void infoClick() {

			}
		});

		// 点击关于，跳转到关于界面
		aboutLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(Setting.this, AboutActivity.class));
			}
		});

		// 如果能找到用户设定的头像，则将头像设置为用户自定义的头像，否则，设置为默认图片
		Bitmap userPhoto = ImageUtil.getLocalBitmap(Constants.ALBUM_PATH,
				"user.jpg");
		if (userPhoto == null) {
			pictureIBTN.setImageResource(R.drawable.ic_contact_picture);
		} else {
			pictureIBTN.setImageBitmap(userPhoto);
		}

		// 头像的点击事件
		pictureIBTN.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 显示选择图片来源的对话框
				showPicDialog();
			}
		});
	}

	// 处理从其他Activity返回的数据
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.v(TAG, "requestCode:" + requestCode);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			// 从相册返回
			case ALBUM_REQUEST_CODE:
				startPhotoZoom(data.getData());
				break;
			// 从相机返回
			case CAMERA_REQUEST_CODE:
				File tmp = new File(Constants.ALBUM_PATH + "tmpuser.jpg");
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
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 将图片裁剪
	 * 
	 * @param uri
	 *            图片的uri地址
	 */
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

	// 将取得的图片设置到控件上
	private void setPicToView(Intent data) {
		// 取得返回的数据
		Bundle bundle = data.getExtras();
		// 不为空则保存图片到本地并设置到控件上
		if (bundle != null) {
			Bitmap picture = bundle.getParcelable("data");
			try {
				ImageUtil.saveImage(picture, Constants.ALBUM_PATH,
						"showuser.jpg");
			} catch (IOException e) {
				e.printStackTrace();
			}
			pictureIBTN.setImageBitmap(picture);
		}
	}

	@Override
	protected void onDestroy() {
		ImageUtil.deleteImage(Constants.ALBUM_PATH, "tmpuser.jpg");
		ImageUtil.deleteImage(Constants.ALBUM_PATH, "showuser.jpg");
		super.onDestroy();
	}

	// 初始化数据
	private void initData() {
		Bundle bundle = getIntent().getExtras();
		mName = bundle.getString("name");
		mPhone = bundle.getString("phone");
		nameET.setText(mName);
		phoneET.setText(mPhone);
	}

	// 显示对话框
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
										"tmpuser.jpg")));
						startActivityForResult(intent, CAMERA_REQUEST_CODE);
					}
				}).show();
	}
}
