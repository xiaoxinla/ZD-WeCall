package com.wecall.contacts;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.wecall.contacts.util.StringUtil;
import com.wecall.contacts.view.DetailBar;
import com.wecall.contacts.view.DetailBar.DetailBarClickListener;

public class ContactInfo extends Activity {

	private TextView nameTV;
	private ImageButton backIB;
	private DetailBar phoneNumBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_info);
		initView();
	}

	private void initView() {
		nameTV = (TextView) findViewById(R.id.tv_contact_name);
		backIB = (ImageButton) findViewById(R.id.back_to_homepage);
		phoneNumBar = (DetailBar) findViewById(R.id.phone_num);
		Bundle bundle = getIntent().getExtras();

		SpannableStringBuilder styled = StringUtil.colorString(
				bundle.getString("cname"), 1, 2, Color.RED);
		nameTV.setText(styled);
		// nameTV.setText(bundle.getString("cname"));

		backIB.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		phoneNumBar.setOnDetailBarClickListener(new DetailBarClickListener() {

			@Override
			public void leftClick() {
				String phoneNumber = phoneNumBar.getInfo().toString();
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
						+ phoneNumber));
				ContactInfo.this.startActivity(intent);
			}

			@Override
			public void rightClick() {
				String phoneNumber = phoneNumBar.getInfo().toString();
				Intent intent = new Intent(Intent.ACTION_SENDTO, Uri
						.parse("smsto:" + phoneNumber));
				ContactInfo.this.startActivity(intent);
			}

			@Override
			public void infoClick() {
				Log.v("TAG", "info");
			}
		});
	}
}
