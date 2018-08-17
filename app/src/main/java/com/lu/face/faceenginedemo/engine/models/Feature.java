package com.lu.face.faceenginedemo.engine.models;


import android.os.Parcel;
import android.os.Parcelable;

import com.lu.face.faceenginedemo.enginesdk.models.FeatureCommon;

public class Feature implements Parcelable {
	private FeatureCommon featureCommon;
	private boolean isShow;
	private boolean isChecked;

	public Feature() {
		// TODO Auto-generated constructor stub
	}

	public boolean isShow() {
		return isShow;
	}

	public void setShow(boolean isShow) {
		this.isShow = isShow;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flag) {
		// TODO Auto-generated method stub
		parcel.writeParcelable(featureCommon, flag);
		parcel.writeByte((byte) (isShow ? 1 : 0));
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public FeatureCommon getFeatureCommon() {
		return featureCommon;
	}

	public void setFeatureCommon(FeatureCommon featureCommon) {
		this.featureCommon = featureCommon;
	}

	public static final Creator<Feature> CREATOR = new Creator<Feature>() {
		@Override
		public Feature createFromParcel(Parcel parcel) {
			Feature feature = new Feature();
			feature.setFeatureCommon((FeatureCommon) parcel.readParcelable(FeatureCommon.class.getClassLoader()));
			feature.setShow(0 != parcel.readByte());
			return feature;
		}

		@Override
		public Feature[] newArray(int size) {
			return new Feature[size];
		}
	};

}
