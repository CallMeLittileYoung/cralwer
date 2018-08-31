package com.young.okhhtp.task;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.young.okhhtp.entity.TblChinaAreaInfo;
import com.young.okhhtp.entity.TblChinaAreaInfoForFai;
import com.young.okhhtp.mapper.TblChinaAreaInfoFailMapper;
import com.young.okhhtp.mapper.TblChinaAreaInfoMapper;


/**
 * 
 * @author        Young
 * @description   
 * @date          2018年8月27日 上午10:19:53 
 *
 */
@Component
@Transactional
public class CrawlerTask {
	
	public static final String url = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2017/";
	
	@Autowired
	private TblChinaAreaInfoMapper areaInfoMapper;
	@Autowired
	private TblChinaAreaInfoFailMapper areaInfoFailMapper;
	//增加原子性 
	private  static  AtomicInteger count= new AtomicInteger(32);
	//atomic包下的大将
	private static AtomicInteger lcount=new AtomicInteger(0);
	
	private static List<TblChinaAreaInfoForFai> listForFail=new LinkedList<>();
	
	private static  String BEGIN_TIME=null;
	
	@Scheduled(cron="0 0/5 * * * *")
	public void crawler() {
		
		   int nowCount = count.incrementAndGet();
		
		try {
			System.out.println("开始------"+nowCount+"--"+Thread.currentThread().getName());
			if(nowCount==1) {
				
				BEGIN_TIME=getDate();
			}
			if(nowCount==32) {
				areaInfoFailMapper.insertList(listForFail);
				writeCount();
				return;
			}
			if(nowCount>=32) {
				return;
			}
			//每次查询出一个省份的所有地区数据
			TblChinaAreaInfo provinceInfo = areaInfoMapper.selectByPrimaryKey(nowCount);
			
			//用来承载所有待插入数据库的数据
			LinkedList<TblChinaAreaInfo> listAll=new LinkedList<>();
			
			//拿到所有市级数据
			LinkedList<TblChinaAreaInfo> cityes = getElementsByClassName(provinceInfo);
			//放入  要做非空判断
			if(cityes!=null&&cityes.size()!=0)listAll.addAll(cityes);
			 System.err.println(listAll.size());
			
			//拿到所有的县级数据
			LinkedList<TblChinaAreaInfo> allCounties=new LinkedList<>();
			
			
			     for (int i = 0; i < cityes.size(); i++) {
			    	 
			    	 TblChinaAreaInfo city = cityes.get(i);
			    	 Thread.sleep(250);
					 LinkedList<TblChinaAreaInfo> counties = getElementsByClassName(city);
					 
					 //做非空判断  放入最大的list
					 if(counties!=null&&counties.size()!=0)allCounties.addAll(counties);
				 }
			    
			//放入
			listAll.addAll(allCounties);
			System.err.println(listAll.size());
			//拿到所有乡级数据
			LinkedList<TblChinaAreaInfo> allTowns=new LinkedList<>();
			
			for (int i = 0; i < allCounties.size(); i++) {
				 Thread.sleep(200);
				 TblChinaAreaInfo county = allCounties.get(i);
				 LinkedList<TblChinaAreaInfo> towns = getElementsByClassName(county);
				 
				 if(towns!=null&&towns.size()!=0)allTowns.addAll(towns);
			}	
				listAll.addAll(allTowns);
			System.err.println(listAll.size());
			
			for (int i = 0; i < allTowns.size(); i++) {
				 Thread.sleep(100);
				 TblChinaAreaInfo town = allTowns.get(i);
				 
				 LinkedList<TblChinaAreaInfo> valliages = getElementsByClassName(town);
				 
				 if(valliages!=null&&valliages.size()!=0) listAll.addAll(valliages);
			}
			  System.err.println(listAll.size());
			  if(listAll.size()!=0) 
				  areaInfoMapper.insertByBatch(listAll);
			  
			 
		} catch (InterruptedException e) {
			   e.printStackTrace();
		}
		
	}
	public static final Map<Integer, String> LEVEL_MAP=new HashMap<>();
	public static final Map<Integer, String> CLASS_MAP=new HashMap<>();
	static {
		 LEVEL_MAP.put(7, "CITY");
		 LEVEL_MAP.put(9, "county");
		 LEVEL_MAP.put(11, "town");
		 LEVEL_MAP.put(14, "village");
		 CLASS_MAP.put(7, "citytr");
		 CLASS_MAP.put(9, "countytr");
		 CLASS_MAP.put(11, "towntr");
		 CLASS_MAP.put(14, "villagetr");
	}
	private LinkedList<TblChinaAreaInfo> getElementsByClassName(TblChinaAreaInfo parentInfo) {
				//不包含子级路径的统统忽略  java.util.LinkedList.addAll(int, Collection<? extends E>) 由于这个源码中没做非空
				//判断 所以  crawler()方法中需要做非空判断
				String pathName=parentInfo.getPathName();
				
				if(pathName==null||pathName.trim().equals(""))  return null;
				//保险起见trim一下，因为接下来需要根据 pathName的长度做判断
				   pathName=pathName.trim();
				   
				  int length=pathName.length();
				   
				  String level= LEVEL_MAP.get(length);
				  
				  String classTr=CLASS_MAP.get(length);
				  //开始拼接url
				  String finalUrl=jointUrl(pathName, length);
				  
				  System.out.println(finalUrl);
				LinkedList<TblChinaAreaInfo> list=new LinkedList<>();
		    try {
		    	//统计次数
		    	lcount.incrementAndGet();
		    	
				Document document = Jsoup.connect(finalUrl).timeout(5000).get();
				
				Elements elementsByClass = document.getElementsByClass(classTr);
				for (Element element : elementsByClass) {
					
					 Element first = element.getElementsByTag("td").first();
				     Element last = element.getElementsByTag("td").last();
				     //判断有没有子节点
				     TblChinaAreaInfo info=new TblChinaAreaInfo();
				     //判断包不包含a标签
				     
				     if(!first.toString().contains("<a")) {
				    	 info.setParentId(parentInfo.getAdcCode());
					     info.setLevel(level);
					     info.setAdcCode(Long.parseLong(first.text()));
					     info.setName(last.text());
				     }else {
				    	 Element a1 = first.getElementsByTag("a").first();
				    	 Element a2 = last.getElementsByTag("a").first();
						    	  info.setParentId(parentInfo.getAdcCode());
				                  info.setLevel(level);
				                  info.setAdcCode(Long.parseLong(a1.text()));
				                  info.setName(a2.text());
				                  //只要文件名
				                  String attr = a2.attr("href");
				                  info.setPathName(attr.substring(attr.lastIndexOf("/")+1));
				     }
				     list.add(info);
				}
			} catch (Exception e) {
				TblChinaAreaInfoForFai fail=new  TblChinaAreaInfoForFai(parentInfo.getName(), parentInfo.getParentId(), parentInfo.getLevel(), parentInfo.getPathName(), parentInfo.getAdcCode());
				CrawlerTask.listForFail.add(fail);
			} 
		    return   list;
	}
	public static String jointUrl(String pathName,int length) {
		String finalPathName=null;
		switch (length) {
		
		case 7:
			finalPathName=pathName;
			break;
		case 9:
			finalPathName=pathName.substring(0, 2)+"/"+pathName;
			break;
		case 11:
			finalPathName= pathName.substring(0, 2)+"/"+pathName.substring(2,4)+"/"+pathName;
			break;
		case 14:
			finalPathName=pathName.substring(0, 2)+"/"+pathName.substring(2,4)+"/"+pathName.substring(4,6)+"/"+pathName;
			break;
		default:
			break;
		}
		return CrawlerTask.url+finalPathName;
	}
	public  void  writeCount() {
		try {
			long selectAllCounts = areaInfoMapper.selectAllCounts();
			Writer writer=new FileWriter("count.txt");
				   writer.write("**************"+BEGIN_TIME+"****************"
				   		        + "\r共向http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2017 请求了 "+lcount.get()+"次"
				   		        +"\r请求失败"+listForFail.size()+"次"
						   		+ "\r获取了"+selectAllCounts+"条数据"
						        +"\r***************"+getDate()+"***************");
				   writer.flush();
				   writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static  String getDate() {
		DateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss s");
		return df.format(new Date());
	}
	public static TblChinaAreaInfo failToInfo(TblChinaAreaInfoForFai fail) {
		
		TblChinaAreaInfo info=new TblChinaAreaInfo(fail.getName(), fail.getParentId(), fail.getLevel(), fail.getPathName(), fail.getAdcCode());
		          return info;
	}
	public static TblChinaAreaInfoForFai infoToFail(TblChinaAreaInfo info) {
		
		TblChinaAreaInfoForFai fail=new TblChinaAreaInfoForFai(info.getName(), info.getParentId(), info.getLevel(), info.getPathName(), info.getAdcCode());
		                return fail;
	}
}
