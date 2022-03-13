package cn.enumaelish.file.dto.file;

import cn.enumaelish.file.enums.FileConstants;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

/**
 * @author: EnumaElish
 * @Date: 2022/2/26 19:34
 * @Description: 文件续期及永久标记
 */
@Data
public class PermanentFileCmd {

	/**
	 * 文件标识
	 */
	@ApiModelProperty(value = "文件标识")
	@NotEmpty(message = FileConstants.FILE_ID_NOT_NULL)
	@Length(min=FileConstants.NOT_EMPTY_MIN_LENGTH,max=FileConstants.FILE_ID_MAX_LENGTH,message = FileConstants.FILE_ID_LENGTH_DESC)
	private String fileId;

	public PermanentFileCmd() {
	}

	public PermanentFileCmd(String fileId) {
		this.fileId = fileId;
	}
}
