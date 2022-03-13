package cn.enumaelish.file.service;

import cn.enumaelish.file.convertor.BusinessTypeConverter;
import cn.enumaelish.file.domain.ability.BusinessTypeService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.enumaelish.file.dao.BusinessTypeEntityMapper;
import cn.enumaelish.file.domain.model.BusinessType;
import cn.enumaelish.file.entity.BusinessTypeEntity;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: EnumaElish
 * @Date: 2022/3/3 20:31
 * @Description: 
 */
@Service
public class BusinessTypeEntityServiceImpl extends ServiceImpl<BusinessTypeEntityMapper, BusinessTypeEntity> implements BusinessTypeService {
	@Override
	public List<BusinessType> listAll() {
		List<BusinessTypeEntity> businessTypeEntities = baseMapper.selectList(new QueryWrapper<>());
		return BusinessTypeConverter.INSTANCE.toBusinessTypeEntityList(businessTypeEntities);
	}

	@Override
	public BusinessType getById(Integer id) {
		if(id == null){
			return null;
		}
		BusinessTypeEntity businessTypeEntity = baseMapper.selectById(id);
		return BusinessTypeConverter.INSTANCE.toBusinessType(businessTypeEntity);
	}

	@Override
	public boolean add(BusinessType businessType) {
		if(businessType == null){
			return false;
		}
		return baseMapper.insert(BusinessTypeConverter.INSTANCE.toBusinessTypeEntity(businessType)) > 0;
	}

	@Override
	public boolean delete(Integer id) {
		if(id == null){
			return false;
		}
		return baseMapper.deleteById(id) > 0;
	}

	@Override
	public boolean update(BusinessType businessType) {
		if(businessType == null){
			return false;
		}
		return baseMapper.updateById(BusinessTypeConverter.INSTANCE.toBusinessTypeEntity(businessType)) > 0;
	}
}