package com.turing.facerecognizationdemo.bean;

import java.util.List;

public class MarkingBean {

	public MarkingBean(boolean isExistText, List<EventBusObject1> eventBusList) {
		super();
		this.isExistText = isExistText;
		this.eventBusList = eventBusList;
	}

	private boolean isExistText;

	public boolean isExistText() {
		return isExistText;
	}

	public List<EventBusObject1> getEventBusList() {
		return eventBusList;
	}

	private List<EventBusObject1> eventBusList;
	
	
}
