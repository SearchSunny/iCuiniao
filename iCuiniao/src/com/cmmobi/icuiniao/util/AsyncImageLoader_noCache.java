package com.cmmobi.icuiniao.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;

public class AsyncImageLoader_noCache {  
		private Context context;
		public AsyncImageLoader_noCache(Context context){ 
			this.context = context;
		}
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
			LogPrint.Print("lybImage", "imageUrl =" + imageUrl);
			final SoftReference<Drawable> softReference = imageCache
					.get(imageUrl);
			if (softReference.get() != null) {
//				handler.post(new Runnable() {
//					public void run() {
//						callback.imageLoaded(softReference.get());
//					}
//				});
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
//					if (file.exists() && file.length() > 0) {
//						LogPrint.Print("lybImage", "get dir =" + file.getName() + "imageUrl =" + imageUrl);
//						InputStream is = new FileInputStream(file);
//						final Drawable drawable = Drawable
//								.createFromStream(is, "image.png");
//						imageCache.put(imageUrl,
//								new SoftReference<Drawable>(drawable));
//						handler.post(new Runnable() {
//							public void run() {
//								callback.imageLoaded(drawable);
//							}
//						});
//						
//					} else {
						InputStream is = getNetInputStream(imageUrl);					
						byte[] data = readStream(is);
						LogPrint.Print("lybImage", "imageUrl =" +imageUrl + "len =" + data.length);
//						is.read(data);
						Bitmap bitmap = CommonUtil.bytes2Bitmap(data);
						final Drawable drawable = CommonUtil.bitmap2Drawable(context, bitmap);													
						imageCache.put(imageUrl,
								new SoftReference<Drawable>(drawable));
						handler.post(new Runnable() {
							public void run() {
								callback.imageLoaded(drawable);
							}
						});	
						//写入文件
//						if(file.exists() && file.length() == 0){
//							file.delete();
//						}											
//						
//						CommonUtil.writeToFile(dir, data);
//						LogPrint.Print("lybImage", "save dir =" + dir + "data len =" + data.length);
//					}

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
	    
		
			
	private InputStream getNetInputStream(String urlStr) {
		try {
			URL url = new URL(urlStr);
			URLConnection conn = url.openConnection();
			conn.connect();
			InputStream is = conn.getInputStream();
			return is;
		} catch (Exception e) {
			e.printStackTrace();
			LogPrint.Print("exp", "down e =" + e.getMessage());
		}
		return null;
	}
	
	public byte[] readStream(InputStream ism) {
		// TODO Auto-generated method stub
		try {
			 ByteArrayOutputStream outstream=new ByteArrayOutputStream();
		     byte[] buffer=new byte[4*1024];
		     int len=-1;
		     while((len=ism.read(buffer)) !=-1){
		         outstream.write(buffer, 0, len);
		     }
		     outstream.close();
		     return outstream.toByteArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	  //对外界开放的回调接口   
    public interface ImageCallback {  
        //注意 此方法是用来设置目标对象的图像资源   
        public void imageLoaded(Drawable imageDrawable);  
    }  
	    
}
