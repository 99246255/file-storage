package cn.enumaelish.file.domain.gateway;

import cn.enumaelish.file.domain.ability.StoragePlatformConfig;
import cn.enumaelish.file.domain.model.FileInfo;
import cn.enumaelish.file.domain.model.StoragePlatform;
import cn.enumaelish.file.domain.model.gateway.PutObjectBytesCmd;
import cn.enumaelish.file.dto.data.GeneratePutUrlResult;
import cn.enumaelish.file.dto.data.MultipartUploadResult;
import com.alibaba.cola.exception.BizException;

/**
 * @author: EnumaElish
 * @Description: 文件存储接口，对应各个平台,本来这部分应该放infrastructure，但是为了扩展性，单独放extension,如果extension臃肿，也可自行加一个拓展包
 * 此处去除了bucket的概念，bucket已经在filestorage实现中默认了的
 * 目前如果有不同的bucket，为不同的platformstore
 */
public interface FileStorage<T extends StoragePlatformConfig> {

	/**
	 * 获取存储平台信息
	 * @return
	 */
	StoragePlatform<T> getPlatformStore();

	/**
	 * 初始化配置
	 * @param storagePlatform
	 */
	void init(StoragePlatform<T> storagePlatform);
	/**
	 * 直传保存文件
	 * @param putObjectBytesCmd
	 * @return md5
	 *
	 */
	String save(PutObjectBytesCmd putObjectBytesCmd);


	/**
	 * 删除文件
	 * @param key 文件路径
	 * @return
	 */
	boolean delete(String key);

	/**
	 * 校验文件
	 * @param fileInfo 文件信息
	 * @return
	 */
	boolean check(FileInfo fileInfo);

	/**
	 * 获取文件下载链接
	 * @param key 文件路径
	 * @param fileName 下载文件名
	 * @param expireTime 过期时间（距离当前时间,单位毫秒）
	 * @param extranet     true外网;false:内网,默认外网，一些特殊情况可通过此参数调整
	 * @return url
	 */
	String getDownloadFileUrl(String key,String fileName, long expireTime, boolean extranet);

	/**
	 * 上传url信息授权给第三方/前端/客户端
	 * @param fileInfo
	 * @param extranet     true外网;false:内网,默认外网，一些特殊情况可通过此参数调整
	 * @return
	 */
	GeneratePutUrlResult generatePutUrl(FileInfo fileInfo,boolean extranet);


	/**
	 * 初始化分片上传事件
	 * @param fileInfo
	 * @param extranet
	 * @return
	 */
	default String initiateMultipartUpload(FileInfo fileInfo, boolean extranet){
		throw new BizException("此功能暂未实现");
	}
	/**
	 * 生成分片上传url
	 * @param fileInfo
	 * @param extranet
	 * @return
	 */
	default MultipartUploadResult generateMultipartUrl(FileInfo fileInfo, boolean extranet){
		throw new BizException("此功能暂未实现");
	}


	/**
	 * 合并分片
	 * @param fileInfo
	 * @return
	 */
	default boolean completeMultipartUpload(FileInfo fileInfo){
		throw new BizException("此功能暂未实现");
	}
}
