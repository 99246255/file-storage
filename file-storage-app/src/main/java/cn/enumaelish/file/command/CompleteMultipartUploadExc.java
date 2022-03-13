package cn.enumaelish.file.command;

import cn.enumaelish.file.domain.ability.FileInfoService;
import cn.enumaelish.file.domain.ability.HydraFileStorageFactory;
import cn.enumaelish.file.domain.gateway.FileStorage;
import cn.enumaelish.file.dto.file.CompleteMultipartUploadCmd;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import cn.enumaelish.file.domain.model.FileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author: EnumaElish
 * @Date: 2022/3/2 20:39
 * @Description: 分片上传
 */
@Component
public class CompleteMultipartUploadExc {

	@Autowired
	FileInfoService fileInfoService;

	@Autowired
	HydraFileStorageFactory hydraFileStorageFactory;


	@Transactional(rollbackFor = Exception.class)
	public boolean execute(CompleteMultipartUploadCmd cmd){
		FileInfo fileInfo = fileInfoService.getByFileId(cmd.getFileId());
		if(StringUtils.isEmpty(fileInfo.getUploadId())){
			// 不需要合并
			return false;
		}
		FileStorage fileStorage = hydraFileStorageFactory.getByStoreId(fileInfo.getStoreId());
		boolean success = fileStorage.completeMultipartUpload(fileInfo);
		if(success){
			// 去除分片uploadId信息
			fileInfoService.setUploadIdById(fileInfo.getId(), null);
		}
		return success;
	}
}
