//package com.lu.face.faceenginedemo.engine;
//
//import android.annotation.SuppressLint;
//import android.app.PendingIntent;
//import android.app.Service;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.hardware.usb.UsbDevice;
//import android.hardware.usb.UsbManager;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.IBinder;
//import android.support.annotation.Nullable;
//import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
//import android.support.v4.widget.DrawerLayout;
//import android.support.v4.widget.SwipeRefreshLayout;
//import android.util.Log;
//
//import com.hisign.fpmoduleservice.aidl.IDCardMsg;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.util.Date;
//
///**
// * 项目名称： FaceEngineDemo
// * 创建人： Jing
// * 创建时间： 2018/7/22  14:25
// * 修改备注：
// */
//public class IDCReadService extends Service {
//    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
//    private static final String IDC_SERVICESEND = "com.hisign.IDCReaderService.IDCardMsg";
//    private IDCardSDK IDCReader = null;
//    boolean SaveLog = false;
//    private int Sdkver = Build.VERSION.SDK_INT;
//    private final String TAG = "FPModuleService";
//    volatile int connectCount = 0;
//    String deviceName = null;
//    private boolean isCryptoed = false;
//    private boolean isUserChoosedPermission = false;
//    public boolean loopRun = false;
//    private UsbDevice mModelDevice;
//    private UsbDevice mSensorDevice = null;
//    private boolean mSensorInited = false;
//    private int mSensorType = 0;
//    private UsbManager mUsbManager = null;
//    private BroadcastReceiver mUsbReceiverPermission = new BroadcastReceiver() {
//        public void onReceive(Context mContext, Intent intent) {
//            String action = intent.getAction();
//            UsbDevice usbDevice;
//            if ("android.hardware.usb.action.USB_DEVICE_DETACHED".equals(action)) {
//                usbDevice = (UsbDevice) intent.getParcelableExtra("device");
//                if (usbDevice.getVendorId() == AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT && usbDevice.getProductId() == 50010) {
//                    Log.e("FPModuleService", "\u8bbe\u5907\u6a21\u5757\u5df2\u79fb\u9664");
//                    try {
//                        finalize();
//                    } catch (Throwable e) {
//                        e.printStackTrace();
//                    }
//                    System.exit(0);
//                }
//            } else if ("android.hardware.usb.action.USB_DEVICE_ATTACHED".equals(action)) {
//                usbDevice = (UsbDevice) intent.getParcelableExtra("device");
//                if (usbDevice.getVendorId() == AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT && usbDevice.getProductId() == 50010) {
//                    InitUsbDevice(mContext);
//                    IDCReadService.this.IDCReader = new IDCardSDK(IDCReadService.this, IDCReadService.this.deviceName);
//                }
//            }
//        }
//    };
//    Thread readFPModuleLoop = null;
//    private int serviceType;
//    private Thread thread2;
//    Thread timer1 = null;
//    private boolean usbReceiverRegisted = false;
//
//    public static class MyStartIDCReaderService extends BroadcastReceiver {
//        public void onReceive(Context context, Intent intent) {
//            Intent it;
//            if (intent.getBooleanExtra("StratIDCardReceiver", false)) {
//                it = new Intent();
//                it.setClass(context, IDCReadService.class);
//                it.putExtra("StratIDCardReceiver", true);
//                String device = intent.getStringExtra("device");
//                if (device != null) {
//                    it.putExtra("device", device);
//                }
//                context.startService(it);
//            } else if (intent.getBooleanExtra("StopIDCardReceiver", false)) {
//                it = new Intent();
//                it.setClass(context, IDCReadService.class);
//                it.putExtra("StopIDCardReceiver", true);
//                context.startService(it);
//            } else if (intent.getBooleanExtra("StartService", false)) {
//                it = new Intent();
//                it.setClass(context, IDCReadService.class);
//                context.startService(it);
//            }
//        }
//    }
//
//    public class threadLoop extends Thread {
//        FileOutputStream Logout = null;
//        private IDCardMsg mIDCard;
//
//        public threadLoop() {
//            if (IDCReadService.this.SaveLog) {
//                try {
//                    this.Logout = new FileOutputStream(new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().getPath())).append("/logout.txt").toString(), true);
//                } catch (FileNotFoundException e1) {
//                    e1.printStackTrace();
//                }
//            }
//        }
//
//        void writeLog(String log) {
//            if (this.Logout != null && IDCReadService.this.SaveLog) {
//                try {
//                    this.Logout.write(log.getBytes());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        public void run() {
//            int findcount = 0;
//            int readcount = 0;
//            int removecount = 0;
//            int readerrorcount = 0;
//            if (IDCReader.isDeviceConnect()) {
//                while (loopRun) {
//                    IDCReadService.this.broadcastConnect();
//                    mIDCard = IDCReader.FCV_ReadIDCard((byte) 1);
//                    String log;
//                    if (mIDCard != null && mIDCard.resultCode == -126) {
//                        IDCardMsg IDCard = new IDCardMsg();
//                        findcount++;
//                        IDCReadService.this.broadcastIDCardMsg(this.mIDCard, 1);
//                        long findTime = new Date().getTime();
//                        IDCard = IDCReader.FCV_OnlyReadIDCard(IDCReadService.this);
//                        if (IDCard == null || IDCard.resultCode != 0) {
//                            if (IDCard != null) {
//                                if (IDCard.resultCode == -2) {
//                                    IDCReadService.this.broadcastIDCardMsg(IDCard, 3);
//                                }
//                            }
//                            readerrorcount++;
//                            IDCReadService.this.broadcastIDCardMsg(IDCard, 3);
//                        } else {
//                            readcount++;
//                            IDCard.tFoundTime = this.mIDCard.tFoundTime;
//                            IDCReadService.this.broadcastIDCardMsg(IDCard, 0);
//                            removecount++;
//                            IDCReadService.this.broadcastIDCardMsg(IDCard, 2);
//                        }
//                    } else if (this.mIDCard == null || this.mIDCard.resultCode != 0) {
//                        if (this.mIDCard != null && this.mIDCard.resultCode == -2) {
//                            break;
//                        }
//                    } else {
//                        readcount++;
//                        IDCReadService.this.broadcastIDCardMsg(this.mIDCard, 0);
//                    }
//                }
//                IDCReadService.this.loopRun = false;
//                try {
//                    if (IDCReadService.this.SaveLog && this.Logout != null) {
//                        this.Logout.close();
//                        return;
//                    }
//                    return;
//                } catch (IOException e2) {
//                    e2.printStackTrace();
//                    return;
//                }
//            }
//            IDCReadService.this.loopRun = false;
//        }
//    }
//
//    private class timerCount extends Thread {
//        private timerCount() {
//        }
//
//        public void run() {
//            while (loopRun) {
//                try {
//                    sleep(200);
//                    IDCReadService iDCReadService = IDCReadService.this;
//                    iDCReadService.connectCount++;
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    public void onCreate() {
//        if (this.deviceName == null) {
//            this.deviceName = initDeviceName();
//        }
//        Log.d("FPModuleService", "deviceName = " + this.deviceName);
//        this.IDCReader = new IDCardSDK(this, this.deviceName);
//        registerUSBpermisson(this);
//    }
//
//    @SuppressLint("WrongConstant")
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        if (intent.getBooleanExtra("StratIDCardReceiver", false)) {
//            Log.d("FPModuleService", "recive StratIDCardReceiver");
//            startLoopRun();
////            intent.setAction("android.hardware.usb.action.USB_DEVICE_ATTACHED");
////            sendBroadcast(intent);
//        } else if (intent.getBooleanExtra("StopIDCardReceiver", false)) {
//            Log.d("FPModuleService", "recive StopIDCardReceiver");
//            try {
//                stopLoopRun();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        return 3;
//    }
//
//    void startLoopRun() {
//        if (!loopRun) {
//            this.loopRun = true;
//            this.timer1 = new timerCount();
//            this.timer1.start();
//            this.readFPModuleLoop = new threadLoop();
//            this.readFPModuleLoop.start();
//            if (!recIDCardCmd.istart) {
//                recIDCardCmd.istart = true;
//                this.thread2 = new recIDCardCmd("/dev/ttyS1");
//                this.thread2.start();
//            }
//        }
//    }
//
//    void stopLoopRun() throws InterruptedException {
//        if (this.loopRun) {
//            this.loopRun = false;
//            this.timer1.join();
//            this.readFPModuleLoop.join();
//            if (recIDCardCmd.istart) {
//                recIDCardCmd.istart = false;
//                this.thread2.join();
//            }
//        }
//    }
//
//    public static String initDeviceName() {
//        String name = "/dev/ttyS1";
//        try {
//            File fl = new File(new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().getPath())).append("/IDCardService/DeviceName.txt").toString());
//            if (!fl.exists()) {
//                return name;
//            }
//            FileInputStream fin = new FileInputStream(fl);
//            if (fin.available() <= 0) {
//                return name;
//            }
//            byte[] data = new byte[fin.available()];
//            fin.read(data);
//            return new String(data);
//        } catch (IOException e) {
//            e.printStackTrace();
//            return name;
//        }
//    }
//
//    public static int setDeviceName(String deviceName) {
//        String filename = new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().getPath())).append("/IDCardService/").toString();
//        try {
//            File dir = new File(filename);
//            if (!dir.exists() && !dir.mkdir()) {
//                return -1;
//            }
//            File fl = new File(new StringBuilder(String.valueOf(filename)).append("DeviceName.txt").toString());
//            if (!fl.exists() && !fl.createNewFile()) {
//                return -1;
//            }
//            if (fl.exists()) {
//                FileOutputStream fout = new FileOutputStream(fl);
//                fout.write(deviceName.getBytes());
//                fout.close();
//            }
//            return 0;
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.e("IDCReadService", "create DeviceName.txt false !");
//            return -1;
//        }
//    }
//
//    void broadcastConnect() {
//        if (this.connectCount > 5) {
//            this.connectCount = 0;
//            IDCardMsg msg = new IDCardMsg();
//            msg.resultCode = 100;
//            broadcastIDCardMsg(msg, 100);
//            Log.d("FPModuleService", "send connect broadcast ");
//        }
//    }
//
//    private void broadcastIDCardMsg(IDCardMsg msg, int type) {
//        msg.resultCode = type;
//        switch (type) {
//            case SwipeRefreshLayout.LARGE /*0*/:
//                msg.tReadTime = new Date().getTime();
//                break;
//            case SwipeRefreshLayout.DEFAULT /*1*/:
//                msg.tFoundTime = new Date().getTime();
//                break;
//            case DrawerLayout.STATE_SETTLING /*2*/:
//                msg.tMovedTime = new Date().getTime();
//                break;
//        }
//        Bundle bundle = new Bundle();
//        bundle.putParcelable("IDCardMsg", msg);
//        Intent itIDC = new Intent(IDC_SERVICESEND);
//        itIDC.putExtras(bundle);
//        sendBroadcast(itIDC);
//        this.connectCount = 0;
//    }
//
//    private boolean InitUsbDevice(Context mContext) {
//        UsbManager mUsbManager = (UsbManager) mContext.getSystemService(USB_SERVICE);
//        if (!(this.mModelDevice == null || mUsbManager.hasPermission(this.mModelDevice))) {
//            mUsbManager.requestPermission(this.mModelDevice, PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), 0));
//            int timeOutCount = 0;
//            while (!this.isUserChoosedPermission && timeOutCount < 10) {
//                try {
//                    Thread.sleep(500);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                timeOutCount++;
//            }
//            this.isUserChoosedPermission = false;
//        }
//        return true;
//    }
//
//    private void registerUSBpermisson(Context mContext) {
//        if (!this.usbReceiverRegisted) {
//            this.usbReceiverRegisted = true;
//            IntentFilter filter = new IntentFilter();
//            filter.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");
//            filter.addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED");
//            mContext.registerReceiver(this.mUsbReceiverPermission, filter);
//        }
//    }
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    public void onDestroy() {
//        if (this.usbReceiverRegisted) {
//            this.usbReceiverRegisted = false;
//            unregisterReceiver(this.mUsbReceiverPermission);
//        }
////        try {
////            stopLoopRun();
////        } catch (InterruptedException e) {
////            e.printStackTrace();
////        }
//        super.onDestroy();
//    }
//}
