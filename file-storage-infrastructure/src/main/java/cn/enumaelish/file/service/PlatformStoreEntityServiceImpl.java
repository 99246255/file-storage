package cn.enumaelish.file.service;

import cn.enumaelish.file.convertor.PlatformStoreConverter;
import cn.enumaelish.file.domain.ability.PlatformStoreService;
import cn.enumaelish.file.domain.model.StoragePlatform;
import cn.enumaelish.file.entity.StoragePlatformEntity;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.enumaelish.file.dao.PlatformStoreEntityMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: EnumaElish
 * @Date: 2022/3/3 20:32
 * @Description: 
 */
@Service
public class PlatformStoreEntityServiceImpl extends ServiceImpl<PlatformStoreEntityMapper, StoragePlatformEntity> implements PlatformStoreService {
	@Override
	public List<StoragePlatform> listAll() {
		List<StoragePlatformEntity> platformStoreEntities = baseMapper.selectList(new QueryWrapper<>());
		return PlatformStoreConverter.INSTANCE.toPlatformStoreList(platformStoreEntities);
	}

	@Override
	public StoragePlatform getById(Integer id) {
		if(id == null){
			return null;
		}
		StoragePlatformEntity businessTypeEntity = baseMapper.selectById(id);
		return PlatformStoreConverter.INSTANCE.toPlatformStore(businessTypeEntity);
	}

	@Override
	public Integer add(StoragePlatform storagePlatform) {
		storagePlatform.getConfig().encrypt();
		StoragePlatformEntity entity = PlatformStoreConverter.INSTANCE.toPlatformStoreEntity(storagePlatform);
		baseMapper.insert(entity);
		return entity.getId();
	}

	@Override
	public boolean deleteById(Integer id) {
		if(id != null) {
			return baseMapper.deleteById(id) > 0;
		}
		return false;
	}

	@Override
	public void setDefaultStatusFalse(Integer id) {
		baseMapper.update(null, new LambdaUpdateWrapper<StoragePlatformEntity>().set(StoragePlatformEntity::isDefaultStatus, false).ne(id != null,StoragePlatformEntity::getId, id));
	}

	@Override
	public boolean updateById(StoragePlatform storagePlatform) {
		storagePlatform.getConfig().encrypt();
		return baseMapper.updateById(PlatformStoreConverter.INSTANCE.toPlatformStoreEntity(storagePlatform)) > 0;
	}
}