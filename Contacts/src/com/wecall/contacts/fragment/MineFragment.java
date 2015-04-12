package com.wecall.contacts.fragment;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.wecall.contacts.R;
import com.wecall.contacts.Setting;
import com.wecall.contacts.constants.Constants;
import com.wecall.contacts.database.DatabaseManager;
import com.wecall.contacts.entity.ContactItem;
import com.wecall.contacts.util.CommonUtil;
import com.wecall.contacts.util.EncodeUtil;
import com.wecall.contacts.util.ImageUtil;
import com.wecall.contacts.util.SPUtil;

/**
 * “我”Fragment
 * 
 * @author xiaoxin 2015-4-11
 */
public class MineFragment extends Fragment {

	private static final String TAG = "MineFragment";
	private static final int SETTING_REQUEST_CODE = 3;
	private static final int LOCAL_CONTACTS_OBTAINED = 3;
	private ImageView userPhoto, qrcodeImage;
	private TextView nameTextView, phoneTextView;
	private LinearLayout settingLayout;
	private Button loadButton;

	private String userName, userPhone;
	private List<ContactItem> contactList;
	private DatabaseManager mManager;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case LOCAL_CONTACTS_OBTAINED:
//				Log.v(TAG, "LocalContacts:" + contactList.toString());
				Log.v(TAG, "LOCAL_CONTACTS_OBTAINED");
				CommonUtil.notifyMessage(getActivity(), R.drawable.icon_1,
						"加载完毕", "加载完毕", "本地联系人加载完毕");
				updateContacts();
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}

	};

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.mine_fragment, container, false);
		findView(view);
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		setUserInfo();
		super.onActivityCreated(savedInstanceState);
	}

	private void findView(View view) {
		loadButton = (Button) view.findViewById(R.id.btn_load_contact);
		userPhoto = (ImageView) view.findViewById(R.id.iv_user_photo);
		qrcodeImage = (ImageView) view.findViewById(R.id.iv_user_qrcode);
		nameTextView = (TextView) view.findViewById(R.id.tv_user_name);
		phoneTextView = (TextView) view.findViewById(R.id.tv_user_phone);
		settingLayout = (LinearLayout) view.findViewById(R.id.ll_setting);

		settingLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(), Setting.class);
				Bundle bundle = new Bundle();
				bundle.putString("name", userName);
				bundle.putString("phone", userPhone);
				intent.putExtras(bundle);
				startActivityForResult(intent, SETTING_REQUEST_CODE);
			}
		});

		loadButton.setOnClickListener(new OnClickListener() {

			@SuppressLint("ShowToast") @Override
			public void onClick(View arg0) {
				Toast.makeText(getActivity(), "联系人正在加载中，请稍后...", Toast.LENGTH_LONG).show();
				new Thread(new Runnable() {

					@Override
					public void run() {
						contactList = CommonUtil
								.getLocalContacts(getActivity());
						handler.sendEmptyMessage(LOCAL_CONTACTS_OBTAINED);
					}
				}).start();
			}
		});
	}

	public void setUserInfo() {
		userName = (String) SPUtil.get(getActivity(), "name", "小新");
		userPhone = (String) SPUtil.get(getActivity(), "phone", "13929514504");

		nameTextView.setText(userName);
		phoneTextView.setText(userPhone);
		Bitmap userBitmap = ImageUtil.getLocalBitmap(Constants.ALBUM_PATH,
				"user.jpg");
		if (userPhoto == null) {
			userPhoto.setImageResource(R.drawable.ic_contact_picture);
		} else {
			userPhoto.setImageBitmap(userBitmap);
		}
		JSONObject jsonObject = new JSONObject();
		Bitmap bitmap = null;
		try {
			jsonObject.put("name", userName);
			jsonObject.put("phone", userPhone);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String codedJson;
		try {
			try {
				codedJson = EncodeUtil.encrypt(Constants.AESKEY,
						jsonObject.toString());
			} catch (Exception e) {
				codedJson = jsonObject.toString();
				e.printStackTrace();
			}
			bitmap = ImageUtil.CreateQRCode(codedJson,300);
		} catch (WriterException e) {
			e.printStackTrace();
		}
		qrcodeImage.setImageBitmap(bitmap);
	}

	private void updateContacts(){
		mManager = new DatabaseManager(getActivity());
		mManager.addContacts(contactList);
	}
}
