/**
 * 
 */
package com.icuiniao.plug.localmessage;

import java.util.ArrayList;

/**
 * @author hw
 *
 */
public class TagLocal {

	private ArrayList<TagInfo> infos;
	
	public TagLocal(){
		infos = new ArrayList<TagInfo>();
	}
	
	public void setLocal(TagInfo value){infos.add(value);}
	public ArrayList<TagInfo> getLocal(){return infos;}
}
