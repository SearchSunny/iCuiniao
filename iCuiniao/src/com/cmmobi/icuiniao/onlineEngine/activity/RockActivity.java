/**
 * 
 */
package com.cmmobi.icuiniao.onlineEngine.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.Activity.Activity;
import com.cmmobi.icuiniao.Activity.LoginAndRegeditActivity;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.SensorUtil;
import com.cmmobi.icuiniao.util.UserUtil;

/**
 * @author hw
 *摇一摇
 */
public class RockActivity extends Activity{

	private int fromScreen;//0：首页过来，1：单品页过来
	private FrameLayout rock;
	private TextView rocktext1;
	private TextView rocktext2;
	private TextView rocktext3;
	private Button rockbackbtn;
	private RelativeLayout rrocktext1;
	private RelativeLayout rrocktext2;
	private RelativeLayout rrocktext3;
	private RelativeLayout rrockbackbtn;
	private SensorUtil sensorUtil;
	private boolean isGotoLogin;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreenMold();
        setContentView(R.layout.rock_layout);
        fromScreen = getIntent().getExtras().getInt("fromscreen");
        rock = (FrameLayout)findViewById(R.id.rock);
        rock.setOnTouchListener(rockTouchListener);
		rocktext1 = (TextView)findViewById(R.id.rocktext1);
		rocktext2 = (TextView)findViewById(R.id.rocktext2);
		rocktext3 = (TextView)findViewById(R.id.rocktext3);
		rockbackbtn = (Button)findViewById(R.id.rockbackbtn);
		rockbackbtn.setOnClickListener(rockbackbtnClickListener);
		rrocktext1 = (RelativeLayout)findViewById(R.id.rrocktext1);
		rrocktext2 = (RelativeLayout)findViewById(R.id.rrocktext2);
		rrocktext3 = (RelativeLayout)findViewById(R.id.rrocktext3);
		rrockbackbtn = (RelativeLayout)findViewById(R.id.rrockbackbtn);
		sensorUtil = new SensorUtil(this, mHandler);
    }
    
    protected void setFullScreenMold(){
    	//隐藏标题
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    	//隐藏信号条
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
    
    @Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if(sensorUtil != null){
			sensorUtil.unRegeditListener();
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		changeRockText();
		if(sensorUtil != null){
			sensorUtil.regeditListener();
		}
	}

	public void finish(int type){
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
    }
	
	public void finish(){
		if(isGotoLogin){
			isGotoLogin = false;
			super.finish();
		}else{
			finish(0);
		}
	}
    
    private void changeRockText(){
		if(UserUtil.userid == -1||UserUtil.userState != 1){
			rock.setBackgroundResource(R.drawable.rock_nologin);
			rrockbackbtn.setPadding(0, 0, 0, CommonUtil.dip2px(RockActivity.this, 200));
			rrocktext1.setPadding(0, 0, 0, CommonUtil.dip2px(RockActivity.this, 110));
			rrocktext2.setPadding(0, 0, 0, CommonUtil.dip2px(RockActivity.this, 90));
			rockbackbtn.setBackgroundResource(R.drawable.rock_backbtn1_0);
			rocktext1.setText("请登陆或注册,变身为翠鸟主人,");
			rocktext2.setText("获得更多专属特权！");
			rocktext3.setVisibility(View.INVISIBLE);
		}else{
			rrockbackbtn.setPadding(0, 0, 0, CommonUtil.dip2px(RockActivity.this, 70));
			rrocktext1.setPadding(0, 0, 0, CommonUtil.dip2px(RockActivity.this, 130));
			rrocktext2.setPadding(0, 0, 0, CommonUtil.dip2px(RockActivity.this, 110));
			rocktext3.setVisibility(View.VISIBLE);
			rockbackbtn.setBackgroundResource(R.drawable.rock_backbtn_0);
			switch (UserUtil.callOnNum) {
			case 0:
				rock.setBackgroundResource(R.drawable.rock_nologin);
				rocktext1.setText("主人,摇不动了,休息休息,");
				rocktext2.setText("明天再陪您玩摇摇!");
				break;
			case 1:
				rock.setBackgroundResource(R.drawable.rock);
				rocktext1.setText("主人,您有1次摇摇机会,标记自己");
				rocktext2.setText("喜欢的商品,要不,会摇不见哦.");
				break;
			default:
				rock.setBackgroundResource(R.drawable.rock);
				rocktext1.setText("主人,您今天还有"+UserUtil.callOnNum+"次摇摇机会哦");
				rocktext2.setText("别忘了标记自己喜欢的商品哦.");
				break;
			}
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
					MainPageActivityA.isSensorSuccess = false;
					finish(0);
					break;
				}
				if(UserUtil.callOnNum == 0){
					MainPageActivityA.isSensorSuccess = false;
					finish(0);
					break;
				}
				if(!CommonUtil.isNetWorkOpen(RockActivity.this)){
					CommonUtil.ShowToast(RockActivity.this, "网络不给力，请稍后再试");
					MainPageActivityA.isSensorSuccess = false;
					finish(0);
					break;
				}
				playRockStart();
				break;
			case MessageID.MESSAGE_SENSOR_STARTROCK_PLAYOVER:
				MainPageActivityA.isSensorSuccess = true;
				finish(1);
				break;
			}
		}
    	
    };
    
    private OnClickListener rockbackbtnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(UserUtil.userid == -1||UserUtil.userState != 1){
				rock.setVisibility(View.INVISIBLE);
				Intent intent11 = new Intent();
				intent11.setClass(RockActivity.this, LoginAndRegeditActivity.class);
				startActivity(intent11);
				isGotoLogin = true;
				finish();
			}else{
				finish(0);
			}
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
					finish(0);
				}
				break;
			}
			return true;
		}
	};
	
	private MediaPlayer mp_start;
	private void playRockStart(){
		try {
			if(mp_start == null){
				mp_start = MediaPlayer.create(RockActivity.this, R.raw.rock_start);
			}
			if(!mp_start.isPlaying()){
				mp_start.start();
				mp_start.setOnCompletionListener(new OnCompletionListener() {
					
					@Override
					public void onCompletion(MediaPlayer mp) {
						// TODO Auto-generated method stub
						mp.release();
						mp_start = null;
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
}
