package com.wecall.contacts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.wecall.contacts.adapter.SortAdapter;
import com.wecall.contacts.constants.Constants;
import com.wecall.contacts.database.DatabaseManager;
import com.wecall.contacts.entity.ContactItem;
import com.wecall.contacts.util.EncodeUtil;
import com.wecall.contacts.util.ImageUtil;
import com.wecall.contacts.util.PinYin;
import com.wecall.contacts.util.SPUtil;
import com.wecall.contacts.view.ClearableEditText;
import com.wecall.contacts.view.SideBar;
import com.wecall.contacts.view.SideBar.onTouchLetterChangeListener;

public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";
	private static final int INFO_REQUEST_CODE = 1;
	private static final int EDIT_REQUEST_CODE = 2;
	private static final int SETTING_REQUEST_CODE = 3;

	// 联系人列表控件
	private ListView contactListView;
	// 侧边栏索引控件
	private SideBar sideBar;
	// 可删除搜索框控件
	private ClearableEditText inputEditText;
	// 显示当前选中的字母索引的文本控件
	private TextView showAheadTV;
	// 用户二维码
	private ImageView qrcodeIMG;
	// 用户头像
	private ImageView ownerPhoto;
	// 用户名，用户电话
	private TextView ownerNameTV, ownerPhoneTV;
	private String ownerName;
	private String ownerPhone;
	// 设置
	private LinearLayout settingLL;
	// 添加联系人按钮
	private ImageButton addContactBtn;
	// 排序的适配器
	private SortAdapter adapter;
	// 联系人信息
	private List<ContactItem> contactList;
	// 数据库管理实例
	private DatabaseManager mManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
	}

	// 初始化控件
	@SuppressWarnings("unchecked")
	private void initView() {
		contactListView = (ListView) findViewById(R.id.lv_contacts);
		sideBar = (SideBar) findViewById(R.id.sidebar);
		inputEditText = (ClearableEditText) findViewById(R.id.ed_input);
		showAheadTV = (TextView) findViewById(R.id.tv_show_ahead);
		addContactBtn = (ImageButton) findViewById(R.id.ibtn_addcontact);
		qrcodeIMG = (ImageView) findViewById(R.id.iv_owner_qrcode);
		ownerPhoto = (ImageView) findViewById(R.id.iv_owner_photo);
		ownerNameTV = (TextView) findViewById(R.id.tv_owner_name);
		ownerPhoneTV = (TextView) findViewById(R.id.tv_owner_phone);
		settingLL = (LinearLayout) findViewById(R.id.ll_setting);
		mManager = new DatabaseManager(this);
		sideBar.setLetterShow(showAheadTV);

		sideBar.setTouchLetterChangeListener(new onTouchLetterChangeListener() {

			@Override
			public void onTouchLetterChange(String letter) {
				int position = adapter.getPositionForSection(letter.charAt(0));
				if (position != -1) {
					contactListView.setSelection(position);
				}
			}
		});
		contactListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(MainActivity.this, ContactInfo.class);
				Bundle bundle = new Bundle();
				bundle.putInt("cid",
						((ContactItem) adapter.getItem(arg2)).getId());
				intent.putExtras(bundle);
				startActivityForResult(intent, INFO_REQUEST_CODE);
			}
		});

		// 获取联系人信息
		filledData(getResources().getStringArray(R.array.date));
		inputEditText.setHint("可搜索" + contactList.size() + "位联系人");
		// 将联系人按照字母的顺序排序
		Collections.sort(contactList);
		adapter = new SortAdapter(contactList, this);
		contactListView.setAdapter(adapter);

		inputEditText = (ClearableEditText) findViewById(R.id.ed_input);
		// 根据输入框输入值的改变来过滤搜索
		inputEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
				filterData(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		addContactBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MainActivity.this,
						ContactEditor.class);
				Bundle bundle = new Bundle();
				bundle.putInt("type", 1);
				intent.putExtras(bundle);
				startActivityForResult(intent, EDIT_REQUEST_CODE);
			}
		});

		settingLL.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MainActivity.this, Setting.class);
				Bundle bundle = new Bundle();
				bundle.putString("name", ownerName);
				bundle.putString("phone", ownerPhone);
				intent.putExtras(bundle);
				startActivityForResult(intent, SETTING_REQUEST_CODE);
			}
		});
		setOwnerInfo();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.v(TAG, "requestcode:" + requestCode + " resultcode:" + resultCode);
		if (requestCode == EDIT_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
				refreshContacts();
			}
		} else if (requestCode == INFO_REQUEST_CODE) {
			// 删除联系人成功返回RESULT_OK
			if (resultCode == RESULT_OK) {
				Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
				refreshContacts();
			}
		} else if (requestCode == SETTING_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				setOwnerInfo();
				Toast.makeText(this, "用户信息修改成功", Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		if (action == MotionEvent.ACTION_DOWN) {
			View view = getCurrentFocus();
			if (isShouldHideKeyboard(view, ev)) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm != null) {
					imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
				}
			}
			return super.dispatchTouchEvent(ev);
		}

		if (getWindow().superDispatchTouchEvent(ev)) {
			return true;
		}

		return onTouchEvent(ev);
	}

	private boolean isShouldHideKeyboard(View view, MotionEvent ev) {
		if (view != null && (view instanceof EditText)) {
			int[] leftTop = { 0, 0 };
			view.getLocationInWindow(leftTop);
			int left = leftTop[0];
			int top = leftTop[1];
			int right = left + view.getWidth();
			int bottom = top + view.getHeight();

			if (ev.getX() > left && ev.getX() < right && ev.getY() > top
					&& ev.getY() < bottom) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

	/**
	 * 为ListView填充数据
	 * 
	 * @param date
	 */
	@SuppressLint("DefaultLocale")
	private void filledData(String[] data) {

		contactList = mManager.queryAllContact();

		Log.v(TAG, "size:" + contactList.size());
		if (contactList == null || contactList.size() == 0) {
			String areas[] = { "白云山", "广东广州", "成都", "NewYork", "周村", "泉州", "大山" };
			for (int i = 0; i < data.length; i++) {

				ContactItem contactItem = new ContactItem();
				contactItem.setName(data[i]);
				contactItem.setPhoneNumber(genRandomPhone());
				int ind = (int) (Math.random() * areas.length);
				contactItem.setAddress(areas[ind]);

				contactList.add(contactItem);
			}
			mManager.addContacts(contactList);
		}

	}

	/**
	 * 根据输入框中的值来过滤数据并更新ListView 可根据拼音，汉字，缩写来过滤
	 * 
	 * @param filterStr
	 */
	private void filterData(String filterStr) {
		List<ContactItem> filterDateList = new ArrayList<ContactItem>();

		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = contactList;
		} else {
			filterDateList.clear();
			for (ContactItem contactItem : contactList) {
				String filterStrInPinyin = PinYin.getPinYin(filterStr);
				String name = contactItem.getName();
				String fullPinyin = contactItem.getFullPinyin();
				String simplePinyin = contactItem.getSimplePinyin();
				if (name.contains(filterStr)
						|| fullPinyin.contains(filterStrInPinyin)
						|| simplePinyin.contains(filterStrInPinyin)) {
					filterDateList.add(contactItem);
				}
			}
		}
		adapter.updateListView(filterDateList);
	}

	@SuppressWarnings("unchecked")
	private void refreshContacts() {
		contactList = mManager.queryAllContact();
		Collections.sort(contactList);
		adapter.updateListView(contactList);
		inputEditText.setHint("可搜索" + contactList.size() + "位联系人");
	}

	/**
	 * 设置用户信息
	 */
	private void setOwnerInfo() {
		ownerName = (String) SPUtil.get(MainActivity.this, "name", "小新");
		ownerPhone = (String) SPUtil.get(MainActivity.this, "phone",
				"13929514504");

		ownerNameTV.setText(ownerName);
		ownerPhoneTV.setText(ownerPhone);
		Bitmap userPhoto = ImageUtil.getLocalBitmap(Constants.ALBUM_PATH,
				"user.jpg");
		if (userPhoto == null) {
			ownerPhoto.setImageResource(R.drawable.ic_contact_picture);
		} else {
			ownerPhoto.setImageBitmap(userPhoto);
		}
		JSONObject jsonObject = new JSONObject();
		Bitmap bitmap = null;
		try {
			jsonObject.put("name", ownerName);
			jsonObject.put("phone", ownerPhone);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String codedJson;
		try {
			try {
				codedJson = EncodeUtil.encrypt(Constants.AESKEY, jsonObject.toString());
			} catch (Exception e) {
				codedJson = jsonObject.toString();
				e.printStackTrace();
			}
			bitmap = ImageUtil.CreateQRCode(codedJson);
		} catch (WriterException e) {
			e.printStackTrace();
		}
		qrcodeIMG.setImageBitmap(bitmap);
	}

	/**
	 * 产生随机号码，测试用
	 * 
	 * @return 随机号码
	 */
	private String genRandomPhone() {
		String str = "";
		for (int i = 0; i < 11; i++) {
			str += (int) (Math.random() * 10);
		}
		return str;
	}

}
