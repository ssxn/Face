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
 * Description：用此类将图片转换为字符串，以便将图片封装为JSON进行传输
 * 
 * @author
 * @Date 2014-07-15
 * @version 1.0
 * */
public class ImgHelper {
	
	private static final String TAG = ImgHelper.class.getSimpleName();
	
	private static BASE64Encoder mBase64Encoder = new BASE64Encoder();
	
	/**
	 * TODO:将byte数组以Base64方式编码为字符串
	 * 
	 * @param bytes
	 *            待编码的byte数组
	 * @return 编码后的字符串
	 * */
	public static String encode(byte[] bytes) {
		if(mBase64Encoder!=null){
			Log.d(TAG, "-----使用成员变量处创建的BASE64Encoder对象-----");
			return mBase64Encoder.encode(bytes);
		}
		Log.d(TAG, "-----重新创建BASE64Encoder对象-----");
		return new BASE64Encoder().encode(bytes);
	}
	
	/**
	 * TODO:将以Base64方式编码的字符串解码为byte数组
	 * 
	 * @param encodeStr
	 *            待解码的字符串
	 * @return 解码后的byte数组
	 * @throws IOException
	 * */
	public static byte[] decode(String encodeStr) throws IOException {
		byte[] bt = null;
		BASE64Decoder decoder = new BASE64Decoder();
		bt = decoder.decodeBuffer(encodeStr);
		return bt;
	}

	/**
	 * TODO:将两个byte数组连接起来后，返回连接后的Byte数组
	 * 
	 * @param front
	 *            拼接后在前面的数组
	 * @param after
	 *            拼接后在后面的数组
	 * @return 拼接后的数组
	 * */
	public static byte[] connectBytes(byte[] front, byte[] after) {
		byte[] result = new byte[front.length + after.length];
		System.arraycopy(front, 0, result, 0, after.length);
		System.arraycopy(after, 0, result, front.length, after.length);
		return result;
	}

	/**
	 * TODO:将图片以Base64方式编码为字符串
	 * 
	 * @param imgUrl
	 *            图片的绝对路径（例如：D:\\jsonimg\\123.jpg）
	 * @return 编码后的字符串
	 * @throws IOException
	 * */
	public static String encodeImage(String imgUrl) throws IOException {//bitmap--byts
		FileInputStream fis = new FileInputStream(imgUrl);
		byte[] rs = new byte[fis.available()];//bitmap.getBytes();
		fis.read(rs);
		fis.close();

		// 转为 bitmap
		Bitmap bitmap = Bytes2Bitmap(rs);
		// 压缩图片
		Bitmap newbitmap = resizeBitmap(bitmap);
		//
		byte[] newbytes = Bitmap2Bytes(newbitmap);
		return encode(newbytes);
	}

	// byte[]转换成Bitmap
	public static Bitmap Bytes2Bitmap(byte[] b) {
		if (b.length != 0) {
			return BitmapFactory.decodeByteArray(b, 0, b.length);
		}
		return null;
	}

	/**
	 * 将图片按照300*400的比例进行压缩 下述算法为核心逻辑
	 * 
	 */
	public static Bitmap resizeBitmap(Bitmap bmp) {
		// int scale = 1; //要保证相对大(无论是宽还是高相对大)的图片显示完整
		Bitmap bitmap = bmp;
		float width = bitmap.getWidth();
		float height = bitmap.getHeight();

		// 我们固定的像素数
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
	 * 把Bitmap转Byte
	 * 
	 * @Author HEH
	 * @EditTime 2010-07-19 上午11:45:56
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
