package com.turing.facerecognizationdemo.bean;

public class EventBusObject1 {

	private String iden;
	private String age;
	private String gender;
	private String emotion;

	public EventBusObject1(String iden, String age, String gender, String emotion) {
		super();
		this.iden = iden;
		this.age = age;
		this.gender = gender;
		this.emotion = emotion;
	}

	public String getIden() {
		return iden;
	}

	public String getAge() {
		return age;
	}

	public String getGender() {
		return gender;
	}

	public String getEmotion() {
		return emotion;
	}

}
