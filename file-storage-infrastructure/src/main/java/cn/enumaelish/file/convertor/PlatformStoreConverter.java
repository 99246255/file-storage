package cn.enumaelish.file.convertor;

import cn.enumaelish.file.domain.ability.StoragePlatformConfig;
import cn.enumaelish.file.domain.model.StoragePlatform;
import cn.enumaelish.file.domain.spi.ExtensionLoaderFactory;
import cn.enumaelish.file.entity.StoragePlatformEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author: EnumaElish
 * @Date: 2022/3/3 19:12
 * @Description: 
 */
@Mapper
public interface PlatformStoreConverter {

	PlatformStoreConverter INSTANCE = Mappers.getMapper(PlatformStoreConverter.class);

	@Mappings({
			@Mapping(expression = "java(storagePlatform.getConfig().encrypt())",target = "config")
	})
	StoragePlatformEntity toPlatformStoreEntity(StoragePlatform storagePlatform);

	@Mappings({
			@Mapping(expression = "java(deserialize(platformStore.getType(), platformStore.getConfig()))",target = "config")
	})
	StoragePlatform toPlatformStore(StoragePlatformEntity platformStore);

	List<StoragePlatform> toPlatformStoreList(List<StoragePlatformEntity> list);

	default StoragePlatformConfig deserialize(Integer type, String config){
		StoragePlatformConfig extension = ExtensionLoaderFactory.getExtensionLoader(StoragePlatformConfig.class).getExtension(String.valueOf(type));
		extension.deserialize(config);
		extension.desDecrypt();
		return extension;
	}
}
