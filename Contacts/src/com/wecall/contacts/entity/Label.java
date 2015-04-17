package com.wecall.contacts.entity;

import com.wecall.contacts.util.PinYin;

/**
 *	标签实体类
 * @author xiaoxin
 * 2015-4-17
 */
public class Label {

	private String lname;
	private int cid;
	private String labelFullPinyin;
	private String labelSimplePinyin;

	public Label(){
		
	}
	
	public Label(String lname, int cid) {
		super();
		this.lname = lname;
		this.cid = cid;
		this.labelFullPinyin = PinYin.getPinYin(lname);
		this.labelSimplePinyin = PinYin.getSimplePinYin(lname);
	}

	public String getLname() {
		return lname;
	}

	public void setLname(String lname) {
		this.lname = lname;
	}

	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}

	public String getLabelFullPinyin() {
		return labelFullPinyin;
	}

	public String getLabelSimplePinyin() {
		return labelSimplePinyin;
	}

	@Override
	public String toString() {
		return "Label [lname=" + lname + ", cid=" + cid + ", labelFullPinyin="
				+ labelFullPinyin + ", labelSimplePinyin=" + labelSimplePinyin
				+ "]";
	}
	
}
