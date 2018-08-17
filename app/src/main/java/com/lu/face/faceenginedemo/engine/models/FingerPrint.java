package com.lu.face.faceenginedemo.engine.models;

import android.os.Parcel;
import android.os.Parcelable;

public class FingerPrint implements Parcelable {
	private int id;
	private String name;
	private String path;
	private boolean isChecked;
	private byte[] feature;

	public FingerPrint() {
		// TODO Auto-generated constructor stub
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flag) {
		// TODO Auto-generated method stub
		parcel.writeInt(id);
		parcel.writeString(name);
		parcel.writeString(path);
		parcel.writeByte((byte) (isChecked ? 1 : 0));
		parcel.writeInt(feature.length);
		parcel.writeByteArray(feature);
	}

	public byte[] getFeature() {
		return feature;
	}

	public void setFeature(byte[] feature) {
		this.feature = feature;
	}

	public static final Creator<FingerPrint> CREATOR = new Creator<FingerPrint>() {
		@Override
		public FingerPrint createFromParcel(Parcel parcel) {
			FingerPrint fp = new FingerPrint();
			fp.setId(parcel.readInt());
			fp.setName(parcel.readString());
			fp.setPath(parcel.readString());
			fp.setChecked(0 != parcel.readByte());
			byte[] bytes = new byte[parcel.readInt()];
			parcel.readByteArray(bytes);
			fp.setFeature(bytes);
			return fp;
		}

		@Override
		public FingerPrint[] newArray(int size) {
			return new FingerPrint[size];
		}
	};

}
