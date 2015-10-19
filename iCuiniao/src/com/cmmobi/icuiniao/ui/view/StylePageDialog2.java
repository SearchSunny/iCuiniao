/**
 * 
 */
package com.cmmobi.icuiniao.ui.view;

import com.cmmobi.icuiniao.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author hw
 *两个按钮
 */
public abstract class StylePageDialog2 extends Dialog{

	private String text = "";
	
	public StylePageDialog2(Context context, int theme, String text) {
		super(context, theme);
		// TODO Auto-generated constructor stub
		this.text = text;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stylepage_dialog2);
		TextView stylepage_text = (TextView)findViewById(R.id.stylepage_text);
		Button stylepage_btn = (Button)findViewById(R.id.stylepage_btn);
		stylepage_text.setText(text);
		stylepage_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				call();
			}
		});
		setCancelable(false);
	}

	public abstract void call();
}
