package com.lu.face.faceenginedemo.enginesdk.models;

import android.os.Parcel;
import android.os.Parcelable;

public class FeatureSdk implements Parcelable {
	private FeatureCommon featureCommon;
	private byte[] features;

	public FeatureSdk() {
		// TODO Auto-generated constructor stub
	}

	public byte[] getFeatures() {
		return features;
	}

	public void setFeatures(byte[] features) {
		this.features = features;
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
		parcel.writeInt(features.length);
		parcel.writeByteArray(features);
	}

	public FeatureCommon getFeatureCommon() {
		return featureCommon;
	}

	public void setFeatureCommon(FeatureCommon featureCommon) {
		this.featureCommon = featureCommon;
	}
	public static final Creator<FeatureSdk> CREATOR = new Creator<FeatureSdk>() {
		@Override
		public FeatureSdk createFromParcel(Parcel parcel) {
			FeatureSdk fs = new FeatureSdk();
			fs.setFeatureCommon((FeatureCommon) parcel.readParcelable(FeatureCommon.class.getClassLoader()));
			byte[] bytes = new byte[parcel.readInt()];
			parcel.readByteArray(bytes);
			fs.setFeatures(bytes);
			return fs;
		}

		@Override
		public FeatureSdk[] newArray(int size) {
			return new FeatureSdk[size];
		}
	};

}
