package com.turing.facerecognizationdemo.bean;

import java.util.List;

public class ServerData {

	public List<EventBusObject> data;

	public class EventBusObject {
		public String iden;
		public String age;
		public String gender;
		public String emotion;
	}
}
