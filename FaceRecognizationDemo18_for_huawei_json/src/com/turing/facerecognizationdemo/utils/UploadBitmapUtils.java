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
	 * �ϴ�ͼƬ��������
	 * @param resultBitmap
	 * 
	 * 
	 * �񾴵��߼���:
	 * (1)ͨ��[file.getAbsolutePath()]---�õ�FileInputSTream�Ķ���fis
	 * (2)����byte����rs(fis.available())
	 * (3)��fis�����ݶ���rs��
	 * (4)��byte����ת��ΪBitmap
	 * (5)ѹ��Bitmap�õ��µ�bitmap
	 * (6)���µ�bitmap��ת��Ϊbyte����----newbytes---------�ҵ��߼������￪ʼ
	 * (7)���µ�byte���� newbytes����Base64����
	 *          ======�õ�String���͵��ַ���imageInfo
	 * 
	 * 
	 * (8)����JSONOBject���� imageInfoд��JSONObject,
	 *    ͬʱ�ٴ���һ������    ["type", "train"]
	 * (9)��JSONOBject����toString()��String���͵�result
	 * (10)��result����encode()������ȡString���͵�url,
	 * (11)ͨ��Http�����ȡ����
	 */
	public static void uploadBitmap2Server(Bitmap resultBitmap,Box box) { //���߳�
		mBox = box;
		long bitmap2BytesStart = System.currentTimeMillis();
		
		//1������Bitmap���ֽڵõ�String���͵�����
		//��ͼƬ������Ϊԭ����30%
		byte [] newbytes = BitmapUtils.bitmap2Bytes(resultBitmap);
		Log.d(TAG, "----��bitmapת��Ϊbyte[]�����ʱ���� : " + (System.currentTimeMillis() - bitmap2BytesStart));
		
		//TODO
		Bitmap bitmap = BitmapFactory.decodeByteArray(newbytes, 0, newbytes.length);
		SaveBitmapUtils.saveCroppedImage2(bitmap);//Ϊ�˼���ϴ���������֮ǰ��ͼƬ��ʲô����
		
		long encodeStart = System.currentTimeMillis();
		String imageInfo = ImgHelper.encode(newbytes);
		Log.d(TAG, "----��byte[]�������Base64ת���ʱ���� : " + (System.currentTimeMillis() - encodeStart));
		
		long objectStart = System.currentTimeMillis(); 
		//2������JSON����
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("type", "add_person");
			jsonObject.put("image", imageInfo);
			String result = jsonObject.toString();
			String url = URLEncoder.encode(result, "utf-8");
			Log.d(TAG, "----��JSONObjectת��ΪString���ͺ��ٽ���encode()�����ĺ�ʱ�� : " + (System.currentTimeMillis() - objectStart));
//			Log.d(TAG, "===�õ���url : " + url);
			HttpConnectCore.doGet("http://" + HOST + ":" + PORT + "/addperson/" + url,mICallback,System.currentTimeMillis());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ����������ص��ӿ�
	 */
	private static IDataCallback mICallback = new IDataCallback() {

		@Override
		public void onSuccess(String result,long startTime) {
//			Log.d(TAG, "----onSuccess----��[���������]��[��ȡ���������˴��ص�����(hello)]�ĺ�ʱ�� : " + (System.currentTimeMillis() - startTime));
			Log.d(TAG, "-------onSuccess :"+result);
			
			if(result!=null){
//				Log.d(TAG, "����ǰ----------");
//				ServerData serverData = ParseJsonUtils.json2JavaBean(result);//---TODO
//				Log.d(TAG, "������---------serverData.eventList : " + serverData.data); //��ǰ��data����һ����JavaBean�е�һ��
//				Log.d(TAG, "===============: " + serverData.data.get(0).gender); //��ǰ��data����һ����JavaBean�е�һ��
//				EventBus.getDefault().post(serverData);
//				serverData = null;
//				System.gc();
				Log.d(TAG, "����ǰ----------");
				EventBusObject eventBusObject = ParseJsonUtils.json2Bean(result);
				Log.d(TAG, "������----------eventBusObject : " + eventBusObject);
				EventBus.getDefault().post(eventBusObject);
				eventBusObject = null;
				System.gc();
			}
		}

		@Override
		public void onFailed(String errorInfo) {
			Log.d(TAG,errorInfo);
			if(errorInfo!=null){
//				errorInfo = "name=Jack/\tsex=male/\tage=[30,40]/\temotion=angery"; //����,
//				MarkingBean markingBean = new MarkingBean(errorInfo,true);
//				EventBus.getDefault().post(markingBean);
//				markingBean = null;
//				errorInfo = null;
//				System.gc();
				
				Log.d(TAG,errorInfo);
			}
		}

		@Override
		public void onException(Exception e) {//û�����ӷ������ߵĲ�������ķ���,����onFailed();
			if(e!=null){
				Log.d(TAG, "----��������Ĺ�����[�����쳣],�쳣��Ϣ��" + e.getMessage());
			}
		}
	};
}
