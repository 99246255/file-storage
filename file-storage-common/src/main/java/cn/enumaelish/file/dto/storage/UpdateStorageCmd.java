package cn.enumaelish.file.dto.storage;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * @author: EnumaElish
 * @Date: 2022/2/28 20:34
 * @Description: 编辑存储配置
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UpdateStorageCmd extends AddStorageCmd{

	@ApiModelProperty(value = "id")
	@NotNull(message = "存储id不能为空")
	private Integer id;

}
