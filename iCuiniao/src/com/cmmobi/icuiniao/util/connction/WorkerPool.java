/**
 * 
 */
package com.cmmobi.icuiniao.util.connction;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.StringTokenizer;

import android.content.Context;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.Activity.MessageReceiveService;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.LogPrint;

/**
 * @author hw
 * 任务工作池
 *复用型线程,使用任务机制处理事件
 */
public class WorkerPool extends Thread{

	private Context mContext;
	/** 线程开关，当线程已经被关闭时，该变量将被设置为true,以应该线程延迟关闭会写入数据的现象 */
	private boolean isStop;

	public static final int BUFFER = 1024;// 一次读1K

	private String host = "";
	private int port;
	private String other_url = "";

	/** 最大写入数据 */
	public final static int MAX_WRITE_LEN_WIFI = 50 * 1024;
	public final static int MAX_WRITE_LEN_WAP = 10 * 1024;
	public final static int MAX_WRITE_LEN_NET = 30 * 1024;
	private static final int MAX_RETRY = 5;

  	/** 下载地址缓冲 */
	private String urlBuffer;
	/** 下载缓冲区 */
	private byte[] buffer;
	/** 接口对象 */
	private HttpListener listener;
	/** 下载类型 */
	private int type;
	/** 重试次数 */
	private int mRetry;
	public final static int TYPE_PAGE = 0;
	public final static int TYPE_IMAGE = 1;

	private Socket socket;

	private int threadIndex;
	private int method;
	public final static String GET = "GET";
	protected final static String POST = "POST";

  	private int curPos = 0;
	
	private String content_type;
	private int content_length;

	private boolean isNetWorkOpen;
	private int APN;// 0 wifi 1 cmwap 2 cmnet
	private final static String wifi = "wifi";
	private final static String cmwap = "wap";
//	private final static String cmnet = "net";

	private static String proxyAddr = "10.0.0.172";
	private final static int proxyPort = 80;
	
	private long startTime;//联网开始的时间
	
	private final static int TIMEOUT_WIFI = 3000;
	private final static int TIMEOUT_WAP = 5000;
	private final static int TIMEOUT_NET = 5000;
	
	private ArrayList<Worker> pool;
	private String connectUrl;
	private int count;
	private boolean wait;
	public WorkerPool(Context context,HttpListener listener){
		mContext = context;
		this.listener = listener;
		isStop = false;
		pool = new ArrayList<Worker>();
		count = 0;
		wait = false;
		start();
	}
	
	public void addWorker(Worker worker){
		if(pool != null){
			pool.add(worker);
		}
	}
	
	public void clearWorker(){
		if(pool != null){
			pool.clear();
		}
	}
	
	/**任务完成,清除完成的任务*/
	public void workerFree(){
		try {
			if(pool != null){
				pool.remove(0);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public void finish(){
		isStop = true;
	}
	
	// 获得host,port,other
	private boolean getUrlInfo(String url) {
		int hoststart;
		int portstart;
		int otherstart;
		if (url.startsWith("http://")) {
			hoststart = 7;
		} else {
			hoststart = 0;
		}
		// 错误地址
		if (url.indexOf("xxx", hoststart) >= 0) {// http://xxxxxx
			return false;
		}
		if(url.indexOf(":",hoststart) > 0){
    		int pos = url.indexOf(":",hoststart);
    		int pos1 = url.indexOf("/",hoststart);
    		//:间没有/才是正确的
    		if(pos < pos1){
    			host = url.substring(hoststart, (portstart = url.indexOf(":",hoststart)));
    			String temp = url.substring(portstart+1, (otherstart = url.indexOf(File.separator, portstart)));
    			if(temp==null||temp.equals("")){
    				temp="80";
    			}
    			port = Integer.parseInt(temp);
    			
    			other_url = url.substring(otherstart);
      		}else{
    			if(url.indexOf(File.separator,hoststart) > 0){
        			host = url.substring(hoststart, (portstart = url.indexOf(File.separator,hoststart)));
        			otherstart = url.indexOf(File.separator, portstart);
        			port = 80;
        			other_url = url.substring(otherstart);
        		}else{
        			host = url.substring(hoststart);
        			port = 80;
        			other_url = "";
        		}
    		}
    	}else {
    		if(url.indexOf(File.separator,hoststart) > 0){
    			host = url.substring(hoststart, (portstart = url.indexOf(File.separator,hoststart)));
    			otherstart = url.indexOf(File.separator, portstart);
    			port = 80;
    			other_url = url.substring(otherstart);
    		}else{
    			host = url.substring(hoststart);
    			port = 80;
    			other_url = "";
    		}
    	}

		LogPrint.Print("worker", "host = "+host);
		LogPrint.Print("worker", "port = "+port);
		LogPrint.Print("worker", "other_url = "+other_url);
		urlBuffer = url;
		return true;
	}
	
	private SocketAddress isa = null;
	protected void startConnect(String requestType) {
		try {
			LogPrint.Print("worker", "startConnect");
			if (isStop)
				return;

			StringBuffer sb = new StringBuffer();
			switch (APN) {
			case 0:// wifi
				socket = new Socket();
				isa = new InetSocketAddress(host, port);
				if (!socket.isConnected()) {
					socket.connect(isa, TIMEOUT_WIFI);
				}
				sb.append(requestType + " " + other_url + " HTTP/1.1\r\n");
				sb.append("Host: " + host + ":" + port + "\r\n");
				break;
			case 1:// cmwap
				socket = new Socket();
				isa = new InetSocketAddress(proxyAddr, proxyPort);
				if (!socket.isConnected()) {
					socket.connect(isa, TIMEOUT_WAP);
				}
				sb.append(requestType + " " + urlBuffer + " HTTP/1.1\r\n");
				sb.append("Host: " + proxyAddr + ":80\r\n");
				break;
  			case 2:// cmnet
				socket = new Socket();
				isa = new InetSocketAddress(host, port);
				if (!socket.isConnected()) {
					socket.connect(isa, TIMEOUT_NET);
				}
				sb.append(requestType + " " + other_url + " HTTP/1.1\r\n");
				sb.append("Host: " + host + ":" + port + "\r\n");
				break;
			}
			sb.append("Accept: */*\r\n");
			sb.append("User-Agent: MAUI WAP Browser\r\n\r\n");
			OutputStream out = socket.getOutputStream();
			out.write(sb.toString().getBytes("UTF-8"));
			LogPrint.Print("worker", "startConnectOver");
		} catch (Exception ex) {
			closeSocket();
			if (mRetry++ < MAX_RETRY) {
				reconnectSocket();
				return;
			}
			workerFree();
			listener.onFinish(buffer, curPos, false, threadIndex);// 下载结束
			listener.onError(102, mContext.getString(R.string.net_busying),threadIndex,type);
			mRetry = 0;
		} finally {
		}
	}
	
	/**
	 * 关闭Socket
	 */
	public void closeSocket() {
		try {
			if (!socket.isInputShutdown() || !socket.isOutputShutdown() || !socket.isClosed()) {
				socket.close();
			}
		} catch (Exception ex) {
		}
	}
	
	/**
	 * 重连Socket
	 */
	private void reconnectSocket() {
		startConnect(method == 0 ? GET : POST);
	}
	
	/**
	 * 从Stream读取一行 并构造StringTokenizer
	 * 
	 * @param reader
	 * @return
	 */
	private StringTokenizer getLineForStream(Reader reader) {
		int c = 0;
		StringBuffer buf = new StringBuffer();

		try {
			while ((c = reader.read()) > 0) {
				if (c == '\n') {
					String line = buf.toString();
					return new StringTokenizer(line);
				}
				buf.append((char) c);
			}
			return null;
		} catch (IOException e) {
			return null;
		}
	}
	
	/**
	 * 从StringTokenizer中读取后续String
	 * 
	 * @param st
	 * @param index
	 *            向后查找多少位，从0开始
	 * @return 如果未找到返回null
	 */
  	private String getTokenizer(StringTokenizer st, int index) {
		int i = 0;
		while (st.hasMoreTokens()) {
			if (i >= index)
				return st.nextToken();
			st.nextToken();
			i++;
		}
		return null;
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
	
	private void waitResponse() {
		boolean done = false;
		int l = 0;
		int i = 0;
  		try {
			if (socket.isConnected() && !socket.isClosed()) {
				socket.setSoTimeout(10000);  //10秒超时
				InputStream in = socket.getInputStream();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				// --------------------读取Head开始---------------------
				if (in != null) {

					while (!done) {
						l = in.read();
						if (l < 0)
							break;
						switch (l) {
						case '\r':
							break;
						case '\n':
							if (i == 0)
								done = true;
							i = 0;
							break;
						default:
							i++;
							break;
						}
						baos.write(l);
						if (i > 100000)
							done = true;
					}

					if (l < 0) {
						reconnectSocket();
						return;
					}
  					// --------------------读取Head结束---------------------

					// --------------------解析code开始---------------------
					StringReader stringReader = new StringReader(baos.toString());
					StringTokenizer st = null;
					String right = "100";
					String left;
					while (right.equals("100")) {
						st = null;
						st = getLineForStream(stringReader);
						right = getTokenizer(st, 1);

						if (right.equals("100")) {
							right = "0";
							st = getLineForStream(stringReader);
						}
					}
					
					if(Integer.parseInt(right) > 400){
						LogPrint.Print("worker", "error: code > 400");
						workerFree();
						listener.onFinish(null, 0, false, threadIndex);// 下载结束
						listener.onError(102, mContext.getString(R.string.net_busying),threadIndex,type);
						closeSocket();
						return;
					}
					
					if (!"200".equals(right) && !"301".equals(right) && !"302".equals(right) && !"206".equals(right)) {
						closeSocket();
						if ("0".equals(right) && mRetry++ < MAX_RETRY) {
							reconnectSocket();
							return;
						}
						mRetry = 0;
						return;
					}
					// --------------------解析code结束---------------------

					// --------------------解析head开始---------------------
  					while (true) {
						st = getLineForStream(stringReader);
						if (st == null || "\r".equals(st.toString()))
							break;
						left = getTokenizer(st, 0);

						if (left != null) {
							if (left.compareToIgnoreCase("Content-Length:") == 0) {
								right = getTokenizer(st, 0);
								if (right != null) {
									content_length = Integer.parseInt(right);
									if(0==curPos)
									listener.onSetSize(content_length,threadIndex);
								}
							} else if (left.compareToIgnoreCase("Content-Type:") == 0) {
								right = getTokenizer(st, 0);
								if (right != null) {
									content_type = right;
									listener.onGetContentType(content_type,threadIndex);
								}
							} else if (left.compareToIgnoreCase("Connection:") == 0) {
								right = getTokenizer(st, 0);
							}
						}
					}
  					listener.onGetUrl(urlBuffer, type,threadIndex);

					// --------------------解析Head结束---------------------

					if (isMobileWap(content_type)) { // 遇到移动推送页 重连
						closeSocket();
						reconnectSocket();
						return;
					}

					// --------------------读取消息实体---------------------
					baos.reset();
					i = content_length;
					byte[] data = new byte[BUFFER];
					int bytesRecv = -1;
					int bufSize = BUFFER;
					if (content_length != 0) { // Http头里指定了消息实体的长度
						while (i != 0) {
							if (isStop) {
								closeSocket();
								return;
							}

							if (i < BUFFER)
								bufSize = i;
							if ((bytesRecv = in.read(data, 0, bufSize)) == 0) {
								break;
  							} else if (bytesRecv < 0) {
								// LogPrint.Print("tag", "recv <0");
								closeSocket();
								if (mRetry++ < MAX_RETRY) { // 发生了超时之类的 重启连接
									reconnectSocket();
									return;
								}
								mRetry = 0;
								return;
							}

							i -= bytesRecv;
//							LogPrint.Print("==================down Book Size================= :", "bytesRecv:" + bytesRecv);
							baos.write(data, 0, bytesRecv);
							curPos += bytesRecv;

						}
  					} else { // 没有指定Content-Length 需要判断是否是chunked（我们服务器没有进行GZip
						// 等压缩 无需要）
				while (bytesRecv != 0) {
					if (isStop) {
						closeSocket();
						return;
					}
					if ((bytesRecv = in.read(data, 0, bufSize)) < 0) {
						// LogPrint.Print("tag", "recv < 0");
						closeSocket();
						if (mRetry++ < 2) { // 发生了超时之类的 重启连接
							reconnectSocket();
							return;
						}
						mRetry = 0;
						return;
					}
					baos.write(data, 0, bytesRecv);
					curPos += bytesRecv;

				}
			}
			closeSocket();

			l = baos.size();
			if (l > 0) {
				workerFree();
				buffer = baos.toByteArray();
				baos.reset();
				if (curPos == content_length)
					listener.onFinish(buffer, l, true, threadIndex);
				else
					listener.onFinish(buffer, l, false, threadIndex);
			}
			LogPrint.Print("worker", "downLoadOver");
			LogPrint.Print("speed", "********download over = "+(System.currentTimeMillis()-startTime));
			LogPrint.Print("speed", "***************over*************");
		}
	}
  		} catch (Exception ex) {
			ex.printStackTrace();
			workerFree();
			listener.onFinish(null, 0, false, threadIndex);// 下载结束
			listener.onError(102, mContext.getString(R.string.net_busying),threadIndex,type);
			closeSocket();
		} finally {
			
		}

	}
	
	public boolean isEmpty(){
		if(pool == null)return true;
		if(pool.size() > 0){
			return false;
		}
		return true;
	}
	
	public void setWait(boolean b){
		wait = b;
	}
	
	@Override
	public void run() {
		while(!isStop){
			if(wait){
				Sleep(200);
				continue;
			}
//			LogPrint.Print("worker","===========run="+(count++));
			if(pool.size() > 0){
				connectUrl = pool.get(0).getUrl();
				type = pool.get(0).getType();
				threadIndex = pool.get(0).getIndex();
				startTime = System.currentTimeMillis();
				LogPrint.Print("speed", "*******************start***************");
				LogPrint.Print("speed", "********url = "+connectUrl);
				
				curPos = 0;
				content_type = null;
				content_length = 0;
				if (connectUrl == null) {
					workerFree();
					listener.onError(102, "请求的url为null",0,type);
					continue;
				} else {
					listener.onCurDataPos(curPos,0);
					if (getUrlInfo(connectUrl)) {
						isNetWorkOpen = CommonUtil.isNetWorkOpen(mContext);

						LogPrint.Print("worker", "isNetWorkOpen = "+isNetWorkOpen);
						if (isNetWorkOpen) {
							String temp = CommonUtil.getApnType(mContext);
							if (temp.toLowerCase().indexOf("ct") >= 0) {
								proxyAddr = "10.0.0.200";
							}
							if (temp.toLowerCase().indexOf(wifi) >= 0) {
								APN = 0;
								MessageReceiveService.CHICK_TIME = 30000;
								LogPrint.Print("worker","WIFI");
							} else if (temp.toLowerCase().indexOf(cmwap) >= 0) {
								APN = 1;
								MessageReceiveService.CHICK_TIME = 600000;
								LogPrint.Print("worker","CMWAP");
		  					} else {
								APN = 2;
								MessageReceiveService.CHICK_TIME = 600000;
								LogPrint.Print("worker","CMNET");
							}
							startConnect(GET);
							LogPrint.Print("speed", "********hand over = "+(System.currentTimeMillis()-startTime));
							LogPrint.Print("worker", "downLoadStart");
							waitResponse();
						} else {
							workerFree();
							listener.onError(102, mContext.getString(R.string.reminder_no_net),0,type);
						}
					}
				}
			}
			
			Sleep(100);
			
		}
	}
	
	private void Sleep(long time){
		try {
			sleep(time);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
}
