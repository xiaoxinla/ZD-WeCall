package com.wecall.contacts;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.Toast;

import com.wecall.contacts.constants.Constants;
import com.wecall.contacts.fragment.LabelFragment;
import com.wecall.contacts.fragment.MainFragment;
import com.wecall.contacts.fragment.MineFragment;
import com.wecall.contacts.util.EncodeUtil;
import com.wecall.contacts.view.ChangeColorIconWithText;

/**
 * @author xiaoxin 2015-4-10
 */
public class MainActivity extends FragmentActivity implements OnClickListener,
		OnPageChangeListener {

	private static final String TAG = "MainActivity";
	private static final int EDIT_REQUEST_CODE = 1;
	private static final int SCAN_REQUEST_CODE = 2;

	private ViewPager mViewPager;
	private List<Fragment> mTabs = new ArrayList<Fragment>();
	private FragmentPagerAdapter mAdapter;
	private ActionBar mActionBar;
//	private SearchView mSearchView;
	private List<ChangeColorIconWithText> mTabIndicators = new ArrayList<ChangeColorIconWithText>();
	private MineFragment mineFragment;
	private MainFragment mainFragment;
	private LabelFragment labelFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// ����overflowͼ��һֱ��ʾ
		setOverflowButtonAlways();
		mActionBar = getActionBar();
		// ʹ���Ͻ�ͼ�겻�ɼ�
		mActionBar.setDisplayShowHomeEnabled(false);
		initView();
		initDatas();

		initEvent();

	}

	/**
	 * ��ʼ�������¼�
	 */
	private void initEvent() {

		mViewPager.setOnPageChangeListener(this);

	}

	// ��ʼ������
	private void initDatas() {
		mineFragment = new MineFragment();
		mainFragment = new MainFragment();
		labelFragment = new LabelFragment();
		mTabs.add(mineFragment);
		mTabs.add(mainFragment);
		mTabs.add(labelFragment);

		mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

			@Override
			public int getCount() {
				return mTabs.size();
			}

			@Override
			public Fragment getItem(int position) {
				return mTabs.get(position);
			}
		};
		mViewPager.setAdapter(mAdapter);
		// ����Ĭ�ϵ�ҳ��Ϊ�ڶ�ҳ
		clickTab(mTabIndicators.get(1));
	}

	// ��ʼ���ؼ�
	private void initView() {
		mViewPager = (ViewPager) findViewById(R.id.id_viewpager);
		mViewPager.setOffscreenPageLimit(2);

		ChangeColorIconWithText one = (ChangeColorIconWithText) findViewById(R.id.id_indicator_one);
		mTabIndicators.add(one);
		ChangeColorIconWithText two = (ChangeColorIconWithText) findViewById(R.id.id_indicator_two);
		mTabIndicators.add(two);
		ChangeColorIconWithText three = (ChangeColorIconWithText) findViewById(R.id.id_indicator_three);
		mTabIndicators.add(three);

		one.setOnClickListener(this);
		two.setOnClickListener(this);
		three.setOnClickListener(this);

		one.setIconAlpha(1.0f);

	}

	// ���ò˵�
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
//		mSearchView = (SearchView) menu.findItem(R.id.action_search)
//				.getActionView();
//		mSearchView.setQueryHint("������" + mainFragment.getContactAmount()
//				+ "λ��ϵ��");
//		mSearchView.setOnQueryTextListener(new OnQueryTextListener() {
//
//			@Override
//			public boolean onQueryTextSubmit(String arg0) {
//				Log.v(TAG, "onQueryTextSubmit:" + arg0);
//				mainFragment.filterData(arg0);
//				return false;
//			}
//
//			@Override
//			public boolean onQueryTextChange(String arg0) {
//				Log.v(TAG, "onQueryTextChange:" + arg0);
//				mainFragment.filterData(arg0);
//				return false;
//			}
//		});
		return true;
	}

	// ���ò˵����İ�ť����¼�
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		Bundle bundle;
		switch (item.getItemId()) {
		case R.id.action_search:
			Log.v(TAG, "searchview click");
			intent = new Intent(MainActivity.this,SearchActivity.class);
//			intent.putExtra("count", mainFragment.getContactAmount());
			startActivity(intent);
			break;
		case R.id.action_add_friend:
			intent = new Intent(MainActivity.this, ContactEditor.class);
			bundle = new Bundle();
			bundle.putInt("type", 1);
			intent.putExtras(bundle);
			startActivityForResult(intent, EDIT_REQUEST_CODE);
			break;
		case R.id.action_scan:
			intent = new Intent(MainActivity.this, MipcaActivityCapture.class);
			startActivityForResult(intent, SCAN_REQUEST_CODE);
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * ʹ�÷���ķ���ǿ����ʾoverflowͼ��
	 */
	private void setOverflowButtonAlways() {
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKey = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			menuKey.setAccessible(true);
			menuKey.setBoolean(config, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ���÷���ķ�������menu��ʾicon
	 */
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {

		if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
			if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
				try {
					Method m = menu.getClass().getDeclaredMethod(
							"setOptionalIconsVisible", Boolean.TYPE);
					m.setAccessible(true);
					m.invoke(menu, true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return super.onMenuOpened(featureId, menu);
	}

	@Override
	public void onClick(View v) {
		clickTab(v);

	}

	/**
	 * ���Tab��ť
	 * 
	 * @param v
	 */
	private void clickTab(View v) {
		resetOtherTabs();

		switch (v.getId()) {
		case R.id.id_indicator_one:
			mTabIndicators.get(0).setIconAlpha(1.0f);
			mViewPager.setCurrentItem(0, false);
			break;
		case R.id.id_indicator_two:
			mTabIndicators.get(1).setIconAlpha(1.0f);
			mViewPager.setCurrentItem(1, false);
			break;
		case R.id.id_indicator_three:
			mTabIndicators.get(2).setIconAlpha(1.0f);
			mViewPager.setCurrentItem(2, false);
			break;
		}
	}

	/**
	 * ����������TabIndicator����ɫ
	 */
	private void resetOtherTabs() {
		for (int i = 0; i < mTabIndicators.size(); i++) {
			mTabIndicators.get(i).setIconAlpha(0);
		}
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
//		Log.v(TAG, "onPageScrolled:position = " + position
//				+ " ,positionOffset =  " + positionOffset);
		mainFragment.initSideBar();
		if (positionOffset > 0) {
			ChangeColorIconWithText left = mTabIndicators.get(position);
			ChangeColorIconWithText right = mTabIndicators.get(position + 1);
			left.setIconAlpha(1 - positionOffset);
			right.setIconAlpha(positionOffset);
		}

	}

	@Override
	public void onPageSelected(int position) {
		Log.v(TAG, "onPageSelected:" + position);
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		Log.v(TAG, "onPageScrollStateChanged:" + state);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.v(TAG, "requestCode:" + requestCode + ",resultCode:" + resultCode);
		mainFragment.updateContacts();
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case EDIT_REQUEST_CODE:
				Toast.makeText(MainActivity.this, "��ӳɹ�", Toast.LENGTH_SHORT)
						.show();
				break;
			case SCAN_REQUEST_CODE:
				dealScanData(data);
				break;
			default:
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void dealScanData(Intent data) {
		Bundle bundle = data.getExtras();
		String obtained = bundle.getString("result");
		try {
			JSONObject jsonObject = new JSONObject(obtained);
			String name = jsonObject.getString("name");
			String phone = jsonObject.getString("phone");
			showDialog(true, name, phone);
		} catch (JSONException e) {
			e.printStackTrace();
			try {
				JSONObject jsonObject = new JSONObject(EncodeUtil.decrypt(
						Constants.AESKEY, obtained));
				String name = jsonObject.getString("name");
				String phone = jsonObject.getString("phone");
				showDialog(true, name, phone);
			} catch (JSONException e1) {
				e1.printStackTrace();
				Toast.makeText(this, "��Ч��Ϣ��" + bundle.getString("result"),
						Toast.LENGTH_LONG).show();
			} catch (Exception e1) {
				e1.printStackTrace();
				Toast.makeText(this, "��Ч��Ϣ��" + bundle.getString("result"),
						Toast.LENGTH_LONG).show();
			}
		}
	}

	private void showDialog(boolean isValid, final String name,
			final String phone) {
		if (isValid) {
			new AlertDialog.Builder(this)
					.setTitle("��ϵ��")
					.setMessage("������" + name + "\n�绰��" + phone + "\n�Ƿ���ӵ���ϵ�ˣ�")
					.setPositiveButton("ȷ��",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									Log.v(TAG, "PositiveClick");
									arg0.dismiss();
									Intent intent = new Intent(
											MainActivity.this,
											ContactEditor.class);
									Bundle bundle = new Bundle();
									bundle.putInt("type", 1);
									bundle.putString("name", name);
									bundle.putString("phone", phone);
									intent.putExtras(bundle);
									startActivityForResult(intent,
											EDIT_REQUEST_CODE);
								}
							})
					.setNegativeButton("ȡ��",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									Log.v(TAG, "NegativeClick");
									arg0.dismiss();
								}
							}).show();

		}
	}

	private long exitTime = 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// ʵ�ְ����·��ؼ��˳�����
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "�ٰ�һ���˳�����",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				moveTaskToBack(false);
				finish();

			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
