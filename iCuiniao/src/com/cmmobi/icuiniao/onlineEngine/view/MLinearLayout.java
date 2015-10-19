/**
 * 
 */
package com.cmmobi.icuiniao.onlineEngine.view;

import android.content.Context;
import android.os.Handler;
import android.widget.LinearLayout;

import com.cmmobi.icuiniao.util.DownImageManager;
import com.cmmobi.icuiniao.util.LogPrint;
import com.cmmobi.icuiniao.util.MessageID;

/**
 * @author hw
 *
 *自定义布局,在页面文本显示完成后,发出通知
 */
public class MLinearLayout extends LinearLayout{

	private boolean onlyFlushOneTime;//只刷新一次
	private boolean isDownLoadOver;//页面数据是否返回了
	private Handler handler;
	
	public MLinearLayout(Context context, Handler handler) {
		super(context);
		// TODO Auto-generated constructor stub
		onlyFlushOneTime = false;
		isDownLoadOver = false;
		this.handler = handler;
		DownImageManager.clear();
	}
	
	public boolean isDownLoadOver(){
		return isDownLoadOver;
	}
	
	public void setIsDownLoadOver(boolean b){
		isDownLoadOver = b;
	}
	
	public void reSetOnlyFlushOneTime(){
		onlyFlushOneTime = false;
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		super.onLayout(changed, l, t, r, b);
		if(isDownLoadOver){
			if(onlyFlushOneTime == false){
				onlyFlushOneTime = true;
				LogPrint.Print("onLayout");
				//布局完成发
				handler.sendEmptyMessage(MessageID.MESSAGE_CONNECT_LAYOUTOVER);
			}
		}
	}
}
