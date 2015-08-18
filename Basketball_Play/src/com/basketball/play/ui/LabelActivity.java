package com.basketball.play.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

import com.basketball.play.bean.Label;
import com.basketball.play.view.FlowLayout;
import com.basketball.play.R;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class LabelActivity extends BaseActivity{
	private ListView listView;
	FlowLayout tag_vessel;
	List<String> mTagList = new ArrayList<String>();
	int screenWidth = 0;
	List<Map<String,Object>> label_list;
	List<Label> labels = new ArrayList<Label>();
	List<Label> add_labels = new ArrayList<Label>();
	private Button submit_label;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.label_activity);
		//初始化控件
		initView();
		//查询标签列表
		findLabel();
		submit_label.setOnClickListener(ocl);
		//获取已经添加的标签，arraylist里没有数据的话不添加
		Intent intent = getIntent();
		add_labels = (List<Label>) intent.getSerializableExtra("add_labels");
		if(add_labels.size()!=0){
			for(int i=0;i<add_labels.size();i++){
				AddTag(add_labels.get(i).getLabel_name(), i);
				mTagList.add(add_labels.get(i).getLabel_name());
			}
		}
	}
	
	private void findLabel(){
		BmobQuery<Label> query = new BmobQuery<Label>();
		//返回50条数据，如果不加上这条语句，默认返回10条数据
		query.setLimit(20);
		//执行查询方法
		query.findObjects(this, new FindListener<Label>() {
		        @Override
		        public void onSuccess(List<Label> object) {
		        	//ShowToast("查询成功，"+object.size());
		        	labels = object;
		        	addLabelList(object);
		        }
		        @Override
		        public void onError(int code, String msg) {
		        	ShowToast("查询失败，"+code+msg);
		        }
		});
	}
	
	private void addLabelList(List<Label> object){
		label_list = new ArrayList<Map<String,Object>>();
		for (Label label : object) {
            Map<String,Object> listItem = new HashMap<String, Object>();
            listItem.put("label_content", label.getLabel_name());
            label_list.add(listItem);
        }
		SimpleAdapter simpleAdapter = new SimpleAdapter(this, label_list, R.layout.label_list_item, new String[]{"label_content"}, new int[]{R.id.label_content});
		listView = (ListView) findViewById(R.id.label_list);
		listView.setAdapter(simpleAdapter);
		listView.setOnItemClickListener(oicl);
	}
	
	OnItemClickListener oicl = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			int j = 0;
			for(int i= 0;i<mTagList.size();i++){
				if(labels.get(position).getLabel_name().equals(mTagList.get(i))){
					ShowToast("这个标签已经添加过啦");
					j=1;
				}
			}
			if(j==0){
				add_labels.add(labels.get(position));
				mTagList.add(labels.get(position).getLabel_name());
				AddTag(mTagList.get(mTagList.size() - 1), mTagList.size() - 1);
			}else{
				return;
			}
			
		}
	};
	
	public void initView() {
		screenWidth = getWindowManager().getDefaultDisplay().getWidth(); // 屏幕宽（像素
		tag_vessel = (FlowLayout) findViewById(R.id.tag_vessel);
		submit_label = (Button) findViewById(R.id.submit_label_activity);
	}
	/**
	 * 添加标签
	 * @param tag
	 * @param i
	 */
	@SuppressLint("NewApi")
	public void AddTag(String tag, int i) {
		final TextView mTag = new TextView(LabelActivity.this);
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
						for (int j = 0; j < mTagList.size(); j++) {
							Log.v("==", mTag.getText().toString() + "==" + mTagList.get(j).toString());
							if (mTag.getText().toString().replaceAll(" ", "")
									.equals(mTagList.get(j).toString().replaceAll(" ", ""))) {
								mTagList.remove(j);
							}
						}
						for (int k = 0; k < add_labels.size(); k++) {
							Log.v("==", mTag.getText().toString() + "==" + add_labels.get(k).toString());
							if (mTag.getText().toString().replaceAll(" ", "")
									.equals(add_labels.get(k).getLabel_name().toString().replaceAll(" ", ""))) {
								add_labels.remove(k);
							}
						}
						return true;
					}
		});
	}
	
	OnClickListener ocl = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = getIntent();
			intent.putExtra("add_labels",(Serializable)add_labels);
			LabelActivity.this.setResult(0,intent);
			LabelActivity.this.finish();
		}
	};
	
}
