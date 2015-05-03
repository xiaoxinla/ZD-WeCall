package com.wecall.contacts.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.wecall.contacts.LabelInfo;
import com.wecall.contacts.R;
import com.wecall.contacts.SelectLabelMember;
import com.wecall.contacts.database.DatabaseManager;

/**
 * 标签页fragment
 * 
 * @author xiaoxin 2015-4-11
 */
public class LabelFragment extends Fragment {

	private static final String TAG = "LabelFragment";
	private static final int EDIT_REQUEST_CODE = 1;
	protected static final int INFO_REQUEST_CODE = 0;
	// 标签列表
	private ListView lableListView;
	// 添加标签按钮
	private ImageButton addImageButton;
	// 适配器
	private ArrayAdapter<String> adapter;
	// 数据
	private List<String> list = new ArrayList<String>();
	private DatabaseManager mManager;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.label_fragment, container, false);
		findView(view);
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		mManager = new DatabaseManager(getActivity());
		initData();
		adapter = new ArrayAdapter<String>(getActivity(),
				R.layout.label_item, list);
		lableListView.setAdapter(adapter);
		super.onActivityCreated(savedInstanceState);
	}

	// 初始化数据
	public void initData() {
		Set<String> tagSet = mManager.queryAllTags();
		Log.v(TAG, tagSet.toString());
		list.clear();
		for (String str : tagSet) {
			list.add(str);
		}
	}

	public void refreshListView(){
		adapter.notifyDataSetChanged();
	}


	// 初始化控件
	private void findView(View view) {
		lableListView = (ListView) view.findViewById(R.id.lv_label);
		addImageButton = (ImageButton) view.findViewById(R.id.ibtn_label_add);

		lableListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(getActivity(), LabelInfo.class);
				intent.putExtra("label", list.get(arg2));
				startActivityForResult(intent,INFO_REQUEST_CODE);
			}
		});

		lableListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				showOperationDialog(arg2);
				return false;
			}
		});

		addImageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Log.v(TAG, "AddBtnClick");
				Intent intent = new Intent(getActivity(),
						SelectLabelMember.class);
				intent.putExtra("tagName", "");
				startActivityForResult(intent, EDIT_REQUEST_CODE);
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.v(TAG, "requestCode:" + requestCode + " resultCode:" + resultCode);
		switch (requestCode) {
		case EDIT_REQUEST_CODE:
			initData();
			adapter.notifyDataSetChanged();
			break;
		case INFO_REQUEST_CODE:
			initData();
			adapter.notifyDataSetChanged();
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	protected void showOperationDialog(final int position) {
		new AlertDialog.Builder(getActivity())
		.setTitle(list.get(position))
		.setPositiveButton("编辑", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.v(TAG, "edit");
				dialog.dismiss();
				Intent intent = new Intent(getActivity(),
						SelectLabelMember.class);
				Bundle bundle = new Bundle();
				bundle.putString("label", list.get(position));
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
				mManager.deleteTagByName(list.get(position));
				list.remove(position);
				adapter.notifyDataSetChanged();
				Toast.makeText(getActivity(), "标签删除成功",
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
}
