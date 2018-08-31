package com.young.test;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.young.okhhtp.RunOkHttpUtils;
import com.young.okhhtp.entity.TblChinaAreaInfo;
import com.young.okhhtp.get.OkHttpGet;
import com.young.okhhtp.mapper.TblChinaAreaInfoMapper;
import com.young.okhhtp.task.CrawlerTask;


@SpringBootTest(classes= {RunOkHttpUtils.class})
@RunWith(value = SpringJUnit4ClassRunner.class)
public class TestGet {
	
	
	@Autowired
	OkHttpGet httpGet;
	
	@Test
	public void test1() {
		
		byte[] syncGet = httpGet.syncGet("http://www.stats.gov.cn/", null);
		
		Document parse = Jsoup.parse(new String(syncGet));
		System.out.println(parse);
	}
	
	@Test
	public void test2() {
		//一个不完整的div 带一个完整的p标签
		String str="<div><p>Lorem ipsum.</p>";
		Document fragment = Jsoup.parseBodyFragment(str);
		Element body = fragment.body();
		System.out.println(body);
	}
	@Test
	public void test3() throws IOException {
		//以菜鸟教程为例
		String url="http://www.runoob.com/";
		//输入url 设置请求参数   选定RequestMethod
		Document document = Jsoup.connect(url).data("s", "java").get();
		System.out.println(document);
	}
	
	@Test
	public void test4() throws IOException{
		String url="http://www.runoob.com/java/java-basic-syntax.html";
		Document document = Jsoup.connect(url).get();
		System.out.println(document);
	}
	@Test
	public void test5() throws IOException{
		
		String filePath="D:\\装机软件ForFun\\bootstrap-jquery-switch-button\\index.html";
		
		Document parse = Jsoup.parse(new File(filePath), "UTF-8");
		
		System.out.println(parse);
	}
	@Test
	public void test6() throws IOException{
		
		String absPath="http://www.runoob.com/java/java-environment-setup.html";
		Document parse = Jsoup.connect(absPath).get();
		System.out.println(parse);
	}
	//解析文档
	@Test
	public void test7() throws IOException {
		String path="http://www.runoob.com/java/java-loop.html";
		Document document = Jsoup.connect(path).get();
		Element elementById = document.getElementById("leftcolumn");
		Elements allElements = elementById.getElementsByTag("a");
		for (Element element : allElements) {
			String attr = element.attr("title");
			String attr2 = element.attr("href");
			System.err.println(attr+"----"+attr2);
		}
	}
	@Test
	public void test8() throws IOException {
		
		String path="http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2017/index.html";
		Document document = Jsoup.connect(path).get();
		Elements select = document.select("table[class=provincetable]");
		for (Element element : select) {
			Elements elementsByTag = element.getElementsByTag("a");
			for (Element element2 : elementsByTag) {
				System.out.println(element2.attr("href")+"---"+element2.text());
			}
		}
	}
	@Test
	public void test9() throws IOException {
		
		Document parse = Jsoup.parse(new File("D:\\项目\\HTML\\Test\\index.html"),"UTF-8");
		Element elementById = parse.getElementById("submit");
		elementById.attr("value", "不能提交");
	    System.out.println(elementById);
	    String data = parse.html(); 
	    System.out.println(data);
	    OutputStream os=new FileOutputStream(new File("D:\\项目\\HTML\\Test\\index.html"));
	    os.write(data.getBytes());
	    os.flush();
	    os.close();
	}
	@Autowired
	private TblChinaAreaInfoMapper chinaAreaInfoMapper;
	
	@Test
	public void test11() {
		System.out.println(chinaAreaInfoMapper);
	}
	/* ------------------拉取所有省份的数据----------------------*/
	
	@Test
	public void test10() throws IOException {
		
			Document document = Jsoup.connect(CrawlerTask.url+"index.html").get();
			Elements select = document.getElementsByClass("provincetr");
			
			List<TblChinaAreaInfo> list=new ArrayList<>();
			for (Element element : select) {
				Elements elementsByTag = element.getElementsByTag("a");
				for (Element element2 : elementsByTag) {
					TblChinaAreaInfo info=new TblChinaAreaInfo();
							         info.setLevel("province");
							         info.setName(element2.text());
							         info.setPathName(element2.attr("href"));
							         String attr = element2.attr("href");
							         String substring = attr.substring(0, attr.lastIndexOf(".html"));
							         info.setAdcCode(Long.parseLong(substring+"0000000000"));
							         list.add(info);
				}
			}
			int insertList = chinaAreaInfoMapper.insertList(list);
			System.err.println(insertList);
	}
    //把第一批数据的adc——code改掉
	@Test
	public void test12() throws IOException {
		   List<TblChinaAreaInfo> selectAll = chinaAreaInfoMapper.selectAll();
		   for (TblChinaAreaInfo tblChinaAreaInfo : selectAll) {
			   
			  Document document = Jsoup.connect(CrawlerTask.url+tblChinaAreaInfo.getPathName()).get();
			  Elements elementsByClass = document.getElementsByClass("citytr");
			  for (Element element : elementsByClass) {
				  Element elementsByTag = element.getElementsByTag("a").first();
				  tblChinaAreaInfo.setPathName(elementsByTag.attr("href"));
				  tblChinaAreaInfo.setAdcCode(Long.parseLong(elementsByTag.text()));
				  //chinaAreaInfoMapper.updateByPrimaryKeySelective(tblChinaAreaInfo);
			 }
			  
		   }
	}
	
  /*----------------------------------------------------------------------------------*/
	@Test
	public void test13() throws IOException {
		
		TblChinaAreaInfo provinceInfo = chinaAreaInfoMapper.selectByPrimaryKey(1L);
		System.out.println(provinceInfo);
		Document document = Jsoup.connect(CrawlerTask.url+provinceInfo.getPathName()).get();
		System.out.println(document);
		
	}
}
