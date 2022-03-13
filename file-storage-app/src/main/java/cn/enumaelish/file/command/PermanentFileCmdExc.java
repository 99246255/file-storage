package cn.enumaelish.file.command;

import cn.enumaelish.file.domain.ability.FileInfoService;
import cn.enumaelish.file.domain.ability.HydraFileStorageFactory;
import cn.enumaelish.file.domain.gateway.FileStorage;
import cn.enumaelish.file.dto.file.PermanentFileCmd;
import cn.enumaelish.file.domain.model.FileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author: EnumaElish
 * @Date: 2022/3/1 20:11
 * @Description: 标记为永久文件，会校验文件是否存在与一致性
 */
@Component
public class PermanentFileCmdExc {

	@Autowired
	FileInfoService fileInfoService;

	@Autowired
	HydraFileStorageFactory hydraFileStorageFactory;


	@Transactional(rollbackFor = Exception.class)
	public void execute(PermanentFileCmd permanentFileCmd){
		FileInfo fileInfo = fileInfoService.getByFileId(permanentFileCmd.getFileId());
		FileStorage fileStorage = hydraFileStorageFactory.getByStoreId(fileInfo.getStoreId());
		fileStorage.check(fileInfo);
		fileInfoService.permanentFile(fileInfo.getId());
	}

}
