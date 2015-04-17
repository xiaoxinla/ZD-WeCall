package com.wecall.contacts.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.wecall.contacts.R;
import com.wecall.contacts.constants.Constants;
import com.wecall.contacts.entity.ContactItem;
import com.wecall.contacts.util.ImageUtil;

/**
 * 将联系人进行排序的适配器
 * 
 * @author xiaoxin
 * 
 */
public class SortAdapter extends BaseAdapter implements SectionIndexer {

	// private static final String TAG = "SortAdapter";
	// 要显示的数据信息
	private List<ContactItem> mContacts = null;
	private List<Boolean> mChecks = null;
	private boolean isVisiable = false;
	// 控件所在上下文
	private Context mContext;

	public SortAdapter(List<ContactItem> mContacts, Context mContext,
			boolean isVisiable) {
		// super();
		this.mContacts = mContacts;
		this.mContext = mContext;
		this.isVisiable = isVisiable;
	}

	public SortAdapter(List<ContactItem> mContacts, Context mContext,
			boolean isVisiable, List<Boolean> mChecks) {
		// super();
		this.mContacts = mContacts;
		this.mContext = mContext;
		this.isVisiable = isVisiable;
		this.mChecks = mChecks;
	}

	@Override
	public int getCount() {
		if(mContacts==null) return 0;
		return mContacts.size();
	}

	@Override
	public Object getItem(int position) {
		return mContacts.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		ViewHolder holder = null;
		final ContactItem contactItem = mContacts.get(position);
		if (view == null) {
			holder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(R.layout.contactitem,
					null);
			holder.tvLetter = (TextView) view.findViewById(R.id.catalog);
			holder.tvTitle = (TextView) view.findViewById(R.id.title);
			holder.ivCheck = (ImageView) view.findViewById(R.id.iv_item_check);
			holder.ivHeader = (ImageView) view.findViewById(R.id.iv_header);
			if (!isVisiable) {
				holder.ivCheck.setVisibility(View.GONE);
			} else {
				holder.ivCheck.setVisibility(View.VISIBLE);
			}
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		if (mChecks != null && position < mChecks.size()) {
			if (mChecks.get(position)) {
				holder.ivCheck.setImageResource(R.drawable.btn_check_on);
			} else {
				holder.ivCheck.setImageResource(R.drawable.btn_check_off);
			}
		}
		Bitmap bitmap = ImageUtil.getLocalBitmap(Constants.ALBUM_PATH, "pic"
				+ contactItem.getId() + ".jpg");
		if(bitmap!=null){
			holder.ivHeader.setImageBitmap(bitmap);
		}else {
			holder.ivHeader.setImageResource(R.drawable.ic_contact_picture);
		}

		int section = getSectionForPosition(position);

		if (position == getPositionForSection(section)) {
			holder.tvLetter.setVisibility(View.VISIBLE);
			holder.tvLetter.setText(contactItem.getSortLetter());
		} else {
			holder.tvLetter.setVisibility(View.GONE);
		}

		holder.tvTitle.setText(mContacts.get(position).getName());
		return view;
	}

	@SuppressLint("DefaultLocale")
	@Override
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = mContacts.get(i).getSortLetter();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public int getSectionForPosition(int position) {
		return mContacts.get(position).getSortLetter().charAt(0);
	}

	@Override
	public Object[] getSections() {

		return null;
	}

	/**
	 * 更新ListView
	 * 
	 * @param list
	 *            传入的联系人列表
	 */
	public void updateListView(List<ContactItem> list) {
		this.mContacts = list;
		notifyDataSetChanged();
	}

	public void updateListView(List<ContactItem> list, List<Boolean> checks) {
		this.mContacts = list;
		this.mChecks = checks;
		notifyDataSetChanged();
	}

	private final static class ViewHolder {
		TextView tvLetter;
		TextView tvTitle;
		ImageView ivCheck;
		ImageView ivHeader;
	}

}
