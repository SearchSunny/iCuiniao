package com.cmmobi.icuiniao.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.os.Handler;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.cmmobi.icuiniao.R;

public class SystemProgress{

	private static boolean isRun;
	private int curPos;
	private int max;
	private int count;
	private ProgressBar progress;
	private AlertDialog ad;
	Handler handler = new Handler();
	public SystemProgress(Activity activity,Context context,String str){
		isRun = true;
		curPos = 0;
		max = 100;
		count = 0;
		LinearLayout l = (LinearLayout)activity.getLayoutInflater().inflate(R.layout.popprogress, null);
		progress = (ProgressBar)l.findViewById(R.id.proGress);
		
		ad = new AlertDialog.Builder(context).setView(l).show();
		ad.setOnCancelListener(new OnCancelListener(){

			public void onCancel(DialogInterface dialog) {
				close();
				cancelData();
			}});
		ad.setOnDismissListener(new OnDismissListener(){

			public void onDismiss(DialogInterface dialog) {
				close();
//				cancelData();
			}});
		
	}
	
//	public void setProgressPos(int pos){
//		curPos = pos;
//	}
//	
//	public void setProgressMaxWid(int width){
//		max = width;
//		progress.setMax(max);
//		handler.post(new Runnable(){
//
//			public void run() {
////				tIndex.setText(progress.getProgress()+"/"+progress.getMax());
//			}});
//	}
	
	//中断进度条增长线程
	private static void stop(){
		isRun = false;
	}
	
	public void close(){
		stop();
		try {
			ad.dismiss();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public int getMax(){
		return progress.getMax();
	}
	
	public void cancelData(){
		
	}
	
}
