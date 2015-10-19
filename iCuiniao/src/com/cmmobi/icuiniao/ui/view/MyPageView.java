package com.cmmobi.icuiniao.ui.view;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.Activity.CommentAllListActivity;
import com.cmmobi.icuiniao.Activity.CommentPageActivity;
import com.cmmobi.icuiniao.Activity.LoginAndRegeditActivity;
import com.cmmobi.icuiniao.onlineEngine.activity.CommodityActivity;
import com.cmmobi.icuiniao.onlineEngine.activity.MyPageActivityA;
import com.cmmobi.icuiniao.onlineEngine.activity.UserPageActivityA;
import com.cmmobi.icuiniao.onlineEngine.activity.WebviewActivity;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_CommodityInfo;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_Image;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_Layout_AbsLayout;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_Page;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_ParserEngine;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_TitleBarFloat;
import com.cmmobi.icuiniao.ui.parser.Parser_MyPageList;
import com.cmmobi.icuiniao.ui.parser.Parser_MyPageListItem;
import com.cmmobi.icuiniao.ui.view.ScrollviewForRefresh.OnFootRefreshListener;
import com.cmmobi.icuiniao.ui.view.ScrollviewForRefresh.OnHeadRefreshListener;
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
import com.cmmobi.icuiniao.util.PageID;
import com.cmmobi.icuiniao.util.ReplayPermit;
import com.cmmobi.icuiniao.util.URLUtil;
import com.cmmobi.icuiniao.util.UserUtil;
import com.cmmobi.icuiniao.util.ViewID;
import com.cmmobi.icuiniao.util.connction.HttpThread;
import com.icuiniao.plug.mediaplayer.MediaDataBase;
import com.icuiniao.plug.mediaplayer.MediaLoader;
import com.icuiniao.plug.mediaplayer.MediaPlayerA;

public class MyPageView extends LinearLayout implements AnimationListener{

	private int titleHeight;
	
	private FrameLayout view_pageLayout;//外层
	private LinearLayout view_xmlLayout;//数据层
	private LinearLayout view_iconLayout;//图标层
	
	private FrameLayout view_likeLayout; //喜欢收藏图标层
	private LinearLayout view_commentLayout;//评论
	private LinearLayout view_commentMore;//更多评论
	private ProgressBar loadingBar;
	private ScrollviewForRefresh scrollView;
	private int pageIndex;
	private Context mContext;
	
	private ConnectUtil mConnectUtil;
	private Parser_ParserEngine parserEngine;
	private Parser_Image parserImage;
	private Parser_CommodityInfo parserCommodityInfo;
	private Parser_Page parserPage;
	private View_Image viewImage;
	private View_CommodityInfo viewCommodityInfo;
	private View_CommodityIcon viewCommodityIcon;
	private View_LikeNotifiIcon viewLikeNotifiIcon;
	private String wapUrl;
	private String title;
	private String commodityImageString;
	private String commodityinfoString;
	private int commodityid;
	private int merchantid;
//	private String preUrl;//上一个单品地址
//	private String nextUrl;//下一个单品地址
	public int curPageid;//在首页中的页码
	private String urlString;
	
	public boolean isFling;//是否滑动，用于写于滑动的离线日志
	
	private DownImageManagerA downImageManagerA;
	private Parser_MyPageList parserMyPageList;
	private View_NoScrollList viewNoScrollList;
	private String commentUrl;
	private byte[] comments;
	//是否可以播放视频
	private boolean isPlayer = false;
	private boolean isFinish = false;
	private TextView textwrongTip;  //网络异常后的提示
	private String imageUrl;
	private boolean isLikeEnable = true; //喜欢的条件
	private float countSize = 0;
	private float curSize = 0;
	private float temp = 0;
	private int progressValue = 0;
	private Timer mTimer=new Timer();
	private int timeout = 0;
	public OnClickListener imageClickListener;
	private String pixels;
	
	private boolean isImageTimerRun; //lyb-3.11
	
	//2G下查看单个商品大图的进度显示
	private TextView textView;
	private LinearLayout imagelayout;
	private String mApn;
	private static final String GESTUREMODE = "1";
	private static final String BUTTONMODE = "0";
	private static final String LEFTHAND = "1";
	private static final String RIGHTHAND = "0";
	private String mode = null;
	private String hand = null;
	//喜欢数量限制成功/失败
	private boolean likeNumLimit;
	public MyPageView(Context context,int pageindex) {
		super(context);
		// TODO Auto-generated constructor stub
		mContext = context;
		pageIndex = pageindex;
		titleHeight = 66;
		curPageid = -1;
		downImageManagerA = new DownImageManagerA();
		initLayout();
		newHandler();
		addView(scrollView);
	}
	
	private void initLayout(){
		int dpi = getResources().getDisplayMetrics().densityDpi;
		if(dpi <= 120){//qvga
			titleHeight=33;
		}else if(dpi <= 160){//hvga
			titleHeight=44;
		}else if(dpi <= 240){//wvga
			titleHeight = 66;
			if(CommonUtil.screen_width > 700){
				titleHeight=85;
			}
		}else{//更大屏幕分辨率
			titleHeight=85;
		}
		
		view_pageLayout = new FrameLayout(mContext);
		view_iconLayout = new LinearLayout(getContext());
		
		view_likeLayout = new FrameLayout(getContext());
		view_likeLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		view_xmlLayout = new LinearLayout(getContext());
		view_xmlLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		view_xmlLayout.setOrientation(LinearLayout.VERTICAL);
		
		LinearLayout view_titleback = new LinearLayout(getContext());
		view_titleback.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
		view_titleback.setBackgroundResource(R.drawable.titlebarfloatbg1);
		view_titleback.setId(ViewID.ID_PAGE_TITLE_BACK);
		//文字大小
		int textsize = CommonUtil.dip2px(mContext, 10);
		if(dpi <= 120){
			textsize = CommonUtil.dip2px(mContext, 14);
		}
		
		//下拉文字提示
		TextView tipText = new TextView(mContext);
		tipText.setTextColor(0xffffffff);		
		tipText.setId(ViewID.ID_PAGE_TITLE_TEXT);
		tipText.setTextSize(textsize);
		tipText.setText("刷新");
		tipText.setVisibility(View.INVISIBLE);
		LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 
				ViewGroup.LayoutParams.WRAP_CONTENT);		
		view_titleback.setGravity(Gravity.CENTER);
		view_titleback.addView(tipText, textParams);
		
		view_xmlLayout.addView(view_titleback);
		
		viewLikeNotifiIcon = new View_LikeNotifiIcon(mContext);
		viewImage = new View_Image(mContext);
		viewCommodityInfo = new View_CommodityInfo(mContext);
		viewCommodityIcon = new View_CommodityIcon(mContext);
		
		view_xmlLayout.addView(viewImage);
		view_xmlLayout.addView(viewCommodityInfo);
		
		view_commentLayout = new LinearLayout(mContext);
		view_commentLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
		view_commentLayout.setOrientation(LinearLayout.VERTICAL);
		view_xmlLayout.addView(view_commentLayout);
		
		view_commentMore = new LinearLayout(mContext);
		view_commentMore.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
		view_commentMore.setGravity(Gravity.CENTER);
		TextView textView = new TextView(mContext);
		textView.setTextColor(0xffacacac);
		
		if(dpi <= 120){//qvga 240x320
			view_commentMore.setPadding(0, 10, 0, 10);
			textView.setTextSize(CommonUtil.dip2px(mContext, 14));
			
		}else{
			view_commentMore.setPadding(0, 20, 0, 20);
			textView.setTextSize(CommonUtil.dip2px(mContext, 10));
		}
		textView.setText("更多评论");
		view_commentMore.addView(textView);
		view_commentMore.setVisibility(GONE);
		view_xmlLayout.addView(view_commentMore);
		view_commentMore.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mHandler.sendEmptyMessage(MessageID.MESSAGE_CLICK_LISTITEM_COMMENT_MORE);
			}
		});
		
		LinearLayout layout = new LinearLayout(getContext());
		layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,CommonUtil.dip2px(getContext(), 60)));
		view_xmlLayout.addView(layout);
		
		AbsoluteLayout absoluteLayout = new AbsoluteLayout(getContext());
		absoluteLayout.addView(viewCommodityIcon, new AbsoluteLayout.LayoutParams(viewCommodityIcon.getCommodityInfoIcon_W(), viewCommodityIcon.getCommodityInfoIcon_H(), CommonUtil.dip2px(getContext(),18), CommonUtil.dip2px(getContext(), CommonUtil.px2dip(getContext(), titleHeight))+CommonUtil.screen_width-viewCommodityIcon.getCommodityInfoIcon_H()/2));
		view_iconLayout.addView(absoluteLayout);
		
		
		AbsoluteLayout absoluteLikeLayout = new AbsoluteLayout(getContext());
		absoluteLikeLayout.addView(viewLikeNotifiIcon, new AbsoluteLayout.LayoutParams(android.widget.AbsoluteLayout.LayoutParams.WRAP_CONTENT,android.widget.AbsoluteLayout.LayoutParams.WRAP_CONTENT, CommonUtil.dip2px(getContext(),145), CommonUtil.dip2px(getContext(), CommonUtil.px2dip(getContext(), titleHeight))+CommonUtil.screen_width-165/2));
		view_likeLayout.addView(absoluteLikeLayout);
		
		
		LinearLayout lprogress = new LinearLayout(getContext());
		lprogress.setLayoutParams(new LayoutParams(CommonUtil.screen_width,CommonUtil.screen_height));
		lprogress.setGravity(Gravity.CENTER);
		
		loadingBar = new ProgressBar(mContext);
		loadingBar.setIndeterminateDrawable(getResources().getDrawable(R.drawable.loading));
		lprogress.addView(loadingBar);
		closeProgress();
		
		//lyb 网络异常后的提示
		textwrongTip = new TextView(getContext());
		textwrongTip.setLayoutParams(new LayoutParams(CommonUtil.screen_width,CommonUtil.screen_height));
		textwrongTip.setGravity(Gravity.CENTER);
		textwrongTip.setText("数据更新失败，下拉刷新数据");
		textwrongTip.setTextSize(textsize);
		textwrongTip.setVisibility(View.INVISIBLE);
		
		view_pageLayout.addView(view_xmlLayout);
		view_pageLayout.addView(view_iconLayout);
		//view_pageLayout.addView(view_likeLayout);
		view_pageLayout.addView(lprogress);
		
		view_pageLayout.addView(textwrongTip);		
		
		scrollView = new ScrollviewForRefresh(mContext);
		scrollView.setVerticalScrollBarEnabled(false);
		scrollView.setHorizontalFadingEdgeEnabled(false);
		scrollView.setVerticalFadingEdgeEnabled(false);
//		scrollView.addView(view_pageLayout);
		
		//增加footview lyb
		LinearLayout linear = new LinearLayout(getContext());
		linear.setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.
        		LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT);
		LayoutInflater inflater = LayoutInflater.from(getContext());
		LinearLayout footView = (LinearLayout) inflater.inflate(R.layout.foot_page,
				null);		
		linear.addView(view_pageLayout,lp);	
		footView.setGravity(Gravity.BOTTOM);
		linear.addView(footView);		
		scrollView.addView(linear);
		scrollView.setHeadView();
		scrollView.setFootView(footView);		

		//测量并获取footview的高度
		scrollView.measureView(footView);
		int footContentHeight = footView.getMeasuredHeight();
		footView.setPadding(0, 0, 0, -1 * footContentHeight);

	}
	
	/**
	 * 初始化图片超时下载定时器
	 */
	private void initImageTimer(){
		if(mTimer == null){
			mTimer = new Timer();
		}
		if(mTimerTask == null){
			mTimerTask = new TimerTask() {

				@Override
				public void run() {
					mHandler.sendEmptyMessage(MessageID.MESSAGE_TIMEOUT);
				}
			};
		}		
	}
	
	public void initConnect(){
		LogPrint.Print("webview","initConnect pageindex = "+pageIndex);
//		LogPrint.Print("webview","url = "+MyPageActivityA.urls[pageIndex]);
		if(UserUtil.isRemoteLogin){			
			Intent intent = new Intent();
			intent.setClass(mContext,
					LoginAndRegeditActivity.class);
			mContext.startActivity(intent);
			return;
		}
		initImageTimer(); //lyb-3.11
		if(MyPageActivityA.urls[pageIndex] != null){
			urlString = MyPageActivityA.urls[pageIndex];
			mConnectUtil = new ConnectUtil(mContext, downHandler,pageIndex);
			mConnectUtil.connect(urlString, HttpThread.TYPE_PAGE,0);
			//滑动对刷新状态的影响，进行恢复；
			scrollView.resetHead();
		}
	}
	
	/**
	 * 不使用缓存数据的页面连接
	 */
	public void initconnectWithNoCache(){
		if(MyPageActivityA.urls[pageIndex] != null){
			urlString = MyPageActivityA.urls[pageIndex];
			mConnectUtil = new ConnectUtil(mContext, downHandler, 1, pageIndex);			
			mConnectUtil.connect(urlString, HttpThread.TYPE_PAGE,0);
			//滑动对刷新状态的影响，进行恢复；
			scrollView.resetHead();
		}
	}
	
	private TimerTask mTimerTask = new TimerTask() {

		@Override
		public void run() {
			mHandler.sendEmptyMessage(MessageID.MESSAGE_TIMEOUT);
		}
	};
	private final int arg_MAY_REPLAY = 234;
	private MHandlerThread mHandlerThread;
	public Handler downHandler;
	//用于下载接收的异步handler
	private void newHandler(){
		mHandlerThread = new MHandlerThread("MyPageActivityA");
		mHandlerThread.start();
		downHandler = new Handler(mHandlerThread.getLooper(), mHandlerThread){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case MessageID.MESSAGE_DOWNLOAD_IMAGE_ERROR:
					if(!isImageTimerRun){
						isImageTimerRun = true;
						initImageTimer();
						mTimer.schedule(mTimerTask, 0, 1000);
					}
					CommonUtil.ShowToast(mContext, "杯具了!联网不给力啊");
					mHandler.sendEmptyMessage(MessageID.MESSAGE_SET_LISTENER);
					break;
				case MessageID.MESSAGE_IMAGE_LOAD_ERROR:
					mHandler.sendEmptyMessage(MessageID.MESSAGE_SHOW_WRONG_TIPS);
					break;
				case MessageID.MESSAGE_DOWNLOAD_IMAGE_SIZE_VALUE:
					Bundle bundleSize = msg.getData();
					countSize = bundleSize.getInt("mSize");
					break;
				case MessageID.MESSAGE_DOWNLOAD_IMAGE_CURSIZE_VALUE:
					Bundle bundleCurSize = msg.getData();
					curSize = bundleCurSize.getInt("mCurrentPos");
//					if(curSize == temp){
//						//开始定时计算超时
//						mTimer.schedule(mTimerTask, 0, 1000);
//						temp = 0;
//					}
//					else{
//						temp = curSize;
//					}
					if(countSize > 0){
						progressValue = (int)((curSize/countSize)*100);
						mHandler.sendEmptyMessage(MessageID.MESSAGE_UPDATE_IMAGE_VALUE);
					}
					break;
				case MessageID.MESSAGE_CONNECT_START:
					mHandler.sendEmptyMessage(MessageID.MESSAGE_CONNECT_START);
					break;
				case MessageID.MESSAGE_CONNECT_ERROR:
					ReplayPermit.isMayClick = true;
					CommonUtil.ShowToast(mContext, (String)msg.obj);
					LogPrint.Print("lyb", "msg.arg1 =" + msg.arg1);
					if(msg.arg1 >= 1&& msg.arg1 <= 3){
						mHandler.sendEmptyMessage(113375);					
					}else{
//						mHandler.sendEmptyMessage(MessageID.MESSAGE_CONNECT_ERROR);
						int type = msg.getData().getInt("type");
						Bundle data = new Bundle();
						data.putInt("type", type);
						msg = new Message();
						msg.setData(data);
						msg.what = MessageID.MESSAGE_CONNECT_ERROR;
						mHandler.sendMessage(msg);
					}
					break;
				case MessageID.MESSAGE_CONNECT_DOWNLOADOVER:
					if("text/json".equals(msg.getData().getString("content_type"))){
						if(msg.arg1 == arg_MAY_REPLAY){							
							ReplayPermit.replayComment(mContext, msg);
							mHandler.sendEmptyMessage(MessageID.MESSAGE_CLOSEPROGRESS);							
							return;
						}
						if(msg.arg1 == 10){
							commentUrl = msg.getData().getString("mUrl");
							comments = (byte[])msg.obj;
						}
						Json((byte[])msg.obj,msg.arg1);
					}else{
						buildPage(msg,msg.arg1,msg.getData().getInt("mType"));						
					}
					break;
				}
			}
			
		};
	}
	
	public void flush(){
//		scrollView.onHeadRefreshComplete();
		downImageManagerA = new DownImageManagerA();		
		scrollView.removeAllViews();
		initLayout();				
//		initConnect();	
		initconnectWithNoCache();
		scrollView.onHeadRefreshComplete();
		addView(scrollView);
	}
	
	@Override
	public void onAnimationEnd(Animation animation) {
		isLikeEnable = true;
		
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationStart(Animation animation) {
		isLikeEnable = false;
		
	}
	
	public Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case MessageID.MESSAGE_SET_LISTENER:
				imagelayout.setOnClickListener(viewImage.imageMagnifyClickListener);
				break;
			case MessageID.MESSAGE_TIMEOUT:
				timeout += 1;
				if(timeout == 20){
					textView.setText("图片加载失败，点击重新加载");					
					timeout = 0;
					mTimer.cancel();
					isImageTimerRun = false;
	    			mTimerTask = null;
	    			mTimer = null;
	    			isFinish = false;
	    			imagelayout.setOnClickListener(viewImage.imageMagnifyClickListener);
				}
				break;
			case MessageID.MESSAGE_UPDATE_IMAGE_VALUE:
				textView.setText(progressValue + "%");
				break;
			case MessageID.MESSAGE_CONNECT_START:
				scrollView.setHeadRefreshable(false);
				addProgress();				
				break;
			case MessageID.MESSAGE_CONNECT_ERROR:				
				closeProgress();
				final int type = msg.getData().getInt("type");
				if(type == HttpThread.TYPE_PAGE){
					scrollView.setonHeadRefreshListener(new OnHeadRefreshListener() {
						@Override
						public void onRefresh() {
							mHandler.sendEmptyMessage(MessageID.MESSAGE_FLUSH_PAGE);			
						}			
					});
					textwrongTip.setVisibility(View.VISIBLE);					
					imagelayout.setOnClickListener(null);
				}
				break;
			case MessageID.MESSAGE_CLOSEPROGRESS:
				closeProgress();
				break;
			case MessageID.MESSAGE_RENDER:
				renderPage(msg,msg.arg1,msg.getData().getInt("mType"));
				break;
			case MessageID.MESSAGE_CONNECT_LAYOUTOVER:
				closeProgress();
				//取出一个可用的图片下载
				DownImageItem downImageItem = downImageManagerA.get();
				if(downImageItem != null){
					LogPrint.Print("image downloading");
					String urlString = downImageItem.getUrl();
					LogPrint.Print("urlString =  "+urlString);
					if(urlString != null){
						if(downImageItem.getType() == Parser_Layout_AbsLayout.MODELTYPE_LISTITEM){
							new ConnectUtil(mContext, downHandler,1,pageIndex).connect(urlString, HttpThread.TYPE_IMAGE, downImageItem.getIndex());
						}else{
							new ConnectUtil(mContext, downHandler,pageIndex).connect(urlString, HttpThread.TYPE_IMAGE, downImageItem.getIndex());
						}
					}
					//发起下一个询问
					mHandler.sendEmptyMessageDelayed(MessageID.MESSAGE_CONNECT_LAYOUTOVER, 100);
				}else{
					DownLoadPool.getInstance(mContext).Notify();
					LogPrint.Print("downImageItem = null");
				}
				break;
			case MessageID.MESSAGE_CLICK_MYPAGEPLAY:
				//检查sd卡空间，小于30m提示用户空间不足
				if(CommonUtil.getAviableSpaceOfSdcard() < 30){
					viewImage.reInitPlayIcon();
					CommonUtil.ShowToast(mContext, "您的存储空间已不多，请先清理一下吧");
					return;
				}
				LogPrint.Print("lyb", "mediaplayer");
				String tmpurl = msg.getData().getString("url");
				
				if(tmpurl != null&&tmpurl.length() > 0){
					LogPrint.Print("webview","media url = "+tmpurl);
					Intent intent1 = new Intent();
					intent1.setClass(mContext, MediaPlayerA.class);
					intent1.putExtra("url", tmpurl);
					intent1.putExtra("title", title);
					intent1.putExtra("commodityInfoString", commodityinfoString);
					intent1.putExtra("image", commodityImageString);
					intent1.putExtra("commodityid", commodityid);
					intent1.putExtra("wapurl", wapUrl);
					if(viewCommodityInfo != null){
					intent1.putExtra("likestate", viewCommodityInfo.getLikeState());
					intent1.putExtra("deleteid", viewCommodityInfo.getDeleteId());
					intent1.putExtra("likenum", viewCommodityInfo.getLikeNum());
					}
			        String networkType = CommonUtil.getApnType(mContext);
			        //wifi
			        if(networkType.toLowerCase().indexOf("wifi")!=-1){
			        	isPlayer = true;
			        	if(!CommonUtil.isNetWorkOpen(mContext)){
			        		boolean isFileExists = false;
			        		File file = new File(CommonUtil.dir_media+"/"+CommonUtil.urlToNum(MediaLoader.getUrl(tmpurl))+".mp4");
				        	if(file.exists()){
				        		MediaDataBase dataBase = new MediaDataBase(mContext, MediaDataBase.DB_NAME, null, MediaDataBase.DB_VERSION);
				        		if(dataBase.isDownLoadOver(CommonUtil.urlToNum(MediaLoader.getUrl(tmpurl)))){
				        			isPlayer = true;
				        			isFileExists = true;
				        		}
				        	}
				        	if(!isFileExists){
				        		isPlayer = false;
				        		CommonUtil.ShowToast(mContext,"杯具了- -!\n联网不给力啊");
				        	}
				        	viewImage.reInitPlayIcon();
			        	}
			        }
			        //其他网络
			        else{
			        	//检查本地是否有下载完成的视频文件
			        	boolean isFileExists = false;
			        	File file = new File(CommonUtil.dir_media+"/"+CommonUtil.urlToNum(MediaLoader.getUrl(tmpurl))+".mp4");
			        	if(file.exists()){
			        		MediaDataBase dataBase = new MediaDataBase(mContext, MediaDataBase.DB_NAME, null, MediaDataBase.DB_VERSION);
			        		if(dataBase.isDownLoadOver(CommonUtil.urlToNum(MediaLoader.getUrl(tmpurl)))){
			        			isPlayer = true;
			        			isFileExists = true;
			        		}
			        	}
			        	if(!isFileExists){
			        		if(CommonUtil.isNetWorkOpen(mContext)){
			        			//未登录
					        	if(UserUtil.userid == -1||UserUtil.userState != 1){
					        		new AlertDialog.Builder(mContext)
						        	.setMessage("未接入无线wifi，\n为节省流量请在wifi环境下观看视频!")
						        	.setNegativeButton("取消", new DialogInterface.OnClickListener() {		
										@Override
										public void onClick(DialogInterface dialog, int which) {

										}
									})
						        	.show();
					        	}
					        	else{
					        		new AlertDialog.Builder(mContext)
						        	.setMessage("未接入无线wifi，\n为节省流量请在wifi环境下观看视频!")
						        	.setPositiveButton("放入我的喜欢", new DialogInterface.OnClickListener(){

										@Override
										public void onClick(DialogInterface dialog,int which) {
								        	if(!viewCommodityInfo.getLikeState()){
												mConnectUtil = new ConnectUtil(mContext, downHandler,false,1);
												mConnectUtil.connect(URLUtil.URL_ADDLIKE+"?oid="+UserUtil.userid+"&cid="+commodityid+"&plaid="+URLUtil.plaid, HttpThread.TYPE_PAGE, 5);
								        	}
											isPlayer = false;
										}
						        	})
						        	.setNegativeButton("取消", new DialogInterface.OnClickListener() {		
										@Override
										public void onClick(DialogInterface dialog, int which) {

										}
									})
						        	.show();
					        	}
			        		}else{
			        			isPlayer = false;
			        			CommonUtil.ShowToast(mContext,"杯具了- -!\n联网不给力啊");
			        		}
			        		
				        	viewImage.reInitPlayIcon();
			        	}
			        }
			        if(isPlayer){
//						mContext.startActivity(intent1);
			        	((Activity)mContext).startActivityForResult(intent1, MessageID.REQUESTCODE_LIKE_FLUSH);
						OfflineLog.writeMediaPlayer(commodityid);//写入离线日志			        	
			        }
				}
				break;
			case MessageID.MESSAGE_CLICK_COMMODITYINFO_LIKEBUTTON:
				if(UserUtil.userid != -1&&UserUtil.userState == 1){
//					addProgress();//2012.11.20张孟申请将loading去掉
					//lyb 增加心跳动画
					if(isLikeEnable){
						Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.zoom_add_like);
						animation.setAnimationListener(MyPageView.this);
						viewCommodityInfo.getLikebutton().startAnimation(animation);
						if(viewCommodityInfo.getLikeState()){//删除
							LogPrint.Print("delete");
							mConnectUtil = new ConnectUtil(mContext, downHandler,false,1);
							mConnectUtil.connect(URLUtil.URL_MYLIKE_DELETE_SINGLE+"?id="+viewCommodityInfo.getDeleteId()+"&oid="+UserUtil.userid+"&cid="+commodityid, HttpThread.TYPE_PAGE, 4);
						}else{//添加
							LogPrint.Print("add");
							mConnectUtil = new ConnectUtil(mContext, downHandler,false,1);
							mConnectUtil.connect(URLUtil.URL_ADDLIKE+"?oid="+UserUtil.userid+"&cid="+commodityid+"&plaid="+URLUtil.plaid, HttpThread.TYPE_PAGE, 5);
						}
					}
				}else{					
					CommonUtil.ShowToast(mContext, "请登陆或者注册，变身为小C的主人！");
					Intent intent11 = new Intent();
					intent11.setClass(mContext, LoginAndRegeditActivity.class);
					mContext.startActivity(intent11);
				}
				break;
			case MessageID.MESSAGE_CLICK_COMMODITYINFO_ICON:
				Intent intent = new Intent();
				intent.setClass(mContext, CommodityActivity.class);
				intent.putExtra("url", msg.getData().getString("url"));
				mContext.startActivity(intent);
				break;
			case MessageID.MESSAGE_CLICK_COMMODITYINFO_BOX:
				if(UserUtil.userid == -1||UserUtil.userState != 1){//未登陆
					CommonUtil.ShowToast(getContext(), "请登陆或者注册，变身为小C的主人！");
					Intent intent11 = new Intent();
					intent11.setClass(getContext(), LoginAndRegeditActivity.class);
					getContext().startActivity(intent11);
				}else{
					Intent intent2 = new Intent();
					intent2.setClass(mContext, CommentPageActivity.class);
					intent2.putExtra("issubject", -1);
					intent2.putExtra("subjectid", -1);
					intent2.putExtra("commentid", -1);
					((Activity)mContext).startActivityForResult(intent2, 9100);
				}
				break;
			case MessageID.MESSAGE_CLICK_LISTITEM_COMMENT:
				if(UserUtil.userid == -1||UserUtil.userState != 1){//未登陆
					CommonUtil.ShowToast(getContext(), "请登陆或者注册，变身为小C的主人！");
					Intent intent11 = new Intent();
					intent11.setClass(getContext(), LoginAndRegeditActivity.class);
					getContext().startActivity(intent11);
					ReplayPermit.isMayClick = true;  //add by lyb
				}else{
					mConnectUtil = new ConnectUtil(
							mContext, downHandler, 1,  0);			
					int is_subject = msg.getData().getInt("is_subject");
					int subjectid = msg.getData().getInt("subjectid");
					int commentid = msg.getData().getInt("commentid");
					int cid = msg.getData().getInt("cid");
					int touserid = msg.getData().getInt("touserid");
					String tousername = msg.getData().getString("toname");
					//is_subject + "@" +  subjectid + "@" + commentid + cid + "@" + touserid  + "@" + tousername;
					String split = ReplayPermit.split;
					String parmValue = is_subject + split +  subjectid + split + commentid  + split + cid + split + touserid;
					mConnectUtil.connect(URLUtil.Url_IS_PRIVATE_MSG + "?oid=" + UserUtil.userid  + "&tousername=" + tousername +  "&parmValue=" + parmValue + "&uid=" + touserid , 
							HttpThread.TYPE_PAGE, arg_MAY_REPLAY);
				}
				break;
			case MessageID.MESSAGE_CLICK_LISTITEM_ICON:
				Intent intent4 = new Intent();
				intent4.putExtra("url",msg.getData().getString("url"));
				intent4.putExtra("nickname",msg.getData().getString("nickname"));
				intent4.putExtra("userid",msg.getData().getInt("userid"));
				intent4.setClass(mContext, UserPageActivityA.class);
				mContext.startActivity(intent4);
				break;
			case MessageID.MESSAGE_CLICK_LISTITEM_COMMENT_MORE://更多评论
				Intent intent5 = new Intent(mContext, CommentAllListActivity.class);
				intent5.putExtra("commodityid", commodityid);
				mContext.startActivity(intent5);
				break;
			case MessageID.MESSAGE_FLUSH_PAGE://刷新页面	
				flush();
				break;
			case 100004:
				viewCommodityInfo.setLikeButtonBgImage(false);
				viewCommodityInfo.setLikeNum(msg.getData().getString("likenum"));
				CommonUtil.deleteCacheFile(urlString, mContext);
				setPopMenuLikeParam(parserCommodityInfo);  //喜欢后设置
				setLikeNum();
				break;
			case 100005:
				
				likeNumLimit = msg.getData().getBoolean("likeNumLimit");
				//喜欢数量是否上限
				if(likeNumLimit == false){
					//dialog提示
					new LikeLimitDialog(getContext());
					
				}else{
					
					viewCommodityInfo.setLikeButtonBgImage(true);
					viewCommodityInfo.setLikeNum(msg.getData().getString("likenum"));
					setPopMenuLikeParam(parserCommodityInfo);//喜欢后设置
					setLikeNum();
				}
				
				CommonUtil.deleteCacheFile(urlString, mContext);
				break;
			case 100006:
				closeProgress();
				break;
			case 113367:
				viewImage.showPlayButton();
				break;
			case 113368:
				viewCommodityIcon.setClick(parserCommodityInfo.getImage());
				break;
			case 113369:
				viewCommodityInfo.setClick();
				viewCommodityInfo.setLikeButtonBgImage(viewCommodityInfo.getLikeState());
				viewCommodityInfo.setLikeNum(parserCommodityInfo.getLikeButton().getStr());
				viewCommodityInfo.setCommodityInfoBoxText();
				viewCommodityInfo.setCommodityInfoTime();
				//喜欢个数
				setLikeNum();
				break;
			case 113370://手势对钩
				if(wapUrl != null){
					Intent intent1 = new Intent();
					intent1.setClass(mContext, WebviewActivity.class);
					intent1.putExtra("url",wapUrl);
					intent1.putExtra("title", title);
					intent1.putExtra("commodityid", commodityid);
					intent1.putExtra("commodityImageString", commodityImageString);
					intent1.putExtra("commodityInfoString", commodityinfoString);
					//add by lyb for taobao's like
					if(viewCommodityInfo != null){
					intent1.putExtra("likestate", viewCommodityInfo.getLikeState());
					intent1.putExtra("deleteid", viewCommodityInfo.getDeleteId());
					intent1.putExtra("likenum", viewCommodityInfo.getLikeNum());
					}
					((Activity)mContext).startActivityForResult(intent1, MessageID.REQUESTCODE_LIKE_FLUSH);
				}
				else{
					CommonUtil.ShowToast(getContext(), "抱歉，不能去看看！");
				}
				break;
			case 113371://手势圆圈
				viewCommodityInfo.onCommodityInfoLikeButtonClick(viewCommodityInfo.getLikeBtnUrl(),viewCommodityInfo.getLikeBtnAction());
				break;
			case 113372://手势三角
				mode = CommonUtil.getOperateMode(getContext());
				hand = CommonUtil.getOperateHand(getContext());
				if(hand.equals(LEFTHAND)){
					if(mode.equals(GESTUREMODE)){
						((MyPageActivityA)mContext).leftBottomPopupMenuA.share();
					}
				}
				else if(hand.equals(RIGHTHAND)){
					if(mode.equals(GESTUREMODE)){
						((MyPageActivityA)mContext).rightBottomMenuA.share();
					}
				}
				
				break;
			case 113373://获取评论列表
				clearCommentCache();
				connentCommentList();
				showCommentState("评论成功");
				break;
			case 113374://更新评论列表
				LogPrint.Print("comment fresh");
				String jsonarray = msg.getData().getString("jsonarray");
				if(jsonarray!=null&&jsonarray.length() > 2){
					try {
						//写缓存
						if(commentUrl != null&&comments != null){
							if(!CommonUtil.exists(CommonUtil.dir_cache_page+"/"+CommonUtil.urlToNum(commentUrl))){
								CommonUtil.writeToFile(CommonUtil.dir_cache_page+"/"+CommonUtil.urlToNum(commentUrl), comments);
							}
						}
						JSONArray jsonArray = new JSONArray(jsonarray);
						if(jsonArray.length() >= 10){
							if(view_commentMore != null){
								view_commentMore.setVisibility(VISIBLE);
							}
						//有更多时，不提供评论刷新 lyb
						}else{
							setScrollFootListener();
						}
						parserMyPageList = new Parser_MyPageList(jsonArray);
						if(viewNoScrollList != null){
							viewNoScrollList.clear();
							viewNoScrollList = null;
						}
						viewNoScrollList = new View_NoScrollList(mContext, parserMyPageList, pageIndex);
						view_commentLayout.removeAllViews();
						view_commentLayout.addView(viewNoScrollList);
						if(viewCommodityInfo != null){
							viewCommodityInfo.changeBackGround();
						}
						downImageManagerA.reset();
						mHandler.sendEmptyMessage(MessageID.MESSAGE_CONNECT_LAYOUTOVER);						
					} catch (Exception e) {
						// TODO: handle exception
					}
				}else{
					commentUrl = null;
					comments = null;
					//没有评论也要提供评论刷新
					setScrollFootListener();
				}					
				scrollView.onFootRefreshComplete();				
				break;
			case 113375:
				showCommentState("评论失败，请检查网络");
				break;
			case MessageID.MESSAGE_SHOW_WRONG_TIPS:
				closeProgress();
				scrollView.setonHeadRefreshListener(new OnHeadRefreshListener() {
					@Override
					public void onRefresh() {
						mHandler.sendEmptyMessage(MessageID.MESSAGE_FLUSH_PAGE);			
					}			
				});
				textwrongTip.setVisibility(View.VISIBLE);
				imagelayout.setOnClickListener(null);
//				imagelayout.setOnClickListener(viewImage.imageMagnifyClickListener);
				break;
			case 222222:
				((MyPageActivityA)mContext).isVideoDis(1);
				break;
			}
		}
		
	};
	
	/**
	 * 设置foot评论刷新的监听，并使其有效
	 */
	private void setScrollFootListener(){
		scrollView.setonFootRefreshListener(new OnFootRefreshListener(){

			@Override
			public void onRefresh() {
				//刷新评论
				clearCommentCache();
				connentCommentList();
			}
			
		});
	}
	//设置喜欢的参数
	private void setPopMenuLikeParam(Parser_CommodityInfo parserCommodityInfo){
		if(viewCommodityInfo == null){
			return;
		}
		boolean likeState = viewCommodityInfo.getLikeState();
		int deleteId = viewCommodityInfo.getDeleteId();		
		((MyPageActivityA)mContext).rightPopupMenuA.setLikeState(likeState);
		((MyPageActivityA)mContext).rightPopupMenuA.setDeleteId(deleteId);		
		
		((MyPageActivityA)mContext).rightBottomMenuA.setLikeState(likeState);
		((MyPageActivityA)mContext).rightBottomMenuA.setDeleteId(deleteId);		
		
		((MyPageActivityA)mContext).leftPopupMenuA.setLikeState(likeState);
		((MyPageActivityA)mContext).leftPopupMenuA.setDeleteId(deleteId);		
		
		((MyPageActivityA)mContext).leftBottomPopupMenuA.setLikeState(likeState);
		((MyPageActivityA)mContext).leftBottomPopupMenuA.setDeleteId(deleteId);		
	}
	//设置喜欢的个数
	private void setLikeNum(){
		String likenum = viewCommodityInfo.getLikeNum();
		((MyPageActivityA)mContext).rightPopupMenuA.setLikenum(likenum);
		((MyPageActivityA)mContext).rightBottomMenuA.setLikenum(likenum);
		((MyPageActivityA)mContext).leftPopupMenuA.setLikenum(likenum);
		((MyPageActivityA)mContext).leftBottomPopupMenuA.setLikenum(likenum);
	}
	
	private void buildPage(Message msg,int threadindex,int type){
		Message message = new Message();
		Bundle bundle = new Bundle();
		if(type == HttpThread.TYPE_PAGE){
			parserEngine = new Parser_ParserEngine(mContext);
			parserEngine.parser((byte[])msg.obj);
			parserPage = parserEngine.getPageObject();
			wapUrl = parserPage.getWapUrl();
			commodityid = parserPage.getCommodityId();
			merchantid = parserPage.getMerchantId();
//			preUrl = parserPage.getPreUrl();
//			nextUrl = parserPage.getNextUrl();
			curPageid = parserPage.getCurPageId();
//			addUrl();
//			((MyPageActivityA)mContext).mPopupMenu.setActivity((Activity)mContext);
//			((MyPageActivityA)mContext).initMenu();
//			((MyPageActivityA)mContext).mPopupMenu.setWapUrl(wapUrl);
//			((MyPageActivityA)mContext).mPopupMenu.setCommodityId(commodityid);
			
			((MyPageActivityA)mContext).rightPopupMenuA.setActivity((Activity)mContext);
			((MyPageActivityA)mContext).rightBottomMenuA.setActivity((Activity)mContext);
			((MyPageActivityA)mContext).leftPopupMenuA.setActivity((Activity)mContext);
			((MyPageActivityA)mContext).leftBottomPopupMenuA.setActivity((Activity)mContext);
			
//			((MyPageActivityA)mContext).rightPopupMenuA.setWapUrl("");
//			((MyPageActivityA)mContext).rightBottomMenuA.setWapUrl("");
//			((MyPageActivityA)mContext).leftPopupMenuA.setWapUrl("");
//			((MyPageActivityA)mContext).leftBottomPopupMenuA.setWapUrl("");
			((MyPageActivityA)mContext).rightPopupMenuA.setWapUrl(wapUrl);
			((MyPageActivityA)mContext).rightBottomMenuA.setWapUrl(wapUrl);
			((MyPageActivityA)mContext).leftPopupMenuA.setWapUrl(wapUrl);
			((MyPageActivityA)mContext).leftBottomPopupMenuA.setWapUrl(wapUrl);
			((MyPageActivityA)mContext).rightPopupMenuA.setCommodityId(commodityid);
			((MyPageActivityA)mContext).rightBottomMenuA.setCommodityId(commodityid);
			((MyPageActivityA)mContext).leftPopupMenuA.setCommodityId(commodityid);
			((MyPageActivityA)mContext).leftBottomPopupMenuA.setCommodityId(commodityid);
			((MyPageActivityA)mContext).initRight();
			((MyPageActivityA)mContext).initRightBottom();
			((MyPageActivityA)mContext).initLeft();
			((MyPageActivityA)mContext).initLeftBottom();
			Parser_Layout_AbsLayout[] tmpAbsLayouts = parserEngine.getLayouts();
			for(int i = 0;tmpAbsLayouts!=null&&i < tmpAbsLayouts.length;i ++){
				if(tmpAbsLayouts[i].getModelType() == Parser_Layout_AbsLayout.MODELTYPE_TITLEBARFLOAT){
					Parser_TitleBarFloat parserTitleBarFloat = (Parser_TitleBarFloat)tmpAbsLayouts[i];
					title = parserTitleBarFloat.getStr();
//					((MyPageActivityA)mContext).mPopupMenu.setTitle(title);
					((MyPageActivityA)mContext).rightPopupMenuA.setTitle(title);
					((MyPageActivityA)mContext).rightBottomMenuA.setTitle(title);
					((MyPageActivityA)mContext).leftPopupMenuA.setTitle(title);
					((MyPageActivityA)mContext).leftBottomPopupMenuA.setTitle(title);
				}else if(tmpAbsLayouts[i].getModelType() == Parser_Layout_AbsLayout.MODELTYPE_IMAGE){
					parserImage = (Parser_Image)tmpAbsLayouts[i];
					commodityImageString = parserImage.getSrc();
//					((MyPageActivityA)mContext).mPopupMenu.setCommodityImageString(commodityImageString);
					((MyPageActivityA)mContext).rightPopupMenuA.setCommodityImageString(commodityImageString);
					((MyPageActivityA)mContext).rightBottomMenuA.setCommodityImageString(commodityImageString);
					((MyPageActivityA)mContext).leftPopupMenuA.setCommodityImageString(commodityImageString);
					((MyPageActivityA)mContext).leftBottomPopupMenuA.setCommodityImageString(commodityImageString);
					if(parserImage.getPlayable()){						
						
						mHandler.sendEmptyMessage(222222);
					}
					//((MyPageActivityA)mContext).isVideoDis(2);
					//压入下载队列
					if(commodityImageString != null){
						DownImageItem tmpDownImageItem = new DownImageItem(Parser_Layout_AbsLayout.MODELTYPE_IMAGE, 0, commodityImageString, PageID.PAGEID_OWN_MAINPAGE,pageIndex);
						downImageManagerA.add(tmpDownImageItem);
					}
					mHandler.sendEmptyMessage(113367);
				}else if(tmpAbsLayouts[i].getModelType() == Parser_Layout_AbsLayout.MODELTYPE_COMMODITYINFO){
					parserCommodityInfo = (Parser_CommodityInfo)tmpAbsLayouts[i];
					commodityinfoString = parserCommodityInfo.getCommodityInfo().getStr();
//					((MyPageActivityA)mContext).mPopupMenu.setCommodityInfoString(commodityinfoString);
					((MyPageActivityA)mContext).rightPopupMenuA.setCommodityInfoString(commodityinfoString);
					((MyPageActivityA)mContext).rightBottomMenuA.setCommodityInfoString(commodityinfoString);
					((MyPageActivityA)mContext).leftPopupMenuA.setCommodityInfoString(commodityinfoString);
					((MyPageActivityA)mContext).leftBottomPopupMenuA.setCommodityInfoString(commodityinfoString);
					setPopMenuLikeParam(parserCommodityInfo);  //add by lyb.淘宝页新增喜欢
					//压入下载队列
					if(parserCommodityInfo.getImage()!=null){
						if(parserCommodityInfo.getImage().getSrc()!=null){
							DownImageItem tmpDownImageItem = new DownImageItem(Parser_Layout_AbsLayout.MODELTYPE_COMMODITYINFO, 1, parserCommodityInfo.getImage().getSrc(), PageID.PAGEID_OWN_MAINPAGE,pageIndex);
							downImageManagerA.add(tmpDownImageItem);
							mHandler.sendEmptyMessage(113368);
						}
					}
					mHandler.sendEmptyMessage(113369);
				}
			}
			//写缓存
			if(!CommonUtil.exists(CommonUtil.dir_cache_page+"/"+CommonUtil.urlToNum(msg.getData().getString("mUrl")))){
				CommonUtil.writeToFile(CommonUtil.dir_cache_page+"/"+CommonUtil.urlToNum(msg.getData().getString("mUrl")), (byte[])msg.obj);
			}
			//加载评论列表
			scheduleDismissOnScreenControls();
			
			//加入下载池
			if(CommonUtil.isNetWorkOpen(mContext)){
				String apn = CommonUtil.getApnType(mContext);
				//wifi下的逻辑已经在引擎中处理了，这里只要处理3g,2g
				if(apn.toLowerCase().indexOf("wifi") < 0){
					if(apn.toLowerCase().indexOf("3g") >= 0){
						DownLoadPoolItem item;
						if(pageIndex+1 < MyPageActivityA.urls.length&&!DownLoadPool.isClear){
							item = new DownLoadPoolItem(MyPageActivityA.urls[pageIndex+1], HttpThread.TYPE_PAGE, false);
							DownLoadPool.getInstance(mContext).add(item, -1);
						}
						if(pageIndex-1 > 0&&!DownLoadPool.isClear){
							item = new DownLoadPoolItem(MyPageActivityA.urls[pageIndex-1], HttpThread.TYPE_PAGE, false);
							DownLoadPool.getInstance(mContext).add(item, -1);
						}
						if(pageIndex+2 < MyPageActivityA.urls.length&&!DownLoadPool.isClear){
							item = new DownLoadPoolItem(MyPageActivityA.urls[pageIndex+2], HttpThread.TYPE_PAGE, false);
							DownLoadPool.getInstance(mContext).add(item, -1);
						}
						if(pageIndex-2 > 0&&!DownLoadPool.isClear){
							item = new DownLoadPoolItem(MyPageActivityA.urls[pageIndex-2], HttpThread.TYPE_PAGE, false);
							DownLoadPool.getInstance(mContext).add(item, -1);
						}
					}else{
						DownLoadPoolItem item;
						if(pageIndex+1 < MyPageActivityA.urls.length&&!DownLoadPool.isClear){
							item = new DownLoadPoolItem(MyPageActivityA.urls[pageIndex+1], HttpThread.TYPE_PAGE, false);
							DownLoadPool.getInstance(mContext).add(item, -1);
						}
						if(pageIndex-1 > 0&&!DownLoadPool.isClear){
							item = new DownLoadPoolItem(MyPageActivityA.urls[pageIndex-1], HttpThread.TYPE_PAGE, false);
							DownLoadPool.getInstance(mContext).add(item, -1);
						}
					}
				}
			}
		}
		message.what = MessageID.MESSAGE_RENDER;
		message.obj = msg.obj;
		message.arg1 = msg.arg1;
		bundle.putInt("mType", type);
		bundle.putString("mApn",msg.getData().getString("mApn"));
		bundle.putString("mUrl", msg.getData().getString("mUrl"));
		message.setData(bundle);
		mHandler.sendMessage(message);
		
	}
	private int downcount;;
	private void renderPage(Message msg,int threadindex,int type){
		switch (type) {
		case HttpThread.TYPE_PAGE:
			if(isFling){
				isFling = false;
				OfflineLog.writeMyPageFling(commodityid);//写入离线日志
			}
			OfflineLog.writeMyPage(commodityid);//写入离线日志
			mHandler.sendEmptyMessageDelayed(MessageID.MESSAGE_CONNECT_LAYOUTOVER, 100);			
			break;
		case HttpThread.TYPE_IMAGE:
			//add by lyb
			mHandler.sendEmptyMessage(MessageID.MESSAGE_SET_LISTENER);
			if(threadindex == 20){
				isFinish = true;
				viewImage.setImageViewAddLine((byte[])msg.obj,imageUrl,msg.getData().getString("mApn"));				
				isImageTimerRun = false;
			}
			else{
				DownImageItem downImageItem = downImageManagerA.get(threadindex>=1000?threadindex-1000:threadindex);
				try {
					if(downImageItem.getType() == Parser_Layout_AbsLayout.MODELTYPE_IMAGE){
						viewImage.setImageViewAddLine((byte[])msg.obj,msg.getData().getString("mUrl"),msg.getData().getString("mApn"));
						downcount++;
					}else if(downImageItem.getType() == Parser_Layout_AbsLayout.MODELTYPE_COMMODITYINFO){
						viewCommodityIcon.setImageView((byte[])msg.obj,msg.getData().getString("mUrl"));
						downcount++;
					}else if(downImageItem.getType() == Parser_Layout_AbsLayout.MODELTYPE_LISTITEM){
						viewNoScrollList.getListItem(threadindex).setImageView((byte[])msg.obj,msg.getData().getString("mUrl"));
					}
					if(downcount == 2){ //下载结束，自动刷新
						((MyPageActivityA)mContext).autoScroll();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;
		}
	}
	
	public void addProgress(){
		if(loadingBar != null){
			loadingBar.setVisibility(View.VISIBLE);
		}
		if(textwrongTip != null){
			textwrongTip.setVisibility(View.INVISIBLE);
		}
			
	}
	
	public void closeProgress(){
		if(loadingBar != null){
			loadingBar.setVisibility(View.INVISIBLE);
		}
	}
	
//	public String addUrlParam(String url){
//		if(URLUtil.IsLocalUrl()){
//			return url;
//		}
//		return url+"&ver="+URLUtil.version+"&deviceid="+CommonUtil.getIMEI(mContext);
//	}
	
//	public void addUrl(){
//    	if(pageIndex > 0&&pageIndex < 53){
//    		if(MyPageActivityA.urls[pageIndex-1] == null){
//    			MyPageActivityA.urls[pageIndex-1] = preUrl;
//    		}
//    		if(MyPageActivityA.urls[pageIndex+1] == null){
//    			MyPageActivityA.urls[pageIndex+1] = nextUrl;
//    		}
//    	}else{
//    		if(pageIndex == 0){
//    			if(MyPageActivityA.urls[pageIndex+1] == null){
//        			MyPageActivityA.urls[pageIndex+1] = nextUrl;
//        		}
//    		}else{
//    			if(MyPageActivityA.urls[pageIndex-1] == null){
//        			MyPageActivityA.urls[pageIndex-1] = preUrl;
//        		}
//    		}
//    	}
//    }
	
	private void Json(byte[] data,int threadindex){
		try{
			String str = new String(data,"UTF-8");
			str = CommonUtil.formUrlEncode(str);
			LogPrint.Print("json = "+str);
			if(threadindex == 4||threadindex == 5){//喜欢按钮逻辑
				JSONObject jObject = new JSONObject(str);
				String result = jObject.getString("result");
				if(result != null){
					if(result.equalsIgnoreCase("true")){
						String likeNum = jObject.getString("likenum");
						Bundle bundle = new Bundle();
						bundle.putString("likenum", likeNum);						
						Message message = new Message();
						if(threadindex == 4){//删除
							message.what= 100004;
							message.setData(bundle);
							mHandler.sendMessage(message);
						}else if(threadindex == 5){//添加
							bundle.putBoolean("likeNumLimit", jObject.getBoolean("likeNumLimit"));
							message.what= 100005;
							message.setData(bundle);
							mHandler.sendMessage(message);
						}
						mHandler.sendEmptyMessage(100006);
					}else{
						mHandler.sendEmptyMessage(100006);
						CommonUtil.ShowToast(mContext, "失败了!");
					}
				}
			}else{
				if(threadindex == 1||threadindex == 2||threadindex == 3){
					//评论返回
					try {
						JSONObject jObject = new JSONObject(str);
						String result = jObject.getString("result");
						if(result != null){
							if(result.equalsIgnoreCase("true")){
								//刷新评论列表
								mHandler.sendEmptyMessage(113373);
							}else{
								mHandler.sendEmptyMessage(113375);
							}
						}
					} catch (Exception e) {
						mHandler.sendEmptyMessage(113375);
					}
				}else{
					//评论列表
					Message message = new Message();
					message.what = 113374;
					Bundle bundle = new Bundle();
					//评论列表协议变更 lyb
					JSONObject json = new JSONObject(str);					
					if(str.length() > 2){
						JSONArray  jsonArray = json.getJSONArray("comment");
						bundle.putString("jsonarray", jsonArray.toString());					
					}else{
						bundle.putString("jsonarray", "");
					}
//					bundle.putString("jsonarray", str);					
					message.setData(bundle);
					mHandler.sendMessage(message);
				}
			}
		}catch(Exception e){
			mHandler.sendEmptyMessage(100006);
			e.printStackTrace();
		}
	}
	
	//发表评论,-1:商品评论，0：一级评论，1：多级评论
	public void sendComment(int issubject,String msgString,int subjectid,int commentid){
		if(issubject == -1){
			mConnectUtil = new ConnectUtil(mContext, downHandler,55);
			mConnectUtil.connect(addUrlParam(URLUtil.URL_COMMENT_COMMODITY, UserUtil.userid, commodityid, msgString), HttpThread.TYPE_PAGE, 1);
			showCommentState("评论发布中...");
		}else if(issubject == 0){
			mConnectUtil = new ConnectUtil(mContext, downHandler,55);
			mConnectUtil.connect(addUrlParam1(URLUtil.URL_COMMENT_USER, UserUtil.userid, subjectid, msgString), HttpThread.TYPE_PAGE, 2);
			showCommentState("评论发布中...");
		}else{
			mConnectUtil = new ConnectUtil(mContext, downHandler,55);
			mConnectUtil.connect(addUrlParam2(URLUtil.URL_COMMENT_THIRDCOMMENT, UserUtil.userid, subjectid, commentid, msgString), HttpThread.TYPE_PAGE, 3);
			showCommentState("评论发布中...");
		}
	}
	
	public String addUrlParam(String url,int oid,int cid,String msg){
		if(URLUtil.IsLocalUrl()){
			return url;
		}
		if(msg == null)msg="";
		return url+"?oid="+oid+"&cid="+cid+"&msg="+CommonUtil.toUrlEncode(msg);
	}
	
	public String addUrlParam1(String url,int oid,int subjectid,String msg){
		if(URLUtil.IsLocalUrl()){
			return url;
		}
		if(msg == null)msg="";
		return url+"?oid="+oid+"&subjectid="+subjectid+"&msg="+CommonUtil.toUrlEncode(msg);
	}
	
	public String addUrlParam2(String url,int oid,int subjectid,int commentid,String msg){
		if(URLUtil.IsLocalUrl()){
			return url;
		}
		if(msg == null)msg="";
		return url+"?oid="+oid+"&subjectid="+subjectid+"&commentid="+commentid+"&msg="+CommonUtil.toUrlEncode(msg);
	}
	
	public String addUrlParam(String url,int cid){
		if(URLUtil.IsLocalUrl()){
			return url;
		}
		return url+"?oid="+UserUtil.userid+"&pi=0&cid="+cid + "&plaid=" + URLUtil.plaid;
	}
	
	//获取评论列表
	public void connentCommentList(){
		commentUrl = null;//连接前清空
		comments = null;
		mConnectUtil = new ConnectUtil(mContext, downHandler,false,pageIndex);
		mConnectUtil.connect(addUrlParam(URLUtil.URL_MYPAGE_COMMENTLIST, commodityid), HttpThread.TYPE_JSON, 10);
	}
	
	public void clearCommentCache(){
		CommonUtil.deleteCacheFile(addUrlParam(URLUtil.URL_MYPAGE_COMMENTLIST, commodityid), mContext);
	}
	
	public Runnable mRunnable = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			connentCommentList();
		}
	};
	
	public void removeRunnable(){
		if(commentHandler != null){
			commentHandler.removeCallbacks(mRunnable);
		}
	}
	
	public Handler commentHandler = new Handler();
	public void scheduleDismissOnScreenControls(){
		if(commentHandler == null){
			commentHandler = new Handler();
		}
		removeRunnable();
		commentHandler.postDelayed(mRunnable, 2000);
	}
	
	NotificationManager notificationManager;
	Notification notification;
	//显示发送状态
	private void showCommentState(String msg){
		if(notificationManager == null){
			notificationManager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		}
		notification = new Notification(R.drawable.notifytionicon, msg, System.currentTimeMillis());
		notification.defaults |= Notification.DEFAULT_VIBRATE;//震动通知
	    notification.flags |= Notification.FLAG_AUTO_CANCEL; //在通知栏上点击此通知后自动清除此通知
	    PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, new Intent(), 0);
	    notification.setLatestEventInfo(mContext, null, msg, pendingIntent);
	    notificationManager.notify(pageIndex, notification);
	    notificationManager.cancel(pageIndex);
	}
	
	public void recycle(){
		if(viewImage != null){
			if(viewImage.bitmap != null){
				viewImage.bitmap.recycle();
			}
		}
	}
	
	public void closeTimer(){
		LogPrint.Print("lyb", "closeTimer");
		timeout = 0;
		if(mTimer != null){
			mTimer.cancel();
		}
		isImageTimerRun = false;
		mTimerTask = null;
		mTimer = null;
	}
	
	public void reinitPlayButton(){
		if(viewImage != null){
			viewImage.reInitPlayIcon();
		}
	}
	
	
	
	
	
	
	
	
	
	

	class View_Image extends LinearLayout{
		private LinearLayout l;
		private ProgressBar loading;
		private LinearLayout lprogress;
		private Button mypage_playbtn;
		private Bitmap bitmap;
		private Context context;

		//视频提示
		private LinearLayout videoLayout;
		private FrameLayout frameVideoLayout;
		//2G下单个商品的图片信息
		private ImageView imageView;
		private Handler handler;
		//设置图片居中
		private int image_left;
		private int image_top ;
		//设置文字居中
		private int textView_left;
		private int textView_top;
		private String mImagePath;
		private String lImagePath;
		private String imagePath41;
		private String imagePath2;
		
		int time = 0;
		int i = 0;
		public Bitmap getBitmap() {
			return bitmap;
		}

		
		private LinearLayout lineLinearLayout;
		
		public View_Image(Context context) {
			super(context);
			mApn = null;
			pixels = getPixels(CommonUtil.screen_width, CommonUtil.screen_height);
			this.context = context;
			// TODO Auto-generated constructor stub
			Bitmap tmp = BitmapFactory.decodeResource(getResources(), R.drawable.loading1);
			FrameLayout frameLayout = (FrameLayout)((Activity)context).getLayoutInflater().inflate(R.layout.imagelayout,null);
			lineLinearLayout = (LinearLayout)frameLayout.findViewById(R.id.line);
			imagelayout = (LinearLayout)frameLayout.findViewById(R.id.imagelayout);
			mypage_playbtn = (Button)frameLayout.findViewById(R.id.mypage_playbtn);
			lprogress = (LinearLayout)frameLayout.findViewById(R.id.lprogress);
			
			frameVideoLayout = (FrameLayout)frameLayout.findViewById(R.id.frmeVideoNotifi);
			videoLayout = (LinearLayout)frameLayout.findViewById(R.id.videonotifi);
			
			lprogress.setPadding(CommonUtil.screen_width/2-tmp.getWidth()/2, CommonUtil.screen_width/2-tmp.getHeight()/2, getRight(), getBottom());
			loading = (ProgressBar)frameLayout.findViewById(R.id.loading);
			loading.setVisibility(GONE);
			l = new LinearLayout(context);
			l.setLayoutParams(new LayoutParams(CommonUtil.screen_width, CommonUtil.screen_width));
			l.setOrientation(LinearLayout.VERTICAL);
			l.setBackgroundColor(0xc9c9c9);
			l.setGravity(Gravity.CENTER_HORIZONTAL);
			textView = new TextView(context);		
			textView.setGravity(Gravity.CENTER_HORIZONTAL);
			textView.setTextColor(0xcc000000);	
			imageView = new ImageView(context);
			imageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			l.addView(textView);
			l.addView(imageView);
			
			imagelayout.addView(l);
			//del by lyb 待图片完之后再设置（Line 963）
//			imagelayout.setOnClickListener(imageMagnifyClickListener);
			addView(frameLayout);
		}
		/**
		 * 视频提示图标是否显示
		 */
		public void isVideoDis(){
			
			String prompLike = CommonUtil.getPromptLikeStatu(getContext());
			String prompVideo = CommonUtil.getPromptVideoStatu(getContext());
			if(prompLike.equals("0")){
				videoLayout.setVisibility(View.GONE);
				//CommonUtil.savePromptVideoStatu(getContext(), "1");
				
			}else if(prompLike.equals("1") && prompVideo.equals("0")){
				
				videoLayout.setVisibility(View.VISIBLE);
				/*mHandler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						videoLayout.setVisibility(View.GONE);
					}
				}, 2000);*/
				CommonUtil.savePromptVideoStatu(getContext(), "1");
			}else if(prompLike.equals("1") && prompVideo.equals("1")){
				
				videoLayout.setVisibility(View.GONE);
			}
		}
		/**
		 * lyb
		 * @param resid
		 */
		public void setImgBack(int resid){
			loadingBar.setIndeterminateDrawable(getResources().getDrawable(resid));
		}
		
		private void setImageViewAddLine(final byte[] data,final String cacheUrl,String apn){
			if(cacheUrl != null){
				try {
					//保存图片
//					new Thread(){
//						public void run(){
							CommonUtil.writeToFile(CommonUtil.dir_cache+"/"+CommonUtil.urlToNum(cacheUrl), data);
//						}
//					}.start();
				} catch (Exception e) {
				}
			}
			mApn = apn;
			Bitmap temp = BitmapFactory.decodeByteArray(data, 0, data.length);
			if(temp != null){
				Bitmap line = null;
				if(apn.toLowerCase().indexOf("wifi") < 0){
//					if(apn.toLowerCase().indexOf("3g") >= 0){
//						lineLinearLayout.setVisibility(View.INVISIBLE);
//						line = BitmapFactory.decodeResource(getResources(), R.drawable.share_line);
//						bitmap = CommonUtil.mergerTwoBitmap(temp, line, 0, temp.getHeight()-line.getHeight());
//						l.setBackgroundDrawable(new BitmapDrawable(bitmap));
//					}else{
						if(!isFinish){
							imageUrl = URLUtil.userIconHost+"/terminalconnector/resource/product/pic/"+pixels;
							String mImageUrl = imageUrl + "/M/"+commodityid+".jpg";
							String lImageUrl = imageUrl + "/L/"+commodityid+".jpg";
							String imageUrl41 = imageUrl + "/41/"+commodityid+".jpg";
							String imageUrl2 = imageUrl + "/2/"+commodityid+".jpg";
							mImagePath = CommonUtil.dir_cache + "/" + CommonUtil.urlToNum(mImageUrl);
							lImagePath = CommonUtil.dir_cache + "/" + CommonUtil.urlToNum(lImageUrl);
							imagePath41 = CommonUtil.dir_cache + "/" + CommonUtil.urlToNum(imageUrl41);
							imagePath2 = CommonUtil.dir_cache + "/" + CommonUtil.urlToNum(imageUrl2);
							byte[] dataImage = null;
							if(CommonUtil.exists(lImagePath)){//判断L存在吗
								dataImage = CommonUtil.getSDCardFileByteArray(lImagePath);
								temp = BitmapFactory.decodeByteArray(dataImage, 0, dataImage.length);
								l.removeAllViews();
								line = BitmapFactory.decodeResource(getResources(), R.drawable.share_line);
								bitmap = CommonUtil.mergerTwoBitmap(temp, line, 0, temp.getHeight()-line.getHeight());
								l.setBackgroundDrawable(new BitmapDrawable(bitmap));
							}else{
								if(CommonUtil.exists(mImagePath)||CommonUtil.exists(imagePath41)||CommonUtil.exists(imagePath2)){
									if(CommonUtil.exists(mImagePath)){
										dataImage = CommonUtil.getSDCardFileByteArray(mImagePath);
									}else if(CommonUtil.exists(imagePath41)){
										dataImage = CommonUtil.getSDCardFileByteArray(imagePath41);
									}else{
										dataImage = CommonUtil.getSDCardFileByteArray(imagePath2);
									}
									temp = null;
									temp = BitmapFactory.decodeByteArray(dataImage, 0, dataImage.length);
								}
								line = BitmapFactory.decodeResource(getResources(), R.drawable.share_line);
								textView.setTextSize(18);
								textView.setText("嫌太小？猛击这里！");
								//文字居中
								textView_top = temp.getHeight()/2;
								textView.setPadding(0, textView_top, 0, 0);
								//设置图片居中
								image_top = l.getHeight()/2-temp.getHeight()/2;
								
								imageView.setPadding(0, image_top-textView_top, 0, 0);
								imageView.setImageBitmap(temp);
							}
						}else{
							l.removeAllViews();
							line = BitmapFactory.decodeResource(getResources(), R.drawable.share_line);
							bitmap = CommonUtil.mergerTwoBitmap(temp, line, 0, temp.getHeight()-line.getHeight());
							l.setBackgroundDrawable(new BitmapDrawable(bitmap));							
						}
//					}
				}
				else{
					lineLinearLayout.setVisibility(INVISIBLE);
					line = BitmapFactory.decodeResource(getResources(), R.drawable.share_line);
					bitmap = CommonUtil.mergerTwoBitmap(temp, line, 0, temp.getHeight()-line.getHeight());
					l.setBackgroundDrawable(new BitmapDrawable(bitmap));

				}
				postInvalidate();
				line.recycle();
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
		
		/** 点击小图放大 **/
		public OnClickListener imageMagnifyClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
//				if(mApn.toLowerCase().indexOf("wifi") < 0&&mApn.toLowerCase().indexOf("3g") < 0){
				if(mApn.toLowerCase().indexOf("wifi") < 0 ){  //lyb-3.11  3g,2g下载大图
					imageUrl = URLUtil.userIconHost+"/terminalconnector/resource/product/pic/";
					imageUrl += pixels + "/L/"+commodityid+".jpg";
					textView.setTextSize(18);
					if(CommonUtil.isNetWorkOpen(context)){
						imagelayout.setOnClickListener(null);
						mConnectUtil.connect(imageUrl, HttpThread.TYPE_IMAGE, 20);
						textView.setText("0%");
					}
					else{
						CommonUtil.ShowToast(context, "杯具了!联网不给力啊");
						isFinish = false;
						
					}
				}
			}
		};
		private OnClickListener clickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(parserImage != null){
					mypage_playbtn.setBackgroundResource(R.drawable.mypage_playbtn_f);
					onMyPagePlayClick(parserImage.getHref());
				}
			}
		};
		
		private OnClickListener frameClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				frameVideoLayout.setVisibility(GONE);
				videoLayout.setVisibility(GONE);
				CommonUtil.savePromptLikeStatu(getContext(), "1");
			}
		};
		
		//重置播放按钮图片
		public void reInitPlayIcon(){
			if(mypage_playbtn != null){
				mypage_playbtn.setBackgroundResource(R.drawable.mypage_playbtn);
			}
		}
		
		//解析完成后，渲染时调用
		private void showPlayButton(){
			if(parserImage.getPlayable()){
				mypage_playbtn.setVisibility(VISIBLE);
				frameVideoLayout.setVisibility(VISIBLE);
				frameVideoLayout.setOnClickListener(frameClickListener);
				mypage_playbtn.setOnClickListener(clickListener);
				//isVideoDis();
			}
		}
		
//		private void showProgress(){
//			loading.setVisibility(VISIBLE);
//		}
		
		/**单品页中大图的响应*/
		public void onMyPagePlayClick(String url){
			LogPrint.Print("=======onMyPagePlayClick=======");
			LogPrint.Print("url = "+url);
			LogPrint.Print("=============over======================");
			
			Bundle bundle = new Bundle();
			bundle.putString("url", url);
			Message msg = new Message();
			msg.setData(bundle);
			msg.what = MessageID.MESSAGE_CLICK_MYPAGEPLAY;
			mHandler.sendMessage(msg);
		}
		
	}
	
	class View_CommodityInfo extends LinearLayout{

		private String likeBtnUrl;
		private int likeBtnAction;
		private LinearLayout lcommoditybox;
		private ImageView likebutton;
		public ImageView getLikebutton() {
			return likebutton;
		}

		private TextView likeTextView;
		private LinearLayout llikebutton;
		private TextView commodityinfoboxTextView;
		private TextView commodityinfoboxtimeTextView;
		private ImageView commodityinfoboxicon;
		private TextView price;
		
		public View_CommodityInfo(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
			LinearLayout l = null;
			l = (LinearLayout)((Activity)context).getLayoutInflater().inflate(R.layout.commodityinfo, null);
			llikebutton = (LinearLayout)l.findViewById(R.id.llikebutton);
			likebutton = (ImageView)l.findViewById(R.id.likebutton);
			likeTextView = (TextView)l.findViewById(R.id.likebuttontext);
			lcommoditybox = (LinearLayout)l.findViewById(R.id.lcommoditybox);
			commodityinfoboxTextView = (TextView)l.findViewById(R.id.commodityinfoboxtext);
			commodityinfoboxtimeTextView = (TextView)l.findViewById(R.id.commodityinfoboxtime);
			commodityinfoboxicon = (ImageView)l.findViewById(R.id.commodityinfoboxicon);
			price = (TextView)l.findViewById(R.id.price);
			
			addView(l);
		}
		
		//解析完成后，渲染时调用
		private void setClick(){
			if(parserCommodityInfo.getLikeButton()!=null){
				likeBtnUrl = parserCommodityInfo.getLikeButton().getHref();
				likeBtnAction = parserCommodityInfo.getLikeButton().getAction();
				if(parserCommodityInfo.getLikeButton().getStr().length() > 0){
					if(Integer.parseInt(parserCommodityInfo.getLikeButton().getStr()) >= 0){
						llikebutton.setOnClickListener(likeClickListener);
					}
				}
			}
//			lcommoditybox.setOnClickListener(commodityboxClickListener);
			commodityinfoboxicon.setOnClickListener(commodityboxClickListener);
		}
		
		private OnClickListener likeClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onCommodityInfoLikeButtonClick(parserCommodityInfo.getLikeButton().getHref(), parserCommodityInfo.getLikeButton().getAction());
			}
		};
		
		private OnClickListener commodityboxClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onCommodityInfoBoxClick();
			}
		};
		
		public Parser_Image getParserImage(){
			return parserCommodityInfo.getImage();
		}
		
		public String getLikeBtnUrl(){
			return likeBtnUrl;
		}
		
		public int getLikeBtnAction(){
			return likeBtnAction;
		}
		
		public String getCommodityInfoString(){
			if(parserCommodityInfo.getCommodityInfo()!=null){
				return parserCommodityInfo.getCommodityInfo().getStr();
			}
			return null;
		}
		
		public void changeBackGround(){
			post(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					lcommoditybox.setBackgroundResource(R.drawable.commodityinfoboxbg);
				}
			});
		}
		
		public boolean getLikeState(){
			if(parserCommodityInfo.getLikeButton()!=null){
				if(parserCommodityInfo.getLikeButton().getStr().length() > 0){
					return parserCommodityInfo.getLikeButton().getLike();
				}
			}
			return false;
		}
		
		public int getDeleteId(){
			if(parserCommodityInfo.getLikeButton()!=null){
				if(parserCommodityInfo.getLikeButton().getStr().length() > 0){
					return parserCommodityInfo.getLikeButton().getLikeId();
				}
			}
			return -1;
		}
		
		public void setLikeButtonBgImage(boolean isLike){
			if(likebutton != null){
				if(isLike){
					parserCommodityInfo.getLikeButton().setLike(Parser_Layout_AbsLayout.TRUE);
					likebutton.setBackgroundResource(R.drawable.likebutton_f);
				}else{
					parserCommodityInfo.getLikeButton().setLike(Parser_Layout_AbsLayout.FALSE);
					likebutton.setBackgroundResource(R.drawable.likebutton);
				}
			}
		}
		
		public void setLikeNum(String num){
			if(likeTextView != null){
				if(num != null&&num.length() > 0){
					LogPrint.Print("num = "+num);
					likeTextView.setText(num);
				}
			}
			//绘制价格 (常态显示)
			if(parserImage != null){
				if(parserImage.getPrice() != null&&parserImage.getPrice().length() > 0){
					price.setText("￥"+parserImage.getPrice());
				}
			}
		}
		
		//获得喜欢个数
		public String getLikeNum() {
			String likenum = "";
			try {
				if (likeTextView != null) {
					likenum = likeTextView.getText().toString();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return likenum;
		}
		
		public void setCommodityInfoBoxText(){
			if(parserCommodityInfo.getCommodityInfo()!=null){
				String tempstr = parserCommodityInfo.getCommodityInfo().getStr();
				tempstr = tempstr.replaceAll("#", "\n");
				commodityinfoboxTextView.setText(tempstr);
			}
		}
		
		public void setCommodityInfoTime(){
			if(parserCommodityInfo.getCommodityTime()!=null){
//				commodityinfoboxtimeTextView.setText(parserCommodityInfo.getCommodityTime().getStr());
				//取消时间显示
				commodityinfoboxtimeTextView.setText("");
			}
		}
		
		/**单品页中喜欢按钮的响应*/
		public void onCommodityInfoLikeButtonClick(String url, int action){
			LogPrint.Print("=======onCommodityInfoLikeButtonClick=======");
			LogPrint.Print("url = "+url);
			LogPrint.Print("action = "+action);
			LogPrint.Print("=============over======================");
			
			Bundle bundle = new Bundle();
			bundle.putString("url", url);
			bundle.putInt("action", action);
			Message msg = new Message();
			msg.setData(bundle);
			msg.what = MessageID.MESSAGE_CLICK_COMMODITYINFO_LIKEBUTTON;
			mHandler.sendMessage(msg);
		}
		
		/**单品页中商品简介区域的响应*/
		public void onCommodityInfoBoxClick(){
			LogPrint.Print("=======onCommodityInfoBoxClick=======");
			LogPrint.Print("还需要在activity中获得commodityid");
			LogPrint.Print("=============over======================");
			
			Message msg = new Message();
			msg.what = MessageID.MESSAGE_CLICK_COMMODITYINFO_BOX;
			mHandler.sendMessage(msg);
		}
		
		
	}
	
//	 class MyDialog extends Dialog{
//
//		public MyDialog(final Context context) {
//			super(context);
//			new android.app.AlertDialog.Builder(getContext())
//			.setMessage("你喜欢的商品数量已达上限，可以去掉部分不太喜欢的商品 ！").setPositiveButton("现在就去掉", new OnClickListener() {
//				
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					
//					Intent intent = new Intent();
//					intent.putExtra("url", URLUtil.URL_MAINPAGE_LIKE);
//					intent.setClass(getContext(), StreampageActivity.class);
//					context.startActivity(intent);
//					((Activity)context).overridePendingTransition(R.anim.menu_left_in, R.anim.menu_left_out);
//				}
//			}).setNegativeButton("这次先算了", null).show();
//		}
//		 
//	 }
	/**
	 * 点击喜欢收藏
	 */
	class View_LikeNotifiIcon extends LinearLayout{
		private ImageView likenotifiImage;
		
		public View_LikeNotifiIcon(Context context) {
			super(context);
			likenotifiImage = new ImageView(context);
			likenotifiImage.setBackgroundResource(R.drawable.likenotifi);
			
			likenotifiImage.setVisibility(View.GONE);
			String promp = CommonUtil.getPrompStatu(getContext());
			String likePromp = CommonUtil.getPromptLikeStatu(getContext());
			if(promp.equals("0")){
				
				likenotifiImage.setVisibility(View.GONE);
				
				
			}else if(promp.equals("1") && likePromp.equals("0")){
				
				likenotifiImage.setVisibility(View.VISIBLE);
				//CommonUtil.savePromptLikeStatu(getContext(), "0");
				/*mHandler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						likenotifiImage.setVisibility(View.GONE);
						CommonUtil.savePromptLikeStatu(getContext(), "1");
					}
				}, 2000);*/
				//CommonUtil.savePromptLikeStatu(getContext(), "1");
				
			}else if(promp.equals("1") && likePromp.equals("1")){
				
				likenotifiImage.setVisibility(View.GONE);
			}
			//initImageSize(getResources().getDisplayMetrics().densityDpi);
			likenotifiImage.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					likenotifiImage.setVisibility(View.GONE);
					CommonUtil.savePromptLikeStatu(getContext(), "1");
				}
			});
			addView(likenotifiImage);
		}

	}
	class View_CommodityIcon extends LinearLayout{

		private ImageView imageView;
		private Parser_Image parserImage;
		private int commodityInfoIcon_width;
		private int commodityInfoIcon_height;
		
		public View_CommodityIcon(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
			imageView = new ImageView(context);
			
			imageView.setBackgroundResource(R.drawable.commodityiconbg);
			
			initImageSize(getResources().getDisplayMetrics().densityDpi);			
			addView(imageView);
			
		}
		
		//解析完成后，渲染时调用
		private void setClick(Parser_Image parserimage){
			parserImage = parserimage;
			if(parserImage.getClickable()){
				imageView.setOnClickListener(imageClickListener);
			}
		}
		
		public void setImageView(final byte[] data,final String cacheUrl){
			try {
				//保存图片
//				new Thread(){
//					public void run(){
						CommonUtil.writeToFile(CommonUtil.dir_cache+"/"+CommonUtil.urlToNum(cacheUrl), data);
//					}
//				}.start();
			} catch (Exception e) {
			}
			Bitmap temp = BitmapFactory.decodeByteArray(data, 0, data.length);
			if(temp == null)return;
			if(commodityInfoIcon_width == 147){
				temp = CommonUtil.resizeImage(temp, commodityInfoIcon_width+4, commodityInfoIcon_height+4);
			}else{
				temp = CommonUtil.resizeImage(temp, commodityInfoIcon_width+3, commodityInfoIcon_height+3);
			}
			Bitmap background = BitmapFactory.decodeResource(getResources(), R.drawable.commodityiconbg);
			imageView.setImageBitmap(CommonUtil.mergerIcon(background, temp,15,5,4));
		}
		
		private OnClickListener imageClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onCommodityInfoIconClick(parserImage.getHref());
			}
		};
		
		public void initImageSize(int dpi){
			if(dpi <= 120){//qvga
				commodityInfoIcon_width = 46;
				commodityInfoIcon_height = 46;
			}else if(dpi <= 160){//hvga
				commodityInfoIcon_width = 62;
				commodityInfoIcon_height = 62;
			}else if(dpi <= 320){//wvga
				commodityInfoIcon_width = 92;
				commodityInfoIcon_height = 92;
				if(CommonUtil.screen_height >= 960){
					if(CommonUtil.screen_width >=640){
						commodityInfoIcon_width = 147;
						commodityInfoIcon_height = 147;
					}
				}
			}else{//更大屏幕分辨率
				commodityInfoIcon_width = 92;
				commodityInfoIcon_height = 92;
			}
		}
		
		public int getCommodityInfoIcon_W(){return commodityInfoIcon_width;}
		public int getCommodityInfoIcon_H(){return commodityInfoIcon_height;}
		
		/**单品页中卖家图标的响应*/
		public void onCommodityInfoIconClick(String url){
			LogPrint.Print("=======onCommodityInfoIconClick=======");
			LogPrint.Print("url = "+url);
			LogPrint.Print("还需要在activity中获得commodityid");
			LogPrint.Print("=============over======================");
			
			Bundle bundle = new Bundle();
			bundle.putString("url", url);
			Message msg = new Message();
			msg.setData(bundle);
			msg.what = MessageID.MESSAGE_CLICK_COMMODITYINFO_ICON;
			mHandler.sendMessage(msg);
		}
	}
	
	public class View_NoScrollList extends LinearLayout{

		private Parser_MyPageList parserList;
		private LinearLayout l;
		private LinearLayout lCommentList;
		private ArrayList<View_NoScrollListItem> viewlistItems;
		private Parser_MyPageListItem[] parserlistItems;
		private int startIndex;//下载队列开始的索引号
		
		public View_NoScrollList(Context context,Parser_MyPageList parserList,int tag) {
			super(context);
			// TODO Auto-generated constructor stub
			this.parserList = parserList;
			l = (LinearLayout)((Activity)context).getLayoutInflater().inflate(R.layout.list_comment, null);
			lCommentList = (LinearLayout)l.findViewById(R.id.lCommentList);
			viewlistItems = new ArrayList<View_NoScrollListItem>();
			parserlistItems = parserList.getMyPageListItems();
			startIndex = downImageManagerA.Size();
			for(int i = 0;parserlistItems!=null&&i < parserlistItems.length;i ++){
				viewlistItems.add(new View_NoScrollListItem(context, parserlistItems[i], startIndex+i+1000, tag));
				lCommentList.addView(viewlistItems.get(i));
			}
			
			addView(l);
		}
		
		public View_NoScrollListItem getListItem(int index){
			return viewlistItems.get(index-startIndex-1000);
		}
		
		public void clear(){
			if(viewlistItems!=null){
				viewlistItems.clear();
			}
		}
		
	}
	
	public class View_NoScrollListItem extends LinearLayout{
		
		private Parser_MyPageListItem parserListItem;
		private LinearLayout l;
		private ImageView listItem_c_icon;
		private TextView listItem_c_name;
		private TextView listItem_c_time;
		private TextView listItem_c_comment;
		private LinearLayout listItem_area;

		public View_NoScrollListItem(Context context,Parser_MyPageListItem parserListItem,int index,int tag) {
			super(context);
			// TODO Auto-generated constructor stub
			this.parserListItem = parserListItem;
			l = (LinearLayout)((Activity)context).getLayoutInflater().inflate(R.layout.listitem_comment_c,null);
			listItem_c_icon = (ImageView)l.findViewById(R.id.listItem_c_icon);
			listItem_c_name = (TextView)l.findViewById(R.id.listItem_c_name);
			listItem_c_time = (TextView)l.findViewById(R.id.listItem_c_time);
			listItem_c_comment = (TextView)l.findViewById(R.id.listItem_c_comment);
			listItem_area = (LinearLayout)l.findViewById(R.id.listItem_area);
			
			if(parserListItem != null){
				if(parserListItem.getCommentTo()!=null&&parserListItem.getUserName()!=null){
					String textString;
					if(parserListItem.getCommentTo().length() > 0){//有回复人，需要特殊颜色处理
						textString = parserListItem.getUserName()+ "\n" + "回复 "+parserListItem.getCommentTo();
						Spannable WordtoSpan = new SpannableString(textString);
						WordtoSpan.setSpan(new ForegroundColorSpan(Color.BLACK), parserListItem.getUserName().length()+1, parserListItem.getUserName().length()+3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
						listItem_c_name.setText(WordtoSpan);
					}else{
						textString = parserListItem.getUserName();
						listItem_c_name.setText(textString);
					}
				}
				if(parserListItem.getTime() != null){
					listItem_c_time.setText(parserListItem.getTime());
				}
				if(parserListItem.getMsg1() != null){
					listItem_c_comment.setText(parserListItem.getMsg1());
				}
			}
			
			listItem_area.setOnClickListener(commentClickListener);
			listItem_c_icon.setOnClickListener(iconClickListener);
			
			//压入下载队列
			DownImageItem downImageItem = new DownImageItem(Parser_Layout_AbsLayout.MODELTYPE_LISTITEM, index, parserListItem.getIconSrcUrl(), PageID.PAGEID_OWN_MAINPAGE,tag);
			downImageManagerA.add(downImageItem);
			
			addView(l);
		}
		
		public void setImageView(final byte[] data,final String cacheUrl){
//			try {
//				//保存图片
//				new Thread(){
//					public void run(){
//						CommonUtil.writeToFile(CommonUtil.dir_cache+"/"+CommonUtil.urlToNum(cacheUrl), data);
//					}
//				}.start();
//			} catch (Exception e) {
//			}
			Bitmap temp = BitmapFactory.decodeByteArray(data, 0, data.length);
			if(temp == null)return;
			Bitmap background = BitmapFactory.decodeResource(getResources(), R.drawable.usericonbg);
			temp = CommonUtil.resizeImage(temp, background.getWidth(), background.getHeight());
			listItem_c_icon.setImageBitmap(CommonUtil.mergerIcon(background, temp,7));
		}
		
		private OnClickListener commentClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!ReplayPermit.isMayClick){
					return;
				}
				ReplayPermit.isMayClick = false;
				if(parserListItem != null){
					onCommentClick(parserListItem.getIsSubject(), parserListItem.getSubjectId(), parserListItem.getCommentId(),parserListItem.getCid(),parserListItem.getUserName(),parserListItem.getUserId());
				}				
			}
		};	

		
		private OnClickListener iconClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(parserListItem != null){
					onIconClick(parserListItem.getUserPageUrl(), parserListItem.getUserName(),
							parserListItem.getUserId());
				}
			}
		};
		
		//单品页中评论项被点击
		private void onCommentClick(int is_subject,int subjectid,int commentid,int cid,String toName,int toUserId){
			LogPrint.Print("=======onCommentClick=======");
			LogPrint.Print("is_subject = "+is_subject);
			LogPrint.Print("subjectid = "+subjectid);
			LogPrint.Print("commentid = "+commentid);
			LogPrint.Print("=============over======================");
			
			Bundle bundle = new Bundle();
			bundle.putInt("is_subject", is_subject);
			bundle.putInt("subjectid", subjectid);
			bundle.putInt("commentid", commentid);
			//商品ID
			bundle.putInt("cid", cid);
			//接收者昵称
			bundle.putString("toname", toName);
			bundle.putInt("touserid", toUserId);
			Message msg = new Message();
			msg.setData(bundle);
			msg.what = MessageID.MESSAGE_CLICK_LISTITEM_COMMENT;
			mHandler.sendMessage(msg);
		}
		
		//单品页中评论项用户图标被点击
		private void onIconClick(String url, String nickname, int uid){
			LogPrint.Print("=======onIconClick=======");
			LogPrint.Print("url = "+url);
			LogPrint.Print("=============over======================");
			
			Bundle bundle = new Bundle();
			bundle.putString("url", url);
			bundle.putString("nickname", nickname);
			bundle.putInt("userid", uid);
			Message msg = new Message();
			msg.setData(bundle);
			msg.what = MessageID.MESSAGE_CLICK_LISTITEM_ICON;
			mHandler.sendMessage(msg);
		}
	}
	
	
	

}
