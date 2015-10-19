/**
 * 
 */
package com.cmmobi.icuiniao.onlineEngine.parser;

import java.util.ArrayList;

/**
 * @author hw
 *
 */
public class Parser_StylePage extends Parser_ParserEngine{

	private ArrayList<Parser_StyleItem> styleItems;
	
	public Parser_StylePage(){
		super();
		styleItems = new ArrayList<Parser_StyleItem>();
	}
	
	public void setItems(Parser_StyleItem value){styleItems.add(value);}
	public ArrayList<Parser_StyleItem> getItems(){return styleItems;}
}
