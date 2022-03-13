package cn.enumaelish.file.qiniu;

import lombok.Data;

/**
 * @author: EnumaElish
 * @Date: 2022/3/2 16:56
 * @Description:
 */
@Data
public class QiniuPartObject {

	private int partNumber;

	private String etag;

	public QiniuPartObject(int partNumber, String etag) {
		this.partNumber = partNumber;
		this.etag = etag;
	}

	public QiniuPartObject() {
	}
}
