package com.cmmobi.icuiniao.ui.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.Activity.LoginAndRegeditActivity;
import com.cmmobi.icuiniao.Activity.SendShockActivity;
import com.cmmobi.icuiniao.entity.Friend;
import com.cmmobi.icuiniao.onlineEngine.activity.UserPageActivityA;
import com.cmmobi.icuiniao.util.AsyncImageLoader_noCache;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.ConnectUtil;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.URLUtil;
import com.cmmobi.icuiniao.util.UserUtil;
import com.cmmobi.icuiniao.util.AsyncImageLoader_noCache.ImageCallback;
import com.cmmobi.icuiniao.util.connction.HttpThread;

public class ShockFriendAdapter extends BaseAdapter{
	private ArrayList<Friend> arrFriend;
	public ArrayList<Friend> getArrFriend() {
		return arrFriend;
	}

	public void setFriendList(ArrayList<Friend> arrFriend) {
		this.arrFriend = arrFriend;
	}

	private Context context;
	private Handler handler;
	private LayoutInflater mInflater;	
	private AsyncImageLoader_noCache asyncImageLoader;
	
	public ShockFriendAdapter(Context context, Handler handler){
		this.context = context;
		this.handler = handler;
		arrFriend = new ArrayList<Friend>();
		mInflater = LayoutInflater.from(context);
		asyncImageLoader = new AsyncImageLoader_noCache(context);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return arrFriend.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder = null;
		if(convertView == null){
			holder = new Holder();
			convertView = mInflater.inflate(R.layout.listitem_shockfriendlist, null);
			holder.iconsrc = (ImageView)convertView.findViewById(R.id.usericonImg);
			holder.username = (TextView)convertView.findViewById(R.id.username);
			holder.friendLayout = (RelativeLayout)convertView.findViewById(R.id.friendLayout);
			holder.imgSendShock = (ImageView)convertView.findViewById(R.id.imgSendShock);
			convertView.setTag(holder);
		}else{
			holder = (Holder)convertView.getTag();
		}
		final Friend friend = arrFriend.get(position);
		holder.iconsrc.setTag(friend.icon_src);
		holder.iconsrc.setImageResource(R.drawable.usericonbg);
		loadImage(friend.icon_src, holder.iconsrc);
		holder.username.setText(friend.username);
		holder.friendLayout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {				
				Intent intent = new Intent(context, UserPageActivityA.class);
				intent.putExtra("url", friend.userPage);
				LogPrint.Print("lyb", "click userid = " + friend.userid);
				intent.putExtra("userid", friend.userid);  //int
				intent.putExtra("nickname", friend.username);
				((SendShockActivity)context).startActivityForResult(intent, 100);	
				
			}
		});
		holder.imgSendShock.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(UserUtil.userid == -1||UserUtil.userState != 1){
					CommonUtil.ShowToast(context, "请登陆或者注册，变身为小C的主人！");
					Intent intent = new Intent();
					intent.setClass(context, LoginAndRegeditActivity.class);
					context.startActivity(intent);
				}else{
					connectIsRefused(friend.userid, friend.username);
				}
			}
		});
		return convertView;

	}
	
	//判断我是否在对方黑名单中
	private void connectIsRefused(int uid, String nickname){
		String url = URLUtil.Url_USER_RELATION+ "?uid=" +
		uid +  "&nickname=" + nickname + "&oid=" + UserUtil.userid;
		ConnectUtil connectUtil = new
		 ConnectUtil(context, handler, 1, 0);
		connectUtil.connect(url, HttpThread.TYPE_PAGE,
				SendShockActivity.thread_is_refused);
	}
	
	public final class Holder{
		public ImageView iconsrc;
		public TextView username;
		public ImageView imgSendShock;
		public RelativeLayout friendLayout;		
	}
	
	//引入线程池，并引入内存缓存功能,并对外部调用封装了接口，简化调用过程   
    private void loadImage(final String url, final ImageView imageView) {  
          //如果缓存过就会从缓存中取出图像，ImageCallback接口中方法也不会被执行   
         Drawable cacheImage = asyncImageLoader.loadDrawable(url,new ImageCallback() {  
             //请参见实现：如果第一次加载url时下面方法会执行   
             public void imageLoaded(Drawable imageDrawable) {            	 
            	 if(imageView.getTag().equals(url)){
            		 imageView.setVisibility(View.VISIBLE);
            		 imageView.setImageBitmap(mergeImg(imageDrawable));
            	 }
             }  
         });  
        if(cacheImage!=null){    
        	imageView.setVisibility(View.VISIBLE);
        	imageView.setImageBitmap(mergeImg(cacheImage));  
        }  
    }
    
	private Bitmap mergeImg(Drawable drawable){
		 if(drawable == null){
			 return null;
		 }
		 Bitmap background = BitmapFactory.decodeResource(context.getResources(), R.drawable.usericonbg); 
  	 BitmapDrawable bd = (BitmapDrawable) drawable;
  	 Bitmap bm = bd.getBitmap();
  	 bm = CommonUtil.resizeImage(bm, background.getWidth(), background.getHeight());  
  	 bm =  CommonUtil.mergerIcon(background, bm,7);
  	 return bm;
	}

}
