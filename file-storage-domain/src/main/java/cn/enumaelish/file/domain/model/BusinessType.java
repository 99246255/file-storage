package cn.enumaelish.file.domain.model;

import lombok.Data;

import java.util.Date;

/**
 * @author: EnumaElish
 * @Date: 2022/1/20 20:49
 * @Description: 业务类型
 * 目前设计一个业务类型对应一个platformStore
 * 但是支持多个业务类型对应的是同一个platformStore，即对应关系是1:1，但允许重复对应platformStore
 * 如果要一个业务类型对应多个platformStore，不支持！！！
 * 非要如此请先数据迁移到一个platformStore，然后使用这个platformStore
 */
@Data
public class BusinessType {
	/**
	 * id
	 */
	private Integer id;

	/**
	 * 名称
	 */
	private String name;

	/**
	 * 使用的存储id
	 */
	private Integer storeId;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 更新时间
	 */
	private Date updateTime;
}
