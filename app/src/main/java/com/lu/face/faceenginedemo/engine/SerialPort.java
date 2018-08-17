//package com.lu.face.faceenginedemo.engine;
//
//import android.util.Log;
//
//import java.io.File;
//import java.io.FileDescriptor;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//
///**
// * 项目名称： FaceEngineDemo
// * 创建人： Jing
// * 创建时间： 2018/7/22  14:33
// * 修改备注：
// */
//public class SerialPort {
//    private static final String TAG = "SerialPort";
//    private FileDescriptor mFd;
//    private FileInputStream mFileInputStream;
//    private FileOutputStream mFileOutputStream;
//
//    private static native FileDescriptor open(String str, int i, int i2);
//
//    public native void close();
//
//    public SerialPort(File device, int baudrate, int flags) throws SecurityException, IOException {
//        if (!(device.canRead() && device.canWrite())) {
//            try {
//                Process su = Runtime.getRuntime().exec("/system/bin/su");
//                su.getOutputStream().write(("chmod 666 " + device.getAbsolutePath() + "\n" + "exit\n").getBytes());
//                if (!(su.waitFor() == 0 && device.canRead() && device.canWrite())) {
//                    throw new SecurityException();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                throw new SecurityException();
//            }
//        }
//        this.mFd = open(device.getAbsolutePath(), baudrate, flags);
//        if (this.mFd == null) {
//            Log.e(TAG, "native open returns null");
//            throw new IOException();
//        }
//        this.mFileInputStream = new FileInputStream(this.mFd);
//        this.mFileOutputStream = new FileOutputStream(this.mFd);
//    }
//
//    public FileInputStream getInputStream() {
//        return this.mFileInputStream;
//    }
//
//    public FileOutputStream getOutputStream() {
//        return this.mFileOutputStream;
//    }
//
//    static {
//        System.loadLibrary("serial_port");
//    }
//}
