package com.wecall.contacts.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import com.wecall.contacts.util.PinYin;

/**
 * 联系人的实体类
 * 
 * @author xiaoxin
 * 
 */
@SuppressWarnings("rawtypes")
public class ContactItem implements Comparable {

	// 联系人id
	private int id;
	// 联系人姓名
	private String name;
	// 首字母
	private String sortLetter;
	// 电话号码
	private String phoneNumber;
	// 备注
	private String note;
	// 地址
	private String address;
	// 标签
	private ArrayList<String> labels;
	// 姓名全拼
	private String fullPinyin;
	// 姓名首字母组合
	private String simplePinyin;

	public ContactItem() {

	}

	public ContactItem(String name, String phoneNumber, String address,
			String note, ArrayList<String> labels) {
		super();
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.note = note;
		this.address = address;
		this.labels = labels;

		fullPinyin = PinYin.getPinYin(this.name);
		simplePinyin = PinYin.getSimplePinYin(this.name);
		setSortLetter(fullPinyin);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		fullPinyin = PinYin.getPinYin(this.name);
		simplePinyin = PinYin.getSimplePinYin(this.name);
		setSortLetter(fullPinyin);
	}

	public String getSortLetter() {
		return sortLetter;
	}

	public String getFullPinyin() {
		return fullPinyin;
	}

	public String getSimplePinyin() {
		return simplePinyin;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public ArrayList<String> getLabels() {
		return labels;
	}

	public void setLabels(ArrayList<String> labels) {
		this.labels = labels;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "ContactItem [id=" + id + ", name=" + name + ", sortLetter="
				+ sortLetter + ", phoneNumber=" + phoneNumber + ", note="
				+ note + ", address=" + address + ", labels=" + labels
				+ ", fullPinyin=" + fullPinyin + ", simplePinyin="
				+ simplePinyin + "]";
	}

	@Override
	public int compareTo(Object arg0) {
		ContactItem tmpItem = (ContactItem) arg0;
		if (tmpItem.getSortLetter().equals("#") && !getSortLetter().equals("#")) {
			return -1;
		} else if (!tmpItem.getSortLetter().equals("#")
				&& getSortLetter().equals("#")) {
			return 1;
		}
		return getFullPinyin().compareTo(tmpItem.getFullPinyin());
	}

	@SuppressLint("DefaultLocale")
	private void setSortLetter(String inputString) {
		if (inputString.isEmpty()) {
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
		if(phoneNumber!=null&&phoneNumber.indexOf(str)!=-1){
			map.put("phone",phoneNumber.indexOf(str));
		}
		if(address!=null&&address.indexOf(str)!=-1){
			map.put("address",address.indexOf(str));
		}
		if(note!=null&&note.indexOf(str)!=-1){
			map.put("note",note.indexOf(str));
		}
		if(fullPinyin!=null&&fullPinyin.indexOf(str)!=-1){
			map.put("fullpinyin",fullPinyin.indexOf(str));
		}
		if(simplePinyin!=null&&simplePinyin.indexOf(str)!=-1){
			map.put("simplepinyin",simplePinyin.indexOf(str));
		}
		return map;
	}
}
