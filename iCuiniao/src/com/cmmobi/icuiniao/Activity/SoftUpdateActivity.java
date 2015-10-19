/**
 * 
 */
package com.cmmobi.icuiniao.Activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.Proxy.Type;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ProgressBar;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.ConnectUtil;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.OfflineLog;
import com.cmmobi.icuiniao.util.URLUtil;
import com.cmmobi.icuiniao.util.connction.HttpListener;
import com.cmmobi.icuiniao.util.connction.HttpThread;

/**
 * @author hw
 *软件更新
 */
public class SoftUpdateActivity extends Activity implements HttpListener{

	private String urlString;
	private ProgressBar grogress;
	private int maxlength;
	private int curlength;
	private String filename;
	private String tmpname;
	private static final String TMP = ".tmp";
	private ConnectUtil mConnectUtil;
	private RandomAccessFile file;
	private File writeFile;
	private String versionnumber;
	
	/** 下载缓冲区 */
	private byte[] buffer;
	private static String proxyAddr = "10.0.0.172";
	private final static int proxyPort = 80;
	private int APN;// 0 wifi 1 cmwap 2 cmnet
	private int curPos = 0,_curPos;
	public static final int BUFFER = 1024;// 一次读1K
	public final static int MAX_WRITE_LEN_WIFI = 50 * 1024;
	public final static int MAX_WRITE_LEN_WAP = 10 * 1024;
	public final static int MAX_WRITE_LEN_NET = 30 * 1024;
	private int mMaxLen_Finish = MAX_WRITE_LEN_WIFI;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.softupdate);
        file = null;
        writeFile = null;
        urlString = getIntent().getExtras().getString("url");
        maxlength = getIntent().getExtras().getInt("filesize");
        versionnumber = getIntent().getExtras().getString("versionnumber");
        grogress = (ProgressBar)findViewById(R.id.grogress);
        if(urlString != null){
        	getFileName(urlString);
        	if(new File(CommonUtil.dir_download+"/"+filename).exists()){
        		mHandler.sendEmptyMessage(991122);
        	}else{
//        		mConnectUtil = new ConnectUtil(this, mHandler, 0);
//        		mConnectUtil.connect(urlString, HttpThread.TYPE_FILE, 0);
        		new Thread(){
        			public void run(){
        				connect();
        			}
        		}.start();
        	}
        }
        OfflineLog.writeSoftUpdate();
	}
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case MessageID.MESSAGE_CONNECT_DOWNLOADOVER:
				curlength = msg.arg1;
				LogPrint.Print("download","curlen = "+curlength);
				LogPrint.Print("download","maxlength = "+maxlength);
				download((byte[])msg.obj);
				break;
			case 991122:
				install();
				break;
			}
		}
		
	};
	
	private void getFileName(String url){
		filename = "iCuiniao_"+URLUtil.fid+"_"+versionnumber+".apk";
		if(filename != null){
			tmpname = filename.substring(0, filename.indexOf("."))+TMP;
		}
	}
	
	private void download(byte[] data){
		if(maxlength > 0){
			if(filename != null&&tmpname != null){
				if(maxlength > 0){
					int progress = (curlength*100/maxlength);
					LogPrint.Print("download",progress+"%");
					grogress.setProgress(progress);
				}
				LogPrint.Print("download","download");
				try {
					if(file == null){
						writeFile = new File(CommonUtil.dir_download+"/"+tmpname);
						if(!writeFile.exists()){//不存在则创建
							writeFile.createNewFile();
						}
						file = new RandomAccessFile(writeFile, "rwd");
						file.seek(0);
					}
					if(curlength < maxlength){
						LogPrint.Print("download","downloading");
						file.write(data);
					}else{
						LogPrint.Print("download","download over");
						grogress.setProgress(100);
						file.write(data);
						if(file != null){
							file.close();
						}
						//命名为真实文件
						writeFile.renameTo(new File(CommonUtil.dir_download+"/"+filename));
						//删除临时文件
						File tmpFile = new File(CommonUtil.dir_download+"/"+tmpname);
						if(tmpFile.exists()){
							tmpFile.delete();
						}
						mHandler.sendEmptyMessageDelayed(991122, 1000);
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
	}
	
	private void install(){
		try {
			//创建URI  
	        Uri uri=Uri.fromFile(new File(CommonUtil.dir_download+"/"+filename));
	        //创建Intent意图  
	        Intent intent=new Intent(Intent.ACTION_VIEW);  
	        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//启动新的activity  
	        //设置Uri和类型  
	        intent.setDataAndType(uri, "application/vnd.android.package-archive");  
	        //执行安装  
	        startActivity(intent);
	        finish();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 * 判断是否推送页面(text/vnd.wap.wml)
	 * 
	 * @param contentType
	 * @return
	 */
	public boolean isMobileWap(String contentType) {
		if (contentType == null)
			return false;
		if (contentType.indexOf("text/vnd.wap.wml") >= 0) {
			return true;
		}
		return false;
	}
	
	private void connect(){
		try {
			boolean isNetWorkOpen = CommonUtil.isNetWorkOpen(this);
			if(isNetWorkOpen){
				String temp = CommonUtil.getApnType(this);
				if (temp.toLowerCase().indexOf("ct") >= 0) {
					proxyAddr = "10.0.0.200";
				}
				if (temp.toLowerCase().indexOf("wifi") >= 0) {
					APN = 0;
					mMaxLen_Finish = MAX_WRITE_LEN_WIFI;
				} else if (temp.toLowerCase().indexOf("wap") >= 0) {
					APN = 1;
					mMaxLen_Finish = MAX_WRITE_LEN_WAP;
				} else {
					APN = 2;
					mMaxLen_Finish = MAX_WRITE_LEN_NET;
				}
			}
			URL url = new URL(urlString);
			HttpURLConnection httpConnection = null;
			Proxy proxy;
			switch (APN) {
			case 0:// wifi
			case 2:// cmnet
				httpConnection = (HttpURLConnection)url.openConnection();
				break;
			case 1:// cmwap
				proxy = new Proxy(Type.HTTP,new InetSocketAddress(proxyAddr, proxyPort));
				httpConnection = (HttpURLConnection)url.openConnection(proxy);
				break;
			}
			httpConnection.setConnectTimeout(6000);
			if (isMobileWap(httpConnection.getContentType())) {
				connect();
				return;
			}
			onSetSize(httpConnection.getContentLength(), 0);
			InputStream in = httpConnection.getInputStream();
			if(in != null){
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				curPos = 0;
				byte[] data = new byte[BUFFER];
				int bytesRecv = -1;
				int bufSize = BUFFER;
				int i = 0,l = 0;
				int count = 0;
				while (bytesRecv != 0) {
					if ((bytesRecv = in.read(data, 0, bufSize)) < 0) {
						LogPrint.Print("download","over = "+curPos);
						break;
					}
					baos.write(data, 0, bytesRecv);
					curPos += bytesRecv;
					
					i = curPos / mMaxLen_Finish;
					if (i > count) {
						l = baos.size();
						buffer = baos.toByteArray();
						baos.reset();
						onCurDataPos(curPos, 0);
						onFinish(buffer, l, false, 0);
						count = i;
					}
				}
				l = baos.size();
				LogPrint.Print("download","l = "+l);
				if (l > 0) {
					buffer = baos.toByteArray();
					baos.reset();
					if (curPos == maxlength){
						LogPrint.Print("download","true");
						onCurDataPos(curPos, 0);
						onFinish(buffer, l, true, 0);
					}
					else{
						LogPrint.Print("download","false");
						onCurDataPos(curPos, 0);
						onFinish(buffer, l, false, 0);
					}
					count = i;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			onFinish(null, 0, false, 0);// 下载结束
			onError(102, this.getString(R.string.net_busying),0,HttpThread.TYPE_PAGE);
		}
	}

	@Override
	public void onCurDataPos(int dataPos, int index) {
		// TODO Auto-generated method stub
		_curPos = dataPos;
	}

	@Override
	public void onError(int code, String message, int index,int type) {
		// TODO Auto-generated method stub
		Message msg = new Message();
		msg.obj = "杯具了- -!\n联网不给力啊";
		msg.what = MessageID.MESSAGE_CONNECT_ERROR;
		mHandler.sendMessage(msg);
	}

	@Override
	public void onFinish(byte[] data, int size, boolean isOver, int index) {
		// TODO Auto-generated method stub
		byte[] result = data;
		if(size <= 0){
            return;
        }
		if(size != data.length){
            result = new byte[size];
            System.arraycopy(data,0,result,0,size);
        }
		Message msg = new Message();
		msg.what = MessageID.MESSAGE_CONNECT_DOWNLOADOVER;
		msg.obj = result;
		msg.arg1 = _curPos;
		mHandler.sendMessage(msg);
	}

	@Override
	public void onGetContentType(String contentType, int index) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetUrl(String url, int type, int index) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSetSize(int size, int index) {
		// TODO Auto-generated method stub
		maxlength = size;
	}

	@Override
	public void onApnType(String apn, int index) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onImageError(int code, String message, int index) {
		
	}
}
