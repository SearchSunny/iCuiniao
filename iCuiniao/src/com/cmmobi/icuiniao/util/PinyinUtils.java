package com.cmmobi.icuiniao.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;

public class PinyinUtils {

	/**
	 * 获得字符串的拼音串(不是中文的字符，不进行转换)
	 * 
	 * @param zhongwen
	 * @return
	 */
	public static String getPinyin(String zhongwen) {
		char[] zhongwenchar = zhongwen.toCharArray();
		StringBuffer headAll = new StringBuffer();
		for (int i = 0; i < zhongwenchar.length; i++) {
			char h = zhongwenchar[i];
			if (h <= 128) {
				headAll.append(h);
				continue;
			}
			net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();
			t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);
			t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
			t3.setVCharType(HanyuPinyinVCharType.WITH_V);
			try {

				String[] head = PinyinHelper.toHanyuPinyinStringArray(h, t3);
				if (head != null) {
					for (int k = 0; k < head.length; k++) {
						headAll.append(head[k]);
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return headAll.toString();

	}

	/**
	 * 获得中文字符串的首字母（不是中文，不转换）
	 * 
	 * @param zhongwen
	 * @return
	 */
	public static String getHeadLetterByString(String zhongwen) {
		char[] zhongwenchar = zhongwen.toCharArray();
		StringBuffer headAll = new StringBuffer();
		for (int i = 0; i < zhongwenchar.length; i++) {
			char h = zhongwenchar[i];
			if (h <= 128) {
				headAll.append(h);
				continue;
			}
			String head = getHeadByChar(h, false)[0] + "";
			headAll.append(head);
		}
		return headAll.toString();
	}

	private static char[] getHeadByChar(char src, boolean isCapital) {
		// 如果不是汉字直接返回
		if (src <= 128) {
			return new char[] { src };
		}
		// 获取所有的拼音
		char[] headChars = null;
		try{
		String[] pinyingStr = PinyinHelper.toHanyuPinyinStringArray(src);
		if(pinyingStr == null){
			return new char[] { src };
		}
		// 创建返回对象
		int polyphoneSize = pinyingStr.length;
		headChars = new char[polyphoneSize];
		int i = 0;
		// 截取首字符
		for (String s : pinyingStr) {
			char headChar = s.charAt(0);
			// 首字母是否大写，默认是小写
			if (isCapital) {
				headChars[i] = Character.toUpperCase(headChar);
			} else {
				headChars[i] = headChar;
			}
			i++;
		}
		}catch(Exception e){
			e.printStackTrace();
			return new char[] { src };
		}
		return headChars;
	}
}
