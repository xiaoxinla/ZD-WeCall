package com.wecall.contacts.util;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

/**
 * 字符串工具类，用来对字符串进行操作
 * 
 * @author xiaoxin
 * 
 */
public class StringUtil {

	/**
	 * 将字符串的一部分改变颜色
	 * 
	 * @param str
	 *            传入字符串
	 * @param begin
	 *            起始下标
	 * @param end
	 *            结束下标
	 * @param color
	 *            要变成的眼神
	 * @return 变色后的字符串
	 */
	public static SpannableStringBuilder colorString(String str, int begin,
			int end, int color) {
		SpannableStringBuilder styled = new SpannableStringBuilder(str);
		styled.setSpan(new ForegroundColorSpan(color), begin, end,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return styled;
	}

	public static SpannableStringBuilder colorString(
			SpannableStringBuilder ssb, int begin, int len, int color) {
		ssb.setSpan(new ForegroundColorSpan(color), begin, begin + len,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return ssb;
	}

	/**
	 * 对字符串进行格式化，为空，返回”无“，不为空，返回原字符串
	 * 
	 * @param str
	 * @return
	 */
	public static String formatString(String str) {
		if (str == null || str.equals("")) {
			return "无";
		} else {
			return str;
		}
	}
	
	public static String analyseHtml(String str){
		String taker = "<script";
		int index = str.indexOf(taker);
		return str.substring(0,index);
	}
}
