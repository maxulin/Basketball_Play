package com.basketball.play.ui;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.GetListener;

import com.basketball.play.R;
import com.basketball.play.ResideMenuDemo.MainActivity;
import com.basketball.play.bean.Site;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class SiteContentActivity extends BaseActivity {
	private ImageView site_image;
	private TextView site_name;
	private TextView site_address;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private ImageView add_group;
	Intent intent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.site_content_activity);
		initView();
		intent = getIntent();
		String obj_id = intent.getStringExtra("obj_id");
		BmobQuery<Site> query = new BmobQuery<Site>();
		query.getObject(this, obj_id, new GetListener<Site>() {

		    @Override
		    public void onSuccess(Site object) {
		    	imageLoader.displayImage(object.getImg_address(), site_image, options);
		    	site_address.setText(object.getSite_address());
		    	site_name.setText(object.getSite_name());
		    }

		    @Override
		    public void onFailure(int code, String arg0) {
		    	
		    }

		});
	}
	
	public static void initImageLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.MAX_PRIORITY).denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();
        ImageLoader.getInstance().init(config);
    }
	
	OnClickListener ocl = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			Intent new_intent = new Intent(SiteContentActivity.this,GroupActivity.class);
			new_intent.putExtra("obj_id", intent.getStringExtra("obj_id"));
			startActivityForResult(new_intent, 0);
		}
	};
	
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		if(arg0==0){
			
		}
	};
	
	public void initView() {
		site_image = (ImageView) findViewById(R.id.site_content_img);
		site_name = (TextView) findViewById(R.id.site_content_name);
		site_address = (TextView) findViewById(R.id.site_content_address);
		add_group = (ImageView) findViewById(R.id.add_group);
		add_group.setOnClickListener(ocl);
		
		imageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(null)//加载过程中显示的图片
		.showImageForEmptyUri(null)//加载内容为空显示的图片
		.showImageOnFail(null)//加载失败显示的图片
		.cacheInMemory(true).cacheOnDisc(true).considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565).displayer(new FadeInBitmapDisplayer(388)).build();
	}
	
}
