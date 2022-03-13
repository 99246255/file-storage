package cn.enumaelish.file.convertor;

import cn.enumaelish.file.domain.model.BusinessType;
import cn.enumaelish.file.entity.BusinessTypeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author: EnumaElish
 * @Date: 2022/2/26 20:23
 * @Description:
 */
@Mapper
public interface BusinessTypeConverter {

	BusinessTypeConverter INSTANCE = Mappers.getMapper(BusinessTypeConverter.class);

	BusinessTypeEntity toBusinessTypeEntity(BusinessType platformStore);

	BusinessType toBusinessType(BusinessTypeEntity platformStore);

	List<BusinessType> toBusinessTypeEntityList(List<BusinessTypeEntity> list);
}
