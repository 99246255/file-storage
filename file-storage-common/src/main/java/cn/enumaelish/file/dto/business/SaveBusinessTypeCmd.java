package cn.enumaelish.file.dto.business;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author: EnumaElish
 * @Date: 2022/2/28 20:41
 * @Description: 新增/修改业务类型
 */
@Data
public class SaveBusinessTypeCmd {

	@ApiModelProperty(value = "业务类型")
	@NotNull(message = "业务类型不能为空")
	private Integer id;

	@ApiModelProperty(value = "名称")
	@NotEmpty(message = "名称不能为空")
	@Length(min=1,max=50,message = "名称长度限制1~50")
	private String name;

	@ApiModelProperty(value = "使用的存储id")
	@NotNull(message = "使用的存储id不能为空")
	private Integer storeId;
}
