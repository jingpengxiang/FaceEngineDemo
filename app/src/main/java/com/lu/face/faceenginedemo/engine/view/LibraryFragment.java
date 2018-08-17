package com.lu.face.faceenginedemo.engine.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.lu.face.faceenginedemo.R;
import com.lu.face.faceenginedemo.engine.ConfigureActivity;
import com.lu.face.faceenginedemo.engine.FileListActivity;
import com.lu.face.faceenginedemo.engine.adapter.FaceLibAdapter;
import com.lu.face.faceenginedemo.engine.adapter.FailAdapter;
import com.lu.face.faceenginedemo.engine.application.MainApplication;
import com.lu.face.faceenginedemo.engine.data.ConstantValues;
import com.lu.face.faceenginedemo.engine.models.PersonFeature;
import com.lu.face.faceenginedemo.engine.utils.FeatureUtil;
import com.lu.face.faceenginedemo.engine.utils.LogUtil;
import com.lu.face.faceenginedemo.enginesdk.utils.StringUtil;
import com.lu.face.faceenginedemo.enginesdk.utils.ValueUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LibraryFragment extends Fragment implements Callback {
	private final String TAG = "LibraryFragment";
	private final int BTN_ADD = 0;
	private final int ITEM_ADD = 1;
	private final int LV_SHOW = 2;
	private final int CLEAR_EDIT = 4;
	private ConfigureActivity mActivity;
	private DisplayMetrics mDisplayMetrics = ValueUtil.getInstance().getmDisplayMetrics();
	private RelativeLayout topRl;
	private ListView libLv;
	private Button delBtn, allDelBtn;
	private FaceLibAdapter mFaceLibAdapter;
	private View mView;
	private ViewStub libraryVs;
	private boolean isLvInited;
	private Button queryBtn, addBtn;
	private EditText queryEt;
	private TextView libsTv;
	private String preCondition;
	private List<PersonFeature> preList = new ArrayList<PersonFeature>();
	private FeatureUtil mFeatureUtil;
	private Handler mHandler;
	private PopupWindow mPopupWindow;
	private ProgressBar mProgressBar;
	private ListView failLv;
	private List<String> failList = new ArrayList<String>();
	private TextView progressTv, failTv;
	private RelativeLayout progressRl, lvRl;
	private int successNum, totalNum, failNum;
	private View headerView;
	private int personId;
	private Resources mResources;
	private FaceLibAdapter.FaceLibAdapterCallback mFaceLibAdapterCallback = new FaceLibAdapter.FaceLibAdapterCallback() {

		@Override
		public void onListChanged(int personId) {
			// TODO Auto-generated method stub
			Iterator<PersonFeature> it = preList.iterator();
			while (it.hasNext()) {
				PersonFeature person = (PersonFeature) it.next();
				if (personId == person.getPersonCommon().getPersonId()) {
					it.remove();
					break;
				}
			}
			Iterator<PersonFeature> itTmp = ValueUtil.getInstance().getPersonList().iterator();
			while (itTmp.hasNext()) {
				PersonFeature person = (PersonFeature) itTmp.next();
				if (personId == person.getPersonCommon().getPersonId()) {
					itTmp.remove();
					break;
				}
			}
		}

		@Override
		public void onListUpdate() {
			// TODO Auto-generated method stub
			if (0 == mFaceLibAdapter.getCount()) {
				allDelBtn.setVisibility(View.GONE);
				ValueUtil.getInstance().getPersonList().clear();
			}
			resetLibsTvData();
		}

		@Override
		public void onNameClick(int position) {
			// TODO Auto-generated method stub
			personId = mFaceLibAdapter.getItem(position).getPersonCommon().getPersonId();
			Intent intent = new Intent(mActivity, FileListActivity.class);
			startActivityForResult(intent, ITEM_ADD);
		}
	};

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.getInstance().i(TAG, "122 onCreate()");
		mActivity = (ConfigureActivity) this.getActivity();
		mResources = mActivity.getResources();
		mHandler = new Handler(this);
		MainApplication mApp = (MainApplication) mActivity.getApplication();
		mFeatureUtil = new FeatureUtil(mActivity, mApp.getmHSFaceEngineInit().getmHSFaceEngine(), mHandler);
		mActivity.setmFacePopupWindowDismissListener(new FacePopupWindowDismissListener() {

			@Override
			public boolean isPopupWindowShowing() {
				// TODO Auto-generated method stub
				return null != mPopupWindow && mPopupWindow.isShowing();
			}
		});
		mDisplayMetrics = mActivity.getResources().getDisplayMetrics();
		mView = NoSaveStateFrameLayout.wrap(LayoutInflater.from(mActivity).inflate(R.layout.fragment_library, null));
		libraryVs = (ViewStub) mView.findViewById(R.id.vs_library);
		mView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {
				// TODO Auto-generated method stub
				View libraryView = null;
				if (null != libraryVs) {
					libraryView = libraryVs.inflate();
				}
				if (null == libraryView) {
					return;
				}
				topRl = (RelativeLayout) libraryView.findViewById(R.id.rl_top);
				queryBtn = (Button) libraryView.findViewById(R.id.btn_lib_query);
				queryBtn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						if (null != mActivity && !mActivity.isFinishing()) {
							InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
							IBinder iBinder = null;
							View view = mActivity.getCurrentFocus();
							if (null != view) {
								iBinder = view.getWindowToken();
							}
							if (null != imm && null != iBinder) {
								imm.hideSoftInputFromWindow(iBinder, InputMethodManager.HIDE_NOT_ALWAYS);
							}
						}
						String txt = queryEt.getText().toString();
						if (!StringUtil.isNotNull(txt)) {
							Toast.makeText(getActivity(), mResources.getString(R.string.query_null), Toast.LENGTH_SHORT).show();
						} else if (!StringUtil.isEqual(txt, preCondition) && (0 < ValueUtil.getInstance().getPersonList().size() || 0 < preList.size())) {
							if (0 == preList.size()) {
								preList.addAll(ValueUtil.getInstance().getPersonList());
							}
							preCondition = txt;
							List<PersonFeature> personList = new ArrayList<PersonFeature>();
							for (int i = 0; i < preList.size(); i++) {
								PersonFeature pf = preList.get(i);
								String name = pf.getPersonCommon().getName();
								String cardId = pf.getPersonCommon().getCardId();
								if (txt.contains("_")) {
									if (StringUtil.isNotNull(name) && StringUtil.isNotNull(cardId)) {
										if ((cardId + "_" + name).contains(txt)) {
											personList.add(pf);
										}
									} else if (StringUtil.isNotNull(name) && name.contains(txt)) {
										personList.add(pf);
									}
								} else {
									if ((StringUtil.isNotNull(name) && name.contains(txt)) || (StringUtil.isNotNull(cardId) && cardId.contains(txt))) {
										personList.add(pf);
									}
								}
								/*List<Feature> featureList = new ArrayList<Feature>();
								List<Feature> list = preList.get(i).getFeatureList();
								if (null != list && 0 < list.size()) {
									for (int j = 0; j < list.size(); j++) {
										if (FileUtil.getInstance().getNameByPath(list.get(j).getFeatureCommon().getPath()).contains(txt)) {
											featureList.add(list.get(j));
										}
									}
								}
								if (0 < featureList.size()) {
									Person person = new Person();
									person.setPersonCommon(preList.get(i).getPersonCommon());
								//									person.setFeatureList(featureList);
									person.setPath(preList.get(i).getPath());
									personList.add(person);
								}*/
							}
							ValueUtil.getInstance().getPersonList().clear();
							if (0 < personList.size()) {
								ValueUtil.getInstance().getPersonList().addAll(personList);
							}
							mFaceLibAdapter.setQuerying(true);
							mFaceLibAdapter.setCheckBoxShowing(false);
							mFaceLibAdapter.resetData();
							delBtn.setText(mResources.getString(R.string.all_select));
							allDelBtn.setVisibility(View.GONE);
							resetLibsTvData();
						}
					}
				});
				addBtn = (Button) libraryView.findViewById(R.id.btn_lib_add);
				queryEt = (EditText) libraryView.findViewById(R.id.et_query);
				queryEt.addTextChangedListener(new TextWatcher() {

					@Override
					public void afterTextChanged(Editable arg0) {
						// TODO Auto-generated method stub
						if (0 == arg0.toString().length() && 0 < preList.size()) {
							new Thread(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									ValueUtil.getInstance().getPersonList().clear();
									ValueUtil.getInstance().getPersonList().addAll(preList);
									preList.clear();
									preCondition = "";
									mHandler.obtainMessage(CLEAR_EDIT).sendToTarget();
								}
							}).start();
						}
					}

					@Override
					public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
						// TODO Auto-generated method stub

					}
				});
				queryEt.setOnFocusChangeListener(new OnFocusChangeListener() {

					@Override
					public void onFocusChange(View arg0, boolean arg1) {
						// TODO Auto-generated method stub
						if (!arg1) {
							if (null != mActivity && !mActivity.isFinishing()) {
								InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
								IBinder iBinder = null;
								View view = mActivity.getCurrentFocus();
								if (null != view) {
									iBinder = view.getWindowToken();
								}
								if (null != imm && null != iBinder) {
									imm.hideSoftInputFromWindow(iBinder, InputMethodManager.HIDE_NOT_ALWAYS);
								}
							}
						}
					}
				});
				libsTv = (TextView) libraryView.findViewById(R.id.tv_libs);
				TextView formatTv = (TextView) libraryView.findViewById(R.id.tv_format);
				formatTv.setTextSize(1.5F * ValueUtil.getInstance().getTextSize());
				queryBtn.setTextSize(ValueUtil.getInstance().getTextSize());
				addBtn.setTextSize(ValueUtil.getInstance().getTextSize());
				queryEt.setTextSize(ValueUtil.getInstance().getTextSize());
				libsTv.setTextSize(ValueUtil.getInstance().getTextSize());

				addBtn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						personId = -1;
						queryEt.getText().clear();
						addBtn.requestFocus();
						mFaceLibAdapter.setCheckBoxShowing(false);
						mFaceLibAdapter.notifyDataSetChanged();
						allDelBtn.setVisibility(View.GONE);
						delBtn.setText(mResources.getString(R.string.all_select));
						Intent intent = new Intent(mActivity, FileListActivity.class);
						startActivityForResult(intent, BTN_ADD);
					}
				});

				libLv = (ListView) libraryView.findViewById(R.id.lv_lib);
				libLv.setVisibility(View.GONE);
				headerView = LayoutInflater.from(mActivity).inflate(R.layout.item_face_lib_header, null);
//				headerView.setVisibility(View.GONE);
				TextView userIdTv = (TextView) headerView.findViewById(R.id.tv_user_id);
				TextView idTv = (TextView) headerView.findViewById(R.id.tv_id);
				TextView nameTv = (TextView) headerView.findViewById(R.id.tv_name);
				TextView pathTv = (TextView) headerView.findViewById(R.id.tv_path);
				delBtn = (Button) headerView.findViewById(R.id.btn_del);
				allDelBtn = (Button) headerView.findViewById(R.id.btn_all_del);

				userIdTv.setTextSize(ValueUtil.getInstance().getTextSize());
				idTv.setTextSize(ValueUtil.getInstance().getTextSize());
				nameTv.setTextSize(ValueUtil.getInstance().getTextSize());
				pathTv.setTextSize(ValueUtil.getInstance().getTextSize());
				delBtn.setTextSize(ValueUtil.getInstance().getTextSize());
				allDelBtn.setTextSize(ValueUtil.getInstance().getTextSize());

				delBtn.setText(mResources.getString(R.string.all_select));
				allDelBtn.setVisibility(View.GONE);
				delBtn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						if (isLvInited && null != mFaceLibAdapter) {
							String delTxt = "";
							String allDelTxt = "";
							if (mFaceLibAdapter.isCheckBoxShowing()) {
								if (mFaceLibAdapter.isAllSelected()) {
									delTxt = mResources.getString(R.string.all_select);
									allDelTxt = mResources.getString(R.string.tv_back);
									mFaceLibAdapter.setAllNotChecked();
								} else {
									delTxt = mResources.getString(R.string.all_not_select);
									allDelTxt = mResources.getString(R.string.btn_del);
									mFaceLibAdapter.setAllChecked();
								}
							} else {
								delTxt = mResources.getString(R.string.all_not_select);
								allDelTxt = mResources.getString(R.string.btn_del);
								mFaceLibAdapter.setAllChecked();
								mFaceLibAdapter.setCheckBoxShowing(true);
							}
							delBtn.setText(delTxt);
							allDelBtn.setText(allDelTxt);
							allDelBtn.setVisibility(View.VISIBLE);
							mFaceLibAdapter.notifyDataSetChanged();
						}
					}
				});
				allDelBtn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						if (isLvInited && null != mFaceLibAdapter) {
							if (mFaceLibAdapter.isCheckBoxShowing()) {
								if (mFaceLibAdapter.isAllSelected()) {
//									mFaceLibAdapter.delChecked();
									libLv.setVisibility(View.GONE);
									mFaceLibAdapter.delAll();
									if (0 < preList.size()) {
										Iterator<PersonFeature> itTmp = ValueUtil.getInstance().getPersonList().iterator();
										while (itTmp.hasNext()) {
											PersonFeature personTmp = (PersonFeature) itTmp.next();
											Iterator<PersonFeature> it = preList.iterator();
											while (it.hasNext()) {
												PersonFeature person = (PersonFeature) it.next();
												if (person.getPersonCommon().getPersonId() == personTmp.getPersonCommon().getPersonId()) {
													it.remove();
													break;
												}
											}
										}
									}
									ValueUtil.getInstance().getPersonList().clear();
								} else {
									if (mFaceLibAdapter.isPartSelected()) {
										mFaceLibAdapter.delChecked();
									} else {
										mFaceLibAdapter.setCheckBoxShowing(false);
										allDelBtn.setVisibility(View.GONE);
									}
								}
								delBtn.setText(mResources.getString(R.string.all_select));
							}
							mFaceLibAdapter.notifyDataSetChanged();
							resetLibsTvData();
						}
					}
				});

				LayoutParams topRlPas = (LayoutParams) topRl.getLayoutParams();
				topRlPas.leftMargin = (int) (0.03F * mDisplayMetrics.widthPixels);
				topRlPas.topMargin = (int) (0.03F * mDisplayMetrics.heightPixels);
				topRlPas.rightMargin = topRlPas.leftMargin;
				topRl.setLayoutParams(topRlPas);

				LayoutParams queryBtnPas = (LayoutParams) queryBtn.getLayoutParams();
				queryBtnPas.width = (int) (0.1F * mDisplayMetrics.widthPixels);
				queryBtnPas.height = (int) (0.06F * mDisplayMetrics.heightPixels);
				queryBtn.setLayoutParams(queryBtnPas);

				LayoutParams queryEtPas = (LayoutParams) queryEt.getLayoutParams();
				queryEtPas.width = (int) (0.2F * mDisplayMetrics.widthPixels);
				queryEtPas.height = (int) (0.06F * mDisplayMetrics.heightPixels);
				queryEtPas.leftMargin = (int) (0.02F * mDisplayMetrics.widthPixels);
				queryEtPas.rightMargin = queryEtPas.leftMargin;
				queryEt.setLayoutParams(queryEtPas);

				LayoutParams addBtnPas = (LayoutParams) addBtn.getLayoutParams();
				addBtnPas.width = queryBtnPas.width;
				addBtnPas.height = queryBtnPas.height;
				addBtnPas.rightMargin = queryEtPas.leftMargin;
				addBtn.setLayoutParams(addBtnPas);

				LayoutParams libLvRlPas = (LayoutParams) libLv.getLayoutParams();
				libLvRlPas.leftMargin = topRlPas.leftMargin;
				libLvRlPas.topMargin = topRlPas.topMargin;
				libLvRlPas.rightMargin = topRlPas.leftMargin;
				libLv.setLayoutParams(libLvRlPas);

//				showPopupWindow();

				if (!isLvInited) {
					isLvInited = true;
					initListview();
					LogUtil.getInstance().i(TAG, "122 initListview()");
				}
				mView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
			}
		});
	}

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LogUtil.getInstance().i(TAG, "122 onCreateView()");
		// TODO Auto-generated method stub
//		return mView;
		return NoSaveStateFrameLayout.wrap(mView);
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		LogUtil.getInstance().i(TAG, "122 setUserVisibleHint()");
		super.setUserVisibleHint(isVisibleToUser);
//		if (isVisibleToUser && isLvInited) {
//			if (StringUtil.isNotNull(preTxt)) {
//				libsTv.setText(preTxt);
//			} else {
//				resetLibsTvData();
//			}
//			mFaceLibAdapter.resetData();
//		} else if (null != libsTv) {
////			preTxt = libsTv.getText().toString();
//		}
		if (null != libLv) {
			if (isVisibleToUser) {
//				libLv.setVisibility(View.VISIBLE);
				if (null != mHandler) {
					mHandler.sendEmptyMessageDelayed(LV_SHOW, 1500);
				}
			} else {
				mHandler.removeMessages(LV_SHOW);
				libLv.setVisibility(View.GONE);
				libsTv.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public void onPause() {
		LogUtil.getInstance().i(TAG, "122 onPause()");
		super.onPause();
		// 按返回键返回桌面
//		mActivity.moveTaskToBack(true);//再次打开程序能提高加载速度
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	public void onBackPressed() {
		if (0 < preList.size()) {
			ValueUtil.getInstance().getPersonList().clear();
			ValueUtil.getInstance().getPersonList().addAll(preList);
			preList.clear();
			preCondition = "";
			mHandler.obtainMessage(CLEAR_EDIT).sendToTarget();
		}
		if (null != queryEt) {
			queryEt.getText().clear();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {// 是否选择，没选择就不会继续
			if (null != data) {
				String path = data.getStringExtra("selectedPath");
				if (StringUtil.isNotNull(path)) {
					mFeatureUtil.extractFeatureByPath(path, personId);
					successNum = 0;
					totalNum = 0;
					failNum = 0;
					failList.clear();
				}
			}
		}
	}

	@SuppressLint("NewApi")
	private void initListview() {
		// TODO Auto-generated method stub
		mFaceLibAdapter = new FaceLibAdapter(mActivity, mFeatureUtil, mFaceLibAdapterCallback);
		libLv.setSelector(R.drawable.system_lv_item_bg);
		libLv.setAdapter(mFaceLibAdapter);
		libLv.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				mFaceLibAdapter.setSelection(arg2 - 1);
				String delTxt = "";
				String allDelTxt = "";
				if (mFaceLibAdapter.isAllNotSelected()) {
					delTxt = mResources.getString(R.string.all_select);
					allDelTxt = mResources.getString(R.string.tv_back);
				} else if (mFaceLibAdapter.isAllSelected()) {
					delTxt = mResources.getString(R.string.all_not_select);
					allDelTxt = mResources.getString(R.string.btn_del);
				} else {
					delTxt = mResources.getString(R.string.all_select);
					allDelTxt = mResources.getString(R.string.btn_del);
				}
				delBtn.setText(delTxt);
				allDelBtn.setText(allDelTxt);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		libLv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				mFaceLibAdapter.setSelection(arg2 - 1);
				String delTxt = "";
				String allDelTxt = "";
				if (mFaceLibAdapter.isAllNotSelected()) {
					delTxt = mResources.getString(R.string.all_select);
					allDelTxt = mResources.getString(R.string.tv_back);
				} else if (mFaceLibAdapter.isAllSelected()) {
					delTxt = mResources.getString(R.string.all_not_select);
					allDelTxt = mResources.getString(R.string.btn_del);
				} else {
					delTxt = mResources.getString(R.string.all_select);
					allDelTxt = mResources.getString(R.string.btn_del);
				}
				delBtn.setText(delTxt);
				allDelBtn.setText(allDelTxt);
			}
		});
		libLv.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				personId = mFaceLibAdapter.getItem(arg2).getPersonCommon().getPersonId();
				Intent intent = new Intent(mActivity, FileListActivity.class);
				startActivityForResult(intent, ITEM_ADD);
				return false;
			}
		});
		if (0 < mFaceLibAdapter.getCount()) {
			libLv.addHeaderView(headerView);
		}
//		libLv.setVisibility(View.VISIBLE);
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case ConstantValues.ID_HINT:
			Toast.makeText(mActivity, (String) msg.obj, Toast.LENGTH_SHORT).show();
			break;
		case ConstantValues.ID_FEATURE_SUCCESS:
			Bundle bundle = msg.getData();
			if (null != bundle) {
				int num = bundle.getInt("num");
				if (0 < num) {
//					ArrayList<LibItemParam> list = bundle.getParcelableArrayList("list");
//					ValueUtil.getInstance().getFaceLibList().addAll(list);
					mFaceLibAdapter.setCheckBoxShowing(false);
					mFaceLibAdapter.resetData();
					if (null != libLv && View.GONE == libLv.getVisibility()) {
						libLv.setVisibility(View.VISIBLE);
					}
					if (0 == libLv.getHeaderViewsCount()) {
						libLv.addHeaderView(headerView);
					}
					Toast.makeText(mActivity, mResources.getString(R.string.add_success) + num + mResources.getString(R.string.add_success_image), Toast.LENGTH_SHORT).show();
				}
			}
			resetLibsTvData();
			if (0 < totalNum) {
				delayDismissPopupWindow();
			}
			break;
		case ConstantValues.ID_FEATURE_FAIL:
			if (null != msg.obj) {
				failList.add((String) msg.obj);
				String txt = mResources.getString(R.string.add_fail_image) + msg.obj;
				failTv.setText(txt);
			}
			break;
		case ConstantValues.ID_SET_DATA:
			successNum = msg.arg1;
			failNum = msg.arg2;
			if (null != mPopupWindow && mPopupWindow.isShowing() && totalNum > 0) {
				mProgressBar.setProgress(100 * successNum / totalNum);
				progressTv.setText(successNum + "/" + totalNum + mResources.getString(R.string.lib_image));
			}
			if (totalNum == failNum) {
				delayDismissPopupWindow();
			}
			break;
		case ConstantValues.ID_SET_TOTAL:
			totalNum = msg.arg1;
			if (!mActivity.isFinishing() && (null == mPopupWindow || !mPopupWindow.isShowing()) && totalNum > 0) {
				showPopupWindow();
			}
			break;
		case LV_SHOW:
			if (LibraryFragment.this.getUserVisibleHint()) {
				libLv.setVisibility(View.VISIBLE);
				if (0 == libLv.getHeaderViewsCount()) {
					libLv.addHeaderView(headerView);
				}
				resetLibsTvData();
			}
			break;
		case CLEAR_EDIT:
			mFaceLibAdapter.setQuerying(false);
			mFaceLibAdapter.setCheckBoxShowing(false);
			mFaceLibAdapter.resetData();
			libLv.setVisibility(View.VISIBLE);
			resetLibsTvData();
			break;
		default:
			break;
		}
		return false;
	}

	private void delayDismissPopupWindow() {
		if (totalNum == successNum + failNum) {
			if (0 < failNum) {
				progressRl.setVisibility(View.GONE);
				lvRl.setVisibility(View.VISIBLE);
				int size = failList.size();
				if (size > 0) {
					FailAdapter adapter = new FailAdapter(mActivity, failList);
					failLv.setAdapter(adapter);
				}
			} else {
				mHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						if (null != mPopupWindow && mPopupWindow.isShowing()) {
							mPopupWindow.dismiss();
						}
						if (0 == totalNum) {
							Toast.makeText(mActivity, mResources.getString(R.string.face_no_files), Toast.LENGTH_SHORT).show();
						}
					}
				}, 2000);
			}
		}
	}

	@SuppressLint("NewApi")
	private void showPopupWindow() {
		// TODO Auto-generated method stub
		final View view = LayoutInflater.from(mActivity).inflate(R.layout.popupwindow_lib, null);
		progressRl = (RelativeLayout) view.findViewById(R.id.rl_progress);
		lvRl = (RelativeLayout) view.findViewById(R.id.rl_lv);
		progressRl.setVisibility(View.VISIBLE);
		lvRl.setVisibility(View.GONE);
		TextView titleTv = (TextView) view.findViewById(R.id.tv_title_dialog);
		progressTv = (TextView) view.findViewById(R.id.tv_progress_dialog);
		failTv = (TextView) view.findViewById(R.id.tv_fail_dialog);
		mProgressBar = (ProgressBar) view.findViewById(R.id.pb_lib);

		TextView titleFailTv = (TextView) view.findViewById(R.id.tv_title_fail);
		failLv = (ListView) view.findViewById(R.id.lv_fail);
		Button okBtn = (Button) view.findViewById(R.id.btn_ok);

		titleTv.setTextSize(2F * ValueUtil.getInstance().getTextSize());
		progressTv.setTextSize(ValueUtil.getInstance().getTextSize());
		failTv.setTextSize(ValueUtil.getInstance().getTextSize());
		okBtn.setTextSize(1.5F * ValueUtil.getInstance().getTextSize());
		titleFailTv.setTextSize(2F * ValueUtil.getInstance().getTextSize());

//		progressTv.setText("10/1000底库图片");
//		failTv.setText("入库失败图片：VID_20170721_093958.jpg");
		okBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mPopupWindow.dismiss();
			}
		});

		LayoutParams pbLibPas = (LayoutParams) mProgressBar.getLayoutParams();
		pbLibPas.width = (int) (0.9F * mDisplayMetrics.widthPixels);
		pbLibPas.height = (int) (0.03F * mDisplayMetrics.heightPixels);
		pbLibPas.topMargin = pbLibPas.height;
		pbLibPas.bottomMargin = pbLibPas.height;
		mProgressBar.setLayoutParams(pbLibPas);

		LayoutParams failLvPas = (LayoutParams) failLv.getLayoutParams();
		failLvPas.width = (int) (0.9F * mDisplayMetrics.widthPixels);
		failLvPas.height = (int) (0.5F * mDisplayMetrics.heightPixels);
		failLvPas.topMargin = pbLibPas.height;
		failLvPas.bottomMargin = pbLibPas.height;
		failLv.setLayoutParams(failLvPas);

		LayoutParams okBtnPas = (LayoutParams) okBtn.getLayoutParams();
		okBtnPas.width = (int) (0.2F * mDisplayMetrics.widthPixels);
		okBtnPas.height = (int) (0.06F * mDisplayMetrics.heightPixels);
		okBtn.setLayoutParams(okBtnPas);

		mPopupWindow = new PopupWindow(view, (int) (0.9F * mDisplayMetrics.widthPixels), (int) (0.9F * mDisplayMetrics.heightPixels), true);
		mPopupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				mPopupWindow = null;
			}
		});

//		ColorDrawable dw = new ColorDrawable(Color.TRANSPARENT);
//		mPopupWindow.setBackgroundDrawable(dw);
		mPopupWindow.setFocusable(false);
		mPopupWindow.setTouchable(true);
		mPopupWindow.setOutsideTouchable(false);
//		mPopupWindow.update();
		mPopupWindow.showAtLocation(mView, Gravity.CENTER, 0, 0);
	}

	private void resetLibsTvData() {
		String retTxt = "";
		int size = ValueUtil.getInstance().getPersonList().size();
		if (0 < size) {
			retTxt = ValueUtil.getInstance().getPersonList().size() + mResources.getString(R.string.unit_face);
			if (mFaceLibAdapter.isQuerying() && StringUtil.isNotNull(preCondition)) {
				retTxt = ValueUtil.getInstance().getPersonList().size() + "/" + preList.size() + mResources.getString(R.string.unit_face);
			}
		}
		libsTv.setVisibility(View.VISIBLE);
		libsTv.setText(retTxt);
	}

//	private int getCountByList(List<Person> totalList) {
//		int count = 0;
//		for (int i = 0; i < totalList.size(); i++) {
//			List<Feature> featureList = totalList.get(i).getFeatureList();
//			if (null != featureList && 0 < featureList.size()) {
//				for (int j = 0; j < featureList.size(); j++) {
//					count++;
//				}
//			}
//		}
//		return count;
//	}

	public interface FacePopupWindowDismissListener {
		public boolean isPopupWindowShowing();
	}
}
