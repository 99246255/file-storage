package cn.enumaelish.file.dto.data;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: EnumaElish
 * @Date: 2022/3/3 20:26
 * @Description:
 */
@Data
public class PutObjectResult {

	/**
	 * 文件id
	 */
	@ApiModelProperty(value = "文件id")
	private String fileId;

	public PutObjectResult() {
	}

	public PutObjectResult(String fileId) {
		this.fileId = fileId;
	}
}
