package cn.enumaelish.file.domain.ability;

import cn.enumaelish.file.domain.model.StoragePlatform;

import java.util.List;

/**
 * @author: EnumaElish
 * @Date: 2022/2/11 20:44
 * @Description:
 */
public interface PlatformStoreService {

	/**
	 * 获取所有存储平台信息
	 * @return
	 */
	List<StoragePlatform> listAll();

	/**
	 * 根据id获取存储平台
	 * @param id
	 * @return
	 */
	StoragePlatform getById(Integer id);

	/**
	 * 添加存储平台
	 * @param storagePlatform
	 * @return 存储平台id
	 */
	Integer add(StoragePlatform storagePlatform);


	/**
	 * 删除存储平台
	 * @param id
	 * @return
	 */
	boolean deleteById(Integer id);
	/**
	 * 取消其他设置的默认存储
	 * @param id
	 */
	void setDefaultStatusFalse(Integer id);

	/**
	 * 修改存储平台
	 * @param storage
	 * @return
	 */
	boolean updateById(StoragePlatform storage);
}
