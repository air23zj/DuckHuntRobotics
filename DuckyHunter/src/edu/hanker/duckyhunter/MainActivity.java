package edu.hanker.duckyhunter;


import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity implements android.view.View.OnClickListener {

	/**
	 * 用于展示消息的Fragment
	 */
	private MessageFragment messageFragment;


	private CameraFragment cameraFragment;


	private View messageLayout;
	private View cameraLayout;
	private ImageView messageImage;
	private ImageView cameraImage;
	private TextView messageText;
	private TextView cameraText;


	/**
	 * 用于对Fragment进行管理
	 */
	private FragmentManager fragmentManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

	    /*force a portrait layout*/
	    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_main);
		// 初始化布局元素
		initViews();
		fragmentManager = getFragmentManager();
		// 第一次启动时选中第0个tab
		setTabSelection(0);
	}

	/**
	 * 在这里获取到每个需要用到的控件的实例，并给它们设置好必要的点击事件。
	 */
	private void initViews() {
		messageLayout = findViewById(R.id.message_layout);
		cameraLayout = findViewById(R.id.camera_layout);
		messageImage = (ImageView) findViewById(R.id.message_image);
		cameraImage = (ImageView) findViewById(R.id.camera_image);
		messageText = (TextView) findViewById(R.id.message_text);
		cameraText = (TextView) findViewById(R.id.camera_text);
		messageLayout.setOnClickListener(this);
		cameraLayout.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.message_layout:
			// click tab 1
			setTabSelection(0);
			break;
		case R.id.camera_layout:
			// click tab 2
			setTabSelection(1);
			break;

		default:
			break;
		}
	}

	/**
	 * 根据传入的index参数来设置选中的tab页。
	 * 
	 * @param index
	 *            每个tab页对应的下标。0表示消息，1表示联系人，2表示动态，3表示设置。
	 */
	private void setTabSelection(int index) {
		// 每次选中之前先清楚掉上次的选中状态
		clearSelection();
		// 开启一个Fragment事务
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		// 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
		hideFragments(transaction);
		switch (index) {
		case 0:
			
			messageImage.setImageResource(R.drawable.ic_tab_more_unselected);
			messageText.setTextColor(Color.WHITE);
			if (messageFragment == null) {
				// 如果MessageFragment为空，则创建一个并添加到界面上
				messageFragment = new MessageFragment();
				transaction.add(R.id.content, messageFragment);
			} else {
				// 如果MessageFragment不为空，则直接将它显示出来
				transaction.show(messageFragment);
			}
			break;
		case 1:
			
			cameraImage.setImageResource(R.drawable.ic_tab_testicon_unselected);
			cameraText.setTextColor(Color.WHITE);
			if (cameraFragment == null) {
				// 如果ContactsFragment为空，则创建一个并添加到界面上
				cameraFragment = new CameraFragment();
				transaction.add(R.id.content, cameraFragment);
			} else {
				// 如果ContactsFragment不为空，则直接将它显示出来
				transaction.show(cameraFragment);
			}
			break;

		default:
			cameraImage.setImageResource(R.drawable.ic_tab_testicon_unselected);
			cameraText.setTextColor(Color.WHITE);
			if (cameraFragment == null) {
				// 如果ContactsFragment为空，则创建一个并添加到界面上
				cameraFragment = new CameraFragment();
				transaction.add(R.id.content, cameraFragment);
			} else {
				// 如果ContactsFragment不为空，则直接将它显示出来
				transaction.show(cameraFragment);
			}
			break;
		}
		transaction.commit();
	}

	/**
	 * 清除掉所有的选中状态。
	 */
	private void clearSelection() {
		messageImage.setImageResource(R.drawable.ic_tab_more_selected);
		messageText.setTextColor(Color.parseColor("#82858b"));
		cameraImage.setImageResource(R.drawable.ic_tab_testicon_selected);
		cameraText.setTextColor(Color.parseColor("#82858b"));
	}

	/**
	 * 将所有的Fragment都置为隐藏状态。
	 * 
	 * @param transaction
	 *            用于对Fragment执行操作的事务
	 */
	private void hideFragments(FragmentTransaction transaction) {
		if (messageFragment != null) {
			transaction.hide(messageFragment);
		}
		if (cameraFragment != null) {
			transaction.hide(cameraFragment);
		}

	}


}