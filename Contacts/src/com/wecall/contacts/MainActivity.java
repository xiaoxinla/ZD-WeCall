package com.wecall.contacts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.wecall.contacts.adapter.SortAdapter;
import com.wecall.contacts.entity.ContactItem;
import com.wecall.contacts.util.PinYin;
import com.wecall.contacts.view.ClearableEditText;
import com.wecall.contacts.view.SideBar;
import com.wecall.contacts.view.SideBar.onTouchLetterChangeListener;

public class MainActivity extends Activity {

	// 联系人列表控件
	private ListView contactListView;
	// 侧边栏索引控件
	private SideBar sideBar;
	// 可删除搜索框控件
	private ClearableEditText inputEditText;
	// 显示当前选中的字母索引的文本控件
	private TextView showAheadTV;
	// 排序的适配器
	private SortAdapter adapter;
	// 联系人信息
	private List<ContactItem> contactList;

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
				bundle.putString("cname",
						((ContactItem) adapter.getItem(arg2)).getName());
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});

		// 获取联系人信息
		// TODO: use SQLite after
		contactList = filledData(getResources().getStringArray(R.array.date));

		// 将联系人按照字母的顺序排序
		Collections.sort(contactList);
		adapter = new SortAdapter(contactList, this);
		contactListView.setAdapter(adapter);

		inputEditText = (ClearableEditText) findViewById(R.id.ed_input);

		inputEditText.setHint("可搜索" + contactList.size() + "位联系人");
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
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	private List<ContactItem> filledData(String[] data) {
		List<ContactItem> mSortList = new ArrayList<ContactItem>();

		for (int i = 0; i < data.length; i++) {
			ContactItem contactItem = new ContactItem();
			contactItem.setName(data[i]);
			String sortString = contactItem.getFullPinyin().substring(0, 1)
					.toUpperCase();

			// 正则表达式，判断首字母是否是英文字母
			if (sortString.matches("[A-Z]")) {
				contactItem.setSortLetter(sortString.toUpperCase());
			} else {
				contactItem.setSortLetter("#");
			}

			mSortList.add(contactItem);
		}
		return mSortList;

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

}
