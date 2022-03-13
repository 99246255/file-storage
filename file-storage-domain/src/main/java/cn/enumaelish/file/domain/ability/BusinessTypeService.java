package cn.enumaelish.file.domain.ability;

import cn.enumaelish.file.domain.model.BusinessType;

import java.util.List;

/**
 * @author: EnumaElish
 * @Date: 2022/2/11 20:45
 * @Description:
 */
public interface BusinessTypeService {

	/**
	 * 获取所有业务类型配置
	 * @return
	 */
	List<BusinessType> listAll();

	/**
	 * 根据id获取业务类型
	 * @param id
	 * @return
	 */
	BusinessType getById(Integer id);

	/**
	 * 添加业务类型
	 * @param businessType
	 * @return
	 */
	boolean add(BusinessType businessType);

	/**
	 * 删除业务类型
	 * @param id
	 * @return
	 */
	boolean delete(Integer id);

	/**
	 * 修改业务类型
	 * @param businessType
	 * @return
	 */
	boolean update(BusinessType businessType);
}
