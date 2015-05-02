package com.wecall.contacts.entity;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;

import com.wecall.contacts.util.PinYin;

/**
 * 简单的联系人实体类，只包含ContactItem中的有限元素
 * @author kim
 *
 */
@SuppressWarnings("rawtypes")
public class SimpleContact implements Comparable{
	private int cid;
	private String name;
	private String fullPinyin;
	private String simplePinyin;
	private String sortLetter;
	
	public SimpleContact()
	{
		
	}
	
	public SimpleContact(int cid, String name)
	{
		setId(cid);
		setName(name);
	}
	
	/**
	 * 通过citem来构造简化版的simplecontact
	 * @param citem
	 */
	public SimpleContact(ContactItem citem)
	{
		setId(citem.getId());
		setName(citem.getName());
	}
	
	public void setId(int cid)
	{
		this.cid = cid;
	}
	
	public void setName(String name)
	{
		if (name == null)
			this.name = null;
		else
		{
			this.name = new String(name);
			fullPinyin = PinYin.getPinYin(this.name);
			simplePinyin = PinYin.getSimplePinYin(this.name);
			setSortLetter(fullPinyin);
		}
	}
	
	public String getSortLetter() {
		return sortLetter;
	}
	
	public int getId()
	{
		return this.cid;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public String getFullPinyin()
	{
		return this.fullPinyin;
	}
	
	public String getSimplePinyin()
	{
		return this.simplePinyin;
	}
	
	@Override
	public String toString() {
		return "ContactItem [id=" + cid + ", name=" + name 
				+ ", fullPinyin=" + fullPinyin + ", simplePinyin="
				+ simplePinyin + "]";
	}

	@Override
	public int compareTo(Object arg0) {
		SimpleContact tmpItem = (SimpleContact) arg0;
		if (tmpItem.getSortLetter().equals("#") && !getSortLetter().equals("#")) {
			return -1;
		} else if (!tmpItem.getSortLetter().equals("#")
				&& getSortLetter().equals("#")) {
			return 1;
		}
		return getFullPinyin().compareTo(tmpItem.getFullPinyin());
	}
	
	@SuppressLint("DefaultLocale") 
	private void setSortLetter(String inputString){
		if(inputString.isEmpty()){
			this.sortLetter = "#";
			return;
		}
		String sortString = inputString.substring(0, 1).toUpperCase();
		// 正则表达式，判断首字母是否是英文字母
		if (sortString.matches("[A-Z]")) {
			this.sortLetter = sortString;
		} else {
			this.sortLetter = "#";
		}
	}

	public Map<String, Integer> contains(String str){
		Map<String, Integer> map = new HashMap<String, Integer>();
		if(name!=null&&name.indexOf(str)!=-1){
			map.put("name",name.indexOf(str));
		}
		if(fullPinyin!=null&&fullPinyin.indexOf(str)!=-1){
			map.put("fullpinyin",fullPinyin.indexOf(str));
		}
		if(simplePinyin!=null&&simplePinyin.indexOf(str)!=-1){
			map.put("simplepinyin",simplePinyin.indexOf(str));
		}
		return map;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof SimpleContact){
			SimpleContact item = (SimpleContact)o;
			return cid==item.cid;
		}
		return super.equals(o);
	}
}
