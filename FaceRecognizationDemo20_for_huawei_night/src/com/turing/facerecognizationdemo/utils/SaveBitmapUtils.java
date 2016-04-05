package com.turing.facerecognizationdemo.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;

public class SaveBitmapUtils {
	
	
	private static final String TAG = SaveBitmapUtils.class.getSimpleName();

	@SuppressLint("SdCardPath")
	public static void saveCroppedImage(Bitmap bmp) {
		Log.d(TAG, "---------saveCroppedImage()---------");
        File file = new File("/sdcard/myFolder");
        if (!file.exists()){
        	file.mkdir();
        }
        
        file = new File("/sdcard/temp.jpg".trim());
        String fileName = file.getName();
        String mName = fileName.substring(0, fileName.lastIndexOf("."));
        String sName = fileName.substring(fileName.lastIndexOf("."));
        
        // /sdcard/myFolder/temp_cropped.jpg
        String newFilePath = "/sdcard/myFolder" + "/" + mName + "_cropped" + sName;
        Log.d(TAG, "---------存储的路径是 : " + newFilePath);
        file = new File(newFilePath);
        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(CompressFormat.JPEG, 30, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	/**
	 * 为了检查上传服务器前的byte数组能生成的图片的质量
	 * @param bitmap
	 */
	public static void saveCroppedImage2(Bitmap bitmap) {
		Log.d(TAG, "---------saveCroppedImage()---------");
        File file = new File("/sdcard/myFolder");
        if (!file.exists()){
        	file.mkdir();
        }
        
        file = new File("/sdcard/temp.jpg".trim());
        String fileName = file.getName();
        String mName = fileName.substring(0, fileName.lastIndexOf("."));
        String sName = fileName.substring(fileName.lastIndexOf("."));
        
        // /sdcard/myFolder/temp_cropped.jpg
        String newFilePath = "/sdcard/myFolder" + "/" + mName + "_cropped_generate" + sName;
        Log.d(TAG, "---------存储的路径是 : " + newFilePath);
        file = new File(newFilePath);
        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(CompressFormat.JPEG, 10, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }		
	}
}
