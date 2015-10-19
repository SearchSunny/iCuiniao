package com.cmmobi.icuiniao.Activity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.onlineEngine.activity.UserPageActivityA;
import com.cmmobi.icuiniao.ui.adapter.CommentAllAdapter;
import com.cmmobi.icuiniao.ui.view.ListviewForRefresh;
import com.cmmobi.icuiniao.ui.view.MainMenu;
import com.cmmobi.icuiniao.ui.view.ListviewForRefresh.OnContinusLoadListener;
import com.cmmobi.icuiniao.ui.view.ListviewForRefresh.OnRefreshListener;
import com.cmmobi.icuiniao.util.Comment;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.ConnectUtil;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.OfflineLog;
import com.cmmobi.icuiniao.util.ReplayPermit;
import com.cmmobi.icuiniao.util.URLUtil;
import com.cmmobi.icuiniao.util.UserUtil;
import com.cmmobi.icuiniao.util.connction.HttpThread;

public class CommentAllListActivity extends Activity implements View.OnClickListener, AnimationListener{
	
	private ProgressBar loadingBar;
	private ConnectUtil mConnectUtil;
	private ArrayList<Comment> arrComment;
	private CommentAllAdapter adapter;
	private ListviewForRefresh listview;
	private int commodityid;
	private String urlStr;
	private Button titlebar_backbutton;
	private Button titlebar_menubutton;
	private Button titlebar_leftmenu;
	private boolean isAnimationOpen;//主菜单动画状态
	private MainMenu mainMenu;
	private LinearLayout commentAllListPage;
	
	private int totalPage; //评论页数
	private int totalcount; //评论总数
	
	private final int arg_overall_refresh = 1;  //整体刷新
	private final int arg_contious_data = 2;	//续加载数据
	private final int arg_comment_refresh = 3;	//评论后的刷新
	private final int arg_comment_data = 4;		//评论数据解析	
	private final int arg_MAY_REPLAY = 5; //评论回复权限查询

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.commentalllist);
		listview = (ListviewForRefresh)findViewById(R.id.listview);		
		loadingBar = (ProgressBar)findViewById(R.id.loading);
		titlebar_backbutton = (Button)findViewById(R.id.titlebar_backbutton);
		titlebar_backbutton.setOnClickListener(this);
		mainMenu = (MainMenu)findViewById(R.id.mainmenu);
		commentAllListPage = (LinearLayout)findViewById(R.id.commentAllListPage);
		titlebar_menubutton = (Button)findViewById(R.id.titlebar_menubutton);
		titlebar_menubutton.setOnClickListener(this);
		titlebar_leftmenu = (Button)findViewById(R.id.titlebar_leftmenu);
		titlebar_leftmenu.setOnClickListener(this);
		commodityid = getIntent().getIntExtra("commodityid", -1);
		mConnectUtil = new ConnectUtil(this, mHandler,0);
		String sendUrl = getIntent().getStringExtra("urlStr");		
		urlStr =(sendUrl==null? addUrlParam_commentList(URLUtil.URL_COMMENT_LIST + "?plaid=" + URLUtil.plaid, UserUtil.userid, 0, commodityid):sendUrl);		
		mConnectUtil.connect(urlStr, HttpThread.TYPE_PAGE, 0);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		if(isAnimationOpen){
			gotoMenu();
		}
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.titlebar_backbutton:
			finish();
			break;
		case R.id.titlebar_leftmenu:
		case R.id.titlebar_menubutton:
			gotoMenu();
		}
	}
	/**
	 * 获取评论列表
	 * @param url
	 * @param oid
	 * @param pi
	 * @param cid
	 * @return
	 */
  	public String addUrlParam_commentList(String url,int oid,int pi,int cid){		
		if(url.indexOf("?") > 0){
			return url+"&oid="+oid+"&pi="+pi+"&cid="+cid;
		}
		return url+"?oid="+oid+"&pi="+pi+"&cid="+cid;
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
				sendComment(issubject,msg,subjectid,commentid);				
			}
		}
	}	

  	
  	/**
  	 * 进入个人主页
  	 * @param url
  	 * @param oid
  	 * @param uid
  	 * @return
  	 */
  	public String addUrlParam_myPage(String url,int oid,int uid){		
		if(url.indexOf("?") > 0){
			return url+"&oid="+oid+"&uid="+uid;
		}
		return url+"?oid="+oid+"&uid="+uid;
	}
  	
	private Handler mHandler = new Handler() {

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
				CommonUtil.ShowToast(getApplicationContext(), (String) msg.obj);
				listview.onFootRefreshComplete();	
				listview.onHeadRefreshComplete();				
				ReplayPermit.isMayClick = true;				
				break;
			case MessageID.MESSAGE_CONNECT_DOWNLOADOVER: {
				switch (msg.arg1) {
				// 整体刷新
				case arg_overall_refresh:
					arrComment = Json((byte[]) msg.obj, true);
					if (arrComment.size() != 0) {
						adapter.setArrComment(arrComment);
						adapter.notifyDataSetChanged();
						listview.onHeadRefreshComplete();
						listview.currPage = 0;
					}
					setFootRefreshable();
					closeProgress();
					break;
				// 向下续加载数据
				case arg_contious_data:
					ArrayList<Comment> temp = Json((byte[]) msg.obj, false);
					// 如果没有数据，则currpage需要恢复
					if (temp.size() == 0) {
						listview.currPage--;
					} else {
						arrComment.addAll(temp);
					}
					setFootRefreshable();
					adapter.setArrComment(arrComment);
					adapter.notifyDataSetChanged();
					listview.onFootRefreshComplete();
					closeProgress();
					break;
				// 评论数据解析
				case arg_comment_data:
					jsonCommentResult((byte[]) msg.obj);
					break;
				// 评论后的刷新和置顶
				case arg_comment_refresh:
					arrComment = Json((byte[]) msg.obj, true);					
					if (arrComment.size() != 0) {
						adapter.setArrComment(arrComment);
						adapter.notifyDataSetChanged();
						listview.setSelection(0);
						listview.currPage = 0;						
					}
					setFootRefreshable();
					closeProgress();
					break;
				case arg_MAY_REPLAY:  //回复权限
					closeProgress();
					ReplayPermit.replayComment(CommentAllListActivity.this, msg);					
					break;
				// 第一次加载
				default:
					firstDataLoad(msg);
					break;
				}

			}
				break;
			// 回复
			case MessageID.MESSAGE_MENUCLICK_COMMENTC_LIST_COMMENT:
				if (UserUtil.userid != -1 && UserUtil.userState == 1) {
					mConnectUtil = new ConnectUtil(
							CommentAllListActivity.this, mHandler, 1,  0);			
					int is_subject = msg.getData().getInt("is_subject");
					int subjectid = msg.getData().getInt("subjectid");
					int commentid = msg.getData().getInt("commentid");
					int cid = msg.getData().getInt("cid");
					int touserid = msg.getData().getInt("touserid");
					String tousername = msg.getData().getString("tousername");
					//is_subject + "-" +  subjectid + "-" + commentid + cid + "-" + touserid  + "-" + tousername;
					String split = ReplayPermit.split;
					String parmValue = is_subject + split +  subjectid + split + commentid  + split + cid + split + touserid;
					mConnectUtil.connect(URLUtil.Url_IS_PRIVATE_MSG + "?oid=" + UserUtil.userid  + "&tousername=" + tousername +  "&parmValue=" + parmValue + "&uid=" + touserid , 
							HttpThread.TYPE_PAGE, arg_MAY_REPLAY);
				} else {
					CommonUtil.ShowToast(CommentAllListActivity.this,
							"小C的主人才能评论哦，登录或者注册成为小C的主人吧。");
					Intent intent11 = new Intent();
					intent11.setClass(CommentAllListActivity.this,
							LoginAndRegeditActivity.class);
					startActivity(intent11);
				}

				break;
			// 个人主页
			case MessageID.MESSAGE_MENUCLICK_COMMENT_LIST_USERPAGE:				
					// isGotoClickMenu = false;
					Intent intent = new Intent(CommentAllListActivity.this,
							UserPageActivityA.class);
					Bundle data = msg.getData();
					intent.putExtra("url", data.getString("url"));
					intent.putExtra("userid", data.getInt("userid"));
					intent.putExtra("nickname", data.getString("nickname"));
					startActivity(intent);
				
				break;
			// 局部刷新评论
			case 112233:
				urlStr = addUrlParam_commentList(URLUtil.URL_COMMENT_LIST + "?plaid=" + URLUtil.plaid,
						UserUtil.userid, 0, commodityid);
				mConnectUtil.connect(urlStr, HttpThread.TYPE_PAGE,
						arg_comment_refresh);
				break;
			}
		}
	};	
	
	/**
	 * 第一次加载的数据解析
	 * @param msg
	 */
	private void firstDataLoad(Message msg){
		arrComment = Json((byte[]) msg.obj, true);
		adapter = new CommentAllAdapter(arrComment, mHandler, 
				CommentAllListActivity.this);
		listview.setAdapter(adapter);		
//		listview.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent,
//					View view, int position, long id) {
//				// 有header，索引从1开始
//				position--;
//				Comment comment = arrComment.get(position);
//				Message msg = new Message();
//				Bundle data = new Bundle();
//				data.putInt("subjectid", Integer.parseInt(comment.subjectid));
//				data.putInt("commentid", Integer.parseInt(comment.commentid));
//				data.putInt("is_subject", comment.isSubject);
//				msg.setData(data);
//				msg.what = MessageID.MESSAGE_MENUCLICK_COMMENTC_LIST_COMMENT;
//				mHandler.sendMessage(msg);
//			}
//		});
		listview.setonRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				mConnectUtil.setShowProgress(false);
				mConnectUtil.connect(urlStr, HttpThread.TYPE_PAGE,
						arg_overall_refresh);
			}
		});
		setFootRefreshable();
		closeProgress();
	}
	
	/**
	 * 根据解析到的评论条数决定是否有上推加载旧评论功能
	 * @param commentSize
	 */
	private void setFootRefreshable() {
		if(listview.currPage < totalPage-1){
			listview.setFootLoadListener(new OnContinusLoadListener() {

				@Override
				public void nextLoad(int page) {
					mConnectUtil.setShowProgress(false);
					String url = addUrlParam_commentList(
							URLUtil.URL_COMMENT_LIST + "?plaid=" + URLUtil.plaid, UserUtil.userid, page,
							commodityid);
					mConnectUtil.connect(url, HttpThread.TYPE_PAGE,
							arg_contious_data);
				}

			});
		}else{
			listview.setFootRefreshable(false);
		}
	}
	
	/**
	 * 解析json数据
	 * @param data
	 */
	private ArrayList<Comment> Json(byte[] data, boolean isResetTotalPage) {
		String str;
		ArrayList<Comment> comments = new ArrayList<Comment>();
		try {
			str = new String(data, "UTF-8");
			str = CommonUtil.formUrlEncode(str);
			LogPrint.Print("lyb", "commList str = " + str);
			if(str.length() <= 2){
				return comments;
			}	
			JSONObject json = new JSONObject(str);
			if (isResetTotalPage) {
				totalcount = json.getInt("totalcount");
				// 每页的条目数
				int pagesize = json.getInt("pagesize");
				//总页数
				totalPage = (totalcount % pagesize == 0) ? (totalcount / pagesize)
						: (totalcount / pagesize) + 1;
				LogPrint.Print("lyb", "totalcount =" + totalcount);
				LogPrint.Print("lyb", "totalPage =" + totalPage);
			}
			//评论列表
			JSONArray jsonArray = json.getJSONArray("comment");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject o = (JSONObject) jsonArray.get(i);
				Comment comment = new Comment();			
				comment.time = o.getString("time");
				comment.msg1 = o.getString("msg1");
				comment.msg2 = o.getString("msg2");
				comment.subjectid = o.getString("subjectid");
				comment.commentid = o.getString("commentid");
				comment.icon_src = o.getString("icon_src");
				comment.username = o.getString("username");
				comment.comment_to = o.getString("comment_to");
				comment.userpage = o.getString("userpage");
				comment.isSubject = o.getInt("is_subject");
				comment.userid = o.getInt("userid");
				comment.cid = o.getString("cid");
				comments.add(comment);
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return comments;
	}
	
	private void jsonCommentResult(byte[] data) {
		try {
			String str = new String(data, "UTF-8");
			str = CommonUtil.formUrlEncode(str);
			JSONObject jObject = new JSONObject(str);
			String result = jObject.getString("result");
			if (result != null) {
				if (result.equalsIgnoreCase("true")) {
					showCommentState("评论发布成功");
					mHandler.sendEmptyMessage(112233);
				} else {
					String msg = jObject.getString("msg");
					if (msg != null) {
						showCommentState("评论发布失败!\n"  + msg);
					} else {
						showCommentState("评论发布失败");						
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	private String msgString;
//	private int input_cur;
//	//评论对话框 0:一级评论  1:多级评论
//	public void buildCommentDialog(final int type, final int commentid, final String subjectid){
////		saveSubjectId = subjectid;
//		input_cur = 100;
//		LinearLayout l = (LinearLayout)getLayoutInflater().inflate(R.layout.comment_dialog,null);
//		final EditText et = (EditText)l.findViewById(R.id.comment_dialog);
//		final TextView input_num = (TextView)l.findViewById(R.id.input_num);
//		et.addTextChangedListener(new TextWatcher() {
//			
//			@Override
//			public void onTextChanged(CharSequence s, int start, int before, int count) {
//				// TODO Auto-generated method stub
//				input_num.setText("您还可输入"+input_cur+"字");
//			}
//			
//			@Override
//			public void beforeTextChanged(CharSequence s, int start, int count,
//					int after) {
//				// TODO Auto-generated method stub
//				input_num.setText("您还可输入"+input_cur+"字");
//			}
//			
//			@Override
//			public void afterTextChanged(Editable s) {
//				// TODO Auto-generated method stub
//				if(et.getText().length() >= 100){
//					CommonUtil.ShowToast(CommentAllListActivity.this, "主人，您写那么多字做咩？");
//				}
//				input_cur = 100-et.getText().length();
//				input_num.setText("您还可输入"+input_cur+"字");
//			}
//		});
//		AlertDialog.Builder builder = new AlertDialog.Builder(CommentAllListActivity.this);
//		builder.setTitle("评论").setView(l).setPositiveButton("发布", new OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				showCommentState("评论发布中...");
//				switch (type) {
//				//一级评论
//				case 0:
//					if(et.getText() == null){
//						msgString = "";
//					}else{
//						msgString = et.getText().toString();
//					}					
//					addProgress();
//					mConnectUtil = new ConnectUtil(CommentAllListActivity.this, mHandler,1);
//					mConnectUtil.connect(addUrlParam(URLUtil.URL_COMMENT_USER, UserUtil.userid, subjectid, msgString), HttpThread.TYPE_PAGE, arg_comment_data);
//					break;
//				case 1:
//					//多级评论
//					if(et.getText() == null){
//						msgString = "";
//					}else{
//						msgString = et.getText().toString();
//					}
//					addProgress();
//					mConnectUtil = new ConnectUtil(CommentAllListActivity.this, mHandler,1);
//					mConnectUtil.connect(addUrlParam(URLUtil.URL_COMMENT_THIRDCOMMENT, UserUtil.userid, subjectid, commentid, msgString), HttpThread.TYPE_PAGE, arg_comment_data);
//					break;				
//					
//				}
//			}
//		}).setNegativeButton("取消", null).show();
//	}
	
	//发表评论,-1:商品评论，0：一级评论，1：多级评论
	public void sendComment(int issubject,String msgString,int subjectid,int commentid){
		if(issubject == -1){
			mConnectUtil = new ConnectUtil(CommentAllListActivity.this, mHandler,1);
			mConnectUtil.connect(addUrlParam(URLUtil.URL_COMMENT_COMMODITY, UserUtil.userid, commodityid+"", msgString), HttpThread.TYPE_PAGE, 1);
			showCommentState("评论发布中...");
		}else if(issubject == 0){
			mConnectUtil = new ConnectUtil(CommentAllListActivity.this, mHandler,1);
			mConnectUtil.connect(addUrlParam(URLUtil.URL_COMMENT_USER, UserUtil.userid, subjectid+"", msgString), HttpThread.TYPE_PAGE, arg_comment_data);
			showCommentState("评论发布中...");
		}else{
			mConnectUtil = new ConnectUtil(CommentAllListActivity.this, mHandler,1);
			mConnectUtil.connect(addUrlParam(URLUtil.URL_COMMENT_THIRDCOMMENT, UserUtil.userid, subjectid+"", commentid, msgString), HttpThread.TYPE_PAGE, arg_comment_data);
			showCommentState("评论发布中...");
		}
	}
	
	NotificationManager notificationManager;
	Notification notification;
	//显示发送状态
	private void showCommentState(String msg){
		if(notificationManager == null){
			notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		}
		notification = new Notification(R.drawable.notifytionicon, msg, System.currentTimeMillis());
		notification.defaults |= Notification.DEFAULT_VIBRATE;//震动通知
	    notification.flags |= Notification.FLAG_AUTO_CANCEL; //在通知栏上点击此通知后自动清除此通知
	    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(), 0);
	    notification.setLatestEventInfo(this, null, msg, pendingIntent);
	    notificationManager.notify(0, notification);
	    notificationManager.cancel(0);
	}
  	
  	/**
  	 * 一级评论
  	 * @param url
  	 * @param oid
  	 * @param cid
  	 * @return
  	 */
  	public String addUrlParam(String url,int oid,String subjectid, String msg){		
		if(url.indexOf("?") > 0){
			return url+"&oid="+oid+"&subjectid="+subjectid +"&msg="+ msg;
		}
		return url+"?oid="+oid+"&subjectid="+subjectid +"&msg="+ msg;
	}
  	
  	/**
  	 * 多级评论
  	 * @param url
  	 * @param oid
  	 * @param cid
  	 * @return
  	 */
  	public String addUrlParam(String url,int oid,String subjectid, int commentid, String msg){		
		if(url.indexOf("?") > 0){
			return url+"&oid="+oid+"&subjectid="+subjectid + "&commentid=" + commentid + "&msg="+ msg;
		}
		return url+"?oid="+oid+"&subjectid="+subjectid + "&commentid=" + commentid + "&msg="+ msg;
	}

	
	private void addProgress() {
		try {
			if (loadingBar != null) {
				loadingBar.setVisibility(View.VISIBLE);
			}

		} catch (Exception e) {
		}
	}
	
	private void closeProgress(){
		if(loadingBar != null){
			loadingBar.setVisibility(View.INVISIBLE);
		}
	}
	
	private void gotoMenu(){
		if(commentAllListPage != null){
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
	    		commentAllListPage.startAnimation(animation);
	    		OfflineLog.writeMainMenu();//写入离线日志
			}else{
				if(mainMenu != null){
					mainMenu.setIsCanClick(false);
				}
				Animation animation = AnimationUtils.loadAnimation(this, R.anim.left_in);
				animation.setAnimationListener(this);
	    		animation.setFillEnabled(true);
	    		animation.setFillAfter(true);
	    		commentAllListPage.startAnimation(animation);
			}						
		}				
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		gotoMenu();
		return true;
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
		if(!isAnimationOpen){			
			titlebar_leftmenu.setVisibility(View.INVISIBLE);
			titlebar_backbutton.setVisibility(View.VISIBLE);			
		}		
	}

}
