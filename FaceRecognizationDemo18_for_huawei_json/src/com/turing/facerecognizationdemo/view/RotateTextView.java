package com.turing.facerecognizationdemo.view;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.turing.facerecognizationdemo.R;
import com.turing.facerecognizationdemo.bean.ServerData;
import com.turing.facerecognizationdemo.bean.ServerData.EventBusObject;

import de.greenrobot.event.EventBus;

/**
 * Created by zhenxixianzai on 2015/5/5.
 */
public class RotateTextView extends TextView {
	private static final int DEFAULT_DEGREES = 0;
	private static final String TAG = RotateTextView.class.getSimpleName();
	private int mDegrees;
	private static final int RECEIVE_SUCCESS = 0;
	private static final int RECEIVE_FALIED = 1;
	private List<EventBusObject> dataList = new ArrayList<EventBusObject>();
	
	private EventBusObject mEventBusObject;
	/**
	 * EventBus接收数据的方法名称
	 */
	private static final String drawText= "drawText";
	private static final String cancelText="cancelText";
	private static final String drawSingleFace="drawSingleFace";
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case RECEIVE_SUCCESS:
				invalidate();
			case RECEIVE_FALIED:
				invalidate();
				break;
			default:
				break;
			}
		};
	};
	
	/**
	 * 构造方法
	 * @param context
	 * @param attrs
	 */
	public RotateTextView(Context context, AttributeSet attrs) {
		super(context, attrs, android.R.attr.textViewStyle);
		initEventBus();
		TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.RotateTextView);
		mDegrees = a.getDimensionPixelSize(R.styleable.RotateTextView_degree,DEFAULT_DEGREES);
		a.recycle();
	}
	
	/**
	 * 注册EventBus
	 */
	private void initEventBus() {
		EventBus.getDefault().register(this,drawText);
		EventBus.getDefault().register(this,cancelText);
		EventBus.getDefault().register(this,drawSingleFace);
	}
	
	/**
	 * 单张人脸
	 * @param eventBusObject
	 */
	private void drawSingleFace(EventBusObject eventBusObject){
		Log.d(TAG, "--------eventBusObject-- : " +eventBusObject);
		if(eventBusObject!=null){
			dataList.clear();
			dataList.add(eventBusObject);
			Log.d(TAG, "----------drawSingleFace : " + dataList);
			Message msg = Message.obtain();
			msg.obj = eventBusObject;
			msg.what = RECEIVE_SUCCESS;
			mHandler.sendMessage(msg);
		}
	}
	
	
	/**
	 * 接收来自
	 * 1,UploadBitmapUtils类中onSuccess()方法发送的数据：请求服务器成功
	 * 2,DrawRectClass类中handleBitmap()方法中else逻辑发送的数据 : 没有识别出人脸
	 * @param mMarkingBean 
	 */
	public void drawText(ServerData serverData){
		Log.d(TAG, "--自定义TextView已经接收到了数据---serverData : " + serverData);
		if (serverData != null) {
			List<EventBusObject> eventList = serverData.data;
			if(eventList!=null){
				dataList = eventList;
				Message msg = Message.obtain();
				msg.obj = dataList;
				msg.what = RECEIVE_SUCCESS;
				mHandler.sendMessage(msg);
			}
		}
	}
	
	
	public void cancelText(String result){
		if(result!=null){
			Message msg = Message.obtain();
			//******
			if(dataList!=null){
				dataList.clear();
			}
			//******
			msg.obj = dataList;
			msg.what = RECEIVE_FALIED;
			mHandler.sendMessage(msg);
		}
	}
	
	
	/**
	 * 测量当前控件
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
	}
	
	/**
	 * 绘制
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		Log.d(TAG, "dataList : " + dataList);
		if(dataList == null || dataList.size()<1){//没有识别出人脸,将屏幕上的文字描述清空
			Log.d(TAG, "-------当前控件不显示任何内容--------");
			this.setText("");
			return ;
		}
		canvas.save();
		canvas.translate(getCompoundPaddingLeft(), getExtendedPaddingTop());
//		Log.d(TAG, "getCompoundPaddingLeft() : " + getCompoundPaddingLeft() + ",getExtendedPaddingTop() : " + getExtendedPaddingTop());
		Log.d(TAG, "this.getWidth() ; " + this.getWidth() + ",this.getHeight() ; " + this.getHeight());
		canvas.rotate(mDegrees, this.getWidth() / 2f, this.getHeight() / 2f);
		super.onDraw(canvas);
		canvas.restore();
		Log.d(TAG, "-----onDraw()-----dataList : " + dataList);
		
//		for(int i = 0 ; i < dataList.size();i++){
//			this.setText(dataList.get(i).iden+"\t" + dataList.get(i).age +"\t"+
//		   dataList.get(i).gender+"\t"+dataList.get(i).emotion+"\r\n");
//		}
		
		if(dataList.size()==1){
			Log.d(TAG, "-----dataList.size() ==1");
			String result = dataList.get(0).iden+"\t"+dataList.get(0).age+"\t"+dataList.get(0).gender+"\t"+dataList.get(0).emotion;
			this.setText(result);
		}else if(dataList.size()==2){
			Log.d(TAG, "-----dataList.size() ==2");
			String result0 = dataList.get(0).iden+"\t"+dataList.get(0).age+"\t"+dataList.get(0).gender+"\t"+dataList.get(0).emotion;
			String result1 = dataList.get(1).iden+"\t"+dataList.get(1).age+"\t"+dataList.get(1).gender+"\t"+dataList.get(1).emotion;
			this.setText(result0+"\r\n"+result1);
		}else if(dataList.size()==3){
			Log.d(TAG, "-----dataList.size() ==3");
			String result0 = dataList.get(0).iden+"\t"+dataList.get(0).age+"\t"+dataList.get(0).gender+"\t"+dataList.get(0).emotion;
			String result1 = dataList.get(1).iden+"\t"+dataList.get(1).age+"\t"+dataList.get(1).gender+"\t"+dataList.get(1).emotion;
			String result2 = dataList.get(2).iden+"\t"+dataList.get(2).age+"\t"+dataList.get(2).gender+"\t"+dataList.get(2).emotion;
			this.setText(result0+"\r\n"+result1+"\r\n"+result2);
		}else if(dataList.size()==4){
			Log.d(TAG, "-----dataList.size() ==4");
			String result0 = dataList.get(0).iden+"\t"+dataList.get(0).age+"\t"+dataList.get(0).gender+"\t"+dataList.get(0).emotion;
			String result1 = dataList.get(1).iden+"\t"+dataList.get(1).age+"\t"+dataList.get(1).gender+"\t"+dataList.get(1).emotion;
			String result2 = dataList.get(2).iden+"\t"+dataList.get(2).age+"\t"+dataList.get(2).gender+"\t"+dataList.get(2).emotion;
			String result3 = dataList.get(3).iden+"\t"+dataList.get(3).age+"\t"+dataList.get(3).gender+"\t"+dataList.get(3).emotion;
			this.setText(result0+"\r\n"+result1+"\r\n"+result2+"\r\n"+result3);
		}else if(dataList.size()==5){
			Log.d(TAG, "-----dataList.size() ==5");
			String result0 = dataList.get(0).iden+"\t"+dataList.get(0).age+"\t"+dataList.get(0).gender+"\t"+dataList.get(0).emotion;
			String result1 = dataList.get(1).iden+"\t"+dataList.get(1).age+"\t"+dataList.get(1).gender+"\t"+dataList.get(1).emotion;
			String result2 = dataList.get(2).iden+"\t"+dataList.get(2).age+"\t"+dataList.get(2).gender+"\t"+dataList.get(2).emotion;
			String result3 = dataList.get(3).iden+"\t"+dataList.get(3).age+"\t"+dataList.get(3).gender+"\t"+dataList.get(3).emotion;
			String result4 = dataList.get(4).iden+"\t"+dataList.get(4).age+"\t"+dataList.get(4).gender+"\t"+dataList.get(4).emotion;
			this.setText(result0+"\r\n"+result1+"\r\n"+result2+"\r\n"+result3+"\r\n"+result4);
		}
		
		
	}
	
	/**
	 * 设置旋转的角度
	 * @param degrees
	 */
	public void setDegrees(int degrees) {
		mDegrees = degrees;
	}
}