package com.cmmobi.icuiniao.ui.view;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

public class LitviewNoOverScroll extends ListView{

	public LitviewNoOverScroll(Context context) {
		super(context);
		if (Integer.parseInt(Build.VERSION.SDK) >= 9) {
            this.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }		
	}
	
	public LitviewNoOverScroll(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (Integer.parseInt(Build.VERSION.SDK) >= 9) {
            this.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
	}

}
