/**
 * 
 */
package com.cmmobi.icuiniao.onlineEngine.parser;

/**
 * @author hw
 *我的撒娇子项
 */
public class Parser_Coquetryitem extends Parser_ParserEngine{

	private Parser_Text mTime;
	private Parser_Text mTo;
	private Parser_Image mImage;
	private String href;
	
	public Parser_Coquetryitem(){
		super();
	}
	
	public void setTime(Parser_Text value){mTime = value;}
	public void setTo(Parser_Text value){mTo = value;}
	public void setImage(Parser_Image value){mImage = value;}
	public void setHref(String value){href = value;}
	
	public Parser_Text getTime(){return mTime;}
	public Parser_Text getTo(){return mTo;}
	public Parser_Image getImage(){return mImage;}
	public String getHref(){return href;}
}
