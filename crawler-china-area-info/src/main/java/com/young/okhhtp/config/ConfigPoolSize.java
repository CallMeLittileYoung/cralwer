package com.young.okhhtp.config;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * 
 * @author        Young
 * @description   配置用定长线程池来做
 * @date          2018年8月30日 上午9:46:36 
 *
 */
@Configuration
public class ConfigPoolSize implements SchedulingConfigurer{

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.setScheduler(getExecutor());
	}
	
	public Executor getExecutor() {
		return Executors.newScheduledThreadPool(10);
		//return Executors.newFixedThreadPool(20);
	}
}
