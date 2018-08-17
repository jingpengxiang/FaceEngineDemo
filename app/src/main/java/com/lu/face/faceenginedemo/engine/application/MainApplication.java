package com.lu.face.faceenginedemo.engine.application;


import android.app.Application;
import android.content.res.Configuration;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.hisign.AS60xSDK.AS60xIO;
import com.hisign.face.match.engine.verify.HSFaceEngine;
import com.hisign.idCardClient.FPModuleClient;
import com.hisign.idCardClient.IDCardClient;
import com.lu.face.faceenginedemo.engine.models.HSFaceEngineInit;
import com.lu.face.faceenginedemo.engine.utils.LogUtil;

public class MainApplication extends Application {
    private final String TAG = "MainApplication";
    private HSFaceEngine mHSFaceEngine;
    private IDCardClient mIDCardClient;
    private FPModuleClient mFPModuleClient;
    private UsbManager usbManager = null;
    HSFaceEngineInit initRet = new HSFaceEngineInit();

    // 这里才是真正的入口点。
    @Override
    public void onCreate() { // 程序创建的时候执行
        super.onCreate();
        Log.i(TAG, "24 onCreate()");
//        CrashHandler.getInstance().init(getApplicationContext());
//		AS60xIO.FCV_OpenDeviceEx(this, 1);
//		AS60xIO.FCV_SetLicensePath(this, getExternalFilesDir("sdkdata").getAbsolutePath());
        mIDCardClient = new IDCardClient(this);
        mFPModuleClient = new FPModuleClient(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i(TAG, "31 onConfigurationChanged()");
    }

    @Override
    public void onLowMemory() {// 低内存的时候执行
        super.onLowMemory();
        Log.i(TAG, "37 onLowMemory()");
    }

    @Override
    public void onTerminate() {// 程序终止的时候执行
        Log.i(TAG, "42 onTerminate()");
        super.onTerminate();
        if (null != mHSFaceEngine) {
            mHSFaceEngine.hsfeDestory();
            mHSFaceEngine = null;
        }
    }

    @Override
    public void onTrimMemory(int level) {// 程序在内存清理的时候执行
        Log.i(TAG, "49 onTrimMemory()");
        super.onTrimMemory(level);
    }

    public HSFaceEngineInit getmHSFaceEngineInit() {

        if (null == mHSFaceEngine) {
            LogUtil.getInstance().i(TAG, "49 new HSFaceEngine()  !!!");
            initRet.setRet(Integer.MIN_VALUE);
            mHSFaceEngine = new HSFaceEngine();
            String sdkDataPath = getExternalFilesDir("sdkdata").getAbsolutePath();
            initRet.setRet(mHSFaceEngine.hsfeCreate(this, sdkDataPath, sdkDataPath + "/faceidsdk.lic"));
            AS60xIO.FCV_SetLicensePath(this, getExternalFilesDir("sdkdata").getAbsolutePath());
        } else if (initRet.getRet() != 0) {
            String sdkDataPath = getExternalFilesDir("sdkdata").getAbsolutePath();
            initRet.setRet(mHSFaceEngine.hsfeCreate(this, sdkDataPath, sdkDataPath + "/faceidsdk.lic"));
        }
        initRet.setmHSFaceEngine(mHSFaceEngine);
        return initRet;
    }

    public IDCardClient getmIDCardClient() {
        return mIDCardClient;
    }

    public void setmIDCardClient(IDCardClient mIDCardClient) {
        this.mIDCardClient = mIDCardClient;
    }

    public FPModuleClient getmFPModuleClient() {
        return mFPModuleClient;
    }

    public void setmFPModuleClient(FPModuleClient mFPModuleClient) {
        this.mFPModuleClient = mFPModuleClient;
    }
}
