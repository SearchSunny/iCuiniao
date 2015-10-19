/**
 * 
 */
package com.cmmobi.icuiniao.onlineEngine.view;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.cmmobi.icuiniao.R;
import com.cmmobi.icuiniao.onlineEngine.parser.Parser_InputLine;

/**
 * @author hw
 *
 */
public class View_Inputline extends XmlViewLayout{

	private Parser_InputLine parserInputLine;
	private EditText inputline_box;
	private Button inputline_button;
	
	public View_Inputline(Context context, Parser_InputLine parserInputLine, Handler handler) {
		super(context, handler, null);
		// TODO Auto-generated constructor stub
		this.parserInputLine = parserInputLine;
		LinearLayout l = (LinearLayout)((Activity)context).getLayoutInflater().inflate(R.layout.inputline, null);
		inputline_box = (EditText)l.findViewById(R.id.inputline_box);
		inputline_button = (Button)l.findViewById(R.id.inputline_button);
		inputline_button.setOnClickListener(okbtnClickListener);
		
		addView(l);
	}
	
	private OnClickListener okbtnClickListener = new OnClickListener() {
		final InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(inputline_box.getText()!=null){
				if(imm != null){
					imm.hideSoftInputFromWindow(getWindowToken(), 0);
				}
				onInputlineClick(parserInputLine.getHref(), inputline_box.getText().toString());
			}
		}
	};

}
