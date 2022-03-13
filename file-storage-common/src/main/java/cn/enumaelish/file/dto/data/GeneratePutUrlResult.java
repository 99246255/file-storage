package cn.enumaelish.file.dto.data;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * @author: EnumaElish
 * @Date: 2022/3/3 18:02
 * @Description:
 */
@Data
public class GeneratePutUrlResult {

	/**
	 * 文件id
	 */
	@ApiModelProperty(value = "文件id")
	private String fileId;

	@ApiModelProperty(value = "true:需要上传，会返回上传信息，false:不需要上传即可展示文件(已上传过)")
	private boolean needUpload = true;

	/**
	 * 上传请求方式
	 */
	@ApiModelProperty(value = "上传请求方式")
	private String httpMethod;

	/**
	 * 上传url
	 */
	@ApiModelProperty(value = "上传url")
	private String url;

	/**
	 * 需要设置的上传请求头
	 */
	@ApiModelProperty(value = "需要设置的上传请求头")
	private Map<String,Object> headers;

	@ApiModelProperty(value = "需要设置的form表单，其中value为${file}的需替换为指定文件")
	private Map<String, Object> formBody;

}
