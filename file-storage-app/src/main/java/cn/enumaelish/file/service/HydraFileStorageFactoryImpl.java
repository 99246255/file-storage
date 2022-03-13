package cn.enumaelish.file.service;

import cn.enumaelish.file.domain.ability.BusinessTypeService;
import cn.enumaelish.file.domain.ability.HydraFileStorageFactory;
import cn.enumaelish.file.domain.ability.PlatformStoreService;
import cn.enumaelish.file.domain.ability.StoragePlatformConfig;
import cn.enumaelish.file.domain.gateway.FileStorage;
import cn.enumaelish.file.domain.model.BusinessType;
import cn.enumaelish.file.domain.model.StoragePlatform;
import cn.enumaelish.file.domain.spi.ExtensionLoaderFactory;
import cn.enumaelish.file.dto.business.SaveBusinessTypeCmd;
import cn.enumaelish.file.dto.storage.AddStorageCmd;
import cn.enumaelish.file.dto.storage.UpdateStorageCmd;
import cn.enumaelish.file.enums.DeleteFlagEnum;
import com.alibaba.cola.exception.BizException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: EnumaElish
 * @Date: 2022/2/18 20:40
 * @Description: TODO 此更新操作低频且数据量极小，所以实现直接放内存，集群后续再考虑实现
 */
@Service
public class HydraFileStorageFactoryImpl implements HydraFileStorageFactory {

	Map<Integer, FileStorage> platformMap = new ConcurrentHashMap<>();

	Map<Integer, BusinessType> businessTypeMap = new ConcurrentHashMap<>();

	@Autowired
	PlatformStoreService platformStoreService;

	@Autowired
	BusinessTypeService businessTypeService;

	FileStorage defaultFileStorage;

	/**
	 * 历史直接上传业务分类
	 */
	static final Integer HISTORY_BUSINESS_TYPE = 2;

	/**
	 * 历史IOT直接上传业务分类
	 */
	static final Integer HISTORY_IOT_BUSINESS_TYPE = 3;
	/**
	 * 一小时的毫秒数
	 */
	public static final Long ONE_HOUR_TIME = 3600 * 1000L;
	/**
	 * 一天的毫秒数
	 */
	public static final Long ONE_DAY_TIME = 24 * 3600 * 1000L;

	@Override
	@PostConstruct
	public void refresh(){
		platformMap.clear();
		//存储平台一般情况下不会变化，而且不会很多，所以直接全量加载至内存，后续变化调整实现
		List<StoragePlatform> storagePlatforms = platformStoreService.listAll();
		if(CollectionUtils.isEmpty(storagePlatforms)) {
			throw new BizException("无存储平台配置");
		}
		for(StoragePlatform storagePlatform : storagePlatforms){
			putStorageMap(storagePlatform);
		}
		if(defaultFileStorage == null){
			// 没有默认的存储平台，强制选择第一个
			defaultFileStorage = platformMap.get(storagePlatforms.get(0).getId());
		}

		businessTypeMap.clear();
		List<BusinessType> businessTypes = businessTypeService.listAll();
		for(BusinessType businessType: businessTypes){
			businessTypeMap.put(businessType.getId(), businessType);
		}
	}

	private void putStorageMap(StoragePlatform storagePlatform) {
		Class<? extends FileStorage> extensionClass = ExtensionLoaderFactory.getExtensionLoader(FileStorage.class).getExtensionClass(storagePlatform.getType().toString());
		try {
			FileStorage fileStorage = extensionClass.newInstance();
			fileStorage.init(storagePlatform);
			platformMap.put(storagePlatform.getId(), fileStorage);
			if(storagePlatform.isDefaultStatus()){
				defaultFileStorage = fileStorage;
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}


	@Override
	public FileStorage getByStoreId(Integer storeId) {
		return platformMap.getOrDefault(storeId, defaultFileStorage);
	}

	@Override
	public boolean addStorageCmd(AddStorageCmd addStorageCmd) {
		StoragePlatform storagePlatform = new StoragePlatform();
		setStoragePlatform(addStorageCmd, storagePlatform);
		storagePlatform.setCreateTime(new Date());
		Integer id = platformStoreService.add(storagePlatform);
		storagePlatform.setId(id);
		putStorageMap(storagePlatform);
		return true;
	}

	private void setStoragePlatform(AddStorageCmd addStorageCmd, StoragePlatform storagePlatform) {
		StoragePlatformConfig extension = ExtensionLoaderFactory.getExtensionLoader(StoragePlatformConfig.class).getExtension(String.valueOf(addStorageCmd.getType()));
		extension.deserialize(addStorageCmd.getConfig());
		storagePlatform.setConfig(extension);
		storagePlatform.setType(addStorageCmd.getType());
		storagePlatform.setDeleted(DeleteFlagEnum.NOT_DELETE.getIsDelete());
		storagePlatform.setDefaultStatus(addStorageCmd.isDefaultStatus());
		if(addStorageCmd.isDefaultStatus()){
			platformStoreService.setDefaultStatusFalse(null);
		}
		storagePlatform.setName(addStorageCmd.getName());
		storagePlatform.setUpdateTime(new Date());
	}

	@Override
	public boolean updateStorageCmd(UpdateStorageCmd addStorageCmd) {
		StoragePlatform storage = platformStoreService.getById(addStorageCmd.getId());
		if(storage == null){
			throw new BizException("找不到此存储平台");
		}
		setStoragePlatform(addStorageCmd, storage);
		boolean b = platformStoreService.updateById(storage);
		putStorageMap(storage);
		return b;
	}


	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean deleteStorageByStoreId(Integer storeId) {
		boolean delete = platformStoreService.deleteById(storeId);
		if(delete){
			platformMap.remove(storeId);
			if(platformMap.size() == 0){
				throw new BizException("不允许删除唯一存储配置");
			}
			if(defaultFileStorage.getPlatformStore().getId().equals(storeId)){
				for (Map.Entry<Integer, FileStorage> entry: platformMap.entrySet()){
					defaultFileStorage = entry.getValue();
					break;
				}
			}
		}
		return delete;
	}

	@Override
	public String getPath(Integer businessType, String fileId) {
		return new StringBuilder().append(businessType).append("/").append(fileId).toString();
	}

	@Override
	public Date getUploadExpireTime() {
		return new Date(System.currentTimeMillis() + ONE_DAY_TIME);
	}


	@Override
	public FileStorage getByBusinessType(Integer businessType) {
		if(businessType == null){
			return defaultFileStorage;
		}
		if(!businessTypeMap.containsKey(businessType)){
			return defaultFileStorage;
		}
		BusinessType businessType1 = businessTypeMap.get(businessType);
		return platformMap.getOrDefault(businessType1.getStoreId(), defaultFileStorage);
	}

	@Override
	public boolean saveBusinessType(SaveBusinessTypeCmd saveBusinessTypeCmd) {
		BusinessType businessType = businessTypeService.getById(saveBusinessTypeCmd.getId());
		boolean flag;
		Date date = new Date();
		if(businessType == null){
			businessType = new BusinessType();
			setBusinessType(saveBusinessTypeCmd, businessType, date);
			businessType.setCreateTime(date);
			flag = businessTypeService.add(businessType);
		}else{
			setBusinessType(saveBusinessTypeCmd, businessType, date);
			flag = businessTypeService.update(businessType);
		}
		if(flag) {
			businessTypeMap.put(businessType.getId(), businessType);
		}
		return flag;
	}

	@Override
	public boolean deleteBusinessType(Integer businessType) {
		boolean delete = businessTypeService.delete(businessType);
		if(delete){
			businessTypeMap.remove(delete);
		}
		return delete;
	}

	/**
	 * 分片最小上传单位1M
	 */
	public long ONE_M_SIZE = 1024*1024;

	public long ONE_HUNDRED_M_SIZE = 100 * ONE_M_SIZE;

	public long ONE_G_SIZE = 1024 * ONE_M_SIZE;

	/**
	 * 分片策略，可根据需要调整，尽量控制100片，特大200片以内
	 * @param size
	 * @return
	 */
	@Override
	public long getFileMulPartPageSize(long size) {
		if(size < ONE_M_SIZE){
			throw new BizException("文件小于1M,不支持分片上传");
		}
		long pageSize;
		if(size <= ONE_HUNDRED_M_SIZE){
			pageSize = ONE_M_SIZE;
		}
		if(size < ONE_G_SIZE){
			pageSize = 10 * ONE_M_SIZE;
		}else{
			pageSize = size / 200;
		}
		return pageSize;
	}

	private void setBusinessType(SaveBusinessTypeCmd saveBusinessTypeCmd, BusinessType businessType, Date date) {
		businessType.setName(saveBusinessTypeCmd.getName());
		businessType.setId(saveBusinessTypeCmd.getId());
		businessType.setStoreId(saveBusinessTypeCmd.getStoreId());
		businessType.setUpdateTime(date);
	}
}
