package com.lu.face.faceenginedemo.engine.utils;

import java.io.File;
import java.io.FilenameFilter;

public class MyFilterBmp implements FilenameFilter {
	boolean isHasDirectory;

	public MyFilterBmp(boolean isHasDirectory) {
		this.isHasDirectory = isHasDirectory;
	}

	public boolean accept(File file, String name) {
//        return name.endsWith(type);
		if (isHasDirectory) {
			return (isPng(name) || isDirectory(file));
		} else {
			return (isPng(name));
		}
	}

//	public boolean isGif(String file) {
//		if (file.toLowerCase().endsWith(".gif")) {
//			return true;
//		} else {
//			return false;
//		}
//	}

	public boolean isJpg(String file) {
		if (file.toLowerCase().endsWith(".jpg") || file.toLowerCase().endsWith(".JPG")) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isPng(String file) {
		if (file.toLowerCase().endsWith(".bmp") || file.toLowerCase().endsWith(".BMP")) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isDirectory(File file) {
		if (file.isDirectory()) {
			return true;
		} else {
			return false;
		}
	}
}
