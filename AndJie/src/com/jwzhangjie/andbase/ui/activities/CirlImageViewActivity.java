package com.jwzhangjie.andbase.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.jwzhangjie.andbase.R;
import com.jwzhangjie.andbase.doc.SelectPicUsed;
import com.jwzhangjie.andbase.ui.base.BaseActivity;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.event.OnClick;

@ContentView(R.layout.circle_imageview_layout)
public class CirlImageViewActivity extends BaseActivity implements
		OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@OnClick({ R.id.profile_image, R.id.profile_image2, R.id.profile_image3 })
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.profile_image:
			showInfo("profile 01 点击,选择图片");
			Intent intent = new Intent(CirlImageViewActivity.this,
					SelectPicUsed.class);
			startActivity(intent);
			break;
		case R.id.profile_image2:
			showInfo("图片选择器");
			Intent intent2 = new Intent(CirlImageViewActivity.this,
					ImageSelectActivity.class);
			startActivity(intent2);
			break;
		case R.id.profile_image3:
			showInfo("profile 03 点击");
			break;

		default:
			break;
		}
	}

}
