package com.turing.facerecognizationdemo.utils;

import android.hardware.Camera;

/**
 * 静态内部单例模式
 */
public class CameraInerface {
	
	private CameraInerface() {
	
	}
	
	/**
	 * 调用前置摄像头 
	 * @author zxk
	 */
	private static class CameraInterfaceHolder{
		private static Camera camera = Camera.open(1);
	}
	
	/**
	 * 调用后置摄像头
	 * @author zxk
	 */
	private static class CameraInterfaceHolder2{
		private static Camera camera = Camera.open();//调用前置摄像头
	}
	
	/**
	 * 获取摄像头对象的接口
	 * @param type
	 * @return
	 */
	public static Camera getCameraInstance(int type){
		if(type == 1){
			return CameraInterfaceHolder.camera;
		}else{
			return CameraInterfaceHolder2.camera;
		}
	}
}
