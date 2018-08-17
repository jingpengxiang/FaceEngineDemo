package com.lu.face.faceenginedemo.engine.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.lu.face.faceenginedemo.R;
import com.lu.face.faceenginedemo.engine.adapter.AbstractSpinerAdapter;
import com.lu.face.faceenginedemo.engine.data.ConstantValues;
import com.lu.face.faceenginedemo.engine.utils.LogUtil;
import com.lu.face.faceenginedemo.engine.utils.SharedPreferencesUtil;
import com.lu.face.faceenginedemo.enginesdk.utils.StringUtil;
import com.lu.face.faceenginedemo.enginesdk.utils.ValueUtil;

import java.util.Timer;
import java.util.TimerTask;

public class SystemFragment extends Fragment {
	private static final String TAG = "SystemFragment";
	private Activity mActivity;
	private Resources mResources;
	private DisplayMetrics mDisplayMetrics = ValueUtil.getInstance().getmDisplayMetrics();
	private LinearLayout faceLl, fingerLl;
	private RelativeLayout lightRl, attributeRl, attributeConRl, attributeTopRl, modeRl, channelRl0, channelRl1, cameraTvRl, modeFaceRl, modeFingerRl, channelIvRl0, channelIvRl1,
			channelIvTvRl0, channelIvTvRl1, channelTopRl0, channelBottomRl0, channelNRl0, channelTopRl1, channelBottomRl1, channelNRl1;
	private TextView chTv0, chTv1, faceTv, fingerTv;
	private EditText frontFilterEt, backFilterEt, fingerThrEt, fingerQuaEt, topThrEt0, bottomThrEt0, nThrEt0, topThrEt1, bottomThrEt1, nThrEt1, minFaceEt, timeroutEt;
	private ImageView ch0Iv, ch1Iv, faceIv, fingerIv;
	private SpinerPopWindow faceSpw, fingerSpw, chSpw0, chSpw1;
	private SeekBar mSeekBar;
	private SwitchButton lightSb, faceFrameSb, faceFeatureSb, faceAttrSb, totalTimeSb, detailTimeSb, autoAddSb, faceVerSb, fingerVerSb, chSb0, chSb1;
	private long baseTime;
	private ViewStub systemVs;
	private View mView;
	private int brightness;
	private Timer mTimer;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		baseTime = System.currentTimeMillis();
		mActivity = this.getActivity();
		mResources = mActivity.getResources();
		mView = NoSaveStateFrameLayout.wrap(LayoutInflater.from(mActivity).inflate(R.layout.fragment_system, null));

		LogUtil.getInstance().i(TAG, "90 consume time = " + (System.currentTimeMillis() - baseTime));
		baseTime = System.currentTimeMillis();

		systemVs = (ViewStub) mView.findViewById(R.id.vs_system);

		LogUtil.getInstance().i(TAG, "93 consume time = " + (System.currentTimeMillis() - baseTime));
		baseTime = System.currentTimeMillis();

		mView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {
				// TODO Auto-generated method stub
				LogUtil.getInstance().i(TAG, "100 consume time = " + (System.currentTimeMillis() - baseTime));
				baseTime = System.currentTimeMillis();
				View systemView = null;
				if (null != systemVs) {
					systemView = systemVs.inflate();
				}
				if (null == systemView) {
					return;
				}

				mDisplayMetrics = mActivity.getResources().getDisplayMetrics();

				lightRl = (RelativeLayout) systemView.findViewById(R.id.rl_light);
				attributeRl = (RelativeLayout) systemView.findViewById(R.id.rl_attribute);
				attributeConRl = (RelativeLayout) systemView.findViewById(R.id.rl_attribute_content);
				attributeTopRl = (RelativeLayout) systemView.findViewById(R.id.rl_attribute_top);
				modeRl = (RelativeLayout) systemView.findViewById(R.id.rl_mode);
				channelRl0 = (RelativeLayout) systemView.findViewById(R.id.rl_channel_0);
				channelRl1 = (RelativeLayout) systemView.findViewById(R.id.rl_channel_1);
				TextView lightTitleTv = (TextView) systemView.findViewById(R.id.tv_light);
				lightTitleTv.setTextSize(ValueUtil.getInstance().getTextSize());
				TextView darkTv = (TextView) systemView.findViewById(R.id.tv_dark);
				darkTv.setTextSize(ValueUtil.getInstance().getTextSize());
				TextView brightTv = (TextView) systemView.findViewById(R.id.tv_bright);
				brightTv.setTextSize(ValueUtil.getInstance().getTextSize());
				TextView lightTv = (TextView) systemView.findViewById(R.id.tv_light_switch);
				lightTv.setTextSize(ValueUtil.getInstance().getTextSize());
				TextView attributeTv = (TextView) systemView.findViewById(R.id.tv_attribute);
				attributeTv.setTextSize(ValueUtil.getInstance().getTextSize());
				TextView faceFrameTv = (TextView) systemView.findViewById(R.id.tv_face_frame);
				faceFrameTv.setTextSize(ValueUtil.getInstance().getTextSize());
				TextView faceFeatureTv = (TextView) systemView.findViewById(R.id.tv_face_feature);
				faceFeatureTv.setTextSize(ValueUtil.getInstance().getTextSize());
				TextView faceAttibuteTv = (TextView) systemView.findViewById(R.id.tv_face_attibute);
				faceAttibuteTv.setTextSize(ValueUtil.getInstance().getTextSize());
				TextView totalTimeTv = (TextView) systemView.findViewById(R.id.tv_total_time);
				totalTimeTv.setTextSize(ValueUtil.getInstance().getTextSize());
				TextView detailTimeTv = (TextView) systemView.findViewById(R.id.tv_detail_time);
				detailTimeTv.setTextSize(ValueUtil.getInstance().getTextSize());
				TextView autoAddTv = (TextView) systemView.findViewById(R.id.tv_auto_add);
				autoAddTv.setTextSize(ValueUtil.getInstance().getTextSize());
				TextView modeTv = (TextView) systemView.findViewById(R.id.tv_mode);
				modeTv.setTextSize(ValueUtil.getInstance().getTextSize());
//				TextView modeFaceTv = (TextView) systemView.findViewById(R.id.tv_mode_face);
//				modeFaceTv.setTextSize(ValueUtil.getInstance().getTextSize());
				TextView faceVerifyTv = (TextView) systemView.findViewById(R.id.tv_face_verify);
				faceVerifyTv.setTextSize(ValueUtil.getInstance().getTextSize());
				TextView frontFilterTv = (TextView) systemView.findViewById(R.id.tv_front_filter);
				frontFilterTv.setTextSize(ValueUtil.getInstance().getTextSize());
				TextView backFilterTv = (TextView) systemView.findViewById(R.id.tv_back_filter);
				backFilterTv.setTextSize(ValueUtil.getInstance().getTextSize());
				TextView verifyTimeoutTv = (TextView) systemView.findViewById(R.id.tv_verify_timeout);
				verifyTimeoutTv.setTextSize(ValueUtil.getInstance().getTextSize());
				TextView minFaceTv = (TextView) systemView.findViewById(R.id.tv_min_face);
				minFaceTv.setTextSize(ValueUtil.getInstance().getTextSize());
				TextView fingerVerifyTv = (TextView) systemView.findViewById(R.id.tv_finger_verify);
				fingerVerifyTv.setTextSize(ValueUtil.getInstance().getTextSize());
				TextView fingerThresholdTv = (TextView) systemView.findViewById(R.id.tv_finger_threshold);
				fingerThresholdTv.setTextSize(ValueUtil.getInstance().getTextSize());
				TextView fingerQualityTv = (TextView) systemView.findViewById(R.id.tv_finger_quality);
				fingerQualityTv.setTextSize(ValueUtil.getInstance().getTextSize());
				TextView faceChannelSetTv = (TextView) systemView.findViewById(R.id.tv_face_channel_set);
				faceChannelSetTv.setTextSize(ValueUtil.getInstance().getTextSize());
//				TextView channel0Tv = (TextView) systemView.findViewById(R.id.tv_channel_0);
//				channel0Tv.setTextSize(ValueUtil.getInstance().getTextSize());
				TextView faceChannel0Tv = (TextView) systemView.findViewById(R.id.tv_face_channel_0);
				faceChannel0Tv.setTextSize(ValueUtil.getInstance().getTextSize());
				TextView topThreshold0Tv = (TextView) systemView.findViewById(R.id.tv_top_threshold_0);
				topThreshold0Tv.setTextSize(ValueUtil.getInstance().getTextSize());
				TextView bottomThreshold0Tv = (TextView) systemView.findViewById(R.id.tv_bottom_threshold_0);
				bottomThreshold0Tv.setTextSize(ValueUtil.getInstance().getTextSize());
				TextView nThreshold0Tv = (TextView) systemView.findViewById(R.id.tv_n_threshold_0);
				nThreshold0Tv.setTextSize(ValueUtil.getInstance().getTextSize());
				TextView nThreshold1Tv = (TextView) systemView.findViewById(R.id.tv_n_threshold_1);
				nThreshold1Tv.setTextSize(ValueUtil.getInstance().getTextSize());
//				TextView channel1Tv = (TextView) systemView.findViewById(R.id.tv_channel_1);
//				channel1Tv.setTextSize(ValueUtil.getInstance().getTextSize());
				TextView faceChannel1Tv = (TextView) systemView.findViewById(R.id.tv_face_channel_1);
				faceChannel1Tv.setTextSize(ValueUtil.getInstance().getTextSize());
				TextView topThreshold1Tv = (TextView) systemView.findViewById(R.id.tv_top_threshold_1);
				topThreshold1Tv.setTextSize(ValueUtil.getInstance().getTextSize());
				TextView bottomThreshold1Tv = (TextView) systemView.findViewById(R.id.tv_bottom_threshold_1);
				bottomThreshold1Tv.setTextSize(ValueUtil.getInstance().getTextSize());

				lightSb = (SwitchButton) systemView.findViewById(R.id.sbtn_light);
				mSeekBar = (SeekBar) systemView.findViewById(R.id.seekbar);
				boolean isCheckedLight = (Boolean) SharedPreferencesUtil.get(mActivity, ConstantValues.getSwitchLight(), false);
				mSeekBar.setEnabled(isCheckedLight);
				lightSb.setChecked(isCheckedLight);
				lightSb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
						// TODO Auto-generated method stub
						mSeekBar.setEnabled(arg1);
						SharedPreferencesUtil.put(mActivity, ConstantValues.getSwitchLight(), arg1);
					}
				});
				mSeekBar.setProgress((Integer) SharedPreferencesUtil.get(mActivity, ConstantValues.getLightBrightness(), 0));
				mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

					@Override
					public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
						// TODO Auto-generated method stub
						if (arg2) {
							brightness = arg1;
						}
					}

					@Override
					public void onStartTrackingTouch(SeekBar arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onStopTrackingTouch(SeekBar arg0) {
						// TODO Auto-generated method stub
						SharedPreferencesUtil.put(mActivity, ConstantValues.getLightBrightness(), brightness);
					}
				});

				faceFrameSb = (SwitchButton) systemView.findViewById(R.id.sbtn_face_frame);
				faceFrameSb.setChecked((Boolean) SharedPreferencesUtil.get(mActivity, ConstantValues.getSwitchFaceFrame(), false));
				faceFrameSb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
						// TODO Auto-generated method stub
						SharedPreferencesUtil.put(mActivity, ConstantValues.getSwitchFaceFrame(), arg1);
					}
				});

				faceFeatureSb = (SwitchButton) systemView.findViewById(R.id.sbtn_face_feature);
				faceFeatureSb.setChecked((Boolean) SharedPreferencesUtil.get(mActivity, ConstantValues.getSwitchFaceFeature(), false));
				faceFeatureSb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
						// TODO Auto-generated method stub
						SharedPreferencesUtil.put(mActivity, ConstantValues.getSwitchFaceFeature(), arg1);
					}
				});

				faceAttrSb = (SwitchButton) systemView.findViewById(R.id.sbtn_face_attribute);
				faceAttrSb.setChecked((Boolean) SharedPreferencesUtil.get(mActivity, ConstantValues.getSwitchFaceAttribute(), false));
				faceAttrSb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
						// TODO Auto-generated method stub
						SharedPreferencesUtil.put(mActivity, ConstantValues.getSwitchFaceAttribute(), arg1);
					}
				});

				totalTimeSb = (SwitchButton) systemView.findViewById(R.id.sbtn_total_time);
				totalTimeSb.setChecked((Boolean) SharedPreferencesUtil.get(mActivity, ConstantValues.getSwitchTotalTime(), false));
				totalTimeSb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
						// TODO Auto-generated method stub
						SharedPreferencesUtil.put(mActivity, ConstantValues.getSwitchTotalTime(), arg1);
					}
				});

				detailTimeSb = (SwitchButton) systemView.findViewById(R.id.sbtn_detail_time);
				detailTimeSb.setChecked((Boolean) SharedPreferencesUtil.get(mActivity, ConstantValues.getSwitchDetailTime(), false));
				detailTimeSb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
						// TODO Auto-generated method stub
						SharedPreferencesUtil.put(mActivity, ConstantValues.getSwitchDetailTime(), arg1);
					}
				});

				autoAddSb = (SwitchButton) systemView.findViewById(R.id.sbtn_auto_add);
				autoAddSb.setChecked((Boolean) SharedPreferencesUtil.get(mActivity, ConstantValues.getSwitchAutoAdd(), false));
				autoAddSb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
						// TODO Auto-generated method stub
						SharedPreferencesUtil.put(mActivity, ConstantValues.getSwitchAutoAdd(), arg1);
					}
				});

				faceLl = (LinearLayout) systemView.findViewById(R.id.ll_face);
				modeFaceRl = (RelativeLayout) systemView.findViewById(R.id.rl_mode_face);
				faceTv = (TextView) systemView.findViewById(R.id.tv_mode_face);
				faceTv.setTextSize(ValueUtil.getInstance().getTextSize());
				faceIv = (ImageView) systemView.findViewById(R.id.iv_mode_face);
				modeFaceRl.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						showFaceSpw();
					}
				});
				frontFilterEt = (EditText) systemView.findViewById(R.id.et_front_filter);
				frontFilterEt.addTextChangedListener(new MyTextWatcher(0, frontFilterEt));
				backFilterEt = (EditText) systemView.findViewById(R.id.et_back_filter);
				backFilterEt.addTextChangedListener(new MyTextWatcher(0, backFilterEt));
				timeroutEt = (EditText) systemView.findViewById(R.id.et_verify_timeout);
				timeroutEt.addTextChangedListener(new MyTextWatcher(0, timeroutEt));
				minFaceEt = (EditText) systemView.findViewById(R.id.et_min_face);
				minFaceEt.addTextChangedListener(new MyTextWatcher(0, minFaceEt));
				frontFilterEt.setTextSize(ValueUtil.getInstance().getTextSize());
				backFilterEt.setTextSize(ValueUtil.getInstance().getTextSize());
				timeroutEt.setTextSize(ValueUtil.getInstance().getTextSize());
				minFaceEt.setTextSize(ValueUtil.getInstance().getTextSize());

				boolean isCheckedFace = (Boolean) SharedPreferencesUtil.get(mActivity, ConstantValues.getSwitchFaceVerify(), false);
				faceVerSb = (SwitchButton) systemView.findViewById(R.id.sbtn_face_verify);
				faceVerSb.setChecked(isCheckedFace);
				refreshFace(isCheckedFace);
				faceVerSb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
						// TODO Auto-generated method stub
						SharedPreferencesUtil.put(mActivity, ConstantValues.getSwitchFaceVerify(), arg1);
						refreshFace(arg1);
					}
				});

				fingerLl = (LinearLayout) systemView.findViewById(R.id.ll_finger);
				modeFingerRl = (RelativeLayout) systemView.findViewById(R.id.rl_mode_finger);
				fingerThrEt = (EditText) systemView.findViewById(R.id.et_finger_threshold);
				fingerThrEt.addTextChangedListener(new MyTextWatcher(0, fingerThrEt));
				fingerQuaEt = (EditText) systemView.findViewById(R.id.et_finger_quality);
				fingerQuaEt.addTextChangedListener(new MyTextWatcher(0, fingerQuaEt));
				fingerThrEt.setTextSize(ValueUtil.getInstance().getTextSize());
				fingerQuaEt.setTextSize(ValueUtil.getInstance().getTextSize());
				fingerTv = (TextView) systemView.findViewById(R.id.tv_mode_finger);
				fingerTv.setTextSize(ValueUtil.getInstance().getTextSize());
				fingerIv = (ImageView) systemView.findViewById(R.id.iv_mode_finger);
				modeFingerRl.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						showFingerSpw();
					}
				});

				boolean isCheckedFinger = (Boolean) SharedPreferencesUtil.get(mActivity, ConstantValues.getSwitchFingerVerify(), false);
				fingerVerSb = (SwitchButton) systemView.findViewById(R.id.sbtn_finger_verify);
				fingerVerSb.setChecked(isCheckedFinger);
				refreshFinger(isCheckedFinger);
				fingerVerSb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
						// TODO Auto-generated method stub
						SharedPreferencesUtil.put(mActivity, ConstantValues.getSwitchFingerVerify(), arg1);
						refreshFinger(arg1);
					}
				});

				channelIvTvRl0 = (RelativeLayout) systemView.findViewById(R.id.rl_ch0);
				channelIvRl0 = (RelativeLayout) systemView.findViewById(R.id.rl_channel0_iv);
				chTv0 = (TextView) systemView.findViewById(R.id.tv_channel_0);
				chTv0.setTextSize(ValueUtil.getInstance().getTextSize());
				ch0Iv = (ImageView) systemView.findViewById(R.id.iv_channel_0);
				channelIvRl0.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						showCh0Spw();
					}
				});
				topThrEt0 = (EditText) systemView.findViewById(R.id.et_top_threshold_0);
				topThrEt0.addTextChangedListener(new MyTextWatcher(1, topThrEt0));
				bottomThrEt0 = (EditText) systemView.findViewById(R.id.et_bottom_threshold_0);
				bottomThrEt0.addTextChangedListener(new MyTextWatcher(1, bottomThrEt0));
				nThrEt0 = (EditText) systemView.findViewById(R.id.et_n_threshold_0);
				nThrEt0.addTextChangedListener(new MyTextWatcher(1, nThrEt0));
				topThrEt0.setTextSize(ValueUtil.getInstance().getTextSize());
				bottomThrEt0.setTextSize(ValueUtil.getInstance().getTextSize());
				nThrEt0.setTextSize(ValueUtil.getInstance().getTextSize());
				channelTopRl0 = (RelativeLayout) systemView.findViewById(R.id.rl_top_0);
				channelBottomRl0 = (RelativeLayout) systemView.findViewById(R.id.rl_bottom_0);
				channelNRl0 = (RelativeLayout) systemView.findViewById(R.id.rl_n_0);

				boolean isCheckedCh0 = (Boolean) SharedPreferencesUtil.get(mActivity, ConstantValues.getSwitchChannel0(), false);
				chSb0 = (SwitchButton) systemView.findViewById(R.id.sbtn_channel_0);
				chSb0.setChecked(isCheckedCh0);
				refreshChannel0(isCheckedCh0);
				chSb0.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
						// TODO Auto-generated method stub
						SharedPreferencesUtil.put(mActivity, ConstantValues.getSwitchChannel0(), arg1);
						refreshChannel0(arg1);
					}
				});

				channelIvTvRl1 = (RelativeLayout) systemView.findViewById(R.id.rl_ch1);
				channelIvRl1 = (RelativeLayout) systemView.findViewById(R.id.rl_channel1_iv);
				chTv1 = (TextView) systemView.findViewById(R.id.tv_channel_1);
				chTv1.setTextSize(ValueUtil.getInstance().getTextSize());
				ch1Iv = (ImageView) systemView.findViewById(R.id.iv_channel_1);
				channelIvRl1.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						showCh1Spw();
					}
				});
				topThrEt1 = (EditText) systemView.findViewById(R.id.et_top_threshold_1);
				topThrEt1.addTextChangedListener(new MyTextWatcher(1, topThrEt1));
				bottomThrEt1 = (EditText) systemView.findViewById(R.id.et_bottom_threshold_1);
				bottomThrEt1.addTextChangedListener(new MyTextWatcher(1, bottomThrEt1));
				nThrEt1 = (EditText) systemView.findViewById(R.id.et_n_threshold_1);
				nThrEt1.addTextChangedListener(new MyTextWatcher(1, nThrEt1));
				topThrEt1.setTextSize(ValueUtil.getInstance().getTextSize());
				bottomThrEt1.setTextSize(ValueUtil.getInstance().getTextSize());
				nThrEt1.setTextSize(ValueUtil.getInstance().getTextSize());
				channelTopRl1 = (RelativeLayout) systemView.findViewById(R.id.rl_top_1);
				channelBottomRl1 = (RelativeLayout) systemView.findViewById(R.id.rl_bottom_1);
				channelNRl1 = (RelativeLayout) systemView.findViewById(R.id.rl_n_1);

				boolean isCheckedCh1 = (Boolean) SharedPreferencesUtil.get(mActivity, ConstantValues.getSwitchChannel1(), false);
				chSb1 = (SwitchButton) systemView.findViewById(R.id.sbtn_channel_1);
				chSb1.setChecked(isCheckedCh1);
				refreshChannel1(isCheckedCh1);
				chSb1.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
						// TODO Auto-generated method stub
						SharedPreferencesUtil.put(mActivity, ConstantValues.getSwitchChannel1(), arg1);
						refreshChannel1(arg1);
					}
				});

				LayoutParams lightRlPas = (LayoutParams) lightRl.getLayoutParams();
				lightRlPas.height = (int) (0.14F * mDisplayMetrics.heightPixels);
				lightRl.setLayoutParams(lightRlPas);

				LayoutParams attributeRlPas = (LayoutParams) attributeRl.getLayoutParams();
				attributeRlPas.height = (int) (0.18F * mDisplayMetrics.heightPixels);
				attributeRl.setLayoutParams(attributeRlPas);

				LayoutParams modeRlPas = (LayoutParams) modeRl.getLayoutParams();
				modeRlPas.height = (int) (0.25F * mDisplayMetrics.heightPixels);
				modeRl.setLayoutParams(modeRlPas);

				LayoutParams faceLlPas = (LayoutParams) faceLl.getLayoutParams();
				faceLlPas.width = (int) (0.238F * mDisplayMetrics.widthPixels);
				// faceLlPas.height = LayoutParams.MATCH_PARENT;
				faceLl.setLayoutParams(faceLlPas);

				LayoutParams modeFaceRlPas = (LayoutParams) modeFaceRl.getLayoutParams();
				modeFaceRlPas.width = (int) (0.146F * mDisplayMetrics.widthPixels);
				modeFaceRlPas.height = (int) (0.042F * mDisplayMetrics.heightPixels);
				modeFaceRl.setLayoutParams(modeFaceRlPas);

				LayoutParams frontFilterEtPas = (LayoutParams) frontFilterEt.getLayoutParams();
				frontFilterEtPas.width = modeFaceRlPas.width;
				frontFilterEtPas.height = modeFaceRlPas.height;
				frontFilterEt.setLayoutParams(frontFilterEtPas);

				LayoutParams backFilterEtPas = (LayoutParams) backFilterEt.getLayoutParams();
				backFilterEtPas.width = modeFaceRlPas.width;
				backFilterEtPas.height = modeFaceRlPas.height;
				backFilterEt.setLayoutParams(backFilterEtPas);

				LayoutParams timeroutEtPas = (LayoutParams) timeroutEt.getLayoutParams();
				timeroutEtPas.width = modeFaceRlPas.width;
				timeroutEtPas.height = modeFaceRlPas.height;
				timeroutEt.setLayoutParams(timeroutEtPas);

				LayoutParams minFaceEtPas = (LayoutParams) minFaceEt.getLayoutParams();
				minFaceEtPas.width = modeFaceRlPas.width;
				minFaceEtPas.height = modeFaceRlPas.height;
				minFaceEt.setLayoutParams(minFaceEtPas);

				LayoutParams fingerLlPas = (LayoutParams) fingerLl.getLayoutParams();
				fingerLlPas.width = faceLlPas.width;
				// faceLlPas.height = LayoutParams.MATCH_PARENT;
				fingerLl.setLayoutParams(fingerLlPas);

				LayoutParams modeFingerRlPas = (LayoutParams) modeFingerRl.getLayoutParams();
				modeFingerRlPas.width = modeFaceRlPas.width;
				modeFingerRlPas.height = modeFaceRlPas.height;
				modeFingerRl.setLayoutParams(modeFingerRlPas);

				LayoutParams fingerThrEtPas = (LayoutParams) fingerThrEt.getLayoutParams();
				fingerThrEtPas.width = modeFaceRlPas.width;
				fingerThrEtPas.height = modeFaceRlPas.height;
				fingerThrEt.setLayoutParams(fingerThrEtPas);

				LayoutParams fingerQuaEtPas = (LayoutParams) fingerQuaEt.getLayoutParams();
				fingerQuaEtPas.width = modeFaceRlPas.width;
				fingerQuaEtPas.height = modeFaceRlPas.height;
				fingerQuaEt.setLayoutParams(fingerQuaEtPas);

				// int thumbWith = (int) (0.283F * cameraRlPas.height);

				LogUtil.getInstance().i(TAG, "180 consume time = " + (System.currentTimeMillis() - baseTime));
				baseTime = System.currentTimeMillis();

				// LayoutParams channelRl0Pas = (LayoutParams)
				// channelRl0.getLayoutParams();
				// channelRl0Pas.width = (int) (0.498F *
				// mDisplayMetrics.widthPixels);
				//// faceLlPas.height = LayoutParams.MATCH_PARENT;
				// channelRl0.setLayoutParams(channelRl0Pas);

				LayoutParams channelIvTvRl0Pas = (LayoutParams) channelIvTvRl0.getLayoutParams();
				channelIvTvRl0Pas.width = (int) (0.8F * faceLlPas.width);
				// faceLlPas.height = LayoutParams.MATCH_PARENT;
				channelIvTvRl0.setLayoutParams(channelIvTvRl0Pas);

				LayoutParams channelTopRl0Pas = (LayoutParams) channelTopRl0.getLayoutParams();
				channelTopRl0Pas.width = (int) (0.7F * faceLlPas.width);
				// faceLlPas.height = LayoutParams.MATCH_PARENT;
				channelTopRl0.setLayoutParams(channelTopRl0Pas);

				LayoutParams channelBottomRl0Pas = (LayoutParams) channelBottomRl0.getLayoutParams();
				channelBottomRl0Pas.width = (int) (0.7F * faceLlPas.width);
				// faceLlPas.height = LayoutParams.MATCH_PARENT;
				channelBottomRl0.setLayoutParams(channelBottomRl0Pas);

				LayoutParams channelNRl0Pas = (LayoutParams) channelNRl0.getLayoutParams();
				channelNRl0Pas.width = (int) (0.7F * faceLlPas.width);
				// faceLlPas.height = LayoutParams.MATCH_PARENT;
				channelNRl0.setLayoutParams(channelNRl0Pas);

				LayoutParams channelIvRl0Pas = (LayoutParams) channelIvRl0.getLayoutParams();
				channelIvRl0Pas.width = (int) (0.9F * modeFaceRlPas.width);
				channelIvRl0Pas.height = modeFaceRlPas.height;
				channelIvRl0.setLayoutParams(channelIvRl0Pas);

				LayoutParams topThrEt0Pas = (LayoutParams) topThrEt0.getLayoutParams();
				topThrEt0Pas.width = (int) (0.5F * modeFaceRlPas.width);
				topThrEt0Pas.height = modeFaceRlPas.height;
				topThrEt0.setLayoutParams(topThrEt0Pas);

				LayoutParams bottomThrEt0Pas = (LayoutParams) bottomThrEt0.getLayoutParams();
				bottomThrEt0Pas.width = (int) (0.5F * modeFaceRlPas.width);
				bottomThrEt0Pas.height = modeFaceRlPas.height;
				bottomThrEt0.setLayoutParams(bottomThrEt0Pas);

				LayoutParams nThrEt0Pas = (LayoutParams) nThrEt0.getLayoutParams();
				nThrEt0Pas.width = (int) (0.5F * modeFaceRlPas.width);
				nThrEt0Pas.height = modeFaceRlPas.height;
				nThrEt0.setLayoutParams(nThrEt0Pas);

				LayoutParams channelIvTvRl1Pas = (LayoutParams) channelIvTvRl1.getLayoutParams();
				channelIvTvRl1Pas.width = channelIvTvRl0Pas.width;
				// faceLlPas.height = LayoutParams.MATCH_PARENT;
				channelIvTvRl1.setLayoutParams(channelIvTvRl1Pas);

				LayoutParams channelTopRl1Pas = (LayoutParams) channelTopRl1.getLayoutParams();
				channelTopRl1Pas.width = channelTopRl0Pas.width;
				// faceLlPas.height = LayoutParams.MATCH_PARENT;
				channelTopRl1.setLayoutParams(channelTopRl1Pas);

				LayoutParams channelBottomRl1Pas = (LayoutParams) channelBottomRl1.getLayoutParams();
				channelBottomRl1Pas.width = channelBottomRl0Pas.width;
				// faceLlPas.height = LayoutParams.MATCH_PARENT;
				channelBottomRl1.setLayoutParams(channelBottomRl1Pas);

				LayoutParams channelNRl1Pas = (LayoutParams) channelNRl1.getLayoutParams();
				channelNRl1Pas.width = channelNRl0Pas.width;
				// faceLlPas.height = LayoutParams.MATCH_PARENT;
				channelNRl1.setLayoutParams(channelNRl1Pas);

				LayoutParams channelIvRl1Pas = (LayoutParams) channelIvRl1.getLayoutParams();
				channelIvRl1Pas.width = channelIvRl0Pas.width;
				channelIvRl1Pas.height = channelIvRl0Pas.height;
				channelIvRl1.setLayoutParams(channelIvRl1Pas);

				LayoutParams topThrEt1Pas = (LayoutParams) topThrEt1.getLayoutParams();
				topThrEt1Pas.width = topThrEt0Pas.width;
				topThrEt1Pas.height = topThrEt0Pas.height;
				topThrEt1.setLayoutParams(topThrEt1Pas);

				LayoutParams bottomThrEt1Pas = (LayoutParams) bottomThrEt1.getLayoutParams();
				bottomThrEt1Pas.width = bottomThrEt0Pas.width;
				bottomThrEt1Pas.height = bottomThrEt0Pas.height;
				bottomThrEt1.setLayoutParams(bottomThrEt1Pas);

				LayoutParams nThrEt1Pas = (LayoutParams) nThrEt1.getLayoutParams();
				nThrEt1Pas.width = nThrEt0Pas.width;
				nThrEt1Pas.height = nThrEt0Pas.height;
				nThrEt1.setLayoutParams(nThrEt1Pas);

				LayoutParams lightSbPas = (LayoutParams) lightSb.getLayoutParams();
				lightSbPas.leftMargin = (int) (0.1F * mDisplayMetrics.widthPixels);
				lightSb.setLayoutParams(lightSbPas);

				LayoutParams attributeConRlPas = (LayoutParams) attributeConRl.getLayoutParams();
				attributeConRlPas.leftMargin = lightSbPas.leftMargin;
				attributeConRl.setLayoutParams(attributeConRlPas);

				LayoutParams attributeTopRlPas = (LayoutParams) attributeTopRl.getLayoutParams();
				attributeTopRlPas.bottomMargin = (int) (0.02F * mDisplayMetrics.heightPixels);
				attributeTopRl.setLayoutParams(attributeTopRlPas);

				LayoutParams faceVerSbPas = (LayoutParams) faceVerSb.getLayoutParams();
				faceVerSbPas.leftMargin = lightSbPas.leftMargin;
				faceVerSb.setLayoutParams(faceVerSbPas);

				LayoutParams fingerVerSbPas = (LayoutParams) fingerVerSb.getLayoutParams();
				fingerVerSbPas.leftMargin = (int) (0.35F * mDisplayMetrics.widthPixels);
				fingerVerSb.setLayoutParams(fingerVerSbPas);

				LayoutParams channelRl0Pas = (LayoutParams) channelRl0.getLayoutParams();
				channelRl0Pas.leftMargin = lightSbPas.leftMargin;
				channelRl0Pas.bottomMargin = (int) (0.02F * mDisplayMetrics.heightPixels);
				channelRl0.setLayoutParams(channelRl0Pas);

				LayoutParams channelRl1Pas = (LayoutParams) channelRl1.getLayoutParams();
				channelRl1Pas.leftMargin = lightSbPas.leftMargin;
				channelRl1.setLayoutParams(channelRl1Pas);

				LayoutParams faceFeatureSbPas = (LayoutParams) faceFeatureSb.getLayoutParams();
				faceFeatureSbPas.leftMargin = (int) (0.13F * mDisplayMetrics.widthPixels);
				faceFeatureSb.setLayoutParams(faceFeatureSbPas);

				LayoutParams faceAttrSbPas = (LayoutParams) faceAttrSb.getLayoutParams();
				faceAttrSbPas.leftMargin = faceFeatureSbPas.leftMargin;
				faceAttrSb.setLayoutParams(faceAttrSbPas);

				LayoutParams exFeatureSbPas = (LayoutParams) detailTimeSb.getLayoutParams();
				exFeatureSbPas.leftMargin = faceFeatureSbPas.leftMargin;
				detailTimeSb.setLayoutParams(exFeatureSbPas);

				LayoutParams autoAddSbPas = (LayoutParams) autoAddSb.getLayoutParams();
				autoAddSbPas.leftMargin = faceFeatureSbPas.leftMargin;
				autoAddSb.setLayoutParams(autoAddSbPas);

				LayoutParams mSeekBarPas = (LayoutParams) mSeekBar.getLayoutParams();
				mSeekBarPas.width = (int) (0.333F * mDisplayMetrics.widthPixels);
				mSeekBar.setLayoutParams(mSeekBarPas);

				LayoutParams lightTvPas = (LayoutParams) lightTv.getLayoutParams();
				lightTvPas.rightMargin = (int) (0.1F * mDisplayMetrics.widthPixels);
				lightTv.setLayoutParams(lightTvPas);

				LogUtil.getInstance().i(TAG, "193 consume time = " + (System.currentTimeMillis() - baseTime));
				baseTime = System.currentTimeMillis();

				initFaceSpw();
				initFingerSpw();
				initCh0Spw();
				initCh1Spw();

				mView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
			}
		});
	}

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		// return mView;
		return NoSaveStateFrameLayout.wrap(mView);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
	}

	protected void initFaceSpw() {
		// TODO Auto-generated method stub
		final String[] verifys = ValueUtil.getInstance().getVerifys();
		faceSpw = new SpinerPopWindow(mActivity);
		faceSpw.refreshData(verifys, 0);
		faceSpw.setItemListener(new AbstractSpinerAdapter.IOnItemSelectListener() {

			@Override
			public void onItemClick(int position) {
				// TODO Auto-generated method stub
				if (position >= 0 && position <= verifys.length) {
					String type = verifys[position];
					faceTv.setText(type);
					SharedPreferencesUtil.put(mActivity, ConstantValues.getTypeFaceVerify(), type);
				}
			}
		});
	}

	protected void initFingerSpw() {
		// TODO Auto-generated method stub
		final String[] verifys = ValueUtil.getInstance().getVerifys();
		fingerSpw = new SpinerPopWindow(mActivity);
		fingerSpw.refreshData(verifys, 0);
		fingerSpw.setItemListener(new AbstractSpinerAdapter.IOnItemSelectListener() {

			@Override
			public void onItemClick(int position) {
				// TODO Auto-generated method stub
				if (position >= 0 && position <= verifys.length) {
					String type = verifys[position];
					fingerTv.setText(type);
					SharedPreferencesUtil.put(mActivity, ConstantValues.getTypeFingerVerify(), verifys[position]);
				}
			}
		});
	}

	protected void initCh0Spw() {
		// TODO Auto-generated method stub
		final String[] videos = ValueUtil.getInstance().getCameras();
		chSpw0 = new SpinerPopWindow(mActivity);
		chSpw0.refreshData(videos, 0);
		chSpw0.setItemListener(new AbstractSpinerAdapter.IOnItemSelectListener() {

			@Override
			public void onItemClick(int position) {
				// TODO Auto-generated method stub
				if (position >= 0 && position <= videos.length) {
					chTv0.setText(videos[position]);
					SharedPreferencesUtil.put(mActivity, ConstantValues.getTypeCamera(), position);
				}
			}
		});
	}

	protected void initCh1Spw() {
		// TODO Auto-generated method stub
		final String[] videos = ValueUtil.getInstance().getCameras();
		chSpw1 = new SpinerPopWindow(mActivity);
		chSpw1.refreshData(videos, 0);
		chSpw1.setItemListener(new AbstractSpinerAdapter.IOnItemSelectListener() {

			@Override
			public void onItemClick(int position) {
				// TODO Auto-generated method stub
				if (position >= 0 && position <= videos.length) {
					chTv1.setText(videos[position]);
					SharedPreferencesUtil.put(mActivity, ConstantValues.getTypeCamera(), position);
				}
			}
		});
	}

	private void showCh0Spw() {
		// TODO Auto-generated method stub
		chSpw0.setWidth(chTv0.getWidth());
		chSpw0.showAsDropDown(channelIvRl0);
	}

	private void showCh1Spw() {
		// TODO Auto-generated method stub
		chSpw1.setWidth(chTv1.getWidth());
		chSpw1.showAsDropDown(channelIvRl1);
	}

	private void showFaceSpw() {
		// TODO Auto-generated method stub
		faceSpw.setWidth(faceTv.getWidth());
		faceSpw.showAsDropDown(modeFaceRl);
	}

	private void showFingerSpw() {
		// TODO Auto-generated method stub
		fingerSpw.setWidth(fingerTv.getWidth());
		fingerSpw.showAsDropDown(modeFingerRl);
	}

	private void refreshFace(boolean isChecked) {
		modeFaceRl.setEnabled(isChecked);
		faceIv.setEnabled(isChecked);
		frontFilterEt.setEnabled(isChecked);
		backFilterEt.setEnabled(isChecked);
		timeroutEt.setEnabled(isChecked);
		minFaceEt.setEnabled(isChecked);
		if (isChecked) {
			faceTv.setText(String.valueOf(SharedPreferencesUtil.get(mActivity, ConstantValues.getTypeFaceVerify(), ValueUtil.getInstance().getVerifys()[0])));
			frontFilterEt.setText(String.valueOf(SharedPreferencesUtil.get(mActivity, ConstantValues.getFrontFilter(), 0)));
			backFilterEt.setText(String.valueOf(SharedPreferencesUtil.get(mActivity, ConstantValues.getBackFilter(), 0)));
			timeroutEt.setText(String.valueOf(SharedPreferencesUtil.get(mActivity, ConstantValues.getCheckTimeout(), 0)));
			minFaceEt.setText(String.valueOf(SharedPreferencesUtil.get(mActivity, ConstantValues.getMinFace(), 0)));
		} else {
			faceTv.setText("");
			frontFilterEt.getText().clear();
			backFilterEt.getText().clear();
			timeroutEt.getText().clear();
			minFaceEt.getText().clear();
		}
	}

	private void refreshFinger(boolean isChecked) {
		modeFingerRl.setEnabled(isChecked);
		fingerIv.setEnabled(isChecked);
		fingerThrEt.setEnabled(isChecked);
		fingerQuaEt.setEnabled(isChecked);
		if (isChecked) {
			fingerTv.setText(String.valueOf(SharedPreferencesUtil.get(mActivity, ConstantValues.getTypeFingerVerify(), ValueUtil.getInstance().getVerifys()[0])));
			fingerThrEt.setText(String.valueOf(SharedPreferencesUtil.get(mActivity, ConstantValues.getFingerThreshold(), 0)));
			fingerQuaEt.setText(String.valueOf(SharedPreferencesUtil.get(mActivity, ConstantValues.getFingerQuality(), 0)));
		} else {
			fingerTv.setText("");
			fingerThrEt.getText().clear();
			fingerQuaEt.getText().clear();
		}
	}

	private void refreshChannel0(boolean isChecked) {
		ch0Iv.setEnabled(isChecked);
		topThrEt0.setEnabled(isChecked);
		bottomThrEt0.setEnabled(isChecked);
		nThrEt0.setEnabled(isChecked);
		if (isChecked) {
			String[] cameras = ValueUtil.getInstance().getCameras();
			if (cameras.length < 2) {
				chTv0.setText(ValueUtil.getInstance().getCameras()[0]);
			} else {
				chTv0.setText(ValueUtil.getInstance().getCameras()[(Integer) SharedPreferencesUtil.get(mActivity, ConstantValues.getTypeCamera(), 1)]);
			}
			topThrEt0.setText(String.valueOf(SharedPreferencesUtil.get(mActivity, ConstantValues.getChannel0Top(), 0.00F)));
			bottomThrEt0.setText(String.valueOf((Float) SharedPreferencesUtil.get(mActivity, ConstantValues.getChannel0Bottom(), 0.00F)));
			nThrEt0.setText(String.valueOf((Float) SharedPreferencesUtil.get(mActivity, ConstantValues.getChannel0N(), 0.00F)));
		} else {
			chTv0.setText("");
			topThrEt0.getText().clear();
			bottomThrEt0.getText().clear();
			nThrEt0.getText().clear();
		}
	}

	private void refreshChannel1(boolean isChecked) {
		ch1Iv.setEnabled(isChecked);
		topThrEt1.setEnabled(isChecked);
		bottomThrEt1.setEnabled(isChecked);
		nThrEt1.setEnabled(isChecked);
		if (isChecked) {
			String[] cameras = ValueUtil.getInstance().getCameras();
			if (cameras.length < 2) {
				chTv1.setText(ValueUtil.getInstance().getCameras()[0]);
			} else {
				chTv1.setText(ValueUtil.getInstance().getCameras()[(Integer) SharedPreferencesUtil.get(mActivity, ConstantValues.getTypeCamera(), 1)]);
			}
			topThrEt1.setText(String.valueOf(SharedPreferencesUtil.get(mActivity, ConstantValues.getChannel1Top(), 0.00F)));
			bottomThrEt1.setText(String.valueOf(SharedPreferencesUtil.get(mActivity, ConstantValues.getChannel1Bottom(), 0.00F)));
			nThrEt1.setText(String.valueOf((Float) SharedPreferencesUtil.get(mActivity, ConstantValues.getChannel1N(), 0.00F)));
		} else {
			chTv1.setText("");
			topThrEt1.getText().clear();
			bottomThrEt1.getText().clear();
			nThrEt1.getText().clear();
		}
	}

	private class MyTextWatcher implements TextWatcher {
		private int type;
		private EditText mEditText;

		public MyTextWatcher(int type, EditText editText) {
			this.type = type;
			mEditText = editText;
		}

		@Override
		public void afterTextChanged(Editable editable) {
			if (!StringUtil.isNotNull(editable.toString())) {
				return;
			}
			if (1 == type) {// float类型参数 保留小数点后2位数
				String temp = editable.toString();
				int posDot = temp.indexOf(".");
//				if (posDot <= 0)
//					return;
				if (temp.length() - posDot - 1 > 2) {
					editable.delete(posDot + 3, posDot + 4);
				}
			}

			switch (mEditText.getId()) {
			case R.id.et_front_filter:
				if ((Boolean) SharedPreferencesUtil.get(mActivity, ConstantValues.getSwitchFaceVerify(), false)) {
					SharedPreferencesUtil.put(mActivity, ConstantValues.getFrontFilter(), Integer.valueOf(editable.toString()));
				}
				break;
			case R.id.et_back_filter:
				if ((Boolean) SharedPreferencesUtil.get(mActivity, ConstantValues.getSwitchFaceVerify(), false)) {
					SharedPreferencesUtil.put(mActivity, ConstantValues.getBackFilter(), Integer.valueOf(editable.toString()));
				}
				break;
			case R.id.et_verify_timeout:
				if ((Boolean) SharedPreferencesUtil.get(mActivity, ConstantValues.getSwitchFaceVerify(), false)) {
					int tmpTime = 0;
					try {
						tmpTime = Integer.valueOf(editable.toString());
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						tmpTime = 0;
					}
					if (tmpTime < 1) {
						tmpTime = 1;
					} else if (tmpTime > 10) {
						tmpTime = 10;
					}
					SharedPreferencesUtil.put(mActivity, ConstantValues.getCheckTimeout(), tmpTime);
					if (null != mTimer) {
						mTimer.cancel();
						mTimer = null;
					}
					mTimer = new Timer();
					mTimer.schedule(new TimerTask() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							mActivity.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									timeroutEt.setText(String.valueOf(SharedPreferencesUtil.get(mActivity, ConstantValues.getCheckTimeout(), 0)));
									timeroutEt.setSelection(timeroutEt.getText().length());
								}
							});
						}
					}, 5000);
				}
				break;
			case R.id.et_min_face:
				if ((Boolean) SharedPreferencesUtil.get(mActivity, ConstantValues.getSwitchFaceVerify(), false)) {
					int tmpFace = 0;
					try {
						tmpFace = Integer.valueOf(editable.toString());
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						tmpFace = 0;
					}
					if (tmpFace < 20) {
						tmpFace = 20;
					} else if (tmpFace > 200) {
						tmpFace = 200;
					}
					SharedPreferencesUtil.put(mActivity, ConstantValues.getMinFace(), tmpFace);
					if (null != mTimer) {
						mTimer.cancel();
						mTimer = null;
					}
					mTimer = new Timer();
					mTimer.schedule(new TimerTask() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							mActivity.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									minFaceEt.setText(String.valueOf(SharedPreferencesUtil.get(mActivity, ConstantValues.getMinFace(), 0)));
									minFaceEt.setSelection(minFaceEt.getText().length());
								}
							});
						}
					}, 5000);
				}
				break;
			case R.id.et_finger_threshold:
				if ((Boolean) SharedPreferencesUtil.get(mActivity, ConstantValues.getSwitchFingerVerify(), false)) {
					int tmpFingerTreshold = 0;
					try {
						tmpFingerTreshold = Integer.valueOf(editable.toString());
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						tmpFingerTreshold = 0;
					}
					if (tmpFingerTreshold < 0) {
						tmpFingerTreshold = 0;
					} else if (tmpFingerTreshold > 1000) {
						tmpFingerTreshold = 1000;
					}
					SharedPreferencesUtil.put(mActivity, ConstantValues.getFingerThreshold(), tmpFingerTreshold);
					if (null != mTimer) {
						mTimer.cancel();
						mTimer = null;
					}
					mTimer = new Timer();
					mTimer.schedule(new TimerTask() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							mActivity.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									fingerThrEt.setText(String.valueOf(SharedPreferencesUtil.get(mActivity, ConstantValues.getFingerThreshold(), 0)));
									fingerThrEt.setSelection(fingerThrEt.getText().length());
								}
							});
						}
					}, 5000);
				}
				break;
			case R.id.et_finger_quality:
				if ((Boolean) SharedPreferencesUtil.get(mActivity, ConstantValues.getSwitchFingerVerify(), false)) {
					int tmpFingerTreshold = 0;
					try {
						tmpFingerTreshold = Integer.valueOf(editable.toString());
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						tmpFingerTreshold = 0;
					}
					if (tmpFingerTreshold < 0) {
						tmpFingerTreshold = 0;
					} else if (tmpFingerTreshold > 100) {
						tmpFingerTreshold = 100;
					}
					SharedPreferencesUtil.put(mActivity, ConstantValues.getFingerQuality(), tmpFingerTreshold);
					if (null != mTimer) {
						mTimer.cancel();
						mTimer = null;
					}
					mTimer = new Timer();
					mTimer.schedule(new TimerTask() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							mActivity.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									fingerQuaEt.setText(String.valueOf(SharedPreferencesUtil.get(mActivity, ConstantValues.getFingerQuality(), 0)));
									fingerQuaEt.setSelection(fingerQuaEt.getText().length());
								}
							});
						}
					}, 5000);
				}
				break;
			case R.id.et_top_threshold_0:
				float tmpTop = 0F;
				try {
					tmpTop = Float.valueOf(editable.toString());
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					tmpTop = 0F;
				}
				if (tmpTop <= 0F) {
					tmpTop = 0.1F;
				} else if (tmpTop >= 1F) {
					tmpTop = 1F;
				}
				if (tmpTop < (Float) SharedPreferencesUtil.get(mActivity, ConstantValues.getChannel0Bottom(), 0.5F)) {
					SharedPreferencesUtil.put(mActivity, ConstantValues.getChannel0Top(), 0.7F);
					Toast.makeText(mActivity, mResources.getString(R.string.face_toast), Toast.LENGTH_SHORT).show();
				} else {
					SharedPreferencesUtil.put(mActivity, ConstantValues.getChannel0Top(), tmpTop);
				}
				if (null != mTimer) {
					mTimer.cancel();
					mTimer = null;
				}
				mTimer = new Timer();
				mTimer.schedule(new TimerTask() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						mActivity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								topThrEt0.setText(String.valueOf(SharedPreferencesUtil.get(mActivity, ConstantValues.getChannel0Top(), 0.7F)));
								topThrEt0.setSelection(topThrEt0.getText().length());
							}
						});
					}
				}, 5000);
				break;
			case R.id.et_bottom_threshold_0:
				float tmpBottom = 0F;
				try {
					tmpBottom = Float.valueOf(editable.toString());
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					tmpBottom = 0F;
				}
				if (tmpBottom <= 0F) {
					tmpBottom = 0.1F;
				} else if (tmpBottom >= 1F) {
					tmpBottom = 1F;
				}
				if (tmpBottom > (Float) SharedPreferencesUtil.get(mActivity, ConstantValues.getChannel0Top(), 0.7F)) {
					SharedPreferencesUtil.put(mActivity, ConstantValues.getChannel0Bottom(), 0.5F);
					Toast.makeText(mActivity, mResources.getString(R.string.face_toast), Toast.LENGTH_SHORT).show();
				} else {
					SharedPreferencesUtil.put(mActivity, ConstantValues.getChannel0Bottom(), tmpBottom);
				}

				if (null != mTimer) {
					mTimer.cancel();
					mTimer = null;
				}
				mTimer = new Timer();
				mTimer.schedule(new TimerTask() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						mActivity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								bottomThrEt0.setText(String.valueOf(SharedPreferencesUtil.get(mActivity, ConstantValues.getChannel0Bottom(), 0.5F)));
								bottomThrEt0.setSelection(bottomThrEt0.getText().length());
							}
						});
					}
				}, 5000);
				break;
			case R.id.et_n_threshold_0:
				float tmpN = 0F;
				try {
					tmpN = Float.valueOf(editable.toString());
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					tmpN = 0F;
				}
				if (tmpN <= 0F) {
					tmpN = 0.1F;
				} else if (tmpN >= 1F) {
					tmpN = 1F;
				}
				SharedPreferencesUtil.put(mActivity, ConstantValues.getChannel0N(), tmpN);
				if (null != mTimer) {
					mTimer.cancel();
					mTimer = null;
				}
				mTimer = new Timer();
				mTimer.schedule(new TimerTask() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						mActivity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								nThrEt0.setText(String.valueOf(SharedPreferencesUtil.get(mActivity, ConstantValues.getChannel0N(), 0.5F)));
								nThrEt0.setSelection(nThrEt0.getText().length());
							}
						});
					}
				}, 5000);
				break;
			case R.id.et_top_threshold_1:
				float tmpTop1 = 0F;
				try {
					tmpTop1 = Float.valueOf(editable.toString());
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					tmpTop1 = 0F;
				}
				if (tmpTop1 <= 0F) {
					tmpTop1 = 0.1F;
				} else if (tmpTop1 >= 1F) {
					tmpTop1 = 1F;
				}
				if (tmpTop1 < (Float) SharedPreferencesUtil.get(mActivity, ConstantValues.getChannel1Bottom(), 0.5F)) {
					SharedPreferencesUtil.put(mActivity, ConstantValues.getChannel1Top(), 0.7F);
					Toast.makeText(mActivity, mResources.getString(R.string.face_toast), Toast.LENGTH_SHORT).show();
				} else {
					SharedPreferencesUtil.put(mActivity, ConstantValues.getChannel1Top(), tmpTop1);
				}
				if (null != mTimer) {
					mTimer.cancel();
					mTimer = null;
				}
				mTimer = new Timer();
				mTimer.schedule(new TimerTask() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						mActivity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								topThrEt1.setText(String.valueOf(SharedPreferencesUtil.get(mActivity, ConstantValues.getChannel1Top(), 0.7F)));
								topThrEt1.setSelection(topThrEt1.getText().length());
							}
						});
					}
				}, 5000);
				break;
			case R.id.et_bottom_threshold_1:
				float tmpBottom1 = 0F;
				try {
					tmpBottom1 = Float.valueOf(editable.toString());
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					tmpBottom1 = 0F;
				}
				if (tmpBottom1 <= 0F) {
					tmpBottom1 = 0.1F;
				} else if (tmpBottom1 >= 1F) {
					tmpBottom1 = 1F;
				}
				if (tmpBottom1 > (Float) SharedPreferencesUtil.get(mActivity, ConstantValues.getChannel1Top(), 0.7F)) {
					SharedPreferencesUtil.put(mActivity, ConstantValues.getChannel1Bottom(), 0.5F);
					Toast.makeText(mActivity, mResources.getString(R.string.face_toast), Toast.LENGTH_SHORT).show();
				} else {
					SharedPreferencesUtil.put(mActivity, ConstantValues.getChannel1Bottom(), tmpBottom1);
				}

				if (null != mTimer) {
					mTimer.cancel();
					mTimer = null;
				}
				mTimer = new Timer();
				mTimer.schedule(new TimerTask() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						mActivity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								bottomThrEt1.setText(String.valueOf(SharedPreferencesUtil.get(mActivity, ConstantValues.getChannel1Bottom(), 0.5F)));
								bottomThrEt1.setSelection(bottomThrEt1.getText().length());
							}
						});
					}
				}, 5000);
				break;
			case R.id.et_n_threshold_1:
				float tmpN1 = 0F;
				try {
					tmpN1 = Float.valueOf(editable.toString());
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					tmpN1 = 0F;
				}
				if (tmpN1 <= 0F) {
					tmpN1 = 0.1F;
				} else if (tmpN1 >= 1F) {
					tmpN1 = 1F;
				}
				SharedPreferencesUtil.put(mActivity, ConstantValues.getChannel1N(), tmpN1);
				if (null != mTimer) {
					mTimer.cancel();
					mTimer = null;
				}
				mTimer = new Timer();
				mTimer.schedule(new TimerTask() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						mActivity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								nThrEt1.setText(String.valueOf(SharedPreferencesUtil.get(mActivity, ConstantValues.getChannel1N(), 0.5F)));
								nThrEt1.setSelection(nThrEt1.getText().length());
							}
						});
					}
				}, 5000);
				break;
			default:
				break;
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}
	}
}
