package cn.enumaelish.file.schedule;

import cn.enumaelish.file.domain.ability.FileInfoService;
import cn.enumaelish.file.domain.ability.HydraFileStorageFactory;
import cn.enumaelish.file.domain.gateway.FileStorage;
import cn.enumaelish.file.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import cn.enumaelish.file.domain.model.FileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author: EnumaElish
 * @Date: 2022/3/1 20:35
 * @Description: 定时任务，后面可换xxl-job
 */
@Component
@Slf4j
public class CleanExpireFileSchedule {

	@Autowired
	FileInfoService fileInfoService;

	@Autowired
	HydraFileStorageFactory hydraFileStorageFactory;


	@Scheduled(cron = "0 0 0/1 * * ? ")//每1小时执行一次
	public void cleanExpireFile(){
		List<FileInfo> list = fileInfoService.listExpireFile();
		for (FileInfo fileInfo: list){
			try {
				Long count = fileInfoService.countByPathAndBusinessType(fileInfo.getPath(), fileInfo.getBusinessType(), fileInfo.getId());
				if(count == null) {
					// 此文件没有其他使用，需要删除，否则不需要删除
					FileStorage fileStorage = hydraFileStorageFactory.getByStoreId(fileInfo.getStoreId());
					fileStorage.delete(fileInfo.getPath());
				}
				fileInfoService.deleteById(fileInfo.getId());
			} catch (Exception e) {
				log.error("删除文件失败{}", JsonUtil.toJSONString(fileInfo),  e);
			}
		}
	}
}
