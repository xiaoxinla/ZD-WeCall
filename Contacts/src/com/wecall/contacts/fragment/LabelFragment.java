package com.wecall.contacts.fragment;

import java.util.ArrayList;
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

/**
 * 标签页fragment
 * 
 * @author xiaoxin 2015-4-11
 */
public class LabelFragment extends Fragment {

	private static final String TAG = "LabelFragment";
	private static final int EDIT_REQUEST_CODE = 1;
	// 标签列表
	private ListView lableListView;
	// 添加标签按钮
	private ImageButton addImageButton;
	// 适配器
	private ArrayAdapter<String> adapter;
	// 数据
	private List<String> list = new ArrayList<String>();

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.label_fragment, container, false);
		findView(view);
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		initData();
		adapter = new ArrayAdapter<String>(getActivity(),
				R.layout.label_item, list);
		lableListView.setAdapter(adapter);
		super.onActivityCreated(savedInstanceState);
	}

	// 初始化数据
	private void initData() {
		list.clear();
		list.add("逗比");
		list.add("什么鬼");
		list.add("幼儿园同床");
		list.add("作死星人");
		list.add("你来咬我呀！");
		list.add("柔情信仰战");
		list.add("小猫咪");
		list.add("一直跟我抢麦");
		list.add("微讯团队");
		list.add("逗比");
		list.add("什么鬼");
		list.add("幼儿园同床");
		list.add("作死星人");
		list.add("你来咬我呀！");
		list.add("柔情信仰战");
		list.add("小猫咪");
		list.add("一直跟我抢麦");
		list.add("微讯团队");
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
				startActivity(intent);
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
				Bundle bundle = new Bundle();
				bundle.putInt("type", 1);
				startActivityForResult(intent, EDIT_REQUEST_CODE);
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case EDIT_REQUEST_CODE:
				Toast.makeText(getActivity(), "编辑成功", Toast.LENGTH_SHORT)
						.show();
				initData();
				adapter.notifyDataSetChanged();
				break;

			default:
				break;
			}
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
						// TODO 修改到数据库中
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
