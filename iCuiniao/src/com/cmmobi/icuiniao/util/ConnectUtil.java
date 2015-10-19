/**
 * 
 */
package com.cmmobi.icuiniao.util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.cmmobi.icuiniao.util.connction.HttpListener;
import com.cmmobi.icuiniao.util.connction.HttpThread;

/**
 * @author hw
 *
 *连接网络的封装类
 */
public class ConnectUtil implements HttpListener{

	private Handler fromHandler;//需要传入的handler,用于数据交互
	private HttpThread pageThread;
	private String content_type;
	private Context context;
	
	private int mCurrentPos;
	private int mSize;
	private String mUrl;
	private int mType;
	private String mApn;
	private static String preUrl = "";
	private boolean isShowProgress;
	public void setShowProgress(boolean isShowProgress) {
		this.isShowProgress = isShowProgress;
	}

	private int notUseCache;
	private int tag;
	
	public ConnectUtil(Context context,Handler handler,int tag){
		this.context = context;
		fromHandler = handler;
		isShowProgress = true;
		notUseCache = 0;
		this.tag = tag;
	}
	
	public ConnectUtil(Context context,Handler handler,boolean isShowProgress,int tag){
		this.context = context;
		fromHandler = handler;
		this.isShowProgress = isShowProgress;
		notUseCache = 0;
		this.tag = tag;
	}
	
	public ConnectUtil(Context context,Handler handler,int notUserCache,int tag){
		this.context = context;
		fromHandler = handler;
		isShowProgress = true;
		this.notUseCache = notUserCache;
		this.tag = tag;
	}
	
	public void setTag(int tag){
		this.tag = tag;
	}
	
	//发起数据连接,页面没有缓存,图片有缓存
	public void connect(String url,int type,int threadindex){
		LogPrint.Print("connect", "connect");
		if(url==null||url.equals("")){
    		return ;
    	}
		String apn = null;
		apn = CommonUtil.getApnType(context);
		mApn = apn;
		//2g,3g时文本提示逻辑
		if(CommonUtil.isNetWorkOpen(context)){
			if(apn == null){
				apn = UserUtil.preNetApn == null?"wifi":UserUtil.preNetApn;
			}
			if(apn != null&&apn.length() > 0){
				if(!apn.equals(UserUtil.preNetApn)){
					if(UserUtil.preNetApn != null){
						context.sendBroadcast(new Intent(ActionID.ACTION_BROADCAST_APN_CHANGE));
					}
					if(UserUtil.preNetApn != null&&apn.toLowerCase().indexOf("wifi") >= 0){
						CommonUtil.ShowToast(context, "接入到wifi网络，开始加载高清大图");
					}
					UserUtil.preNetApn = apn;
					CommonUtil.savePreApn(context, UserUtil.preNetApn);
					if(apn.toLowerCase().indexOf("wifi") < 0){
						if(apn.toLowerCase().indexOf("3g") >= 0){
//							CommonUtil.ShowToast(context, "查找到3G网络，开始加载高清大图");
							CommonUtil.ShowToast(context, "您在使用2G/3G方式上网\n进入省流量模式");
						}else{
							CommonUtil.ShowToast(context, "您在使用2G/3G方式上网\n进入省流量模式");
						}
					}
				}
			}
		}
		
		//有缓冲的话读缓冲,没有则连接
		String tmpString = null;
		String dir = null;
		String tempurlString = "";
		if(type == HttpThread.TYPE_IMAGE){
//			if(apn.toLowerCase().indexOf("wifi") < 0&&apn.toLowerCase().indexOf("3g") < 0){//2g
			if(apn.toLowerCase().indexOf("wifi") < 0){//2g,3g lyb-3.11
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
			}else{//wifi
				dir = CommonUtil.dir_cache+"/"+CommonUtil.urlToNum(url);
			}
		}else{
			dir = CommonUtil.chickResForApn(url, apn, context);
		}
		if(notUseCache == 0&&CommonUtil.exists(dir)){
			preUrl = url;
			tmpString = dir;
		}
		if(notUseCache == 1){
			preUrl = "";
		}
		
		byte[] data = null;
		//缓存有数据
		if(tmpString != null){
			if(type == HttpThread.TYPE_IMAGE){
				if(tempurlString.length() == 0){
					onGetUrl(url, HttpThread.TYPE_IMAGE,threadindex);
				}else{
					if(CommonUtil.exists(CommonUtil.dir_cache+"/"+CommonUtil.urlToNum(tempurlString))){
						onGetUrl(tempurlString, HttpThread.TYPE_IMAGE,threadindex);
					}else{
						onGetUrl(url, HttpThread.TYPE_IMAGE,threadindex);
					}
				}
				onGetContentType("image/jpeg",threadindex);
			}else{
				if(type == HttpThread.TYPE_JSON){
//					onGetUrl(url, HttpThread.TYPE_PAGE,threadindex);
					onGetUrl(url, HttpThread.TYPE_JSON,threadindex);  //lyb-3.11
					onGetContentType("text/json",threadindex);
				}else{
					onGetUrl(url, HttpThread.TYPE_PAGE,threadindex);
					onGetContentType("text/html",threadindex);
				}
			}
			LogPrint.Print("connect", "cache Url = "+tmpString);
			data = CommonUtil.getSDCardFileByteArray(tmpString);
			preUrl = url;
			onCurDataPos(0,threadindex);
			onSetSize(data.length,threadindex);
			onApnType(mApn, threadindex);
			onFinish(data, data.length, true, threadindex);
		}else{//缓存没有数据
			if(url.startsWith("/")||url.startsWith("\\")){
				preUrl = url;
				if(isShowProgress){
					fromHandler.sendEmptyMessage(MessageID.MESSAGE_CONNECT_START);
				}
				//sd卡文件
				data = CommonUtil.getSDCardFileByteArray(url);
				onGetUrl(url, HttpThread.TYPE_PAGE,threadindex);
				onCurDataPos(0,threadindex);
				onGetContentType("text/html",threadindex);
				onSetSize(data.length,threadindex);
				onApnType(mApn, threadindex);
				onFinish(data, data.length, true, 0);
			}else{
				if(url.equals(preUrl)){
					if(url.indexOf(URLUtil.URL_GET_USERICON) < 0||url.indexOf(URLUtil.URL_SAJIAO) < 0){//如果是获取用户头像,撒娇则忽略相同url判断
						return;
					}
				}
				preUrl = url;
				LogPrint.Print("connect", "connect Url = "+url);
				if(type == HttpThread.TYPE_FILE){
					final String _url = url;
					final int _type = type;
					final int _threadindex = threadindex;
					new Handler().postDelayed(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							pageThread = new HttpThread(context, _url, 0,_type, ConnectUtil.this,_threadindex);
						}
					}, 200);//做一下延迟操作,提高activity的跳转速度
				}else if(type == HttpThread.TYPE_PAGE||type == HttpThread.TYPE_JSON || type == HttpThread.TYPE_MESSAGE_GET){
//					type = HttpThread.TYPE_PAGE;  //lyb-3.11
					//拼接deviceid
					if(url.indexOf("?") >= 0){
						if(url.indexOf("deviceid=") < 0){
							url += "&deviceid="+CommonUtil.getIMEI(context);
						}
					}else{
						if(url.indexOf("deviceid=") < 0){
							url += "?deviceid="+CommonUtil.getIMEI(context);
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
							url += "&network_type="+CommonUtil.getApnType(context);
						}
					}else{
						if(url.indexOf("network_type=") < 0){
							url += "?network_type="+CommonUtil.getApnType(context);
						}
					}
					if(isShowProgress){
						//连接开始
						fromHandler.sendEmptyMessage(MessageID.MESSAGE_CONNECT_START);
					}
					final String _url = url;
					final int _type = type;
					final int _threadindex = threadindex;
					new Handler().postDelayed(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							pageThread = new HttpThread(context, _url, 0,_type, ConnectUtil.this,_threadindex);
						}
					}, 200);//做一下延迟操作,提高activity的跳转速度
					
				}else if(type == HttpThread.TYPE_IMAGE){
					new HttpThread(context, url, 0, HttpThread.TYPE_IMAGE, ConnectUtil.this, threadindex);
				}
			}
		}
	}
	
	@Override
	public void onCurDataPos(int dataPos,int index) {
		mCurrentPos = dataPos;
		//单品页下载大图线程
		if(index == 20){
			Message  msg = new Message();
			msg.what = MessageID.MESSAGE_DOWNLOAD_IMAGE_CURSIZE_VALUE;
			int ddd = dataPos;
			msg.arg1 = index;
			Bundle bundle = new Bundle();
			bundle.putInt("mCurrentPos", mCurrentPos);
			msg.setData(bundle);
			fromHandler.sendMessage(msg);
		}
	}

	@Override
	public void onError(int code, String message,int index,int type) {
		// TODO Auto-generated method stub
		preUrl = "";
		Message msg = new Message();
		if(type == HttpThread.TYPE_PAGE){
			msg.obj = "杯具了- -!\n联网不给力啊";			
		}else{
			msg.obj = "";			
		}
		Bundle data = new Bundle();
		data.putInt("type", type);
		msg.what = MessageID.MESSAGE_CONNECT_ERROR;
		msg.arg1 = index;
		msg.setData(data);
		fromHandler.sendMessage(msg);
	}

	@Override
	public void onFinish(byte[] data, int size, boolean isOver, int index) {
		// TODO Auto-generated method stub
		synchronized (this) {
			try {
				preUrl = "";
				byte[] result = data;
				if(size <= 0){
					return;
				}
				if(size != data.length){
					result = new byte[size];
					System.arraycopy(data,0,result,0,size);
				}
				LogPrint.Print("download","isOver = "+isOver);
				LogPrint.Print("onFinish: content-type = "+content_type);
				Message msg = new Message();		
				msg.what = MessageID.MESSAGE_CONNECT_DOWNLOADOVER;//页面数据下载完成
				msg.obj = result;
				msg.arg1 = index;
				Bundle bundle = new Bundle();
				bundle.putInt("mCurrentPos", mCurrentPos);
				bundle.putInt("mSize", mSize);
				bundle.putString("mUrl", mUrl);
				bundle.putInt("mType", mType);
				bundle.putString("mApn", mApn);
				bundle.putString("content_type", content_type);
				bundle.putInt("tag", tag);
				msg.setData(bundle);
				fromHandler.sendMessage(msg);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	@Override
	public void onGetContentType(String contentType,int index) {
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
        }else if(contentType.indexOf("json") >= 0){//json
            content_type = "text/json";
        }
	}

	@Override
	public void onGetUrl(String url, int type,int index) {
		mUrl = url;
		mType = type;
	}

	@Override
	public void onSetSize(int size,int index) {
		mSize = size;
		//单品页下载大图线程
		if(index == 20){
			Message  msg = new Message();
			msg.what = MessageID.MESSAGE_DOWNLOAD_IMAGE_SIZE_VALUE;
			msg.arg1 = index;
			Bundle bundle = new Bundle();
			bundle.putInt("mSize", mSize);
			msg.setData(bundle);
			fromHandler.sendMessage(msg);
		}
	}

	@Override
	public void onApnType(String apn, int index) {
		mApn = apn;
	}
	
	@Override
	public void onImageError(int code, String message, int index) {
		if(index == 20){
			Message  msg = new Message();
			msg.what = MessageID.MESSAGE_DOWNLOAD_IMAGE_ERROR;
			msg.arg1 = index;
			fromHandler.sendMessage(msg);
		}else if(index == 0){
			Message  msg = new Message();
			msg.what = MessageID.MESSAGE_IMAGE_LOAD_ERROR;
			msg.arg1 = index;
			fromHandler.sendMessage(msg);
		}
	}

}
