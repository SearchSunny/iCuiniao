/**
 * 
 */
package com.cmmobi.icuiniao.Activity;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.onlineEngine.activity.MainPageActivityA;
import com.cmmobi.icuiniao.onlineEngine.activity.MyPageActivityA;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.OfflineLog;
import com.cmmobi.icuiniao.util.TimerForRock;
import com.cmmobi.icuiniao.util.UserUtil;
import com.icuiniao.plug.im.IMService;

/**
 * @author hw
 *js回调接口
 */
public class JSDate {
	private Context context;
//	private final static String MODE = "1";
//	private final static String HAND = "0";
	private final static String MODE = MyPageActivityA.BUTTONMODE;
	private final static String HAND = MyPageActivityA.RIGHTHAND; //lyb redo at 2013.06.08
	public JSDate(Context context){
		this.context = context;
	}

	/**登陆</br>
	 * {"result":"true","oid":"用户id","name":"昵称","gender":"0","logintype":"0"}</br>
	 * gender 0:男,1:女</br>
	 * logintype 自方平台登陆：0,QQ登陆：1,新浪微博登陆：2,淘宝登陆：3,微信:4
	 * */
	public void event_login(String json){
		try {
			String str = new String(json.getBytes("UTF-8"));
			str = CommonUtil.formUrlEncode(str);
			LogPrint.Print("webview","event_login = "+str);
			JSONObject jObject = new JSONObject(str);
			String result = jObject.getString("result");
			if(result != null){
				if(result.equalsIgnoreCase("true")){
					String oid = jObject.getString("oid");
					String name = jObject.getString("name");
					String gender = jObject.getString("gender");
					UserUtil.logintype = jObject.getInt("logintype");
					//所在地
					String location = jObject.getString("location");
					//真实姓名
					String buyerName = jObject.getString("buyerName");
					//生日
					String birthday = jObject.getString("birthday");
					//兴趣爱好
					String interest = jObject.getString("interest");
					//职业
					String occupational = jObject.getString("occupational");
					//教育
					String study = jObject.getString("study");
					//婚姻
					String marriage = jObject.getString("marriage");
					//收入
					String salary = jObject.getString("salary");
					//喜欢好友可见
					int likeperm = jObject.getInt("likeperm");
					//公开分享
					int sharaperm = jObject.getInt("sharaperm");
					//公开评论
					int commperm = jObject.getInt("commperm");
					//允许任何人加好友
					int addfriendperm = jObject.getInt("addfriendperm");
					//mode
					int bIsGestureMode = jObject.getInt("bIsGestureMode");
					//hand
					int bIsLeftMode = jObject.getInt("bIsLeftMode");
					//显示商品价格
					boolean isShowPrice = jObject.getBoolean("isShowPrice");
					//显示商品特征
					boolean isShowFeature = jObject.getBoolean("isShowFeature"); 
					if(oid != null){
						UserUtil.userid = Integer.parseInt(oid);
					}
					UserUtil.username = name;
					if(gender != null){
						UserUtil.gender = Integer.parseInt(gender);
					}else{
						UserUtil.gender = -1;
					}
					if(UserUtil.userid != -1&&UserUtil.username != null){
						UserUtil.isNewLoginOrExit = true;
						TimerForRock.reset();
						LogPrint.Print("=============oid = "+UserUtil.userid);
						LogPrint.Print("=============name = "+UserUtil.username);
						LogPrint.Print("=============gender = "+UserUtil.gender);
						LogPrint.Print("=============location = "+location);
						CommonUtil.saveUserId(context, UserUtil.userid);
						CommonUtil.saveUserName(context, UserUtil.username);
						CommonUtil.saveLoginType(context, UserUtil.logintype);
						CommonUtil.saveGender(context, UserUtil.gender);
						CommonUtil.saveArea(context, location);
						CommonUtil.saveLikeFriendState(context, likeperm==1?true:false);
						CommonUtil.saveShareFriendState(context,sharaperm==0?true:false);
						CommonUtil.saveBaskFriendState(context, commperm==0?true:false);
						CommonUtil.saveAllAddFriendState(context, addfriendperm==0?true:false);
						CommonUtil.saveRealName(context, buyerName);
						CommonUtil.saveBirthday(context, birthday);
						CommonUtil.saveInterest(context, interest);
						CommonUtil.saveOccuption(context, occupational);
						CommonUtil.saveMoney(context, salary);
						CommonUtil.saveEducation(context, study);
						CommonUtil.saveMarry(context, marriage);
						//新增
						UserUtil.isShowPrice = isShowPrice;
						CommonUtil.savePriceShow(context, UserUtil.isShowPrice);
						UserUtil.isShowFeature = isShowFeature;
						CommonUtil.saveFeatureShow(context, UserUtil.isShowFeature);
						//判断操作条参数
						if (bIsGestureMode!=-1&&bIsLeftMode!=-1) {
							CommonUtil.saveOperateMode(context, bIsGestureMode+"");
							CommonUtil.saveOperateHand(context, bIsLeftMode+"");
						}
						else{
							CommonUtil.saveOperateMode(context, MODE);
							CommonUtil.saveOperateHand(context, HAND);
						}
						UserUtil.userState = 1;
						CommonUtil.saveUserState(context, UserUtil.userState);
						CommonUtil.ShowToast(context, UserUtil.username+"，\n等您好久了，您终于回来啦！");
						//先关闭，重新连接
						context.stopService(new Intent(context,IMService.class));
						//开启信息服务
						context.startService(new Intent(context,IMService.class));
						CommonUtil.saveMessageReceiverState(context, true);
						OfflineLog.writeMainMenu_MessageButton((byte)1);//写入离线日志
						Intent intent = new Intent("true");
						((Activity)context).setResult(Activity.RESULT_OK,intent);
						((Activity)context).finish();
						//登录操作后直接跳转到首页
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						intent.setClass(context, MainPageActivityA.class);
						((Activity)context).startActivity(intent);
						((Activity)context).overridePendingTransition(R.anim.menu_left_in_new, R.anim.menu_left_out_new);
					}
				}else{
					String msg = jObject.getString("msg");
					if(msg != null){
						CommonUtil.ShowToast(context, "囧！登不进去，再来一次！\n"+msg);
					}else{
						CommonUtil.ShowToast(context, "囧！登不进去，再来一次！");
					}
					Intent intent = new Intent("false");
					((Activity)context).setResult(Activity.RESULT_OK,intent);
					((Activity)context).finish();
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			Intent intent = new Intent("false");
			((Activity)context).setResult(Activity.RESULT_OK,intent);
			((Activity)context).finish();
		}
	}
	
	/**注册</br>
	 * {"result":"true","oid":"用户id","name":"昵称","gender":"0"}</br>
	 * gender 0:男,1:女</br>
	 * */
	public void event_regedit(String json){
		try{
			String str = new String(json.getBytes("UTF-8"));
			str = CommonUtil.formUrlEncode(str);
			LogPrint.Print("webview","event_regedit = "+str);
			JSONObject jObject = new JSONObject(str);
			String result = jObject.getString("result");
			if(result != null){
				if(result.equalsIgnoreCase("true")){
					String oid = jObject.getString("oid");
					String name = jObject.getString("name");
					String gender = jObject.getString("gender");
					if(oid != null){
						UserUtil.userid = Integer.parseInt(oid);
					}
					UserUtil.username = name;
					if(gender != null){
						UserUtil.gender = Integer.parseInt(gender);
					}else{
						UserUtil.gender = -1;
					}
					if(UserUtil.userid != -1&&UserUtil.username != null){
						UserUtil.isNewLoginOrExit = true;
						TimerForRock.reset();
						LogPrint.Print("=============oid = "+UserUtil.userid);
						LogPrint.Print("=============name = "+UserUtil.username);
						LogPrint.Print("=============gender = "+UserUtil.gender);
						CommonUtil.saveUserId(context, UserUtil.userid);
						CommonUtil.saveUserName(context, UserUtil.username);
						UserUtil.logintype = 0;
						CommonUtil.saveLoginType(context, UserUtil.logintype);
						CommonUtil.saveGender(context, UserUtil.gender);
						CommonUtil.saveLikeFriendState(context, false);
						CommonUtil.saveShareFriendState(context,true);
						CommonUtil.saveBaskFriendState(context, true);
						CommonUtil.saveAllAddFriendState(context, true);
						//清空操作提示
						CommonUtil.savePromptStatu(context, "0");
						CommonUtil.savePromptLikeStatu(context, "0");
						CommonUtil.savePromptVideoStatu(context, "0");
						//清空商品价格和特征的显示设置
						CommonUtil.savePriceShow(context, false);
						CommonUtil.saveFeatureShow(context, true);
						UserUtil.isShowFeature = true;
						UserUtil.isShowPrice = false;
						//
						UserUtil.userState = 1;
						CommonUtil.saveUserState(context, UserUtil.userState);
						CommonUtil.ShowToast(context, "注册成功，主人你好，我是小C。");
						//先关闭，重新连接
						context.stopService(new Intent(context,IMService.class));
						//开启信息服务
						context.startService(new Intent(context,IMService.class));
						CommonUtil.saveMessageReceiverState(context, true);
						OfflineLog.writeMainMenu_MessageButton((byte)1);//写入离线日志
						Intent intent = new Intent("true");
						((Activity)context).setResult(Activity.RESULT_OK,intent);
						((Activity)context).finish();
						//登录操作后直接跳转到首页
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						intent.setClass(context, MainPageActivityA.class);
						((Activity)context).startActivity(intent);
					}
				}else{
					String msg = jObject.getString("msg");
					if(msg != null){
						CommonUtil.ShowToast(context, "囧！注册失败，再来一次！\n"+msg);
					}else{
						CommonUtil.ShowToast(context, "囧！注册失败，再来一次！");
					}
				}
				Intent intent = new Intent("false");
				((Activity)context).setResult(Activity.RESULT_OK,intent);
				((Activity)context).finish();
			}
		}catch(Exception e){
			Intent intent = new Intent("false");
			((Activity)context).setResult(Activity.RESULT_OK,intent);
			((Activity)context).finish();
		}
	}
	
	/**分享</br>
	 * {"result":"true","oid":"用户id","name":"昵称","gender":"0","logintype":"0"}</br>
	 * gender 0:男,1:女</br>
	 * logintype 自方平台登陆：0,QQ登陆：1,新浪微博登陆：2,淘宝登陆：3,微信:4
	 * */
	public void event_share(String json){
		try {
			String str = new String(json.getBytes("UTF-8"));
			str = CommonUtil.formUrlEncode(str);
			LogPrint.Print("webview","event_share = "+str);
			JSONObject jObject = new JSONObject(str);
			String result = jObject.getString("result");
			if(result != null){
				if(result.equalsIgnoreCase("true")){
					CommonUtil.saveUserState(context, UserUtil.userState);
					CommonUtil.ShowToast(context, "分享成功");
					((Activity)context).finish();
//					String oid = jObject.getString("oid");
//					String name = jObject.getString("name");
//					String gender = jObject.getString("gender");
//					UserUtil.logintype = jObject.getInt("logintype");
//					if(oid != null){
//						UserUtil.userid = Integer.parseInt(oid);
//					}
//					UserUtil.username = name;
//					if(gender != null){
//						UserUtil.gender = Integer.parseInt(gender);
//					}else{
//						UserUtil.gender = -1;
//					}
//					if(UserUtil.userid != -1&&UserUtil.username != null){
//						UserUtil.isNewLoginOrExit = true;
//						LogPrint.Print("=============oid = "+UserUtil.userid);
//						LogPrint.Print("=============name = "+UserUtil.username);
//						LogPrint.Print("=============gender = "+UserUtil.gender);
//						CommonUtil.saveUserId(context, UserUtil.userid);
//						CommonUtil.saveUserName(context, UserUtil.username);
//						CommonUtil.saveLoginType(context, UserUtil.logintype);
//						CommonUtil.saveGender(context, UserUtil.gender);
//						UserUtil.userState = 1;
//						CommonUtil.saveUserState(context, UserUtil.userState);
//						CommonUtil.ShowToast(context, "分享成功");
//						//开启信息服务
//						if(CommonUtil.getMessageReceiverState(context)){
//							context.startService(new Intent(context,MessageReceiveService.class));
//							CommonUtil.saveMessageReceiverState(context, true);
//						}else{
//							context.stopService(new Intent(context,MessageReceiveService.class));
//							CommonUtil.saveMessageReceiverState(context, false);
//						}
//						((Activity)context).finish();
//					}
				}else{
					try{
						String msg = jObject.getString("msg");
						if(msg != null){
							CommonUtil.ShowToast(context, "分享失败！\n"+msg);
						}else{
							CommonUtil.ShowToast(context, "分享失败！");
						}
					}catch (Exception e) {
						// TODO: handle exception
						CommonUtil.ShowToast(context, "分享失败！");
					}
					try {
						String code = jObject.getString("code");
						if(code != null){
							LogPrint.Print("webview","event_share code = "+code);
						}
					} catch (Exception e) {
						// TODO: handle exception
					}
					((Activity)context).finish();
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			((Activity)context).finish();
		}
	}
	
	/**按钮点击</br>
	 * {"result":"true","click":"0"}</br>
	 * click 0:返回,1:菜单,2:找回密码
	 * */
	public void event_click(String json){
		try {
			String str = new String(json.getBytes("UTF-8"));
			str = CommonUtil.formUrlEncode(str);
			LogPrint.Print("webview","event_click = "+str);
			JSONObject jObject = new JSONObject(str);
			String result = jObject.getString("result");
			if(result != null){
				if(result.equalsIgnoreCase("true")){
					int chick = jObject.getInt("click");
					switch (chick) {
					case 0:
						((Activity)context).finish();
						break;
//					case 1:
//						mHandler.sendEmptyMessage(112233);
//						break;
//					case 2:
//						context.startActivity(new Intent(context, FindPasswordActivity.class));
//						break;
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			LogPrint.Print("webview",e.toString());
		}
	}
	
	/**错误</br>
	 * {"result":"false","msg":"错误信息"}</br>
	 * */
	public void event_error(String json){
		try {
			String str = new String(json.getBytes("UTF-8"));
			str = CommonUtil.formUrlEncode(str);
			LogPrint.Print("webview","event_error = "+str);
			JSONObject jObject = new JSONObject(str);
			String result = jObject.getString("result");
			if(result != null){
				String msg = jObject.getString("msg");
				if(msg != null){
					CommonUtil.ShowToast(context, msg);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**绑定/解除绑定</br>
	 * {"result":"true","bindtype":"0","msg":"信息","nickname":"昵称"}</br>
	 * bindtype：绑定操作的返回类型：0，解除绑定操作的返回类型：1
	 * */
	public void event_bind(String json){
		try {
			String str = new String(json.getBytes("UTF-8"));
			str = CommonUtil.formUrlEncode(str);
			LogPrint.Print("webview","event_bind = "+str);
			JSONObject jObject = new JSONObject(str);
			String result = jObject.getString("result");
			String bindtype = jObject.getString("bindtype");
			int binderid = -1;
			if(result != null){
				String msg = jObject.getString("msg");
				String nickname = jObject.getString("nickname");
				//绑定成功
				if(bindtype.equals("0")){
					binderid = jObject.getInt("binderid");
				}
				//绑定或者解绑成功,nickname有值
				if(result.equalsIgnoreCase("true")){
					if(msg != null){
						CommonUtil.ShowToast(context, msg);
					}
					Intent intent = new Intent("true");
					intent.putExtra("bindtype", jObject.getInt("bindtype"));
					intent.putExtra("nickname", nickname);
					intent.putExtra("binderid", binderid);
					((Activity)context).setResult(Activity.RESULT_OK,intent);
					((Activity)context).finish();
				}
				//绑定或者解绑失败,nickname空值
				else{
					if(msg != null){
						CommonUtil.ShowToast(context, msg);
					}
					Intent intent = new Intent("false");
					intent.putExtra("bindtype", jObject.getInt("bindtype"));
					intent.putExtra("nickname", nickname);
					((Activity)context).setResult(Activity.RESULT_OK,intent);
					((Activity)context).finish();
				}
				
			}
		} catch (Exception e) {
			// TODO: handle exception
			Intent intent = new Intent("false");
			intent.putExtra("bindtype", -1);
			((Activity)context).setResult(Activity.RESULT_OK,intent);
			((Activity)context).finish();
		}
	}
	
	/**邀请好友</br>
	 * {"result":"true","oid":"用户id","name":"昵称","gender":"0","logintype":"0"}</br>
	 * gender 0:男,1:女</br>
	 * logintype 自方平台登陆：0,QQ登陆：1,新浪微博登陆：2,淘宝登陆：3,微信:4
	 * */
	public void event_invite(String json){
		try {
			String str = new String(json.getBytes("UTF-8"));
			str = CommonUtil.formUrlEncode(str);
			LogPrint.Print("webview","event_invite = "+str);
			JSONObject jObject = new JSONObject(str);
			String result = jObject.getString("result");
			if(result != null){
				if(result.equalsIgnoreCase("true")){
					CommonUtil.saveUserState(context, UserUtil.userState);
					CommonUtil.ShowToast(context, "邀请成功");
					((Activity)context).finish();
				}else{
					try{
						String msg = jObject.getString("msg");
						if(msg != null){
							CommonUtil.ShowToast(context, "邀请失败！\n"+msg);
						}else{
							CommonUtil.ShowToast(context, "邀请失败！");
						}
					}catch (Exception e) {
						// TODO: handle exception
						CommonUtil.ShowToast(context, "邀请失败！");
					}
					try {
						String code = jObject.getString("code");
						if(code != null){
							LogPrint.Print("webview","event_invite code = "+code);
						}
					} catch (Exception e) {
						// TODO: handle exception
					}
					((Activity)context).finish();
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			((Activity)context).finish();
		}
	}
	
}
