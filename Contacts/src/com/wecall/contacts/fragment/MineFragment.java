package com.wecall.contacts.fragment;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.wecall.contacts.R;
import com.wecall.contacts.SettingActivity;
import com.wecall.contacts.constants.Constants;
import com.wecall.contacts.util.AESUtil;
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
	private ImageView userPhoto, qrcodeImage;
	private TextView nameTextView, phoneTextView;
	private LinearLayout settingLayout;

	private String userName, userPhone;

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
		userPhoto = (ImageView) view.findViewById(R.id.iv_user_photo);
		qrcodeImage = (ImageView) view.findViewById(R.id.iv_user_qrcode);
		nameTextView = (TextView) view.findViewById(R.id.tv_user_name);
		phoneTextView = (TextView) view.findViewById(R.id.tv_user_phone);
		settingLayout = (LinearLayout) view.findViewById(R.id.ll_setting);

		settingLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(), SettingActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("name", userName);
				bundle.putString("phone", userPhone);
				intent.putExtras(bundle);
				startActivityForResult(intent, SETTING_REQUEST_CODE);
			}
		});

	}

	private void setUserInfo() {
		userName = (String) SPUtil.get(getActivity(), "name", "匿名");
		userPhone = (String) SPUtil.get(getActivity(), "phone", "00000");
		nameTextView.setText(userName);
		phoneTextView.setText(userPhone);
		Bitmap userBitmap = ImageUtil.getLocalBitmap(Constants.ALBUM_PATH,
				"user.jpg");
		if (userPhoto == null) {
			userPhoto.setImageResource(R.drawable.ic_contact_picture);
		} else {
			userPhoto.setImageBitmap(userBitmap);
		}
		setQRCode();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.v(TAG, "requestCode:" + requestCode + ",resultCode:" + resultCode);
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case SETTING_REQUEST_CODE:
				setUserInfo();
				Toast.makeText(getActivity(), "用户信息修改成功", Toast.LENGTH_SHORT)
						.show();
				break;

			default:
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void setQRCode() {
		int did = (Integer) SPUtil.get(getActivity(), "did", -1);
		String aesKey = (String) SPUtil.get(getActivity(), "aid",
				Constants.DEFAULT_AESKEY);
		JSONObject jsonObject = new JSONObject();
		Bitmap bitmap = null;
		String codedJson = "";
		try {
			jsonObject.put("did", did);
			JSONObject jsonObject2 = new JSONObject();
			jsonObject2.put("name", userName);
			jsonObject2.put("phone", userPhone);
			String data = "";
			try {
				data = AESUtil.encrypt(aesKey, jsonObject2.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			jsonObject.put("data", data);
			codedJson = jsonObject.toString();
			Log.v(TAG, "codedJson:"+codedJson);
			try {
				bitmap = ImageUtil.CreateQRCode(codedJson, 300);
			} catch (WriterException e) {
				e.printStackTrace();
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		qrcodeImage.setImageBitmap(bitmap);
	}
}
