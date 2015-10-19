package com.cmmobi.icuiniao.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.Activity.Activity;
import com.cmmobi.icuiniao.Activity.ShareActivity;
import com.cmmobi.icuiniao.util.CommonUtil;
import com.cmmobi.icuiniao.util.ConnectUtil;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.MessageID;
import com.cmmobi.icuiniao.util.URLUtil;
import com.cmmobi.icuiniao.util.UserUtil;
import com.cmmobi.icuiniao.util.WXAppID;
import com.cmmobi.icuiniao.util.connction.HttpThread;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.ConstantsAPI;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
/**
 * 接收微信的请求及返回值
 * @author XP
 *
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler{
	
	// IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogPrint.Print("console", "WXEntryActivity====================");
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
    	api = WXAPIFactory.createWXAPI(this, WXAppID.APP_ID, false);
        
        api.handleIntent(getIntent(), this);
    }

    @Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		setIntent(intent);
        api.handleIntent(intent, this);
	}
	// 微信发送请求到第三方应用时，会回调到该方法
	@Override
	public void onReq(BaseReq req) {
		switch (req.getType()) {
		//微信向第三方app请求消息数据
		case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
			//暂时不用
			//goToGetMsg();		
			LogPrint.Print("console", "onReq..................GETMESSAGE");
			break;
			//微信向第三方app请求显示消息数据
		case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
			//goToShowMsg((ShowMessageFromWX.Req) req);
			LogPrint.Print("console", "onReq..................SHOWMESSAGE");
			break;
		default:
			break;
		}
	}

	// 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
	@Override
	public void onResp(BaseResp resp) {
		int result = 0;		
		switch (resp.errCode) {
		//正确返回
		case BaseResp.ErrCode.ERR_OK:
			result = R.string.wx_share_err_ok;
			new ConnectUtil(this, mHandler, 0).connect(URLUtil.URL_SHARE_UPLAOD+"?logintype=13"+"&oid="+UserUtil.userid+"&cid="+ ShareActivity.commodityid, HttpThread.TYPE_PAGE, 0);
			break;
		//用户取消
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			result = R.string.wx_share_err_user_cancel;
			break;
		//发送失败
		case BaseResp.ErrCode.ERR_SENT_FAILED:
			result = R.string.wx_share_err_sent_failed;
			break;
		default:
			//不支持错误
			result = R.string.wx_share_err_unsupport;
			break;
		}
		LogPrint.Print("console", "result ===="+result);
		CommonUtil.ShowToast(WXEntryActivity.this, getString(result));
		//Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
		finish();
	}
	
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case MessageID.MESSAGE_CONNECT_DOWNLOADOVER:
				if("text/json".equals(msg.getData().getString("content_type"))){
					Json((byte[])msg.obj,msg.arg1);
				}
			}
		}
	};
	
	private void Json(byte[] data,int threadindex){
		try {
			String str = new String(data,"UTF-8");
			str = CommonUtil.formUrlEncode(str);
			LogPrint.Print("wxentry json = "+str);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void goToGetMsg() {
		Intent intent = new Intent(this, GetFromWXActivity.class);
		intent.putExtras(getIntent());
		startActivity(intent);
		finish();
	}
}