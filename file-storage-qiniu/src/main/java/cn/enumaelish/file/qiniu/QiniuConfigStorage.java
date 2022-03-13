package cn.enumaelish.file.qiniu;

import cn.enumaelish.file.domain.ability.StoragePlatformConfig;
import cn.enumaelish.file.domain.spi.Extension;
import cn.enumaelish.file.util.Base64Utils;
import cn.enumaelish.file.util.EncryptUtil;
import cn.enumaelish.file.util.JsonUtil;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author: EnumaElish
 * @Date: 2022/2/14 16:36
 * @Description:
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Extension("1")
public class QiniuConfigStorage implements StoragePlatformConfig {

	/**
	 * accessKey
	 */
	private String accessKey;

	/**
	 * accessSecret
	 */
	private String accessSecret;

	/**
	 * bucketName
	 */
	private String bucketName;

	/**
	 * 下载域名
	 */
	private String domain;
	/**
	 * 加密字符
	 */
	private String secretKey;

	/**
	 * 上传url
	 */
	private String uploadUrl;

	@Override
	public String encrypt(){
		QiniuConfigStorage qiniuConfigStorage = new QiniuConfigStorage();
		qiniuConfigStorage.setAccessKey(Base64Utils.encode(EncryptUtil.encrypt(accessKey.getBytes(), secretKey)));
		qiniuConfigStorage.setAccessSecret(Base64Utils.encode(EncryptUtil.encrypt(accessSecret.getBytes(), secretKey)));
		qiniuConfigStorage.setBucketName(this.getBucketName());
		qiniuConfigStorage.setDomain(this.getDomain());
		qiniuConfigStorage.setSecretKey(this.getSecretKey());
		qiniuConfigStorage.setUploadUrl(this.getUploadUrl());
		return JsonUtil.toJSONString(qiniuConfigStorage);
	}
	@Override
	public void desDecrypt(){
		this.accessKey = new String(EncryptUtil.desDecrypt(Base64Utils.decode(accessKey), secretKey));
		this.accessSecret = new String(EncryptUtil.desDecrypt(Base64Utils.decode(accessSecret), secretKey));
	}

	@Override
	@JSONField(serialize = false)
	public void deserialize(String config) {
		QiniuConfigStorage qiniuConfig = JsonUtil.toBean(config, this.getClass());
		if(qiniuConfig != null) {
			setAccessKey(qiniuConfig.getAccessKey());
			setAccessSecret(qiniuConfig.getAccessSecret());
			setBucketName(qiniuConfig.getBucketName());
			setDomain(qiniuConfig.getDomain());
			setSecretKey(qiniuConfig.getSecretKey());
			setUploadUrl(qiniuConfig.getUploadUrl());
		}
	}
}
