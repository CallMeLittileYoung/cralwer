package com.young.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.young.okhhtp.RunOkHttpUtils;
import com.young.okhhtp.faulttolerant.ChinaAreaDataInfo;

@SpringBootTest(classes= {RunOkHttpUtils.class})
@RunWith(value = SpringJUnit4ClassRunner.class)
public class TestDataAreaInfo {
	
	@Autowired
	private ChinaAreaDataInfo chinaAreaDataInfo;
	@Test
	public void test1() {
		chinaAreaDataInfo.falutTolerant();
	}
}
