//package com.lu.face.faceenginedemo.engine;
//
//import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
//import android.util.Log;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.util.Arrays;
//
///**
// * 项目名称： FaceEngineDemo
// * 创建人： Jing
// * 创建时间： 2018/7/22  14:32
// * 修改备注：
// */
//public class UARTIDCardManager {
//    private static final String tag = "UARTIDCardManager";
//    private FileInputStream mFileInputStream;
//    private FileOutputStream mFileOutputStream;
//    private SerialPort mSerialPort = null;
//
//    public UARTIDCardManager(String serialPort) throws IOException {
//        try {
//            this.mSerialPort = new SerialPort(new File(serialPort), 115200, 0);
//        } catch (Exception e) {
//            this.mSerialPort = null;
//            e.printStackTrace();
//        }
//        if (this.mSerialPort == null) {
//            Log.e(tag, "opne SerialPort \"" + serialPort + "\" error ");
//            return;
//        }
//        this.mFileInputStream = this.mSerialPort.getInputStream();
//        this.mFileOutputStream = this.mSerialPort.getOutputStream();
//        byte[] readbuf = new byte[AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT];
//        while (mFileInputStream.available() != 0) {
//            try {
//                this.mFileInputStream.read(readbuf);
//            } catch (IOException e2) {
//                e2.printStackTrace();
//                return;
//            }
//        }
//    }
//
//    byte[] TransmiteToSAM(byte[] sendCMD) {
//        Exception e;
//        byte[] readbuf = new byte[3096];
//        byte[] temp = new byte[3096];
//        int fullLen = 0;
//        try {
//            int templen;
//            this.mFileOutputStream.write(sendCMD);
//            Thread.sleep(10);
//            int readlen = this.mFileInputStream.read(readbuf);
//            while (readlen < 11) {
//                Thread.sleep(10);
//                templen = this.mFileInputStream.read(temp);
//                System.arraycopy(temp, 0, readbuf, readlen, templen);
//                readlen += templen;
//            }
//            if (readbuf[0] != (byte) -86 || readbuf[1] != (byte) -86 || readbuf[2] != (byte) -86 || readbuf[3] != (byte) -106 || readbuf[4] != (byte) 105) {
//                return null;
//            }
//            fullLen = ((readbuf[5] * AccessibilityNodeInfoCompat.ACTION_NEXT_AT_MOVEMENT_GRANULARITY) + readbuf[6]) + 7;
//            while (readlen < fullLen) {
//                Thread.sleep(10);
//                templen = this.mFileInputStream.read(temp);
//                System.arraycopy(temp, 0, readbuf, readlen, templen);
//                readlen += templen;
//            }
//            return Arrays.copyOfRange(readbuf, 0, fullLen);
//        } catch (InterruptedException e2) {
//            e = e2;
//        } catch (IOException e3) {
//            e = e3;
//        }
//        e.printStackTrace();
//        return Arrays.copyOfRange(readbuf, 0, fullLen);
//    }
//}
