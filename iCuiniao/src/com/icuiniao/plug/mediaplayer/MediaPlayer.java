//package com.icuiniao.plug.mediaplayer;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Timer;
//import java.util.TimerTask;
//
//import org.json.JSONObject;
//
//import com.cmmobi.icuiniao.R;
//import com.cmmobi.icuiniao.Activity.LoginAndRegeditActivity;
//import com.cmmobi.icuiniao.Activity.ShareActivity;
//import com.cmmobi.icuiniao.Activity.ShareEditActivity;
//import com.cmmobi.icuiniao.Activity.WebViewLoginActivity;
//import com.cmmobi.icuiniao.onlineEngine.activity.WebviewActivity;
//import com.cmmobi.icuiniao.util.ActionID;
//import com.cmmobi.icuiniao.util.CommonUtil;
//import com.cmmobi.icuiniao.util.ConnectUtil;
//import com.cmmobi.icuiniao.util.LogPrint;
//import com.cmmobi.icuiniao.util.MessageID;
//import com.cmmobi.icuiniao.util.OfflineLog;
//import com.cmmobi.icuiniao.util.SystemProgress;
//import com.cmmobi.icuiniao.util.URLUtil;
//import com.cmmobi.icuiniao.util.UserUtil;
//import com.cmmobi.icuiniao.util.WXAppID;
//import com.cmmobi.icuiniao.util.connction.HttpThread;
//import com.tencent.mm.sdk.openapi.IWXAPI;
//import com.tencent.mm.sdk.openapi.SendMessageToWX;
//import com.tencent.mm.sdk.openapi.WXAPIFactory;
//import com.tencent.mm.sdk.openapi.WXMediaMessage;
//import com.tencent.mm.sdk.openapi.WXVideoObject;
//import com.tencent.mm.sdk.openapi.WXWebpageObject;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Bitmap.CompressFormat;
//import android.content.IntentFilter;
//import android.graphics.drawable.ColorDrawable;
//import android.media.AudioManager;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.view.SurfaceHolder;
//import android.view.SurfaceView;
//import android.view.View;
//import android.view.Window;
//import android.view.WindowManager;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup.LayoutParams;
//import android.view.animation.Animation;
//import android.view.animation.AnimationUtils;
//import android.view.animation.Animation.AnimationListener;
//import android.widget.AdapterView;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.ListView;
//import android.widget.ProgressBar;
//import android.widget.RelativeLayout;
//import android.widget.SeekBar;
//import android.widget.SimpleAdapter;
//import android.widget.TextView;
//import android.widget.Toast;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.SeekBar.OnSeekBarChangeListener;
//
//public class MediaPlayer extends Activity implements SurfaceHolder.Callback{
//	
//	private RelativeLayout rctrlscreen;
//	private SurfaceView surfaceView;
//	private TextView titlebar_titletext;
//	private Button titlebar_backbutton;
//	private Button titlebar_shareButton;
//	private Button media_likebtn;
//	private Button media_prebtn;
//	private Button media_playbtn;
//	private Button media_nextbtn;
//	private Button media_lookbtn;
//	private TextView curtime;
//	private TextView maxtime;
//	private SeekBar seekBar;
//	private PopNumView popNumView;
//	private RelativeLayout media_share;
//	private ListView media_sharelist;
//	private Handler screenHandler;
//	private SurfaceHolder holder;
//	private android.media.MediaPlayer mediaPlayer;
//	private boolean isPlay;//播放true,暂停false
//	private int mProgress;//进度百分比
//	
//	private String playUrl;//视频播放地址
//	private String titleString;
//	private String commodityInfoString;
//	private String commodityImageString;
//	private int commodityid;
//	private String wapUrl;
//	private boolean likeState;
//	private int deleteId;
//	private boolean isTitleAnimationFinish;
//	private boolean isBottomAnimationFinish;
//	private boolean isSurfaceDestory;
//	private int saveCurPos;//记录当前播放位置
//	public final static int[] itemImages = {R.drawable.listitem_weibo,R.drawable.listitem_qq,R.drawable.listitem_wx,R.drawable.listitem_sms,R.drawable.listitem_email};
//	
//	private ProgressBar loadingBar;
//	private boolean isPrepare;//是否准备完成了
//	private boolean isPlayOver;//是否播放完成
//	private boolean isBufferPrepare;//缓冲是否准备好了
//	// IWXAPI 是第三方app和微信通信的openapi接口
//    private IWXAPI api;
//    private byte[] wxByte;
//	//头部菜单布局
//	private RelativeLayout relativeLayout_title;
//	//播放控制器布局
//	private RelativeLayout relativeLayout_bottom;
//	//是否全屏
//	private boolean isScreen = false;
//	private boolean flag = false;
//	private boolean isRegisterBroadcast = false;
//	private Timer mTimer=new Timer();
//	private TimerTask mTimerTask = new TimerTask() {
//
//		@Override
//		public void run() {
//			// TODO Auto-generated method stub
//			if(mediaPlayer != null){
//				if(isPrepare&&isBufferPrepare&&mediaPlayer.isPlaying()){
//					mHandler.sendEmptyMessage(MessageID.MESSAGE_MEDIA_CURRENTTIME);
//				}
//			}
//		}
//		
//	};
//	
//	private boolean isOnPause;
//	//手动跳转至其他界面，置为true;(分享，登录，看看)
//	public static boolean pauseing;
//	
//    /** Called when the activity is first created. */
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setFullScreenMold();
//        setContentView(R.layout.mediaplayer);
//        String deviceNameString = CommonUtil.getDeviceName();
//        if(deviceNameString != null&&deviceNameString.toLowerCase().indexOf("i8150") >= 0){
//        	CommonUtil.ShowToast(this, "抱歉，此设备无法播放此视频");
//        	finish();
//        }  
//        //初始化头部菜单布局
//        relativeLayout_title = (RelativeLayout)findViewById(R.id.relativeLayout_title);
//        //初始化播放控制器布局
//        relativeLayout_bottom = (RelativeLayout)findViewById(R.id.relativeLayout_bottom);
//        isSurfaceDestory = false;
//        saveCurPos = 0;
//        isPlayOver = false;
//        isPrepare = false;
//        isBufferPrepare = false;
//        playUrl = getIntent().getExtras().getString("url");
//        titleString = getIntent().getExtras().getString("title");
//        commodityInfoString = getIntent().getExtras().getString("commodityInfoString");
//        commodityImageString = getIntent().getExtras().getString("image");
//        commodityid = getIntent().getExtras().getInt("commodityid");
//        wapUrl = getIntent().getExtras().getString("wapurl");
//        likeState = getIntent().getExtras().getBoolean("likestate");
//        deleteId = getIntent().getExtras().getInt("deleteid");
//        rctrlscreen = (RelativeLayout)findViewById(R.id.rctrlscreen);
//        surfaceView = (SurfaceView)findViewById(R.id.surfaceView);
//        loadingBar = (ProgressBar)findViewById(R.id.loading);
//        addProgress();
//        titlebar_titletext = (TextView)findViewById(R.id.titlebar_titletext);
//        titlebar_backbutton = (Button)findViewById(R.id.titlebar_backbutton);
//        titlebar_shareButton = (Button)findViewById(R.id.titlebar_menubutton);
//        media_likebtn = (Button)findViewById(R.id.media_likebtn);
//        if(likeState){
//        	media_likebtn.setBackgroundResource(R.drawable.media_likebtn_f);
//        }
//        media_prebtn = (Button)findViewById(R.id.media_prebtn);
//        media_playbtn = (Button)findViewById(R.id.media_playbtn);
//        media_nextbtn = (Button)findViewById(R.id.media_nextbtn);
//        media_lookbtn = (Button)findViewById(R.id.media_lookbtn);
//        curtime = (TextView)findViewById(R.id.curtime);
//        maxtime = (TextView)findViewById(R.id.maxtime);
//        seekBar = (SeekBar)findViewById(R.id.seekbar);
//        popNumView = (PopNumView)findViewById(R.id.popnumview);
//        popNumView.initImageWH();
//        media_share = (RelativeLayout)findViewById(R.id.media_share);
//        media_sharelist = (ListView)findViewById(R.id.media_sharelist);
//        SimpleAdapter adapter = new SimpleAdapter(this, getData(), R.layout.media_shareitem, new String[]{"item"}, new int[]{R.id.media_shareitemimg});
//        media_sharelist.setAdapter(adapter);
//        media_sharelist.setOnItemClickListener(listener);
//        titlebar_titletext.setText(titleString);
//        
//        changeScreenMode(false);
//        
//        surfaceView.setOnClickListener(surfaceClickListener);
//        titlebar_backbutton.setOnClickListener(backClickListener);
//        titlebar_shareButton.setOnClickListener(shareClickListener);
//        media_likebtn.setOnClickListener(likeClickListener);
//        media_prebtn.setOnClickListener(preClickListener);
//        media_playbtn.setOnClickListener(playClickListener);
//        media_nextbtn.setOnClickListener(nextClickListener);
//        media_lookbtn.setOnClickListener(lookClickListener);
//        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);
//        
//        holder = surfaceView.getHolder();
////        holder.setFixedSize(CommonUtil.screen_height, CommonUtil.screen_width);
//        holder.addCallback(this);
//        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//        
//        if(mTimer != null&&mTimerTask != null){
//        	mTimer.schedule(mTimerTask, 0, 500);
//        }
//    }
//    
//    @Override
//	protected void onPause() {
//		super.onPause();
////		pauseing = true;
//		LogPrint.Print("webview","Meida onPause");
//		if(mHandler != null){
//			mHandler.removeCallbacks(cloassRunnable);
//		}
//		Intent broadcastIntent = new Intent(ActionID.ACTION_BROADCAST_MEDIAPLAYER_FINISH);
//		sendBroadcast(broadcastIntent);		
//		if(!flag){
//			isOnPause = true;
//			pauseMedia();
//		}
//	}
//	@Override
//	protected void onDestroy() {
//		// TODO Auto-generated method stub
//		super.onDestroy();
//		if(mHandler != null){
//			mHandler.removeCallbacks(cloassRunnable);
//		}
//		Intent broadcastIntent = new Intent(ActionID.ACTION_BROADCAST_MEDIAPLAYER_FINISH);
//		sendBroadcast(broadcastIntent);	
//		//销毁广播
//		unRegisterReceiver();
//		pauseing = true;
//	}
//	@Override
//	protected void onResume() {
//		isScreen = false;
//		pauseing = false;
//		animtionIn();	
//		super.onResume();
//		//注册广播
//		registerReceiver();
//		isOnPause = false;
//		LogPrint.Print("webview","onResume");
//	}
//
//	public void finish(){
//    	if(mediaPlayer != null){
//    		mHandler.removeMessages(MessageID.MESSAGE_MEDIA_CURRENTTIME);
//    		if(mTimer != null){
//    			mTimer.cancel();
//    			mTimerTask = null;
//    			mTimer = null;
//    		}
//    		mediaPlayer.stop();
//    		mediaPlayer.release();
//    	}
//    	super.finish();
//    }
//    
//    private List<Map<String, Object>> getData() {
//		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//		
//		for(int i = 0;i < itemImages.length;i ++){
//			Map<String, Object> map = new HashMap<String, Object>();
//			map.put("item", itemImages[i]);
//			list.add(map);
//		}
//		
//		return list;
//	}
//    
//    private void init(){
//    	if(isSurfaceDestory){
//    		addProgress();
//    	}
//    	mediaPlayer = new android.media.MediaPlayer();
//    	mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//    	mediaPlayer.setDisplay(holder);
//    	try {
//    		mediaPlayer.setDataSource(this, Uri.parse(playUrl));
//    		mediaPlayer.setOnPreparedListener(onPreparedListener);
//    		mediaPlayer.setOnBufferingUpdateListener(onBufferingUpdateListener);
//    		mediaPlayer.setOnCompletionListener(onCompletionListener);
//    		mediaPlayer.setScreenOnWhilePlaying(true);
//    		mediaPlayer.prepareAsync();
//		} catch (Exception e) {
//			closeProgress();
//			changePlayRes(false);
//			e.printStackTrace();
//		}
//    }
//    
//    private void changePlayRes(boolean b){
//    	isPlay = b;
//    	if(isPlay){
//    		media_playbtn.setBackgroundResource(R.drawable.media_pausebtn_0);
//    	}else{
//    		media_playbtn.setBackgroundResource(R.drawable.media_playbtn_0);
//    	}
//    }
//    
//    private void changeScreenMode(boolean b){
//    	if(b){
//			rctrlscreen.setVisibility(View.INVISIBLE);
//		}else{
//			rctrlscreen.setVisibility(View.VISIBLE);
//		}
//    }
//    
//    //已小时:分:秒的方式展示时间
//    private String getTime(int time){
//    	String result = "";
//    	if(time <= 0)return "00:00";
//    	
//    	int h = time/1000/3600;
//    	int m = (time/1000/60)%60;
//    	int s = (time/1000)%60;
//    	String sh = h>9?(""+h):("0"+h);
//    	String sm = m>9?(""+m):("0"+m);
//    	String ss = s>9?(""+s):("0"+s);
//    	
//    	if(h > 0){
//    		result = sh+":"+sm+":"+ss;
//    	}else{
//    		result = sm+":"+ss;
//    	}
//    	
//    	return result;
//    }
//    
//    private OnClickListener backClickListener = new OnClickListener() {
//		
//		@Override
//		public void onClick(View v) {
//			// TODO Auto-generated method stub
//			finish();
//		}
//	};
//	
//	private OnClickListener shareClickListener = new OnClickListener() {
//		
//		@Override
//		public void onClick(View v) {
//			// TODO Auto-generated method stub
//			if(UserUtil.userid == -1||UserUtil.userState != 1){//未登陆
//				pauseMedia();				
//				shareLikeIfNoLogin("请您登录后分享");				
//				return; 
//			}
//			if(media_share.isShown()){
//				media_share.setVisibility(View.INVISIBLE);
//				titlebar_shareButton.setBackgroundResource(R.drawable.media_sharebtn);
//			}else{
//				media_share.setVisibility(View.VISIBLE);
//				//lyb 让分享列表到上层
//				media_share.bringToFront();
//				titlebar_shareButton.setBackgroundResource(R.drawable.media_sharebtn_f);
//			}
//		}
//	};
//	
//	private void shareLikeIfNoLogin(String str) {
//		// 分享的登录判断
//		AlertDialog.Builder builder = new AlertDialog.Builder(this);
//		builder.setMessage(str).setPositiveButton("登录",
//				new DialogInterface.OnClickListener() {
//
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						CommonUtil.ShowToast(MediaPlayer.this,
//								"请登陆或者注册，变身为小C的主人！");
//						mHandler.removeCallbacks(cloassRunnable);
//						pauseing = true;
//						Intent intent11 = new Intent();
//						intent11.setClass(MediaPlayer.this,
//								LoginAndRegeditActivity.class);						
//						MediaPlayer.this.startActivity(intent11);						
//
//					}
//				}).setNegativeButton("取消", null).show();
//
//	}
//	
//	private OnClickListener likeClickListener = new OnClickListener() {
//		
//		@Override
//		public void onClick(View v) {
//			// TODO Auto-generated method stub
//			if(UserUtil.userid != -1&&UserUtil.userState == 1){
//				if(likeState){//删除
//					new ConnectUtil(MediaPlayer.this, mHandler,0).connect(URLUtil.URL_MYLIKE_DELETE_SINGLE+"?id="+deleteId+"&oid="+UserUtil.userid+"&cid="+commodityid, HttpThread.TYPE_PAGE, 4);
//				}else{//添加
//					new ConnectUtil(MediaPlayer.this, mHandler,0).connect(URLUtil.URL_ADDLIKE+"?oid="+UserUtil.userid+"&cid="+commodityid, HttpThread.TYPE_PAGE, 5);
//				}
//			}else{
//				pauseMedia();
//				shareLikeIfNoLogin("登录后才能喜欢");
////				CommonUtil.ShowToast(MediaPlayer.this, "请登陆或者注册，变身为小C的主人！");
////				Intent intent11 = new Intent();
////				intent11.setClass(MediaPlayer.this, LoginAndRegeditActivity.class);
////				startActivity(intent11);
//			}
//		}
//	};
//	
//	private OnClickListener preClickListener = new OnClickListener() {
//		
//		@Override
//		public void onClick(View v) {
//			// TODO Auto-generated method stub
//			if(isPrepare&&isBufferPrepare){
//				mHandler.sendEmptyMessage(MessageID.MESSAGE_MEDIA_PRE);
//			}else{
//				CommonUtil.ShowToast(MediaPlayer.this, "努力加载中", false);
//			}
//		}
//	};
//	
//	private OnClickListener playClickListener = new OnClickListener() {
//		
//		@Override
//		public void onClick(View v) {
//			// TODO Auto-generated method stub
//			if(isPrepare&&isBufferPrepare){
//				mHandler.sendEmptyMessage(MessageID.MESSAGE_MEDIA_PLAY);
//			}else{
//				CommonUtil.ShowToast(MediaPlayer.this, "努力加载中", false);
//			}
//		}
//	};
//	
//	private OnClickListener nextClickListener = new OnClickListener() {
//		
//		@Override
//		public void onClick(View v) {
//			// TODO Auto-generated method stub
//			if(isPrepare&&isBufferPrepare){
//				mHandler.sendEmptyMessage(MessageID.MESSAGE_MEDIA_NEXT);
//			}else{
//				CommonUtil.ShowToast(MediaPlayer.this, "努力加载中", false);
//			}
//		}
//	};
//	
//	private OnClickListener lookClickListener = new OnClickListener() {
//		
//		@Override
//		public void onClick(View v) {
//			// TODO Auto-generated method stub
//			if(wapUrl != null){
//				pauseMedia();
//				//lyb
//				pauseing = true;
//				mHandler.removeCallbacks(cloassRunnable);
//				//
//				Intent intent = new Intent();
//				intent.setClass(MediaPlayer.this, WebviewActivity.class);
//				LogPrint.Print("==============kankan = "+wapUrl);
//				intent.putExtra("url",wapUrl);
//				intent.putExtra("title", titleString);
//				intent.putExtra("commodityid", commodityid);
//				intent.putExtra("commodityImageString", commodityImageString);
//				intent.putExtra("commodityInfoString", commodityInfoString);
//				startActivity(intent);
//			}
//		}
//	};
//	
//	private OnClickListener surfaceClickListener = new OnClickListener() {
//		
//		@Override
//		public void onClick(View v) {
//			if(isTitleAnimationFinish&&isBottomAnimationFinish){
//				mHandler.removeCallbacks(cloassRunnable);
//				flag = true;
//				isScreen = true;
//				animtionOut();
//				Intent intent = new Intent(MediaPlayer.this,MediaPlayerGestureActivity.class);
//				startActivity(intent);
//				titlebar_shareButton.setBackgroundResource(R.drawable.media_sharebtn);
//			}
//		}
//	};
//	
//	private OnSeekBarChangeListener seekBarChangeListener = new OnSeekBarChangeListener() {
//		
//		@Override
//		public void onStopTrackingTouch(SeekBar seekBar) {
//			// TODO Auto-generated method stub
//			if(isPrepare&&isBufferPrepare){
//				mHandler.sendEmptyMessage(MessageID.MESSAGE_MEDIA_SEEK);
//				popNumView.hide(true);
//			}else{
//				CommonUtil.ShowToast(MediaPlayer.this, "努力加载中", false);
//			}
//		}
//		
//		@Override
//		public void onStartTrackingTouch(SeekBar seekBar) {
//			// TODO Auto-generated method stub
//			if(!isPrepare||!isBufferPrepare)return;
//			popNumView.hide(false);
//			popNumView.setNum(getTime(mediaPlayer.getCurrentPosition()));
//		}
//		
//		@Override
//		public void onProgressChanged(SeekBar seekBar, int progress,
//				boolean fromUser) {
//			// TODO Auto-generated method stub
//			if(!isPrepare||!isBufferPrepare)return;
//			mProgress = progress;
//			int pos = mediaPlayer.getDuration()/100*mProgress;
//			setCurTime(pos);
//			popNumView.setNum(getTime(pos));
//		}
//	};
//	
//	private android.media.MediaPlayer.OnPreparedListener onPreparedListener = new android.media.MediaPlayer.OnPreparedListener(){
//
//		@Override
//		public void onPrepared(android.media.MediaPlayer mp) {
//			// TODO Auto-generated method stub
//			if(isOnPause)return;
//			LogPrint.Print("webview","onprepared");
//			isPrepare = true;
//			isBufferPrepare = false;
//	    	setScreenSize();
//			if(isSurfaceDestory){
//				mediaPlayer.start();
//				changePlayRes(true);
//				setCurTime(saveCurPos);
//				setMaxTime(mediaPlayer.getDuration());
//				mediaPlayer.seekTo(saveCurPos);
//				surfaceView.postInvalidate();
//				mHandler.sendEmptyMessage(MessageID.MESSAGE_MEDIA_CURRENTTIME);
//				isSurfaceDestory = false;
//				isScreen = true;
//				flag = true;
//				mHandler.sendEmptyMessageDelayed(MessageID.MESSAGE_MEDIA_CLOSE, 5000);
//			}else{
//				mediaPlayer.start();
//				changePlayRes(true);
//				setCurTime(0);
//				setMaxTime(mediaPlayer.getDuration());
//				if(isPlayOver){
//					isPlayOver = false;
//					changePlayRes(false);
//					changeScreenMode(false);
//					mediaPlayer.pause();
//				}
//				surfaceView.postInvalidate();
//				mHandler.sendEmptyMessage(MessageID.MESSAGE_MEDIA_CURRENTTIME);
//				isScreen = true;
//				flag = true;
//				mHandler.postDelayed(cloassRunnable, 5000);
//			}
//		}
//	};
//	
//	private android.media.MediaPlayer.OnBufferingUpdateListener onBufferingUpdateListener = new android.media.MediaPlayer.OnBufferingUpdateListener(){
//
//		@Override
//		public void onBufferingUpdate(android.media.MediaPlayer mp, int percent) {
//			// TODO Auto-generated method stub
//			if(!isBufferPrepare&&isPrepare){
//				LogPrint.Print("webview","percent = "+percent);
//				if(percent >=5){
//					isBufferPrepare = true;
//					closeProgress();
//				}
//			}
//			seekBar.setSecondaryProgress(percent);
//		}
//	};
//	
//	private android.media.MediaPlayer.OnCompletionListener onCompletionListener = new android.media.MediaPlayer.OnCompletionListener(){
//
//		@Override
//		public void onCompletion(android.media.MediaPlayer mp) {
//			// TODO Auto-generated method stub
//			if(!isPlayOver&&isPrepare){
//				isPlayOver = true;
//				LogPrint.Print("webview","onCompletion");
//				mHandler.sendEmptyMessage(100010);
//				
//			}
//		}};
//		
//    private Handler mHandler = new Handler(){
//
//		@Override
//		public void handleMessage(Message msg) {
//			// TODO Auto-generated method stub
//			super.handleMessage(msg);
//			switch (msg.what) {
//			case MessageID.MESSAGE_CONNECT_START:
//				addProgress();
//				break;
//			case MessageID.MESSAGE_CONNECT_ERROR:
//				closeProgress();
//				CommonUtil.ShowToast(MediaPlayer.this, (String)msg.obj);
//				break;
//			case MessageID.MESSAGE_CONNECT_DOWNLOADOVER:
//				closeProgress();
//				if("text/json".equals(msg.getData().getString("content_type"))){
//					Json((byte[])msg.obj,msg.arg1);
//				}else{
//					
//					wxSend((byte[])msg.obj);
//				}
//				break;
//			case MessageID.MESSAGE_MEDIA_INIT:
//				init();
//				break;
//			case MessageID.MESSAGE_MEDIA_PLAY:
//				isPlay = !isPlay;
//				changePlayRes(isPlay);
//				try {
//					if(isPlay){
//						if(mediaPlayer != null){
//							mediaPlayer.start();
//							mHandler.sendEmptyMessage(MessageID.MESSAGE_MEDIA_CURRENTTIME);
//						}
//					}else{
//						if(mediaPlayer != null){
//							mediaPlayer.pause();
//							mHandler.removeMessages(MessageID.MESSAGE_MEDIA_CURRENTTIME);
//						}
//					}
//				} catch (Exception e) {
//					// TODO: handle exception
//					changePlayRes(false);
//				}
//				break;
//			case MessageID.MESSAGE_MEDIA_PRE:
//				try {
//					int timepos = mediaPlayer.getCurrentPosition()-5000;
//					int max = mediaPlayer.getDuration();
//					if(timepos <= 0){
//						timepos = 0;
//					}
//					mediaPlayer.seekTo(timepos);
//					setCurTime(timepos);
//					float pec = 0;
//					if(max != 0){
//						pec = (float)(timepos*100/max);
//					}
//					seekBar.setProgress((int)pec);
//					
//				} catch (Exception e) {
//					// TODO: handle exception
//				}
//				break;
//			case MessageID.MESSAGE_MEDIA_NEXT:
//				try {
//					int timepos = mediaPlayer.getCurrentPosition()+5000;
//					int max = mediaPlayer.getDuration();
//					if(timepos >= max){
//						timepos = max;
//					}
//					mediaPlayer.seekTo(timepos);
//					setCurTime(timepos);
//					float pec = 0;
//					if(max != 0){
//						pec = (float)(timepos*100/max);
//					}
//					seekBar.setProgress((int)pec);
//				} catch (Exception e) {
//					// TODO: handle exception
//				}
//				break;
//			case MessageID.MESSAGE_MEDIA_CURRENTTIME:
//				try {
//					surfaceView.postInvalidate();
//					setCurTime(mediaPlayer.getCurrentPosition());
//					int max = mediaPlayer.getDuration();
//					float pec = 0;
//					if(max != 0){
//						pec = (float)(mediaPlayer.getCurrentPosition()*100/max);
//					}
//					seekBar.setProgress((int)pec);
//				} catch (Exception e) {
//					// TODO: handle exception
//				}
////				//1秒后继续获取
////				mHandler.sendEmptyMessageDelayed(MessageID.MESSAGE_MEDIA_CURRENTTIME, 1000);
//				break;
//			case MessageID.MESSAGE_MEDIA_SEEK:
//				try {
//					int pos = mediaPlayer.getDuration()/100*mProgress;
//					mediaPlayer.seekTo(pos);
//					setCurTime(pos);
//				} catch (Exception e) {
//					// TODO: handle exception
//				}
//				break;
//			case 100005://邮件分享
//				shareEmail("翠鸟分享:"+titleString,commodityInfoString+"\n\n"+msg.getData().getString("url")+"\n\n(分享自 翠鸟客户端)\n\n"+"【客户端下载地址】"+URLUtil.URL_APPDOWNLOAD+"mail", null);
//				break;
//			case 100006://短信分享
//				shareSms("翠鸟分享:"+titleString+"\n\n去看看 "+msg.getData().getString("url")+"\n\n(分享自 翠鸟客户端)\n\n"+"【客户端下载地址】"+URLUtil.URL_APPDOWNLOAD+"love");
//				break;
//			case 100007:
//				CommonUtil.ShowToast(MediaPlayer.this, "失败了!");
//				break;
//			case 100008://删除喜欢
//				likeState = false;
//				media_likebtn.setBackgroundResource(R.drawable.media_likebtn);
//				break;
//			case 100009://添加喜欢
//				likeState = true;
//				media_likebtn.setBackgroundResource(R.drawable.media_likebtn_f);
//				break;
//			case 100010://视频播放完成
//				setCurTime(mediaPlayer.getDuration());
//				seekBar.setProgress(100);
//				surfaceView.postInvalidate();
//				mHandler.sendEmptyMessageDelayed(100011,200);
//				break;
//			case 100011://视频播放完成后的处理
//				isPrepare = false;
//				isBufferPrepare = false;
//				mediaPlayer.stop();
//				mediaPlayer.release();
//				mediaPlayer = null;
//				addProgress();	
//				//用于数据广播
//				Intent broadcastIntent = new Intent(ActionID.ACTION_BROADCAST_MEDIAPLAYER_FINISH);
//				sendBroadcast(broadcastIntent);
//				init();
//				break;
//			//处理视频播放5秒后关闭头部和播控控制器
//			case MessageID.MESSAGE_MEDIA_CLOSE:
//				animtionOut();
//				Intent intent = new Intent(MediaPlayer.this,MediaPlayerGestureActivity.class);
//				startActivity(intent);
//				break;
//			}
//		}
//    	
//    };
//    
//	public void addProgress(){
//		if(loadingBar != null){
//			loadingBar.setVisibility(View.VISIBLE);
//		}
//	}
//	
//	public void closeProgress(){
//		if(loadingBar != null){
//			loadingBar.setVisibility(View.INVISIBLE);
//		}
//	}
//	
//	private OnItemClickListener listener = new OnItemClickListener() {
//		
//		@Override
//		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//				long arg3) {
//			// TODO Auto-generated method stub
//			pauseMedia();
//			mHandler.removeCallbacks(cloassRunnable);
//			pauseing = true;
//			switch (arg2) {
//			case 0://新浪微博
//				LogPrint.Print("shareItem = weibo");
//				Intent intent = new Intent();
//				intent.putExtra("title", titleString);
//				intent.putExtra("image", commodityImageString);
//				intent.putExtra("commodityInfoString", commodityInfoString);
//				intent.putExtra("commodityid", commodityid);
//				intent.putExtra("logintype", 2);
//				intent.setClass(MediaPlayer.this, ShareEditActivity.class);
//				startActivity(intent);
//				break;
//			case 1://qq空间
//				LogPrint.Print("shareItem = qq");
//				Intent qqIntent = new Intent();
//				qqIntent.putExtra("title", titleString);
//				qqIntent.putExtra("image", commodityImageString);
//				qqIntent.putExtra("commodityInfoString", commodityInfoString);
//				qqIntent.putExtra("commodityid", commodityid);
//				qqIntent.putExtra("logintype", 1);
//				qqIntent.setClass(MediaPlayer.this, ShareEditActivity.class);
//				startActivity(qqIntent);
//				break;
//			case 2://微信
//				LogPrint.Print("shareItem = wx");
//				if(CommonUtil.isNetWorkOpen(MediaPlayer.this)){
//				// 通过WXAPIFactory工厂，获取IWXAPI的实例
//				api = WXAPIFactory.createWXAPI(MediaPlayer.this, WXAppID.APP_ID, false);
//				//注册微信
//				api.registerApp(WXAppID.APP_ID);
//				//检查是否安装微信
//		    	if(!api.isWXAppInstalled()){
//		    		//提示用户安装微信
//		    		Toast.makeText(MediaPlayer.this, "未检测到微信终端", Toast.LENGTH_SHORT).show();
//		    		
//		    	}else{
//		    		
//					//判断缓存是否有图片
//					if(CommonUtil.exists(CommonUtil.dir_cache+"/"+CommonUtil.urlToNum(commodityImageString))){
//						
//						//获取图片
//						wxByte = CommonUtil.getSDCardFileByteArray(CommonUtil.dir_cache+"/"+CommonUtil.urlToNum(commodityImageString));
//						wxSend(wxByte);
//					}else{
//						new ConnectUtil(MediaPlayer.this, mHandler, 0).connect(commodityImageString, HttpThread.TYPE_IMAGE, 10);
//					}
//		    	}
//				}else{
//					
//					CommonUtil.ShowToast(MediaPlayer.this, "杯具了- -!\n联网不给力啊");
//				}
//				break;
//			case 3://短信
//				LogPrint.Print("shareItem = sms");
//				new ConnectUtil(MediaPlayer.this, mHandler, 0).connect(URLUtil.URL_GET_SHORTLINK+"?type=6"+"&oid="+UserUtil.userid+"&cid="+commodityid, HttpThread.TYPE_PAGE, 0);
////				new ConnectUtil(MediaPlayer.this, mHandler, 0).connect(URLUtil.URL_SHARE_UPLAOD+"?logintype=12"+"&oid="+UserUtil.userid+"&cid="+commodityid, HttpThread.TYPE_PAGE, 0);
//				break;
//			case 4://邮件
//				LogPrint.Print("shareItem = email");
//				new ConnectUtil(MediaPlayer.this, mHandler, 0).connect(URLUtil.URL_GET_SHORTLINK+"?type=5"+"&oid="+UserUtil.userid+"&cid="+commodityid, HttpThread.TYPE_PAGE, 1);
////				new ConnectUtil(MediaPlayer.this, mHandler, 0).connect(URLUtil.URL_SHARE_UPLAOD+"?logintype=11"+"&oid="+UserUtil.userid+"&cid="+commodityid, HttpThread.TYPE_PAGE, 0);
//				break;
//			}
//		}
//		
//	};
//	
//	private void Json(byte[] data,int threadindex){
//		try {
//			String str = new String(data,"UTF-8");
//			str = CommonUtil.formUrlEncode(str);
//			LogPrint.Print("media json = "+str);
//			JSONObject jObject = new JSONObject(str);
//			String result = jObject.getString("result");
//			if(result != null){
//				if(threadindex >= 4){
//					if(result.equalsIgnoreCase("true")){
//						String likeNum = jObject.getString("likenum");
//						if(threadindex == 4){//删除
//							mHandler.sendEmptyMessage(100008);
//						}else if(threadindex == 5){//添加
//							mHandler.sendEmptyMessage(100009);
//						}
//					}else{
//						CommonUtil.ShowToast(MediaPlayer.this, "失败了!");
//					}
//				}else{
//					if(result.equalsIgnoreCase("true")){
//						String url = jObject.getString("url");
//						Bundle bundle = new Bundle();
//						bundle.putString("url", url);
//						Message message = new Message();
//						if(threadindex == 0){//短信
//							message.what = 100006;
//							message.setData(bundle);
//							mHandler.sendMessage(message);
//						}else{//邮件
//							message.what = 100005;
//							message.setData(bundle);
//							mHandler.sendMessage(message);
//						}
//					}else{
//						mHandler.sendEmptyMessage(100007);
//					}
//				}
//			}
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//	}
//	
//	public void shareSms(String message){
//		Uri smsToUri = Uri.parse("smsto:");
//		Intent smsIntent = new Intent(Intent.ACTION_SENDTO,smsToUri);
//		smsIntent.putExtra("sms_body", message);
//		startActivity(smsIntent);
//		OfflineLog.writeShareSms(commodityid);//写入离线日志
//	}
//	
//	public void shareEmail(String title,String message,String imgDir){
//		try {
//			if(CommonUtil.isNetWorkOpen(this)){
//				Intent emailIntent = new Intent(Intent.ACTION_SEND);
//				emailIntent.putExtra(Intent.EXTRA_SUBJECT, title);
//				emailIntent.putExtra(Intent.EXTRA_TEXT, message);
//				if(imgDir != null){
//					emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(imgDir));
//				}
//				emailIntent.setType("plain/text");
//				startActivity(emailIntent);
//				OfflineLog.writeShareEmail(commodityid);//写入离线日志
//			}else{
//				CommonUtil.ShowToast(this, "主人，邮件发送失败了");
//			}
//		} catch (Exception e) {
//			CommonUtil.ShowToast(MediaPlayer.this, "主人，邮件罢工了，呼叫失败！");
//		}
//	}
//	
//	public String addUrlParam(String url,int logintype){
//  		if(URLUtil.IsLocalUrl()){
//			return url;
//		}
//  		return url+"?pid="+commodityid+"&oid="+UserUtil.userid+"&dpi="+URLUtil.dpi()+"&logintype="+logintype;
//  	}
//	
//	private void pauseMedia(){
//		//播放暂停
//		isPlay = false;
//		changePlayRes(isPlay);
//		try {
//			if(mediaPlayer != null){
//				mediaPlayer.pause();
//				mHandler.removeMessages(MessageID.MESSAGE_MEDIA_CURRENTTIME);
//			}
//		} catch (Exception e) {
//			// TODO: handle exception
//			changePlayRes(false);
//		}
//	}
//    
//    protected void setFullScreenMold(){
//    	//隐藏标题
//    	requestWindowFeature(Window.FEATURE_NO_TITLE);
//    	//隐藏信号条(联想需要显示信号条所以以下注释掉)
//    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//    }
//
//	@Override
//	public void surfaceChanged(SurfaceHolder holder, int format, int width,
//			int height) {
//		// TODO Auto-generated method stub
//	}
//
//	@Override
//	public void surfaceCreated(SurfaceHolder holder) {
//		// TODO Auto-generated method stub
//		if(playUrl != null&&playUrl.length() > 0){
//        	mHandler.sendEmptyMessageDelayed(MessageID.MESSAGE_MEDIA_INIT, 500);
//        }else{
//        	CommonUtil.ShowToast(this, "播放地址出错了");
//        }
//	}
//
//	@Override
//	public void surfaceDestroyed(SurfaceHolder holder) {
//		// TODO Auto-generated method stub
//		try {
//			LogPrint.Print("webview","surfaceDestroyed");
//			isPrepare = false;
//			isBufferPrepare = false;
//			isSurfaceDestory = true;
//			saveCurPos = mediaPlayer.getCurrentPosition();
//			mediaPlayer.stop();
//    		mediaPlayer.release();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//	private void setScreenSize(){
//		float usePecent;//最终使用比率
//		float wPecent,hPecent;//宽高比率
//		//视频源尺寸，需要在视频准备好后获取
//		float srcW = mediaPlayer.getVideoWidth();
//		float srcH = mediaPlayer.getVideoHeight();
//		wPecent = CommonUtil.screen_height/srcW;
//		hPecent = CommonUtil.screen_width/srcH;
//		usePecent = Math.min(wPecent, hPecent);
//		LayoutParams lp = surfaceView.getLayoutParams();
//        lp.width = (int)(wPecent < hPecent?CommonUtil.screen_height:srcW*usePecent);
//        lp.height = (int)(hPecent < wPecent?CommonUtil.screen_width:srcH*usePecent);
//        surfaceView.setLayoutParams(lp);
//        //视频logo       
//        ImageView image = (ImageView)findViewById(R.id.videoLogo);
////        image.setVisibility(View.VISIBLE);
//	}
//	
//	private void setCurTime(int time){
//		if(maxtime != null&&!"00:00".equals(maxtime.getText().toString())){
//			curtime.setText(getTime(time));
//		}
//	}
//	
//	private void setMaxTime(int time){
//		maxtime.setText(getTime(time));
//	}
//	
//	/**
//	 * 微信分享 发送消息
//	 * @param wxData
//	 */
//	public void wxSend(byte[] wxData){
//		//发送视频数据
//		/*WXVideoObject video = new WXVideoObject();
//		video.videoUrl = playUrl;*/
//
//
//		WXWebpageObject webpage = new WXWebpageObject();
//		//第三方展示页面
//		webpage.webpageUrl = wapUrl;
//		
//		WXMediaMessage msg = new WXMediaMessage(webpage);
//		msg.title = getString(R.string.wx_share_title);
//		msg.description = commodityInfoString;
//		
//		Bitmap temp = BitmapFactory.decodeByteArray(wxData, 0, wxData.length);
//		//设置分享图片的大小,不能超过32KB
//		temp = CommonUtil.resizeImage(temp, 110, 110);
//		msg.thumbData = bmpToByteArray(temp, true);
//		
//		SendMessageToWX.Req req = new SendMessageToWX.Req();
//		//req.transaction = buildTransaction("video");
//		req.transaction = buildTransaction("webpage");
//		req.message = msg;
//		api.sendReq(req);
//	}
//	/**
//	 * @param bmp 位图对象
//	 * @param needRecycle 是否回收
//	 * @return
//	 */
//	public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
//		ByteArrayOutputStream output = new ByteArrayOutputStream();
//		bmp.compress(CompressFormat.PNG, 100, output);
//		if (needRecycle) {
//			bmp.recycle();
//		}
//		byte[] result = output.toByteArray();
//		try {
//			output.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		return result;
//	}
//	//
//	private String buildTransaction(final String type) {
//		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
//	}
//	
//	/**
//	 * 头部菜单动画进入监听事件
//	 */
//	private AnimationListener title_in_animationListener = new AnimationListener() {
//		
//		@Override
//		public void onAnimationStart(Animation animation) {
//			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//			params.setMargins(0, 0, 0, 0);
//			relativeLayout_title.setLayoutParams(params);
//			isTitleAnimationFinish = false;
//			
//		}
//		
//		@Override
//		public void onAnimationRepeat(Animation animation) {	
//		}
//		
//		@Override
//		public void onAnimationEnd(Animation animation) {
//			isTitleAnimationFinish = true;
//		}
//	};
//	
//	/**
//	 * 播放控制器动画进入监听事件
//	 */
//	private AnimationListener bottom_in_animationListener = new AnimationListener() {
//		
//		@Override
//		public void onAnimationStart(Animation animation) {
//			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//			params.setMargins(0, 0, 0, 0);
//			relativeLayout_bottom.setLayoutParams(params);
//			isBottomAnimationFinish = false;
//		}
//		
//		@Override
//		public void onAnimationRepeat(Animation animation) {
//	
//		}
//		
//		@Override
//		public void onAnimationEnd(Animation animation) {
//			isBottomAnimationFinish = true;
//		}
//	};
//	
//	/**
//	 * 头部菜单退出屏幕动画监听事件
//	 */
//	private AnimationListener title_out_animationListener = new AnimationListener() {
//		
//		@Override
//		public void onAnimationStart(Animation animation) {
//			
//		}
//		
//		@Override
//		public void onAnimationRepeat(Animation animation) {
//
//		}
//		
//		@Override
//		public void onAnimationEnd(Animation animation) {
//			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//			params.setMargins(0, -relativeLayout_title.getBottom(), 0, 0);
//			relativeLayout_title.setLayoutParams(params);
//			media_share.setVisibility(View.INVISIBLE);
//		}
//	};
//	
//	/**
//	 * 播放控制器退出屏幕动画监听事件
//	 */
//	private AnimationListener bottom_out_animationListener = new AnimationListener() {
//		
//		@Override
//		public void onAnimationStart(Animation animation) {
//
//		}
//		
//		@Override
//		public void onAnimationRepeat(Animation animation) {
//
//		}
//		
//		@Override
//		public void onAnimationEnd(Animation animation) {
//			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//			params.setMargins(0, 0, 0, -relativeLayout_bottom.getBottom());
//			relativeLayout_bottom.setLayoutParams(params);
//		}
//	};
//	
//	/**手势动作广播接收器*/
//	private BroadcastReceiver mGestuReceiver = new BroadcastReceiver() {
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			String action = intent.getAction();
//			//无匹配到任何手势
//			if(ActionID.ACTION_BROADCAST_MEDIAPLAYER_GESTURE_NO_RESULT.equals(action)){
//				LogPrint.Print("broadcase: no");
//				Toast.makeText(MediaPlayer.this, "主人，您的画太抽象，小C没认出来", Toast.LENGTH_SHORT).show();
//			}
//			//匹配到对钩手势--//购买
//			else if(ActionID.ACTION_BROADCAST_MEDIAPLAYER_GESTURE_RIGHT.equals(action)){
//				if(wapUrl != null){
//					pauseMedia();	
//					pauseing = true;
//					Intent i = new Intent();
//					i.setClass(MediaPlayer.this, WebviewActivity.class);
//					LogPrint.Print("==============kankan = "+wapUrl);
//					i.putExtra("url",wapUrl);
//					i.putExtra("title", titleString);
//					i.putExtra("commodityid", commodityid);
//					i.putExtra("commodityImageString", commodityImageString);
//					i.putExtra("commodityInfoString", commodityInfoString);
//					startActivity(i);
//				}
//			}
//			//喜欢
//			else if(ActionID.ACTION_BROADCAST_MEDIAPLAYER_GESTURE_CIRCLE.equals(action)){
//				// TODO Auto-generated method stub
//				if(UserUtil.userid != -1&&UserUtil.userState == 1){					
//					if(likeState){//删除
//						new ConnectUtil(MediaPlayer.this, mHandler,0).connect(URLUtil.URL_MYLIKE_DELETE_SINGLE+"?id="+deleteId+"&oid="+UserUtil.userid+"&cid="+commodityid, HttpThread.TYPE_PAGE, 4);
//					}else{//添加
//						new ConnectUtil(MediaPlayer.this, mHandler,0).connect(URLUtil.URL_ADDLIKE+"?oid="+UserUtil.userid+"&cid="+commodityid, HttpThread.TYPE_PAGE, 5);
//					}
//				}else{
//					pauseMedia();
////					CommonUtil.ShowToast(MediaPlayer.this, "请登陆或者注册，变身为小C的主人！");
////					Intent intent11 = new Intent();
////					intent11.setClass(MediaPlayer.this, LoginAndRegeditActivity.class);
////					startActivity(intent11);
//					shareLikeIfNoLogin("登录后才能喜欢");
//				}
//			}
//			//分享
//			else if(ActionID.ACTION_BROADCAST_MEDIAPLAYER_GESTURE_TRIANGLE.equals(action)){
//				// TODO Auto-generated method stub
//				if (UserUtil.userid != -1 && UserUtil.userState == 1) {
//					if (media_share.isShown()) {
//						media_share.setVisibility(View.INVISIBLE);
//						titlebar_shareButton
//								.setBackgroundResource(R.drawable.media_sharebtn);
//					} else {
//						media_share.setVisibility(View.VISIBLE);
//						titlebar_shareButton
//								.setBackgroundResource(R.drawable.media_sharebtn_f);
//					}
//				} else {
//					pauseMedia();
//					shareLikeIfNoLogin("请您登录后分享");
//				}
//			}
//			//关闭手势面板跳回视频Activity
//			else if(ActionID.ACTION_BROADCAST_MEDIAPLAYER_GESTURE_CLOSE.equals(action)){
//				isScreen = true;
//				flag = true;
//				mHandler.postDelayed(cloassRunnable, 5000);
//			}
//		}	
//	};
//  	/**
//	 * 注册广播接收器
//	 */
//	private final void registerReceiver() {
//		isRegisterBroadcast = true;
//		IntentFilter intentFilter = new IntentFilter();
//		intentFilter.addAction(ActionID.ACTION_BROADCAST_MEDIAPLAYER_GESTURE_NO_RESULT);
//		intentFilter.addAction(ActionID.ACTION_BROADCAST_MEDIAPLAYER_GESTURE_RIGHT);
//		intentFilter.addAction(ActionID.ACTION_BROADCAST_MEDIAPLAYER_GESTURE_CIRCLE);
//		intentFilter.addAction(ActionID.ACTION_BROADCAST_MEDIAPLAYER_GESTURE_TRIANGLE);
//		intentFilter.addAction(ActionID.ACTION_BROADCAST_MEDIAPLAYER_GESTURE_CLOSE);
//		registerReceiver(mGestuReceiver, intentFilter);
//	}
//	
//	/**
//	 * 卸载广播接收器
//	 */
//	private final void unRegisterReceiver() {
//		if(mGestuReceiver != null&&isRegisterBroadcast){
//			unregisterReceiver(mGestuReceiver);
//			isRegisterBroadcast = false;
//		}
//	}
//	
//	/**
//	 * 头部和播放控制器进入动画
//	 */
//	private void animtionIn(){
//		Animation animation_title_in = AnimationUtils.loadAnimation(MediaPlayer.this,R.anim.mediaplayer_title_in);
//		relativeLayout_title.startAnimation(animation_title_in);
//		animation_title_in.setAnimationListener(title_in_animationListener);
//		Animation animation_bottom_in = AnimationUtils.loadAnimation(MediaPlayer.this, R.anim.mediaplayer_bottom_in);
//		relativeLayout_bottom.startAnimation(animation_bottom_in);
//		animation_bottom_in.setAnimationListener(bottom_in_animationListener);
//	}
//	
//	/**
//	 * 头部和播放控制器退出动画
//	 */
//	private void animtionOut(){
//		Animation animation_title_out = AnimationUtils.loadAnimation(MediaPlayer.this, R.anim.mediaplayer_title_out);
//		relativeLayout_title.startAnimation(animation_title_out);
//		animation_title_out.setAnimationListener(title_out_animationListener);
//		Animation animation_bottom_out = AnimationUtils.loadAnimation(MediaPlayer.this, R.anim.mediaplayer_bottom_out);
//		relativeLayout_bottom.startAnimation(animation_bottom_out);
//		animation_bottom_out.setAnimationListener(bottom_out_animationListener);
//	}	
//	
//	Runnable cloassRunnable = new Runnable() {
//		
//		@Override
//		public void run() {
//			animtionOut();
//			Intent intent = new Intent(MediaPlayer.this,MediaPlayerGestureActivity.class);
//			startActivity(intent);
//		}
//	};
//}