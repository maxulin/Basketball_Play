package com.basketball.play.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.bmob.v3.listener.UpdateListener;

import com.basketball.play.ResideMenuDemo.MainActivity;
import com.basketball.play.adapter.MySpinnerAdapter;
import com.basketball.play.bean.SpinnerBean;
import com.basketball.play.bean.UserBean;
import com.basketball.play.util.TimeUtil;
import com.basketball.play.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class RegistContentActivity extends BaseActivity implements View.OnTouchListener{
	private Button regist_button;
	private EditText nickname;
	private EditText birthday;
	private EditText height;
	private EditText weight;
	private Spinner role;
	private TextView male;
	private TextView female;
	private UserBean user = new UserBean();
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.regist_content);
		role = (Spinner) findViewById(R.id.role);
		// 建立数据源
		List<SpinnerBean>  persons=new ArrayList<SpinnerBean>();
		persons.add(new SpinnerBean("控球后卫"));
		persons.add(new SpinnerBean("得分后卫"));
		persons.add(new SpinnerBean("中锋"));
		persons.add(new SpinnerBean("小前锋"));
		persons.add(new SpinnerBean("大前锋"));
		//  建立Adapter绑定数据源
		MySpinnerAdapter _MyAdapter=new MySpinnerAdapter(this, persons);
		//绑定Adapter
		role.setAdapter(_MyAdapter);
		role.setOnItemSelectedListener(new OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parent, View view,
		            int position, long id) {
		        //SpinnerBean str=(SpinnerBean) parent.getItemAtPosition(position);
		        user.setRole(position);
		    }
		    @Override
		    public void onNothingSelected(AdapterView<?> parent) {
		        // TODO Auto-generated method stub
		    }
		});
		user.setSex(true);
		birthday = (EditText) findViewById(R.id.birthday);
		male = (TextView) findViewById(R.id.male);
		female = (TextView) findViewById(R.id.female);
		regist_button = (Button) findViewById(R.id.regist_submit);
		height = (EditText) findViewById(R.id.height);
		weight = (EditText) findViewById(R.id.weight);
		nickname = (EditText) findViewById(R.id.nickname);
		female.setOnClickListener(tvOcl);
		male.setOnClickListener(tvOcl);
		birthday.setOnTouchListener(this);
		regist_button.setOnClickListener(regOcl);
	}
		
	OnClickListener tvOcl = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(v.getId() == R.id.male){
				male.setBackgroundResource(R.drawable.tvbar);
				male.setTextColor(getResources().getColor(R.color.white));
				female.setBackgroundResource(R.drawable.tvbartwo);
				female.setTextColor(getResources().getColor(R.color.black));
				user.setSex(true);
			}else{
				male.setBackgroundResource(R.drawable.tvbartwo);
				male.setTextColor(getResources().getColor(R.color.black));
				female.setBackgroundResource(R.drawable.tvbar);
				female.setTextColor(getResources().getColor(R.color.white));
				user.setSex(false);
			}
		}
	};

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this); 
        View view = View.inflate(this, R.layout.date_picker, null); 
        final DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_picker); 
        builder.setView(view); 
        Calendar cal = Calendar.getInstance(); 
        cal.setTimeInMillis(System.currentTimeMillis()); 
        datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null);
        if (v.getId() == R.id.birthday) { 
            final int inType = birthday.getInputType(); 
            birthday.setInputType(InputType.TYPE_NULL); 
            birthday.onTouchEvent(event); 
            birthday.setInputType(inType); 
            birthday.setSelection(birthday.getText().length());
            builder.setTitle("出生日期");
            builder.setPositiveButton("确  定", new DialogInterface.OnClickListener() { 

                @Override 
                public void onClick(DialogInterface dialog, int which) { 

                    StringBuffer sb = new StringBuffer(); 
                    sb.append(String.format("%d-%02d-%02d",  
                            datePicker.getYear(),  
                            datePicker.getMonth() + 1, 
                            datePicker.getDayOfMonth())); 
                    birthday.setText(sb);
                    TimeUtil tu = new TimeUtil();
                    user.setBirthday(TimeUtil.stringToDate(sb.toString(), "yyyy-mm-dd"));
                    dialog.cancel(); 
                } 
            }); 
               
        }
        Dialog dialog = builder.create(); 
        dialog.show(); 
        }
        return true;
	}
	
	OnClickListener regOcl = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(TextUtils.isEmpty(nickname.getText())){
				ShowToast(R.string.toast_error_username_null);
				return;
			}
			if(TextUtils.isEmpty(birthday.getText())){
				ShowToast(R.string.toast_error_birthday_null);
				return;
			}
			if(TextUtils.isEmpty(height.getText())){
				ShowToast(R.string.toast_error_height_null);
				return;
			}
			if(TextUtils.isEmpty(weight.getText())){
				ShowToast(R.string.toast_error_weight_null);
				return;
			}
			user.setNick(nickname.getText().toString());
			user.setHeight(Integer.parseInt(height.getText().toString()));
			user.setWeight(Integer.parseInt(weight.getText().toString()));
			registContent(user);
		}
	};
	
	//提交个人资料
	private void registContent(final UserBean user){
		UserBean curruser = userManager.getCurrentUser(UserBean.class);
		user.setObjectId(curruser.getObjectId());
		user.update(this,new UpdateListener() {
			
			@Override
			public void onSuccess() {
				ShowToast("个人资料更新成功啦！");
				Intent intent = new Intent(RegistContentActivity.this,MainActivity.class);
				intent.putExtra("nick", user.getNick());
				intent.putExtra("role", user.getRole());
				startActivity(intent);
				finish();
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				ShowToast("个人资料更新失败，"+arg1);
			}
		});
	}
	
}
