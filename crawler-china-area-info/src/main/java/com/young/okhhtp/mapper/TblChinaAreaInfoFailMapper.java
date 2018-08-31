package com.young.okhhtp.mapper;


import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.young.okhhtp.entity.TblChinaAreaInfoForFai;
import com.young.okhhtp.mapper.base.BaseMapper;


/**
 * 
 * @author        Young
 * @description   用来操作`tbl_china_area_info`表
 * @date          2018年8月27日 上午10:14:42 
 *
 */
@Mapper
public interface TblChinaAreaInfoFailMapper extends BaseMapper<TblChinaAreaInfoForFai>{
	
	List<TblChinaAreaInfoForFai> selectUnFetched();
	//截断表
	void  deleteAll();
}
