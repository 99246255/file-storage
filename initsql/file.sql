
-- ----------------------------
-- Table structure for t_business_type
-- ----------------------------
CREATE TABLE `t_business_type`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '名称',
  `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  `store_id` int(11) NOT NULL COMMENT '存储平台id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB COMMENT = '业务类型' ;

-- ----------------------------
-- Records of t_business_type
-- ----------------------------
INSERT INTO `t_business_type` VALUES (1, '默认分类', '2022-02-16 20:55:58', '2022-02-16 20:55:58', 1);

-- ----------------------------
-- Table structure for t_file_info
-- ----------------------------
CREATE TABLE `t_file_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `file_id` varchar(40) NOT NULL COMMENT '对外暴露的文件id唯一主键',
  `path` varchar(255) NOT NULL DEFAULT '' COMMENT '报表唯一ID',
  `file_type` varchar(100) NOT NULL COMMENT '文件类型,同Content-Type',
  `store_id` int(11) NOT NULL COMMENT '存储id',
  `business_type` int(11) NOT NULL COMMENT '业务类型',
  `expire_time` datetime NOT NULL COMMENT '过期时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `size` bigint(20) NOT NULL COMMENT '文件大小',
  `file_name` varchar(255) NOT NULL COMMENT '文件名称',
  `md5` varchar(40) NOT NULL COMMENT '文件md5',
  `deleted` tinyint(2) NOT NULL COMMENT '删除标记',
  `check_md5` tinyint(2) NOT NULL COMMENT '是否校验md5，false不校验md5，并且无法秒传',
  `status` tinyint(4) NOT NULL COMMENT '文件状态,默认0临时文件;1:永久文件；需要标记之后才会改成永久文件，永久文件才会秒传',
  `upload_id` varchar(100) NOT NULL DEFAULT '' COMMENT '分片上传id，分片上传时才有，上传完后清空',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uniq_fileId` (`file_id`) USING BTREE,
  KEY `idx_md5` (`md5`,`business_type`) COMMENT 'md5和业务类型索引，判断是否秒传'
) ENGINE=InnoDB COMMENT='文件信息';

-- ----------------------------
-- Table structure for t_platform_store
-- ----------------------------
CREATE TABLE `t_platform_store`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '名称',
  `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  `config` json NULL COMMENT '配置',
  `type` smallint(4) NOT NULL COMMENT '平台类型',
  `default_status` tinyint(2) NOT NULL COMMENT 'true:默认存储，false 非默认',
  `deleted` tinyint(2) NOT NULL COMMENT '删除标记',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB COMMENT = '存储平台';

