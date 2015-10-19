/**
 * 
 */
package com.cmmobi.icuiniao.ui.view;

import java.util.Timer;
import java.util.TimerTask;

import com.cmmobi.icuiniao.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author hw
 *一个按钮
 */
public class StylePageDialog1 extends Dialog{

	private String text = "";
	
	public StylePageDialog1(Context context, int theme, String text) {
		super(context, theme);
		// TODO Auto-generated constructor stub
		this.text = text;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stylepage_dialog1);
		TextView stylepage_text = (TextView)findViewById(R.id.stylepage_text);
		Button stylepage_btn = (Button)findViewById(R.id.stylepage_btn);
		stylepage_text.setText(text);
		stylepage_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cancel();
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
