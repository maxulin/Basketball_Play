package com.basketball.play.bean;

import cn.bmob.v3.BmobObject;

public class Label extends BmobObject{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String label_name;

	public String getLabel_name() {
		return label_name;
	}
	
	public void setLabel_name(String label_name) {
		this.label_name = label_name;
	}

	public Label(String label_name){
		this.label_name = label_name;
	}
	
}
