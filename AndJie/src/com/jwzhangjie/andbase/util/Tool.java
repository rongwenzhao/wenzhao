package com.jwzhangjie.andbase.util;

import android.content.Context;
import android.os.Environment;

public class Tool {

	public static String getPath(Context context) {

		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			return android.os.Environment.getExternalStorageDirectory()
					.getAbsolutePath()
					+ "/rongwenzhaoApp/";
		}else{
			return android.os.Environment.getDataDirectory()
					.getAbsolutePath()
					+ "/rongwenzhaoApp/";
		}
	}
}
