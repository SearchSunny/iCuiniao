/**
 * 
 */
package com.icuiniao.plug.localmessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.cmmobi.icuiniao.util.ActionID;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.ConnectUtil;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.MHandlerThread;
import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.OfflineTimeService;
import com.cmmobi.icuiniao.util.URLUtil;
import com.cmmobi.icuiniao.util.UserUtil;
import com.cmmobi.icuiniao.util.connction.HttpThread;
import com.icuiniao.plug.im.IMDataBase;
import com.icuiniao.plug.im.IMService;

/**
 * @author hw
 *
 */
public class LocalMessageService extends Service{

	private Decoder decoder;
	private TagLocal tagLocal;
	private boolean isServiceStart;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		LogPrint.Print("local","======service oncreate======");
		doing();
		isServiceStart = true;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		LogPrint.Print("local","======service ondestory======");
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		LogPrint.Print("local","======service onstart======");
		if(isServiceStart == false){
			doing();
		}
	}

	private void doing(){
		if(downHandler == null){
			newHandler();
		}
		mHandler.sendEmptyMessage(336699);
	}
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 336699:
				//判断上一次获取时间,是否超过1天
				if(System.currentTimeMillis()-CommonUtil.getLocalMessageTime(LocalMessageService.this) > 3600*24*1000){
					LogPrint.Print("local","超过一天，重新获取");
					new ConnectUtil(LocalMessageService.this, downHandler, 0).connect(URLUtil.URL_LOCALMESSAGE, HttpThread.TYPE_PAGE, 0);
				}else{
					if(decoder == null||tagLocal == null){//重新获取
						LogPrint.Print("local","未超过一天，但数据空了，重新获取");
						decoder = null;
						tagLocal = null;
						new ConnectUtil(LocalMessageService.this, downHandler, 0).connect(URLUtil.URL_LOCALMESSAGE, HttpThread.TYPE_PAGE, 0);
					}else{
						LogPrint.Print("local","不需要重新获取");
						mHandler.removeMessages(337799);
						mHandler.sendEmptyMessage(337799);
					}
				}
				break;
			case 337799://解析后的逻辑处理
				eventDate();
				mHandler.removeMessages(336699);
				mHandler.sendEmptyMessageDelayed(336699, 60*30*1000);//半小时检测一次
				//检测另外两个服务是否运行，守护
				if(UserUtil.isRemoteLogin == false){
					if(CommonUtil.isServiceRunning(LocalMessageService.this, "com.icuiniao.plug.im.IMService") == false){
						startService(new Intent(LocalMessageService.this, IMService.class));
					}
				}
				if(CommonUtil.isServiceRunning(LocalMessageService.this, "com.cmmobi.icuiniao.util.OfflineTimeService") == false){
					startService(new Intent(LocalMessageService.this, OfflineTimeService.class));
				}
				break;
			case 338899://消息发出
				long id = msg.arg1;
				String content = (String)msg.obj;
				LogPrint.Print("local","id = "+id+" | content = "+content);
				//发出消息广播
				Date now=new Date();
				SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
				String commenttime = sdf.format(now);
				
				Intent intent = new Intent(ActionID.ACTION_BROADCAST_RESPONSE_RECEIVE_SYSTEMMESSAGE);
				intent.putExtra("messageId", id);
				intent.putExtra("commentTime", commenttime);
				intent.putExtra("message", content);
				sendBroadcast(intent);
				//上报服务器
				new ConnectUtil(LocalMessageService.this, new Handler(), 1).connect(URLUtil.URL_UPLOADLOCALMESSAGE+"?oid="+UserUtil.userid+"&messid="+id, HttpThread.TYPE_PAGE, 1);
				break;
			}
		}
		
	};
	
	private MHandlerThread mHandlerThread;
	public Handler downHandler;
	//用于下载
	private void newHandler(){
		mHandlerThread = new MHandlerThread("LocalMessageService");
		mHandlerThread.start();
		downHandler = new Handler(mHandlerThread.getLooper(), mHandlerThread){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case MessageID.MESSAGE_CONNECT_DOWNLOADOVER:
					decoder = new Decoder();
					tagLocal = decoder.doDecode((byte[])msg.obj);
					CommonUtil.saveLocalMessageTime(LocalMessageService.this);
					mHandler.removeMessages(337799);
					mHandler.sendEmptyMessage(337799);
					break;
				}
			}
			
		};
	}
	
	//事件处理
	private void eventDate(){
		LogPrint.Print("local","eventDate");
		boolean isNewMessageId,isInActiveTime,isInShowTime,isAndroidPlatForm,isSexRight,isNotStartRight,isTypeRight,isCityRight,isBirthdayRight,isAgeRight,isSalaryRight;
		if(tagLocal != null){
			ArrayList<TagInfo> tagInfos = tagLocal.getLocal();
			if(tagInfos != null){
				LogPrint.Print("local","size = "+tagInfos.size());
			}
			for(int i = 0;tagInfos!=null&&i < tagInfos.size();i ++){
				isNewMessageId = isNewMessageId(tagInfos.get(i));
				isInActiveTime = isInActiveTime(tagInfos.get(i));
				isInShowTime = isInShowTime(tagInfos.get(i));
				isAndroidPlatForm = isAndroidPlatForm(tagInfos.get(i));
				isSexRight = isSexRight(tagInfos.get(i));
				isNotStartRight = isNotStartRight(tagInfos.get(i));
				isTypeRight = isTypeRight(tagInfos.get(i));
				isCityRight = isCityRight(tagInfos.get(i));
				isBirthdayRight = isBirthdayRight(tagInfos.get(i));
				isAgeRight = isAgeRight(tagInfos.get(i));
				isSalaryRight = isSalaryRight(tagInfos.get(i));
				LogPrint.Print("local","isNewMessageId = "+isNewMessageId);
				LogPrint.Print("local","isInActiveTime = "+isInActiveTime);
				LogPrint.Print("local","isInShowTime = "+isInShowTime);
				LogPrint.Print("local","isAndroidPlatForm = "+isAndroidPlatForm);
				LogPrint.Print("local","isSexRight = "+isSexRight);
				LogPrint.Print("local","isNotStartRight = "+isNotStartRight);
				LogPrint.Print("local","isTypeRight = "+isTypeRight);
				LogPrint.Print("local","isCityRight = "+isCityRight);
				LogPrint.Print("local","isBirthdayRight = "+isBirthdayRight);
				LogPrint.Print("local","isAgeRight = "+isAgeRight);
				LogPrint.Print("local","isSalaryRight = "+isSalaryRight);
				LogPrint.Print("local","===============================");
				if (isNewMessageId && isInActiveTime && isInShowTime
						&& isAndroidPlatForm && isSexRight && isNotStartRight
						&& isTypeRight && isCityRight && isBirthdayRight
						&& isAgeRight && isSalaryRight) {
					
					TagMessage message = tagInfos.get(i).getMessage();
					TagContent tagContent;
					int id;
					String content;
					if(message != null){
						id = message.getId();
						tagContent = message.getContent();
						if(tagContent != null){
							content = tagContent.getContent();
							Message msg = new Message();
							msg.what = 338899;
							msg.arg1 = id;
							msg.obj = content;
							mHandler.sendMessage(msg);
							try {
								Thread.sleep(500);
							} catch (Exception e) {
								// TODO: handle exception
							}
						}
					}
				}
			}
		}
	}
	
	//检查此id是否已经在数据库中
	private boolean isNewMessageId(TagInfo info){
		TagMessage message = info.getMessage();
		if(message != null){
			int id = message.getId();
			IMDataBase dataBase = new IMDataBase(this);
			return !dataBase.isInDB(id);
		}
		return false;
	}
	
	//是否在活动区间
	private boolean isInActiveTime(TagInfo info){
		TagMessage message = info.getMessage();
		if(message != null){
			String activeTimeStart = message.getActiveTimeStart();
			String activeTimeEnd = message.getActiveTimeEnd();
			if(activeTimeStart == null||activeTimeStart.length() <= 0){
				return true;
			}
			if(activeTimeEnd == null||activeTimeEnd.length() <= 0){
				return true;
			}
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				Date dStart = sdf.parse(activeTimeStart);
				Date dEnd = sdf.parse(activeTimeEnd);
				long curTime = System.currentTimeMillis();
				//比对是否在活动区间
				if(curTime >= dStart.getTime() && curTime <= dEnd.getTime()){
					return true;
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		
		return false;
	}
	
	//是否在显示时间区间
	private boolean isInShowTime(TagInfo info){
		TagMessage message = info.getMessage();
		if(message != null){
			String showTimeStart = message.getShowTimeStart();
			String showTimeEnd = message.getShowTimeEnd();
			if(showTimeStart == null||showTimeStart.length() <= 0){
				return true;
			}
			if(showTimeEnd == null||showTimeEnd.length() <= 0){
				return true;
			}
			SimpleDateFormat sdf=new SimpleDateFormat("HH:mm:ss");
			try {
				Date dStart = sdf.parse(showTimeStart);
				Date dEnd = sdf.parse(showTimeEnd);
				int sStart = dStart.getHours()*3600+dStart.getMinutes()*60+dStart.getSeconds();
				int sEnd = dEnd.getHours()*3600+dEnd.getMinutes()*60+dEnd.getSeconds();
				Date date = new Date(System.currentTimeMillis());
				int cur = date.getHours()*3600+date.getMinutes()*60+date.getSeconds();
				//对比是否在显示时间区间内
				if(cur >= sStart && cur <= sEnd){
					return true;
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return false;
	}
	
	//是否是制定平台显示
	private boolean isAndroidPlatForm(TagInfo info){
		TagUser user = info.getUser();
		if(user != null){
			int platform = user.getSystemPlatform();
			if(platform <= 1){
				return true;
			}
		}
		
		return false;
	}
	
	//性别是否比对正确
	private boolean isSexRight(TagInfo info){
		TagUser user = info.getUser();
		if(user != null){
			int sex = user.getSex();
			if(sex == -1)return true;
			if(sex == CommonUtil.getGender(this)){
				return true;
			}
		}
		
		return false;
	}
	
	//长时间未启动比对
	private boolean isNotStartRight(TagInfo info){
		TagUser user = info.getUser();
		if(user != null){
			int notstart = user.getNotStart();
			if(notstart == -1)return true;
			long last = CommonUtil.getLastExitTime(this);
			if(System.currentTimeMillis() - last >= notstart*3600*24*1000){
				return true;
			}
		}
		
		return false;
	}
	
	//用户身份比对
	private boolean isTypeRight(TagInfo info){
		TagUser user = info.getUser();
		if(user != null){
			int type = user.getType();
			if(type == -1)return true;
			//游客条件
			if(type == 0&&UserUtil.userState != 1){
				return true;
			}
			//用户条件
			if(type == 1&&UserUtil.userState == 1&&UserUtil.userid != -1){
				return true;
			}
		}
		
		return false;
	}
	
	//城市比对
	private boolean isCityRight(TagInfo info){
		TagUser user = info.getUser();
		if(user != null){
			String city = user.getCity();
			if(city == null||city.length() <= 0){
				return true;
			}
			String[] citys = city.split(",");
			if(citys!=null&&citys.length == 2){
				String local = CommonUtil.getArea(this);
				if(local != null&&local.length() > 0){
					String[] locals = local.split(" ");
					if(locals!=null&&locals.length == 2){
						//比对省市
						if(citys[0].equals(locals[0])&&citys[1].equals(locals[1])){
							return true;
						}
					}
				}
			}
		}
		
		return false;
	}
	
	//生日比对
	private boolean isBirthdayRight(TagInfo info){
		TagUser user = info.getUser();
		if(user != null){
			String birthdayStart = user.getBirthdayStart();
			String birthdayEnd = user.getBirthdayEnd();
			if(birthdayStart == null||birthdayStart.length() <= 0){
				return true;
			}
			if(birthdayEnd == null||birthdayEnd.length() <= 0){
				return true;
			}
			SimpleDateFormat sdf=new SimpleDateFormat("MM-dd");
			try {
				Date dStart = sdf.parse(birthdayStart);
				Date dEnd = sdf.parse(birthdayEnd);
				int sStart = dStart.getMonth()*30+dStart.getDate();
				int sEnd = dEnd.getMonth()*30+dEnd.getDate();
				String localBirthday = CommonUtil.getBirthday(this);
				if(localBirthday != null&&localBirthday.length() > 0){
					SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
					Date dLocal = sdf1.parse(localBirthday);
					int sLocal = dLocal.getMonth()*30+dLocal.getDate();
					//生日比对
					if(sLocal >= sStart && sLocal <= sEnd){
						return true;
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		
		return false;
	}
	
	//年龄比对
	private boolean isAgeRight(TagInfo info){
		TagUser user = info.getUser();
		if(user != null){
			int ageStart = user.getAgeStart();
			int ageEnd = user.getAgeEnd();
			if(ageStart == -1)return true;
			if(ageEnd == -1)return true;
			
			String localBirthday = CommonUtil.getBirthday(this);
			if(localBirthday != null&&localBirthday.length() > 0){
				try {
					SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
					Date dLocal = sdf1.parse(localBirthday);
					int year = dLocal.getYear();
					Date date = new Date(System.currentTimeMillis());
					int age = date.getYear()-year;
					//比对年龄
					if(age >= ageStart && age <= ageEnd){
						return true;
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
		
		return false;
	}
	
	//收入比对
	private boolean isSalaryRight(TagInfo info){
		TagUser user = info.getUser();
		if(user != null){
			int salaryStart = user.getSalaryStart();
			int salaryEnd = user.getSalaryEnd();
			if(salaryStart == -1)return true;
			if(salaryEnd == -1)return true;
			
			String moneyStr = CommonUtil.getMoney(this);
			int money = 0;
			if(moneyStr != null&&moneyStr.length() > 0){
				if(moneyStr.equals("2000以下")){
					money = 1999;
				}else if(moneyStr.equals("2000-5000")){
					money = 4999;
				}else if(moneyStr.equals("5000-10000")){
					money = 9999;
				}else if(moneyStr.equals("10000-20000")){
					money = 19999;
				}else if(moneyStr.equals("20000以上")){
					money = 20000;
				}
				//比对收入
				if(money >= salaryStart && money <= salaryEnd){
					return true;
				}
			}
		}
		
		return false;
	}
	
}
