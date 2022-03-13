package cn.enumaelish.file.command;

import cn.enumaelish.file.domain.ability.FileInfoService;
import cn.enumaelish.file.domain.ability.HydraFileStorageFactory;
import cn.enumaelish.file.domain.gateway.FileStorage;
import cn.enumaelish.file.dto.data.GeneratePutUrlResult;
import cn.enumaelish.file.dto.file.GeneratePutUrlCmd;
import cn.enumaelish.file.enums.FileStatusEnum;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import cn.enumaelish.file.domain.model.FileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * @author: EnumaElish
 * @Date: 2022/3/1 20:11
 * @Description: 获取上传url，支持秒传
 */
@Component
public class GeneratePutUrlCmdExc {

	@Autowired
	FileInfoService fileInfoService;

	@Autowired
	HydraFileStorageFactory hydraFileStorageFactory;


	@Transactional(rollbackFor = Exception.class)
	public GeneratePutUrlResult execute(GeneratePutUrlCmd generatePutUrlCmd){
		FileStorage fileStorage = hydraFileStorageFactory.getByBusinessType(generatePutUrlCmd.getBusinessType());
		FileInfo fileInfo = new FileInfo();
		fileInfo.setFileId(IdWorker.getTimeId());
		fileInfo.setFileName(generatePutUrlCmd.getFileName());
		Optional<MediaType> mediaType = MediaTypeFactory.getMediaType(generatePutUrlCmd.getFileName());
		fileInfo.setFileType(mediaType.orElse(MediaType.APPLICATION_OCTET_STREAM).toString());
		fileInfo.setMd5(generatePutUrlCmd.getMd5());
		fileInfo.setSize(generatePutUrlCmd.getSize());
		fileInfo.setStoreId(fileStorage.getPlatformStore().getId());
		fileInfo.setBusinessType(generatePutUrlCmd.getBusinessType());
		fileInfo.setCheckMd5(generatePutUrlCmd.isCheck());
		String path = hydraFileStorageFactory.getPath(generatePutUrlCmd.getBusinessType(), fileInfo.getFileId());
		// 秒传的路径同已经上传的文件path
		boolean needUpload = true;
		if(generatePutUrlCmd.isCheck()) {
			FileInfo hasInfo = fileInfoService.getByMd5AndBusinessType(fileInfo.getMd5(), fileInfo.getBusinessType());
			if(hasInfo != null) {
				path = hasInfo.getPath();
				needUpload = false;
			}
		}
		fileInfo.setPath(path);
		fileInfo.setExpireTime(hydraFileStorageFactory.getUploadExpireTime());
		fileInfo.setStatus(FileStatusEnum.TEMPORARY);
		fileInfoService.insert(fileInfo);
		if(needUpload) {
			return fileStorage.generatePutUrl(fileInfo, generatePutUrlCmd.isExtranet());
		}else{
			// 秒传
			GeneratePutUrlResult generatePutUrlResult = new GeneratePutUrlResult();
			generatePutUrlResult.setFileId(fileInfo.getFileId());
			generatePutUrlResult.setNeedUpload(false);
			return generatePutUrlResult;
		}
	}

}
