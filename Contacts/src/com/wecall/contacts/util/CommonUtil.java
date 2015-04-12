package com.wecall.contacts.util;

import java.util.ArrayList;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;

import com.wecall.contacts.MainActivity;
import com.wecall.contacts.entity.ContactItem;

/**
 * 通用工具类，用来与手机交互
 * 
 * @author xiaoxin 2015-4-11
 */
public class CommonUtil {

	/**
	 * 获取本机的联系人
	 * 
	 * @param context
	 *            所在上下文
	 * @return 本机的联系人信息
	 */
	public static List<ContactItem> getLocalContacts(Context context) {
		List<ContactItem> list = new ArrayList<ContactItem>();
		ContentResolver resolver = context.getContentResolver();
		Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI,
				null, null, null, null);
		while (cursor.moveToNext()) {
			int nameIndex = cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME);
			String contact = cursor.getString(nameIndex);

			String contactId = cursor.getString(cursor
					.getColumnIndex(ContactsContract.Contacts._ID));
			Cursor phoneCursor = resolver.query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "="
							+ contactId, null, null);
			while (phoneCursor.moveToNext()) {
				String strPhoneNumber = phoneCursor
						.getString(phoneCursor
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				ContactItem item = new ContactItem();
				item.setName(contact);
				item.setPhoneNumber(strPhoneNumber);
				list.add(item);
			}
			phoneCursor.close();
		}
		cursor.close();
		return list;
	}

	@SuppressWarnings("deprecation")
	public static void notifyMessage(Context context, int iconid,
			String tickerText, String contentTitle, String contentText) {
		NotificationManager manager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(iconid, tickerText,
				System.currentTimeMillis());

		Intent intent = new Intent(context, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				intent, 0);
		notification.setLatestEventInfo(context, contentTitle, contentText,
				pendingIntent);
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.defaults = Notification.DEFAULT_SOUND;
		manager.notify(1, notification);
	}
}
