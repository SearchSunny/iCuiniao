/**
 * 
 */
package com.cmmobi.icuiniao.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.cmmobi.icuiniao.util.connction.HttpThread;

/**
 * @author hw
 *单品页数据批量下载
 */
public class BulkDownloadUtil {

	private int[] cids;//商品id集合
	private Context context;
	private final static String startTag = "<page ";
	private final static String endTag = "</page>";
	private final static String buildStartTag = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
	
	public BulkDownloadUtil(Context context,int[] cids){
		this.context = context;
		this.cids = cids;
		newHandler();
	}
	
	public void download(){
		if(cids != null&&cids.length > 0){
			LogPrint.Print("bulk","download");
			new ConnectUtil(context, downHandler, 0).connect(addUrlParam(), HttpThread.TYPE_PAGE, 0);
		}
	}
	
	private String addUrlParam(){
		String paramcid = "";
		for(int i = 0;i < cids.length;i ++){
			if(i == cids.length-1){
				paramcid += cids[i];
			}else{
				paramcid += cids[i]+",";
			}
		}
		return URLUtil.URL_BULKDOWNLOAD+"?dpi="+URLUtil.dpi()+"&pi=0&oid="+UserUtil.userid+"&cids="+paramcid+
		"&deviceid="+CommonUtil.getIMEI(context)+"&network_type="+CommonUtil.getApnType(context)+"&ver="+URLUtil.version+
		"&imei="+CommonUtil.getIMEI(context)+"&imsi="+CommonUtil.getIMSI(context)+"&pla="+CommonUtil.toUrlEncode(CommonUtil.getDeviceName());
	}
	
	//解析
	private void decode(byte[] data){
		try {
			LogPrint.Print("bulk","decode start");
			String str = new String(data,"utf-8");
			if(str != null&&str.length() > 0){
				String page = "";
				String url = "";
				int startpos = 0;
				int endpos = 0;
				for(int i = 0;i < cids.length;i ++){
					url = URLUtil.URL_MYPAGE+"?cid="+cids[i]+"&dpi="+URLUtil.dpi()+"&pi=0&plaid="+URLUtil.plaid;
					url = editUrl(url);
					page = "";
					startpos = str.indexOf(startTag, startpos);
					endpos = str.indexOf(endTag, startpos)+endTag.length();
					LogPrint.Print("bulk","======"+i+"======");
					//判断是否有缓存
					if(!CommonUtil.exists(CommonUtil.dir_cache_page+"/"+CommonUtil.urlToNum(url))){
						LogPrint.Print("bulk","cid"+cids[i]);
						page = str.substring(startpos, endpos);
						startpos = endpos;
						if(page.length() > 0){
							page = buildStartTag+page;//将头拼上
							CommonUtil.writeToFile(CommonUtil.dir_cache_page+"/"+CommonUtil.urlToNum(url), page.getBytes("utf-8"));
							page = "";
							LogPrint.Print("bulk","write over");
						}
					}else{
						LogPrint.Print("bulk","cid"+cids[i]+" exists");
					}
					LogPrint.Print("bulk","======"+i+"======");
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	private String editUrl(String url){
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
		return url;
	}
	
	private MHandlerThread mHandlerThread;
	public Handler downHandler;
	//用于下载
	private void newHandler(){
		mHandlerThread = new MHandlerThread("BulkDownloadUtil");
		mHandlerThread.start();
		downHandler = new Handler(mHandlerThread.getLooper(), mHandlerThread){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case MessageID.MESSAGE_CONNECT_DOWNLOADOVER:
					LogPrint.Print("bulk","downloadover");
					decode((byte[])msg.obj);
					break;
				}
			}
			
		};
	}
}
