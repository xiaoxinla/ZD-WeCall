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

import com.google.zxing.WriterException;
import com.wecall.contacts.R;
import com.wecall.contacts.Setting;
import com.wecall.contacts.constants.Constants;
import com.wecall.contacts.util.EncodeUtil;
import com.wecall.contacts.util.ImageUtil;
import com.wecall.contacts.util.SPUtil;

/**
 * ¡°ÎÒ¡±Fragment
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
				Intent intent = new Intent(getActivity(), Setting.class);
				Bundle bundle = new Bundle();
				bundle.putString("name", userName);
				bundle.putString("phone", userPhone);
				intent.putExtras(bundle);
				startActivityForResult(intent, SETTING_REQUEST_CODE);
			}
		});

	}

	private void setUserInfo() {
		userName = (String) SPUtil.get(getActivity(), "name", "Ð¡ÐÂ");
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
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.v(TAG, "requestCode:"+requestCode+",resultCode:"+resultCode);
		if(resultCode==Activity.RESULT_OK){
			switch (requestCode) {
			case SETTING_REQUEST_CODE:
				setUserInfo();				
				break;

			default:
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
