package cn.enumaelish.file.dto.storage;

import cn.enumaelish.file.enums.FileConstants;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author: EnumaElish
 * @Date: 2022/2/28 16:34
 * @Description: 添加存储配置
 */
@Data
public class AddStorageCmd {

	/**
	 * 名称
	 */
	@ApiModelProperty(value = "名称")
	@NotEmpty(message = "名称不能为空")
	@Length(min= FileConstants.NOT_EMPTY_MIN_LENGTH,max=FileConstants.NAME_MAX_LENGTH,message = "名称长度限制1~50")
	private String name;

	/**
	 * 加密前的配置
	 */
	@ApiModelProperty(value = "加密前的配置")
	private String config;

	/**
	 * 类型
	 */
	@ApiModelProperty(value = "存储类型")
	@NotNull(message = "存储类型不能为空")
	private Integer type;


	/**
	 * 是否默认存储
	 * true:默认存储，false 非默认
	 */
	@ApiModelProperty(value = "是否默认存储，true:默认存储，false 非默认")
	private boolean defaultStatus = false;
}
