package com.turing.facerecognizationdemo.view;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.tzutalin.dlib.VisionDetRet;


public class MyOverlayView extends View{

	private final static String tag = "MyOverlayView";
	private static final int DRAW_RECT = 0;
	private Map<String, Box> boxMap = new ConcurrentHashMap<String, Box>();
	private Paint p;
	
	
	/**
	 * 构造方法
	 * @param context
	 * @param attrs
	 */
	public MyOverlayView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	/**
	 * 初始化:
	 *   1、注册EventBus:画文字的时候使用
	 *   2、初始化画笔:
	 */
	private void init() {
		p = new Paint();
		p.setColor(Color.GREEN);
		p.setStyle(Style.STROKE);
		p.setStrokeWidth(3);
		
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DRAW_RECT://刷新人脸识别框
				invalidate();
				break;
				
			default:
				break;
			}
		}
	};
	
	/**
	 * 当前自定义控件的宽
	 */
	private float rw; //1860
	
	/**
	 * 当前控件的高
	 */
	private float rh; //1080
	
	/**
	 * 一定要有这个方法
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

//		rw = this.getMeasuredHeight(); //hw:670
//		rh = this.getMeasuredWidth(); //hw:1196
		
		rh = this.getMeasuredHeight(); //670------------TODO
		rw = this.getMeasuredWidth(); //1196------------TODO
		Log.i(tag, "当前自定义view的 分辨率：" + rw + "*" + rh + ",this.getWidth() ; " + this.getWidth()+",this.getHeight():" +this.getHeight());
	}
	
	/**
	 * 当前控件的宽/Bitmap的宽
	 */
	private float sw ; //6.2
	
	/**
	 * 屏幕显示区域的高/safaceView的高
	 */
	private float sh; //4.8
	
	/**
	 * 摄像头的类型
	 */
	private int mCameraType;
	
	
	private long startJNI;
	
	/**
	 * 画框
	 * 
	 *         MainActivity中的核心逻辑是: 
	 *         1、将人脸Bitmap压缩 
	 *         2、存到指定的路径中 
	 *         3、调用jni得到坐标集合
	 * 
	 *         因此我们在这个方法里面， 
	 *         1、得到原来的Bitmap : 
	 *         2、确定缩放率 
	 *         3、computeBox
	 *         
	 *         scale : 0.14648438
	 *         
	 *         Bitmap : 300*225(小米4c)
	 *         当前控件宽高 : 1860*1080(平铺)
	 *         
	 * @param facesRect
	 * @params width :
	 * 
	 */
	public void setFacesRect(long startJNI,int mCameraType,Bitmap bm,List<VisionDetRet> facesRect, int faceCount,ICoordinationInterface coordinationInterface) {
		this.startJNI = startJNI;
		this.mCameraType = mCameraType;
		int width = bm.getWidth();
		int height = bm.getHeight();
		
		Log.i(tag, "width : " + width+",height : "+height);
		
		if (faceCount == 0 || facesRect == null) {
			boxMap.clear();
			Log.i(tag, "boxMap size ---- setfaceRect:" + boxMap.size());
			handler.obtainMessage(DRAW_RECT).sendToTarget();
			return;
		}
		
		//当前控件的的宽/图片的宽之比
		this.sw = (float)this.rw/width; //1196/300  --->3.98
		
		//当前控件的的高/图片的高之比
		this.sh = (float)this.rh/height; //670/225 --->2.97
		
//		Log.d(tag, "sw : " + sw + ", sh : " + sh);
		
		boxMap.clear();
		for (VisionDetRet ret : facesRect) {
			String rect = String.valueOf(ret.getLeft())+ String.valueOf(ret.getTop())+ String.valueOf(ret.getRight())+String.valueOf(ret.getBottom());  
			Box box = computeBoxCoordination(width,height,(int)ret.getLeft(),(int) ret.getTop(),(int)ret.getRight(),(int)ret.getBottom());
			
			//回调方法，在DrawRectClass中获取
			coordinationInterface.coordinationCallback(box,rw,rh); //0ms
			
			boxMap.put(rect, box); //0ms
		}
		handler.obtainMessage(DRAW_RECT).sendToTarget();
	}
	
	
	private int mLeft;
	private int mTop;
	private int mRight;
	private int mBottom;

	/**
	 * 计算显示坐标
	 */
	private Box computeBoxCoordination(int width,int height,int left,int top,int right,int bottom){
		Log.d(tag, "---left: " + left + ",top : " + top + " , right : " + right + ", bottom : " + bottom);
		if(mCameraType == 1){//后置
//			this.mLeft = (int)(sh*(height - bottom));
//			this.mTop = (int)(sw*(width - right));
//			this.mRight = (int)(sh*(height - top));
//			this.mBottom = (int)(sw*(width - left));
			
			this.mLeft = (int)(sw*(width-right)); //手机横铺时候距离左边的距离
			this.mTop = (int)(sh*top);
			this.mRight = (int)(sw*(width-left));
			this.mBottom = (int)(sh*bottom);
			Log.e(tag,"-----换算之后的结果是 this.left : " + this.mLeft + ", this.mTop : " + this.mTop + ", this.mRight : " + this.mRight + ", this.mBottom : " + this.mBottom);
			return new Box(this.mLeft, this.mTop, this.mRight, this.mBottom);
		}else{
//			this.mLeft = (int)(sh*(height - bottom));
//			this.mTop = (int)(sw*left);
//			this.mRight = (int)(sh*(height - top));
//			this.mBottom = (int)(sw*right);
			this.mLeft = (int)(sw*left);
			this.mTop = (int)(sh*top);
			this.mRight = (int)(sw*right);
			this.mBottom = (int)(sh*bottom);
			return new Box(this.mLeft, this.mTop, this.mRight, this.mBottom);
		}
	}
	
	/**
	 * onDraw()方法由handler中的invalidate()方法进行调用
	 * 
	 * 华为手机在绘制的时候是以屏幕的左上角(也就是华为手机摄像头的那个角作为原点坐标画的)
	 */
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		if (boxMap == null ||boxMap.size() < 1) {
			return;
		}
		
//		canvas.rotate(degree, this.getWidth() / 2f, this.getHeight() / 2f);
//		canvas.rotate(degree, this.getHeight() / 2f, this.getWidth()/ 2f);
		Log.d(tag, "this.getWidth ; " + this.getWidth() + ",this.getHeight: " + this.getHeight());//1196,670hw
//		canvas.rotate(90,200,200);
		
		for (String rect : boxMap.keySet()) {
			Box box = boxMap.get(rect);
			if (box == null) {
				return;
			}
//			Log.d(tag, "==onDraw前=====box.left" + box.left + ",box.top : " + box.top + ",box.right : " + box.right + ",box.bottom : " + box.bottom);

			//TODO ----在这里发送box的相关信息，接收的方法在DrawRectClass中
//			EventBus.getDefault().post(box);
			
			RectF rectF = new RectF(box.left, box.top, box.right,box.bottom);
//			RectF rectF = new RectF(20, 0, 200,300);
			canvas.drawRect(rectF,  p);
			
			Log.d(tag, "---传递进来的JNI开始检测的时间点是: " + startJNI);
			Log.d(tag, "---从JNI开始检测到画框的速度是 : " + (SystemClock.uptimeMillis() - startJNI));
			
			box = null;
			rectF = null;
		}
		
		//为了当人脸离开屏幕的时候将识别框进行消失处理
		boxMap.clear();
		invalidate();
	}
	/**
	 * 存储 框的坐标的类
	 */
	public class Box {

		int left; // 左侧
		int top;
		int right;
		int bottom;
		
		public int getLeft() {
			return left;
		}

		public int getTop() {
			return top;
		}

		public int getRight() {
			return right;
		}

		public int getBottom() {
			return bottom;
		}
		
		public Box(int left, int top, int right, int bottom) {
			this.left = left;
			this.top = top;
			this.right = right;
			this.bottom = bottom;
		}
	}
	
	
	
	public interface ICoordinationInterface{
		public void coordinationCallback(Box box,float rw,float rh);
	}


	private int degree;

	public void setDegrees(int degree) {
		this.degree = degree;
	}
}
