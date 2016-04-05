package com.turing.facerecognizationdemo.utils;

import java.net.URLEncoder;

import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.turing.facerecognizationdemo.bean.ServerData.EventBusObject;
import com.turing.facerecognizationdemo.utils.HttpConnectCore.IDataCallback;
import com.turing.facerecognizationdemo.view.MyOverlayView.Box;

import de.greenrobot.event.EventBus;

public class UploadBitmapUtils {
	
	private static final String TAG = "UploadBitmapUtils";
	private static final String HOST = "123.57.143.174";
	private static final String PORT = "10000";
	private static Box mBox;
	
	/**
	 * 上传图片到服务器
	 * @param resultBitmap
	 * 
	 * 
	 * 振敬的逻辑是:
	 * (1)通过[file.getAbsolutePath()]---得到FileInputSTream的对象fis
	 * (2)创建byte数组rs(fis.available())
	 * (3)将fis的数据读到rs中
	 * (4)将byte数组转化为Bitmap
	 * (5)压缩Bitmap得到新的bitmap
	 * (6)将新的bitmap再转化为byte数组----newbytes---------我的逻辑从这里开始
	 * (7)将新的byte数组 newbytes进行Base64编码
	 *          ======得到String类型的字符串imageInfo
	 * 
	 * 
	 * (8)创建JSONOBject对象将 imageInfo写进JSONObject,
	 *    同时再传递一个参数    ["type", "train"]
	 * (9)将JSONOBject对象toString()得String类型的result
	 * (10)将result进行encode()操作获取String类型的url,
	 * (11)通过Http请求获取数据
	 */
	public static void uploadBitmap2Server(Bitmap resultBitmap,Box box) { //主线程
		mBox = box;
		long bitmap2BytesStart = System.currentTimeMillis();
		
		//1、根据Bitmap的字节得到String类型的数据
		//将图片质量变为原来的30%
		byte [] newbytes = BitmapUtils.bitmap2Bytes(resultBitmap);
		Log.d(TAG, "----将bitmap转化为byte[]数组的时间是 : " + (System.currentTimeMillis() - bitmap2BytesStart));
		
		//TODO
		Bitmap bitmap = BitmapFactory.decodeByteArray(newbytes, 0, newbytes.length);
		SaveBitmapUtils.saveCroppedImage2(bitmap);//为了检测上传至服务器之前的图片是什么样子
		
		long encodeStart = System.currentTimeMillis();
		String imageInfo = ImgHelper.encode(newbytes);
		Log.d(TAG, "----将byte[]数组进行Base64转码的时间是 : " + (System.currentTimeMillis() - encodeStart));
		
		long objectStart = System.currentTimeMillis(); 
		//2、创建JSON对象
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("type", "add_person");
			jsonObject.put("image", imageInfo);
			String result = jsonObject.toString();
			String url = URLEncoder.encode(result, "utf-8");
			Log.d(TAG, "----将JSONObject转化为String类型后再进行encode()操作的耗时是 : " + (System.currentTimeMillis() - objectStart));
//			Log.d(TAG, "===得到的url : " + url);
			HttpConnectCore.doGet("http://" + HOST + ":" + PORT + "/addperson/" + url,mICallback,System.currentTimeMillis());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 请求服务器回调接口
	 */
	private static IDataCallback mICallback = new IDataCallback() {

		@Override
		public void onSuccess(String result,long startTime) {
//			Log.d(TAG, "----onSuccess----从[请求服务器]到[获取到服务器端传回的数据(hello)]的耗时是 : " + (System.currentTimeMillis() - startTime));
			Log.d(TAG, "-------onSuccess :"+result);
			
			if(result!=null){
//				Log.d(TAG, "解析前----------");
//				ServerData serverData = ParseJsonUtils.json2JavaBean(result);//---TODO
//				Log.d(TAG, "解析后---------serverData.eventList : " + serverData.data); //当前的data名字一定与JavaBean中的一致
//				Log.d(TAG, "===============: " + serverData.data.get(0).gender); //当前的data名字一定与JavaBean中的一致
//				EventBus.getDefault().post(serverData);
//				serverData = null;
//				System.gc();
				Log.d(TAG, "解析前----------");
				EventBusObject eventBusObject = ParseJsonUtils.json2Bean(result);
				Log.d(TAG, "解析后----------eventBusObject : " + eventBusObject);
				EventBus.getDefault().post(eventBusObject);
				eventBusObject = null;
				System.gc();
			}
		}

		@Override
		public void onFailed(String errorInfo) {
			Log.d(TAG,errorInfo);
			if(errorInfo!=null){
//				errorInfo = "name=Jack/\tsex=male/\tage=[30,40]/\temotion=angery"; //年龄,
//				MarkingBean markingBean = new MarkingBean(errorInfo,true);
//				EventBus.getDefault().post(markingBean);
//				markingBean = null;
//				errorInfo = null;
//				System.gc();
				
				Log.d(TAG,errorInfo);
			}
		}

		@Override
		public void onException(Exception e) {//没有连接服务器走的不是这里的方法,而是onFailed();
			if(e!=null){
				Log.d(TAG, "----请求网络的过程中[出现异常],异常信息是" + e.getMessage());
			}
		}
	};
}
