package com.jxy.safemanager.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.util.Log;

public class StreamUtil {

	private static final String tag = "StreamUtil";

	/**
	 * @param iStream
	 *            InputStream对象
	 * @return 流转换为String，异常则为null
	 */
	public static String stream2String(InputStream iStream) {
		// (1)
		ByteArrayOutputStream oStream = new ByteArrayOutputStream();

		// (2)(buffer)
		byte[] buffer = new byte[1024];

		// (3)
		// (4)
		int temp = -1;
		try {
			while ((temp = iStream.read(buffer)) != -1) {
				oStream.write(buffer, 0, temp);
			}
			// (5)toString()
			return oStream.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.d(tag, "iStream.read(buffer) 异常");
			e.printStackTrace();
		} finally {
			try {
				// (6)Close Stream
				iStream.close();
				oStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.d(tag, "oStream/iStream.close() 异常");
				e.printStackTrace();
			}
		}

		return null;
	}

}
