package com.turing.facerecognizationdemo.utils;

import android.graphics.Bitmap;
import android.util.Log;

import com.turing.facerecognizationdemo.view.MyOverlayView.Box;

public class CutBitmapUtils {
	
	
	private static final String TAG = CutBitmapUtils.class.getSimpleName();
	private static float scale = 1.3f;
	
	/**
	 * �����µ�����ϵ
	 * this.mLeft = (int)(sh*(height - bottom));
	 *  
	 *  �Զ���view(MyOverlayView)�Ŀ�߷ֱ��� : С��:[rw:1860] , [rh : 1080]  | ��Ϊ[rw:1196],[rh��670]
 	 *  
	 *  ˼·: 
	 *   1�����ǵ��߼��ǣ�������ͷ��ȡ��Bitmap��(2048*1536)����һ���ı�������ѹ������ȡһ���µ�Bitmap--��resultBitmap
	 *   2����resultBitmapѹ�������أ���������·�����ݸ�JNI,JNIʶ����������꼯��
	 *   3�����������꼯�Ͻ�������Ƶ��Զ���View(MyOverlayView)��
	 *   4��****����һ��Ҫע�����:
	 *          MyOverlayView�Ŀ�߷ֱ��� : 1860*1080
	 *          Bitmap�Ŀ�߷ֱ��� : 2048*1536
	 *       �����ǻ��Ƶ�ʱ���ǰ���MyOverlayView�Ŀ����Bitmap�Ŀ�߽��жԱȣ���󡾻��Ƶ�MyOverlayView������
	 *       Ҳ����˵����ʶ����������MyOverlayView���Ե�
	 *   5��Ҫ��ٳ�ʶ����Ӧ��Bitmap����Ӧλ��,����Ҫ���м���,��������
	 *      (1)ͨ���ص���ȡʶ��������MyOverlayView�����ϡ��ҡ���λ��
	 *      (2)��ȡMyOverlayView��Bitmap��߱��� ��scaleW �� scaleH��
	 *      (3)��ȡʶ���ġ����̡����,
	 *      (4)��ʶ���ĺ��̿�߷Ŵ�1.3��
	 *      (5)�������MyOverlayView�����ϵ� : �µ�ʶ�����(1.3��)��ԭ��ʶ����ߵĲ�ֵ�� /2
	 *      (6)�����MyOverlayView����ġ���ȡBitmapʱ�򡿿�ʼ����(x,y)--->ԭ���������ֻ�ƽ��ʱ������Ͻ�
	 *         x:���Ҳ�߽�ľ���
	 *         y:���ϲ�߽�ľ���
	 *      (7)��������������scaleW��scaleH�������Bitmap�����ʶ���Ŀ��
	 *      (8)��������������scaleW��scaleH�����Bitmap����Ŀ�ʼ����
	 *      (9)ʹ��Bitmap.create(��������Ĺ��췽��)������ȡͼƬ,������
	 *  
	 */
	public static Bitmap cutBitmapByBox(int mCameraType,Box box,Bitmap resourceBitmap,float myViewWidth, float myViewHeight){
//		int x;//Bitmap�����ȡͼƬ��ԭ��x����
//		int y; //Bitmap�����ȡͼƬ��ԭ��y����
		int myView_x = 0;
		int myView_y = 0;
		if(box!=null && resourceBitmap!=null){
			 
			int sourceLeft = box.getLeft();
			int sourceTop = box.getTop();
			int sourceRight = box.getRight();
			int sourceBottom = box.getBottom(); //��ǰ�õ��������Զ���ؼ�����ľ���
			Log.d(TAG, "�Զ���ؼ������������  �� : " + sourceLeft + ", �� : " + sourceTop + ", �� : " + sourceRight + ",�� : " +sourceBottom);
			
			//��ȡ�Զ���ؼ��Ŀ��/Bitmap�Ŀ�ȣ��Զ���ؼ��ĸ߶�/Bitmap�ĸ߶�
			float scaleW = (float)(myViewWidth/resourceBitmap.getWidth());
			float scaleH = (float)(myViewHeight/resourceBitmap.getHeight());
			
			//���ڻ�Ϊ�ֻ�����,�Զ���ؼ��Ŀ�� : 1196,�߶� :670,bitmap�Ŀ��:640 ,�߶� :480
			Log.d(TAG, "view�Ŀ��" + myViewWidth+ ", �߶�: " + myViewHeight + ",resourceBitmap�Ŀ�� : " + resourceBitmap.getWidth()+",�߶��� : " + resourceBitmap.getHeight());//1196,670;640,480
			Log.d(TAG, "view��bitmap�Ŀ��֮���� : " + scaleW+ ", �߶�֮���� : " + scaleH);//1.87,1.40
			
			//�Զ���ؼ�����ģ�ʶ���Ŀ�߷ֱ���realWidth,��sourceWidth(���ֻ�ƽ��Ч��Ϊ��)
//			int sourceWidth = sourceRight - sourceLeft;
//			int sourceHeight = sourceBottom - sourceTop;
//			int realWidth = sourceHeight;
//			int realHeight = sourceWidth;
			
			int realWidth = sourceRight - sourceLeft;
			int realHeight = sourceBottom - sourceTop;
			Log.d(TAG, "�Զ���ؼ�����ģ�ʶ���Ŀ�߷ֱ��� : " + realWidth + ","+realHeight);//415,307
			
			int newWidth = (int)(scale*realWidth);
			int newHeight = (int)(scale*realHeight);
			Log.d(TAG, "---�Զ���ؼ�����,1.3���Ժ�--newWidth : " + newWidth + ",newHeight : " +newHeight);//539,399
			
			int D_value_of_horizatal = (newWidth - realWidth)/2; //62
			int D_value_of_vertical = (newHeight - realHeight)/2; //46
			Log.d(TAG, "D_value_of_horizatal: " + D_value_of_horizatal + ",D_value_of_vertical :" + D_value_of_vertical);
			
//			���ڻ�Ϊ���ֻ���С�׵��ֻ��Ļ�������ԭ�㲻ͬ,�������ı�
//			int myView_x = (int)(myViewWidth - (sourceBottom+D_value_of_horizatal));
//			int myView_y = (int)(myViewHeight -( sourceRight+D_value_of_vertical));
			
			
			if(mCameraType==1){ //ǰ������ͷ,��ͼ������ԭ�������Ͻ�,[��������]���п�ȡ
				 myView_x = (int)(myViewWidth - (sourceRight+D_value_of_horizatal));
			}else{ //��������ͷ,��ͼ��ԭ�����������Ͻ�,[��������]���п�ȡ
				myView_x = (int)(sourceLeft-D_value_of_horizatal);
			}
			myView_y = (int)(sourceTop-D_value_of_vertical);
			Log.d(TAG, "---�Զ���ؼ�����,ʶ��������ԭ���� : (" +myView_x+","+ myView_y+")");
			
			int x = (int) (myView_x/scaleW);
			int y = (int)(myView_y/scaleH);
			Log.d(TAG, "---��ȡBitmap�Ŀ�ʼ��������  ( " + x + "," + y +")");
			
			int bitmapW = (int)(newWidth/scaleW);
			int bitmapH = (int)(newHeight/scaleH);
			Log.d(TAG, "--��ȡBitmap�Ŀ���� : " + bitmapW + ",�߶��� : " + bitmapH);
			
			Log.d(TAG, "x + bitmapW =" +(x + bitmapW) + ",resourceBitmap.getWidth() : " + resourceBitmap.getWidth());
			Log.d(TAG, "y + bitmapH =" +(y + bitmapH) + ",resourceBitmap.getHeight() : " + resourceBitmap.getHeight());
			
			if((x + bitmapW)<= resourceBitmap.getWidth() && (y + bitmapH)<=resourceBitmap.getHeight() && y>=0 && x>=0){
				Log.d(TAG, "------���ڿ�ͼ-----");
				Bitmap bmp = Bitmap.createBitmap(resourceBitmap, x, y, bitmapW, bitmapH);
				return bmp;
			}
			
		}
		return null;
	}
}
