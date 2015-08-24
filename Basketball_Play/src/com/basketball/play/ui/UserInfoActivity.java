package com.basketball.play.ui;


import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;

import com.basketball.play.R;
import com.basketball.play.bean.Group;
import com.basketball.play.bean.UserBean;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class UserInfoActivity extends ActivityBase {
	private ImageView user_img_large,user_info_sex;
	private TextView user_text_name,user_textv_name,play_time,user_role,individual_resume,group_count,site_count;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_usermessage);
		initView();
		loadData();
	}
	
	private void initView(){
		user_img_large = (ImageView) findViewById(R.id.user_img_large);
		user_info_sex = (ImageView) findViewById(R.id.user_info_sex);
		user_text_name = (TextView) findViewById(R.id.user_text_name);
		user_textv_name = (TextView) findViewById(R.id.user_textv_name);
		play_time = (TextView) findViewById(R.id.play_time);
		user_role = (TextView) findViewById(R.id.user_role);
		individual_resume = (TextView) findViewById(R.id.individual_resume);
		group_count = (TextView) findViewById(R.id.group_count);
		site_count = (TextView) findViewById(R.id.site_count);
		imageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(null)//加载过程中显示的图片
		.showImageForEmptyUri(null)//加载内容为空显示的图片
		.showImageOnFail(null)//加载失败显示的图片
		.cacheInMemory(true).cacheOnDisc(true).considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565).displayer(new FadeInBitmapDisplayer(388)).build();
	}
	
	private void loadData(){
		Intent intent = getIntent();
		String obj_id = intent.getStringExtra("obj_id");
		BmobQuery<UserBean> user_info = new BmobQuery<UserBean>();
		user_info.getObject(this, obj_id, new GetListener<UserBean>() {
			
			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onSuccess(UserBean arg0) {
				imageLoader.displayImage(arg0.getAvatar(), user_img_large, options);
				user_text_name.setText(arg0.getNick());
				user_textv_name.setText(arg0.getNick());
				String role = null;
				int role_int = arg0.getRole();
				if (role_int == 0) {
					role = "控球后卫";
				}
				if (role_int == 1) {
					role = "得分后卫";
				}
				if (role_int == 2) {
					role = "中锋";
				}
				if (role_int == 3) {
					role = "小前锋";
				}
				if (role_int == 4) {
					role = "大前锋";
				}
				if(arg0.getSex()){
					user_info_sex.setBackgroundResource(R.drawable.male21);
				}else{
					user_info_sex.setBackgroundResource(R.drawable.female206);
				}
				user_role.setText(role);
			}
		});
		BmobQuery<Group> group_info = new BmobQuery<Group>();
		UserBean user = new UserBean();
		user.setObjectId(obj_id);
		group_info.addWhereRelatedTo("group_member", new BmobPointer(user));
		group_info.findObjects(this, new FindListener<Group>() {
			
			@Override
			public void onSuccess(List<Group> arg0) {
				group_count.setText(arg0.size());
			}
			
			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
}
