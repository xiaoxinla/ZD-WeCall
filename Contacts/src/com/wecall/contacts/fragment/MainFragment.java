package com.wecall.contacts.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wecall.contacts.ContactEditor;
import com.wecall.contacts.ContactInfo;
import com.wecall.contacts.R;
import com.wecall.contacts.adapter.SortAdapter;
import com.wecall.contacts.database.DatabaseManager;
import com.wecall.contacts.entity.SimpleContact;
import com.wecall.contacts.view.SideBar;
import com.wecall.contacts.view.SideBar.onTouchLetterChangeListener;

/**
 * 主Fragment，显示联系人列表
 * 
 * @author xiaoxin 2015-4-10
 */
public class MainFragment extends Fragment {

	private static String TAG = "MainFragment";
	private static final int INFO_REQUEST_CODE = 4;
	private static final int EDIT_REQUEST_CODE = 2;
	private ListView contactListView;
	private TextView letterTextView;
	// 侧边栏索引控件
	private SideBar sideBar;
	// 排序的适配器
	private SortAdapter adapter;
	// 联系人信息
	private List<SimpleContact> contactList = new ArrayList<SimpleContact>();
	// 数据库管理实例
	private DatabaseManager mManager;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		Log.v(TAG, "mainFragment");
		View view = inflater.inflate(R.layout.main_fragment, container, false);
		findView(view);
		return view;
	}

	/**
	 * 初始化控件
	 * 
	 * @param view
	 */
	private void findView(View view) {
		contactListView = (ListView) view.findViewById(R.id.lv_contacts);
		letterTextView = (TextView) view.findViewById(R.id.tv_show_ahead);
		sideBar = (SideBar) view.findViewById(R.id.sidebar);

	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setListener();
		sideBar.setLetterShow(letterTextView);
		adapter = new SortAdapter(contactList, getActivity(), false);
		mManager = new DatabaseManager(getActivity());

		contactListView.setAdapter(adapter);

		// 获取联系人信息
		updateContacts();

	}

	@SuppressWarnings("unchecked")
	public void updateContacts() {
		contactList.clear();
		try {
			contactList = mManager.queryAllContacts();
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(getActivity(), "获取联系人列表失败", Toast.LENGTH_SHORT)
			.show();
		}
		Collections.sort(contactList);
		adapter.updateListView(contactList);
	}

	/**
	 * 设置监听事件
	 */
	private void setListener() {
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
				Log.v(TAG, "position:" + arg2);
				Intent intent = new Intent(getActivity(), ContactInfo.class);
				Bundle bundle = new Bundle();
				bundle.putInt("cid",
						((SimpleContact) adapter.getItem(arg2)).getId());
				intent.putExtras(bundle);
				startActivityForResult(intent, INFO_REQUEST_CODE);
			}
		});
		contactListView
		.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0,
					View arg1, int arg2, long arg3) {
				Log.v(TAG, "position:" + arg2);
				showOperationDialog(arg2);
				return false;
			}
		});
	}

	protected void showOperationDialog(final int position) {
		new AlertDialog.Builder(getActivity())
		.setTitle(((SimpleContact) adapter.getItem(position)).getName())
		.setPositiveButton("编辑", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.v(TAG, "edit");
				dialog.dismiss();
				Intent intent = new Intent(getActivity(),
						ContactEditor.class);
				Bundle bundle = new Bundle();
				bundle.putInt("cid", ((SimpleContact) adapter
						.getItem(position)).getId());
				bundle.putInt("type", 2);
				intent.putExtras(bundle);
				startActivityForResult(intent, EDIT_REQUEST_CODE);
			}

		})
		.setNegativeButton("删除", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.v(TAG, "delete");
				dialog.dismiss();
				showDeleteDialog(position);
			}
		}).show();
	}

	private void showDeleteDialog(final int position) {
		new AlertDialog.Builder(getActivity())
		.setTitle("是否确认删除？")
		.setPositiveButton("是", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				mManager.deleteContactById(((SimpleContact) adapter
						.getItem(position)).getId());
				updateContacts();
				Toast.makeText(getActivity(), "联系人删除成功",
						Toast.LENGTH_SHORT).show();
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
	 * 取得联系人的数目
	 * 
	 * @return
	 */
	public int getContactAmount() {
		return contactList.size();
	}

	// /**
	// * 根据输入框中的值来过滤数据并更新ListView 可根据拼音，汉字，缩写来过滤
	// *
	// * @param filterStr
	// */
	// @SuppressLint("DefaultLocale")
	// public void filterData(String filterStr) {
	// List<ContactItem> filterDateList = new ArrayList<ContactItem>();
	//
	// if (TextUtils.isEmpty(filterStr)) {
	// filterDateList = contactList;
	// } else {
	// filterDateList.clear();
	// for (ContactItem contactItem : contactList) {
	// // String filterStrInPinyin = PinYin.getPinYin(filterStr);
	// // String name = contactItem.getName();
	// // String fullPinyin = contactItem.getFullPinyin();
	// // String simplePinyin = contactItem.getSimplePinyin();
	// // if (name.contains(filterStr)
	// // || fullPinyin.contains(filterStrInPinyin)
	// // || simplePinyin.contains(filterStrInPinyin)) {
	// // filterDateList.add(contactItem);
	// // }
	// String convertStr = filterStr.toLowerCase();
	// Map<String, Integer> originMap = contactItem
	// .contains(filterStr);
	// Map<String, Integer> convertMap = contactItem
	// .contains(convertStr);
	// if (originMap != null && convertMap != null
	// && originMap.size() != 0 && convertMap.size() != 0) {
	// filterDateList.add(contactItem);
	// }
	// }
	// }
	// adapter.updateListView(filterDateList);
	// }

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.v(TAG, "requestCode:" + requestCode + ",resultCode:" + resultCode);
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case INFO_REQUEST_CODE:
				updateContacts();
				break;
			case EDIT_REQUEST_CODE:
				updateContacts();
				break;
			default:
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void initSideBar() {
		// TODO There's something to fix
		if (sideBar != null) {
			sideBar.init();
		}
	}
}
