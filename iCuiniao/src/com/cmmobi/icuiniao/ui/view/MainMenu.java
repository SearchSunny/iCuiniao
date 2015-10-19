/**
 * 
 */
package com.cmmobi.icuiniao.ui.view;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.Activity.BindActivity;
import com.cmmobi.icuiniao.Activity.CoquetryActivity;
import com.cmmobi.icuiniao.Activity.LoginAndRegeditActivity;
import com.cmmobi.icuiniao.Activity.MessageManagerActivityA;
import com.cmmobi.icuiniao.Activity.SettingActivityA;
import com.cmmobi.icuiniao.Activity.SoftPushActivity;
import com.cmmobi.icuiniao.onlineEngine.activity.FriendActivityA;
import com.cmmobi.icuiniao.onlineEngine.activity.MainPageActivityA;
import com.cmmobi.icuiniao.onlineEngine.activity.RockActivityA;
import com.cmmobi.icuiniao.onlineEngine.activity.StreampageActivity;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.ConnectUtil;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.OfflineLog;
import com.cmmobi.icuiniao.util.TimerForRock;
import com.cmmobi.icuiniao.util.URLUtil;
import com.cmmobi.icuiniao.util.UserUtil;
import com.icuiniao.plug.im.Entity;
import com.icuiniao.plug.im.IMDataBase;

/**
 * @author hw
 *主菜单
 */
public class MainMenu extends LinearLayout{
	
	private  int[] GRID_RES0 = { R.drawable.mainmenubtn_mainpage_0,
			R.drawable.mainmenubtn_mylike_0, R.drawable.mainmenubtn_mylooked_0,
			R.drawable.mainmenubtn_sajiao_0, R.drawable.mainmenubtn_message_0,
			R.drawable.mainmenubtn_friend_0, R.drawable.mainmenubtn_lock_0,
			R.drawable.mainmenubtn_setting_0 , R.drawable.mainmenubtn_soft_0};


	/**1000036	宝软网的包需要去掉软件推荐的功能*/
	private  int[] GRID_RES1 = { R.drawable.mainmenubtn_mainpage_0,
			R.drawable.mainmenubtn_mylike_0, R.drawable.mainmenubtn_mylooked_0,
			R.drawable.mainmenubtn_sajiao_0, R.drawable.mainmenubtn_message_0,
			R.drawable.mainmenubtn_friend_0, R.drawable.mainmenubtn_lock_0,
			R.drawable.mainmenubtn_setting_0};
	private int[] grid_res;

	private boolean isCanClick;
	private ArrayList<HashMap<String, Object>> lstImageItem;
	private SimpleAdapter simpleAdapter;
	private ConnectUtil mConnectUtil;
	private Context mContext;
	private IMDataBase imDataBase;
	private ArrayList<Entity> entitys = null;
	public MainMenu(Context context,int [] arg1,int [] arg2) {
		super(context);
		// TODO Auto-generated constructor stub
		//this.GRID_RES0 = arg1;
		//this.GRID_RES1 = arg2;
		mContext = context;
		isCanClick = false;
		//安卓市场、小米增加“应用推荐”
		if(URLUtil.fid.equals(URLUtil.himarket) || URLUtil.fid.equals(URLUtil.xiaomi)
				|| URLUtil.fid.equals(URLUtil.qihu360)){//特殊处理
			grid_res = GRID_RES0;
		}else{//正常处理
			grid_res = GRID_RES1;
		}
		
		LinearLayout l = (LinearLayout)((Activity)context).getLayoutInflater().inflate(R.layout.mainmenuview,null);
		GridView mainmenu_grid = (GridView)l.findViewById(R.id.mainmenu_grid);
		lstImageItem = new ArrayList<HashMap<String, Object>>();
		for(int i = 0;i < grid_res.length;i ++){
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemImage", grid_res[i]);
			lstImageItem.add(map);
		}
		
		simpleAdapter = new SimpleAdapter(context, lstImageItem, R.layout.mainmenu_griditem, new String[]{"ItemImage"}, new int[]{R.id.mainmenu_gridImage});
		mainmenu_grid.setAdapter(simpleAdapter);
		mainmenu_grid.setOnItemClickListener(gridItemClickListener);
		
		imDataBase = new IMDataBase(mContext);
		new Thread(){
			public void run(){
				entitys =  imDataBase.getTheMessageByUserId(UserUtil.userid);
				mHandler.sendEmptyMessage(1111);
			}
		}.start();
		
		addView(l);
		
	}

	public MainMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mContext = context;
		isCanClick = false;
		//安卓市场、小米增加“应用推荐”
		if(URLUtil.fid.equals(URLUtil.himarket) || URLUtil.fid.equals(URLUtil.xiaomi) ||
				URLUtil.fid.equals(URLUtil.qihu360)){//特殊处理
			grid_res = GRID_RES0;
		}else{//正常处理
			grid_res = GRID_RES1;
		}
		
		LinearLayout l = (LinearLayout)((Activity)context).getLayoutInflater().inflate(R.layout.mainmenuview,null);
		GridView mainmenu_grid = (GridView)l.findViewById(R.id.mainmenu_grid);
		lstImageItem = new ArrayList<HashMap<String, Object>>();
		for(int i = 0;i < grid_res.length;i ++){
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemImage", grid_res[i]);
			lstImageItem.add(map);
		}
		
		simpleAdapter = new SimpleAdapter(context, lstImageItem, R.layout.mainmenu_griditem, new String[]{"ItemImage"}, new int[]{R.id.mainmenu_gridImage});
		mainmenu_grid.setAdapter(simpleAdapter);
		mainmenu_grid.setOnItemClickListener(gridItemClickListener);
		//禁止滚动
		mainmenu_grid.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                 return MotionEvent.ACTION_MOVE == event.getAction() ? true
                           : false;
            }
       });

		imDataBase = new IMDataBase(mContext);
		new Thread(){
			public void run(){
				entitys =  imDataBase.getTheMessageByUserId(UserUtil.userid);
				mHandler.sendEmptyMessage(1111);
			}
		}.start();
		
		addView(l);
	}
	
//	public void flush(){
//		if(lstImageItem != null){
//			lstImageItem.clear();
//			for(int i = 0;i < GRID_RES.length;i ++){
//				HashMap<String, Object> map = new HashMap<String, Object>();
//				if(UserUtil.userid != -1){//已登陆
//					map.put("ItemImage", GRID_RES1[i]);
//				}else{//未登陆
//					map.put("ItemImage", GRID_RES[i]);
//				}
//				lstImageItem.add(map);
//			}
//			simpleAdapter.notifyDataSetChanged();
//		}
//	}
	
	public void setIsCanClick(boolean b){
		isCanClick = b;
	}

	private OnItemClickListener gridItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			if(!isCanClick)return;
			LogPrint.Print("index = "+arg2);
			switch (arg2) {
			case 0://首页
//				CommonUtil.deleteAll(new File(CommonUtil.dir_cache_page));
				if(TimerForRock.isNoTime()){
					Intent intent = new Intent();
					intent.setClass(mContext, RockActivityA.class);
					intent.putExtra("fromscreen", 0);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					getContext().startActivity(intent);
					((Activity)getContext()).overridePendingTransition(R.anim.menu_left_in_new, R.anim.menu_left_out_new);
					break;
				}
				Intent intent = new Intent();
				intent.putExtra("url", URLUtil.URL_MAINPAGE);
				intent.putExtra("isDownLoadCache", true);
				intent.putExtra("cacheUrl", URLUtil.URL_MAINPAGE_CACHE_INFO);
				intent.putExtra("cacheUrlType", URLUtil.TYPE_MAINPAGE_CACHE_INFO);
				intent.putExtra("threadindex", URLUtil.THREAD_MAINPAGE_CACHE_INFO);
				intent.putExtra("pageIndex", 0);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.setClass(getContext(), MainPageActivityA.class);
				getContext().startActivity(intent);
				((Activity)getContext()).overridePendingTransition(R.anim.menu_left_in_new, R.anim.menu_left_out_new);
				OfflineLog.writeMainMenu_MainPage();//写入离线日志
				break;
			case 1://我的喜欢
				if(UserUtil.userid == -1||UserUtil.userState != 1){//未登陆
					CommonUtil.ShowToast(getContext(), "请登陆或者注册，变身为小C的主人！");
					Intent intent11 = new Intent();
					intent11.setClass(getContext(), LoginAndRegeditActivity.class);
					getContext().startActivity(intent11);
					((Activity)getContext()).overridePendingTransition(R.anim.menu_left_in, R.anim.menu_left_out);
				}else{
					Intent intent8 = new Intent();
					intent8.putExtra("url", URLUtil.URL_MAINPAGE_LIKE);
					intent8.setClass(getContext(), StreampageActivity.class);
//					getContext().startActivity(intent8);
					((Activity)getContext()).startActivityForResult(intent8, MessageID.REQUESTCODE_LIKE_FLUSH);
					((Activity)getContext()).overridePendingTransition(R.anim.menu_left_in, R.anim.menu_left_out);
				}
				break;
			case 2://我看过的
				if(UserUtil.userid == -1||UserUtil.userState != 1){//未登陆
					CommonUtil.ShowToast(getContext(), "请登陆或者注册，变身为小C的主人！");
					Intent intent11 = new Intent();
					intent11.setClass(getContext(), LoginAndRegeditActivity.class);
					getContext().startActivity(intent11);
					((Activity)getContext()).overridePendingTransition(R.anim.menu_left_in, R.anim.menu_left_out);
				}else{
					Intent intent9 = new Intent();
					intent9.putExtra("url", URLUtil.URL_MAINPAGE_LOOKED);
					intent9.setClass(getContext(), StreampageActivity.class);
					getContext().startActivity(intent9);
					((Activity)getContext()).overridePendingTransition(R.anim.menu_left_in, R.anim.menu_left_out);
				}
				break;
			case 3://我的撒娇
				if(UserUtil.userid == -1||UserUtil.userState != 1){//未登陆
					CommonUtil.ShowToast(getContext(), "请登陆或者注册，变身为小C的主人！");
					Intent intent11 = new Intent();
					intent11.setClass(getContext(), LoginAndRegeditActivity.class);
					getContext().startActivity(intent11);
					((Activity)getContext()).overridePendingTransition(R.anim.menu_left_in, R.anim.menu_left_out);
				}else{
					Intent intent2 = new Intent();
					intent2.setClass(getContext(), CoquetryActivity.class);
					intent2.putExtra("hide", true);
					getContext().startActivity(intent2);
					((Activity)getContext()).overridePendingTransition(R.anim.menu_left_in, R.anim.menu_left_out);
				}
				break;
			case 4://消息中心
//				if(UserUtil.userid == -1||UserUtil.userState != 1){//未登陆
//					CommonUtil.ShowToast(getContext(), "请登陆或者注册，变身为小C的主人！");
//					Intent intent11 = new Intent();
//					intent11.setClass(getContext(), LoginAndRegeditActivity.class);
//					getContext().startActivity(intent11);
//				}else{
				Intent intent22 = new Intent();
				intent22.putExtra("type", 0);//默认为用户消息
				intent22.putExtra("animation", true); //
				intent22.setClass(getContext(), MessageManagerActivityA.class);
				getContext().startActivity(intent22);
				((Activity)getContext()).overridePendingTransition(R.anim.menu_left_in, R.anim.menu_left_out);
				OfflineLog.writeMessageManager();//写入离线日志
//				}
				break;
			case 5://我的好友
				if(UserUtil.userid == -1||UserUtil.userState != 1){//未登陆
					CommonUtil.ShowToast(getContext(), "请登陆或者注册，变身为小C的主人！");
					Intent intent11 = new Intent();
					intent11.setClass(getContext(), LoginAndRegeditActivity.class);
					getContext().startActivity(intent11);
					((Activity)getContext()).overridePendingTransition(R.anim.menu_left_in, R.anim.menu_left_out);
				}else{
					Intent intent1 = new Intent();
					intent1.putExtra("tabbuttonIndex", 0);
					intent1.putExtra("url", URLUtil.URL_MYFRIEND_ADD);
//					intent1.setClass(getContext(), FriendActivity.class);
					intent1.setClass(getContext(), FriendActivityA.class);
					getContext().startActivity(intent1);
					((Activity)getContext()).overridePendingTransition(R.anim.menu_left_in, R.anim.menu_left_out);
				}
				break;
			case 6://账户绑定
				if(UserUtil.userid == -1||UserUtil.userState != 1){//未登陆
					CommonUtil.ShowToast(getContext(), "请登陆或者注册，变身为小C的主人！");
					Intent intent11 = new Intent();
					intent11.setClass(getContext(), LoginAndRegeditActivity.class);
					getContext().startActivity(intent11);
					((Activity)getContext()).overridePendingTransition(R.anim.menu_left_in, R.anim.menu_left_out);
				}else{
					Intent intent2 = new Intent();
					//换成新绑定Activity
					intent2.setClass(getContext(), BindActivity.class);
					getContext().startActivity(intent2);
					((Activity)getContext()).overridePendingTransition(R.anim.menu_left_in, R.anim.menu_left_out);
				}
				break;
			case 7://我的设置
				if(UserUtil.userid == -1||UserUtil.userState != 1){//未登陆
					CommonUtil.ShowToast(getContext(), "请登陆或者注册，变身为小C的主人！");
					Intent intent11 = new Intent();
					intent11.setClass(getContext(), LoginAndRegeditActivity.class);
					getContext().startActivity(intent11);
					((Activity)getContext()).overridePendingTransition(R.anim.menu_left_in, R.anim.menu_left_out);
				}else{
					Intent intent10 = new Intent();
					intent10.setClass(getContext(), SettingActivityA.class);
					getContext().startActivity(intent10);
					((Activity)getContext()).overridePendingTransition(R.anim.menu_left_in, R.anim.menu_left_out);
					OfflineLog.writeMainMenu_Setting();//写入离线日志
				}
				break;
//			case 3://随便看看
//				Intent intent3 = new Intent();
//				intent3.putExtra("url", URLUtil.URL_MAINPAGE_RANDOM);
//				intent3.setClass(MainMenuActivity.this, MainPageActivity.class);
//				startActivity(intent3);
//				finish();
//				break;
//			case 3://晒单
//				if(UserUtil.userid == -1){//未登陆
//					CommonUtil.ShowToast(getApplicationContext(), "您还未登陆");
//				}else{
//					Intent intent4 = new Intent();
//					intent4.putExtra("url", URLUtil.URL_MAINPAGE_FORM);
//					intent4.setClass(MainMenuActivity.this, MainPageActivity.class);
//					startActivity(intent4);
//					finish();
//				}
//				break;
//			case 4://折扣
//				CommonUtil.deleteAll(new File(CommonUtil.dir_cache_page));
//				Intent intent5 = new Intent();
//				intent5.putExtra("url", URLUtil.URL_MAINPAGE_DISCOUNT);
//				intent5.putExtra("isDownLoadCache", true);
//				intent5.putExtra("cacheUrl", URLUtil.URL_MAINPAGE_DISCOUNT_CACHE_INFO);
//				intent5.putExtra("cacheUrlType", URLUtil.TYPE_MAINPAGE_DISCOUNT_CACHE_INFO);
//				intent5.putExtra("threadindex", URLUtil.THREAD_MAINPAGE_DISCOUNT_CACHE_INFO);
//				intent5.setClass(MainMenuActivity.this, MainPageActivity.class);
//				startActivity(intent5);
//				finish();
//				break;
//			case 5://推荐
//				CommonUtil.deleteAll(new File(CommonUtil.dir_cache_page));
//				Intent intent6 = new Intent();
//				intent6.putExtra("url", URLUtil.URL_MAINPAGE_RECOMMEND);
//				intent6.putExtra("isDownLoadCache", true);
//				intent6.putExtra("cacheUrl", URLUtil.URL_MAINPAGE_RECOMMEND_CACHE_INFO);
//				intent6.putExtra("cacheUrlType", URLUtil.TYPE_MAINPAGE_RECOMMEND_CACHE_INFO);
//				intent6.putExtra("threadindex", URLUtil.THREAD_MAINPAGE_RECOMMEND_CACHE_INFO);
//				intent6.setClass(MainMenuActivity.this, MainPageActivity.class);
//				startActivity(intent6);
//				finish();
//				break;
//			case 6://活动
//				Intent intent7 = new Intent();
//				intent7.putExtra("url", URLUtil.URL_MAINPAGE_ACTIVITIES);
//				intent7.setClass(MainMenuActivity.this, MainPageActivity.class);
//				startActivity(intent7);
//				finish();
//				break;
			case 8://其他软件
//				if(UserUtil.userid == -1||UserUtil.userState != 1){//未登陆
//					CommonUtil.ShowToast(getContext(), "请登陆或者注册，变身为小C的主人！");
//					Intent intent11 = new Intent();
//					intent11.setClass(getContext(), LoginAndRegeditActivity.class);
//					getContext().startActivity(intent11);
//				}else{
					Intent intent12 = new Intent();
					intent12.setClass(getContext(), SoftPushActivity.class);
					getContext().startActivity(intent12);
//				}
				break;
			}
		}
	};
	
	private void getMessageByUserId(ArrayList<Entity> entitys){
		if(entitys != null){
			boolean isFind = false;
			for (Entity entity : entitys) {
				if(entity.getIsRead() == 0 && entity.getFromId() != UserUtil.userid){
					isFind = true;
					break;
				}
			}
			setMessageButtonRes(isFind);
		}
	}
	
	public void setMessageButtonRes(boolean b){
		if(b){
			grid_res[4] = R.drawable.mainmenubtn_message_a_0;
		}else{
			grid_res[4] = R.drawable.mainmenubtn_message_0;
		}
		lstImageItem.clear();
		for(int i = 0;i < grid_res.length;i ++){
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemImage", grid_res[i]);
			lstImageItem.add(map);
		}
		simpleAdapter.notifyDataSetChanged();
		simpleAdapter.notifyDataSetInvalidated();
	}
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 1111:
				getMessageByUserId(entitys);
				break;
			}
		}
		
	};

}
