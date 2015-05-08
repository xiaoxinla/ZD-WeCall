package com.wecall.contacts;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import com.sina.push.PushManager;
import com.wecall.contacts.constants.Constants;
import com.wecall.contacts.util.SPUtil;

/**
 * 欢迎页
 * @author xiaoxin
 * 2015-4-30
 */
public class SplashActivity extends Activity {

	private static final int sleepTime = 1000;
	private static final String TAG = "SplashActivity";
	private PushManager manager;

	private ImageView welcomeImageView;
	private int imgIndex[] = { R.drawable.welcompage1, R.drawable.welcompage2,
			R.drawable.welcompage3, R.drawable.welcompage4,
			R.drawable.welcompage5 };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		final View view = View.inflate(this, R.layout.activity_splash, null);
		setContentView(view);
		super.onCreate(savedInstanceState);
		initFile();
		initSP();
		manager = PushManager.getInstance(getApplicationContext());
		startSinaPushService();
		welcomeImageView = (ImageView) findViewById(R.id.img_welcomepage);
		welcomeImageView
				.setImageResource(imgIndex[(int) (Math.random() * imgIndex.length)]);

		AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
		animation.setDuration(1500);
		view.startAnimation(animation);
	}

	private void initSP() {
		String name = (String) SPUtil.get(this, "name", "匿名");
		String phone = (String) SPUtil.get(this, "phone", "00000");
		SPUtil.put(this, "name", name);
		SPUtil.put(this, "phone", phone);
	}

	@Override
	protected void onStart() {
		super.onStart();
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// 进入主页面
				startActivity(new Intent(SplashActivity.this,
						MainActivity.class));
				finish();
			}
		}).start();
	}

	private void initFile() {
		Log.v(TAG, "initFile");
		File dirFile = new File(Constants.ALBUM_PATH);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
	}

	/**
	 * 开启SinaPush服务
	 */
	private void startSinaPushService() {

		manager.openChannel("22633", "100", "100");
	}

}
