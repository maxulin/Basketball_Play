package com.basketball.play.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.SaveListener;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.basketball.play.bean.Label;
import com.basketball.play.bean.Site;
import com.basketball.play.bean.UserBean;
import com.basketball.play.util.PhotoPickUtil;
import com.basketball.play.util.PhotoPickUtil.OnPhotoPickedlistener;
import com.basketball.play.view.FlowLayout;
import com.basketball.play.CustomApplcation;
import com.basketball.play.R;
import com.bmob.BTPFileResponse;
import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadListener;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MarkSiteActivity extends BaseActivity{
	private LinearLayout add_label;
	FlowLayout tag_vessel;
	List<Label> label_list = new ArrayList<Label>();
	TextView address_geo;
	ImageView site_image;
	Button site_cancel;
	Button site_commit;
	EditText site_name;
	public LocationClient mLocationClient;
	private BmobGeoPoint lastPoint = new BmobGeoPoint();// 上一次定位到的经纬度
	MyHandler myHandler;
	PhotoPickUtil photoPickUtil;
	private static final String IMAGE_FILE_LOCATION = "file:///sdcard/temp.jpg";
	private Uri imageUri;//to store the big bitmap
	private String site_img;
	private String upload_site_img = "";
	int j = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mark_site_activity);
		initView();
		initLocClient();
		add_label.setOnClickListener(lab_ocl);
		site_image.setOnClickListener(image_ocl);
		site_commit.setOnClickListener(com_ocl);
		myHandler = new MyHandler();
		MyThread thread = new MyThread();
		new Thread(thread).start();
	}
	OnClickListener lab_ocl = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(MarkSiteActivity.this,LabelActivity.class);
			intent.putExtra("add_labels",(Serializable)label_list);
			startActivityForResult(intent, 0);
		}
	};
	
	public void initView(){
		add_label = (LinearLayout) findViewById(R.id.add_label);
		tag_vessel = (FlowLayout) findViewById(R.id.tag_vessel_ed);
		address_geo = (TextView) findViewById(R.id.site_address_geo);
		site_image = (ImageView) findViewById(R.id.site_image);
		site_cancel = (Button) findViewById(R.id.site_cancel);
		site_commit = (Button) findViewById(R.id.site_commit);
		site_name = (EditText) findViewById(R.id.site_name);
		photoPickUtil = new PhotoPickUtil(this, oppl);
		imageUri = Uri.parse(IMAGE_FILE_LOCATION);
	}
	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		if(arg0 == 0 && arg1==0){
			if(arg2!=null){
				tag_vessel.removeAllViewsInLayout();
				label_list = (List<Label>)arg2.getSerializableExtra("add_labels");
				for(int i=0;i<label_list.size();i++){
					AddTag(label_list.get(i).getLabel_name(), i);
				}
			}
		}
		if(arg0 == 2){
			cropImageUri(imageUri, 400, 300, 3);  
		}
		if(arg0 == 3){
			if(imageUri != null){
				Bitmap bitmap = decodeUriAsBitmap(imageUri);
				site_image.setImageBitmap(bitmap);
				saveMyBitmap(bitmap,"site_img");
				upload();
			}  
		}
	}
	
	/**
	 * 图片保存到sd卡
	 * @param mBitmap
	 * @param bitName
	 */
	public void saveMyBitmap(Bitmap mBitmap,String bitName)  {
		String img_file = "/sdcard/"+bitName + ".png";
        File f = new File(img_file);
        FileOutputStream fOut = null;
        try {
                fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
                e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
                fOut.flush();
        } catch (IOException e) {
                e.printStackTrace();
        }
        try {
                fOut.close();
                site_img = img_file;
        } catch (IOException e) {
                e.printStackTrace();
        }
}
	
	/**
	 * 裁剪图片方法
	 * @param uri
	 * @param outputX
	 * @param outputY
	 * @param requestCode
	 */
	private void cropImageUri(Uri uri, int outputX, int outputY, int requestCode){
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 2);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("return-data", false);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		startActivityForResult(intent, requestCode);
	}
    
	private Bitmap decodeUriAsBitmap(Uri uri){
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}
	
	/**
	 * 添加标签
	 * @param tag
	 * @param i
	 */
	@SuppressLint("NewApi")
	public void AddTag(String tag, int i) {
		final TextView mTag = new TextView(MarkSiteActivity.this);
		mTag.setText("  " + tag + "    ");
		// mTag.setPadding(0, 15, 40, 15);
		mTag.setGravity(Gravity.CENTER);
		mTag.setTextSize(14);
		mTag.setBackground(getResources().getDrawable(R.drawable.mylable));
		// mTag.setBackgroundColor(getResources().getColor(R.color.black));
		mTag.setTextColor(Color.BLACK);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 40);
		params.setMargins(10, 10, 20, 10);
		tag_vessel.addView(mTag, i, params);

		mTag.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// 长按标签删除操作
						tag_vessel.removeView(mTag);
						for (int k = 0; k < label_list.size(); k++) {
							Log.v("==", mTag.getText().toString() + "==" + label_list.get(k).toString());
							if (mTag.getText().toString().replaceAll(" ", "")
									.equals(label_list.get(k).getLabel_name().toString().replaceAll(" ", ""))) {
								label_list.remove(k);
							}
						}
						return true;
					}
		});
	}
	
	class MyHandler extends Handler{
		public MyHandler() {
			// TODO Auto-generated constructor stub
		}
		MyHandler(Looper l){
			super(l);
		}
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			Bundle b = msg.getData();
			String address = b.getString("address");
			address_geo.setText(address);
		}
	}
	
	/**
	 * 开启定位，更新当前用户的经纬度坐标
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
	
	class MyThread implements Runnable{

		@Override
		public void run() {
			while(CustomApplcation.address.getAddress()==null || "nullnullnull".equals(CustomApplcation.address.getAddress())){
				try {	
						mLocationClient.start();
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					
				}
					
			}
			Message msg = new Message(); 
            Bundle b = new Bundle();// 存放数据 
            b.putString("address",CustomApplcation.address.getAddress()); 
            lastPoint.setLatitude(CustomApplcation.lastPoint.getLatitude());
            lastPoint.setLongitude(CustomApplcation.lastPoint.getLongitude());
            msg.setData(b); 
            MarkSiteActivity.this.myHandler.sendMessage(msg); // 向Handler
		}
		
	}
	
	OnPhotoPickedlistener oppl = new OnPhotoPickedlistener() {
		
		@Override
		public void photoPicked(String path, Bitmap bmp) {
			
		}
	};
	
	/**
	 * 图片点击监听
	 */
	OnClickListener image_ocl = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//action is capture
			intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			startActivityForResult(intent, 2);
		}
	};
	
	/**
	 * 场地标记数据提交
	 */
	OnClickListener com_ocl = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			siteCommit();
		}
	};
	
	String downLoadUrl = "";
	/**
	 * @Description:单一文件上传
	 * @param  
	 * @return void
	 * @throws
	 */
	ProgressDialog dialog =null;
	ProgressDialog dialog_two =null;

	private void upload(){
		dialog = new ProgressDialog(MarkSiteActivity.this);
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);                 
		dialog.setTitle("上传中...");
		dialog.setIndeterminate(false);               
		dialog.setCancelable(true);       
		dialog.setCanceledOnTouchOutside(false);  
		dialog.show();
		BTPFileResponse response = BmobProFile.getInstance(MarkSiteActivity.this).upload(site_img, new UploadListener() {

			@Override
			public void onSuccess(String fileName,String url,BmobFile file) {
				// TODO Auto-generated method stub
				Log.i("smile", "新版文件服务的fileName = "+fileName+",新版文件服务的url ="+url);
				upload_site_img =BmobProFile.getInstance(getApplicationContext()).signURL(fileName,url,"ffa7150668e1dc6b25eae12607d1cecb",0,null);
				if(file!=null){
					Log.i("smile", "兼容旧版文件服务的源文件名 = "+file.getFilename()+",文件地址url = "+file.getUrl());
				}
				dialog.dismiss();
				//ShowToast("图片上传成功");
			}

			@Override
			public void onProgress(int ratio) {
				// TODO Auto-generated method stub
				Log.i("smile","MainActivity -onProgress :"+ratio);
				dialog.setProgress(ratio);
			}

			@Override
			public void onError(int statuscode, String errormsg) {
				// TODO Auto-generated method stub
				//showLog("MainActivity -onError :"+statuscode +"--"+errormsg);
				dialog.dismiss();
				ShowToast("上传出错："+errormsg);
			}

		});

	}
	
	public void siteCommit(){
		Site site = new Site();
		dialog_two = new ProgressDialog(MarkSiteActivity.this);
		BmobRelation relation = new BmobRelation();
		site.setImg_address(upload_site_img);
		site.setMark_user(userManager.getCurrentUser(UserBean.class));
		site.setSite_address(address_geo.getText().toString());
		site.setSite_location(lastPoint);
		site.setSite_name(site_name.getText().toString());
		for(int i = 0;i<label_list.size();i++){
			relation.add(label_list.get(i));
		}
		site.setSite_labels(relation);
		dialog_two.setTitle("上传中...");
		dialog_two.setIndeterminate(false);               
		dialog_two.setCancelable(true);       
		dialog_two.setCanceledOnTouchOutside(false); 
		dialog_two.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog_two.show();
		site.save(getApplicationContext(), new SaveListener() {
			
			@Override
			public void onSuccess() {
				dialog_two.dismiss();
				ShowToast("球场标记成功");
				finish();
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				dialog_two.dismiss();
				ShowToast("上传出现错误,类型:"+arg0+",原因为:"+arg1);
				return;
			}
		});
//		for(int i=0;i<label_list.size();i++){
//			Site siteOne = new Site();
//			Label label = label_list.get(i);
//			relation.add(label);
//			siteOne.setSite_labels(relation);
//			siteOne.setObjectId(obj_id);
//			siteOne.update(getApplicationContext(), new UpdateListener() {
//				
//				@Override
//				public void onSuccess() {
//					j ++;
//					ShowToast("添加一个标签");
//				}
//				
//				@Override
//				public void onFailure(int arg0, String arg1) {
//					dialog.dismiss();
//					ShowToast("上传出现错误,类型:"+arg0+",原因为:"+arg1);
//					return;
//				}
//			});
//		}
	}
	
}
