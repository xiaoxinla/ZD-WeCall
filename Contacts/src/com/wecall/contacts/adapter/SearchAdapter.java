package com.wecall.contacts.adapter;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wecall.contacts.R;
import com.wecall.contacts.constants.Constants;
import com.wecall.contacts.entity.ContactItem;
import com.wecall.contacts.util.ImageUtil;
import com.wecall.contacts.util.StringUtil;

public class SearchAdapter extends BaseAdapter {

	// private static final String TAG = "SearchAdapter";
	private Context mContext;
	private List<ContactItem> mList;
	private List<Map<String, Integer>> mIndex;
	private int strlen = 0;

	public SearchAdapter(Context context, List<ContactItem> list,
			List<Map<String, Integer>> index) {
		this.mContext = context;
		this.mList = list;
		this.mIndex = index;
	}

	public SearchAdapter(Context context, List<ContactItem> list,
			List<Map<String, Integer>> index, int strlen) {
		this.mContext = context;
		this.mList = list;
		this.mIndex = index;
		this.strlen = strlen;
	}

	@Override
	public int getCount() {
		if (mList == null)
			return 0;
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

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		ContactItem item = mList.get(position);
		Map<String, Integer> map = mIndex.get(position);
		// Log.v(TAG, "item:" + item.toString() + "map:" + map.toString());
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.searchlist_item, null);
			holder = new ViewHolder();
			holder.tvName = (TextView) convertView
					.findViewById(R.id.tv_search_item_name);
			holder.tvOther = (TextView) convertView
					.findViewById(R.id.tv_search_item_other);
			holder.ivHeader = (ImageView) convertView
					.findViewById(R.id.iv_search_header);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Bitmap bitmap = ImageUtil.getLocalBitmap(Constants.ALBUM_PATH, "pic"
				+ item.getId() + ".jpg");
		if (bitmap != null) {
			holder.ivHeader.setImageBitmap(bitmap);
		} else {
			holder.ivHeader.setImageResource(R.drawable.ic_contact_picture);
		}

		SpannableStringBuilder ssbName = new SpannableStringBuilder(
				item.getName());
		SpannableStringBuilder ssbOther = new SpannableStringBuilder();
		for (Entry<String, Integer> entity : map.entrySet()) {
			// Log.v(TAG, entity.toString());
			if (entity.getKey().equals("name")) {
				int index = entity.getValue();
				ssbName = StringUtil.colorString(ssbName, index, strlen,
						Color.RED);
			}

			if (entity.getKey().equals("phone")) {
				int index = entity.getValue();
				for (String str : item.getPhoneNumber()) {
					ssbOther = StringUtil.colorString(str, index, index
							+ strlen, Color.RED);
					break;
				}
			}
			if (entity.getKey().equals("address")) {
				SpannableStringBuilder tmp = new SpannableStringBuilder();
				int index = entity.getValue();
				tmp = StringUtil.colorString(item.getAddress(), index, index
						+ strlen, Color.RED);
				ssbOther.append(tmp);
			}
			if (entity.getKey().equals("note")) {
				SpannableStringBuilder tmp = new SpannableStringBuilder();
				int index = entity.getValue();
				tmp = StringUtil.colorString(item.getNote(), index, index
						+ strlen, Color.RED);
				ssbOther.append(tmp);
			}
		}
		holder.tvName.setText(ssbName);
		if (ssbOther == null || ssbOther.length() == 0) {
			holder.tvOther.setVisibility(View.GONE);
		} else {
			holder.tvOther.setVisibility(View.VISIBLE);
			holder.tvOther.setText(ssbOther);
		}
		return convertView;
	}

	public void updateListView(List<ContactItem> list,
			List<Map<String, Integer>> index, int strlen) {
		this.mList = list;
		this.mIndex = index;
		this.strlen = strlen;
		notifyDataSetChanged();
	}

	private final static class ViewHolder {
		TextView tvName;
		TextView tvOther;
		ImageView ivHeader;
	}
}
