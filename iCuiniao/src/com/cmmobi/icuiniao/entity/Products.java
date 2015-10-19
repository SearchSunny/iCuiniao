package com.cmmobi.icuiniao.entity;

import java.util.ArrayList;

public class Products {
	public int count;
	public String findTime;
	public String showTime;
//	public ArrayList<Commodity> arrCommodity = new ArrayList<Commodity>();
	public ArrayList<String> arrcid;
	public ArrayList<String> arrProImg;
	public ArrayList<Integer> arrPlayable;
	
	public Products(){
		arrcid = new ArrayList<String>();
		arrProImg = new ArrayList<String>();
		arrPlayable = new ArrayList<Integer>();
	}
}
