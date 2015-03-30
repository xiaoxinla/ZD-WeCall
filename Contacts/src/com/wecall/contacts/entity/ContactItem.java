package com.wecall.contacts.entity;

import java.util.ArrayList;

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

	public ContactItem(int id, String name, String phoneNumber, String address,
			String note, ArrayList<String> labels, String sortLetter,
			String fullPinyin, String simplePinyin) {
		super();
		this.id = id;
		this.name = name;
		this.sortLetter = sortLetter;
		this.phoneNumber = phoneNumber;
		this.note = note;
		this.address = address;
		this.labels = labels;
		this.fullPinyin = fullPinyin;
		this.simplePinyin = simplePinyin;
	}

	public ContactItem(String name, String sortLetter, String phoneNumber,
			String note, String address, ArrayList<String> labels) {
		super();
		this.name = name;
		this.sortLetter = sortLetter;
		this.phoneNumber = phoneNumber;
		this.note = note;
		this.address = address;
		this.labels = labels;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		fullPinyin = PinYin.getPinYin(this.name);
		simplePinyin = PinYin.getSimplePinYin(this.name);
	}

	public void setSortLetter(String sortLetter) {
		this.sortLetter = sortLetter;
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
	public int compareTo(Object arg0) {
		ContactItem tmpItem = (ContactItem) arg0;
		if(tmpItem.getSortLetter().equals("#")&&!getSortLetter().equals("#")){
			return -1;
		}else if(!tmpItem.getSortLetter().equals("#")&&getSortLetter().equals("#")){
			return 1;
		}
		return getFullPinyin().compareTo(tmpItem.getFullPinyin());
	}

}
