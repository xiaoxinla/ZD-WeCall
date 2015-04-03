package com.wecall.contacts.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wecall.contacts.R;

/**
 * ´øiconµÄtextview
 * @author xiaoxin
 * 2015-4-3
 */
public class IconTextView extends LinearLayout{

	private TextView mTextView;
	private ImageView mImage;
	
	private String mText;
	private Drawable mIcon; 
	
	public IconTextView(Context context) {
		this(context,null);
	}
	
	public IconTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initAttrs(context,attrs);
		initView(context);
	}

	public IconTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	private void initAttrs(Context context, AttributeSet attrs) {
		TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.IconTextView);
		mText = ta.getString(R.styleable.IconTextView_text);
		mIcon = ta.getDrawable(R.styleable.IconTextView_icon);
		ta.recycle();
	}
	
	private void initView(Context context) {
		mTextView = new TextView(context);
		mImage = new ImageView(context);
		
		mTextView.setText(mText);
		mImage.setImageDrawable(mIcon);
		mTextView.setGravity(Gravity.CENTER);
//		mImage.setBackgroundDrawable(null);
		
		setOrientation(LinearLayout.VERTICAL);
		setGravity(Gravity.CENTER);
		addView(mImage);
		addView(mTextView);
	}

	
}
