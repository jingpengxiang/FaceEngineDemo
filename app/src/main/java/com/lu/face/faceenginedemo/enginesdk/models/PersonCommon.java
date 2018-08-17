package com.lu.face.faceenginedemo.enginesdk.models;

import android.os.Parcel;
import android.os.Parcelable;

public class PersonCommon implements Parcelable {
	private int personId;
	private String name;
	private String cardId;

	public int getPersonId() {
		return personId;
	}

	public void setPersonId(int personId) {
		this.personId = personId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int arg1) {
		// TODO Auto-generated method stub
		parcel.writeInt(personId);
		parcel.writeString(name);
		parcel.writeString(cardId);
//		parcel.writeList(featureList);
	}

	public static final Creator<PersonCommon> CREATOR = new Creator<PersonCommon>() {
		@Override
		public PersonCommon createFromParcel(Parcel parcel) {
			PersonCommon person = new PersonCommon();
			person.setPersonId(parcel.readInt());
			person.setName(parcel.readString());
			person.setCardId(parcel.readString());
//			person.featureList = new ArrayList<FeatureSdk>();
//			parcel.readList(person.featureList, getClass().getClassLoader());
			return person;
		}

		@Override
		public PersonCommon[] newArray(int size) {
			return new PersonCommon[size];
		}
	};
}
