package com.basketball.play.ui;

import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.RequestSMSCodeListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.VerifySMSCodeListener;

import com.basketball.play.bean.UserBean;
import com.basketball.play.config.BmobConstants;
import com.basketball.play.R;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegistActivity extends BaseActivity {

	private TextView login;
	private EditText email_phone;
	private EditText password;
	private EditText re_password;
	private Button regist;
	private Button code;
	private EditText code_content;
	private Boolean verify_sms=false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.regist_activity);
		
		login = (TextView) findViewById(R.id.login_text);
		email_phone = (EditText) findViewById(R.id.email_phone);
		password = (EditText) findViewById(R.id.password);
		re_password = (EditText) findViewById(R.id.repeat_password);
		regist = (Button) findViewById(R.id.regist_submit);
		code = (Button) findViewById(R.id.verification_code_btn);
		code_content = (EditText) findViewById(R.id.verification_code_edt);
		regist.setOnClickListener(reOcl);
		login.setOnClickListener(logOcl);
		code.setOnClickListener(code_ocl);
		
	}
	/**
	 * 注册提交监听
	 */
	OnClickListener reOcl = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			String user_message = email_phone.getText().toString();
			String email = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
			String phone = "^1[3|4|5|7|8][0-9]\\d{4,8}$";
			String password_text = password.getText().toString();
			String re_password_text = re_password.getText().toString();
			String code = code_content.getText().toString();
			if(TextUtils.isEmpty(code)){
				ShowToast("请输入短信验证码");
			}
			if(user_message.matches(phone)){
				if(password_text.equals("") || password_text.equals(null)){
					Toast.makeText(getApplicationContext(), "请输入密码", Toast.LENGTH_SHORT).show();
				}else if(!password_text.equals(re_password_text)){
					Toast.makeText(getApplicationContext(), "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
				}else{
					verifySms(user_message,password_text);
				}
			}else{
				Toast.makeText(getApplicationContext(), "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
				
			}
			
			
		}
	};
	
	public void saveUser(final String user_message,String password){
		
		final ProgressDialog progress = new ProgressDialog(RegistActivity.this);
		progress.setMessage("正在注册...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		final UserBean user = new UserBean();
		user.setUsername(user_message);
		user.setPassword(password);
		user.setDeviceType("android");
		user.setInstallId(BmobInstallation.getInstallationId(this));
		user.signUp(this, new SaveListener() {
			
			@Override
			public void onSuccess() {
				progress.dismiss();
				ShowToast("注册成功啦！");
				// 将设备与username进行绑定
				userManager.bindInstallationForRegister(user.getUsername());
				//更新地理位置信息
				updateUserLocation();
				//发广播通知登陆页面退出
				sendBroadcast(new Intent(BmobConstants.ACTION_REGISTER_SUCCESS_FINISH));
				Intent intent = new Intent(RegistActivity.this,RegistContentActivity.class);
				intent.putExtra("user_message", user_message);
				startActivity(intent);
				finish();
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				progress.dismiss();
				ShowToast("注册失败啦！失败类型为"+arg0+","+arg1);
			}
		});
	}
	
	
	OnClickListener code_ocl = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(TextUtils.isEmpty(email_phone.getText().toString().trim())){
				ShowToast("请输入手机号码");
			}else{
				sendMessage(email_phone.getText().toString().trim());
				
			}
			
		}
	};
	
	private void verifySms(final String user_message,final String password_text){
		final ProgressDialog progress = new ProgressDialog(RegistActivity.this);
		progress.setMessage("正在验证验证码");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		BmobSMS.verifySmsCode(this,email_phone.getText().toString().trim(), code_content.getText().toString().trim(), new VerifySMSCodeListener() {

		    @Override
		    public void done(BmobException ex) {
		        // TODO Auto-generated method stub
		        if(ex==null){//短信验证码已验证成功
		        	progress.dismiss();
		        	saveUser(user_message,password_text);
		        }else{
		        	progress.dismiss();
		            ShowToast("验证失败：code ="+ex.getErrorCode()+",msg = "+ex.getLocalizedMessage());
		        }
		    }
		});
	}
	
	private void sendMessage(String phone){
		BmobSMS.requestSMSCode(this, phone, "message", new RequestSMSCodeListener(){

			@Override
			public void done(Integer arg0, BmobException arg1) {
				ShowToast("短信发送成功");
				code.setText("已发送");
				code.setClickable(false);
			}
			
		});
	}
	
	/**
	 * 跳转到登陆页面
	 */
	OnClickListener logOcl = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(RegistActivity.this,LoginActivity.class);
			startActivity(intent);
		}
	};
}
