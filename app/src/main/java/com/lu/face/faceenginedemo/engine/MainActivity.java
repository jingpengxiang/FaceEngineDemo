package com.lu.face.faceenginedemo.engine;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.THID.FaceSDK.THIDFaceRect;
import com.USBCamera.USBCamCtrl;
import com.hisign.AS60xSDK.AS60xIO;
import com.hisign.face.match.engine.model.HSFEChannelParameters;
import com.hisign.face.match.engine.model.HSFEFeatTime;
import com.hisign.face.match.engine.model.HSFEFrame;
import com.hisign.face.match.engine.model.HSFEImageFormat;
import com.hisign.face.match.engine.model.HSFEThreshold;
import com.hisign.face.match.engine.model.HSFEVerifyInputData;
import com.hisign.face.match.engine.model.HSFEVerifyResult;
import com.hisign.face.match.engine.model.HsfeMatchResult;
import com.hisign.face.match.engine.verify.HSFaceEngine;
import com.hisign.face.match.engine.verify.HSProcessFaceCallBack;
import com.hisign.face.match.engine.verify.HSVerifyInfoCallBack;
import com.hisign.face.match.engine.verify.HsfeMatchCallback;
import com.hisign.fpmoduleservice.aidl.IDCardMsg;
import com.hisign.idCardClient.BroadcastCallback;
import com.hisign.idCardClient.FPModuleClient;
import com.hisign.idCardClient.IDCardClient;
import com.lu.face.faceenginedemo.R;
import com.lu.face.faceenginedemo.engine.application.MainApplication;
import com.lu.face.faceenginedemo.engine.data.ConstantValues;
import com.lu.face.faceenginedemo.engine.models.FingerPrint;
import com.lu.face.faceenginedemo.engine.models.FpCollectRet;
import com.lu.face.faceenginedemo.engine.models.FpMatchResult;
import com.lu.face.faceenginedemo.engine.models.HSFaceEngineInit;
import com.lu.face.faceenginedemo.engine.models.PersonFeature;
import com.lu.face.faceenginedemo.engine.utils.DisplayUtil;
import com.lu.face.faceenginedemo.engine.utils.FeatureUtil;
import com.lu.face.faceenginedemo.engine.utils.FpFeatureUtil;
import com.lu.face.faceenginedemo.engine.utils.LogUtil;
import com.lu.face.faceenginedemo.engine.utils.SharedPreferencesUtil;
import com.lu.face.faceenginedemo.engine.utils.SysCameraControl;
import com.lu.face.faceenginedemo.enginesdk.utils.FileUtil;
import com.lu.face.faceenginedemo.enginesdk.utils.StringUtil;
import com.lu.face.faceenginedemo.enginesdk.utils.ValueUtil;
import com.sam.sdticreader.WltDec;
import com.sdt.Sdtapi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

@SuppressLint("NewApi")
public class MainActivity extends Activity implements HSProcessFaceCallBack, Callback, HSVerifyInfoCallBack, HsfeMatchCallback, Handler.Callback {
    protected static final String TAG = "MainActivity";
    public static final String IDC_START = "com.lu.face.faceenginedemo.startCardService";
    private final int TYPE_FINGER_PIC = 0;
    private final int TYPE_FINGER_THRESHOLD = 1;
    private final int TYPE_FINGER_VALUE = 2;
    private final int TYPE_FINGER_RESULT = 3;
    private final int TYPE_FACE_PIC = 4;
    private final int TYPE_FACE_THRESHOLD = 5;
    private final int TYPE_FACE_VALUE = 6;
    private final int TYPE_FACE_RESULT = 7;
    private final int TYPE_CARD_PIC = 8;
    private final int TYPE_CARD_NAME = 9;
    private final int TYPE_CARD_SEX = 10;
    private final int TYPE_CARD_NATION = 11;
    private final int TYPE_CARD_BIRTHDAY = 12;
    private final int TYPE_CARD_ID = 13;
    private final int TYPE_FACE_DETECT = 14;
    private final int TYPE_CARD_READ = 15;
    private final int TYPE_FEATURE_EXTRACT = 16;
    private final int TYPE_VERSION = 17;
    private final int TYPE_FACE_DETECT_TOTAL = 18;
    private final int ID_FP_IMG = 19;
    private final int ID_FP_RESULT = 20;
    private final int TYPE_FP = 21;
    private final int ID_FP_SHOW = 22;
    /**
     * 引擎初始化结果对象
     */
    private HSFaceEngineInit mHSFaceEngineInit;
    /**
     * 核验引擎对象
     */
    private HSFaceEngine mHSFaceEngine;
    private Paint mPaint;
    /**
     * 视频帧编号
     */
    private int frameIndex;
    private Paint clearPaint;

    private DisplayMetrics mDisplayMetrics;
    /**
     * 摄像头管理对象
     */
    private SysCameraControl cameraControl;
    /**
     * 摄像头回调接口
     */
    private SysCameraControl.OnCameraStatusListener cameraListener;
    private SurfaceView previewSfv;
    private SurfaceView rectSfv;
    private LinearLayout ffRl;
    private RelativeLayout previewRl, cardRl, fiIvRl, faIvRl, cardIvRl, setRl, fingerTvRl, faceTvRl;
    private ImageView fingerIv, fingerTipIv, faceIv, faceTipIv, cardIv, cardTipIv, fingerMuskIv, cardMuskIv;
    private TextView fingerThdTv, fingerVerTv, fingerRetTv, faceThdTv, faceVerTv, faceRetTv, nameTv, sexTv, nationTv, birthdayTv, idTv, fpTv, detectTv, detectTotalTv, cardRedTv,
            featureExTv, versionTv, fingerTv, faceTv, cardTv;
    private ProgressDialog mProgressDialog;
    /**
     * 设置弹出框左右滑动x轴坐标参数
     */
    private float startX;
    /**
     * 设置弹出框左右滑动控制参数
     */
    private boolean isToRightable, isToLeftable = true;
    /**
     * 设置弹出框左右滑动隐藏定时器对象
     */
    private Timer mTimer = new Timer();
    /**
     * 摄像头是否初始化参数
     */
    private boolean isCameraInited;
    private SurfaceHolder mSurfaceHolder;
    /**
     * 人脸框绘制控制参数
     */
    private boolean isDrawing;
    private Canvas mCanvas = new Canvas();
    /**
     * isFaceDoing：人脸1：1比对是否进行  isFpDoing：指纹1：1比对是否进行   isFromCard：是否刷身份证
     */
    private boolean isFaceDoing, isFpDoing, isFromCard;
    /**
     * isFpResetUiRunning：指纹比对显示结果倒计时是否进行  isResetUiRunning：人脸比对显示结果倒计时是否进行
     */
    private boolean isFpResetUiRunning, isResetUiRunning, isMatchResultRunning;
    /**
     * 入库弹出框显示控制变量
     */
    private boolean isCameraDialogShowing;
    private long startVerTime, cardTime = 0L;
    private MediaPlayer mMediaPlayer;
    private boolean isFace1V1;
    private Bitmap resultBm;
    private PopupWindow mPopupWindow;
    private String mName, mCardId;

    private Handler mHandler;
    private Resources mResources;
    private FeatureUtil mFeatureUtil;
    private FpFeatureUtil mFpFeatureUtil;

    private IDCardClient mIDCardClient;
    private FPModuleClient mFPModuleClient;

    //    private Common common; // common对象，存储一些需要的参数
    private Sdtapi sdta;
    /**
     * 人脸1：n比对结果是否处理
     */
    private boolean isMatchResultRejected;
    private boolean isDestroyed;
    /**
     * fpFeatures:指纹特征库数组	cardFpFeatures：身份证指纹特征数组
     */
    private byte[][] fpFeatures, cardFpFeatures;
    /**
     * isFpStoped：指纹采集线程是否停止	isFpVerifying：指纹比对控制变量	isFp1V1：指纹比对是否为1：1
     */
    private boolean isFpStoped, isFpVerifying, isFp1V1;
    /**
     * isFrameReverse：人脸框是否左右调整		isFrameOpened：人脸框绘制控制变量		isDetailTimeShow：详细时间显示控制变量	isTotalTimeShow：核验总计时间显示控制变量
     * isFaceEnable：人脸核验控制变量		isFingerEnable：指纹核验控制变量		isAutoAddable：人脸1：1比对通过自动入库控制变量
     */
    private boolean isFrameReverse, isFrameOpened, isDetailTimeShow, isTotalTimeShow, isFaceEnable, isFingerEnable, isAutoAddable;
    /**
     * fingerQuality：指纹质量阈值	fingerThreshold：指纹比对结果阈值		displayOrientation：摄像头旋转角度	checkTimeout：人脸核验超时参数
     */
    private int fingerQuality, displayOrientation, fingerThreshold, checkTimeout;
    @SuppressLint("UseSparseArrays")
    private Map<Integer, FingerPrint> indexMap = new HashMap<Integer, FingerPrint>();

    // 广播接收器
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != intent) {
                String action = intent.getAction();
                switch (action) {
                    case "com.lu.face.faceenginedemo.engine.MainActivity":
                        IdCardMsg msg = new IdCardMsg();
                        msg = (IdCardMsg) intent.getSerializableExtra("cardMsg");
                        isFace1V1 = true;
                        isFp1V1 = false;
                        mHandler.removeMessages(ID_FP_IMG);
                        mHandler.removeMessages(ID_FP_SHOW);
                        mHandler.removeMessages(ID_FP_RESULT);
                        mName = null;
                        mCardId = null;
                        cardFpFeatures = null;
                        isResetUiRunning = false;
                        isCameraDialogShowing = false;
                        startPlay(R.raw.beep);

                        fingerTv.setVisibility(View.VISIBLE);
                        fingerMuskIv.setVisibility(View.GONE);
                        fingerIv.setImageResource(R.drawable.main_iv_bg);
                        updateUi(TYPE_FINGER_VALUE, "");
                        updateUi(TYPE_FINGER_RESULT, "");

                        faceTv.setVisibility(View.VISIBLE);
                        faceIv.setImageResource(R.drawable.main_iv_bg);
                        updateUi(TYPE_FACE_VALUE, "");
                        updateUi(TYPE_FACE_RESULT, "");

                        cardTv.setVisibility(View.GONE);
                        cardIv.setImageResource(R.drawable.main_iv_bg);
                        cardMuskIv.setVisibility(View.GONE);
//                        showIv(msg.imagePath, cardIv, 1);
                        byte[] bm = msg.photo;
                        WltDec dec = new WltDec();
                        byte[] bip = dec.decodeToBitmap(bm);
                        if (bm == null) {
                        } else {
                            Bitmap bp = BitmapFactory.decodeByteArray(bip, 0, bip.length);
                            cardIv.setImageBitmap(bmp(bp));
                        }
                        mName = msg.name;
                        updateUi(TYPE_CARD_NAME, StringUtil.addStars(mName, 1, mName.length()));
                        updateUi(TYPE_CARD_SEX, msg.sex);
                        updateUi(TYPE_CARD_NATION, msg.nation_str);
                        String id = msg.id_num;
                        mCardId = id;
                        updateUi(TYPE_CARD_ID, StringUtil.addStars(id, 3, id.length() - 4));
                        if (0L == startVerTime) {
                            startVerTime = System.currentTimeMillis();
                        }
                        if (!isFaceDoing && isFaceEnable) {
                            isFaceDoing = true;
                            HSFEVerifyInputData data = new HSFEVerifyInputData();
                            data.image.format = HSFEImageFormat.HSFE_IMG_FORMAT_JPG;
                            data.image.imageBuf = bip;
                            data.image.width = 102;
                            data.image.height = 126;
                            data.timeOutMs = checkTimeout;
                            mHSFaceEngine.hsfeInputVerifyData(data);
                            isFromCard = true;
                        }
                        isFpResetUiRunning = false;
                        mHandler.removeCallbacks(resetFpUiRunnable);
                        updateUi(TYPE_FP, mResources.getString(R.string.card_no_fp));
                        resetUiRunnable();
//                        isFaceDoing = false;
                        break;
                }
            }
        }
    };

    /**
     * 调整图片比例
     *
     * @param bp
     * @return
     */
    Bitmap bmp(Bitmap bp) {
        float scaleWidth = 1;
        float scaleHeight = 1;
        int bmpWidth = bp.getWidth();
        int bmpHeight = bp.getHeight();
        /* 设置图片放大的比例 */
        double scale = 2;
        /* 计算这次要放大的比例 */
        scaleWidth = (float) (scaleWidth * scale);
        scaleHeight = (float) (scaleHeight * scale);

        /* 产生reSize后的Bitmap对象 */
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizeBmp = Bitmap.createBitmap(bp, 0, 0, bmpWidth, bmpHeight, matrix, true);
        return resizeBmp;
    }

    private Runnable resetUiRunnable = new Runnable() {

        @Override
        public void run() {
            if (isResetUiRunning) {
                isResetUiRunning = false;
                isFace1V1 = false;
//				isFp1V1 = false;
                updateUi(TYPE_FINGER_VALUE, "");
                faceTv.setVisibility(View.VISIBLE);
                faceIv.setImageResource(R.drawable.main_iv_bg);
                updateUi(TYPE_FACE_VALUE, "");
                updateUi(TYPE_FACE_RESULT, "");
                // updateUi(TYPE_FACE_DETECT, "");
                if (!isFpResetUiRunning) {
                    cardTv.setVisibility(View.VISIBLE);
                    cardMuskIv.setVisibility(View.GONE);
                    cardIv.setImageResource(R.drawable.main_iv_bg);
                    updateUi(TYPE_CARD_NAME, "");
                }
                updateUi(TYPE_CARD_SEX, "");
                updateUi(TYPE_CARD_NATION, "");
                updateUi(TYPE_CARD_ID, "");
                updateUi(TYPE_FP, "");
                // updateUi(TYPE_CARD_READ, "");
                // updateUi(TYPE_FEATURE_EXTRACT, "");
                startVerTime = 0L;
//				isFpVerifying = false;
            }
//			if (null != verTimer) {
//				isVerTimerRuning = false;
//				verTimer.cancel();
//				verTimer = null;
//			}
//			isMatchShowing = false;
        }
    };
    private Runnable matchResultRunnable = new Runnable() {

        @Override
        public void run() {
            if (isMatchResultRunning) {
                isMatchResultRunning = false;
                if (isMatchResultRejected) {
                    isMatchResultRejected = false;
                }
            }

        }
    };
    private Runnable fpRunnable = new Runnable() {

        @Override
        public void run() {
            while (!isFpStoped) {
                if (!isFpVerifying && !isFace1V1) {
                    isFpVerifying = true;
                    FpCollectRet fpRet = new FpCollectRet();
                    fpRet.setFp1V1(false);
                    fpRet.setRetBm(mFPModuleClient.FCV_CollectionFpImage());
                    mHandler.obtainMessage(ID_FP_IMG, fpRet).sendToTarget();
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    };

    class FpVerifyRunnable implements Runnable {
        private FpCollectRet fpCollectRet;

        public FpVerifyRunnable(FpCollectRet fpCollectRet) {
            this.fpCollectRet = fpCollectRet;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            com.hisign.AS60xSDK.entity.MatchResult matchResult = null;
            // 提取特征
            byte[] imgData = FileUtil.getInstance().bitmap2BytesNew(fpCollectRet.getRetBm());
            if (null != imgData && 0 < imgData.length) {
                byte[] feat = new byte[512];
                int retScore = AS60xIO.FCV_GetQualityScore(imgData);
                if (retScore >= fingerQuality) {
                    int ret = AS60xIO.FCV_ExtractFeature(imgData, feat);
                    if (0 == ret) {
                        if (isFace1V1 && isFp1V1 && fpCollectRet.isFp1V1() && null != cardFpFeatures && 0 < cardFpFeatures.length) {
                            mHandler.obtainMessage(ID_FP_SHOW, fpCollectRet).sendToTarget();
                            matchResult = AS60xIO.FCV_FingerMatch1vN(feat, cardFpFeatures);
                            LogUtil.getInstance().i(TAG, "265 isFp1V1 = " + isFp1V1);
                        } else if (!isFace1V1 && !fpCollectRet.isFp1V1() && null != fpFeatures && 0 < fpFeatures.length) {
                            mHandler.obtainMessage(ID_FP_SHOW, fpCollectRet).sendToTarget();
                            matchResult = AS60xIO.FCV_FingerMatch1vN(feat, fpFeatures);
                            LogUtil.getInstance().i(TAG, "269 isFp1V1 = " + isFp1V1);
                        }
                    } else {
                        LogUtil.getInstance().i(TAG, "276 AS60xIO.FCV_ExtractFeature ret = " + ret);
                    }
                }
            }
            FpMatchResult fpMatchResult = new FpMatchResult();
            fpMatchResult.setFpCollectRet(fpCollectRet);
            fpMatchResult.setMatchResult(matchResult);
            mHandler.obtainMessage(ID_FP_RESULT, fpMatchResult).sendToTarget();
        }
    }

    ;

    private Runnable resetFpUiRunnable = new Runnable() {

        @Override
        public void run() {
            if (isFpResetUiRunning) {
                isFpResetUiRunning = false;
                isFp1V1 = false;
                isFpVerifying = false;
                fingerTv.setVisibility(View.VISIBLE);
                fingerMuskIv.setVisibility(View.GONE);
                fingerIv.setImageResource(R.drawable.main_iv_bg);
                updateUi(TYPE_FINGER_VALUE, "");
                updateUi(TYPE_FINGER_RESULT, "");
                if (!isResetUiRunning && !isFaceDoing) {
                    cardTv.setVisibility(View.VISIBLE);
                    cardMuskIv.setVisibility(View.GONE);
                    cardIv.setImageResource(R.drawable.main_iv_bg);
                    updateUi(TYPE_CARD_NAME, "");
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mResources = getResources();
        LogUtil.getInstance().setDebug(true);
        LogUtil.getInstance().i(TAG, "onCreate()  isTaskRoot() = " + isTaskRoot());
        if (!this.isTaskRoot()) {
            Intent mainIntent = getIntent();
            String action = mainIntent.getAction();
            if (mainIntent.hasCategory(Intent.CATEGORY_LAUNCHER) && action.equals(Intent.ACTION_MAIN)) {
                finish();
                return;
            }
        }
        if (!(Boolean) SharedPreferencesUtil.get(this, ConstantValues.getFirstRun(), false)) {
            SharedPreferencesUtil.put(this, ConstantValues.getFirstRun(), true);
            initData();
        }
//        common = new Common();
//        IntentFilter filter = new IntentFilter();// 意图过滤器
//        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);// USB设备接入
//        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);// USB设备拔出
//        filter.addAction(common.ACTION_USB_PERMISSION);// 自定义的USB设备请求授权
//        registerReceiver(mUsbReceiver, filter);
        initView();
//		initEngine();
        ValueUtil.getInstance().setmActivity(this);
        ValueUtil.getInstance().setFeaturePostfix(".fpv");
//		resetLibData();
        ValueUtil.getInstance().setFaceFeaturesInited(false);
        mHandler = new Handler(this);
        MainApplication mApp = (MainApplication) getApplication();
        mHSFaceEngineInit = mApp.getmHSFaceEngineInit();
        mHSFaceEngine = mHSFaceEngineInit.getmHSFaceEngine();
        mFeatureUtil = new FeatureUtil(this, mHSFaceEngine, mHandler);
        mFpFeatureUtil = new FpFeatureUtil(this, mHandler);
        if (0 == mHSFaceEngineInit.getRet()) {
            mHSFaceEngine.hsfeSetProcessFaceCallBack(this);
            mHSFaceEngine.setVerifyInfoCallBack(this);
            mHSFaceEngine.setMatchCallback(this);
            new Thread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    mFeatureUtil.scanFeature();
//					resetMatchResultRunnable();
                }
            }).start();
        } else if (-10 == mHSFaceEngineInit.getRet()) {
            Toast.makeText(this, mResources.getString(R.string.authorization_overdue), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, mResources.getString(R.string.authorization_check), Toast.LENGTH_SHORT).show();
        }
//        Intent it = getIntent();
//        if (it == null || !it.getAction().contains("MAIN")) {
//            startIDCardService();
//        } else {
//            startBroadcastMode(this, "USB");
//        }
        startIDCardService();
//        mIDCardClient = mApp.getmIDCardClient();
        mFPModuleClient = mApp.getmFPModuleClient();
//        initReader();
        ValueUtil.getInstance().setFpFeaturesInited(false);
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                ValueUtil.getInstance().getFpMap().clear();
                mFpFeatureUtil.rescanFeature(null);
            }

        }).start();
        ValueUtil.getInstance().getFpScoreMap().clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.getInstance().i(TAG, "232 onResume() isDestroyed = " + isDestroyed);
        initEngine();
//        initCard();
        resetData();
        initCamera();
//		if (!isDestroyed) {
//			isDestroyed = true;
//			new Thread(new Runnable() {
//
//				@Override
//				public void run() {
//					// TODO Auto-generated method stub
//					isMatchResultRejected = true;
//					mFeatureUtil.scanFeature();
////					resetMatchResultRunnable();
//				}
//			}).start();
//		}
        FileUtil.getInstance().mkDir(ValueUtil.getInstance().getFP_PATH());
        if (ValueUtil.getInstance().isFpFeaturesInited()) {
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    startFpCollect();
                }
            }, 2000);
        }
    }

    private void startIDCardService() {
//        Intent intent = new Intent();
//        intent.setAction(IDC_START);
//        intent.putExtra("StartService", true);
//        sendBroadcast(intent);
        Intent intent = new Intent(this, CardReadService.class);
        intent.setAction(IDC_START);
        intent.putExtra("StratIDCardReceiver", true);
        this.startService(intent);
        IntentFilter filter = new IntentFilter();// 意图过滤器
        filter.addAction("com.lu.face.faceenginedemo.engine.MainActivity");
        registerReceiver(mUsbReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.getInstance().i(TAG, "253 onPause()");
        isCameraInited = false;
        isFpStoped = true;
        previewSfv.setVisibility(View.GONE);
        previewRl.removeView(previewSfv);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.getInstance().i(TAG, "261 onDestroy()");
        unregisterReceiver(mUsbReceiver);
        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
        }
//		if (null != mHSFaceEngine) {
//			mHSFaceEngine.hsfeDestory();
//		}
        if (null != mIDCardClient) {
            mIDCardClient.clearIDcardCallback();
        }
        if (null != mMediaPlayer) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
//		ValueUtil.getInstance().setmHSFaceEngine(null);
//		isDestroyed = false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        LogUtil.getInstance().i(TAG, "284 onBackPressed()");
    }

    private void onPreview(final byte[] uvcData, Camera camera) {
        if (null != mProgressDialog && mProgressDialog.isShowing()) {
            return;
        }
//		FileUtil.getInstance().saveImageByByteAndPath(uvcData, "/sdcard/11.raw");
        LogUtil.getInstance().i(TAG, "166 faceEngine.hsfeInputFrame(frame)");
        switch (displayOrientation) {
            case 0:
                break;
            case 90:
                USBCamCtrl.UVCYuvSPMirror(ValueUtil.getInstance().getHeight(), ValueUtil.getInstance().getWidth(), uvcData, 90, 1);// 顺时针转90°
                USBCamCtrl.UVCYuvFlip(ValueUtil.getInstance().getHeight(), ValueUtil.getInstance().getWidth(), uvcData);
                break;
            case 180:
                USBCamCtrl.UVCYuvFlip(ValueUtil.getInstance().getWidth(), ValueUtil.getInstance().getHeight(), uvcData);
                break;
            case 270:
                USBCamCtrl.UVCYuvSPMirror(ValueUtil.getInstance().getHeight(), ValueUtil.getInstance().getWidth(), uvcData, 90, 1);
                break;
            default:
                break;
        }
//		FileUtil.getInstance().saveImageByByteAndPath(uvcData, "/sdcard/12.raw");

        HSFEFrame frame = new HSFEFrame();
        frame.channelId = 1;
        frame.imageBuf = uvcData;
        frame.width = ValueUtil.getInstance().getWidth();
        frame.height = ValueUtil.getInstance().getHeight();
        frame.format = HSFEImageFormat.HSFE_IMG_FORMAT_YUV;
        frame.frameId = frameIndex;
        if (null != frame && null != frame.imageBuf) {
            LogUtil.getInstance().i(TAG, "138 frame.width = " + frame.width + " frame.height = " + frame.height);
            mHSFaceEngine.hsfeInputFrame(frame);
            frameIndex++;
            if (frameIndex == Integer.MAX_VALUE) {
                frameIndex = 0;
            }
        }
    }

    private void initReader() {
        // TODO Auto-generated method stub
        mIDCardClient.setIDCardCallback(new BroadcastCallback() {

            @Override
            public void getoutIDCardMsg(final IDCardMsg msg) {
                if (null != mProgressDialog && mProgressDialog.isShowing()) {
                    return;
                }
                if (previewSfv.getVisibility() == View.GONE) {
                    return;
                }
                if (isFaceDoing || isFpDoing) {
                    return;
                }
                // TODO Auto-generated method stub
                // resultCode == 0 时， 读卡成功，此时已经读到了身份证里的信息。
                // resultCode == 1 时 ，寻卡和选卡成功，这时候并没有读身份证里的信息。
                // resultCode == 2 时，身份证已移除。
                switch (msg.resultCode) {
                    case 0:
                        isFace1V1 = true;
                        isFp1V1 = false;
                        mHandler.removeMessages(ID_FP_IMG);
                        mHandler.removeMessages(ID_FP_SHOW);
                        mHandler.removeMessages(ID_FP_RESULT);
                        mName = null;
                        mCardId = null;
                        cardFpFeatures = null;
                        isResetUiRunning = false;
                        isCameraDialogShowing = false;
                        startPlay(R.raw.beep);

                        fingerTv.setVisibility(View.VISIBLE);
                        fingerMuskIv.setVisibility(View.GONE);
                        fingerIv.setImageResource(R.drawable.main_iv_bg);
                        updateUi(TYPE_FINGER_VALUE, "");
                        updateUi(TYPE_FINGER_RESULT, "");

                        faceTv.setVisibility(View.VISIBLE);
                        faceIv.setImageResource(R.drawable.main_iv_bg);
                        updateUi(TYPE_FACE_VALUE, "");
                        updateUi(TYPE_FACE_RESULT, "");

                        cardTv.setVisibility(View.GONE);
                        cardIv.setImageResource(R.drawable.main_iv_bg);
                        cardMuskIv.setVisibility(View.GONE);
                        showIv(msg.imagePath, cardIv, 1);
                        mName = msg.name;
                        updateUi(TYPE_CARD_NAME, StringUtil.addStars(mName, 1, mName.length()));
                        updateUi(TYPE_CARD_SEX, msg.gender);
                        updateUi(TYPE_CARD_NATION, msg.folk);
                        String id = msg.code;
                        mCardId = id;
                        updateUi(TYPE_CARD_ID, StringUtil.addStars(id, 3, id.length() - 4));
                        if (0L != msg.tFoundTime) {
                            cardTime = msg.tReadTime - msg.tFoundTime;
                        }
                        if (isDetailTimeShow && (0L != msg.tFoundTime)) {
                            updateUi(TYPE_CARD_READ, String.valueOf(msg.tReadTime - msg.tFoundTime) + "ms");
                        }
//					if (null != verTimer) {
//					isVerTimerRuning = false;
//					verTimer.cancel();
//					verTimer = null;
//				}
                        if (0L == startVerTime) {
                            startVerTime = System.currentTimeMillis();
                        }
                        if (!isFaceDoing && isFaceEnable) {
                            isFaceDoing = true;
                            File cardImage = new File(msg.imagePath);
                            if (null != cardImage && cardImage.exists()) {
                                try {
                                    BitmapFactory.Options options = new BitmapFactory.Options();
                                    /**
                                     * 最关键在此，把options.inJustDecodeBounds = true;
                                     * 这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了
                                     */
                                    options.inJustDecodeBounds = true;
                                    Bitmap bitmap = BitmapFactory.decodeFile(msg.imagePath, options); // 此时返回的bitmap为null
                                    /**
                                     *options.outHeight为原始图片的高
                                     */
                                    LogUtil.getInstance().i(TAG, "Bitmap Height == " + options.outWidth + "  " + options.outHeight);
                                    byte[] buffer = null;
                                    FileInputStream fis = new FileInputStream(cardImage);
                                    buffer = new byte[fis.available()];
                                    fis.read(buffer);
                                    if (buffer != null) {
                                        HSFEVerifyInputData data = new HSFEVerifyInputData();
                                        data.image.format = HSFEImageFormat.HSFE_IMG_FORMAT_JPG;
                                        data.image.imageBuf = buffer;
                                        data.image.width = options.outWidth;
                                        data.image.height = options.outHeight;
                                        data.timeOutMs = checkTimeout;
                                        mHSFaceEngine.hsfeInputVerifyData(data);
                                        isFromCard = true;
                                    }
                                    fis.close();
                                } catch (FileNotFoundException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                    isFaceDoing = false;
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                    isFaceDoing = false;
                                }
                            }
                        }
                        if (1 == msg.bHaveFingerFeat) {
                            updateUi(TYPE_FP, getFingerByCode(msg.fpFeatL[5]) + "\n\u3000\u3000\u3000" + getFingerByCode(msg.fpFeatR[5]));
                            if (!isFpDoing && isFingerEnable) {
                                isFpDoing = true;
                                new Thread(new Runnable() {

                                    @Override
                                    public void run() {
                                        // TODO Auto-generated method stub
                                        isFp1V1 = true;
                                        cardFpFeatures = new byte[2][];
                                        cardFpFeatures[0] = msg.fpFeatL;
                                        cardFpFeatures[1] = msg.fpFeatR;
                                        FpCollectRet fpRet = new FpCollectRet();
                                        fpRet.setFp1V1(true);
                                        fpRet.setRetBm(fingerCollection(5));
                                        mHandler.obtainMessage(ID_FP_IMG, fpRet).sendToTarget();
                                        LogUtil.getInstance().i(TAG, "602 isFp1V1 = " + isFp1V1);
                                    }
                                }).start();
                            }
                        } else {
                            isFpResetUiRunning = false;
                            mHandler.removeCallbacks(resetFpUiRunnable);
                            updateUi(TYPE_FP, mResources.getString(R.string.card_no_fp));
                        }
                        resetUiRunnable();
                        break;
                    case 1:
                    case 2:
                    case 3:
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void initEngine() {
        HSFEChannelParameters parameters = new HSFEChannelParameters();
        parameters.channelId = 1;
        parameters.faceMinSize = (Integer) SharedPreferencesUtil.get(this, ConstantValues.getMinFace(), 80);
        String type = (String) SharedPreferencesUtil.get(this, ConstantValues.getTypeFaceVerify(), "");
        if (StringUtil.isEqual(type, getResources().getStringArray(R.array.array_verify_type)[0])) {
            parameters.channelMode = HSFEChannelParameters.ChannelMode_1V1;
        } else if (StringUtil.isEqual(type, getResources().getStringArray(R.array.array_verify_type)[1])) {
            parameters.channelMode = HSFEChannelParameters.ChannelMode_1VN;
        }
        mHSFaceEngine.hsfeAddChannel(parameters);
    }

//    private void initCard() {
//        try {
//            if (android.os.Build.MODEL.equals("TPS480")) {
//                PosUtil.setIdCardPower(PosUtil.IDCARD_POWER_ON);// 身份证上电
//            } else {
//                PosUtil.setFingerPrintPower(PosUtil.FINGERPRINT_POWER_ON);// 身份证/指纹仪上电
//            }
//            Thread.sleep(1000);
////            sendBroadcast(new Intent(common.ACTION_USB_PERMISSION));
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (sdta == null) {
//                // Toast.makeText(this, "模块连接失败", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(this, "模块连接成功", Toast.LENGTH_SHORT).show();
//            }
//        }
//
//    }

    @SuppressLint("NewApi")
    private void initCamera() {
        // TODO Auto-generated method stub
        if (isCameraInited || !(Boolean) SharedPreferencesUtil.get(this, ConstantValues.getSwitchChannel0(), false)) {
            return;
        }
        isCameraInited = true;
        previewSfv = new SurfaceView(this);
        final LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        params.height = (int) (0.666F * mDisplayMetrics.heightPixels);
        float scale = 0F;
        if (0 == displayOrientation || 180 == displayOrientation) {
            scale = 1.333F;
            ValueUtil.getInstance().setWidth(640);
            ValueUtil.getInstance().setHeight(480);
        } else if (90 == displayOrientation || 270 == displayOrientation) {
            scale = 0.75F;
            ValueUtil.getInstance().setWidth(480);
            ValueUtil.getInstance().setHeight(640);
        }
        params.width = (int) (scale * params.height);
        previewSfv.setZOrderOnTop(false);
        previewRl.addView(previewSfv, 0, params);
        previewSfv.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                // TODO Auto-generated method stub
                if (0 == displayOrientation || 180 == displayOrientation) {
                    cameraControl = new SysCameraControl((Integer) SharedPreferencesUtil.get(MainActivity.this, ConstantValues.getTypeCamera(), 1), previewSfv,
                            ValueUtil.getInstance().getWidth(), ValueUtil.getInstance().getHeight(), 0, 0);
                } else if (90 == displayOrientation || 270 == displayOrientation) {
                    cameraControl = new SysCameraControl((Integer) SharedPreferencesUtil.get(MainActivity.this, ConstantValues.getTypeCamera(), 1), previewSfv,
                            ValueUtil.getInstance().getHeight(), ValueUtil.getInstance().getWidth(), 0, 0);
                }
                cameraListener = new SysCameraControl.OnCameraStatusListener() {

                    @Override
                    public void onSnapPhotoDone(String picSavePath) {

                    }

                    @Override
                    public void onPreviewUVCData(byte[] uvcData, Camera camera) {
                        onPreview(uvcData, camera);
                    }

                    @Override
                    public void onActionDown() {

                    }

                    @Override
                    public void getRealPreivewResolution(int width, int height) {

                    }
                };
                cameraControl.setOnCameraStatusListener(cameraListener);

                LayoutParams rectSfvPas = (LayoutParams) rectSfv.getLayoutParams();
                rectSfvPas.width = params.width;
                rectSfvPas.height = params.height;
                rectSfvPas.addRule(RelativeLayout.CENTER_IN_PARENT);
                rectSfv.setLayoutParams(rectSfvPas);
                previewSfv.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    @SuppressLint("NewApi")
    private void initView() {
        // TODO Auto-generated method stub
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(mResources.getString(R.string.msg_progress_dialog));
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);

        Camera.CameraInfo camerinfo = new Camera.CameraInfo();
        int cameraNum = Camera.getNumberOfCameras();
        LogUtil.getInstance().i(TAG, "cameraNum :" + cameraNum);
        if (cameraNum == 1) {
            if (Camera.CameraInfo.CAMERA_FACING_BACK == camerinfo.facing) {
                ValueUtil.getInstance().setCameras(getResources().getStringArray(R.array.array_camera_back));
            } else if (Camera.CameraInfo.CAMERA_FACING_FRONT == camerinfo.facing) {
                ValueUtil.getInstance().setCameras(getResources().getStringArray(R.array.array_camera_front));
            }
        } else if (cameraNum == 2) {
            ValueUtil.getInstance().setCameras(getResources().getStringArray(R.array.array_camera));
        }
        ValueUtil.getInstance().setVerifys(getResources().getStringArray(R.array.array_verify_type));
        setContentView(R.layout.activity_main);
        fingerThdTv = (TextView) findViewById(R.id.tv_finger_threshold);
        fingerVerTv = (TextView) findViewById(R.id.tv_finger_value);
        fingerRetTv = (TextView) findViewById(R.id.tv_finger_result);
        faceThdTv = (TextView) findViewById(R.id.tv_face_threshold);
        faceVerTv = (TextView) findViewById(R.id.tv_face_value);
        faceRetTv = (TextView) findViewById(R.id.tv_face_result);
        nameTv = (TextView) findViewById(R.id.tv_card_name);
        sexTv = (TextView) findViewById(R.id.tv_card_sex);
        nationTv = (TextView) findViewById(R.id.tv_card_nation);
        birthdayTv = (TextView) findViewById(R.id.tv_card_birthday);
        idTv = (TextView) findViewById(R.id.tv_card_id);
        fpTv = (TextView) findViewById(R.id.tv_fp);
        detectTv = (TextView) findViewById(R.id.tv_detect);
        detectTotalTv = (TextView) findViewById(R.id.tv_detect_total);
        cardRedTv = (TextView) findViewById(R.id.tv_card_read);
        featureExTv = (TextView) findViewById(R.id.tv_feature);
        versionTv = (TextView) findViewById(R.id.tv_version);
        fingerTv = (TextView) findViewById(R.id.tv_finger);
        faceTv = (TextView) findViewById(R.id.tv_face);
        cardTv = (TextView) findViewById(R.id.tv_card);

        mDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        ValueUtil.getInstance().setmDisplayMetrics(mDisplayMetrics);
        ValueUtil.getInstance().setTextSize(DisplayUtil.px2sp(this, 0.0117F * mDisplayMetrics.widthPixels));
        fingerTv.setTextSize(ValueUtil.getInstance().getTextSize());
        fingerThdTv.setTextSize(ValueUtil.getInstance().getTextSize());
        fingerVerTv.setTextSize(ValueUtil.getInstance().getTextSize());
        faceTv.setTextSize(ValueUtil.getInstance().getTextSize());
        faceThdTv.setTextSize(ValueUtil.getInstance().getTextSize());
        faceVerTv.setTextSize(ValueUtil.getInstance().getTextSize());
        cardTv.setTextSize(ValueUtil.getInstance().getTextSize());
        nameTv.setTextSize(ValueUtil.getInstance().getTextSize());
        sexTv.setTextSize(ValueUtil.getInstance().getTextSize());
        nationTv.setTextSize(ValueUtil.getInstance().getTextSize());
        birthdayTv.setTextSize(ValueUtil.getInstance().getTextSize());
        idTv.setTextSize(ValueUtil.getInstance().getTextSize());
        fpTv.setTextSize(ValueUtil.getInstance().getTextSize());
        detectTv.setTextSize(ValueUtil.getInstance().getTextSize());
        detectTotalTv.setTextSize(ValueUtil.getInstance().getTextSize());
        cardRedTv.setTextSize(ValueUtil.getInstance().getTextSize());
        featureExTv.setTextSize(ValueUtil.getInstance().getTextSize());
        versionTv.setTextSize(ValueUtil.getInstance().getTextSize());
        fingerRetTv.setTextSize(DisplayUtil.px2sp(this, 0.04F * mDisplayMetrics.widthPixels));
        faceRetTv.setTextSize(DisplayUtil.px2sp(this, 0.04F * mDisplayMetrics.widthPixels));

        previewRl = (RelativeLayout) findViewById(R.id.rl_preview);
        cardIv = (ImageView) findViewById(R.id.iv_card);
        cardMuskIv = (ImageView) findViewById(R.id.iv_card_musk);
        cardMuskIv.setVisibility(View.GONE);
        cardTipIv = (ImageView) findViewById(R.id.iv_card_tip);

        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(2);
        mPaint.setColor(Color.BLUE);
        mPaint.setAntiAlias(true); // 消除锯齿

        clearPaint = new Paint();
        clearPaint.setAntiAlias(true);// 抗锯齿
        clearPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));// 所有的图层都不会在画布上展示

        ffRl = (LinearLayout) findViewById(R.id.rl_finger_face);
        cardRl = (RelativeLayout) findViewById(R.id.rl_card);
        fiIvRl = (RelativeLayout) findViewById(R.id.rl_finger_iv);
        fingerIv = (ImageView) findViewById(R.id.iv_finger);
        fingerMuskIv = (ImageView) findViewById(R.id.iv_finger_musk);
        fingerMuskIv.setVisibility(View.GONE);
        fingerTipIv = (ImageView) findViewById(R.id.iv_finger_tip);
        faIvRl = (RelativeLayout) findViewById(R.id.rl_face_iv);
        faIvRl.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                LogUtil.getInstance().i(TAG,
                        "620 onClick isFromCard = " + isFromCard + " isMatchShowing = " + isFace1V1 + " isCameraDialogShowing = " + isCameraDialogShowing + "tongguo = "
                                + StringUtil.isEqual(mResources.getString(R.string.face_pass), faceRetTv.getText().toString()) + " null != resultBm = " + (null != resultBm)
                                + " !resultBm.isRecycled() = ");
                if (!isMatchResultRejected && isFromCard && isFace1V1 && !isCameraDialogShowing
                        && StringUtil.isEqual(mResources.getString(R.string.face_pass), faceRetTv.getText().toString()) && null != resultBm && !resultBm.isRecycled()) {
                    isFromCard = false;
                    isCameraDialogShowing = true;
                    showPopupWindow();
                }
            }
        });
        faceIv = (ImageView) findViewById(R.id.iv_face);
        faceTipIv = (ImageView) findViewById(R.id.iv_face_tip);
        cardIvRl = (RelativeLayout) findViewById(R.id.rl_card_iv);
        cardIvRl.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                isCameraDialogShowing = false;
                Intent intent = new Intent(MainActivity.this, FileListActivity.class);
                startActivityForResult(intent, 1);
                cardTv.setVisibility(View.VISIBLE);
                cardMuskIv.setVisibility(View.GONE);
                cardIv.setImageResource(R.drawable.main_iv_bg);
                updateUi(TYPE_CARD_NAME, "");
                updateUi(TYPE_CARD_SEX, "");
                updateUi(TYPE_CARD_NATION, "");
                updateUi(TYPE_CARD_ID, "");
                updateUi(TYPE_FP, "");
//				String type = (String) SharedPreferencesUtil.get(MainActivity.this, ConstantValues.getTypeFaceVerify(), "");
//				try {
//					Intent intent = new Intent();
//					intent.setType("image/*");// 从所有图片中进行选择
//					// 根据版本号不同使用不同的Action
//					if (Build.VERSION.SDK_INT < 19) {
//						intent.setAction(Intent.ACTION_GET_CONTENT);
//					} else {
//						intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
//					}
//					intent.addCategory(Intent.CATEGORY_OPENABLE);
//					MainActivity.this.startActivityForResult(intent, 1);
//				} catch (ActivityNotFoundException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//					Toast.makeText(MainActivity.this, "请安装文件管理器！", Toast.LENGTH_SHORT).show();
//				}
            }
        });
        setRl = (RelativeLayout) findViewById(R.id.rl_set);
        findViewById(R.id.btn_orientation).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                int angle = (Integer) SharedPreferencesUtil.get(ValueUtil.getInstance().getmActivity(), ConstantValues.getDisplayOrientation(), 0);
                angle -= 90;
                if (0 > angle) {
                    angle = 270;
                }
                SharedPreferencesUtil.put(MainActivity.this, ConstantValues.getDisplayOrientation(), angle);
                displayOrientation = angle;
                previewSfv.setVisibility(View.GONE);
                previewRl.removeView(previewSfv);
                isCameraInited = false;
                initCamera();
                if (!isDrawing && rectSfv.isShown() && isFrameOpened) {
                    isDrawing = true;
                    mCanvas = mSurfaceHolder.lockCanvas();
                    if (null != mCanvas) {
                        mCanvas.drawPaint(clearPaint);
                        mSurfaceHolder.unlockCanvasAndPost(mCanvas);
                    }
                    isDrawing = false;
                }
                animationLeft2Right();
            }
        });
        findViewById(R.id.btn_frame_switch).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                SharedPreferencesUtil.put(MainActivity.this, ConstantValues.getTypeFrame(),
                        !(Boolean) SharedPreferencesUtil.get(MainActivity.this, ConstantValues.getTypeFrame(), false));
                isFrameReverse = !isFrameReverse;
                animationLeft2Right();
            }
        });
        findViewById(R.id.btn_set).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                startActivity(new Intent(MainActivity.this, ConfigureActivity.class));
                animationLeft2Right();
            }
        });
        findViewById(R.id.btn_camera_switch).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
//				if (1 < ValueUtil.getInstance().getCameras().length) {
                if (0 == (Integer) SharedPreferencesUtil.get(MainActivity.this, ConstantValues.getTypeCamera(), 0)) {
                    SharedPreferencesUtil.put(MainActivity.this, ConstantValues.getTypeCamera(), 1);
                } else {
                    SharedPreferencesUtil.put(MainActivity.this, ConstantValues.getTypeCamera(), 0);
                }
                previewSfv.setVisibility(View.GONE);
                previewRl.removeView(previewSfv);
                isCameraInited = false;
                initCamera();
                if (!isDrawing && rectSfv.isShown() && isFrameOpened) {
                    isDrawing = true;
                    mCanvas = mSurfaceHolder.lockCanvas();
                    if (null != mCanvas) {
                        mCanvas.drawPaint(clearPaint);
                        mSurfaceHolder.unlockCanvasAndPost(mCanvas);
                    }
                    isDrawing = false;
                }
//				}
                animationLeft2Right();
                // TODO Auto-generated method stub
                /*
                 * new Builder(MainActivity.this).setItems(ValueUtil.getInstance().getCameras(), new DialogInterface.OnClickListener() { public void onClick(DialogInterface arg0,
                 * int arg1) { if (!isCameraDialogShowing) { isCameraDialogShowing = true; SharedPreferencesUtil.put(MainActivity.this, ConstantValues.getTypeCamera(), arg1);
                 * previewSfv.setVisibility(View.GONE); previewRl.removeView(previewSfv); isCameraInited = false; initCamera(); arg0.dismiss(); isCameraDialogShowing = false; if
                 * (!isDrawing && rectSfv.isShown() && (Boolean) SharedPreferencesUtil.get(MainActivity.this, ConstantValues.getSwitchFaceFrame(), false)) { isDrawing = true;
                 * mCanvas = mSurfaceHolder.lockCanvas(); if (null != mCanvas) { mCanvas.drawPaint(clearPaint); mSurfaceHolder.unlockCanvasAndPost(mCanvas); // TODO Auto-generated
                 * method stub isDrawing = false; } } } } }).show();
                 */
            }
        });
        findViewById(R.id.rl_root).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (null != mPopupWindow && mPopupWindow.isShowing()) {
                    return;
                }
                if (isToRightable) {
                    animationLeft2Right();
                }
                if (isToLeftable) {
                    Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.right_to_left);
                    animation.setAnimationListener(new AnimationListener() {

                        @Override
                        public void onAnimationStart(Animation arg0) {
                            // TODO Auto-generated method stub
                        }

                        @Override
                        public void onAnimationRepeat(Animation arg0) {
                            // TODO Auto-generated method stub
                        }

                        @Override
                        public void onAnimationEnd(Animation arg0) {
                            isToRightable = true;
                            isToLeftable = false;
                            if (null != mTimer) {
                                mTimer.cancel();
                                mTimer = new Timer();
                                mTimer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        MainActivity.this.runOnUiThread(new Runnable() {

                                            @Override
                                            public void run() {
                                                // TODO Auto-generated method
                                                // stub
                                                animationLeft2Right();
                                            }
                                        });
                                    }
                                }, 3000);
                            }
                        }
                    });
                    setRl.setVisibility(View.VISIBLE);
                    setRl.startAnimation(animation);
                }
            }
        });
        fingerTvRl = (RelativeLayout) findViewById(R.id.rl_finger_tv);
        faceTvRl = (RelativeLayout) findViewById(R.id.rl_face_tv);
        ffRl.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                // TODO Auto-generated method stub
                LayoutParams ffRlPas = (LayoutParams) ffRl.getLayoutParams();
                ffRlPas.height = (int) (0.222F * mDisplayMetrics.heightPixels);
                ffRlPas.width = (int) (4F * ffRlPas.height);
                ffRl.setLayoutParams(ffRlPas);

                LinearLayout.LayoutParams fiIvRlPas = (LinearLayout.LayoutParams) fiIvRl.getLayoutParams();
                fiIvRlPas.width = (int) (0.188F * ffRlPas.width);
                fiIvRlPas.height = (int) (1.083F * fiIvRlPas.width);
                fiIvRl.setLayoutParams(fiIvRlPas);

                LayoutParams fingerIvPas = (LayoutParams) fingerIv.getLayoutParams();
                fingerIvPas.width = (int) (0.166F * ffRlPas.width);
                fingerIvPas.height = (int) (1.226F * fingerIvPas.width);
                fingerIv.setLayoutParams(fingerIvPas);

                fingerMuskIv.setLayoutParams(fingerIvPas);

                LayoutParams fingerTipIvPas = (LayoutParams) fingerTipIv.getLayoutParams();
                fingerTipIvPas.width = (int) (0.047F * ffRlPas.width);
                fingerTipIvPas.height = fingerTipIvPas.width;
                fingerTipIv.setLayoutParams(fingerTipIvPas);

                LinearLayout.LayoutParams faIvRlPas = (LinearLayout.LayoutParams) faIvRl.getLayoutParams();
                faIvRlPas.width = fiIvRlPas.width;
                faIvRlPas.height = fiIvRlPas.height;
                faIvRl.setLayoutParams(faIvRlPas);

                LayoutParams faceIvPas = (LayoutParams) faceIv.getLayoutParams();
                faceIvPas.width = fingerIvPas.width;
                faceIvPas.height = fingerIvPas.height;
                faceIv.setLayoutParams(faceIvPas);

                LayoutParams faceTipIvPas = (LayoutParams) faceTipIv.getLayoutParams();
                faceTipIvPas.width = fingerTipIvPas.width;
                faceTipIvPas.height = faceTipIvPas.width;
                faceTipIv.setLayoutParams(faceTipIvPas);

                LayoutParams previewRlPas = (LayoutParams) previewRl.getLayoutParams();
                previewRlPas.height = (int) (3F * ffRlPas.height);
                previewRlPas.width = (int) (1.333F * previewRlPas.height);
                previewRl.setLayoutParams(previewRlPas);

                LayoutParams cardRlPas = (LayoutParams) cardRl.getLayoutParams();
                cardRlPas.width = (int) (1.769F * ffRlPas.height);
                cardRl.setLayoutParams(cardRlPas);

                LayoutParams cardIvRlPas = (LayoutParams) cardIvRl.getLayoutParams();
                cardIvRlPas.width = fiIvRlPas.width;
                cardIvRlPas.height = fiIvRlPas.height;
                cardIvRl.setLayoutParams(cardIvRlPas);

                LayoutParams cardIvPas = (LayoutParams) cardIv.getLayoutParams();
                cardIvPas.width = fingerIvPas.width;
                cardIvPas.height = fingerIvPas.height;
                cardIv.setLayoutParams(cardIvPas);

                cardMuskIv.setLayoutParams(cardIvPas);

                LayoutParams cardTipIvPas = (LayoutParams) cardTipIv.getLayoutParams();
                cardTipIvPas.width = fingerTipIvPas.width;
                cardTipIvPas.height = cardTipIvPas.width;
                cardTipIv.setLayoutParams(cardTipIvPas);

                // previewSfv = (SurfaceView) findViewById(R.id.sfv_preview);
                // sfv.setZOrderOnTop(false);
                // sfv.setZOrderMediaOverlay(false);
                rectSfv = (SurfaceView) findViewById(R.id.sfv_rect);
                mSurfaceHolder = rectSfv.getHolder();
                mSurfaceHolder.addCallback(MainActivity.this);
                mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);
                rectSfv.setZOrderOnTop(true);
                // rectSfv.setZOrderMediaOverlay(false);

//				showPopupWindow();

                // initCamera();
                ffRl.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

    }

    @SuppressLint("NewApi")
    private void showPopupWindow() {
        // TODO Auto-generated method stub
        final View view = LayoutInflater.from(this).inflate(R.layout.popupwindow_main, null);
        TextView titleTv = (TextView) view.findViewById(R.id.tv_title_dialog);
        RelativeLayout etRl = (RelativeLayout) view.findViewById(R.id.rl_dialog);
        TextView etTv = (TextView) view.findViewById(R.id.tv_name_dialog);
        final String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        final TextView nameEt = (TextView) view.findViewById(R.id.et_name_dialog);
        if (StringUtil.isNotNull(mName) && StringUtil.isNotNull(mCardId)) {
            nameEt.setText(mCardId + "_" + mName + "_" + mFeatureUtil.getMaxIndexByNameAndCard(mName, mCardId));
        }
        final ImageView faceIv = (ImageView) view.findViewById(R.id.iv_face_dialog);
        if (null != resultBm && !resultBm.isRecycled()) {
            faceIv.setImageBitmap(resultBm);
        }
        final LinearLayout btnLl = (LinearLayout) view.findViewById(R.id.ll_btn_dialog);
        Button okBtn = (Button) view.findViewById(R.id.btn_ok);
        final Button cancelBtn = (Button) view.findViewById(R.id.btn_cancel);
        okBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                final String name = nameEt.getText().toString();
                if (StringUtil.isNotNull(name)) {
                    if (null != resultBm && !resultBm.isRecycled()) {
                        new Thread(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                String path = ValueUtil.getInstance().getFACE_PATH() + File.separator + mCardId + "_" + mName + File.separator + name + ".jpg";
                                FileUtil.getInstance().bitmapToJpeg(resultBm, path);
                                mFeatureUtil.extractFeatureByPath(path, -1);
								/*boolean isExFeaturingTmp = false;
								HSFEImage image = new HSFEImage();
								image.imageBuf = FileUtil.getInstance().bitmap2Bytes(resultBm);
								image.format = HSFEImageFormat.HSFE_IMG_FORMAT_JPG;
								image.width = resultBm.getWidth();
								image.height = resultBm.getHeight();
								if (null != image.imageBuf) {
									byte[] feature = ValueUtil.getInstance().getmHSFaceEngine().hsfeExtractFaceFeature(image);
									if (null != feature && 0 < feature.length) {
										String path = PATH + File.separator + name + ".jpg";
										FileUtil.getInstance().bitmapToJpeg(resultBm, path);
										String featurePath = path + ValueUtil.getInstance().getFeaturePostfix();
										FileUtil.getInstance().saveImageByByteAndPath(feature, featurePath);
										mName = null;
										mFeatureUtil.extractFeatureByPath(path);
										isExFeaturingTmp = true;
									}
								}
								if (isMatchResultRejected) {
									isMatchResultRejected = isExFeaturingTmp;
								}*/
//								resetMatchResultRunnable();
                            }
                        }).start();
                    }
                    if (null != mPopupWindow && mPopupWindow.isShowing()) {
                        mPopupWindow.dismiss();
                    }
                } else {
                    Toast.makeText(MainActivity.this, mResources.getString(R.string.face_name_null), Toast.LENGTH_SHORT).show();
                }
            }
        });
        cancelBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (null != mPopupWindow && mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                }
            }
        });

        titleTv.setTextSize(ValueUtil.getInstance().getTextSize());
        nameEt.setTextSize(ValueUtil.getInstance().getTextSize());
        etTv.setTextSize(ValueUtil.getInstance().getTextSize());
        okBtn.setTextSize(ValueUtil.getInstance().getTextSize());
        cancelBtn.setTextSize(ValueUtil.getInstance().getTextSize());

        LayoutParams faceIvPas = (LayoutParams) faceIv.getLayoutParams();
        faceIvPas.width = (int) (0.1F * mDisplayMetrics.widthPixels);
        faceIvPas.height = (int) (1.333F * faceIvPas.width);
        faceIvPas.topMargin = (int) (0.03F * mDisplayMetrics.heightPixels);
        faceIvPas.bottomMargin = (int) (0.03F * mDisplayMetrics.heightPixels);
        faceIv.setLayoutParams(faceIvPas);

        LayoutParams nameEtPas = (LayoutParams) nameEt.getLayoutParams();
        nameEtPas.width = (int) (0.3F * mDisplayMetrics.widthPixels);
        nameEtPas.height = (int) (0.05F * mDisplayMetrics.heightPixels);
        nameEt.setLayoutParams(nameEtPas);

        LayoutParams btnLlPas = (LayoutParams) btnLl.getLayoutParams();
        btnLlPas.width = (int) (0.4F * mDisplayMetrics.widthPixels);
//		btnLlPas.height = (int) (0.1F * mDisplayMetrics.heightPixels);
        btnLlPas.topMargin = (int) (0.03F * mDisplayMetrics.heightPixels);
        btnLl.setLayoutParams(btnLlPas);

        mPopupWindow = new PopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                // TODO Auto-generated method stub
                mPopupWindow = null;
                isCameraDialogShowing = false;
            }
        });
        mPopupWindow.setFocusable(true);
        ColorDrawable dw = new ColorDrawable(Color.TRANSPARENT);
        mPopupWindow.setBackgroundDrawable(dw);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(false);
        mPopupWindow.showAtLocation(LayoutInflater.from(this).inflate(R.layout.activity_main, null), Gravity.CENTER, 0, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        boolean isResetUiable = true;
        isFace1V1 = true;
        startPlay(R.raw.beep);
        faceTv.setVisibility(View.VISIBLE);
        faceIv.setImageResource(R.drawable.main_iv_bg);
        updateUi(TYPE_FACE_VALUE, "");
        updateUi(TYPE_FACE_RESULT, "");
        if (resultCode == Activity.RESULT_OK && null != intent) {
            String path = intent.getStringExtra("selectedPath");
            if (StringUtil.isNotNull(path) && (path.endsWith(".jpg") || path.endsWith(".bmp") || path.endsWith(".JPG") || path.endsWith(".BMP"))) {
                cardTv.setVisibility(View.GONE);
                cardMuskIv.setVisibility(View.GONE);
                cardIv.setImageResource(R.drawable.main_iv_bg);
                showIv(path, cardIv, 1);
                File cardImage = new File(path);
                if (null != cardImage && cardImage.exists() && cardImage.isFile()) {
                    mName = cardImage.getName();
                    updateUi(TYPE_CARD_NAME, StringUtil.addStars(mName, 1, mName.length()));
                    if (!isFaceDoing && isFaceEnable) {
                        isFaceDoing = true;
                        if (0L == startVerTime) {
                            startVerTime = System.currentTimeMillis();
                        }
                        int imgSampleSize = 0;
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        /**
                         * 最关键在此，把options.inJustDecodeBounds = true;
                         * 这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了
                         */
                        options.inJustDecodeBounds = true;
                        Bitmap bitmap = BitmapFactory.decodeFile(path, options); // 此时返回的bitmap为null
                        if (options.outWidth > options.outHeight) {// must < 1024*768*2
                            imgSampleSize = (options.outWidth + 360) / 720; // >1000 -> resample
                        } else {
                            imgSampleSize = (options.outHeight + 360) / 720;
                        }
                        options.inSampleSize = imgSampleSize;
                        options.inJustDecodeBounds = false;// 读取数据
                        bitmap = BitmapFactory.decodeFile(path, options); // 此时返回的bitmap为null
                        /**
                         *options.outHeight为原始图片的高
                         */
                        LogUtil.getInstance().i(TAG, "Bitmap Height == " + options.outWidth + "  " + options.outHeight);
                        if (null != bitmap && !bitmap.isRecycled()) {
                            byte[] buffer = FileUtil.getInstance().bitmap2Bytes(bitmap);
                            if (buffer != null) {
                                HSFEVerifyInputData data = new HSFEVerifyInputData();
                                data.image.format = HSFEImageFormat.HSFE_IMG_FORMAT_JPG;
                                data.image.imageBuf = buffer;
                                data.image.width = options.outWidth;
                                data.image.height = options.outHeight;
                                data.timeOutMs = checkTimeout;
                                mHSFaceEngine.hsfeInputVerifyData(data);
                                isFromCard = false;
                                isResetUiable = false;
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(this, mResources.getString(R.string.formate_invalide), Toast.LENGTH_SHORT).show();
            }
        }
        if (isResetUiable) {
            resetUiRunnable();
        }
    }

    private void initData() {
        // TODO Auto-generated method stub
        SharedPreferencesUtil.put(this, ConstantValues.getTypeFaceVerify(), getResources().getStringArray(R.array.array_verify_type)[1]);
        SharedPreferencesUtil.put(this, ConstantValues.getTypeCamera(), 1);
        // SharedPreferencesUtil.put(this, ConstantValues.getSwitchLight(),
        // true);
        SharedPreferencesUtil.put(this, ConstantValues.getSwitchFaceFrame(), true);
        SharedPreferencesUtil.put(this, ConstantValues.getSwitchTotalTime(), true);
//		SharedPreferencesUtil.put(this, ConstantValues.getSwitchFeatureTime(), true);
//		SharedPreferencesUtil.put(this, ConstantValues.getSwitchCheckTime(), true);
        SharedPreferencesUtil.put(this, ConstantValues.getSwitchFaceVerify(), true);
        // SharedPreferencesUtil.put(this, ConstantValues.getFrontFilter(), 0);
        // SharedPreferencesUtil.put(this, ConstantValues.getBackFilter(), 0);
        SharedPreferencesUtil.put(this, ConstantValues.getCheckTimeout(), 3);
        SharedPreferencesUtil.put(this, ConstantValues.getMinFace(), 80);
        SharedPreferencesUtil.put(this, ConstantValues.getSwitchChannel0(), true);
        SharedPreferencesUtil.put(this, ConstantValues.getChannel0Top(), 0.7F);
        SharedPreferencesUtil.put(this, ConstantValues.getChannel0Bottom(), 0.5F);
        SharedPreferencesUtil.put(this, ConstantValues.getChannel0N(), 0.7F);

//		SharedPreferencesUtil.put(this, ConstantValues.getDisplayOrientation(), 180);

        SharedPreferencesUtil.put(this, ConstantValues.getTypeFingerVerify(), getResources().getStringArray(R.array.array_verify_type)[0]);
        SharedPreferencesUtil.put(this, ConstantValues.getSwitchFingerVerify(), true);
        SharedPreferencesUtil.put(this, ConstantValues.getFingerThreshold(), 500);
        SharedPreferencesUtil.put(this, ConstantValues.getFingerQuality(), 50);
        SharedPreferencesUtil.put(this, ConstantValues.getSwitchAutoAdd(), true);
    }

    private void resetData() {
        HSFEThreshold threshold = new HSFEThreshold();
        threshold.topThreshold = (Float) SharedPreferencesUtil.get(this, ConstantValues.getChannel0Top(), 0.7F);
        threshold.bottomThreshold = (Float) SharedPreferencesUtil.get(this, ConstantValues.getChannel0Bottom(), 0.5F);
        threshold.topThreshold1vn = (Float) SharedPreferencesUtil.get(this, ConstantValues.getChannel0N(), 0.5F);
        if (threshold.topThreshold <= threshold.bottomThreshold) {
            SharedPreferencesUtil.put(this, ConstantValues.getChannel0Top(), 0.7F);
            SharedPreferencesUtil.put(this, ConstantValues.getChannel0Bottom(), 0.5F);
            resetData();
            Toast.makeText(this, mResources.getString(R.string.face_toast), Toast.LENGTH_SHORT).show();
        } else {
            mHSFaceEngine.hsfeSetThresold(threshold);
        }

        isDrawing = false;
        fingerThreshold = (Integer) SharedPreferencesUtil.get(MainActivity.this, ConstantValues.getFingerThreshold(), 0);
        updateUi(TYPE_FINGER_THRESHOLD, String.valueOf(fingerThreshold));
        // updateUi(TYPE_FINGER_VALUE, "0.8");
        String type = (String) SharedPreferencesUtil.get(this, ConstantValues.getTypeFaceVerify(), "");
        if (StringUtil.isEqual(type, getResources().getStringArray(R.array.array_verify_type)[0])) {
            updateUi(TYPE_FACE_THRESHOLD, String.valueOf((Float) SharedPreferencesUtil.get(this, ConstantValues.getChannel0Bottom(), 0.5F)));
        } else if (StringUtil.isEqual(type, getResources().getStringArray(R.array.array_verify_type)[1])) {
            updateUi(TYPE_FACE_THRESHOLD, String.valueOf((Float) SharedPreferencesUtil.get(this, ConstantValues.getChannel0N(), 0.5F)));
        }

        // updateUi(TYPE_FACE_VALUE, "0.8");
        // updateUi(TYPE_FACE_DETECT, "500ms");
        // updateUi(TYPE_CARD_READ, "500ms");
        // updateUi(TYPE_FEATURE_EXTRACT, "500ms");
        // updateUi(TYPE_VERSION, "V3.0.0.184-831LAA");
        // String name = "王文明";
        // updateUi(TYPE_CARD_NAME, StringUtil.addStars(name, 1,
        // name.length()));
        // updateUi(TYPE_CARD_SEX, "男");
        // updateUi(TYPE_CARD_NATION, "汉族");
        // updateUi(TYPE_CARD_BIRTHDAY, "1999年09月30日");
        // String id = "13028319990930398X";
        // updateUi(TYPE_CARD_ID, StringUtil.addStars(id, 3, id.length() - 4));
        isTotalTimeShow = (Boolean) SharedPreferencesUtil.get(this, ConstantValues.getSwitchTotalTime(), false);
        if (isTotalTimeShow) {
//			cardRedTv.setVisibility(View.VISIBLE);
            detectTotalTv.setVisibility(View.VISIBLE);
        } else {
//			cardRedTv.setVisibility(View.INVISIBLE);
            detectTotalTv.setVisibility(View.INVISIBLE);
        }
        isDetailTimeShow = (Boolean) SharedPreferencesUtil.get(this, ConstantValues.getSwitchDetailTime(), false);
        if (isDetailTimeShow) {
            detectTv.setVisibility(View.VISIBLE);
            cardRedTv.setVisibility(View.VISIBLE);
            featureExTv.setVisibility(View.VISIBLE);
        } else {
            detectTv.setVisibility(View.INVISIBLE);
            cardRedTv.setVisibility(View.INVISIBLE);
            featureExTv.setVisibility(View.INVISIBLE);
        }
//		if ((Boolean) SharedPreferencesUtil.get(this, ConstantValues.getSwitchCheckTime(), false)) {
//			detectTv.setVisibility(View.VISIBLE);
//		} else {
//			detectTv.setVisibility(View.INVISIBLE);
//		}
        updateUi(TYPE_VERSION, "3.1.0.1331-817");
        isFrameReverse = (Boolean) SharedPreferencesUtil.get(MainActivity.this, ConstantValues.getTypeFrame(), false);
        isFrameOpened = (Boolean) SharedPreferencesUtil.get(MainActivity.this, ConstantValues.getSwitchFaceFrame(), false);
        isFaceEnable = (Boolean) SharedPreferencesUtil.get(MainActivity.this, ConstantValues.getSwitchFaceVerify(), false);
        isFingerEnable = (Boolean) SharedPreferencesUtil.get(MainActivity.this, ConstantValues.getSwitchFingerVerify(), false);
        fingerQuality = (Integer) SharedPreferencesUtil.get(MainActivity.this, ConstantValues.getFingerQuality(), 0);
        displayOrientation = (Integer) SharedPreferencesUtil.get(ValueUtil.getInstance().getmActivity(), ConstantValues.getDisplayOrientation(), 0);
        isAutoAddable = (Boolean) SharedPreferencesUtil.get(this, ConstantValues.getSwitchAutoAdd(), false);
        checkTimeout = 1000 * (Integer) SharedPreferencesUtil.get(MainActivity.this, ConstantValues.getCheckTimeout(), 3);
    }

    private void startPlay(int id) {
        if (previewSfv.getVisibility() == View.GONE) {
            return;
        }
        if (null != mMediaPlayer) {
            return;
//			mMediaPlayer.release();
//			mMediaPlayer = null;
        }

        mMediaPlayer = MediaPlayer.create(MainActivity.this, id);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.start();
        mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                mp.reset();
                mp.release();
                mMediaPlayer = null;
            }
        });
        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                mp.reset();
                mp.release();
                mMediaPlayer = null;
                return false;
            }
        });
    }

    private void setTv(TextView tv, String txt) {
        // TODO Auto-generated method stub
        if (null != tv && null != txt) {
            String text = tv.getText().toString();
            if (StringUtil.isNotNull(text) && text.contains("：")) {
                tv.setText(text.substring(0, text.indexOf("：") + 1) + txt);
            }
        }
    }

    private void updateUi(int type, String txt) {
        // TODO Auto-generated method stub
        switch (type) {
            case TYPE_FINGER_PIC:
                break;
            case TYPE_FINGER_THRESHOLD:
                setTv(fingerThdTv, txt);
                break;
            case TYPE_FINGER_VALUE:
                setTv(fingerVerTv, txt);
                break;
            case TYPE_FINGER_RESULT:
                if (null != txt && "".equals(txt)) {
                    fingerRetTv.setText("");
                } else if (Boolean.valueOf(txt)) {
                    fingerRetTv.setTextColor(Color.parseColor("#29db00"));
                    fingerRetTv.setText(mResources.getString(R.string.face_pass));
                    startPlay(R.raw.success);
                } else {
                    fingerRetTv.setTextColor(Color.parseColor("#b31e01"));
                    fingerRetTv.setText(mResources.getString(R.string.face_not_pass));
                    startPlay(R.raw.failed);
                }
                break;
            case TYPE_FACE_PIC:
                break;
            case TYPE_FACE_THRESHOLD:
                setTv(faceThdTv, txt);
                break;
            case TYPE_FACE_VALUE:
                setTv(faceVerTv, txt);
                break;
            case TYPE_FACE_RESULT:
                if (null != txt && "".equals(txt)) {
                    faceRetTv.setText("");
                } else if (Boolean.valueOf(txt)) {
                    faceRetTv.setTextColor(Color.parseColor("#29db00"));
                    faceRetTv.setText(mResources.getString(R.string.face_pass));
                    startPlay(R.raw.success);
                } else {
                    faceRetTv.setTextColor(Color.parseColor("#b31e01"));
                    faceRetTv.setText(mResources.getString(R.string.face_not_pass));
                    startPlay(R.raw.failed);
                }
                break;
            case TYPE_CARD_PIC:
                break;
            case TYPE_CARD_NAME:
                setTv(nameTv, txt);
                break;
            case TYPE_CARD_SEX:
                setTv(sexTv, txt);
                break;
            case TYPE_CARD_NATION:
                setTv(nationTv, txt);
                break;
            case TYPE_CARD_BIRTHDAY:
                setTv(birthdayTv, txt);
                break;
            case TYPE_CARD_ID:
                setTv(idTv, txt);
                break;
            case TYPE_FACE_DETECT:
                setTv(detectTv, txt);
                break;
            case TYPE_FACE_DETECT_TOTAL:
                setTv(detectTotalTv, txt);
                break;
            case TYPE_CARD_READ:
                setTv(cardRedTv, txt);
                break;
            case TYPE_FEATURE_EXTRACT:
                setTv(featureExTv, txt);
                break;
            case TYPE_VERSION:
                setTv(versionTv, txt);
                break;
            case TYPE_FP:
                setTv(fpTv, txt);
                break;
            default:
                break;
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null != mPopupWindow && mPopupWindow.isShowing()) {
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                if (event.getX() > startX) { // 向右滑动
                    if (isToRightable) {
                        animationLeft2Right();
                    }

                } else if (event.getX() < startX) { // 向左滑动
                    if (isToLeftable) {
                        Animation animation = AnimationUtils.loadAnimation(this, R.anim.right_to_left);
                        animation.setAnimationListener(new AnimationListener() {

                            @Override
                            public void onAnimationStart(Animation arg0) {
                                // TODO Auto-generated method stub
                            }

                            @Override
                            public void onAnimationRepeat(Animation arg0) {
                                // TODO Auto-generated method stub
                            }

                            @Override
                            public void onAnimationEnd(Animation arg0) {
                                isToRightable = true;
                                isToLeftable = false;
                                if (null != mTimer) {
                                    mTimer.cancel();
                                    mTimer = new Timer();
                                    mTimer.schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                            MainActivity.this.runOnUiThread(new Runnable() {

                                                @Override
                                                public void run() {
                                                    // TODO Auto-generated method
                                                    // stub
                                                    animationLeft2Right();
                                                }
                                            });
                                        }
                                    }, 3000);
                                }
                            }
                        });
                        setRl.setVisibility(View.VISIBLE);
                        setRl.startAnimation(animation);
                    }
                }

                break;
        }
        return super.onTouchEvent(event);
    }

    private void animationLeft2Right() {
        Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.left_to_right);
        animation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                isToRightable = false;
                isToLeftable = true;
                if (null != mTimer) {
                    mTimer.cancel();
                }
                setRl.setVisibility(View.GONE);
            }
        });
        setRl.startAnimation(animation);
    }

    private void showIv(String path, ImageView iv, int scalingSize) {
        // TODO Auto-generated method stub
        try {
            FileInputStream fis = new FileInputStream(path);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = scalingSize;
            Bitmap bm = BitmapFactory.decodeStream(fis, null, options);
            if (null != bm) {
                iv.setImageBitmap(bm);
            }
            fis.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void showIv(HSFEVerifyResult result, ImageView iv, int scalingSize) {
        // TODO Auto-generated method stub
        try {
            long startCutTime = System.currentTimeMillis();
            Bitmap bm = null;
            byte[] rgb = new byte[ValueUtil.getInstance().getWidth() * ValueUtil.getInstance().getHeight() * 4];// 2ms
            if (0 == displayOrientation || 180 == displayOrientation) {
                USBCamCtrl.UVCYuvtoRgb(ValueUtil.getInstance().getWidth(), ValueUtil.getInstance().getHeight(), result.image.imageBuf, rgb);// 40ms
                ByteBuffer buffer = ByteBuffer.wrap(rgb);// 0ms
                bm = Bitmap.createBitmap(ValueUtil.getInstance().getWidth(), ValueUtil.getInstance().getHeight(), Config.ARGB_8888);// 0ms
                bm.copyPixelsFromBuffer(buffer);// 10ms
            } else if (90 == displayOrientation || 270 == displayOrientation) {
//				FileUtil.getInstance().saveImageByByteAndPath(buf, "/sdcard/12.raw");
                USBCamCtrl.UVCYuvtoRgb(ValueUtil.getInstance().getWidth(), ValueUtil.getInstance().getHeight(), result.image.imageBuf, rgb);// 40ms
                ByteBuffer buffer = ByteBuffer.wrap(rgb);// 0ms
                bm = Bitmap.createBitmap(ValueUtil.getInstance().getWidth(), ValueUtil.getInstance().getHeight(), Config.ARGB_8888);// 0ms
                bm.copyPixelsFromBuffer(buffer);// 10ms
            }

            // InputStream ins = new ByteArrayInputStream(rgb);
            // Bitmap bm = BitmapFactory.decodeStream(ins);
            // ins.close();
            // ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // Rect rectTmp = new Rect(0, 0, 640, 480);
            // YuvImage yuv = new YuvImage(buf, ImageFormat.NV21, 640, 480,
            // null);
            // if (null != yuv) {
            // yuv.compressToJpeg(rectTmp, 100, baos);
            // }
            // Bitmap bm = BitmapFactory.decodeByteArray(baos.toByteArray(), 0,
            // baos.size());
            // baos.close();
            // Bitmap bm = FileUtil.getInstance().yuv2Bitmap(buf, 640, 480);
            LogUtil.getInstance().i(TAG, "yuv2Bitmap time = " + String.valueOf(System.currentTimeMillis() - startCutTime));
            if (null != bm && !bm.isRecycled()) {
                Bitmap bmTmp = bm;
                if (null != result.faceRect && 0 < result.faceRect.length && 0 < result.faceRect[0].right - result.faceRect[0].left
                        && 0 < result.faceRect[0].bottom - result.faceRect[0].top) {
                    bmTmp = FileUtil.getInstance().getCutImage(bm, result.faceRect[0]);
                }
                if (isFace1V1) {
                    resultBm = bmTmp;
                    boolean isAddable = true;
                    int personId = mFeatureUtil.getPersonIdByNameAndCard(mName, mCardId);
                    if (ValueUtil.getInstance().getFpScoreMap().containsKey(mCardId) && 0 <= personId) {
                        float score = ValueUtil.getInstance().getFpScoreMap().get(mCardId);
                        if (result.score <= score) {
                            isAddable = false;
                        }
                    }
                    if (isAddable && isFromCard && result.pass && isAutoAddable) {
                        if (0 <= personId) {
                            mFeatureUtil.deleteFeatureById(personId);
                            String name = mCardId + "_" + mName;
                            String dir = ValueUtil.getInstance().getFACE_PATH() + File.separator + name;
                            FileUtil.getInstance().delFile(dir);
                        }
                        autoAdd(bmTmp);
                        ValueUtil.getInstance().getFpScoreMap().put(mCardId, result.score);
                    }
                }
                if (null != bmTmp && !bmTmp.isRecycled()) {
                    iv.setImageBitmap(bmTmp);
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onDetectResult(final THIDFaceRect[] faceRect, final int faceNum) {
        LogUtil.getInstance().i(TAG, "onDetectResult 805");
        if (!isDrawing && rectSfv.isShown() && isFrameOpened) {
            long time = System.currentTimeMillis();
            isDrawing = true;
            mCanvas = mSurfaceHolder.lockCanvas();
            if (null != mCanvas) {
                mCanvas.drawPaint(clearPaint);
                float xRatioW = (float) mCanvas.getWidth() / ValueUtil.getInstance().getWidth();
                float xRatioH = (float) mCanvas.getHeight() / ValueUtil.getInstance().getHeight();
                if (null != faceRect && faceNum > 0) {
                    for (int i = 0; i < faceNum; i++) {
                        THIDFaceRect rect = faceRect[i];
                        if (null != rect) {
                            if (isFrameReverse) {
                                mCanvas.drawRect((ValueUtil.getInstance().getWidth() - rect.left) * xRatioW, rect.top * xRatioH,
                                        (ValueUtil.getInstance().getWidth() - rect.right) * xRatioW, rect.bottom * xRatioH, mPaint);
                            } else {
                                mCanvas.drawRect(rect.left * xRatioW, rect.top * xRatioH, rect.right * xRatioW, rect.bottom * xRatioH, mPaint);
                            }
                        }
                    }
                }

                mSurfaceHolder.unlockCanvasAndPost(mCanvas);
                // TODO Auto-generated method stub
                LogUtil.getInstance().i(TAG, "onDetectResult consume = " + String.valueOf(System.currentTimeMillis() - time));
            }
            isDrawing = false;
        }
    }

    @Override
    public void onVerify(HSFEVerifyResult result) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onVerifyResult(final HSFEVerifyResult result) {
        // TODO Auto-generated method stub
        MainActivity.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
//				if (null != verTimer) {
//					isVerTimerRuning = false;
//					verTimer.cancel();
//					verTimer = null;
//				}
//				verTimer = new Timer();
//				verTimer.schedule(new TimerTask() {
//
//					@Override
//					public void run() {
//						// TODO Auto-generated method stub
//						MainActivity.this.runOnUiThread(new Runnable() {
//
//							@Override
//							public void run() {
//								if (isVerTimerRuning) {
//									faceTv.setVisibility(View.VISIBLE);
//									faceIv.setImageResource(R.drawable.main_iv_bg);
//									updateUi(TYPE_FACE_VALUE, "");
//									updateUi(TYPE_FACE_RESULT, "");
//									// updateUi(TYPE_FACE_DETECT, "");
//									cardTv.setVisibility(View.VISIBLE);
//									cardIv.setImageResource(R.drawable.main_iv_bg);
//									updateUi(TYPE_CARD_NAME, "");
//									updateUi(TYPE_CARD_SEX, "");
//									updateUi(TYPE_CARD_NATION, "");
//									updateUi(TYPE_CARD_ID, "");
//									// updateUi(TYPE_CARD_READ, "");
//									// updateUi(TYPE_FEATURE_EXTRACT, "");
//									startVerTime = 0L;
//								}
//								if (null != verTimer) {
//									isVerTimerRuning = false;
//									verTimer.cancel();
//									verTimer = null;
//								}
//								isMatchShowing = false;
//							}
//						});
//						isVerifying = false;
//					}
//				}, 5000);
                faceIv.setImageResource(R.drawable.main_iv_bg);
                faceTv.setVisibility(View.GONE);
                long startCutTime = System.currentTimeMillis();
                if (null != result.image && null != result.image.imageBuf && 0 < result.image.imageBuf.length) {
                    showIv(result, faceIv, 1);
                    LogUtil.getInstance().i(TAG, "793 onVerifyResult faceRect : " + result.frameId + "," + result.faceRect[0].left + "," + result.faceRect[0].top + ","
                            + result.faceRect[0].right + "," + result.faceRect[0].bottom);
                }
                LogUtil.getInstance().i(TAG, "showIv time = " + String.valueOf(System.currentTimeMillis() - startCutTime));
                String score = String.valueOf(result.score);
                LogUtil.getInstance().i(TAG, "886 score = " + score);
                String scoreRet = "0";
                if (StringUtil.isNotNull(score) && score.contains(".")) {
                    int posDot = score.indexOf(".");
                    if (posDot > 0) {
                        if (score.length() - posDot - 1 > 3) {
                            int lastNum = 0;
                            if (score.length() - posDot - 1 > 4) {
                                lastNum = Integer.valueOf(score.substring(5, 6));
                            } else if (score.length() - posDot - 1 == 4) {
                                lastNum = Integer.valueOf(score.substring(4));
                            }
                            if (lastNum >= 5) {
                                Float tmp = Float.parseFloat(score) + 0.001F;
                                score = String.valueOf(tmp);
                            }
                            scoreRet = score.substring(0, 5);
                        }
                    }
                }
                LogUtil.getInstance().i(TAG, "886 score after = " + scoreRet);
                updateUi(TYPE_FACE_VALUE, scoreRet);
                updateUi(TYPE_FACE_RESULT, String.valueOf(result.pass));
                if (isDetailTimeShow) {
                    LogUtil.getInstance().i(TAG, "result.verifyTime = " + result.verifyTime);
                    updateUi(TYPE_FACE_DETECT, String.valueOf(result.verifyTime) + "ms");
//					updateUi(TYPE_FACE_DETECT_TOTAL, String.valueOf(result.verifyTime + (int) cardTime) + "ms");
                    startVerTime = 0L;
//					cardTime = 0L;
                }
                if (isTotalTimeShow) {
                    updateUi(TYPE_FACE_DETECT_TOTAL, String.valueOf(result.verifyTime + (int) cardTime) + "ms");
                    startVerTime = 0L;
                    cardTime = 0L;
                }

                resetUiRunnable();
//					LogUtil.getInstance().writeTxtToFile(TimeUtil.getDateFormat(3) + " verify time:" + String.valueOf(result.verifyTime) + "ms" + " result:" + scoreRet,
//							LogUtil.getInstance().getLOG_VERIFY_PATH());
                isFaceDoing = false;
            }
        });
    }

    @Override
    public void onExFeattime(final HSFEFeatTime time) {
        if (isDetailTimeShow) {
            MainActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    updateUi(TYPE_FEATURE_EXTRACT, String.valueOf(time.timems) + "ms");
//					LogUtil.getInstance().writeTxtToFile(TimeUtil.getDateFormat(3) + " exfeat time:" + String.valueOf(time.timems) + "ms",
//							LogUtil.getInstance().getLOG_FEAT_PATH());
                }
            });
            // TODO Auto-generated method stub
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceCreated(final SurfaceHolder holder) {
        // TODO Auto-generated method stub
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void matchResult(final HsfeMatchResult matchResult) {
        // TODO Auto-generated method stub
        LogUtil.getInstance().i(TAG, "1891 isMatchResultRejected = " + isMatchResultRejected + "  isFace1V1 = " + isFace1V1 + " matchResult = " + (null == matchResult));
        if (!isMatchResultRejected && !isFace1V1 && null != matchResult && isFaceEnable) {
            HSFEVerifyResult result = new HSFEVerifyResult();
            result.image = matchResult.image;
            result.faceRect = matchResult.faceRect;
            result.score = matchResult.score;
            result.pass = matchResult.pass;
            result.verifyTime = (int) matchResult.matchTime;
            LogUtil.getInstance().i(TAG, "1470 result.verifyTime = " + result.verifyTime);
            onVerifyResult(result);
            MainActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    List<PersonFeature> personList = ValueUtil.getInstance().getPersonList();
                    if (null != personList && 0 < personList.size()) {
                        for (int i = 0; i < personList.size(); i++) {
                            PersonFeature person = personList.get(i);
                            if (matchResult.id == person.getPersonCommon().getPersonId()) {
                                cardTv.setVisibility(View.GONE);
                                cardMuskIv.setVisibility(View.GONE);
//								Feature feature = person.getFeatureList().get(0);
                                showIv(person.getFeatureCommon().getPath(), cardIv, 1);
                                String nameRet = null;
                                String nameRetTmp = null;
                                String name = person.getPersonCommon().getName();
                                String cardId = person.getPersonCommon().getCardId();
                                if (StringUtil.isNotNull(name)) {
                                    if (StringUtil.isNotNull(cardId)) {
                                        nameRet = cardId + "_" + name;
                                    } else {
                                        nameRet = name;
                                    }
                                }
                                nameRetTmp = nameRet;
                                String nameTmp = FileUtil.getInstance().getNameByPath(person.getFeatureCommon().getPath());
//								String name = nameTmp.substring(0, nameTmp.lastIndexOf("."));
                                String ret = null;
                                if (StringUtil.isNotNull(nameRet)) {
                                    if (12 >= nameRet.length()) {
                                        ret = nameRet;
                                    } else {
                                        ret = StringUtil.addStars(nameRet, 9, nameRet.length() - 3);
                                    }
                                }
                                updateUi(TYPE_CARD_NAME, ret);

                                // 保存比中图片
								/*String featurePath = Environment.getExternalStorageDirectory().getPath() + File.separator + "HisignEngine" + File.separator + "Log" + File.separator
										+ nameRetTmp + File.separator + nameTmp;
								FileUtil.getInstance().copyFile(person.getFeatureCommon().getPath(), featurePath);
								final String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
								String frameName = timeStamp + "_" + matchResult.score + ".jpg";
								String framePath = Environment.getExternalStorageDirectory().getPath() + File.separator + "HisignEngine" + File.separator + "Log" + File.separator
										+ nameRetTmp + File.separator + frameName;
								Bitmap bm = null;
								byte[] rgb = new byte[ValueUtil.getInstance().getWidth() * ValueUtil.getInstance().getHeight() * 4];// 2ms
								int displayOrientation = (Integer) SharedPreferencesUtil.get(ValueUtil.getInstance().getmActivity(), ConstantValues.getDisplayOrientation(), 0);
								if (0 == displayOrientation || 180 == displayOrientation) {
									USBCamCtrl.UVCYuvtoRgb(ValueUtil.getInstance().getWidth(), ValueUtil.getInstance().getHeight(), matchResult.image.imageBuf, rgb);// 40ms
									ByteBuffer buffer = ByteBuffer.wrap(rgb);// 0ms
									bm = Bitmap.createBitmap(ValueUtil.getInstance().getWidth(), ValueUtil.getInstance().getHeight(), Config.ARGB_8888);// 0ms
									bm.copyPixelsFromBuffer(buffer);// 10ms
								} else if (90 == displayOrientation || 270 == displayOrientation) {
								//									FileUtil.getInstance().saveImageByByteAndPath(buf, "/sdcard/12.raw");
									USBCamCtrl.UVCYuvtoRgb(ValueUtil.getInstance().getWidth(), ValueUtil.getInstance().getHeight(), matchResult.image.imageBuf, rgb);// 40ms
									ByteBuffer buffer = ByteBuffer.wrap(rgb);// 0ms
									bm = Bitmap.createBitmap(ValueUtil.getInstance().getWidth(), ValueUtil.getInstance().getHeight(), Config.ARGB_8888);// 0ms
									bm.copyPixelsFromBuffer(buffer);// 10ms
								}
								// 保存现场比中图片
								if (StringUtil.isNotNull(framePath)) {
									FileUtil.getInstance().bitmapToJpeg(bm, framePath);
									framePath = null;
								}*/
                                break;
                            }
                        }
                    }
					/*for (int i = 0; i < ValueUtil.getInstance().getFaceLibList().size(); i++) {
						
						LibItemParam fp = ValueUtil.getInstance().getFaceLibList().get(i);
						if (matchResult.id == fp.getId()) {
							cardTv.setVisibility(View.GONE);
							showIv(fp.getPath(), cardIv, 1);
							String name = fp.getName().substring(0, fp.getName().lastIndexOf("."));
							String ret = null;
							if (StringUtil.isNotNull(name)) {
								if (12 >= name.length()) {
									ret = name;
								} else {
									ret = StringUtil.addStars(name, 9, name.length() - 3);
								}
							}
							updateUi(TYPE_CARD_NAME, ret);
							break;
						}
					}*/
                }
            });
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        // TODO Auto-generated method stub
        switch (msg.what) {
            case ConstantValues.ID_FEATURE_SUCCESS:
                Bundle bundle = msg.getData();
                if (null != bundle) {
                    int num = bundle.getInt("num");
                    if (0 < num) {
                        Toast.makeText(this, mResources.getString(R.string.add_success) + num + mResources.getString(R.string.add_success_face), Toast.LENGTH_SHORT).show();
                    }
                }
                ValueUtil.getInstance().setFaceFeaturesInited(true);
                if (ValueUtil.getInstance().isFpFeaturesInited() && !MainActivity.this.isFinishing()) {
                    mProgressDialog.dismiss();
                    isMatchResultRejected = false;
                }
                break;
            case ConstantValues.ID_FP_FEATURE_SUCCESS:
                startFpCollect();
                if (0 < msg.arg1) {
                    Toast.makeText(this, mResources.getString(R.string.add_success) + msg.arg1 + mResources.getString(R.string.add_success_fp), Toast.LENGTH_SHORT).show();
                }
                ValueUtil.getInstance().setFpFeaturesInited(true);
                if (ValueUtil.getInstance().isFaceFeaturesInited() && !MainActivity.this.isFinishing()) {
                    mProgressDialog.dismiss();
                    isMatchResultRejected = false;
                }
                break;
            case ConstantValues.ID_FEATURE_FAIL:
                Toast.makeText(this, mResources.getString(R.string.add_fail) + msg.obj, Toast.LENGTH_SHORT).show();
                break;
            case ConstantValues.SCAN_START:
                isMatchResultRejected = true;
                mProgressDialog.show();
                break;
            case ID_FP_IMG:
                boolean isFpVerifiable = false;
                Bitmap bp = null;
                FpCollectRet fpCollectRet = (FpCollectRet) msg.obj;
                if (null != fpCollectRet) {
                    bp = fpCollectRet.getRetBm();
                    if (null != bp && !bp.isRecycled()) {
                        if ((!isFace1V1 && !fpCollectRet.isFp1V1()) || (isFace1V1 && isFp1V1 && fpCollectRet.isFp1V1())) {
                            isFpVerifiable = true;
                        }
                    }
                }
                if (isFpVerifiable) {
                    FpVerifyRunnable fp = new FpVerifyRunnable(fpCollectRet);
                    new Thread(fp).start();
                    LogUtil.getInstance().i(TAG, "2092 isFp1V1 = " + isFp1V1);
                } else {
                    isFpDoing = false;
                    isFpVerifying = false;
                }
                break;
            case ID_FP_RESULT:
                FpMatchResult fpMatchResult = (FpMatchResult) msg.obj;
                if (null != fpMatchResult) {
                    com.hisign.AS60xSDK.entity.MatchResult matchResult = fpMatchResult.getMatchResult();
                    if (null != matchResult) {
                        if (((!isFace1V1 && !fpMatchResult.getFpCollectRet().isFp1V1()) || (isFace1V1 && isFp1V1 && fpMatchResult.getFpCollectRet().isFp1V1()))) {
                            LogUtil.getInstance().i(TAG, "2136 isFp1V1 = " + isFp1V1);
                            if (0 == matchResult.resultCode) {
                                boolean passed = matchResult.score >= fingerThreshold;
                                updateUi(TYPE_FINGER_VALUE, String.valueOf(matchResult.score));
                                updateUi(TYPE_FINGER_RESULT, String.valueOf(passed));
                                FingerPrint fp = indexMap.get(matchResult.index);
                                if (!isFace1V1 && passed && null != fp) {
                                    cardTv.setVisibility(View.GONE);
                                    cardMuskIv.setVisibility(View.VISIBLE);
                                    showIv(fp.getPath(), cardIv, 1);
                                    String nameRet = null;
                                    String name = fp.getName();
                                    if (StringUtil.isNotNull(name)) {
                                        if (12 >= name.length()) {
                                            nameRet = name;
                                        } else {
                                            nameRet = StringUtil.addStars(name, 9, name.length() - 3);
                                        }
                                    }
                                    updateUi(TYPE_CARD_NAME, nameRet);

                                    // 保存比中图片
                                    File featureFile = new File(fp.getPath());
                                    String featurePath = Environment.getExternalStorageDirectory().getPath() + File.separator + "HisignEngine" + File.separator + "FpLog"
                                            + File.separator + name + File.separator + featureFile.getName();
                                    FileUtil.getInstance().copyFile(fp.getPath(), featurePath);

                                    final String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                                    String frameName = timeStamp + "_" + matchResult.score + ".bmp";
                                    String framePath = Environment.getExternalStorageDirectory().getPath() + File.separator + "HisignEngine" + File.separator + "FpLog" + File.separator
                                            + name + File.separator + frameName;
                                    // 保存现场比中图片
                                    Bitmap bm = fpMatchResult.getFpCollectRet().getRetBm();
                                    if (null != bm && !bm.isRecycled()) {
//										SDKUtilty.SaveRawToBmp(FileUtil.getInstance().bitmap2Bytes(bm), framePath, true);
                                        FileUtil.getInstance().bitmapToJpeg(bm, framePath);
                                    }
                                }
                            } else {
                                Toast.makeText(this, mResources.getString(R.string.authorization_fp) + matchResult.resultCode + mResources.getString(R.string.authorization_fp_0),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
                isFpVerifying = false;
                resetFpUiRunnable();
                isFpDoing = false;
                break;
            case ID_FP_SHOW:
                FpCollectRet fpCollectRetShow = (FpCollectRet) msg.obj;
                if ((!isFace1V1 && !fpCollectRetShow.isFp1V1()) || (isFace1V1 && isFp1V1 && fpCollectRetShow.isFp1V1())) {
                    startPlay(R.raw.beep);

                    fingerTv.setVisibility(View.GONE);
                    fingerMuskIv.setVisibility(View.VISIBLE);
                    fingerIv.setImageBitmap(fpCollectRetShow.getRetBm());

                    updateUi(TYPE_FINGER_VALUE, "");
                    updateUi(TYPE_FINGER_RESULT, "");
                    if (!isFace1V1) {
                        cardTv.setVisibility(View.VISIBLE);
                        cardMuskIv.setVisibility(View.GONE);
                        cardIv.setImageResource(R.drawable.main_iv_bg);
                        updateUi(TYPE_CARD_NAME, "");
                        updateUi(TYPE_CARD_SEX, "");
                        updateUi(TYPE_CARD_NATION, "");
                        updateUi(TYPE_CARD_ID, "");
                    }

                    isFpResetUiRunning = false;
                    mHandler.removeCallbacks(resetFpUiRunnable);
                }
                break;
            default:
                break;
        }
        return false;

    }

    private void resetUiRunnable() {
        isResetUiRunning = false;
//		mHandler.removeCallbacksAndMessages(null);
        mHandler.removeCallbacks(resetUiRunnable);
        mHandler.postDelayed(resetUiRunnable, 5000);
        isResetUiRunning = true;
    }

    private void resetMatchResultRunnable() {
        isMatchResultRunning = false;
//		mHandler.removeCallbacksAndMessages(null);
        mHandler.removeCallbacks(matchResultRunnable);
        mHandler.postDelayed(matchResultRunnable, 5000);
        isMatchResultRunning = true;
    }

    private void resetFpUiRunnable() {
        isFpResetUiRunning = false;
        mHandler.removeCallbacks(resetFpUiRunnable);
        mHandler.postDelayed(resetFpUiRunnable, 5000);
        isFpResetUiRunning = true;
    }

    private byte[][] getFpFeatures() {
        // TODO Auto-generated method stub
        indexMap.clear();
        int count = 0;
        byte[][] fpFeatures = new byte[ValueUtil.getInstance().getFpMap().size()][];
        for (FingerPrint fp : ValueUtil.getInstance().getFpMap().values()) {
            indexMap.put(count, fp);
            fpFeatures[count++] = fp.getFeature();
        }
        return fpFeatures;
    }

    /**
     * 指纹采集 返回指纹数据
     *
     * @param timeout 采集超时时间  秒
     * @return
     */
    public Bitmap fingerCollection(int timeout) {
        Bitmap retBp = null;
//		int nRet = -1;
//		int len = 256 * 360;
//		byte[] FPImageRaw = new byte[len];
//		int[] imageLen = new int[2];
        long preTime = System.currentTimeMillis();
        boolean isStoped = false;
        while (!isStoped) {
            if ((System.currentTimeMillis() - preTime) > timeout * 1000 || (null != retBp && !retBp.isRecycled())) {
                isStoped = true;
                break;
            }
            retBp = mFPModuleClient.FCV_CollectionFpImage();
			/*if (null != retBp && !retBp.isRecycled()) {
			//				SDKUtilty.SaveRawToBmp(FileUtil.getInstance().bitmap2BytesNew(retBp), "/sdcard/fp_tmp.bmp", true);
			//				SDKUtilty.ReadBmpToRaw(FPImageRaw, "/sdcard/fp_tmp.bmp", true);
				FPImageRaw = FileUtil.getInstance().bitmap2BytesNew(retBp);
				if (null != FPImageRaw) {
					int retScore = AS60xIO.FCV_GetQualityScore(FPImageRaw);
					LogUtil.getInstance().i(TAG, "finger quality score:" + retScore);
					if (retScore >= 50) {
						break;
					} else {
						retBp = null;
					}
				} else {
					retBp = null;
				}
			} else {
				retBp = null;
			}*/

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

    private void autoAdd(final Bitmap bp) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (StringUtil.isNotNull(mName) && StringUtil.isNotNull(mCardId)) {
                    String name = mCardId + "_" + mName;
                    String dir = ValueUtil.getInstance().getFACE_PATH() + File.separator + name;
                    String path = dir + File.separator + name + "_" + 0 + ".jpg";
                    FileUtil.getInstance().bitmapToJpeg(bp, path);
                    mFeatureUtil.extractFeatureByPath(path, -1);
                }
            }
        }).start();
    }

    private void startFpCollect() {
        String type = (String) SharedPreferencesUtil.get(MainActivity.this, ConstantValues.getTypeFingerVerify(), "");
        if (isFingerEnable && StringUtil.isEqual(getResources().getStringArray(R.array.array_verify_type)[1], type)) {
            fpFeatures = getFpFeatures();
            isFpStoped = false;
            new Thread(fpRunnable).start();
        }
    }

    private String getFingerByCode(int code) {
        String ret = mResources.getString(R.string.card_fp_unknown);
        switch (code) {
            case 11:
                ret = mResources.getString(R.string.card_fp_right_0);
                break;
            case 12:
                ret = mResources.getString(R.string.card_fp_right_1);
                break;
            case 13:
                ret = mResources.getString(R.string.card_fp_right_2);
                break;
            case 14:
                ret = mResources.getString(R.string.card_fp_right_3);
                break;
            case 15:
                ret = mResources.getString(R.string.card_fp_right_4);
                break;
            case 16:
                ret = mResources.getString(R.string.card_fp_left_0);
                break;
            case 17:
                ret = mResources.getString(R.string.card_fp_left_1);
                break;
            case 18:
                ret = mResources.getString(R.string.card_fp_left_2);
                break;
            case 19:
                ret = mResources.getString(R.string.card_fp_left_3);
                break;
            case 20:
                ret = mResources.getString(R.string.card_fp_left_4);
                break;
            case 97:
                ret = mResources.getString(R.string.card_fp_right_unknown);
                break;
            case 98:
                ret = mResources.getString(R.string.card_fp_left_unknown);
                break;
            case 99:
                ret = mResources.getString(R.string.card_fp_other_unknown);
                break;
            default:
                break;
        }
        return ret;
    }
}
