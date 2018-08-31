package com.young.test;


import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.young.okhhtp.RunOkHttpUtils;
import com.young.okhhtp.entity.TblChinaAreaInfoForFai;
import com.young.okhhtp.mapper.TblChinaAreaInfoFailMapper;
import com.young.okhhtp.mapper.TblChinaAreaInfoMapper;
import com.young.okhhtp.task.CrawlerTask;

@SpringBootTest(classes= {RunOkHttpUtils.class})
@RunWith(value = SpringJUnit4ClassRunner.class)
public class TestCrawlerTask {
	
	@Autowired
	private CrawlerTask crawlerTask;
	
	
	@Test
	public void test()  {
		try {
			crawlerTask.crawler();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Autowired
	private TblChinaAreaInfoMapper areaInfoMapper;
	
	@Test
	public void test1() {
		long selectAllCounts = areaInfoMapper.selectAllCounts();
		System.out.println(selectAllCounts);
	}
	@Test
	public void test2() {
	  crawlerTask.writeCount();
	}
	@Autowired
	private TblChinaAreaInfoFailMapper areaInfoFailMapper;
	@Test
	public void test3() {
		List<TblChinaAreaInfoForFai> selectUnFetched = areaInfoFailMapper.selectUnFetched();
		for (TblChinaAreaInfoForFai tblChinaAreaInfoForFai : selectUnFetched) {
			System.out.println(tblChinaAreaInfoForFai);
		}
		/*List<TblChinaAreaInfoForFai> selectAll = areaInfoFailMapper.selectAll();
		for (TblChinaAreaInfoForFai tblChinaAreaInfoForFai : selectAll) {
			System.out.println(tblChinaAreaInfoForFai);
		}*/
	}
}
