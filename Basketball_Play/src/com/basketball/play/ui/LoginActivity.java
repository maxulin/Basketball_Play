package com.basketball.play.ui;

import cn.bmob.v3.listener.SaveListener;

import com.basketball.play.ResideMenuDemo.MainActivity;
import com.basketball.play.bean.UserBean;
import com.basketball.play.R;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends BaseActivity {
	
	private TextView regist;
	private EditText email_phone;
	private EditText password;
	private Button login;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
		
		regist = (TextView) findViewById(R.id.regist_text);
		email_phone = (EditText) findViewById(R.id.email_phone);
		password = (EditText) findViewById(R.id.password);
		login = (Button) findViewById(R.id.login_submit);
		
		regist.setOnClickListener(reOcl);
		login.setOnClickListener(logOcl);
		
	}
	
	/**
	 * 跳转到注册页面
	 */
	OnClickListener reOcl = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(LoginActivity.this,RegistActivity.class);
			startActivity(intent);
		}
	};
	
	
	/**
	 * 登录按钮监听
	 */
	OnClickListener logOcl = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			String user_message = email_phone.getText().toString();
			String email = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
			String phone = "^1[3|4|5|8][0-9]\\d{4,8}$";
			String password_text = password.getText().toString();
			if(user_message.matches(email) || user_message.matches(phone)){
				if(password_text.equals("") || password_text.equals(null)){
					Toast.makeText(getApplicationContext(), "请输入密码", Toast.LENGTH_SHORT).show();
				}else{
					login(user_message,password_text);
				}
			}else{
				Toast.makeText(getApplicationContext(), "请输入正确的邮箱地址或手机号码", Toast.LENGTH_SHORT).show();
				
			}
			
			
		}
	};
	
	
	
	private void login(String user_message,String password_text){
		final ProgressDialog progress = new ProgressDialog(
				LoginActivity.this);
		progress.setMessage("正在登陆...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		UserBean userBean = new UserBean();
		userBean.setUsername(user_message);
		userBean.setPwd(password_text);
		userManager.login(userBean, new SaveListener() {
			
			@Override
			public void onSuccess() {
				
				progress.dismiss();
				Intent intent = new Intent(LoginActivity.this,MainActivity.class);
				startActivity(intent);
				finish();
				
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				progress.dismiss();
				ShowToast("啊哦，登录失败了，"+arg1);
			}
		});
	}
}
