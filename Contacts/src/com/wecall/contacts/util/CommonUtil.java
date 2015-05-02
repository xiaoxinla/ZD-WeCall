package com.wecall.contacts.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
 * ͨ�ù����࣬�������ֻ�����
 * 
 * @author xiaoxin 2015-4-11
 */
public class CommonUtil {

	/**
	 * ��ȡ��������ϵ��
	 * 
	 * @param context
	 *            ����������
	 * @return ��������ϵ����Ϣ
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
				Set<String> phoneSet = new HashSet<String>();
				phoneSet.add(strPhoneNumber);
				item.setPhoneNumber(phoneSet);
				list.add(item);
			}
			phoneCursor.close();
		}
		cursor.close();
		return list;
	}

	/**
	 * ֪ͨ����Ϣ����
	 * @param context
	 * @param iconid
	 * @param tickerText
	 * @param contentTitle
	 * @param contentText
	 */
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
