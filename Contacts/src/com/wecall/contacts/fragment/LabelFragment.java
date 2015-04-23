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
 * ��ǩҳfragment
 * 
 * @author xiaoxin 2015-4-11
 */
public class LabelFragment extends Fragment {

	private static final String TAG = "LabelFragment";
	private static final int EDIT_REQUEST_CODE = 1;
	// ��ǩ�б�
	private ListView lableListView;
	// ��ӱ�ǩ��ť
	private ImageButton addImageButton;
	// ������
	private ArrayAdapter<String> adapter;
	// ����
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

	// ��ʼ������
	private void initData() {
		list.clear();
		list.add("����");
		list.add("ʲô��");
		list.add("�׶�԰ͬ��");
		list.add("��������");
		list.add("����ҧ��ѽ��");
		list.add("��������ս");
		list.add("Сè��");
		list.add("һֱ��������");
		list.add("΢Ѷ�Ŷ�");
		list.add("����");
		list.add("ʲô��");
		list.add("�׶�԰ͬ��");
		list.add("��������");
		list.add("����ҧ��ѽ��");
		list.add("��������ս");
		list.add("Сè��");
		list.add("һֱ��������");
		list.add("΢Ѷ�Ŷ�");
	}

	// ��ʼ���ؼ�
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
				Toast.makeText(getActivity(), "�༭�ɹ�", Toast.LENGTH_SHORT)
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
				.setPositiveButton("�༭", new DialogInterface.OnClickListener() {

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
				.setNegativeButton("ɾ��", new DialogInterface.OnClickListener() {

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
				.setTitle("�Ƿ�ȷ��ɾ����")
				.setPositiveButton("��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// TODO �޸ĵ����ݿ���
						list.remove(position);
						adapter.notifyDataSetChanged();
						Toast.makeText(getActivity(), "��ǩɾ���ɹ�",
								Toast.LENGTH_SHORT).show();
					}
				})
				.setNegativeButton("��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();
	}
}
