package cn.enumaelish.file.domain.model;

import cn.enumaelish.file.domain.ability.StoragePlatformConfig;
import lombok.Data;

import java.util.Date;

/**
 * @author: EnumaElish
 * @Date: 2022/1/20 20:49
 * @Description: 存储平台，此细致到bucket层，而不是账号层，一个PlatformStore示例对应一个FileStorage实现
 */
@Data
public class StoragePlatform<T extends StoragePlatformConfig> {
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
	private T config;

	/**
	 * 类型
	 */
	private Integer type;


	/**
	 * 是否默认存储
	 * true:默认存储，false 非默认
	 */
	private boolean defaultStatus = false;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 更新时间
	 */
	private Date updateTime;

	/**
	 * 删除标记
	 */
	private Integer deleted;
}
