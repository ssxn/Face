package com.tzutalin.dlib;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

public class PeopleDet {
	private static final String TAG = "PeopleDet";
	protected static boolean sInitialized = false;
	static {
		try {
			System.loadLibrary("people_det");
			jniNativeClassInit();
			sInitialized = true;
			android.util.Log.d("PeopleDet", "jniNativeClassInit success");
		} catch (UnsatisfiedLinkError e) {
			android.util.Log.d("PeopleDet", "library not found!");
		}
	}

	protected Context mContext;

	public List<VisionDetRet> detPerson(final String path) {
		List<VisionDetRet> ret = new ArrayList<VisionDetRet>();
		int size = jniOpencvHOGDetect(path);
		Log.d(TAG, "detPerson size " + size);
		for (int i = 0; i != size; i++) {
			Log.d(TAG, "enter vision loop");
			VisionDetRet det = new VisionDetRet();
			int success = jniGetOpecvHOGRet(det, i);
			Log.d(TAG, "detPerson success " + success);
			if (success >= 0) {
				Log.d(TAG, "detPerson rect " + det.toString());
				ret.add(det);
			}
		}
		return ret;
	}

	public List<VisionDetRet> detFace(final String path) {
		long sss = System.currentTimeMillis();
		List<VisionDetRet> ret = new ArrayList<VisionDetRet>();
		Log.d(TAG, "every time: " + (System.currentTimeMillis() - sss));
		int size = jniDLibHOGDetect(path);
		Log.d(TAG, "-----size--- : " + size);
		for (int i = 0; i != size; i++) {
			long start = System.currentTimeMillis();
			VisionDetRet det = new VisionDetRet();
			int success = jniGetDLibRet(det, i);
			if (success >= 0) {
				Log.d(TAG, "detFace rect " + det.toString());
				ret.add(det);
			}
			Log.d(TAG, "every time: " + (System.currentTimeMillis() - start));
		}
		
		Log.d(TAG, "------JNIÊ¶±ðºÄÊ± : " + (System.currentTimeMillis() - sss));
		return ret;
	}

	public void init() {
		jniInit();
	}

	public void deInit() {
		jniDeInit();
	}

	private native static void jniNativeClassInit();

	private native int jniInit();

	private native int jniDeInit();

	private native int jniOpencvHOGDetect(String path);

	private native int jniGetOpecvHOGRet(VisionDetRet det, int index);

	private native int jniDLibHOGDetect(String path);

	private native int jniGetDLibRet(VisionDetRet det, int index);

}
