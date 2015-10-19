package com.cmmobi.icuiniao.entity;

import java.util.ArrayList;

public class ProductItem {
	public int totalcount;
	public int pagesize;
	public String propage;	
	public ArrayList<Products> arrProducts;
	
	public int totalPage;
	
	public ProductItem(){
		arrProducts = new ArrayList<Products>();
	}
	
}
