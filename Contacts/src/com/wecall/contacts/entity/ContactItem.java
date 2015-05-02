package com.wecall.contacts.entity;


import java.util.HashSet;
import java.util.Set;

import android.annotation.SuppressLint;

import com.wecall.contacts.util.PinYin;

/**
 * ��ϵ�˵�ʵ����
 * 
 * @author xiaoxin
 * 
 */
@SuppressWarnings("rawtypes")
public class ContactItem implements Comparable {

	// ��ϵ��id
	private int id;
	// ��ϵ������
	private String name;
	// ����ĸ
	private String sortLetter;
	// �绰����
	private Set<String> phoneNumber;
	// ��ע
	private String note;
	// ��ַ
	private String address;
	// ��ǩ
	// û��Ҫ����label��ƴ��
	// private List<Label> labels;
	private Set<String> labels;
	
	// FIXME: ��ʵֻ��Ҫ�����ݿ��д���,û��Ҫ����������
	// ����ȫƴ
	private String fullPinyin;
	// ��������ĸ���
	private String simplePinyin;
	

	public ContactItem() {
		phoneNumber = new HashSet<String>();
		labels = new HashSet<String>();
	}

	public ContactItem(String name, Set<String> phoneNumber, String address,
			String note, Set<String> labels) {
		super();
		setName(name);
		setPhoneNumber(phoneNumber);
		setAddress(address);
		setNote(note);
		setLabels(labels);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
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

	public String getFullPinyin() {
		return fullPinyin;
	}

	public String getSimplePinyin() {
		return simplePinyin;
	}

	public Set<String> getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(Set<String> phoneNumber) {
		if(phoneNumber != null)
			this.phoneNumber = new HashSet<String>(phoneNumber);
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		if (note == null)
			this.note = null;
		else
			this.note = new String(note);
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		if (address == null)
			this.address = null;
		else
			this.address = new String(address);
	}

	public Set<String> getLabels() {
		return labels;
	}
	
//	/**
//	 * ֻ���ر�ǩ�����֣������ر�ǩ��
//	 * @return ArrayList<String>
//	 */
//	public ArrayList<String> getLabelNames() {
//		ArrayList<String> list = new ArrayList<String>();
//		for(Label l: this.labels)
//		{
//			list.add(l.getLname());
//		}
//		return list;
//	}

	public void setLabels(Set<String> labels) {
		if (labels != null)
			this.labels = new HashSet<String>(labels);
	}
	
//	/**
//	 * ֻ�ñ�ǩ������������ʼ�������ñ�ǩ��
//	 * @param labelsName
//	 */
//	public void setLabelNames(ArrayList<String> labelsName)
//	{
//		Label l = new Label();
//		for (String s: labelsName)
//		{
//			l.setLname(s);
//			this.labels.add(l);
//		}
//	}

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
	private void setSortLetter(String inputString){
		if(inputString.isEmpty()){
			this.sortLetter = "#";
			return;
		}
		String sortString = inputString.substring(0, 1).toUpperCase();
		// ������ʽ���ж�����ĸ�Ƿ���Ӣ����ĸ
		if (sortString.matches("[A-Z]")) {
			this.sortLetter = sortString;
		} else {
			this.sortLetter = "#";
		}
	}

//	public Map<String, Integer> contains(String str){
//		Map<String, Integer> map = new HashMap<String, Integer>();
//		if(name!=null&&name.indexOf(str)!=-1){
//			map.put("name",name.indexOf(str));
//		}
//		if(phoneNumber!=null&&phoneNumber.indexOf(str)!=-1){
//			map.put("phone",phoneNumber.indexOf(str));
//		}
//		if(address!=null&&address.indexOf(str)!=-1){
//			map.put("address",address.indexOf(str));
//		}
//		if(note!=null&&note.indexOf(str)!=-1){
//			map.put("note",note.indexOf(str));
//		}
//		if(fullPinyin!=null&&fullPinyin.indexOf(str)!=-1){
//			map.put("fullpinyin",fullPinyin.indexOf(str));
//		}
//		if(simplePinyin!=null&&simplePinyin.indexOf(str)!=-1){
//			map.put("simplepinyin",simplePinyin.indexOf(str));
//		}
//		return map;
//	}
}