package cn.enumaelish.file.dto.data;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author: EnumaElish
 * @Date: 2022/3/2 20:49
 * @Description: 分片上传返回
 */
@Data
public class MultipartUploadResult {

	/**
	 * 文件id
	 */
	@ApiModelProperty(value = "文件id")
	private String fileId;

	@ApiModelProperty(value = "true:需要上传，会返回上传信息，false:不需要上传即可展示文件(已上传过)")
	private boolean needUpload = true;

	private List<MultipartRequestParam> list;

}
