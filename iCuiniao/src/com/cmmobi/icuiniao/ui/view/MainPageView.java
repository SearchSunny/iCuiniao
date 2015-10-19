/**
 * 
 */
package com.cmmobi.icuiniao.ui.view;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.onlineEngine.activity.MainPageActivityA;
import com.cmmobi.icuiniao.onlineEngine.activity.MyPageActivityA;
import com.cmmobi.icuiniao.onlineEngine.activity.RockActivityA;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_Image;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_Layout_AbsLayout;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_MainPage;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_ParserEngine;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.ConnectUtil;
import com.cmmobi.icuiniao.util.DownImageItem;
import com.cmmobi.icuiniao.util.DownImageManagerA;
import com.cmmobi.icuiniao.util.DownLoadPool;
import com.cmmobi.icuiniao.util.DownLoadPoolItem;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.MHandlerThread;
import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.OfflineLog;
import com.cmmobi.icuiniao.util.OfflineTimeService;
import com.cmmobi.icuiniao.util.TimerForRock;
import com.cmmobi.icuiniao.util.URLUtil;
import com.cmmobi.icuiniao.util.UserUtil;
import com.cmmobi.icuiniao.util.connction.HttpThread;

/**
 * @author hw
 *
 */
public class MainPageView extends LinearLayout{

	private LinearLayout pagelayout;
	private int pageIndex;
	private Context mContext;
	
	private ConnectUtil mConnectUtil;
	private Parser_ParserEngine parserEngine;
	private String connectUrl;
	private String cacheUrl;//缓存信息的请求地址
	private int cacheThreadindex;//缓存专署线程号
	public static boolean isStartLoadPage;//是否开始加载页面,需要在每次预下载缓存前重置为false
	public static boolean isDownLoadCache;//是否预先加载页面缓存
	public static boolean isSensor;
	
	private Parser_MainPage parserMainPage;
	private ImageView[] imageViews;
	private Parser_Image[] parserImages;//图片数组
	
	private int[] modelIndex;//大图位置索引
	
	private final static int MODELTYPE_1_BIGIMAGEINDEX = 0;//模板1大图固定位置
	private final static int MODELTYPE_2_BIGIMAGEINDEX = 3;//模板2大图固定位置
	private final static int MODELTYPE_3_BIGIMAGEINDEX = 6;//模板3大图固定位置
	private final static int MODELTYPE_4_BIGIMAGEINDEX = 1;//模板4大图固定位置
	private final static int MODELTYPE_5_BIGIMAGEINDEX = 4;//模板5大图固定位置
	private final static int MODELTYPE_6_BIGIMAGEINDEX = 7;//模板6大图固定位置
	private int smallImageWH;//小图的尺寸
	private int bigImageWH;//大图的尺寸
	private int bigPos = 0;
	private ProgressBar loadingBar;
	private DownImageManagerA downImageManagerA;
	
	private int timeCount;
	private Timer mTimer;
	private TimerTask mTimerTask;
	
	public MainPageView(Context context,int pageindex) {
		super(context);
		mContext = context;
		pageIndex = pageindex;
		connectUrl = URLUtil.URL_MAINPAGE;
		cacheUrl = URLUtil.URL_MAINPAGE_CACHE_INFO;
		cacheThreadindex = URLUtil.THREAD_MAINPAGE_CACHE_INFO;
		downImageManagerA = new DownImageManagerA();
		// TODO Auto-generated constructor stub
		switch (pageindex) {
		case 0:
			bigPos = MODELTYPE_1_BIGIMAGEINDEX;
			pagelayout = (LinearLayout)((Activity)context).getLayoutInflater().inflate(R.layout.mainpage_modeltype_1,null);
			break;
		case 1:
			bigPos = MODELTYPE_2_BIGIMAGEINDEX;
			pagelayout = (LinearLayout)((Activity)context).getLayoutInflater().inflate(R.layout.mainpage_modeltype_2,null);
			break;
		case 2:
			bigPos = MODELTYPE_3_BIGIMAGEINDEX;
			pagelayout = (LinearLayout)((Activity)context).getLayoutInflater().inflate(R.layout.mainpage_modeltype_3,null);
			break;
		case 3:
			bigPos = MODELTYPE_4_BIGIMAGEINDEX;
			pagelayout = (LinearLayout)((Activity)context).getLayoutInflater().inflate(R.layout.mainpage_modeltype_4,null);
			break;
		case 4:
			bigPos = MODELTYPE_5_BIGIMAGEINDEX;
			pagelayout = (LinearLayout)((Activity)context).getLayoutInflater().inflate(R.layout.mainpage_modeltype_5,null);
			break;
		case 5:
			bigPos = MODELTYPE_6_BIGIMAGEINDEX;
			pagelayout = (LinearLayout)((Activity)context).getLayoutInflater().inflate(R.layout.mainpage_modeltype_6,null);
			break;
		}
		loadingBar = (ProgressBar)pagelayout.findViewById(R.id.loading);
		newHandler();
		
		parserImages = new Parser_Image[9];
		imageViews = new ImageView[parserImages.length];
		imageViews[0] = (ImageView)pagelayout.findViewById(R.id.mainpage_img0);
		imageViews[1] = (ImageView)pagelayout.findViewById(R.id.mainpage_img1);
		imageViews[2] = (ImageView)pagelayout.findViewById(R.id.mainpage_img2);
		imageViews[3] = (ImageView)pagelayout.findViewById(R.id.mainpage_img3);
		imageViews[4] = (ImageView)pagelayout.findViewById(R.id.mainpage_img4);
		imageViews[5] = (ImageView)pagelayout.findViewById(R.id.mainpage_img5);
		imageViews[6] = (ImageView)pagelayout.findViewById(R.id.mainpage_img6);
		imageViews[7] = (ImageView)pagelayout.findViewById(R.id.mainpage_img7);
		imageViews[8] = (ImageView)pagelayout.findViewById(R.id.mainpage_img8);
		imageViews[0].setOnClickListener(imageViewClickListener0);
		imageViews[1].setOnClickListener(imageViewClickListener1);
		imageViews[2].setOnClickListener(imageViewClickListener2);
		imageViews[3].setOnClickListener(imageViewClickListener3);
		imageViews[4].setOnClickListener(imageViewClickListener4);
		imageViews[5].setOnClickListener(imageViewClickListener5);
		imageViews[6].setOnClickListener(imageViewClickListener6);
		imageViews[7].setOnClickListener(imageViewClickListener7);
		imageViews[8].setOnClickListener(imageViewClickListener8);
		
		addView(pagelayout);
	}
	
	public void initConnect(){
		timeCount = 0;
		if(mTimer == null){
			mTimer = new Timer();
			mTimerTask = new TimerTask() {
				public void run(){
					timeCount ++;
					if(timeCount > 30){//30秒检测
						timeCount = 0;
						mHandler.sendEmptyMessage(115522);
					}
				}
			};
			mTimer.schedule(mTimerTask, 0, 1000);
		}
		if(UserUtil.isNewLoginOrExit){
			UserUtil.isNewLoginOrExit = false;
			isSensor = false;
			//获取服务器时间
			mConnectUtil = new ConnectUtil(mContext, downHandler,pageIndex);
    		mConnectUtil.connect(URLUtil.URL_GET_SYSTIME, HttpThread.TYPE_PAGE, 11);
    		isDownLoadCache = false;
		}else{
			if(isSensor){
				addProgress();
			}
			if(isDownLoadCache){
				if(cacheUrl != null){
					mConnectUtil = new ConnectUtil(mContext, downHandler,pageIndex);
					mConnectUtil.connect(addUrlParam(cacheUrl, pageIndex, UserUtil.userid), HttpThread.TYPE_PAGE, cacheThreadindex);
					isDownLoadCache = false;
				}
			}else{
				if(connectUrl != null){
					mConnectUtil = new ConnectUtil(mContext, downHandler,pageIndex);
					mConnectUtil.connect(addUrlParam(connectUrl,URLUtil.dpi(),pageIndex,UserUtil.userid), HttpThread.TYPE_PAGE,0);
				}
			}
		}
	}
	
	private Parser_Image[] order(int pos,int bigpos){
		int bigImageIndex = bigpos;//固定大图位置
		if(pos == bigImageIndex)return parserImages;
		
		Parser_Image[] tmpImages = new Parser_Image[parserImages.length];
		tmpImages[bigImageIndex] = parserImages[pos];
		for(int i = 0;i < parserImages.length;i ++){
			if(tmpImages[i] == null){
				tmpImages[i] = parserImages[i];
			}else{
				if(i == parserImages.length-1)break;
				tmpImages[i+1] = parserImages[i];
			}
		}
		return tmpImages;
	}
    
	public void setImageView(final byte[] data,int index,final String cacheUrl,String apn){
		String imagePath;
		byte[] dataImage = null;
		String pixels = getPixels(CommonUtil.screen_width, CommonUtil.screen_height);
		String[] imageNames = cacheUrl.split("/");
		String url;
		try{
//			new Thread(){
//				public void run(){
					String string = cacheUrl;
					if(!CommonUtil.exists(CommonUtil.dir_cache+"/"+CommonUtil.urlToNum(cacheUrl))){
						CommonUtil.writeToFile(CommonUtil.dir_cache+"/"+CommonUtil.urlToNum(cacheUrl), data);
					}
//				}
//			}.start();
			
		}catch (Exception e) {
		}
		int bigImagePos = 0;
		switch (parserMainPage.getType()) {
		case Parser_Layout_AbsLayout.MODELTYPE_1:
			bigImagePos = MODELTYPE_1_BIGIMAGEINDEX;
			break;
		case Parser_Layout_AbsLayout.MODELTYPE_2:
			bigImagePos = MODELTYPE_2_BIGIMAGEINDEX;
			break;
		case Parser_Layout_AbsLayout.MODELTYPE_3:
			bigImagePos = MODELTYPE_3_BIGIMAGEINDEX;
			break;
		case Parser_Layout_AbsLayout.MODELTYPE_4:
			bigImagePos = MODELTYPE_4_BIGIMAGEINDEX;
			break;
		case Parser_Layout_AbsLayout.MODELTYPE_5:
			bigImagePos = MODELTYPE_5_BIGIMAGEINDEX;
			break;
		case Parser_Layout_AbsLayout.MODELTYPE_6:
			bigImagePos = MODELTYPE_6_BIGIMAGEINDEX;
			break;
		}
		Bitmap temp = BitmapFactory.decodeByteArray(data, 0, data.length);
		if(temp == null)return;
		if(index == bigImagePos){
			if(temp.getWidth() != bigImageWH){
				if(apn.toLowerCase().indexOf("wifi") >= 0){
					temp = CommonUtil.resizeImage(temp, bigImageWH, bigImageWH);
				}
//				if(apn.toLowerCase().indexOf("wifi") < 0){
//					if(apn.toLowerCase().indexOf("3g") >= 0){
//						temp = CommonUtil.resizeImage(temp, bigImageWH, bigImageWH);
//					}
//				}
//				else{
//					temp = CommonUtil.resizeImage(temp, bigImageWH, bigImageWH);
//				}
			}
		}else{
			temp = CommonUtil.resizeImage(temp, smallImageWH, smallImageWH);
		}
		Bitmap playBitmap = null;
		if(parserImages[index].getPlayable()){
			if(index == bigImagePos){
				playBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.playicon_m);
			}else{
				playBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.playicon_s);
			}
		}
		String price = parserImages[index].getPrice();
		String feature = parserImages[index].getFeature();
		int color = 0;
		switch (parserImages[index].getType()) {
		case Parser_Layout_AbsLayout.TYPE_DISCOUNT://折扣
			color = 0xff0000ff;
			break;
		case Parser_Layout_AbsLayout.TYPE_RECOMMEND://推荐
			color = 0xffff00ff;
			break;
		case Parser_Layout_AbsLayout.TYPE_FORM://晒单
			color = 0xffffff00;
			break;
		case Parser_Layout_AbsLayout.TYPE_ACTIVITIES://活动
			color = 0xff00ff00;
			break;
		default://异常情况,type为空
			color = 0x00ffffff;	
			break;
		}
		if(apn.toLowerCase().indexOf("wifi") < 0){
//			if(apn.toLowerCase().indexOf("3g") >= 0){
//				temp = CommonUtil.mergerBitmapForMainPage(getContext(),temp, color, playBitmap);
//				imageViews[index].setBackgroundDrawable(new BitmapDrawable(temp));
//				postInvalidate();
//			}else{
				String[] imageName = cacheUrl.split("/");
				String imageUrl = URLUtil.userIconHost+"/terminalconnector/resource/product/pic/"+pixels;
				String mImageUrl = imageUrl + "/M/"+imageName[imageName.length-1];
				String lImageUrl = imageUrl + "/L/"+imageName[imageName.length-1];
				String mImagePath = CommonUtil.dir_cache + "/" + CommonUtil.urlToNum(mImageUrl);
				String lImagePath = CommonUtil.dir_cache + "/" + CommonUtil.urlToNum(lImageUrl);
				switch (pageIndex) {
				case 0:
					if(index == MODELTYPE_1_BIGIMAGEINDEX){
						if(!CommonUtil.exists(mImagePath)&&!CommonUtil.exists(lImagePath)){
							temp = CommonUtil.addBoxLine(CommonUtil.mergerBitmapForMainPageBy2g(getContext(), temp, imageViews[index].getWidth(), imageViews[index].getHeight(),playBitmap,price,feature));
						}
						else{
							if(CommonUtil.exists(mImagePath)){
								dataImage = CommonUtil.getSDCardFileByteArray(mImagePath);
							}
							else if(CommonUtil.exists(lImagePath)){
								dataImage = CommonUtil.getSDCardFileByteArray(lImagePath);
							}
							temp = BitmapFactory.decodeByteArray(dataImage, 0, dataImage.length);
							temp = CommonUtil.mergerBitmapForMainPage(getContext(),temp, color, playBitmap,price,feature);
						}		
						imageViews[index].setBackgroundDrawable(new BitmapDrawable(temp));
					}else{
						temp = CommonUtil.mergerBitmapForMainPage(getContext(),temp, color, playBitmap,price,feature);
						imageViews[index].setBackgroundDrawable(new BitmapDrawable(temp));
					}
					break;
				case 1:
					if(index == MODELTYPE_2_BIGIMAGEINDEX){
						if(!CommonUtil.exists(mImagePath)&&!CommonUtil.exists(lImagePath)){
							temp = CommonUtil.addBoxLine(CommonUtil.mergerBitmapForMainPageBy2g(getContext(), temp, imageViews[index].getWidth(), imageViews[index].getHeight(),playBitmap,price,feature));
						}
						else{
							if(CommonUtil.exists(mImagePath)){
								dataImage = CommonUtil.getSDCardFileByteArray(mImagePath);
							}
							else if(CommonUtil.exists(lImagePath)){
								dataImage = CommonUtil.getSDCardFileByteArray(lImagePath);
							}
							temp = BitmapFactory.decodeByteArray(dataImage, 0, dataImage.length);
							temp = CommonUtil.mergerBitmapForMainPage(getContext(),temp, color, playBitmap,price,feature);
						}	
						imageViews[index].setBackgroundDrawable(new BitmapDrawable(temp));
					}else{
						temp = CommonUtil.mergerBitmapForMainPage(getContext(),temp, color, playBitmap,price,feature);
						imageViews[index].setBackgroundDrawable(new BitmapDrawable(temp));
					}
					break;
				case 2:
					if(index == MODELTYPE_3_BIGIMAGEINDEX){
						if(!CommonUtil.exists(mImagePath)&&!CommonUtil.exists(lImagePath)){
							temp = CommonUtil.addBoxLine(CommonUtil.mergerBitmapForMainPageBy2g(getContext(), temp, imageViews[index].getWidth(), imageViews[index].getHeight(),playBitmap,price,feature));
						}
						else{
							if(CommonUtil.exists(mImagePath)){
								dataImage = CommonUtil.getSDCardFileByteArray(mImagePath);
							}
							else if(CommonUtil.exists(lImagePath)){
								dataImage = CommonUtil.getSDCardFileByteArray(lImagePath);
							}
							temp = BitmapFactory.decodeByteArray(dataImage, 0, dataImage.length);
							temp = CommonUtil.mergerBitmapForMainPage(getContext(),temp, color, playBitmap,price,feature);
						}						
						imageViews[index].setBackgroundDrawable(new BitmapDrawable(temp));
					}else{
						temp = CommonUtil.mergerBitmapForMainPage(getContext(),temp, color, playBitmap,price,feature);
						imageViews[index].setBackgroundDrawable(new BitmapDrawable(temp));
					}
					break;
				case 3:
					if(index == MODELTYPE_4_BIGIMAGEINDEX){
						if(!CommonUtil.exists(mImagePath)&&!CommonUtil.exists(lImagePath)){
							temp = CommonUtil.addBoxLine(CommonUtil.mergerBitmapForMainPageBy2g(getContext(), temp, imageViews[index].getWidth(), imageViews[index].getHeight(),playBitmap,price,feature));
						}
						else{
							if(CommonUtil.exists(mImagePath)){
								dataImage = CommonUtil.getSDCardFileByteArray(mImagePath);
							}
							else if(CommonUtil.exists(lImagePath)){
								dataImage = CommonUtil.getSDCardFileByteArray(lImagePath);
							}
							temp = BitmapFactory.decodeByteArray(dataImage, 0, dataImage.length);
							temp = CommonUtil.mergerBitmapForMainPage(getContext(),temp, color, playBitmap,price,feature);
						}						
						imageViews[index].setBackgroundDrawable(new BitmapDrawable(temp));
					}else{
						temp = CommonUtil.mergerBitmapForMainPage(getContext(),temp, color, playBitmap,price,feature);
						imageViews[index].setBackgroundDrawable(new BitmapDrawable(temp));
					}
					break;
				case 4:
					if(index == MODELTYPE_5_BIGIMAGEINDEX){
						if(!CommonUtil.exists(mImagePath)&&!CommonUtil.exists(lImagePath)){
							temp = CommonUtil.addBoxLine(CommonUtil.mergerBitmapForMainPageBy2g(getContext(), temp, imageViews[index].getWidth(), imageViews[index].getHeight(),playBitmap,price,feature));
						}
						else{
							if(CommonUtil.exists(mImagePath)){
								dataImage = CommonUtil.getSDCardFileByteArray(mImagePath);
							}
							else if(CommonUtil.exists(lImagePath)){
								dataImage = CommonUtil.getSDCardFileByteArray(lImagePath);
							}
							temp = BitmapFactory.decodeByteArray(dataImage, 0, dataImage.length);
							temp = CommonUtil.mergerBitmapForMainPage(getContext(),temp, color, playBitmap,price,feature);
						}
						imageViews[index].setBackgroundDrawable(new BitmapDrawable(temp));
					}else{
						temp = CommonUtil.mergerBitmapForMainPage(getContext(),temp, color, playBitmap,price,feature);
						imageViews[index].setBackgroundDrawable(new BitmapDrawable(temp));
					}
					break;
				case 5:
					if(index == MODELTYPE_6_BIGIMAGEINDEX){
						if(!CommonUtil.exists(mImagePath)&&!CommonUtil.exists(lImagePath)){
							temp = CommonUtil.addBoxLine(CommonUtil.mergerBitmapForMainPageBy2g(getContext(), temp, imageViews[index].getWidth(), imageViews[index].getHeight(),playBitmap,price,feature));
						}
						else{
							if(CommonUtil.exists(mImagePath)){
								dataImage = CommonUtil.getSDCardFileByteArray(mImagePath);
							}
							else if(CommonUtil.exists(lImagePath)){
								dataImage = CommonUtil.getSDCardFileByteArray(lImagePath);
							}
							temp = BitmapFactory.decodeByteArray(dataImage, 0, dataImage.length);
							temp = CommonUtil.mergerBitmapForMainPage(getContext(),temp, color, playBitmap,price,feature);
						}
						imageViews[index].setBackgroundDrawable(new BitmapDrawable(temp));
					}else{
						temp = CommonUtil.mergerBitmapForMainPage(getContext(),temp, color, playBitmap,price,feature);
						imageViews[index].setBackgroundDrawable(new BitmapDrawable(temp));
					}
					break;
				}
				
				postInvalidate();
//			}
		}
		else{
			temp = CommonUtil.mergerBitmapForMainPage(getContext(),temp, color, playBitmap,price,feature);
			imageViews[index].setBackgroundDrawable(new BitmapDrawable(temp));
			postInvalidate();
		}
	}

	public void initImageSize(int dpi){
		LogPrint.Print("dpi = "+dpi);
		if(dpi <= 120){//qvga
			smallImageWH = 60;
			bigImageWH = 126;
		}else if(dpi <= 160){//hvga
			smallImageWH = 95;
			bigImageWH = 196;
		}else if(dpi <= 320){//wvga
			smallImageWH = 140;
			bigImageWH = 290;
			if(CommonUtil.screen_width >= 540&&CommonUtil.screen_height >= 960){
				smallImageWH = 160;
				bigImageWH = 330;
			}else if(CommonUtil.screen_height >= 1280){
				smallImageWH = 217;
				bigImageWH = 448;
				if(CommonUtil.screen_width >= 800){
					smallImageWH = 244;
					bigImageWH = 502;
				}
			}
		}else{//更大屏幕分辨率
			smallImageWH = 160;
			bigImageWH = 330;
		}
		LogPrint.Print("smallImageWH = "+smallImageWH);
		LogPrint.Print("bigImageWH = "+bigImageWH);
	}
	
	private OnClickListener imageViewClickListener0 = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(parserImages[0]!=null&&parserImages[0].getHref()!=null&&parserImages[0].getHref().length() > 0){
				onMainPageItemClick(parserImages[0].getHref(), parserImages[0].getType(),0);
			}
		}
	};
	
	private OnClickListener imageViewClickListener1 = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(parserImages[1]!=null&&parserImages[1].getHref()!=null&&parserImages[1].getHref().length() > 0){
				onMainPageItemClick(parserImages[1].getHref(), parserImages[1].getType(),1);
			}
		}
	};
	
	private OnClickListener imageViewClickListener2 = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(parserImages[2]!=null&&parserImages[2].getHref()!=null&&parserImages[2].getHref().length() > 0){
				onMainPageItemClick(parserImages[2].getHref(), parserImages[2].getType(),2);
			}
		}
	};
	
	private OnClickListener imageViewClickListener3 = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(parserImages[3]!=null&&parserImages[3].getHref()!=null&&parserImages[3].getHref().length() > 0){
				onMainPageItemClick(parserImages[3].getHref(), parserImages[3].getType(),3);
			}
		}
	};
	
	private OnClickListener imageViewClickListener4 = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(parserImages[4]!=null&&parserImages[4].getHref()!=null&&parserImages[4].getHref().length() > 0){
				onMainPageItemClick(parserImages[4].getHref(), parserImages[4].getType(),4);
			}
		}
	};
	
	private OnClickListener imageViewClickListener5 = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(parserImages[5]!=null&&parserImages[5].getHref()!=null&&parserImages[5].getHref().length() > 0){
				onMainPageItemClick(parserImages[5].getHref(), parserImages[5].getType(),5);
			}
		}
	};
	
	private OnClickListener imageViewClickListener6 = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(parserImages[6]!=null&&parserImages[6].getHref()!=null&&parserImages[6].getHref().length() > 0){
				onMainPageItemClick(parserImages[6].getHref(), parserImages[6].getType(),6);
			}
		}
	};
	
	private OnClickListener imageViewClickListener7 = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(parserImages[7]!=null&&parserImages[7].getHref()!=null&&parserImages[7].getHref().length() > 0){
				onMainPageItemClick(parserImages[7].getHref(), parserImages[7].getType(),7);
			}
		}
	};
	
	private OnClickListener imageViewClickListener8 = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(parserImages[8]!=null&&parserImages[8].getHref()!=null&&parserImages[8].getHref().length() > 0){
				onMainPageItemClick(parserImages[8].getHref(), parserImages[8].getType(),8);
			}
		}
	};
	
	private MHandlerThread mHandlerThread;
	public Handler downHandler;
	//用于下载接收的异步handler
	private void newHandler(){
		mHandlerThread = new MHandlerThread("MainPageActivityA");
		mHandlerThread.start();
		downHandler = new Handler(mHandlerThread.getLooper(), mHandlerThread){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case MessageID.MESSAGE_CONNECT_START:
					mHandler.sendEmptyMessage(MessageID.MESSAGE_CONNECT_START);
					break;
				case MessageID.MESSAGE_CONNECT_ERROR:
					CommonUtil.ShowToast(mContext, (String)msg.obj);
					if(msg.arg1 == 20){
						mHandler.sendEmptyMessage(115511);
					}else{
						mHandler.sendEmptyMessage(MessageID.MESSAGE_CONNECT_ERROR);
					}
					break;
				case MessageID.MESSAGE_CONNECT_DOWNLOADOVER:
					if((msg.arg1 >= URLUtil.THREAD_MAINPAGE_CACHE_INFO&&msg.arg1 <= URLUtil.THREAD_MAINPAGE_LOOKED_CACHE_INFO)||msg.arg1 == 11){
						//关闭loading
						mHandler.sendEmptyMessage(MessageID.MESSAGE_CLOSEPROGRESS);
						Json((byte[])msg.obj,msg.getData().getString("content_type"),msg.arg1);
					}else if(msg.arg1 == 20){
						Json1((byte[])msg.obj, msg.getData().getString("content_type"));
					}else if(msg.arg1 == 33){
						Json2((byte[])msg.obj, msg.getData().getString("content_type"));
					}else{
						buildPage(msg,msg.arg1, msg.getData().getInt("mType"));
					}
					break;
				}
			}
			
		};
	}
	
	public Handler mHandler = new Handler(){

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
				timeCount = 0;
				if(mTimer != null){
	    			mTimer.cancel();
	    			mTimer = null;
	    			mTimerTask = null;
	    		}
				break;
			case MessageID.MESSAGE_CLOSEPROGRESS:
				closeProgress();
				break;
			case MessageID.MESSAGE_RENDER:
				renderPage(msg,msg.arg1,msg.getData().getInt("mType"));
				break;
			case MessageID.MESSAGE_CONNECT_LAYOUTOVER:
				//取出一个可用的图片下载
				DownImageItem downImageItem = downImageManagerA.get();
				if(downImageItem != null){
					LogPrint.Print("image downloading");
					//只有在同一页面中才发起图片下载
					if(parserEngine!=null&&parserEngine.getPageObject()!=null){
						if(parserEngine.getPageObject().getPageId() == downImageItem.getPageId()&&
								pageIndex == downImageItem.getTag()){
							String urlString = downImageItem.getUrl();
							LogPrint.Print("urlString =  "+urlString);
							if(urlString != null){
								new ConnectUtil(mContext, downHandler,pageIndex).connect(urlString, HttpThread.TYPE_IMAGE, downImageItem.getIndex());
							}
							//发起下一个询问
							mHandler.sendEmptyMessageDelayed(MessageID.MESSAGE_CONNECT_LAYOUTOVER, 100);
						}else{
							DownLoadPool.getInstance(mContext).Notify();
						}
					}
				}else{
					LogPrint.Print("downImageItem = null");
				}
				break;
			case MessageID.MESSAGE_DOWNLOAD_CACHE:
				getAllCommdityUrl();
				break;
			case MessageID.MESSAGE_CLICK_MAINPAGE_ITEM:
				if(mTimer != null){
	    			mTimer.cancel();
	    			mTimer = null;
	    			mTimerTask = null;
	    		}
				DownLoadPool.getInstance(mContext).Wait();
				Intent intent = new Intent();
				intent.setClass(mContext, MyPageActivityA.class);
				intent.putExtra("url", msg.getData().getString("url"));
				intent.putExtra("chickPos", msg.getData().getInt("chickPos"));
				intent.putExtra("type", parserEngine.getPageObject().getPageId());
				intent.putExtra("pageindex", pageIndex);
				mContext.startActivity(intent);
//				((Activity)mContext).overridePendingTransition(R.anim.push_scale_in, R.anim.push_scale_out);//中心突出
				break;
			case 115511:
				new ConnectUtil(mContext, downHandler,false,pageIndex)
				.connect(addUrlParam(URLUtil.URL_MAINPAGE,URLUtil.dpi(),msg.arg1,UserUtil.userid), HttpThread.TYPE_PAGE,URLUtil.THREAD_CACHE);
				break;
			case 115522:
				CommonUtil.ShowToast(mContext, "        主人！        \n  网络好纠结，  \n但我一直在努力");
				closeTimer();//只提示一次
				break;
			case 335522:
				CommonUtil.ShowToast(mContext, "呼叫成功");
				break;
			case 335523:
				CommonUtil.ShowToast(mContext, "呼叫失败");
				break;
//			case MessageID.MESSAGE_CONNECT_DOWNLOADOVER: //权重上传结果
//				if ("text/json".equals(msg.getData().getString("content_type"))) {
//					try {
//						String str = new String((byte[]) msg.obj, "UTF-8");
//						str = CommonUtil.formUrlEncode(str);
//						LogPrint.Print("json", "json = " + str);
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//				break;
			}
		}
		
	};
	
	/**mainpage的图片响应*/
	public void onMainPageItemClick(String url, int type,int chickPos){
		LogPrint.Print("=======onMainPageItemClick=======");
		LogPrint.Print("url = "+url);
		LogPrint.Print("type = "+type);
		LogPrint.Print("chickPos = "+chickPos);
		LogPrint.Print("=============over======================");

		Bundle bundle = new Bundle();
		bundle.putString("url", url);
		bundle.putInt("type", type);
		bundle.putInt("chickPos", chickPos);
		Message msg = new Message();
		msg.setData(bundle);
		msg.what = MessageID.MESSAGE_CLICK_MAINPAGE_ITEM;
		mHandler.sendMessage(msg);		
		//上传商品权重
		uploadWeightProduct(chickPos);
	}
	
	//上传商品权重
	private void uploadWeightProduct(int index){
		final int weight = parserImages[index].getWeight();
		if(weight > -1){
			String url = parserImages[index].getHref();
			String cid = CommonUtil.getSubString(url, "cid=", "&dpi=");
			mConnectUtil = new ConnectUtil(mContext, mHandler,pageIndex);  //不用解析上传结果
			mConnectUtil.setShowProgress(false);
			mConnectUtil.connect(URLUtil.URL_UPLOAD_WEIGHT_PRODUCT + "?cid="+ cid + "&weighttype=" + weight, HttpThread.TYPE_PAGE,0);
		}
	}
	
	public String addUrlParam(String url,String dpi,int pi,int oid){
		if(URLUtil.IsLocalUrl()){
			return url;
		}
		if(url.indexOf("?") > 0){
			return url+"&dpi="+dpi+"&pi="+pi+"&plaid="+URLUtil.plaid+"&deviceid="+CommonUtil.getIMEI(mContext);
		}
		return url+"?dpi="+dpi+"&pi="+pi+"&plaid="+URLUtil.plaid+"&deviceid="+CommonUtil.getIMEI(mContext);
	}
	
	public String addUrlParam(String url,int pi,int oid){
		if(URLUtil.IsLocalUrl()){
			return url;
		}
		int type = 0;
		if(UserUtil.isLogout){
			type = 2;
		}else{
			type = isSensor?1:0;
		}
		return url+"?pi="+pi+"&type="+type+"&oid="+oid+"&dpi="+URLUtil.dpi()+"&deviceid="+CommonUtil.getIMEI(mContext)+"&plaid="+URLUtil.plaid;
	}
	
	public void addProgress(){
		if(loadingBar != null){
			loadingBar.setVisibility(View.VISIBLE);
		}
	}
	
	public void closeProgress(){
		if(loadingBar != null){
			loadingBar.setVisibility(View.INVISIBLE);
		}
	}
	
	private void Json(byte[] data,String contenttype,int threadindex){
		try{
			if("text/json".equals(contenttype)){
				String str = new String(data,"UTF-8");
				str = CommonUtil.formUrlEncode(str);
				LogPrint.Print("json = "+str);
				JSONObject jObject = new JSONObject(str);
				String result = jObject.getString("result");
				if(result != null){
					
					switch (threadindex) {
					case 11:
						//获得服务器时间
						if(result.equalsIgnoreCase("true")){
							UserUtil.sysTime = jObject.getLong("time");
							UserUtil.sysDate = jObject.getString("date");
							OfflineTimeService.resetTimeDef();//时间差清零
							OfflineLog.writeUserId();//写入离线日志
						}else{
							UserUtil.sysTime = System.currentTimeMillis();
							UserUtil.sysDate = "";
						}
						CommonUtil.saveSysTime(mContext, UserUtil.sysTime);
						CommonUtil.saveSysDate(mContext, UserUtil.sysDate);
						
						cacheUrl = URLUtil.URL_MAINPAGE_CACHE_INFO;
						cacheThreadindex = URLUtil.THREAD_MAINPAGE_CACHE_INFO;
						isStartLoadPage = false;
						mConnectUtil = new ConnectUtil(mContext, downHandler,pageIndex);
						mConnectUtil.connect(addUrlParam(cacheUrl, pageIndex, UserUtil.userid), HttpThread.TYPE_PAGE, cacheThreadindex);
						break;
					default:
						if(result.equalsIgnoreCase("true")){
							if(UserUtil.isLogout){
								UserUtil.isLogout = false;
							}
							if(UserUtil.isNewLoginOrExit){
								UserUtil.isNewLoginOrExit = false;
							}
							int startpi = jObject.getInt("startpi");
							int limit = jObject.getInt("limit");
							boolean autoupdate = jObject.getBoolean("autoupdate");
							boolean showmsg = jObject.getBoolean("showmsg");
							
							try {
								long createtime = jObject.getLong("createtime");
								CommonUtil.saveCreateTime(mContext, createtime);
								CommonUtil.save54Cid(mContext, "");
								UserUtil.sysTime = jObject.getLong("time");
								UserUtil.sysDate = jObject.getString("date");
								LogPrint.Print("sysTime = "+UserUtil.sysTime);
								LogPrint.Print("sysDate = "+UserUtil.sysDate);
							} catch (Exception e) {
								// TODO: handle exception
							}
							LogPrint.Print("json:startpi = "+startpi);
							LogPrint.Print("json:limit = "+limit);
							CommonUtil.saveStartPi(mContext, startpi);
							CommonUtil.saveLimit(mContext, limit);
							UserUtil.callOnNum = jObject.getInt("callonnum");
							if(isNewDay(UserUtil.sysDate, CommonUtil.getSysDate(mContext))){
								CommonUtil.saveCallOnNum(mContext, -1);
								CommonUtil.saveSysDate(mContext, UserUtil.sysDate);
							}
							CommonUtil.saveCallOnNum(mContext, UserUtil.callOnNum);
							
							if(autoupdate){
								//清除缓存
								if(CommonUtil.isNetWorkOpen(mContext)){
									CommonUtil.deleteAll(new File(CommonUtil.dir_cache_page));
								}
								DownLoadPool.getInstance(mContext).clear();
								DownLoadPool.getInstance(mContext).reInit();
								
								//提示文本
								if(showmsg){
									CommonUtil.ShowToast(mContext, "每半小时翠鸟会自动摇一下，别忘了标记喜欢的商品哦！");
								}
								CommonUtil.saveSysTime(mContext, UserUtil.sysTime);
								pageIndex = startpi;
							}
							if(UserUtil.userid != -1&&UserUtil.userState == 1){//登录
								//初始化摇一摇倒计时
								TimerForRock.initLimitTime(CommonUtil.getLimitTime(CommonUtil.getCreateTime(mContext), CommonUtil.getSysTime(mContext)), mContext);
							}
							Message msg = new Message();
							msg.arg1 = startpi;
							msg.what = MessageID.MESSAGE_DOWNLOAD_CACHE;
							mHandler.sendMessageDelayed(msg, 10);
							
							if(TimerForRock.isNoTime()){
								Intent intent = new Intent();
								intent.setClass(mContext, RockActivityA.class);
								intent.putExtra("fromscreen", 0);
								mContext.startActivity(intent);
								((Activity)mContext).overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
							}
						}else{
							isSensor = false;
							LogPrint.Print("cache load false");
						}
						break;
					}
					
				}
			}
		}catch(Exception e){
			
		}
	}
	
	//解析创建页面
	private void buildPage(Message msg,int threadindex,int type){
		Message message = new Message();
		Bundle bundle = new Bundle();
		if(type == HttpThread.TYPE_PAGE){
			downcount = 0;
			parserEngine = new Parser_ParserEngine(mContext);
			parserEngine.parser((byte[])msg.obj);
			Parser_Layout_AbsLayout[] tmpAbsLayouts = parserEngine.getLayouts();
			for(int i = 0;tmpAbsLayouts!=null&&i < tmpAbsLayouts.length;i ++){
				if(tmpAbsLayouts[i]!=null&&tmpAbsLayouts[i].getModelType() == Parser_Layout_AbsLayout.MODELTYPE_MAINPAGE){
					parserMainPage = (Parser_MainPage)tmpAbsLayouts[i];
					if(parserMainPage.getValidNum() != 9){//不够9个，提示
						if(!UserUtil.isCallEditerOver){
							String strmsg = "格子未满，呼叫小编帮您填满";
							StylePageDialog2 dialog = new StylePageDialog2(mContext,
									R.style.dialogshock, strmsg) {
								
								@Override
								public void call() {
									// TODO Auto-generated method stub
									UserUtil.isCallEditerOver = true;
									new ConnectUtil(mContext, downHandler, false, 0).connect(URLUtil.URL_CALLEDITER+"?proNum="+parserMainPage.getValidNum(), HttpThread.TYPE_PAGE, 33);
									cancel();
								}
							};
							dialog.show();
						}
					}
					break;
				}
			}
			//写缓存
			if(!CommonUtil.exists(CommonUtil.dir_cache_page+"/"+CommonUtil.urlToNum(msg.getData().getString("mUrl")))){
				CommonUtil.writeToFile(CommonUtil.dir_cache_page+"/"+CommonUtil.urlToNum(msg.getData().getString("mUrl")), (byte[])msg.obj);
			}
			
			ArrayList<Parser_Image> tmpArrayList = parserMainPage.getImages();
			for(int i = 0;i < parserImages.length;i ++){
				parserImages[i] = tmpArrayList.get(i);
			}
			modelIndex = parserMainPage.getModelIndexs();
			parserImages = order(modelIndex[0],bigPos);
			
			for(int i = 0;i < imageViews.length;i ++){
				String url = parserImages[i].getSrc();
				if(parserImages[i].getSrc() == null||parserImages[i].getSrc().equalsIgnoreCase("null")||parserImages[i].getSrc().equalsIgnoreCase("")){
					url = null;
				}
				//压入下载队列
				if(parserImages[i].getSrc() != null){
					if(parserImages[i].getValidable()){
						DownImageItem tmpDownImageItem = new DownImageItem(Parser_Layout_AbsLayout.MODELTYPE_MAINPAGE, i, url, parserImages[i].getPageId(),pageIndex);
						downImageManagerA.add(tmpDownImageItem);
					}
				}
			}
			
			//加入下载池
			if(CommonUtil.isNetWorkOpen(mContext)){
				String apn = CommonUtil.getApnType(mContext);
				if(apn.toLowerCase().indexOf("wifi") >= 0){
					if(isStartLoadPage == false){
						isStartLoadPage = true;
						DownLoadPoolItem item;
						for(int i = 0;i < 6;i ++){
							if(!DownLoadPool.isClear){
								item = new DownLoadPoolItem(addUrlParam(URLUtil.URL_MAINPAGE,URLUtil.dpi(),i,UserUtil.userid), HttpThread.TYPE_PAGE, false);
								DownLoadPool.getInstance(mContext).add(item, -1);
							}
						}
					}
				}else if(apn.toLowerCase().indexOf("3g") >= 0){
					DownLoadPoolItem item;
					if(pageIndex+1 < 6&&!DownLoadPool.isClear){
						item = new DownLoadPoolItem(addUrlParam(URLUtil.URL_MAINPAGE,URLUtil.dpi(),pageIndex+1,UserUtil.userid), HttpThread.TYPE_PAGE, false);
						DownLoadPool.getInstance(mContext).add(item, -1);
					}
					if(pageIndex-1 > 0&&!DownLoadPool.isClear){
						item = new DownLoadPoolItem(addUrlParam(URLUtil.URL_MAINPAGE,URLUtil.dpi(),pageIndex-1,UserUtil.userid), HttpThread.TYPE_PAGE, false);
						DownLoadPool.getInstance(mContext).add(item, -1);
					}
					if(pageIndex+2 < 6&&!DownLoadPool.isClear){
						item = new DownLoadPoolItem(addUrlParam(URLUtil.URL_MAINPAGE,URLUtil.dpi(),pageIndex+2,UserUtil.userid), HttpThread.TYPE_PAGE, false);
						DownLoadPool.getInstance(mContext).add(item, -1);
					}
					if(pageIndex-2 > 0&&!DownLoadPool.isClear){
						item = new DownLoadPoolItem(addUrlParam(URLUtil.URL_MAINPAGE,URLUtil.dpi(),pageIndex-2,UserUtil.userid), HttpThread.TYPE_PAGE, false);
						DownLoadPool.getInstance(mContext).add(item, -1);
					}
				}else{
					DownLoadPoolItem item;
					if(pageIndex+1 < 6&&!DownLoadPool.isClear){
						item = new DownLoadPoolItem(addUrlParam(URLUtil.URL_MAINPAGE,URLUtil.dpi(),pageIndex+1,UserUtil.userid), HttpThread.TYPE_PAGE, false);
						DownLoadPool.getInstance(mContext).add(item, -1);
					}
					if(pageIndex-1 > 0&&!DownLoadPool.isClear){
						item = new DownLoadPoolItem(addUrlParam(URLUtil.URL_MAINPAGE,URLUtil.dpi(),pageIndex-1,UserUtil.userid), HttpThread.TYPE_PAGE, false);
						DownLoadPool.getInstance(mContext).add(item, -1);
					}
				}
			}
		}
		
		message.what = MessageID.MESSAGE_RENDER;
		message.obj = msg.obj;
		message.arg1 = msg.arg1;
		bundle.putInt("mType", type);
		bundle.putString("mUrl", msg.getData().getString("mUrl"));
		bundle.putString("mApn",msg.getData().getString("mApn"));
		message.setData(bundle);
		mHandler.sendMessage(message);
		
	}
	
	public int downcount;
	//渲染页面
	private void renderPage(Message msg,int threadindex,int type){
		switch (type) {
		case HttpThread.TYPE_PAGE:
			if(isSensor){
				isSensor = false;
				playRockEnd();
			}
			downcount = downImageManagerA.getUnDownLoadSize();
			if(downcount <= 0){
				closeProgress();
				closeTimer();
			}
			OfflineLog.writeMainPage();//写入离线日志
			mHandler.sendEmptyMessageDelayed(MessageID.MESSAGE_CONNECT_LAYOUTOVER, 100);
			break;
		case HttpThread.TYPE_IMAGE:
			try{
				initImageSize(getResources().getDisplayMetrics().densityDpi);
				setImageView((byte[])msg.obj, threadindex,msg.getData().getString("mUrl"),msg.getData().getString("mApn"));
				downcount --;
				if(downcount <= 0){
					((MainPageActivityA)mContext).autoScroll();
					closeProgress();
					closeTimer();
				}
			}catch (Exception e) {
			}
			break;
		}
		
	}
	
	//是否是新的一天了
	private boolean isNewDay(String last,String save){
		//2012-09-09 17:00:00:000
		try {
			int last_year;
			int last_month;
			int last_day;
			int save_year;
			int save_month;
			int save_day;
			if(save == null)return true;
			if(save.length() <= 0)return true;
			String lastString = last.substring(0, last.indexOf(" "));
			String saveString = save.substring(0, save.indexOf(" "));
			String[] array = lastString.split("-");
			last_year = Integer.parseInt(array[0]);
			if(array[1].length() == 2){
				if(array[1].charAt(0) == '0'){
					last_month = Integer.parseInt(array[1].substring(1));
				}else{
					last_month = Integer.parseInt(array[1]);
				}
			}else{
				last_month = Integer.parseInt(array[1]);
			}
			if(array[2].length() == 2){
				if(array[2].charAt(0) == '0'){
					last_day = Integer.parseInt(array[2].substring(1));
				}else{
					last_day = Integer.parseInt(array[2]);
				}
			}else{
				last_day = Integer.parseInt(array[2]);
			}
			
			array = saveString.split("-");
			save_year = Integer.parseInt(array[0]);
			if(array[1].length() == 2){
				if(array[1].charAt(0) == '0'){
					save_month = Integer.parseInt(array[1].substring(1));
				}else{
					save_month = Integer.parseInt(array[1]);
				}
			}else{
				save_month = Integer.parseInt(array[1]);
			}
			if(array[2].length() == 2){
				if(array[2].charAt(0) == '0'){
					save_day = Integer.parseInt(array[2].substring(1));
				}else{
					save_day = Integer.parseInt(array[2]);
				}
			}else{
				save_day = Integer.parseInt(array[2]);
			}
			//对比时间
			if(last_year > save_year){
				return true;
			}else{
				if(last_year < save_year){//错误时间
					return true;
				}else{
					if(last_month > save_month){
						return true;
					}else{
						if(last_month < save_month){//错误时间
							return true;
						}else{
							if(last_day > save_day){
								return true;
							}else{
								if(last_day < save_day){//错误时间
									return true;
								}else{
									return false;
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return true;
	}
	
	private MediaPlayer mp_end;
	private void playRockEnd(){
		try {
			if(mp_end ==null){
				mp_end = MediaPlayer.create(mContext, R.raw.rock_over);
			}
			if(!mp_end.isPlaying()){
				mp_end.start();
				mp_end.setOnCompletionListener(new OnCompletionListener() {
					
					@Override
					public void onCompletion(MediaPlayer mp) {
						// TODO Auto-generated method stub
						mp.release();
						mp_end = null;
						LogPrint.Print("webview","rock end completion");
					}
				});
				addVibrator(200);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	//震动
	private void addVibrator(long ms){
		try {
			Vibrator vibrator = (Vibrator)mContext.getSystemService(mContext.VIBRATOR_SERVICE);
			vibrator.vibrate(ms);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public String addUrlParam(String url,String dpi){
  		if(URLUtil.IsLocalUrl()){
			return url;
		}
  		return url+"?dpi="+dpi;
  	}
	
	//获取全部54个单品页商品id
  	private void getAllCommdityUrl(){
  		if(CommonUtil.isNetWorkOpen(mContext)){
  			MyPageActivityA.urls = null;
  			mConnectUtil = new ConnectUtil(mContext, downHandler,0);
  			mConnectUtil.connect(addUrlParam(URLUtil.URL_ALLCOMMDITYID,URLUtil.dpi()), HttpThread.TYPE_PAGE, 20);
  		}else{
  			mHandler.sendEmptyMessage(115511);
  		}
  	}
  	
  	public void bulidUrl(int index,int cid,int length){
  		if(MyPageActivityA.urls == null){
  			MyPageActivityA.urls = new String[length];
  		}else{
  			if(MyPageActivityA.urls.length != length){
  				MyPageActivityA.urls = new String[length];
  			}
  		}
  		UserUtil.cidMaxNum = length;
  		MyPageActivityA.urls[index] = URLUtil.URL_MYPAGE+"?cid="+cid+"&dpi="+URLUtil.dpi()+"&pi=0&plaid="+URLUtil.plaid;
  	}
  	
  	private void Json1(byte[] data,String contenttype){
  		try {
  			if("text/json".equals(contenttype)){
  				String str = new String(data,"UTF-8");
				str = CommonUtil.formUrlEncode(str);
				LogPrint.Print("jsonarray = "+str);
				if(str != null&&str.length() > 2){
					CommonUtil.save54Cid(mContext, str);
					JSONArray jsonArray = new JSONArray(str);
					int length = jsonArray.length();
					for(int i = 0;i < length;i ++){
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						int cid = jsonObject.getInt("cid");
						bulidUrl(i, cid, length);
					}
					MyPageActivityA.saveUrlsFromMain = MyPageActivityA.urls;
				}
  			}
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			mHandler.sendEmptyMessage(115511);
		}
  	}
  	private String getPixels(int width,int height){
		String pixels = "";
		if(width<=130){
			pixels = "130X130";
		}
		else if(width<=240){
			pixels = "240X320";
		}
		else if(width<=320){
			pixels = "320X480";
		}
		else if(width<=480){
			pixels = "480X800";
		}
		else if(width<=540){
			pixels = "540X960";
		}
		else{
			pixels = "640X960";
		}
		return pixels;
	}
  	
  	public void closeTimer(){
  		timeCount = 0;
		if(mTimer != null){
			mTimer.cancel();
			mTimer = null;
			mTimerTask = null;
		}
  	}
  	
  	private void Json2(byte[] data,String contenttype){
		try{
			if("text/json".equals(contenttype)){
				String str = new String(data,"UTF-8");
				str = CommonUtil.formUrlEncode(str);
				LogPrint.Print("json = "+str);
				JSONObject jObject = new JSONObject(str);
				String result = jObject.getString("result");
				if(result != null){
					if(result.equalsIgnoreCase("true")){
						mHandler.sendEmptyMessage(335522);
					}else{
						mHandler.sendEmptyMessage(335523);
					}
				}
			}
		}catch(Exception e){
			
		}
	}
}
