package cn.enumaelish.file.domain.model.gateway;

import lombok.Data;

import java.io.InputStream;

/**
 * @author: EnumaElish
 * @Date: 2022/2/26 21:44
 * @Description:
 */
@Data
public class PutObjectBytesCmd {

	/**
	 * 存储key
	 */
	private String key;

	/**
	 * 文件流
	 */
	private InputStream inputStream;

	/**
	 * 文件MIME类型,同httpContent-type
	 */
	String contentType;

	/**
	 * true 外网， false:内网,默认外网，一些特殊情况可通过此参数调整
	 */
	boolean extranet;


	public PutObjectBytesCmd(String key, InputStream inputStream, String contentType, boolean extranet) {
		this.key = key;
		this.inputStream = inputStream;
		this.contentType = contentType;
		this.extranet = extranet;
	}
}
