package cn.enumaelish.file.query;

import cn.enumaelish.file.domain.ability.FileInfoService;
import cn.enumaelish.file.domain.ability.HydraFileStorageFactory;
import cn.enumaelish.file.domain.gateway.FileStorage;
import cn.enumaelish.file.domain.model.FileInfo;
import cn.enumaelish.file.dto.data.FileUrlDto;
import cn.enumaelish.file.dto.file.GetFileUrlQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author: EnumaElish
 * @Date: 2022/3/1 20:03
 * @Description:  获取文件下载链接
 */
@Component
public class GetFileUrlQueryExc {

	@Autowired
	FileInfoService fileInfoService;

	@Autowired
	HydraFileStorageFactory hydraFileStorageFactory;

	public FileUrlDto execute(GetFileUrlQuery getFileUrlQuery) {
		FileInfo fileInfo = fileInfoService.getByFileId(getFileUrlQuery.getFileId());
		FileStorage fileStorage = hydraFileStorageFactory.getByStoreId(fileInfo.getStoreId());
		String downloadFileUrl = fileStorage.getDownloadFileUrl(fileInfo.getPath(), fileInfo.getFileName(), getFileUrlQuery.getExpireTime(), getFileUrlQuery.isExtranet());
		FileUrlDto fileUrlDto = new FileUrlDto();
		fileUrlDto.setUrl(downloadFileUrl);
		fileUrlDto.setFileName(fileInfo.getFileName());
		fileUrlDto.setFileType(fileInfo.getFileType());
		fileUrlDto.setSize(fileInfo.getSize());
		fileUrlDto.setMd5(fileInfo.getMd5());
		return fileUrlDto;
	}
}
