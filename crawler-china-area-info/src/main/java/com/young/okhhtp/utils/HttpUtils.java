package com.young.okhhtp.utils;
/**
 * 
 * @author        Young
 * @description   Ϊ�����Ŀ�ṩ��ݲ����Ĺ�����
 * @date          2018��8��25�� ����9:17:29 
 *
 */

import okhttp3.OkHttpClient;

public class HttpUtils {
	//���ɾ�̬����
	private static final OkHttpClient client=new OkHttpClient();
	
	/**
	 * @description   ����OkHtttpClient��һ���ܺ�ʱ�Ĺ�����
	 *                ���������Ƚ�����������һ��������������������ģ��պ��ٸ���
	 * @return
	 */
	public static OkHttpClient getOkHttpClient() {
		
		return client;
	}
}
