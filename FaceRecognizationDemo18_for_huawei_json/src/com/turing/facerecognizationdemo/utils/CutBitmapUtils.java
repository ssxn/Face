package com.turing.facerecognizationdemo.utils;

import android.graphics.Bitmap;
import android.util.Log;

import com.turing.facerecognizationdemo.view.MyOverlayView.Box;

public class CutBitmapUtils {
	
	
	private static final String TAG = CutBitmapUtils.class.getSimpleName();
	private static float scale = 1.3f;
	
	/**
	 * 计算新的坐标系
	 * this.mLeft = (int)(sh*(height - bottom));
	 *  
	 *  自定义view(MyOverlayView)的宽高分别是 : 小米:[rw:1860] , [rh : 1080]  | 华为[rw:1196],[rh：670]
 	 *  
	 *  思路: 
	 *   1、我们的逻辑是，将摄像头获取的Bitmap的(2048*1536)根据一定的比例进行压缩，获取一个新的Bitmap--》resultBitmap
	 *   2、将resultBitmap压缩到本地，并将本地路径传递给JNI,JNI识别出人脸坐标集合
	 *   3、将人脸坐标集合解析后绘制到自定义View(MyOverlayView)上
	 *   4、****这里一定要注意的是:
	 *          MyOverlayView的宽高分别是 : 1860*1080
	 *          Bitmap的宽高分别是 : 2048*1536
	 *       而我们绘制的时候，是按照MyOverlayView的宽高与Bitmap的宽高进行对比，最后【绘制到MyOverlayView】身上
	 *       也就是说人脸识别框是相对于MyOverlayView而言的
	 *   5、要想抠出识别框对应的Bitmap的相应位置,还需要进行计算,步骤如下
	 *      (1)通过回调获取识别框相对于MyOverlayView的左、上、右、下位置
	 *      (2)获取MyOverlayView与Bitmap宽高比例 【scaleW 和 scaleH】
	 *      (3)获取识别框的【横铺】宽高,
	 *      (4)将识别框的横铺宽高放大1.3倍
	 *      (5)计算出在MyOverlayView层面上的 : 新的识别框宽高(1.3倍)与原先识别框宽高的差值并 /2
	 *      (6)计算出MyOverlayView层面的【截取Bitmap时候】开始坐标(x,y)--->原点坐标是手机平放时候的右上角
	 *         x:与右侧边界的距离
	 *         y:与上侧边界的距离
	 *      (7)根据上面计算出的scaleW和scaleH计算出在Bitmap层面的识别框的宽高
	 *      (8)根据上面计算出的scaleW和scaleH计算出Bitmap层面的开始坐标
	 *      (9)使用Bitmap.create(五个参数的构造方法)方法截取图片,并返还
	 *  
	 */
	public static Bitmap cutBitmapByBox(int mCameraType,Box box,Bitmap resourceBitmap,float myViewWidth, float myViewHeight){
//		int x;//Bitmap层面截取图片的原点x坐标
//		int y; //Bitmap层面截取图片的原点y坐标
		int myView_x = 0;
		int myView_y = 0;
		if(box!=null && resourceBitmap!=null){
			 
			int sourceLeft = box.getLeft();
			int sourceTop = box.getTop();
			int sourceRight = box.getRight();
			int sourceBottom = box.getBottom(); //当前得到的是在自定义控件层面的距离
			Log.d(TAG, "自定义控件层面的坐标是  左 : " + sourceLeft + ", 上 : " + sourceTop + ", 右 : " + sourceRight + ",下 : " +sourceBottom);
			
			//获取自定义控件的宽度/Bitmap的宽度，自定义控件的高度/Bitmap的高度
			float scaleW = (float)(myViewWidth/resourceBitmap.getWidth());
			float scaleH = (float)(myViewHeight/resourceBitmap.getHeight());
			
			//对于华为手机而言,自定义控件的宽度 : 1196,高度 :670,bitmap的宽度:640 ,高度 :480
			Log.d(TAG, "view的宽度" + myViewWidth+ ", 高度: " + myViewHeight + ",resourceBitmap的宽度 : " + resourceBitmap.getWidth()+",高度是 : " + resourceBitmap.getHeight());//1196,670;640,480
			Log.d(TAG, "view与bitmap的宽度之比是 : " + scaleW+ ", 高度之比是 : " + scaleH);//1.87,1.40
			
			//自定义控件层面的：识别框的匡高分别是realWidth,和sourceWidth(以手机平铺效果为例)
//			int sourceWidth = sourceRight - sourceLeft;
//			int sourceHeight = sourceBottom - sourceTop;
//			int realWidth = sourceHeight;
//			int realHeight = sourceWidth;
			
			int realWidth = sourceRight - sourceLeft;
			int realHeight = sourceBottom - sourceTop;
			Log.d(TAG, "自定义控件层面的：识别框的匡高分别是 : " + realWidth + ","+realHeight);//415,307
			
			int newWidth = (int)(scale*realWidth);
			int newHeight = (int)(scale*realHeight);
			Log.d(TAG, "---自定义控件层面,1.3倍以后--newWidth : " + newWidth + ",newHeight : " +newHeight);//539,399
			
			int D_value_of_horizatal = (newWidth - realWidth)/2; //62
			int D_value_of_vertical = (newHeight - realHeight)/2; //46
			Log.d(TAG, "D_value_of_horizatal: " + D_value_of_horizatal + ",D_value_of_vertical :" + D_value_of_vertical);
			
//			由于华为的手机和小米的手机的绘制坐标原点不同,故有所改变
//			int myView_x = (int)(myViewWidth - (sourceBottom+D_value_of_horizatal));
//			int myView_y = (int)(myViewHeight -( sourceRight+D_value_of_vertical));
			
			
			if(mCameraType==1){ //前置摄像头,抠图的坐标原点在右上角,[方向向左]进行扣取
				 myView_x = (int)(myViewWidth - (sourceRight+D_value_of_horizatal));
			}else{ //后置摄像头,抠图的原点坐标在左上角,[方向向右]进行扣取
				myView_x = (int)(sourceLeft-D_value_of_horizatal);
			}
			myView_y = (int)(sourceTop-D_value_of_vertical);
			Log.d(TAG, "---自定义控件层面,识别框的坐标原点是 : (" +myView_x+","+ myView_y+")");
			
			int x = (int) (myView_x/scaleW);
			int y = (int)(myView_y/scaleH);
			Log.d(TAG, "---截取Bitmap的开始点坐标是  ( " + x + "," + y +")");
			
			int bitmapW = (int)(newWidth/scaleW);
			int bitmapH = (int)(newHeight/scaleH);
			Log.d(TAG, "--截取Bitmap的宽度是 : " + bitmapW + ",高度是 : " + bitmapH);
			
			Log.d(TAG, "x + bitmapW =" +(x + bitmapW) + ",resourceBitmap.getWidth() : " + resourceBitmap.getWidth());
			Log.d(TAG, "y + bitmapH =" +(y + bitmapH) + ",resourceBitmap.getHeight() : " + resourceBitmap.getHeight());
			
			if((x + bitmapW)<= resourceBitmap.getWidth() && (y + bitmapH)<=resourceBitmap.getHeight() && y>=0 && x>=0){
				Log.d(TAG, "------正在抠图-----");
				Bitmap bmp = Bitmap.createBitmap(resourceBitmap, x, y, bitmapW, bitmapH);
				return bmp;
			}
			
		}
		return null;
	}
}
