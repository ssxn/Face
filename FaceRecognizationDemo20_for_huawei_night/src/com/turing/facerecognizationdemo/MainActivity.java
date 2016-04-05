package com.turing.facerecognizationdemo;

import java.io.File;

import android.app.Activity;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;

import com.turing.facerecognizationdemo.engine.DrawRectClass;
import com.turing.facerecognizationdemo.utils.BitmapUtils;
import com.turing.facerecognizationdemo.utils.CameraInerface;
import com.turing.facerecognizationdemo.utils.ParamsFactory;
import com.turing.facerecognizationdemo.view.MyOverlayView;
import com.turing.facerecognizationdemo.view.RotateTextView;
import com.tzutalin.dlib.PeopleDet;

/**
 * 注意:
 *    拍照的时候必须得有预览界面
 *    
 *    SurfaceHolder.Callback():用来预览摄像头视频
 *    http://stackoverflow.com/questions/17674634/saving-and-reading-bitmaps-images-from-internal-memory-in-android
 *    
 *    优化1、onPreFrame中的方法提取到了工具类，并且
 *    
 *    
 *    SurfaceView是重量级的控件,占用的cpu,内存消耗比较大
 *    SurfaceView一创建的时候,双缓冲切换器,占用的资源比较大
 *    
 *    界面可见之前,县城占用的资源都会被释放掉，县城也会被结束掉
 *    只有界面可见之前，才能执行逻辑
 *    
 *    
 *    注意MainActivity的onPause()方法先于surfaceDestroy()执行,
 *    资源是释放(包括VideoView、Camera)都应该在surfaceDestroy()中或者
 *    Activity的onPause()方法中集中进行，而不能分来进行操作
 *    
 */
public class MainActivity extends Activity implements SurfaceHolder.Callback,PreviewCallback{

	private static final String TAG = MainActivity.class.getSimpleName();
	private Camera mCamera;
	private SurfaceView camera_preview;
	private SurfaceHolder mySurfaceHolder;
	
	/**
	 * 调用JNI的类
	 */
	private PeopleDet peopleDet;
	
	
	/**
	 * 照相机的摄像头类型:
	 *   0 : 前置
	 *   1 : 后置
	 */
	private int cameraTypeInt;
	
	/**
	 * 图片存储的路径
	 */
	private File imageFile;
	private MyOverlayView myOverlayView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		Log.d(TAG, "==========onCreate=============");
		init();
	}
	
	/**
	 * 初始化
	 *  1、拿到全局的MainActivity:方便BitmapUtils类中的getBitmapFile()方法进行调用
	 *  2、获取存储图片的路径
	 *    /data/data/com.turing.facerecognizationdemo/app_imageDir/profile.jpg
	 *  3、创建调用人脸识别的实例，并进行初始化
	 */
	private void init() {
		imageFile = BitmapUtils.getBitmapFile(this);
		peopleDet = new PeopleDet();
		peopleDet.init();
		
		myOverlayView = (MyOverlayView) findViewById(R.id.myOverlayView);
		camera_preview = (SurfaceView) findViewById(R.id.camera_preview);
		rotateTextView = (RotateTextView) findViewById(R.id.rotateTextView);
		
		mySurfaceHolder = camera_preview.getHolder();
		mySurfaceHolder.addCallback(this);

	    rotateTextView.setDegrees(0);
//		myOverlayView.setDegrees(90);
	}

	/**
	 * 开启手机的前置摄像头
	 */
	private void openBeforeCamera(int type) {
		mCamera = CameraInerface.getCameraInstance(type);
	}
	
	private RotateTextView rotateTextView;
	
	
	private int reduceFrame = 3;
	
	/**
	 * 当前方法的data是实时预览的帧数据
	 *   1、主线程
	 *   2、将byte数组转化成bmp
	 *   3、处理Bitmap
	 *   data 的长度默认是百万级的。
	 */
	@Override
	public void onPreviewFrame(final byte[] data, final Camera camera) {
		if(reduceFrame%reduceFrame==0){
			Bitmap bmp = BitmapUtils.transformByte2Bitmap(data, camera);//---->130ms左右 : 小米:2048---1536 HW:640*480
			Log.d(TAG, "生成帧图片的宽度是 : " + bmp.getWidth()+",高度是 : " + bmp.getHeight());//
			//SaveBitmapUtils.saveCroppedImage(bmp);
			if(bmp!=null){
				DrawRectClass.handleBitmap(cameraTypeInt,myOverlayView,peopleDet,imageFile,bmp);
			}
		}
		reduceFrame++;
		if(reduceFrame==99){
			reduceFrame = 3;
		}
	}
	
	/**
	 * 调用的顺序是 : 
	 *    onResume() : 获取照相机的实例
	 *    surfaceCreated()
	 *    surfaceChanged():注意,此方法只调用一次
	 *    onPause()
	 *    surfaceDestory();
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.i(TAG, "---surfaceCreated---" );
		try {
			if (mCamera != null) {
				mCamera.setDisplayOrientation(90);
				mCamera.setOneShotPreviewCallback(this);
				mCamera.setPreviewDisplay(mySurfaceHolder);
				mCamera.startPreview();
				mCamera.setPreviewCallback(this);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * width : 1080
	 * height : 1860
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.i(TAG, "---surfaceChanged---" );
	}
	
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.i(TAG, "---surfaceDestroyed---" );
	}

	/**
	 * 只要当前的界面失去了焦点,就将摄像头释放掉
	 */
	@Override
	protected void onPause() {
		super.onPause();
		Log.i(TAG, "--------onPause--------------");
		if(mCamera!=null){
			mCamera.stopPreview();
			mCamera.setOneShotPreviewCallback(null);
			mCamera.release();
			mCamera = null;
			
			//彻底杀死程序，防止出现camera is being used after release
			android.os.Process.killProcess(android.os.Process.myPid()); 
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "------onResume-------");
		String cameraTypeString = ParamsFactory.createCamera();
		cameraTypeInt = Integer.parseInt(cameraTypeString);
		openBeforeCamera(cameraTypeInt);
	}
}
