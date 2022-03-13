package cn.enumaelish.file.command;

import cn.enumaelish.file.domain.ability.FileInfoService;
import cn.enumaelish.file.domain.ability.HydraFileStorageFactory;
import cn.enumaelish.file.domain.gateway.FileStorage;
import cn.enumaelish.file.dto.file.DeleteFileCmd;
import cn.enumaelish.file.domain.model.FileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author: EnumaElish
 * @Date: 2022/3/1 20:11
 * @Description: 删除文件
 */
@Component
public class DeleteFileCmdExc {

	@Autowired
	FileInfoService fileInfoService;

	@Autowired
	HydraFileStorageFactory hydraFileStorageFactory;


	@Transactional(rollbackFor = Exception.class)
	public boolean execute(DeleteFileCmd deleteFileCmd){
		FileInfo fileInfo = fileInfoService.getByFileId(deleteFileCmd.getFileId());
		Long count = fileInfoService.countByPathAndBusinessType(fileInfo.getPath(), fileInfo.getBusinessType(), fileInfo.getId());
		if(count == null) {
			// 此文件没有其他使用，需要删除，否则不需要删除
			FileStorage fileStorage = hydraFileStorageFactory.getByStoreId(fileInfo.getStoreId());
			fileStorage.delete(fileInfo.getPath());
		}
		fileInfoService.deleteById(fileInfo.getId());
		return true;
	}

}
