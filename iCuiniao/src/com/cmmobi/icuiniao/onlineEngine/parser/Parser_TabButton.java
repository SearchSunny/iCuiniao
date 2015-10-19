/**
 * 
 */
package com.cmmobi.icuiniao.onlineEngine.parser;

import java.util.ArrayList;

/**
 * @author hw
 *
 */
public class Parser_TabButton extends Parser_ParserEngine {

	private ArrayList<Parser_Button> mTabButtons;//按钮集合
	
	public Parser_TabButton(){
		super();
		mTabButtons = new ArrayList<Parser_Button>();
	}
	
	public void addButton(Parser_Button button){mTabButtons.add(button);}
	
	public ArrayList<Parser_Button> getButtons(){return mTabButtons;}
}
