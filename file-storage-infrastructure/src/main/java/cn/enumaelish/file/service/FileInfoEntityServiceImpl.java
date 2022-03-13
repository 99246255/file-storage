package cn.enumaelish.file.service;

import cn.enumaelish.file.convertor.FileInfoConverter;
import cn.enumaelish.file.dao.FileInfoEntityMapper;
import cn.enumaelish.file.domain.ability.FileInfoService;
import cn.enumaelish.file.domain.model.FileInfo;
import cn.enumaelish.file.entity.FileInfoEntity;
import cn.enumaelish.file.enums.FileConstants;
import cn.enumaelish.file.enums.FileStatusEnum;
import com.alibaba.cola.exception.BizException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * @author: EnumaElish
 * @Date: 2022/3/3 19:24
 * @Description: 后续量大考虑分库分表
 */
@Service
public class FileInfoEntityServiceImpl extends ServiceImpl<FileInfoEntityMapper, FileInfoEntity> implements FileInfoService {


	@Override
	public FileInfo getByFileId(String fileId) {
		if(StringUtils.isEmpty(fileId)){
			throw new BizException("文件标识不可为空");
		}
		FileInfoEntity fileInfoEntity = baseMapper.selectOne(new QueryWrapper<FileInfoEntity>().lambda().eq(FileInfoEntity::getFileId, fileId));
		if(fileInfoEntity == null){
			throw new BizException("没有对应的文件");
		}
		return FileInfoConverter.INSTANCE.toFileInfo(fileInfoEntity);
	}

	@Override
	public Long insert(FileInfo fileInfo) {
		FileInfoEntity fileInfoEntity = FileInfoConverter.INSTANCE.toFileInfoEntity(fileInfo);
		baseMapper.insert(fileInfoEntity);
		return fileInfo.getId();
	}

	@Override
	public void deleteById(Long id) {
		baseMapper.deleteById(id);
	}

	@Override
	public FileInfo getByMd5AndBusinessType(String md5, Integer businessType) {
		if(StringUtils.isEmpty(md5) || businessType == null){
			return null;
		}
		FileInfoEntity fileInfoEntity = baseMapper.selectOne(new QueryWrapper<FileInfoEntity>().lambda().eq(FileInfoEntity::getMd5, md5).eq(FileInfoEntity::getBusinessType, businessType).eq(FileInfoEntity::getStatus, FileStatusEnum.PERMANENT.getValue()).eq(FileInfoEntity::isCheckMd5, true).last(" limit 1"));
		return FileInfoConverter.INSTANCE.toFileInfo(fileInfoEntity);
	}

	@Override
	public Long countByPathAndBusinessType(String path, Integer businessType, Long id) {
		if(StringUtils.isEmpty(path) || businessType == null || id == null){
			return null;
		}
		return baseMapper.selectCount(new QueryWrapper<FileInfoEntity>().lambda().eq(FileInfoEntity::getPath, path).eq(FileInfoEntity::getBusinessType, businessType).eq(FileInfoEntity::getStatus, FileStatusEnum.PERMANENT.getValue()).eq(FileInfoEntity::isCheckMd5, true).ne(FileInfoEntity::getId, id));
	}

	@Override
	public void permanentFile(Long id) {
		if(id == null){
			return;
		}
		baseMapper.update(null, new LambdaUpdateWrapper<FileInfoEntity>().eq(FileInfoEntity::getId, id).set(FileInfoEntity::getStatus, FileStatusEnum.PERMANENT.getValue()).eq(FileInfoEntity::getExpireTime, FileConstants.NO_EXPIRE_TIME));
	}

	@Override
	public List<FileInfo> listExpireFile() {
		List<FileInfoEntity> fileInfoEntities = baseMapper.selectList(new QueryWrapper<FileInfoEntity>().lambda().le(FileInfoEntity::getExpireTime, new Date()).eq(FileInfoEntity::getStatus, FileStatusEnum.TEMPORARY.getValue()));
		return FileInfoConverter.INSTANCE.listToFileInfo(fileInfoEntities);
	}

	@Override
	public boolean setUploadIdById(Long id, String uploadId) {
		if(id == null){
			return false;
		}
		return baseMapper.update(null, new LambdaUpdateWrapper<FileInfoEntity>().eq(FileInfoEntity::getId, id).set(FileInfoEntity::getUploadId, uploadId == null ? "": uploadId)) > 0;
	}
}