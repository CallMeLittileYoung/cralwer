package com.young.okhhtp.get;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.young.okhhtp.utils.HttpUtils;
import com.young.okhhtp.utils.StringUtils;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.Map.Entry;


/**
 * 
 * @author        Young
 * @description         
 * @date          
 *
 */
@Component
public class OkHttpGet {
	
	/**
	 * 
	 * @param   url     
	 * @param   params   
	 * @return
	 */
	public byte[] syncGet(String url,Map<String,String> params) {
		
		String jointUrl = jointUrl(url, params);
		
		OkHttpClient okHttpClient = HttpUtils.getOkHttpClient();
		
		Request request=new Request.Builder()
							       .url(jointUrl)
							       .build();
		Call newCall = okHttpClient.newCall(request);
		try {
			Response execute = newCall.execute();
			byte[] bytes = execute.body().bytes();
			return bytes;
		} catch (IOException e) {
			e.printStackTrace();
		}
		 	return null;
	}
	
	
	
	
	
	
	private String jointUrl(String url,Map<String,String> params) {
		
		String urlParam=null;
		
		if(params!=null&&params.size()>0) {
			
			StringBuilder sb=new StringBuilder();
			
			Set<Entry<String, String>> entrySet = params.entrySet();
			
			for (Entry<String, String> entry : entrySet) {
				
				if(StringUtils.isEmpty(entry.getKey())||StringUtils.isEmpty(entry.getValue())) continue;
				
				sb.append("&"+entry.getKey()+"="+entry.getValue());
		    }
			 if(sb.length()>2) {
				 sb.setCharAt(0, '?');
		      	 urlParam = sb.toString();
			 }
		}
		String finalUrl=urlParam==null?url:url+urlParam;
		return finalUrl;
	}
	
}
