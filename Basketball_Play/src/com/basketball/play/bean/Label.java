package com.basketball.play.bean;

import android.os.Parcel;
import android.os.Parcelable;
import cn.bmob.v3.BmobObject;

public class Label extends BmobObject implements Parcelable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String label_name;

	public String getLabel_name() {
		return label_name;
	}
	
	public Label(String label_name){
		this.label_name = label_name;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(label_name);
	}
	
	public static final Parcelable.Creator<Label> CREATOR = new Creator<Label>() {
		
		@Override
		public Label[] newArray(int size) {
			return new Label[size];
		}
		
		@Override
		public Label createFromParcel(Parcel source) {
			  Label label=  new Label("");
		      label.label_name = source.readString();
		      return label;

		}
	};
	
}
