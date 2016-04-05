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
 * ע��:
 *    ���յ�ʱ��������Ԥ������
 *    
 *    SurfaceHolder.Callback():����Ԥ������ͷ��Ƶ
 *    http://stackoverflow.com/questions/17674634/saving-and-reading-bitmaps-images-from-internal-memory-in-android
 *    
 *    �Ż�1��onPreFrame�еķ�����ȡ���˹����࣬����
 *    
 *    
 *    SurfaceView���������Ŀؼ�,ռ�õ�cpu,�ڴ����ıȽϴ�
 *    SurfaceViewһ������ʱ��,˫�����л���,ռ�õ���Դ�Ƚϴ�
 *    
 *    ����ɼ�֮ǰ,�س�ռ�õ���Դ���ᱻ�ͷŵ����س�Ҳ�ᱻ������
 *    ֻ�н���ɼ�֮ǰ������ִ���߼�
 *    
 *    
 *    ע��MainActivity��onPause()��������surfaceDestroy()ִ��,
 *    ��Դ���ͷ�(����VideoView��Camera)��Ӧ����surfaceDestroy()�л���
 *    Activity��onPause()�����м��н��У������ܷ������в���
 *    
 */
public class MainActivity extends Activity implements SurfaceHolder.Callback,PreviewCallback{

	private static final String TAG = MainActivity.class.getSimpleName();
	private Camera mCamera;
	private SurfaceView camera_preview;
	private SurfaceHolder mySurfaceHolder;
	
	/**
	 * ����JNI����
	 */
	private PeopleDet peopleDet;
	
	
	/**
	 * �����������ͷ����:
	 *   0 : ǰ��
	 *   1 : ����
	 */
	private int cameraTypeInt;
	
	/**
	 * ͼƬ�洢��·��
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
	 * ��ʼ��
	 *  1���õ�ȫ�ֵ�MainActivity:����BitmapUtils���е�getBitmapFile()�������е���
	 *  2����ȡ�洢ͼƬ��·��
	 *    /data/data/com.turing.facerecognizationdemo/app_imageDir/profile.jpg
	 *  3��������������ʶ���ʵ���������г�ʼ��
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
	 * �����ֻ���ǰ������ͷ
	 */
	private void openBeforeCamera(int type) {
		mCamera = CameraInerface.getCameraInstance(type);
	}
	
	private RotateTextView rotateTextView;
	
	
	private int reduceFrame = 3;
	
	/**
	 * ��ǰ������data��ʵʱԤ����֡����
	 *   1�����߳�
	 *   2����byte����ת����bmp
	 *   3������Bitmap
	 *   data �ĳ���Ĭ���ǰ��򼶵ġ�
	 */
	@Override
	public void onPreviewFrame(final byte[] data, final Camera camera) {
		if(reduceFrame%reduceFrame==0){
			Bitmap bmp = BitmapUtils.transformByte2Bitmap(data, camera);//---->130ms���� : С��:2048---1536 HW:640*480
			Log.d(TAG, "����֡ͼƬ�Ŀ���� : " + bmp.getWidth()+",�߶��� : " + bmp.getHeight());//
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
	 * ���õ�˳���� : 
	 *    onResume() : ��ȡ�������ʵ��
	 *    surfaceCreated()
	 *    surfaceChanged():ע��,�˷���ֻ����һ��
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
	 * ֻҪ��ǰ�Ľ���ʧȥ�˽���,�ͽ�����ͷ�ͷŵ�
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
			
			//����ɱ�����򣬷�ֹ����camera is being used after release
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
