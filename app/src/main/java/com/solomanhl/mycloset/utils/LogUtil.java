package com.solomanhl.mycloset.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * This is Log print class, set isPrint =false ,when release,no log will be printed
 *
 * @author zhuchen
 *
 */
public class LogUtil {
	// TODO ***********************Set isPrint private when SDK release********************************
	private final static boolean isPrint = true;
	// add test to avoid testcode not clolsed
	public final static boolean test = isPrint;
	// TODO ***********************Set test private when SDK release**********************************
	public static void i(String tag, String message) {
		if (isPrint) {
			if (tag != null && message != null && !"".equals(tag.trim())
					&& !"".equals(message.trim())) {
				android.util.Log.i(tag, message);
			}
		}
	}

	public static void d(String tag, String message) {
		if (isPrint) {
			if (tag != null && message != null && !"".equals(tag.trim())
					&& !"".equals(message.trim())) {
				android.util.Log.d(tag, message);
			}
		}
	}

	public static void e(String tag, String message) {
		if (isPrint) {
			if (tag != null && message != null && !"".equals(tag.trim())
					&& !"".equals(message.trim())) {
				android.util.Log.e(tag, message);
			}
		}
	}

	public static void w(String tag, String message) {
		if (isPrint) {
			if (tag != null && message != null && !"".equals(tag.trim())
					&& !"".equals(message.trim())) {
				android.util.Log.w(tag, message);
			}
		}
	}

	public static void e(Exception e) {
		if (isPrint) {
			if (e != null) {
				e.printStackTrace();
			}
		}
	}

	public static void showToast(Context context, String content) {
		if (isPrint) {
			if (context != null && content != null)
				Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
		}
	}
}
