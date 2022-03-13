package cn.enumaelish.file.entity;

import cn.enumaelish.file.enums.DeleteFlagEnum;
import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author: EnumaElish
 * @Date: 2022/3/3 20:33
 * @Description: 
 */
@Data
@TableName("t_file_info")
public class FileInfoEntity{

	/**
	 * id
	 */
	private Long id;

	/**
	 * 文件大小
	 */
	private Long size;

	/**
	 * 文件md5
	 */
	private String md5;

	/**
	 * 对外暴露的文件id唯一主键
	 */
	@TableField("file_id")
	private String fileId;

	/**
	 * 存储路径
	 */
	private String path;

	/**
	 * 文件类型
	 */
	@TableField("file_type")
	private String fileType;

	/**
	 * 文件名称
	 */
	@TableField("file_name")
	private String fileName;

	/**
	 * 存储id
	 */
	@TableField("store_id")
	private Integer storeId;

	/**
	 * 业务类型
	 */
	@TableField("business_type")
	private Integer businessType;

	/**
	 * 过期时间
	 */
	@TableField("expire_time")
	private Date expireTime;

	/**
	 * 创建时间
	 */
	@TableField("create_time")
	private Date createTime;

	/**
	 * 更新时间
	 */
	@TableField("update_time")
	private Date updateTime;


	@TableLogic
	@JSONField(serialize = false)
	private Integer deleted = DeleteFlagEnum.NOT_DELETE.getIsDelete();

	/**
	 * 文件状态,默认临时文件，需要标记之后才会改成永久文件，永久文件才会秒传
	 */
	private Integer status;

	/**
	 * 是否校验md5
	 */
	@TableField("check_md5")
	private boolean checkMd5 = true;


	/**
	 * 分片上传标记，不为空标识分片上传
	 */
	@TableField("upload_id")
	private String uploadId;

}
