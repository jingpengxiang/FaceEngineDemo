package com.lu.face.faceenginedemo.engine.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 
 * @ClassName: SharedPreferencesUtils
 * @Description:TODO SharedPreferences工具类
 * @author: lvwenyan
 * @date: Jul 1, 2015 2:42:07 PM
 */
public class SharedPreferencesUtil {
	/**
	 * 保存在手机里面的文件名
	 */
	public static final String FILE_NAME = "share_data";

	/**
	 * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
	 * @Title: put   
	 * @Description: TODO 
	 * @param: @param context
	 * @param: @param key
	 * @param: @param object      
	 * @return: void      
	 * @throws   
	 * @author: lvwenyan     
	 * @date:   Jul 1, 2015 2:47:02 PM
	 */
	public static void put(Context context, String key, Object object) {

		SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();

		if (object instanceof String) {
			editor.putString(key, (String) object);
		} else if (object instanceof Integer) {
			editor.putInt(key, (Integer) object);
		} else if (object instanceof Boolean) {
			editor.putBoolean(key, (Boolean) object);
		} else if (object instanceof Float) {
			editor.putFloat(key, (Float) object);
		} else if (object instanceof Long) {
			editor.putLong(key, (Long) object);
		} else {
			editor.putString(key, object.toString());
		}

		SharedPreferencesCompat.apply(editor);
	}

	/**
	 * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
	 * @Title: get   
	 * @Description: TODO 
	 * @param: @param context
	 * @param: @param key
	 * @param: @param defaultObject
	 * @param: @return      
	 * @return: Object      
	 * @throws   
	 * @author: lvwenyan     
	 * @date:   Jul 1, 2015 2:47:48 PM
	 */
	public static Object get(Context context, String key, Object defaultObject) {
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);

		if (defaultObject instanceof String) {
			return sp.getString(key, (String) defaultObject);
		} else if (defaultObject instanceof Integer) {
			return sp.getInt(key, (Integer) defaultObject);
		} else if (defaultObject instanceof Boolean) {
			return sp.getBoolean(key, (Boolean) defaultObject);
		} else if (defaultObject instanceof Float) {
			return sp.getFloat(key, (Float) defaultObject);
		} else if (defaultObject instanceof Long) { return sp.getLong(key, (Long) defaultObject); }

		return null;
	}

	/**
	 * 移除某个key值对应的值
	 * @Title: remove   
	 * @Description: TODO 
	 * @param: @param context
	 * @param: @param key      
	 * @return: void      
	 * @throws   
	 * @author: lvwenyan     
	 * @date:   Jul 1, 2015 2:48:11 PM
	 */
	public static void remove(Context context, String key) {
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.remove(key);
		SharedPreferencesCompat.apply(editor);
	}

	/**
	 * 清除所有数据
	 * @Title: clear   
	 * @Description: TODO 
	 * @param: @param context      
	 * @return: void      
	 * @throws   
	 * @author: lvwenyan     
	 * @date:   Jul 1, 2015 2:48:40 PM
	 */
	public static void clear(Context context) {
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.clear();
		SharedPreferencesCompat.apply(editor);
	}

	/**
	 * 查询某个key是否已经存在
	 * @Title: contains   
	 * @Description: TODO 
	 * @param: @param context
	 * @param: @param key
	 * @param: @return      
	 * @return: boolean      
	 * @throws   
	 * @author: lvwenyan     
	 * @date:   Jul 1, 2015 2:48:55 PM
	 */
	public static boolean contains(Context context, String key) {
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		return sp.contains(key);
	}

	/**
	 * 返回所有的键值对
	 * @Title: getAll   
	 * @Description: TODO 
	 * @param: @param context
	 * @param: @return      
	 * @return: Map<String,?>      
	 * @throws   
	 * @author: lvwenyan     
	 * @date:   Jul 1, 2015 2:49:12 PM
	 */
	public static Map<String, ?> getAll(Context context) {
		SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
		return sp.getAll();
	}

	/**
	 * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
	 * @ClassName:	SharedPreferencesCompat 
	 * @Description:TODO 
	 * @author:	lvwenyan
	 * @date: Jul 1, 2015 2:49:31 PM
	 */
	private static class SharedPreferencesCompat {
		private static final Method sApplyMethod = findApplyMethod();

		/**
		 * 反射查找apply的方法
		 * @Title: findApplyMethod   
		 * @Description: TODO 
		 * @param: @return      
		 * @return: Method      
		 * @throws   
		 * @author: lvwenyan     
		 * @date:   Jul 1, 2015 2:49:56 PM
		 */
		@SuppressWarnings({ "unchecked", "rawtypes" })
		private static Method findApplyMethod() {
			try {
				Class clz = SharedPreferences.Editor.class;
				return clz.getMethod("apply");
			} catch (NoSuchMethodException e) {
			}

			return null;
		}

		/**
		 * 如果找到则使用apply执行，否则使用commit
		 * @Title: apply   
		 * @Description: TODO 
		 * @param: @param editor      
		 * @return: void      
		 * @throws   
		 * @author: lvwenyan     
		 * @date:   Jul 1, 2015 2:50:20 PM
		 */
		public static void apply(SharedPreferences.Editor editor) {
			try {
				if (sApplyMethod != null) {
					sApplyMethod.invoke(editor);
					return;
				}
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
			} catch (InvocationTargetException e) {
			}
			editor.commit();
		}
	}
}
