package com.young.okhhtp.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="tbl_china_area_info")
public class TblChinaAreaInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1599773172988736768L;
	
	@Id
	@Column
	private long id;
	@Column(name="name")
	private String name;
	@Column(name="parent_id")
	private long parentId;
	private String level;
	@Column(name="path_name")
	private String pathName;
	@Column(name="adc_code")
	private long adcCode;

	public TblChinaAreaInfo() {
	}

	public TblChinaAreaInfo(long id, String name, long parentId, String level) {
		super();
		this.id = id;
		this.name = name;
		this.parentId = parentId;
		this.level = level;
	}
	
	public TblChinaAreaInfo(String name, long parentId, String level, String pathName, long adcCode) {
		super();
		this.name = name;
		this.parentId = parentId;
		this.level = level;
		this.pathName = pathName;
		this.adcCode = adcCode;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getParentId() {
		return parentId;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}
	
	public String getPathName() {
		return pathName;
	}

	public void setPathName(String pathName) {
		this.pathName = pathName;
	}
	
	public long getAdcCode() {
		return adcCode;
	}

	public void setAdcCode(long adcCode) {
		this.adcCode = adcCode;
	}

	@Override
	public String toString() {
		return "TblChinaAreaInfo [id=" + id + ", name=" + name + ", parentId=" + parentId + ", level=" + level
				+ ", pathName=" + pathName + ", adcCode=" + adcCode + "]";
	}
}
