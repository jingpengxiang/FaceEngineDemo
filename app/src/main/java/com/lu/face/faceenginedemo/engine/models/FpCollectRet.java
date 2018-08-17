package com.lu.face.faceenginedemo.engine.models;

import android.graphics.Bitmap;

public class FpCollectRet {
	private boolean isFp1V1;
	private Bitmap retBm;
	public boolean isFp1V1() {
		return isFp1V1;
	}
	public void setFp1V1(boolean isFp1V1) {
		this.isFp1V1 = isFp1V1;
	}
	public Bitmap getRetBm() {
		return retBm;
	}
	public void setRetBm(Bitmap retBm) {
		this.retBm = retBm;
	} 

}
