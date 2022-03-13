package cn.enumaelish.file.command;

import cn.enumaelish.file.domain.ability.FileInfoService;
import cn.enumaelish.file.domain.ability.HydraFileStorageFactory;
import cn.enumaelish.file.domain.gateway.FileStorage;
import cn.enumaelish.file.domain.model.gateway.PutObjectBytesCmd;
import cn.enumaelish.file.dto.data.PutObjectResult;
import cn.enumaelish.file.dto.file.PutObjectCmd;
import cn.enumaelish.file.enums.FileStatusEnum;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import cn.enumaelish.file.domain.model.FileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author: EnumaElish
 * @Date: 2022/3/1 20:06
 * @Description: 上传文件, 这里不做秒传了，都文件传上来的，没意义
 */
@Component
public class PutObjectCmdExc {

	@Autowired
	FileInfoService fileInfoService;

	@Autowired
	HydraFileStorageFactory hydraFileStorageFactory;
	/**
	 *
	 * @param putObjectCmd
	 * @return
	 */
	public PutObjectResult execute(PutObjectCmd putObjectCmd){
		FileStorage fileStorage = hydraFileStorageFactory.getByBusinessType(putObjectCmd.getBusinessType());
		String fileId = IdWorker.getTimeId();
		String path = hydraFileStorageFactory.getPath(putObjectCmd.getBusinessType(), fileId);
		FileInfo fileInfo = new FileInfo();
		fileInfo.setFileId(fileId);
		fileInfo.setFileName(putObjectCmd.getFileName());
		fileInfo.setFileType(putObjectCmd.getFileType());
		String md5 = fileStorage.save(new PutObjectBytesCmd(path, putObjectCmd.getInputStream(), putObjectCmd.getFileType(), putObjectCmd.isExtranet()));
		fileInfo.setMd5(md5);
		fileInfo.setBusinessType(putObjectCmd.getBusinessType());
		fileInfo.setSize(putObjectCmd.getSize());
		fileInfo.setStoreId(fileStorage.getPlatformStore().getId());
		fileInfo.setPath(path);
		fileInfo.setStatus(FileStatusEnum.TEMPORARY);
		fileInfo.setExpireTime(hydraFileStorageFactory.getUploadExpireTime());
		// 此处一定是能正确校验的
		fileInfo.setCheckMd5(true);
		fileInfoService.insert(fileInfo);
		return new PutObjectResult(fileId);
	}

}
