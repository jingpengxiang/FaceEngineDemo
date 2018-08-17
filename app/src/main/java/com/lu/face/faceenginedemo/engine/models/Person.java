package com.lu.face.faceenginedemo.engine.models;


import android.os.Parcel;
import android.os.Parcelable;

import com.lu.face.faceenginedemo.enginesdk.models.PersonCommon;

public class Person implements Parcelable {
	private PersonCommon personCommon;
//	private String path;
//	private List<Feature> featureList = new ArrayList<Feature>();

//	public List<Feature> getFeatureList() {
//		return featureList;
//	}
//
//	public void setFeatureList(List<Feature> featureList) {
//		this.featureList = featureList;
//	}

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
//		parcel.writeList(featureList);
	}

//	public String getPath() {
//		return path;
//	}
//
//	public void setPath(String path) {
//		this.path = path;
//	}

	public static final Creator<Person> CREATOR = new Creator<Person>() {
		@Override
		public Person createFromParcel(Parcel parcel) {
			Person person = new Person();
			person.setPersonCommon((PersonCommon) parcel.readParcelable(PersonCommon.class.getClassLoader()));
//			person.featureList = new ArrayList<Feature>();
//			parcel.readList(person.featureList, getClass().getClassLoader());
			return person;
		}

		@Override
		public Person[] newArray(int size) {
			return new Person[size];
		}
	};
}
