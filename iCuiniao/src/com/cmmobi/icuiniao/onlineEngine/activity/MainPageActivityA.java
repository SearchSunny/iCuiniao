/**
 * 
 */
package com.cmmobi.icuiniao.onlineEngine.activity;

import java.io.File;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemSelectedListener;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.Activity.Activity;
import com.cmmobi.icuiniao.Activity.FirstPageActivity;
import com.cmmobi.icuiniao.Activity.LoginAndRegeditActivity;
import com.cmmobi.icuiniao.ui.adapter.MainPageAdapter;
import com.cmmobi.icuiniao.ui.view.MainMenu;
import com.cmmobi.icuiniao.ui.view.MainPageGallery;
import com.cmmobi.icuiniao.ui.view.MainPageView;
import com.cmmobi.icuiniao.ui.view.StylePage;
import com.cmmobi.icuiniao.ui.view.StylePageDialog1;
import com.cmmobi.icuiniao.util.ActionID;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.ConnectUtil;
import com.cmmobi.icuiniao.util.DownLoadPool;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.LogoutUtil;
import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.OfflineLog;
import com.cmmobi.icuiniao.util.TimerForRock;
import com.cmmobi.icuiniao.util.URLUtil;
import com.cmmobi.icuiniao.util.UserUtil;
import com.cmmobi.icuiniao.util.connction.HttpThread;
import com.icuiniao.plug.im.Entity;
import com.icuiniao.plug.im.IMDataBase;

import org.json.JSONObject;

/**
 * @author hw
 *首页
 */
public class MainPageActivityA extends Activity implements AnimationListener{

	private FrameLayout fmainpage;
	private FrameLayout mainpagelayout;
	private MainMenu mainMenu;
	private MainPageGallery mGallery;
	private Button titlebar_menubutton;
	private Button titlebar_leftmenu;
	private Button titlebar_backbutton;
	private Button titlebar_rightstylebtn;
	private StylePage stylePage;
	
	private ImageView titleMessageImageId; //消息点
	IMDataBase imDataBase;
	private MainPageAdapter adapter;
	private boolean isAnimationOpen;//主菜单动画状态
	private boolean isStylePageAnimationOpen;//自定义列表动画状态
	private int animationIndex;
	
	private int titleHeight = 66;
	
	private boolean bFristPage;
	
	private MainPageView[] mainPageViews;//保存首页的六页数据
	private Handler flingHandler;
	private Runnable flingRunnable;
	private int curRenderPageIndex;//当前显示的页索引
	public static boolean isSensorSuccess;//是否摇一摇成功
	public static int backPageIndex;//单品页返回的页索引
	private Runnable scrollRun;	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainpage_layout);
        imDataBase = new IMDataBase(this);
        isAnimationOpen = false;
        isStylePageAnimationOpen = false;
        isSensorSuccess = false;
        backPageIndex = -1;
        fmainpage = (FrameLayout)findViewById(R.id.fmainpage);
        //mainpagelayout = (FrameLayout)findViewById(R.id.mainpagelayout);
        mainMenu = (MainMenu)findViewById(R.id.mainmenu);
        stylePage = (StylePage)findViewById(R.id.stylepage);
        mGallery = (MainPageGallery)findViewById(R.id.mainpage_gallery);
        titlebar_menubutton = (Button)findViewById(R.id.titlebar_menubutton);
        titlebar_backbutton = (Button)findViewById(R.id.titlebar_backbutton);
        titlebar_leftmenu = (Button)findViewById(R.id.titlebar_leftmenu);
        titlebar_rightstylebtn = (Button)findViewById(R.id.titlebar_rightstylebtn);
        titleMessageImageId = (ImageView)findViewById(R.id.messageImageId);
        titlebar_menubutton.setOnClickListener(menubtnClickListener);
        titlebar_leftmenu.setOnClickListener(menubtnClickListener);
        titlebar_backbutton.setOnClickListener(stylepageClickListener);
        titlebar_rightstylebtn.setOnClickListener(stylepageClickListener);
        
//        getMessageByUserId();
        mainPageViews = new MainPageView[6];
        
        bFristPage = getIntent().getExtras().getBoolean("firstpage", false);
		//启动服务
		if(bFristPage){
			if(UserUtil.userid == -1||UserUtil.userState != 1){//没登陆不启动
				OfflineLog.writeMainMenu_MessageButton((byte)0);//写入离线日志
			}else if(CommonUtil.getMessageReceiverState(this)){
				OfflineLog.writeMainMenu_MessageButton((byte)1);//写入离线日志
			}
		}
        
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
		mGallery.setPadding(0, CommonUtil.dip2px(MainPageActivityA.this, CommonUtil.px2dip(MainPageActivityA.this, titleHeight)), 0, 0);
        mGallery.setSpacing(30);
        mGallery.setUnselectedAlpha(1.0f);
        adapter = new MainPageAdapter(this,mainPageViews,UserUtil.isShowPrice,UserUtil.isShowFeature);
        mGallery.setAdapter(adapter);
        
        //gallery滑动停止的监听逻辑////////////////////////
        flingRunnable = new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(mainPageViews[curRenderPageIndex] != null){
					for(int i = 0;i < mainPageViews.length;i ++){
						if(mainPageViews[i] != null){
							mainPageViews[i].closeTimer();
						}
					}
					//不包括摇一摇页面
					if (curRenderPageIndex < 5 && UserUtil.isRemoteLogin) {
						Intent intent = new Intent();
						intent.setClass(MainPageActivityA.this,
								LoginAndRegeditActivity.class);
						startActivity(intent);
						return;
					}
					mainPageViews[curRenderPageIndex].initConnect();
				}
			}
		};
		
        mGallery.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				LogPrint.Print("webview","arg2 = "+arg2);
				if(TimerForRock.isNoTime()){
					Intent intent = new Intent();
					intent.setClass(MainPageActivityA.this, RockActivityA.class);
					intent.putExtra("fromscreen", 0);
					startActivity(intent);
					overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
				}else{
					curRenderPageIndex = arg2;
					scheduleDismissOnScreenControls();
					if(curRenderPageIndex == 5){
						if(UserUtil.userState != 1){
							titlebar_backbutton.setVisibility(View.INVISIBLE);
						}else{
							titlebar_backbutton.setBackgroundResource(R.drawable.stylepagebtn_0);
							titlebar_backbutton.setVisibility(View.VISIBLE);
						}
					}else{
						titlebar_backbutton.setVisibility(View.INVISIBLE);
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
      //del by lyb at 2013.06.4 按下事件不刷新
//        mGallery.setOnTouchListener(new OnTouchListener() {
//			
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				// TODO Auto-generated method stub
//				if(mainPageViews[curRenderPageIndex] != null){
//					mainPageViews[curRenderPageIndex].mHandler.removeMessages(MessageID.MESSAGE_CONNECT_LAYOUTOVER);
//					scheduleDismissOnScreenControls();
//				}
//				return false;
//			}
//		});
        //////////////////////////////////////////
        scrollRun = 
			new Runnable(){	
        		int startx = (int)(CommonUtil.screen_width * 0.95f);				
				int stopX = (int)(CommonUtil.screen_width * 0.8);
				int x = startx; //滑动开始的坐标
				int y = (int)(CommonUtil.screen_height * 0.6f);
				float ratioW = CommonUtil.screen_width / 480f;
				int space = (int)(15 * ratioW); //每次滑动的距离
				int delay = 0;//每次滑动的延迟时间
				boolean isBack;
				@Override				
				public void run() {					
					if(x <= stopX || isBack ){  //滑动到1/3时开始返回
						if(x >= startx){ //返回结束							
							return;
						}
						isBack = true;  //开始返回
						MotionEvent mev1 = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 0, x, y, 0); 
						if(x + space > startx){  //最后超出的微调
							space = startx - x;
						}
						x = x + space;						
						MotionEvent mev2 = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 2, x, y, 0);
						mGallery.onScroll(mev1, mev2, -space, 0);
						mHandler.postDelayed(this, delay);
						return;
					}
					//自动滑动
					MotionEvent mev1 = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 0, x, y, 0); 
					x = x - space;					
					MotionEvent mev2 = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 2, x, y, 0);
					mGallery.onScroll(mev1, mev2, space, 0);
					mHandler.postDelayed(this, delay);
					CommonUtil.savePromptGalleryStatu(MainPageActivityA.this, true);
					
				}
			};
    }
    
    public void autoScroll() {
		boolean galleryStatu = CommonUtil
				.getPromptGallery(this);
		if (!galleryStatu) {
			mHandler.removeCallbacks(scrollRun);
			mHandler.postDelayed(scrollRun, 0);
			
		}
	}
    
    @Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
    	for(int i = 0;mainPageViews != null&&i < mainPageViews.length;i ++){
			if(mainPageViews[i] != null){
				mainPageViews[i].closeTimer();
			}
		}
		super.onSaveInstanceState(outState);
	}

	private boolean systemError = false;
    @Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		systemError = true;
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		for(int i = 0;mainPageViews != null&&i < mainPageViews.length;i ++){
			if(mainPageViews[i] != null){
				mainPageViews[i].closeTimer();
			}
		}
		mHandler.postDelayed(new Runnable(){

			@Override
			public void run() {
				if (isAnimationOpen) {
					closeMenuQuick();
				}
				
			}
			
		}, 500);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
    	LogPrint.Print("lyb", "mainOnResume");
		super.onResume();
		if (systemError) {
			systemError = false;
			return;
		}
		if(CommonUtil.getRemoteLogout(this)){
		LogoutUtil.showMessagetToLogout(this);	
		CommonUtil.saveRemoteLogout(this, false);
		}
		getMessageByUserId();
//		//注册广播
		registerReceiver();
		MyPageActivityA.urls = MyPageActivityA.saveUrlsFromMain;
		if(!UserUtil.isNewLoginOrExit&&!isSensorSuccess){
			if(backPageIndex >= 0){
				if(curRenderPageIndex != backPageIndex){
					mGallery.setSelection(backPageIndex);
				}
				backPageIndex = -1;
			}
		}
		if(UserUtil.isNewLoginOrExit){
			for(int i = 0;mainPageViews!=null&&i < mainPageViews.length;i ++){
				mainPageViews[i] = null;
			}
			for(int i = 0;MyPageActivityA.urls!=null&&i < MyPageActivityA.urls.length;i ++){
				MyPageActivityA.urls[i] = null;
			}
			MyPageActivityA.urls = null;
			sendBroadcast(new Intent(ActionID.ACTION_BROADCAST_MYPAGE_CLEAR));
			if(mGallery.getSelectedItemPosition() == 0){
				adapter.notifyDataSetInvalidated();
				adapter.notifyDataSetChanged();
			}else{
				adapter.notifyDataSetChanged();
				mGallery.setSelection(0);
			}
			//lyb 关闭菜单
			if(isAnimationOpen){
				gotoMenu();
			}
			return;
		}
		if(isSensorSuccess){
			isSensorSuccess = false;
			MainPageView.isStartLoadPage = false;
			MainPageView.isDownLoadCache = true;
			MainPageView.isSensor = true;
			for(int i = 0;mainPageViews!=null&&i < mainPageViews.length;i ++){
				mainPageViews[i] = null;
			}
			for(int i = 0;MyPageActivityA.urls!=null&&i < MyPageActivityA.urls.length;i ++){
				MyPageActivityA.urls[i] = null;
			}
			MyPageActivityA.urls = null;
			sendBroadcast(new Intent(ActionID.ACTION_BROADCAST_MYPAGE_CLEAR));
			if(mGallery.getSelectedItemPosition() == 0){
				adapter.notifyDataSetInvalidated();
				adapter.notifyDataSetChanged();
			}else{
				adapter.notifyDataSetChanged();
				mGallery.setSelection(0);
			}
		}
		if(UserUtil.isPriceOrFeatureChange){
			UserUtil.isPriceOrFeatureChange = false;
			adapter.notifyDataSetInvalidated();
			adapter.notifyDataSetChanged();
		}
		//lyb 关闭菜单
		if(isAnimationOpen){			
			gotoMenu();
//			new Handler().postDelayed(new Runnable(){    
//			    public void run() {    
//			    	gotoMenu();  
//			    }    
//			 }, 100);
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//销毁广播
		unRegisterReceiver();
	}

	@Override
	public void finish(){
		if(true == bFristPage){
			buildExitDialog();
		}else{
			super.finish();
		}
	}	

    
    private void scheduleDismissOnScreenControls(){
    	if(flingHandler == null){
    		flingHandler = new Handler();
    	}
    	flingHandler.removeCallbacks(flingRunnable);
    	flingHandler.postDelayed(flingRunnable, 100);
    }
    
    private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case MessageID.MESSAGE_CONNECT_DOWNLOADOVER:
				Json((byte[])msg.obj,msg.getData().getString("content_type"),msg.arg1);
				break;
			case MessageID.MESSAGE_MAINMENU_OPEN:
				if(fmainpage != null){
					if(fmainpage.getRight() > getMainMenuAnimationPos(50)){
						fmainpage.offsetLeftAndRight(animationIndex);
						fmainpage.postInvalidate();
						mHandler.sendEmptyMessage(MessageID.MESSAGE_MAINMENU_OPEN);
						animationIndex = -(CommonUtil.screen_width-getMainMenuAnimationPos(50))/5;
					}
				}
				break;
			case MessageID.MESSAGE_MAINMENU_CLOSE:
				if(fmainpage != null){
					if(fmainpage.getLeft() < 0){
						fmainpage.offsetLeftAndRight(animationIndex);
						mHandler.sendEmptyMessage(MessageID.MESSAGE_MAINMENU_CLOSE);
						fmainpage.postInvalidate();
						animationIndex = (CommonUtil.screen_width-getMainMenuAnimationPos(50))/5;
					}
				}
				break;
			case 998877:
				//查询消息已读、未读
				long messageId = msg.getData().getLong("messageId");
				int state = imDataBase.getIsReadState(messageId);
				//已读
				if(state == 1){
					mainMenu.setMessageButtonRes(false);
					titleMessageImageId.setVisibility(View.GONE);
				}else if(state == 0 && UserUtil.userState == 1){
					mainMenu.setMessageButtonRes(true);
					titleMessageImageId.setVisibility(View.VISIBLE);
				}else if(state == 0 && UserUtil.userState != 1){
					mainMenu.setMessageButtonRes(false);
					titleMessageImageId.setVisibility(View.GONE);
				}
				else if(state == -1){
					mainMenu.setMessageButtonRes(false);
					titleMessageImageId.setVisibility(View.GONE);
				}
				break;
			}
		}
    	
    };
    
    @Override
	public boolean onPrepareOptionsMenu(Menu menu) {
    	if(!isAnimationOpen&&!isStylePageAnimationOpen){
    		gotoMenu();
    	}else{
    		if(isAnimationOpen){
    			gotoMenu();
    		}else{
    			if(isStylePageAnimationOpen){
    				gotoStylePage();
    			}
    		}
    	}
		return true;
	}

	private void gotoMenu(){
		if(fmainpage != null){
			isAnimationOpen = !isAnimationOpen;			
			if(isAnimationOpen){
				if(mainMenu != null){
					mainMenu.setIsCanClick(true);
				}
				mHandler.removeMessages(MessageID.MESSAGE_CONNECT_LAYOUTOVER);
				Animation animation = AnimationUtils.loadAnimation(this, R.anim.right_out);
	    		animation.setFillEnabled(true);
	    		animation.setFillAfter(true);
	    		animation.setAnimationListener(this);
	    		//动画播放，实际布局坐标不变
	    		fmainpage.startAnimation(animation);
	    		OfflineLog.writeMainMenu();//写入离线日志
			}else{
				if(mainMenu != null){
					mainMenu.setIsCanClick(false);
				}
				Animation animation = AnimationUtils.loadAnimation(this, R.anim.left_in);
				animation.setAnimationListener(this);
	    		animation.setFillEnabled(true);
	    		animation.setFillAfter(true);
	    		fmainpage.startAnimation(animation);
			}						
		}	
	}
	
	public void gotoStylePage(){
		if(curRenderPageIndex == 5){
			if(fmainpage != null){
				isStylePageAnimationOpen = !isStylePageAnimationOpen;
				if(isStylePageAnimationOpen){
					if(stylePage != null){
						stylePage.setVisibility(View.VISIBLE);
					}
					mHandler.removeMessages(MessageID.MESSAGE_CONNECT_LAYOUTOVER);
					Animation animation = AnimationUtils.loadAnimation(this, R.anim.right_in);
		    		animation.setFillEnabled(true);
		    		animation.setFillAfter(true);
		    		animation.setAnimationListener(this);
		    		//动画播放，实际布局坐标不变
		    		fmainpage.startAnimation(animation);
				}else{
					Animation animation = AnimationUtils.loadAnimation(this, R.anim.left_out);
					animation.setAnimationListener(this);
		    		animation.setFillEnabled(true);
		    		animation.setFillAfter(true);
		    		fmainpage.startAnimation(animation);
				}
			}
		}
	}
	
	public void closeMenuQuick(){
		if(fmainpage == null){
			return;
		}
		isAnimationOpen = !isAnimationOpen;	
		if(mainMenu != null){
			mainMenu.setIsCanClick(false);
		}
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.left_in_quick);
		animation.setAnimationListener(this);
		animation.setFillEnabled(true);
		animation.setFillAfter(true);
		fmainpage.startAnimation(animation);
	}
	
	private int getMainMenuAnimationPos(int dip){
		int result = CommonUtil.dip2px(this, dip);
		if(result%10 == 0){
			return result;
		}else{
			if(result%10 < 5){
				result -= result%10;
			}else{
				result += 10 - result%10;
			}
		}
		return result;
	}
	
	private OnClickListener menubtnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			gotoMenu();
		}
	};
	
	private OnClickListener stylepageClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			gotoStylePage();
		}
	};
	
	public void buildExitDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(MainPageActivityA.this);
		builder.setTitle("确定要退出翠鸟吗?").setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				CommonUtil.isInMessageScreen = false;
				CommonUtil.isNormalInToApp = false;
				DownLoadPool.getInstance(MainPageActivityA.this).stop();
//				stopService(serviceIntent);//不关闭服务
//				android.os.Process.killProcess(android.os.Process.myPid());
				bFristPage = false;
				String cameraDirString = CommonUtil.getExtendsCardPath()+"iCuiniao/camera";
				CommonUtil.deleteAll(new File(cameraDirString));
				//解决首页未绘制完成时快速退出，再进入时FC的问题////////
				for(int i = 0;mainPageViews != null&&i < mainPageViews.length;i ++){
					if(mainPageViews[i] != null){
						mainPageViews[i].closeTimer();
						mainPageViews[i].downHandler.removeMessages(MessageID.MESSAGE_CONNECT_DOWNLOADOVER);
						mainPageViews[i].mHandler.removeMessages(MessageID.MESSAGE_RENDER);
						mainPageViews[i].mHandler.removeMessages(MessageID.MESSAGE_CONNECT_LAYOUTOVER);
					}
				}
				for(int i = 0;MyPageActivityA.urls!=null&&i < MyPageActivityA.urls.length;i ++){
					MyPageActivityA.urls[i] = null;
				}
				MyPageActivityA.urls = null;
				////////////////////////////////////////////////////
				OfflineLog.writeExit();//写入离线日志
				//关闭获取评论数的handler
				Intent intent = new Intent(ActionID.ACTION_BROADCAST_STOP_COMMENT_CONNECT);
				sendBroadcast(intent);
				//关闭所有activity
				Intent intent1 = new Intent();
				intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent1.setClass(MainPageActivityA.this, FirstPageActivity.class);
				intent1.putExtra("type", 1);
				startActivity(intent1);
				finish();
			}
		}).setNegativeButton("取消", null).show();
	}

	@Override
	public void onAnimationEnd(Animation animation) {		
		if(isAnimationOpen){						
			titlebar_leftmenu.setVisibility(View.VISIBLE);			
			titlebar_menubutton.setClickable(false);
			titlebar_backbutton.setVisibility(View.INVISIBLE);
			titlebar_rightstylebtn.setVisibility(View.INVISIBLE);
		}else{
			titlebar_menubutton.setClickable(true);
			if(curRenderPageIndex == 5){
				if(UserUtil.userState == 1){
					titlebar_backbutton.setVisibility(View.VISIBLE);
					titlebar_rightstylebtn.setVisibility(View.INVISIBLE);
				}
			}else{
				titlebar_backbutton.setVisibility(View.INVISIBLE);
				titlebar_rightstylebtn.setVisibility(View.INVISIBLE);
			}
		}
		
		if(curRenderPageIndex == 5&&!isAnimationOpen&&UserUtil.userState == 1){
			if(isStylePageAnimationOpen){
				titlebar_rightstylebtn.setVisibility(View.VISIBLE);
				titlebar_backbutton.setVisibility(View.VISIBLE);
				titlebar_menubutton.setVisibility(View.INVISIBLE);
				titlebar_leftmenu.setVisibility(View.INVISIBLE);
				stylePage.initConnect(MainPageActivityA.this);
			}else{
				if(stylePage != null){
					stylePage.setVisibility(View.INVISIBLE);
				}
				titlebar_rightstylebtn.setVisibility(View.INVISIBLE);
				titlebar_backbutton.setVisibility(View.VISIBLE);
				titlebar_menubutton.setVisibility(View.VISIBLE);
				titlebar_leftmenu.setVisibility(View.INVISIBLE);
				if(stylePage.selectedIndex.length() > 0){
					new ConnectUtil(MainPageActivityA.this, mHandler, 0).connect(URLUtil.URL_STYLEPAGE_SELECT+"?styleId="+stylePage.selectedIndex, HttpThread.TYPE_PAGE, 5);
				}else if(stylePage.removeIndex.length() > 0){
					new ConnectUtil(MainPageActivityA.this, mHandler, 0).connect(URLUtil.URL_STYLEPAGE_REMOVESELECT+"?styleId="+stylePage.removeIndex, HttpThread.TYPE_PAGE, 6);
				}
			}
		}
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationStart(Animation animation) {
		if (!isAnimationOpen) {
			titlebar_leftmenu.setVisibility(View.GONE);			
			if(curRenderPageIndex == 5){
				if(UserUtil.userState == 1){
					titlebar_backbutton.setVisibility(View.VISIBLE);
					titlebar_rightstylebtn.setVisibility(View.INVISIBLE);
				}
			}else{
				titlebar_backbutton.setVisibility(View.INVISIBLE);
				titlebar_rightstylebtn.setVisibility(View.INVISIBLE);
			}
		}
		
		if(curRenderPageIndex == 5&&!isAnimationOpen&&UserUtil.userState == 1){
			if(!isStylePageAnimationOpen){
				titlebar_rightstylebtn.setVisibility(View.INVISIBLE);
			}
		}
		
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		int barY = 80 * CommonUtil.screen_width / 480 ;
		if(isAnimationOpen){			
			if(ev.getY() <= barY+15){
				return super.dispatchTouchEvent(ev);
			}
			final int offsetY = 40 * CommonUtil.screen_width / 480;
			ev.setLocation(ev.getX(), ev.getY() - offsetY);		
			return mainMenu.dispatchTouchEvent(ev);
		}
		if(isStylePageAnimationOpen&&!isAnimationOpen){
			if(ev.getY() <= barY+15){
				return super.dispatchTouchEvent(ev);
			}
			final int offsetY = 40 * CommonUtil.screen_width / 480;
			ev.setLocation(ev.getX(), ev.getY() - offsetY);		
			return stylePage.dispatchTouchEvent(ev);
		}
		return super.dispatchTouchEvent(ev);
	}
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(ActionID.ACTION_BROADCAST_APN_CHANGE.equals(action)){
				for(int i = 0;mainPageViews!=null&&i < mainPageViews.length;i ++){
					mainPageViews[i] = null;
				}
				if(mGallery.getSelectedItemPosition() == 0){
					adapter.notifyDataSetInvalidated();
					adapter.notifyDataSetChanged();
				}else{
					adapter.notifyDataSetChanged();
					mGallery.setSelection(0);
				}
			}
			//新增消息广播
			else if(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_MESSAGE.equals(action)||
					ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_PRIVATEMESSAGE.equals(action)||
					ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_SYSTEMMESSAGE.equals(action)||
					ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_FRIENDMESSAGE.equals(action)||
					ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_FRIENDMESSAGE_SUCCESS.equals(action) ||
					ActionID.ACTION_BROADCAST_LOGOUT.equals(action)){
				Message message = new Message();
				message.what = 998877;
				Bundle bundle = new Bundle();
				if(intent.getExtras() != null){
					
					bundle.putLong("messageId", intent.getExtras().getLong("messageId"));
				}
				
				message.setData(bundle);
				mHandler.sendMessageDelayed(message, 1000);
			}
		}
		
	};
	
	/**
	 * 注册广播接收器
	 */
	private final void registerReceiver() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ActionID.ACTION_BROADCAST_APN_CHANGE);
		//新增消息广播
		//接收到一条消息
		intentFilter.addAction(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_MESSAGE);
		//接收到一条私信
		intentFilter.addAction(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_PRIVATEMESSAGE);
		//接收到一条系统广播
		intentFilter.addAction(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_SYSTEMMESSAGE);
		//接收建立好友关系消息
		intentFilter.addAction(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_FRIENDMESSAGE);
		intentFilter.addAction(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_FRIENDMESSAGE_SUCCESS);
		intentFilter.addAction(ActionID.ACTION_BROADCAST_LOGOUT);
		registerReceiver(mReceiver, intentFilter);
	}
	
	/**
	 * 卸载广播接收器
	 */
	private final void unRegisterReceiver() {
		try {
			if(mReceiver != null){
				unregisterReceiver(mReceiver);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
    
	/**
	 * 根据消息已读\未读状态
	 */
	private void getMessageByUserId(){
		
		ArrayList<Entity> entitys =  imDataBase.getTheMessageByUserId(UserUtil.userid);
		if(entitys != null){
			boolean isFind = false;
			for (Entity entity : entitys) {
				//未读
				if(entity.getIsRead() == 0 && entity.getFromId() != UserUtil.userid && UserUtil.userState == 1){
					isFind = true;
					break;
				}
			}
			if(isFind){
				mainMenu.setMessageButtonRes(true);
				titleMessageImageId.setVisibility(View.VISIBLE);
			}else{
				mainMenu.setMessageButtonRes(false);
				titleMessageImageId.setVisibility(View.GONE);
			}
			
		}else{
			mainMenu.setMessageButtonRes(false);
			titleMessageImageId.setVisibility(View.GONE);
		}
		
	}
	
	private void Json(byte[] data,String contenttype,int threadindex){
		try {
			if("text/json".equals(contenttype)){
				String str = new String(data,"UTF-8");
				str = CommonUtil.formUrlEncode(str);
				LogPrint.Print("json = "+str);
				JSONObject jObject = new JSONObject(str);
				String result = jObject.getString("result");
				if(result != null){
					if(result.equalsIgnoreCase("true")){
						if(threadindex == 5){//添加
							stylePage.selectedIndex = "";
							stylePage.removeIndex = "";
							String msg = "自定义"+stylePage.selectedName+"成功，\n使用摇一下功能即可生效";
							Dialog dialog = new StylePageDialog1(this,
									R.style.dialogshock, msg);
							dialog.show();
						}else if(threadindex == 6){//删除
							stylePage.selectedIndex = "";
							stylePage.removeIndex = "";
							String msg = "取消自定义"+stylePage.selectedName+"成功，\n使用摇一下功能即可生效";
							Dialog dialog = new StylePageDialog1(this,
									R.style.dialogshock, msg);
							dialog.show();
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	
}
