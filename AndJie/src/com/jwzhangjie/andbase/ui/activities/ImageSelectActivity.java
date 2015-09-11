package com.jwzhangjie.andbase.ui.activities;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jwzhangjie.andbase.R;
import com.jwzhangjie.andbase.util.Bimp;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 
 *加载本地所有图片activity
 */
public class ImageSelectActivity extends Activity implements
		OnClickListener {

	private final static int SCAN_OK = 1;
	// private final static int SCAN_FOLDER_OK = 2;
	private GridView gridview;
	private TextView group_text;
	private TextView total_text;
	private DisplayImageOptions options;
	private ImageLoader mImageLoader;
	private ArrayList<String> mAllImgs;
	private ArrayList<String> addedPath;
	private ArrayList<Integer> chooseItem = new ArrayList<Integer>();
	// private HashMap<String, ArrayList<String>> mGruopMap = new
	// HashMap<String, ArrayList<String>>();
	// private ArrayList<String> nowStrs = new ArrayList<String>();
	private int limit_count = 5; // 选中图片不能超过5张
	private String[] lists;
	private GridAdapter gridAdatper;
	private boolean isPreview; // 图片是否预览
	private String path;
	private TextView bt_end;
	private ImageView iv_back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_itreport);
//		limit_count = this.getIntent().getIntExtra("imgcount", 1);
		initView();
		initData();
	}

	public void initView() {
		gridview = (GridView) findViewById(R.id.gridview);
		group_text = (TextView) findViewById(R.id.group_text);
		total_text = (TextView) findViewById(R.id.total_text);
		bt_end = (TextView) findViewById(R.id.bt_end);
		iv_back = (ImageView) findViewById(R.id.iv_back);
		bt_end.setOnClickListener(this);
		group_text.setOnClickListener(this);
		iv_back.setOnClickListener(this);
	}

	private void initData() {
		// 初始化数据，所有图片应在281张以内
		chooseItem.add(0);
		// imageLoader配置

		mImageLoader = ImageLoader.getInstance();

		options = new DisplayImageOptions.Builder().cacheOnDisc()
				.showImageForEmptyUri(R.drawable.friends_sends_pictures_no)
				.showImageOnFail(R.drawable.friends_sends_pictures_no)
				.showStubImage(R.drawable.friends_sends_pictures_no).build();

		mAllImgs = new ArrayList<String>(281);
		addedPath = new ArrayList<String>();
		getImages();
	}

	private void getImages() {
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			Toast.makeText(this, "暂无外部存储", Toast.LENGTH_SHORT).show();
			return;
		}

		new Thread(new Runnable() {

			@Override
			public void run() {
				Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver mContentResolver = ImageSelectActivity.this
						.getContentResolver();

				// 只查询jpeg和png,jpg的图片
				Cursor mCursor = mContentResolver
						.query(mImageUri, null,
								MediaStore.Images.Media.MIME_TYPE + "=? or "
										+ MediaStore.Images.Media.MIME_TYPE
										+ "=? or "
										+ MediaStore.Images.Media.MIME_TYPE
										+ "=?", new String[] { "image/jpeg",
										"image/png", "image/jpg" },
								MediaStore.Images.Media.DATE_MODIFIED);
				// penghui修改 2015-06-19 实现按照拍照时间排序
				try {
					mCursor.moveToLast();
					do {

						String path = mCursor.getString(mCursor
								.getColumnIndex(MediaStore.Images.Media.DATA));
						if (mAllImgs.size() < 281) {
							mAllImgs.add(path);
						}

						// 获取该图片的父路径名
						// File pa_file = new File(path).getParentFile();
						// String parentName = pa_file.getAbsolutePath();

					} while (mCursor.moveToPrevious());

					mCursor.close();
				} catch (Exception e) {
					// TODO: handle exception
				}
				// 通知Handler扫描图片完成
				mHandler.sendEmptyMessage(SCAN_OK);

			}
		}).start();

	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SCAN_OK:

				gridAdatper = new GridAdapter();
				gridAdatper.setData(mAllImgs);
				gridview.setAdapter(gridAdatper);
				break;
			// case SCAN_FOLDER_OK:
			// // 获取到mAllImgs；并显示到数据中
			// GridAdapter gridAdatper1 = new GridAdapter();
			// gridAdatper1.setData(nowStrs);
			// gridview.setAdapter(gridAdatper1);
			// gridAdatper1 = null;
			// break;
			default:
				break;
			}
		}

	};

	protected void onDestroy() {
		Bimp.bmp.clear();
		Bimp.drr.clear();
		super.onDestroy();
	};

	Handler mYhandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				total_text.setText(addedPath.size() + "/" + limit_count + "张");
				break;
			case 1:
				Toast.makeText(ImageSelectActivity.this,
						"选择图片不能超过" + limit_count + "张!", Context.MODE_PRIVATE)
						.show();
				break;
			default:
				break;
			}
		}
	};

	// gridview的Adapter
	class GridAdapter extends BaseAdapter {
		// 根据三种不同的布局来应用
		final int VIEW_TYPE = 2;
		final int TYPE_1 = 0;
		final int TYPE_2 = 1;
		LayoutInflater inflater;
		private ArrayList<String> gridStrings;

		/**
		 * 用来存储图片的选中情况
		 */

		public GridAdapter() {
			gridStrings = new ArrayList<String>();
			inflater = LayoutInflater.from(ImageSelectActivity.this);
		}

		public void setData(ArrayList<String> strs) {
			if (null != strs) {
				gridStrings.clear();
				gridStrings.addAll(strs);
				notifyDataSetChanged();
			}
		}

		@Override
		public int getCount() {
			return gridStrings.size()==0?1:gridStrings.size();
		}

		@Override
		public String getItem(int position) {
			if (chooseItem.get(0) == 0) {
				return gridStrings.get(position - 1);
			} else {
				Log.i("cxm",
						"position====" + position + ",path="
								+ gridStrings.get(position));
				return gridStrings.get(position);
			}
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public int getItemViewType(int position) {
			if (chooseItem.get(0) == 0) {
				if (position == 0) {
					return TYPE_1;
				} else {
					return TYPE_2;
				}
			} else {
				return TYPE_2;
			}
		}

		@Override
		public int getViewTypeCount() {
			if (chooseItem.get(0) == 0) {
				return VIEW_TYPE;
			} else {
				return 1;
			}
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup arg2) {
			GridHolder gridHolder = null;
			PhotoHolder photoHodler = null;
			int type = getItemViewType(position);
			if (convertView == null) {
				switch (type) {
				case TYPE_1:
					// 显示拍照
					photoHodler = new PhotoHolder();
					convertView = inflater.inflate(R.layout.take_photo, null);
					photoHodler.grid_item_layout = (RelativeLayout) convertView
							.findViewById(R.id.grid_item_layout);
					convertView.setTag(photoHodler);
					break;
				case TYPE_2:
					convertView = inflater.inflate(R.layout.grid_item, null);
					gridHolder = new GridHolder();
					gridHolder.grid_image = (ImageView) convertView
							.findViewById(R.id.grid_image);
					gridHolder.grid_img = (ImageView) convertView
							.findViewById(R.id.grid_img);
					convertView.setTag(gridHolder);
					break;
				default:
					break;
				}
			} else {
				switch (type) {
				case TYPE_1:
					// 显示拍照
					photoHodler = (PhotoHolder) convertView.getTag();
					break;
				case TYPE_2:
					gridHolder = (GridHolder) convertView.getTag();
					break;
				default:
					break;
				}
			}

			if (type == TYPE_2) {
				// 通过第三方的imageloader来加载图片防止oom
				mImageLoader.displayImage("file://" + getItem(position),
						gridHolder.grid_image, options);

				gridHolder.grid_img
						.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View view) {
								if (addedPath.contains(getItem(position))) {
									// 已经包含这个path了，则干掉
									addedPath.remove(getItem(position));
									System.out.println("bimp:----"
											+ Bimp.drr.size());
									for (int i = 0; i < Bimp.drr.size(); i++) {
										if (Bimp.drr.get(i).contains(
												getItem(position))) {
											Bimp.bmp.remove(i);
											Bimp.drr.remove(i);
										}
									}
									lists = addedPath
											.toArray(new String[addedPath
													.size()]);
									((ImageView) view)
											.setImageResource(R.drawable.friends_sends_pictures_select_icon_unselected);
								} else {
									// 判断大小
									if (addedPath.size() < limit_count) {
										try {
											// 将图片保存到静态类中，不去重复加载图片
											Bitmap bm = Bimp
													.revitionImageSize(getItem(position));
											Bimp.bmp.add(bm);
											Bimp.drr.add(getItem(position));
											// 将选中的图片保存到list集合中
											addedPath.add(getItem(position));
											int size = addedPath.size();
											// 此处将图片转换为数组的原因是html5需要将图片路径放到数组中传给他们
											lists = addedPath
													.toArray(new String[size]);
											((ImageView) view)
													.setImageResource(R.drawable.friends_sends_pictures_select_icon_selected);
										} catch (Exception e) {
											e.printStackTrace();
										}
										// 添加图片，显示出来张数
									} else {
										mYhandler.sendEmptyMessage(1);
									}
								}
								mYhandler.sendEmptyMessage(0);
							}
						});
				// 判断图片是否进行了预览
				if (isPreview == true) {
					if (addedPath.contains(getItem(position))) {
						// 根据预览返回的结果选中图片
						lists = addedPath.toArray(new String[addedPath.size()]);
						gridHolder.grid_img
								.setImageResource(R.drawable.friends_sends_pictures_select_icon_selected);
					} else {
						gridHolder.grid_img
								.setImageResource(R.drawable.friends_sends_pictures_select_icon_unselected);
					}
				} else {
					if (addedPath.contains(getItem(position))) {
						// 已经添加过了
						gridHolder.grid_img
								.setImageResource(R.drawable.friends_sends_pictures_select_icon_selected);
					} else {
						gridHolder.grid_img
								.setImageResource(R.drawable.friends_sends_pictures_select_icon_unselected);
					}
				}
			} else {
				photoHodler.grid_item_layout
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								photo();
							}
						});
			}

			return convertView;
		}

		class PhotoHolder {
			RelativeLayout grid_item_layout;
		}

		class GridHolder {
			ImageView grid_image;
			public ImageView grid_img;
		}
	}

	/**
	 * 调用照相机拍照
	 */
	public void photo() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			File dir = new File(Environment.getExternalStorageDirectory()
					+ "/myimage/");
			if (!dir.exists())
				dir.mkdirs();

			Intent openCameraIntent = new Intent(
					MediaStore.ACTION_IMAGE_CAPTURE);
			File file = new File(dir,
					String.valueOf(System.currentTimeMillis()) + ".jpg");
			path = file.getPath();
			Uri imageUri = Uri.fromFile(file);
			openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			openCameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
			startActivityForResult(openCameraIntent, 0);
		} else {
			Toast.makeText(this, "没有储存卡", Toast.LENGTH_LONG).show();
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0) {
			if (resultCode != 0) {
				addedPath.clear();
				addedPath.add(path);
				int size = addedPath.size();
				lists = addedPath.toArray(new String[size]);
				// Intent intent = new Intent(this, ITReportActivity.class);
				// intent.putExtra("imagePath", lists);
				// intent.putExtra("path", getIntent().getStringExtra("path"));
				// startActivity(intent);
				Intent intent = new Intent();
				intent.putExtra("imagePath", lists);
				setResult(20, intent);
				finish();

			}
		} else {
			isPreview = true;
			addedPath = data.getStringArrayListExtra("imageList");
			gridAdatper.notifyDataSetChanged();
			mYhandler.sendEmptyMessage(0);
		}
		// switch (requestCode) {
		// case 0:
		// // if (Bimp.drr.size() < 9 && resultCode == -1) {
		// // Bimp.drr.add(path);
		// // }
		//
		// break;
		// }
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_end:
			if (addedPath.size() == 0) {
				return;
			}
			// Intent intent = new Intent(this, ITReportActivity.class);
			// intent.putExtra("imagePath", lists);
			// intent.putExtra("path", getIntent().getStringExtra("path"));
			// startActivity(intent);
			// finish();
			Intent intent = new Intent();
			intent.putExtra("imagePath", lists);
			setResult(20, intent);
			finish();
			break;
		case R.id.group_text:
			if (addedPath.size() == 0) {
				return;
			}
			//图片详情
			Intent intent1 = new Intent(this, PhotoActivity.class);
			intent1.putStringArrayListExtra("path", addedPath);
			startActivityForResult(intent1, 100);
			break;
		case R.id.iv_back:
			Bimp.bmp.clear();
			Bimp.drr.clear();
			addedPath.clear();
			finish();
		default:
			break;
		}
	}
}
