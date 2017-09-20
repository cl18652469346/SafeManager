package com.jxy.safemanager.activity;

import com.jxy.safemanager.R;
import com.jxy.safemanager.view.SettingItemView;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class SettingActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		initUpdate();
	}

	private void initUpdate() {
		// TODO Auto-generated method stub
		final SettingItemView siv_update = (SettingItemView) findViewById(R.id.siv_update);
		siv_update.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 取反状态
				// 获取之前的选中状态
				boolean check = siv_update.ischeck();
				siv_update.setCheck(!check);
			}
		});
	}
}
