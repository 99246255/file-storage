package cn.enumaelish.file.dto.file;

import cn.enumaelish.file.enums.FileConstants;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author: EnumaElish
 * @Date: 2022/2/25 19:10
 * @Description: 生成上传url
 */
@Data
public class GeneratePutUrlCmd {

	/**
	 * 业务类型
	 */
	@ApiModelProperty(value = "业务类型")
	@NotNull(message = "业务类型不能为空")
	private Integer businessType = 1;

	/**
	 * 文件大小
	 */
	@ApiModelProperty(value = "文件大小")
	@NotNull(message = "文件大小不能为空")
	private Long size;

	/**
	 * 文件名称
	 */
	@ApiModelProperty(value = "文件名称")
	@NotEmpty(message = "文件名称不能为空")
	@Length(min=1,max=255,message = "文件名称限制1~255")
	private String fileName;

	/**
	 * 文件md5
	 */
	@ApiModelProperty(value = "文件md5")
	@NotEmpty(message = "文件md5不能为空")
	@Length(min= FileConstants.NOT_EMPTY_MIN_LENGTH,max= FileConstants.FILE_ID_MAX_LENGTH,message = "文件md5限制1~40")
	private String md5;


	/**
	 * true 外网， false:内网
	 * 默认外网，一些特殊情况可通过此参数调整
	 */
	@ApiModelProperty(value = "true 外网， false:内网,默认外网，一些特殊情况可通过此参数调整")
	private boolean extranet = true;

	/**
	 * true校验md5,false不校验，前端个性化项目md5加密实现有问题
	 */
	@ApiModelProperty(value = "true校验md5,false不校验，前端个性化项目md5加密实现有问题")
	private boolean check = true;
}
