package com.basketball.play.ui;
import com.basketball.play.CustomApplcation;
import com.basketball.play.R;
import com.basketball.play.bean.UserBean;
import com.basketball.play.view.HeaderLayout;
import com.basketball.play.view.HeaderLayout.HeaderStyle;
import com.basketball.play.view.HeaderLayout.onLeftImageButtonClickListener;
import com.basketball.play.view.HeaderLayout.onRightImageButtonClickListener;
import com.basketball.play.view.dialog.DialogTips;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.v3.listener.UpdateListener;

/** 基类
 * @ClassName: BaseActivity
 * @Description: TODO
 * @author smile
 * @date 2014-6-13 下午5:05:38
 */
public class BaseActivity extends FragmentActivity {

	protected BmobUserManager userManager;
	BmobChatManager manager;
	protected HeaderLayout mHeaderLayout;
	
	CustomApplcation mApplication;
	
	protected int mScreenWidth;
	protected int mScreenHeight;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		userManager = BmobUserManager.getInstance(this);
		manager = BmobChatManager.getInstance(this);
		mApplication = CustomApplcation.getInstance();
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		mScreenWidth = metric.widthPixels;
		mScreenHeight = metric.heightPixels;
	}

	Toast mToast;

	public void ShowToast(final String text) {
		if (!TextUtils.isEmpty(text)) {
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (mToast == null) {
						mToast = Toast.makeText(getApplicationContext(), text,
								Toast.LENGTH_LONG);
					} else {
						mToast.setText(text);
					}
					mToast.show();
				}
			});
			
		}
	}

	public void ShowToast(final int resId) {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (mToast == null) {
					mToast = Toast.makeText(BaseActivity.this.getApplicationContext(), resId,
							Toast.LENGTH_LONG);
				} else {
					mToast.setText(resId);
				}
				mToast.show();
			}
		});
	}

	/** 打Log
	  * ShowLog
	  * @return void
	  * @throws
	  */
	public void ShowLog(String msg){
		Log.i("life",msg);
	}
	
	public void startAnimActivity(Class<?> cla) {
		this.startActivity(new Intent(this, cla));
	}
	
	public void startAnimActivity(Intent intent) {
		this.startActivity(intent);
	}
	/** 更新用户的经纬度信息
	  * @Title: uploadLocation
	  * @Description: TODO
	  * @param  
	  * @return void
	  * @throws
	  */
	public void updateUserLocation(){
		if(CustomApplcation.lastPoint!=null){
			String saveLatitude  = mApplication.getLatitude();
			String saveLongtitude = mApplication.getLongtitude();
			String newLat = String.valueOf(CustomApplcation.lastPoint.getLatitude());
			String newLong = String.valueOf(CustomApplcation.lastPoint.getLongitude());
//			ShowLog("saveLatitude ="+saveLatitude+",saveLongtitude = "+saveLongtitude);
//			ShowLog("newLat ="+newLat+",newLong = "+newLong);
			if(!saveLatitude.equals(newLat)|| !saveLongtitude.equals(newLong)){//只有位置有变化就更新当前位置，达到实时更新的目的
				UserBean u = userManager.getCurrentUser(UserBean.class);
				final UserBean user = new UserBean();
				user.setLocation(CustomApplcation.lastPoint);
				user.setObjectId(u.getObjectId());
				user.update(this,new UpdateListener() {
					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						CustomApplcation.getInstance().setLatitude(String.valueOf(user.getLocation().getLatitude()));
						CustomApplcation.getInstance().setLongtitude(String.valueOf(user.getLocation().getLongitude()));
//						ShowLog("经纬度更新成功");
					}
					@Override
					public void onFailure(int code, String msg) {
						// TODO Auto-generated method stub
//						ShowLog("经纬度更新 失败:"+msg);
					}
				});
			}else{
//				ShowLog("用户位置未发生过变化");
			}
		}
	}
	
	/**
	 * 初始化标题栏-带左右按钮
	 * @return void
	 * @throws
	 */
	public void initTopBarForBoth(String titleName, int rightDrawableId,String text,
			onRightImageButtonClickListener listener) {
		mHeaderLayout = (HeaderLayout)findViewById(R.id.common_actionbar);
		mHeaderLayout.init(HeaderStyle.TITLE_DOUBLE_IMAGEBUTTON);
		mHeaderLayout.setTitleAndLeftImageButton(titleName,
				R.drawable.base_action_bar_back_bg_selector,
				new OnLeftButtonClickListener());
		mHeaderLayout.setTitleAndRightButton(titleName, rightDrawableId,text,
				listener);
	}
	
	/**
	 * 只有左边按钮和Title initTopBarLayout
	 * 
	 * @throws
	 */
	public void initTopBarForLeft(String titleName) {
		mHeaderLayout = (HeaderLayout)findViewById(R.id.common_actionbar);
		mHeaderLayout.init(HeaderStyle.TITLE_DOUBLE_IMAGEBUTTON);
		mHeaderLayout.setTitleAndLeftImageButton(titleName,
				R.drawable.base_action_bar_back_bg_selector,
				new OnLeftButtonClickListener());
	}
	
	// 左边按钮的点击事件
		public class OnLeftButtonClickListener implements
				onLeftImageButtonClickListener {

			@Override
			public void onClick() {
				finish();
			}
		}
		
		/** 显示下线的对话框
		  * showOfflineDialog
		  * @return void
		  * @throws
		  */
		public void showOfflineDialog(final Context context) {
			DialogTips dialog = new DialogTips(this,"您的账号已在其他设备上登录!", "重新登录");
			// 设置成功事件
			dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int userId) {
					CustomApplcation.getInstance().logout();
					startActivity(new Intent(context, LoginActivity.class));
					finish();
					dialogInterface.dismiss();
				}
			});
			// 显示确认对话框
			dialog.show();
			dialog = null;
		}
}
