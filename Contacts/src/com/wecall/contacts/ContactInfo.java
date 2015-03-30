package com.wecall.contacts;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class ContactInfo extends Activity {

	private TextView nameTV,phoneTV;
	private ImageButton backIB;
	private ImageButton sendSMSBtn,makePhonecall;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_info);
		initView();
	}

	private void initView() {
		nameTV = (TextView)findViewById(R.id.tv_contact_name);
		phoneTV = (TextView)findViewById(R.id.tv_phone_show);
		backIB = (ImageButton)findViewById(R.id.back_to_homepage);
		sendSMSBtn = (ImageButton)findViewById(R.id.btn_send_sms);
		makePhonecall = (ImageButton)findViewById(R.id.btn_call_phone);
		Bundle bundle = getIntent().getExtras();
		
		SpannableStringBuilder styled = new SpannableStringBuilder(bundle.getString("cname"));

		// i 未起始字符索引，j 为结束字符索引
		styled.setSpan(new ForegroundColorSpan(Color.RED), 1, 2,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		nameTV.setText(styled);
		//nameTV.setText(bundle.getString("cname"));
		
		backIB.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
		makePhonecall.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				String phoneNumber = phoneTV.getText().toString();
				Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+phoneNumber));
				ContactInfo.this.startActivity(intent);
			}
		});
		
		sendSMSBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String phoneNumber = phoneTV.getText().toString();
				Intent intent = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:"+phoneNumber));
				ContactInfo.this.startActivity(intent);
			}
		});
	}
}
