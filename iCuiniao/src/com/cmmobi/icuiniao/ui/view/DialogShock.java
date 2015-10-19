package com.cmmobi.icuiniao.ui.view;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.cmmobi.icuiniao.R;

public class DialogShock extends Dialog {

	private String text = "";
	

	public DialogShock(Context context, int theme, String text) {
		super(context, theme);
		this.text = text;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.dialogshock);
		TextView tv = (TextView)findViewById(R.id.diaText);
		tv.setText(text);
		tv.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DialogShock.this.cancel();
				
			}
		});
		setCancelable(true);	//点击返回键消失
		setCanceledOnTouchOutside(true);  //点击外部区域消失
		final Timer t = new Timer(); 

        t.schedule(new TimerTask() { 

            public void run() { 
                dismiss();
                cancel();
            } 

        }, 3000); 
	}

}
