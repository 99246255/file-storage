package cn.enumaelish.file.domain.model;

import cn.enumaelish.file.enums.FileConstants;
import cn.enumaelish.file.enums.FileStatusEnum;
import com.alibaba.cola.exception.BizException;
import lombok.Data;

import java.util.Date;

/**
 * @author: EnumaElish
 * @Date: 2022/1/20 20:36
 * @Description: 文件信息 记录存储平台，以及存储平台路径
 */
@Data
public class FileInfo{

	/**
	 * id
	 */
	private Long id;

	/**
	 * 文件大小
	 */
	private Long size;

	/**
	 * 文件md5, 小写
	 */
	private String md5;

	/**
	 * 对外暴露的文件id唯一主键
	 */
	private String fileId;

	/**
	 * 存储路径
	 */
	private String path;

	/**
	 * 文件类型
	 */
	private String fileType;

	/**
	 * 存储id
	 */
	private Integer storeId;

	/**
	 * 业务类型
	 */
	private Integer businessType;

	/**
	 * 过期时间
	 */
	private Date expireTime;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 更新时间
	 */
	private Date updateTime;

	/**
	 * 文件名称
	 */
	private String fileName;

	/**
	 * 文件状态,默认临时文件，需要标记之后才会改成永久文件，永久文件才会秒传
	 */
	private FileStatusEnum status = FileStatusEnum.TEMPORARY;

	/**
	 * 是否校验md5
	 */
	private boolean checkMd5 = true;


	/**
	 * 分片上传标记，不为空标识分片上传
	 */
	private String uploadId;

	/**
	 * 分片策略，可根据需要调整，尽量控制100片，特大200片以内
	 * @return
	 */
	public long getFileMulPartPageSize() {
		if(size < FileConstants.ONE_M_SIZE){
			throw new BizException("文件小于1M,不支持分片上传");
		}
		long pageSize;
		if(size <= FileConstants.ONE_HUNDRED_M_SIZE){
			pageSize = FileConstants.ONE_M_SIZE;
		}else if(size < FileConstants.ONE_G_SIZE){
			pageSize = 10 * FileConstants.ONE_M_SIZE;
		}else{
			pageSize = size / 200;
		}
		return pageSize;
	}

	public int getPageCount(){
		long pageSize = getFileMulPartPageSize();
		int partCount = (int) (size / pageSize);
		if (size % pageSize != 0) {
			partCount++;
		}
		return partCount;
	}
}
