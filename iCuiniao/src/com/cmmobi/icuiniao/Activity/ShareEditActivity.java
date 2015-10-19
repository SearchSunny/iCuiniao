/**
 * 
 */
package com.cmmobi.icuiniao.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.InputFilter.LengthFilter;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.ConnectUtil;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.URLUtil;
import com.cmmobi.icuiniao.util.UserUtil;
import com.cmmobi.icuiniao.util.connction.HttpThread;
import com.cmmobi.icuiniao.weibo.WeiboLoginActivity;

/**
 * @author hw
 *分享编辑界面
 */
public class ShareEditActivity extends Activity{
	
	private final static String WEIBO_TEXT = "喜欢这个，推荐给朋友们。";
	private final static String QQ_TEXT = "我给的恰好是您的所爱！";
	private final static int shortWord = 27;//短链接长度
	private final static int clientWord = 25;//客户端下载地址长度
	private final static int weibo_maxNum = 140;//微博可输入的最大字数
	private final static int qq_maxNum = 200;//qq可输入的最大字数
	private int curMaxNum;//可编辑的最大字数
	private int input_num;//剩余字数
	
	private String urlString;
	private String titleString;
	private String commodityImageString;
	private String commodityInfoString;
	private int commodityid;
	private int logintype;
	private byte[] image;
	private Button titlebar_backbutton;
	private Button titlebar_menubutton;
	private TextView titlebar_titletext;
	private EditText shareedit_edit;
	private TextView shareedit_text;
	private ImageView shareedit_image;
	private TextView shareedit_num;
	//add by lyb 小鱼loading
	private ProgressBar loadingBar;

	private String dir;
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.shareedit);
		titlebar_backbutton = (Button)findViewById(R.id.titlebar_backbutton);
		titlebar_menubutton = (Button)findViewById(R.id.titlebar_menubutton);
		titlebar_titletext = (TextView)findViewById(R.id.titlebar_titletext);
		shareedit_edit = (EditText)findViewById(R.id.shareedit_edit);
		shareedit_text = (TextView)findViewById(R.id.shareedit_text);
		shareedit_image = (ImageView)findViewById(R.id.shareedit_image);
		shareedit_num = (TextView)findViewById(R.id.shareedit_num);
		//lyb
		loadingBar = (ProgressBar)findViewById(R.id.loading);
		titlebar_backbutton.setOnClickListener(backClickListener);
		titlebar_menubutton.setOnClickListener(sendClickListener);
		
		titleString = getIntent().getExtras().getString("title");
		commodityImageString = getIntent().getExtras().getString("image");
        commodityInfoString = getIntent().getExtras().getString("commodityInfoString");
        commodityid = getIntent().getExtras().getInt("commodityid");
        logintype = getIntent().getExtras().getInt("logintype");
        urlString = getIntent().getExtras().getString("urlString");
        
        if(logintype == 1){//qq
        	curMaxNum = qq_maxNum-titleString.length()-shortWord-clientWord-commodityInfoString.length();
        	shareedit_edit.setText(QQ_TEXT);
        	titlebar_titletext.setText("腾讯空间");
        }else if(logintype == 2){//weibo
        	curMaxNum = weibo_maxNum-titleString.length()-shortWord-clientWord;
        	shareedit_edit.setText(WEIBO_TEXT);
        	titlebar_titletext.setText("新浪微博");
        }
     
        InputFilter[] filters = {new LengthFilter(curMaxNum)};
        shareedit_edit.setFilters(filters);
        shareedit_edit.setSelection(shareedit_edit.length());
        input_num = curMaxNum-shareedit_edit.getText().length();
        shareedit_num.setText(""+input_num);
        shareedit_edit.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				shareedit_num.setText(""+input_num);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				shareedit_num.setText(""+input_num);
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				input_num = curMaxNum-shareedit_edit.getText().length();
				shareedit_num.setText(""+input_num);
			}
		});
        shareedit_text.setText(titleString);
        //获取图片
        if(commodityImageString != null&&commodityImageString.length() > 0&&CommonUtil.isNetWorkOpen(this)){
        	addProgress();
        	new ConnectUtil(this, mHandler, 0).connect(commodityImageString, HttpThread.TYPE_IMAGE, 0);
        	
        }
        //此路径用于新浪微博分享
        dir = CommonUtil.dir_cache+"/"+CommonUtil.urlToNum(commodityImageString);
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
			if(CommonUtil.isNetWorkOpen(ShareEditActivity.this)){
				send();
			}else{
				CommonUtil.ShowToast(ShareEditActivity.this, "杯具了- -!\n联网不给力啊");
			}
		}
	};
	
	private void send(){
		if(logintype == 1){//qq
			Intent qqIntent = new Intent(ShareEditActivity.this, WebViewLoginActivity.class);
			LogPrint.Print("webview","url = "+addUrlParam(URLUtil.URL_SHARE,1));
			qqIntent.putExtra("url", addUrlParam(URLUtil.URL_SHARE,1));
			startActivity(qqIntent);
			finish();
		}else if(logintype == 2){//weibo
			Intent intent = new Intent();
			LogPrint.Print("webview","url = "+addUrlParam(URLUtil.URL_SHARE,2));
			//intent.putExtra("url", addUrlParam(URLUtil.URL_SHARE,2));
			intent.putExtra("actionType", "share");
			//商品ID
			intent.putExtra("pid", commodityid);
			//标题
			intent.putExtra("title", titleString);
			//描述
			intent.putExtra("info", commodityInfoString);
			//图片地址
			intent.putExtra("image", dir);
			intent.putExtra("urlString", urlString);
	  		intent.setClass(ShareEditActivity.this, WeiboLoginActivity.class);
			startActivity(intent);
			finish();
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
			case MessageID.MESSAGE_CONNECT_ERROR:
				closeProgress();
				CommonUtil.ShowToast(getApplicationContext(), (String)msg.obj);
				break;
			case MessageID.MESSAGE_CONNECT_DOWNLOADOVER:
				closeProgress();
				image = (byte[])msg.obj;
				setImageView((byte[])msg.obj);
				break;
			}
		}
		
	};
	
	public void setImageView(byte[] data){
		try {
			Bitmap temp = BitmapFactory.decodeByteArray(data, 0, data.length);
			shareedit_image.setBackgroundDrawable(new BitmapDrawable(getCoverBitmap(temp)));
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public String addUrlParam(String url,int logintype){
  		if(URLUtil.IsLocalUrl()){
			return url;
		}
  		return url+"?pid="+commodityid+"&oid="+UserUtil.userid+"&dpi="+URLUtil.dpi()+"&logintype="+logintype+"&plaid="+URLUtil.plaid+"&ver="+URLUtil.version+"&title="+CommonUtil.toUrlEncode(shareedit_edit.getText().toString().trim());
  	}
	
	private Bitmap getCoverBitmap(Bitmap src){
		int dpi = getResources().getDisplayMetrics().densityDpi;
		
		//创建图像位
		Bitmap bmp = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Config.ARGB_8888);
		//创建画布
    	Canvas canvas = new Canvas();
    	//创建画笔
    	Paint paint = new Paint();
    	canvas.setBitmap(bmp);
    	canvas.drawBitmap(src, 0, 0, null);
    	//绘制边框
    	paint.setColor(0xffacacac);
    	paint.setStyle(Style.STROKE);
    	if(dpi<=160){
    		canvas.drawRect(CommonUtil.dip2px(this, 2), CommonUtil.dip2px(this, 2), src.getWidth()-CommonUtil.dip2px(this, 4), src.getHeight()-CommonUtil.dip2px(this, 4), paint);
    	}
    	else{
    		canvas.drawRect(CommonUtil.dip2px(this, 1), CommonUtil.dip2px(this, 1), src.getWidth()-CommonUtil.dip2px(this, 2), src.getHeight()-CommonUtil.dip2px(this, 2), paint);
    	}   	
    	canvas.save();
    	return bmp;
	}
}
