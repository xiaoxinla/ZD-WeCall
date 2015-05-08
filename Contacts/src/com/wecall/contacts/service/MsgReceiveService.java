package com.wecall.contacts.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.sina.push.PushManager;
import com.sina.push.model.ActionResult;
import com.sina.push.receiver.PushMsgRecvService;
import com.sina.push.receiver.event.IEvent;
import com.sina.push.response.PushDataPacket;
import com.wecall.contacts.constants.Constants;
import com.wecall.contacts.util.HttpConnectionUtils;
import com.wecall.contacts.util.RSAUtil;
import com.wecall.contacts.util.SPUtil;

public class MsgReceiveService extends PushMsgRecvService {

	private static final String TAG = "MsgReceiveService";
	// log信息，仅供测试使用
	private String log = new String();

	@Override
	public void onActionResult(IEvent<?> event) {
		// TODO Auto-generated method stub
		if (event.getType() == PushManager.ACTION_OPEN_CHANNEL) {

			// 调用打开，切换接口的执行结果
			ActionResult result = (ActionResult) event.getPayload();
			log = result + "\n";
			Log.v(TAG, log);

			if (result.getResultCode() == 1) {
				// 打开通道成功，可以正常接收Push和调用接口功能
				Log.v(TAG, (String) SPUtil.get(getApplicationContext(), "aid",
						"not exist"));
			}
		}
	}

	@Override
	public void onPush(IEvent<?> event) {
		try {
			switch (event.getType()) {
			case PushManager.MSG_TYPE_MPS_PUSH_DATA:
				PushDataPacket packet = (PushDataPacket) event.getPayload();
				// to do something

				log = "received MPS push:[appid=" + packet.getAppID()
						+ ",msgID=" + packet.getMsgID() + ",srcJson="
						+ packet.getSrcJson() + "\n";
				Log.v(TAG, log);
				break;
			case PushManager.MSG_TYPE_GET_AID:

				String aid = (String) event.getPayload();
				// to bind with uid

				SPUtil.put(getApplicationContext(), "aid", aid);
				int did = (Integer) SPUtil.get(this, "did", -1);
				if (did == -1) {
					getDid();
				}
				updateAid();
				log = "received aid:[" + aid + "]\n";

				Log.v(TAG, log);

				break;

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressLint("HandlerLeak") private void getDid() {
		String url = Constants.SERVER_URL + "/getdid.php";
		Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case HttpConnectionUtils.DID_SUCCEED:
					String response = RSAUtil.decryptByPublic((String) msg.obj);
					Log.v(TAG, response);
					int did = Integer.parseInt(response);
					SPUtil.put(getApplicationContext(), "did", did);
					updateAid();
					break;

				default:
					break;
				}
				super.handleMessage(msg);
			}

		};
		new HttpConnectionUtils(handler).get(url);
	}

	@SuppressLint("HandlerLeak")
	private void updateAid() {
		String url = Constants.SERVER_URL + "/updateaid.php";
		Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case HttpConnectionUtils.DID_SUCCEED:
					String response = (String) msg.obj;
					Log.v(TAG, response);
					// Log.v(TAG, RSAUtil.decryptByPublic(response));
					break;

				default:
					break;
				}
				super.handleMessage(msg);
			}

		};
		try {
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			list.add(new BasicNameValuePair("did", RSAUtil
					.encryptByPublic(String.valueOf((Integer) SPUtil.get(this,
							"did", -1)))));
			list.add(new BasicNameValuePair("aid", RSAUtil
					.encryptByPublic((String) SPUtil.get(this, "aid", ""))));
			list.add(new BasicNameValuePair("name", RSAUtil
					.encryptByPublic((String) SPUtil.get(this, "name", ""))));
			list.add(new BasicNameValuePair("phone", RSAUtil
					.encryptByPublic((String) SPUtil.get(this, "phone", ""))));
			new HttpConnectionUtils(handler).post(url, list);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
