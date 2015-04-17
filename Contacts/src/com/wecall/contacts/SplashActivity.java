package com.wecall.contacts;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import com.wecall.contacts.constants.Constants;

public class SplashActivity extends Activity {

	private static final int sleepTime = 1000;
	private static final String TAG = "SplashActivity";

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
		welcomeImageView = (ImageView) findViewById(R.id.img_welcomepage);
		welcomeImageView
				.setImageResource(imgIndex[(int) (Math.random() * imgIndex.length)]);

		AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
		animation.setDuration(1500);
		view.startAnimation(animation);
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
}
