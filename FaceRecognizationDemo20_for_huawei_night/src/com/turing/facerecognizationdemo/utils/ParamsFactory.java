package com.turing.facerecognizationdemo.utils;

import java.io.InputStream;
import java.util.Properties;

public class ParamsFactory {

	private static final String TAG = ParamsFactory.class.getSimpleName();

	private static Properties properties;

	static {
		try {
			properties = new Properties();
			InputStream inputStream = ParamsFactory.class.getClassLoader().getResourceAsStream("params.properties");
			properties.load(inputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 工厂模式选择前后摄像头
	 * @param clazz
	 * @return
	 */
	public static String createCamera() {
		String className = properties.getProperty("cameraType");
		return className;
	}
}
