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
	 * @param str 传入字符串
	 * @param begin 起始下标
	 * @param end 结束下标
	 * @param color 要变成的眼神
	 * @return 变色后的字符串
	 */
	public static SpannableStringBuilder colorString(String str, int begin,
			int end, int color) {
		SpannableStringBuilder styled = new SpannableStringBuilder(str);
		styled.setSpan(new ForegroundColorSpan(color), begin, end,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return styled;
	}
	
	/**
	 * 对字符串进行格式化，为空，返回”无“，不为空，返回原字符串
	 * @param str
	 * @return
	 */
	public static String formatString(String str){
		if(str==null||str.equals("")){
			return "无";
		}else {
			return str;
		}
	}
}
