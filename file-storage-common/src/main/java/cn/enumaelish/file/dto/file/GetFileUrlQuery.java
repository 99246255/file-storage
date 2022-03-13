package cn.enumaelish.file.dto.file;

import cn.enumaelish.file.enums.FileConstants;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

/**
 * @author: EnumaElish
 * @Date: 2022/2/25 19:10
 * @Description:
 */
@Data
public class GetFileUrlQuery {

	/**
	 * 文件标识
	 */
	@ApiModelProperty(value = "文件标识")
	@NotEmpty(message = FileConstants.FILE_ID_NOT_NULL)
	@Length(min=FileConstants.NOT_EMPTY_MIN_LENGTH,max=FileConstants.FILE_ID_MAX_LENGTH,message = FileConstants.FILE_ID_LENGTH_DESC)
	private String fileId;

	/**
	 * 过期时间(毫秒），默认3600000L，一小时
	 */
	@ApiModelProperty(value = "过期时间(毫秒），默认3600000L，一小时")
	private long expireTime = 3600000L;

	/**
	 * true 外网， false:内网
	 * 默认外网，一些特殊情况可通过此参数调整
	 */
	@ApiModelProperty(value = "true 外网， false:内网,默认外网，一些特殊情况可通过此参数调整")
	private boolean extranet = true;
}
