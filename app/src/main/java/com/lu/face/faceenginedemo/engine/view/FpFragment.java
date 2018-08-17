package com.lu.face.faceenginedemo.engine.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.hisign.AS60xSDK.AS60xIO;
import com.hisign.AS60xSDK.SDKUtilty;
import com.hisign.idCardClient.FPModuleClient;
import com.lu.face.faceenginedemo.R;
import com.lu.face.faceenginedemo.engine.ConfigureActivity;
import com.lu.face.faceenginedemo.engine.FileListActivity;
import com.lu.face.faceenginedemo.engine.adapter.FailAdapter;
import com.lu.face.faceenginedemo.engine.adapter.FpAdapter;
import com.lu.face.faceenginedemo.engine.application.MainApplication;
import com.lu.face.faceenginedemo.engine.data.ConstantValues;
import com.lu.face.faceenginedemo.engine.models.FingerPrint;
import com.lu.face.faceenginedemo.engine.utils.FpFeatureUtil;
import com.lu.face.faceenginedemo.engine.utils.LogUtil;
import com.lu.face.faceenginedemo.engine.utils.SharedPreferencesUtil;
import com.lu.face.faceenginedemo.enginesdk.utils.FileUtil;
import com.lu.face.faceenginedemo.enginesdk.utils.StringUtil;
import com.lu.face.faceenginedemo.enginesdk.utils.ValueUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class FpFragment extends Fragment implements Callback {
	private final String TAG = "FingerPrintFragment";
	private final String PATH = Environment.getExternalStorageDirectory() + File.separator + "HisignEngine" + File.separator + "FingerPrint";
	private final int BTN_ADD = 0;
	private final int ITEM_ADD = 1;
	private final int LV_SHOW = 2;
	private final int FP_COLLECT = 3;
	private final int CLEAR_EDIT = 4;
	private ConfigureActivity mActivity;
	private DisplayMetrics mDisplayMetrics = ValueUtil.getInstance().getmDisplayMetrics();
	private RelativeLayout topRl;
	private ListView libLv;
	private Button delBtn, allDelBtn;
	private FpAdapter mFpAdapter;
	private View headerView;
	private View mView;
	private ViewStub libraryVs;
	private boolean isLvInited;
	private Button queryBtn, batchBtn, collectBtn;
	private EditText queryEt;
	private TextView libsTv;
	private String preCondition;
	private ProgressDialog mProgressDialog;
	private FpFeatureUtil mFpFeatureUtil;
	private Handler mHandler;
	private PopupWindow mPopupWindow;
	private ProgressBar mProgressBar;
	private ListView failLv;
	private List<String> failList = new ArrayList<String>();
	private TextView progressTv, failTv;
	private RelativeLayout progressRl, lvRl;
	private int successNum, totalNum, failNum;
	private int retScore;
	private Bitmap retBp;
	private String fpPath;
	private FPModuleClient mFPModuleClient;
	private Button okBtn;
	private TextView scoreTv, nameTv, cFailTv;
	private ImageView fpIv;
	private boolean isCollecting;
	private Resources mResources;
	private TreeMap<Integer, FingerPrint> preFpMap = new TreeMap<Integer, FingerPrint>(new Comparator<Integer>() {
		public int compare(Integer obj1, Integer obj2) {
			return obj2.compareTo(obj1);// 降序排序
		}
	});
	private FpAdapter.FingerPrintAdapterCallback mFingerPrintAdapterCallback = new FpAdapter.FingerPrintAdapterCallback() {

		@Override
		public void onListChanged(FingerPrint fp) {
			// TODO Auto-generated method stub
			preFpMap.remove(fp.getId());
		}

		@Override
		public void onListUpdate() {
			// TODO Auto-generated method stub
			if (0 == mFpAdapter.getCount()) {
				allDelBtn.setVisibility(View.GONE);
				ValueUtil.getInstance().getPersonList().clear();
			}
			resetLibsTvData();
		}

		@Override
		public void onNameClick(int position) {
			// TODO Auto-generated method stub

		}
	};
	Runnable collectRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			mHandler.obtainMessage(FP_COLLECT, startReadFingerPrint(5)).sendToTarget();
		}
	};

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.getInstance().i(TAG, "122 onCreate()");
		mActivity = (ConfigureActivity) this.getActivity();
		mResources = mActivity.getResources();
		MainApplication mApp = (MainApplication) mActivity.getApplication();
		mFPModuleClient = mApp.getmFPModuleClient();
		mHandler = new Handler(this);
		mFpFeatureUtil = new FpFeatureUtil(mActivity, mHandler);
		mActivity.setmFpPopupWindowDismissListener(new FpPopupWindowDismissListener() {

			@Override
			public boolean isPopupWindowShowing() {
				// TODO Auto-generated method stub
				return null != mPopupWindow && mPopupWindow.isShowing();
			}
		});
//		initProgressDialog(mActivity);
		mDisplayMetrics = mActivity.getResources().getDisplayMetrics();
		mView = LayoutInflater.from(mActivity).inflate(R.layout.fragment_fp, null);
		libraryVs = (ViewStub) mView.findViewById(R.id.vs_fingerprint);
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
						} else if (!StringUtil.isEqual(txt, preCondition) && (0 < ValueUtil.getInstance().getFpMap().size() || 0 < preFpMap.size())) {
							if (0 == preFpMap.size()) {
								preFpMap.putAll((Map<? extends Integer, ? extends FingerPrint>) ValueUtil.getInstance().getFpMap().clone());
							}
							preCondition = txt;
							TreeMap<Integer, FingerPrint> fpMapTmp = new TreeMap<Integer, FingerPrint>(new Comparator<Integer>() {
								public int compare(Integer obj1, Integer obj2) {
									return obj2.compareTo(obj1);// 降序排序
								}
							});
							for (FingerPrint fp : preFpMap.values()) {
								if (fp.getName().contains(txt)) {
									fpMapTmp.put(fp.getId(), fp);
								}
							}
							ValueUtil.getInstance().getFpMap().clear();
							ValueUtil.getInstance().getFpMap().putAll(fpMapTmp);

							mFpAdapter.setQuerying(true);
							mFpAdapter.setCheckBoxShowing(false);
							mFpAdapter.resetData();
							delBtn.setText(mResources.getString(R.string.all_select));
							allDelBtn.setVisibility(View.GONE);
							resetLibsTvData();
						}
					}
				});
				batchBtn = (Button) libraryView.findViewById(R.id.btn_batch_add);
				collectBtn = (Button) libraryView.findViewById(R.id.btn_collect_add);
				queryEt = (EditText) libraryView.findViewById(R.id.et_query);
				queryEt.addTextChangedListener(new TextWatcher() {

					@Override
					public void afterTextChanged(Editable arg0) {
						// TODO Auto-generated method stub
						if (0 == arg0.toString().length() && 0 < preFpMap.size()) {
							new Thread(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									ValueUtil.getInstance().getFpMap().clear();
									ValueUtil.getInstance().getFpMap().putAll((Map<? extends Integer, ? extends FingerPrint>) preFpMap.clone());
									preFpMap.clear();
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
				batchBtn.setTextSize(ValueUtil.getInstance().getTextSize());
				collectBtn.setTextSize(ValueUtil.getInstance().getTextSize());
				queryEt.setTextSize(ValueUtil.getInstance().getTextSize());
				libsTv.setTextSize(ValueUtil.getInstance().getTextSize());

				batchBtn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(mActivity, FileListActivity.class);
						startActivityForResult(intent, BTN_ADD);
						queryEt.getText().clear();
						batchBtn.requestFocus();
						mFpAdapter.setCheckBoxShowing(false);
						mFpAdapter.notifyDataSetChanged();
						allDelBtn.setVisibility(View.GONE);
						delBtn.setText(mResources.getString(R.string.all_select));
					}
				});

				collectBtn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						if (null == mPopupWindow) {
							showCollectPopupWindow();
						}
						queryEt.getText().clear();
						collectBtn.requestFocus();
						mFpAdapter.setCheckBoxShowing(false);
						mFpAdapter.notifyDataSetChanged();
						allDelBtn.setVisibility(View.GONE);
						delBtn.setText(mResources.getString(R.string.all_select));
						if (mFpFeatureUtil.isAdding()) {
							Toast.makeText(getActivity(), mResources.getString(R.string.fp_adding), Toast.LENGTH_SHORT).show();
							return;
						}
//						try {
//							Intent intent = new Intent();
////						intent.setType("*/*");
//							intent.setType("image/*");// 从所有图片中进行选择
//							// 根据版本号不同使用不同的Action
//							if (Build.VERSION.SDK_INT < 19) {
//								intent.setAction(Intent.ACTION_GET_CONTENT);
//							} else {
//								intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
//							}
////						Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
////						intent.setType("*/*");// 设置类型，我这里是任意类型，任意后缀的可以这样写。
//							intent.addCategory(Intent.CATEGORY_OPENABLE);
//							LibraryFragment.this.startActivityForResult(intent, 1);
//							mFaceLibAdapter.resetBtnState();
//						} catch (ActivityNotFoundException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//							Toast.makeText(getActivity(), "请安装文件管理器！", Toast.LENGTH_SHORT).show();
//						}
//						Intent intent = new Intent(mActivity, FileListActivity.class);
//						startActivityForResult(intent, 1);
					}
				});

				libLv = (ListView) libraryView.findViewById(R.id.lv_lib);
				libLv.setVisibility(View.INVISIBLE);

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

				pathTv.setText(mResources.getString(R.string.tv_path_fp));
				delBtn.setText(mResources.getString(R.string.all_select));
				allDelBtn.setVisibility(View.GONE);
				delBtn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						if (isLvInited && null != mFpAdapter) {
							String delTxt = "";
							String allDelTxt = "";
							if (mFpAdapter.isCheckBoxShowing()) {
								if (mFpAdapter.isAllSelected()) {
									delTxt = mResources.getString(R.string.all_select);
									allDelTxt = mResources.getString(R.string.tv_back);
									mFpAdapter.setAllNotChecked();
								} else {
									delTxt = mResources.getString(R.string.all_not_select);
									allDelTxt = mResources.getString(R.string.btn_del);
									mFpAdapter.setAllChecked();
								}
							} else {
								delTxt = mResources.getString(R.string.all_not_select);
								allDelTxt = mResources.getString(R.string.btn_del);
								mFpAdapter.setAllChecked();
								mFpAdapter.setCheckBoxShowing(true);
							}
							delBtn.setText(delTxt);
							allDelBtn.setText(allDelTxt);
							allDelBtn.setVisibility(View.VISIBLE);
							mFpAdapter.notifyDataSetChanged();
						}
					}
				});
				allDelBtn.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						if (isLvInited && null != mFpAdapter) {
							if (mFpAdapter.isCheckBoxShowing()) {
								if (mFpAdapter.isAllSelected()) {
									libLv.setVisibility(View.GONE);
									allDelBtn.setVisibility(View.GONE);
									if (0 < preFpMap.size()) {
										for (FingerPrint fp : ValueUtil.getInstance().getFpMap().values()) {
											preFpMap.remove(fp.getId());
										}
										mFpAdapter.delChecked();
									} else {
										mFpAdapter.delAll();
									}
								} else {
									if (mFpAdapter.isPartSelected()) {
										mFpAdapter.delChecked();
									} else {
										mFpAdapter.setCheckBoxShowing(false);
										allDelBtn.setVisibility(View.GONE);
									}
								}
								delBtn.setText(mResources.getString(R.string.all_select));
							}
							mFpAdapter.notifyDataSetChanged();
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

				LayoutParams batchBtnPas = (LayoutParams) batchBtn.getLayoutParams();
				batchBtnPas.width = queryBtnPas.width;
				batchBtnPas.height = queryBtnPas.height;
				batchBtnPas.rightMargin = queryEtPas.leftMargin;
				batchBtn.setLayoutParams(batchBtnPas);

				LayoutParams collectBtnPas = (LayoutParams) collectBtn.getLayoutParams();
				collectBtnPas.width = queryBtnPas.width;
				collectBtnPas.height = queryBtnPas.height;
				collectBtnPas.rightMargin = queryEtPas.leftMargin;
				collectBtn.setLayoutParams(collectBtnPas);

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

	public void initProgressDialog(Context context) {
		mProgressDialog = new ProgressDialog(context);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setCancelable(false);
		mProgressDialog.setMessage(mResources.getString(R.string.msg_processing));
		mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				if (null != mProgressDialog && mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
					mProgressDialog = null;
				}
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
//			}
//			mFpAdapter.notifyDataSetChanged();
//		} else if (null != libsTv) {
//			preTxt = libsTv.getText().toString();
//		}
		if (null != libLv) {
			if (isVisibleToUser) {
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
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	public void onBackPressed() {
		if (0 < preFpMap.size()) {
			ValueUtil.getInstance().getFpMap().clear();
			ValueUtil.getInstance().getFpMap().putAll((Map<? extends Integer, ? extends FingerPrint>) preFpMap.clone());
			preFpMap.clear();
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
			failList.clear();
			if (null != data) {
				String path = data.getStringExtra("selectedPath");
				if (StringUtil.isNotNull(path)) {
					mFpFeatureUtil.extractFeatureByPath(path);
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
//		for (int i = 0; i < 1; i++) {
//			FaceLibParam flParam = new FaceLibParam("编号", "人脸名称", "人脸图片", false);
//			faceLibList.add(flParam);
//		}
		mFpAdapter = new FpAdapter(mActivity, mFpFeatureUtil, mFingerPrintAdapterCallback);
		libLv.setSelector(R.drawable.system_lv_item_bg);
		libLv.setAdapter(mFpAdapter);
		libLv.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				mFpAdapter.setSelection(arg2 - 1);
				String delTxt = "";
				String allDelTxt = "";
				if (mFpAdapter.isAllNotSelected()) {
					delTxt = mResources.getString(R.string.all_select);
					allDelTxt = mResources.getString(R.string.tv_back);
				} else if (mFpAdapter.isAllSelected()) {
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
//		mFaceLibAdapter.setSelection(9);
		libLv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				mFpAdapter.setSelection(arg2 - 1);
				String delTxt = "";
				String allDelTxt = "";
				if (mFpAdapter.isAllNotSelected()) {
					delTxt = mResources.getString(R.string.all_select);
					allDelTxt = mResources.getString(R.string.tv_back);
				} else if (mFpAdapter.isAllSelected()) {
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
		if (0 < mFpAdapter.getCount()) {
			libLv.addHeaderView(headerView);
		}
//		new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				mFeatureUtil.rescanFeature(null);
//			}
//		}).start();
//		mProgressDialog.show();
//		resetData();
//		libLv.setVisibility(View.VISIBLE);
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case ConstantValues.ID_HINT:
			Toast.makeText(mActivity, (String) msg.obj, Toast.LENGTH_SHORT).show();
			break;
		case ConstantValues.ID_FP_FEATURE_SUCCESS:
			mFpAdapter.setCheckBoxShowing(false);
			mFpAdapter.resetData();
			if (null != libLv && View.GONE == libLv.getVisibility()) {
				libLv.setVisibility(View.VISIBLE);
			}
			if (0 == libLv.getHeaderViewsCount()) {
				libLv.addHeaderView(headerView);
			}
//				Toast.makeText(mActivity, "成功入库" + msg.arg1 + "张图片！", Toast.LENGTH_SHORT).show();
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
			if (FpFragment.this.getUserVisibleHint()) {
				libLv.setVisibility(View.VISIBLE);
				resetLibsTvData();
				if (0 == libLv.getHeaderViewsCount()) {
					libLv.addHeaderView(headerView);
				}
			}
			break;
		case FP_COLLECT:
			retBp = startReadFingerPrint(5);
			if (null != retBp && !retBp.isRecycled()) {
				scoreTv.setVisibility(View.VISIBLE);
				nameTv.setVisibility(View.VISIBLE);
				cFailTv.setVisibility(View.GONE);
				fpIv.setImageBitmap(scaleBitmap(retBp, 0.5F));
				setTv(scoreTv, String.valueOf(retScore));
				String timeStamp = (String) new DateFormat().format("yyyyMMddHHmmss", Calendar.getInstance(Locale.CHINA));
				setTv(nameTv, timeStamp);
				fpPath = PATH + File.separator + timeStamp + ".bmp";
				okBtn.setText(mResources.getString(R.string.btn_ok_collect));
			} else {
				scoreTv.setVisibility(View.GONE);
				nameTv.setVisibility(View.GONE);
				cFailTv.setVisibility(View.VISIBLE);
				fpIv.setImageResource(R.drawable.main_iv_bg);
				cFailTv.setText(mResources.getString(R.string.tv_fp_fail));
			}
			isCollecting = false;
			break;
		case CLEAR_EDIT:
			mFpAdapter.setQuerying(false);
			mFpAdapter.setCheckBoxShowing(false);
			mFpAdapter.resetData();
			libLv.setVisibility(View.VISIBLE);
			resetLibsTvData();
			break;
		default:
			break;
		}
//		mProgressDialog.dismiss();
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
							Toast.makeText(mActivity, mResources.getString(R.string.fp_no_files), Toast.LENGTH_SHORT).show();
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
		titleTv.setText(mResources.getString(R.string.fp_add));
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
				failList.clear();
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

	@SuppressLint("NewApi")
	private void showCollectPopupWindow() {
		// TODO Auto-generated method stub
		final View view = LayoutInflater.from(mActivity).inflate(R.layout.popupwindow_collect, null);
		TextView titleTv = (TextView) view.findViewById(R.id.tv_collect_title);
		fpIv = (ImageView) view.findViewById(R.id.iv_fp);
		scoreTv = (TextView) view.findViewById(R.id.tv_fp_score);
		nameTv = (TextView) view.findViewById(R.id.tv_fp_name);
		cFailTv = (TextView) view.findViewById(R.id.tv_fp_fail);
		LinearLayout btnLl = (LinearLayout) view.findViewById(R.id.ll_btn);
		okBtn = (Button) view.findViewById(R.id.btn_ok);
		Button cancelBtn = (Button) view.findViewById(R.id.btn_cancel);

		titleTv.setTextSize(2F * ValueUtil.getInstance().getTextSize());
		scoreTv.setTextSize(ValueUtil.getInstance().getTextSize());
		nameTv.setTextSize(ValueUtil.getInstance().getTextSize());
		cFailTv.setTextSize(1.5F * ValueUtil.getInstance().getTextSize());
		okBtn.setTextSize(1.5F * ValueUtil.getInstance().getTextSize());
		cancelBtn.setTextSize(1.5F * ValueUtil.getInstance().getTextSize());

		scoreTv.setVisibility(View.GONE);
		nameTv.setVisibility(View.GONE);
		cFailTv.setVisibility(View.VISIBLE);
		okBtn.setText(mResources.getString(R.string.fp_collect_start));
		cFailTv.setText(mResources.getString(R.string.fp_hint));

		okBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				scoreTv.setVisibility(View.GONE);
				nameTv.setVisibility(View.GONE);
//				cFailTv.setVisibility(View.GONE);
				if (StringUtil.isEqual(okBtn.getText().toString(), mResources.getString(R.string.fp_collect_start))) {
					if (!isCollecting) {
						isCollecting = true;
//					cFailTv.setVisibility(View.GONE);
						new Thread(collectRunnable).start();
					}
					/*retBp = startReadFingerPrint(5);
					if (null != retBp && !retBp.isRecycled()) {
						okBtn.setEnabled(true);
						scoreTv.setVisibility(View.VISIBLE);
						nameTv.setVisibility(View.VISIBLE);
						fpIv.setImageBitmap(scaleBitmap(retBp, 0.5F));
						setTv(scoreTv, String.valueOf(retScore));
						String timeStamp = (String) new DateFormat().format("yyyyMMddHHmmss", Calendar.getInstance(Locale.CHINA));
						setTv(nameTv, timeStamp);
						fpPath = PATH + File.separator + timeStamp + ".bmp";
						okBtn.setText("入库");
					} else {
						scoreTv.setVisibility(View.GONE);
						nameTv.setVisibility(View.GONE);
						failTv.setVisibility(View.VISIBLE);
						fpIv.setImageResource(R.drawable.main_iv_bg);
					}*/
				} else {
					if (null != retBp && !retBp.isRecycled()) {
						byte[] fpImageRaw = FileUtil.getInstance().bitmap2BytesNew(retBp);
						if (null != fpImageRaw && 0 < fpImageRaw.length) {
							FileUtil.getInstance().mkDir(ValueUtil.getInstance().getFP_PATH());
							SDKUtilty.SaveRawToBmp(fpImageRaw, fpPath, true);
							mFpFeatureUtil.extractFeatureByPath(fpPath);
						}
					}
					mPopupWindow.dismiss();
				}
			}
		});

		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				isCollecting = false;
				retBp = null;
				mPopupWindow.dismiss();
			}
		});

		LayoutParams fpIvPas = (LayoutParams) fpIv.getLayoutParams();
		fpIvPas.width = (int) (0.1F * mDisplayMetrics.widthPixels);
		fpIvPas.height = (int) (1.413F * fpIvPas.width);
		fpIvPas.topMargin = (int) (0.03F * mDisplayMetrics.heightPixels);
		fpIvPas.bottomMargin = (int) (0.03F * mDisplayMetrics.heightPixels);
		fpIv.setLayoutParams(fpIvPas);

		LayoutParams btnLlPas = (LayoutParams) btnLl.getLayoutParams();
		btnLlPas.width = (int) (0.4F * mDisplayMetrics.widthPixels);
//		btnLlPas.height = (int) (0.1F * mDisplayMetrics.heightPixels);
		btnLlPas.topMargin = (int) (0.03F * mDisplayMetrics.heightPixels);
		btnLl.setLayoutParams(btnLlPas);

		mPopupWindow = new PopupWindow(view, (int) (0.75F * mDisplayMetrics.widthPixels), (int) (0.75F * mDisplayMetrics.heightPixels), true);
		mPopupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				mPopupWindow = null;
				failList.clear();
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
		int size = ValueUtil.getInstance().getFpMap().size();
		if (0 < size) {
			retTxt = size + mResources.getString(R.string.unit_fp);
			if (mFpAdapter.isQuerying() && StringUtil.isNotNull(preCondition)) {
				retTxt = size + "/" + preFpMap.size() + mResources.getString(R.string.unit_fp);
			}
		}
		libsTv.setVisibility(View.VISIBLE);
		libsTv.setText(retTxt);
	}

	/**
	 *  开始循环读取指纹，将指纹图像存入模块中的图像缓冲区
	 *   timeoutSeconds 超时秒数
	 * 	@return 返回指纹bitmap图像
	 */
	public Bitmap startReadFingerPrint(int timeout) {
		return fingerCollection(timeout);
//		fpImageRaw = fingerCollection(timeout);
//		Bitmap bitmap = null;
//		if (null != fpImageRaw) {
//			ByteBuffer buffer = ByteBuffer.wrap(gray2ARGB(fpImageRaw));
//			bitmap = Bitmap.createBitmap(256, 360, Config.ARGB_8888);
//			bitmap.copyPixelsFromBuffer(buffer);
//		}
//		return bitmap;
	}

	/**
	 * 指纹采集 返回指纹数据
	 * @param timeout  采集超时时间  秒
	 * @return
	 */
	public Bitmap fingerCollection(int timeout) {
		Bitmap retBp = null;
		int nRet = -1;
		int len = 256 * 360;
		byte[] FPImageRaw = new byte[len];
		long preTime = System.currentTimeMillis();
		int[] imageLen = new int[2];
		boolean isStoped = false;
		while (!isStoped) {
			if ((System.currentTimeMillis() - preTime) > timeout * 1000) {
				isStoped = true;
				break;
			}
			retBp = mFPModuleClient.FCV_CollectionFpImage();
			if (null != retBp && !retBp.isRecycled()) {
//				SDKUtilty.SaveRawToBmp(FileUtil.getInstance().bitmap2BytesNew(retBp), "/sdcard/fp_tmp.bmp", true);
//				SDKUtilty.ReadBmpToRaw(FPImageRaw, "/sdcard/fp_tmp.bmp", true);
				FPImageRaw = FileUtil.getInstance().bitmap2BytesNew(retBp);
				if (null != FPImageRaw) {
					retScore = AS60xIO.FCV_GetQualityScore(FPImageRaw);
					LogUtil.getInstance().i(TAG, "finger quality score:" + retScore);
					if (retScore >= (Integer) SharedPreferencesUtil.get(mActivity, ConstantValues.getFingerQuality(), 0)) {
						isStoped = true;
						break;
					} else {
						retBp = null;
					}
				} else {
					retBp = null;
				}
			} else {
				retBp = null;
			}

//			nRet = AS60xIO.FCV_CollectionFpImage(FPImageRaw, imageLen);
//			if (nRet == 0 && imageLen[0] == len) {
//				retScore = AS60xIO.FCV_GetQualityScore(FPImageRaw);
//				Log.d(TAG, "finger quality score:" + retScore);
//				if (retScore < 50)
//					continue;
//				return FPImageRaw;
//			}
		}
		return retBp;
	}

	/** 灰度填充成彩色*/
	private byte[] gray2ARGB(byte[] grayImage) {
		byte[] argb = new byte[grayImage.length * 4];
		for (int i = 0; i < grayImage.length; i++) {
			argb[4 * i] = argb[4 * i + 1] = argb[4 * i + 2] = grayImage[i];
			argb[4 * i + 3] = (byte) 0xff;
		}
		return argb;
	}

	private void setTv(TextView tv, String txt) {
		// TODO Auto-generated method stub
		if (null != tv && null != txt) {
			String text = (String) tv.getText();
			if (StringUtil.isNotNull(text) && text.contains("：")) {
				tv.setText(text.substring(0, text.indexOf("：") + 1) + txt);
			}
		}
	}

	/**
	 * 按比例缩放图片
	 *
	 * @param origin 原图
	 * @param ratio  比例
	 * @return 新的bitmap
	 */
	private Bitmap scaleBitmap(Bitmap origin, float ratio) {
		if (origin == null) {
			return null;
		}
		int width = origin.getWidth();
		int height = origin.getHeight();
		Matrix matrix = new Matrix();
		matrix.preScale(ratio, ratio);
		Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
		if (newBM.equals(origin)) {
			return newBM;
		}
//		origin.recycle();
		return newBM;
	}

	/**
	 * 裁剪
	 *
	 * @param bitmap 原图
	 * @return 裁剪后的图像
	 */
	private Bitmap cropBitmap(Bitmap bitmap) {
		int w = bitmap.getWidth(); // 得到图片的宽，高
//        int h = bitmap.getHeight();
//        int cropWidth = w >= h ? h : w;// 裁切后所取的正方形区域边长
//        cropWidth /= 2;
//        int cropHeight = (int) (cropWidth / 1.2);
		w /= 2;
		int cropHeight = (int) (w * 1.412F);
		return Bitmap.createBitmap(bitmap, 0, 0, w, cropHeight, null, false);
	}

	public interface FpPopupWindowDismissListener {
		public boolean isPopupWindowShowing();
	}
}
