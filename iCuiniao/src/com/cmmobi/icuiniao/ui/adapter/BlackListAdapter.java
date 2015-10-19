package com.cmmobi.icuiniao.ui.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.entity.Friend;
import com.cmmobi.icuiniao.onlineEngine.activity.FriendActivityA;
import com.cmmobi.icuiniao.onlineEngine.activity.UserPageActivityA;
import com.cmmobi.icuiniao.util.AsyncImageLoader_noCache;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.AsyncImageLoader_noCache.ImageCallback;

public class BlackListAdapter extends BaseAdapter{
	private ArrayList<Friend> arrBlack;
	public void setFriendList(ArrayList<Friend> arrBlack) {
		this.arrBlack = arrBlack;
	}

	private Context context;
	private Handler handler;
	private LayoutInflater mInflater;	
	private AsyncImageLoader_noCache asyncImageLoader;
	
	public BlackListAdapter(Context context, Handler handler){
		this.context = context;
		this.handler = handler;
		arrBlack = new ArrayList<Friend>();
		mInflater = LayoutInflater.from(context);
		asyncImageLoader = new AsyncImageLoader_noCache(context);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return arrBlack.size();
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
		LogPrint.Print("list", "pos =" + position);
		Holder holder = null;
		if(convertView == null){
			holder = new Holder();
			convertView = mInflater.inflate(R.layout.listitem_blacklist, parent, false);
			holder.iconsrc = (ImageView)convertView.findViewById(R.id.usericonImg);
			holder.username = (TextView)convertView.findViewById(R.id.username);
			holder.imgDelBlack = (ImageView)convertView.findViewById(R.id.imgDelBlack);
			holder.blackLayout = (RelativeLayout)convertView.findViewById(R.id.blackLayout);
			convertView.setTag(holder);
		}else{
			holder = (Holder)convertView.getTag();
		}		
		final Friend friend = arrBlack.get(position);
		holder.iconsrc.setTag(friend.icon_src);
		holder.iconsrc.setImageResource(R.drawable.usericonbg);
		loadImage(friend.icon_src, holder.iconsrc);
		holder.username.setText(friend.username);
		holder.blackLayout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				LogPrint.Print("lyb", "black onItemClick");
				Intent intent = new Intent(context, UserPageActivityA.class);
				intent.putExtra("url", friend.userPage);
				LogPrint.Print("lyb", "click userid = " + friend.userid);
				intent.putExtra("userid", friend.userid);  //int
				intent.putExtra("nickname", friend.username);
				((FriendActivityA)context).startActivityForResult(intent, 100);			
				
			}
		});
		holder.imgDelBlack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Message msg = new Message();
				Bundle data = new Bundle();
				data.putInt("uid", friend.userid);
				data.putString("nickname", friend.username);
				msg.setData(data);
				msg.what = MessageID.MESSAGE_ADD_FRIEND;
				handler.sendMessage(msg);
				
			}
		});
		return convertView;

	}
	
	public static final class Holder{
		public ImageView iconsrc;
		public TextView username;
		public ImageView imgDelBlack;
		public RelativeLayout blackLayout;
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
