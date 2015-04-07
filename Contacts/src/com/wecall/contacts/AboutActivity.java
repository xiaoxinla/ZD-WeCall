package com.wecall.contacts;

import android.app.Activity;
import android.os.Bundle;

import com.wecall.contacts.view.DetailBar;
import com.wecall.contacts.view.DetailBar.DetailBarClickListener;

/**
 * 关于信息Activity
 * @author xiaoxin
 * 2015-4-5
 */
public class AboutActivity extends Activity {

	private DetailBar topBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
		topBar=(DetailBar) findViewById(R.id.db_about_topbar);
		topBar.setOnDetailBarClickListener(new DetailBarClickListener() {
			
			@Override
			public void rightClick() {
				
			}
			
			@Override
			public void leftClick() {
				finish();
			}
			
			@Override
			public void infoClick() {
				
			}
		});
	}
}
