package com.turing.facerecognizationdemo.utils;

import android.hardware.Camera;

/**
 * ��̬�ڲ�����ģʽ
 */
public class CameraInerface {
	
	private CameraInerface() {
	
	}
	
	/**
	 * ����ǰ������ͷ 
	 * @author zxk
	 */
	private static class CameraInterfaceHolder{
		private static Camera camera = Camera.open(1);
	}
	
	/**
	 * ���ú�������ͷ
	 * @author zxk
	 */
	private static class CameraInterfaceHolder2{
		private static Camera camera = Camera.open();//����ǰ������ͷ
	}
	
	/**
	 * ��ȡ����ͷ����Ľӿ�
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
