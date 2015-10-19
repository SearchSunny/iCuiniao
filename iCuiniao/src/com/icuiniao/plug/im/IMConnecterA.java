/**
 * 
 */
package com.icuiniao.plug.im;

import java.net.InetSocketAddress;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import android.content.Context;
import android.content.Intent;

import com.cmmobi.icuiniao.util.ActionID;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.UserUtil;

/**
 * @author hw
 *连接器
 */
public class IMConnecterA {

	private Context mContext;
	private IoConnector connector;
	private IoSession session;
	private Entity pe;
	private boolean isConnectSuccess;//是否连接成功，在tag3返回后为连接成功
	private String host;
	private int port;
	
	public IMConnecterA(Context context,String host,int port){
		mContext = context;
		connector = null;
		session = null;
		isConnectSuccess = false;
		this.host = host;
		this.port = port;
	}
	
	public void connect(){
		LogPrint.Print("im","准备连接");
		connector = new NioSocketConnector();
		connector.setConnectTimeoutMillis(30000);
		connector.getFilterChain().addLast("mycoder", new ProtocolCodecFilter(new IMCoderFactory()));
		IMClientHandler clientHandler = new IMClientHandler();
		clientHandler.setContext(mContext);
		connector.setHandler(clientHandler);
		
		try {
			ConnectFuture future = connector.connect( new InetSocketAddress(host , port ));
			future.awaitUninterruptibly();
			session = future.getSession();
			LogPrint.Print("im","准备连接完成 ");
		} catch (Exception e) {
			// TODO: handle exception
			LogPrint.Print("im","准备连接出错："+e.toString());
		}
		
		if(session == null){
			LogPrint.Print("im","error: session = null");
			mContext.sendBroadcast(new Intent(ActionID.ACTION_BROADCAST_SOCKET_IOERROR));
		}else{
			//客户端请求连接,tag:0
			if(!CommonUtil.isNetWorkOpen(mContext)){
				Intent intent = new Intent(ActionID.ACTION_BROADCAST_RESPONSE_SENDMESSAGE_FAIL);
				intent.putExtra("code", 0);
				mContext.sendBroadcast(intent);
			}else{
				short tag = 0;
				pe = new Entity();
				pe.setTag(tag);
				if(UserUtil.userid <= 0){//避免开机自启动时漏掉id,先取一遍
					UserUtil.userid = CommonUtil.getUserId(mContext);
				}
				pe.setUserId(UserUtil.userid);
				pe.setDeviceId(CommonUtil.getIMEI(mContext));
				session.write(pe);
				session.getCloseFuture().awaitUninterruptibly(); // 等待连接断开
			}
		}
	}
	
	public boolean isConnected(){
		if(connector == null){
			return false;
		}
		if(session == null){
			return false;
		}
		return connector.isActive()&&session.isConnected();
	}
	
	public boolean isClosed(){
		if(connector == null){
			return true;
		}
		return connector.isDisposed();
	}
	
	public void closeSocket(){
		if(connector == null){
			return;
		}
		if(session == null){
			return;
		}
		try {
			setIsConnectSuccess(false);
			session.close(false);
			connector.dispose();
			LogPrint.Print("im","==========socket close==========");
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			session = null;
			connector = null;
		}
	}
	
	public void setIsConnectSuccess(boolean b){
		isConnectSuccess = b;
	}
	
	//发起新请求
	public boolean write(Entity pe){
		boolean result = false;
		if(!CommonUtil.isNetWorkOpen(mContext)){
			Intent intent = new Intent(ActionID.ACTION_BROADCAST_RESPONSE_SENDMESSAGE_FAIL);
			intent.putExtra("code", 0);
			mContext.sendBroadcast(intent);
		}else{
			if(isConnectSuccess){
				if(connector != null&&session != null){
					session.write(pe);
					result = true;
				}
			}
			
			if(!result){
				Intent intent = new Intent(ActionID.ACTION_BROADCAST_RESPONSE_SENDMESSAGE_FAIL);
				intent.putExtra("code", 1);
				mContext.sendBroadcast(intent);
			}
		}
		return result;
	}
	
	public void printSession(){
		if(session == null){
			LogPrint.Print("im","session = null | userid = "+UserUtil.userid+" | userstate = "+UserUtil.userState);
		}else{
			LogPrint.Print("im","session = "+session.toString()+" | userid = "+UserUtil.userid+" | userstate = "+UserUtil.userState);
		}
	}
}
