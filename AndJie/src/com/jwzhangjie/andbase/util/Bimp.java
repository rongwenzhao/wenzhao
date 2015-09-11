package com.jwzhangjie.andbase.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Bimp {
	public static int max = 0;
	public static boolean act_bool = true;
	public static List<Bitmap> bmp = new ArrayList<Bitmap>();

	// 图片sd地址 上传服务器时把图片调用下面方法压缩后 保存到临时文件夹 图片压缩后小于100KB，失真度不明显
	public static List<String> drr = new ArrayList<String>();

	/**
	 * 将图片地址通过bitmpFactory将图片进行压缩
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static Bitmap revitionImageSize(String path) throws IOException {
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(
				new File(path)));
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(in, null, options);
		in.close();
		int i = 0;
		Bitmap bitmap = null;
		while (true) {
			if ((options.outWidth >> i <= 1000)
					&& (options.outHeight >> i <= 1000)) {
				in = new BufferedInputStream(
						new FileInputStream(new File(path)));
				options.inSampleSize = (int) Math.pow(2.0D, i);
				options.inJustDecodeBounds = false;
				bitmap = BitmapFactory.decodeStream(in, null, options);
				break;
			}
			i += 1;
		}
		return bitmap;
	}

	/**
	 * 保存图片只本地文件夹
	 * 
	 * @param mBitmap
	 * @param bitName
	 * @return
	 */
	public static String saveMyBitmap(Bitmap mBitmap, String bitName,
			Context context) {
		File f = new File(Tool.getPath(context) + bitName + ".jpg");
		try {
			if (f.exists()) {
				f.delete();
				f.createNewFile();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
		try {
			if(fOut != null){
				fOut.flush();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			if(fOut!= null){
				fOut.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return f.getPath();
	}
}
