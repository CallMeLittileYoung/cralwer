/*
Navicat MySQL Data Transfer

Source Server         : 192.168.12.133-docker-mysql
Source Server Version : 80011
Source Host           : 192.168.12.139:3306
Source Database       : Crawler

Target Server Type    : MYSQL
Target Server Version : 80011
File Encoding         : 65001

Date: 2018-09-01 02:48:16
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for tbl_china_area_info_fail
-- ----------------------------
DROP TABLE IF EXISTS `tbl_china_area_info_fail`;
CREATE TABLE `tbl_china_area_info_fail` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(64) NOT NULL COMMENT '地区名称',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '父级地区代码',
  `level` varchar(16) NOT NULL COMMENT '地区级别',
  `path_name` varchar(64) DEFAULT NULL COMMENT '对应页面路径',
  `adc_code` bigint(20) DEFAULT NULL,
  `flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '0' COMMENT '判断是否已经重新录入数据',
  PRIMARY KEY (`id`),
  KEY `adc_code_unique` (`adc_code`)
) ENGINE=InnoDB AUTO_INCREMENT=570 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
