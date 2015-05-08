package com.wecall.contacts.adapter;

import java.util.List;
import java.util.Map;
import java.util.Set;
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

/**
 * 搜索ListView适配器
 * @author xiaoxin
 * 2015-4-28
 */
public class SearchAdapter extends BaseAdapter {

	// private static final String TAG = "SearchAdapter";
	private Context mContext;
	private List<List<Object>> mList;
	private int strlen = 0;
	private String queryStr = "";

	public SearchAdapter(Context context, List<List<Object>> list) {
		this.mContext = context;
		this.mList = list;
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
		List<Object> item = mList.get(position);
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
				+ (Integer)item.get(0) + ".jpg");
		if (bitmap != null) {
			holder.ivHeader.setImageBitmap(bitmap);
		} else {
			holder.ivHeader.setImageResource(R.drawable.ic_contact_picture);
		}

		SpannableStringBuilder ssbName = new SpannableStringBuilder(
				(String)item.get(1));
		SpannableStringBuilder ssbOther = new SpannableStringBuilder();
		int type = (Integer) item.get(2);
		int index = -1;
		String str = "";
		switch (type) {
		case Constants.TYPE_NAME:
			str = (String) item.get(3);
			index = str.indexOf(queryStr);
			if(index>=0){
				ssbName = StringUtil.colorString(ssbName, index, strlen, Color.RED);
			}
			break;
		case Constants.TYPE_PHONE:
			Set<String> phones = (Set<String>) item.get(3);
			for(String s:phones){
				str = s;
				break;
			}
			index = str.indexOf(queryStr);
			if(index>=0){
				ssbOther = StringUtil.colorString(str, index, index
						+ strlen, Color.RED);
			}
			break;
		case Constants.TYPE_ADDRESS:
			str = (String) item.get(3);
			index = str.indexOf(queryStr);
			if(index>=0){
				ssbOther = StringUtil.colorString(str, index, index
						+ strlen, Color.RED);
			}
			break;
		case Constants.TYPE_NOTE:
			str = (String) item.get(3);
			index = str.indexOf(queryStr);
			if(index>=0){
				ssbOther = StringUtil.colorString(str, index, index
						+ strlen, Color.RED);
			}
			break;

		default:
			break;
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

	public void updateListView(List<List<Object>> contactList, int strlen,String str) {
		this.mList = contactList;
		this.strlen = strlen;
		this.queryStr = str;
		notifyDataSetChanged();
	}

	private final static class ViewHolder {
		TextView tvName;
		TextView tvOther;
		ImageView ivHeader;
	}
}
