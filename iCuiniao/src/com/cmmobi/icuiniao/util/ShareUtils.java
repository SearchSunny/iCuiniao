package com.cmmobi.icuiniao.util;

import java.io.ByteArrayOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;

import com.cmmobi.icuiniao.R;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;

public class ShareUtils {
	//微信版本号
    private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;
	private String description = "";
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * 微信分享 发送消息
	 * @param wxData
	 */
	public void wxSend(Bitmap temp, Context context, IWXAPI api, String wapUrl){
	
		//设置分享图片的大小,不能超过32KB//	
		temp = CommonUtil.resizeImage(temp, 110, 110);
		WXWebpageObject webpage = new WXWebpageObject();
		//第三方展示页面
		webpage.webpageUrl = wapUrl;
		WXMediaMessage msg = new WXMediaMessage(webpage);
		//微信分享标题
		msg.title =context.getString(R.string.wx_share_title);
		//商品描述
		msg.description = description;
		msg.thumbData = bmpToByteArray(temp,true);
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("webpage");
		req.message = msg;
		api.sendReq(req);
	}
	
	//用于标识一个唯一请求
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
	
	/**
	 * 
	 * @param bmp 位图对象
	 * @param needRecycle 是否回收
	 * @return
	 */
	public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, 100, output);
		if (needRecycle) {
			bmp.recycle();
		}
		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * 微信朋友圈分享 发送消息
	 * @param wxData
	 */
	public void wxFreindSend(Bitmap temp, Context context, IWXAPI api, String wapUrl){
	
		//设置分享图片的大小,不能超过32KB//	
		temp = CommonUtil.resizeImage(temp, 110, 110);
		WXWebpageObject webpage = new WXWebpageObject();
		//第三方展示页面
		webpage.webpageUrl = wapUrl;
		WXMediaMessage msg = new WXMediaMessage(webpage);
		//微信分享标题
		msg.title =context.getString(R.string.wx_share_title);
		//商品描述
		msg.description = description;
		msg.thumbData = bmpToByteArray(temp,true);
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("webpage");
		req.message = msg;
		req.scene = SendMessageToWX.Req.WXSceneTimeline;
		api.sendReq(req);
	}
	/**
	 * 检测是否支持微信朋友圈
	 * @return
	 */
	public boolean isWeiXinFreind(Context context, IWXAPI api){
		//当前微信版本号
		int wxSdkVersion = api.getWXAppSupportAPI();
		if (wxSdkVersion >= TIMELINE_SUPPORTED_VERSION) {
			return true;
		} else {
			CommonUtil.ShowToast(context, "此版本不支持!");
			return false;
		}
	}
}
