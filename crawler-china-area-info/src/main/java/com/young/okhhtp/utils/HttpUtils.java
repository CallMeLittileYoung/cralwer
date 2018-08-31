package com.young.okhhtp.utils;
/**
 * 
 * @author        Young
 * @description   为真个项目提供便捷操作的工具类
 * @date          2018年8月25日 下午9:17:29 
 *
 */

import okhttp3.OkHttpClient;

public class HttpUtils {
	//做成静态常量
	private static final OkHttpClient client=new OkHttpClient();
	
	/**
	 * @description   构建OkHtttpClient是一个很耗时的工作，
	 *                所以我们先将其做成这样一个单例。绝对是有问题的，日后再改造
	 * @return
	 */
	public static OkHttpClient getOkHttpClient() {
		
		return client;
	}
}
