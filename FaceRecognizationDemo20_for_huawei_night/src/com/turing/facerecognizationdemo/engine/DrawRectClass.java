package com.turing.facerecognizationdemo.engine;

import java.io.File;
import java.util.List;

import android.graphics.Bitmap;
import android.os.SystemClock;
import android.util.Log;

import com.turing.facerecognizationdemo.bean.MarkingBean;
import com.turing.facerecognizationdemo.bean.ServerData;
import com.turing.facerecognizationdemo.bean.ServerData.EventBusObject;
import com.turing.facerecognizationdemo.utils.BitmapUtils;
import com.turing.facerecognizationdemo.utils.CutBitmapUtils;
import com.turing.facerecognizationdemo.utils.SaveBitmapUtils;
import com.turing.facerecognizationdemo.utils.UploadBitmapUtils;
import com.turing.facerecognizationdemo.view.MyOverlayView;
import com.turing.facerecognizationdemo.view.MyOverlayView.Box;
import com.turing.facerecognizationdemo.view.MyOverlayView.ICoordinationInterface;
import com.tzutalin.dlib.PeopleDet;
import com.tzutalin.dlib.VisionDetRet;

import de.greenrobot.event.EventBus;

public class DrawRectClass {

	private static final String TAG = DrawRectClass.class.getSimpleName();

	/**
	 * ��Bitmap���д��� 
	 * 1����ÿһ֡ͼƬ��400*300Ϊ��׼��������,��ʱ:2-3ms |��600*450Ϊ��׼�������ź�ʱ 7-14���룬�󲿷ֱ�����9ms����
	 * 2����bitmapд������,���õ�bitmap��·�� :/data/data/com.turing.facerecognizationdemo/app_imageDir/profile.jpg,��ʱ:9--10ms 
	 * 3������JNI�ļ�,��ȡ���꼯��List<VisionDetRet> detFace,��ʱ:60-70ms
	 * 4����������,�������ϻ��� 
	 * 5����ʶ���ĳ��������30%������ͼƬ�۳������ϴ��������� ����ǰͼƬ��2048,�� : 1536,��߱��� :1.3333334 ���ź�ͼƬ��300,�� : 225,��߱��� : 1.3333334
	 * ���ݽ�����bmp�Ŀ����� : 2048*1536
	 */
	public static void handleBitmap(final int mCameraType,MyOverlayView myOverlayView, PeopleDet peopleDet, File imageFile,Bitmap bmp) {
//		Log.d(TAG, "ѹ��ǰ,bmp.getWidth() : " + bmp.getWidth()+",bmp.getHeight(): " + bmp.getHeight());
		Bitmap bitmap = BitmapUtils.resizeBitmap(bmp);
//		SaveBitmapUtils.saveCroppedImage(bitmap);
//		Log.d(TAG, "------bitmap.getWidth() : " + bitmap.getWidth() +"bitmap.getHeight() : " + bitmap.getHeight());
		String bitmapPath = BitmapUtils.saveToInternalStorage(imageFile, bitmap);
		
		List<VisionDetRet> detFace = peopleDet.detFace(bitmapPath);
		
		final Bitmap bitmap2 = bmp;//ѹ��֮ǰ��ͼƬ
		if (detFace != null && detFace.size() > 0) { // ����ʶ���;ͨ���ص������������û����Ա���д���
			myOverlayView.setFacesRect(mCameraType, bitmap, detFace,detFace.size(), new ICoordinationInterface() {
				@Override
				public void coordinationCallback(final Box box,final float myViewWidth, final float myViewHeight) { // ���߳�
					handleBitmapCallback(mCameraType,bitmap2, box, myViewWidth,myViewHeight);
				}
			});
		} else { // û��ʶ�������
			Log.d(TAG, "--û�м�������,");
			EventBus.getDefault().post("");
			System.gc();
		}
	}
	
	private static long flagTime = -1;
	
	/**
	 * ����ص��ķ���
	 * @param mCameraType ����ͷ������ [1:ǰ��][0:����]
	 * @param bitmap2
	 * @param box
	 * @param myViewWidth
	 * @param myViewHeight
	 */
	private static void handleBitmapCallback(int mCameraType,final Bitmap bitmap2, final Box box,final float myViewWidth, final float myViewHeight) { //200ms����һ��
		if((System.currentTimeMillis() - flagTime)>2000){//TODO
			Log.d(TAG, "--------(System.currentTimeMillis() - flagTime) : " + (System.currentTimeMillis() - flagTime)) ;
//			Bitmap cutedBitmap = CutBitmapUtils.cutBitmapByBox(box, bitmap2, myViewWidth, myViewHeight);//TODO
			Bitmap cutedBitmap = CutBitmapUtils.cutBitmapByBox(mCameraType,box, bitmap2, myViewWidth, myViewHeight);//TODO
//			cutedBitmap = BitmapUtils.resizeBitmap2(cutedBitmap); //���ٳ�����ͼƬ��300*300Ϊ����׼�ٴ�����
			
			if(cutedBitmap!=null){
				Log.d(TAG, "----cutedBitmap��Ϊnull,�ϴ���������------");
				SaveBitmapUtils.saveCroppedImage(cutedBitmap);//Ϊ�˼��۳�����ͼƬ(��300*300)Ϊ��׼��ͼƬ��ʲô����(������Ϊԭ����30%�ǳ�ϸ��)
				UploadBitmapUtils.uploadBitmap2Server(cutedBitmap,box);//server��û���Ļ���ʱ300--400ms
			}
			flagTime = System.currentTimeMillis();
		}
	}
}
