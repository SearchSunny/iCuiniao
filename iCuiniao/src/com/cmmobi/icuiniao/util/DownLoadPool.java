/**
 * 
 */
package com.cmmobi.icuiniao.util;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.cmmobi.icuiniao.onlineEngine.parser.Parser_ParserEngine;
import com.cmmobi.icuiniao.util.connction.HttpListener;
import com.cmmobi.icuiniao.util.connction.HttpThread;
import com.cmmobi.icuiniao.util.connction.Worker;
import com.cmmobi.icuiniao.util.connction.WorkerPool;

/**
 * @author hw
 *预下载池管理器
 */
public class DownLoadPool implements HttpListener{
	
	private Context mContext;
	private static DownLoadPool downLoadPoll;
	private static ArrayList<DownLoadPoolItem> pools;
	private boolean isChicking;//是否在下载中
	private boolean isStop;//用于中止下载池
	private DownLoadPoolItem item;
	
	private final static int MESSAGE_CHICKPOOL = 1234;
	private String content_type;
	private int mCurrentPos;
	private int mSize;
	private String mUrl;
	private int mType;
	private String mApn;
	private WorkerPool workerPool;
	public static boolean isClear;//是否清楚全部队列
	
	public DownLoadPool(Context context){
		mContext = context;
		isChicking = false;
		isStop = false;
		isClear = false;
		pools = new ArrayList<DownLoadPoolItem>();
		workerPool = new WorkerPool(context, this);
	}
	
	public static DownLoadPool getInstance(Context context){
		if(downLoadPoll == null){
			downLoadPoll = new DownLoadPool(context);
		}
		return downLoadPoll;
	}
	
	public void add(DownLoadPoolItem item,int index){
		try {
			if(index == -1){//差到最后
				if(pools != null)
					pools.add(item);
			}else{
				if(pools != null)
					pools.add(index, item);
			}
			if(!isStop){
				if(!isChicking){
					mHandler.sendEmptyMessage(MESSAGE_CHICKPOOL);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public void stop(){
		try {
			isStop = true;
			if(pools != null){
				pools.clear();
			}
			pools = null;
			downLoadPoll = null;
			if(workerPool != null){
				Wait();
				workerPool.finish();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public void clear(){
		try {
			isClear = true;
			if(pools != null){
				pools.clear();
			}
			if(workerPool != null){
				workerPool.clearWorker();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public void reInit(){
		isClear = false;
		if(pools == null){
			pools = new ArrayList<DownLoadPoolItem>();
		}
		if(workerPool == null){
			workerPool = new WorkerPool(mContext, this);
		}
	}
	
	public void Wait(){
		if(workerPool != null){
			workerPool.setWait(true);
		}
	}
	
	public void Notify(){
		if(workerPool != null){
			workerPool.setWait(false);
		}
	}
	
	byte[] data;
	String url;
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case MESSAGE_CHICKPOOL:
				download();
				break;
			case MessageID.MESSAGE_CONNECT_DOWNLOADOVER:
				Bundle bundle = msg.getData();
				data = (byte[])msg.obj;
				url = msg.getData().getString("mUrl");
				switch (bundle.getInt("mType")) {
				case HttpThread.TYPE_PAGE:
//					item.setIsOver(true);
					if(pools != null){
						pools.remove(item);
					}
					new Thread(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							super.run();
							Parser_ParserEngine parserEngine = new Parser_ParserEngine(mContext);
							parserEngine.parser(data);
							//写缓存
							try {
								if(!CommonUtil.exists(CommonUtil.dir_cache_page+"/"+CommonUtil.urlToNum(url))){
									LogPrint.Print("cache","data = "+data+"|"+url+"|"+CommonUtil.dir_cache_page+"/"+CommonUtil.urlToNum(url));
									CommonUtil.writeToFile(CommonUtil.dir_cache_page+"/"+CommonUtil.urlToNum(url), data);
								}
							} catch (Exception e) {
								// TODO: handle exception
							}
							mHandler.sendEmptyMessage(MESSAGE_CHICKPOOL);
						}
						
					}.start();
					break;
				case HttpThread.TYPE_IMAGE:
//					item.setIsOver(true);
					if(pools != null){
						pools.remove(item);
					}
					new Thread(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							super.run();
							try {
								if(!CommonUtil.exists(CommonUtil.dir_cache+"/"+CommonUtil.urlToNum(url))){
									LogPrint.Print("cache","data1 = "+data+"|"+url+"|"+CommonUtil.dir_cache+"/"+CommonUtil.urlToNum(url));
									CommonUtil.writeToFile(CommonUtil.dir_cache+"/"+CommonUtil.urlToNum(url), data);
								}
							} catch (Exception e) {
								// TODO: handle exception
							}
							mHandler.sendEmptyMessage(MESSAGE_CHICKPOOL);
						}
						
					}.start();
					break;
				}
				break;
			}
		}
		
	};
	
	//下载逻辑
	private void download(){
		isChicking = true;
		boolean isFind = false;
		//取出一个可用连接
		for(int i = 0;pools!=null&&i < pools.size();i ++){
			item = pools.get(i);
			if(item != null){
				if(item.getIsOver() == false){
					item.setIsOver(true);
					isFind = true;
					connect(item.getUrl(), item.getType(), i);
					break;
				}
			}
		}
		isChicking = isFind;
		//发送完成广播
		if(isChicking == false){
			Intent broadcastIntent = new Intent(ActionID.ACTION_BROADCAST_DOWNLOADOVER);
			mContext.sendBroadcast(broadcastIntent);
			if(pools != null){
				pools.clear();
			}
		}
	}
	
	//发起数据连接,页面没有缓存,图片有缓存
	public void connect(String url,int type,int threadindex){
		LogPrint.Print("pools", "=======downloadpool=====");
		if(url==null||url.equals("")){
//			item.setIsOver(true);
			if(pools != null){
				pools.remove(item);
			}
			mHandler.sendEmptyMessage(MESSAGE_CHICKPOOL);
    		return ;
    	}
		
		String apn = null;
		apn = CommonUtil.getApnType(mContext);
		//2g,3g时文本提示逻辑
		if(CommonUtil.isNetWorkOpen(mContext)){
			if(apn == null){
				apn = UserUtil.preNetApn == null?"wifi":UserUtil.preNetApn;
			}
			if(apn != null&&apn.length() > 0){
				if(!apn.equals(UserUtil.preNetApn)){
					if(UserUtil.preNetApn != null){
						mContext.sendBroadcast(new Intent(ActionID.ACTION_BROADCAST_APN_CHANGE));
					}
					UserUtil.preNetApn = apn;
					CommonUtil.savePreApn(mContext, UserUtil.preNetApn);
				}
				onApnType(apn,threadindex);
			}
		}
		
		//有缓冲的话读缓冲,没有则连接
		String tmpString = null;
		String dir = null;
		String tempurlString = "";
		if(type == HttpThread.TYPE_IMAGE){
//			if(apn.toLowerCase().indexOf("wifi") < 0&&apn.toLowerCase().indexOf("3g") < 0){//2g
			if(apn.toLowerCase().indexOf("wifi") < 0){//2g,3g
				//2g,3g时判断
				boolean isFind = false;
				if(url.indexOf("/2/") > 0||url.indexOf("/L/") > 0||url.indexOf("/41/") > 0){
					if(url.indexOf("/2/") > 0){
						tempurlString = url.replace("/2/", "/M/");
						dir = CommonUtil.dir_cache+"/"+CommonUtil.urlToNum(tempurlString);
						isFind = CommonUtil.exists(dir);
						if(!isFind){
							tempurlString = url.replace("/2/", "/L/");
							dir = CommonUtil.dir_cache+"/"+CommonUtil.urlToNum(tempurlString);
							isFind = CommonUtil.exists(dir);
							if(!isFind){
								tempurlString = url.replace("/2/", "/41/");
								dir = CommonUtil.dir_cache+"/"+CommonUtil.urlToNum(tempurlString);
								isFind = CommonUtil.exists(dir);
							}
						}
					}else{
						if(url.indexOf("/41/") > 0){
							tempurlString = url.replace("/41/", "/M/");
							dir = CommonUtil.dir_cache+"/"+CommonUtil.urlToNum(tempurlString);
							isFind = CommonUtil.exists(dir);
							if(!isFind){
								tempurlString = url.replace("/41/", "/L/");
								dir = CommonUtil.dir_cache+"/"+CommonUtil.urlToNum(tempurlString);
								isFind = CommonUtil.exists(dir);
							}
						}
					}
					
					if(!isFind){
						dir = CommonUtil.dir_cache+"/"+CommonUtil.urlToNum(url);
					}
				}else{
					dir = CommonUtil.dir_cache+"/"+CommonUtil.urlToNum(url);
				}
			}else{
				dir = CommonUtil.dir_cache+"/"+CommonUtil.urlToNum(url);
			}
		}else{
			dir = CommonUtil.chickResForApn(url, apn, mContext);
		}
		if(CommonUtil.exists(dir)){
			tmpString = dir;
		}
		
		byte[] data = null;
		//缓存有数据
		if(tmpString != null){
//			item.setIsOver(true);
			if(pools != null){
				pools.remove(item);
			}
			mHandler.sendEmptyMessage(MESSAGE_CHICKPOOL);
		}else{//缓存没有数据
			LogPrint.Print("pools", "pools Url = "+url);
			if(type == HttpThread.TYPE_PAGE){
				//拼接deviceid
				if(url.indexOf("?") >= 0){
					if(url.indexOf("deviceid=") < 0){
						url += "&deviceid="+CommonUtil.getIMEI(mContext);
					}
				}else{
					if(url.indexOf("deviceid=") < 0){
						url += "?deviceid="+CommonUtil.getIMEI(mContext);
					}
				}
				//拼接oid参数
				if(url.indexOf("?") >= 0){
					if(url.indexOf("oid=") < 0){
						url += "&oid="+UserUtil.userid;
					}
				}else{
					if(url.indexOf("oid=") < 0){
						url += "?oid="+UserUtil.userid;
					}
				}
				//拼接vid参数
				if(url.indexOf("?") >= 0){
					if(url.indexOf("vid=") < 0){
						url += "&vid="+UserUtil.vid;
					}
				}else{
					if(url.indexOf("vid=") < 0){
						url += "?vid="+UserUtil.vid;
					}
				}
				//拼接ver参数
				if(url.indexOf("?") >= 0){
					if(url.indexOf("ver=") < 0){
						url += "&ver="+URLUtil.version;
					}
				}else{
					if(url.indexOf("ver=") < 0){
						url += "?ver="+URLUtil.version;
					}
				}
				//拼接network_type参数
				if(url.indexOf("?") >= 0){
					if(url.indexOf("network_type=") < 0){
						url += "&network_type="+CommonUtil.getApnType(mContext);
					}
				}else{
					if(url.indexOf("network_type=") < 0){
						url += "?network_type="+CommonUtil.getApnType(mContext);
					}
				}
				if(workerPool != null){
					Worker worker = new Worker(url, type, threadindex);
					workerPool.addWorker(worker);
				}
				if(pools != null){
					pools.remove(item);
				}
//				final String _url = url;
//				final int _type = type;
//				final int _threadindex = threadindex;
//				new Handler().postDelayed(new Runnable() {
//					
//					@Override
//					public void run() {
//						// TODO Auto-generated method stub
//						poolThread = new HttpThread(mContext, _url, 0,_type, DownLoadPool.this,_threadindex);
//					}
//				}, 200);//做一下延迟操作,提高activity的跳转速度
			}else if(type == HttpThread.TYPE_IMAGE){
				if(workerPool != null){
					Worker worker = new Worker(url, type, threadindex);
					workerPool.addWorker(worker);
				}
				if(pools != null){
					pools.remove(item);
				}
//				poolThread = new HttpThread(mContext, url, 0, HttpThread.TYPE_IMAGE, DownLoadPool.this, threadindex);
			}
		}
	}
	
	@Override
	public void onCurDataPos(int dataPos, int index) {
		// TODO Auto-generated method stub
		mCurrentPos = dataPos;
	}

	@Override
	public void onError(int code, String message, int index,int type) {
		// TODO Auto-generated method stub
		LogPrint.Print("pools", "Error: "+item.getUrl());
//		item.setIsOver(true);
//		if(pools != null){
//			pools.remove(item);
//		}
		mHandler.sendEmptyMessage(MESSAGE_CHICKPOOL);
	}

	@Override
	public void onFinish(byte[] data, int size, boolean isOver, int index) {
		// TODO Auto-generated method stub
		synchronized (this) {
			try {
				byte[] result = data;
				if(isOver == false||size <= 0){
		            return;
		        }
				if(size != data.length){
		            result = new byte[size];
		            System.arraycopy(data,0,result,0,size);
		        }
				LogPrint.Print("onFinish: content-type = "+content_type);
				Message msg = new Message();
				msg.what = MessageID.MESSAGE_CONNECT_DOWNLOADOVER;
				msg.obj = result;
				msg.arg1 = index;
				Bundle bundle = new Bundle();
				bundle.putInt("mCurrentPos", mCurrentPos);
				bundle.putInt("mSize", mSize);
				bundle.putString("mUrl", mUrl);
				bundle.putString("mApn", mApn);
				bundle.putInt("mType", mType);
				msg.setData(bundle);
				mHandler.sendMessage(msg);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	@Override
	public void onGetContentType(String contentType, int index) {
		// TODO Auto-generated method stub
		contentType = contentType.toLowerCase();
		if(contentType.indexOf("text/html") >= 0){
            content_type = "text/html";
        }else if(contentType.indexOf("text/xml") >= 0){
        	content_type = "text/html";
        }else if(contentType.indexOf("text/vnd.wap.wml") >= 0){
            content_type = "text/vnd.wap.wml";
        }else if(contentType.indexOf("application/xhtml+xml") >= 0){
            content_type = "application/xhtml+xml";
        }else if(contentType.indexOf("image/jpeg") >= 0||contentType.indexOf("image/jpg") >= 0){
            content_type = "image/jpeg";
        }else if(contentType.indexOf("image/gif") >= 0){
            content_type = "image/gif";
        }else if(contentType.indexOf("image/png") >= 0){
            content_type = "image/png";
        }else if(contentType.indexOf("application/x-gzip") >= 0){//压缩过的页面数据
            content_type = "application/x-gzip";
        }else if(contentType.indexOf("text/json") >= 0){//json
            content_type = "text/json";
        }
	}

	@Override
	public void onGetUrl(String url, int type, int index) {
		// TODO Auto-generated method stub
		mUrl = url;
		mType = type;
	}

	@Override
	public void onSetSize(int size, int index) {
		// TODO Auto-generated method stub
		mSize = size;
	}

	@Override
	public void onApnType(String apn, int index) {
		// TODO Auto-generated method stub
		mApn = apn;
	}
	
	@Override
	public void onImageError(int code, String message, int index) {
		
	}

}
