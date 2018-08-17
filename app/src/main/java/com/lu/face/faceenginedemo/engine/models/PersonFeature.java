package com.lu.face.faceenginedemo.engine.models;


import com.lu.face.faceenginedemo.enginesdk.models.FeatureCommon;
import com.lu.face.faceenginedemo.enginesdk.models.PersonCommon;

public class PersonFeature {
	private PersonCommon personCommon;
	private FeatureCommon featureCommon;
	private boolean isChecked;

	public PersonCommon getPersonCommon() {
		return personCommon;
	}

	public void setPersonCommon(PersonCommon personCommon) {
		this.personCommon = personCommon;
	}

	public FeatureCommon getFeatureCommon() {
		return featureCommon;
	}

	public void setFeatureCommon(FeatureCommon featureCommon) {
		this.featureCommon = featureCommon;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
}
