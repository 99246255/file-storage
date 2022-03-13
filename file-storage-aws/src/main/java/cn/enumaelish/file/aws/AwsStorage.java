package cn.enumaelish.file.aws;

import cn.enumaelish.file.domain.gateway.FileStorage;
import cn.enumaelish.file.domain.model.FileInfo;
import cn.enumaelish.file.domain.model.StoragePlatform;
import cn.enumaelish.file.domain.model.gateway.PutObjectBytesCmd;
import cn.enumaelish.file.domain.spi.Extension;
import cn.enumaelish.file.util.JsonUtil;
import cn.enumaelish.file.dto.data.GeneratePutUrlResult;
import cn.enumaelish.file.enums.FileConstants;
import com.alibaba.cola.exception.BizException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.HttpMethod;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;

import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: EnumaElish
 * @Date: 2022/2/20 20:35
 * @Description: 本来以为这是通用的aws实现，实际不是，内外网映射规则和sdk的不一样
 * 例如内网下载url为 http://10.34.20.221:12001/zgqc/test?AWSAccessKeyId=HJCETELJRELHXHOVVGNH&Expires=1645151043&Signature=nkjz2l4%2B%2FEW651eFxfr0rU6vXyU%3D
 * sdk生成的下载地址
 * http://zgqc.oss.zgqc.hbzg.gov.cn/test?AWSAccessKeyId=HJCETELJRELHXHOVVGNH&Expires=1645151121&Signature=Zf0m3Hrtv0PY9NRawSW7xHaOUwY%3D
 * 即外网规则是http://{bucketName}.{domain}/{key}+签名}  domain会自动去除/后内容，比如test/123则智慧保留test
 * 内网规则是http://{domain}/bucketName/{key}+签名}
 * 原生sdk不支持外网调用
 */
@Extension("2")
public class AwsStorage implements FileStorage<AwsConfigStorage> {

	private StoragePlatform storagePlatform;

	private AwsConfigStorage config;

	/**
	 * 内网client，处理内网相关
	 */
	private AmazonS3 intranetClient;


	@Override
	public StoragePlatform getPlatformStore() {
		return storagePlatform;
	}

	@Override
	public void init(StoragePlatform<AwsConfigStorage> storagePlatform) {
		this.storagePlatform = storagePlatform;
		config = storagePlatform.getConfig();
		if(config == null){
			throw new BizException(String.format("存储配置错误：%s", JsonUtil.toJSONString(storagePlatform)));
		}
		ClientConfiguration clientConfiguration = new ClientConfiguration();
		clientConfiguration.setSignerOverride("S3SignerType");
		clientConfiguration.setProtocol(Protocol.HTTP);
		clientConfiguration.withUseExpectContinue(false);
		AWSCredentials credentials = new BasicAWSCredentials(config.getAccessKey(), config.getAccessSecret());
		intranetClient = new AmazonS3Client(credentials, clientConfiguration);
		intranetClient.setEndpoint(config.getIntranetDomain());
	}

	@Override
	public String save(PutObjectBytesCmd putObjectBytesCmd) {
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentType(putObjectBytesCmd.getContentType());
		PutObjectResult putObjectResult = getClient(putObjectBytesCmd.isExtranet()).putObject(config.getBucketName(), putObjectBytesCmd.getKey(), putObjectBytesCmd.getInputStream(), metadata);
		return putObjectResult.getETag().toLowerCase();
	}

	@Override
	public boolean delete(String key) {
		intranetClient.deleteObject(config.getBucketName(), key);
		return true;
	}

	@Override
	public boolean check(FileInfo fileInfo) {
		ObjectMetadata objectMetadata = intranetClient.getObjectMetadata(config.getBucketName(), fileInfo.getPath());
		if(fileInfo.isCheckMd5()){
			if(!objectMetadata.getETag().toLowerCase().equals(fileInfo.getMd5())) {
				return false;
			}
		}
		return objectMetadata.getContentLength() == fileInfo.getSize();
	}

	/**
	 * 返回对应使用的client
	 * @param extranet
	 * @return
	 */
	private AmazonS3 getClient(boolean extranet){
		return intranetClient;
	}

	private String replaceEndpoint(String url, boolean extranet){
		if(extranet){
			return url.replace(config.getIntranetDomain(), config.getExtranetDomain());
		}else{
			return url;
		}

	}

	@Override
	public String getDownloadFileUrl(String key, String fileName, long expireTime, boolean extranet) {
		GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(config.getBucketName(), key)
				.withMethod(HttpMethod.GET)
				.withExpiration(new Date(System.currentTimeMillis() + expireTime));
		URL url = getClient(extranet).generatePresignedUrl(generatePresignedUrlRequest);
		return replaceEndpoint(url.toString(), extranet);
	}

	@Override
	public GeneratePutUrlResult generatePutUrl(FileInfo fileInfo, boolean extranet) {
		java.util.Date expiration = new java.util.Date();
		long expTimeMillis = expiration.getTime();
		expTimeMillis += 1000 * 60 * 60;
		expiration.setTime(expTimeMillis);
		GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(config.getBucketName(), fileInfo.getPath())
				.withMethod(HttpMethod.PUT)
				.withContentType(fileInfo.getFileType())
				.withExpiration(expiration);
		URL url = getClient(extranet).generatePresignedUrl(generatePresignedUrlRequest);
		GeneratePutUrlResult generatePutUrlResult = new GeneratePutUrlResult();
		String url1 = replaceEndpoint(url.toString(),extranet);
		generatePutUrlResult.setUrl(url1);
		generatePutUrlResult.setHttpMethod(FileConstants.HTTP_METHOD_PUT);
		generatePutUrlResult.setFileId(fileInfo.getFileId());
		Map<String, Object> headers = new HashMap<>(4);
		headers.put("Content-Type", fileInfo.getFileType());
		generatePutUrlResult.setHeaders(headers);
		// 表示使用binary方式传文件
		generatePutUrlResult.setFormBody(null);
		return generatePutUrlResult;
	}
}
