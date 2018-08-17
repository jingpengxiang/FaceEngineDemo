package com.lu.face.faceenginedemo.engine;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.lu.face.faceenginedemo.R;
import com.lu.face.faceenginedemo.engine.adapter.ViewPagerAdapter;
import com.lu.face.faceenginedemo.engine.view.FpFragment;
import com.lu.face.faceenginedemo.engine.view.LibraryFragment;
import com.lu.face.faceenginedemo.engine.view.PagerSlidingTabStrip;
import com.lu.face.faceenginedemo.engine.view.SystemFragment;

import java.util.ArrayList;
import java.util.List;

public class ConfigureActivity extends FragmentActivity {
	private static final String TAG = "ConfigureActivity";
	private ViewPager mViewPager;
	private PagerSlidingTabStrip mSlidingTab;
	private SystemFragment mSystemFragment;
	private LibraryFragment mLibraryFragment;
	private FpFragment mFpFragment;
	private List<String> titleList = new ArrayList<String>();
	private List<Fragment> fragList = new ArrayList<Fragment>();
	private ViewPagerAdapter mViewPagerAdapter;
	private DisplayMetrics mDisplayMetrics; // 获取当前屏幕密度
	private RelativeLayout rootRl;
	private long baseTime;
	private LibraryFragment.FacePopupWindowDismissListener mFacePopupWindowDismissListener;
	private FpFragment.FpPopupWindowDismissListener mFpPopupWindowDismissListener;
	private Resources mResources;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}

	@SuppressLint("NewApi")
	private void initView() {
		// TODO Auto-generated method stub
		baseTime = System.currentTimeMillis();
		mResources = getResources();
		setContentView(R.layout.activity_configure);
//		rootRl = (RelativeLayout) findViewById(R.id.rl_root);
		mDisplayMetrics = getResources().getDisplayMetrics();

		mViewPager = (ViewPager) findViewById(R.id.view_pager);
		mSlidingTab = (PagerSlidingTabStrip) findViewById(R.id.sliding_tab);
		mSystemFragment = new SystemFragment();
		mLibraryFragment = new LibraryFragment();
		mFpFragment = new FpFragment();
		titleList.add(mResources.getString(R.string.config_system));
		titleList.add(mResources.getString(R.string.config_face));
		titleList.add(mResources.getString(R.string.config_fp));
		fragList.add(mSystemFragment);
		fragList.add(mLibraryFragment);
		fragList.add(mFpFragment);
		mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), titleList, fragList);
		mViewPager.setAdapter(mViewPagerAdapter);
		mSlidingTab.setViewPager(mViewPager);
		setTabsValue();

//		LogUtil.getInstance().i(TAG, "66 consume time = " + (System.currentTimeMillis() - baseTime));
		baseTime = System.currentTimeMillis();
//		rootRl.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
//
//			@Override
//			public void onGlobalLayout() {
//				// TODO Auto-generated method stub
//
////				LogUtil.getInstance().i(TAG, "75 consume time = " + (System.currentTimeMillis() - baseTime));
//				baseTime = System.currentTimeMillis();
//
//				LayoutParams rootRlPas = (LayoutParams) rootRl.getLayoutParams();
//				rootRlPas.leftMargin = (int) (0.043F * mDisplayMetrics.widthPixels);
//				rootRlPas.topMargin = (int) (0.049F * mDisplayMetrics.heightPixels);
//				rootRl.setLayoutParams(rootRlPas);
//
////				LayoutParams mSlidingTabPas = (LayoutParams) mSlidingTab.getLayoutParams();
////				mSlidingTabPas.height = (int) (0.056F * mDisplayMetrics.heightPixels);
////				mSlidingTab.setLayoutParams(mSlidingTabPas);
//
////				LogUtil.getInstance().i(TAG, "87 consume time = " + (System.currentTimeMillis() - baseTime));
//				baseTime = System.currentTimeMillis();
//
//				rootRl.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//			}
//		});
	}

	@Override
	public void onBackPressed() {
		if ((null != mFacePopupWindowDismissListener && mFacePopupWindowDismissListener.isPopupWindowShowing())
				|| (null != mFpPopupWindowDismissListener && mFpPopupWindowDismissListener.isPopupWindowShowing())) {
			return;// 不在分发触控给子控件
		}
		if (null != mLibraryFragment) {
			mLibraryFragment.onBackPressed();
		}
		if (null != mFpFragment) {
			mFpFragment.onBackPressed();
		}
		super.onBackPressed();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		if ((null != mFacePopupWindowDismissListener && mFacePopupWindowDismissListener.isPopupWindowShowing())
				|| (null != mFpPopupWindowDismissListener && mFpPopupWindowDismissListener.isPopupWindowShowing())) {
			return false;// 不在分发触控给子控件
		}
		return super.dispatchTouchEvent(event);
	}

	private void setTabsValue() {
		// 设置Tab是自动填充满屏幕的
		mSlidingTab.setShouldExpand(false);
		// 设置Tab的分割线是透明的
		mSlidingTab.setDividerColor(Color.TRANSPARENT);
		// 设置Tab底部线的高度
		mSlidingTab.setUnderlineHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, mDisplayMetrics));
		// 设置Tab Indicator的高度
		mSlidingTab.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, mDisplayMetrics));
		// 设置Tab标题文字的大小
		mSlidingTab.setTextSize((int) (0.03F * mDisplayMetrics.widthPixels));
		// 设置Tab标题默认的颜色
		mSlidingTab.setTextColor(Color.parseColor("#b3ffffff"));
		// 设置选中Tab标题的颜色
		mSlidingTab.setSelectedTextColor(Color.WHITE);
		// 设置Tab底部线的颜色
		mSlidingTab.setUnderlineColor(Color.parseColor("#33ffffff"));
		// 设置Tab Indicator的颜色
		mSlidingTab.setIndicatorColor(getResources().getColor(R.color.get_record_line_selected_color));
		// 取消点击Tab时的背景色
		mSlidingTab.setTabBackground(R.drawable.btn_tab_title_selector);
		mSlidingTab.setDividerPadding(20);
//		mSlidingTab.setTabPaddingLeftRight((int) (0.004F * mDisplayMetrics.widthPixels));
	}

	public LibraryFragment.FacePopupWindowDismissListener getmFacePopupWindowDismissListener() {
		return mFacePopupWindowDismissListener;
	}

	public void setmFacePopupWindowDismissListener(LibraryFragment.FacePopupWindowDismissListener mFacePopupWindowDismissListener) {
		this.mFacePopupWindowDismissListener = mFacePopupWindowDismissListener;
	}

	public FpFragment.FpPopupWindowDismissListener getmFpPopupWindowDismissListener() {
		return mFpPopupWindowDismissListener;
	}

	public void setmFpPopupWindowDismissListener(FpFragment.FpPopupWindowDismissListener mFpPopupWindowDismissListener) {
		this.mFpPopupWindowDismissListener = mFpPopupWindowDismissListener;
	}
}
