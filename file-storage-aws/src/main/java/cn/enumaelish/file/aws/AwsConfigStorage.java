package cn.enumaelish.file.aws;

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
 * @Date: 2022/2/20 20:36
 * @Description:
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Extension("2")
public class AwsConfigStorage implements StoragePlatformConfig {

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
	 * 内网域名
	 */
	private String intranetDomain;

	/**
	 * 外网域名
	 */
	private String extranetDomain;

	/**
	 * 加密字符
	 */
	private String secretKey;

	@Override
	public String encrypt(){
		AwsConfigStorage storage = new AwsConfigStorage();
		storage.setAccessKey(Base64Utils.encode(EncryptUtil.encrypt(accessKey.getBytes(), secretKey)));
		storage.setAccessSecret(Base64Utils.encode(EncryptUtil.encrypt(accessSecret.getBytes(), secretKey)));
		storage.setBucketName(this.getBucketName());
		storage.setExtranetDomain(this.getExtranetDomain());
		storage.setIntranetDomain(this.getIntranetDomain());
		storage.setSecretKey(this.getSecretKey());
		return JsonUtil.toJSONString(storage);
	}
	@Override
	public void desDecrypt(){
		this.accessKey = new String(EncryptUtil.desDecrypt(Base64Utils.decode(accessKey), secretKey));
		this.accessSecret = new String(EncryptUtil.desDecrypt(Base64Utils.decode(accessSecret), secretKey));
	}

	@Override
	public String toString() {
		return JsonUtil.toJSONString(this);
	}

	@Override
	@JSONField(serialize = false)
	public void deserialize(String config) {
		AwsConfigStorage awsConfig = JsonUtil.toBean(config, this.getClass());
		if(awsConfig != null) {
			setAccessKey(awsConfig.getAccessKey());
			setAccessSecret(awsConfig.getAccessSecret());
			setBucketName(awsConfig.getBucketName());
			setExtranetDomain(awsConfig.getExtranetDomain());
			setIntranetDomain(awsConfig.getIntranetDomain());
			setSecretKey(awsConfig.getSecretKey());
		}
	}
}
