package com.turing.facerecognizationdemo.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Base64;
import android.util.Log;

/**
 * Description���ô��ཫͼƬת��Ϊ�ַ������Ա㽫ͼƬ��װΪJSON���д���
 * 
 * @author
 * @Date 2014-07-15
 * @version 1.0
 * */
public class ImgHelper {
	
	private static final String TAG = ImgHelper.class.getSimpleName();
	
	private static BASE64Encoder mBase64Encoder = new BASE64Encoder();
	
	/**
	 * TODO:��byte������Base64��ʽ����Ϊ�ַ���
	 * 
	 * @param bytes
	 *            �������byte����
	 * @return �������ַ���
	 * */
	public static String encode(byte[] bytes) {
		if(mBase64Encoder!=null){
			Log.d(TAG, "-----ʹ�ó�Ա������������BASE64Encoder����-----");
			return mBase64Encoder.encode(bytes);
		}
		Log.d(TAG, "-----���´���BASE64Encoder����-----");
		return new BASE64Encoder().encode(bytes);
	}
	
	/**
	 * TODO:����Base64��ʽ������ַ�������Ϊbyte����
	 * 
	 * @param encodeStr
	 *            ��������ַ���
	 * @return ������byte����
	 * @throws IOException
	 * */
	public static byte[] decode(String encodeStr) throws IOException {
		byte[] bt = null;
		BASE64Decoder decoder = new BASE64Decoder();
		bt = decoder.decodeBuffer(encodeStr);
		return bt;
	}

	/**
	 * TODO:������byte�������������󣬷������Ӻ��Byte����
	 * 
	 * @param front
	 *            ƴ�Ӻ���ǰ�������
	 * @param after
	 *            ƴ�Ӻ��ں��������
	 * @return ƴ�Ӻ������
	 * */
	public static byte[] connectBytes(byte[] front, byte[] after) {
		byte[] result = new byte[front.length + after.length];
		System.arraycopy(front, 0, result, 0, after.length);
		System.arraycopy(after, 0, result, front.length, after.length);
		return result;
	}

	/**
	 * TODO:��ͼƬ��Base64��ʽ����Ϊ�ַ���
	 * 
	 * @param imgUrl
	 *            ͼƬ�ľ���·�������磺D:\\jsonimg\\123.jpg��
	 * @return �������ַ���
	 * @throws IOException
	 * */
	public static String encodeImage(String imgUrl) throws IOException {//bitmap--byts
		FileInputStream fis = new FileInputStream(imgUrl);
		byte[] rs = new byte[fis.available()];//bitmap.getBytes();
		fis.read(rs);
		fis.close();

		// תΪ bitmap
		Bitmap bitmap = Bytes2Bitmap(rs);
		// ѹ��ͼƬ
		Bitmap newbitmap = resizeBitmap(bitmap);
		//
		byte[] newbytes = Bitmap2Bytes(newbitmap);
		return encode(newbytes);
	}

	// byte[]ת����Bitmap
	public static Bitmap Bytes2Bitmap(byte[] b) {
		if (b.length != 0) {
			return BitmapFactory.decodeByteArray(b, 0, b.length);
		}
		return null;
	}

	/**
	 * ��ͼƬ����300*400�ı�������ѹ�� �����㷨Ϊ�����߼�
	 * 
	 */
	public static Bitmap resizeBitmap(Bitmap bmp) {
		// int scale = 1; //Ҫ��֤��Դ�(�����ǿ��Ǹ���Դ�)��ͼƬ��ʾ����
		Bitmap bitmap = bmp;
		float width = bitmap.getWidth();
		float height = bitmap.getHeight();

		// ���ǹ̶���������
		int mWidth = 300;
		int mHeight = 400;

		float scale = 1.0f;

		float scaleX = (float) mWidth / width;
		float scaleY = (float) mHeight / height;

		if (scaleX < scaleY && (scaleX > 0 || scaleY > 0)) {
			scale = scaleX;
		}

		if (scaleY <= scaleX && (scaleX > 0 || scaleY > 0)) {
			scale = scaleY;
		}

		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);

		bitmap = Bitmap.createBitmap(bitmap, 0, 0, (int) width, (int) height, matrix, true);
		matrix = null;
		return bitmap;
	}

	/**
	 * ��BitmapתByte
	 * 
	 * @Author HEH
	 * @EditTime 2010-07-19 ����11:45:56
	 */
	public static byte[] Bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String str;
		try {
			str = encodeImage("D:\\SUN_TEST\\attachment_jpg.jpg");
			System.out.println(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
