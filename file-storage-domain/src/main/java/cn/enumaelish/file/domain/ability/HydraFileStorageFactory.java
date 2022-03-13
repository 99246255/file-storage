package cn.enumaelish.file.domain.ability;

import cn.enumaelish.file.dto.business.SaveBusinessTypeCmd;
import cn.enumaelish.file.dto.storage.AddStorageCmd;
import cn.enumaelish.file.dto.storage.UpdateStorageCmd;
import cn.enumaelish.file.domain.gateway.FileStorage;

import java.util.Date;

/**
 * @author: EnumaElish
 * @Date: 2022/3/3 20:57
 * @Description:
 */
public interface HydraFileStorageFactory {

	/**
	 * 刷新配置
	 */
	void refresh();
	/**
	 * 根据存储id获取存储平台
	 * @param storeId
	 * @return
	 */
	FileStorage getByStoreId(Integer storeId);


	/**
	 * 添加存储平台
	 * @param addStorageCmd
	 * @return
	 */
	boolean addStorageCmd(AddStorageCmd addStorageCmd);

	/**
	 * 修改存储平台
	 * @param addStorageCmd
	 * @return
	 */
	boolean updateStorageCmd(UpdateStorageCmd addStorageCmd);

	/**
	 * 根据存储id删除存储平台
	 * @param storeId
	 * @return
	 */
	boolean deleteStorageByStoreId(Integer storeId);
	/**
	 * 根据businessType和fileId生成存储路径，这个需考虑秒传情况，目前秒传使用已上传的文件path
	 * 为了兼容老的项目，业务类型2为旧的文件上传，3为Iot，存储路径即为fileId
	 * TODO 后续有多种策略，可用策略模式实现
	 * @param businessType
	 * @param fileId
	 * @return
	 */
	String getPath(Integer businessType, String fileId);

	/**
	 * 获取默认的上传过期时间
	 * 根据配置使用不同的策略
	 * @return
	 */
	Date getUploadExpireTime();

	/**
	 * 根据业务类型获取存储平台
	 * @param businessType
	 * @return
	 */
	FileStorage getByBusinessType(Integer businessType);

	/**
	 * 保存业务类型
	 * @param saveBusinessTypeCmd
	 * @return
	 */
	boolean saveBusinessType(SaveBusinessTypeCmd saveBusinessTypeCmd);

	/**
	 * 根据业务类型获取存储平台
	 * @param businessType
	 * @return
	 */
	boolean deleteBusinessType(Integer businessType);

	/**
	 * 获取文件除了最后一片的分片大小
	 * @return
	 */
	long getFileMulPartPageSize(long size);
}
