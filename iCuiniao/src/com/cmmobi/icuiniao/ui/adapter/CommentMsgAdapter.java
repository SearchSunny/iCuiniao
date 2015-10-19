package com.cmmobi.icuiniao.ui.adapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.onlineEngine.activity.UserPageActivityA;
import com.cmmobi.icuiniao.util.Comment;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.LogPrint;

public class CommentMsgAdapter extends BaseAdapter{
	
	private ArrayList<Comment> arrComment = new ArrayList<Comment>();
	private Context context;
	private LayoutInflater mInflater;
	private HashMap<String, SoftReference<Drawable>> imageCache; 
	private AsyncImageLoader asyncImageLoader;
	private Handler mHandler;	

	public void setArrComment(ArrayList<Comment> arrComment) {
		this.arrComment = arrComment;
	}
	
	/**
	 * 获得所有商品id列表
	 */
	public ArrayList<String> getAllCidList(){
		ArrayList<String> arrCid = new ArrayList<String>();
		for(int i=0 ;i<arrComment.size(); i++){
			arrCid.add(arrComment.get(i).cid);
		}
		return arrCid;
	}
	
	public CommentMsgAdapter(ArrayList<Comment> arrComment, Handler handler, Context context){
		this.arrComment = arrComment;
		this.context = context;
		mHandler = handler;
		mInflater = LayoutInflater.from(context);
		imageCache = new HashMap<String, SoftReference<Drawable>>();
		asyncImageLoader = new AsyncImageLoader();
	}
	@Override
	public int getCount() {
		return arrComment.size();
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		LogPrint.Print("list", "pos = " + position);
		Comment comment = arrComment.get(position);
		Holder holder = null;
		if(convertView == null){
			holder = new Holder();
			convertView = mInflater.inflate(R.layout.listitem_message_center, null);
			holder.userName = (TextView)convertView.findViewById(R.id.listItem_c_name);
			holder.content = (TextView)convertView.findViewById(R.id.listItem_c_comment);
			holder.time = (TextView)convertView.findViewById(R.id.listItem_c_time);
			holder.reply = (TextView)convertView.findViewById(R.id.listItem_c_reply);
			holder.userIcon = (ImageView)convertView.findViewById(R.id.listItem_c_icon);			
			holder.linearMainbody = (LinearLayout)convertView.findViewById(R.id.linearMainbody);
			convertView.setTag(holder);
		}else{
			holder = (Holder)convertView.getTag();
		}	
		holder.userName.setText(comment.username);		
		holder.userIcon.setTag(position);
		loadImage(comment.icon_src, holder.userIcon);
		holder.content.setText(comment.msg1);
		holder.time.setText(comment.time);		
		holder.reply.setText("回复我的评论：”" + comment.msg2 +"”");
		holder.linearMainbody.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Message msg = new Message();
				msg.what = 223344;
				msg.arg1 = position;
				mHandler.sendMessage(msg);
				
			}
		});
		holder.userIcon.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context,
						UserPageActivityA.class);
				Comment comment = arrComment.get((Integer)v.getTag());
				intent.putExtra("url", comment.userpage);
				intent.putExtra("userid", comment.userid);
				intent.putExtra("nickname", comment.username);
				context.startActivity(intent);				
			}
		});
		return convertView;
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
	
	//引入线程池，并引入内存缓存功能,并对外部调用封装了接口，简化调用过程   
    private void loadImage(final String url, final ImageView imageView) {  
          //如果缓存过就会从缓存中取出图像，ImageCallback接口中方法也不会被执行   
         Drawable cacheImage = asyncImageLoader.loadDrawable(url,new ImageCallback() {  
             //请参见实现：如果第一次加载url时下面方法会执行   
             public void imageLoaded(Drawable imageDrawable) { 
            	           	  
            	 imageView.setImageBitmap(mergeImg(imageDrawable)); 
             }  
         });  
        if(cacheImage!=null){  
        	imageView.setImageBitmap(mergeImg(cacheImage));  
        }  
    }  

	
	public final class Holder{
		public TextView userName;
		public TextView content;
		public TextView reply;
		public TextView time;
		public ImageView userIcon;	
		public LinearLayout linearMainbody;
		
	}

	
	public class AsyncImageLoader {  
		   //为了加快速度，在内存中开启缓存（主要应用于重复图片较多时，或者同一个图片要多次被访问，比如在ListView时来回滚动）   
		    public Map<String, SoftReference<Drawable>> imageCache = new HashMap<String, SoftReference<Drawable>>();  
		    private ExecutorService executorService = Executors.newFixedThreadPool(5);    //固定五个线程来执行任务   
		    private final Handler handler=new Handler();  
		  
		     /** 
		     * 
		     * @param imageUrl     图像url地址 
		     * @param callback     回调接口 
		     * @return     返回内存中缓存的图像，第一次加载返回null 
		     */  
		public Drawable loadDrawable(final String imageUrl,
				final ImageCallback callback) {
			// 如果缓存过就从缓存中取出数据
			if (imageCache.containsKey(imageUrl)) {
				SoftReference<Drawable> softReference = imageCache
						.get(imageUrl);
				if (softReference.get() != null) {
					return softReference.get();
				}
			}
			
			// 缓存中没有图像，则先从本地文件找，没有则从网络上取出数据，并将取出的数据缓存到内存中
			executorService.submit(new Runnable() {
				public void run() {
					try {
						String dir = CommonUtil.dir_cache + "/"
								+ CommonUtil.urlToNum(imageUrl);
						File file = new File(dir);
						//先判断文件是否存在
						if (file.exists() && file.length() > 0) {
							InputStream is = new FileInputStream(file);
							final Drawable drawable = Drawable
									.createFromStream(is, "image.png");
							imageCache.put(imageUrl,
									new SoftReference<Drawable>(drawable));
							handler.post(new Runnable() {
								public void run() {
									callback.imageLoaded(drawable);
								}
							});
							
						} else {
							InputStream is = new URL(imageUrl).openStream();
							final Drawable drawable = Drawable
									.createFromStream(is, "image.png");
							imageCache.put(imageUrl,
									new SoftReference<Drawable>(drawable));
							handler.post(new Runnable() {
								public void run() {
									callback.imageLoaded(drawable);
								}
							});	
							//写入文件
							if(file.exists() && file.length() == 0){
								file.delete();
							}
							int len = is.available();
							byte[] data = new byte[len];
							is.read(data);
							CommonUtil.writeToFile(dir, data);
						}

					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			});
			return null;
		}  
		     //从网络上取数据方法   
		    protected Drawable loadImageFromUrl(String imageUrl) {  
		        try {  
		            return Drawable.createFromStream(new URL(imageUrl).openStream(), "image.png");  
		        } catch (Exception e) {  
		            throw new RuntimeException(e);  
		        }  
		    } 
		    
	}
	
	  //对外界开放的回调接口   
    interface ImageCallback {  
        //注意 此方法是用来设置目标对象的图像资源   
        public void imageLoaded(Drawable imageDrawable);  
    }   
	
		
	




}
