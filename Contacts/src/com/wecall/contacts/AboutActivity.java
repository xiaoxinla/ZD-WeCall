package com.wecall.contacts;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

/**
 * 关于信息Activity
 * @author xiaoxin
 * 2015-4-5
 */
public class AboutActivity extends Activity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(false);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId()==android.R.id.home) {
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
}
