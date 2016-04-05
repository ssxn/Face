package com.turing.facerecognizationdemo.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.IntentService;
import android.util.Log;

public class HttpConnectCore {
	
	private static final String TAG = "HttpConnectCore";
	
	/**
	 * 初始化线程池
	 */
	private static ExecutorService mThreadPool = Executors.newFixedThreadPool(10);
	
	/**
	 * GET方法
	 * @param param
	 * @param url
	 * @return
	 */
	public static String SendPost(String param, String url) {
		OutputStreamWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) realUrl
					.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setConnectTimeout(10000);
			conn.setReadTimeout(15000);
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Authorization", "token");
			conn.setRequestProperty("tag", "htc_new");

			conn.connect();

			out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
			out.write(param);

			out.flush();
			out.close();
			//
			in = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "UTF-8"));
			String line = "";
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * GET请求
	 * @param url 服务器端地址
	 * @param iCallback 结果回调接口
	 * @param uploadStart 开始上传的时间戳
	 */
	public static void doGet(String url, IDataCallback iCallback,long uploadStart) {
		Log.d(TAG, "----doGet-----");
//		url = "http://192.168.42.4:8080/qbc/server.json";
		url = "http://192.168.17.210:8080/qbc/server2.json";
		MyRunnable myRunnable = new MyRunnable(url, iCallback, uploadStart);
		mThreadPool.execute(myRunnable);
	}
	
	private static class MyRunnable implements Runnable{
		
		public String url;
		private IDataCallback iCallback;
		private long uploadStart;
		public MyRunnable(String url, IDataCallback iCallback,long uploadStart) {
			this.url = url;
			this.iCallback = iCallback;
			this.uploadStart = uploadStart;
		}
		
		@Override
		public void run() {
			Log.d(TAG, "----thread.currentThread() : " + Thread.currentThread());
			OutputStreamWriter out = null;
			BufferedReader in = null;
			String result = "";
			try {
				URL realUrl = new URL(url);
				HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
				conn.setDoOutput(true);
				conn.setDoInput(true);
				conn.setUseCaches(false);
				conn.setRequestMethod("GET");
				conn.setConnectTimeout(50000);
				conn.setReadTimeout(10000);
				conn.setRequestProperty("Content-Type", "application/json");
				conn.setRequestProperty("Accept", "application/json");
				conn.setRequestProperty("Authorization", "token");
				conn.setRequestProperty("tag", "htc_new");
				Log.d(TAG, "-----setRequestProperty----");
				conn.connect();
				Log.d(TAG, "-----connect----"); //---这一步如果服务器不开启的话执行不到,会走异常回调

				int statusCode=conn.getResponseCode();
				Log.d(TAG, "--------statusCode : " + statusCode);
				if(statusCode==200){
					in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
					String line = "";
					while ((line = in.readLine()) != null) {
						result += line;
					}
					Log.d(TAG, "****请求网络成功,结果是 result : " + result);
					if(iCallback!=null){
						iCallback.onSuccess(result,uploadStart);
					}
				}else{
					if(this.iCallback!=null){
						this.iCallback.onFailed("请求网络失败，状态码statusCode : "+statusCode);//失败回调
					}
				}
	
			} catch (Exception e) {
				if(this.iCallback!=null){
					this.iCallback.onException(e);;//失败回调:异常
				}
				e.printStackTrace();
			} finally {
				try {
					if (out != null) {
						out.close();
					}
					if (in != null) {
						in.close();
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 请求服务器端数据的线程 
	 * @author zxk
	 *
	 */
	private static class NetThread extends Thread {

		public String url;
		private IDataCallback iCallback;
		private long uploadStart;
		public NetThread(String url, IDataCallback iCallback,long uploadStart) {
			this.url = url;
			this.iCallback = iCallback;
			this.uploadStart = uploadStart;
		}

		@Override
		public void run() {
			OutputStreamWriter out = null;
			BufferedReader in = null;
			String result = "";
			try {
				URL realUrl = new URL(url);
				HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
				conn.setDoOutput(true);
				conn.setDoInput(true);
				conn.setUseCaches(false);
				conn.setRequestMethod("GET");
				conn.setConnectTimeout(50000);
				conn.setReadTimeout(50000);
				conn.setRequestProperty("Content-Type", "application/json");
				conn.setRequestProperty("Accept", "application/json");
				conn.setRequestProperty("Authorization", "token");
				conn.setRequestProperty("tag", "htc_new");
				Log.d(TAG, "-----setRequestProperty----");
				conn.connect();
				Log.d(TAG, "-----connect----"); //---这一步如果服务器不开启的话执行不到,会走异常回调

				int statusCode=conn.getResponseCode();
				Log.d(TAG, "--------statusCode : " + statusCode);
				if(statusCode==200){
					in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
					String line = "";
					while ((line = in.readLine()) != null) {
						result += line;
					}
					Log.d(TAG, "****请求网络成功,结果是 result : " + result);
					if(iCallback!=null){
						iCallback.onSuccess(result,uploadStart);
					}
				}else{
					if(this.iCallback!=null){
						this.iCallback.onFailed("请求网络失败，状态码statusCode : "+statusCode);//失败回调
					}
				}
	
			} catch (Exception e) {
				if(this.iCallback!=null){
					this.iCallback.onFailed("请求网络中出现异常,异常信息是 : " + e.getMessage());//失败回调:异常
				}
				e.printStackTrace();
			} finally {
				try {
					if (out != null) {
						out.close();
					}
					if (in != null) {
						in.close();
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 请求网络回调接口
	 * @author zxk
	 *
	 */
	public interface IDataCallback {
		
		/**
		 * 请求成功的回调,状态码为200
		 * 
		 * @param result 请求结果
		 * @param startTime 发起请求的开始时间
		 */
		public void onSuccess(String result,long startTime);
		
		/**
		 * 请求失败的回调,状态码不为200
		 * 
		 * @param errorString
		 */
		public void onFailed(String errorString);
		
		/**
		 * 请求网络的过程中出现异常
		 * @param e
		 */
		public void onException(Exception e);
	}
	
//=========================================================
	
	/**
	 * 振敬的逻辑,不走子线程
	 * @param url
	 * @return
	 */
	public static String SendGet(String url) {
		OutputStreamWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) realUrl
					.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(50000);
			conn.setReadTimeout(50000);
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Authorization", "token");
			conn.setRequestProperty("tag", "htc_new");

			conn.connect();

			in = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "UTF-8"));
			String line = "";
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}
}
