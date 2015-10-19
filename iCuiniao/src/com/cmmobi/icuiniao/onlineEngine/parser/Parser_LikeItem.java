/**
 * 
 */
package com.cmmobi.icuiniao.onlineEngine.parser;

import java.util.ArrayList;

/**
 * @author lenovo
 *
 */
public class Parser_LikeItem extends Parser_ParserEngine {

	private ArrayList<Parser_Image> mImages;
	
	public Parser_LikeItem(){
		super();
		mImages = new ArrayList<Parser_Image>();
	}
	
	public void addImage(Parser_Image image){mImages.add(image);}
	public ArrayList<Parser_Image> getImages(){return mImages;}
}
