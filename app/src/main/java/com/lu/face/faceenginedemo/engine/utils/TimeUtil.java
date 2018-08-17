package com.lu.face.faceenginedemo.engine.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间工具类
 * 
 * @ClassName: TimeUtils
 * @Description:TODO
 * @author: lvwenyan
 * @date: Jul 16, 2015 3:22:30 PM
 */
public class TimeUtil {
	/**
	 * 将当前时间format成String   
	 * type 0 	：	yyyy-MM-dd-HH-mm-ss
	 * type 1	：	yyyy-MM-dd HH:mm:ss
	 * type 2	：
	 * type 3	：
	 * type 4	：
	 * type 5	：
	 * type 6	：
	 * @Title: getDateFormat   
	 * @Description: TODO 
	 * @param: @param type
	 * @param: @return      
	 * @return: String      
	 * @throws   
	 * @author: lvwenyan     
	 * @date:   Jul 16, 2015 3:45:41 PM
	 */
	public static String getDateFormat(int type) {
		SimpleDateFormat mSimpleDateFormat = null;
		switch (type) {
		case 0:
			mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
			break;
		case 1:
			mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			break;
		case 2:
			mSimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			break;
			
		case 3:
			mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss:sss");
			break;

		default:
			break;
		}
		if (null == mSimpleDateFormat) {
			return "";
		} else {
			return mSimpleDateFormat.format(new Date());
		}
	}

	
}
