package com.basketball.play.ui;

import com.basketball.play.R;
import com.basketball.play.adapter.MessageRecentAdapter;
import com.basketball.play.view.ClearEditText;

import android.os.Bundle;
import android.widget.ListView;

public class RecentMessageActivity extends ActivityBase {
	ClearEditText mClearEditText;
	ListView listview;
	MessageRecentAdapter adapter;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recent_message);
	}

}
