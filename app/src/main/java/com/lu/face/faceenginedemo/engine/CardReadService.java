package com.lu.face.faceenginedemo.engine;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.common.pos.api.util.posutil.PosUtil;
import com.sdt.Common;
import com.sdt.Sdtapi;
import com.sdt.UsbWelIDUtil;

import java.util.Timer;

/**
 * 项目名称： FaceEngineDemo
 * 创建人： Jing
 * 创建时间： 2018/7/28  14:50
 * 修改备注：
 */
public class CardReadService extends Service {
    private Common common; // common对象，存储一些需要的参数
    UsbWelIDUtil mUtil;
    private Sdtapi sdta;
    boolean findloop = false;
    Readloop readloop;
    private Timer idTimer;
    int times_count = 0;
    int success_count = 0;
    private boolean usbReceiverRegisted = false;
    /* 民族列表 */
    String[] nation = {"汉", "蒙古", "回", "藏", "维吾尔", "苗", "彝", "壮", "布依", "朝鲜", "满", "侗", "瑶", "白", "土家", "哈尼", "哈萨克", "傣", "黎", "傈僳", "佤", "畲", "高山", "拉祜", "水", "东乡", "纳西", "景颇", "克尔克孜", "土", "达斡尔", "仫佬", "羌", "布朗", "撒拉", "毛南", "仡佬", "锡伯", "阿昌", "普米", "塔吉克", "怒", "乌兹别克", "俄罗斯", "鄂温克", "德昂", "保安", "裕固", "京", "塔塔尔", "独龙", "鄂伦春", "赫哲", "门巴", "珞巴", "基诺"};
    // 广播接收器
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i("CardReadservice", "==========收到广播：========" + action);
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                mUtil = new UsbWelIDUtil(context);
                UsbDevice usbDevice = mUtil.getUsbWelDevice();
                mUtil.requestPermission(usbDevice, callback);

            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {// USB设备拔出广播
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                String deviceName = device.getDeviceName();
                if (device != null && device.equals(deviceName)) {

                    Log.i("CardReadservice", "USB设备拔出，应用程序即将关闭。");

                }

            } else if (common.ACTION_USB_PERMISSION.equals(action)) {
                mUtil = new UsbWelIDUtil(context);
                UsbDevice usbDevice = mUtil.getUsbWelDevice();
                UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
                if (usbDevice != null) {
                    if (usbManager.hasPermission(usbDevice)) {

                        try {
                            if (sdta == null) {
                                sdta = new Sdtapi(context, usbDevice);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            if (sdta == null) {
                                Log.i("CardReadservice", "模块连接失败");
                            } else {
                                Log.i("CardReadservice", "模块连接成功");
                            }
                        }
                    } else {
                        mUtil.requestPermission(usbDevice, callback);
                    }
                    startLoopRun();
                }
            }

        }
    };

    UsbWelIDUtil.OnUsbPermissionCallback callback = new UsbWelIDUtil.OnUsbPermissionCallback() {

        @Override
        public void onUsbPermissionEvent(UsbDevice dev, boolean granted) {
            if (granted) {
                try {
                    if (sdta == null) {
                        sdta = new Sdtapi(getApplication(), dev);
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    if (sdta == null) {
                        Log.i("CardReadservice", "模块连接失败");
                    } else {
                        Log.i("CardReadservice", "模块连接成功");
                    }
                }
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        common = new Common();
        registerUSBpermisson(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if (intent.getBooleanExtra("StratIDCardReceiver", false)) {
                try {
                    if (android.os.Build.MODEL.equals("TPS480")) {
                        PosUtil.setIdCardPower(PosUtil.IDCARD_POWER_ON);// 身份证上电
                    } else {
                        PosUtil.setFingerPrintPower(PosUtil.FINGERPRINT_POWER_ON);// 身份证/指纹仪上电
                    }
                    sendBroadcast(new Intent(common.ACTION_USB_PERMISSION));
                    Thread.sleep(1000);

//                startLoopRun();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (sdta == null) {
                        // Toast.makeText(this, "模块连接失败", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.i("CardReadService", "模块连接成功");
                    }
                }
            } else if (intent.getBooleanExtra("StopIDCardReceiver", false)) {
                stopTimer();
                stopThread();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void stopTimer() {
        if (idTimer != null) {
            idTimer.cancel();
            idTimer = null;
        }
    }

    private void stopThread() {
        if (readloop != null && readloop.isAlive()) {
            readloop.interrupt();
            findloop = false;
            readloop = null;
        }

    }

    void startLoopRun() {
        if (readloop == null) {
            readloop = new Readloop();
            findloop = true;
            readloop.start();
        }
    }

    /**
     * 线程循环读取
     */
    class Readloop extends Thread {
        @Override
        public void run() {
            while (findloop) {
                times_count++;
                int ret = sdta.SDT_StartFindIDCard();
                if (ret == 0x9f) {
                    ret = sdta.SDT_SelectIDCard();
                    if (ret == 0x90) {
                        IdCardMsg cardmsg = new IdCardMsg();
                        ret = ReadBaseMsgToStr(cardmsg);
                        if (ret == 0x90) {//读卡成功
                            success_count++;
//                            Message mMessage = new Message();
//                            mMessage.what = 3;
//                            mMessage.obj = cardmsg;
                            Intent intent = new Intent("com.lu.face.faceenginedemo.engine.MainActivity");
                            intent.putExtra("cardMsg", cardmsg);
                            sendBroadcast(intent);
//                            beepManager.playBeepSoundAndVibrate();// 蜂鸣器发声

//                            mHandler.sendMessage(mMessage);
                        } else {//失败
//                            mHandler.sendEmptyMessage(2);
                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        }
    }

    /**
     * 读取身份证中的文字信息（可阅读格式的）
     *
     * @param msg 用于保存身份证数据
     * @return
     */
    public int ReadBaseMsgToStr(IdCardMsg msg) {
        int ret;
        int[] puiCHMsgLen = new int[1];
        int[] puiPHMsgLen = new int[1];
        byte[] pucCHMsg = new byte[256];
        byte[] pucPHMsg = new byte[1024];
        // sdtapi中标准接口，输出字节格式的信息。
        ret = sdta.SDT_ReadBaseMsg(pucCHMsg, puiCHMsgLen, pucPHMsg, puiPHMsgLen);
        if (ret == 0x90) {
            try {
                char[] pucCHMsgStr = new char[128];
                DecodeByte(pucCHMsg, pucCHMsgStr);// 将读取的身份证中的信息字节，解码成可阅读的文字
                PareseItem(pucCHMsgStr, msg); // 将信息解析到msg中
                msg.photo = pucPHMsg;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        return ret;

    }

    /**
     * 分段信息提取
     *
     * @param pucCHMsgStr
     * @param msg
     */
    void PareseItem(char[] pucCHMsgStr, IdCardMsg msg) {
        msg.name = String.copyValueOf(pucCHMsgStr, 0, 15);
        String sex_code = String.copyValueOf(pucCHMsgStr, 15, 1);

        if (sex_code.equals("1"))
            msg.sex = "男";
        else if (sex_code.equals("2"))
            msg.sex = "女";
        else if (sex_code.equals("0"))
            msg.sex = "未知";
        else if (sex_code.equals("9"))
            msg.sex = "未说明";

        String nation_code = String.copyValueOf(pucCHMsgStr, 16, 2);
        msg.nation_str = nation[Integer.valueOf(nation_code) - 1];

        msg.birth_year = String.copyValueOf(pucCHMsgStr, 18, 4);
        msg.birth_month = String.copyValueOf(pucCHMsgStr, 22, 2);
        msg.birth_day = String.copyValueOf(pucCHMsgStr, 24, 2);
        msg.address = String.copyValueOf(pucCHMsgStr, 26, 35);
        msg.id_num = String.copyValueOf(pucCHMsgStr, 61, 18);
        msg.sign_office = String.copyValueOf(pucCHMsgStr, 79, 15);

        msg.useful_s_date_year = String.copyValueOf(pucCHMsgStr, 94, 4);
        msg.useful_s_date_month = String.copyValueOf(pucCHMsgStr, 98, 2);
        msg.useful_s_date_day = String.copyValueOf(pucCHMsgStr, 100, 2);

        msg.useful_e_date_year = String.copyValueOf(pucCHMsgStr, 102, 4);
        msg.useful_e_date_month = String.copyValueOf(pucCHMsgStr, 106, 2);
        if (msg.useful_e_date_year.equalsIgnoreCase("长")) {
            msg.useful_e_date_day = "";
        } else {
            msg.useful_e_date_day = String.copyValueOf(pucCHMsgStr, 108, 2);
        }

    }

    /**
     * 字节解码函数
     *
     * @param msg
     * @param msg_str
     * @throws Exception
     */
    void DecodeByte(byte[] msg, char[] msg_str) throws Exception {
        byte[] newmsg = new byte[msg.length + 2];

        newmsg[0] = (byte) 0xff;
        newmsg[1] = (byte) 0xfe;

        for (int i = 0; i < msg.length; i++)
            newmsg[i + 2] = msg[i];

        String s = new String(newmsg, "UTF-16");
        for (int i = 0; i < s.toCharArray().length; i++)
            msg_str[i] = s.toCharArray()[i];
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void registerUSBpermisson(Context mContext) {
        if (!usbReceiverRegisted) {
            usbReceiverRegisted = true;
            IntentFilter filter = new IntentFilter();// 意图过滤器
            filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);// USB设备接入
            filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);// USB设备拔出
            filter.addAction(common.ACTION_USB_PERMISSION);// 自定义的USB设备请求授权
            mContext.registerReceiver(mUsbReceiver, filter);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (usbReceiverRegisted) {
            usbReceiverRegisted = false;
            unregisterReceiver(mUsbReceiver);
        }
        stopTimer();
        stopThread();
    }
}
