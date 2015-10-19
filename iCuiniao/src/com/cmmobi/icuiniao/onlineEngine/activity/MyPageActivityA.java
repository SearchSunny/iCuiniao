/**
 * 
 */
package com.cmmobi.icuiniao.onlineEngine.activity;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
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
import android.view.View.OnLongClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.Activity.Activity;
import com.cmmobi.icuiniao.Activity.LoginAndRegeditActivity;
import com.cmmobi.icuiniao.Activity.OperateActivity;
import com.cmmobi.icuiniao.ui.adapter.MyPageAdapter;
import com.cmmobi.icuiniao.ui.view.MainMenu;
import com.cmmobi.icuiniao.ui.view.MyPageGallery;
import com.cmmobi.icuiniao.ui.view.MyPageSoftReference;
import com.cmmobi.icuiniao.ui.view.PopupMenu;
import com.cmmobi.icuiniao.ui.view.PopupMenuA;
import com.cmmobi.icuiniao.util.ActionID;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.OfflineLog;
import com.cmmobi.icuiniao.util.PageID;
import com.cmmobi.icuiniao.util.TimerForRock;
import com.cmmobi.icuiniao.util.URLUtil;
import com.cmmobi.icuiniao.util.UserUtil;
import com.icuiniao.plug.im.Entity;
import com.icuiniao.plug.im.IMDataBase;

/**
 * @author hw
 *单品页
 */
public class MyPageActivityA extends Activity implements AnimationListener{

	private FrameLayout fmypage;
	private MainMenu mainMenu;
	private MyPageGallery mGallery;
	private Button titlebar_backbutton;
	private Button titlebar_menubutton;
	private Button titlebar_leftmenu;
	private ImageView titleMessageImageId; //消息点
	private ImageView messageImageId; //消息点
	IMDataBase imDataBase;
	public PopupMenu mPopupMenu;
	public PopupMenuA rightPopupMenuA;
	public PopupMenuA rightBottomMenuA;
	public PopupMenuA leftPopupMenuA;
	public PopupMenuA leftBottomPopupMenuA;
	//帮助提示
	private FrameLayout notifiFrameLayout;
	//喜欢提示
	private LinearLayout likeNotifi;
	//视频提示
	private LinearLayout videoNotifi;
	
	private MyPageAdapter adapter;
	private boolean isAnimationOpen;//主菜单动画状态
	private int animationIndex;
	
	private String urlString;
	private int chickPos;//首页中的第几个图
	private int type;
	
	private Handler flingHandler;
	private Runnable flingRunnable;
	private int curRenderPageIndex;//当前显示的页索引
	
	public static String[] urls;//保存所有54个地址
	public static String[] saveUrlsFromMain;//保存首页过来的54个地址
	public static String[] saveUrlsFromLike;//保存我的喜欢页过来地址
	public static String[] saveUrlsFromLook;//保存我看过的页过来的地址
	public static String[] saveUrlsFromCoquetry;//保存我的撒娇页过来的地址
	public static String[] saveUrlsFromMessage;//保存消息管理页过来的地址	
	public static String[] saveUrlsFromUserPage;//保存个人主页过来的地址
	
	private MyPageSoftReference reference;
	
	public static final String GESTUREMODE = "1";
	public static final String BUTTONMODE = "0";
	public static final String LEFTHAND = "1";
	public static final String RIGHTHAND = "0";
	public String newMode = "";
	public String newHand = "";
	public String oldMode = "";
	public String oldHand = "";
	private String statu = "";
	private LinearLayout leftNotification;
	private LinearLayout rightNotification;
	private Handler handler = null;
	private Runnable scrollRun;	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imDataBase = new IMDataBase(this);
        isAnimationOpen = false;
        reference = new MyPageSoftReference();
        setContentView(R.layout.mypage_layout);
        fmypage = (FrameLayout)findViewById(R.id.fmypage);
        mPopupMenu = (PopupMenu)findViewById(R.id.popupmenu);
        rightPopupMenuA = (PopupMenuA)findViewById(R.id.rightPopupMenuA);
        rightBottomMenuA = (PopupMenuA)findViewById(R.id.rightBottomPopupMenuA);
        leftPopupMenuA = (PopupMenuA)findViewById(R.id.leftPopupMenuA);
        leftBottomPopupMenuA = (PopupMenuA)findViewById(R.id.leftBottomPopupMenuA);
		mainMenu = (MainMenu)findViewById(R.id.mainmenu);
		mGallery = (MyPageGallery)findViewById(R.id.mypage_gallery);
		titlebar_backbutton = (Button)findViewById(R.id.titlebar_backbutton);
		titlebar_backbutton.setOnClickListener(backbtnClickListener);
		titlebar_menubutton = (Button)findViewById(R.id.titlebar_menubutton);
		titlebar_menubutton.setOnClickListener(menubtnClickListener);
		titlebar_leftmenu = (Button)findViewById(R.id.titlebar_leftmenu);
		titlebar_leftmenu.setOnClickListener(menubtnClickListener);
		
		titleMessageImageId = (ImageView)findViewById(R.id.titleMessageImageId);
		messageImageId = (ImageView)findViewById(R.id.messageImageId);
		
		leftNotification = (LinearLayout)findViewById(R.id.leftNotification);
		rightNotification = (LinearLayout)findViewById(R.id.rightNotification);
		
		notifiFrameLayout = (FrameLayout)findViewById(R.id.notifiFrame);
		likeNotifi = (LinearLayout)findViewById(R.id.likenotifi);
		videoNotifi = (LinearLayout)findViewById(R.id.videonotifi);
//		getMessageByUserId();
//		isVideoDis(1);
		isVideoDis(2);
		urlString = getIntent().getExtras().getString("url");
		chickPos = getIntent().getExtras().getInt("chickPos");
		int pageIndex = getIntent().getExtras().getInt("pageindex");
		if(urls == null){
			urls = new String[UserUtil.cidMaxNum];
		}
		type = getIntent().getExtras().getInt("type");
		switch (type) {
		case PageID.PAGEID_MAINPAGE_ALL:
			saveUrlsFromMain = urls;
			break;
		case PageID.PAGEID_MAINPAGE_MYLIKE:
			saveUrlsFromLike = urls;
			break;
		case PageID.PAGEID_MAINPAGE_MYLOOKED:
			saveUrlsFromLook = urls;
			break;
		case PageID.PAGEID_COQUETRY:
			saveUrlsFromCoquetry = urls;
			break;
		case PageID.PAGEID_MESSAGEMANAGER:
			saveUrlsFromMessage = urls;
			break;
		case PageID.PAGEID_USERPAGE:
			saveUrlsFromUserPage = urls;
			break;
		}
		mGallery.setType(type);
		mGallery.setMax(urls.length);
		mGallery.setReference(reference);
		
		urls[pageIndex*9+chickPos] = urlString;
		
		mGallery.setSpacing(0);
		mGallery.setUnselectedAlpha(1.0f);
		adapter = new MyPageAdapter(this,urls.length,reference);
		mGallery.setAdapter(adapter);
		mGallery.setSelection(pageIndex*9+chickPos);
		
		//gallery滑动停止的监听逻辑////////////////////////
        flingRunnable = new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(reference.get(""+curRenderPageIndex) != null){
					//lyb-3.11 关闭timer
					reference.get(""+curRenderPageIndex).closeTimer();
					reference.get(""+curRenderPageIndex).initConnect();
				}
			}
		};
		
        mGallery.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				reference.recycle(arg2);
				if(TimerForRock.isNoTime()&&type == PageID.PAGEID_MAINPAGE_ALL){
					Intent intent = new Intent();
					intent.setClass(MyPageActivityA.this, RockActivityA.class);
					intent.putExtra("fromscreen", 1);
					startActivity(intent);
					overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
				}else{
					curRenderPageIndex = arg2;
					scheduleDismissOnScreenControls();
					isVideoDis(2);
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
//				if(reference.get(""+curRenderPageIndex) != null){
//					reference.get(""+curRenderPageIndex).mHandler.removeMessages(MessageID.MESSAGE_CONNECT_LAYOUTOVER);
//					scheduleDismissOnScreenControls();
//					reference.get(""+curRenderPageIndex).removeRunnable();
//				}
//				return false;
//			}
//		});
        //////////////////////////////////////////
        handler = new Handler();
		hide();
		if(UserUtil.userid == -1||UserUtil.userState != 1){
			//给予默认值			
//			CommonUtil.saveOperateMode(MyPageActivityA.this, GESTUREMODE);
			CommonUtil.saveOperateMode(MyPageActivityA.this, BUTTONMODE);  //默认按钮模式
			CommonUtil.saveOperateHand(MyPageActivityA.this, RIGHTHAND);
//			rightBottomAnimationIn();
			rightAnimationIn();  //默认右边按钮操作 lyb redo
		}
		else{
			rightBottomMenuA.setVisibility(View.GONE);
			oldMode = CommonUtil.getOperateMode(MyPageActivityA.this);
			oldHand = CommonUtil.getOperateHand(MyPageActivityA.this);
			animationIn(oldMode,oldHand);
		}
		
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
					if(x <= stopX || isBack ){  //滑动到1/2时开始返回
						if(x >= startx){ //返回结束 .（第一次滑动后会返回，返回时少返回一个space）
							LogPrint.Print("lyb", "stop x =" + x);							
							return;
						}
						isBack = true;  //开始返回
						MotionEvent mev1 = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 0, x, y, 0); 
						if(x + space > startx){  //最后超出的微调
							space = startx - x;
						}
						x = x + space;						
						LogPrint.Print("lyb", "back x =" + x);
						MotionEvent mev2 = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 2, x, y, 0);
						mGallery.onScroll(mev1, mev2, -space, 0);
						mHandler.postDelayed(this, delay);
						return;
					}
					//自动滑动
					LogPrint.Print("lyb", "go x =" + x);
					MotionEvent mev1 = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 0, x, y, 0); 
					x = x - space;					
					MotionEvent mev2 = MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 2, x, y, 0);
					mGallery.onScroll(mev1, mev2, space, 0);
					mHandler.postDelayed(this, delay);
					CommonUtil.savePromptMyGalleryStatu(MyPageActivityA.this, true);
					
				}
			};
    }
    
	private OnLongClickListener longListener = new OnLongClickListener() {
		
		@Override
		public boolean onLongClick(View v) {
			if(UserUtil.userid == -1||UserUtil.userState != 1){
				CommonUtil.ShowToast(MyPageActivityA.this, "请登陆或者注册，变身为小C的主人！");
				Intent intent11 = new Intent();
				intent11.setClass(MyPageActivityA.this, LoginAndRegeditActivity.class);
				startActivity(intent11);
			}
			else{
				//启动操作条Activity
				oldMode = CommonUtil.getOperateMode(MyPageActivityA.this);
				oldHand = CommonUtil.getOperateHand(MyPageActivityA.this);
				Intent intent = new Intent(MyPageActivityA.this,OperateActivity.class);
				startActivityForResult(intent, 9999);
			}
			return true;
		}
	};	
	
	/**
	 * 操作条出动画
	 * @param mode
	 * @param hand
	 */
	private void animationOut(String mode ,String hand){
		if(hand.equals(LEFTHAND)){
			if(mode.equals(GESTUREMODE)){
				leftBottomAnimationOut();
			}
			else{
				leftAnimationOut();
			}
		}
		else{
			if(mode.equals(GESTUREMODE)){
				rightBottomAnimationOut();
			}
			else{
				rightAnimationOut();
			}
		}
		
	}
	
	/**
	 * 操作条进动画
	 * @param mode
	 * @param hand
	 */
	private void animationIn(String mode , String hand){
		if(hand.equals(LEFTHAND)){
			if(mode.equals(GESTUREMODE)){
				leftBottomAnimationIn();
			}
			else{
				leftAnimationIn();
			}
		}
		else{
			if(mode.equals(GESTUREMODE)){
				rightBottomAnimationIn();
			}
			else{
				rightAnimationIn();
			}
		}
	}
	/**
	 * 提示是否显示
	 * @param statu
	 */
	private void promp(String hand,String statu){
		if(hand.equals(LEFTHAND)){
			if(statu.equals("0")){
				leftNotification.setVisibility(View.VISIBLE);
				notifiFrameLayout.setOnClickListener(leftNotificationClickListener);
				/*handler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						leftNotification.setVisibility(View.GONE);
					}
				}, 2000);*/
			}
		}
		else{
			if(statu.equals("0")){
				rightNotification.setVisibility(View.VISIBLE);
				
				notifiFrameLayout.setOnClickListener(rightNotificationClickListener);
				/*handler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						rightNotification.setVisibility(View.GONE);
					}
				}, 2000);*/
			}
		}
		//保存黄色提示信息，登录时只显示一次
		CommonUtil.savePromptStatu(MyPageActivityA.this, "1");
	}
	
	private OnClickListener leftNotificationClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			notifiFrameLayout.setVisibility(View.GONE);
			leftNotification.setVisibility(View.GONE);
		}
	};
	
private OnClickListener rightNotificationClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
				notifiFrameLayout.setVisibility(View.GONE);
				rightNotification.setVisibility(View.GONE);
		}
	};
	
    public void initMenu(){
		mPopupMenu.init(mPopupMenu);
    }
    
    public void initRight(){
    	if(!rightPopupMenuA.isCreate){
    		rightPopupMenuA.init(MyPageActivityA.this,"3");
    	}
    	rightPopupMenuA.lookImageView.setOnLongClickListener(longListener);
    	rightPopupMenuA.shareImageView.setOnLongClickListener(longListener);
    	rightPopupMenuA.gestureImageView.setOnLongClickListener(longListener);
    	rightPopupMenuA.setOnLongClickListener(longListener);
    }
    
    public void initRightBottom(){
    	if(!rightBottomMenuA.isCreate){
    		rightBottomMenuA.init(MyPageActivityA.this,"4");
    	}
    	rightBottomMenuA.lookImageView.setOnLongClickListener(longListener);
    	rightBottomMenuA.shareImageView.setOnLongClickListener(longListener);
    	rightBottomMenuA.gestureImageView.setOnLongClickListener(longListener);
    	rightBottomMenuA.setOnLongClickListener(longListener);
    }
    
    public void initLeft(){
    	if(!leftPopupMenuA.isCreate){
    		leftPopupMenuA.init(MyPageActivityA.this, "1");
    	}
    	leftPopupMenuA.lookImageView.setOnLongClickListener(longListener);
    	leftPopupMenuA.shareImageView.setOnLongClickListener(longListener);
    	leftPopupMenuA.gestureImageView.setOnLongClickListener(longListener);
    	leftPopupMenuA.setOnLongClickListener(longListener);
    }
    
    public void initLeftBottom(){
    	if(!leftBottomPopupMenuA.isCreate){
    		leftBottomPopupMenuA.init(MyPageActivityA.this, "2");
    	}
    	leftBottomPopupMenuA.lookImageView.setOnLongClickListener(longListener);
    	leftBottomPopupMenuA.shareImageView.setOnLongClickListener(longListener);
    	leftBottomPopupMenuA.gestureImageView.setOnLongClickListener(longListener); 
    	leftBottomPopupMenuA.setOnLongClickListener(longListener);
    }
    private AnimationListener animationListener = new AnimationListener() {
		
		@Override
		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onAnimationEnd(Animation animation) {
			if(oldHand.equals(LEFTHAND)){
				if(oldMode.equals(GESTUREMODE)){
					leftBottomPopupMenuA.setVisibility(View.GONE);
				}
				else{
					leftPopupMenuA.setVisibility(View.GONE);
				}
			}
			else{
				if(oldMode.equals(GESTUREMODE)){
					rightBottomMenuA.setVisibility(View.GONE);
				}
				else{
					rightPopupMenuA.setVisibility(View.GONE);
				}				
			}
			//执行进入动画
			animationIn(newMode, newHand);
			oldMode = newMode;
			oldHand = newHand;
		}
	};    

    private AnimationListener prompListener = new AnimationListener() {
		
		@Override
		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onAnimationEnd(Animation animation) {
			promp(oldHand,statu);
		}
	}; 
	
    /**
     * 左边按钮进入动画
     */
    private void leftAnimationIn(){
    	leftPopupMenuA.setVisibility(View.VISIBLE);
    	Animation animation = AnimationUtils.loadAnimation(MyPageActivityA.this, R.anim.leftbutton_in);
    	leftPopupMenuA.startAnimation(animation);
    	animation.setAnimationListener(prompListener);
    }	
	
    /**
     * 左边按钮移出动画
     */
    private void leftAnimationOut(){
    	Animation animation = AnimationUtils.loadAnimation(MyPageActivityA.this, R.anim.leftbutton_out);
    	leftPopupMenuA.startAnimation(animation);
    	animation.setAnimationListener(animationListener);
    }
    
	/**
	 * 手势按钮进入动画
	 */
	private void leftBottomAnimationIn(){
		leftBottomPopupMenuA.setVisibility(View.VISIBLE);
		Animation animation = AnimationUtils.loadAnimation(MyPageActivityA.this, R.anim.leftbottom_in);
		leftBottomPopupMenuA.startAnimation(animation);
		animation.setAnimationListener(prompListener);
	}		
	/**
	 * 手势按钮移出动画
	 */
	private void leftBottomAnimationOut(){
		Animation animation = AnimationUtils.loadAnimation(MyPageActivityA.this, R.anim.leftbottom_out);
		leftBottomPopupMenuA.startAnimation(animation);
		animation.setAnimationListener(animationListener);
	}
		
    /**
     * 右边按钮进入动画
     */
    private void rightAnimationIn(){
        rightPopupMenuA.setVisibility(View.VISIBLE);
    	Animation animation = AnimationUtils.loadAnimation(MyPageActivityA.this, R.anim.rightbutton_in);
    	rightPopupMenuA.startAnimation(animation);
    	animation.setAnimationListener(prompListener);
    }   
    /**
     * 右边按钮移出动画
     */
    private void rightAnimationOut(){
    	Animation animation = AnimationUtils.loadAnimation(MyPageActivityA.this, R.anim.rightbutton_out);
    	rightPopupMenuA.startAnimation(animation);
    	//设置监听
    	animation.setAnimationListener(animationListener);
    }
    
    /**
     * 右边手势按钮进入动画
     */
    private void rightBottomAnimationIn(){
    	rightBottomMenuA.setVisibility(View.VISIBLE);
    	Animation animation = AnimationUtils.loadAnimation(MyPageActivityA.this, R.anim.rightbottom_in);
    	rightBottomMenuA.startAnimation(animation);
    	animation.setAnimationListener(prompListener);
    }
    
    /**
     * 右边手势按钮移出动画
     */
    private void rightBottomAnimationOut(){
    	Animation animation = AnimationUtils.loadAnimation(MyPageActivityA.this, R.anim.rightbottom_out);
    	rightBottomMenuA.startAnimation(animation);
    	//设置监听
    	animation.setAnimationListener(animationListener);
    }   
    
    private void hide(){
    	rightPopupMenuA.setVisibility(View.GONE);
    	rightBottomMenuA.setVisibility(View.GONE);
    	leftPopupMenuA.setVisibility(View.GONE);
    	leftBottomPopupMenuA.setVisibility(View.GONE);
    }
    
    @Override
    protected void onPause(){
    	super.onPause();
    	//关闭图片超时计时器
    	if(reference.get(""+curRenderPageIndex) != null){
			reference.get(""+curRenderPageIndex).closeTimer();
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
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		switch (type) {
		case PageID.PAGEID_MAINPAGE_ALL:
			 urls = saveUrlsFromMain;
			break;
		case PageID.PAGEID_MAINPAGE_MYLIKE:
			 urls = saveUrlsFromLike;
			break;
		case PageID.PAGEID_MAINPAGE_MYLOOKED:
			 urls = saveUrlsFromLook;
			break;
		case PageID.PAGEID_COQUETRY:
			 urls = saveUrlsFromCoquetry;
			break;
		case PageID.PAGEID_MESSAGEMANAGER:
			 urls = saveUrlsFromMessage;
			break;
		case PageID.PAGEID_USERPAGE:
			urls = saveUrlsFromUserPage;
			break;
		}
		getMessageByUserId();
		//注册广播
		registerReceiver();
		//lyb 关闭菜单
		if(isAnimationOpen){
			gotoMenu();
		}
		mPopupMenu.setVisibility(View.GONE);
		statu = CommonUtil.getPrompStatu(MyPageActivityA.this);
		if((newMode!=null&&!newMode.equals(""))&&(newHand!=null&&!newHand.equals(""))){
			if(!oldMode.trim().equals(newMode.trim())||!oldHand.trim().equals(newHand.trim())){
				animationOut(oldMode, oldHand);
			}
		}
		if(reference.get(""+curRenderPageIndex) != null){
			reference.get(""+curRenderPageIndex).reinitPlayButton();
		}
//		autoScroll();
	}
	
	public void autoScroll() {
		boolean galleryStatu = CommonUtil
				.getPromptMyGallery(MyPageActivityA.this);
		if (!galleryStatu) {
			mHandler.removeCallbacks(scrollRun);
			mHandler.postDelayed(scrollRun, 500);
			
		}
	}
	
	public void finish(){
		Intent intent;
		switch (type) {
		case PageID.PAGEID_MAINPAGE_ALL:
			if(reference.get(""+curRenderPageIndex) != null){
				MainPageActivityA.backPageIndex = reference.get(""+curRenderPageIndex).curPageid;
			}else{
				MainPageActivityA.backPageIndex = -1;
			}
			break;
		case PageID.PAGEID_MAINPAGE_MYLIKE:
			intent = new Intent();
			intent.putExtra("url", URLUtil.URL_MAINPAGE_LIKE);
			intent.setClass(MyPageActivityA.this, StreampageActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		case PageID.PAGEID_MAINPAGE_MYLOOKED:
			intent = new Intent();
			intent.putExtra("url", URLUtil.URL_MAINPAGE_LOOKED);
			intent.setClass(MyPageActivityA.this, StreampageActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			break;
		default:
			break;
		}
    	super.finish();
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
			case MessageID.MESSAGE_MAINMENU_OPEN:
				if(fmypage != null){
					if(fmypage.getRight() > getMainMenuAnimationPos(50)){
						fmypage.offsetLeftAndRight(animationIndex);
						fmypage.postInvalidate();
						mHandler.sendEmptyMessage(MessageID.MESSAGE_MAINMENU_OPEN);
						animationIndex = -(CommonUtil.screen_width-getMainMenuAnimationPos(50))/5;
					}
				}
				break;
			case MessageID.MESSAGE_MAINMENU_CLOSE:
				if(fmypage != null){
					if(fmypage.getLeft() < 0){
						fmypage.offsetLeftAndRight(animationIndex);
						mHandler.sendEmptyMessage(MessageID.MESSAGE_MAINMENU_CLOSE);
						fmypage.postInvalidate();
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
					messageImageId.setVisibility(View.GONE);
					
				}else if(state == 0 && UserUtil.userState == 1){
					mainMenu.setMessageButtonRes(true);
					titleMessageImageId.setVisibility(View.VISIBLE);
					messageImageId.setVisibility(View.VISIBLE);
				}else if(state == 0 && UserUtil.userState != 1){
					mainMenu.setMessageButtonRes(true);
					titleMessageImageId.setVisibility(View.GONE);
					messageImageId.setVisibility(View.GONE);
				}else if(state == -1){
					mainMenu.setMessageButtonRes(false);
					titleMessageImageId.setVisibility(View.GONE);
					messageImageId.setVisibility(View.GONE);
				}
				break;
			}
		}
    	
    };
    
    @Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		gotoMenu();
		return true;
	}

	private void gotoMenu(){
//		if(fmypage != null){
//			animationIndex = 0;
//			isAnimationOpen = !isAnimationOpen;
//			if(isAnimationOpen){
//				if(mainMenu != null){
//					mainMenu.setIsCanClick(true);
//				}
//				if(reference.get(""+curRenderPageIndex) != null){
//					reference.get(""+curRenderPageIndex).mHandler.removeMessages(MessageID.MESSAGE_CONNECT_LAYOUTOVER);
//				}
//				mHandler.sendEmptyMessage(MessageID.MESSAGE_MAINMENU_OPEN);
//				OfflineLog.writeMainMenu();//写入离线日志
//			}else{
//				if(mainMenu != null){
//					mainMenu.setIsCanClick(false);
//				}
//				mHandler.sendEmptyMessage(MessageID.MESSAGE_MAINMENU_CLOSE);
//				if(reference.get(""+curRenderPageIndex) != null){
//					reference.get(""+curRenderPageIndex).mHandler.sendEmptyMessage(MessageID.MESSAGE_CONNECT_LAYOUTOVER);
//				}
//			}
//		}
		if(fmypage != null){
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
	    		fmypage.startAnimation(animation);
	    		OfflineLog.writeMainMenu();//写入离线日志
			}else{
				if(mainMenu != null){
					mainMenu.setIsCanClick(false);
				}
				Animation animation = AnimationUtils.loadAnimation(this, R.anim.left_in);
				animation.setAnimationListener(this);
	    		animation.setFillEnabled(true);
	    		animation.setFillAfter(true);
	    		fmypage.startAnimation(animation);
			}						
		}		
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
	
	private OnClickListener backbtnClickListener = new OnClickListener(){

		@Override
		public void onClick(View v) {
			finish();			
		}		
	};
	
	private OnClickListener menubtnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			gotoMenu();
		}
	};
	
	/**手势动作广播接收器*/
	private BroadcastReceiver mGestuReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if(ActionID.ACTION_BROADCAST_GESTURE_NO_RESULT.equals(action)){
				LogPrint.Print("broadcase: no");
				Toast.makeText(MyPageActivityA.this, "主人，您的画太抽象，小C没认出来", Toast.LENGTH_SHORT).show();
			}else if(ActionID.ACTION_BROADCAST_GESTURE_RIGHT.equals(action)){//购买
				LogPrint.Print("broadcase: right");
				if(reference.get(""+curRenderPageIndex) != null){
					reference.get(""+curRenderPageIndex).mHandler.sendEmptyMessage(113370);
				}
  			}else if(ActionID.ACTION_BROADCAST_GESTURE_CIRCLE.equals(action)){//喜欢
				LogPrint.Print("broadcase: circle");
				if(reference.get(""+curRenderPageIndex) != null){
					reference.get(""+curRenderPageIndex).mHandler.sendEmptyMessage(113371);
				}
			}else if(ActionID.ACTION_BROADCAST_GESTURE_TRIANGLE.equals(action)){//分享
				LogPrint.Print("broadcase: triangle");
				if(reference.get(""+curRenderPageIndex) != null){
					reference.get(""+curRenderPageIndex).mHandler.sendEmptyMessage(113372);
				}
			}else if(ActionID.ACTION_BROADCAST_MYPAGE_CLEAR.equals(action)){//清空数据
				reference.clear();
			}else if(ActionID.ACTION_BROADCAST_APN_CHANGE.equals(action)){
				reference.clear();
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
		intentFilter.addAction(ActionID.ACTION_BROADCAST_GESTURE_NO_RESULT);
		intentFilter.addAction(ActionID.ACTION_BROADCAST_GESTURE_RIGHT);
		intentFilter.addAction(ActionID.ACTION_BROADCAST_GESTURE_CIRCLE);
		intentFilter.addAction(ActionID.ACTION_BROADCAST_GESTURE_TRIANGLE);
		intentFilter.addAction(ActionID.ACTION_BROADCAST_MYPAGE_CLEAR);
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
		registerReceiver(mGestuReceiver, intentFilter);
	}
	
	/**
	 * 卸载广播接收器
	 */
	private final void unRegisterReceiver() {
		try {
			if(mGestuReceiver != null){
				unregisterReceiver(mGestuReceiver);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	//code:9100
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK && requestCode==9100){			
			boolean isSend = data.getExtras().getBoolean("issend");
			String msg = data.getExtras().getString("msg");
			int issubject = data.getExtras().getInt("issubject");
			int subjectid = data.getExtras().getInt("subjectid");
			int commentid = data.getExtras().getInt("commentid");
			if(isSend){
				if(reference.get(""+curRenderPageIndex) != null){
					reference.get(""+curRenderPageIndex).sendComment(issubject,msg,subjectid,commentid);
				}
			}
		}
		switch (type) {
		case PageID.PAGEID_MAINPAGE_ALL:
			 urls = saveUrlsFromMain;
			break;
		case PageID.PAGEID_MAINPAGE_MYLIKE:
			 urls = saveUrlsFromLike;
			break;
		case PageID.PAGEID_MAINPAGE_MYLOOKED:
			 urls = saveUrlsFromLook;
			break;
		case PageID.PAGEID_COQUETRY:
			 urls = saveUrlsFromCoquetry;
			break;
		case PageID.PAGEID_MESSAGEMANAGER:
			 urls = saveUrlsFromMessage;
			break;
		case PageID.PAGEID_USERPAGE:
			urls = saveUrlsFromUserPage;
			break;
		}
		//喜欢返回的刷新
		if(requestCode == MessageID.REQUESTCODE_LIKE_FLUSH){			
			if(reference.get(""+curRenderPageIndex) != null){
				LogPrint.Print("lyb", "like flush " );
				reference.get(""+curRenderPageIndex).initconnectWithNoCache();
			}
		}
		if(resultCode == RESULT_OK && requestCode==9999){
			Bundle bundle = data.getExtras();
			newMode = bundle.getString("newMode");
			newHand = bundle.getString("newHand");
		}
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		if(isAnimationOpen){
			titlebar_backbutton.setVisibility(View.GONE);
			titlebar_leftmenu.setVisibility(View.VISIBLE);			
			titlebar_menubutton.setClickable(false);
		}else{
			titlebar_menubutton.setClickable(true);
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
			titlebar_backbutton.setVisibility(View.VISIBLE);
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
		return super.dispatchTouchEvent(ev);
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
				if(entity.getIsRead() == 0 && entity.getFromId() != UserUtil.userid){
					isFind = true;
					break;
				}
			}
			if(isFind){
				mainMenu.setMessageButtonRes(true);
				titleMessageImageId.setVisibility(View.VISIBLE);
				messageImageId.setVisibility(View.VISIBLE);
			}else{
				mainMenu.setMessageButtonRes(false);
				titleMessageImageId.setVisibility(View.GONE);
				messageImageId.setVisibility(View.GONE);
			}
		}else{
			mainMenu.setMessageButtonRes(false);
			titleMessageImageId.setVisibility(View.GONE);
			messageImageId.setVisibility(View.GONE);
		}
	}
	
	/**
	 * 视频/喜欢提示图标是否显示
	 */
	public void isVideoDis(int type){
		//视频提示
		if(type == 1){
			String prompLike = CommonUtil.getPromptLikeStatu(this);
			String prompVideo = CommonUtil.getPromptVideoStatu(this);
			if(prompLike.equals("0")){
				videoNotifi.setVisibility(View.GONE);
				//CommonUtil.savePromptVideoStatu(getContext(), "1");
				
			}else if(prompLike.equals("1") && prompVideo.equals("0")){
				
				videoNotifi.setVisibility(View.VISIBLE);
				notifiFrameLayout.setVisibility(View.VISIBLE);
				notifiFrameLayout.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						notifiFrameLayout.setVisibility(View.GONE);
						videoNotifi.setVisibility(View.GONE);
						CommonUtil.savePromptVideoStatu(MyPageActivityA.this, "1");
					}
				});
				/*mHandler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						videoLayout.setVisibility(View.GONE);
					}
				}, 2000);*/
				
			}else if(prompLike.equals("1") && prompVideo.equals("1")){
				
				videoNotifi.setVisibility(View.GONE);
			}
		} //喜欢提示
		else{
			String promp = CommonUtil.getPrompStatu(this);
			String likePromp = CommonUtil.getPromptLikeStatu(this);
			if(promp.equals("0")){
				
				likeNotifi.setVisibility(View.GONE);
				
				
			}else if(promp.equals("1") && likePromp.equals("0")){
				
				likeNotifi.setVisibility(View.VISIBLE);
				notifiFrameLayout.setVisibility(View.VISIBLE);
				notifiFrameLayout.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
						likeNotifi.setVisibility(View.GONE);
						notifiFrameLayout.setVisibility(View.GONE);
						CommonUtil.savePromptLikeStatu(MyPageActivityA.this, "1");
					}
				});
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
				
				likeNotifi.setVisibility(View.GONE);
			}
			//initImageSize(getResources().getDisplayMetrics().densityDpi);
			
		}
		
	}
	
}
