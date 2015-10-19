/**
 * 
 */
package com.cmmobi.icuiniao.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONObject;

/**
 * @author hw
 *信息管理器逻辑
 */
public class MessgeManager {

	private ArrayList<MyMessage> messages;
	
	
	public MessgeManager(){
		messages = new ArrayList<MyMessage>();
	}
	
	
	
	//导入信息文件
	public void loadFile(String dir){
		try {
			byte[] data = CommonUtil.getSDCardFileByteArray(dir);
			int startPos = 0;
			int count = 0;
			for(int i = 0;data!=null&&i < data.length;i ++){
				if(i > 0){
					if(data[i-1] == (byte)0x0d&&data[i] == (byte)0x0a){
						byte[] tmp = new byte[i-startPos+1];
						System.arraycopy(data, startPos, tmp, 0, tmp.length);
						startPos = i+1;
						
						MyMessage myMessage = new MyMessage();
						myMessage.jsonObject = new JSONObject(new String(tmp,"utf-8"));
						myMessage.index = count;
						
						messages.add(myMessage);
						count ++;
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public int getMessageNum(int type){
		int size = 0;
		try {
			int from,to;
			if(type == 0){
				for(int i = 0;i < messages.size();i ++){
					from = messages.get(i).jsonObject.getInt("from");
					to = messages.get(i).jsonObject.getInt("to");
					if(from != 0&&to != 0){
						size ++;
					}
				}
			}else{
				for(int i = 0;i < messages.size();i ++){
					from = messages.get(i).jsonObject.getInt("from");
					to = messages.get(i).jsonObject.getInt("to");
					if(from == 0||to == 0){
						size ++;
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return size;
	}
	
	//返回用于信息管理器展示的数据项,0:用户消息,1:系统消息
	public ArrayList<MessageItem> getManagerList(int type){
		ArrayList<MessageItem> items = new ArrayList<MessageItem>();
		try {
			for(int i = 0;i < messages.size();i ++){
				int from = Integer.parseInt(messages.get(i).jsonObject.getString("from"));
				int to = Integer.parseInt(messages.get(i).jsonObject.getString("to"));
				String from_name = messages.get(i).jsonObject.getString("from_name");
				String to_name = messages.get(i).jsonObject.getString("to_name");
				String msg = messages.get(i).jsonObject.getString("msg");
				String time = messages.get(i).jsonObject.getString("time");
				int index = messages.get(i).index;
				MessageItem item = new MessageItem(from, to, from_name, to_name, msg, time,index);
				
				boolean isfind = false;
				for(int j = 0;j < items.size();j ++){
					if(items.get(j).getFrom() == from&&items.get(j).getTo() == to){//如果找到,则更新数据
						items.get(j).setMsg(msg);
						items.get(j).setTime(time);
						isfind = true;
						break;
					}
				}
				if(!isfind){
					if(type == 0){
						if(from != 0&&to != 0){
							items.add(item);
						}
					}else{
						if(from == 0||to == 0){
							items.add(item);
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return items;
	}
	
	//返回点对点私聊信息展示的数据项,limit为天数,-1为不限制
//	public ArrayList<MessageItem> getMessageList(int oid,int uid,int limit){
//		ArrayList<MessageItem> items = new ArrayList<MessageItem>();
//		int count = 0;
//		try {
//			for(int i = 0;i < messages.size();i ++){
//				int from = Integer.parseInt(messages.get(i).jsonObject.getString("from"));
//				int to = Integer.parseInt(messages.get(i).jsonObject.getString("to"));
//				String from_name = messages.get(i).jsonObject.getString("from_name");
//				String to_name = messages.get(i).jsonObject.getString("to_name");
//				String msg = messages.get(i).jsonObject.getString("msg");
//				String time = messages.get(i).jsonObject.getString("time");
//				int index = messages.get(i).index;
//				MessageItem item = new MessageItem(from, to, from_name, to_name, msg, time,index);
//				if((oid == from&&uid==to)||(oid==to&&uid==from)){
//					if(limit > 0){
//						if(bLoad(time, limit)){
//							count ++;
//							items.add(item);
//						}
//					}else{
//						count ++;
//						items.add(item);
//					}
//				}
//			}
//			//如果不够5条,则抓取最后的5条
//			if(items.size() <= 5){
//				items.clear();
//				for(int i = messages.size()-1;i >= 0;i --){
//					int from = Integer.parseInt(messages.get(i).jsonObject.getString("from"));
//					int to = Integer.parseInt(messages.get(i).jsonObject.getString("to"));
//					String from_name = messages.get(i).jsonObject.getString("from_name");
//					String to_name = messages.get(i).jsonObject.getString("to_name");
//					String msg = messages.get(i).jsonObject.getString("msg");
//					String time = messages.get(i).jsonObject.getString("time");
//					int index = messages.get(i).index;
//					MessageItem item = new MessageItem(from, to, from_name, to_name, msg, time,index);
//					if((oid == from&&uid==to)||(oid==to&&uid==from)){
//						if(count < 5){
//							items.add(0,item);
//							count ++;
//						}
//					}
//				}
//			}
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//		return items;
//	}
	
	//修改为只取最新的五条,新版本修改为15条
	public ArrayList<MessageItem> getMessageList(int oid,int uid,int limit){
		ArrayList<MessageItem> items = new ArrayList<MessageItem>();
		int count = 0;
		try {
			//如果不够5条,则抓取最后的5条
			for(int i = messages.size()-1;i >= 0;i --){
				int from = Integer.parseInt(messages.get(i).jsonObject.getString("from"));
				int to = Integer.parseInt(messages.get(i).jsonObject.getString("to"));
				String from_name = messages.get(i).jsonObject.getString("from_name");
				String to_name = messages.get(i).jsonObject.getString("to_name");
				String msg = messages.get(i).jsonObject.getString("msg");
				String time = messages.get(i).jsonObject.getString("time");
				int index = messages.get(i).index;
				MessageItem item = new MessageItem(from, to, from_name, to_name, msg, time,index);
				if((oid == from&&uid==to)||(oid==to&&uid==from)){
					if(limit < 0){
						items.add(0,item);
					}else{
						if(count < 15){
							items.add(0,item);
							count ++;
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return items;
	}
	
	//删除某项
	public void deleteItem(int index){
		messages.remove(index);
		StringBuffer sBuffer = new StringBuffer();
		try {
			for(int i = 0;i < messages.size();i ++){
				sBuffer.append(messages.get(i).jsonObject.toString()+"\r\n");
			}
			if(sBuffer.length() > 0){
				CommonUtil.writeToFileFromEnd(CommonUtil.dir_message_user+UserUtil.userid+CommonUtil.endName, sBuffer.toString().getBytes("utf-8"),true);
				sBuffer.delete(0, sBuffer.length());
				sBuffer = null;
			}else{
				CommonUtil.deleteAll(new File(CommonUtil.dir_message_user+UserUtil.userid+CommonUtil.endName));
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	//将日期换算成天
	private int getDayNum(String str){
		int day = 0;
		String tmp;
		if(str.length() > 10){//如2012-07-05
			tmp = str.substring(0, 10);
			String[] array = tmp.split("-");
			day += Integer.parseInt(array[0])*365;
			if(array[1].substring(0, 1).equalsIgnoreCase("0")){
				day += (Integer.parseInt(array[1].substring(1,2))-1)*30;
			}else{
				day += (Integer.parseInt(array[1])-1)*30;
			}
			if(array[2].substring(0, 1).equalsIgnoreCase("0")){
				day += Integer.parseInt(array[2].substring(1,2));
			}else{
				day += Integer.parseInt(array[2]);
			}
		}
		return day;
	}
	
	private int getCurDayNum(){
		int day = 0;
		
		Calendar rightNow = Calendar.getInstance();
    	int curYear = rightNow.get(Calendar.YEAR);
    	int curMonth = rightNow.get(Calendar.MONTH);
    	int curDay = rightNow.get(Calendar.DATE);
    	
    	day = curYear*365+curMonth*30+curDay;
		return day;
	}
	
	//是否加载
	private boolean bLoad(String date,int limit){
		if(getDayNum(date)+limit >= getCurDayNum()){
			return true;
		}
		return false;
	}
	
	public class MyMessage{
		public JSONObject jsonObject;
		public int index;
	}
}
