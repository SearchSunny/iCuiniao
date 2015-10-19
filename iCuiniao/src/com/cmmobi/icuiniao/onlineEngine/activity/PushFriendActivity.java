/**
 * 
 */
package com.cmmobi.icuiniao.onlineEngine.activity;

import java.util.ArrayList;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.LinearLayout.LayoutParams;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.Activity.Activity;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_Layout_AbsLayout;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_ParserEngine;
import com.cmmobi.icuiniao.onlineEngine.view.MLinearLayout;
import com.cmmobi.icuiniao.onlineEngine.view.View_FriendList_item;
import com.cmmobi.icuiniao.onlineEngine.view.XmlViewLayout;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.ConnectUtil;
import com.cmmobi.icuiniao.util.DownImageItem;
import com.cmmobi.icuiniao.util.DownImageManager;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.URLUtil;
import com.cmmobi.icuiniao.util.UserUtil;
import com.cmmobi.icuiniao.util.connction.HttpThread;

/**
 * @author hw
 *
 */
public class PushFriendActivity extends Activity{

	private ConnectUtil mConnectUtil;
	private XmlViewLayout xmlViewLayout;
	private MLinearLayout mPageLayout;
	private Parser_ParserEngine parserEngine;
	
	private ArrayList<User> useridArrayList;
	
	private String msgString;
	private int fromid;
	private String userName;
	
	private int curIndex;
	//lyb
	private LinearLayout showPage;
	//add by lyb 小鱼loading
	private ProgressBar loadingBar;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//lyb
		setContentView(R.layout.generalpage);
		//
		if(UserUtil.userid == -1||UserUtil.userState != 1){
			finish();
			return;
		}
		//lyb
		showPage = (LinearLayout)findViewById(R.id.userpage);
		loadingBar = (ProgressBar)findViewById(R.id.loading);
		//
		init();
		
//		setContentView(mPageLayout);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(UserUtil.userid == -1||UserUtil.userState != 1){
			finish();
			return;
		}
	}

	public void init(){
		//lyb
		showPage.removeAllViews();
		//
		curIndex = 0;
		useridArrayList = new ArrayList<User>();
		mPageLayout = new MLinearLayout(this,mHandler);
		mPageLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		mPageLayout.setOrientation(LinearLayout.VERTICAL);
		mPageLayout.setBackgroundResource(R.drawable.mainpagebg);
		//lyb
		showPage.addView(mPageLayout);
		
		xmlViewLayout = new XmlViewLayout(this,mHandler,null);
		mConnectUtil = new ConnectUtil(this, mHandler,0);
		String urlString = getIntent().getExtras().getString("url");
		fromid = getIntent().getExtras().getInt("fromid");
		userName = getIntent().getExtras().getString("username");
		msgString = getIntent().getExtras().getString("msg");
		if(urlString != null){
			mConnectUtil.connect(addUrlParam(urlString, UserUtil.userid, 0, msgString), HttpThread.TYPE_PAGE,0);
		}
	}
	
	public void init(String urlString,String msg){
		curIndex = 0;
		useridArrayList = new ArrayList<User>();
		mPageLayout = new MLinearLayout(this,mHandler);
		mPageLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		mPageLayout.setOrientation(LinearLayout.VERTICAL);
		mPageLayout.setBackgroundResource(R.drawable.mainpagebg);
		
		xmlViewLayout = new XmlViewLayout(this,mHandler,null);
		mConnectUtil = new ConnectUtil(this, mHandler,0);
		if(urlString != null){
			mConnectUtil.connect(addUrlParam(urlString, UserUtil.userid, 0, msg), HttpThread.TYPE_PAGE,0);
		}
	}
	
	public void renderPage(Message msg,int threadindex,int type){
		switch (type) {
		case HttpThread.TYPE_PAGE:
			parserEngine = new Parser_ParserEngine(this);
			parserEngine.parser((byte[])msg.obj);
			xmlViewLayout.setData(parserEngine.getLayouts());
			xmlViewLayout.setParserPage(parserEngine.getPageObject());
			LinearLayout tmplayout = xmlViewLayout.buildView(0);
			if(xmlViewLayout.isPartRenderList()){
				curIndex ++;
				xmlViewLayout.getFriendList().insertList(xmlViewLayout.getParserList());
				DownImageManager.reset();
				mHandler.sendEmptyMessage(MessageID.MESSAGE_CONNECT_LAYOUTOVER);
			}else{
				curIndex = 1;
				mPageLayout.removeAllViews();
				mPageLayout.addView(tmplayout);
				mPageLayout.postInvalidate();
			}
			break;
		case HttpThread.TYPE_IMAGE:
			DownImageItem downImageItem = DownImageManager.get(threadindex);
			try {
				//保存图片
				CommonUtil.writeToFile(CommonUtil.dir_cache+"/"+CommonUtil.urlToNum(downImageItem.getUrl()), (byte[])msg.obj);
				if(downImageItem.getType() == Parser_Layout_AbsLayout.MODELTYPE_LISTITEM){
					View_FriendList_item viewFriendListItem = xmlViewLayout.getFriendList().getListItem(threadindex);
					viewFriendListItem.setImageView((byte[])msg.obj);
				}
			} catch (Exception e) {
			}
			break;
		}
	}
	
	private Handler mHandler = new Handler(){

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
				CommonUtil.ShowToast(getApplicationContext(), (String)msg.obj);
				break;
			case MessageID.MESSAGE_CONNECT_DOWNLOADOVER:
				if(msg.arg1 == 999999){
					Json((byte[])msg.obj);
					closeProgress();
				}else{
					mPageLayout.setIsDownLoadOver(true);
					Bundle bundle = msg.getData();
					renderPage(msg,msg.arg1,bundle.getInt("mType"));
				}
				break;
			case MessageID.MESSAGE_CONNECT_LAYOUTOVER:
				closeProgress();
				//取出一个可用的图片下载
				DownImageItem downImageItem = DownImageManager.get();
				if(downImageItem != null){
					LogPrint.Print("image downloading");
					//只有在同一页面中才发起图片下载
					if(parserEngine.getPageObject().getPageId() == downImageItem.getPageId()){
						String urlString = downImageItem.getUrl();
						LogPrint.Print("urlString =  "+urlString);
						if(urlString != null){
							new ConnectUtil(PushFriendActivity.this, mHandler,0).connect(urlString, HttpThread.TYPE_IMAGE, downImageItem.getIndex());
						}
					}
					//发起下一个询问
					mHandler.sendEmptyMessageDelayed(MessageID.MESSAGE_CONNECT_LAYOUTOVER, 100);
				}else{
					LogPrint.Print("downImageItem = null");
				}
				break;
			case MessageID.MESSAGE_CLICK_TITLEBAR_LEFTBUTTON:
				finish();
				break;
			case MessageID.MESSAGE_CLICK_TITLEBAR_RIGHTBUTTON:
				//将useridArraylist里的数据群发出去
				for(int i = 0;useridArrayList!=null&&i < useridArrayList.size();i ++){
					String text = "%!%{"+userName+"!"+fromid+"}";
					LogPrint.Print("push text = "+text);
					mConnectUtil = new ConnectUtil(PushFriendActivity.this, mHandler,0);
					mConnectUtil.connect(_addUrlParam(URLUtil.URL_MESSAGE_SEND, UserUtil.userid, useridArrayList.get(i).id, text), HttpThread.TYPE_PAGE, 999999);
					addProgress();
				}
				break;
			case MessageID.MESSAGE_CLICK_LISTITEM_FRIEND:
				String url = msg.getData().getString("url");
				boolean isMoreItem = msg.getData().getBoolean("isMoreItem");
//				int curIndex = msg.getData().getInt("curIndex");
				int totalpage = msg.getData().getInt("totalpage");
				if(isMoreItem){//查看更多
					if(curIndex < totalpage){
						if(URLUtil.IsLocalUrl()){
							
						}else{
							new ConnectUtil(PushFriendActivity.this, mHandler,0).connect(addUrlParam(URLUtil.URL_PUSHFRIEND_MORE_LISTITEM, UserUtil.userid, curIndex, msgString), HttpThread.TYPE_PAGE, 0);
						}
					}
				}else{//普通列表项
//					Intent intent = new Intent();
//					intent.putExtra("url", addUrlParam1(URLUtil.URL_USERPAGE, UserUtil.userid, msg.getData().getInt("userid")));
//					intent.setClass(PushFriendActivity.this, UserPageActivity.class);
//					startActivity(intent);
				}
				break;
			case MessageID.MESSAGE_CLICK_INPUTLINE:
//				String url1 = msg.getData().getString("url");
				String str = msg.getData().getString("str");
				if(URLUtil.IsLocalUrl())return;
				if(str.length() > 0){
					Intent intent = new Intent();
					intent.putExtra("msg", str);
					intent.putExtra("url", URLUtil.URL_PUSHFRIEND);
					intent.setClass(PushFriendActivity.this, PushFriendActivity.class);
					startActivity(intent);
					finish();
				}
				break;
			case MessageID.MESSAGE_CLICK_LISTITEM_FRIENDCHILD:
				int userid = msg.getData().getInt("userid");
				boolean isselected = msg.getData().getBoolean("isselected");
				String name = msg.getData().getString("name");
				saveUserid(userid, name,isselected);
				break;
			}
		}
		
	};
	
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
	
	//拼接msg参数
	public String editUrl(String url,String msg){
		String result = "";
		if(msg == null){
			if(url.indexOf("?") > 0){
				result = url+"&msg=";
			}else{
				result = url+"?msg=";
			}
		}else{
			if(url.indexOf("?") > 0){
				result = url+"&msg="+CommonUtil.toUrlEncode(msg);
			}else{
				result = url+"?msg="+CommonUtil.toUrlEncode(msg);
			}
		}
		return result;
	}
	
	//将勾选上的用户id都保存下来，用于群发消息，titlebar的右侧按钮
	private void saveUserid(int userid,String name,boolean isselected){
		for(int i = 0;useridArrayList!=null&&i < useridArrayList.size();i ++){
			if(!isselected){
				if(useridArrayList.get(i).id == userid){
					useridArrayList.remove(i);
					break;
				}
			}
		}
		if(isselected){
			useridArrayList.add(new User(userid, name));
		}
	}
	
	public String addUrlParam(String url,int oid,int pi,String msg){
		if(URLUtil.IsLocalUrl()){
			return url;
		}
		if(msg == null)msg = "";
		return url+"?oid="+oid+"&pi="+pi+"&msg="+CommonUtil.toUrlEncode(msg);
	}
	
	public String _addUrlParam(String url,int oid,int uid,String msg){
		if(URLUtil.IsLocalUrl()){
			return url;
		}
		if(msg == null)msg="";
		return url+"?oid="+oid+"&uid="+uid+"&msg="+CommonUtil.toUrlEncode(msg);
	}
	
	public String addUrlParam1(String url,int oid,int uid){
		if(URLUtil.IsLocalUrl()){
			return url;
		}
		return url+"?oid="+oid+"&uid="+uid;
	}
	
	private void Json(byte[] data){
		try {
			String str = new String(data,"UTF-8");
			str = CommonUtil.formUrlEncode(str);
			LogPrint.Print("json = "+str);
			JSONObject jObject = new JSONObject(str);
			String result = jObject.getString("result");
			if(result != null){
				if(result.equalsIgnoreCase("true")){
					CommonUtil.ShowToast(PushFriendActivity.this, "发送成功");
					String to = jObject.getString("to");
					String to_name = jObject.getString("to_name");
					String msg = jObject.getString("msg");
					String time = jObject.getString("time");
					//写入本地文件
					CommonUtil.writeMessage(CommonUtil.dir_message_user+UserUtil.userid+CommonUtil.endName, ""+UserUtil.userid, UserUtil.username, to, to_name, msg, time);
				}else{
					String msg = jObject.getString("msg");
					if(msg != null){
						CommonUtil.ShowToast(PushFriendActivity.this, "发送失败!\n"+msg);
					}else{
						CommonUtil.ShowToast(PushFriendActivity.this, "发送失败!");
					}
				}
			}else{
				CommonUtil.ShowToast(PushFriendActivity.this, "发送失败");
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public class User{
		int id;
		String name;
		public User(int id,String name){
			this.id = id;
			this.name = name;
		}
	}
}
