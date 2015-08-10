package com.basketball.play.ui;

import com.basketball.play.R;

import cn.bmob.v3.Bmob;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class WelcomeActivity extends Activity {
	
	private Button login;
	private Button regist;
	private ImageView weixin;
	private ImageView sina;
	private ImageView qq;
	private ImageView douban;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.welcome);
		setContent();
		Bmob.initialize(this, "47f2e38a85157233aef129c282ead622");
	}

	public void setContent() {
		login = (Button) findViewById(R.id.login);
		regist = (Button) findViewById(R.id.regist);
		weixin = (ImageView) findViewById(R.id.weixin);
		sina = (ImageView) findViewById(R.id.sina);
		qq = (ImageView) findViewById(R.id.qq);
		douban = (ImageView) findViewById(R.id.douban);

		login.setOnClickListener(loginOcl);
		regist.setOnClickListener(registOcl);
		weixin.setOnClickListener(weixinOcl);
		sina.setOnClickListener(sinaOcl);
		qq.setOnClickListener(qqOcl);
		douban.setOnClickListener(doubanOcl);
	}

	OnClickListener loginOcl = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent login_intent = new Intent(WelcomeActivity.this,LoginActivity.class);
			startActivity(login_intent);
			finish();
		}
	};
	OnClickListener registOcl = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent regist_intent = new Intent(WelcomeActivity.this,RegistActivity.class);
			startActivity(regist_intent);
			finish();
		}
	};
	OnClickListener weixinOcl = new OnClickListener() {

		@Override
		public void onClick(View v) {

		}
	};
	OnClickListener sinaOcl = new OnClickListener() {

		@Override
		public void onClick(View v) {

		}
	};
	OnClickListener qqOcl = new OnClickListener() {

		@Override
		public void onClick(View v) {

		}
	};
	OnClickListener doubanOcl = new OnClickListener() {

		@Override
		public void onClick(View v) {

		}
	};

}
