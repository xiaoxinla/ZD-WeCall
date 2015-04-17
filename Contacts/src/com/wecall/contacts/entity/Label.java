package com.wecall.contacts.entity;

import com.wecall.contacts.util.PinYin;

/**
 *	标签实体类
 * @author xiaoxin
 * 2015-4-17
 */
public class Label {

	private String lname;
	private String labelFullPinyin;
	private String labelSimplePinyin;

	public Label(){
		
	}
	
	public Label(String lname) {
		super();
		this.lname = lname;
		this.labelFullPinyin = PinYin.getPinYin(lname);
		this.labelSimplePinyin = PinYin.getSimplePinYin(lname);
	}

	public String getLname() {
		return lname;
	}

	public void setLname(String lname) {
		if (lname == null)
			this.lname = null;
		else
			this.lname = lname;
	}

	public String getLabelFullPinyin() {
		return labelFullPinyin;
	}

	public String getLabelSimplePinyin() {
		return labelSimplePinyin;
	}

	@Override
	public String toString() {
		return "Label [lname=" + lname + ", labelFullPinyin="
				+ labelFullPinyin + ", labelSimplePinyin=" + labelSimplePinyin
				+ "]";
	}
	
}
