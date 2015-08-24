package com.basketball.play.ResideMenuDemo;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.basketball.play.ResideMenu.ResideMenu;
import com.basketball.play.ResideMenu.ResideMenuInfo;
import com.basketball.play.ResideMenu.ResideMenuItem;
import com.basketball.play.bean.Label;
import com.basketball.play.bean.Site;
import com.basketball.play.bean.UserBean;
import com.basketball.play.ui.BaseActivity;
import com.basketball.play.ui.MarkSiteActivity;
import com.basketball.play.ui.SiteContentActivity;
import com.basketball.play.ui.UserInfoActivity;
import com.basketball.play.view.CircularImage;
import com.basketball.play.view.FlowLayout;
import com.basketball.play.view.XListView;
import com.basketball.play.view.XListView.IXListViewListener;
import com.basketball.play.CustomApplcation;
import com.basketball.play.R;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class MainActivity extends BaseActivity implements View.OnClickListener {

	private ResideMenu resideMenu;

	private ResideMenuItem itemHuiyuan,itemQianbao,itemZhuangban,itemShoucang,itemXiangce;
	private ResideMenuInfo info;
	private boolean is_closed = false;
	private long mExitTime;

	private Button leftMenu;
	private Button sign;

	private List<Site> list;
	private Map<String,List<Label>> label_list;
	private Context context;
	private MyBaseAdapter adapter;
	private Handler mHandler;
	private DisplayImageOptions options;
	private LocationClient mLocationClient;
	private PullToRefreshListView mPullRefreshListView;

	private ImageLoader imageLoader;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setUpMenu();
		changeFragment(new NewsFragment());
		setListener();
		initImageLoader(getApplicationContext());
		context = this;
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.xListView);
		mPullRefreshListView.setMode(Mode.BOTH);
		mPullRefreshListView.setOnItemClickListener(oicl);
		initListview();
		mHandler = new Handler();
		mPullRefreshListView
		.setOnRefreshListener(new OnRefreshListener2<ListView>()
		{
			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView)
			{
				initIndicator();
				//这里写下拉刷新的任务
				refreshList();
//				new GetDataTask().execute();
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView)
			{	
				initIndicator();
				Log.e("TAG", "onPullUpToRefresh");
				//这里写上拉加载更多的任务
				refreshList();
			}
		});
	}
	
//	private class GetDataTask extends AsyncTask<Void, Void, List<Site>>
//	{
//
//		@Override
//		protected List<Site> doInBackground(Void... params)
//		{
//		}
//
//		@Override
//		protected void onPostExecute(List<Site> result)
//		{
//			
//		}
//	}
	String label = "";
	private void initIndicator()  
    {  
		
        ILoadingLayout startLabels = mPullRefreshListView  
                .getLoadingLayoutProxy(true, false);  
        startLabels.setPullLabel("往下拉...");// 刚下拉时，显示的提示  
        startLabels.setRefreshingLabel("玩命加载中...");// 刷新时  
        startLabels.setReleaseLabel("放开就刷新给你看...");// 下来达到一定距离时，显示的提示  
        startLabels.setLastUpdatedLabel(label);
  
        ILoadingLayout endLabels = mPullRefreshListView.getLoadingLayoutProxy(  
                false, true);
        endLabels.setPullLabel("往上拉...");// 刚下拉时，显示的提示  
        endLabels.setRefreshingLabel("正在加载...");// 刷新时  
        endLabels.setReleaseLabel("放开就加载给你看...");// 下来达到一定距离时，显示的提示 
        endLabels.setLastUpdatedLabel(label);
          
        label = DateUtils.formatDateTime(  
                getApplicationContext(),  
                System.currentTimeMillis(),  
                DateUtils.FORMAT_SHOW_TIME  
                        | DateUtils.FORMAT_SHOW_DATE  
                        | DateUtils.FORMAT_ABBREV_ALL);
        // Update the LastUpdatedLabel  
    } 
	
	@SuppressWarnings("deprecation")
	private void setUpMenu() {
		leftMenu = (Button) findViewById(R.id.title_bar_left_menu);
		// attach to current activity;
		resideMenu = new ResideMenu(this);
		resideMenu.setBackground(R.drawable.menu_background);
		resideMenu.attachToActivity(this);
		resideMenu.setMenuListener(menuListener);
		// valid scale factor is between 0.0f and 1.0f. leftmenu'width is
		// 150dip.
		resideMenu.setScaleValue(0.6f);
		// 禁止使用右侧菜单
		resideMenu.setDirectionDisable(ResideMenu.DIRECTION_RIGHT);

		// create menu items;
		itemHuiyuan = new ResideMenuItem(this, R.drawable.ic_launcher, "我的好友");
		itemQianbao = new ResideMenuItem(this, R.drawable.ic_launcher, "我的场地");
		itemZhuangban = new ResideMenuItem(this, R.drawable.ic_launcher, "消息");
		itemShoucang = new ResideMenuItem(this, R.drawable.ic_launcher, "吐槽");
		itemXiangce = new ResideMenuItem(this, R.drawable.ic_launcher, "关于去打球");

		resideMenu.addMenuItem(itemHuiyuan, ResideMenu.DIRECTION_LEFT);
		resideMenu.addMenuItem(itemQianbao, ResideMenu.DIRECTION_LEFT);
		resideMenu.addMenuItem(itemZhuangban, ResideMenu.DIRECTION_LEFT);
		resideMenu.addMenuItem(itemShoucang, ResideMenu.DIRECTION_LEFT);
		resideMenu.addMenuItem(itemXiangce, ResideMenu.DIRECTION_LEFT);

		sign = (Button) findViewById(R.id.sign);
		sign.setOnClickListener(sign_ocl);
		UserBean curruser = userManager.getCurrentUser(UserBean.class);
		String role = null;
		int role_int = curruser.getRole();
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
		info = new ResideMenuInfo(this, R.drawable.maxulin, curruser.getNick(),
				role);

	}

	private void setListener() {
		resideMenu.addMenuInfo(info);

		itemHuiyuan.setOnClickListener(this);
		itemQianbao.setOnClickListener(this);
		itemZhuangban.setOnClickListener(this);
		itemShoucang.setOnClickListener(this);
		itemXiangce.setOnClickListener(this);

		info.setOnClickListener(this);

		leftMenu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
			}
		});
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		return resideMenu.dispatchTouchEvent(ev);
	}

	@Override
	public void onClick(View view) {
		if (view == itemHuiyuan) {
			Intent intent = new Intent();
			intent.putExtra("flog", "好友");
			intent.setClass(getApplicationContext(), SettingActivity.class);
			startActivity(intent);
		} else if (view == itemQianbao) {
			Intent intent = new Intent();
			intent.putExtra("flog", "钱包");
			intent.setClass(getApplicationContext(), SettingActivity.class);
			startActivity(intent);
		} else if (view == itemZhuangban) {
			Intent intent = new Intent();
			intent.putExtra("flog", "装扮");
			intent.setClass(getApplicationContext(), SettingActivity.class);
			startActivity(intent);
		} else if (view == itemShoucang) {
			Intent intent = new Intent();
			intent.putExtra("flog", "收藏");
			intent.setClass(getApplicationContext(), SettingActivity.class);
			startActivity(intent);
		} else if (view == itemXiangce) {
			Intent intent = new Intent();
			intent.putExtra("flog", "相册");
			intent.setClass(getApplicationContext(), SettingActivity.class);
			startActivity(intent);
		} else if (view == info) {
			Intent intent = new Intent();
			intent.putExtra("flog", "个人信息");
			intent.setClass(getApplicationContext(), SettingActivity.class);
			startActivity(intent);
		}
	}

	private ResideMenu.OnMenuListener menuListener = new ResideMenu.OnMenuListener() {
		@Override
		public void openMenu() {
			is_closed = false;
			leftMenu.setVisibility(View.GONE);
		}

		@Override
		public void closeMenu() {
			is_closed = true;
			leftMenu.setVisibility(View.VISIBLE);
		}
	};

	private void changeFragment(Fragment targetFragment) {
		resideMenu.clearIgnoredViewList();
		// getSupportFragmentManager().beginTransaction()
		// .replace(R.id.main_fragment, targetFragment, "fragment")
		// .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
		// .commit();
	}

	// What good method is to access resideMenu？
	public ResideMenu getResideMenu() {
		return resideMenu;
	}

	OnClickListener sign_ocl = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(getApplicationContext(), MarkSiteActivity.class);
			startActivity(intent);
		}
	};

	// 监听手机上的BACK键
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 判断菜单是否关闭
			if (is_closed) {
				// 判断两次点击的时间间隔（默认设置为2秒）
				if ((System.currentTimeMillis() - mExitTime) > 2000) {
					Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();

					mExitTime = System.currentTimeMillis();
				} else {
					finish();
					System.exit(0);
					super.onBackPressed();
				}
			} else {
				resideMenu.closeMenu();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void initListview() {
		imageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(null)
				// 加载过程中显示的图片
				.showImageForEmptyUri(null)
				// 加载内容为空显示的图片
				.showImageOnFail(null)
				// 加载失败显示的图片
				.cacheInMemory(true).cacheOnDisc(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.displayer(new FadeInBitmapDisplayer(388)).build();
		getList();

	}

	/**
	 * 开启定位，更新当前用户的经纬度坐标
	 * 
	 * @Title: initLocClient
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	private void initLocClient() {
		mLocationClient = CustomApplcation.getInstance().mLocationClient;
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Battery_Saving);// 设置定位模式:低功耗模式
		option.setCoorType("bd09ll"); // 设置坐标类型:百度经纬度
		option.setScanSpan(1000);// 设置发起定位请求的间隔时间为1000ms:低于1000为手动定位一次，大于或等于1000则为定时定位
		option.setIsNeedAddress(true);// 需要包含地址信息
		mLocationClient.setLocOption(option);
		mLocationClient.start();
	}


	OnItemClickListener oicl = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			String site_id = list.get(position).getObjectId();
			Intent intent = new Intent(MainActivity.this,
					SiteContentActivity.class);
			intent.putExtra("obj_id", site_id);
			startActivity(intent);
		}
	};
	
	ProgressDialog progress;
	
	private void getList() {// 获取数据
		list = new ArrayList<Site>();
		label_list = new HashMap<String, List<Label>>();
		if (CustomApplcation.lastPoint == null) {
			ShowToast("您的网络太慢啦，请下拉重试或稍等一会再试!");
			initLocClient();
			adapter = new MyBaseAdapter(context, list,label_list);
			mPullRefreshListView.setAdapter(adapter);
			return;
		}
		progress = new ProgressDialog(MainActivity.this);
		
		progress.setMessage("正在加载场地数据");
		progress.setCanceledOnTouchOutside(false);
		progress.setCancelable(true);
		progress.show();
		BmobQuery<Site> site_query = new BmobQuery<Site>();
		site_query.addWhereNear("site_location", new BmobGeoPoint(
				CustomApplcation.lastPoint.getLongitude(),
				CustomApplcation.lastPoint.getLatitude()));
		site_query.setLimit(10); // 获取最接近用户地点的10条数据
		site_query.include("mark_user");
		site_query.findObjects(this, new FindListener<Site>() {
			@Override
			public void onSuccess(List<Site> arg0) {
				list = arg0;
				findLabels(arg0);
				
			}

			@Override
			public void onError(int code, String msg) {
				adapter = new MyBaseAdapter(context, list,label_list);
				mPullRefreshListView.setAdapter(adapter);
				progress.dismiss();
				if(code==9010){
					ShowToast("服务器连接超时，请下拉重试！");
				}
				
			}

		});

	}
	
	
	private void findLabels(final List<Site> sites){
		BmobQuery<Label> query = new BmobQuery<Label>();
		Site site = new Site();
		for(int i=0;i<sites.size();i++){
			site.setObjectId(sites.get(i).getObjectId());
			query.addWhereRelatedTo("site_labels", new BmobPointer(site));
			query.setLimit(3);
			query.findObjects(this, new FindListener<Label>() {
				
				@Override
				public void onSuccess(List<Label> arg0) {
					addLabelMap(arg0);
				}
				
				@Override
				public void onError(int arg0, String arg1) {
					// TODO Auto-generated method stub
					
				}
			});
		}
		
	}
	int i=0;
	private void addLabelMap(List<Label> labels){
		label_list.put(list.get(i).getObjectId(), labels);
		i++;
		if(i==list.size()){
			adapter = new MyBaseAdapter(context, list,label_list);
			mPullRefreshListView.setAdapter(adapter);
			progress.dismiss();
			i=0;
		}
	}
	

	private List<Site> refreshList() {// 获取最新的信息
		if(CustomApplcation.lastPoint==null){
			ShowToast("正在定位中，请稍等一会再试试！");
			initLocClient();
		}else{
			BmobQuery<Site> site_query = new BmobQuery<Site>();
			site_query.addWhereNear("site_location", new BmobGeoPoint(
					CustomApplcation.lastPoint.getLongitude(),
					CustomApplcation.lastPoint.getLatitude()));
			site_query.setLimit(10); // 获取最接近用户地点的10条数据
			site_query.include("mark_user");
			site_query.findObjects(this, new FindListener<Site>() {
				@Override
				public void onSuccess(List<Site> arg0) {
					list = arg0;
					adapter.notifyDataSetChanged();
					// Call onRefreshComplete when the list has been refreshed.
					mPullRefreshListView.onRefreshComplete();
				}

				@Override
				public void onError(int code, String msg) {
					if(code==9010){
						ShowToast("服务器连接超时，请重试！");
					}
					// Call onRefreshComplete when the list has been refreshed.
					mPullRefreshListView.onRefreshComplete();
				}

			});
		}
		return list;
	}

	private void getmoreList() {// 获取以前的信息
		// list.add("more Message");
		// list.add("more Message");
		// list.add("more Message");
		// list.add("more Message");
		// list.add("more Message");
		// list.add("more Message");
	}

	public class MyBaseAdapter extends BaseAdapter {
		private Context mContext;
		private LayoutInflater mInflater;
		private List<Site> mlist;
		private Map<String, List<Label>> llist;

		public MyBaseAdapter(Context context, List<Site> list,Map<String, List<Label>> label_list) {
			this.mInflater = LayoutInflater.from(context);
			this.mContext = context;
			this.mlist = list;
			this.llist = label_list;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			convertView = mInflater.inflate(R.layout.site_list_item, null);
			TextView site_name = (TextView) convertView
					.findViewById(R.id.site_main_name);
			site_name.setText(mlist.get(position).getSite_name());
			TextView site_distance = (TextView) convertView
					.findViewById(R.id.site_distance);
			double distance = distance(mlist.get(position).getSite_location().getLongitude(), mlist.get(position).getSite_location().getLatitude(), CustomApplcation.lastPoint.getLongitude(), CustomApplcation.lastPoint.getLatitude());
			if(distance<=100){
				site_distance.setText("小于0.1km");
			}else{
				DecimalFormat df = new DecimalFormat("0.0");
				String result = df.format(distance/1000);
				site_distance.setText("约"+result+"km");
			}
			
			site_name.setText(mlist.get(position).getSite_name());
			TextView site_address = (TextView) convertView
					.findViewById(R.id.site_main_address);
			site_address.setText(mlist.get(position).getSite_address());
			
			FlowLayout site_labels = (FlowLayout) convertView.findViewById(R.id.main_group_labels);
			List<Label> add_labels = label_list.get(mlist.get(position).getObjectId());
			for(int i=0;i<add_labels.size();i++){
				AddTag(add_labels.get(i).getLabel_name(), i,site_labels);
			}
			
			CircularImage user_image = (CircularImage) convertView
					.findViewById(R.id.cover_user_photo);
			imageLoader.displayImage(mlist.get(position).getMark_user()
					.getAvatar().toString(), user_image, options);
			user_image.setContentDescription(mlist.get(position).getMark_user()
					.getObjectId().toString());
			user_image.setOnClickListener(user_image_ocl);
			ImageView site_image = (ImageView) convertView
					.findViewById(R.id.site_image);
			imageLoader.displayImage(mlist.get(position).getImg_address(),
					site_image, options);
			return convertView;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public int getCount() {
			return mlist.size();
		}
	}

	OnClickListener user_image_ocl = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(MainActivity.this, UserInfoActivity.class);
			intent.putExtra("obj_id", v.getContentDescription().toString());
			startActivity(intent);
		}
	};

	public static void initImageLoader(Context context) {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.MAX_PRIORITY)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs() // Remove for release app
				.build();
		ImageLoader.getInstance().init(config);
	}
	
	/**
	 * 计算地球上任意两点(经纬度)距离
	 * 
	 * @param long1
	 *            第一点经度
	 * @param lat1
	 *            第一点纬度
	 * @param long2
	 *            第二点经度
	 * @param lat2
	 *            第二点纬度
	 * @return 返回距离 单位：米
	 */
	public double distance(double long1, double lat1, double long2,
			double lat2) {
		double a, b, R;
		R = 6378137; // 地球半径
		lat1 = lat1 * Math.PI / 180.0;
		lat2 = lat2 * Math.PI / 180.0;
		a = lat1 - lat2;
		b = (long1 - long2) * Math.PI / 180.0;
		double d;
		double sa2, sb2;
		sa2 = Math.sin(a / 2.0);
		sb2 = Math.sin(b / 2.0);
		d = 2
				* R
				* Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1)
						* Math.cos(lat2) * sb2 * sb2));
		return d;
	}
	
	
	/**
	 * 添加标签
	 * @param tag
	 * @param i
	 */
	@SuppressLint("NewApi")
	public void AddTag(String tag, int i,FlowLayout site_labels) {
		final TextView mTag = new TextView(MainActivity.this);
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
	
	
	// /**
	// * 加载图片
	// *
	// * @author Administrator
	// *
	// */
	// public class ImageLoader {
	//
	// public void loadImage(String url, CircularImage imageView) {
	// if (cancelPotentialDownload(url, imageView)) {
	// BitmapDownloaderTask task = new BitmapDownloaderTask(imageView);
	// DownloadedDrawable downloadedDrawable = new DownloadedDrawable(task);
	// imageView.setImageDrawable(downloadedDrawable);
	// task.execute(url);
	// }
	// }
	// }
	//
	// /**
	// * 异步下载图片
	// *
	// * @author Administrator
	// *
	// */
	// class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {
	// private String url;
	// private final WeakReference<CircularImage> imageViewReference;
	//
	// public BitmapDownloaderTask(CircularImage imageView) {
	// imageViewReference = new WeakReference<CircularImage>(imageView);
	// }
	//
	// @Override
	// // Once the image is downloaded, associates it to the imageView
	// protected void onPostExecute(Bitmap bitmap) {
	// if (isCancelled()) {
	// bitmap = null;
	// }
	//
	// if (imageViewReference != null) {
	// CircularImage imageView = imageViewReference.get();
	// BitmapDownloaderTask bitmapDownloaderTask =
	// getBitmapDownloaderTask(imageView);
	// // Change bitmap only if this process is still associated with
	// // it
	// if (this == bitmapDownloaderTask) {
	// imageView.setImageBitmap(bitmap);
	// }
	// }
	// }
	//
	// @Override
	// protected Bitmap doInBackground(String... params) {
	// return downloadBitmap(params[0]);
	// }
	// }
	//
	// /**
	// * http请求下载网络图片
	// *
	// * @param url
	// * @return
	// */
	// static Bitmap downloadBitmap(String url) {
	// final AndroidHttpClient client = AndroidHttpClient
	// .newInstance("Android");
	// final HttpGet getRequest = new HttpGet(url);
	//
	// try {
	// HttpResponse response = client.execute(getRequest);
	// final int statusCode = response.getStatusLine().getStatusCode();
	// if (statusCode != HttpStatus.SC_OK) {
	// Log.w("ImageDownloader", "Error " + statusCode
	// + " while retrieving bitmap from " + url);
	// return null;
	// }
	//
	// final HttpEntity entity = response.getEntity();
	// if (entity != null) {
	// InputStream inputStream = null;
	// try {
	// inputStream = entity.getContent();
	// final Bitmap bitmap = BitmapFactory
	// .decodeStream(inputStream);
	// return bitmap;
	// } finally {
	// if (inputStream != null) {
	// inputStream.close();
	// }
	// entity.consumeContent();
	// }
	// }
	// } catch (Exception e) {
	// // Could provide a more explicit error message for IOException or
	// // IllegalStateException
	// getRequest.abort();
	// } finally {
	// if (client != null) {
	// client.close();
	// }
	// }
	// return null;
	// }
	//
	// /**
	// * 判断imageview是否有线程在下载图片
	// *
	// * @param url
	// * @param imageView
	// * @return
	// */
	// private static boolean cancelPotentialDownload(String url,
	// CircularImage imageView) {
	// BitmapDownloaderTask bitmapDownloaderTask =
	// getBitmapDownloaderTask(imageView);
	//
	// if (bitmapDownloaderTask != null) {
	// String bitmapUrl = bitmapDownloaderTask.url;
	// if ((bitmapUrl == null) || (!bitmapUrl.equals(url))) {
	// bitmapDownloaderTask.cancel(true);
	// } else {
	// // 相同的url已经在下载中.
	// return false;
	// }
	// }
	// return true;
	// }
	//
	// /**
	// * 获取图片下载异步线程状态
	// *
	// * @param imageView
	// * @return
	// */
	// private static BitmapDownloaderTask getBitmapDownloaderTask(
	// CircularImage imageView) {
	// if (imageView != null) {
	// Drawable drawable = imageView.getDrawable();
	// if (drawable instanceof DownloadedDrawable) {
	// DownloadedDrawable downloadedDrawable = (DownloadedDrawable) drawable;
	// return downloadedDrawable.getBitmapDownloaderTask();
	// }
	// }
	// return null;
	// }
	//
	// /**
	// * 为imageview记录下载任务
	// *
	// * @author Administrator
	// *
	// */
	// static class DownloadedDrawable extends ColorDrawable {
	// private final WeakReference<BitmapDownloaderTask>
	// bitmapDownloaderTaskReference;
	//
	// public DownloadedDrawable(BitmapDownloaderTask bitmapDownloaderTask) {
	// super(Color.BLACK);
	// bitmapDownloaderTaskReference = new WeakReference<BitmapDownloaderTask>(
	// bitmapDownloaderTask);
	// }
	//
	// public BitmapDownloaderTask getBitmapDownloaderTask() {
	// return bitmapDownloaderTaskReference.get();
	// }
	// }

}
