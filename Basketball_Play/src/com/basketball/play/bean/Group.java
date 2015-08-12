package com.basketball.play.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobRelation;

public class Group extends BmobObject {

	/**
	 * 群组Bean
	 */
	private static final long serialVersionUID = 1L;
	
	private String group_name;
	private String group_img_address;
	private String group_content;
	private BmobRelation group_member;
	private UserBean group_owner;
	private Site site;
	private Integer group_peoplecount;
	public String getGroup_name() {
		return group_name;
	}
	public void setGroup_name(String group_name) {
		this.group_name = group_name;
	}
	public String getGroup_img_address() {
		return group_img_address;
	}
	public void setGroup_img_address(String group_img_address) {
		this.group_img_address = group_img_address;
	}
	public String getGroup_content() {
		return group_content;
	}
	public void setGroup_content(String group_content) {
		this.group_content = group_content;
	}
	public BmobRelation getGroup_member() {
		return group_member;
	}
	public void setGroup_member(BmobRelation group_member) {
		this.group_member = group_member;
	}
	public Site getSite() {
		return site;
	}
	public void setSite(Site site) {
		this.site = site;
	}
	public UserBean getGroup_owner() {
		return group_owner;
	}
	public void setGroup_owner(UserBean group_owner) {
		this.group_owner = group_owner;
	}
	public Integer getGroup_peoplecount() {
		return group_peoplecount;
	}
	public void setGroup_peoplecount(Integer group_peoplecount) {
		this.group_peoplecount = group_peoplecount;
	}
	
	
}
