/**
 * 
 */
package com.icuiniao.plug.localmessage;

/**
 * @author hw
 *
 */
public class TagUser {

	private int systemPlatform;//（1安卓，2苹果）系统平台。空为全部
	private int sex;//（0男，1女）性别。空为全部
	private int notStart;//（天数，几天没有启动软件的）不启动的。可为空
	private int type;//（0游客，1注册用户）用户类型。空为全部
	private String city;//（北京市 北京市）城市，空为全部。省份名称,城市名称
	private String birthdayStart;//(04-29)生日区间，空为全部，格式MM-DD
	private String birthdayEnd;
	private int ageStart;//年龄范围。空为全部
	private int ageEnd;
	private int salaryStart;//收入范围，空为全部
	private int salaryEnd;
	private String label;//（多个用","分隔，满足一个即可）标签。空为全部
	private String keyword;//（多个用","分隔，满足一个即可）关键字。空为全部
	
	public TagUser(){
		systemPlatform = -1;
		sex = -1;
		notStart = -1;
		type = -1;
		city = "";
		birthdayStart = "";
		birthdayEnd = "";
		ageStart = -1;
		ageEnd = -1;
		salaryStart = -1;
		salaryEnd = -1;
		label = "";
		keyword = "";
	}
	
	public void setSystemPlatform(int value){systemPlatform = value;}
	public void setSex(int value){sex = value;}
	public void setNotStart(int value){notStart = value;}
	public void setType(int value){type = value;}
	public void setCity(String value){city = value;}
	public void setBirthdayStart(String value){birthdayStart = value;}
	public void setBirthdayEnd(String value){birthdayEnd = value;}
	public void setAgeStart(int value){ageStart = value;}
	public void setAgeEnd(int value){ageEnd = value;}
	public void setSalaryStart(int value){salaryStart = value;}
	public void setSalaryEnd(int value){salaryEnd = value;}
	public void setLabel(String value){label = value;}
	public void setKeyword(String value){keyword = value;}
	
	public int getSystemPlatform(){return systemPlatform;}
	public int getSex(){return sex;}
	public int getNotStart(){return notStart;}
	public int getType(){return type;}
	public String getCity(){return city;}
	public String getBirthdayStart(){return birthdayStart;}
	public String getBirthdayEnd(){return birthdayEnd;}
	public int getAgeStart(){return ageStart;}
	public int getAgeEnd(){return ageEnd;}
	public int getSalaryStart(){return salaryStart;}
	public int getSalaryEnd(){return salaryEnd;}
	public String getLabel(){return label;}
	public String getKeyword(){return keyword;}
}
