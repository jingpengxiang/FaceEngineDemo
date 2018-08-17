//package com.lu.face.faceenginedemo.engine;
//
//import android.content.Context;
//
//import com.hisign.AS60xSDK.SAMCommunicationProtocol;
//import com.hisign.fpmoduleservice.aidl.IDCardMsg;
//
//import java.io.IOException;
//
///**
// * 项目名称： FaceEngineDemo
// * 创建人： Jing
// * 创建时间： 2018/7/22  14:31
// * 修改备注：
// */
//public class IDCardSDK {
//    byte[] cmd_find;
//    byte[] cmd_init0;
//    byte[] cmd_init1;
//    byte[] cmd_init2;
//    byte[] cmd_readAll;
//    byte[] cmd_readindex;
//    byte[] cmd_selt;
//    int deviceType = 0;
//    private String tag = "IDCardSDK";
//    UARTIDCardManager uartIDCard = null;
//    USBIDCardManager usbIDCard = null;
//
//    public IDCardSDK(Context mContext, String serialPort) {
//        byte[] bArr = new byte[17];
//        bArr[0] = (byte) 2;
//        bArr[2] = (byte) 17;
//        bArr[3] = (byte) 3;
//        bArr[4] = (byte) -86;
//        bArr[6] = (byte) 1;
//        bArr[7] = (byte) 2;
//        bArr[8] = (byte) 3;
//        bArr[9] = (byte) 4;
//        bArr[10] = (byte) 5;
//        bArr[11] = (byte) 6;
//        bArr[12] = (byte) 7;
//        bArr[13] = (byte) 8;
//        bArr[14] = (byte) 9;
//        bArr[15] = (byte) -71;
//        bArr[16] = (byte) 3;
//        this.cmd_init0 = bArr;
//        bArr = new byte[10];
//        bArr[0] = (byte) -86;
//        bArr[1] = (byte) -86;
//        bArr[2] = (byte) -86;
//        bArr[3] = (byte) -106;
//        bArr[4] = (byte) 105;
//        bArr[6] = (byte) 3;
//        bArr[7] = (byte) 17;
//        bArr[8] = (byte) -1;
//        bArr[9] = (byte) -19;
//        this.cmd_init1 = bArr;
//        bArr = new byte[10];
//        bArr[0] = (byte) -86;
//        bArr[1] = (byte) -86;
//        bArr[2] = (byte) -86;
//        bArr[3] = (byte) -106;
//        bArr[4] = (byte) 105;
//        bArr[6] = (byte) 3;
//        bArr[7] = (byte) 18;
//        bArr[8] = (byte) -1;
//        bArr[9] = (byte) -18;
//        this.cmd_init2 = bArr;
//        bArr = new byte[10];
//        bArr[0] = (byte) -86;
//        bArr[1] = (byte) -86;
//        bArr[2] = (byte) -86;
//        bArr[3] = (byte) -106;
//        bArr[4] = (byte) 105;
//        bArr[6] = (byte) 3;
//        bArr[7] = (byte) 32;
//        bArr[8] = (byte) 1;
//        bArr[9] = (byte) 34;
//        this.cmd_find = bArr;
//        bArr = new byte[10];
//        bArr[0] = (byte) -86;
//        bArr[1] = (byte) -86;
//        bArr[2] = (byte) -86;
//        bArr[3] = (byte) -106;
//        bArr[4] = (byte) 105;
//        bArr[6] = (byte) 3;
//        bArr[7] = (byte) 32;
//        bArr[8] = (byte) 2;
//        bArr[9] = (byte) 33;
//        this.cmd_selt = bArr;
//        bArr = new byte[10];
//        bArr[0] = (byte) -86;
//        bArr[1] = (byte) -86;
//        bArr[2] = (byte) -86;
//        bArr[3] = (byte) -106;
//        bArr[4] = (byte) 105;
//        bArr[6] = (byte) 3;
//        bArr[7] = (byte) 48;
//        bArr[8] = (byte) 16;
//        bArr[9] = (byte) 35;
//        this.cmd_readAll = bArr;
//        bArr = new byte[10];
//        bArr[0] = (byte) -86;
//        bArr[1] = (byte) -86;
//        bArr[2] = (byte) -86;
//        bArr[3] = (byte) -106;
//        bArr[4] = (byte) 105;
//        bArr[6] = (byte) 3;
//        bArr[7] = (byte) 48;
//        bArr[8] = (byte) 3;
//        bArr[9] = (byte) 48;
//        this.cmd_readindex = bArr;
//        if (serialPort.equalsIgnoreCase("USB")) {
//            this.deviceType = 1;
//            this.usbIDCard = new USBIDCardManager(mContext);
//            return;
//        }
//        this.deviceType = 2;
//        try {
//            uartIDCard = new UARTIDCardManager(serialPort);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public boolean isDeviceConnect() {
//        return isConnection();
//    }
//
//    byte[] TransmiteToSAM(byte[] sendCMD) {
//        if (this.deviceType == 1) {
//            return this.usbIDCard.TransmiteToSAM(sendCMD);
//        }
//        if (this.deviceType == 2) {
//            return this.uartIDCard.TransmiteToSAM(sendCMD);
//        }
//        return null;
//    }
//
//    boolean isConnection() {
//        byte[] readbuf = new byte[64];
//        readbuf = TransmiteToSAM(this.cmd_init1);
//        if (readbuf == null || readbuf.length <= 7 || SAMCommunicationProtocol.SAMTransCommand(readbuf) != SAMCommunicationProtocol.Operation_Success) {
//            return false;
//        }
//        return true;
//    }
//
//    public int FCV_OnlyFindIDCard() {
//        byte[] readbuf = new byte[64];
//        readbuf = TransmiteToSAM(this.cmd_find);
//        if (readbuf == null || readbuf.length <= 7) {
//            return -1;
//        }
//        if (SAMCommunicationProtocol.SAMTransCommand(readbuf) == SAMCommunicationProtocol.FindCard_Success) {
//            readbuf = TransmiteToSAM(this.cmd_selt);
//            if (readbuf != null && readbuf.length > 0 && SAMCommunicationProtocol.SAMTransCommand(readbuf) == SAMCommunicationProtocol.Operation_Success) {
//                return 1;
//            }
//        }
//        return 0;
//    }
//
//    public IDCardMsg FCV_OnlyReadIDCard(Context mContext) {
//        IDCardMsg msg = new IDCardMsg();
//        byte[] readbuf = new byte[3078];
//        readbuf = TransmiteToSAM(this.cmd_readAll);
//        if (SAMCommunicationProtocol.SAMTransCommand(readbuf) == SAMCommunicationProtocol.Operation_Success) {
//            return /*SAMCommunicationProtocol.decodeIDCardMsg(mContext, readbuf)*/msg;
//        }
//        return msg;
//    }
//
//    public boolean FCV_IsIDcardAlive() {
//        byte[] readbuf = new byte[64];
//        if (SAMCommunicationProtocol.SAMTransCommand(TransmiteToSAM(this.cmd_readindex)) == SAMCommunicationProtocol.ReadAddMsg_Success) {
//            return true;
//        }
//        return false;
//    }
//
//    public IDCardMsg FCV_ReadIDCard(byte b) {
//        if (b == (byte) 1) {
//            IDCardMsg msg = new IDCardMsg();
//            int ret = FCV_OnlyFindIDCard();
//            if (ret == 1) {
//                msg.resultCode = -126;
//                return msg;
//            } else if (ret < 0) {
//                msg.resultCode = -2;
//                return msg;
//            }
//        }
//        return null;
//    }
//
////    public IDCardMsg FCV_OnlyReadIDCard() {
////        return FCV_OnlyReadIDCard(null);
////    }
//}
