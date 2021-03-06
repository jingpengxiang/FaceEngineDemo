package com.lu.face.faceenginedemo.engine.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * 实现对象的深拷贝
 * @ClassName:	CloneUtil 
 * @date: Oct 31, 2017 4:21:46 PM  @author:	lvwenyan
 */
public class CloneUtil {
	@SuppressWarnings("unchecked")
	public static <T extends Serializable> T clone(T obj) {

		T clonedObj = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(obj);
			oos.close();

			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			clonedObj = (T) ois.readObject();
			ois.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return clonedObj;
	}
}
