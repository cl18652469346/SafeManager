package com.jxy.safemanager.activity;

import com.jxy.safemanager.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings.SettingNotFoundException;
import android.renderscript.Int2;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeActivity extends Activity {
	private GridView gv_home;
	private String[] mtitleStrs;
	private int[] mdrawableIds;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		initUi();
		initData();
	}

	/**
	 * 为九宫格准备数据
	 */
	private void initData() {
		mtitleStrs = new String[] { "手机防盗", "通信卫士", "软件管理", "进程管理", "流量统计", "手机杀毒", "缓存清理", "高级工具", "设置中心" };
		mdrawableIds = new int[] { R.drawable.home_safe, R.drawable.home_callmsgsafe, R.drawable.home_apps,
				R.drawable.home_taskmanager, R.drawable.home_netmanager, R.drawable.home_trojan,
				R.drawable.home_sysoptimize, R.drawable.home_tools, R.drawable.home_settings };
		// 设置数据适配器
		gv_home.setAdapter(new myAdapter());
		
		// 点击事件的注册
		gv_home.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				switch (position) {
				case 0:
					
					break;
				case 8:
					Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
					startActivity(intent);
					break;
				default:
					break;
				}
			}
		});
	}

	class myAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			// 条目的总数
			return mtitleStrs.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mtitleStrs[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		/* 
		 * 九宫格item所对应的布局，图片+ 文字
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view = View.inflate(getApplicationContext(), R.layout.gridview_item, null);
			ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
			TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
			
			iv_icon.setBackgroundResource(mdrawableIds[position]);
			tv_title.setText(mtitleStrs[position]);
			
			return view;
		}

	}

	private void initUi() {
		gv_home = (GridView) findViewById(R.id.gv_home);
	}
}
