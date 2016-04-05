package com.turing.facerecognizationdemo.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.turing.facerecognizationdemo.bean.EventBusObject1;
import com.turing.facerecognizationdemo.bean.ServerData;
import com.turing.facerecognizationdemo.bean.ServerData.EventBusObject;

public class ParseJsonUtils {

	private static Gson gson = new Gson();

	/**
	 * 解析json数据返回JavaBean
	 * 
	 * @param result
	 * @return
	 */
	public static EventBusObject json2Bean(String result) {
		EventBusObject mEventBusObject = null;
		if (gson != null && result != null) {
			mEventBusObject = gson.fromJson(result, EventBusObject.class);
		}
		return mEventBusObject;
	}

//	/**
//	 * 只获取我们想要的数据
//	 * 
//	 * @param mBusObject
//	 */
//	public static String bean2ResultString(EventBusObject mBusObject) {
//		StringBuffer mStringBuffer = new StringBuffer();
//		if (mBusObject != null) {
//			String iden = mBusObject.getIden();
//			mStringBuffer.append(iden);
//			String age = mBusObject.getAge();
//			mStringBuffer.append(age);
//			String gender = mBusObject.getGender();
//			mStringBuffer.append(gender);
//			String emotion = mBusObject.getEmotion();
//			mStringBuffer.append(emotion);
//		}
//		return mStringBuffer.toString();
//	}

	/**
	 * 解析JSON,拼接我们想要的字符串
	 * iden,age,gender,emotion
	 * 
	 * @param result
	 * @return
	 */
	public static String json2ResultString(String result) {
		StringBuffer mStringBuffer = new StringBuffer();
		if (gson != null && result != null) {
			EventBusObject1 mBusObject = gson.fromJson(result, EventBusObject1.class);
			String iden = mBusObject.getIden();
			mStringBuffer.append(iden);
			mStringBuffer.append("\t");
			
			String age = mBusObject.getAge();
			mStringBuffer.append(age);
			mStringBuffer.append("\t");
			
			String gender = mBusObject.getGender();
			mStringBuffer.append(gender);
			mStringBuffer.append("\t");
			
			String emotion = mBusObject.getEmotion();
			mStringBuffer.append(emotion);
			return mStringBuffer.toString();
		}
		return null;
	}
	
	/**
	 * JSON转换成JavaBean
	 * @param result
	 */
	public static ServerData json2JavaBean(String result) {
		if(gson!=null&&!TextUtils.isEmpty(result)){
			ServerData serverData = gson.fromJson(result,ServerData.class);
			return serverData;
		}
		return null;
	}
	
//	public static String json2ResultBean(String result){
//		
//		if(gson!=null && !TextUtils.isEmpty(result)){
////			gson.fromJson(result, classOfT);
//			
//		}
//		
//		return null;
//	}
}
