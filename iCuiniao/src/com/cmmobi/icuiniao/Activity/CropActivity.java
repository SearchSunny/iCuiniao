/**
 * 
 */
package com.cmmobi.icuiniao.Activity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.ui.view.CropView;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.MulitPointTouchListener;
import com.cmmobi.icuiniao.util.OfflineLog;
import com.cmmobi.icuiniao.util.URLUtil;
import com.cmmobi.icuiniao.util.UserUtil;

/**
 * @author hw
 *
 */
public class CropActivity extends Activity{
	
	private String photoPath;
	private CropView cropView;
	private Button leftbtn;
	private Button rightbtn;
	private ImageView cropBack;
	private Bitmap image;
	private Bitmap uploadImage;
	private Button crop_rote;
	private Button crop_zoomin;
	private Button crop_zoomout;
	//add by lyb 小鱼loading
	private ProgressBar loadingBar;
	
	private final static float ScaleIn = 1.1f;
	private final static float ScaleOut = 0.9f;
	private final static float RoteAngel = 90f;

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crop);
        photoPath = getIntent().getExtras().getString("imagepath");
        cropBack = (ImageView)findViewById(R.id.cropback);
        cropView = (CropView)findViewById(R.id.cropview);
        leftbtn = (Button)findViewById(R.id.leftbtn);
        rightbtn = (Button)findViewById(R.id.rightbtn);
        crop_rote = (Button)findViewById(R.id.crop_rote);
        crop_zoomin = (Button)findViewById(R.id.crop_zoomin);
        crop_zoomout = (Button)findViewById(R.id.crop_zoomout);
        loadingBar = (ProgressBar)findViewById(R.id.loading);
        
        if(setImage(photoPath)){
        	cropBack.setImageBitmap(image);
        	cropBack.setOnTouchListener(new MulitPointTouchListener());
        	crop_rote.setOnClickListener(rote);
        	crop_zoomin.setOnClickListener(zoomin);
        	crop_zoomout.setOnClickListener(zoomout);
        }
        leftbtn.setOnClickListener(leftClickListener);
        rightbtn.setOnClickListener(rightClickListener);
        OfflineLog.writeEditIcon();
    }
	
	private OnClickListener zoomin = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Matrix matrix = new Matrix();
			matrix.set(cropBack.getImageMatrix());
			matrix.postScale(ScaleIn, ScaleIn,CommonUtil.screen_width/2,CommonUtil.screen_height/2);
			cropBack.setImageMatrix(matrix);
		}
	};
	
	private OnClickListener zoomout = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Matrix matrix = new Matrix();
			matrix.set(cropBack.getImageMatrix());
			matrix.postScale(ScaleOut, ScaleOut,CommonUtil.screen_width/2,CommonUtil.screen_height/2);
			cropBack.setImageMatrix(matrix);
		}
	};
	
	private OnClickListener rote = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Matrix matrix = new Matrix();
			matrix.set(cropBack.getImageMatrix());
			matrix.postRotate(RoteAngel, CommonUtil.screen_width/2,CommonUtil.screen_height/2);
			cropBack.setImageMatrix(matrix);
		}
	};
	
	public boolean setImage(String path){
		if(path != null){
			try {
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;//仅获取图片的基本信息
				image = BitmapFactory.decodeFile(path,options);
				//此时返回的图片为空,只有基本信息,但节约内存,防止大图内存爆掉
				
				int w = options.outWidth;
				int h = options.outHeight;
				LogPrint.Print("image = "+w+"|"+h);
				int pecent;
				pecent = (int)(w/(float)CommonUtil.screen_width);
				if(pecent <= 0){
					pecent = 1;
				}
				LogPrint.Print("pecent = "+pecent);
				//正式加载图片
				options.inJustDecodeBounds = false;
				options.inSampleSize = pecent;
				image = BitmapFactory.decodeFile(path,options);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return image == null?false:true;
	}
	
	private OnClickListener leftClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			finish();
		}
	};
	
	private OnClickListener rightClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(UserUtil.userid == -1||UserUtil.userState != 1){//未登陆
				CommonUtil.ShowToast(getApplicationContext(), "请登陆或者注册，变身为小C的主人！");
				Intent intent11 = new Intent();
				intent11.setClass(CropActivity.this, LoginAndRegeditActivity.class);
				startActivity(intent11);
			}else{
				uploadImage = null;
				cropBack.setDrawingCacheEnabled(true);
				Bitmap cache = cropBack.getDrawingCache();
				
				uploadImage = Bitmap.createBitmap(CommonUtil.screen_width, CommonUtil.screen_width, Config.ARGB_8888);
				Canvas canvas = new Canvas();
				Paint paint = new Paint();
				canvas.setBitmap(uploadImage);
				canvas.drawBitmap(cache, 0, -(cache.getHeight()-CommonUtil.screen_width)/2, paint);
				canvas.save();
				
				if(uploadImage != null){
					uploadImage();
				}
			}
		}
	};
	
	//上传图片
	private void uploadImage(){
		addProgress();
		new Thread(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				uploadFile(URLUtil.URL_IMAGE_UPLOAD+"?oid="+UserUtil.userid+"&dpi="+URLUtil.dpi());
			}
			
		}.start();
	}
	
	/* 上传文件至Server的方法 */
	private void uploadFile(String actionUrl) {
		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		LogPrint.Print("connect","uploadurl = "+actionUrl);
		try {
			URL url = new URL(actionUrl);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			/* 允许Input、Output，不使用Cache */
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			con.setConnectTimeout(10000);
			/* 设置传送的method=POST */
			con.setRequestMethod("POST");
			/* setRequestProperty */
			con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestProperty("Charset", "UTF-8");
			con.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);
			/* 设置DataOutputStream */
			DataOutputStream ds = new DataOutputStream(con.getOutputStream());
//			ds.writeBytes(twoHyphens + boundary + end);
//			ds.writeBytes("Content-Disposition: form-data; "
//					+ "name=\"image\";filename=\"usericon.jpg\"" + end);
//			ds.writeBytes(end);
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			uploadImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			LogPrint.Print("bais.len = "+bais.available());
			/* 设置每次写入1024bytes */
			int bufferSize = 1024;
			byte[] buffer = new byte[bufferSize];
			int length = -1;
			int sum = 0;
			/* 从文件读取数据至缓冲区 */
			while ((length = bais.read(buffer)) != -1) {
				/* 将资料写入DataOutputStream中 */
				sum += length;
				ds.write(buffer, 0, length);
			}
			LogPrint.Print("sum = "+sum);
//			ds.writeBytes(end);
//			ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
			/* close streams */
			bais.close();
			baos.close();
			ds.flush();
			/* 取得Response内容 */
			InputStream is = con.getInputStream();
			int ch;
			StringBuffer b = new StringBuffer();
			while ((ch = is.read()) != -1) {
				b.append((char) ch);
			}
			/* 将Response显示于Dialog */
			mHandler.sendEmptyMessage(1001);
			LogPrint.Print("============="+b.toString().trim());
//			showDialog("上传成功" + b.toString().trim());
			/* 关闭DataOutputStream */
			ds.close();
//			closeProgress();
		} catch (Exception e) {
//			closeProgress();
			mHandler.sendEmptyMessage(1002);
			LogPrint.Print("============="+e.toString());
//			showDialog("上传失败" + e);
		}
	}

//	private SystemProgress progress;
	public void addProgress(){
		try {
//			if(progress != null)return;
//			progress = new SystemProgress(this, this, null){
//				public void cancelData(){
//					
//				}
//			};
			if(loadingBar != null){
				loadingBar.setVisibility(View.VISIBLE);
			}
			
		} catch (Exception e) {
		}
	}
	
	public void closeProgress(){
//		if(progress != null){
//			progress.close();
//			progress = null;
//		}
		if(loadingBar != null){
			loadingBar.setVisibility(View.INVISIBLE);
		}
	}
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 1001://上传成功
				closeProgress();
				CommonUtil.ShowToast(CropActivity.this, "耶！成功上传！");
				if(image != null){
					image.recycle();
				}
				if(uploadImage != null){
					uploadImage.recycle();
				}
				finish();
				break;
			case 1002://上传失败
				closeProgress();
				CommonUtil.ShowToast(CropActivity.this, "sorry啦，上传失败了，要不再试一次？");
				break;
			}
		}
		
	};
}
