/**
 * 
 */
package com.cmmobi.icuiniao.ui.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.util.AsyncImageLoader_noCache;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.URLUtil;
import com.cmmobi.icuiniao.util.AsyncImageLoader_noCache.ImageCallback;
import com.icuiniao.plug.im.Entity;

/**
 * @author hw
 *
 */
public class MessageAdapter extends BaseAdapter{

	private List<Entity> entitys;
	private Context context;
	private LayoutInflater l;
	private Holder holder;
	private AsyncImageLoader_noCache asyncImageLoader;
	
	public MessageAdapter(Context context,List<Entity> entities){
		this.entitys = entities;
		this.context = context;
		l = LayoutInflater.from(context);
		asyncImageLoader = new AsyncImageLoader_noCache(context);
	}
	
	@Override
	public int getCount() {
		return entitys.size();
	}

	@Override
	public Object getItem(int position) {		
		return entitys.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Entity entity = entitys.get(position);
		LogPrint.Print("lyb", "mess pos = " + position);
		if(convertView == null){
			convertView = l.inflate(R.layout.listitem_messagemanager, null);
			holder = new Holder();
			holder.img = (ImageView)convertView.findViewById(R.id.listItem_c_icon);
			holder.title = (TextView)convertView.findViewById(R.id.listItem_c_name);
			holder.time = (TextView)convertView.findViewById(R.id.listItem_c_time);
			holder.msg = (TextView)convertView.findViewById(R.id.listItem_c_comment);
			convertView.setTag(holder);
		}
		else{
			holder = (Holder)convertView.getTag();
		}
		holder.title.setText(entity.getNickName());
		holder.msg.setText(entity.getMessageBodies().get(0).getMessage());
		holder.time.setText(parseTime(entity.getCommenttime()));
		holder.entity = entity;
		holder.img.setTag(URLUtil.URL_GET_USERICON+entity.getRevicerUserId()+".jpg");
		loadImage(URLUtil.URL_GET_USERICON+entity.getRevicerUserId()+".jpg", holder.img,holder.entity);
		
		return convertView;
	}
	
	//引入线程池，并引入内存缓存功能,并对外部调用封装了接口，简化调用过程   
    private void loadImage(final String url, final ImageView imageView,final Entity entity) { 
    	
    	
		Drawable cacheImage = asyncImageLoader.loadDrawable(url,new ImageCallback() {  
    		//请参见实现：如果第一次加载url时下面方法会执行   
			public void imageLoaded(Drawable imageDrawable) {            	 
				if(imageView.getTag().equals(url)){
					imageView.setVisibility(View.VISIBLE);
					//是否是未读信息
					if(entity.getIsRead()==0){
						Bitmap readicon = BitmapFactory.decodeResource(context.getResources(), R.drawable.point);
						BitmapDrawable bd = (BitmapDrawable) imageDrawable;
						Bitmap temp = bd.getBitmap();
						temp = CommonUtil.mergerBitmapForRead(temp, readicon);
						imageView.setImageBitmap(mergeImg(temp));
					}
					else{
						imageView.setImageBitmap(mergeImg(imageDrawable));
					}

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

	private Bitmap mergeImg(Bitmap drawable){
		 if(drawable == null){
			 return null;
		 }
		 Bitmap background = BitmapFactory.decodeResource(context.getResources(), R.drawable.usericonbg); 
		 Bitmap bm = drawable;
		 bm = CommonUtil.resizeImage(bm, background.getWidth(), background.getHeight());  
		 bm =  CommonUtil.mergerIcon(background, bm,7);
		 return bm;
	}
	
	public void setEntitys(List<Entity> entities){
		this.entitys = entities;
	}
	
	public List<Entity> getEntitys(){
		return entitys;
	}
	
	public void setImageView(byte[] data){
		Bitmap temp = BitmapFactory.decodeByteArray(data, 0, data.length);
		if(temp == null)return;
		Bitmap background = BitmapFactory.decodeResource(context.getResources(), R.drawable.usericonbg);
		temp = CommonUtil.resizeImage(temp, background.getWidth(), background.getHeight());
		//是否是未读信息
		if(holder.entity.getIsRead()==0){
			Bitmap readicon = BitmapFactory.decodeResource(context.getResources(), R.drawable.point);
			temp = CommonUtil.mergerBitmapForRead(temp, readicon);
		}
		holder.img.setImageBitmap(CommonUtil.mergerIcon(background, temp,7));
	}
	
	private String parseTime(String time){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String curDate = sdf.format(new Date());
		String date = "";
		String year = time.substring(0,4);
		String month = time.substring(4,6);
		String day = time.substring(6,8);
		String hour = time.substring(8,10);
		String minute = time.substring(10,12);
		String second = time.substring(12,14);
		if((year+"-"+month+"-"+day).equals(curDate)){
			date = "今天 "+hour+":"+minute;
		}else{
			date = month +"-"+ day+" "+hour+":"+minute;
		}
		
		return date;
	}
	
	public class Holder{
		
		private ImageView img;
		private TextView title;
		private TextView msg;
		private TextView time;
		private Entity entity;
		
	}
}
