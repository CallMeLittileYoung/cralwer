package com.young.okhhtp.utils;

public class StringUtils {
	
	public static  boolean isEmpty(String str) {
		if(str==null) return false;
		if(str.trim().equals("")) return false;
		return true;
	}
}
