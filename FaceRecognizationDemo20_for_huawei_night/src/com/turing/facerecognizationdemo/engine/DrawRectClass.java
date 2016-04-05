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
	 * 对Bitmap进行处理 
	 * 1、将每一帧图片以400*300为基准进行缩放,耗时:2-3ms |以600*450为基准进行缩放耗时 7-14毫秒，大部分保持在9ms左右
	 * 2、将bitmap写进本地,并得到bitmap的路径 :/data/data/com.turing.facerecognizationdemo/app_imageDir/profile.jpg,耗时:9--10ms 
	 * 3、调用JNI文件,获取坐标集合List<VisionDetRet> detFace,耗时:60-70ms
	 * 4、解析坐标,往人脸上画框 
	 * 5、将识别框的长宽各增加30%，并将图片扣出来，上传至服务器 缩放前图片宽2048,高 : 1536,宽高比是 :1.3333334 缩放后图片宽300,高 : 225,宽高比是 : 1.3333334
	 * 传递进来的bmp的宽、高是 : 2048*1536
	 */
	public static void handleBitmap(final int mCameraType,MyOverlayView myOverlayView, PeopleDet peopleDet, File imageFile,Bitmap bmp) {
//		Log.d(TAG, "压缩前,bmp.getWidth() : " + bmp.getWidth()+",bmp.getHeight(): " + bmp.getHeight());
		Bitmap bitmap = BitmapUtils.resizeBitmap(bmp);
//		SaveBitmapUtils.saveCroppedImage(bitmap);
//		Log.d(TAG, "------bitmap.getWidth() : " + bitmap.getWidth() +"bitmap.getHeight() : " + bitmap.getHeight());
		String bitmapPath = BitmapUtils.saveToInternalStorage(imageFile, bitmap);
		
		List<VisionDetRet> detFace = peopleDet.detFace(bitmapPath);
		
		final Bitmap bitmap2 = bmp;//压缩之前的图片
		if (detFace != null && detFace.size() > 0) { // 绘制识别框;通过回调方法将数据拿回来以便进行处理
			myOverlayView.setFacesRect(mCameraType, bitmap, detFace,detFace.size(), new ICoordinationInterface() {
				@Override
				public void coordinationCallback(final Box box,final float myViewWidth, final float myViewHeight) { // 主线程
					handleBitmapCallback(mCameraType,bitmap2, box, myViewWidth,myViewHeight);
				}
			});
		} else { // 没有识别出人脸
			Log.d(TAG, "--没有检测出人脸,");
			EventBus.getDefault().post("");
			System.gc();
		}
	}
	
	private static long flagTime = -1;
	
	/**
	 * 处理回调的方法
	 * @param mCameraType 摄像头的类型 [1:前置][0:后置]
	 * @param bitmap2
	 * @param box
	 * @param myViewWidth
	 * @param myViewHeight
	 */
	private static void handleBitmapCallback(int mCameraType,final Bitmap bitmap2, final Box box,final float myViewWidth, final float myViewHeight) { //200ms调用一次
		if((System.currentTimeMillis() - flagTime)>2000){//TODO
			Log.d(TAG, "--------(System.currentTimeMillis() - flagTime) : " + (System.currentTimeMillis() - flagTime)) ;
//			Bitmap cutedBitmap = CutBitmapUtils.cutBitmapByBox(box, bitmap2, myViewWidth, myViewHeight);//TODO
			Bitmap cutedBitmap = CutBitmapUtils.cutBitmapByBox(mCameraType,box, bitmap2, myViewWidth, myViewHeight);//TODO
//			cutedBitmap = BitmapUtils.resizeBitmap2(cutedBitmap); //将抠出来的图片以300*300为最大基准再次缩放
			
			if(cutedBitmap!=null){
				Log.d(TAG, "----cutedBitmap不为null,上传至服务器------");
				SaveBitmapUtils.saveCroppedImage(cutedBitmap);//为了检测扣出来的图片(以300*300)为基准的图片是什么样子(质量变为原来的30%非常细腻)
				UploadBitmapUtils.uploadBitmap2Server(cutedBitmap,box);//server端没开的话耗时300--400ms
			}
			flagTime = System.currentTimeMillis();
		}
	}
}
