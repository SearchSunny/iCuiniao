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

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.menuclick.Setting_MenuClick;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.PageID;
import com.cmmobi.icuiniao.util.SystemProgress;
import com.cmmobi.icuiniao.util.URLUtil;
import com.cmmobi.icuiniao.util.UserUtil;

/**
 * @author hw
 *
 */
public class FormUpLoadActivity extends Activity{

	private Button titlebar_backbutton;
	private Button titlebar_menubutton;
	private EditText edittext;
	private Button camrebtn;
	private ImageView imageView;
	private Bitmap photo;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.formupload);
		getWindow().setSoftInputMode(  
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		titlebar_backbutton = (Button)findViewById(R.id.titlebar_backbutton);
		titlebar_menubutton = (Button)findViewById(R.id.titlebar_menubutton);
		edittext = (EditText)findViewById(R.id.edittext);
		camrebtn = (Button)findViewById(R.id.camrebtn);
		imageView = (ImageView)findViewById(R.id.image);
		
		titlebar_backbutton.setOnClickListener(backClickListener);
		titlebar_menubutton.setOnClickListener(sendClickListener);
		camrebtn.setOnClickListener(camreClickListener);
	}
	
	public void finish(){
		if(photo != null){
			photo.recycle();
		}
		setResult(950);
		super.finish();
	}
	
	private OnClickListener backClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			finish();
		}
	};
	
	private OnClickListener sendClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			final InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			if(imm != null){
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}
			if(UserUtil.userid == -1||UserUtil.userState != 1){//未登陆
				CommonUtil.ShowToast(getApplicationContext(), "请登陆或者注册，变身为小C的主人！");
				Intent intent11 = new Intent();
				intent11.setClass(FormUpLoadActivity.this, LoginAndRegeditActivity.class);
				startActivity(intent11);
			}else{
				uploadImage();
			}
		}
	};
	
	private OnClickListener camreClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			final InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			if(imm != null){
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}
			Setting_MenuClick menuClick = new Setting_MenuClick(mHandler);
			Intent intent = new Intent();
			intent.setClass(FormUpLoadActivity.this, AbsCuiniaoMenu.class);
			intent.putExtra("title", PageID.SETTING_MENU_TITLE);
			intent.putExtra("items", PageID.SETTING_MENU_ITEM);
			startActivity(intent);
			AbsCuiniaoMenu.set(menuClick);
		}
	};
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case MessageID.MESSAGE_CONNECT_START:
				addProgress();
				break;
			case MessageID.MESSAGE_CONNECT_ERROR:
				closeProgress();
				CommonUtil.ShowToast(getApplicationContext(), (String)msg.obj);
				break;
			case MessageID.MESSAGE_CONNECT_DOWNLOADOVER:
				Json((byte[])msg.obj);
				break;
			case MessageID.MESSAGE_MENUCLICK_SETTING_CAMERA:
				Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
				startActivityForResult(intent, 900);//相机
				break;
			case MessageID.MESSAGE_MENUCLICK_SETTING_PHOTO:
				Intent intent1 =new Intent(Intent.ACTION_GET_CONTENT);
				intent1.addCategory(Intent.CATEGORY_OPENABLE);
				intent1.setType("image/*");
				intent1.putExtra("crop", "true");
				intent1.putExtra("aspectX", 1);
				intent1.putExtra("aspectY", 1);
				intent1.putExtra("outputX", CommonUtil.screen_width);
				intent1.putExtra("outputY", CommonUtil.screen_width);
				intent1.putExtra("return-data", true);
				startActivityForResult(intent1, 901);//相册
				break;
			case MessageID.MESSAGE_PHOTO_RESULT:
				if(photo != null){
					imageView.setBackgroundDrawable(new BitmapDrawable(photo));
				}
				break;
			case MessageID.MESSAGE_CAMERA_RESULT:
				Intent intent3 = new Intent("com.android.camera.action.CROP");
				intent3.setType("image/*");
				intent3.putExtra("data", photo);
				intent3.putExtra("crop", "true");
				intent3.putExtra("aspectX", 1);
				intent3.putExtra("aspectY", 1);
				intent3.putExtra("outputX", CommonUtil.screen_width);
				intent3.putExtra("outputY", CommonUtil.screen_width);
				intent3.putExtra("return-data", true);
				startActivityForResult(intent3, 902);
				break;
			case 1002://上传失败
				CommonUtil.ShowToast(FormUpLoadActivity.this, "sorry啦，上传失败了，要不再试一次？");
				break;
			}
		}
		
	};
	
	private SystemProgress progress;
	public void addProgress(){
		try {
			if(progress != null)return;
			progress = new SystemProgress(this, this, null){
				public void cancelData(){
					
				}
			};
			
		} catch (Exception e) {
		}
	}
	
	public void closeProgress(){
		if(progress != null){
			progress.close();
			progress = null;
		}
	}
	
	public String addUrlParam(String url,int oid,String msg){
		if(URLUtil.IsLocalUrl()){
			return url;
		}
		if(msg == null)msg="";
		return url+"?oid="+oid+"&msg="+CommonUtil.toUrlEncode(msg);
	}

	//处理相机的回调结果
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 900||requestCode == 901||requestCode == 902){//相机,相册
			photo = null;
			if(data == null)return;
			LogPrint.Print("requestCode = "+requestCode);
			
			photo = data.getParcelableExtra("data");
			if(photo != null){
				if(requestCode == 901||requestCode == 902){
					mHandler.sendEmptyMessageDelayed(MessageID.MESSAGE_PHOTO_RESULT, 1000);
				}else if(requestCode == 900){
					mHandler.sendEmptyMessageDelayed(MessageID.MESSAGE_CAMERA_RESULT, 1000);
				}
			}
		}
	}
	
	//上传图片
	private void uploadImage(){
		addProgress();
		new Thread(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				uploadFile(addUrlParam(URLUtil.URL_FORM_UPLOAD, UserUtil.userid, edittext.getText().toString().trim()));
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
			photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
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
			Message msg = new Message();
			msg.what = MessageID.MESSAGE_CONNECT_DOWNLOADOVER;
			msg.obj = b.toString().trim().getBytes("utf-8");
			mHandler.sendMessage(msg);
			LogPrint.Print("============="+b.toString().trim());
//			showDialog("上传成功" + b.toString().trim());
			/* 关闭DataOutputStream */
			ds.close();
			closeProgress();
		} catch (Exception e) {
			closeProgress();
			mHandler.sendEmptyMessage(1002);
			LogPrint.Print("============="+e.toString());
//			showDialog("上传失败" + e);
		}
	}
	
	private void Json(byte[] data){
		try {
			String str = new String(data,"UTF-8");
			str = CommonUtil.formUrlEncode(str);
			JSONObject jObject = new JSONObject(str);
			String result = jObject.getString("result");
			if(result != null){
				if(result.equalsIgnoreCase("true")){
					CommonUtil.ShowToast(FormUpLoadActivity.this, "耶！成功上传！");
				}else{
					String msg = jObject.getString("msg");
					if(msg != null){
						CommonUtil.ShowToast(FormUpLoadActivity.this, "sorry啦，上传失败了，要不再试一次？\n"+msg);
					}else{
						CommonUtil.ShowToast(FormUpLoadActivity.this, "sorry啦，上传失败了，要不再试一次？");
					}
				}
			}else{
				CommonUtil.ShowToast(FormUpLoadActivity.this, "sorry啦，上传失败了，要不再试一次？");
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
