package com.lu.face.faceenginedemo.enginesdk.utils;

/**
 * @ClassName: StringUtils
 * @Description:TODO String工具类
 * @author: lvwenyan
 * @date: Jul 1, 2015 2:40:58 PM
 */
public class StringUtil {
	/**
	 * 判断str是否为空
	 * @Title: isNotNull   
	 * @Description: TODO 
	 * @param: @param str
	 * @param: @return      
	 * @return: boolean      
	 * @throws   
	 * @author: lvwenyan     
	 * @date:   Jul 1, 2015 5:41:23 PM
	 */
	public static boolean isNotNull(String str) {
		boolean ret = false;
		if (null != str) {
			if (str.length() > 0) {
				ret = true;
			}
		}
		return ret;
	}

	/**
	 * 判断str是否相等
	 * @Title: isStrEqual   
	 * @Description: TODO 
	 * @param: @param str1
	 * @param: @param str2
	 * @param: @return      
	 * @return: boolean      
	 * @throws   
	 * @author: lvwenyan     
	 * @date:   Jul 2, 2015 9:32:14 AM
	 */
	public static boolean isEqual(String str1, String str2) {
		boolean ret = false;
		if (isNotNull(str1) && isNotNull(str2)) {
			if (str1.equalsIgnoreCase(str2)) {
				ret = true;
			}
		}
		return ret;
	}

	/**
	 * 判断数据是否包含特定int元素
	 * Name isArrayContainInteger
	 * Function (todo)
	 *  [in]
	 *  [out]
	 * @return boolean
	 */
	public static boolean isArrayContainInteger(int[] arr, int targetValue) {
		for (int s : arr) {
			if (targetValue == s)
				return true;
		}
		return false;
	}

	/**
	 * String转Int数组
	 * @Title: stringToInts   
	 * @Description: TODO 
	 * @param: @param s
	 * @param: @return      
	 * @return: int[]      
	 * @throws   
	 * @author: lvwenyan     
	 * @date:   Oct 16, 2015 3:32:58 PM
	 */
	public static int[] stringToInts(String s) {
		if (isNotNull(s)) {
			s.trim();
			s.replace(" ", "");
			int length = s.length();
			int[] n = new int[length];
			for (int i = 0; i < length; i++) {
				n[i] = Integer.parseInt(s.substring(i, i + 1));
			}
			return n;
		}
		return null;
	}

	public static String addStars(String str, int start, int end) {
		String ret = null;
		if (start >= str.length() || start < 0) { return str; }
		if (end > str.length() || end < 0) { return str; }
		if (start >= end) { return str; }
		String starStr = "";
		for (int i = start; i < end; i++) {
			starStr = starStr + "*";
		}
		ret = str.substring(0, start) + starStr + str.substring(end, str.length());
		return ret;
	}
}
