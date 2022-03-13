package cn.enumaelish.file.dto.data;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: EnumaElish
 * @Date: 2022/3/3 20:09
 * @Description:
 */
@Data
public class FileUrlDto {

	/**
	 * 文件url
	 */
	@ApiModelProperty(value = "文件url")
	private String url;

	/**
	 * 文件大小
	 */
	@ApiModelProperty(value = "文件大小")
	private Long size;

	/**
	 * 文件名称
	 */
	@ApiModelProperty(value = "fileName")
	private String fileName;

	/**
	 * 文件md5
	 */
	@ApiModelProperty(value = "文件md5")
	private String md5;

	/**
	 * 文件类型
	 */
	@ApiModelProperty(value = "文件类型,同ContentType")
	private String fileType;
}
