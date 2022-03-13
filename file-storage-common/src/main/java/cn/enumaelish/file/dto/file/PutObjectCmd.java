package cn.enumaelish.file.dto.file;

import cn.enumaelish.file.enums.FileConstants;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.InputStream;

/**
 * @author: EnumaElish
 * @Date: 2022/3/9 19:00
 * @Description: 上传文件命令
 */
public class PutObjectCmd {

	/**
	 * 文件流
	 */
	@ApiModelProperty(value = "文件流")
	@NotNull(message = "文件流不能为空")
	private InputStream inputStream;

	/**
	 * 文件名
	 */
	@ApiModelProperty(value = "文件名")
	@NotEmpty(message = "文件名称不能为空")
	@Length(min= FileConstants.NOT_EMPTY_MIN_LENGTH,max=FileConstants.FILE_NAME_MAX_LENGTH,message = "文件名称长度限制1~255")
	private String fileName;

	@ApiModelProperty(value = "文件大小")
	@NotNull(message = "文件大小不能为空")
	private Long size;

	/**
	 * 文件类型,同contentType
	 */
	@ApiModelProperty(value = "文件类型,同contentType")
	@NotEmpty(message = "文件类型不能为空")
	@Length(min=FileConstants.NOT_EMPTY_MIN_LENGTH,max=FileConstants.FILE_TYPE_MAX_LENGTH,message = "文件类型长度限制1~100")
	private String fileType;

	/**
	 * 业务类型
	 */
	@ApiModelProperty(value = "业务类型")
	@NotNull(message = "业务类型不能为空")
	private Integer businessType;

	/**
	 * true 外网， false:内网
	 * 默认外网，一些特殊情况可通过此参数调整
	 */
	@ApiModelProperty(value = "true 外网， false:内网,默认外网，一些特殊情况可通过此参数调整")
	private boolean extranet = true;

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		if(fileName == null){
			throw new IllegalArgumentException("文件名不可为空");
		}
		if(fileName.length() < FileConstants.NOT_EMPTY_MIN_LENGTH || fileName.length() > FileConstants.FILE_NAME_MAX_LENGTH ){
			throw new IllegalArgumentException("文件名称长度限制1~255");
		}
		this.fileName = fileName;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		if(fileType == null){
			throw new IllegalArgumentException("文件类型不能为空");
		}
		if(fileType.length() < FileConstants.NOT_EMPTY_MIN_LENGTH || fileType.length() > FileConstants.FILE_TYPE_MAX_LENGTH ){
			throw new IllegalArgumentException("文件类型长度限制1~100");
		}
		this.fileType = fileType;
	}

	public Integer getBusinessType() {
		return businessType;
	}

	public void setBusinessType(Integer businessType) {
		this.businessType = businessType;
	}

	public boolean isExtranet() {
		return extranet;
	}

	public void setExtranet(boolean extranet) {
		this.extranet = extranet;
	}
}
