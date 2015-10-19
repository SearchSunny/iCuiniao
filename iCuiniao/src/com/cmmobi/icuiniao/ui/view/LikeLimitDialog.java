package com.cmmobi.icuiniao.ui.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.onlineEngine.activity.StreampageActivity;
import com.cmmobi.icuiniao.util.URLUtil;

public class LikeLimitDialog extends Dialog{
	
	public LikeLimitDialog(final Context context) {
		super(context);
		new android.app.AlertDialog.Builder(getContext())
		.setMessage("你喜欢的商品数量已达上限，可以去掉部分不太喜欢的商品 ！").setPositiveButton("现在就去掉", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				Intent intent = new Intent();
				intent.putExtra("url", URLUtil.URL_MAINPAGE_LIKE);
				intent.putExtra("fromdialog", true);
				intent.setClass(context, StreampageActivity.class);
				context.startActivity(intent);
				((Activity)context).overridePendingTransition(R.anim.menu_left_in, R.anim.menu_left_out);
			}
		}).setNegativeButton("这次先算了", null).show();
	}
}
