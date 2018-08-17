//package com.lu.face.faceenginedemo.engine;
//
//import android.support.v4.view.MotionEventCompat;
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
// * 创建时间： 2018/7/22  16:09
// * 修改备注：
// */
//public class recIDCardCmd extends Thread {
//    public static boolean istart = true;
//    byte[] Data = null;
//    FileInputStream mFileInputStream;
//    FileOutputStream mFileOutputStream;
//    SerialPort mSerialPort;
//    private int nwPKLen;
//    private int nwPKOFCMDLen;
//    private int readlen;
//    private byte[] findok;
//    private byte[] findno;
//    private byte[] readno;
//    private byte[] readok;
//    private byte[] status;
//    String tag = "recIDCardCmd";
//
//    public recIDCardCmd(String sSerialPort) {
//        SerialPort mSerialPort;
//        try {
//            mSerialPort = new SerialPort(new File(sSerialPort), 115200, 0);
//        } catch (Exception e) {
//            mSerialPort = null;
//            e.printStackTrace();
//        }
//        this.mFileInputStream = mSerialPort.getInputStream();
//        this.mFileOutputStream = mSerialPort.getOutputStream();
//    }
//
//    public void run() {
//        byte[] data = new byte[3000];
//        while (istart) {
//            try {
//                Log.d(this.tag, "start run");
//                this.readlen = this.mFileInputStream.read(data);
//                String show = String.format("Uart Read Len %d ", new Object[]{Integer.valueOf(this.readlen)});
//                for (int i = 0; i < this.readlen; i++) {
//                    show = new StringBuilder(String.valueOf(show)).append(String.format("%x ", new Object[]{Byte.valueOf(data[i])})).toString();
//                }
//                Log.d(this.tag, show);
//                comunication(data);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    byte checksum(byte[] buf, int offset, int len) {
//        byte ch_sum = (byte) 0;
//        for (int i = 0; i < len; i++) {
//            ch_sum = (byte) ((buf[offset + i] & MotionEventCompat.ACTION_MASK) ^ ch_sum);
//        }
//        return ch_sum;
//    }
//
//    int reqIDCardCmd(byte[] data) {
//        int packLen = data.length + 8;
//        byte[] req = new byte[packLen];
//        req[0] = (byte) -86;
//        req[1] = (byte) -86;
//        req[2] = (byte) -86;
//        req[3] = (byte) -106;
//        req[4] = (byte) 105;
//        int cmdLen = data.length + 1;
//        req[5] = (byte) ((cmdLen >> 8) & MotionEventCompat.ACTION_MASK);
//        req[6] = (byte) (cmdLen & MotionEventCompat.ACTION_MASK);
//        for (int i = 0; i < data.length; i++) {
//            req[i + 7] = data[i];
//        }
//        req[packLen - 1] = checksum(req, 5, data.length + 2);
//        try {
//            this.mFileOutputStream.write(req);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return 0;
//    }
//
//    private int comunication(byte[] ncpUARTRecBuf) {
//        if (ncpUARTRecBuf[0] != (byte) -86 || ncpUARTRecBuf[1] != (byte) -86 || ncpUARTRecBuf[2] != (byte) -86 || ncpUARTRecBuf[3] != (byte) -106 || ncpUARTRecBuf[4] != (byte) 105) {
//            return -1;
//        }
//        if (ncpUARTRecBuf.length <= 7) {
//            return -1;
//        }
//        this.nwPKOFCMDLen = (ncpUARTRecBuf[5] << 8) + ncpUARTRecBuf[6];
//        this.nwPKLen = this.nwPKOFCMDLen + 7;
//        if (this.nwPKLen > 70000) {
//            return -1;
//        }
//        if (ncpUARTRecBuf.length >= this.nwPKLen) {
//            if (ncpUARTRecBuf[this.nwPKLen - 1] != checksum(ncpUARTRecBuf, 5, this.nwPKOFCMDLen + 1)) {
//                return -1;
//            }
//            if (ncpUARTRecBuf[7] != (byte) 17) {
//                if (ncpUARTRecBuf[7] == (byte) 18) {
//                    byte[] reqCmdInit2 = new byte[19];
//                    reqCmdInit2[2] = (byte) -112;
//                    reqCmdInit2[3] = (byte) 5;
//                    reqCmdInit2[5] = (byte) 3;
//                    reqCmdInit2[7] = (byte) 122;
//                    reqCmdInit2[8] = (byte) 83;
//                    reqCmdInit2[9] = (byte) 51;
//                    reqCmdInit2[10] = (byte) 1;
//                    reqCmdInit2[11] = (byte) -5;
//                    reqCmdInit2[12] = (byte) -22;
//                    reqCmdInit2[13] = (byte) 38;
//                    reqCmdInit2[15] = (byte) -81;
//                    reqCmdInit2[16] = (byte) -59;
//                    reqCmdInit2[17] = (byte) 123;
//                    reqCmdInit2[18] = (byte) -115;
//                    reqIDCardCmd(reqCmdInit2);
//                } else if (ncpUARTRecBuf[7] == (byte) 32 && ncpUARTRecBuf[8] == (byte) 1) {
//                    if (this.Data != null) {
//                        findok = new byte[7];
//                        findok[2] = (byte) -97;
//                        reqIDCardCmd(findok);
//                    } else {
//                        findno = new byte[3];
//                        findno[2] = Byte.MIN_VALUE;
//                        reqIDCardCmd(findno);
//                    }
//                } else if (ncpUARTRecBuf[7] == (byte) 32 && ncpUARTRecBuf[8] == (byte) 2) {
//                    if (this.Data != null) {
//                        byte[] seltok = new byte[11];
//                        seltok[2] = (byte) -112;
//                        reqIDCardCmd(seltok);
//                    } else {
//                        findno = new byte[3];
//                        findno[2] = (byte) -127;
//                        reqIDCardCmd(findno);
//                    }
//                } else if (ncpUARTRecBuf[7] == (byte) 48 && ncpUARTRecBuf[8] == (byte) 1) {
//                    if (this.Data == null) {
//                        readno = new byte[3];
//                        readno[2] = (byte) 65;
//                        reqIDCardCmd(readno);
//                    } else if (this.Data.length == 1295) {
//                        try {
//                            this.mFileOutputStream.write(this.Data);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    } else if (this.Data.length >= 1297) {
//                        readok = new byte[1287];
//                        status = new byte[7];
//                        status[2] = (byte) -112;
//                        status[3] = (byte) 1;
//                        status[5] = (byte) 4;
//                        System.arraycopy(status, 0, readok, 0, 7);
//                        System.arraycopy(this.Data, 16, readok, 7, 1280);
//                        reqIDCardCmd(readok);
//                    }
//                } else if (ncpUARTRecBuf[7] != (byte) 48 || ncpUARTRecBuf[8] != (byte) 16) {
//                    findok = new byte[3];
//                    findok[2] = (byte) -97;
//                    reqIDCardCmd(findok);
//                } else if (this.Data == null) {
//                    readno = new byte[3];
//                    readno[2] = (byte) 65;
//                    reqIDCardCmd(readno);
//                } else if (this.Data.length == 1297 || this.Data.length == 2321) {
//                    try {
//                        this.mFileOutputStream.write(this.Data);
//                    } catch (IOException e2) {
//                        e2.printStackTrace();
//                    }
//                } else if (this.Data.length < 1297) {
//                    readok = new byte[1289];
//                    status = new byte[9];
//                    status[2] = (byte) -112;
//                    status[3] = (byte) 1;
//                    status[5] = (byte) 4;
//                    System.arraycopy(status, 0, readok, 0, 9);
//                    System.arraycopy(this.Data, 14, readok, 9, 1280);
//                    reqIDCardCmd(readok);
//                }
//            }
//        }
//        return 0;
//    }
//
//    public void set(byte[] data) {
//        if (data != null) {
//            this.Data = Arrays.copyOfRange(data, 0, data.length);
//        } else {
//            this.Data = null;
//        }
//    }
//}
