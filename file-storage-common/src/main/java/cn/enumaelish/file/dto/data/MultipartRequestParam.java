package cn.enumaelish.file.dto.data;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * @author: EnumaElish
 * @Date: 2022/3/2 20:53
 * @Description: 客户端发起的请求参数
 */
@Data
public class MultipartRequestParam {

	@ApiModelProperty(value = "分片号")
	private Integer partNumber;

	@ApiModelProperty(value = "该片文件大小")
	private Long partSize;

	@ApiModelProperty(value = "文件偏移量")
	private Long startPos;

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
