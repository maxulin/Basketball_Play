package com.basketball.play.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.datatype.BmobRelation;

public class Site extends BmobObject {

	/**
	 * 场地表
	 */
	private static final long serialVersionUID = 1L;
	//图片地址
	private String img_address;
	//场地名称
	private String site_name;
	//地理位置
	private BmobGeoPoint site_location;
	//球场地址
	private String site_address;
	//球场标签
	private BmobRelation site_labels;
	//标记球场的用户
	private UserBean mark_user;
	public String getImg_address() {
		return img_address;
	}
	public void setImg_address(String img_address) {
		this.img_address = img_address;
	}
	public String getSite_name() {
		return site_name;
	}
	public void setSite_name(String site_name) {
		this.site_name = site_name;
	}
	public BmobGeoPoint getSite_location() {
		return site_location;
	}
	public void setSite_location(BmobGeoPoint site_location) {
		this.site_location = site_location;
	}
	public String getSite_address() {
		return site_address;
	}
	public void setSite_address(String site_address) {
		this.site_address = site_address;
	}
	public BmobRelation getSite_labels() {
		return site_labels;
	}
	public void setSite_labels(BmobRelation site_labels) {
		this.site_labels = site_labels;
	}
	public UserBean getMark_user() {
		return mark_user;
	}
	public void setMark_user(UserBean mark_user) {
		this.mark_user = mark_user;
	}
	
	
	
	
}
