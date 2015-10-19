/**
 * 
 */
package com.cmmobi.icuiniao.onlineEngine.parser;

import java.util.ArrayList;

/**
 * @author hw
 *
 */
public class Parser_MainPage extends Parser_ParserEngine {

	private int mModelType;//模板类型
	private String mModelIndex;//大图位置索引
	private ArrayList<Parser_Image> mImages;//图片数组
	private int[] mModelIndexs;//大图位置索引数组
	private int validNum;//有效图片个数
	
	public Parser_MainPage(){
		super();
		mModelType = MODELTYPE_1;
		mModelIndex = null;
		mImages = new ArrayList<Parser_Image>();
		mModelIndexs = null;
		validNum = 9;
	}
	
	public void setType(int value){mModelType = value;}
	public void setModelIndex(String str){mModelIndex = str;}
	public void addImage(Parser_Image image){mImages.add(image);}
	public void setValidNum(int value){validNum = value;}
	
	public int getType(){return mModelType;}
	public ArrayList<Parser_Image> getImages(){return mImages;}
	public int[] getModelIndexs(){
		if(mModelIndex == null)return null;
		
		String[] tmps = mModelIndex.trim().split(",");
		mModelIndexs = new int[tmps.length];
		for(int i = 0;i < mModelIndexs.length;i ++){
			mModelIndexs[i] = Integer.parseInt(tmps[i]);
		}
		
		return mModelIndexs;
	}
	public int getValidNum(){return validNum;}
}
