package com.cmmobi.icuiniao.ui.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.Activity.GestureActivity;
import com.cmmobi.icuiniao.Activity.LoginAndRegeditActivity;
import com.cmmobi.icuiniao.Activity.ShareActivity;
import com.cmmobi.icuiniao.onlineEngine.activity.WebviewActivity;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.OfflineLog;
import com.cmmobi.icuiniao.util.UserUtil;

public class PopupMenuA extends FrameLayout {

	
	private LinearLayout linearLayout = null;
	public ImageView lookImageView = null;
	public ImageView shareImageView = null;
	public ImageView gestureImageView = null;
	
	private String wapUrl;
	private String titleString;
	private String commodityImageString;
	private int commodityid;
	private String commodityInfoString;
	private Activity activity;
	private boolean isOnlyBack;
	private String direction;
	public boolean isCreate = false;
	
	private boolean likeState;
	private int deleteId;
	private String likenum;
	
	public void setLikenum(String likenum) {
		this.likenum = likenum;
	}

	public void setDeleteId(int deleteId) {
		this.deleteId = deleteId;
	}
	
	public void setLikeState(boolean likeState) {
		this.likeState = likeState;
	}

	public void setActivity(Activity activity){
		this.activity = activity;
	}
	
	public void setWapUrl(String wapUrl){
		this.wapUrl = wapUrl;
	}
	
	public void setTitle(String title){
		this.titleString = title;
	}
	
	public void setCommodityImageString(String str){
		this.commodityImageString = str;
	}
	
	public void setCommodityId(int id){
		this.commodityid = id;
	}
	public void setCommodityInfoString(String str){
		this.commodityInfoString = str;
	}	
	public void setDirection(String direction){
		this.direction = direction;
	}
	
	public PopupMenuA(Context context){
		super(context);
	}
	
	public PopupMenuA(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public PopupMenuA(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	/** 左边=1 左下=2 右边=3 右下=4  */
	public void init(Context context,String direction){
		linearLayout = new LinearLayout(context);	
		lookImageView = new ImageView(context);
		lookImageView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		lookImageView.setBackgroundResource(R.drawable.look_0);
		lookImageView.setOnClickListener(lookListener);
		shareImageView = new ImageView(context);
		shareImageView.setBackgroundResource(R.drawable.share_0);
		shareImageView.setOnClickListener(shareListener);
		gestureImageView = new ImageView(context);
		gestureImageView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		gestureImageView.setBackgroundResource(R.drawable.gesture_0);
		gestureImageView.setOnClickListener(gestureListener);
		
		if(direction.equals("1")){
			LinearLayout.LayoutParams shareParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			shareParams.setMargins(0, 0, 20, 0); 
			shareImageView.setLayoutParams(shareParams);
			linearLayout.setOrientation(LinearLayout.HORIZONTAL);
			linearLayout.setBackgroundResource(R.drawable.operate_11);	
			linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);	
			if(wapUrl!=null&&!wapUrl.equals("")){
				linearLayout.addView(shareImageView);
				linearLayout.addView(lookImageView);
			}
			else{
				//没有第三方链接地址时，只显示分享按钮并且更换背景图
				linearLayout.setBackgroundResource(R.drawable.operate_11_1);
				linearLayout.addView(shareImageView);
			}
		}
		else if(direction.equals("2")){
			linearLayout.setOrientation(LinearLayout.VERTICAL);
			linearLayout.setBackgroundResource(R.drawable.operate_2);
			linearLayout.setGravity(Gravity.CENTER_VERTICAL);
			linearLayout.addView(gestureImageView);
		}
		else if(direction.equals("3")){
			LinearLayout.LayoutParams shareParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			shareParams.setMargins(20, 0, 0, 0); 
			shareImageView.setLayoutParams(shareParams);
			linearLayout.setOrientation(LinearLayout.HORIZONTAL);
			linearLayout.setBackgroundResource(R.drawable.operate_1);			
			linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
			if(wapUrl!=null&&!wapUrl.equals("")){
				linearLayout.addView(lookImageView);
				linearLayout.addView(shareImageView);
			}
			else{
				//没有第三方链接地址时，只显示分享按钮并且更换背景图
				linearLayout.setBackgroundResource(R.drawable.operate_1_1);
				linearLayout.addView(shareImageView);
			}
		}
		else if(direction.equals("4")){
			linearLayout.setOrientation(LinearLayout.VERTICAL);
			linearLayout.setBackgroundResource(R.drawable.operate_2);
			linearLayout.setGravity(Gravity.CENTER_VERTICAL);
			linearLayout.addView(gestureImageView);
		}
		isCreate = true;
		setDirection(direction);
		mHandler.sendEmptyMessage(0);
		
	}
	
	public Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) { 
			addView(linearLayout);
		}
	};
	private OnClickListener lookListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			LogPrint.Print("look");
			if(wapUrl != null){
				isOnlyBack = true;
				Intent intent = new Intent();
				intent.setClass(getContext(), WebviewActivity.class);
				LogPrint.Print("==============kankan = "+wapUrl);
				intent.putExtra("url",wapUrl);
				intent.putExtra("title", titleString);
				intent.putExtra("commodityid", commodityid);
				intent.putExtra("commodityImageString", commodityImageString);
				intent.putExtra("commodityInfoString", commodityInfoString);
				//add by lyb for taobao's like
				intent.putExtra("likestate", likeState);
				intent.putExtra("deleteid", deleteId);
				intent.putExtra("likenum", likenum);
				((Activity)getContext()).startActivityForResult(intent, MessageID.REQUESTCODE_LIKE_FLUSH);
			}
		}
	};
	
	private OnClickListener shareListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			LogPrint.Print("share");
			isOnlyBack = true;
			share();
		}
	};
	
	private OnClickListener gestureListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			LogPrint.Print("gesture");
			isOnlyBack = true;
			getContext().startActivity(new Intent(getContext(), GestureActivity.class));
			OfflineLog.writeGesture(commodityid);
		}
	};
	
	//分享功能
  	public void share(){
  		//分享的登录判断
  		if(UserUtil.userid == -1||UserUtil.userState != 1){//未登陆  			
			CommonUtil.ShowToast(getContext(), "请登陆或者注册，变身为小C的主人！");
			Intent intent11 = new Intent();
			intent11.setClass(getContext(), LoginAndRegeditActivity.class);
			getContext().startActivity(intent11);
			return;
  		}  		
  		Intent intent = new Intent();
  		intent.putExtra("url", wapUrl);
  		intent.putExtra("title", titleString);
  		intent.putExtra("image", commodityImageString);
  		intent.putExtra("commodityInfoString", commodityInfoString);
  		intent.putExtra("commodityid", commodityid);
  		intent.setClass(getContext(), ShareActivity.class);
  		getContext().startActivity(intent);
  	}
	private void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}  	
}
