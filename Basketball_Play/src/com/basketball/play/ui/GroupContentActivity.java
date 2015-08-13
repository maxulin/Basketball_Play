package com.basketball.play.ui;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.GetListener;

import com.basketball.play.R;
import com.basketball.play.bean.Group;
import com.basketball.play.bean.Site;
import com.basketball.play.bean.UserBean;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class GroupContentActivity extends BaseActivity {
	private TextView group_text_content;
	private TextView group_text_location;
	private TextView group_text_master;
	private TextView group_text_name;
	private TextView group_text_peoplecount;
	private TextView group_text_sitename;
	private ImageView group_img_large;
	private ImageView group_img_master;
	private Button group_button_add;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group_content_activity);
		initView();
		BmobQuery<Group> query = new BmobQuery<Group>();
		query.getObject(getApplicationContext(), getIntent().getStringExtra("obj_id"), new GetListener<Group>() {
			
			@Override
			public void onFailure(int arg0, String arg1) {
				ShowToast(arg1);
				
			}
			
			@Override
			public void onSuccess(Group arg0) {
				setMessage(arg0);
			}
		});
	}
	private void initView(){
		group_text_content =  (TextView) findViewById(R.id.group_text_content);
		group_text_location =  (TextView) findViewById(R.id.group_text_location);
		group_text_master =  (TextView) findViewById(R.id.group_text_master);
		group_text_name =  (TextView) findViewById(R.id.group_text_name);
		group_text_peoplecount =  (TextView) findViewById(R.id.group_text_peoplecount);
		group_text_sitename =  (TextView) findViewById(R.id.group_text_sitename);
		group_img_large =  (ImageView) findViewById(R.id.group_img_large);
		group_img_master =  (ImageView) findViewById(R.id.group_img_master);
		group_button_add = (Button) findViewById(R.id.group_button_add);
		imageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(null)//加载过程中显示的图片
		.showImageForEmptyUri(null)//加载内容为空显示的图片
		.showImageOnFail(null)//加载失败显示的图片
		.cacheInMemory(true).cacheOnDisc(true).considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565).displayer(new FadeInBitmapDisplayer(388)).build();
	}
	
	private void setMessage(Group group){
		BmobQuery<UserBean> query_u = new BmobQuery<UserBean>();
		query_u.getObject(this, group.getGroup_owner().getObjectId(), new GetListener<UserBean>() {

			@Override
			public void onSuccess(UserBean arg0) {
				group_text_master.setText(arg0.getNick());
				imageLoader.displayImage(arg0.getAvatar(), group_img_master, options);
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}
			
		});
		BmobQuery<Site> query_s = new BmobQuery<Site>();
		query_s.getObject(this, group.getSite().getObjectId(), new GetListener<Site>() {

			@Override
			public void onSuccess(Site arg0) {
				group_text_location.setText(arg0.getSite_address());
				group_text_sitename.setText(arg0.getSite_name());
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}
			
		});
		group_text_content.setText(group.getGroup_content());
		group_text_name.setText(group.getGroup_name());
		group_text_peoplecount.setText(group.getGroup_peoplecount()+"/30");
		imageLoader.displayImage(group.getGroup_img_address(), group_img_large, options);
	}
	
}
