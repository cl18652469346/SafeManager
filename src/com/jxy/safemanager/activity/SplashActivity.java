package com.jxy.safemanager.activity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import com.jxy.safemanager.R;
import com.jxy.safemanager.utils.StreamUtil;
import com.jxy.safemanager.utils.ToastUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SplashActivity extends Activity {

	protected static final String tag = "SplashActivity";
	/**
	 * 更新新版本的状态码
	 */
	protected static final int UPDATA_VERSION = 100;
	/**
	 * 进入应用主界面的状态码
	 */
	protected static final int ENTER_HOME = 101;
	/**
	 * URL异常的状态码
	 */
	protected static final int URL_ERROR = 102;
	/**
	 * IO异常的状态码
	 */
	protected static final int IO_ERROR = 103;
	/**
	 * JSON解析异常的状态码
	 */
	protected static final int JSON_ERROR = 104;
	private TextView textView;
	private int mLocalVersionCode;
	private String mversionDes;
	private String mdownloadUrl;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UPDATA_VERSION:
				// 弹出对话框，提示更新
				showUpdateDialog();
				break;
			case ENTER_HOME:
				// 进入引用程序主界面(activity的跳转过程)
				enterHome();
				break;
			case URL_ERROR:
				// Toast.makeText(context, text, duration)
				ToastUtil.show(SplashActivity.this, "URL_ERROR");
				enterHome();
				break;
			case IO_ERROR:
				ToastUtil.show(SplashActivity.this, "IO_ERROR");
				// 或者以下方式
				// ToastUtil.show(getApplicationContext(), "URL_ERROR");
				enterHome();
				break;
			case JSON_ERROR:
				ToastUtil.show(SplashActivity.this, "JSON_ERROR");
				enterHome();
				break;
			default:
				break;
			}
		};
	};
	private RelativeLayout rl_root;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 去头的第1种方式,仅仅局限在当前的activity
		// 去头的第二种方式,針對所有的activity
		// 修改清单文件里面的android:theme="@style/AppTheme",
		// 变为android:theme="@android:style/Theme.Light.NoTitleBar"
		// 但時使用該theme會使得progress變得很丑,因此將去頭的操作copy到原本的theme中~
		// 這樣既保證所有activity去頭，又保證了progre比較好看
		// requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_splash);

		// 初始化Ui
		initUi(); // "ctrl + 1" 会提示创建该方法
		// 初始化数据
		initData();
		
		// 初始化动画
		initAnimation();
	}

	/**
	 * 添加淡入的动画效果
	 */
	private void initAnimation() {
		// TODO Auto-generated method stub
		// 选中Animation，ctrl+T可以查看一些东西
//		Animation
		AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
		alphaAnimation.setDuration(3000);
		// 当前界面执行这个动画
		rl_root.startAnimation(alphaAnimation);
	}

	/**
	 * 弹出提示用户更新版本的Dailog
	 */
	protected void showUpdateDialog() {
		// 对话框是依赖于Activity，所以必须是this(当前activity)
		// 而getApplicationContext() 虽然也能返回context对象，但是不能将dialog绑定在当前的activity上面
		Builder builder = new AlertDialog.Builder(this);
		// 设置左上角图标
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle("版本更新");
		builder.setMessage(mversionDes);
		builder.setPositiveButton("更新", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				// 下载apk,用到了xutils（需要导入架包置于工程的lib目录下）
				// xutils的架包可以自行x度下载,也可以在资料中用其较旧版本的架包
				// xutils的注意事项:
				// (1)不光lib下有该架包，还需要在Android Private Libraries下也存在[这个可能会帮你自动做]
				// (2)配置清单文件里面加上网络和读取外部存储的权限[xutils一共就需要这两个权限]
				// <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
				// <uses-permission android:name="android.permission.INTERNET" />
				// 使用:
				// 获取HttpUtils对象，下载指定链接地址的APK
				downLoadApk();
			}
		});
		builder.setNegativeButton("取消", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				// 取消对话框,停在了splash界面,应该进入主界面
				enterHome();
			}
		});
		builder.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				// 点击返回键的时候也要进入到主界面
				enterHome();
				dialog.dismiss();
			}
		});
		builder.show();

	}

	/**
	 * 版本更新提示框点击更新按钮下载apk
	 */
	protected void downLoadApk() {
		// TODO Auto-generated method stub
		// apk的url，以及apk放置的路径
		// (1)sd许要判断是否可用（挂载与否）
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			// (2)apk下载到sd卡路径的获取设置
			String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
					+ "SafeManager.apk";
			// (3)发送请求，获取apk，并且放置在指定的位置
			HttpUtils httpUtils = new HttpUtils();
			httpUtils.download(mdownloadUrl, path, new RequestCallBack<File>() {
				@Override
				public void onStart() {
					super.onStart();
					ToastUtil.show(SplashActivity.this, "DownLoad Start");
				}

				@Override
				public void onLoading(long total, long current, boolean isUploading) {
					// 参数介绍:apk的总大小,当前的下载位置，是否正在下载
					super.onLoading(total, current, isUploading);
					ToastUtil.show(SplashActivity.this, "DownLoading");
					Log.d(tag, "" + total);
					Log.d(tag, "" + current);
					Log.d(tag, "" + isUploading);
				}

				@Override
				public void onSuccess(ResponseInfo<File> arg0) {
					// 下载过后放置sd卡的apk
					ToastUtil.show(SplashActivity.this, "DownLoad Success");
					File file = arg0.result;
					// 提示用户安装
					installApk(file);
				}

				@Override
				public void onFailure(HttpException arg0, String arg1) {
					ToastUtil.show(SplashActivity.this, "DownLoad Failure");
				}
			});
		}
	}

	/**
	 * 安装下载到指定位置的apk 注意事项： 1.包名要一致
	 * 2.签名要一致【服务器端apk(C:\Eclipse\Pro\KeySafeManager_KeyStore) 本地是debug
	 * apk(C:\Users\app\.android\debug.keystore)】
	 */
	protected void installApk(File file) {
		// TODO Auto-generated method stub
		// 系统ying用界面，源码，安装apk的入口[隐式启动一个Activity]
		// 在清单配置文件中配置好之后再写下面的代码
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		// 文件作为数据来源
		// intent.setData(Uri.fromFile(file));
		// 设置安装的类型
		// intent.setType("application/vnd.android.package-archive");
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
//		startActivity(intent);
		// 若是在安装确认的dialog上点击取消，确实会回到splash界面，但是会卡住，如何解决？
		startActivityForResult(intent, 0);
	}

	/**
	 * 开启一个activity后，返回结果调用的方法
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 * 
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		// 不能卡住在splash界面，要主动进入主界面
		enterHome();
	}
	/**
	 * 进入引用程序主界面(activity的跳转过程)
	 */
	protected void enterHome() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		// 在开启新的界面的时候，将导航界面关闭(导航界面只可以见一次)
		finish();
	}

	/**
	 * 获取数据的方法. 1.显示本地的版名名字于UI. 2.获取本地的版本号（以用于检测本地code和服务code）.
	 */
	private void initData() {
		try {
			PackageInfo info = getPackageManager().getPackageInfo(this.getPackageName(), 0);
			textView.setText("版本名: " + info.versionName);
			// 获取本地版本号
			mLocalVersionCode = info.versionCode;
			// 获取服务器的版本号（请求，响应[xml | json]）
			// Tips : 访问http://www.oxxx.com/xxx.josn?key=value 返回200 请求成功.
			// 以流的方式将数据读取出来

			// 提示更新的json文件应该包含的数据如下：
			/*
			 * 新版本的版本名称/新版本的描述信息/ 服务端的版本号 /新版本apk的下载地址
			 */

			// Test:模仿更新
			/* (1)update.json */
			// {
			// versionName:"2.0",
			// versionDes:"2.0相当的酷炫，快来下载吧",
			// versionCode:"2",
			// downloadUrl:"http://www.oxxx.com/xxx.apk"
			// }

			/* (2)写好后可以用hiJson工具进行验证是否写的正确 */

			/* (3)update.json放在apache tomcat服务器上 */
			// 具体实现:update.json放于C:\ApacheTomcat\Tomcat 9.0\webapps\ROOT下

			/* (4)访问http://localhost:8080/update.json */
			// 若是提示下载或者正确显示出json数据，则josn文件置于服务器成功

			checkVersionCode();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.d(tag, "getPackageName() 产生异常");
			e.printStackTrace();
		}
	}

	/**
	 * 检测版本号，若是服务器Code大于本地Code，则提示更新. 解析请求服务器端后响应所返回的json数据. 比较版本号，根据url提示下载.
	 */
	private void checkVersionCode() {
		// 请求是耗时操作，所以要在子线程进行操作
		// 开线程的第一种方式
		new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();

				// Message message = new Message();
				Message message = Message.obtain();
				long startTime = System.currentTimeMillis();
				// 发送请求获取数据,参数为请求json数据的链接
				// ****加网络请求权限****
				try {
					// URL url = new URL("http://localhost:8080/update.json");
					// 由于安装在手机上，手机上没有tomcat，所以没有localhost。
					// 解决方法：localhost转换位本机电脑的IP地址:192.168.31.101

					// URL url = new URL("http://192.168.31.101:8080/update.json");
					// Tips: http://192.168.31.101:8080/update.json该网址用于别人(除本机以外都是)下载josn数据
					// ****因为ip会自动分配，所以这种方法不是最优的***
					// 解决方案: 192.168.31.101:8080改为固定的域名（无法做成为www.baidu.com样式）

					// 解决方案: 10.0.2.2:8080 仅仅限制在模拟器访问tomcat(google提供的)
					// (1)封装url地址
					URL url = new URL("http://192.168.31.101:8080/update.json");
					// (2)开启一个链接
					HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
					// (3)设置常见的请求参数（请求头）
					// (3-1)请求超时(没连上)
					urlConnection.setConnectTimeout(2000);
					// (3-2)读取超时(连上了，连得时间超过了2s)
					urlConnection.setReadTimeout(2000);
					// (3-3)默认请求方式是"GET",否则"POST"(必须要写清楚，大写)
					// urlConnection.setRequestMethod("POST");
					// (4)获取请求成功的相应码
					if (urlConnection.getResponseCode() == 200) {
						// (5)以流的形式将数据获取下来
						InputStream iStream = urlConnection.getInputStream();
						// (6)将流转换成字符串(StreamUtil工具类封装)，之后才可以解析
						String json = StreamUtil.stream2String(iStream);
						Log.d(tag, json);
						// (7)json数据的解析
						JSONObject jsonObject = new JSONObject(json);
						String versionName = jsonObject.getString("versionName");
						mversionDes = jsonObject.getString("versionDes");
						String versionCode = jsonObject.getString("versionCode");
						mdownloadUrl = jsonObject.getString("downloadUrl");
						Log.d(tag, versionName);
						Log.d(tag, mversionDes);
						Log.d(tag, versionCode);
						Log.d(tag, mdownloadUrl);
						// (8)比對版本號(若是本地版本號小於服務器版本號，則提示更新)
						if (mLocalVersionCode < Integer.valueOf(versionCode)) {
							// 提示用户更新(弹出属于UI的对话框)
							// 但是现在是在新开的Thread里面，不能处理主线程的UI
							// 解决方案:消息处理机制Handler
							message.what = UPDATA_VERSION;
						} else {
							// 进入用户界面
							message.what = ENTER_HOME;
						}

					} else {
						Log.d(tag, "urlConnection请求失败");
					}

				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					Log.d(tag, "new URL http://10.0.2.2:8080/update.json 产生异常");
					message.what = URL_ERROR;
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.d(tag, "url.openConnection 产生异常");
					message.what = IO_ERROR;
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					message.what = JSON_ERROR;
					e.printStackTrace();
				} finally {
					// 制定睡眠时间,请求网络超过4秒就不做处理
					// 请求网络时间少于4秒，则让其满4秒
					long endTime = System.currentTimeMillis();
					if (endTime - startTime < 4000) {
						try {
							Thread.sleep(4000 - (endTime - startTime));
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					mHandler.sendMessage(message);
				}

			}
		}.start();

		// 开线程的第二种方式
		// new Thread(new Runnable() {
		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		//
		// }
		// });
	}

	/**
	 * 初始化Ui alt + shift + j 会出现上面这种的注释
	 */
	private void initUi() {
		textView = (TextView) findViewById(R.id.tv_version_name);
		rl_root = (RelativeLayout) findViewById(R.id.rl_root);
	}

}
