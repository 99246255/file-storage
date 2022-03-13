package cn.enumaelish.file.dto.file;

import cn.enumaelish.file.enums.FileConstants;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

/**
 * @author: EnumaElish
 * @Date: 2022/3/2 18:58
 * @Description:
 */
@Data
public class CompleteMultipartUploadCmd {

	/**
	 * 文件标识
	 */
	@ApiModelProperty(value = "文件标识")
	@NotEmpty(message = FileConstants.FILE_ID_NOT_NULL)
	@Length(min=FileConstants.NOT_EMPTY_MIN_LENGTH,max=FileConstants.FILE_ID_MAX_LENGTH,message = FileConstants.FILE_ID_LENGTH_DESC)
	private String fileId;

	public CompleteMultipartUploadCmd() {
	}

	public CompleteMultipartUploadCmd(String fileId) {
		if(fileId == null){
			throw new IllegalArgumentException(FileConstants.HEADER_CONTENT_TYPE);
		}
		if(fileId.length()  < FileConstants.NOT_EMPTY_MIN_LENGTH || fileId.length() > FileConstants.FILE_ID_MAX_LENGTH){

		}
		this.fileId = fileId;

	}
}
