package com.basketball.play.ui;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;

import com.basketball.play.R;
import com.basketball.play.ResideMenuDemo.MainActivity;
import com.basketball.play.bean.Group;
import com.basketball.play.bean.Label;
import com.basketball.play.bean.Site;
import com.basketball.play.view.FlowLayout;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class SiteContentActivity extends BaseActivity {
	private ImageView site_image;
	private TextView site_name,map_view;
	private TextView site_address;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private ImageView add_group;
	private ListView group_list;
	private List<Group> list;
	private Context context;
	private MyBaseAdapter adapter;
	private Site this_site;
	private FlowLayout flowLayout;
	Intent intent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.site_content_activity);
		initView();
		intent = getIntent();
		context = this;
		String obj_id = intent.getStringExtra("obj_id");
		BmobQuery<Site> query = new BmobQuery<Site>();
		query.getObject(this, obj_id, new GetListener<Site>() {

		    @Override
		    public void onSuccess(Site object) {
		    	imageLoader.displayImage(object.getImg_address(), site_image, options);
		    	site_address.setText(object.getSite_address());
		    	site_name.setText(object.getSite_name());
		    	this_site = object;
		    	queryLabels(object);
		    	queryGroup(object);
		    }

		    @Override
		    public void onFailure(int code, String arg0) {
		    	
		    }

		});
		
	}
	
	private void queryLabels(Site site){
		BmobQuery<Label> queryLabel = new BmobQuery<Label>();
		queryLabel.addWhereRelatedTo("site_labels", new BmobPointer(site));
		queryLabel.findObjects(this, new FindListener<Label>() {
			
			@Override
			public void onSuccess(List<Label> arg0) {
				for(int i=0;i<arg0.size();i++){
					AddTag(arg0.get(i).getLabel_name(),i,flowLayout);
				}
			}
			
			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	/**
	 * 添加标签
	 * @param tag
	 * @param i
	 */
	@SuppressLint("NewApi")
	public void AddTag(String tag, int i,FlowLayout site_labels) {
		final TextView mTag = new TextView(SiteContentActivity.this);
		mTag.setText("  " + tag + "    ");
		// mTag.setPadding(0, 15, 40, 15);
		mTag.setGravity(Gravity.CENTER);
		mTag.setTextSize(12);
		mTag.setBackground(getResources().getDrawable(R.drawable.mylable));
		// mTag.setBackgroundColor(getResources().getColor(R.color.black));
		mTag.setTextColor(Color.BLACK);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 40);
		params.setMargins(10, 10, 20, 10);
		site_labels.addView(mTag, i, params);

	}
	
	private void queryGroup(Site site){
		BmobQuery<Group> queryGroup = new BmobQuery<Group>();
		queryGroup.addWhereEqualTo("site", site);
		queryGroup.setLimit(30);
		queryGroup.findObjects(this, new FindListener<Group>() {
			
			@Override
			public void onSuccess(List<Group> arg0) {
				list = arg0;
				adapter = new MyBaseAdapter(context, arg0);
				group_list.setAdapter(adapter);
				
			}
			
			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
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
			if(arg0.getId()==R.id.add_group){
				Intent new_intent = new Intent(SiteContentActivity.this,GroupActivity.class);
				new_intent.putExtra("obj_id", intent.getStringExtra("obj_id"));
				startActivityForResult(new_intent, 0);
			}
			if(arg0.getId()==R.id.map_view){
				Intent new_intent = new Intent(SiteContentActivity.this,MapActivity.class);
				BmobGeoPoint geo = this_site.getSite_location();
				new_intent.putExtra("geo_point", geo);
				startActivity(new_intent);
			}
			
		}
	};
	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		if(arg0==0){
			queryGroup(this_site);
		}
	};
	
	public void initView() {
		site_image = (ImageView) findViewById(R.id.site_content_img);
		site_name = (TextView) findViewById(R.id.site_content_name);
		site_address = (TextView) findViewById(R.id.site_content_address);
		add_group = (ImageView) findViewById(R.id.add_group);
		add_group.setOnClickListener(ocl);
		group_list = (ListView) findViewById(R.id.site_group_list);
		group_list.setOnItemClickListener(oicl);
		flowLayout = (FlowLayout) findViewById(R.id.site_content_label);
		map_view = (TextView) findViewById(R.id.map_view);
		map_view.setOnClickListener(ocl);
		imageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(null)//加载过程中显示的图片
		.showImageForEmptyUri(null)//加载内容为空显示的图片
		.showImageOnFail(null)//加载失败显示的图片
		.cacheInMemory(true).cacheOnDisc(true).considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565).displayer(new FadeInBitmapDisplayer(388)).build();
	}
	
	public class MyBaseAdapter extends BaseAdapter{
		private Context mContext;
		private LayoutInflater mInflater;
		private List<Group> mlist;
		
		
		public MyBaseAdapter(Context context, List<Group> list) {
			this.mInflater = LayoutInflater.from(context);
			this.mContext = context;
			this.mlist = list;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mlist.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			arg1 = mInflater.inflate(R.layout.site_grouplist_item, null);
			ImageView group_img = (ImageView) arg1.findViewById(R.id.site_group_img);
			imageLoader.displayImage(mlist.get(arg0).getGroup_img_address()
					, group_img, options);
			TextView group_name = (TextView) arg1.findViewById(R.id.site_group_name);
			group_name.setText(mlist.get(arg0).getGroup_name());
			TextView group_content = (TextView) arg1.findViewById(R.id.site_group_content);
			group_content.setText(mlist.get(arg0).getGroup_content());
			TextView group_peopleCount = (TextView) arg1.findViewById(R.id.site_group_peoplecount);
			group_peopleCount.setText(mlist.get(arg0).getGroup_peoplecount()+"/50");
			return arg1;
		}
		
	}
	
	OnItemClickListener oicl = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			String group_id = list.get(position).getObjectId();
			Intent intent = new Intent(SiteContentActivity.this,
					GroupContentActivity.class);
			intent.putExtra("obj_id", group_id);
			startActivity(intent);
		}
	};
	
}
