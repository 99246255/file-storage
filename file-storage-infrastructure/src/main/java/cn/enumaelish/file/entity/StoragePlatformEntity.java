package cn.enumaelish.file.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author: EnumaElish
 * @Date: 2022/3/3 20:31
 * @Description: 
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_platform_store")
public class StoragePlatformEntity {

	/**
	 * id
	 */
	private Integer id;

	/**
	 * 名称
	 */
	private String name;

	/**
	 * 配置,存储时需加密，从数据库获取后需解密
	 */
	private String config;

	/**
	 * 类型
	 */
	private Integer type;


	/**
	 * 是否默认存储
	 * true:默认存储，false 非默认
	 */
	@TableField("default_status")
	private boolean defaultStatus = false;

	/**
	 * 创建时间
	 */
	@TableField("create_time")
	private Date createTime;

	/**
	 * 更新时间
	 */
	@TableField("update_time")
	private Date updateTime;

	/**
	 * 删除标记
	 */
	private Integer deleted;
}
