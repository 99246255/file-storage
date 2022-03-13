package cn.enumaelish.file.domain.ability;

import cn.enumaelish.file.domain.model.FileInfo;

import java.util.List;

/**
 * @author: EnumaElish
 * @Date: 2022/2/11 20:43
 * @Description:
 */
public interface FileInfoService {


	/**
	 * 根据文件id获取文件信息
	 * @param fileId
	 * @return
	 */
	FileInfo getByFileId(String fileId);

	/**
	 * 插入文件
	 * @param fileInfo
	 */
	Long insert(FileInfo fileInfo);

	/**
	 * 根据id删除文件
	 * @param id
	 */
	void deleteById(Long id);

	/**
	 * 根据md5和业务类型查询永久并且需要校验的文件，如果有多条，只取第一条
	 * @param md5
	 * @param businessType
	 * @return
	 */
	FileInfo getByMd5AndBusinessType(String md5, Integer businessType);

	/**
	 * 根据文件路径查询永久并且需要校验的文件，如果有多条，只取第一条
	 * @param path 文件存储路径
	 * @param businessType 业务类型
	 * @param id 已有的文件id
	 * @return 这个是小频率，暂时没加索引，后续根据情况添加索引
	 */
	Long countByPathAndBusinessType(String path, Integer businessType, Long id);

	/**
	 * 标记文件为永久文件，设置过期时间为永久以及状态为永久
	 * @param id 文件id
	 */
	void permanentFile(Long id);

	/**
	 * 获取已经过期的临时文件
	 * @return
	 */
	List<FileInfo> listExpireFile();

	/**
	 * 设置分片id
	 * @param id
	 * @param uploadId
	 * @return
	 */
	boolean setUploadIdById(Long id, String uploadId);
}
