package cn.enumaelish.file.dto.data;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: EnumaElish
 * @Date: 2022/3/2 18:53
 * @Description:
 */
@Data
public class UploadPartDto {

	@ApiModelProperty(value = "分片号")
	private int partNumber;

	@ApiModelProperty(value = "分片eTag")
	private String eTag;
}
