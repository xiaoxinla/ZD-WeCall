package com.wecall.contacts.util;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

public class StringUtil {

	public static SpannableStringBuilder colorString(String str, int begin,
			int end, int color) {
		SpannableStringBuilder styled = new SpannableStringBuilder(str);
		styled.setSpan(new ForegroundColorSpan(color), begin, end,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return styled;
	}
}
