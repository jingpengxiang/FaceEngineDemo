//package com.lu.face.faceenginedemo.engine;
//
//import android.app.PendingIntent;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.hardware.usb.UsbDevice;
//import android.hardware.usb.UsbDeviceConnection;
//import android.hardware.usb.UsbEndpoint;
//import android.hardware.usb.UsbInterface;
//import android.hardware.usb.UsbManager;
//import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
//import android.util.Log;
//
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.Iterator;
//
//import static android.content.Context.USB_SERVICE;
//
///**
// * 项目名称： FaceEngineDemo
// * 创建人： Jing
// * 创建时间： 2018/7/22  13:55
// * 修改备注：
// */
//public class USBIDCardManager {
//    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
//    private static final String TAG = "USBIDCardManager";
//    Context iContext = null;
//    private boolean isUserChoosedPermission = false;
//    private UsbDeviceConnection mConnection;
//    private UsbDevice mDevice;
//    private UsbEndpoint mEndpointIn;
//    private UsbEndpoint mEndpointOut;
//    private UsbManager mUsbManager = null;
//    private BroadcastReceiver mUsbReceiverPermission = new BroadcastReceiver() {
//        public void onReceive(Context mContext, Intent intent) {
//            if (USBIDCardManager.ACTION_USB_PERMISSION.equals(intent.getAction())) {
//                synchronized (this) {
//                    UsbDevice usbDevice = (UsbDevice) intent.getParcelableExtra("device");
//                    Log.d(USBIDCardManager.TAG, "init java permission");
//                    if (usbDevice == null || !intent.getBooleanExtra("permission", false)) {
//                    } else {
//                        openUSBDevice();
//                        Log.d(USBIDCardManager.TAG, "init java permission ok");
//                    }
//                }
//                USBIDCardManager.this.isUserChoosedPermission = true;
//            }
//        }
//    };
//
//    public USBIDCardManager(Context mContext) {
//        this.iContext = mContext;
//        registerUSBpermisson(mContext);
//        openUSBDevice();
//    }
//
//    public boolean isDeviceConnect() {
//        if (this.mDevice == null) {
//            return false;
//        }
//        return true;
//    }
//
//    public int openUSBDevice() {
//        int ret = -1;
//        this.mUsbManager = (UsbManager) this.iContext.getSystemService(USB_SERVICE);
//        HashMap<String, UsbDevice> deviceList = this.mUsbManager.getDeviceList();
//        String initInfo = "";
//        this.mDevice = null;
//        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
//        if (!deviceIterator.hasNext()) {
//            Log.i(TAG, "Device has no next:");
//        }
//        while (deviceIterator.hasNext()) {
//            UsbDevice device = (UsbDevice) deviceIterator.next();
//            String devInfo = device.getDeviceName() + "(" + device.getVendorId() + ":" + device.getProductId() + ")";
//            initInfo = new StringBuilder(String.valueOf(initInfo)).append("USB:").append(devInfo).append("\n").toString();
//            Log.i(TAG, "Device:" + devInfo);
//            Log.d(TAG, "getInterfaceCount " + device.getInterfaceCount());
//            if (device.getVendorId() == AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT) {
//                this.mDevice = device;
//                this.mEndpointIn = GetEndPoint(device, 0);
//                this.mEndpointOut = GetEndPoint(device, 1);
//                ret = 0;
//                break;
//            }
//        }
//        if (this.mDevice != null && !this.mUsbManager.hasPermission(this.mDevice)) {
//            this.mUsbManager.requestPermission(this.mDevice, PendingIntent.getBroadcast(this.iContext, 0, new Intent(ACTION_USB_PERMISSION), 0));
//            int timeOutCount = 0;
//            while (!this.isUserChoosedPermission && timeOutCount < 16) {
//                try {
//                    Thread.sleep(500);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                timeOutCount++;
//            }
//            this.isUserChoosedPermission = false;
//        } else if (this.mDevice != null) {
//            this.mConnection = GetConnection(this.mDevice);
//        } else {
//            Log.e(TAG, "can't find USB IDCReader .");
//        }
//        return ret;
//    }
//
//    private void registerUSBpermisson(Context mContext) {
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(ACTION_USB_PERMISSION);
//        mContext.registerReceiver(mUsbReceiverPermission, filter);
//    }
//
//    public UsbEndpoint GetEndPoint(UsbDevice device, int nEndpoint) {
//        if (device == null) {
//            return null;
//        }
//        UsbInterface intf = device.getInterface(0);
//        Log.d(TAG, "getEndpointCount " + intf.getEndpointCount());
//        UsbEndpoint ep1 = intf.getEndpoint(nEndpoint);
//        Log.d(TAG, "endpoint type: " + ep1.getType() + " Addr:" + ep1.getAddress() + " Dir:" + ep1.getDirection());
//        if (ep1.getType() == 2) {
//            return ep1;
//        }
//        Log.e(TAG, "endpoint is not Bulk type");
//        return null;
//    }
//
//    public UsbDeviceConnection GetConnection(UsbDevice device) {
//        if (device == null) {
//            return null;
//        }
//        UsbInterface intf = device.getInterface(0);
//        UsbDeviceConnection connection = this.mUsbManager.openDevice(device);
//        if (connection == null || !connection.claimInterface(intf, true)) {
//            Log.d(TAG, "open FAIL");
//            return null;
//        }
//        Log.d(TAG, "open SUCCESS");
//        return connection;
//    }
//
//    byte[] TransmiteToSAM(byte[] sendCMD) {
//        byte[] recvbuffer = new byte[3096];
//        if (this.mConnection == null || this.mConnection.bulkTransfer(this.mEndpointOut, sendCMD, sendCMD.length, 2000) < 0) {
//            return null;
//        }
//        int recvSize = this.mConnection.bulkTransfer(this.mEndpointIn, recvbuffer, recvbuffer.length, 2000);
//        Log.d(TAG, "bulkTransfer recv(bytes): " + recvSize);
//        if (recvSize >= 0) {
//            return Arrays.copyOfRange(recvbuffer, 0, recvSize);
//        }
//        return null;
//    }
//
//    protected void finalize() {
//        iContext.unregisterReceiver(this.mUsbReceiverPermission);
//    }
//}
