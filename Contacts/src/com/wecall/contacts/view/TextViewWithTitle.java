package com.wecall.contacts.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wecall.contacts.R;

/**
 * 带标题的文本框
 * @author xiaoxin
 * 2015-4-12
 */
public class TextViewWithTitle extends LinearLayout {

	private String mText;
	private String mTitle;
	private int mTextSize = (int) TypedValue.applyDimension(
			TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics());
	private int mTitleSize = (int) TypedValue.applyDimension(
			TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics());
	private TextView mTextView;
	private TextView mTitleText;

	public TextViewWithTitle(Context context) {
		this(context, null);
	}

	public TextViewWithTitle(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TextViewWithTitle(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		TypedArray ta = context.obtainStyledAttributes(attrs,
				R.styleable.TextViewWithTitle);
		for (int i = 0; i < ta.getIndexCount(); i++) {
			int att = ta.getIndex(i);
			switch (att) {
			case R.styleable.TextViewWithTitle_text:
				mText = ta.getString(att);
				break;
			case R.styleable.TextViewWithTitle_title:
				mTitle = ta.getString(att);
				break;
			case R.styleable.TextViewWithTitle_text_size:
				mTextSize = (int) ta.getDimension(att, TypedValue
						.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12,
								getResources().getDisplayMetrics()));
			case R.styleable.TextViewWithTitle_title_size:
				mTitleSize = (int) ta.getDimension(att, TypedValue
						.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12,
								getResources().getDisplayMetrics()));
			default:
				break;
			}
		}
		ta.recycle();

		mTextView = new TextView(context);
		mTitleText = new TextView(context);
	}

	@SuppressLint("DrawAllocation") @Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		setOrientation(LinearLayout.HORIZONTAL);
		removeAllViews();

		mTextView.setText(mText);
		mTextView.setTextSize(mTextSize);
		mTextView.setSingleLine(true);
		mTextView.setEllipsize(TruncateAt.END);
		mTextView.setBackgroundResource(R.drawable.jog_tab_bar_right_end_confirm_gray);
		mTitleText.setText(mTitle);
		mTitleText.setTextSize(mTitleSize);
		mTitleText.setBackgroundResource(R.drawable.jog_tab_bar_left_end_confirm_blue);

		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		params.weight = 1;
		
		addView(mTitleText);
		addView(mTextView,params);
		super.onLayout(changed, l, t, r, b);
	}

	public void setTitle(String title){
		mTitle = title;
		mTitleText.setText(mTitle);
	}
	
	public void setText(String text){
		mText = text;
		mTextView.setText(mText);
	}
}
