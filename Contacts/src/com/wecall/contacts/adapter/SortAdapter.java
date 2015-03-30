package com.wecall.contacts.adapter;

import java.util.List;

import com.wecall.contacts.R;
import com.wecall.contacts.entity.ContactItem;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

/**
 * 将联系人进行排序的适配器
 * 
 * @author xiaoxin
 *
 */
public class SortAdapter extends BaseAdapter implements SectionIndexer{

	//要显示的数据信息
	private List<ContactItem> mContacts = null;
	//控件所在上下文
	private Context mContext;
	
	public SortAdapter(List<ContactItem> mContacts, Context mContext) {
		//super();
		this.mContacts = mContacts;
		this.mContext = mContext;
	}

	@Override
	public int getCount() {
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

	@SuppressLint("InflateParams") @Override
	public View getView(int position, View view, ViewGroup arg2) {
		ViewHolder holder = null;
		final ContactItem contactItem = mContacts.get(position);
		if(view==null){
			holder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(R.layout.contactitem, null);
			holder.tvLetter = (TextView) view.findViewById(R.id.catalog);
			holder.tvTitle = (TextView) view.findViewById(R.id.title);
			view.setTag(holder);
		}else {
			holder = (ViewHolder)view.getTag();
		}
		
		int section = getSectionForPosition(position);
		
		if(position == getPositionForSection(section)){
			holder.tvLetter.setVisibility(View.VISIBLE);
			holder.tvLetter.setText(contactItem.getSortLetter());
		}else {
			holder.tvLetter.setVisibility(View.GONE);
		}
		
		holder.tvTitle.setText(mContacts.get(position).getName());
		return view;
	}

	@SuppressLint("DefaultLocale") @Override
	public int getPositionForSection(int section) {
		for(int i=0;i<getCount();i++){
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
		// TODO Auto-generated method stub
		return null;
	}

	
	/**
	 * 更新ListView
	 * 
	 * @param list 传入的联系人列表
	 */
	public void updateListView(List<ContactItem> list){
		this.mContacts = list;
		notifyDataSetChanged();
	}
	
	final static class ViewHolder {
		TextView tvLetter;
		TextView tvTitle;
	}
	
	
}
