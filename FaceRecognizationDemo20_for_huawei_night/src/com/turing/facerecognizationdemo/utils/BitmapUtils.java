package com.turing.facerecognizationdemo.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.turing.facerecognizationdemo.MainActivity;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.SystemClock;
import android.util.Log;

public class BitmapUtils {
	
	private static final String TAG = BitmapUtils.class.getSimpleName();
	
	
	/**
	 * ��byte����ת����bitmap:
	 * @param data
	 * @param camera
	 * @return
	 */
	public static Bitmap transformByte2Bitmap(byte[] data, Camera camera){
		Size previewSize = camera.getParameters().getPreviewSize(); //��ȡԤ���Ĵ�С
		final int width = previewSize.width; //2048
		final int height = previewSize.height; //1536
		int [] rgbData = new int[width * height];
		decodeYUV420SP(rgbData, data, width, height);
		Bitmap resultBitmap = Bitmap.createBitmap(rgbData, width, height, Config.RGB_565);
//		byteToBitmap(rgbData);
		//TODO===============================================================================
		return resultBitmap;
	}
	
//	@SuppressWarnings({ "rawtypes", "unchecked" })
//	public static Bitmap byteToBitmap(byte[] imgByte) {  
//        InputStream input = null;  
//        Bitmap bitmap = null;  
//        BitmapFactory.Options options = new BitmapFactory.Options();  
//        options.inSampleSize = 8;  
//        input = new ByteArrayInputStream(imgByte);  
//        SoftReference softRef = new SoftReference(BitmapFactory.decodeStream(input, null, options));  
//        bitmap = (Bitmap) softRef.get();  
//        if (imgByte != null) {  
//            imgByte = null;  
//        }  
//  
//        try {  
//            if (input != null) {  
//                input.close();  
//            }  
//        } catch (IOException e) {  
//            e.printStackTrace();  
//        }  
//        return bitmap;  
//    }
	
	
	/**
	 * byte[]����תRGB�ĺ����㷨
	 * @param rgb
	 * @param yuv420sp
	 * @param width
	 * @param height
	 */
	public static void decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width, int height) {
        final int frameSize = width * height;

        for (int j = 0, yp = 0; j < height; j++) {
        int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
        for (int i = 0; i < width; i++, yp++) {
            int y = (0xff & ((int) yuv420sp[yp])) - 16;
            if (y < 0) y = 0;
            if ((i & 1) == 0) {
                v = (0xff & yuv420sp[uvp++]) - 128;
                u = (0xff & yuv420sp[uvp++]) - 128;
            }

            int y1192 = 1192 * y;
            int r = (y1192 + 1634 * v);
            int g = (y1192 - 833 * v - 400 * u);
            int b = (y1192 + 2066 * u);

            if (r < 0) r = 0; else if (r > 262143) r = 262143;
            if (g < 0) g = 0; else if (g > 262143) g = 262143;
            if (b < 0) b = 0; else if (b > 262143) b = 262143;

            rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
        }
       }
	}
	
//	/**
//	 * ��ȡBitmap��·��
//	 * @return
//	 */
//	public static File getBitmapFile(MainActivity mainActivity) {
//		ContextWrapper cw =new ContextWrapper(mainActivity);
//		File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
//		File file = new File(directory, "profile.jpg");
//		return file;
//	}
//	
//	
	/**
	 * ��ͼƬ����300*400�ı�������ѹ��
	 * �����㷨Ϊ�����߼�
	 */
	public static Bitmap resizeBitmap(Bitmap bmp) {
		if(bmp!=null){
			long startResize = SystemClock.uptimeMillis();
			Bitmap bitmap = bmp;
			float width = bitmap.getWidth();
			float height = bitmap.getHeight();
			Log.d(TAG, "----ԭͼƬ�Ŀ�� : " +bmp.getWidth()+",�߶�:"+bmp.getHeight());//1920/1088 = 1.76
			
			//���ǹ̶���������
			int mWidth = 400;//Ҫ�ϸ���ͼƬ�ĳ���Ƚ�������
			int mHeight = 300;
//			int mWidth = 600;//Ҫ�ϸ���ͼƬ�ĳ���Ƚ�������
//			int mHeight = 450;
			
			float scale = 1.0f;
			
			float scaleX = (float)mWidth/width;
			float scaleY = (float)mHeight/height;
			
			Log.d(TAG, "----scaleX: " +scaleX+",scaleY:"+scaleY);//0.2083,0.2757(400*300) | 0.3125,0.4136(600*450)
			
			if(scaleX < scaleY && (scaleX > 0 || scaleY >0)){
				scale = scaleX;
			}
			
			if(scaleY <= scaleX &&(scaleX > 0 || scaleY >0)){
				scale = scaleY;
			}
			
			Log.d(TAG, "----scale----- : " + scale);//0.208(400*300) | 0.3125(600*450)
			
			Matrix matrix = new Matrix();
			matrix.postScale(scale, scale);
			
			int tarWidth = (int)(width*scale);
			int tarHeight = (int)(height*scale);
			Log.d(TAG, "---��������ͼƬ�Ŀ�  : " + tarWidth+"���߶��� : " + tarHeight); //400/226 = 1.77(400*300) | 600/340=1.76(600*450)
			
			Log.i(TAG, "----�������ʱ���� : " + (SystemClock.uptimeMillis() - startResize));
			bitmap= Bitmap.createBitmap(bitmap, 0, 0, (int)width, (int)height, matrix, true);
			Log.d(TAG, "==Ŀ��ͼƬ�Ŀ�  : " + bitmap.getWidth()+"���߶��� : " + bitmap.getHeight()); //400/226 = 1.77(400*300) | 600/340=1.76(600*450)
			
			Log.i(TAG, "----�ӿ�ʼ���㵽����Bitmap����ʱ���� : " + (SystemClock.uptimeMillis() - startResize));
			matrix = null;
			
//			SaveBitmapUtils.saveCroppedImage(bitmap);
			return bitmap;
		}
		
		return null;
	}

	/**
	 * ��ͼƬ����300*400�ı�������ѹ��
	 * �����㷨Ϊ�����߼�
	 */
	public static Bitmap resizeBitmap1(Bitmap bmp) {
		if(bmp!=null){
			Bitmap bitmap = bmp;
			float width = bitmap.getWidth();
			float height = bitmap.getHeight();
			
			Log.d(TAG, "----ԭͼƬ�Ŀ�� : " +bmp.getWidth()+",�߶�:"+bmp.getHeight());//1920/1088 = 1.76
			
			//����������
			int hMax = 450;
			int wMax = 600;
//			int hMax = 300;
//			int wMax = 400;
//			
			float scale = 0.0f;
			float r1 = (float)height/hMax;
			float r2 = (float)width/wMax;
			Log.d(TAG, "----r1 : " +r1+",r2:"+r2); //3.62,4.8(400*300) |  2.41,3.2(600*450)
			
			scale = Math.max(r1, r2);
			Log.d(TAG, "----scale : " +scale);//4.8(400*300) | 3.2(600*450)
			
			//Ҫ����δ���û��,ֻ��Ϊ�˶Ա� ��[resizeBitmap������,scale:0.208]�� ����Ľ��
			Log.d(TAG, "----1/scale : " +1/scale);//0.208(400*300)  |  0.3125(600*450)
			
			int afterheight = (int)(height/scale); //
			int afterwidth = (int)(width/scale);
			Log.d(TAG, " ,afterwidth : " + afterwidth+",afterheight : " + afterheight);//399/226=1.76(400*300) | 600/340=1.76(600*450) 
			
//			int mWidth = 400;
//			int mHeight = 300;
//			float scale = 1.0f;
//			
//			float scaleX = (float)mWidth/width;
//			float scaleY = (float)mHeight/height;
//			
//			if(scaleX < scaleY && (scaleX > 0 || scaleY >0)){
//				scale = scaleX;
//			}
//			
//			if(scaleY <= scaleX &&(scaleX > 0 || scaleY >0)){
//				scale = scaleY;
//			}
			
			Matrix matrix = new Matrix();
			matrix.postScale(1/scale, 1/scale);
			
			bitmap= Bitmap.createBitmap(bitmap, 0, 0, (int)width,(int)height, matrix, true);
			Log.d(TAG, "===�������ɵ�ͼƬ����: " +bitmap.getWidth()+",�߶���:"+bitmap.getHeight());//400/227=1.76(400*300) | 600/340=1.76(600*450)

			matrix = null;
			SaveBitmapUtils.saveCroppedImage(bitmap);
			
			return bitmap;
		}
		
		return null;
	}
	
	
	/**
	 * ��bitmapд���ƶ�·��
	 * @param bitmapImage
	 * @return
	 */
	public static String saveToInternalStorage(File imageFile,Bitmap bitmapImage){
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(imageFile);
			bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			fos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				bitmapImage.recycle();
				bitmapImage = null;
				if(fos!=null){
					fos.close();
					fos = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return imageFile.getAbsolutePath(); //�õ�.jpg��ȫ·����
	}
	
	/**
	 * bitmapת��Ϊbyte[]����
	 * @param bitmap
	 * @return
	 */
	public static byte[] bitmap2Bytes(Bitmap bitmap){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);
		return baos.toByteArray();
	}
	
	/**
	 * ��ȡBitmap��·��
	 * @return
	 */
	public static File getBitmapFile(MainActivity mainActivity) {
		ContextWrapper cw =new ContextWrapper(mainActivity);
		File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
		File file = new File(directory, "profile.jpg");
		return file;
	}
	
	/**
	 * ��ͼƬ��300*300Ϊ����޶Ƚ���ѹ��
	 * @param cutedBitmap
	 * @return
	 */
	public static Bitmap resizeBitmap2(Bitmap cutedBitmap) {
		if(cutedBitmap!=null){
			Bitmap bitmap = cutedBitmap;
			float width = bitmap.getWidth();
			float height = bitmap.getHeight();
			Log.d(TAG, "----ԭͼƬ�Ŀ�� : " +width+",�߶�:"+height);//1920/1088 = 1.76
			
			int mWidth = 300;//Ҫ�ϸ���ͼƬ�ĳ���Ƚ�������
			int mHeight = 300;
			float scale = 1.0f;
			
			float scaleX = (float)mWidth/width;
			float scaleY = (float)mHeight/height;
			
			Log.d(TAG, "----scaleX: " +scaleX+",scaleY:"+scaleY);//TODO(400*300) | TODO(600*450)
			
			if(scaleX < scaleY && (scaleX > 0 || scaleY >0)){
				scale = scaleX;
			}
			
			if(scaleY <= scaleX &&(scaleX > 0 || scaleY >0)){
				scale = scaleY;
			}
			
			Log.d(TAG, "----scale----- : " + scale);//TODO(400*300) | TODO(600*450)
			
			Matrix matrix = new Matrix();
			matrix.postScale(scale, scale);
			
			int tarWidth = (int)(width*scale);
			int tarHeight = (int)(height*scale);
			Log.d(TAG, "---��������ͼƬ�Ŀ�  : " + tarWidth+"���߶��� : " + tarHeight); //TODO(400*300) | TODO(600*450)
			
			bitmap= Bitmap.createBitmap(bitmap, 0, 0, (int)width, (int)height, matrix, true);
			Log.d(TAG, "==Ŀ��ͼƬ�Ŀ�  : " + bitmap.getWidth()+"���߶��� : " + bitmap.getHeight()); //TODO(400*300) |TODO(600*450)
			matrix = null;
			
//			SaveBitmapUtils.saveCroppedImage(bitmap);�����汣�浽����
			return bitmap;
		}
		
		return null;
	}
}
