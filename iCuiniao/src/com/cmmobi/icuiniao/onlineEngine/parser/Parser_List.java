/**
 * 
 */
package com.cmmobi.icuiniao.onlineEngine.parser;

import java.util.ArrayList;

/**
 * @author hw
 *
 */
public class Parser_List extends Parser_ParserEngine {

	private int mType;
	private boolean mPartRender;
	private ArrayList<Parser_ListItem> mListItems;
	
	public Parser_List(){
		super();
		mType = -1;
		mPartRender = false;
		mListItems = new ArrayList<Parser_ListItem>();
	}
	
	public void setType(int value){mType = value;}
	public void setPartRender(int value){mPartRender = value==TRUE?true:false;}
	public void addListItem(Parser_ListItem item){mListItems.add(item);}
	
	public int getType(){return mType;}
	public boolean getPartRender(){return mPartRender;}
	public ArrayList<Parser_ListItem> getListItems(){return mListItems;}
}
