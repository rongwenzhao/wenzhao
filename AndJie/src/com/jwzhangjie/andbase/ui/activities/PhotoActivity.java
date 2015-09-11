package com.jwzhangjie.andbase.ui.activities;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.jwzhangjie.andbase.R;
import com.jwzhangjie.andbase.ui.base.BaseActivity;
import com.jwzhangjie.andbase.util.Bimp;

/**
  * 图片预览
 */
public class PhotoActivity extends BaseActivity {

	private ViewPager viewPager;
	public List<Bitmap> bmp = new ArrayList<Bitmap>();
	public ArrayList<String> imageUrl;
	private ArrayList<View> listViews = null;
	private MyPageAdapter adapter;
	private ImageView iv_image;
	private int position;
	private Intent data;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loadXml();
		init();
	}
	
	public void loadXml() {

		setContentView(R.layout.activity_photo);
	}

	public void getIntentData(Bundle savedInstanceState) {

	}

	public void init() {
		data = new Intent();
		imageUrl = getIntent().getStringArrayListExtra("path");
		
		iv_image = (ImageView) findViewById(R.id.iv_imageView);
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		TextView tv_del = (TextView) findViewById(R.id.photo_del);
        
		//静态类中的图片
		for (int i = 0; i < Bimp.bmp.size(); i++) {
			initListViews(Bimp.bmp.get(i));
		}

		adapter = new MyPageAdapter(listViews);// 构造adapter
		viewPager.setAdapter(adapter);// 设置适配器

		viewPager.setOnPageChangeListener(new onPageChangeListener());
		iv_image.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				data.putStringArrayListExtra("imageList", imageUrl);
				setResult(10, data);
				finish();
			}
		});

		tv_del.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (listViews.size() == 1) {
					Bimp.bmp.clear();
					Bimp.drr.clear();
					imageUrl.clear();
					data.putStringArrayListExtra("imageList", imageUrl);
					setResult(10, data);
					finish();
				} else {
					Bimp.bmp.remove(position);
					Bimp.drr.remove(position);
					imageUrl.remove(position);
					viewPager.removeAllViews();
					listViews.remove(position);
					adapter.setListViews(listViews);
					adapter.notifyDataSetChanged();
				}
			}
		});
	}

	@SuppressWarnings("deprecation")
	private void initListViews(Bitmap bm) {
		if (listViews == null) {
			listViews = new ArrayList<View>();
		}
		ImageView img = new ImageView(this);// 构造textView对象
		img.setBackgroundColor(0xff000000);
		img.setImageBitmap(bm);
		img.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
		listViews.add(img);// 添加view
	}


	class MyPageAdapter extends PagerAdapter {

		private ArrayList<View> listViews;// content

		private int size;// 页数

		public MyPageAdapter(ArrayList<View> listViews) {// 构造函数
															// 初始化viewpager的时候给的一个页面
			this.listViews = listViews;
			size = listViews == null ? 0 : listViews.size();
		}

		public void setListViews(ArrayList<View> listViews) {// 自己写的一个方法用来添加数据
			this.listViews = listViews;
			size = listViews == null ? 0 : listViews.size();
		}

		public int getCount() {// 返回数量
			return size;
		}

		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		public void destroyItem(View arg0, int arg1, Object arg2) {// 销毁view对象

			((ViewPager) arg0).removeView(listViews.get(arg1 % size));
		}

		public void finishUpdate(View arg0) {
		}

		public Object instantiateItem(View arg0, int arg1) {// 返回view对象
			try {
				((ViewPager) arg0).addView(listViews.get(arg1 % size), 0);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return listViews.get(arg1 % size);
		}

		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	}

	class onPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int arg0) {
			position = arg0;
		}

	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			data.putStringArrayListExtra("imageList", imageUrl);
			setResult(10, data);
			finish();
			return true;
		}else{
			  return super.onKeyDown(keyCode, event); 
		}
	}

}
