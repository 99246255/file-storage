package cn.enumaelish.file.qiniu;

import java.util.List;

/**
 * @author: EnumaElish
 * @Date: 2022/3/4 15:25
 * @Description:
 */
public class QiniuPartDto {

	private List<QiniuPartObject> parts;

	public QiniuPartDto(List<QiniuPartObject> parts) {
		this.parts = parts;
	}

	public QiniuPartDto() {
	}

	public List<QiniuPartObject> getParts() {
		return parts;
	}

	public void setParts(List<QiniuPartObject> parts) {
		this.parts = parts;
	}
}
