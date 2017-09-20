package com.jxy.safemanager.view;

import com.jxy.safemanager.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingItemView extends RelativeLayout {

	private CheckBox cb_box;
	private TextView tv_des;

	public SettingItemView(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}

	public SettingItemView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
	}

	public SettingItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		// xml -> view 将设置界面的一个item转换为view对象
		View.inflate(context, R.layout.setting_item_view, this);
		// 等同于下面两行代码
		/*
		 * View view = View.inflate(context, R.layout.setting_item_view, null);
		 * this.addView(view);
		 */
		
		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_des = (TextView) findViewById(R.id.tv_des);
		cb_box = (CheckBox) findViewById(R.id.cb_box);
	}

	/**
	 * @return 判断当前SettingItemView的选中状态
	 * true 开启
	 * false 关闭
	 */
	public boolean ischeck(){
		return cb_box.isChecked();
	}
	
	/**
	 * 点击过程去切换当前状态
	 * @param ischecked
	 */
	public void setCheck(boolean ischecked){
		cb_box.setChecked(ischecked);
		if(ischecked) {
			tv_des.setText("自动更新已经开启");
		}else {
			tv_des.setText("自动更新已经关闭");
		}
	}
}
