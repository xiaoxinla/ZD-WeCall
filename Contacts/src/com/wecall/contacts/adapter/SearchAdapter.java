package com.wecall.contacts.adapter;

import java.util.List;
import java.util.Map;

import com.wecall.contacts.R;
import com.wecall.contacts.entity.ContactItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SearchAdapter extends BaseAdapter {

	private Context mContext;
	private List<ContactItem> mList;
	private List<Map<String, Integer>> mIndex;

	public SearchAdapter(Context context, List<ContactItem> list,
			List<Map<String, Integer>> index) {
		this.mContext = context;
		this.mList = list;
		this.mIndex = index;
	}

	@Override
	public int getCount() {
		if(mList==null) return 0;
		return mList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.searchlist_item, null);
			holder = new ViewHolder();
			holder.tvName = (TextView) convertView
					.findViewById(R.id.tv_search_item_name);
			holder.tvOther = (TextView) convertView
					.findViewById(R.id.tv_search_item_other);
		}
		return null;
	}

	private final static class ViewHolder {
		TextView tvName;
		TextView tvOther;
	}
}
