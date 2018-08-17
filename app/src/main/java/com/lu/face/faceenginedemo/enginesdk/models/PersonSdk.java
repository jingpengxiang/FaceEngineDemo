package com.lu.face.faceenginedemo.enginesdk.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class PersonSdk implements Parcelable {
	private PersonCommon personCommon;
	private List<FeatureSdk> featureList = new ArrayList<FeatureSdk>();

	public List<FeatureSdk> getFeatureList() {
		return featureList;
	}

	public void setFeatureList(List<FeatureSdk> featureList) {
		this.featureList = featureList;
	}

	public PersonCommon getPersonCommon() {
		return personCommon;
	}

	public void setPersonCommon(PersonCommon personCommon) {
		this.personCommon = personCommon;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int arg1) {
		// TODO Auto-generated method stub
		parcel.writeParcelable(personCommon, arg1);
		parcel.writeList(featureList);
	}

	public static final Creator<PersonSdk> CREATOR = new Creator<PersonSdk>() {
		@Override
		public PersonSdk createFromParcel(Parcel parcel) {
			PersonSdk person = new PersonSdk();
			person.setPersonCommon((PersonCommon) parcel.readParcelable(PersonCommon.class.getClassLoader()));
			person.featureList = new ArrayList<FeatureSdk>();
			parcel.readList(person.featureList, getClass().getClassLoader());
			return person;
		}

		@Override
		public PersonSdk[] newArray(int size) {
			return new PersonSdk[size];
		}
	};
}
