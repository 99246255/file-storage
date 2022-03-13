package cn.enumaelish.file.convertor;

import cn.enumaelish.file.domain.model.FileInfo;
import cn.enumaelish.file.entity.FileInfoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author: EnumaElish
 * @Date: 2022/3/3 19:11
 * @Description: 
 */
@Mapper
public interface FileInfoConverter {

	FileInfoConverter INSTANCE = Mappers.getMapper(FileInfoConverter.class);

	@Mappings({
			@Mapping(expression = "java(fileInfo.getStatus().getValue())",target = "status")
	})
	FileInfoEntity toFileInfoEntity(FileInfo fileInfo);

	@Mappings({
			@Mapping(expression = "java(cn.enumaelish.file.enums.FileStatusEnum.getByValue(fileInfo.getStatus()))",target = "status")
	})
	FileInfo toFileInfo(FileInfoEntity fileInfo);

	List<FileInfo> listToFileInfo(List<FileInfoEntity> list);

	List<FileInfoEntity> listToFileInfoEntity(List<FileInfo> list);
}
