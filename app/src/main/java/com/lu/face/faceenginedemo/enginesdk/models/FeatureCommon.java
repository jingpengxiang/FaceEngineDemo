package com.lu.face.faceenginedemo.enginesdk.models;

import android.os.Parcel;
import android.os.Parcelable;

public class FeatureCommon implements Parcelable {
	private int personId;
	private int featureId;
	private String path;

	public FeatureCommon() {
		// TODO Auto-generated constructor stub
	}

	public int getPersonId() {
		return personId;
	}

	public void setPersonId(int personId) {
		this.personId = personId;
	}

	public int getFeatureId() {
		return featureId;
	}

	public void setFeatureId(int featureId) {
		this.featureId = featureId;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flag) {
		// TODO Auto-generated method stub
		parcel.writeInt(personId);
		parcel.writeInt(featureId);
		parcel.writeString(path);
	}
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	public static final Creator<FeatureCommon> CREATOR = new Creator<FeatureCommon>() {
		@Override
		public FeatureCommon createFromParcel(Parcel parcel) {
			FeatureCommon fs = new FeatureCommon();
			fs.setPersonId(parcel.readInt());
			fs.setFeatureId(parcel.readInt());
			fs.setPath(parcel.readString());
			return fs;
		}

		@Override
		public FeatureCommon[] newArray(int size) {
			return new FeatureCommon[size];
		}
	};

}
