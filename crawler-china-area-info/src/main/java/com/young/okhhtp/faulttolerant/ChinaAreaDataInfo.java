package com.young.okhhtp.faulttolerant;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * 
 * @author        Young
 * @description   为全国地区数据做容错
 * @date          2018年9月1日 上午12:19:52 
 *
 */
import org.springframework.transaction.annotation.Transactional;

import com.young.okhhtp.entity.TblChinaAreaInfo;
import com.young.okhhtp.entity.TblChinaAreaInfoForFai;
import com.young.okhhtp.mapper.TblChinaAreaInfoFailMapper;
import com.young.okhhtp.mapper.TblChinaAreaInfoMapper;
import com.young.okhhtp.task.CrawlerTask;
@Service
@Transactional
public class ChinaAreaDataInfo {
	
	@Autowired
	private TblChinaAreaInfoFailMapper areaInfoFailMapper;
	
	@Autowired
	private TblChinaAreaInfoMapper areaInfoMapper;
	//计数器
	private  AtomicInteger lcount=new AtomicInteger(0);
	
	public void falutTolerant() {
		
		String beginTime=CrawlerTask.getDate();
		
		List<TblChinaAreaInfoForFai> unFetcheds = areaInfoFailMapper.selectUnFetched();
		
		if(unFetcheds==null||unFetcheds.size()==0) return;
		//装在连接异常的数据
		List<TblChinaAreaInfoForFai> listForFails=new LinkedList<>();
		//装在获取的数据
		List<TblChinaAreaInfo> listForFetched=new LinkedList<>();
		for (TblChinaAreaInfoForFai unFetched : unFetcheds) {
				  
				  TblChinaAreaInfo parentInfo = CrawlerTask.failToInfo(unFetched);
			      getAll(parentInfo, listForFetched,listForFails);
		}
		//删除之前的全部数据
		areaInfoFailMapper.deleteAll();
		//插入新的失败数据数据
		if(listForFails.size()!=0)areaInfoFailMapper.insertList(listForFails);
		if(listForFetched.size()!=0)areaInfoMapper.insertByBatch(listForFetched);
		write(beginTime, lcount.get(),listForFails.size(),listForFetched.size());
	}
	private void getAll(TblChinaAreaInfo parentInfo,List<TblChinaAreaInfo> areaInfo,List<TblChinaAreaInfoForFai> listForFails) {
		//拼接url三人组
		String pathName = parentInfo.getPathName();
		int length=pathName.length();
		String jointUrl = CrawlerTask.jointUrl(pathName, length);
		
		System.out.println(jointUrl	);
		
		Document document=null;
		try {
			Thread.sleep(100);
			lcount.incrementAndGet();
			document = Jsoup.connect(jointUrl).timeout(5000).get();
		} catch (Exception e) {
			
		}
		//对document 做非空判断 其实是在做异常处理  为空则结束掉这一层递归 并把该对象加入异常
		if(document==null) {
			TblChinaAreaInfoForFai infoToFail = CrawlerTask.infoToFail(parentInfo);
			listForFails.add(infoToFail);
			return ;
		}
		String level= CrawlerTask.LEVEL_MAP.get(length);
		String classTr=CrawlerTask.CLASS_MAP.get(length);
		
		Elements elementsByClass = document.getElementsByClass(classTr);
		for (Element element : elementsByClass) {
			
			 Element first = element.getElementsByTag("td").first();
		     Element last = element.getElementsByTag("td").last();
		     //判断有没有子节点
		     TblChinaAreaInfo info=new TblChinaAreaInfo();
		     //不报a标签说明到底了 直接将其加入areaInfo
		     if(!first.toString().contains("<a")) {
		    	 info.setParentId(parentInfo.getAdcCode());
			     info.setLevel(level);
			     info.setAdcCode(Long.parseLong(first.text()));
			     info.setName(last.text());
			     areaInfo.add(info);
		     }else {
		    //包含a标签 说明还没到底 先将这条数据加入 areaInfo 在继续调用此方法进行递归 然后将新对象作为父类对象传入该方法
		    	 Element a1 = first.getElementsByTag("a").first();
		    	 Element a2 = last.getElementsByTag("a").first();
				    	  info.setParentId(parentInfo.getAdcCode());
		                  info.setLevel(level);
		                  info.setAdcCode(Long.parseLong(a1.text()));
		                  info.setName(a2.text());
		                  //只要文件名
		                  String attr = a2.attr("href");
		                  info.setPathName(attr.substring(attr.lastIndexOf("/")+1));
		          areaInfo.add(info);
		          //递归调用
		          getAll(info,areaInfo,listForFails);
		     }
		}
	}
	
	private void write(String beginTime,int count,int fail,int number) {
		try {
			Writer writer=new FileWriter("forfail.txt");
				   writer.write("******Young****"+beginTime+"*******Young*****"
						       +"\r本次容错处理http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2017 请求了"+count+"次"
						       +"\r失败"+fail+"次"
						       +"\r新获取了"+number+"条数据"
						       +"******Young****"+CrawlerTask.getDate()+"*******Young*****");
				   writer.flush();
				   writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
