package com.cmmobi.icuiniao.wxapi;

import java.io.ByteArrayOutputStream;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.Activity.Activity;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.WXAppID;
import com.tencent.mm.sdk.openapi.GetMessageFromWX;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;
import com.tencent.mm.sdk.openapi.WXWebpageObject;

/**
 * 微信发出的向第三方应用请求app message
 * @author XP
 *
 */
public class GetFromWXActivity extends Activity {

	private IWXAPI api;
	private Bundle bundle;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		api = WXAPIFactory.createWXAPI(this, WXAppID.APP_ID);
		bundle = getIntent().getExtras();
		
		setContentView(R.layout.get_from_wx);
		initView();
	}
	
	@Override
	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		bundle = intent.getExtras();
	}
	
	private void initView() {
		//获取文本信息
		findViewById(R.id.get_text).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final EditText editor = new EditText(GetFromWXActivity.this);
				editor.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
				editor.setText("这是测试数据....");
				
				// 初始化一个WXTextObject对象
				WXTextObject textObj = new WXTextObject();
				textObj.text = editor.getText().toString();

				// 用WXTextObject对象初始化一个WXMediaMessage对象
				WXMediaMessage msg = new WXMediaMessage(textObj);
				msg.description = editor.getText().toString();
				
				// 构造一个Resp
				GetMessageFromWX.Resp resp = new GetMessageFromWX.Resp();
				// 将req的transaction设置到resp对象中，其中bundle为微信传递过来的intent所带的内容，通过getExtras方法获取
				resp.transaction = getTransaction();
				resp.message = msg;
				
				// 调用api接口响应数据到微信
				api.sendResp(resp);
				finish();
			}
		});
		
		
		//获取webpage信息
		findViewById(R.id.get_webpage).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//获取图片
				//byte[] wxData = CommonUtil.getSDCardFileByteArray(CommonUtil.dir_cache+"/"+CommonUtil.urlToNum(commodityImageString));
				Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
				getFromWX(thumb);
				finish();
			}
		});
	}
	
	public void getFromWX(Bitmap temp){
		//Bitmap temp = BitmapFactory.decodeByteArray(wxData, 0, wxData.length);
		//设置分享图片的大小,不能超过32KB
		temp = CommonUtil.resizeImage(temp, 110, 110);
		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = "http://www.baidu.com";
		WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.title = "Video Title";
		msg.description = "Video Description";
		msg.thumbData = bmpToByteArray(temp,true);
		GetMessageFromWX.Resp resp = new GetMessageFromWX.Resp();
		resp.transaction = getTransaction();
		resp.message = msg;
		
		api.sendResp(resp);
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
	private String getTransaction() {
		final GetMessageFromWX.Req req = new GetMessageFromWX.Req(bundle);
		return req.transaction;
	}
	
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
}
