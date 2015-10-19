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
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.Activity.Activity;
import com.cmmobi.icuiniao.Activity.FirstPageActivity;
import com.cmmobi.icuiniao.Activity.LoginAndRegeditActivity;
import com.cmmobi.icuiniao.Activity.SendShockActivity;
import com.cmmobi.icuiniao.ui.view.DialogShock;
import com.cmmobi.icuiniao.ui.view.MainMenu;
import com.cmmobi.icuiniao.util.ActionID;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.DownLoadPool;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.LogoutUtil;
import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.MySoftReference;
import com.cmmobi.icuiniao.util.OfflineLog;
import com.cmmobi.icuiniao.util.SensorUtil;
import com.cmmobi.icuiniao.util.TimerForRock;
import com.cmmobi.icuiniao.util.URLUtil;
import com.cmmobi.icuiniao.util.UserUtil;
import com.icuiniao.plug.im.Entity;
import com.icuiniao.plug.im.IMDataBase;

/**
 * @author hw
 *新摇一摇
 */
public class RockActivityA extends Activity implements AnimationListener{

	private int fromScreen;//0：首页过来，1：单品页过来
	private SensorUtil sensorUtil;
	private boolean isGotoLogin;
	private MainMenu mainMenu;
	private boolean isCanBack;//是否允许返回
	
	private FrameLayout rock;
	private LinearLayout rockpage;
	private Button titlebar_backbutton;
	private Button titlebar_menubutton;
	private Button leftMenu;
	
	private Button loginbtn;//登录按钮
	
	private RelativeLayout rock_givelayout;//赠送外层布局，隐藏时使用
	private Button rock_give_btn;//赠送按钮
	private RelativeLayout rock_birdlayout;//鸟外层布局，隐藏时使用
	private ImageView rock_bird;//鸟
	private RelativeLayout rock_likelooklayout;//喜欢，看过的外层布局，隐藏时使用
	private ImageView rock_likebtn;//喜欢按钮
	private ImageView rock_lookbtn;//看过的按钮
	private TextView rock_text1;//文本1文本
	private RelativeLayout rock_textlayout2;//文本2外层布局，隐藏时使用
	private TextView rock_text2;//文本2文本
	private RelativeLayout rock_infolayout;//倒计时信息框外层布局，隐藏时使用
	private TextView time;//倒计时时间
	private RelativeLayout rock_infolayout1;//倒计时信息框2外层布局，隐藏时使用
	private TextView rock_infotext2;//信息框文本2
	private ImageView rock_icon;//摇一摇图标
	
	private boolean isAnimationOpen;//菜单是否开启
	//消息提醒
	private ImageView messageImageId;
	private IMDataBase imDataBase;
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        imDataBase = new IMDataBase(this);
        fromScreen = getIntent().getExtras().getInt("fromscreen");
        if(UserUtil.userid == -1||UserUtil.userState != 1){//未登录
        	setContentView(R.layout.rock_nologin);
        	loginbtn = (Button)findViewById(R.id.rockbtn);
        	loginbtn.setOnClickListener(loginClickListener);
        }else{
        	setContentView(R.layout.rock_layout_a);
        	rock_givelayout = (RelativeLayout)findViewById(R.id.rock_givelayout);
        	rock_give_btn = (Button)findViewById(R.id.rock_give_btn);
        	rock_birdlayout = (RelativeLayout)findViewById(R.id.rock_birdlayout);
        	rock_bird = (ImageView)findViewById(R.id.rock_bird);
        	rock_likelooklayout = (RelativeLayout)findViewById(R.id.rock_likelooklayout);
        	rock_likebtn = (ImageView)findViewById(R.id.rock_likebtn);
        	rock_lookbtn = (ImageView)findViewById(R.id.rock_lookbtn);
        	rock_text1 = (TextView)findViewById(R.id.rock_text1);
        	rock_textlayout2 = (RelativeLayout)findViewById(R.id.rock_textlayout2);
        	rock_text2 = (TextView)findViewById(R.id.rock_text2);
        	rock_infolayout = (RelativeLayout)findViewById(R.id.rock_infolayout);
        	time = (TextView)findViewById(R.id.time);
        	rock_infolayout1 = (RelativeLayout)findViewById(R.id.rock_infolayout1);
        	rock_infotext2 = (TextView)findViewById(R.id.rock_infotext2);
        	rock_icon = (ImageView)findViewById(R.id.rock_icon);
        	
        	messageImageId = (ImageView)findViewById(R.id.messageImageId);
        	
        	rock_give_btn.setOnClickListener(giveClickListener);
        	rock_likebtn.setOnClickListener(myLikeClickListener);
        	rock_lookbtn.setOnClickListener(myLookClickListener);
        }
        rock = (FrameLayout)findViewById(R.id.rock);
        rockpage = (LinearLayout)findViewById(R.id.rockpage);
        titlebar_backbutton = (Button)findViewById(R.id.titlebar_backbutton);
        titlebar_menubutton = (Button)findViewById(R.id.titlebar_menubutton);
        leftMenu = (Button)findViewById(R.id.titlebar_leftmenu);
        mainMenu = (MainMenu)findViewById(R.id.mainmenu);
        messageImageId = (ImageView)findViewById(R.id.messageImageId);
        sensorUtil = new SensorUtil(this, mHandler);
        rock.setOnTouchListener(rockTouchListener);
        titlebar_backbutton.setOnClickListener(backClickListener);
        titlebar_menubutton.setOnClickListener(menuClickListener);
        leftMenu.setOnClickListener(menuClickListener);
    }
    
    @Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if(sensorUtil != null){
			sensorUtil.unRegeditListener();
		}
		unRegisterReceiver();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(UserUtil.userid == -1||UserUtil.userState != 1){//未登录
        	if(UserUtil.isRemoteLogin){
    			LogoutUtil.showMessagetToLogout(this);
    		}
    	}
		getMessageByUserId();
		changeRockLayout();
		if(sensorUtil != null){
			sensorUtil.regeditListener();
		}
		registerReceiver();
		if(UserUtil.isNewLoginOrExit){
			isCanBack = true;
			finish(1);
		}
	}

	public void finish(int type){
		if(isCanBack){
			Intent intent = new Intent();
			if(fromScreen == 1){
				if(type == 1){//摇一摇成功后返回
					intent.setClass(this, MainPageActivityA.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}
			}
			super.finish();
			if(type == 0){//正常返回
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			}else{//摇一摇成功后返回
				overridePendingTransition(R.anim.push_scale_in, R.anim.push_scale_out);//中心突出
			}
		}else{
			buildExitDialog();
		}
    }
	
	public void finish(){
		if(isGotoLogin){
			isGotoLogin = false;
			super.finish();
		}else{
			finish(0);
		}
	}
	
	private void changeRockLayout(){
		if(UserUtil.userid != -1&&UserUtil.userState == 1){
			if(UserUtil.callOnNum <= 0){
				if(TimerForRock.getLimitTime() <= 0){//没次数，没时间
					isCanBack = false;
					rock_givelayout.setVisibility(View.INVISIBLE);
					rock_birdlayout.setVisibility(View.INVISIBLE);
					rock_likelooklayout.setVisibility(View.VISIBLE);
					rock_text1.setText("主人，您今天没有摇一下机会了哦，");
					rock_textlayout2.setVisibility(View.VISIBLE);
					rock_text2.setText("请明天再来！");
					rock_infolayout.setVisibility(View.INVISIBLE);
					rock_infolayout1.setVisibility(View.VISIBLE);
					rock_infotext2.setText("好友可以借摇一下机会给你哦！");
					rock_icon.setBackgroundResource(R.drawable.rockicon_un);
					titlebar_backbutton.setVisibility(View.INVISIBLE);
				}else{//没次数，有时间
					isCanBack = true;
					rock_givelayout.setVisibility(View.INVISIBLE);
					rock_birdlayout.setVisibility(View.VISIBLE);
					rock_bird.setBackgroundResource(R.drawable.rock_bird);
					rock_likelooklayout.setVisibility(View.INVISIBLE);
					rock_text1.setText("主人，您今天没有摇一下机会了哦，");
					rock_textlayout2.setVisibility(View.VISIBLE);
					rock_text2.setText("请明天再来！");
					rock_infolayout.setVisibility(View.VISIBLE);
					time.setText(""+TimerForRock.getLimitTime());
					rock_infolayout1.setVisibility(View.INVISIBLE);
					rock_icon.setBackgroundResource(R.drawable.rockicon_un);
					titlebar_backbutton.setVisibility(View.VISIBLE);
				}
			}else{
				if(TimerForRock.getLimitTime() <= 0){//有次数，没时间
					isCanBack = false;
					rock_givelayout.setVisibility(View.VISIBLE);
					rock_birdlayout.setVisibility(View.VISIBLE);
					rock_bird.setBackgroundResource(R.drawable.rock_bird);
					rock_likelooklayout.setVisibility(View.INVISIBLE);
					rock_text1.setText("主人，您今天还有"+UserUtil.callOnNum+"次摇一下机会哦！");
					rock_textlayout2.setVisibility(View.INVISIBLE);
					rock_infolayout.setVisibility(View.INVISIBLE);
					rock_infolayout1.setVisibility(View.VISIBLE);
					rock_infotext2.setText("请摇一下找到新商品。");
					rock_icon.setBackgroundResource(R.drawable.rockicon);
					titlebar_backbutton.setVisibility(View.INVISIBLE);
				}else{//有次数，有时间
					isCanBack = true;
					rock_givelayout.setVisibility(View.VISIBLE);
					rock_birdlayout.setVisibility(View.VISIBLE);
					rock_bird.setBackgroundResource(R.drawable.rock_bird);
					rock_likelooklayout.setVisibility(View.INVISIBLE);
					rock_text1.setText("主人，您今天还有"+UserUtil.callOnNum+"次摇一下机会哦！");
					rock_textlayout2.setVisibility(View.INVISIBLE);
					rock_infolayout.setVisibility(View.VISIBLE);
					time.setText(""+TimerForRock.getLimitTime());
					rock_infolayout1.setVisibility(View.INVISIBLE);
					rock_icon.setBackgroundResource(R.drawable.rockicon);
					titlebar_backbutton.setVisibility(View.VISIBLE);
				}
			}
		}else{
			isCanBack = true;
		}
	}
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case MessageID.MESSAGE_SENSOR:
				if(UserUtil.userid == -1||UserUtil.userState != 1){
					isCanBack = true;
					MainPageActivityA.isSensorSuccess = false;
					finish(0);
					break;
				}
				if(UserUtil.callOnNum <= 0){
					if(isCanBack){
						MainPageActivityA.isSensorSuccess = false;
						finish(0);
					}
					break;
				}
				if(!CommonUtil.isNetWorkOpen(RockActivityA.this)){
					if(isCanBack){
						CommonUtil.ShowToast(RockActivityA.this, "网络不给力，请稍后再试");
						MainPageActivityA.isSensorSuccess = false;
						finish(0);
					}
					break;
				}
				
				isCanBack = true;
				rock_bird.setBackgroundResource(R.drawable.rock_birdgif3);
				if(rock_give_btn != null){
					rock_give_btn.setClickable(false);
				}
				
				playRockStart();
				break;
			case MessageID.MESSAGE_SENSOR_STARTROCK_PLAYOVER:
				MainPageActivityA.isSensorSuccess = true;
				if(rock_give_btn != null){
					rock_give_btn.setBackgroundResource(R.drawable.rock_givebtn_0);
					rock_give_btn.setClickable(true);
				}
				finish(1);
				TimerForRock.reset();
				break;
			case 2222:
				changeRockLayout();
				break;
			case 998877:
				//查询消息已读、未读
				long messageId = msg.getData().getLong("messageId");
				int state = imDataBase.getIsReadState(messageId);
				//已读
				if(state == 1){
					mainMenu.setMessageButtonRes(false);
					messageImageId.setVisibility(View.GONE);
				}else if(state == 0 && UserUtil.userState == 1){
					mainMenu.setMessageButtonRes(true);
					messageImageId.setVisibility(View.VISIBLE);
				}else if(state == 0 && UserUtil.userState != 1){
					mainMenu.setMessageButtonRes(false);
					messageImageId.setVisibility(View.GONE);
				}
				else if(state == -1){
					mainMenu.setMessageButtonRes(false);
					messageImageId.setVisibility(View.GONE);
				}
				break;
			}
		}
    	
    };
    
    private MediaPlayer mp_start;
	private void playRockStart(){
		try {
			if(mp_start == null){
				mp_start = MediaPlayer.create(RockActivityA.this, R.raw.rock_start);
			}
			if(!mp_start.isPlaying()){
				mp_start.start();
				mp_start.setOnCompletionListener(new OnCompletionListener() {
					
					@Override
					public void onCompletion(MediaPlayer mp) {
						// TODO Auto-generated method stub
						mp.release();
						mp_start = null;
						isCanBack = true;
						mHandler.sendEmptyMessage(MessageID.MESSAGE_SENSOR_STARTROCK_PLAYOVER);
						LogPrint.Print("webview","rock start completion");
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
			Vibrator vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(ms);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if(ActionID.ACTION_BROADCAST_ROCK_TIME_CHANGE.equals(action)){
				mHandler.sendEmptyMessage(2222);
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
		intentFilter.addAction(ActionID.ACTION_BROADCAST_ROCK_TIME_CHANGE);
		
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
	
	private OnClickListener backClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			finish(0);
		}
	};
	
	private OnClickListener menuClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			gotoMenu();
		}
	};
	
	private OnClickListener giveClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(RockActivityA.this, SendShockActivity.class);
			startActivityForResult(intent, 1001);			
		}
	};
	
	private OnClickListener loginClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			rock.setVisibility(View.INVISIBLE);
			Intent intent11 = new Intent();
			intent11.setClass(RockActivityA.this, LoginAndRegeditActivity.class);
			startActivity(intent11);
			isGotoLogin = true;
			finish();
		}
	};
	
	private OnClickListener myLikeClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent8 = new Intent();
			intent8.putExtra("url", URLUtil.URL_MAINPAGE_LIKE);
			intent8.setClass(RockActivityA.this, StreampageActivity.class);
			startActivity(intent8);
		}
	};
	
	private OnClickListener myLookClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent9 = new Intent();
			intent9.putExtra("url", URLUtil.URL_MAINPAGE_LOOKED);
			intent9.setClass(RockActivityA.this, StreampageActivity.class);
			startActivity(intent9);
		}
	};
	
	private float downX,upX;
	private OnTouchListener rockTouchListener = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				downX = event.getRawX();
				break;
			case MotionEvent.ACTION_MOVE:
				upX = event.getRawX();
				break;
			case MotionEvent.ACTION_UP:
				if(upX - downX > 40){
					downX = 0;
					upX = 0;
					if(isCanBack){
						finish(0);
					}
				}
				break;
			}
			return true;
		}
	};
	
	public void buildExitDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(RockActivityA.this);
		builder.setTitle("确定要退出翠鸟吗?").setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				CommonUtil.isInMessageScreen = false;
				CommonUtil.isNormalInToApp = true;
				DownLoadPool.getInstance(RockActivityA.this).stop();
//				android.os.Process.killProcess(android.os.Process.myPid());
				MySoftReference.getInstance().clear();
				String cameraDirString = CommonUtil.getExtendsCardPath()+"iCuiniao/camera";
				CommonUtil.deleteAll(new File(cameraDirString));
				OfflineLog.writeExit();//写入离线日志
				//关闭所有activity
				Intent intent = new Intent();
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setClass(RockActivityA.this, FirstPageActivity.class);
				intent.putExtra("type", 1);
				startActivity(intent);
				isGotoLogin = true;
				finish();
			}
		}).setNegativeButton("取消", null).show();
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		gotoMenu();
		return true;
	}
	
	private void gotoMenu(){
		if(rockpage != null){
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
	    		rockpage.startAnimation(animation);
	    		OfflineLog.writeMainMenu();//写入离线日志
			}else{
				if(mainMenu != null){
					mainMenu.setIsCanClick(false);
				}
				Animation animation = AnimationUtils.loadAnimation(this, R.anim.left_in);
				animation.setAnimationListener(this);
	    		animation.setFillEnabled(true);
	    		animation.setFillAfter(true);
	    		rockpage.startAnimation(animation);
			}
		}
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub
		if(isAnimationOpen){
			titlebar_backbutton.setVisibility(View.INVISIBLE);
			leftMenu.setVisibility(View.VISIBLE);
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
		// TODO Auto-generated method stub
		if(!isAnimationOpen){
			leftMenu.setVisibility(View.GONE);
			if(!TimerForRock.isNoTime()){
				titlebar_backbutton.setVisibility(View.VISIBLE);
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
		return super.dispatchTouchEvent(ev);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(data == null){
			return;
		}
		String msg = data.getExtras().getString("msg");
		if (!msg.equals("")) {
			Dialog dialog = new DialogShock(RockActivityA.this,
					R.style.dialogshock, msg);
			dialog.show();
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
				messageImageId.setVisibility(View.VISIBLE);
			}else{
				mainMenu.setMessageButtonRes(false);
				messageImageId.setVisibility(View.GONE);
			}
			
		}else{
			mainMenu.setMessageButtonRes(false);
			messageImageId.setVisibility(View.GONE);
		}
	}
}
